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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
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
import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebView;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.Experimental;
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.impl.IMGClient;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.utils.MarkupUtils;

public class NodePanel extends JScrollablePanel
{
    private static final String FONT_STYLE, STYLE;

    static
    {
        String s;
        try
        {
            s = IOUtils.toString(
                    new XZCompressorInputStream(NodePanel.class.getResourceAsStream(
                            "/utybo/branchingstorytree/swing/font/fonts.css.xz")),
                    StandardCharsets.UTF_8);
        }
        catch(IOException e)
        {
            OpenBST.LOG.warn("Failed to load fonts CSS file", e);
            s = "";
        }

        FONT_STYLE = s;

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
    private final IMGClient imageClient;
    private String text;
    private Color textColor;
    private boolean backgroundVisible = true;
    private WebView view;
    private String storyFont;
    private boolean hrefEnabled;
    private final StoryPanel parent;
    private boolean isDark, isAlreadyBuilt;
    @Experimental
    private ArrayList<String> additionalCSS = new ArrayList<>();

    private final Consumer<Boolean> callback = b ->
    {
        isDark = b;
        if(isAlreadyBuilt)
        {
            build();
            Platform.runLater(() ->
            {
                if(!b)
                    view.setFontSmoothingType(FontSmoothingType.LCD);
                else
                    view.setFontSmoothingType(FontSmoothingType.GRAY);
            });

        }
    };

    public NodePanel(BranchingStory story, StoryPanel parent, final IMGClient imageClient)
    {
        OpenBST.getInstance().addDarkModeCallback(callback);
        callback.accept(OpenBST.getInstance().isDark());
        this.parent = parent;
        this.imageClient = imageClient;
        setLayout(new BorderLayout());

        JFXPanel panel = new JFXPanel();
        add(panel, BorderLayout.CENTER);

        if(story.hasTag("font"))
        {
            storyFont = story.getTag("font");
        }
        else
        {
            storyFont = "libre_baskerville";
        }

        CountDownLatch cdl = new CountDownLatch(1);
        Platform.runLater(() ->
        {
            try
            {
                view = new WebView();
                view.getEngine().setOnAlert(e -> SwingUtilities.invokeLater(
                        () -> JOptionPane.showMessageDialog(OpenBST.getInstance(), e.getData())));
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

                Scene sc = new Scene(view);
                if(!OpenBST.getInstance().isDark())
                    view.setFontSmoothingType(FontSmoothingType.LCD);
                else
                    view.setFontSmoothingType(FontSmoothingType.GRAY);
                try
                {
                    view.getEngine().loadContent(IOUtils
                            .toString(
                                    NodePanel.class.getResourceAsStream(
                                            "/utybo/branchingstorytree/swing/html/error.html"),
                                    StandardCharsets.UTF_8)
                            .replace("$MSG", Lang.get("story.problem"))
                            .replace("$STYLE", FONT_STYLE));
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

        build();
    }

    private void build()
    {
        // Build the base, with fonts and style (injecting font info)
        String base = "<head><meta charset=\"utf-8\"/>" //
                + "<style type='text/css'>" + FONT_STYLE + "</style>" //
                + "<style type='text/css'>" + STYLE.replace("$f", storyFont) + "</style>" //
                + "$CUSTOMCSS" + "</head>" //
                + "<body class='$bbg" + (isDark ? " dark" : "") + "'>" + "<div class='storydiv'>" //
                + "<div style=\"$COLOR\">" + text + "</div></div></body>";
        String additional, c;

        if(imageClient.getCurrentBackground() != null && backgroundVisible)
        {
            // Inject background (base64) and add the background class to the body
            base = base.replace("$b64bg", b64bg()).replace("$bbg", "bg");
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
        try
        {
            jfxRunAndWait(() ->
            {
                view.getEngine().loadContent(s);
            });
        }
        catch(InterruptedException e)
        {
            OpenBST.LOG.warn("Failed to synchronize", e);
        }
        isAlreadyBuilt = true;
    }

    private String b64bg()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            ImageIO.write(imageClient.getCurrentBackground(), "PNG", baos);
        }
        catch(IOException e)
        {
            OpenBST.LOG.warn("Failed to create Base64 background", e);
        }
        return Base64.getMimeEncoder().encodeToString(baos.toByteArray()).replaceAll("[\n\r]", "");
    }

    private void jfxRunAndWait(Runnable runnable) throws InterruptedException
    {
        if(Platform.isFxApplicationThread())
        {
            Platform.runLater(runnable);
        }
        else
        {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() ->
            {
                try
                {
                    runnable.run();
                }
                finally
                {
                    latch.countDown();
                }
            });
            latch.await();
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
                SwingUtilities
                        .invokeLater(() -> JOptionPane.showMessageDialog(OpenBST.getInstance(),
                                Lang.get("story.unknowncolor").replace("$c", color),
                                Lang.get("error"), JOptionPane.ERROR_MESSAGE));
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
        repaint();
    }

    public void setJSEnabled(boolean b)
    {
        view.getEngine().setJavaScriptEnabled(b);
        parent.getJSHint().setToolTipText(Lang.get("html.js" + (b ? "enabled" : "blocked")));
        parent.getJSHint().setIcon(new ImageIcon(b ? OpenBST.jsEnabled : OpenBST.jsBlocked));
        parent.getJSHint()
                .setDisabledIcon(new ImageIcon(b ? OpenBST.jsEnabled : OpenBST.jsBlocked));
        parent.getJSHint().setVisible(true);
    }

    public void setHrefEnabled(boolean b)
    {
        hrefEnabled = b;
        parent.getHrefHint().setToolTipText(Lang.get("html.href" + (b ? "enabled" : "blocked")));
        parent.getHrefHint().setIcon(new ImageIcon(b ? OpenBST.hrefEnabled : OpenBST.hrefBlocked));
        parent.getHrefHint()
                .setDisabledIcon(new ImageIcon(b ? OpenBST.hrefEnabled : OpenBST.hrefBlocked));
        parent.getHrefHint().setVisible(true);
    }

    public void dispose()
    {
        OpenBST.getInstance().removeDarkModeCallbback(callback);
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
