/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.BorderUIResource;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.story.StoryNode;

@SuppressWarnings("serial")
public class StoryNodeIdComponent extends JPanel
{
    private JTextField txtStringid;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButton rdbtnUseIntegerId;
    private JSpinner spinner;
    private JRadioButton rdbtnUseStringId;
    private StoryNodesEditor nodes;
    private JLabel lblNodeStatus;

    public StoryNodeIdComponent(StoryNodesEditor sne)
    {
        nodes = sne;

        setLayout(new MigLayout("", "[][50px][][][grow]", "[][]"));

        rdbtnUseIntegerId = new JRadioButton("Use integer ID");

        rdbtnUseIntegerId.setSelected(true);
        buttonGroup.add(rdbtnUseIntegerId);
        add(rdbtnUseIntegerId, "cell 0 0");

        spinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        spinner.addChangeListener(e -> updateIdAvailability());
        rdbtnUseIntegerId.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                spinner.setEnabled(rdbtnUseIntegerId.isSelected());
                updateIdAvailability();
            }
        });
        add(spinner, "cell 1 0,growx");

        Component horizontalStrut = Box.createHorizontalStrut(20);
        add(horizontalStrut, "cell 2 0");

        rdbtnUseStringId = new JRadioButton("Use string ID");
        buttonGroup.add(rdbtnUseStringId);
        add(rdbtnUseStringId, "cell 3 0");

        txtStringid = new JTextField();
        txtStringid.getDocument().addDocumentListener(new DocumentListener()
        {

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                updateIdAvailability();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                updateIdAvailability();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                updateIdAvailability();
            }
        });
        txtStringid.setEnabled(false);
        rdbtnUseStringId.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                txtStringid.setEnabled(rdbtnUseStringId.isSelected());
                updateIdAvailability();
            }
        });
        add(txtStringid, "cell 4 0,growx");
        txtStringid.setColumns(10);
        
        System.out.println(((BorderUIResource.CompoundBorderUIResource)txtStringid.getBorder()).getOutsideBorder().getClass().getName());
        System.out.println(((BorderUIResource.CompoundBorderUIResource)txtStringid.getBorder()).getInsideBorder().getClass().getName());

        lblNodeStatus = new JLabel("");
        // TODO
        add(lblNodeStatus, "cell 0 1 5 1");

    }

    public void importIdInfo(StoryNode node)
    {
        if(node.hasTag("alias"))
        {
            rdbtnUseStringId.setSelected(true);
            txtStringid.setText(node.getTag("alias"));
        }
        else
            spinner.setValue(node.getId());

    }

    private void updateIdAvailability()
    {
        nodes.checkAllAvailable();
    }

    public String getIdentifier()
    {
        return rdbtnUseIntegerId.isSelected() ? spinner.getValue() + "" : txtStringid.getText();
    }

    public String getIdNumber()
    {
        return rdbtnUseIntegerId.isSelected() ? spinner.getValue() + "" : "*";
    }

    public String getAliasDeclaration()
    {
        return rdbtnUseStringId.isSelected() ? "::alias=" + txtStringid.getText() : "";
    }

    public boolean matches(StoryNodeIdComponent other)
    {
        if(rdbtnUseStringId.isSelected() && other.rdbtnUseStringId.isSelected())
            return txtStringid.getText().equals(other.txtStringid.getText());
        if(rdbtnUseIntegerId.isSelected() && other.rdbtnUseIntegerId.isSelected())
            return spinner.getValue().equals(other.spinner.getValue());
        return false;
    }

    public void notifyOk()
    {
        lblNodeStatus.setText("This node ID is free.");
        lblNodeStatus.setForeground(Color.GREEN.darker());
    }

    public void notifyError()
    {
        lblNodeStatus.setText("This node ID is already taken.");
        lblNodeStatus.setForeground(Color.RED.darker());
    }

    public int getRawIntegerId()
    {
        return rdbtnUseIntegerId.isSelected() ? (int)spinner.getValue() : -1;
    }

    protected void setInitialIntegerId(int id)
    {
        spinner.setValue(id);
    }
}
