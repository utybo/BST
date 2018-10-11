/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.api;

/**
 * This exception is raised (and must be raised) whenever an unimplemented
 * experimental feature, method or function is used.
 * 
 * @author utybo
 *
 */
public class UnsupportedExperimentalException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public UnsupportedExperimentalException()
    {}

    public UnsupportedExperimentalException(String reason)
    {
        super(reason);
    }

    public UnsupportedExperimentalException(Throwable cause)
    {
        super(cause);
    }

    public UnsupportedExperimentalException(String reason, Throwable cause)
    {
        super(reason, cause);
    }
}
