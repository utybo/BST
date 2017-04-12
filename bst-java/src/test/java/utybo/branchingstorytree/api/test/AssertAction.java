/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.CheckerDescriptor;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.story.BranchingStory;

public class AssertAction implements ScriptAction
{
    private static Dictionnary dict;

    @Override
    public void exec(final String head, final String desc, final int line, final BranchingStory story, final BSTClient client) throws BSTException
    {
        try
        {
            if(dict == null)
            {
                dict = new Dictionnary();
            }
        }
        catch(InstantiationException | IllegalAccessException e)
        {
            throw new BSTException(line, "Could not create dictionary", e, story.getTag("__sourcename"));
        }

        final Pattern p = Pattern.compile("([\\w_]+?):(.*)");
        final Matcher m = p.matcher(desc);
        if(!m.matches())
        {
            throw new BSTException(line, "Incorrect checker");
        }
        final String h = m.group(1);
        final String d = m.group(2);
        try
        {
            final CheckerDescriptor cd = new CheckerDescriptor(dict.getChecker(h), h, d, line, story, client);
            assert cd.check() == true;
        }
        catch(final AssertionError error)
        {
            throw new AssertionError("Assertion " + desc + " failed. (Registry dumb : " + story.getRegistry().dump(), error);
        }

    }

    @Override
    public String[] getName()
    {
        return new String[] {"assert"};
    }

}
