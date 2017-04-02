/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.ssb;

import utybo.branchingstorytree.brm.BRMResourceConsumer;

/**
 * Handler for SSB related tasks (most of these are called from
 * {@link SSBAction})
 *
 * @author utybo
 *
 */
public interface SSBHandler extends BRMResourceConsumer
{
    /**
     * Play a sound denoted by the name
     *
     * @param name
     */
    public void play(String name);

    /**
     * Loop an ambient sound. There can only be one ambient sound at a time :
     * replace any currently playing ambient sound.
     *
     * @param name
     */
    public void ambient(String name);

    /**
     * Stop the current ambient sound.
     */
    public void stop();
}
