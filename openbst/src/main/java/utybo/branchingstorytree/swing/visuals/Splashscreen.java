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
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.JXPanel;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.TimelineScenario;
import org.pushingpixels.trident.callback.TimelineCallback;

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

        System.setProperty("sun.java2d.opengl", "true");
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

            Splashscreen s = new Splashscreen(null);
            s.setVisible(true);
            s.play();
        });
    }

    private Timeline timeline;
    private TimelineScenario scenario;
    private Timeline tl2;
    private JLabel lblLogo;
    private JLabel lblText;
    private final JLabel lblZrrk;
    private JXPanel panLogo, panZrrk, panBlue;
    private JBackgroundPanel panBackground;
    private boolean locked = false;
    private BufferedImage background = null;

    public Splashscreen(BufferedImage splashImg)
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
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        getContentPane().setBackground(OpenBSTGUI.OPENBST_BLUE);

        JLayeredPane layers = new JLayeredPane();
        layers.setBackground(new Color(0, 0, 0, 0));
        layers.setBounds($(0, 0, 400, 120));
        getContentPane().add(layers);

        if(splashImg != null)
        {
            background = splashImg;
            panBackground = new JBackgroundPanel(background, Image.SCALE_SMOOTH);
            panBackground.setApplyColor(false);
            panBackground.setBounds($(0, 0, 400, 120));
            layers.add(panBackground, new Integer(0));
        }

        panBlue = new JXPanel();
        panBlue.setBackground(OpenBSTGUI.OPENBST_BLUE);
        panBlue.setBounds($(0, 0, 400, 120));
        layers.add(panBlue, new Integer(1));

        panLogo = new JXPanel();
        panLogo.setLayout(null);
        panLogo.setBackground(OpenBSTGUI.OPENBST_BLUE);
        lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setVerticalAlignment(SwingConstants.CENTER);
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
        lblLogo.setBounds($(0, 0, 400, 100));
        panLogo.add(lblLogo);
        panLogo.setBounds($(0, 0, 400, 120));
        layers.add(panLogo, new Integer(2));

        lblText = new JLabel(Lang.get("splash.loading"));
        lblText.setForeground(Color.WHITE);
        lblText.setVerticalAlignment(SwingConstants.BOTTOM);
        lblText.setBounds($(10, 90, 322, 20));
        layers.add(lblText, new Integer(10));

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
        layers.add(panZrrk, new Integer(11));

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

        if(background != null)
        {
            Timeline revealTl = new Timeline(panLogo);
            revealTl.setDuration(400L);
            revealTl.setEase(new BezierEase(0.4F, 0F, 1F, 1F));
            revealTl.addPropertyToInterpolate("bounds", finalLogoBounds,
                    new Rectangle(0, $(-120), finalLogoBounds.width, finalLogoBounds.height));
            scenario = new TimelineScenario.Sequence();
            timeline.addCallback(new TimelineCallback()
            {

                @Override
                public void onTimelinePulse(float arg0, float arg1)
                {}

                @Override
                public void onTimelineStateChanged(TimelineState arg0, TimelineState arg1,
                        float arg2, float arg3)
                {
                    if(arg1 == TimelineState.DONE)
                        VisualsUtils.invokeSwingAndWait(() -> panBlue.setVisible(false));
                }
            });
            scenario.addScenarioActor(timeline);
            Timeline wait = new Timeline(new Object());
            wait.setDuration(750L);
            scenario.addScenarioActor(wait);
            scenario.addScenarioActor(revealTl);
        }
    }

    public void play()
    {
        if(scenario != null)
            scenario.play();
        else
            timeline.play();
        tl2.playLoop(RepeatBehavior.REVERSE);
    }

    public static Splashscreen start(BufferedImage splashImg)
    {
        List<Splashscreen> sc = Collections.synchronizedList(new ArrayList<>());
        VisualsUtils.invokeSwingAndWait(() ->
        {
            Splashscreen s = new Splashscreen(splashImg);
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
        if(scenario != null)
            scenario.suspend();
        else
            timeline.end();
        tl2.end();
    }

    public void setText(String text)
    {
        if(!locked)
            lblText.setText(text);
    }

    public void lock()
    {
        locked = true;
    }
}
