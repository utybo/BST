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
    private File bstFileLocation;
    private BSTClient client;

    public BRMClient(File bstFile, BSTClient client)
    {
        bstFileLocation = bstFile;
        this.client = client;
    }

    @Override
    public void loadAuto() throws BSTException
    {
        File parent = bstFileLocation.getParentFile();
        File resources = new File(parent, "resources");
        if(resources.exists() && resources.isDirectory())
        {
            // Analysis of module directories list
            for(File moduleFolder : resources.listFiles())
            {
                // Analysis of module directory
                if(!moduleFolder.isDirectory())
                    continue;
                String module = moduleFolder.getName();
                BRMResourceConsumer handler = getHandler(module);
                if(handler != null)
                {
                    for(File file : moduleFolder.listFiles())
                    {
                        handler.load(file.getAbsolutePath(), FilenameUtils.getBaseName(file.getName()));
                    }
                }
            }
        }
    }

    private BRMResourceConsumer getHandler(String module)
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
