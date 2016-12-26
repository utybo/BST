/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.TextNode;

@SuppressWarnings("serial")
public class NodePanel extends JScrollablePanel
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final JLabel textLabel;
    private final IMGClient imageClient;
    private boolean backgroundVisible = true;

    private Dimension previousBounds;
    private Image previousScaledImage;
    private BufferedImage previousImage;
    private int imageX, imageY;

    public NodePanel(final IMGClient imageClient)
    {
        this.imageClient = imageClient;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        textLabel = new JLabel(Lang.get("story.problem"));
        textLabel.setVerticalAlignment(SwingConstants.TOP);
        textLabel.setFont(new JTextArea().getFont());
        textLabel.setForeground(Color.BLACK);
        textLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(textLabel, BorderLayout.CENTER);
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
            setTextColor(Color.BLACK);
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
        System.out.println(textNode.getTagMap());
    }

    public void setText(final String text)
    {
        textLabel.setText(text);
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
                System.err.println("COLOR DOES NOT EXIST : " + color);
                e.printStackTrace();
            }
        }
        if(c != null)
        {
            textLabel.setForeground(c);
        }
        else
        {
            textLabel.setForeground(Color.BLACK);
        }
    }

    public void setTextColor(final Color color)
    {
        textLabel.setForeground(color);
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
