package utybo.branchingstorytree.swing.editor;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class StoryEditorWelcomeScreen extends JPanel
{
    public StoryEditorWelcomeScreen()
    {
        setLayout(new MigLayout("", "[grow]", "[]"));

        JLabel label = new JLabel("<html><b>THIS IS A BETA VERSION OF THE EDITOR</b><p><p>" //
                + "While it has been lightly tested, it comes with no guarantee and may well cause the 3rd world war. "
                + "Make sure to save your work!<p>NOTE : Logical nodes formatting is always crushed when converting."
                + "This should be fixed soonish.<p><p><b>This editor is appropriate for beginners. However, more advanced"
                + " users should use a regular text editor, and not this editor, as it is seriously lacking in several"
                + "domains.");
        add(label, "cell 0 0");
    }

}
