package cn.happyz.happyphoto.DataProvider.DocumentNews;

/**
 * Created by zcmzc on 13-11-18.
 */
public class DocumentNews {
    private int _documentNewsId;
    private String _documentNewsTitle;
    private String _titlePic;

    public DocumentNews(int documentNewsId,String documentNewsTitle,String titlePic){
        super();
        _documentNewsId = documentNewsId;
        _documentNewsTitle = documentNewsTitle;
        _titlePic = titlePic;
    }

    public int get_documentNewsId(){
        return _documentNewsId;
    }

    public String getDocumentNewsTitle(){
        return _documentNewsTitle;
    }

    public String getTitlePic(){
        return _titlePic;
    }
}
