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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.swing.Icons;
import utybo.branchingstorytree.swing.Messagers;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.utils.BSTPackager;

public class EmbedDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    private JTextField txtTopackage;
    private JTextField txtSplash;
    private JTextField txtObstJar;
    private JTextField txtSaveLoc;
    private JLabel lblStatus;

    public EmbedDialog()
    {
        super(OpenBST.getGUIInstance());
        setTitle("Embedded file creator");
        setModalityType(ModalityType.APPLICATION_MODAL);

        CardLayout cl = new CardLayout(0, 0);
        JPanel wrapper = new JPanel(cl);
        getContentPane().add(wrapper, BorderLayout.CENTER);

        JPanel propertiesPanel = new JPanel();
        wrapper.add(propertiesPanel, "prop");
        propertiesPanel.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][grow][]"));

        JLabel lblFileToPackage = new JLabel("File to package :");
        propertiesPanel.add(lblFileToPackage, "cell 0 0,alignx trailing");

        txtTopackage = new JTextField();
        propertiesPanel.add(txtTopackage, "flowx,cell 1 0,growx");
        txtTopackage.setColumns(10);

        JLabel lblSplashscreen = new JLabel("Splashscreen :");
        propertiesPanel.add(lblSplashscreen, "cell 0 1,alignx trailing");

        txtSplash = new JTextField();
        propertiesPanel.add(txtSplash, "flowx,cell 1 1,growx");
        txtSplash.setColumns(10);

        JLabel lblleaveEmptyIf = new JLabel(
                "<html>(leave empty if you do not wish to use the custom splashscreen mode)");
        propertiesPanel.add(lblleaveEmptyIf, "cell 0 2 2 1");

        JButton button = new JButton("...");
        button.addActionListener(e ->
        {
            FileDialog fd = new FileDialog(EmbedDialog.this, "Select a BST or BSP file",
                    FileDialog.LOAD);
            fd.setVisible(true);
            if(fd.getFile() != null)
            {
                txtTopackage.setText(fd.getDirectory() + fd.getFile());
            }
        });
        propertiesPanel.add(button, "cell 1 0");

        JButton button_1 = new JButton("...");
        button_1.addActionListener(e ->
        {
            FileDialog fd = new FileDialog(EmbedDialog.this, "Select an image", FileDialog.LOAD);
            fd.setVisible(true);
            if(fd.getFile() != null)
            {
                txtSplash.setText(fd.getDirectory() + fd.getFile());
            }
        });
        propertiesPanel.add(button_1, "cell 1 1");

        JSeparator separator = new JSeparator();
        propertiesPanel.add(separator, "cell 0 3 2 1,growx");

        JLabel lblOpenbstJarTo = new JLabel(
                "<html>OpenBST JAR to modify (do not use the one you are currently running! Download a new one from the official website, link in the About page)");
        propertiesPanel.add(lblOpenbstJarTo, "cell 0 4 2 1");

        txtObstJar = new JTextField();
        propertiesPanel.add(txtObstJar, "flowx,cell 0 5 2 1,growx");
        txtObstJar.setColumns(10);

        JButton button_2 = new JButton("...");
        button_2.addActionListener(e ->
        {
            FileDialog fd = new FileDialog(EmbedDialog.this, "Select an OpenBST JAR file (.jar)",
                    FileDialog.LOAD);
            fd.setVisible(true);
            if(fd.getFile() != null)
            {
                txtObstJar.setText(fd.getDirectory() + fd.getFile());
            }
        });
        propertiesPanel.add(button_2, "cell 0 5 2 1");

        JLabel lblExplanation = new JLabel("Save new JAR to :");
        propertiesPanel.add(lblExplanation, "cell 0 6,alignx trailing");

        txtSaveLoc = new JTextField();
        propertiesPanel.add(txtSaveLoc, "flowx,cell 1 6,growx");
        txtSaveLoc.setColumns(10);

        JButton button_3 = new JButton("...");
        button_3.addActionListener(e ->
        {
            FileDialog fd = new FileDialog(EmbedDialog.this,
                    "Select where the executable .jar file should be saved", FileDialog.SAVE);
            fd.setVisible(true);
            if(fd.getFile() != null)
            {
                txtSaveLoc.setText(fd.getDirectory() + fd.getFile()
                        + (fd.getFile().endsWith(".jar") ? "" : ".jar"));
            }
        });
        propertiesPanel.add(button_3, "cell 1 6");

        JButton btnOk = new JButton("Start");
        btnOk.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(txtTopackage.getText().isEmpty())
                {
                    Messagers.showMessage(EmbedDialog.this, "File to package field is empty",
                            Messagers.TYPE_ERROR);
                    return;
                }
                if(txtSaveLoc.getText().isEmpty())
                {
                    Messagers.showMessage(EmbedDialog.this, "Save location field is empty",
                            Messagers.TYPE_ERROR);
                    return;
                }
                if(txtObstJar.getText().isEmpty())
                {
                    Messagers.showMessage(EmbedDialog.this,
                            "OpenBST JAR field is empty. "
                                    + "If you need one, you can get one from the OpenBST website.",
                            Messagers.TYPE_ERROR);
                    return;
                }

                cl.show(wrapper, "work");

                SwingWorker<Void, String> sw = new SwingWorker<Void, String>()
                {
                    @Override
                    protected Void doInBackground() throws Exception
                    {
                        // TODO BSTPackager.embed stuff
                        BSTPackager.embed(new File(txtTopackage.getText()),
                                new File(txtObstJar.getText()),
                                txtSplash.getText().isEmpty() ? null
                                        : new File(txtSplash.getText()),
                                new File(txtSaveLoc.getText()), s -> publish(s));
                        return null;
                    }

                    @Override
                    protected void process(List<String> chunks)
                    {
                        lblStatus.setText(chunks.get(chunks.size() - 1));
                    }

                    @Override
                    protected void done()
                    {
                        try
                        {
                            get();
                        }
                        catch(Exception e)
                        {
                            OpenBST.LOG.error(e);
                            Messagers.showException(EmbedDialog.this,
                                    "Unexpected exception : " + e.getMessage(), e);
                            cl.show(wrapper, "prop");
                            return;
                        }
                        Messagers.showMessage(EmbedDialog.this, "Runnable JAR created succesfully!",
                                Messagers.TYPE_OK);
                        dispose();
                    }

                    // When done, show a message if everything went fine
                    // or show exception,
                    // If fine, close dialog
                    // If exception, go back to the properties screen.
                };
                sw.execute();
            }
        });
        propertiesPanel.add(btnOk, "cell 1 8,alignx trailing");

        JPanel workingPanel = new JPanel();
        wrapper.add(workingPanel, "work");
        workingPanel.setLayout(new MigLayout("", "[grow]", "[][grow]"));

        JLabel lblpleaseWaitOpenbst = new JLabel(
                "<html>Please wait, OpenBST is working on your files...");
        workingPanel.add(lblpleaseWaitOpenbst, "cell 0 0");

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        workingPanel.add(progressBar, "flowy,cell 0 1,growx,aligny center");

        lblStatus = new JLabel("Status");
        workingPanel.add(lblStatus, "cell 0 1,alignx center");

        setSize((int)(Icons.getScale() * 450), (int)(Icons.getScale() * 300));
        setLocationRelativeTo(OpenBST.getGUIInstance());
    }
}
