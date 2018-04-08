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
import java.awt.CardLayout;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.swing.Icons;
import utybo.branchingstorytree.swing.Messagers;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.utils.BSTPackager;
import utybo.branchingstorytree.swing.utils.Lang;

public class PackageDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    private JTextField textField;
    private JTextField textField_1;
    private JLabel lblCurrent;
    private JPanel panelWorking;
    private JProgressBar progressBar;

    /**
     * Create the dialog.
     */
    public PackageDialog(OpenBST parent)
    {
        super(parent);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setTitle(Lang.get("pkg.title"));

        JPanel wrapperPanel = new JPanel();
        getContentPane().add(wrapperPanel, BorderLayout.CENTER);
        wrapperPanel.setLayout(new CardLayout(0, 0));

        JPanel panel = new JPanel();
        wrapperPanel.add(panel, "settings");
        panel.setLayout(new MigLayout("", "[][grow]", "[][][][grow]"));

        JLabel lblToPackage = new JLabel(Lang.get("pkg.in"));
        panel.add(lblToPackage, "cell 0 0,alignx trailing");

        textField = new JTextField();
        panel.add(textField, "flowx,cell 1 0,growx");
        textField.setColumns(10);

        JButton button = new JButton(Lang.get("pkg.select"));
        button.addActionListener(e ->
        {
            FileDialog fd = new FileDialog(parent, Lang.get("pkg.selin"), FileDialog.LOAD);
            fd.setVisible(true);
            if(fd.getFile() != null)
            {
                textField.setText(fd.getDirectory() + fd.getFile());
            }
        });
        panel.add(button, "cell 1 0");

        JLabel lblOutput = new JLabel(Lang.get("pkg.out"));
        panel.add(lblOutput, "cell 0 1,alignx trailing");

        textField_1 = new JTextField();
        panel.add(textField_1, "flowx,cell 1 1,growx");
        textField_1.setColumns(10);

        JButton button_1 = new JButton(Lang.get("pkg.select"));
        button_1.addActionListener(e ->
        {
            FileDialog fd = new FileDialog(parent, Lang.get("pkg.selout"), FileDialog.SAVE);
            fd.setVisible(true);
            if(fd.getFile() != null)
            {
                textField_1.setText(fd.getDirectory() + fd.getFile()
                        + (fd.getFile().endsWith(".bsp") ? "" : ".bsp"));
            }
        });
        panel.add(button_1, "cell 1 1");

        JLabel lblaBstPackage = new JLabel(Lang.get("pkg.help"));
        panel.add(lblaBstPackage, "cell 0 2 2 1");

        JPanel panel_1 = new JPanel();
        panel.add(panel_1, "cell 0 3 2 1,growx,aligny bottom");
        FlowLayout flowLayout = (FlowLayout)panel_1.getLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);

        JButton btnStartPackaging = new JButton(Lang.get("pkg.start"));
        btnStartPackaging.addActionListener(e ->
        {
            File in = new File(textField.getText());
            File out = new File(textField_1.getText());
            CardLayout cl = (CardLayout)wrapperPanel.getLayout();
            cl.show(wrapperPanel, "loading");
            SwingWorker<Object, String> sw = new SwingWorker<Object, String>()
            {
                @Override
                protected Object doInBackground() throws Exception
                {
                    BSTPackager.toPackage(in, new FileOutputStream(out), new HashMap<>(),
                            s -> publish(s));
                    return null;
                }

                @Override
                protected void process(List<String> chunks)
                {
                    lblCurrent.setText(chunks.get(chunks.size() - 1));
                }

                @Override
                protected void done()
                {
                    try
                    {
                        get();
                    }
                    catch(ExecutionException | InterruptedException e)
                    {
                        Messagers.showException(PackageDialog.this,
                                "<html>Something bad happened and the packaging process failed", e);
                        cl.show(wrapperPanel, "settings");
                    }
                    progressBar.setIndeterminate(false);
                    progressBar.setMaximum(1);
                    progressBar.setValue(1);
                    Messagers.showMessage(parent, "Packaged succesfully!");
                    dispose();
                }
            };
            sw.execute();
        });
        panel_1.add(btnStartPackaging);

        JButton btnCancel = new JButton(Lang.get("pkg.cancel"));
        btnCancel.addActionListener(e -> dispose());
        panel_1.add(btnCancel);

        panelWorking = new JPanel();
        wrapperPanel.add(panelWorking, "loading");
        panelWorking.setLayout(new MigLayout("", "[grow]", "[][][]"));

        JLabel lblPackagingPleaseWait = new JLabel(Lang.get("pkg.inprogress"));
        panelWorking.add(lblPackagingPleaseWait, "cell 0 0");

        lblCurrent = new JLabel("Starting...");
        panelWorking.add(lblCurrent, "cell 0 1");

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panelWorking.add(progressBar, "cell 0 2,growx");

        setSize((int)(Icons.getScale() * 450), (int)(Icons.getScale() * 300));
        setLocationRelativeTo(parent);
    }

}
