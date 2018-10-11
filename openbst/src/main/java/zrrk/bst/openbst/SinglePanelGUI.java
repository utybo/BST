/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst;

import static zrrk.bst.openbst.OpenBST.LOG;

import java.io.File;

import javax.swing.JPanel;
import javax.swing.WindowConstants;

import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.openbst.impl.TabClient;
import zrrk.bst.openbst.visuals.StoryPanel;

public class SinglePanelGUI extends AbstractBSTGUI
{
    private static final long serialVersionUID = 1L;
    private StoryPanel sp;
    public SinglePanelGUI()
    {
        LOG.trace("Creating tab");
        setSize((int)(830 * Icons.getScale()), (int)(480 * Icons.getScale()));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(Icons.getImage("Logo", 48));
    }
    
    public void setStory(BranchingStory story, File f, TabClient tc)
    {
        sp = new StoryPanel(story, f, tc);
        getContentPane().add(sp);
        if(!sp.postCreation())
        {
            System.exit(0);
        }
        
        setTitle(sp.getTitle());
    }

    @Override
    public void removeTab(JPanel pan)
    {
        exit();
    }

    private void exit()
    {
        System.exit(0);
    }

    @Override
    public void updateName(JPanel pan, String title)
    {
        setTitle(title);
    }

    @Override
    public void openStory(File bstFile)
    {
        throw new UnsupportedOperationException(
                "Cannot open additional stories in single frame mode");
    }

    public void begin()
    {
        sp.setupStory();
    }
}
