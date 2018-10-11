/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.visuals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.Experimental;
import zrrk.bst.bstjava.api.StoryUtils;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.bstjava.api.story.TextNode;
import zrrk.bst.openbst.Icons;
import zrrk.bst.openbst.Messagers;
import zrrk.bst.openbst.OpenBST;
import zrrk.bst.openbst.OpenBSTGUI;
import zrrk.bst.openbst.VisualsUtils;
import zrrk.bst.openbst.impl.IMGClient;
import zrrk.bst.openbst.utils.Lang;
import zrrk.bst.openbst.utils.MarkupUtils;

public class NodePanel extends JScrollablePanel
{
    private static final String STYLE;
    static
    {

        String s = null;
        try
        {
            s = IOUtils.toString(NodePanel.class.getResourceAsStream("/zrrk/bst/openbst/story.css"),
                    StandardCharsets.UTF_8);
        }
        catch(IOException e)
        {
            OpenBST.LOG.warn("Failed to load fonts CSS file", e);
            s = "";
        }
        STYLE = s;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String defaultFont = "libre_baskerville";
    private final IMGClient imageClient;
    private String text;
    private Color textColor;
    private boolean backgroundVisible = true;
    private WebView view;
    private boolean hrefEnabled;
    private final StoryPanel parent;
    private boolean isDark, isAlreadyBuilt;
    private String fontSize;
    @Experimental
    private ArrayList<String> additionalCSS = new ArrayList<>();

    private final Consumer<Boolean> callback = b ->
    {
        isDark = b;
        if(isAlreadyBuilt)
        {
            build();
        }
    };

    public NodePanel(BranchingStory story, StoryPanel parent, final IMGClient imageClient)
    {
        OpenBSTGUI.addDarkModeCallback(callback);
        callback.accept(OpenBSTGUI.isDark());
        this.parent = parent;
        this.imageClient = imageClient;
        setLayout(new BorderLayout());

        JFXPanel panel = new JFXPanel();
        add(panel, BorderLayout.CENTER);

        CountDownLatch cdl = new CountDownLatch(1);
        Platform.runLater(() ->
        {
            try
            {
                view = new WebView();
                view.getEngine().setOnAlert(e -> SwingUtilities.invokeLater(
                        () -> Messagers.showMessage(OpenBST.getGUIInstance(), e.getData())));
                view.getEngine().getLoadWorker().stateProperty()
                        .addListener((obs, oldState, newState) ->
                        {
                            if(newState == State.SUCCEEDED)
                            {
                                Document doc = view.getEngine().getDocument();
                                NodeList nl = doc.getElementsByTagName("a");
                                for(int i = 0; i < nl.getLength(); i++)
                                {
                                    Node n = nl.item(i);
                                    HTMLAnchorElement a = (HTMLAnchorElement)n;
                                    if(a.getHref() != null)
                                    {
                                        ((EventTarget)a).addEventListener("click", ev ->
                                        {
                                            if(!hrefEnabled)
                                            {
                                                ev.preventDefault();
                                            }
                                        }, false);
                                    }
                                }
                            }
                        });

                view.setZoom(Icons.getScale());

                Scene sc = new Scene(view);
                try
                {
                    view.getEngine()
                            .loadContent(IOUtils
                                    .toString(
                                            NodePanel.class.getResourceAsStream(
                                                    "/zrrk/bst/openbst/html/error.html"),
                                            StandardCharsets.UTF_8)
                                    .replace("$MSG", Lang.get("story.problem")));
                }
                catch(IOException e)
                {
                    OpenBST.LOG.warn("Error on trying to load error HTML file", e);
                }
                panel.setScene(sc);
            }
            finally
            {
                cdl.countDown();
            }
        });
        try
        {
            cdl.await();
        }
        catch(InterruptedException e)
        {
            OpenBST.LOG.warn("Synchronization failed", e);
        }
    }

    public void applyNode(final BranchingStory story, final TextNode textNode) throws BSTException
    {
        final String text = StoryUtils.solveVariables(textNode, story);
        final int markupLanguage = MarkupUtils.solveMarkup(parent.story, story, textNode);
        setText(MarkupUtils.translateMarkup(markupLanguage, text));

        if(textNode.hasTag("color"))
        {
            final String color = textNode.getTag("color");
            setTextColor(color);
        }
        else
        {
            textColor = null;
        }

        if(textNode.hasTag("img_background"))
        {
            final String bg = textNode.getTag("img_background");
            if("none".equals(bg))
            {
                imageClient.setBackground(null);
            }
            else
            {
                imageClient.setBackground(bg);
            }
        }
        else if(textNode.hasTag("img_manual")
                && Boolean.parseBoolean(textNode.getTag("img_manual")))
        {
            imageClient.setBackground(null);
        }

        fontSize = textNode.getStory().getTagOrDefault("fontsize", "16px");

        build();
    }

    private void build()
    {
        // Build the base, with fonts and style (injecting font info)
        String base = "<head><meta charset=\"utf-8\"/>" //
                + "<style type='text/css'>" + STYLE.replace("$font", getCurrentFont()) + "</style>" //
                + "$CUSTOMCSS" + "</head>" //
                + "<body class='$bbg" + (isDark ? " dark" : "") + "'>" //
                + "<div class='storydiv' style=\"$COLOR\">" + text + "</div></body>";
        String additional, c;

        base = base.replace("$fsize", fontSize);
        if(imageClient.getCurrentBackground() != null && backgroundVisible)
        {
            // Inject background (base64) and add the background class to the body
            base = base.replace("$b64bg", imageClient.getBase64Background()).replace("$bbg", "bg");
            additional = isDark ? "background-color:rgba(0,0,0,0.66)"
                    : "background-color:rgba(255,255,255,0.66)";
            System.out.println("11");
        }
        else
        {
            System.out.println("22");
            base = base.replace("$bbg", "");
            additional = "";
        }
        if(textColor != null)
        {
            c = "color: " + MarkupUtils.toHex(textColor.getRed(), textColor.getGreen(),
                    textColor.getBlue());
        }
        else
        {
            c = isDark ? "color: #FFFFFF" : "";
        }

        // $EXPERIMENTAL
        StringBuilder sb = new StringBuilder();
        for(String string : additionalCSS)
        {
            sb.append("<style type='text/css'>" + string + "</style>");
        }

        String s = base.replace("$ADDITIONAL", additional).replace("$COLOR", c)
                // $EXPERIMENTAL
                .replace("$CUSTOMCSS", sb.toString());

        try
        {
            Files.write(Paths.get("test.html"), s.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println(Paths.get("test.html").toRealPath());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        VisualsUtils.invokeJfxAndWait(() ->
        {
            view.getEngine().loadContent(s);
        });

        isAlreadyBuilt = true;
    }

    private String getCurrentFont()
    {
        if((int)parent.currentNode.getStory().getRegistry()
                .get("__nonlatin_" + parent.currentNode.getStory().getTag("__sourcename"), 0) == 1)
        {
            return "sans-serif";
        }
        return parseFont(parent.currentNode.getStory()).replace("ubuntu", "Ubuntu")
                .replace("librebaskerville", "LibreBaskerville");
    }

    private String parseFont(BranchingStory s)
    {
        if(s.hasTag("font"))
        {
            return s.getTag("font");
        }
        else if(parent.story.hasTag("font"))
        {
            return parent.story.getTag("font");
        }
        else
        {
            return defaultFont;
        }
    }

    public void setText(final String text)
    {
        this.text = text;
    }

    public void setTextColor(final String color)
    {
        Color c = null;
        if(color.startsWith("#"))
        {
            c = new Color(Integer.parseInt(color.substring(1), 16));
        }
        else
        {
            try
            {
                c = (Color)Color.class.getField(color).get(null);
            }
            catch(IllegalArgumentException | IllegalAccessException | NoSuchFieldException
                    | SecurityException e)
            {
                OpenBST.LOG.warn("Color does not exist : " + color, e);
                SwingUtilities.invokeLater(() -> Messagers.showMessage(OpenBST.getGUIInstance(),
                        Lang.get("story.unknowncolor").replace("$c", color), Messagers.TYPE_ERROR));
            }
        }
        if(c != null)
        {
            textColor = c;
        }
        else
        {
            textColor = null;
        }
    }

    public void setBackgroundVisible(final boolean selected)
    {
        backgroundVisible = selected;
    }

    public void setJSEnabled(boolean b)
    {
        CountDownLatch cdl = new CountDownLatch(1);
        Platform.runLater(() ->
        {
            try
            {
                view.getEngine().setJavaScriptEnabled(b);
            }
            finally
            {
                cdl.countDown();
            }
        });
        try
        {
            cdl.await();
        }
        catch(InterruptedException e)
        {
            OpenBST.LOG.error(e);
        }

        parent.getJSHint().setToolTipText(Lang.get("html.js" + (b ? "enabled" : "block")));
        parent.getJSHint()
                .setIcon(new ImageIcon(b ? Icons.getImage("JSY", 16) : Icons.getImage("JSN", 16)));
        parent.getJSHint().setDisabledIcon(
                new ImageIcon(b ? Icons.getImage("JSY", 16) : Icons.getImage("JSN", 16)));
        parent.getJSHint().setVisible(true);
    }

    public void setHrefEnabled(boolean b)
    {
        hrefEnabled = b;
        parent.getHrefHint().setToolTipText(Lang.get("html.href" + (b ? "enabled" : "block")));
        parent.getHrefHint().setIcon(
                new ImageIcon(b ? Icons.getImage("LinkY", 16) : Icons.getImage("LinkN", 16)));
        parent.getHrefHint().setDisabledIcon(
                new ImageIcon(b ? Icons.getImage("LinkY", 16) : Icons.getImage("LinkN", 16)));
        parent.getHrefHint().setVisible(true);
    }

    public void dispose()
    {
        OpenBSTGUI.removeDarkModeCallbback(callback);
    }

    @Experimental
    public void addCSSSheet(String sheet)
    {
        additionalCSS.add(sheet);
    }

    @Experimental
    public void removeCSSSheet(String sheet)
    {
        additionalCSS.remove(sheet);
    }

    @Experimental
    public void removeAllCSSSheets()
    {
        additionalCSS.clear();
    }

}
