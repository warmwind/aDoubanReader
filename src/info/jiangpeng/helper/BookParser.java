package info.jiangpeng.helper;

import info.jiangpeng.model.Book;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public interface BookParser {
    Book parse(JSONObject jsonBookObject) throws JSONException, IOException;
}
