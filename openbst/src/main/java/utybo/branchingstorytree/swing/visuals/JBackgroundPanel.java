package utybo.branchingstorytree.swing.visuals;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JBackgroundPanel extends JPanel
{
    private Image previousScaledImage;
    private Image previousImage;
    private int imageX, imageY;
    private Dimension previousBounds;
    private BufferedImage image;
    private int scaling;
    private boolean dark;

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
        if(previousBounds != null && previousScaledImage != null && getParent().getSize().equals(previousBounds) && previousImage == this.image)
        {
            image = previousScaledImage;
        }
        else
        {
            final BufferedImage bi = this.image;
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

            image = bi.getScaledInstance(scaleWidth, scaleHeight, scaling);

            previousBounds = getParent().getSize();
            previousScaledImage = image;
            previousImage = bi;
            imageX = (width - image.getWidth(this)) / 2;
            imageY = (height - image.getHeight(this)) / 2;
        }

        g.drawImage(image, imageX, imageY, this);
        if(dark)
            g.setColor(new Color(0, 0, 0, 125));
        else
            g.setColor(new Color(255, 255, 255, 125));
        g.fillRect(0, 0, width + 1, height + 1);
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

    public void setDark(boolean b)
    {
        dark = b;
        repaint();
    }
}
