package cn.happyz.happyphoto.DataProvider.DocumentNews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zcmzc on 13-11-18.
 */
public class DocumentNewsCollections {

    List<DocumentNews> list = new ArrayList<DocumentNews>();

    public void Add(DocumentNews documentNews){
        list.add(documentNews);
    }

    public void Remove(DocumentNews documentNews){
        list.remove(documentNews);
    }

    public void Clear(){
        list.clear();
    }

    public DocumentNews Get(int index){
        return list.get(index);
    }

    public int Count(){
        return list.toArray().length;
    }
}
