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
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FilenameUtils;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.ssb.SSBHandler;

public class SSBClient implements SSBHandler
{
    private HashMap<String, Clip> resources = new HashMap<>();
    private Clip currentAmbient;
    private boolean muted;

    @Override
    public void load(String pathToResource, String name) throws BSTException
    {
        try
        {
            File f = new File(pathToResource);
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            if(FilenameUtils.isExtension(pathToResource, "mp3") || FilenameUtils.isExtension(pathToResource, "ogg"))
            {
                AudioFormat base = ais.getFormat();

                System.out.println(base.getChannels() * 2);
                AudioFormat decoded = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, base.getSampleRate(), 16, base.getChannels(), base.getChannels() * 2, base.getSampleRate(), false);
                ais = AudioSystem.getAudioInputStream(decoded, ais);
            }
            AudioFormat format = ais.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            System.out.println("FF");
            System.out.println(ais.getFrameLength());
            System.out.println(ais.getFormat().getFrameSize());
            System.out.println(ais.getFrameLength() * ais.getFormat().getFrameSize());
            Clip c = (Clip)AudioSystem.getLine(info);
            c.open(ais);
            resources.put(name, c);
        }
        catch(UnsupportedAudioFileException | IOException | LineUnavailableException e)
        {
            throw new BSTException(-1, "Error when loading " + pathToResource, e);
        }
        catch(NegativeArraySizeException e)
        {
            // Happens on Linux using IcedTea -- no known fix...
            e.printStackTrace();

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
        if(currentAmbient != null)
        {
            currentAmbient.stop();
        }
        Clip c = resources.get(name);
        if(!muted && c != null)
            c.loop(Clip.LOOP_CONTINUOUSLY);
        currentAmbient = c;
    }

    @Override
    public void stop()
    {
        currentAmbient.stop();
        currentAmbient = null;
    }

    public void setMuted(boolean muted)
    {
        resources.forEach((id, clip) ->
        {
            BooleanControl control = (BooleanControl)clip.getControl(BooleanControl.Type.MUTE);
            control.setValue(muted);
        });
    }

}
