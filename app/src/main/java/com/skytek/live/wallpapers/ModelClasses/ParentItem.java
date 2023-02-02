package com.skytek.live.wallpapers.ModelClasses;
import java.util.List;

public class ParentItem {

    // Declaration of the variables
    private String id;
    private String ParentItemTitle;
    private List<ModelClass> ChildItemList;

    // Constructor of the class
    // to initialize the variables
    public ParentItem(String id,String ParentItemTitle, List<ModelClass> ChildItemList)
    {

        this.ParentItemTitle = ParentItemTitle;
        this.ChildItemList = ChildItemList;
        this.id=id;
    }

    // Getter and Setter methods
    // for each parameter
    public String getParentItemTitle()
    {
        return ParentItemTitle;
    }

    public void setParentItemTitle(
            String parentItemTitle)
    {
        ParentItemTitle = parentItemTitle;
    }

    public List<ModelClass> getChildItemList()
    {
        return ChildItemList;
    }

    public void setChildItemList(
            List<ModelClass> childItemList)
    {
        ChildItemList = childItemList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}