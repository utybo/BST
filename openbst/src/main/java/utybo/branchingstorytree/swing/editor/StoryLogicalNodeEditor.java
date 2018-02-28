/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.editor;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.pushingpixels.substance.internal.utils.border.SubstanceTextComponentBorder;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.logicalnode.LNCondReturn;
import utybo.branchingstorytree.api.story.logicalnode.LNExec;
import utybo.branchingstorytree.api.story.logicalnode.LNInstruction;
import utybo.branchingstorytree.api.story.logicalnode.LNTern;
import utybo.branchingstorytree.swing.utils.UndoUtils;

@SuppressWarnings("serial")
public class StoryLogicalNodeEditor extends StorySingleNodeEditor
        implements EditorControl<LogicalNode>
{
    private StoryNodeIdComponent id;
    private JTextArea textArea;

    public StoryLogicalNodeEditor(StoryNodesEditor sne)
    {
        setLayout(new MigLayout("", "[][grow]", "[][grow]"));

        id = new StoryNodeIdComponent(sne);
        add(id, "cell 0 0 2 1,grow");

        JLabel lblText = new JLabel("Script : ");
        add(lblText, "cell 0 1,alignx trailing,aligny top");

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 1 1,grow");

        textArea = new JTextArea();
        UndoUtils.attachSimpleUndoManager(textArea);
        textArea.setBorder(new SubstanceTextComponentBorder(new Insets(5,5,5,5)));
        textArea.getDocument().addDocumentListener(new DocumentListener()
        {

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                sne.refreshList();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                sne.refreshList();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                sne.refreshList();
            }
        });
        textArea.setMargin(new Insets(5, 5, 5, 5));
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        scrollPane.setViewportView(textArea);

    }

    @Override
    public void importFrom(LogicalNode from) throws BSTException
    {
        StringBuilder sb = new StringBuilder();
        for(LNInstruction instruction : from.getInstructions())
        {
            if(instruction instanceof LNExec)
            {
                sb.append(DecompilingUtils.decompileAction(((LNExec)instruction).getAction()));
                sb.append("\n");
            }
            else if(instruction instanceof LNCondReturn)
            {
                sb.append(
                        ":" + DecompilingUtils.decompileNND(((LNCondReturn)instruction).getNND()));
                sb.append("\n");
            }
            else if(instruction instanceof LNTern)
            {
                LNTern t = (LNTern)instruction;
                sb.append(DecompilingUtils.decompileCheckersChain(t.getCheckers()) + "?"
                        + DecompilingUtils.decompileActionsChain(t.getTrueActions()) + ":"
                        + DecompilingUtils.decompileActionsChain(t.getFalseActions()));
                sb.append("\n");
            }
        }
        sb.append("\n");
        sb.append(DecompilingUtils.decompileSecondaryTags(from));
        textArea.setText(sb.toString());
        id.importIdInfo(from);
    }

    @Override
    public LogicalNode exportToObject()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String exportToString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(id.getIdNumber() + ":&\n");
        sb.append(textArea.getText() + "\n");
        sb.append(id.getAliasDeclaration() + "\n");
        return sb.toString();
    }

    @Override
    public String getIdentifier()
    {
        return id.getIdentifier();
    }

    @Override
    public String getSummary()
    {
        return "(logical node)";
    }

    @Override
    protected StoryNodeIdComponent getId()
    {
        return id;
    }
}
