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
import java.io.FileNotFoundException;

import org.apache.commons.io.FilenameUtils;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.brm.BRMHandler;
import utybo.branchingstorytree.brm.BRMResourceConsumer;

public class BRMFileClient implements BRMHandler
{
    private final File bstFileLocation;
    private final BSTClient client;

    public BRMFileClient(final File bstFile, final BSTClient client)
    {
        bstFileLocation = bstFile;
        this.client = client;
    }

    @Override
    public void loadAuto() throws BSTException
    {
        final File parent = bstFileLocation.getParentFile();
        final File resources = new File(parent, "resources");
        if(resources.exists() && resources.isDirectory())
        {
            // Analysis of module directories list
            for(final File moduleFolder : resources.listFiles())
            {
                // Analysis of module directory
                if(!moduleFolder.isDirectory())
                {
                    continue;
                }
                final String module = moduleFolder.getName();
                final BRMResourceConsumer handler = client.getResourceHandler(module);
                if(handler != null)
                {
                    for(final File file : moduleFolder.listFiles())
                    {
                        try
                        {
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
}
