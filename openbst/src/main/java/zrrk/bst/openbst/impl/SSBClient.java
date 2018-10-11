/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.ssb.SSBHandler;
import zrrk.bst.openbst.visuals.StoryPanel;

public class SSBClient implements SSBHandler
{
    private final HashMap<String, MediaPlayer> resources = new HashMap<>();
    private MediaPlayer currentAmbient;
    private final StoryPanel panel;

    public SSBClient(final StoryPanel panel)
    {
        new JFXPanel(); // Init JavaFX
        this.panel = panel;
    }

    @Override
    public void load(final InputStream in, final String name) throws BSTException
    {
        // Write the input stream in a temporary folder
        try
        {
            File f = File.createTempFile("openbst" + name, "");
            f.deleteOnExit();
            IOUtils.copy(in, new FileOutputStream(f));
            final Media m = new Media(f.toURI().toString());
            resources.put(name, new MediaPlayer(m));
        }
        catch(IOException e)
        {
            throw new BSTException(-1, "Could not create temporary file", e, "<none>");
        }
    }

    @Override
    public void load(final File file, final String name) throws BSTException
    {
        final Media m = new Media(file.toURI().toString());
        resources.put(name, new MediaPlayer(m));
    }

    @Override
    public void play(final String name)
    {
        resources.get(name).seek(new Duration(0));
        resources.get(name).play();
    }

    @Override
    public void ambient(final String name)
    {
        if(currentAmbient != null)
        {
            currentAmbient.stop();
        }
        panel.getStory().getRegistry().put("__ssb__ambient", name);
        final MediaPlayer sound = resources.get(name);
        currentAmbient = sound;
        sound.setCycleCount(Integer.MAX_VALUE);
        sound.play();
    }

    @Override
    public void stop()
    {
        if(currentAmbient != null)
        {
            currentAmbient.stop();
            currentAmbient = null;
            panel.getStory().getRegistry().put("__ssb__ambient", "//null");
        }
    }

    public void shutdown()
    {
        resources.forEach((id, clip) ->
        {
            clip.stop();
        });
    }

    public void setMuted(final boolean muted)
    {
        resources.forEach((id, sound) ->
        {
            sound.setMute(muted);
        });
    }

    public void reset()
    {
        resources.forEach((id, clip) ->
        {
            clip.stop();
            clip.dispose();
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
