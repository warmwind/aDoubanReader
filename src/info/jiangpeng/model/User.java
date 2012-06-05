package info.jiangpeng.model;

import android.graphics.drawable.BitmapDrawable;

public class User {

    private String name;
    private String id;
    private BitmapDrawable imageDrawable;
    private String signature;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSignedIn(){
        return !(this instanceof NullUser);
    }

    public BitmapDrawable getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(BitmapDrawable imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }
}

