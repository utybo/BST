/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.visuals;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("SE_BAD_FIELD_STORE")
public class JBackgroundPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private Image previousScaledImage;
    private Image previousImage;
    private int imageX, imageY;
    private Dimension previousBounds;
    private BufferedImage image;
    private final int scaling;
    private boolean dark;
    private boolean applyColor = true;

    public JBackgroundPanel(BufferedImage image, int scaling)
    {
        this.image = image;
        this.scaling = scaling;
    }

    @Override
    protected void paintComponent(final Graphics g)
    {
        super.paintComponent(g);
        Image image;
        final int width = getWidth() - 1;
        final int height = getHeight() - 1;
        if(previousBounds != null && previousScaledImage != null
                && getParent().getSize().equals(previousBounds) && previousImage == this.image)
        {
            image = previousScaledImage;
        }
        else
        {
            final BufferedImage bi = this.image;
            double scaleFactor = 1d;
            if(bi.getWidth() > bi.getHeight())
            {
                scaleFactor = getScaleFactorToFill(new Dimension(bi.getWidth(), bi.getHeight()),
                        getParent().getSize());
            }
            else if(bi.getHeight() > bi.getWidth())
            {
                scaleFactor = getScaleFactorToFill(new Dimension(bi.getWidth(), bi.getHeight()),
                        getParent().getSize());
            }
            final int scaleWidth = (int)Math.round(bi.getWidth() * scaleFactor);
            final int scaleHeight = (int)Math.round(bi.getHeight() * scaleFactor);

            image = bi.getScaledInstance(scaleWidth, scaleHeight, scaling);

            previousBounds = getParent().getSize();
            previousScaledImage = image;
            previousImage = bi;
            imageX = (width - image.getWidth(this)) / 2;
            imageY = (height - image.getHeight(this)) / 2;
        }

        g.drawImage(image, imageX, imageY, this);
        if(applyColor)
        {
            if(dark)
            {
                g.setColor(new Color(0, 0, 0, 125));
            }
            else
            {
                g.setColor(new Color(255, 255, 255, 175));
            }
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
        dScale = (double)iTargetSize / (double)iMasterSize;
        return dScale;
    }

    public void setDark(boolean b)
    {
        dark = b;
        repaint();
    }

    public void setImage(BufferedImage image)
    {
        this.image = image;
    }

    public BufferedImage getImage()
    {
        return image;
    }

    public void setApplyColor(boolean b)
    {
        applyColor = b;
    }
}
