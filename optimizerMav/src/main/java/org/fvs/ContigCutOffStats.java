package org.fvs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



/**
 *
 * @author Fred
 */
public class ContigCutOffStats {

    private FastQReader fastqReader;
    private int contigCount;
    private int nucleotideCount;
    private int nucleotideCountUnderCutOff;
    private int cutOff;

    ContigCutOffStats(String fileName, int _cutOff)
    {
        contigCount = 0;
        cutOff=_cutOff;
        nucleotideCount = 0;
        nucleotideCountUnderCutOff = 0;
        
        try
        {
            fastqReader = new FastQReader(new BufferedReader (new FileReader(fileName)));
                        
            while (fastqReader.next()){
                StringBuilder str = fastqReader.getSequence();
                if (str.length() >= cutOff){
                    contigCount++;
                    nucleotideCountUnderCutOff+=str.length();                    
                }
                nucleotideCount+=str.length();
            }
            fastqReader.close();         
        } catch (IOException ex)
        {
            System.err.println("Could not open " + fileName + ": " + ex.getMessage());
        }

    }

    public int N_long()
    {
        return contigCount;
    }

    public int Sum_all()
    {
        return nucleotideCount;
    }

    public int Sum_long()
    {
       return nucleotideCountUnderCutOff;
    }
}
