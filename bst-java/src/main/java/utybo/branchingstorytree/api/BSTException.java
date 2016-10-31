/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api;

public class BSTException extends Exception
{
    private static final long serialVersionUID = 1L;

    private int where;

    public BSTException(int where)
    {
        super();
        this.where = where;
    }

    public BSTException(int where, String message, Throwable cause)
    {
        super(message, cause);
        this.where = where;
    }

    public BSTException(int where, String message)
    {
        super(message);
        this.where = where;
    }

    public BSTException(int where, Throwable cause)
    {
        super(cause);
        this.where = where;
    }

    public int getWhere()
    {
        return where;
    }

    public void setWhere(int where)
    {
        this.where = where;
    }

}
