package JESS;

import java.util.ArrayList;
import java.util.HashMap;
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
    public static Rete jess;
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
        while(results.hasNext()){ // create Facts from results
            HashMap result=(HashMap) results.next();
            Fact fact=new Fact("device", jess);
            fact.setSlotValue("name", new Value(result.get("name")));
            fact.setSlotValue("device_type", new Value(result.get("type")));
            fact.setSlotValue("manufacturer", new Value(result.get("name")));
            fact.setSlotValue("OS", new Value(result.get("os")));
            fact.setSlotValue("display_resolution", new Value(result.get("dislpay")));
            fact.setSlotValue("storage", new Value(result.get("storage")));
            fact.setSlotValue("camera", new Value(result.get("camera")));

            ValueVector vv = new ValueVector();
            Object[] v=(Object[]) result.get("connectivity");
            System.out.print(" connectivity=");
            for(int i=v.length-1;i>=0;i--){
                System.out.print(" " + v[i]);
                vv.add(new Value(v[i].toString(), RU.STRING));
            }
            fact.setSlotValue("connectivity", new Value(vv, RU.LIST));

            v=(Object[]) result.get("io");
            System.out.print(" io=");
            for(int i=v.length-1;i>=0;i--){
                System.out.print(" " + v[i]);
                vv.add(new Value(v[i].toString(), RU.STRING));
            }
            fact.setSlotValue("input_output", new Value(vv, RU.LIST));

            fact.setSlotValue("battery", new Value(result.get("battery")));
            fact.setSlotValue("price", new Value(result.get("price")));

            working_memory.add(fact);

            System.out.print("name=" + result.get("name"));
/*            System.out.println("Result from Java = name=" + ((HashMap) results.next()).get("name") +
                    "type=" + ((HashMap) results.next()).get("type") +
                    "manufacturer=" + ((HashMap) results.next()).get("manufacturer") +
                    "OS=" + ((HashMap) results.next()).get("os") +
                    "display resolution=" + ((HashMap) results.next()).get("display") +
                    "storage=" + ((HashMap) results.next()).get("storage") +
                    "camera=" + ((HashMap) results.next()).get("camera") +
                    "connectivity=" + ((HashMap) results.next()).get("connectivity") +
                    "io=" + ((HashMap) results.next()).get("io") +
                    "battery=" + ((HashMap) results.next()).get("battery") +
                    "price=" + ((HashMap) results.next()).get("price"));*/
        }
    }
    
    private static void initWorkingMemory() throws JessException{ // init from results
        for(int i=0;i<=working_memory.size();i++){
            jess.assertFact(working_memory.get(i));
        }
        working_memory=null;
        working_memory=new ArrayList<>();
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
    
    public static void setManufacturer(String manufacturer){
        System.out.println("setting manufacturer...");
        try {
            jess.reset(); // call reset
 
            command="(defglobal ?*global_manufacturer* = " + manufacturer + ")"; // redefine global
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

    public static void setOS(String OS){
        System.out.println("setting OS...");
        try {
            jess.reset(); // call reset
 
            command="(defglobal ?*global_os* = " + OS + ")"; // redefine global
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
    
    public static void setDisplay(float min, float max){
        System.out.println("setting display resolution...");
        try {
            jess.reset();
            
            String deffunction="(defglobal ?*global_display_min* = " + min + ")";
            jess.executeCommand(deffunction);
            deffunction="(defglobal ?*global_display_max* = " + max + ")";
            jess.executeCommand(deffunction);
            JESS.initWorkingMemory();
            
            jess.run();
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
         } catch (JessException ex) {
            Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setStorage(int min, int max){
        System.out.println("setting storage capacity...");
        try {
            jess.reset();
            
            String deffunction="(defglobal ?*global_storage_min* = " + min + ")";
            jess.executeCommand(deffunction);
            deffunction="(defglobal ?*global_storage_max* = " + max + ")";
            jess.executeCommand(deffunction);
            JESS.initWorkingMemory();
            
            jess.run();
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
         } catch (JessException ex) {
            Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setCamera(float min, float max){
        System.out.println("setting camera resolution...");
        try {
            jess.reset();
            
            String deffunction="(defglobal ?*global_camera_min* = " + min + ")";
            jess.executeCommand(deffunction);
            deffunction="(defglobal ?*global_camera_max* = " + max + ")";
            jess.executeCommand(deffunction);
            JESS.initWorkingMemory();
            
            jess.run();
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
         } catch (JessException ex) {
            Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setConnectivity(ArrayList<String> conns){
        System.out.println("setting connectivity...");
        try {
            jess.reset(); // call reset
 
            command="(defglobal ?*global_connectivity* = (create$";
            for(int i=0;i<=conns.size();i++){
                command.concat(" " + conns.get(i));
            }
            command.concat("))");
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
 
            command="(defglobal ?*global_io* = (create$"; // redefine global
            for(int i=0;i<=io.size();i++){
                command.concat(" " + io.get(i));
            }
            command.concat("))");

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
            
            String deffunction="(defglobal ?*global_battery_min* = " + min + ")";
            jess.executeCommand(deffunction);
            deffunction="(defglobal ?*global_battery_max* = " + max + ")";
            jess.executeCommand(deffunction);
            JESS.initWorkingMemory();
            
            jess.run();
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
         } catch (JessException ex) {
            Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setPrice(int min, int max){
        System.out.println("setting price...");
        try {
            jess.reset();
            
            String deffunction="(defglobal ?*global_price_min* = " + min + ")";
            jess.executeCommand(deffunction);
            deffunction="(defglobal ?*global_price_max* = " + max + ")";
            jess.executeCommand(deffunction);
            JESS.initWorkingMemory();
            
            jess.run();
            results=jess.getObjects(new Filter.ByClass(HashMap.class));
         } catch (JessException ex) {
            Logger.getLogger(JESS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
