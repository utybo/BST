/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.editor;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import zrrk.bst.bstjava.api.story.VirtualNode;
import zrrk.bst.openbst.utils.Lang;
import zrrk.bst.openbst.utils.UndoUtils;

@SuppressWarnings("serial")
public class StoryVirtualNodeEditor extends StorySingleNodeEditor
        implements EditorControl<VirtualNode>
{
    private StoryNodeIdComponent id;
    private JTextArea textArea;

    public StoryVirtualNodeEditor(StoryNodesEditor sne)
    {
        setLayout(new MigLayout("", "[][grow]", "[][grow]"));

        id = new StoryNodeIdComponent(sne);
        add(id, "cell 0 0 2 1,grow");

        JLabel lblText = new JLabel(Lang.get("editor.node.text"));
        add(lblText, "cell 0 1,alignx trailing,aligny top");

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "cell 1 1,grow");

        textArea = new JTextArea();
        UndoUtils.attachSimpleUndoManager(textArea);
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
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setLineWrap(true);
        scrollPane.setViewportView(textArea);

    }

    @Override
    public void importFrom(VirtualNode from)
    {
        id.importIdInfo(from);
        StringBuilder sb = new StringBuilder();
        sb.append(from.getText() + "\n");
        sb.append(DecompilingUtils.decompileSecondaryTags(from));
        textArea.setText(sb.toString());
    }

    @Override
    public VirtualNode exportToObject()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String exportToString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(id.getIdNumber() + ":>");
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
        return DecompilingUtils.summarize(textArea.getText(), 40);
    }

    @Override
    protected StoryNodeIdComponent getId()
    {
        return id;
    }
}
