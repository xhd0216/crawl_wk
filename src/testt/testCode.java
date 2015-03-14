/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testt;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Zhizhou
 */


//controllor
class testCode{
    private Queue<PageNode> q = new LinkedList<PageNode>();
    private HashMap<String, PageNode> hm = new HashMap<> ();
    private Integer count = 0;
    //private PrintWriter writer;
    private String BaseUrl;
    private boolean shouldRun;
    private mongoConnector conn;
    /*@Deprecated
    public void checkDB() throws UnknownHostException{
        conn.showOne();
    }*/
    testCode(String u, String fileName) throws UnsupportedEncodingException, UnknownHostException{
        //set to false when the queue is empty and no more new links is coming
        shouldRun = true;
        //root url
        BaseUrl = u;
        String url = u + "/wiki/" + fileName;
        PageNode pn = new PageNode(url);
        q.add(pn);
        hm.put(url, pn);
        /*try{
            writer = new PrintWriter(fileName+".txt", "UTF-8");
            writer.println(url);
        }
        catch (FileNotFoundException e1){
            System.out.println("file open error");
        }*/
        try{
            conn = new mongoConnector();
            
        }
        catch (UnknownHostException e2){
            System.out.println("can not connect to database");
        }
    }
    /* returns true if new nodes added to queue*/
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
	while(count < 200){
            PageNode temp = null;
	    synchronized (this){
                while(q.isEmpty()){
                    if(!shouldRun){
                        notify();
                        break;
                    }
                    else{
                        wait();
                    }
                }
                //else if(!q.isEmpty()){
		    temp = q.remove();
		    count++;
		    notify();
		//}
	    }
            if(temp != null){
                System.out.println("count "+count + " " + t+ " works on " + temp.getURL());
                boolean b = processURL(temp);
                if(b) conn.writeToDB(temp);
                else
                    synchronized(this){
                        if(q.isEmpty()){
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
        //return;
    }
    public void close(){
        
        try{
            //writer.close();
            //conn.close();
            System.out.println("closed");
        }
        catch(Exception e){
            //do nothing;
        }
        
    }

}
