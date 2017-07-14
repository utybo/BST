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
            objects = new ArrayList<T>();
            requirePush = true;
        }
        Collections.addAll(objects, obj);
        if(requirePush)
            push();
    }
    
    public void push()
    {
        SwingUtilities.invokeLater(this);
    }
}
