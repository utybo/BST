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
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.story.NodeOption;
import zrrk.bst.openbst.utils.Lang;
import zrrk.bst.openbst.utils.UndoUtils;

@SuppressWarnings("serial")
public class StoryOptionsEditor extends JPanel implements EditorControl<List<NodeOption>>
{
    private JTextArea textArea;

    public StoryOptionsEditor()
    {
        setBorder(new TitledBorder(null, Lang.get("editor.node.options"), TitledBorder.LEADING, TitledBorder.TOP, null,
                null));
        setLayout(new MigLayout("", "[grow]", "[][grow]"));

        JLabel lblForSyntaxReference = new JLabel(Lang.get("editor.node.optsyntax"));
        add(lblForSyntaxReference, "cell 0 0");

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, "cell 0 1,grow");

        textArea = new JTextArea();
        UndoUtils.attachSimpleUndoManager(textArea);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setViewportView(textArea);

    }

    @Override
    public void importFrom(List<NodeOption> list) throws BSTException
    {
        StringBuilder sb = new StringBuilder();
        for(NodeOption o : list)
        {
            sb.append(":" + o.getText() + "|" + DecompilingUtils.decompileNND(o.getNND()));
            if(o.hasChecker() || !o.getDoOnClickActions().isEmpty())
                sb.append("|");
            if(o.hasChecker())
                sb.append("[" + DecompilingUtils.decompileChecker(o.getChecker()) + "]");
            if(!o.getDoOnClickActions().isEmpty())
                sb.append(DecompilingUtils.decompileActionsChain(o.getDoOnClickActions()));
            sb.append("\n");
            sb.append(DecompilingUtils.decompileSecondaryTags(o));
        }
        textArea.setText(sb.toString());
    }

    @Override
    public List<NodeOption> exportToObject() throws BSTException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String exportToString()
    {
        return textArea.getText();
    }
}
