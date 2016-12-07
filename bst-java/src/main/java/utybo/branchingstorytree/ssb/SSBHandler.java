package utybo.branchingstorytree.ssb;

import utybo.branchingstorytree.brm.BRMResourceConsumer;

public interface SSBHandler extends BRMResourceConsumer
{
    public void load(String relativePath, String name);
    
    public void play(String name);
    
    public void ambient(String name);
    
    public void stop();
}
