/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testt;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zhizhou
 */
class ThreadDemo extends Thread {
    private Thread t;
    private String threadName;
    testCode  PD;
    
    ThreadDemo( String name,  testCode pd){
	threadName = name;
	PD = pd;
    }
    @Override
    public void run() {
	try{
	    PD.calc(threadName);
            
            
	}catch (InterruptedException e){
	    System.out.println("Oops...");
	}
        /*for(int i = 1; i<=10; i++){
            synchronized(PD){
                try {
                    wait();
                } catch (InterruptedException ex) {
                    //Logger.getLogger(ThreadDemo.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(threadName + " is running at "+i);
            }
        }*/
    }
    
    @Override
    public void start ()
    {
	//System.out.println("Starting " +  threadName );
	if (t == null)
	    {
		t = new Thread (this, threadName);
		t.start ();
	    }
    }
    
}