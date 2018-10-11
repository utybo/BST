/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.test.utils;

import java.io.InputStream;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.htb.HTBHandler;

public class JSETestClient implements BSTClient
{
    @Override
    public String askInput(final String message)
    {
        return "";
    }

    @Override
    public void exit()
    {}

    @Override
    public HTBHandler getHTBHandler()
    {
        return new HTBHandler()
        {

            @Override
            public void load(InputStream in, String name) throws BSTException
            {}

            @Override
            public boolean requestJSAccess()
            {
                return true;
            }

            @Override
            public boolean requestHrefAccess()
            {
                return true;
            }

            @Override
            public boolean hasResource(String resource)
            {
                return false;
            }

            @Override
            public String getAsString(String resource)
            {
                return null;
            }

            @Override
            public String getAsBase64(String resource)
            {
                return null;
            }
        };
    }

}
