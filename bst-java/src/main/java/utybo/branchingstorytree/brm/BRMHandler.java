/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.brm;

import utybo.branchingstorytree.api.BSTException;

/**
 * Handler for the BRM module, required by many other modules
 *
 * @author utybo
 *
 */
public interface BRMHandler
{
    /**
     * Load the files in folder resources/<module name> and use the module
     * handlers' implementation of
     * {@link BRMResourceConsumer#load(String, String)}
     * <p>
     * The loading process should be called on file load. As such, this method is useless.
     *
     * @throws BSTException
     */
    @Deprecated
    public default void loadAuto() throws BSTException
    {}
}
