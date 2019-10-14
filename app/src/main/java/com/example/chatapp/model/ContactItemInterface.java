package com.example.chatapp.model;

import java.io.Serializable;

public interface ContactItemInterface extends Serializable {

    public String getItemForIndex();   // return the item_codec_view that we want to categorize under this index. It can be first_name or last_name or display_name
    //  e.g. "Albert Tan" , "Amy Green" , "Alex Ferguson" will fall under index A
    // "Ben Alpha", "Ben Beta" will fall under index B

    public String getNumber();

    public String getPhoto();
}
