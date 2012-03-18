package org.fvs;




import java.io.*;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author frederick
 */
public class AssemblyJob implements Runnable{

    private String executablePath;
    private String arguments;
    private final String outputPathSub;
    private int kmer;
    private ArrayList<AssemblyResult> scores = new ArrayList<AssemblyResult>();
    private PrintWriter outLogFile;
    private BufferedReader bri;
    private BufferedReader bre;
    private String name;
    
    
    //each job should have its own folder to store assembly results
    //get stats and console output from assembly and save in shared hash map  (key is kmer)
    //when finished delete folder
    
    AssemblyJob(String _executable, String _output, String _args, int _kmer, ArrayList<AssemblyResult> _scores, String _name){
        kmer = _kmer;
        arguments = _args;
        executablePath = _executable;
        name = _name;
        outputPathSub = _output+"\\"+name+"_"+kmer;
        scores = _scores;        
    }

   
    
    @Override
    public void run() {

        setupJobSubDirectory();
        
        executeJob();

        //getJobStats();

        cleanJobDirectory();
    }
    
    
    public boolean executeJob() {

        String line;
        try
        {
            outLogFile = new PrintWriter(new FileWriter(outputPathSub + "/logfile.txt"));
        } catch (Exception e)
        {
            System.err.println("Error creating log file: " + e.getMessage());
            return false;
        }

                
        try {          
            //executablePath ,arguments, outputPath
            Process p = Runtime.getRuntime().exec("cmd /c dir");
            bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            while ((line = bri.readLine()) != null) {
                outLogFile.println(line);
                //System.out.println(line);
            }                                                            
            bri.close();
            
            while ((line = bre.readLine()) != null) {
                outLogFile.println("ERROR: "+line);
                System.err.println(line);
            }
            bre.close();
           
            outLogFile.close();            
            p.waitFor();

            System.out.println(kmer +" job done.");
            
        } catch (Exception err) {
            System.err.println("Error while running "+kmer +" job: "+ err.getMessage());
            return false;
        }
        
        return true;
    }

    private void setupJobSubDirectory()
    {
        try{
            if (new File(outputPathSub).mkdir()){
                System.out.println("Subdirectory created for "+ kmer);
            }else{
                System.out.println("Subdirectory for "+ kmer + " already exists.");
            }
        }catch(Exception e){
            System.err.println("Error creating job sub-directory for " + kmer);
        }
    }

    private void getJobStats()
    {
        //run stats on fastq and return to jobiterator
        
        String line;
        String falenResult = "";
        double velvetScore;
        
        try
        {
            Process p = Runtime.getRuntime().exec("faLen < " + outputPathSub +"/*.fastq" + " | stats");

            bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((line = bri.readLine()) != null)
            {
                System.out.println(line);
                falenResult+=(line+"\n");
            }
            bri.close();

            while ((line = bre.readLine()) != null)
            {
                System.err.println(line);
                //falenResult+=line;
            }

            bre.close();
            p.waitFor();
        } catch (Exception e)
        {
            System.err.println("Error while trying to run faLen on " + kmer);
        }
        
        
        
        /*
        $ faLen < six_cesa_sequences.fa | stats
                   N = 6
                 SUM = 21540
                 MIN = 3341
        1ST-QUARTILE = 3452
              MEDIAN = 3591.5
        3RD-QUARTILE = 3782
                 MAX = 3782
                MEAN = 3590.0
                 N50 = 3712
       
        (N50_{all}*N_{long})/Sum_{all}+log(Sum_{long})

        N50_all: die N50 van al die contigs is
        N_long: die hoeveelheid contigs langer as 1000bp is
        Sum_all: Die  hoeveelheid bases in al die contigs is
        Sum_long: Die hoebeelheid bases in alle contigs langer as 1000bp is
        */
                       
        
        ContigCutOffStats stats = new ContigCutOffStats(outputPathSub +"/*.fastq",1000);
        
        int n_long = stats.N_long();
        int sum_all = stats.Sum_all();
        int sum_long = stats.Sum_long();
        int n50 = Integer.parseInt(falenResult.split("N50 = ")[1].trim());
        
        velvetScore = (n50 * n_long) / sum_all + Math.log(sum_long);
        
        scores.add(new AssemblyResult(falenResult, kmer, velvetScore));
    }

    private void cleanJobDirectory()
    {
        if (deleteDir(new File(outputPathSub)))
        {
            //System.out.println("Removed job subdirectory for " + kmer);
        } else
        {
            System.err.println(" Error removing job subdirectory for " + kmer);
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}