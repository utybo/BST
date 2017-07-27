package utybo.branchingstorytree.swing.visuals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.impl.TabClient;
import utybo.branchingstorytree.swing.utils.Lang;

public class JumpToNodeDialog extends JDialog
{
    private static final long serialVersionUID = 1L;
    private JTextField textField;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private JSpinner spinner;
    private JLabel lblNodeExistanceInfo;
    private JComboBox<String> comboBox;
    private BranchingStory mainStory;
    private TabClient client;
    /**
     * 0 = with index; 1 = with alias
     */
    private int mode;
    private JButton btnOk;

    public JumpToNodeDialog(TabClient client, BranchingStory mainStory, Consumer<StoryNode> callback)
    {
        super(OpenBST.getInstance());
        this.mainStory = mainStory;
        setModalityType(ModalityType.DOCUMENT_MODAL);
        setTitle(Lang.get("nodejump.title"));
        getContentPane().setLayout(new MigLayout("", "[][][][grow]", "[][][][][grow][]"));

        JLabel lblGoTo = new JLabel(Lang.get("nodejump.gotonode"));
        getContentPane().add(lblGoTo, "cell 1 0");

        JRadioButton rdbtnWithId = new JRadioButton(Lang.get("nodejump.withid"));
        rdbtnWithId.setHorizontalAlignment(SwingConstants.TRAILING);
        rdbtnWithId.addActionListener(e ->
        {
            textField.setEnabled(false);
            spinner.setEnabled(true);
            mode = 0;
            updateExistanceInfo();
        });
        rdbtnWithId.setSelected(true);
        buttonGroup.add(rdbtnWithId);
        getContentPane().add(rdbtnWithId, "cell 2 0");

        JLabel label = new JLabel(new ImageIcon(OpenBST.jumpBigImage));
        getContentPane().add(label, "cell 0 0 1 5,aligny top");

        spinner = new JSpinner();
        spinner.addChangeListener(e -> updateExistanceInfo());
        spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
        getContentPane().add(spinner, "cell 3 0,growx");

        JRadioButton rdbtnWithAlias = new JRadioButton(Lang.get("nodejump.withalias"));
        rdbtnWithAlias.addActionListener(e ->
        {
            textField.setEnabled(true);
            spinner.setEnabled(false);
            mode = 1;
            updateExistanceInfo();
        });
        buttonGroup.add(rdbtnWithAlias);
        getContentPane().add(rdbtnWithAlias, "cell 2 1");

        textField = new JTextField();
        textField.getDocument().addDocumentListener(new DocumentListener()
        {

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                updateExistanceInfo();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                updateExistanceInfo();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                updateExistanceInfo();
            }
        });
        textField.setEnabled(false);
        getContentPane().add(textField, "cell 3 1,growx");
        textField.setColumns(10);

        JLabel lblFromFile = new JLabel(Lang.get("nodejump.fromfile"));
        getContentPane().add(lblFromFile, "cell 1 2,alignx trailing");

        comboBox = new JComboBox<>();
        comboBox.addItemListener(e -> updateExistanceInfo());
        Vector<String> values = new Vector<>();
        values.add("<main>");
        ArrayList<String> additional = new ArrayList<String>(client.getXBFHandler().getAdditionalStoryNames());
        Collections.sort(additional);
        values.addAll(additional);
        comboBox.setSelectedItem("<main>");
        comboBox.setModel(new DefaultComboBoxModel<>(values));
        getContentPane().add(comboBox, "cell 2 2 2 1,growx");

        lblNodeExistanceInfo = new JLabel();
        getContentPane().add(lblNodeExistanceInfo, "cell 1 3 3 1,alignx trailing");

        btnOk = new JButton(Lang.get("nodejump.go"));
        btnOk.addActionListener(e ->
        {
            callback.accept(updateExistanceInfo());
            dispose();
        });
        getContentPane().add(btnOk, "flowx,cell 0 5 4 1,alignx trailing");

        JButton btnCancel = new JButton(Lang.get("cancel"));
        btnCancel.addActionListener(e -> dispose());
        getContentPane().add(btnCancel, "cell 0 5");

        updateExistanceInfo();
        
        pack();
        setLocationRelativeTo(OpenBST.getInstance());
    }

    private StoryNode updateExistanceInfo()
    {
        String storyName = comboBox.getSelectedItem().toString();
        BranchingStory story;
        if("<main>".equals(storyName))
            story = mainStory;
        else
            story = client.getXBFHandler().getAdditionalStory(storyName);
        // Story can't be null here. If it is, then there's a bug in XBF.
        boolean exists = false;
        StoryNode sn = null;
        if(mode == 0)
        {
            sn = story.getNode((Integer)spinner.getValue());
            exists = sn != null;
        }
        else
        {
            for(StoryNode n : story.getAllNodes())
            {
                if(n.hasTag("alias") && n.getTag("alias").equals(textField.getText()))
                {
                    sn = n;
                    exists = true;
                    break;
                }
            }
        }

        btnOk.setEnabled(exists);
        lblNodeExistanceInfo.setText(Lang.get(exists ? "nodejump.exists" : "nodejump.notexists"));
        lblNodeExistanceInfo.setForeground(exists ? (OpenBST.getInstance().isDark() ? Color.GREEN : Color.GREEN.darker()) : Color.RED);
        
        return sn;
    }
}
