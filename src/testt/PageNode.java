package testt;
import com.mongodb.DBObject;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Zhizhou
 */



public abstract class PageNode{
    static int serial = 0;
    String UniqueID;//url
    String title;//title of the page
    HashSet<PageNode> outgoings; 
    HashSet<PageNode> incomings;
    Boolean traversed;
    int ID;
    public String getURL(){
        return UniqueID;
    }
    public String getTitle(){
        return title;
    }
    public synchronized boolean isChecked(){
        return traversed;
    }
    public PageNode [] getOut(){
        return (PageNode [])outgoings.toArray();
    }
    public PageNode [] getIn(){
        return (PageNode [])incomings.toArray();
    }
    PageNode(String u){
        outgoings  = new HashSet();
        incomings  = new HashSet();
        UniqueID = u;
        traversed = false;
        ID = serial;
        serial++;
    }
    int getID(){
        return ID;
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
        catch(IOException e){
            System.out.println("can not open page");
            return result;
        }
        title = doc.title();
        title = title.split(" - ")[0];
        links.stream().forEach((l) -> {
            String cl = l.attr("class");
            if (!(!cl.equals("mw-redirect"))) {
                String ref = l.attr("href");
                if(ref.charAt(0) == '/'){
                    ref = BaseUrl + ref;
                }
                result.add(ref);
            }
        });
        traversed = true;
        return result;
    }


    
}