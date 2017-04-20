package utybo.branchingstorytree.swing.impl;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.brm.BRMHandler;

/**
 * A BRM handler with more abstraction required by OpenBST
 * 
 * @author utybo
 *
 */
public interface BRMAdvancedHandler extends BRMHandler
{
    public void restoreSaveState() throws BSTException;
    
    /**
     * A load method to replace the action-triggered from BRMHandler
     * @throws BSTException
     */
    public void load() throws BSTException;
}
