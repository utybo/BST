/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.visuals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

public abstract class AccumulativeRunnable<T> implements Runnable
{
    private List<T> objects = null;

    @Override
    public final void run()
    {
        run(retrieveObjects());
    }

    public abstract void run(List<T> retrieveObjects);

    private synchronized List<T> retrieveObjects()
    {
        List<T> l = objects;
        objects = null;
        return l;
    }

    @SafeVarargs
    public final synchronized void add(T... obj)
    {
        boolean requirePush = false;
        if(objects == null)
        {
            objects = new ArrayList<>();
            requirePush = true;
        }
        Collections.addAll(objects, obj);
        if(requirePush)
        {
            push();
        }
    }

    public void push()
    {
        SwingUtilities.invokeLater(this);
    }
}
