/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.ssb.SSBHandler;
import utybo.branchingstorytree.swing.visuals.StoryPanel;

public class SSBClient implements SSBHandler
{
    private final HashMap<String, BasicPlayer> resources = new HashMap<>();
    private BasicPlayer currentAmbient;
    private final StoryPanel panel;

    public SSBClient(final StoryPanel panel)
    {
        this.panel = panel;
    }

    @Override
    public void load(final InputStream in, final String name) throws BSTException
    {
        BasicPlayer bp = new BasicPlayer();
        try
        {
            bp.open(in);
        }
        catch(BasicPlayerException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        resources.put(name, bp);

    }
    
    

    @Override
    public void load(File file, String name) throws BSTException, FileNotFoundException
    {
        BasicPlayer bp = new BasicPlayer();
        try
        {
            bp.open(file);
        }
        catch(BasicPlayerException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        resources.put(name, bp);
    }

    @Override
    public void play(final String name)
    {
        try
        {
            resources.get(name).stop();
            resources.get(name).play();
        }
        catch(BasicPlayerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void ambient(final String name)
    {
        try
        {
            if(currentAmbient != null)
            {
                currentAmbient.stop();
            }
            panel.getStory().getRegistry().put("__ssb__ambient", name);
            BasicPlayer bp = resources.get(name);
            bp.play();
            currentAmbient = bp;
        }
        catch(BasicPlayerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void stop()
    {
        try
        {
            System.out.println("1");
            currentAmbient.pause();
            currentAmbient.stop();
            System.out.println("2");
        }
        catch(BasicPlayerException e)
        {
            e.printStackTrace();
        }
        currentAmbient = null;
        panel.getStory().getRegistry().put("__ssb__ambient", "//null");
    }

    public void shutdown()
    {
        resources.forEach((id, clip) ->
        {
            try
            {
                clip.stop();
            }
            catch(BasicPlayerException e)
            {
                e.printStackTrace();
            }
        });
    }

    public void setMuted(final boolean muted)
    {
        resources.forEach((id, sound) ->
        {
            try
            {
                sound.setGain(muted ? 0F : 0.5F);
            }
            catch(BasicPlayerException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    public void reset()
    {
        resources.forEach((id, clip) ->
        {
            try
            {
                clip.stop();
            }
            catch(BasicPlayerException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        resources.clear();
    }

    public void restoreSaveState()
    {
        try
        {
            ambient(panel.getStory().getRegistry().get("__ssb__ambient", null).toString());
        }
        catch(final NullPointerException e)
        {
            // This is thrown is the save state has an invalid tag, which can happen in many cases
        }
    }

}
