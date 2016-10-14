package utybo.branchingstorytree.api.story;

import java.util.HashMap;

public class TagHolder
{
    private HashMap<String, String> tags = new HashMap<>();

    public String getTag(String tag)
    {
        return tags.get(tag);
    }

    public void putTag(String tag, String value)
    {
        tags.put(tag, value);
    }

    public HashMap<String, String> getTagMap()
    {
        return tags;
    }
    
    public boolean hasTag(String tag)
    {
        return tags.containsKey(tag);
    }
}
