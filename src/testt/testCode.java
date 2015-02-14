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
import java.util.HashMap;
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
class PageNode{
    String UniqueID;
    String title;
    LinkedList<PageNode> outgoings; 
    LinkedList<PageNode> incomings;
    Boolean traversed;
    public String getURL(){
        return UniqueID;
    }
    public String getTitle(){
        return title;
    }
    public boolean isChecked(){
        return traversed;
    }
    public LinkedList<PageNode> getOut(){
        return outgoings;
    }
    public LinkedList<PageNode> getIn(){
        return incomings;
    }
    @Deprecated
    PageNode(String u, PageNode p){
        outgoings  = new LinkedList();
        incomings  = new LinkedList();
        UniqueID = u;
        traversed = false;
        incomings.add(p);
    }
    PageNode(String u){
        outgoings  = new LinkedList();
        incomings  = new LinkedList();
        UniqueID = u;
        traversed = false;
    }
    public void insert(PageNode p, Boolean isOut){
        if(isOut)
            outgoings.add(p);
        else
            incomings.add(p);
    }
    public LinkedList<String> traverse(String BaseUrl){
        Document doc;
        Elements links;
        LinkedList<String> result = new LinkedList();
        try{
            doc = Jsoup.connect(UniqueID).get();
            links = doc.select("a");
        }
        catch(Exception e){
            System.out.println("can not open page");
            return result;
        }
        title = doc.title();
        for(Element l : links){
            /*
            Extract forwarding links
            if the link does not contain a title, it is not content in wiki
            */
            String cl = l.attr("class");
            if(!cl.equals("mw-redirect")) continue;
            String ref = l.attr("href");
            //String outlink = l.attr("title");
            if(ref.charAt(0) == '/'){
                ref = BaseUrl + ref;
            }
            result.add(ref);
            
        }
        traversed = true;
        return result;
    }
    
}

//controllor
class testCode{
    private Queue<PageNode> q = new LinkedList<PageNode>();
    private HashMap<String, PageNode> hm = new HashMap<> ();
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
        PageNode pn = new PageNode(url);
        q.add(pn);
        hm.put(url, pn);
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
    private boolean processURL(PageNode p){
        Boolean result = false;
        /* returns true if there are new links added, otherwise, return false*/
        if(p == null || p.isChecked()){
            return result;
        }
        LinkedList<String> ss = p.traverse(BaseUrl);
        for(String s : ss){
            synchronized(this){
                if(!hm.containsKey(s)){
                    PageNode n = new PageNode(s);
                    hm.put(s, n);
                    q.add(n);
                    result = true;
                }
                hm.get(s).insert(p, false);
            }
            p.insert(hm.get(s), true);
        }
        return result;
    }
    public void calc(String t) throws InterruptedException{
	while(count < 20){
            PageNode temp = null;
	    synchronized (this){
                if(q.isEmpty()){
                    if(!shouldRun){
                        notify();
                        break;
                    }
                    else{
                        wait();
                    }
                }
                else if(!q.isEmpty()){
		    temp = q.remove();
		    count++;
		    notify();
		}
	    }
            if(temp != null){
                System.out.println("count "+count + " " + t+ " works on " + temp.getURL());
                boolean b = processURL(temp);
                synchronized(this){
                    if(!b && q.isEmpty()){
                        shouldRun = false;
                        System.out.println("no more pages in the pool");
                        notify();
                        break;
                    }
                }
            }
	}
        //close();
        //System.out.println("Thread really ends");
    }
    public void close(){
        
        try{
            writer.close();
            conn.close();
            System.out.println("closed");
        }
        catch(Exception e){
            //do nothing;
        }
    }

}
