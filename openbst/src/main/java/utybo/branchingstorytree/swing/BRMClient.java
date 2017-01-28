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

import org.apache.commons.io.FilenameUtils;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.brm.BRMHandler;
import utybo.branchingstorytree.brm.BRMResourceConsumer;

public class BRMClient implements BRMHandler
{
    private final File bstFileLocation;
    private final BSTClient client;

    public BRMClient(final File bstFile, final BSTClient client)
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
                final BRMResourceConsumer handler = getHandler(module);
                if(handler != null)
                {
                    for(final File file : moduleFolder.listFiles())
                    {
                        handler.load(file.getAbsolutePath(), FilenameUtils.getBaseName(file.getName()));
                    }
                }
            }
        }
    }

    private BRMResourceConsumer getHandler(final String module)
    {
        switch(module)
        {
        case "ssb":
            return client.getSSBHandler();
        case "img":
            return client.getIMGHandler();
        case "bdf":
            return client.getBDFHandler();
        default:
            return null;
        }
    }
}
