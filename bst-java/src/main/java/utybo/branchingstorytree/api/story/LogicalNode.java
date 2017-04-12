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

/**
 * A logical node is a node that executes a list of {@link LNInstruction}s in
 * order, eventually returning a next node.
 *
 * @author utybo
 *
 */
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

    /**
     * Create a logical node
     *
     * @param id
     *            the ID of the logical node
     */
    public LogicalNode(final int id, BranchingStory story)
    {
        super(id, story);
    }

    /**
     * Add an instruction at the bottom of the instruction stack
     *
     * @param instruction
     *            The instruction to add.
     */
    public void addInstruction(final LNInstruction instruction)
    {
        instructionStack.add(instruction);
    }

    /**
     * Execute the logical node
     *
     * @return the next node. Can be -1 if no next node could be determined
     * @throws BSTException
     *             If an exception occurs during the execution of an instruction
     */
    public StoryNode solve(BranchingStory story) throws BSTException
    {
        StoryNode node = null;
        for(final LNInstruction instruction : instructionStack)
        {
            final StoryNode j = instruction.execute(story);
            if(j != null)
            {
                node = j;
                break;
            }
        }
        return node;
    }
}
