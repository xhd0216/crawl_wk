/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testt;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Zhizhou
 */
//controllor
class testCode{
    private Queue<String> q = new LinkedList<String>();
    private HashSet<String> hs = new HashSet<String> ();
    private Integer count = 0;
    private PrintWriter writer;
    private String BaseUrl;
    private boolean shouldRun;
    private mongoConnector conn;
    @Deprecated
    public void checkDB() throws UnknownHostException{
        conn.showOne();
    }
    testCode(String u, String fileName) throws UnsupportedEncodingException, UnknownHostException{
        //set to false when the queue is empty and no more new links is coming
        shouldRun = true;
        //root url
        BaseUrl = u;
        String url = u + "/wiki/" + fileName;
        q.add(url);
        hs.add(url);
        try{
            writer = new PrintWriter(fileName+".txt", "UTF-8");
            writer.println(url);
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
        try{
            doc = Jsoup.connect(page).get();
            links = doc.select("a");
        }
        catch(Exception e){
            System.out.println("can not open page");
            return false;
        }
        for(Element l : links){
            /*
            Extract forwarding links
            if the link does not contain a title, it is not content in wiki
            */
            String cl = l.attr("class");
            if(!cl.equals("mw-redirect")) continue;
            String ref = l.attr("href");
            String outlink = l.attr("title");
            if(ref.charAt(0) == '/'){
                ref = BaseUrl + ref;
            }
            //writer.println(outlink);
            synchronized(this){
                if(hs.contains(ref)){
                    continue;
                }
                else{
                    hs.add(ref);
                    q.add(ref);
                    writer.println(outlink);
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
                System.out.println("count "+count + " " + t+ " works on " + temp);
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
        close();
    }
    public void close(){
        writer.close();
        try{
            conn.close();
        }
        catch(Exception e){
            //do nothing;
        }
    }

}
