package br.com.helpmecook.model;

/**
<<<<<<< HEAD
 * Created by Thais Torres on 16/04/2015.
 */
=======
 * Created by Thais Torres on 22/04/2015.
 */

>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231
public class NavDrawerItem {

    private String title;
    private int icon;
<<<<<<< HEAD
    private String count = "0";
    // boolean to set visiblity of the counter
    private boolean isCounterVisible = false;
=======
>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231

    public NavDrawerItem(){}

    public NavDrawerItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

<<<<<<< HEAD
    public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count){
        this.title = title;
        this.icon = icon;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }

=======
>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231
    public String getTitle(){
        return this.title;
    }

    public int getIcon(){
        return this.icon;
    }

<<<<<<< HEAD
    public String getCount(){
        return this.count;
    }

    public boolean getCounterVisibility(){
        return this.isCounterVisible;
    }

=======
>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231
    public void setTitle(String title){
        this.title = title;
    }

    public void setIcon(int icon){
        this.icon = icon;
    }
<<<<<<< HEAD

    public void setCount(String count){
        this.count = count;
    }

    public void setCounterVisibility(boolean isCounterVisible){
        this.isCounterVisible = isCounterVisible;
    }
=======
>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231
}
