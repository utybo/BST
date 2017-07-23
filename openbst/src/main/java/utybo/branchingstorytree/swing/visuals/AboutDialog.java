/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.visuals;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import org.apache.commons.io.IOUtils;

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
        JLabel lblOpenbst = new JLabel(new ImageIcon(OpenBST.fullLogoWhite));
        lblOpenbst.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
                        OpenBST.LOG.warn("Exception when trying to open website", e1);
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

        try(InputStream in = getClass().getResourceAsStream("/utybo/branchingstorytree/swing/about.txt");)
        {
            textArea.setText(IOUtils.toString(in, StandardCharsets.UTF_8));
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
