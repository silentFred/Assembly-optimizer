package org.fvs;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * FastQReader
 *
 * @author Pierre Lindenbaum
 * @modified by Frederick van Staden
 */

public class FastQReader {

    private BufferedReader in;
    private String prev_line = null;
    private String name = null;
    private StringBuilder sequence = new StringBuilder();
    private StringBuilder quality = new StringBuilder();
    private long nLine = 0L;

    public FastQReader(BufferedReader in)
    {
        this.in = in;       
    }

    public String toFasQ()
    {
        return getName() + "\n" + getSequence() + "\n+\n" + getQuality();
    }

    private String _readLine() throws IOException
    {
        if (prev_line != null)
        {
            String s = prev_line;
            prev_line = null;
            return s;
        }
        String s;
        while ((s = in.readLine()) != null)
        {
            ++nLine;
            if (s.trim().isEmpty())
            {
                continue;
            }
            return s;
        }
        return null;
    }

    public boolean next() throws IOException
    {
        name = null;
        sequence.setLength(0);
        quality.setLength(0);
        String line = _readLine();
        if (line == null)
        {
            return false;
        }
        if (!line.startsWith("@"))
        {
            throw new IOException("expected a line starting with @ but found " + line);
        }
        name = line.substring(1).trim();
        while (true)
        {
            line = _readLine();
            if (line == null)
            {
                throw new IOException("bad sequence for @" + name);
            }
            if (line.startsWith("+"))
            {
                break;
            }
            sequence.append(line);
        }
        while (true)
        {
            line = _readLine();
            if (line == null) //last line ?
            {
                if (quality.length() != sequence.length())
                {
                    throw new IOException("bad quality for @" + name + " next line is null and sequence:\n"
                            + sequence + "(" + sequence.length() + ") and quality:\n" + quality + "(" + quality.length() + ")");
                }
                break;
            }
            if (line.startsWith("@")
                    && quality.length() == sequence.length()) //quality can start with '@'
            {
                this.prev_line = line;
                break;
            }
            quality.append(line);
        }
        if (quality.length() != sequence.length())
        {
            throw new IOException(
                    "length(quality)!=length(dna) for @" + name + "\nseq:" + sequence + "(" + sequence.length() + ")\nqual:" + quality + "(" + quality.length() + ")");
        }
        return true;
    }

    public String getName()
    {
        return name;
    }

    public StringBuilder getSequence()
    {
        return sequence;
    }

    public StringBuilder getQuality()
    {
        return quality;
    }

    public long getLine()
    {
        return nLine;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj == this;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.sequence != null ? this.sequence.hashCode() : 0);
        hash = 23 * hash + (this.quality != null ? this.quality.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        return getClass().getName() + ":" + getLine();
    }

    public void close()
    {
        try
        {
            in.close();
        } catch (IOException ex)
        {
            Logger.getLogger(FastQReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}