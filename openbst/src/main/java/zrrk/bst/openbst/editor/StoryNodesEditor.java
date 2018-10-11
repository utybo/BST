/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import org.pushingpixels.substance.api.renderer.SubstanceDefaultListCellRenderer;

import net.miginfocom.swing.MigLayout;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.story.LogicalNode;
import zrrk.bst.bstjava.api.story.StoryNode;
import zrrk.bst.bstjava.api.story.TextNode;
import zrrk.bst.bstjava.api.story.VirtualNode;
import zrrk.bst.openbst.Icons;
import zrrk.bst.openbst.Messagers;
import zrrk.bst.openbst.OpenBST;
import zrrk.bst.openbst.editor.StorySingleNodeEditor.Status;
import zrrk.bst.openbst.utils.AlphanumComparator;
import zrrk.bst.openbst.utils.Lang;
import zrrk.bst.openbst.utils.MarkupUtils;
import zrrk.bst.openbst.visuals.JScrollablePanel;
import zrrk.bst.openbst.visuals.JScrollablePanel.ScrollableSizeHint;

@SuppressWarnings("serial")
public class StoryNodesEditor extends JPanel implements EditorControl<Collection<StoryNode>>
{
    private ListListModel<StorySingleNodeEditor> list;
    private JList<StorySingleNodeEditor> jlist;
    private JPanel container;
    private JButton btnAddNode;

    public StoryNodesEditor()
    {
        setLayout(new MigLayout("", "[:33%:300px][grow 150]", "[grow][]"));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, "cell 0 0,grow");

