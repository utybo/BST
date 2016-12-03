/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Adapted from MinkJ -- Originally licensed under the MIT license
 * 
 * @author utybo
 *
 */
public class Lang
{
    private static transient Map<Locale, Map<String, String>> map = new HashMap<Locale, Map<String, String>>();
    private static Locale defaultLanguage = Locale.ENGLISH;
    private static Locale selectedLanguage = new Locale(System.getProperty("user.language"));
    private static boolean muted = false;

    private Lang()
    {}

    public static synchronized void setLocaleMap(Map<String, String> translations, Locale locale)
    {
        map.put(locale, translations);
    }

    public static synchronized void mergeMapWithLocaleMap(Map<String, String> toMerge, Locale locale)
    {
        for(String s : toMerge.keySet())
            map.get(locale).put(s, toMerge.get(s));
    }

    public static Map<String, String> getLocaleMap(Locale locale)
    {
        return map.get(locale);
    }

    public static synchronized void setDefaultLanguage(Locale locale)
    {
        defaultLanguage = locale;
    }

    public static Locale getDefaultLanguage()
    {
        return defaultLanguage;
    }

    public static void addTranslation(String key, String translation)
    {
        addTranslation(selectedLanguage, key, translation);
    }

    public static void removeTranslation(String key)
    {
        removeTranslation(selectedLanguage, key);
    }

    public static void addTranslation(Locale locale, String key, String translation)
    {
        if(!(map.containsKey(locale)))
            map.put(locale, new HashMap<String, String>());
        map.get(locale).put(key, translation);

    }

    public static void removeTranslation(Locale locale, String key)
    {
        if(map.containsKey(locale) && map.get(locale).containsKey(key))
            map.get(locale).remove(key);

    }

    public static synchronized void loadTranslationsFromFile(Locale locale, File file) throws UnrespectedModelException, NullPointerException, FileNotFoundException, IOException
    {
        if(locale == null || file == null)
            throw new NullPointerException();

        if(map.get(locale) == null)
            map.put(locale, new HashMap<String, String>());

        loadTranslationFromBufferedReader(new BufferedReader(new FileReader(file)), locale, file.getName());

    }

    public static synchronized void loadTranslationsFromFile(Locale locale, InputStream input) throws UnrespectedModelException, NullPointerException, FileNotFoundException, IOException
    {
        if(locale == null || input == null)
            throw new NullPointerException();

        if(map.get(locale) == null)
            map.put(locale, new HashMap<String, String>());

        loadTranslationFromBufferedReader(new BufferedReader(new InputStreamReader(input, "UTF-8")), locale, input.toString());

    }

    private synchronized static void loadTranslationFromBufferedReader(BufferedReader br, Locale locale, String fileName) throws IOException, UnrespectedModelException
    {
        if(br == null)
            throw new NullPointerException();
        assert br != null;
        try
        {
            String line;
            while((line = br.readLine()) != null)
            {
                if(!(line.startsWith("#") || line.isEmpty()))
                {
                    String[] translation = line.split("=");
                    if(!(translation.length > 1))
                    {
                        log("Errrored String : " + line + ". Here is the index :", true);
                        for(int i = 0; i < translation.length; i++)
                            log(translation[i] + "          @ index " + i, true);
                        throw new UnrespectedModelException(line);
                    }
                    if(map.get(locale).containsKey(translation[0]))
                        log("WARNING : File " + fileName + " overwrites a translation @ " + translation[0], true);
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

    public static synchronized void setSelectedLanguage(Locale locale)
    {
        selectedLanguage = locale;

    }

    public static Locale getSelectedLanguage()
    {
        return selectedLanguage;
    }

    public static String get(String key)
    {
        return get(key, selectedLanguage);
    }

    public static String get(String key, Locale locale)
    {
        return isTranslated(key, locale) ? map.get(locale).get(key) : isTranslated(key, defaultLanguage) ? map.get(defaultLanguage).get(key) : key;
    }

    public static boolean isTranslated(String key, Locale locale)
    {
        return map.containsKey(locale) && map.get(locale).containsKey(key);
    }

    public static void mute()
    {
        muted = true;

    }

    public static void unmute()
    {
        muted = false;

    }

    private static void log(String str, boolean error)
    {
        if(!muted)
        {
            if(error)
                System.err.println(str);
            if(!error)
                System.out.println(str);
        }
    }

    public static class UnrespectedModelException extends Exception
    {
        private static final long serialVersionUID = -1539821762590369248L;
        private File file;
        private String line;

        public UnrespectedModelException(File f, String line)
        {
            this.line = line;
            file = f;
        }

        public UnrespectedModelException(String line)
        {
            this.line = line;
        }

        @Override
        public String getMessage()
        {
            String message = "Unrespected model (" + line + ") on file";
            if(file != null)
                message = message + file.getName();
            return message;
        }
    }
}
