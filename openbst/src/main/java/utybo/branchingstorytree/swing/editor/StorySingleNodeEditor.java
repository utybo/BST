/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.editor;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class StorySingleNodeEditor extends JPanel
{
    public static enum Status
    {
        OK, ERROR
    };

    private Status status;

    public abstract String getSummary();

    public abstract String getIdentifier();

    protected abstract StoryNodeIdComponent getId();

    public String toString()
    {
        return getIdentifier() + " - " + getSummary();
    }

    public void updateUI()
    {
        super.updateUI();
        doUpdateUI(this);
    }

    private void doUpdateUI(Container cont)
    {
        for(Component c : cont.getComponents())
        {
            if(c instanceof JComponent)
                ((JComponent)c).updateUI();
            if(c instanceof Container)
                doUpdateUI((Container)c);

        }
    }

    public boolean matchesId(StoryNodeIdComponent id)
    {
        return getId().matches(id);
    }

    public boolean matchesId(StorySingleNodeEditor other)
    {
        return matchesId(other.getId());
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    /**
     * @return The ID of the node in this editor, or -1 if it uses an alias
     */
    public int getRawIntegerId()
    {
        return getId().getRawIntegerId();
    }

    protected void setInitialIntegerId(int id)
    {
        getId().setInitialIntegerId(id);

    }

}
