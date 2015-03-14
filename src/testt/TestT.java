/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testt;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import static java.lang.System.exit;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author Zhizhou
 */
public class TestT {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws UnsupportedEncodingException, UnknownHostException {
        String url = "http://en.wikipedia.org";
        String fileName = "Ohio";
        testCode PD = new testCode(url, fileName);
        
	int nThreads = 4;
        ArrayList<ThreadDemo> ths = new ArrayList<>();
        
        for(int i = 1; i <= nThreads; i++){
            ThreadDemo t = new ThreadDemo("Thread-"+i, PD);
            ths.add(t);
            t.start();
            
        }
	// wait for threads to end
	try {
            for(ThreadDemo j : ths){
                j.join();
                System.out.println(j.getName()+" ended");
            }
        } catch( Exception e) {
	    System.out.println("Interrupted");
	}
        //PD.close();
        System.out.println("****all over!****");
        
    }
}