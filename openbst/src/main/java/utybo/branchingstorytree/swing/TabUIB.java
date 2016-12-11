/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.StoryUtils;
import utybo.branchingstorytree.api.script.VariableRegistry;
import utybo.branchingstorytree.api.story.LogicalNode;
import utybo.branchingstorytree.api.story.VirtualNode;
import utybo.branchingstorytree.uib.UIBarHandler;

public class TabUIB implements UIBarHandler
{
    protected final StoryPanel tab;

    private String layout;
    private TreeMap<String, JComponent> uibComponents = new TreeMap<>();
    private boolean uibInitialized = false;

    public TabUIB(StoryPanel story)
    {
        tab = story;
    }

    @Override
    public void setLayout(String layoutIdentifier) throws BSTException
    {
        layout = layoutIdentifier;
    }

    @Override
    public void initialize() throws BSTException
    {
        uibInitialized = true;
        tab.story.getRegistry().put("__uib__initialized", "true");
        boolean gridMode = tab.story.hasTag("uib_grid");
        String columnDef = tab.story.getTag("uib_grid");
        boolean advancedMode = Boolean.parseBoolean(tab.story.getTag("uib_advanced"));
        tab.uibPanel.setLayout(new MigLayout((gridMode ? "" : "nogrid, ") + "fillx", columnDef));
        String[] parts = layout.split(advancedMode ? ";" : "[,;]");
        boolean newLine = false;
        for(int i = 0; i < parts.length; i++)
        {
            String s = parts[i];
            int index = s.indexOf(':');
            String comp = index > -1 ? s.substring(0, index) : s;
            String constraints = advancedMode && index > -1 ? s.substring(index + 1) : "";
            Component toAdd = null;
            switch(comp)
            {
            case "tb":
                JLabel t = new JLabel();
                uibComponents.put(determineNext("t"), t);
                tab.uibPanel.add(t, (newLine ? "newline" : "") + ", alignx right");
                newLine = false;
                // $FALL-THROUGH$
            case "b":
                JProgressBar jpb = new JProgressBar();
                uibComponents.put(determineNext("b"), jpb);
                toAdd = jpb;
                if(!advancedMode)
                    constraints += ", grow";
                tab.uibPanel.add(jpb);
                break;
            case "t":
                JLabel t1 = new JLabel();
                uibComponents.put(determineNext("t"), t1);
                constraints += ", aligny top";
                toAdd = t1;
                break;
            case "vs":
                toAdd = new JSeparator(SwingConstants.VERTICAL);
                constraints += ", growy";
                break;
            case "hs":
                toAdd = new JSeparator(SwingConstants.HORIZONTAL);
                constraints += ", growx";
                break;
            case "nl":
                // $FALL-THROUGH$
            case "ln":
                newLine = true;
                continue; // Get out of here! we don't have anything to add
            case "gu":
                toAdd = Box.createHorizontalStrut(5);
                break;
            default:
                throw new BSTException(-1, "Unknown component : '" + comp + "'");
            }
            if(newLine)
            {
                constraints += ", newline";
            }
            tab.uibPanel.add(toAdd, constraints);
        }
        tab.uibPanel.revalidate();
    }

    private String determineNext(String string)
    {
        int i = 0;
        Pattern p = Pattern.compile(string + "\\d+");
        for(String s : uibComponents.keySet())
        {
            if(p.matcher(s).matches())
            {
                i++;
            }
        }
        return string + i;
    }

    @Override
    public boolean isElementValueTypeInteger(String element) throws BSTException
    {
        JComponent c = uibComponents.get(element);
        return c != null && c instanceof JProgressBar;
    }

    @Override
    public void setElementValue(String element, int value) throws BSTException
    {
        JComponent c = uibComponents.get(element);
        if(c != null)
        {
            tab.story.getRegistry().put("__uib__" + element + "_value", "" + value);
            updateComponent(element, c);
        }
    }

    @Override
    public void setElementValue(String element, String value) throws BSTException
    {
        JComponent c = uibComponents.get(element);
        if(c != null)
        {
            tab.story.getRegistry().put("__uib__" + element + "_value", "" + value);
            updateComponent(element, c);
        }
    }

    @Override
    public void setElementMax(String element, int max)
    {
        JComponent c = uibComponents.get(element);
        if(c instanceof JProgressBar)
        {
            tab.story.getRegistry().put("__uib__" + element + "_max", "" + max);
            ((JProgressBar)c).getModel().setMaximum(max);
        }

    }

