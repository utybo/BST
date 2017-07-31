/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api;

import utybo.branchingstorytree.bdf.BDFHandler;
import utybo.branchingstorytree.brm.BRMHandler;
import utybo.branchingstorytree.brm.BRMResourceConsumer;
import utybo.branchingstorytree.htb.HTBHandler;
import utybo.branchingstorytree.img.IMGHandler;
import utybo.branchingstorytree.jse.JSEHandler;
import utybo.branchingstorytree.ssb.SSBHandler;
import utybo.branchingstorytree.uib.UIBarHandler;
import utybo.branchingstorytree.xbf.XBFHandler;

/**
 * A BSTClient is the core class to be used for interaction between the engine
 * (bst-java) and the higher-level implementation (OpenBST for example).
 * <p>
 * Module handlers return null by default to indicate that they are not
 * supported.
 *
 * @author utybo
 *
 */
public interface BSTClient
{

    /**
     * Ask the user for input
     *
     * @param message
     *            the message to be shown to them.
     * @return the input. CANNOT BE NULL.
     */
    public String askInput(String message);

    /**
     * Close the current story
     */
    public void exit();

    /**
     * @return The UIB module handler, or null is not supported
     */
    public default UIBarHandler getUIBarHandler()
    {
        return null;
    }

    /**
     * @return The SSB module handler, or null is not supported
     */
    public default SSBHandler getSSBHandler()
    {
        return null;
    }

    /**
     * @return The BRM module handler, or null is not supported
     */
    public default BRMHandler getBRMHandler()
    {
        return null;
    }

    /**
     * @return The IMG module handler, or null is not supported
     */
    public default IMGHandler getIMGHandler()
    {
        return null;
    }

    /**
     * @return The BDF module handler, or null is not supported
     */
    public default BDFHandler getBDFHandler()
    {
        return null;
    }

    public default JSEHandler getJSEHandler()
    {
        return null;
    }

    public default XBFHandler getXBFHandler()
    {
        return null;
    }

    public default HTBHandler getHTBHandler()
    {
        return null;
    }

    public default BRMResourceConsumer getResourceHandler(String name)
    {
        switch(name)
        {
        case "ssb":
            return getSSBHandler();
        case "img":
            return getIMGHandler();
        case "bdf":
            return getBDFHandler();
        case "xbf":
            return getXBFHandler();
        case "htb":
            return getHTBHandler();
        default:
            return null;
        }
    }

    public default void warn(String string)
    {
        System.err.println(string);
    }
}
