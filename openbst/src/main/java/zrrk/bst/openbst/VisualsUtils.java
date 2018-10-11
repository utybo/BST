/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst;

import static zrrk.bst.openbst.OpenBST.LOG;

import java.awt.Desktop;
import java.awt.FileDialog;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javafx.application.Platform;

public class VisualsUtils
{
    private VisualsUtils()
    {}

    public static void invokeSwingAndWait(Runnable r)
    {
        try
        {
            SwingUtilities.invokeAndWait(r);
        }
        catch(InvocationTargetException | InterruptedException e)
        {
            OpenBST.LOG.warn("Swing invocation failed", e);
        }
    }
    
    public static void invokeJfxAndWait(Runnable runnable)
    {
        if(Platform.isFxApplicationThread())
        {
            Platform.runLater(runnable);
        }
        else
        {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() ->
            {
                try
                {
                    runnable.run();
                }
                finally
                {
                    latch.countDown();
                }
            });
            try
            {
                latch.await();
            }
            catch(InterruptedException e)
            {
                OpenBST.LOG.error(e);
            }
        }

    }


    /**
     * Open a prompt asking for a BST File
     *
     * @return The file selected, or null if none was chosen/the dialog was
     *         closed
     */
    public static File askForFile(JFrame frame, String title)
    {
        final FileDialog jfc = new FileDialog(frame);
        jfc.setTitle(title);
        jfc.setLocationRelativeTo(frame);
        jfc.setVisible(true);
        if(jfc.getFile() != null)
        {
            OpenBST.LOG.trace("File selected");
            final File file = new File(jfc.getDirectory() + jfc.getFile());
            return file;
        }
        else
        {
            OpenBST.LOG.trace("No file selected.");
            return null;
        }
    }

    public static void browse(String url)
    {
        try
        {
            Desktop.getDesktop().browse(new URL(url).toURI());
        }
        catch(Exception e1)
        {
            LOG.error("Exception during link opening", e1);
        }
    }

}
