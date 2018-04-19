/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class OutputStreamToOutputAndPrint extends OutputStream
{
    public OutputStream toStream;
    public PrintStream toPrint;

    public OutputStreamToOutputAndPrint(OutputStream toStream, PrintStream toPrint)
    {
        this.toStream = toStream;
        this.toPrint = toPrint;
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        toStream.write(b);
        toPrint.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        toStream.write(b, off, len);
        toPrint.write(b, off, len);
    }

    @Override
    public void flush() throws IOException
    {
        toStream.flush();
        toPrint.flush();
    }

    @Override
    public void close() throws IOException
    {
        toStream.close();
        toPrint.close();
    }

    @Override
    public void write(int b) throws IOException
    {
        toStream.write(b);
        toPrint.write(b);
    }

}
