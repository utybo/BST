package utybo.branchingstorytree.api.script;

import java.util.HashMap;

public class VariableRegistry
{
    private HashMap<String, Integer> variables = new HashMap<>();
    private HashMap<String, String> strVar = new HashMap<>();

    public void put(String name, int var)
    {
        remove(name);
        variables.put(name, var);
    }

    public void put(String name, String value)
    {
        remove(name);
        strVar.put(name, value);
    }

    public void remove(String name)
    {
        variables.remove(name);
        strVar.remove(name);
    }

    @Deprecated
    public int getInt(String name)
    {
        return variables.getOrDefault(name, 0);
    }

    public HashMap<String, Integer> getAllInt()
    {
        return variables;
    }

    public void reset()
    {
        for(String s : variables.keySet())
            variables.remove(s);
    }

    public Class<?> typeOf(String name)
    {
        if(variables.containsKey(name))
        {
            return Integer.class;
        }

        return null;
    }

    public Object get(String varName)
    {
        Object tryingToFindMe = null;
        if(variables.containsKey(varName))
        {
            tryingToFindMe = variables.get(varName);
        }
        else if(strVar.containsKey(varName))
        {
            tryingToFindMe = strVar.get(varName);
        }
        return tryingToFindMe;
    }
}
