/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.api.story;

import java.util.HashMap;

/**
 * A Tag Holder is an object that can, well... hold tags.
 *
 * @author utybo
 *
 */
public class TagHolder
{
    private final HashMap<String, String> tags = new HashMap<>();

    public String getTag(final String tag)
    {
        return tags.get(tag);
    }

    public String getTagOrDefault(final String tag, final String defaultVal)
    {
        return tags.getOrDefault(tag, defaultVal);
    }

    public void putTag(final String tag, final String value)
    {
        tags.put(tag, value);
    }

    public HashMap<String, String> getTagMap()
    {
        return tags;
    }

    public boolean hasTag(final String tag)
    {
        return tags.containsKey(tag);
    }
}
