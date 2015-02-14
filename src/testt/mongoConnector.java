/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Zhizhou
 */
package testt;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.WriteConcern;
import java.net.UnknownHostException;

public class mongoConnector {
    private  MongoClient client;
    private  DB db;
    private  DBCollection coll;
    public mongoConnector() throws UnknownHostException {
        /*
        this section won't cause any exception yet, exception will be thrown
        when committing update to the database        
        */
        client = new MongoClient();
        db = client.getDB("myDB");
        coll = db.getCollection("websites");
        client.setWriteConcern(WriteConcern.ACKNOWLEDGED);        
    }
    public void close() throws UnknownHostException{
        /* the close method will also cause exception*/
        client.close();
    }
    public void writeToDB(String t, String u) throws UnknownHostException{
        BasicDBObject doc = new BasicDBObject("title", t);
        doc.append("url", u);
        coll.insert(doc);
    }
    public boolean writeToDB(PageNode p){
        try{
            BasicDBObject doc = new BasicDBObject("url", p.getURL());
            /*DBCursor cursor = coll.find(doc);
            if(cursor.count() > 0){
                while(cursor.hasNext()){
                    cursor.remove();
                    cursor.next();
                }
            }
            System.out.println("no deplicates now");*/
            if(p.isChecked()) doc.append("title", p.getTitle());
            BasicDBObject out = new BasicDBObject();
            for(PageNode t : p.getOut()){
                if(t.isChecked()){
                    out.append("out",t.getTitle());
                }
            }
            for(PageNode t : p.getIn()){
                if(t.isChecked()){
                    out.append("in", t.getTitle());
                }
            }
            if(!out.isEmpty()){
                doc.append("friends", out);
            }
        
            coll.insert(doc);
        }
        catch(Exception e){
            System.out.println("--cannot connect to host--");
            return false;
        }
        return true;
        
    }
    @Deprecated
    public void showOne() throws UnknownHostException{
        
        DBObject myDoc = coll.findOne();
        System.out.println(myDoc);
    }
}
