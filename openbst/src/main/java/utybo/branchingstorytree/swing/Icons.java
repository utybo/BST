/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import static utybo.branchingstorytree.swing.OpenBST.LOG;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;

public class Icons
{
    private static String factor;
    private static HashMap<String, BufferedImage> images = new HashMap<>();
    private static List<BufferedImage> backgroundImages;

    public static void loadScalingFactor()
    {
        int i = (int)(Math.floor(SubstanceSizeUtils.getPointsToPixelsRatio() * 100) / 1.33);
        if(i == 100)
            factor = "";
        else if(i <= 125)
            factor = "1_25";
        else if(i <= 150)
            factor = "1_5";
        else
            factor = "2";
        LOG.info("Scaling factor : " + (factor.isEmpty() ? "1x" : factor.replace('_', '.') + "x"));
    }

    public static void load()
    {

        // 16px icons names
        String[] arr = new String[] {"About", "Add Property", "Add Subnode", "Audio",
                "Camera Addon Identification", "Cancel", "Change Theme", "Circled Play", "Code",
                "Color Wheel", "Copy To Clipboard", "Easy to Find", "Edit Property", "Export",
                "External Link", "Eye", "Gears", "Import", "Invisible", "Mute", "Open Archive",
                "Open", "Picture", "Refresh", "Delete Subnode", "Return", "Save", "Save As",
                "Synchronize", "Undo"};
        for(String s : arr)
        {
            images.put(s + 16, loadImage(s, 16));
        }

        // 16c (custom icons not available through icons8 collections)
        int[] sizes = {16, 20, 24, 32};
        images.put("LinkY16", loadImage("16c", "LinkY" + applyScaleValue(sizes)));
        images.put("LinkN16", loadImage("16c", "LinkN" + applyScaleValue(sizes)));

        images.put("JSY16", loadImage("16c", "JSY" + applyScaleValue(sizes)));
        images.put("JSN16", loadImage("16c", "JSN" + applyScaleValue(sizes)));

        images.put("VirtualNode16", loadImage("16c", "VirtualNode" + applyScaleValue(sizes)));
        images.put("LogicalNode16", loadImage("16c", "LogicalNode" + applyScaleValue(sizes)));
        images.put("TextNode16", loadImage("16c", "TextNode" + applyScaleValue(sizes)));

        images.put("PreviewText16", loadImage("16c", "PreviewText" + applyScaleValue(sizes)));
        images.put("LangCheck16", loadImage("16c", "LangCheck" + applyScaleValue(sizes)));

        // 16px logos
        images.put("Logo16", loadImage("logos/Logo" + applyScaleValue(sizes)));
        images.put("LogoWhite16", loadImage("logos/LogoWhite" + applyScaleValue(sizes)));

        // 32px icons
        arr = new String[] {"Attention", "Experiment"};
        for(String s : arr)
        {
            images.put(s + 32, loadImage(s, 32));
        }

        // 32px based msgr icons
        sizes = new int[] {32, 40, 48, 64};
        images.put("err32", loadImage("msgr", "err" + applyScaleValue(sizes)));
        images.put("ok32", loadImage("msgr", "ok" + applyScaleValue(sizes)));
        images.put("quest32", loadImage("msgr", "quest" + applyScaleValue(sizes)));
        images.put("warn32", loadImage("msgr", "warn" + applyScaleValue(sizes)));

        // 40px icons
        arr = new String[] {"Camera Addon Identification", "Cancel", "Code", "Easy to Find",
                "Edit Property", "Open", "Refresh", "Return", "Synchronize", "Undo"};
        for(String s : arr)
        {
            images.put(s + 40, loadImage(s, 40));
        }

        // 48px icons
        arr = new String[] {"About", "Cancel", "Discord", "Error", "Installing Updates", "Rename"};
        for(String s : arr)
        {
            images.put(s + 48, loadImage(s, 48));
        }

        // 48c
        sizes = new int[] {48, 60, 72, 96};
        images.put("Experiment48", loadImage("48c", "Experiment" + applyScaleValue(sizes)));
        images.put("JSAlert48", loadImage("48c", "JSAlert" + applyScaleValue(sizes)));
        images.put("AAlert48", loadImage("48c", "AAlert" + applyScaleValue(sizes)));
        images.put("LanguageError48", loadImage("48c", "LanguageError" + applyScaleValue(sizes)));

        // 48px logos
        sizes = new int[] {48, 60, 72, 96};
        images.put("FullLogo48", loadImage("logos/FullLogo" + applyScaleValue(sizes)));
        images.put("FullLogoWhite48", loadImage("logos/FullLogoWhite" + applyScaleValue(sizes)));
        images.put("Logo48", loadImage("logos/Logo" + applyScaleValue(sizes)));
        images.put("LogoWhite48", loadImage("logos/LogoWhite" + applyScaleValue(sizes)));

    }

