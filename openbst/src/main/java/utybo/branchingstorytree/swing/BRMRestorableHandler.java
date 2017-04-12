package utybo.branchingstorytree.swing;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.brm.BRMHandler;

public interface BRMRestorableHandler extends BRMHandler
{
    public void restoreSaveState() throws BSTException;
}
