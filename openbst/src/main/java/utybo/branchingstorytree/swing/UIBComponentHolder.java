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
    private final JComponent component;
    private Object value;
    private boolean dynamic, textual;

    public UIBComponentHolder(final JComponent component, final boolean dynamic, final boolean textual)
    {
        this.component = component;
        this.dynamic = dynamic;
        setTextual(textual);
    }

    public JComponent getComponent()
    {
        return component;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(final Object value)
    {
        this.value = value;
    }

    public boolean isDynamic()
    {
        return dynamic;
    }

    public void setDynamic(final boolean dynamic)
    {
        this.dynamic = dynamic;
    }

    public boolean isTextual()
    {
        return textual;
    }

    public void setTextual(final boolean textual)
    {
        this.textual = textual;
    }
}
