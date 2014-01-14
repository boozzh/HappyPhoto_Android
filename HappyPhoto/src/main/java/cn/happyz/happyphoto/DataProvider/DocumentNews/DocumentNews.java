package cn.happyz.happyphoto.DataProvider.DocumentNews;

/**
 * Created by zcmzc on 13-11-18.
 */
public class DocumentNews {
    private int DocumentNewsId;
    private String DocumentNewsTitle;
    private String TitlePic;

    public DocumentNews(int documentNewsId,String documentNewsTitle,String titlePic){
        super();
        DocumentNewsId = documentNewsId;
        DocumentNewsTitle = documentNewsTitle;
        TitlePic = titlePic;
    }

    public int getDocumentNewsId(){
        return DocumentNewsId;
    }

    public String getDocumentNewsTitle(){
        return DocumentNewsTitle;
    }

    public String getTitlePic(){
        return TitlePic;
    }
}
