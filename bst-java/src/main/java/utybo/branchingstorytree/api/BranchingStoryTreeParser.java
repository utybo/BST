/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utybo.branchingstorytree.api.script.ActionDescriptor;
import utybo.branchingstorytree.api.script.CheckerDescriptor;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.script.IfNextNodeDefiner;
import utybo.branchingstorytree.api.script.NextNodeDefiner;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.ScriptChecker;
import utybo.branchingstorytree.api.script.SimpleNextNodeDefiner;
import utybo.branchingstorytree.api.script.VariableRegistry;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.NodeOption;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TagHolder;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.api.story.VirtualNode;
import utybo.branchingstorytree.api.story.logicalnode.LNCondReturn;
import utybo.branchingstorytree.api.story.logicalnode.LNExec;
import utybo.branchingstorytree.api.story.logicalnode.LNTern;

/**
 * A parser that turns an input from a {@link BufferedReader} to a
 * {@link BranchingStory}
 *
 * @author utybo
 *
 */
public class BranchingStoryTreeParser
{
    /*
     * Node types
     */
    private static final int NORMAL = 0, VIRTUAL = 1, LOGICAL = 2;

    private final Pattern beginningOfNodePattern = Pattern.compile("(^\\d+|\\*):.+$");
    private final Pattern logicalNodePattern = Pattern.compile("(^\\d+|\\*):&$");
    private final Pattern virtualNodePattern = Pattern.compile("(^\\d+|\\*):>.+$");
    private final Pattern scriptPattern = Pattern.compile("(\\{(.+?):(.*?)})|(\\[(.+?):(.*?)])");
    private final Pattern ifNextNodeDefiner = Pattern.compile("([-]?\\d+|[\\w_]+?),([-]?\\d+|[\\w_]+?)\\[(.+:.+)]");
    private final Pattern externalNodeDefiner = Pattern.compile("([\\w_]+?):(.*)");

    private final Pattern lnLineSubscript = Pattern.compile("([\\w_]+?):(.*)");
    private final Pattern lnTernary = Pattern.compile("((\\[.+?:.*?])+)\\?((\\{.+?:.*?})+)(:((\\{.+?:.*?})*))?");
    private final Pattern lnChecker = Pattern.compile("\\[(.+?):(.*?)]");
    private final Pattern lnScript = Pattern.compile("\\{(.+?):(.*?)}");

    /**
     * Parse the input from the {@link BufferedReader} into a
     * {@link BranchingStory}
     *
     * @param br
     *            the BufferedReader to use
     * @param dictionnary
     *            the dictionnary to pick the actions and checkers from
     * @param client
     *            the client to use in actions and checkers. It does not have to
     *            be fully functional for the parser to do its work
     * @return A fresh {@link BranchingStory} that represents the BST input from
     *         the {@link BufferedReader}
     * @throws IOException
     *             if an I/O error occurs
     * @throws BSTException
     *             if something is wrong with your input
     */
    public synchronized BranchingStory parse(final BufferedReader br, final Dictionnary dictionnary, final BSTClient client, String name) throws IOException, BSTException
    {
        return parse(br, dictionnary, client, name, new BranchingStory());
    }

    public synchronized BranchingStory parse(final BufferedReader br, final Dictionnary dictionnary, final BSTClient client, String name, VariableRegistry registry) throws IOException, BSTException
    {
        return parse(br, dictionnary, client, name, new BranchingStory(registry));
    }

