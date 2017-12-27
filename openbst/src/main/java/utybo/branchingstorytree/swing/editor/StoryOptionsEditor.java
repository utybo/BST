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
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.NodeOption;

@SuppressWarnings("serial")
public class StoryOptionsEditor extends JPanel implements EditorControl<List<NodeOption>>
{
    private JTextArea textArea;

    public StoryOptionsEditor()
    {
        setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null,
                null));
        setLayout(new MigLayout("", "[grow]", "[][grow]"));

        JLabel lblForSyntaxReference = new JLabel(
                "For syntax reference, please check the Reference or the Tutorial");
        add(lblForSyntaxReference, "cell 0 0");

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, "cell 0 1,grow");

        textArea = new JTextArea();
        textArea.setMargin(new Insets(5, 5, 5, 5));
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
