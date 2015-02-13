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
    private final MongoClient client;
    private final DB db;
    private final DBCollection coll;
    public mongoConnector() throws UnknownHostException {
        /*try{
            client = new MongoClient("localhost", 27017);
            db = client.getDB("myDB");
            coll = db.getCollection("websites");
        }
        catch(UnknownHostException e){
            throw e;
        }*/
        client = new MongoClient("localhost", 27017);
        db = client.getDB("myDB");
        coll = db.getCollection("websites");
        client.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        System.out.println("connected");
        
    }
    public void close() throws UnknownHostException{
        client.close();
    }
    public boolean writeToDB(String s, String u) throws UnknownHostException{
        BasicDBObject doc = new BasicDBObject("title", s);
        doc.append("url", u);
        coll.insert(doc);
        return true;
        
    }
    public void showOne() throws UnknownHostException{
        
        DBObject myDoc = coll.findOne();
        System.out.println(myDoc);
    }
}
