package utybo.branchingstorytree.swing.visuals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.swing.OpenBST;
import utybo.branchingstorytree.swing.utils.Lang;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog
{
    @SuppressWarnings("unchecked")
    public AboutDialog(OpenBST parent)
    {
        super(parent);
        setTitle(Lang.get("about.title"));
        setModalityType(ModalityType.APPLICATION_MODAL);

        JPanel banner = new JPanel(new FlowLayout(FlowLayout.CENTER));
        banner.setBackground(OpenBST.OPENBST_BLUE);
        JLabel lblOpenbst = new JLabel(new ImageIcon(OpenBST.bigLogoWhite));
        lblOpenbst.setText("<html><font size=32>" + Lang.get("title"));
        lblOpenbst.setForeground(Color.WHITE);
        banner.add(lblOpenbst, "flowx,cell 0 0,alignx center");
        getContentPane().add(banner, BorderLayout.NORTH);

        JPanel pan = new JPanel();
        pan.setLayout(new MigLayout("", "[grow]", "[][][grow]"));
        getContentPane().add(pan, BorderLayout.CENTER);

        JLabel lblWebsite = new JLabel("https://utybo.github.io/BST/");
        Font f = lblWebsite.getFont();
        @SuppressWarnings("rawtypes")
        Map attrs = f.getAttributes();
        attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        lblWebsite.setFont(f.deriveFont(attrs));
        lblWebsite.setForeground(OpenBST.OPENBST_BLUE);
        lblWebsite.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblWebsite.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if(Desktop.isDesktopSupported())
                    try
                    {
                        Desktop.getDesktop().browse(new URL("https://utybo.github.io/BST/").toURI());
                    }
                    catch(IOException | URISyntaxException e1)
                    {
                        e1.printStackTrace();
                    }
            }
        });
        pan.add(lblWebsite, "cell 0 0,alignx center");

        JLabel lblVersion = new JLabel(Lang.get("about.version").replace("$v", OpenBST.version));
        pan.add(lblVersion, "flowy,cell 0 1");

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new LineBorder(pan.getBackground().darker(), 1, true));
        pan.add(scrollPane, "cell 0 2,grow");

        JTextArea textArea = new JTextArea();
        textArea.setMargin(new Insets(5, 5, 5, 5));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Monospace", Font.PLAIN, 11));
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/about.txt"), "UTF-8"));
            String s = "";
            String line;
            while((line = br.readLine()) != null)
            {
                s += line + "\n";
            }
            textArea.setText(s);
        }
        catch(IOException ex)
        {
            OpenBST.LOG.warn("Loading about information failed", ex);
        }
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
        scrollPane.setViewportView(textArea);

        JLabel lblTranslatedBy = new JLabel(Lang.get("author"));
        pan.add(lblTranslatedBy, "cell 0 1");

        setSize(450, 300);
        setLocationRelativeTo(parent);
    }

}
