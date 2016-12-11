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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.script.VariableRegistry;

@SuppressWarnings("serial")
public class VariableWatchDialog extends JDialog
{
    private final JPanel contentPanel = new JPanel();
    private DefaultTableModel model;
    private StoryPanel parent;
    private boolean wasDeathNotified;

    /**
     * Create the dialog.
     */
    public VariableWatchDialog(StoryPanel parent)
    {
        super(parent.parentWindow);
        setTitle(parent.getTitle() + " -- Variable Watcher");
        setModalityType(ModalityType.MODELESS);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if(!wasDeathNotified)
                {
                    parent.variableWatchClosing();
                }
                else
                {
                    dispose();
                }
            }
        });

        this.parent = parent;
        setBounds(100, 100, 455, 298);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[grow][]", "[][][grow]"));

        JLabel lblthisDialogAllows = new JLabel("<html>This dialog allows you to check the value of all the variables. If you got here by mistake, no worries, just close this dialog!");
        lblthisDialogAllows.setIcon(new ImageIcon(OpenBST.addonSearchMediumImage));
        contentPanel.add(lblthisDialogAllows, "cell 0 0,aligny top");

        JButton btnRefresh = new JButton("Refresh", new ImageIcon(OpenBST.refreshImage));
        btnRefresh.addActionListener(e -> refresh());
        contentPanel.add(btnRefresh, "cell 1 0,aligny center");

        JSeparator separator = new JSeparator();
        contentPanel.add(separator, "cell 0 1 2 1,growx");

        JScrollPane scrollPane = new JScrollPane();
        contentPanel.add(scrollPane, "cell 0 2 2 1,grow");

        JTable table = new JTable();
        model = new DefaultTableModel()
        {
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        table.setModel(model);
        scrollPane.setViewportView(table);
        refresh();

        setSize(450, 300);
        setLocationRelativeTo(parent.parentWindow);
    }

    private void refresh()
    {
        Vector<Vector<String>> finalVector = new Vector<>();
        TreeMap<String, String> map = generateFullMap();
        for(String varName : map.keySet())
        {
            Vector<String> v = new Vector<>();
            v.add(varName);
            v.add(map.get(varName));
            finalVector.add(v);
        }
        Vector<String> columnsName = new Vector<String>();
        columnsName.add("Variable Name");
        columnsName.add("Variable Value");
        model.setDataVector(finalVector, columnsName);
    }

    private TreeMap<String, String> generateFullMap()
    {
        VariableRegistry registry = parent.story.getRegistry();
        TreeMap<String, String> fullMap = new TreeMap<>();
        fullMap.putAll(registry.getAllString());
        registry.getAllInt().forEach((str, i) -> fullMap.put(str, i.toString()));
        if(fullMap.isEmpty())
        {
            fullMap.put("<no known variables>", "<no known variables>");
        }
        return fullMap;
    }

    protected void deathNotified()
    {
        wasDeathNotified = true;
    }
}
