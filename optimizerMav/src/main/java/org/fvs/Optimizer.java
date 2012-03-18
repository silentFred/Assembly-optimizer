package org.fvs;



//http://www.plantagora.org/conclusions/hybrid_454-illumina.html

/**
 *
 * @author frederick
 */
public class Optimizer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        Configurator conf =  new Configurator(args[0]);
        //test for correct conf
        if (!conf.isOK()){            
            System.err.println("Config file did not parse correctly. Make sure the format and values are correct");
            return;
        }           
                
        //assembler path, assembler arguments, output path,start,end,threads
        for (int x = 0; x < conf.size; x++)
        {
            Configurator.Args current = conf.nextConf();
            System.out.println("Started running assembly job: " + current.name);                       
            System.out.println(current);
            
            JobIterator it = new JobIterator(current);

            if (it.run())
            {
                System.out.println("Assembly job executed sucsesfully: " + current.name);
            } else
            {
                System.err.println("Assembly job encountered an error: " + current.name);
            }
            
            //go though scores
                //get best
                    //optimize on coverage or whatever else the current assembler can optimize on
                        
            //delete all but best assembly
            
        }
    }
}
