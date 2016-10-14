package utybo.branchingstorytree.api.story;

import java.util.ArrayList;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.script.IfNextNodeDefiner;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.ScriptChecker;

public class LogicalNode extends StoryNode
{
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

    public static abstract class LNInstruction
    {
        public abstract int execute() throws BSTException;
    }

    public static class LNExec extends LNInstruction
    {
        private ScriptAction action;

        public LNExec(ScriptAction action)
        {
            this.action = action;
        }

        @Override
        public int execute() throws BSTException
        {
            action.exec();
            return -1;
        }
    }

    public static class LNTern extends LNInstruction
    {
        private ArrayList<ScriptAction> trueActions;
        private ArrayList<ScriptAction> falseActions;
        private ArrayList<ScriptChecker> checkers;

        public LNTern(ArrayList<ScriptChecker> checker, ArrayList<ScriptAction> trueActions, ArrayList<ScriptAction> falseActions)
        {
            checkers = checker;
            this.trueActions = trueActions;
            this.falseActions = falseActions;
        }

        @Override
        public int execute() throws BSTException
        {
            if(solveCheckers())
                solve(trueActions);
            else
                solve(falseActions);
            return -1;
        }

        private boolean solveCheckers() throws BSTException
        {
            boolean b = true;
            for(ScriptChecker checker : checkers)
            {
                if(!checker.check())
                    b = false;
            }
            return b;
        }

        private void solve(ArrayList<ScriptAction> actions) throws BSTException
        {
            for(ScriptAction action : actions)
                action.exec();
        }
    }

    public static class LNCondReturn extends LNInstruction
    {
        private IfNextNodeDefiner innd;

        public LNCondReturn(IfNextNodeDefiner innd)
        {
            this.innd = innd;
        }

        @Override
        public int execute() throws BSTException
        {
            return innd.getNextNode();
        }

    }

    public static class LNReturn extends LNInstruction
    {
        private int next;

        public LNReturn(int next)
        {
            this.next = next;
        }

        @Override
        public int execute()
        {
            return next;
        }

    }

    private ArrayList<LNInstruction> instructionStack = new ArrayList<>();

    public LogicalNode(int id)
    {
        super(id);
    }

    public void addInstruction(LNInstruction instruction)
    {
        instructionStack.add(instruction);
    }

    public int solve() throws BSTException
    {
        int i = -1;
        for(LNInstruction instruction : instructionStack)
        {
            int j = instruction.execute();
            if(j > -1)
            {
                i = j;
                break;
            }
        }
        return i;
    }
}
