/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.Experimental;
import utybo.branchingstorytree.htb.HTBHandler;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.utils.Lang;
import utybo.branchingstorytree.swing.visuals.NodePanel;

public class HTBClient implements HTBHandler
{
    private final HashMap<String, byte[]> map = new HashMap<>();
    private final NodePanel nodePanel;

    public HTBClient(NodePanel panel)
    {
        nodePanel = panel;
    }

    @Override
    public void load(InputStream in, String name) throws BSTException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            IOUtils.copy(in, baos);
            map.put(name, baos.toByteArray());
        }
        catch(IOException e)
        {
            throw new BSTException(-1, "Error while loading " + name, e, "<none>");
        }
    }

    @Override
    public String getAsString(String resource)
    {
        return new String(map.get(resource), StandardCharsets.UTF_8);
    }

    @Override
    public String getAsBase64(String resource)
    {
        return Base64.getMimeEncoder().encodeToString(map.get(resource));
    }

    @Override
    public boolean requestJSAccess()
    {
        int result = JOptionPane.showConfirmDialog(OpenBST.getInstance(),
                "<html><body style='width:300px'>" + Lang.get("html.jsrequest"),
                Lang.get("html.securityalert"), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, new ImageIcon(OpenBST.jsAlert));
        if(result == JOptionPane.YES_OPTION)
        {
            nodePanel.setJSEnabled(true);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean requestHrefAccess()
    {
        int result = JOptionPane.showConfirmDialog(OpenBST.getInstance(),
                "<html><body style='width:300px'>" + Lang.get("html.hrefrequest"),
                Lang.get("html.securityalert"), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, new ImageIcon(OpenBST.hrefAlert));
        if(result == JOptionPane.YES_OPTION)
        {
            nodePanel.setHrefEnabled(true);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean hasResource(String resource)
    {
        return map.containsKey(resource);
    }

    @Override
    @Experimental
    public void applyCSS(String resource)
    {
        nodePanel.addCSSSheet(getAsString(resource));
    }

    @Override
    @Experimental
    public void removeCSS(String resource)
    {
        nodePanel.removeCSSSheet(getAsString(resource));
    }

    @Override
    @Experimental
    public void clearCSS()
    {
        nodePanel.removeAllCSSSheets();
    }

    
}
