/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.brm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import utybo.branchingstorytree.api.BSTException;

/**
 * Subinterface for module handlers that use BRM for resource handling
 *
 * @author utybo
 *
 */
public interface BRMResourceConsumer
{
    @Deprecated
    public default void load(String pathToResource, String name) throws BSTException, IOException
    {
        load(new FileInputStream(new File(pathToResource)), name);
    }

    public default void load(File file, String name) throws BSTException, FileNotFoundException
    {
        load(new FileInputStream(file), name);
    }

    public void load(InputStream in, String name) throws BSTException;
}
