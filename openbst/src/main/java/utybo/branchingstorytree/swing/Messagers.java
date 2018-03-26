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
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;

import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel;

import net.miginfocom.swing.MigLayout;

public class Messagers
{
    public static final int OPTIONS_OK = 1, OPTIONS_OK_CANCEL = 2, OPTIONS_YES_NO = 3,
            OPTIONS_YES_NO_CANCEL = 4, OPTIONS_GENERIC_ONE = 11, OPTIONS_GENERIC_TWO = 12,
            OPTIONS_GENERIC_THREE = 13, OPTIONS_GENERIC_FOUR = 14, OPTIONS_GENERIC_FIVE = 15;

    public static final int OPTION_OK = 1, OPTION_CANCEL = 2, OPTION_YES = 3, OPTION_NO = 4;

    public static final int OPTION_ONE = 11, OPTION_TWO = 12, OPTION_THREE = 13, OPTION_FOUR = 14,
            OPTION_FIVE = 15;

    public static final int TYPE_OK = 0, TYPE_INFO = 1, TYPE_WARNING = 2, TYPE_ERROR = 3,
            TYPE_QUESTION = 4;

    public static void main(String[] args) throws InvocationTargetException, InterruptedException
    {
        SwingUtilities.invokeAndWait(() ->
        {
            try
            {
                UIManager.setLookAndFeel(new SubstanceBusinessLookAndFeel());
                SubstanceCortex.GlobalScope.setColorizationFactor(1.0D);
            }
            catch(UnsupportedLookAndFeelException e)
            {
                e.printStackTrace();
            }
        });

        Icons.load();
        SwingUtilities
                .invokeAndWait(() -> System.out.println(showMessage(null, "A simple information")));
        SwingUtilities.invokeAndWait(
                () -> System.out.println(showMessage(null, "A simple warning", TYPE_WARNING)));
        SwingUtilities.invokeAndWait(
                () -> System.out.println(showMessage(null, "A simple error", TYPE_ERROR)));

        SwingUtilities.invokeAndWait(() -> System.out.println(showConfirm(null, "Test 1 2 1 2")));

        SwingUtilities
                .invokeAndWait(() -> System.out.println(showInput(null, "What's your name?")));
        try
        {
            failingFunc();
        }
        catch(IOException e)
        {
            SwingUtilities.invokeLater(() -> showException(null, "An exception was caught", e));
        }
    }

    private static void failingFunc() throws IOException
    {
        throw new IOException("Dickens");
    }

    /**
     * @wbp.parser.entryPoint
     */
    public static <T> T createMessagerDialog(Window parent, Color bannerColor, Icon bannerIcon,
            String windowTitle, String mainText, JPanel additional, String[] optionsNames,
            T[] optionsReturnValues, T defaultReturnValue)
    {
        // This ensures an appropriate dialog is created
        JDialog dialog = parent instanceof Dialog ? new JDialog((Dialog)parent)
                : parent instanceof Frame ? new JDialog((Frame)parent) : new JDialog(parent);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);

        dialog.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(bannerColor);
        sidePanel.setMinimumSize(new Dimension((int)(Icons.getScale() * 48), 10));
        sidePanel.setPreferredSize(new Dimension(
                (int)(Icons.getScale() * (adaptSize(bannerIcon.getIconWidth()))), 10));
        dialog.getContentPane().add(sidePanel, BorderLayout.WEST);
        sidePanel.setLayout(new BorderLayout(0, 0));

        JLabel lblIcon = new JLabel(bannerIcon);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        sidePanel.add(lblIcon);

