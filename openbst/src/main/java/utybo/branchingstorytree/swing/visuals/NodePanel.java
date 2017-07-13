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
import java.util.Base64;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebView;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.impl.IMGClient;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.utils.MarkupUtils;

public class NodePanel extends JScrollablePanel
{
    private static final String STYLE = "@font-face {" + "font-family: 'alegreya';" + "src: url(" + NodePanel.class.getResource("/utybo/branchingstorytree/swing/font/Alegreya-Regular.otf").toExternalForm() + ");" + "font-weight: normal;" + "font-style: normal;" + "}" + "html {" + "font-family: 'alegreya'";
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final IMGClient imageClient;
    private String text;
    private Color textColor;
    private boolean backgroundVisible = true;
    private WebView view;

    public NodePanel(final IMGClient imageClient)
    {
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
                Scene sc = new Scene(view);
                view.setFontSmoothingType(FontSmoothingType.LCD);
                try
                {
                    view.getEngine().loadContent(IOUtils.toString(NodePanel.class.getResourceAsStream("/utybo/branchingstorytree/swing/html/error.html"), StandardCharsets.UTF_8).replace("$MSG", Lang.get("story.problem")).replace("$STYLE", STYLE));
                }
                catch(IOException e)
                {
                    e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public void applyNode(final BranchingStory story, final TextNode textNode) throws BSTException
    {
        final String text = StoryUtils.solveVariables(textNode, story);
        final int markupLanguage = MarkupUtils.solveMarkup(story, textNode);
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
        else if(textNode.hasTag("img_manual") && Boolean.parseBoolean(textNode.getTag("img_manual")))
        {
            imageClient.setBackground(null);
        }
        build();
    }

    private void build()
    {
        String base = "<head><meta charset=\"utf-8\"/><style type='text/css'>" + STYLE + "</style></head><body style=\"margin:10px;padding:0px;$BG\"><div style=\"margin:-10px;padding:10px;$ADDITIONAL;width: 100%; height:100%\"><div style=\"$COLOR\">" + text + "</div></div></body>";
        String bg, additional, c;
        if(imageClient.getCurrentBackground() != null && backgroundVisible)
        {
            bg = "background-image:url('data:image/png;base64," + b64bg() + "'); background-size:cover; background-position:center; background-attachment:fixed";
            additional = "background-color:rgba(255,255,255,0.5)";
        }
        else
        {
            bg = "";
            additional = "";
        }
        if(textColor != null)
        {
            c = "color: " + MarkupUtils.toHex(textColor.getRed(), textColor.getGreen(), textColor.getBlue());
        }
        else
        {
            c = "";
        }
        String s = base.replace("$BG", bg).replace("$ADDITIONAL", additional).replace("$COLOR", c);
        try
        {
            jfxRunAndWait(() ->
            {
                view.getEngine().loadContent(s);
            });
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
        return Base64.getMimeEncoder().encodeToString(baos.toByteArray()).replaceAll("[\n\r]", "");
    }

    private void jfxRunAndWait(Runnable runnable) throws InterruptedException
    {
        if(Platform.isFxApplicationThread())
            Platform.runLater(runnable);
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
            catch(IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
            {
                // TODO add a warning?
                OpenBST.LOG.warn("Color does not exist : " + color, e);
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
}
