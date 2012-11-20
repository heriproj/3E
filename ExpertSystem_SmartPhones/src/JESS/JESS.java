package JESS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import jess.Fact;
import jess.Filter;
import jess.Funcall;
import jess.JessException;
import jess.Rete;
import jess.Value;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author lubostar
 */
public class JESS {
    public static Rete jess;
    public static Rete test;
    private static String command=new String();
    private static Iterator results;
    
    public static void init(){
        JESS.initJess();
    }

    private static void initJess(){
        System.out.println("initializing JESS...");
        JESS.jess=new Rete();
        try {
            command="(batch init.clp)";            
            jess.executeCommand(command);
            command="(batch data.clp)";
            jess.executeCommand(command);
            
            JESS.testJess();
        } catch (JessException ex) {
             Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("initializing JESS was successfuly done.");
    }
    
    private static void testJess() throws JessException{
        System.out.println("testing JESS...");
        Funcall call=new Funcall("eq", jess);
        call.arg(1);
        call.arg(2);
        Value v=call.execute(jess.getGlobalContext());
        String test=v.atomValue(jess.getGlobalContext());
        System.out.println(test);        
    }
    
    private static void writeResults(Iterator results){
        while(results.hasNext()){ // create Facts from results
            System.out.println("Result from Java = " + ((HashMap) results.next()).get("name"));
        }
    }
    
    private static void initWorkingMemory(){ // init from results
    }
    
    public static void setType(String type){
        System.out.println("setting type...");
        try {
            jess.reset(); // call reset
 
            command="(defglobal ?*global_type* = " + type + ")"; // redefine global
            jess.executeCommand(command);
            command="(batch data.clp)";     // reinit data
            jess.executeCommand(command);
            
            jess.run(); // run JESS
            // load results from JESS
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
            writeResults(results);
         } catch (JessException ex) {
              Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setPrice(int price){
        try {
            jess.reset();
            JESS.initWorkingMemory();
            
            String deffunction="(defglobal ?*global_price* = " + price;            
            jess.executeCommand(deffunction);
            
            jess.run();
         } catch (JessException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