    @Override
    public void setElementMin(String element, int min)
    {
        JComponent c = uibComponents.get(element);
        if(c instanceof JProgressBar)
        {
            tab.story.getRegistry().put("__uib__" + element + "_min", "" + min);
            ((JProgressBar)c).getModel().setMinimum(min);
        }

    }

    @Override
    public boolean supportsDynamicInteger(String element)
    {
        JComponent c = uibComponents.get(element);
        return c instanceof JProgressBar;
    }

    @Override
    public void setUIBVisisble(boolean parseBoolean)
    {
        tab.uibPanel.setVisible(parseBoolean);
        tab.story.getRegistry().put("__uib__visible", Boolean.toString(parseBoolean));
    }

    @Override
    public boolean elementExists(String element)
    {
        return uibComponents.containsKey(element);
    }

    public void updateUIB() throws BSTException
    {
        if(uibInitialized)
        {
            for(String element : uibComponents.keySet())
            {
                updateComponent(element, uibComponents.get(element));
            }
        }
    }

    private void updateComponent(String element, JComponent c) throws BSTException
    {
        if(c instanceof JLabel)
        {
            String s = MarkupUtils.translateMarkup(MarkupUtils.solveMarkup(tab.story, null), computeText(tab.story.getRegistry().get("__uib__" + element + "_value", "").toString()));
            ((JLabel)c).setText(s);
        }
        else if(c instanceof JProgressBar)
        {
            ((JProgressBar)c).setValue(computeInt(tab.story.getRegistry().get("__uib__" + element + "_value", "0").toString()));
        }

    }

    private int computeInt(String value)
    {
        if(value == null)
            return 0;

        VariableRegistry registry = tab.story.getRegistry();
        try
        {
            return registry.typeOf(value) == Integer.class ? (Integer)registry.get(value, 0) : Integer.parseInt(value);
        }
        catch(NumberFormatException nfe2)
        {
            return 0;
        }
    }

    private String computeText(String string) throws BSTException
    {
        if(string == null)
            return "";
        try
        {
            if(string.startsWith(">"))
            {
                return computeText(Integer.valueOf(string.substring(1)), true);
            }
            else if(string.startsWith("&"))
            {
                return computeText(Integer.valueOf(string.substring(1)), false);
            }
        }
        catch(NumberFormatException e)
        {}
        return string;
    }

    private String computeText(int i, boolean isVirtual) throws BSTException
    {
        if(isVirtual)
        {
            return StoryUtils.solveVariables((VirtualNode)tab.story.getNode(i), tab.story);
        }
        else
        {
            int j = ((LogicalNode)tab.story.getNode(i)).solve();
            return computeText(j, tab.story.getNode(j) instanceof LogicalNode ? false : true);
        }
    }

    @Override
    public void restoreState() throws BSTException
    {
        // While save states aim to be restored at a minimum cost,
        // UIB will be reset when restored. This is to avoid glitches
        // and make sure we build onto a clean UIB.
        resetUib();

        if(Boolean.parseBoolean(tab.story.getRegistry().get("__uib__initialized", "false").toString()))
        {
            // Initialize
            initialize();

            // Restore all values
            for(String element : uibComponents.keySet())
            {
                Map<String, String> componentInfo = getUibInfo(element);

                for(String varName : componentInfo.keySet())
                {
                    String value = componentInfo.get(varName);
                    switch(varName)
                    {
                    case "max":
                        setElementMax(element, Integer.parseInt(value));
                        break;
                    case "min":
                        setElementMin(element, Integer.parseInt(value));
                        break;
                    case "value":
                        setElementValue(element, value);
                        break;
                    }
                }
            }

            // Redefine if visible or not
            if("true".equals(tab.story.getRegistry().get("__uib__visible", null)))
            {
                tab.uibPanel.setVisible(true);
            }
            else
            {
                tab.uibPanel.setVisible(false);
            }
        }
    }

    private Map<String, String> getUibInfo(String element)
    {
        HashMap<String, String> map = new HashMap<>();
        for(String varName : tab.story.getRegistry().getAllString().keySet())
        {
            if(varName.startsWith("__uib__" + element + "_"))
            {
                map.put(varName.substring("__uib__".length() + element.length() + "_".length()), tab.story.getRegistry().getAllString().get(varName));
            }
        }
        return map;
    }

    public void resetUib()
    {
        if(uibInitialized || tab.uibPanel != null)
        {
            StoryPanel.log("=> Performing UIB Reset");
            tab.uibPanel.removeAll();
            tab.uibPanel.revalidate();
            tab.uibPanel.repaint();
            tab.uibPanel.setVisible(false);
            uibComponents.clear();
            uibInitialized = false;
        }
    }

}
