package utybo.branchingstorytree.api;

import java.awt.Component;

/**
 * The BSTCentral class is a set of useful methods for better interaction
 * between the Player and the BST API. Most options can return null if they are
 * unsupported. The BSTCentral is aimed at the AWT/Swing framework for all
 * UI-related tasks.
 * 
 * @author utybo
 *
 */
public class BSTCentral
{
    private static Component playerComponent;

    public static Component getPlayerComponent()
    {
        return playerComponent;
    }

    public static void setPlayerComponent(Component playerComponent)
    {
        BSTCentral.playerComponent = playerComponent;
    }
    
    
}
