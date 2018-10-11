/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.editor;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractListModel;

/**
 * A list model that uses a List as its data vector, providing synchronization
 * between both, and fully implementing the List interface.
 * 
 * Simply use the provided functions instead of the ones of the underlying list.
 * 
 * @author utybo
 *
 * @param <T>
 */
public class ListListModel<T> extends AbstractListModel<T> implements List<T>
{
    private static final long serialVersionUID = 1L;
    private List<T> list;

    public ListListModel(List<T> list)
    {
        this.list = list;
    }

    @Override
    public int getSize()
    {
        return list.size();
    }

    @Override
    public T getElementAt(int index)
    {
        return list.get(index);
    }

    @Override
    public int size()
    {
        return list.size();
    }

    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator()
    {
        return list.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return list.toArray();
    }

    @SuppressWarnings("hiding")
    @Override
    public <T> T[] toArray(T[] a)
    {
        return list.toArray(a);
    }

    @Override
    public boolean add(T e)
    {
        if(list.add(e))
        {
            fireIntervalAdded(this, list.size() - 1, list.size() - 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o)
    {
        int index = list.indexOf(o);
        if(list.remove(o))
        {
            fireIntervalRemoved(this, index, index);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c)
    {
        if(c.size() > 0)
        {
            int index0 = list.size();
            if(list.addAll(c))
            {
                fireIntervalAdded(this, index0, list.size() - 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c)
    {
        if(list.addAll(c))
        {
            fireContentsChanged(this, index, list.size() - 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        int previousSize = list.size();
        if(list.removeAll(c))
        {
            fireContentsChanged(this, 0, previousSize - 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        int previousSize = list.size();
        if(list.retainAll(c))
        {
            fireContentsChanged(this, 0, previousSize - 1);
            return true;
        }
        return false;
    }

    @Override
    public void clear()
    {
        int index1 = list.size() - 1;
        list.clear();
        fireIntervalRemoved(this, 0, index1);

    }

    @Override
    public T get(int index)
    {
        return list.get(index);
    }

    @Override
    public T set(int index, T element)
    {
        T t = list.set(index, element);
        fireContentsChanged(this, index, index);
        return t;
    }

    @Override
    public void add(int index, T element)
    {
        int oldSize = size();
        list.add(index, element);
        if(oldSize != size())
            fireIntervalAdded(this, index, index);
    }

    @Override
    public T remove(int index)
    {
        int oldSize = size();
        T t = list.remove(index);
        if(oldSize != size())
            fireIntervalRemoved(this, index, index);
        return t;
    }

    @Override
    public int indexOf(Object o)
    {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator()
    {
        return list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index)
    {
        return list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex)
    {
        return list.subList(fromIndex, toIndex);
    }

    public int addSorted(T element, Comparator<T> comparator)
    {
        if(isEmpty())
        {
            add(element);
            return 0;
        }

        for(int i = 0; i < list.size(); i++)
        {
            if(i == 0)
            {
                if(comparator.compare(element, list.get(i)) <= 0)
                {
                    add(0, element);
                    return 0;
                }
            }
            if(i == list.size() - 1)
            {
                add(element);
                return list.size() - 1;
            }
            else if(i != 0 && comparator.compare(list.get(i - 1), element) <= 0
                    && comparator.compare(element, list.get(i)) <= 0)
            {
                add(i, element);
                return i;
            }
        }

        return -1;
    }

}
