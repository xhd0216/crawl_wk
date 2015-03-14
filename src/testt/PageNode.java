package testt;
import java.io.IOException;
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



public class PageNode{
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
    public synchronized boolean isChecked(){
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
                //String outlink = l.attr("title");
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