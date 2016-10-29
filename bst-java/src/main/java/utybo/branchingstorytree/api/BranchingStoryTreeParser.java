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

import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.script.IfNextNodeDefiner;
import utybo.branchingstorytree.api.script.ScriptAction;
import utybo.branchingstorytree.api.script.ScriptChecker;
import utybo.branchingstorytree.api.script.StaticNextNode;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.NodeOption;
import utybo.branchingstorytree.api.story.StoryNode;
import utybo.branchingstorytree.api.story.TagHolder;
import utybo.branchingstorytree.api.story.TextNode;
import utybo.branchingstorytree.api.story.VirtualNode;

public class BranchingStoryTreeParser
{
    private static final int NORMAL = 0, VIRTUAL = 1, LOGICAL = 2;

    public static BranchingStory parse(BufferedReader br, Dictionnary dictionnary) throws IOException, BSTException
    {
        BranchingStory story = new BranchingStory();

        String line;
        int lineNumber = 0;

        StoryNode node = null;
        int nodeType = -1;
        TagHolder latestHolder = null;
        boolean optionsStarted = false;
        int skipLinesOnNextAdd = 0;

        Pattern beginningOfNodePattern = Pattern.compile("^\\d+:.+$");
        Pattern logicalNodePattern = Pattern.compile("^\\d+:&$");
        Pattern virtualNodePattern = Pattern.compile("\\d+:>.+$");
        Pattern scriptPattern = Pattern.compile("(\\{(.+?):(.*?)})|(\\[(.+?):(.*?)])");
        Pattern ifNextNodeDefiner = Pattern.compile("(\\d+),(\\d+)\\[(.+:.+)]");

        // Patterns specific to Logical Nodes
        Pattern lnLineSubscript = Pattern.compile("(.+?):(.+)");
        Pattern lnTernary = Pattern.compile("((\\[.+?:.*?])+)\\?((\\{.+?:.*?})+):((\\{.+?:.*?})*)");
        Pattern lnChecker = Pattern.compile("\\[(.+?):(.*?)]");
        Pattern lnScript = Pattern.compile("\\{(.+?):(.*?)}");

        while((line = br.readLine()) != null)
        {
            try
            {
                lineNumber++;
                if(line.startsWith("#"))
                    continue;

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

                char firstChar = line.charAt(0);
                if(beginningOfNodePattern.matcher(line).matches())
                {
                    // This is a new node
                    int id = Integer.parseInt(line.split("\\:")[0]);

                    // Check if logical node
                    if(logicalNodePattern.matcher(line).matches())
                    {
                        node = new LogicalNode(id);
                        nodeType = LOGICAL;
                        story.addNode(node);
                    }
                    else if(virtualNodePattern.matcher(line).matches())
                    {
                        node = new VirtualNode(id);
                        nodeType = VIRTUAL;
                        story.addNode(node);
                        ((VirtualNode)node).setText(line.substring(line.indexOf(":") + 2));
                        skipLinesOnNextAdd = 0;
                    }
                    else
                    {
                        node = new TextNode(id);
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
                    String tagName = line.split("\\=")[0];
                    story.putTag(tagName, line.substring(line.indexOf("=") + 1));
                }

                else if(firstChar == ':')
                {
                    if(nodeType == NORMAL)
                    {
                        if(line.startsWith("::"))
                        {
                            String s = line.substring(2);
                            String[] bits = s.split("\\=");
                            latestHolder.putTag(bits[0], bits[1]);
                        }
                        else
                        {
                            String s = line.substring(1);
                            String[] bits = s.split("\\|");
                            String text = bits[0];
                            NodeOption option = new NodeOption(text);
                            latestHolder = option;
                            ((TextNode)node).addOption(option);

                            String nextNodeDefiner = bits[1];
                            Matcher matcher = ifNextNodeDefiner.matcher(nextNodeDefiner);
                            if(matcher.matches())
                            {
                                int first = Integer.parseInt(matcher.group(1));
                                int second = Integer.parseInt(matcher.group(2));
                                String script = matcher.group(3);
                                String command = script.substring(0, script.indexOf(':'));
                                String desc = script.substring(script.indexOf(':') + 1);
                                ScriptChecker oc = dictionnary.getChecker(command, desc, story.getRegistry());
                                if(oc == null)
                                    throw new IllegalArgumentException("Unknown checker : " + command);
                                else
                                    option.setNextNode(new IfNextNodeDefiner(first, second, oc));
                            }
                            else
                            {
                                option.setNextNode(new StaticNextNode(Integer.parseInt(nextNodeDefiner)));
                            }

                            if(bits.length > 2)
                            {
                                String scripts = s.substring(s.indexOf('|', s.indexOf('|') + 1) + 1);
                                matcher = scriptPattern.matcher(scripts);
                                while(matcher.find())
                                {
                                    String script = scripts.substring(matcher.start(), matcher.end());
                                    int type = script.startsWith("{") ? 0 : script.startsWith("[") ? 1 : -1;
                                    script = script.substring(1, script.length() - 1);
                                    String command = matcher.group(type == 0 ? 2 : 5);
                                    String desc = matcher.group(type == 0 ? 3 : 6);
                                    if(type == 0)
                                    {
                                        ScriptAction action = dictionnary.getAction(command, desc, story.getRegistry());
                                        if(action == null)
                                            throw new IllegalArgumentException("Unknown action : " + action);
                                        else
                                            option.addDoOnClick(action);
                                    }
                                    else if(type == 1)
                                    {
                                        ScriptChecker oc = dictionnary.getChecker(command, desc, story.getRegistry());
                                        if(oc == null)
                                            throw new IllegalArgumentException("Unknown checker : " + command);
                                        else
                                            option.setChecker(oc);
                                    }
                                }
                            }
                        }
                    }
                    else if(nodeType == LOGICAL)
                    {
                        String nextNodeDefiner = line.substring(1);
                        Matcher matcher = ifNextNodeDefiner.matcher(nextNodeDefiner);
                        if(matcher.matches())
                        {
                            int first = Integer.parseInt(matcher.group(1));
                            int second = Integer.parseInt(matcher.group(2));
                            String script = matcher.group(3);
                            String command = script.substring(0, script.indexOf(':'));
                            String desc = script.substring(script.indexOf(':') + 1);
                            ScriptChecker oc = dictionnary.getChecker(command, desc, story.getRegistry());
                            if(oc == null)
                                throw new IllegalArgumentException("Unknown checker : " + command);
                            else
                                ((LogicalNode)node).addInstruction(new LogicalNode.LNCondReturn(new IfNextNodeDefiner(first, second, oc)));
                        }
                        else
                        {
                            ((LogicalNode)node).addInstruction(new LogicalNode.LNReturn(Integer.parseInt(nextNodeDefiner)));
                        }
                    }
                }
                else if(node != null && !optionsStarted)
                {
                    if((nodeType == NORMAL && !optionsStarted) || nodeType == VIRTUAL)
                    {
                        VirtualNode tn = (VirtualNode)node;
                        // This is the continuation of a text node
                        for(int i = 0; i < skipLinesOnNextAdd; i++)
                            tn.appendText("\n");
                        tn.appendText("\n");
                        tn.appendText(line);
                        skipLinesOnNextAdd = 0;
                    }
                    else if(nodeType == LOGICAL)
                    {
                        // This is a Logical Node for
                        LogicalNode ln = (LogicalNode)node;
                        Matcher m = lnLineSubscript.matcher(line);
                        if(m.matches())
                        {
                            String name = m.group(1);
                            String body = m.group(2);
                            ln.addInstruction(new LogicalNode.LNExec(dictionnary.getAction(name, body, story.getRegistry())));
                        }
                        else
                        {
                            Matcher m2 = lnTernary.matcher(line);
                            if(m2.matches())
                            {
                                String conditions = m2.group(1);
                                String yes = m2.group(3);
                                String no = m2.group(5);

                                ArrayList<ScriptChecker> check = new ArrayList<>();
                                ArrayList<ScriptAction> yeses = new ArrayList<>();
                                ArrayList<ScriptAction> nos = new ArrayList<>();

                                Matcher matcher = lnChecker.matcher(conditions);
                                while(matcher.find())
                                {
                                    String command = matcher.group(1);
                                    String desc = matcher.group(2);

                                    ScriptChecker oc = dictionnary.getChecker(command, desc, story.getRegistry());
                                    if(oc == null)
                                        throw new IllegalArgumentException("Unknown checker : " + command);
                                    else
                                        check.add(oc);
                                }

                                matcher = lnScript.matcher(yes);
                                while(matcher.find())
                                {
                                    String command = matcher.group(1);
                                    String desc = matcher.group(2);

                                    ScriptAction oc = dictionnary.getAction(command, desc, story.getRegistry());
                                    if(oc == null)
                                        throw new IllegalArgumentException("Unknown checker : " + command);
                                    else
                                        yeses.add(oc);
                                }

                                matcher = lnScript.matcher(no);
                                while(matcher.find())
                                {
                                    String command = matcher.group(1);
                                    String desc = matcher.group(2);

                                    ScriptAction oc = dictionnary.getAction(command, desc, story.getRegistry());
                                    if(oc == null)
                                        throw new IllegalArgumentException("Unknown checker : " + command);
                                    else
                                        nos.add(oc);
                                }

                                ln.addInstruction(new LogicalNode.LNTern(check, yeses, nos));
                            }

                        }
                    }
                }
            }
            catch(Exception e)
            {
                if(e instanceof IOException)
                {
                    throw e;
                }
                else if(e instanceof BSTException)
                {
                    ((BSTException)e).setWhere(lineNumber);
                    throw e;
                }
                else
                {
                    throw new BSTException(lineNumber, "An error was detected while trying to understand your file. Please check the line : " + line, e);
                }
            }
        }
        return story;
    }
}
