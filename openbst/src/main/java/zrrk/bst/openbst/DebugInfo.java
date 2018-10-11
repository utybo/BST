/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;
import zrrk.bst.openbst.utils.Lang;

public class DebugInfo extends JDialog
{
    private static final long serialVersionUID = 1L;
    private String text;

    private DebugInfo(JFrame parent)
    {
        super(parent);
        JPanel contentPanel = new JPanel();
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[][grow][]", "[][grow]"));

        JLabel lblIcon = new JLabel(new ImageIcon(Icons.getImage("Code", 40)));
        contentPanel.add(lblIcon, "cell 0 0");

        JLabel lblInfo = new JLabel("<html>" + Lang.get("debug.help"));
        contentPanel.add(lblInfo, "cell 1 0");

        JButton btnCopy = new JButton(Lang.get("copy"),
                new ImageIcon(Icons.getImage("Copy To Clipboard", 16)));
        btnCopy.addActionListener(e ->
        {
            try
            {
                StringSelection ss = new StringSelection(text);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
                Messagers.showMessage(OpenBST.getGUIInstance(), Lang.get("debug.logscopied"),
                        Messagers.TYPE_OK);
            }
            catch(Exception ex)
            {
                OpenBST.LOG.error("Failed to copy logs to clipboard", ex);
                Messagers.showMessage(OpenBST.getGUIInstance(),
                        Lang.get("debug.copyfailed"),
                        Messagers.TYPE_ERROR);
            }
        });
        contentPanel.add(btnCopy, "cell 2 0");

        JPanel switcherPanel = new JPanel();
        contentPanel.add(switcherPanel, "cell 0 1 3 1,grow");
        switcherPanel.setLayout(new CardLayout(0, 0));

        JPanel waitingPanel = new JPanel();
        switcherPanel.add(waitingPanel, "wait");
        waitingPanel.setLayout(new MigLayout("ay center", "[418px]", "[][14px]"));

        JLabel lblPleaseWait = new JLabel(Lang.get("debug.wait"));
        waitingPanel.add(lblPleaseWait, "cell 0 0,alignx center");

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        waitingPanel.add(progressBar, "cell 0 1,alignx center,aligny center");

        JPanel panel = new JPanel();
        switcherPanel.add(panel, "results");
        panel.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createLineBorder(panel.getBackground().darker(), 1));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane);

        JTextArea textArea = new JTextArea();
        textArea.setFont(Font.decode(Font.MONOSPACED).deriveFont((float) textArea.getFont().getSize()));
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        scrollPane.setViewportView(textArea);

        SwingWorker<String, String> worker = new SwingWorker<String, String>()
        {

            @Override
            protected String doInBackground() throws Exception
            {
                StringBuilder sb = new StringBuilder();
                publish(Lang.get("debug.gathersys"));
                sb.append("--- SYSTEM PROPERTIES ---\n");
                String[] properties = new String[] {"file.separator", "java.home", "java.vendor",
                        "java.vendor.url", "java.version", "os.arch", "os.name", "os.version",
                        "user.dir", "user.home"};
                for(String s : properties)
                    sb.append(s + " : " + System.getProperty(s) + "\n");

                sb.append("\n");
                publish(Lang.get("debug.startupstats"));
                sb.append("--- STARTUP INFO ---\n");
                sb.append(OpenBST.getStartupInfo());
                publish(Lang.get("debug.getlogs"));
                sb.append("--- LOGS ---\n");
                sb.append(OpenBST.getAllLogs());

                return sb.toString();
            }

            @Override
            protected void process(List<String> chunks)
            {
                String s = chunks.get(chunks.size() - 1);
                lblPleaseWait.setText(s);
            }

            @Override
            protected void done()
            {
                try
                {
                    text = get();
                    textArea.setText(text);
                }
                catch(Exception e)
                {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    text = "Failed to create debug information.\n\n" + sw.toString();
                    lblPleaseWait.setText(text);
                    OpenBST.LOG.error("Failed to create debug information", e);
                }

                ((CardLayout)switcherPanel.getLayout()).show(switcherPanel, "results");
            }
        };

        setTitle(Lang.get("debug.title"));
        setSize((int)(400 * Icons.getScale()), (int)(300 * Icons.getScale()));
        setLocationRelativeTo(parent);
        setModalityType(ModalityType.APPLICATION_MODAL);
        worker.execute();
        setVisible(true);
    }

    public static void launch(JFrame parent)
    {
        new DebugInfo(parent);

    }
}
