package utybo.branchingstorytree.swing;

import javax.swing.JOptionPane;

import utybo.branchingstorytree.api.BSTClient;

public class TabClient implements BSTClient
{
    private OpenBST instance;
    private StoryPanel tab;

    public TabClient(OpenBST instance)
    {
        this.instance = instance;
    }

    @Override
    public String askInput(String message)
    {
        String input = null;
        while(input == null || input.isEmpty())
        {
            input = JOptionPane.showInputDialog(instance, message);
        }
        return input;
    }

    @Override
    public void exit()
    {
        instance.removeStory(tab);
    }

    public void setStoryPanel(StoryPanel sp)
    {
        tab = sp;
    }

}
