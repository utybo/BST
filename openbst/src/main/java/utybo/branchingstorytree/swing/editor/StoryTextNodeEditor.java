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

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.swing.utils.UndoUtils;

@SuppressWarnings("serial")
public class StoryTextNodeEditor extends StorySingleNodeEditor implements EditorControl<TextNode>
{
    private JTextArea textArea;
    private StoryOptionsEditor options;
    StoryNodeIdComponent id;

    public StoryTextNodeEditor(StoryNodesEditor sne)
    {
        setLayout(new MigLayout("", "[][grow]", "[][grow][150px:n]"));

        id = new StoryNodeIdComponent(sne);
        add(id, "cell 0 0 2 1,grow");

        JLabel lblText = new JLabel("Text : ");
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
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        scrollPane.setViewportView(textArea);

        options = new StoryOptionsEditor();
        add(options, "cell 0 2 2 1,grow");

    }

    @Override
    public void importFrom(TextNode from) throws BSTException
    {
        textArea.setText(from.getText());
        options.importFrom(from.getOptions());
        id.importIdInfo(from);
    }

    @Override
    public TextNode exportToObject()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String exportToString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(id.getIdNumber() + ":");
        sb.append(textArea.getText() + "\n");
        sb.append(id.getAliasDeclaration() + "\n");
        sb.append(options.exportToString() + "\n");
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
