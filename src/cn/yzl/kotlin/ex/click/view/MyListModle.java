package cn.yzl.kotlin.ex.click.view;


import cn.yzl.kotlin.ex.click.model.Element;

import javax.swing.*;
import java.util.List;

/**
 * Created by YZL on 2017/8/15.
 */
public class MyListModle extends DefaultListModel<Element> {
    List<Element> data;

    public MyListModle(List<Element> bean) {
        this.data = bean;
        for (int i = 0; i < data.size(); i++) {
            addElement(data.get(i));
        }
    }

    @Override
    public int getSize() {
        return data.size();
    }
}