    private synchronized BranchingStory parse(final BufferedReader br, final Dictionnary dictionnary, final BSTClient client, String name, BranchingStory story) throws IOException, BSTException
    {

        String line = null;
        int lineNumber = 0;

        StoryNode node = null;
        int nodeType = -1;
        TagHolder latestHolder = null;
        boolean optionsStarted = false;
        int skipLinesOnNextAdd = 0;
        try
        {
            while((line = br.readLine()) != null)
            {
                lineNumber++;
                if(line.startsWith("#"))
                {
                    continue;
                }

                // Handle empty lines
                if(line.isEmpty())
                {
                    if(node != null && !optionsStarted && (nodeType == VIRTUAL || nodeType == NORMAL))
                    {
                        // We're in a node, before the reply : there is an empty line in the node
                        skipLinesOnNextAdd++;
                        continue;
                    }
                    else
                    {
                        // Just a random empty line passing by.
                        continue;
                    }
                }

                final char firstChar = line.charAt(0);
                if(beginningOfNodePattern.matcher(line).matches())
                {
                    // This is a new node
                    String sid = line.split("\\:")[0];
                    boolean autoNode = sid.equals("*");

                    final int id = autoNode ? story.nextAvailableAuto() : Integer.parseInt(line.split("\\:")[0]);

                    // Check if logical node
                    if(logicalNodePattern.matcher(line).matches())
                    {
                        node = new LogicalNode(id, story);
                        nodeType = LOGICAL;
                        story.addNode(node);
                    }
                    else if(virtualNodePattern.matcher(line).matches())
                    {
                        node = new VirtualNode(id, story);
                        nodeType = VIRTUAL;
                        story.addNode(node);
                        ((VirtualNode)node).setText(line.substring(line.indexOf(":") + 2));
                        skipLinesOnNextAdd = 0;
                    }
                    else
                    {
                        node = new TextNode(id, story);
                        nodeType = NORMAL;
                        story.addNode(node);
                        ((TextNode)node).setText(line.substring(line.indexOf(":") + 1));
                        skipLinesOnNextAdd = 0;
                        optionsStarted = false;
                    }
                    latestHolder = node;
                }
                else if(Character.isAlphabetic(firstChar) && node == null)
                {
                    // This is a tag (probably)
                    final String tagName = line.split("\\=")[0];
                    story.putTag(tagName, line.substring(line.indexOf("=") + 1));
                }

                else if(firstChar == ':')
                {
                    if(line.startsWith("::"))
                    {
                        if(nodeType == -1)
                        {
                            throw new BSTException(lineNumber, "Cannot define node tag when no nodes were started", name);
                        }
                        final String s = line.substring(2);
                        final String[] bits = s.split("\\=");
                        latestHolder.putTag(bits[0], bits[1]);
                    }
                    else
                    {
                        if(nodeType == NORMAL)
                        {

                            final String s = line.substring(1);
                            final String[] bits = s.split("\\|");
                            final String text = bits[0];
                            final NodeOption option = new NodeOption(text);
                            latestHolder = option;
                            ((TextNode)node).addOption(option);

                            final String nextNodeDefiner = bits[1];
                            option.setNextNode(parseNND(nextNodeDefiner, dictionnary, lineNumber, story, client, name));

                            Matcher matcher;
                            if(bits.length > 2)
                            {
                                final String scripts = s.substring(s.indexOf('|', s.indexOf('|') + 1) + 1);
                                matcher = scriptPattern.matcher(scripts);
                                if(Character.isAlphabetic(scripts.charAt(0)))
                                {
                                    // Attempt to find a checker first
                                    // If not found, attempt to find an action
                                    final Matcher m = lnLineSubscript.matcher(scripts);
                                    if(m.matches())
                                    {
                                        final String header = m.group(1);
                                        final ScriptAction possibleAction = dictionnary.getAction(header);
                                        final ScriptChecker possibleChecker = dictionnary.getChecker(header);
                                        if(possibleAction != null)
                                        {
                                            option.addDoOnClick(new ActionDescriptor(possibleAction, header, m.group(2), lineNumber, story, client));
                                        }
                                        else if(possibleChecker != null)
                                        {
                                            option.setChecker(new CheckerDescriptor(possibleChecker, header, m.group(2), lineNumber, story, client));
                                        }
                                        else
                                        {
                                            throw new BSTException(lineNumber, "This checker or action does not exist : " + header, name);
                                        }

                                    }
                                }
                                else
                                {
                                    while(matcher.find())
                                    {
                                        String script = scripts.substring(matcher.start(), matcher.end());
                                        final int type = script.startsWith("{") ? 0 : script.startsWith("[") ? 1 : -1;
                                        script = script.substring(1, script.length() - 1);
                                        final String command = matcher.group(type == 0 ? 2 : 5);
                                        final String desc = matcher.group(type == 0 ? 3 : 6);
                                        if(type == 0)
                                        {
                                            final ActionDescriptor action = new ActionDescriptor(dictionnary.getAction(command), command, desc, lineNumber, story, client);
                                            if(action.getAction() == null)
                                            {
                                                throw new IllegalArgumentException("Unknown action : " + action);
                                            }
                                            else
                                            {
                                                option.addDoOnClick(action);
                                            }
                                        }
                                        else if(type == 1)
                                        {

                                            final CheckerDescriptor oc = new CheckerDescriptor(dictionnary.getChecker(command), command, desc, lineNumber, story, client);
                                            if(oc.getChecker() == null)
                                            {
                                                throw new IllegalArgumentException("Unknown checker : " + command);
                                            }
                                            else
                                            {
                                                option.setChecker(oc);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if(nodeType == LOGICAL)
                        {
                            final String nextNodeDefiner = line.substring(1);
                            ((LogicalNode)node).addInstruction(new LNCondReturn(parseNND(nextNodeDefiner, dictionnary, lineNumber, story, client, name)));
                        }
                    }
                }
                else if(node != null && !optionsStarted)
                {
                    if(nodeType == NORMAL && !optionsStarted || nodeType == VIRTUAL)
                    {
                        final VirtualNode tn = (VirtualNode)node;
                        // This is the continuation of a text node
                        for(int i = 0; i < skipLinesOnNextAdd; i++)
                        {
                            tn.appendText("\n");
                        }
                        tn.appendText("\n");
                        tn.appendText(line);
                        skipLinesOnNextAdd = 0;
                    }
                    else if(nodeType == LOGICAL)
                    {
                        // This is a Logical Node for
                        final LogicalNode ln = (LogicalNode)node;
                        final Matcher m = lnLineSubscript.matcher(line);
                        if(m.matches())
                        {
                            final String head = m.group(1);
                            final String body = m.group(2);
                            ln.addInstruction(new LNExec(new ActionDescriptor(dictionnary.getAction(head), head, body, lineNumber, story, client)));
                        }
                        else
                        {
                            final Matcher m2 = lnTernary.matcher(line);
                            if(m2.matches())
                            {
                                final String conditions = m2.group(1);
                                final String yes = m2.group(3);
                                final String no = m2.group(6);

                                final ArrayList<CheckerDescriptor> check = new ArrayList<>();
                                final ArrayList<ActionDescriptor> yeses = new ArrayList<>();
                                final ArrayList<ActionDescriptor> nos = new ArrayList<>();

                                Matcher matcher = lnChecker.matcher(conditions);
                                while(matcher.find())
                                {
                                    final String command = matcher.group(1);
                                    final String desc = matcher.group(2);

                                    final CheckerDescriptor oc = new CheckerDescriptor(dictionnary.getChecker(command), command, desc, lineNumber, story, client);
                                    if(oc.getChecker() == null)
                                    {
                                        throw new IllegalArgumentException("Unknown checker : " + command);
                                    }
                                    else
                                    {
                                        check.add(oc);
                                    }
                                }

                                matcher = lnScript.matcher(yes);
                                while(matcher.find())
                                {
                                    final String command = matcher.group(1);
                                    final String desc = matcher.group(2);

                                    final ActionDescriptor oc = new ActionDescriptor(dictionnary.getAction(command), command, desc, lineNumber, story, client);
                                    if(oc.getAction() == null)
                                    {
                                        throw new IllegalArgumentException("Unknown checker : " + command);
                                    }
                                    else
                                    {
                                        yeses.add(oc);
                                    }
                                }

                                if(no != null)
                                {
                                    matcher = lnScript.matcher(no);
                                    while(matcher.find())
                                    {
                                        final String command = matcher.group(1);
                                        final String desc = matcher.group(2);

                                        final ActionDescriptor oc = new ActionDescriptor(dictionnary.getAction(command), command, desc, lineNumber, story, client);
                                        if(oc.getAction() == null)
                                        {
                                            throw new IllegalArgumentException("Unknown checker : " + command);
                                        }
                                        else
                                        {
                                            nos.add(oc);
                                        }
                                    }
                                }

                                ln.addInstruction(new LNTern(check, yeses, nos));
                            }

                        }
                    }
                }

            }
        }
        catch(

        final IOException e)
        {
            throw e;
        }
        catch(final BSTException e)
        {
            e.setWhere(lineNumber);
            throw e;
        }
        catch(final Exception e)
        {
            throw new BSTException(lineNumber, "An error was detected while trying to understand your file. Please check the line : " + (line.length() > 25 ? line.substring(0, 25) + "[...]" : line), e, name);
        }

        // Always add the story's name in the tag __sourcename, as it allows to get more traceability
        story.putTag("__sourcename", name);
        return story;
    }

    public NextNodeDefiner parseNND(final String nnd, final Dictionnary dictionnary, final int lineNumber, final BranchingStory story, final BSTClient client, String name) throws BSTException
    {
        final Matcher matcher = ifNextNodeDefiner.matcher(nnd);
        final Matcher matchExt = externalNodeDefiner.matcher(nnd);

        if(matcher.matches())
        {
            String one = matcher.group(1);
            String two = matcher.group(2);
            final String script = matcher.group(3);
            final String command = script.substring(0, script.indexOf(':'));
            final String desc = script.substring(script.indexOf(':') + 1);
            final CheckerDescriptor oc = new CheckerDescriptor(dictionnary.getChecker(command), command, desc, lineNumber, story, client);
            return new IfNextNodeDefiner(one, two, oc);
        }
        else if(matchExt.matches())
        {
            return dictionnary.getExtNND(matchExt.group(1), matchExt.group(2), client, lineNumber, name);
        }
        else
        {
            return new SimpleNextNodeDefiner(nnd);
        }

    }
}
