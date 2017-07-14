/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.uib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.VariableRegistry;
import utybo.branchingstorytree.api.story.BranchingStory;

/**
 * Implementation of UIB related actions
 *
 * @author utybo
 *
 */
public class UIBarAction implements ScriptAction
{
    private final Pattern setPropPattern = Pattern.compile("(\\w+),(\\w+),(.+)");
    private final Pattern setPattern = Pattern.compile("(\\w+),(.+)");

    @Override
    public void exec(final String head, final String desc, final int line, final BranchingStory story, final BSTClient client) throws BSTException
    {
        final UIBarHandler handler = client.getUIBarHandler();
        if("uib_setprop".equals(head))
        {
            final Matcher m = setPropPattern.matcher(desc);
            if(!m.matches())
            {
                throw new BSTException(line, "incorrect syntax : uib_set:element,id,value",story);
            }
            final String element = m.group(1);
            elementCheck(element, line, handler,story);
            final String id = m.group(2);
            final String value = m.group(3);
            switch(id)
            {
            case "min":
                handler.setElementMin(element, intIfPossible(value, line, story.getRegistry(), story));
                break;
            case "max":
                handler.setElementMax(element, intIfPossible(value, line, story.getRegistry(), story));
                break;
            }
        }
        else if("uib_set".equals(head))
        {
            final Matcher m = setPattern.matcher(desc);
            if(!m.matches())
            {
                throw new BSTException(line, "incorrect syntax : uib_set:element,id,value",story);
            }
            final String element = m.group(1);
            elementCheck(element, line, handler,story);
            final String value = m.group(2);
            if(handler.isElementValueTypeInteger(element))
            {
                try
                {
                    final int i = Integer.parseInt(value);
                    handler.setElementValue(element, i);
                }
                catch(final NumberFormatException e)
                {
                    if(handler.supportsDynamicInteger(element))
                    {
                        handler.setElementValue(element, value);
                    }
                    else
                    {
                        throw new BSTException(line, "Invalid value : '" + value + "' for element " + element,story);
                    }
                }
            }
            else
            {
                handler.setElementValue(element, value);
            }
        }
        else if("uib_setvisible".equals(head))
        {
            handler.setUIBVisisble(Boolean.parseBoolean(desc));
        }
        else if("uib_init".equals(head))
        {
            handler.setLayout(story.getTag("uib_layout"));
            handler.initialize();
        }
    }

    private void elementCheck(final String element, final int line, final UIBarHandler handler, BranchingStory story) throws BSTException
    {
        if(!handler.elementExists(element))
        {
            throw new BSTException(line, "Unknown component : " + element,story);
        }
    }

    private int intIfPossible(final String value, final int line, final VariableRegistry registry, BranchingStory story) throws BSTException
    {
        try
        {
            return registry.typeOf(value) == Integer.class ? (Integer)registry.get(value, 0) : Integer.parseInt(value);
        }
        catch(final NumberFormatException e)
        {
            throw new BSTException(line, "invalid value : " + value,story);
        }
    }

    @Override
    public String[] getName()
    {
        return new String[] {"uib_setprop", "uib_set", "uib_setvisible", "uib_init"};
    }

}
