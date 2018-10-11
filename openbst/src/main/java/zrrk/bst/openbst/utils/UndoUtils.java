/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.utils;

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
