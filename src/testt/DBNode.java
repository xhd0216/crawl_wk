/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testt;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 *
 * @author Zhizhou
 */
public class DBNode extends PageNode implements DBCompatible{
    BasicDBObject doc = null;  
    public DBNode(String u) {
        super(u);
    }
    /*public LinkedList<String> traverse(String BaseURL){
        LinkedList<String> res = super.traverse(BaseURL);
    }*/
    @Override
    public DBObject toDBO() {
        if(doc != null) return doc;
        //if(!this.isChecked()) return null;
        doc = new BasicDBObject();
        doc.append("url", this.getURL());
        doc.append("title", this.getTitle());
        doc.append("ID",this.getID());
        BasicDBList list1 = new BasicDBList();
        BasicDBList list2 = new BasicDBList();
        this.incomings.stream().forEach((r11) -> {
            list1.add(r11.getID());
        });
        this.outgoings.stream().forEach((r21) -> {
            list2.add(r21.getID());
        });
        doc.append("in", list1);
        doc.append("out", list2);
        return doc;
    }
    
}