    public static void loadBackgrounds()
    {
        // Background images
        backgroundImages = Arrays.asList(loadImages("images/bg$.jpg", 10));
    }

    private static BufferedImage[] loadImages(String string, int length)
    {
        BufferedImage[] array = new BufferedImage[length];
        for(int i = 0; i < length; i++)
        {
            array[i] = loadXZImage(string.replace("$", "" + i));
        }
        return array;
    }

    private static BufferedImage loadXZImage(String path)
    {
        try
        {
            return ImageIO.read(new XZCompressorInputStream(OpenBST.class
                    .getResourceAsStream("/utybo/branchingstorytree/swing/" + path + ".xz")));
        }
        catch(Exception e)
        {
            LOG.warn("Failed to load image at path " + path, e);
            return null;
        }
    }

    public static int applyScaleValue(int[] scaledSizes)
    {
        switch(factor)
        {
        case "":
            return scaledSizes[0];
        case "1_25":
            return scaledSizes[1];
        case "1_5":
            return scaledSizes[2];
        case "2":
            return scaledSizes[3];
        default:
            LOG.error("Could not determine scaled size, with scale " + factor + " and scaled sizes "
                    + scaledSizes.toString());
            return -1;
        }
    }

    private static BufferedImage loadImage(String dirname, String filename)
    {
        try
        {
            return ImageIO.read(OpenBST.class.getResourceAsStream(
                    "/utybo/branchingstorytree/swing/icons/" + dirname + "/" + filename + ".png"));
        }
        catch(Exception e)
        {
            LOG.warn("Failed to load image at path " + dirname + "/" + filename, e);
            return null;
        }
    }

    private static BufferedImage loadImage(String fullPath)
    {
        try
        {
            return ImageIO.read(OpenBST.class
                    .getResourceAsStream("/utybo/branchingstorytree/swing/" + fullPath + ".png"));
        }
        catch(Exception e)
        {
            LOG.warn("Failed to load image at path " + fullPath, e);
            return null;
        }
    }

    public static BufferedImage getImage(String name, int size)
    {
        BufferedImage img = images.get(name + size);
        if(img == null)
            LOG.error("Unknown image : " + name + " at original size " + size);
        return img;
    }

    public static BufferedImage getBackground(int id)
    {
        return backgroundImages.get(id);
    }

    public static BufferedImage getRandomBackground()
    {
        return backgroundImages.get(new Random().nextInt(backgroundImages.size()));
    }

    public static List<BufferedImage> getAllBackgrounds()
    {
        return Collections.unmodifiableList(backgroundImages);
    }

    private static BufferedImage loadImage(String name, int originalSize)
    {
        try
        {
            return ImageIO
                    .read(OpenBST.class.getResourceAsStream("/utybo/branchingstorytree/swing/icons/"
                            + scaled(originalSize) + "/" + name + ".png"));
        }
        catch(Exception e)
        {
            LOG.warn("Failed to load image at path " + name, e);
            return null;
        }
    }

    private static String scaled(int originalSize)
    {
        return originalSize + (!factor.isEmpty() ? "x" + factor : "");
    }

    public static float getScale()
    {
        switch(factor)
        {
        case "":
            return 1F;
        case "1_25":
            return 1.25F;
        case "1_5":
            return 1.5F;
        case "2":
            return 2F;
        default:
            LOG.error("Unknown scale");
            return -1;
        }
    }

}
