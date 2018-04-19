/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class FontTF
{

    public static void main(String[] args) throws MalformedURLException, IOException
    {
        Scanner sc = new Scanner(System.in);
        System.out.print("Path to transform from => ");
        String s = sc.nextLine();
        System.out.print("Save to => ");
        File f = new File(sc.nextLine());
        f.createNewFile();
        System.out.println("Downloading...");
        String string = IOUtils.toString(new FileInputStream(new File(s)), StandardCharsets.UTF_8);
        System.out.println(string);
        System.out.println("Transforming");
        Pattern p = Pattern.compile("local\\(.*?\\), local\\(.*?\\), url\\((http.+?)\\)", Pattern.MULTILINE);
        Matcher m = p.matcher(string);
        while(m.find())
        {
            String toReplace = m.group();
            String url = m.group(1);
            System.out.println("Downloading " + url);
            byte[] raw = IOUtils.toByteArray(new URL(url).openStream());
            System.out.println("Converting");
            String b64 = Base64.getEncoder().encodeToString(raw);
            string = string.replace(toReplace, "url(data:font/woff2;charset=utf-8;base64," + b64 + ")");
            System.out.println(string);
            m.reset(string);
        }
        System.out.println("Writing");
        FileUtils.write(f, string, StandardCharsets.UTF_8);
        System.out.println("Done");
        sc.close();
    }

}
