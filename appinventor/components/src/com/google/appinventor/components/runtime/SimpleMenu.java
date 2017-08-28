// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

/**
 * Menu Component. A non visible component to provide the missing menu item
 * options for the App Inventor developers.
 * Currently supports the manual adding of various menu items from Designer block.
 *
 * TODO: Convert it to Visible component and provide drag and drop feature similar to Map component
 * @author vishwajeets912@gmail.com (Vishwajeet Srivastava)
 */


import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
  private boolean menuClick = true;
  private List<String> items = new ArrayList<String>();
  protected final ComponentContainer container;

  public SimpleMenu(ComponentContainer container) {
    super(container.$form());
    this.container = container;

    form.registerForOnCreateOptionsMenu(this);
    form.registerForOnPrepareOptionsMenu(this);

    simpleMenuItemTitle = DEFAULT_TITLE;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
    defaultValue = DEFAULT_TITLE)
  @SimpleProperty
  public void SimpleMenuItemTitle(String title) {
    simpleMenuItemTitle = title;
  }


  /**
   * Specifies the text elements of the SimpleMenu.
   * @param itemstring a string containing a comma-separated list of the strings to be picked from
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = "")
  @SimpleProperty(description="The TextView elements specified as a string with the " +
    "items separated by commas " +
    "such as: Cheese,Fruit,Bacon,Radish. Each word before the comma will be an element in the " +
    "list.",  category = PropertyCategory.BEHAVIOR)
  public void ElementsFromString(String itemstring) {
    items = elementsFromString(itemstring);
  }

  private List elementsFromString(String itemstring) {

     items = Arrays.asList(itemstring.split(" *, *"));

    return items;
  }


  @SimpleProperty()
  public boolean MenuIcon(){return menuClick;}

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "True")
  @SimpleProperty()
  public void MenuIcon(boolean click){menuClick = click;}

  @Override
  public void onCreateOptionsMenu(Menu menu) {
    MenuItem menuItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, simpleMenuItemTitle);
    menuItem.setOnMenuItemClickListener(this);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    menu.clear();
    MenuItem menuItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, simpleMenuItemTitle);

    for(String item: items){
      menu.add(item);
    }
    menuItem.setOnMenuItemClickListener(this);
  }

  @Override
  public void onDelete() {
  }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

/*  @Override
  public boolean onMenuItemClick(MenuItem item) {
    return MenuItemClick();

  }*/
}