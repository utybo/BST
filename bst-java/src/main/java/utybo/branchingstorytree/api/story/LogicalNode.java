/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story;

import java.util.ArrayList;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.logicalnode.LNInstruction;

public class LogicalNode extends StoryNode
{
    private final ArrayList<LNInstruction> instructionStack = new ArrayList<>();

    // Logical nodes are nodes which actually DO something, based on theBSTScript syntax
    // Every part of the logical node is stored in a list of LNInstruction.
    // An LNInstruction can either be a
    // - A simple script execution (LNExec, a wrapper for ScriptAction) (Format : "xyz:xyz", /!\ Need to use Regex for parsing)
    // - A more complex ternary condition (LNTern) (Format : "[condition][conditon]...?{iftrue}{iftrue}:{iffalse}{iffalse})
    // - A conditional return (LNCondReturn, a wrapper for IfNextNodeDefiner) (Format ":iftrue,iffalse[condition][condition]")
    // - A simple return (LNReturn)
    // It is up to the parser to create all the necessary objects.
    //
    // -- Logical Nodes are part of the Branching Story Tree Scripting interface

    public LogicalNode(final int id)
    {
        super(id);
    }

    public void addInstruction(final LNInstruction instruction)
    {
        instructionStack.add(instruction);
    }

    public int solve() throws BSTException
    {
        int i = -1;
        for(final LNInstruction instruction : instructionStack)
        {
            final int j = instruction.execute();
            if(j > -1)
            {
                i = j;
                break;
            }
        }
        return i;
    }
}
