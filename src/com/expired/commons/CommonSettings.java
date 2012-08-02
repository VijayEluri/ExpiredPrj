package com.expired.commons;

public class CommonSettings {

  // public static int list_item_preview_width = 0;
  // public static int list_item_preview_height = 0;

  public enum Sex {
    BOY, GIRL
  }

  
  public enum LIST_ITEM_PREVIEW_SIZE {
    xhdpiSize(128),
    hdpiSize(128),
    ldpiSize(96),
    mdpiSize(72);
    
    LIST_ITEM_PREVIEW_SIZE(int size){
    }
  }
  
  
  public static float xdpi, ydpi;

  public static float font_size_help_content = 18;

}