        JPanel panel = new JPanel();
        dialog.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new MigLayout("insets dialog, hidemode 2", "[grow]", "[][][][grow][]"));

        JLabel lblBigTitle = new JLabel(windowTitle);
        lblBigTitle
                .setFont(lblBigTitle.getFont().deriveFont(1.75F * lblBigTitle.getFont().getSize()));
        panel.add(lblBigTitle, "cell 0 0");
        if(windowTitle == null)
            lblBigTitle.setVisible(false);

        JLabel lblSwagSwag = new JLabel(mainText);
        panel.add(lblSwagSwag, "cell 0 1");

        if(additional != null)
            panel.add(additional, "cell 0 2,grow");

        ArrayList<T> returnValue = new ArrayList<>();
        JButton btn;
        for(int i = 0; i < optionsNames.length; i++)
        {
            String name = optionsNames[i];
            btn = new JButton(name);
            int j = i;
            btn.addActionListener(e ->
            {
                returnValue.add(optionsReturnValues[j]);
                dialog.dispose();
            });
            panel.add(btn, "cell 0 4" + (i == 0 ? ",alignx trailing,flowx" : ""));
        }

        dialog.setMinimumSize(
                new Dimension((int)(Icons.getScale() * 300), (int)(100 * Icons.getScale())));
        dialog.setMaximumSize(
                new Dimension((int)(450 * Icons.getScale()), (int)(800 * Icons.getScale())));
        dialog.pack();
        if(dialog.getWidth() > 450 * Icons.getScale())
        {
            lblSwagSwag.setText("<html><body style='width: " + (int)(400 * Icons.getScale())
                    + "px'>" + lblSwagSwag.getText());
            dialog.pack();
        }
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        Toolkit.getDefaultToolkit().beep();
        dialog.setVisible(true);
        return returnValue.size() > 0 ? returnValue.get(0) : defaultReturnValue;
    }

    private static float adaptSize(int iconWidth)
    {
        switch((int)(iconWidth / Icons.getScale()))
        {
        case 32:
            return 48;
        case 40:
            return 48;
        case 48:
            return 64;
        default:
            return iconWidth + 16;
        }
    }

    public static int showMessage(Window parent, String message)
    {
        return showMessage(parent, message, TYPE_INFO);
    }

    public static int showMessage(Window parent, String message, int type)
    {
        return showMessage(parent, message, type, null);
    }

    public static int showMessage(Window parent, String message, int type, String title)
    {
        return showMessage(parent, message, type, title, getDefaultIcon(type));
    }

    public static int showMessage(Window parent, String message, int type, String title, Icon icon)
    {
        return createMessagerDialog(parent, getColor(type), icon, title, message, null,
                getOptionNames(OPTIONS_OK), getReturnValues(OPTIONS_OK), OPTION_CANCEL);
    }

    public static int showConfirm(Window parent, String message, int options, int type,
            String title, Icon icon)
    {
        return createMessagerDialog(parent, getColor(type), icon, title, message, null,
                getOptionNames(options), getReturnValues(options), OPTION_CANCEL);
    }

    public static int showConfirm(Window parent, String message, int options, int type,
            String title)
    {
        return showConfirm(parent, message, options, type, title, getDefaultIcon(type));
    }

    public static int showConfirm(Window parent, String message, int options, int type)
    {
        return showConfirm(parent, message, options, type, null);
    }

    public static int showConfirm(Window parent, String message, int options)
    {
        return showConfirm(parent, message, options, TYPE_QUESTION);
    }

    public static int showConfirm(Window parent, String message)
    {
        return showConfirm(parent, message, OPTIONS_YES_NO);
    }

    public static String showInput(Window parent, String message, int options, int type,
            String title, Icon icon)
    {
        JTextField jtf = new JTextField();
        ArrayList<Boolean> b = new ArrayList<>();
        jtf.addActionListener(e ->
        {
            // Get the dialog using a dirty hack
            Component lastContainer = jtf;
            while(!(lastContainer instanceof JDialog))
                lastContainer = lastContainer.getParent();
            b.add(true);

            ((JDialog)lastContainer).dispose();
        });

        JPanel pan = new JPanel(new MigLayout("", "[grow]", "[]"));
        pan.add(jtf, "ax center, w 75%");
        Integer output = createMessagerDialog(parent, getColor(type), icon, title, message, pan,
                getOptionNames(options), getReturnValues(options), null);
        if(b.size() == 0 && (output == null || output == OPTION_CANCEL || output == OPTION_NO))
            return null;
        else
            return jtf.getText();
    }

    public static String showInput(Window parent, String message, int options, int type,
            String title)
    {
        return showInput(parent, message, options, type, title, getDefaultIcon(type));
    }

    public static String showInput(Window parent, String message, int options, int type)
    {
        return showInput(parent, message, options, type, null);
    }

    public static String showInput(Window parent, String message, int options)
    {
        return showInput(parent, message, options, TYPE_QUESTION);
    }

    public static String showInput(Window parent, String message)
    {
        return showInput(parent, message, OPTIONS_OK);
    }

    public static void showException(Window parent, String message, Exception e)
    {
        Writer w = new StringWriter();
        e.printStackTrace(new PrintWriter(w));
        String stack = w.toString();

        JTextArea jta = new JTextArea(stack);
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        jta.setEditable(false);
        JPanel pan = new JPanel(new MigLayout("", "[grow]", "[]"));
        JScrollPane scrollPane = new JScrollPane(jta);
        scrollPane.setMinimumSize(
                new Dimension((int)(Icons.getScale() * 500), (int)(Icons.getScale() * 100)));

        scrollPane.setBorder(new LineBorder(pan.getBackground().darker(), 1, true));
        pan.add(scrollPane, "ax center, w 75%");
        createMessagerDialog(parent, getColor(TYPE_ERROR), getDefaultIcon(TYPE_ERROR),
                "Exception caught", message, pan, getOptionNames(OPTIONS_OK),
                getReturnValues(OPTIONS_OK), OPTION_CANCEL);
    }

    private static Color getColor(int type)
    {
        switch(type)
        {
        case TYPE_INFO:
            return Color.BLUE.darker();
        case TYPE_QUESTION:
            return Color.DARK_GRAY;
        case TYPE_WARNING:
            return new Color(255, 175, 0);
        case TYPE_ERROR:
            return Color.RED.darker();
        default:
            throw new IllegalArgumentException("Unknown type : " + type);
        }
    }

    private static Integer[] getReturnValues(int options)
    {
        switch(options)
        {
        case OPTIONS_OK:
            return new Integer[] {OPTION_OK};
        case OPTIONS_OK_CANCEL:
            return new Integer[] {OPTION_OK, OPTION_CANCEL};
        case OPTIONS_YES_NO:
            return new Integer[] {OPTION_YES, OPTION_NO};
        case OPTIONS_YES_NO_CANCEL:
            return new Integer[] {OPTION_YES, OPTION_NO, OPTION_CANCEL};
        default:
            throw new IllegalArgumentException("Unknown options values for " + options);
        }
    }

    private static String[] getOptionNames(int options)
    {
        switch(options)
        {
        case OPTIONS_OK:
            return new String[] {"OK"};
        case OPTIONS_OK_CANCEL:
            return new String[] {"OK", "Cancel"};
        case OPTIONS_YES_NO:
            return new String[] {"Yes", "No"};
        case OPTIONS_YES_NO_CANCEL:
            return new String[] {"Yes", "No", "Cancel"};
        default:
            throw new IllegalArgumentException("Unknown options name for " + options);
        }
    }

    private static Icon getDefaultIcon(int type)
    {
        switch(type)
        {
        case TYPE_INFO:
            return new ImageIcon(Icons.getImage("ok", 32));
        case TYPE_QUESTION:
            return new ImageIcon(Icons.getImage("quest", 32));
        case TYPE_WARNING:
            return new ImageIcon(Icons.getImage("warn", 32));
        case TYPE_ERROR:
            return new ImageIcon(Icons.getImage("err", 32));
        default:
            throw new IllegalArgumentException("Unknown type : " + type);
        }
    }
}
