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
    private JLabel textLabel;
    private IMGClient imageClient;

    public NodePanel(IMGClient imageClient)
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

    public void applyNode(BranchingStory story, TextNode textNode) throws BSTException
    {
        String text = StoryUtils.solveVariables(textNode, story);
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
            String bg = textNode.getTag("img_background");
            if("none".equals(bg))
            {
                imageClient.setBackground(null);
            }
            else
                imageClient.setBackground(bg);
        }
        System.out.println(textNode.getTagMap());
    }

    public void setText(String text)
    {
        textLabel.setText(text);
    }

    public void setTextColor(String color)
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

    public void setTextColor(Color color)
    {
        textLabel.setForeground(color);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(imageClient != null && imageClient.getCurrentBackground() != null)
        {
            System.out.println("boop");
            BufferedImage image = imageClient.getCurrentBackground();
            double scaleFactor = 1d;
            if(image.getWidth() > image.getHeight())
            {
                scaleFactor = Math.min(1d, getScaleFactorToFill(new Dimension(image.getWidth(), image.getHeight()), getParent().getSize()));
            }
            else if(image.getHeight() > image.getWidth())
            {
                scaleFactor = Math.max(1d, getScaleFactorToFill(new Dimension(image.getWidth(), image.getHeight()), getParent().getSize()));
            }
            int scaleWidth = (int)Math.round(image.getWidth() * scaleFactor);
            int scaleHeight = (int)Math.round(image.getHeight() * scaleFactor);

            Image scaled = image.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_FAST);

            int width = getWidth() - 1;
            int height = getHeight() - 1;

            int x = (width - scaled.getWidth(this)) / 2;
            int y = (height - scaled.getHeight(this)) / 2;

            g.drawImage(scaled, x, y, this);
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(0, 0, width + 1, height + 1);
        }
    }

    private double getScaleFactorToFill(Dimension masterSize, Dimension targetSize)
    {
        double dScaleWidth = getScaleFactor(masterSize.width, targetSize.width);
        double dScaleHeight = getScaleFactor(masterSize.height, targetSize.height);
        double dScale = Math.max(dScaleHeight, dScaleWidth);
        return dScale;
    }

    private double getScaleFactor(int iMasterSize, int iTargetSize)
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
}
