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

public class UIBarAction implements ScriptAction
{
    private Pattern setPropPattern = Pattern.compile("(\\w+),(\\w+),(.+)");
    private Pattern setPattern = Pattern.compile("(\\w+),(.+)");

    @Override
    public void exec(String head, String desc, BranchingStory story, BSTClient client) throws BSTException
    {
        UIBarHandler handler = client.getUIBarHandler();
        if("uib_setprop".equals(head))
        {
            Matcher m = setPropPattern.matcher(desc);
            if(!m.matches())
            {
                throw new BSTException(-1, "incorrect syntax : uib_set:element,id,value");
            }
            String element = m.group(1);
            elementCheck(element, handler);
            String id = m.group(2);
            String value = m.group(3);
            switch(id)
            {
            case "min":
                handler.setElementMin(element, intIfPossible(value, story.getRegistry()));
                break;
            case "max":
                handler.setElementMax(element, intIfPossible(value, story.getRegistry()));
                break;
            }
        }
        else if("uib_set".equals(head))
        {
            Matcher m = setPattern.matcher(desc);
            if(!m.matches())
            {
                throw new BSTException(-1, "incorrect syntax : uib_set:element,id,value");
            }
            String element = m.group(1);
            elementCheck(element, handler);
            String value = m.group(2);
            if(handler.isElementValueTypeInteger(element))
            {
                try
                {
                    int i = Integer.parseInt(value);
                    handler.setElementValue(element, i);
                }
                catch(NumberFormatException e)
                {
                    if(handler.supportsDynamicInteger(element))
                    {
                        handler.setElementValue(element, value);
                    }
                    else
                    {
                        throw new BSTException(-1, "Invalid value : '" + value + "' for element " + element);
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

    private void elementCheck(String element, UIBarHandler handler) throws BSTException
    {
        if(!handler.elementExists(element))
            throw new BSTException(-1, "Unknown component : " + element);
    }

    private int intIfPossible(String value, VariableRegistry registry) throws BSTException
    {
        try
        {
            return registry.typeOf(value) == Integer.class ? (Integer)registry.get(value, 0) : Integer.parseInt(value);
        }
        catch(NumberFormatException e)
        {
            throw new BSTException(-1, "invalid value : " + value);
        }
    }

    @Override
    public String[] getName()
    {
        return new String[] {"uib_setprop", "uib_set", "uib_setvisible", "uib_init"};
    }

}
