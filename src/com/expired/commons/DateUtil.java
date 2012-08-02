package com.expired.commons;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;

public class DateUtil {
  private static final SimpleDateFormat DATE_FORMAT =
    new SimpleDateFormat("yyyy-MM-dd");
  
  private static final SimpleDateFormat DATE_TIME_FORMAT = 
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  private static final SimpleDateFormat TIME_FORMAT = 
    new SimpleDateFormat("HH:mm");
  
  private static final DecimalFormat TWO_DIGITAL_FORMAT = 
    new DecimalFormat("00");
  
  private static final DecimalFormat FOUR_DIGITAL_FORMAT = 
    new DecimalFormat("0000");
  
  private static final String EMAIL_FORMAT =
    "^[a-zA-Z0-9\\+_\\-\\.]+@[0-9a-zA-Z][\\.\\-0-9a-zA-Z]*\\.[a-zA-Z]+$";
  
  private static final String UNAME_FORMAT = 
    "^[a-zA-Z0-9一-龥][a-zA-Z0-9一-龥_\\.]*$";
  
  private static final String NUMBER_FORMAT = "^\\d{5,13}$";
  
  /**
   * @author chronoer
   * @param dateString
   * @return timeAgo
   */
  public static String toRelativeDateTime(String dateString) {
    if(dateString == null){
      return "";
    }
    
    try {
      Date date = DATE_TIME_FORMAT.parse(dateString);
      Date now = new Date();
      // Seconds.
      long diff = (now.getTime() - date.getTime()) / 1000;
      
      if (diff < 0) {
        return "1m";
      }

      if (diff < 60) {
        return "1m";
      }

      // Minutes.
      diff /= 60;
   
      if (diff < 60) {
        return diff + "m";
      }

      // Hours.
      diff /= 60;
      
      if (diff < 24) {
        return diff + "h";
      }

      // days
      
      diff /=24;
      if(diff < 8) {
        return diff + "d";
      }
      
      return dateString;
      
    } catch (ParseException e) {
      return dateString;
    }
  }
  
  public static String toRelativeDateTimeNew(String dateString) {
    if(dateString == null){
      return "";
    }
    
    try {
      Date date = DATE_TIME_FORMAT.parse(dateString);
      Date now = new Date();
      // Seconds.
      long diff = (now.getTime() - date.getTime()) / 1000;
      
      if (diff < 0) {
        return "1m";
      }

      if (diff < 60) {
        return "1m";
      }

      // Minutes.
      diff /= 60;
   
      if (diff < 60) {
        return diff + "m";
      }

      // Hours.
      diff /= 60;
      
      if (diff < 24) {
        return diff + "h";
      }
      
      return dateString;
      
    } catch (ParseException e) {
      return dateString;
    }
  }
  
  /**
   * 
   * @param dateString
   * @param zoneOffset zone offset from GMT in hours
   * @return
   */
  public static String toLocalDateTimeString(String dateString, int zoneOffset){
    String result = dateString;
    
    try {
      if(TextUtils.isEmpty(result)==false){
        
        Calendar calendar = Calendar.getInstance();
        int sytemZoneOffset = calendar.get(Calendar.ZONE_OFFSET);//milliseconds
        
        Date date = DATE_TIME_FORMAT.parse(dateString);
        date.setTime(date.getTime()-(zoneOffset*60*60*1000)+sytemZoneOffset);
        result = DATE_TIME_FORMAT.format(date);
      }
    } catch (ParseException e) {
      //don't do anything just return dateString.
    }
    
    return result;
  }
  
  public static String toJiepangDateString(Date date){
    return TIME_FORMAT.format(date);
  }
  
  public static String toDateString(Date date){
    return DATE_FORMAT.format(date);
  }
    
  /**
   * parse date string to Calendar
   * @param dateString date string (yyyy-MM-dd)
   * @return the Calendar
   */
  public static Calendar parseDateString(String dateString){
    Calendar c = Calendar.getInstance();
    try {
      if(dateString != null && dateString.length()>0){
        c.setTime(DATE_FORMAT.parse(dateString));
      }
    } catch (ParseException e) {
      //don't do anything just return Calendar.
    }
    
    return c;
  }
  
