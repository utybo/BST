/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.swing.JScrollablePanel.ScrollableSizeHint;

public class OpenBST extends JFrame
{
    public static final String VERSION = "0.3";
    private static final long serialVersionUID = 1L;

    private BranchingStoryTreeParser parser = new BranchingStoryTreeParser();
    private static OpenBST instance;

    public static Image ideaImage;
    public static Image blogImage;
    public static Image controllerImage;
    public static Image inLoveImage;
    public static Image activeDirectoryImage;
    public static Image openFolderImage;
    public static Image cancelImage, errorImage, aboutImage, renameImage;
    public static Image addonSearchImage, addonSearchMediumImage, closeImage, closeBigImage, jumpImage, jumpBigImage, exportImage;
    public static Image gearsImage, importImage, refreshImage, refreshBigImage, returnImage, returnBigImage;
    public static Image saveAsImage, synchronizeImage, synchronizeBigImage, undoImage, undoBigImage;
    private JTabbedPane container;

    public static void main(final String[] args)
    {
        log("OpenBST version " + VERSION + ", part of the BST project");
        log("[ INIT ]");
        log("Applying Look and Feel");
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            log("=> GTKLookAndFeel");
        }
        catch(final Exception e)
        {
            // Do not print as an exception is thrown in most cases
            try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                log("=> System LookAndFeel");
            }
            catch(final Exception e1)
            {
                e1.printStackTrace();
                log("=> No LookAndFeel compatible. Using default");
            }
        }

        instance = new OpenBST();
    }

    private File askForFile()
    {
        final FileDialog jfc = new FileDialog(instance);
        jfc.setLocationRelativeTo(instance);
        jfc.setTitle("Choose a Branching Story Tree file...");
        jfc.setVisible(true);
        if(jfc.getFile() != null)
        {
            log("=> File selected");
            File file = new File(jfc.getDirectory() + jfc.getFile());
            log("[ LAUNCHING ]");
            return file;
        }
        else
        {
            log("=> No file selected.");
            return null;
        }
    }

    public BranchingStory loadFile(File file, BSTClient client)
    {
        try
        {
            log("Parsing story");
            return parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"))), new Dictionnary(), client);
        }
        catch(final IOException e)
        {
            log("=! IOException caught");
            e.printStackTrace();
            JOptionPane.showMessageDialog(instance, "<html>There was an error during file loading. Please try again and make sure your file is correct.<p>(" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        catch(final BSTException e)
        {
            log("=! BSTException caught");
            e.printStackTrace();
            String s = "<html><b>-- BST Error --</b><p>";
            s += "Your file seems to have an error here :<p>";
            s += "Line : " + e.getWhere() + "<p>";
            if(e.getCause() != null)
            {
                s += "Cause : " + e.getCause().getClass().getSimpleName() + " : " + e.getCause().getMessage() + "<p>";
            }
            s += "Message : " + e.getMessage() + "<p>";
            s += "<b>-- BST Error --</b>";
            s += "<p><p>Do you wish to reload the file?";
            if(JOptionPane.showConfirmDialog(instance, s, "BST Error", JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                log("Reloading");
                return loadFile(file, client);
            }
            return null;
        }
        catch(final Exception e)
        {
            log("=! Random exception caught");
            e.printStackTrace();
            JOptionPane.showMessageDialog(instance, "OpenBST crashed upon opening your file. Your file may have a problem.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private StoryPanel addStory(BranchingStory story, File file, TabClient client)
    {
        log("Creating tab");
        StoryPanel sp = new StoryPanel(story, this, file, client);
        container.addTab(sp.getTitle(), null, sp, null);
        container.setSelectedIndex(container.getTabCount() - 1);
        if(!sp.postCreation())
        {
            container.removeTabAt(container.getTabCount() - 1);
            return null;
        }
        else
        {
            return sp;
        }
    }

    public OpenBST()
    {
        setTitle("OpenBST " + VERSION);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try
        {
            log("=> Loading icons");
            activeDirectoryImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Active Directory.png"));
            setIconImage(activeDirectoryImage);
            blogImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Blog.png"));
            controllerImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Controller.png"));
            ideaImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Idea.png"));
            inLoveImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/In Love.png"));
            openFolderImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Open Folder.png"));
            cancelImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Cancel.png"));
            errorImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Error.png"));
            aboutImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/About.png"));
            renameImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Rename.png"));

            addonSearchImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Addon Search.png"));
            addonSearchMediumImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Addon Search Medium.png"));
            closeImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Close.png"));
            closeBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Close Big.png"));
            jumpImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Jump.png"));
            jumpBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Jump Big.png"));
            exportImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Export.png"));
            gearsImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Gears.png"));
            importImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Import.png"));
            refreshImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Refresh.png"));
            refreshBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Refresh Big.png"));
            returnImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Return.png"));
            returnBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Return Big.png"));
            saveAsImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Save as.png"));
            synchronizeImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Synchronize.png"));
            synchronizeBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Synchronize Big.png"));
            undoImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Undo.png"));
            undoBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Undo Big.png"));

            // Note : this does not work with GTKLookAndFeel
            UIManager.put("OptionPane.errorIcon", new ImageIcon(cancelImage));
            UIManager.put("OptionPane.informationIcon", new ImageIcon(aboutImage));
            UIManager.put("OptionPane.questionIcon", new ImageIcon(renameImage));
            UIManager.put("OptionPane.warningIcon", new ImageIcon(errorImage));
        }
        catch(final IOException e1)
        {
            log("=! IOException caught when loading icon");
            e1.printStackTrace();
        }
        getContentPane().setLayout(new BorderLayout());
        container = new JTabbedPane();
        getContentPane().add(container, BorderLayout.CENTER);

        JScrollablePanel welcomePanel = new JScrollablePanel();
        welcomePanel.setScrollableHeight(ScrollableSizeHint.STRETCH);
        welcomePanel.setScrollableWidth(ScrollableSizeHint.FIT);
        welcomePanel.setLayout(new MigLayout("", "[grow,center]", "[][][][][][][][][]"));
        container.add(new JScrollPane(welcomePanel));
        container.setTitleAt(0, "Welcome");

        JLabel lblOpenbst = new JLabel("<html><font size=32>OpenBST");
        lblOpenbst.setIcon(new ImageIcon(activeDirectoryImage));
        welcomePanel.add(lblOpenbst, "cell 0 0");

        JLabel lblWelcomeToOpenbst = new JLabel("Welcome to OpenBST. To get started, please open a file using the button below.");
        welcomePanel.add(lblWelcomeToOpenbst, "cell 0 1");

        JButton btnOpenAFile = new JButton("Open a file...");
        btnOpenAFile.setIcon(new ImageIcon(openFolderImage));
        btnOpenAFile.addActionListener(e ->
        {
            File f = askForFile();
            if(f != null)
            {
                TabClient client = new TabClient(instance);
                BranchingStory bs = loadFile(f, client);
                if(bs != null)
                {
                    addStory(bs, f, client);
                }
            }
        });
        welcomePanel.add(btnOpenAFile, "cell 0 2");

        JSeparator separator = new JSeparator();
        welcomePanel.add(separator, "cell 0 3,growx");

        JLabel lblwhatIsBst = new JLabel("What is BST?");
        lblwhatIsBst.setFont(lblwhatIsBst.getFont().deriveFont(28F));
        welcomePanel.add(lblwhatIsBst, "cell 0 4");

        JLabel lblbstIsA = new JLabel("<html>BST is a simple language that allows you to easily create a branching story.");
        welcomePanel.add(lblbstIsA, "cell 0 5,alignx center,growy");

        JLabel lblimagineItcreate = new JLabel("<html><body style='width: 100px; text-align: center;'><h1>Imagine it\n<p style='text-align: justify'>Create your own branching story inside your head, think about all the details and how to make it an unbelievable experience.");
        lblimagineItcreate.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblimagineItcreate.setHorizontalTextPosition(SwingConstants.CENTER);
        lblimagineItcreate.setIcon(new ImageIcon(ideaImage));
        welcomePanel.add(lblimagineItcreate, "flowx,cell 0 6,alignx center,aligny top");

        welcomePanel.add(Box.createHorizontalStrut(20), "cell 0 6");

        JLabel lblwriteItwriteSaid = new JLabel("<html><body style='width: 100px; text-align: center;'><h1>Write it\n<p style='text-align: justify'>Write it down, make it real. With BST's syntax, creating your branching story has never been easier! You can even make it even more dynamic with some scripting!");
        lblwriteItwriteSaid.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblwriteItwriteSaid.setHorizontalTextPosition(SwingConstants.CENTER);
        lblwriteItwriteSaid.setIcon(new ImageIcon(blogImage));
        welcomePanel.add(lblwriteItwriteSaid, "cell 0 6,aligny top");

        welcomePanel.add(Box.createHorizontalStrut(20), "cell 0 6");

        JLabel lblPlayIt = new JLabel("<html><body style='width: 100px; text-align: center;'><h1>Play it\n<p style='text-align: justify'>After some hard work, enjoy your new, fun story and just do cool stuff with it! Or even play some little text based game that use the scripting system in BST!");
        lblPlayIt.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblPlayIt.setHorizontalTextPosition(SwingConstants.CENTER);
        lblPlayIt.setIcon(new ImageIcon(controllerImage));
        welcomePanel.add(lblPlayIt, "cell 0 6,aligny top");

        welcomePanel.add(Box.createHorizontalStrut(20), "cell 0 6");

        JLabel lblEnjoyIt = new JLabel("<html><body style='width: 100px; text-align: center;'><h1>Enjoy it\n<p style='text-align: justify'>Write what you love, love what you write! Here, your imagination is the limit; go wild, create everything you want and make it a magnificent branching story. BST FTW!");
        lblEnjoyIt.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblEnjoyIt.setHorizontalTextPosition(SwingConstants.CENTER);
        lblEnjoyIt.setIcon(new ImageIcon(inLoveImage));
        welcomePanel.add(lblEnjoyIt, "cell 0 6");

        JLabel lblIconsByIconscom = new JLabel("Icons by icons8.com, go check them out, they make awesome icons!");
        lblIconsByIconscom.setEnabled(false);
        welcomePanel.add(lblIconsByIconscom, "cell 0 8,alignx left");

        // TODO Add components 
        setSize(830, 480);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void log(String message)
    {
        // TODO Add a better logging system
        System.out.println(message);
    }

    public void removeStory(StoryPanel storyPanel)
    {
        container.remove(storyPanel);
    }

}
