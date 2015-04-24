/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testt;

import java.net.UnknownHostException;

/**
 *
 * @author Zhizhou
 */
class ThreadDemo extends Thread {
    private Thread t;
    private final String threadName;
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
	} catch (UnknownHostException ex) {
            System.out.println("db connection fails");
            //Logger.getLogger(ThreadDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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