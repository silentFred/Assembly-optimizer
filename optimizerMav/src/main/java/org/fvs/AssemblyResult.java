package org.fvs;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fred HP
 */
public class AssemblyResult {
    public String falen;
    public int kmer;
    public double velvetScore;

    AssemblyResult(String _falen, int _kmer, double _velvetScore){
        falen = _falen;
        kmer = _kmer;
        velvetScore = _velvetScore;
    }
}