        jlist = new JList<>();
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setCellRenderer(new SubstanceDefaultListCellRenderer()
        {

            @Override
            public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list,
                    Object o, int index, boolean isSelected, boolean cellHasFocus)
            {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, o, index,
                        isSelected, cellHasFocus);

                if(o instanceof StorySingleNodeEditor)
                {
                    if(((StorySingleNodeEditor)o).getStatus() == Status.ERROR)
                        label.setForeground(Color.RED.darker());
                    if(o instanceof StoryLogicalNodeEditor)
                        label.setIcon(new ImageIcon(Icons.getImage("LogicalNode", 16)));
                    else if(o instanceof StoryTextNodeEditor)
                        label.setIcon(new ImageIcon(Icons.getImage("TextNode", 16)));
                    else if(o instanceof StoryVirtualNodeEditor)
                        label.setIcon(new ImageIcon(Icons.getImage("VirtualNode", 16)));
                }

                return label;
            }

        });
        jlist.addListSelectionListener(e ->
        {
            if(jlist.getSelectedValue() instanceof JComponent)
            {
                container.removeAll();
                container.add(jlist.getSelectedValue());
                container.revalidate();
                container.repaint();
            }
        });

        JScrollablePanel pan = new JScrollablePanel(new BorderLayout(0, 0));
        jlist.addComponentListener(new ComponentAdapter()
        {

            @Override
            public void componentResized(ComponentEvent e)
            {
                scrollPane.revalidate();
                pan.revalidate();
                jlist.revalidate();

                revalidate();
                repaint();
            }

        });
        pan.setScrollableWidth(ScrollableSizeHint.FIT);
        pan.setScrollableHeight(ScrollableSizeHint.STRETCH);
        pan.add(jlist, BorderLayout.CENTER);
        scrollPane.setViewportView(pan);

        container = new JPanel();
        add(container, "cell 1 0,grow");
        container.setLayout(new BorderLayout(0, 0));

        container.add(new JPanel(), BorderLayout.CENTER); // TODO

        JPanel panel = new JPanel();
        add(panel, "cell 0 1 2 1,alignx leading,growy");

        btnAddNode = new JButton(Lang.get("editor.panel.add"),
                new ImageIcon(Icons.getImage("Add Subnode", 16)));
        btnAddNode.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                createMenu().show(btnAddNode, e.getX(), e.getY());
            }
        });
        panel.add(btnAddNode);

        JButton btnRemoveNode = new JButton(Lang.get("editor.panel.remove"),
                new ImageIcon(Icons.getImage("Delete Subnode", 16)));
        btnRemoveNode.addActionListener(e -> removeNode());
        panel.add(btnRemoveNode);
    }

    private JPopupMenu createMenu()
    {
        JPopupMenu createMenu = new JPopupMenu();
        createMenu.add(new AbstractAction(Lang.get("editor.panel.text"), //
                new ImageIcon(Icons.getImage("TextNode", 16)))
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                addNode(new StoryTextNodeEditor(StoryNodesEditor.this));
            }
        });
        createMenu.add(new AbstractAction(Lang.get("editor.panel.virtual"),
                new ImageIcon(Icons.getImage("VirtualNode", 16)))
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                addNode(new StoryVirtualNodeEditor(StoryNodesEditor.this));
            }
        });
        createMenu.add(new AbstractAction(Lang.get("editor.panel.logical"),
                new ImageIcon(Icons.getImage("LogicalNode", 16)))
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                addNode(new StoryLogicalNodeEditor(StoryNodesEditor.this));
            }
        });
        return createMenu;
    }

    private void removeNode()
    {
        StorySingleNodeEditor ssne = jlist.getSelectedValue();
        if(ssne == null)
        {
            Messagers.showMessage(OpenBST.getGUIInstance(), Lang.get("editor.panel.noselected"),
                    Messagers.TYPE_ERROR);
            return;
        }
        else
        {
            int i = Messagers.showConfirm(OpenBST.getGUIInstance(),
                    "<html>" + Lang.get("editor.panel.removeconfirm")
                            .replace("$i", ssne.getIdentifier())
                            .replace("$s", MarkupUtils.escapeHTML(ssne.getSummary())),
                    Messagers.OPTIONS_YES_NO);
            if(i == Messagers.OPTION_YES)
            {
                int index = list.indexOf(ssne);
                list.remove(ssne);
                if(index < list.size())
                    jlist.setSelectedIndex(index);
                else if(index - 1 < list.size())
                    jlist.setSelectedIndex(index - 1);
                else if(list.size() > 0)
                    jlist.setSelectedIndex(0);
                else
                    container.removeAll(); // TODO

            }
        }
    }

    protected void addNode(StorySingleNodeEditor node)
    {
        int id = -1;
        for(int i = 1; i < Integer.MAX_VALUE; i++)
        {
            boolean freeSpot = true;
            for(StorySingleNodeEditor ssne : list)
            {
                if(ssne.getRawIntegerId() == i)
                {
                    freeSpot = false;
                    break;
                }
            }
            if(freeSpot == true)
            {

                id = i;
                break;
            }
        }

        node.setInitialIntegerId(id);
        list.addSorted(node, (o1, o2) -> new AlphanumComparator().compare(o1.getIdentifier(),
                o2.getIdentifier()));
        jlist.setSelectedValue(node, true);
        jlist.revalidate();
        jlist.repaint();
    }

    @Override
    public void importFrom(Collection<StoryNode> from) throws BSTException
    {
        list = new ListListModel<>(new ArrayList<>());
        for(StoryNode sn : from)
        {
            if(sn instanceof TextNode)
            {
                StoryTextNodeEditor stne = new StoryTextNodeEditor(this);
                stne.importFrom((TextNode)sn);
                list.add(stne);
            }
            else if(sn instanceof VirtualNode)
            {
                StoryVirtualNodeEditor svne = new StoryVirtualNodeEditor(this);
                svne.importFrom((VirtualNode)sn);
                list.add(svne);
            }
            else if(sn instanceof LogicalNode)
            {
                StoryLogicalNodeEditor slne = new StoryLogicalNodeEditor(this);
                slne.importFrom((LogicalNode)sn);
                list.add(slne);
            }
        }
        list.sort((o1, o2) -> new AlphanumComparator().compare(o1.getIdentifier(),
                o2.getIdentifier()));

        jlist.setModel(list);

        checkAllAvailable();

        if(list.size() > 0)
            jlist.setSelectedIndex(0);
    }

    @Override
    public Collection<StoryNode> exportToObject()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String exportToString() throws BSTException
    {
        StringBuilder sb = new StringBuilder();
        for(StorySingleNodeEditor editor : list)
        {
            sb.append(((EditorControl<?>)editor).exportToString());
            sb.append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public void updateUI()
    {
        super.updateUI();
        if(list != null)
            for(StorySingleNodeEditor ssne : list)
                ssne.updateUI();
    }

    public void refreshList()
    {
        jlist.repaint();
    }

    public void checkAllAvailable()
    {
        ArrayList<StorySingleNodeEditor> seen = new ArrayList<>();
        // Because the algorithm used can mark errors twice we use a set to ensure
        // it only contains each errored element once
        LinkedHashSet<StorySingleNodeEditor> toMarkAsError = new LinkedHashSet<>();
        for(StorySingleNodeEditor ssne : list)
        {
            for(StorySingleNodeEditor other : seen)
                if(other.matchesId(ssne))
                {
                    toMarkAsError.add(ssne);
                    toMarkAsError.add(other);
                    break;
                }
            seen.add(ssne);
        }
        // Turn "seen" into the "everything is ok" list by removing all the errors
        seen.removeAll(toMarkAsError);
        seen.forEach(ssne ->
        {
            ssne.setStatus(Status.OK);
            ssne.getId().notifyOk();
        });
        toMarkAsError.forEach(ssne ->
        {
            ssne.setStatus(Status.ERROR);
            ssne.getId().notifyError();
        });
        jlist.repaint();
    }
}
