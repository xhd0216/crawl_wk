package testt;
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
    final String UniqueID;//url
    String title;//title of the page
    final HashSet<PageNode> outgoings; 
    final HashSet<PageNode> incomings;
    Boolean traversed;
    final int ID;
    public Boolean isOutLink(PageNode n){
        return this.outgoings.contains(n);
    }
    public Boolean isInLink(PageNode n){
        return this.incomings.contains(n);
    }
    public String getURL(){
        return UniqueID;
    }
    public String getTitle(){
        return title;
    }
    public synchronized void setChecked(){
        traversed = true;
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
    public int getID(){
        return ID;
    }
    public void insert(PageNode p, Boolean isOut){
        if(isOut)
            outgoings.add(p);
        else
            incomings.add(p);
    }
    public LinkedList<String> traverse(String BaseUrl){
        if(traversed) return null;
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
        title = doc.title().split(" - ")[0];
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
        /* make it true util it it written to database */
        //traversed = true;
        return result;
    }


    
}