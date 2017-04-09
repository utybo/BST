/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.virtualfiles;

import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.brm.BRMHandler;
import utybo.branchingstorytree.brm.BRMResourceConsumer;

public class BRMVirtualFileClient implements BRMHandler
{
    private final VirtualFileHolder vfHolder;
    private final BSTClient client;

    public BRMVirtualFileClient(final VirtualFileHolder vfHolder, final BSTClient client)
    {
        this.vfHolder = vfHolder;
        this.client = client;
    }

    @Override
    public void loadAuto() throws BSTException
    {
        Pattern filePattern = Pattern.compile("resources\\/(.+?)\\/(.+)");
        for(VirtualFile vf : vfHolder)
        {
            Matcher m = filePattern.matcher(vf.getName());
            if(m.matches())
            {
                String module = m.group(1);
                String name = m.group(2);
                BRMResourceConsumer consumer = client.getResourceHandler(module);
                if(consumer != null)
                    consumer.load(new ByteArrayInputStream(vf.getData()), FilenameUtils.getBaseName(name));
            }
        }
    }

}
