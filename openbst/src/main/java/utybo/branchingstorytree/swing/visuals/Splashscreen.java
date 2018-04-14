package utybo.branchingstorytree.swing.visuals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.JXPanel;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;

import utybo.branchingstorytree.swing.Icons;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.OpenBSTGUI;
import utybo.branchingstorytree.swing.VisualsUtils;
import utybo.branchingstorytree.swing.utils.BezierEase;
import utybo.branchingstorytree.swing.utils.Lang;

@SuppressWarnings("serial")
public class Splashscreen extends JFrame
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            try
            {
                UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
                SubstanceCortex.GlobalScope.setColorizationFactor(1.0D);
                Icons.loadScalingFactor();
            }
            catch(UnsupportedLookAndFeelException e)
            {
                e.printStackTrace();
            }

            Splashscreen s = new Splashscreen();
            s.setVisible(true);
            s.play();
        });
    }

    private Timeline timeline;
    private Timeline tl2;
    private JLabel lblLogo;
    private JLabel lblText;
    private final JLabel lblZrrk;
    private JXPanel panLogo, panZrrk;

    public Splashscreen()
    {
        setUndecorated(true);
        try
        {
            setIconImage(ImageIO.read(Splashscreen.class
                    .getResourceAsStream("/utybo/branchingstorytree/swing/logos/Logo48.png")));
        }
        catch(IOException e)
        {
            OpenBST.LOG.error(e);
        }
        setSize($(400, 120));
        System.out.println(getSize());
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        getContentPane().setBackground(OpenBSTGUI.OPENBST_BLUE);

        panLogo = new JXPanel();
        panLogo.setBackground(new Color(0, 0, 0, 0));
        lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        // We cannot use Icons.getImage because it is not loaded at this point.
        try
        {
            lblLogo.setIcon(new ImageIcon(ImageIO.read(Splashscreen.class
                    .getResourceAsStream("/utybo/branchingstorytree/swing/logos/FullLogoWhite"
                            + Icons.applyScaleValue(new int[] {60, 75, 90, 120}) + ".png"))));
        }
        catch(IOException e)
        {
            OpenBST.LOG.error(e);
        }
        panLogo.setLayout(new BorderLayout());
        panLogo.add(lblLogo);
        panLogo.setBounds($(0, 0, 400, 100));
        getContentPane().add(panLogo);

        lblText = new JLabel(Lang.get("splash.loading"));
        lblText.setForeground(Color.WHITE);
        lblText.setVerticalAlignment(SwingConstants.BOTTOM);
        lblText.setBounds($(10, 90, 322, 20));
        getContentPane().add(lblText);

        panZrrk = new JXPanel();
        panZrrk.setBackground(new Color(0, 0, 0, 0));
        lblZrrk = new JLabel();
        try
        {
            lblZrrk.setIcon(new ImageIcon(ImageIO.read(Splashscreen.class
                    .getResourceAsStream("/utybo/branchingstorytree/swing/logos/minizrrk"
                            + Icons.applyScaleValue(new int[] {16, 20, 24, 32}) + ".png"))));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        panZrrk.setLayout(new BorderLayout());
        panZrrk.add(lblZrrk);
        panZrrk.setBounds($(342, 94, 48, 16));
        getContentPane().add(panZrrk);

        setupAnim();
    }

    private Rectangle $(int i, int j, int k, int l)
    {
        return new Rectangle($(i), $(j), $(k), $(l));
    }

    private Dimension $(int i, int j)
    {
        return new Dimension($(i), $(j));
    }

    private int $(int i)
    {
        return (int)(Icons.getScale() * i);
    }

    private void setupAnim()
    {
        Rectangle finalLogoBounds = panLogo.getBounds();
        Rectangle origLogoBounds = new Rectangle($(-200), 0, finalLogoBounds.width,
                finalLogoBounds.height);
        panLogo.setBounds(origLogoBounds);

        float finalAlpha = 0.99F;
        float origAlpha = 0F;
        panLogo.setAlpha(origAlpha);

        Color finalCol = lblText.getForeground();
        Color orgCol = new Color(finalCol.getRed(), finalCol.getGreen(), finalCol.getBlue(), 0);
        lblText.setForeground(orgCol);

        timeline = new Timeline(panLogo);
        timeline.setDuration(400L);
        timeline.setEase(new BezierEase(0F, 0F, 0.2F, 1F));
        timeline.addPropertyToInterpolate("bounds", origLogoBounds, finalLogoBounds);
        timeline.addPropertyToInterpolate("alpha", origAlpha, finalAlpha);
        timeline.addPropertyToInterpolate(
                Timeline.<Color>property("foreground").on(lblText).from(orgCol).to(finalCol));

        panZrrk.setAlpha(0.5F);
        tl2 = new Timeline(panZrrk);
        tl2.setDuration(300L);
        tl2.setEase(new BezierEase(.25F, .1F, .25F, 1F));
        tl2.addPropertyToInterpolate("alpha", 0.2F, .99F);
    }

    public void play()
    {
        timeline.play();
        tl2.playLoop(RepeatBehavior.REVERSE);
    }

    public static Splashscreen start()
    {
        List<Splashscreen> sc = Collections.synchronizedList(new ArrayList<>());
        VisualsUtils.invokeSwingAndWait(() ->
        {
            Splashscreen s = new Splashscreen();
            s.setVisible(true);
            s.play();
            s.requestFocus();
            sc.add(s);
        });
        return sc.get(0);
    }

    public void stop()
    {
        // Used to let the render of the app breathe
        timeline.end();
        tl2.end();
    }

    public void setText(String text)
    {
        lblText.setText(text);
    }
}
