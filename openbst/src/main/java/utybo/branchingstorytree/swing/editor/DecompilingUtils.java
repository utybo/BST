/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.editor;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.ActionDescriptor;
import utybo.branchingstorytree.api.script.CheckerDescriptor;
import utybo.branchingstorytree.api.script.IfNextNodeDefiner;
import utybo.branchingstorytree.api.script.NextNodeDefiner;
import utybo.branchingstorytree.api.script.SimpleNextNodeDefiner;
import utybo.branchingstorytree.api.story.TagHolder;
import utybo.branchingstorytree.htb.HTBNextNodeDefiner;
import utybo.branchingstorytree.xbf.XBFNextNodeDefiner;

public class DecompilingUtils
{
    public static String decompileAction(ActionDescriptor ad)
    {
        return ad.getHead() + ":" + ad.getDesc();
    }

    public static String decompileNND(NextNodeDefiner nnd) throws BSTException
    {
        if(nnd instanceof SimpleNextNodeDefiner)
            return ((SimpleNextNodeDefiner)nnd).alias;
        else if(nnd instanceof IfNextNodeDefiner)
            return ((IfNextNodeDefiner)nnd).one + "," + ((IfNextNodeDefiner)nnd).two + "["
                    + decompileChecker(((IfNextNodeDefiner)nnd).checker) + "]";
        else if(nnd instanceof HTBNextNodeDefiner)
            return ((HTBNextNodeDefiner)nnd).head + ":" + ((HTBNextNodeDefiner)nnd).desc;
        else if(nnd instanceof XBFNextNodeDefiner)
            return "xbf:" + ((XBFNextNodeDefiner)nnd).desc;
        throw new BSTException(-1,
                "Unsupported NND for decompilation : " + nnd.getClass().getSimpleName(), "<none>");
    }

    public static String decompileChecker(CheckerDescriptor checker)
    {
        return checker.getHead() + ":" + checker.getDesc();
    }

    public static String decompileCheckersChain(List<CheckerDescriptor> checkers)
    {
        StringBuilder sb = new StringBuilder();
        for(CheckerDescriptor cd : checkers)
            sb.append("[" + cd.getHead() + ":" + cd.getDesc() + "]");

        return sb.toString();
    }

    public static String decompileActionsChain(List<ActionDescriptor> actions)
    {
        StringBuilder sb = new StringBuilder();
        for(ActionDescriptor cd : actions)
            sb.append("{" + cd.getHead() + ":" + cd.getDesc() + "}");

        return sb.toString();
    }

    public static String summarize(String longString, int limit)
    {
        String s = longString;
        if(s.indexOf("<p>") > -1)
            s = s.substring(0, s.indexOf("<p>"));
        if(s.indexOf("\n") > -1)
            s = s.substring(0, s.indexOf("\n"));
        return s.length() > limit ? s.substring(0, limit) + "..." : s;
    }

    public static String decompileSecondaryTags(TagHolder from)
    {
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> map = new HashMap<>(from.getTagMap());
        map.remove("alias");
        for(Entry<String, String> entry : map.entrySet())
        {
            sb.append("::" + entry.getKey() + "=" + entry.getValue() + "\n");
        }
        return sb.toString();
    }
}
