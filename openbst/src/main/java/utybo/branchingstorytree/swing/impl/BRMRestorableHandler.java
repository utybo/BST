package utybo.branchingstorytree.swing.impl;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.brm.BRMHandler;

public interface BRMRestorableHandler extends BRMHandler
{
    public void restoreSaveState() throws BSTException;
}
