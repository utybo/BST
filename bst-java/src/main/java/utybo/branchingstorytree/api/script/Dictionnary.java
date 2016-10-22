package utybo.branchingstorytree.api.script;

import javax.swing.JOptionPane;

import utybo.branchingstorytree.api.BSTCentral;
import utybo.branchingstorytree.api.BSTException;

public class Dictionnary
{
    public ScriptAction getAction(String action, String desc, VariableRegistry registry) throws BSTException
    {
        switch(action)
        {
        // TODO Handle class cast exceptions
        case "incr":
            return () ->
            {
                if(registry.typeOf(desc) != Integer.class)
                    throw new BSTException(-1, "incr : The variable " + desc + " is not a number.");
                registry.put(desc, (Integer)registry.get(desc) + 1);

            };
        case "decr":
            return () -> registry.put(desc, (Integer)registry.get(desc) - 1);
        case "set":
            return () ->
            {
                String varName = desc.split(",")[0];
                String value = desc.substring(desc.indexOf(',') + 1);
                try
                {
                    registry.put(varName, Integer.parseInt(value));
                }
                catch(NumberFormatException e)
                {
                    // No printStackTrace because this exception is expected in many cases
                    registry.put(varName, value);

                }
            };
        case "exit":
            return () -> System.exit(0);
        case "input":
            return () -> {
                String varName = desc.split(",")[0];
                String msg = desc.substring(desc.indexOf(',') + 1);
                String input = null;
                while(input == null || input.isEmpty())
                    input = JOptionPane.showInputDialog(BSTCentral.getPlayerComponent(), msg);
                registry.put(varName, input);
            };
        default:
            return null;
        }
    }

    public ScriptChecker getChecker(String action, String desc, VariableRegistry registry) throws BSTException
    {
        switch(action)
        {
        // TODO Handle class cast exceptions + string comparison
        case "equ":
            return () ->
            {
                String varName = desc.split(",")[0];
                Object var = registry.get(varName);
                String isEqualWith = desc.split(",")[1];

                try
                {
                    int i = Integer.valueOf(isEqualWith);
                    if(var.getClass() == Integer.class)
                    {
                        return ((Integer)var).intValue() == i;
                    }
                }
                catch(NumberFormatException e)
                {}
                return var.toString().equals(isEqualWith);
            };
        case "not":
            return () ->
            {
                String varName = desc.split(",")[0];
                Object var = registry.get(varName);
                String isEqualWith = desc.split(",")[1];

                try
                {
                    int i = Integer.valueOf(isEqualWith);
                    if(var.getClass() == Integer.class)
                    {
                        return ((Integer)var).intValue() != i;
                    }
                }
                catch(NumberFormatException e)
                {}
                return !var.toString().equals(isEqualWith);
            };
        default:
            return null;
        }
    }
}
