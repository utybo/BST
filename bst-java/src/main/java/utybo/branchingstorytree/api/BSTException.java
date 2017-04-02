/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api;

/**
 * A BSTException is an exception that is thrown by a component of the BST
 * language or of the bst-java implementation of the language, or anything else
 * related to BST.
 * <p>
 * The integer you will find in all the constructors is either the line where
 * the exception happened in the file, or -1 if it is unknown or N/A
 *
 * @author utybo
 *
 */
public class BSTException extends Exception
{
    private static final long serialVersionUID = 1L;

    private int where;

    public BSTException(final int where)
    {
        super();
        this.where = where;
    }

    public BSTException(final int where, final String message, final Throwable cause)
    {
        super(message, cause);
        this.where = where;
    }

    public BSTException(final int where, final String message)
    {
        super(message);
        this.where = where;
    }

    public BSTException(final int where, final Throwable cause)
    {
        super(cause);
        this.where = where;
    }

    public int getWhere()
    {
        return where;
    }

    public void setWhere(final int where)
    {
        this.where = where;
    }

}
