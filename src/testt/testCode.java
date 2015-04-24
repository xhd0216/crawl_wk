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
    private final Queue<DBNode> q; //= new LinkedList<PageNode>();
    /* page url to pagenode map */
    private final HashMap<String, DBNode> hm; //= new HashMap<> ();
    /* page url to its _id in database */
    //private final HashMap<String, String> ptd;// = new HashMap<> ();
    private Integer count = 0;
    //private PrintWriter writer;
    private final String BaseUrl;
    private boolean shouldRun;
    private mongoConnector conn;
    private final int depth;

    testCode(String u, String fileName) throws UnsupportedEncodingException{
        //set to false when the queue is empty and no more new links is coming
        shouldRun = true;
        //root url
        BaseUrl = u;
        depth = 5;
        String url = u + "/wiki/" + fileName;
        q = new LinkedList<>();
        hm = new HashMap<> ();
        //ptd = new HashMap<> ();
        DBNode pn = new DBNode(url);
        q.add(pn);
        hm.put(url, pn);
        try{
            conn = new mongoConnector();
        }
        catch (UnknownHostException e2){
            System.out.println("can not connect to database");
        }
    }
    /* returns true if new nodes added to queue*/
    private boolean processURL(DBNode p){
        Boolean result = false;
        /* returns true if there are new links added, otherwise, return false*/
        if(p == null || p.isChecked()){
            return result;
        }
        LinkedList<String> ss = p.traverse(BaseUrl);
        
        for(String s : ss){
            DBNode n;
            synchronized(this){
                if(!hm.containsKey(s)){
                    n = new DBNode(s);
                    //n.setID(count);
                    hm.put(s, n);
                    q.add(n);
                    result = true;
                }
                else n = hm.get(s);
            }
            /* update graph */
            if(!p.isOutLink(n)){
                p.insert(n, true);
                /* update database */
                //conn.pushToArray("ID", p.getID(), "out", n.getID());
            }
            if(!n.isInLink(p)){
                n.insert(p, false);
                if(n.isChecked()){
                    conn.pushToArray("ID", n.getID(), "in", p.getID());
                }
            }
            
            /* update graph in database */
            /* add outbound link to existing node p */            
        }
        if(!p.isChecked()){
            conn.insertToCollection(p);
            p.setChecked();
            
        }
        return result;
    }
    public void calc(String t) throws InterruptedException, UnknownHostException{
	while(count < 200){
            DBNode temp = null;
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
		temp = q.remove();
		count++;
                //temp.setID(count);
		notify();
	    }
            if(temp != null){
                System.out.println("count "+count + " " + t+ " works on " + temp.getURL());
                //temp.setID(count);
                boolean b = processURL(temp);
                //conn.insertToCollection(temp);
                if(!b){
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
	}
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
