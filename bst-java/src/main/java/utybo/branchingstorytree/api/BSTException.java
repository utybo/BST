/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api;

import utybo.branchingstorytree.api.story.BranchingStory;

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

    private String source;
    private int where;

    public BSTException(int where, String message, BranchingStory source)
    {
        super(message);
        this.where = where;
        this.source = source.getTag("__sourcename");
    }

    public BSTException(final int where, final String message, String source)
    {
        super(message);
        this.where = where;
        this.source = source;
    }

    public BSTException(final int where, final String message, final Throwable cause, BranchingStory source)
    {
        super(message, cause);
        this.where = where;
        this.source = source.getTag("__sourcename");
    }

    public BSTException(final int where, final String message, final Throwable cause, String source)
    {
        super(message, cause);
        this.where = where;
        this.source = source;
    }
    
    public BSTException(final int where, final Throwable cause, BranchingStory story)
    {
        super(cause);
        this.where = where;
        this.source = story.getTag("__sourcename");
    }


    public BSTException(final int where, final Throwable cause, String source)
    {
        super(cause);
        this.where = where;
        this.source = source;
    }

    public String getSourceFile()
    {
        return source;
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
