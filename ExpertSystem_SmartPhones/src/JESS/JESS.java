package JESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import jess.Fact;
import jess.Filter;
import jess.Funcall;
import jess.JessException;
import jess.RU;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author lubostar
 */
public class JESS {
    private static Rete jess;
    private static String command=new String();
    private static Iterator results;
    private static ArrayList<Fact> working_memory=new ArrayList();
   
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
    
    private static void writeResults(Iterator results) throws JessException{
        System.out.println("writing results...");
        while(results.hasNext()){ // create Facts from results
            HashMap result=(HashMap) results.next();
            Fact fact=new Fact("device", jess);
            System.out.println("name=" + result.get("name"));
            fact.setSlotValue("name", new Value(result.get("name").toString(),RU.STRING));
            fact.setSlotValue("device_type", new Value(result.get("type").toString(),RU.STRING));
            fact.setSlotValue("manufacturer", new Value(result.get("manufacturer").toString(),RU.STRING));
            fact.setSlotValue("OS", new Value(result.get("os").toString(),RU.STRING));
            fact.setSlotValue("display_resolution", new Value(Float.parseFloat(result.get("display").toString()), RU.FLOAT));
            fact.setSlotValue("storage", new Value((int)result.get("storage"),RU.INTEGER));
            fact.setSlotValue("camera", new Value((int)result.get("camera"),RU.INTEGER));

            ValueVector vv = new ValueVector();
            Object[] v=(Object[]) result.get("connectivity");
//            System.out.print(" connectivity=");
            for(int i=v.length-1;i>=0;i--){
//                System.out.print(" " + v[i]);
                vv.add(new Value(v[i].toString(), RU.STRING));
            }
            fact.setSlotValue("connectivity", new Value(vv, RU.LIST));

            v=(Object[]) result.get("io");
//            System.out.print(" io=");
            for(int i=v.length-1;i>=0;i--){
//                System.out.print(" " + v[i]);
                vv.add(new Value(v[i].toString(), RU.STRING));
            }
            fact.setSlotValue("input_output", new Value(vv, RU.LIST));

            fact.setSlotValue("battery", new Value((int)result.get("battery"),RU.INTEGER));
            fact.setSlotValue("price", new Value((int)result.get("price"),RU.INTEGER));

            working_memory.add(fact);
            jess.remove(result);
        }
    }
    
    private static void initWorkingMemory() throws JessException{ // init from results
        for(int i=0;i<working_memory.size();i++){
            jess.assertFact(working_memory.get(i));
        }
        working_memory=null; // facts are asserted, reset ArrayList
        working_memory=new ArrayList<>();
        
        results=null;
    }
    
    public static void setType(String type){
        System.out.println("setting type...");
        try {
            jess.reset(); // call reset
 
            command="(defglobal ?*global_type* = " + type + ")"; // redefine global
            jess.executeCommand(command);
            command="(batch data.clp)";     // reinit data
            jess.executeCommand(command);
            
            command="(facts)"; // redefine global
            jess.executeCommand(command);
            jess.run(); // run JESS
            // load results from JESS
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
            writeResults(results);
         } catch (JessException ex) {
              Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void setManufacturer(String manufacturer){
        System.out.println("setting manufacturer...");
        try {
            jess.reset(); // call reset
 
            resetAllGlobalVariables();
            command="(defglobal ?*global_manufacturer* = \"" + manufacturer + "\")"; // redefine global
            jess.executeCommand(command);

            JESS.initWorkingMemory();

            command="(facts)"; // redefine global
            jess.executeCommand(command);

            jess.run(); // run JESS
            // load results from JESS
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
            writeResults(results);
         } catch (JessException ex) {
              Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setOS(String OS){
        System.out.println("setting OS...");
        try {
            jess.reset(); // call reset
 
            resetAllGlobalVariables();
            command="(defglobal ?*global_os* = \"" + OS + "\")"; // redefine global
            jess.executeCommand(command);
            
            JESS.initWorkingMemory();

            command="(facts)"; // redefine global
            jess.executeCommand(command);
            
            jess.run(); // run JESS
            // load results from JESS
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
            writeResults(results);
         } catch (JessException ex) {
              Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void resetAllGlobalVariables() throws JessException{
        command="(defglobal ?*global_os* = undefined)"; // redefine global
        jess.executeCommand(command);
          command="(defglobal ?*global_manufacturer* = undefined)"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_type* = undefined)"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_display_min* = 0)"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_display_max* = 0)"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_storage_min* = 0)"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_storage_max* = 0)"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_camera_min* = 0)"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_camera_max* = 0)"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_connectivity* = (create$ undefined))"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_io* = (create$ undefined))"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_battery_min* = 0)"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_battery_max* = 0)"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_price_min* = 0)"; // delete previous global
          jess.executeCommand(command);
          command="(defglobal ?*global_price_max* = 0)"; // delete previous global
          jess.executeCommand(command);
    }
    
