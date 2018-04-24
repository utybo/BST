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
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.brm.BRMResourceConsumer;
import utybo.branchingstorytree.swing.impl.BRMAdvancedHandler;
import utybo.branchingstorytree.swing.impl.LoadStatusCallback;

public class BRMVirtualFileClient implements BRMAdvancedHandler
{
    private final VirtualFileHolder vfHolder;
    private final BSTClient client;
    private final BranchingStory origin;
    private boolean initialized;
    private LoadStatusCallback callback;

    public BRMVirtualFileClient(final VirtualFileHolder vfHolder, final BSTClient client,
            final BranchingStory story)
    {
        this.vfHolder = vfHolder;
        this.client = client;
        origin = story;
    }

    @Override
    public void load() throws BSTException
    {
        initialized = true;
        origin.getRegistry().put("__brm_initialized", 1);
        Pattern filePattern = Pattern.compile("resources\\/(.+?)\\/(.+)");

        if(callback != null)
        {
            callback.setTotal(vfHolder.size());
        }

        int i = 0;
        for(VirtualFile vf : vfHolder)
        {
            if(callback != null)
                callback.updateStatus(i++, "Processing " + vf.getName());
            Matcher m = filePattern.matcher(vf.getName());
            if(m.matches())
            {
                String module = m.group(1);
                String name = m.group(2);
                BRMResourceConsumer consumer = client.getResourceHandler(module);
                if(consumer != null)
                {
                    consumer.load(new ByteArrayInputStream(vf.getData()),
                            FilenameUtils.getBaseName(name));
                }
            }
        }
    }

    @Override
    public void restoreSaveState() throws BSTException
    {
        Object o = origin.getRegistry().get("__brm_initialized", 0);
        if(!initialized && o instanceof Integer && (Integer)o == 1)
        {
            load();
        }
    }

    @Override
    public void setLoadCallback(LoadStatusCallback callback)
    {
        this.callback = callback;
    }
}
