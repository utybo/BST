/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.ssb.SSBHandler;

public class SSBClient implements SSBHandler
{
    private HashMap<String, Clip> resources = new HashMap<>();
    private Clip currentAmbient;
    private boolean muted;
    private StoryPanel panel;

    public SSBClient(StoryPanel panel)
    {
        this.panel = panel;
    }

    @Override
    public void load(String pathToResource, String name) throws BSTException
    {
        try
        {
            File f = new File(pathToResource);
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            AudioFormat format = ais.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip c = (Clip)AudioSystem.getLine(info);
            c.open(ais);
            resources.put(name, c);
            FloatControl control = (FloatControl)c.getControl(FloatControl.Type.VOLUME);
            control.setValue(control.getMaximum());
        }
        catch(UnsupportedAudioFileException | IOException | LineUnavailableException e)
        {
            throw new BSTException(-1, "Error when loading " + pathToResource, e);
        }
    }

    @Override
    public void play(String name)
    {
        Clip c = resources.get(name);
        c.stop();
        c.setMicrosecondPosition(0L);
        if(!muted)
            c.start();
    }

    @Override
    public void ambient(String name)
    {
        System.out.println("Playing " + name);
        if(currentAmbient != null)
        {
            currentAmbient.stop();
        }
        panel.story.getRegistry().put("__ssb__ambient", name);
        Clip c = resources.get(name);
        if(c != null)
            c.loop(Clip.LOOP_CONTINUOUSLY);
        currentAmbient = c;
    }

    @Override
    public void stop()
    {
        currentAmbient.stop();
        currentAmbient = null;
        panel.story.getRegistry().put("__ssb__ambient", "//null");
    }

    public void shutdown()
    {
        resources.forEach((id, clip) ->
        {
            clip.stop();
            clip.close();
        });
    }

    public void setMuted(boolean muted)
    {
        resources.forEach((id, clip) ->
        {
            FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.VOLUME);
            control.setValue(muted ? 0F : control.getMaximum());
        });
    }

    public void reset()
    {
        resources.forEach((id, clip) ->
        {
            clip.stop();
            clip.setMicrosecondPosition(0L);
        });
        resources.clear();
    }

    public void restoreSaveState()
    {
        try
        {
            ambient(panel.story.getRegistry().get("__ssb__ambient", null).toString());
        }
        catch(NullPointerException e)
        {
            // This is thrown is the save state has an invalid tag, which can happen in many cases
        }
    }

}
