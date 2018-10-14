/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.visuals;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class JAlphaPanel extends JPanel
{

    private float alpha = 1F;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public JAlphaPanel()
    {
        super();
    }

    public JAlphaPanel(boolean isDoubleBuffered)
    {
        super(isDoubleBuffered);
    }

    public JAlphaPanel(LayoutManager layout, boolean isDoubleBuffered)
    {
        super(layout, isDoubleBuffered);
    }

    public JAlphaPanel(LayoutManager layout)
    {
        super(layout);
    }

    public float getAlpha()
    {
        return alpha;
    }

    public void setAlpha(float alpha)
    {
        float from = this.alpha;
        if(from != alpha)
        {
            if(from == 1F)
                setOpaque(false);
            else if(alpha == 1F)
                setOpaque(true);
            this.alpha = alpha;
            repaint();
        }
    }

    public float getInheritedAlpha()
    {
        float a = alpha;
        Component c = this;
        while((c = c.getParent()) != null)
        {
            if(c instanceof JAlphaPanel)
            {
                a = Math.min(a, ((JAlphaPanel)c).getAlpha());
            }
        }
        return a;
    }

    /**
     * Overriden paint method to take into account the alpha setting
     *
     * @param g
     */
    @Override
    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;

        Composite old = g2d.getComposite();
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getInheritedAlpha());
        g2d.setComposite(comp);
        super.paint(g2d);

        g2d.setComposite(old);
    }

}
