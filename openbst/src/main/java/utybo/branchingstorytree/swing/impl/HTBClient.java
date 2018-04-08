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

import org.apache.commons.io.IOUtils;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.Experimental;
import utybo.branchingstorytree.htb.HTBHandler;
import utybo.branchingstorytree.swing.Icons;
import utybo.branchingstorytree.swing.Messagers;
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
        int result = Messagers.showConfirm(OpenBST.getInstance(),
                "<html><body style='width:" + (int)(Icons.getScale() * 300) + "px'>"
                        + Lang.get("html.jsrequest"),
                Messagers.TYPE_QUESTION, Messagers.OPTIONS_YES_NO, Lang.get("html.securityalert"),
                new ImageIcon(Icons.getImage("JSAlert", 48)));
        if(result == Messagers.OPTION_YES)
        {
            nodePanel.setJSEnabled(true);
            return true;
        }
        else
        {
            nodePanel.setJSEnabled(false);
            return false;
        }
    }

    @Override
    public boolean requestHrefAccess()
    {
        int result = Messagers.showConfirm(OpenBST.getInstance(),
                "<html><body style='width:" + (int)(Icons.getScale() * 300) + "px'>"
                        + Lang.get("html.htmlrequest"),
                Messagers.TYPE_QUESTION, Messagers.OPTIONS_YES_NO, Lang.get("html.securityalert"),
                new ImageIcon(Icons.getImage("JSAlert", 48)));
        if(result == Messagers.OPTION_YES)
        {
            nodePanel.setHrefEnabled(true);
            return true;
        }
        else
        {
            nodePanel.setHrefEnabled(false);
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
