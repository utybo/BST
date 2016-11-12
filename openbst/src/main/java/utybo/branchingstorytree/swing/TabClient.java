/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import javax.swing.JOptionPane;

import utybo.branchingstorytree.api.BSTClient;

public class TabClient implements BSTClient
{
    private OpenBST instance;
    private StoryPanel tab;

    public TabClient(OpenBST instance)
    {
        this.instance = instance;
    }

    @Override
    public String askInput(String message)
    {
        String input = null;
        while(input == null || input.isEmpty())
        {
            input = JOptionPane.showInputDialog(instance, message);
        }
        return input;
    }

    @Override
    public void exit()
    {
        instance.removeStory(tab);
    }

    public void setStoryPanel(StoryPanel sp)
    {
        tab = sp;
    }

}
