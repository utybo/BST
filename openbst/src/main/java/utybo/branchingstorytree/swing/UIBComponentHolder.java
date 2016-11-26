/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import javax.swing.JComponent;

public class UIBComponentHolder
{
    private JComponent component;
    private Object value;
    private boolean dynamic, textual;

    public UIBComponentHolder(JComponent component, boolean dynamic, boolean textual)
    {
        this.component = component;
        this.dynamic = dynamic;
        this.setTextual(textual);
    }

    public JComponent getComponent()
    {
        return component;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public boolean isDynamic()
    {
        return dynamic;
    }

    public void setDynamic(boolean dynamic)
    {
        this.dynamic = dynamic;
    }

    public boolean isTextual()
    {
        return textual;
    }

    public void setTextual(boolean textual)
    {
        this.textual = textual;
    }
}
