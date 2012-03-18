package org.fvs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author frederick
 */

public class JobIterator {
    
    private int numThreads;
    private int start,end;
    private String assembler, assemblerArgs, outPath, name;
    private ExecutorService executor;    
    private Map<Runnable,Future> threadMap = new HashMap<Runnable,Future>();
    private ArrayList<AssemblyResult> scores = new ArrayList<AssemblyResult>();
    

    JobIterator(Configurator.Args current)
    {
        numThreads = current.threads;
        executor = Executors.newFixedThreadPool(numThreads);
        assembler = current.assembler;
        assemblerArgs = current.assemblerArgs;
        outPath = current.outPath;
        start = current.start;
        end = current.end;
        name = current.name;
    }

    public boolean run() {
                                 
        
        for (int x = start; x <= end; x+=2)
        {
            //assembler,out, args, kmer, scores
            if(!setupJobDirectory(outPath,name))
                return false;
            
            Runnable aTask = new AssemblyJob(assembler,outPath,assemblerArgs,x,scores,name);
            threadMap.put(aTask, executor.submit(aTask));
        }
                            
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        
        //write score list to file in outpath
        exportScores();
        return executor.isTerminated();
    }
    
    private boolean setupJobDirectory(String dir,String name)
    {
        try{
            if (new File(dir).mkdir()){
                System.out.println("Job directory created for "+name);
                return true;
            }else{
                System.out.println("Job directory already exists for " +name);
                return true;
            }
        }catch(Exception e){
            System.err.println("Error creating job irectory for "+name+": "+e.getMessage());
            return false;
        }
    }

    private void exportScores()
    {
        try
        {
            PrintWriter outStatFile = new PrintWriter(new FileWriter(outPath + "/jobStats.txt"));
            
            for (AssemblyResult stat: scores){ 
                outStatFile.println("Results on kmer size:"+ stat.kmer);
                outStatFile.println(stat.falen);
                outStatFile.println("VELVET_SCORE="+stat.velvetScore);                
            }
            outStatFile.close();
            
        } catch (IOException ex)
        {
            System.err.println("Error creating jobStats.txt: " + ex.getMessage());
        }
    }
}
