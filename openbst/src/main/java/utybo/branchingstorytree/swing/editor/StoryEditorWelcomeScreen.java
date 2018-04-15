/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.editor;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

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
