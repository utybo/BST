package utybo.branchingstorytree.swing.visuals;

import java.awt.Color;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.utils.Lang;

@SuppressWarnings("serial")
public class JBannerPanel extends JPanel
{
    private Color c;
    private Consumer<Boolean> callback = b -> this.setBackground(b ? c.darker() : c.brighter());

    public JBannerPanel(Icon icon, Color c, String text, JComponent btn, boolean hideButton)
    {
        c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 150);
        this.c = c;
        setBackground(c.brighter());
        OpenBST.getInstance().addDarkModeCallback(callback);
        this.setLayout(new MigLayout("gap 10px", "[][grow][]", "[]"));

        JLabel label = new JLabel(icon);
        this.add(label, "cell 0 0");

        JLabel lblNewLabel = new JLabel("<html>" + text);
        this.add(lblNewLabel, "cell 1 0,aligny top");

        JButton btnHide = new JButton(Lang.get("hide"));
        btnHide.addActionListener(e ->
        {
            this.setVisible(false);
            OpenBST.getInstance().removeDarkModeCallbback(callback);
        });

        if(btn != null)
            this.add(btn, "flowy,cell 2 0,alignx center, aligny center");
        if(hideButton)
            this.add(btnHide, "cell 2 0,alignx center, aligny center");
    }
}
