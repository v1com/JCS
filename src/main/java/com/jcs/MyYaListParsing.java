package com.jcs;

import com.yandex.disk.client.ListItem;
import com.yandex.disk.client.ListParsingHandler;

/**
 * Created by homie on 21.12.2015.
 */
public class MyYaListParsing extends ListParsingHandler {

    @Override
    public boolean handleItem(ListItem item) {
        if(item.isVisible())
        {
            System.out.println(item.getDisplayName());
            return true;
        }
        return false;
    }
}
