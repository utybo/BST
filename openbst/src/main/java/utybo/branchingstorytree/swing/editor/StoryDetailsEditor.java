/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.editor;

import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.swing.utils.Lang;

@SuppressWarnings("serial")
public class StoryDetailsEditor extends JPanel implements EditorControl<Map<String, String>>
{
    private JTextField storyName;
    private JTextField author;
    private JComboBox<String> markup;
    private JComboBox<String> font;
    private JComboBox<String> supertools;
    private JCheckBox nsfw;

    public StoryDetailsEditor(StoryEditor editor)
    {
        setLayout(new MigLayout("", "[][grow]", "[][][][][][]"));

        JLabel lblStoryName = new JLabel(Lang.get("editor.details.name"));
        add(lblStoryName, "cell 0 0,alignx trailing");

        storyName = new JTextField();
        storyName.getDocument().addDocumentListener(new DocumentListener()
        {

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                editor.updateTabTitle();
            }

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                editor.updateTabTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                editor.updateTabTitle();
            }
        });
        add(storyName, "cell 1 0,growx");
        storyName.setColumns(10);

        JLabel lblAuthor = new JLabel(Lang.get("editor.details.author"));
        add(lblAuthor, "cell 0 1,alignx trailing");

        author = new JTextField();
        author.getDocument().addDocumentListener(new DocumentListener()
        {

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                editor.updateTabTitle();
            }

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                editor.updateTabTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                editor.updateTabTitle();
            }
        });
        add(author, "cell 1 1,growx");
        author.setColumns(10);

        JSeparator separator = new JSeparator();
        add(separator, "cell 0 2 2 1,growx");

        JLabel lblMarkup = new JLabel(Lang.get("editor.details.markup"));
        add(lblMarkup, "cell 0 3,alignx trailing");

        markup = new JComboBox<String>();
        markup.setModel(new DefaultComboBoxModel<>(new String[] {"none", "markdown", "html"}));
        add(markup, "cell 1 3,growx");

        JLabel lblFont = new JLabel(Lang.get("editor.details.font"));
        add(lblFont, "cell 0 4,alignx trailing");

        font = new JComboBox<String>();
        font.setModel(new DefaultComboBoxModel<>(
                new String[] {"[default]", "libre_baskerville", "ubuntu"}));
        add(font, "flowx,cell 1 4,growx");

        nsfw = new JCheckBox(Lang.get("editor.details.nsfw"));
        add(nsfw, "cell 0 5 2 1");

        JLabel lblSupertools = new JLabel(Lang.get("editor.details.supertools"));
        add(lblSupertools, "gap 10px, cell 1 4,alignx trailing");

        supertools = new JComboBox<String>();
        supertools.setModel(new DefaultComboBoxModel<>(
                new String[] {"all", "hidecheat", "savestate", "savestatenoio", "none"}));
        add(supertools, "cell 1 4,growx");

    }

    @Override
    public void importFrom(Map<String, String> from)
    {
        storyName.setText(from.getOrDefault("title", ""));
        author.setText(from.getOrDefault("author", ""));
        markup.setSelectedItem(from.getOrDefault("markup", "none"));
        font.setSelectedItem(from.getOrDefault("font", "[default]"));
        supertools.setSelectedItem(from.getOrDefault("supertools", "all"));
        nsfw.setSelected(Boolean.parseBoolean(from.getOrDefault("nsfw", "false")));
    }

    @Override
    public Map<String, String> exportToObject()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String exportToString()
    {
        StringBuilder sb = new StringBuilder();
        if(!storyName.getText().isEmpty())
            sb.append("title=" + storyName.getText() + "\n");

        if(!author.getText().isEmpty())
            sb.append("author=" + author.getText() + "\n");

        if(!markup.getSelectedItem().toString().isEmpty())
            sb.append("markup=" + markup.getSelectedItem().toString() + "\n");

        // While it does not hurt to write the default to other files, this one
        // is actually quite important.
        // Using a font like this bypasses the non-latin character checker which
        // switches to a compatible font.
        if(!font.getSelectedItem().toString().isEmpty()
                && !("[default]".equals(font.getSelectedItem().toString())))
            sb.append("font=" + font.getSelectedItem().toString() + "\n");

        if(!supertools.getSelectedItem().toString().isEmpty())
            sb.append("supertools=" + supertools.getSelectedItem().toString() + "\n");

        if(nsfw.isSelected())
            sb.append("nsfw=true");

        return sb.toString();
    }

    public String getTitle()
    {
        return storyName.getText();
    }

    public String getAuthor()
    {
        return author.getText();
    }

}