  /**
   * to date string(yyyy-MM-dd)
   * @param year year
   * @param month month
   * @param date date
   * @return date string(yyyy-MM-dd)
   */
  public static String toDateString(int year, int month, int date){
    return FOUR_DIGITAL_FORMAT.format(year)
      + "-"+TWO_DIGITAL_FORMAT.format(month)
      + "-"+TWO_DIGITAL_FORMAT.format(date);
  }
  
  public static int getDataSequence(String[] valueSource, String value){
    int seq = 0;
    
    for(int i=0;i<valueSource.length;i++){
      if(valueSource[i].equals(value)){
        seq = i;
        break;
      }
    }
    
    return seq;
  }
  
  /**
   * parse dataString to long
   * @param dateString
   * @return
   */
  public static long toLong(String dateString){
    if(dateString == null){
      return 0;
    }
    Date date;
    try {
      date = DATE_TIME_FORMAT.parse(dateString);
    } catch (ParseException e) {
      return 0;
    }
    return date.getTime();  
  }
  
  /**
   * check the email is valid or not
   * @param email email string
   * @return the email string is valid or not
   */
  public static boolean validateEmail(String email){
    return email.matches(EMAIL_FORMAT);
  }
  
  public static boolean validateUname(String uname){
    return uname.matches(UNAME_FORMAT);
  }
  
  public static boolean validateNumber(String number){
    return number.matches(NUMBER_FORMAT);
  }
  
  /**
   * 取得兩點的距離
   * @param originLatitude
   * @param originLongitude
   * @param destinationLatitude
   * @param destinationLongitude
   * @return m
   */
  public static int getDistance(
      double originLatitude, double originLongitude, 
      double destinationLatitude, double destinationLongitude){
    double originLatR = Math.toRadians(originLatitude);
    double originLongR = Math.toRadians(originLongitude);
    
    double destLatR = Math.toRadians(destinationLatitude);
    double destLongR = Math.toRadians(destinationLongitude);

    double radius = 6378137; // Earth's radius (m)
    int distance =
      new Double(
        Math.acos(
            Math.sin(originLatR) * Math.sin(destLatR) + 
            Math.cos(originLatR) * Math.cos(destLatR) * Math.cos(destLongR-originLongR)
            ) * radius).intValue();

    return distance; //m
  }
  
  public static String getKilometer(int meter){
    return (meter/1000)+"."+(meter%1000/100);
  }
  
  public static String getAlternativeString(String original, String alternative){
    return TextUtils.isEmpty(original)?alternative:original;
  }
  
  /*
   * file size unit
   */
  public static String formatSize(float  size)   
  {   
    long  kb = 1024;   
    long  mb = (kb * 1024);   
    long  gb  = (mb * 1024);   
    if  (size < kb) {   
       return  String.format( "%d B" , ( int ) size);   
    }   
    else   if  (size < mb) {   
       return  String.format( "%.2f KB" , size / kb);
    }   
    else   if  (size < gb) {   
       return  String.format( "%.2f MB" , size / mb);   
    }   
    else  {   
       return  String.format( "%.2f GB" , size / gb);   
    }   
  }  
  
  public static boolean compareStrings(String s1, String s2){
    if(s1 == null && s2 == null){
      return true;
    }
    else if(s1 != null && s2 != null){
      return s1.equals(s2);
    }
    else{
      return false;
    }
  }
  
  public static int calculateUnameLength(String uName){
    int total = 0;
    for(int i=0;i<uName.length();i++){
      String a = uName.substring(i, i+1);
      if(a.getBytes().length > 1){
        total += 2;
      }
      else{
        total++;
      }
    }
    return total;
  }
  
  public static String cutUnameByLength(String uName, int length){
    int total = 0;
    for(int i=0;i<uName.length();i++){
      String a = uName.substring(i, i+1);
      if(a.getBytes().length > 1){
        total += 2;
      }
      else{
        total++;
      }
      if(total > length){
        return uName.substring(0,i);
      }
    }
    return null;
  }
}
