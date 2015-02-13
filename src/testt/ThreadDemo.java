/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testt;

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
    public void run() {
	//System.out.println("Thread " +  threadName + "is working.");
	try{
	    PD.calc(threadName);
	}catch (InterruptedException e){
	    System.out.println("Oops...");
	}
    }
    
    public void start ()
    {
	System.out.println("Starting " +  threadName );
	if (t == null)
	    {
		t = new Thread (this, threadName);
		t.start ();
	    }
    }
    
}