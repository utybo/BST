/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import zrrk.bst.openbst.OpenBST;

/**
 * Adapted from MinkJ -- Originally licensed under the MIT license
 *
 * @author utybo
 *
 */
public class Lang
{
    private static transient Map<Locale, Map<String, String>> map = new HashMap<>();
    private static Locale defaultLanguage = Locale.ENGLISH;
    private static Locale selectedLanguage = new Locale(System.getProperty("user.language"));

    private Lang()
    {}

    public static synchronized void setLocaleMap(final Map<String, String> translations,
            final Locale locale)
    {
        map.put(locale, translations);
    }

    public static synchronized void mergeMapWithLocaleMap(final Map<String, String> toMerge,
            final Locale locale)
    {
        map.get(locale).putAll(toMerge);
    }

    public static Map<String, String> getLocaleMap(final Locale locale)
    {
        return map.get(locale);
    }

    public static synchronized void setDefaultLanguage(final Locale locale)
    {
        defaultLanguage = locale;
    }

    public static Locale getDefaultLanguage()
    {
        return defaultLanguage;
    }

    public static void addTranslation(final String key, final String translation)
    {
        addTranslation(selectedLanguage, key, translation);
    }

    public static void removeTranslation(final String key)
    {
        removeTranslation(selectedLanguage, key);
    }

    public static void addTranslation(final Locale locale, final String key,
            final String translation)
    {
        if(!map.containsKey(locale))
        {
            map.put(locale, new HashMap<String, String>());
        }
        map.get(locale).put(key, translation);

    }

    public static void removeTranslation(final Locale locale, final String key)
    {
        if(map.containsKey(locale) && map.get(locale).containsKey(key))
        {
            map.get(locale).remove(key);
        }

    }

    public static synchronized void loadTranslationsFromFile(final Locale locale, final File file)
            throws UnrespectedModelException, FileNotFoundException, IOException
    {
        if(map.get(locale) == null)
        {
            map.put(locale, new HashMap<String, String>());
        }

        loadTranslationFromBufferedReader(
                new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)),
                locale, file.getName());

    }

    public static synchronized void loadTranslationsFromFile(final Locale locale,
            final InputStream input)
            throws UnrespectedModelException, FileNotFoundException, IOException
    {
        if(map.get(locale) == null)
        {
            map.put(locale, new HashMap<String, String>());
        }

        loadTranslationFromBufferedReader(
                new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8)), locale,
                input.toString());
    }

    private synchronized static void loadTranslationFromBufferedReader(final BufferedReader br,
            final Locale locale, final String fileName)
            throws IOException, UnrespectedModelException
    {
        try
        {
            String line;
            while((line = br.readLine()) != null)
            {
                if(!(line.startsWith("#") || line.isEmpty()))
                {
                    final String[] translation = line.split("=");
                    if(translation.length < 2)
                    {
                        log("Errrored String : " + line + ". Here is the index :", true);
                        for(int i = 0; i < translation.length; i++)
                        {
                            log(translation[i] + "          @ index " + i, true);
                        }
                        continue;
                    }
                    if(map.get(locale).containsKey(translation[0]))
                    {
                        log("WARNING : File " + fileName + " overwrites a translation @ "
                                + translation[0], true);
                    }
                    addTranslation(locale, translation[0], line.substring(line.indexOf("=") + 1));
                }
            }
        }
        finally
        {
            br.close();
        }
        log("Successfully read file : " + fileName, false);

    }

    public static synchronized void setSelectedLanguage(final Locale locale)
    {
        selectedLanguage = locale;

    }

    public static Locale getSelectedLanguage()
    {
        return selectedLanguage;
    }

    public static String get(final String key)
    {
        return get(key, selectedLanguage);
    }

    public static String get(final String key, final Locale locale)
    {
        return isTranslated(key, locale) ? map.get(locale).get(key)
                : isTranslated(key, defaultLanguage) ? map.get(defaultLanguage).get(key) : key;
    }

    public static boolean isTranslated(final String key, final Locale locale)
    {
        boolean b = map.containsKey(locale) && map.get(locale).containsKey(key);
        if(!b)
        {
            OpenBST.LOG
                    .warn("Missing translation : '" + key + "' in locale " + locale.getLanguage());
        }
        return b;
    }

    public static Map<Locale, Map<String, String>> getMap()
    {
        return map;
    }

    private static void log(final String str, final boolean error)
    {
        if(error)
        {
            OpenBST.LOG.warn(str);
        }
        if(!error)
        {
            OpenBST.LOG.info(str);
        }
    }

    public static class UnrespectedModelException extends Exception
    {
        private static final long serialVersionUID = -1539821762590369248L;
        private File file;
        private final String line;

        public UnrespectedModelException(final File f, final String line)
        {
            this.line = line;
            file = f;
        }

        public UnrespectedModelException(final String line)
        {
            this.line = line;
        }

        @Override
        public String getMessage()
        {
            String message = "Unrespected model (" + line + ") on file";
            if(file != null)
            {
                message = message + file.getName();
            }
            return message;
        }
    }
}
