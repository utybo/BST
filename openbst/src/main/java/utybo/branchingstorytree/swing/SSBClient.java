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
import java.util.HashMap;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.ssb.SSBHandler;

public class SSBClient implements SSBHandler
{
    private final HashMap<String, MediaPlayer> resources = new HashMap<>();
    private MediaPlayer currentAmbient;
    private boolean muted;
    private final StoryPanel panel;

    public SSBClient(final StoryPanel panel)
    {
        new JFXPanel(); // Init JavaFX
        this.panel = panel;
    }

    @Override
    public void load(final String pathToResource, final String name) throws BSTException
    {
        Media m = new Media(new File(pathToResource).toURI().toString());
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
        System.out.println("Playing " + name);
        if(currentAmbient != null)
        {
            currentAmbient.stop();
        }
        panel.story.getRegistry().put("__ssb__ambient", name);
        final MediaPlayer sound = resources.get(name);
        sound.setCycleCount(Integer.MAX_VALUE);
        sound.play();
        currentAmbient = sound;
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
            ambient(panel.story.getRegistry().get("__ssb__ambient", null).toString());
        }
        catch(final NullPointerException e)
        {
            // This is thrown is the save state has an invalid tag, which can happen in many cases
        }
    }

}
