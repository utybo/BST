/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.visuals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
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
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.Experimental;
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.swing.Icons;
import utybo.branchingstorytree.swing.Messagers;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.OpenBSTGUI;
import utybo.branchingstorytree.swing.VisualsUtils;
import utybo.branchingstorytree.swing.impl.IMGClient;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.utils.MarkupUtils;

public class NodePanel extends JScrollablePanel
{
    private static final String FONT_UBUNTU, FONT_LIBRE_BASKERVILLE, STYLE;

    static
    {
        FONT_UBUNTU = loadFont("ubuntu");
        FONT_LIBRE_BASKERVILLE = loadFont("libre_baskerville");

        String s = null;
        try
        {
            s = IOUtils.toString(NodePanel.class.getResourceAsStream(
                    "/utybo/branchingstorytree/swing/story.css"), StandardCharsets.UTF_8);
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
                    view.getEngine().loadContent(IOUtils
                            .toString(
                                    NodePanel.class.getResourceAsStream(
                                            "/utybo/branchingstorytree/swing/html/error.html"),
                                    StandardCharsets.UTF_8)
                            .replace("$MSG", Lang.get("story.problem"))
                            .replace("$STYLE", FONT_UBUNTU));
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

    private static String loadFont(String name)
    {
        String s;
        try
        {
            s = IOUtils.toString(
                    new XZCompressorInputStream(NodePanel.class.getResourceAsStream(
                            "/utybo/branchingstorytree/swing/font/" + name + ".css.xz")),
                    StandardCharsets.UTF_8);
        }
        catch(IOException e)
        {
            OpenBST.LOG.warn("Failed to load fonts CSS file", e);
            s = "";
        }
        return s;
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
                + "<style type='text/css'>" + getCurrentFontCss() + "</style>" //
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
        }
        else
        {
            base.replace("$bbg", "");
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

        VisualsUtils.invokeJfxAndWait(() ->
        {
            view.getEngine().loadContent(s);
        });

        isAlreadyBuilt = true;
    }

    private String getCurrentFontCss()
    {
        String fontName = getCurrentFont().toLowerCase();
        if(fontName.equals("ubuntu"))
            return FONT_UBUNTU;
        else if(fontName.contains("baskerville"))
            return FONT_LIBRE_BASKERVILLE;
        else
            return "";
    }

    private String getCurrentFont()
    {
        if((int)parent.currentNode.getStory().getRegistry()
                .get("__nonlatin_" + parent.currentNode.getStory().getTag("__sourcename"), 0) == 1)
        {
            return "sans-serif";
        }
        return parseFont(parent.currentNode.getStory());
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
