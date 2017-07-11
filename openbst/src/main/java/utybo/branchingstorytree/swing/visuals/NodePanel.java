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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
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
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final IMGClient imageClient;
    private String text;
    private Color textColor;
    private boolean backgroundVisible = true;
    private WebView view;
    private Dimension previousBounds; 
    private Image previousScaledImage; 
    private BufferedImage previousImage; 
    private int imageX, imageY; 

    public NodePanel(final IMGClient imageClient)
    {
        this.imageClient = imageClient;
        setLayout(new BorderLayout());

        JFXPanel panel = new JFXPanel();
        panel.setBackground(new Color(0,0,0,0));
        add(panel, BorderLayout.CENTER);

        CountDownLatch cdl = new CountDownLatch(1);
        Platform.runLater(() ->
        {
            view = new WebView();
            BorderPane bp = new BorderPane(view);
            bp.setBackground(Background.EMPTY);
            Scene sc = new Scene(bp);
            view.setFontSmoothingType(FontSmoothingType.LCD);
            view.getEngine().loadContent("<head></head><body>" + Lang.get("story.problem") + "</body>");
            sc.setFill(javafx.scene.paint.Color.TRANSPARENT);
            panel.setScene(sc);
            cdl.countDown();
        });
        try
        {
            cdl.await();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        //        textLabel = new JLabel(Lang.get("story.problem"));
        //        textLabel.setVerticalAlignment(SwingConstants.TOP);
        //        textLabel.setForeground(Color.BLACK);
        //        textLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        //        add(textLabel, BorderLayout.CENTER);
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
        String base = "<head></head><body style='background-color: rgba(0,0,0,0)'>"+text+"</body>";
        try
        {
            jfxRunAndWait(() -> {
                view.getEngine().loadContent(base);
                final com.sun.webkit.WebPage webPage = com.sun.javafx.webkit.Accessor.getPageFor(view.getEngine());
                webPage.setBackgroundColor(0);
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
        return Base64.getMimeEncoder().encodeToString(baos.toByteArray());
    }

    private void jfxRunAndWait(Runnable runnable) throws InterruptedException
    {
        if(Platform.isFxApplicationThread())
            Platform.runLater(runnable);
        else
        {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                runnable.run();
                latch.countDown();
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

    @Override 
    protected void paintComponent(final Graphics g) 
    { 
        super.paintComponent(g); 
        if(imageClient != null && imageClient.getCurrentBackground() != null && backgroundVisible) 
        { 
            Image image; 
            final int width = getWidth() - 1; 
            final int height = getHeight() - 1; 
            if(previousBounds != null && previousScaledImage != null && getParent().getSize().equals(previousBounds) && imageClient.getCurrentBackground() == previousImage) 
            { 
                image = previousScaledImage; 
            } 
            else 
            { 
                final BufferedImage bi = imageClient.getCurrentBackground(); 
                double scaleFactor = 1d; 
                if(bi.getWidth() > bi.getHeight()) 
                { 
                    scaleFactor = getScaleFactorToFill(new Dimension(bi.getWidth(), bi.getHeight()), getParent().getSize()); 
                } 
                else if(bi.getHeight() > bi.getWidth()) 
                { 
                    scaleFactor = getScaleFactorToFill(new Dimension(bi.getWidth(), bi.getHeight()), getParent().getSize()); 
                } 
                final int scaleWidth = (int)Math.round(bi.getWidth() * scaleFactor); 
                final int scaleHeight = (int)Math.round(bi.getHeight() * scaleFactor); 
 
                image = bi.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_FAST); 
 
                previousBounds = getParent().getSize(); 
                previousScaledImage = image; 
                previousImage = bi; 
                imageX = (width - image.getWidth(this)) / 2; 
                imageY = (height - image.getHeight(this)) / 2; 
            } 
 
            g.drawImage(image, imageX, imageY, this); 
            g.setColor(new Color(255, 255, 255, 200)); 
            g.fillRect(0, 0, width + 1, height + 1); 
        } 
    } 
 
    private double getScaleFactorToFill(final Dimension masterSize, final Dimension targetSize) 
    { 
        final double dScaleWidth = getScaleFactor(masterSize.width, targetSize.width); 
        final double dScaleHeight = getScaleFactor(masterSize.height, targetSize.height); 
        final double dScale = Math.max(dScaleHeight, dScaleWidth); 
        return dScale; 
    } 
 
    private double getScaleFactor(final int iMasterSize, final int iTargetSize) 
    { 
        double dScale = 1; 
        if(iMasterSize > iTargetSize) 
        { 
            dScale = (double)iTargetSize / (double)iMasterSize;  
        } 
        else 
        { 
            dScale = (double)iTargetSize / (double)iMasterSize; 
        } 
        return dScale; 
    } 

    public void setBackgroundVisible(final boolean selected)
    {
        backgroundVisible = selected;
        repaint();
    }
}
