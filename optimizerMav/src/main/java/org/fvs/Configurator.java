package org.fvs;




import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;


/**
 *
 * @author Fred
 */
public class Configurator {

    public class Args {

        public String name;
        public String assembler;
        public String assemblerArgs;
        public String assemblerPath;
        public String outPath;
        public int start, end, threads;
        public int size;

        private Args(Args newArgs)
        {
            name=newArgs.name;
            assembler=newArgs.assembler;
            assemblerArgs=newArgs.assemblerArgs;
            assemblerPath=newArgs.assemblerPath;
            outPath=newArgs.outPath;
            start=newArgs.start;
            end=newArgs.end;
            threads=newArgs.threads;
        }

        private Args()
        {
            name="";
            assembler="";
            assemblerArgs="";
            assemblerPath="";
            outPath="";
            start=0;
            end=0;
            threads=0;
        }
        @Override
        public String toString()
        {
            return "NAME="+ name + "\n" +"ASSEMBLER="+assembler + "\n" +"ASSEMBLERARGS="+assemblerArgs + "\n" + 
                    "ASSEMBLERPATH="+assemblerPath + "\n" +"OUTPUTPATH="+outPath + "\n" +"START="+start + "\n" +
                    "END="+end+ "\n" +"THREADS="+threads;
        }
    }
    
    private BufferedReader in;
    private boolean succeeded;
    private  Queue<Args> arglist = new LinkedList<Args>();
    public int size;
    
    Configurator(String configFile)
    {
        size = 0;
        succeeded = false;
        try
        {
            in = new BufferedReader(new FileReader(configFile));
            BufferedReader br = new BufferedReader(in);
            String strLine;
            
            try
            {
                Args newArgs = new Args();
                int start=0,end=0;
                                
                while ((strLine = br.readLine()) != null)
                {
                    //System.out.println(strLine);
                    if (strLine.startsWith("##"))
                            continue;
                    if (strLine.startsWith(">")){
                        arglist.add(new Args (newArgs));
                        
                        if(start < 1 || end < 1 ){
                            System.err.println("Start or end range must not be negative");
                            return;
                        }
                        if (start >= end){
                            System.err.println("End cannot be smaller than start parameter");
                            return;
                        }                        
                        size++;
                        
                    } else
                    if(strLine.startsWith("NAME=")){
                        newArgs.name=strLine.split("=")[1].trim();
                    } else
                    if(strLine.startsWith("ASSEMBLER=")){
                        newArgs.assembler=strLine.split("=")[1].trim();
                    } else
                    if(strLine.startsWith("ASSEMBLERARGS=")){
                        newArgs.assemblerArgs=strLine.split("=")[1].trim();
                    } else
                    if(strLine.startsWith("ASSEMBLERPATH=")){
                        newArgs.assemblerPath=strLine.split("=")[1].trim();
                    } else
                    if(strLine.startsWith("OUTPUTPATH=")){
                        newArgs.outPath=strLine.split("=")[1].trim();
                    } else
                    if(strLine.startsWith("THREADS=")){                                                
                        newArgs.threads=Integer.parseInt(strLine.split("=")[1].trim());                        
                    } else
                    if(strLine.startsWith("START=")){
                        
                        start =Integer.parseInt(strLine.split("=")[1].trim());                                                                        
                        if (start % 2 == 0)
                            start--;
                        newArgs.start=start;
                        
                    } else
                    if(strLine.startsWith("END=")){
                                                                        
                        end = Integer.parseInt(strLine.split("=")[1].trim());
                        if (end % 2 == 0)
                            end++;
                        newArgs.end = end;
                    }                    
                }
                
                succeeded = true;
                in.close();
                
            } catch (IOException ex)
            {
                System.err.println("Could not read from config file: " + ex.getMessage());
            }           
        }catch (FileNotFoundException ex)
        {
            System.err.println("Could not open config file: " + ex.getMessage());
        }
    }
    
    
    public Args nextConf(){        
        return arglist.remove();
    }
    
    public boolean isOK(){
        return succeeded;
    }
}
