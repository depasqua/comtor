/**
 * Comment Mentor: A Comment Quality Assessment Tool Copyright (C) 2013 The
 * College of New Jersey
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.comtor.cloudclients.netbeansclient;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "File", id = "org.comtor.cloudclients.netbeansclient.COMTORActionListener")
@ActionRegistration(displayName = "#CTL_COMTORActionListener", lazy=false)
@ActionReferences({@ActionReference(path = "Toolbars/File", position = 0)})
@Messages("CTL_COMTORActionListener=COMTOR")

/**
 * Somehow this class enables the registration of our NetBeans module GUI.
 * 
 * I do not pretend to understand the magic that this class makes happen.
 *
 * @author Michael E. Locasto
 */
public final class COMTORActionListener
        extends AbstractAction
        implements Presenter.Toolbar {

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Component getToolbarPresenter() {
        return new COMTORJPanel();
    }
}
