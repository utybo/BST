/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.uib;

import zrrk.bst.bstjava.api.BSTException;

/**
 * Self explanatory methods here for the BST module
 *
 * @author utybo
 *
 */
public interface UIBarHandler
{
    public void setLayout(String layoutIdentifier) throws BSTException;

    public void initialize() throws BSTException;

    public boolean isElementValueTypeInteger(String element) throws BSTException;

    public void setElementValue(String element, int value) throws BSTException;

    public void setElementValue(String element, String value) throws BSTException;

    public void setElementMax(String element, int max) throws BSTException;

    public void setElementMin(String element, int min) throws BSTException;

    public boolean supportsDynamicInteger(String element) throws BSTException;

    public void setUIBVisisble(boolean parseBoolean) throws BSTException;

    public boolean elementExists(String element);

    public void restoreState() throws BSTException;
}
