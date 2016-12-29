/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.test;

import javax.script.ScriptEngine;

import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.jse.JSEHandler;

public class JSETestClient implements BSTClient, JSEHandler
{
    private ScriptEngine engine;

    @Override
    public ScriptEngine getEngine()
    {
        return engine;
    }

    @Override
    public void setEngine(ScriptEngine engine)
    {
        this.engine = engine;
    }

    @Override
    public String askInput(String message)
    {
        return "";
    }

    @Override
    public void exit()
    {}

    @Override
    public JSEHandler getJSEHandler()
    {
        return this;
    }
}