    public static void setDisplay(float min, float max){
        System.out.println("setting display resolution...");
        try {
            jess.reset();
            
            resetAllGlobalVariables();
            String deffunction="(defglobal ?*global_display_min* = " + min + ")";
            jess.executeCommand(deffunction);
            deffunction="(defglobal ?*global_display_max* = " + max + ")";
            jess.executeCommand(deffunction);
            JESS.initWorkingMemory();
            
            jess.run();
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
            writeResults(results);
         } catch (JessException ex) {
            Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setStorage(int min, int max){
        System.out.println("setting storage capacity...");
        try {
            jess.reset();
            
            resetAllGlobalVariables();
            String deffunction="(defglobal ?*global_storage_min* = " + min + ")";
            jess.executeCommand(deffunction);
            deffunction="(defglobal ?*global_storage_max* = " + max + ")";
            jess.executeCommand(deffunction);
            JESS.initWorkingMemory();
            
            jess.run();
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
            writeResults(results);
         } catch (JessException ex) {
            Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setCamera(float min, float max){
        System.out.println("setting camera resolution...");
        try {
            jess.reset();
            
            resetAllGlobalVariables();
            String deffunction="(defglobal ?*global_camera_min* = " + min + ")";
            jess.executeCommand(deffunction);
            deffunction="(defglobal ?*global_camera_max* = " + max + ")";
            jess.executeCommand(deffunction);
            JESS.initWorkingMemory();
            
            jess.run();
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
            writeResults(results);
         } catch (JessException ex) {
            Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setConnectivity(ArrayList<String> conns){
        System.out.println("setting connectivity...");
        try {
            jess.reset(); // call reset
 
            resetAllGlobalVariables();
            command="(defglobal ?*global_connectivity* = (create$";
            for(int i=0;i<conns.size();i++){
                command=command.concat(" " + conns.get(i));
            }
            command=command.concat("))");
            jess.executeCommand(command);
            JESS.initWorkingMemory();
            
            jess.run(); // run JESS
            // load results from JESS
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
            writeResults(results);
         } catch (JessException ex) {
              Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setIO(ArrayList<String> io){
        System.out.println("setting input/output...");
        try {
            jess.reset(); // call reset
 
            resetAllGlobalVariables();
            command="(defglobal ?*global_io* = (create$"; // redefine global
            for(int i=0;i<io.size();i++){
                command=command.concat(" " + io.get(i));
            }
            command=command.concat("))");

            jess.executeCommand(command);
            JESS.initWorkingMemory();
            
            jess.run(); // run JESS
            // load results from JESS
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
            writeResults(results);
         } catch (JessException ex) {
              Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void setBattery(int min, int max){
        System.out.println("setting battery...");
        try {
            jess.reset();
            
            resetAllGlobalVariables();
            String deffunction="(defglobal ?*global_battery_min* = " + min + ")";
            jess.executeCommand(deffunction);
            deffunction="(defglobal ?*global_battery_max* = " + max + ")";
            jess.executeCommand(deffunction);
            JESS.initWorkingMemory();
            
            jess.run();
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
            writeResults(results);
         } catch (JessException ex) {
            Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setPrice(int min, int max){
        System.out.println("setting price...");
        try {
            jess.reset();
            
            resetAllGlobalVariables();
            String deffunction="(defglobal ?*global_price_min* = " + min + ")";
            jess.executeCommand(deffunction);
            deffunction="(defglobal ?*global_price_max* = " + max + ")";
            jess.executeCommand(deffunction);
            JESS.initWorkingMemory();
            
            jess.run();
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
            writeResults(results);
         } catch (JessException ex) {
            Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Iterator getResults(){
        return JESS.results;
    }
}
