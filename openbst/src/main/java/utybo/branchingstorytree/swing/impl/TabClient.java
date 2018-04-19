/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.impl;

import javax.swing.ImageIcon;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.htb.HTBHandler;
import utybo.branchingstorytree.swing.Icons;
import utybo.branchingstorytree.swing.Messagers;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.OpenBSTGUI;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.visuals.NodePanel;
import utybo.branchingstorytree.swing.visuals.StoryPanel;
import utybo.branchingstorytree.xbf.XBFHandler;
import utybo.branchingstorytree.xsf.XSFHandler;

public class TabClient implements BSTClient
{
    private final OpenBSTGUI instance;
    private StoryPanel tab;
    private TabUIB uibHandler;
    private BRMAdvancedHandler brmClient;
    private SSBClient ssbClient;
    private IMGClient imgClient;
    private final BDFClient bdfClient;
    private XBFClient xbfClient;
    private HTBClient htbClient;
    private XSFClient xsfClient = new XSFClient();
    private boolean isExperimental;

    public TabClient(final OpenBSTGUI instance)
    {
        this.instance = instance;
        bdfClient = new BDFClient();
    }

    @Override
    public String askInput(final String message)
    {
        Object input = null;
        while(input == null || input.toString().isEmpty())
        {
            input = Messagers.showInput(instance, message);
        }
        return input.toString();
    }

    @Override
    public void exit()
    {
        instance.removeTab(tab);
    }

    public void setStoryPanel(final StoryPanel sp)
    {
        tab = sp;
        uibHandler = new TabUIB(tab);
        ssbClient = new SSBClient(tab);
        imgClient = new IMGClient(tab);
        xbfClient = new XBFClient(tab, this);
    }

    public void setNodePanel(final NodePanel np)
    {
        htbClient = new HTBClient(np);
    }

    @Override
    public TabUIB getUIBarHandler()
    {
        return uibHandler;
    }

    @Override
    public SSBClient getSSBHandler()
    {
        return ssbClient;
    }

    @Override
    public BRMAdvancedHandler getBRMHandler()
    {
        return brmClient;
    }

    @Override
    public IMGClient getIMGHandler()
    {
        return imgClient;
    }

    @Override
    public BDFClient getBDFHandler()
    {
        return bdfClient;
    }

    @Override
    public XBFHandler getXBFHandler()
    {
        return xbfClient;
    }

    @Override
    public HTBHandler getHTBHandler()
    {
        return htbClient;
    }

    @Override
    public XSFHandler getXSFHandler()
    {
        return xsfClient;
    }

    public void setBRMHandler(BRMAdvancedHandler handler)
    {
        brmClient = handler;
    }

    @Override
    public void warn(String string)
    {
        OpenBST.LOG.warn(string);
    }
    
    public void error(String string)
    {
        OpenBST.LOG.error(string);
        Messagers.showMessage(OpenBSTGUI.getInstance(), string, Messagers.TYPE_ERROR);
    }

    @Override
    public void warnExperimental(int line, String from, String what)
    {
        if(!isExperimental)
        {
            isExperimental = true;
            Messagers.showMessage(OpenBSTGUI.getInstance(),
                    "<html><body style='width:" + (int)(Icons.getScale() * 300) + "px'>"
                            + Lang.get("story.experimental").replace("$l", "" + line).replace("$f",
                                    from).replace("$w", what),
                    Messagers.TYPE_WARNING, Lang.get("story.experimental.title"),
                    new ImageIcon(Icons.getImage("Experiment", 48)));
        }
    }
}
