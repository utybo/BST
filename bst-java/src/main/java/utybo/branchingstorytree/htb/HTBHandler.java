package utybo.branchingstorytree.htb;

import utybo.branchingstorytree.brm.BRMResourceConsumer;

public interface HTBHandler extends BRMResourceConsumer
{
    public boolean hasResource(String resource);
    
    public String getAsString(String resource);

    public String getAsBase64(String resource);

    public boolean requestJSAccess();

    public boolean requestHrefAccess();

}
