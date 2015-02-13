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
        //String url = "http://www.guancha.cn";
        String url = "http://news.sina.com.cn";
        //String url = "http://m.newsmth.net";
        testCode PD = new testCode(url);
        
	int nThreads = 4;
        ArrayList<ThreadDemo> ths = new ArrayList<ThreadDemo>();
        
        for(int i = 1; i <= nThreads; i++){
            ThreadDemo t = new ThreadDemo("Thread - "+i, PD);
            ths.add(t);
            t.start();
        }
	// wait for threads to end
	try {
            for(ThreadDemo j : ths){
                j.join();
            }
	} catch( Exception e) {
	    System.out.println("Interrupted");
	}
        try{
            PD.checkDB();
        }
        catch(Exception e){
            System.out.println("****Can not connect to DB****");
        }
    }
}
class testCode{
    private Queue<String> q = new LinkedList<String>();
    private HashSet<String> hs = new HashSet<String> ();
    private int count = 0;
    private PrintWriter writer;
    private String url;
    private boolean shouldRun;
    private mongoConnector conn;
    public void checkDB() throws UnknownHostException{
        conn.showOne();
    }
    testCode(String u) throws UnsupportedEncodingException, UnknownHostException{
        shouldRun = true;
        url = u;
        q.add(url);
        hs.add(url);
        try{
            //writer = new PrintWriter("guancha-result.txt", "UTF-8");
            writer = new PrintWriter("sina-result.txt", "UTF-8");
            writer.println(u);
        }
        catch (FileNotFoundException e1){
            System.out.println("file open error");
        }
        try{
            conn = new mongoConnector();
        }
        catch (UnknownHostException e2){
            System.out.println("can not connect to database");
        }
    }
    private boolean processURL(String page){
        Document doc;
        Elements links;
        Elements keys;
        try{
            doc = Jsoup.connect(page).get();
            links = doc.select("a");
            keys = doc.getElementsByClass("article-keywords");
        }
        catch(Exception e){
            System.out.println("can not open page");
            //e.printStackTrace();
            return false;
        }
        System.out.println(doc.title());
        String keywords = "";
        for(Element k : keys){
            Elements a = k.getElementsByTag("a");
            for(Element b : a){
                keywords += b.text()+";";
            }
        }
        if(keywords.length() > 0){
            synchronized(this){
                writer.println(doc.title());
                writer.println(keywords);
                try{
                    conn.writeToDB(page, doc.title());
                }
                catch (Exception e){
                    System.out.println("----Can't connect to database----");
                }
            }
        }
        for(Element l : links){
            String ref = l.attr("href");
            if(ref.length() > 5 && ref.charAt(0) == '/'){
                ref = url + ref;
            }
            synchronized(this){
                if(hs.contains(ref)){
                    continue;
                }
                else{
                    hs.add(ref);
                    q.add(ref);
                }
            }
        }
        return true;
        
    }
    public void calc(String t) throws InterruptedException{
	while(count < 200){
            String temp  = "*";
	    synchronized (this){
		if(!q.isEmpty()){
		    temp = q.remove();
		    count++;
		    notify();
		}
		else{
                    if(!shouldRun){
                        notify();
                        break;
                    }
                    else wait();
		}
	    }
            if(temp.length()> 1){
                System.out.println("count "+count + " " + t+ " works on link:");
                System.out.println(temp);
                boolean b = processURL(temp);
                synchronized(this){
                    if(!b && q.isEmpty()){
                        shouldRun = false;
                        System.out.println("no more page in the pool");
                        notify();
                        break;
                    }
                }
            }
	}
	writer.close();
        try{
            conn.close();
        }
        catch(Exception e){
            
        }
    }

}

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

