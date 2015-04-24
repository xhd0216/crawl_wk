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
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
public class mongoConnector {
    private final  MongoClient client;
    private final  DB db;
    private final  DBCollection coll;
    private final WriteConcern wc = WriteConcern.ACKNOWLEDGED;
    @Deprecated
    public String urlToID(String u){
        BasicDBObject query = new BasicDBObject("url", u);
        DBObject o = coll.findOne(query);
        if(o == null){
            return "";
        }
        else{
            return (String)o.get("_id");
        }
    }
    public mongoConnector() throws UnknownHostException {
        /*
        *  this section won't cause any exception yet, exception will be thrown
        *  when committing update to the database        
        */
        Scanner sc = new Scanner(System.in);
        String pwd;
        System.out.print("password:");
        pwd = sc.next();
        
        /*char [] pwd;
        pwd = System.console().readPassword();*/
        MongoCredential mc = MongoCredential.createCredential("rwmy", "myDB", pwd.toCharArray());
        ServerAddress sa = new ServerAddress("localhost");
        client = new MongoClient(sa, Arrays.asList(mc));
        db = client.getDB("myDB");
        coll = db.getCollection("websites");
        client.setWriteConcern(WriteConcern.ACKNOWLEDGED);        
    }
    public void close() throws UnknownHostException{
        /* the close method will also cause exception*/
        client.close();
    }
    @Deprecated
    public void writeToDB(String t, String u) throws UnknownHostException{
        BasicDBObject doc = new BasicDBObject("title", t);
        doc.append("url", u);
        coll.insert(doc);
    }
    public boolean pushToArray(String key, Object val,  String field, Object o){
        WriteResult res;
        try{
            BasicDBObject query = new BasicDBObject(key, val);
            BasicDBObject push = new BasicDBObject("$push", new BasicDBObject(field, o));
            res = coll.update(query, push, false, true, wc);
        }catch(Exception e){
            return false;
        } 
        return res.getN() > 0;
    }
    public void insertToCollection(DBCompatible dbc){
        if(dbc == null){
            return;
        }
        WriteResult res = coll.insert(dbc.toDBO(), wc);
        //return res.getN() > 0;
    }
    @Deprecated
    public boolean writeToDB(PageNode p){
        try{
            BasicDBObject doc = new BasicDBObject("url", p.getURL());
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
