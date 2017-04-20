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
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FilenameUtils;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.brm.BRMResourceConsumer;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.visuals.AccumulativeRunnable;

public class BRMFileClient implements BRMAdvancedHandler
{
    private final File bstFileLocation;
    private final BSTClient client;
    private final BranchingStory origin;
    private boolean initialized = false;

    public BRMFileClient(final File bstFile, final BSTClient client, BranchingStory origin)
    {
        bstFileLocation = bstFile;
        this.client = client;
        this.origin = origin;
    }
    
    private ProgressMonitor pm;

    public void load() throws BSTException
    {
        initialized = true;
        origin.getRegistry().put("__brm_initialized", 1);
        System.out.println(bstFileLocation.getAbsolutePath());
        final File parent = bstFileLocation.getParentFile();
        final File resources = new File(parent, "resources");
        System.out.println(resources.getAbsolutePath());
        if(resources.exists() && resources.isDirectory())
        {
            int total = countFiles(resources);
            int current = 0;
            invokeAndWait(() -> pm = new ProgressMonitor(OpenBST.getInstance(), "Loading resources...", "Initializing...", 0, total));
            AccumulativeRunnable<Integer> r = new AccumulativeRunnable<Integer>()
            {
                @Override
                public void run(List<Integer> ints)
                {
                    pm.setProgress(ints.get(ints.size() -1));
                }
            };
            System.out.println("1");
            // Analysis of module directories list
            for(final File moduleFolder : resources.listFiles())
            {
                System.out.println("2");
                // Analysis of module directory
                if(!moduleFolder.isDirectory())
                {
                    continue;
                }
                final String module = moduleFolder.getName();
                final BRMResourceConsumer handler = client.getResourceHandler(module);
                if(handler != null)
                {
                    System.out.println("3");
                    for(final File file : moduleFolder.listFiles())
                    {
                        System.out.println("4");
                        try
                        {
                            r.add(current++);
                            handler.load(file, FilenameUtils.getBaseName(file.getName()));
                        }
                        catch(FileNotFoundException e)
                        {
                            throw new Error("Impossible state", e);
                        }
                    }
                }
            }
        }
    }

    private void invokeAndWait(Runnable r)
    {
        try
        {
            SwingUtilities.invokeAndWait(r);
        }
        catch(InvocationTargetException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public int countFiles(File folder)
    {
        int i = 0;
        for(File f : folder.listFiles())
        {
            if(f.isDirectory())
                i += countFiles(f);
            else
                i += 1;
        }
        return i;
    }

    public void restoreSaveState() throws BSTException
    {
        Object o = origin.getRegistry().get("__brm_initialized", 0);
        if(!initialized && o instanceof Integer && (Integer)o == 1)
        {
            load();
        }
    }
}
