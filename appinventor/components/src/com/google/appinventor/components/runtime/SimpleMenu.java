package com.google.appinventor.components.runtime;

/**
 * Created by vjs3 on 27/07/17.
 */


import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;

@DesignerComponent(version = YaVersion.SIMPLEMENU_COMPONENT_VERSION,
  description = "your option menu item",
  category = ComponentCategory.USERINTERFACE,
  nonVisible = true,
  iconName = "images/simpleMenu.png")
@SimpleObject
public final class SimpleMenu extends AndroidNonvisibleComponent
  implements OnCreateOptionsMenuListener, OnPrepareOptionsMenuListener, Deleteable, OnMenuItemClickListener, Component {

  private String simpleMenuItemTitle;

  static final String DEFAULT_TITLE = "Menu Item";

  public SimpleMenu(ComponentContainer container) {
    super(container.$form());

    form.registerForOnCreateOptionsMenu(this);
    form.registerForOnPrepareOptionsMenu(this);

    simpleMenuItemTitle = DEFAULT_TITLE;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
    defaultValue = DEFAULT_TITLE)
  @SimpleProperty
  public void setSimpleMenuItemTitle(String title) {
    simpleMenuItemTitle = title;
  }

  @SimpleEvent(description = "")
  public boolean MenuItemClick() {
    return EventDispatcher.dispatchEvent(this, "OptionsMenuItem");
  }

  @Override
  public void onCreateOptionsMenu(Menu menu) {
    MenuItem menuItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, simpleMenuItemTitle);
    menuItem.setOnMenuItemClickListener(this);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    MenuItem menuItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, simpleMenuItemTitle);
    menuItem.setOnMenuItemClickListener(this);
  }

  @Override
  public void onDelete() {
  }

    /*@Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }*/

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    return MenuItemClick();

  }
}