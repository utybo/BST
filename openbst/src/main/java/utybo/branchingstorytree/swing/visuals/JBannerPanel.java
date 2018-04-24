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
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.swing.OpenBSTGUI;
import utybo.branchingstorytree.swing.utils.Lang;

public class JBannerPanel extends JPanel
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Color c;
    private final Consumer<Boolean> callback = b -> setBackground(
            b ? getColor().darker() : getColor().brighter());
    private boolean canBeHidden;

    public JBannerPanel(Icon icon, Color c, String text, JComponent btn, boolean hideButton,
            JComponent... otherButtons)
    {
        canBeHidden = hideButton;
        c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 150);
        this.c = c;
        setBackground(c.brighter());
        OpenBSTGUI.addDarkModeCallback(callback);
        setLayout(new MigLayout("gap 10px", "[][grow][]", "[]"));

        JLabel label = new JLabel(icon);
        this.add(label, "cell 0 0");

        JLabel lblNewLabel = new JLabel("<html>" + text);
        this.add(lblNewLabel, "cell 1 0,aligny center");

        JButton btnHide = new JButton(Lang.get("hide"));
        btnHide.addActionListener(e ->
        {
            setVisible(false);
            OpenBSTGUI.removeDarkModeCallbback(callback);
        });

        if(btn != null)
        {
            this.add(btn, "flowy,cell 2 0,alignx center, aligny center");
            if(otherButtons.length > 0)
                for(JComponent cx : otherButtons)
                    this.add(cx, "cell 2 0,alignx center, aligny center");
        }
        if(hideButton)
        {
            this.add(btnHide, "cell 2 0,alignx center, aligny center");
        }
    }

    private Color getColor()
    {
        return c;
    }
    
    public boolean isHideable()
    {
        return canBeHidden;
    }
}
