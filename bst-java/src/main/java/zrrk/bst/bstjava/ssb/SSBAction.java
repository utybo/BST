/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.ssb;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.script.ScriptAction;
import zrrk.bst.bstjava.api.story.BranchingStory;

/**
 * Implementation of SSB related actions
 *
 * @author utybo
 *
 */
public class SSBAction implements ScriptAction
{

    @Override
    public void exec(final String head, final String desc, final int line,
            final BranchingStory story, final BSTClient client) throws BSTException
    {
        final SSBHandler ssb = client.getSSBHandler();
        if(ssb == null)
        {
            throw new BSTException(line, "SSB not supported", story);
        }
        final String action = head.substring(4);
        switch(action)
        {
        case "play":
            ssb.play(desc);
            break;
        case "ambient":
            ssb.ambient(desc);
            break;
        case "stop":
            ssb.stop();
            break;
        default:
            // Cannot happen
            break;
        }
    }

    @Override
    public String[] getName()
    {
        return new String[] {"ssb_play", "ssb_stop", "ssb_ambient"};
    }

}
