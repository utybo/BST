package utybo.branchingstorytree.swing.utils;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

public class UndoUtils
{
    @SuppressWarnings("serial")
    public static void attachSimpleUndoManager(JTextArea jta)
    {
        UndoManager um = new UndoManager();
        jta.getDocument().addUndoableEditListener(um);

        jta.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke("control Z"), "doUndo");
        jta.getActionMap().put("doUndo", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(um.canUndo())
                    um.undo();
            }
        });

        jta.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke("control Y"), "doRedo");
        jta.getActionMap().put("doRedo", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(um.canRedo())
                    um.redo();
            }
        });
    }
}
