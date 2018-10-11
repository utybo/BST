/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.test;

import java.io.IOException;

import org.junit.Test;

import zrrk.bst.bstjava.api.BSTException;

public class NNDTesting
{
    @Test
    public void testNND()
            throws InstantiationException, IllegalAccessException, IOException, BSTException
    {
        ActionTesting.testFile("nnd.bst", null);
    }
}
