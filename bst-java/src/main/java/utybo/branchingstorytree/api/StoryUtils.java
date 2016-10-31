package utybo.branchingstorytree.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.api.story.VirtualNode;

public class StoryUtils
{
    public static String solveVariables(final TextNode textNode, final BranchingStory story) throws BSTException
    {
        String text = textNode.getText();
        final Pattern vp = Pattern.compile("\\$\\{((\\>\\d+)|(\\w+))\\}");
        final Matcher vn = vp.matcher(text);
        while(vn.find())
        {
            final String toReplace = vn.group();
            final String varName = toReplace.substring(2, toReplace.length() - 1);
            if(varName.startsWith(">"))
            {
                final String s = varName.substring(1);
                final int i = Integer.parseInt(s);
                text = text.replace(toReplace, ((VirtualNode)story.getNode(i)).getText());
            }
            else if(varName.startsWith("&"))
            {
                final String s = varName.substring(1);
                final int i = Integer.parseInt(s);
                final LogicalNode ln = (LogicalNode)story.getNode(i);
                text = text.replace(toReplace, ((VirtualNode)story.getNode(ln.solve())).getText());
            }
            else
            {
                text = vn.replaceFirst(story.getRegistry().get(varName).toString());
            }
            vn.reset(text);
        }

        return text;
    }
}
