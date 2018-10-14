/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
module zrrk.bst.openbst {
    requires zrrk.bst.bstjava;

    requires java.desktop;
    requires java.scripting;
    requires jdk.xml.dom;

    requires javafx.swing;
    requires javafx.media;
    requires javafx.web;

    requires org.apache.commons.io;
    requires org.apache.logging.log4j;

    exports zrrk.bst.openbst;
    exports zrrk.bst.openbst.editor;
    exports zrrk.bst.openbst.ext;
    exports zrrk.bst.openbst.impl;
    exports zrrk.bst.openbst.utils;
    exports zrrk.bst.openbst.virtualfiles;
    exports zrrk.bst.openbst.visuals;
}
