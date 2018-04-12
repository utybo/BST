/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;

import org.apache.commons.io.IOUtils;

import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.img.IMGHandler;
import utybo.branchingstorytree.swing.Icons;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.OpenBSTGUI;
import utybo.branchingstorytree.swing.VisualsUtils;
import utybo.branchingstorytree.swing.utils.Pair;
import utybo.branchingstorytree.swing.visuals.AccumulativeRunnable;
import utybo.branchingstorytree.swing.visuals.StoryPanel;

public class IMGClient implements IMGHandler
{
    private final HashMap<String, BufferedImage> images = new HashMap<>();
    private final HashMap<String, String> b64images = new HashMap<>();
    private static final HashMap<Integer, String> internalb64images = new HashMap<>();
    private BufferedImage current = null;
    private String currentBase64 = null;
    private final StoryPanel panel;

    public IMGClient(final StoryPanel panel)
    {
        this.panel = panel;
    }

    @Override
    public void load(final InputStream in, final String name) throws BSTException
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(in, baos);
            images.put(name, ImageIO.read(new ByteArrayInputStream(baos.toByteArray())));
            b64images.put(name, Base64.getMimeEncoder().encodeToString(baos.toByteArray())
                    .replaceAll("[\n\r]", ""));
        }
        catch(final IOException e)
        {
            throw new BSTException(-1, "Could not load image", e, "<none>");
        }
    }

    @Override
    public void setBackground(final String name)
    {
        panel.getStory().getRegistry().put("__img__background", name);
        if(name == null)
        {
            current = null;
        }
        else
        {
            if(name.startsWith("$internal"))
            {
                if(internalb64images.isEmpty())
                    initInternal();
                panel.getClient().warnExperimental(-1, "<unknown>", "$internal background");
                int i = Integer.parseInt(name.substring(9));
                current = Icons.getBackground(i);
                currentBase64 = internalb64images.get(i);
            }
            else
            {
                current = images.get(name);
                currentBase64 = b64images.get(name);
            }
        }
    }

    public static void initInternal()
    {
        if(!internalb64images.isEmpty())
            return;
            
        OpenBST.LOG.info("Creating internal images cache, this could take some time...");
        ProgressMonitor[] pm = new ProgressMonitor[1];
        List<BufferedImage> bgs = Icons.getAllBackgrounds();
        VisualsUtils.invokeSwingAndWait(() ->
        {
            pm[0] = new ProgressMonitor(OpenBSTGUI.getInstance(), "Caching internal images...",
                    "Initializing...", 0, bgs.size());
            pm[0].setMillisToDecideToPopup(1);
            pm[0].setMillisToPopup(1);
        });
        AccumulativeRunnable<Pair<Integer, String>> r = new AccumulativeRunnable<Pair<Integer, String>>()
        {
            @Override
            public void run(List<Pair<Integer, String>> pairs)
            {
                pm[0].setProgress(pairs.get(pairs.size() - 1).a);
                pm[0].setNote(pairs.get(pairs.size() - 1).b);
            }
        };
        for(int i = 0; i < bgs.size(); i++)
        {
            r.add(new Pair<>(i, "Caching file " + i));
            BufferedImage img = bgs.get(i);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try
            {
                ImageIO.write(img, "JPG", baos);
            }
            catch(IOException e)
            {
                OpenBST.LOG.warn("Failed to create Base64 background", e);
            }
            internalb64images.put(i, Base64.getMimeEncoder().encodeToString(baos.toByteArray())
                    .replaceAll("[\n\r]", ""));
        }
        VisualsUtils.invokeSwingAndWait(() -> pm[0].close());

    }

    public BufferedImage getCurrentBackground()
    {
        return current;
    }

    public String getBase64Background()
    {
        return currentBase64;
    }

    public void reset()
    {
        images.clear();
        b64images.clear();
        setBackground(null);
    }

    public void restoreSaveState()
    {
        try
        {
            setBackground(panel.getStory().getRegistry().get("__img__background", null).toString());
        }
        catch(final NullPointerException e)
        {}
    }
}
