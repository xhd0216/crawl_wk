/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testt;
import java.io.UnsupportedEncodingException;
import static java.lang.Thread.sleep;
import java.net.UnknownHostException;
import java.util.ArrayList;
/**
 *
 * @author Zhizhou
 */
public class TestT {

    /**
     * @param args the command line arguments
     * @throws java.io.UnsupportedEncodingException
     * @throws java.net.UnknownHostException
     */
    public static void main(String args[]) throws UnsupportedEncodingException, UnknownHostException {
        String url = "http://en.wikipedia.org";
        String fileName = "Ohio";
        testCode PD = new testCode(url, fileName);
        //boolean allFinished = false;
	int nThreads = 4;
        ArrayList<ThreadDemo> ths = new ArrayList<>();
        
        for(int i = 1; i <= nThreads; i++){
            ThreadDemo t = new ThreadDemo("Thread--"+i, PD);
            ths.add(t);
            t.start();
            //System.out.println(t.isAlive());
        }
	// wait for threads to end
	try {
            sleep(10000);
            for(ThreadDemo j : ths){
                j.join();
                System.out.println(j.getName()+j.isAlive());
            }
        } catch( InterruptedException e) {
	    System.out.println("Interrupted");
	}
        //PD.close();
        System.out.println("****all over!****");
        
    }
}