/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class AbstractBSTGUI extends JFrame
{
    private static final long serialVersionUID = 1L;

    public abstract void removeTab(JPanel pan);

    public abstract void updateName(JPanel pan, String title);

    public abstract void openStory(File bstFile);

}
