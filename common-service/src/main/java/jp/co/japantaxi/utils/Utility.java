package jp.co.japantaxi.utils;

import java.util.ArrayList;
import java.util.List;

public class Utility {

  public static <T> List<T> difference(List<T> list1, List<T> list2) {
    List<T> difference = new ArrayList<T>();
    if (list1.isEmpty()) {
      return new ArrayList<T>();
    } else if (list2.isEmpty()) {
      for (T object : list1) {
        difference.add(object);
      }
    } else {
      for (T object : list2) {
        if (!list1.contains(object)) {
          difference.add(object);
        }
      }
    }
    return difference;
  }

  public static <T> List<T> intersection(List<T> list1, List<T> list2) {
    List<T> intersection = new ArrayList<>();
    if (list1.isEmpty()) {
      return new ArrayList<T>();
    }
    if (list2.isEmpty()) {
      return difference(list1, list2);
    }
    for (T object : list1) {
      if (list2.contains(object)) {
        intersection.add(object);
      }
    }
    return intersection;
  }

  public static Float parseFloat(String value) {
    try {
      return Float.parseFloat(value);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  public static Integer parseInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  public static Double parseDouble(String value) {
	try {
	   return Double.parseDouble(value);
	} catch (NumberFormatException ex) {
	   return null;
	}
  }
  
  public static Boolean parseBoolean(String b) {
	if ("TRUE".equalsIgnoreCase(b.toUpperCase()) || "1".equalsIgnoreCase(b)) {
	  return true;
	} else {
	  return null;
	}
  }

  public static Long parseLong(String value) {
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  public static String parseString(String startTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("'");
    sb.append(startTime);
    sb.append("'");
    return sb.toString();
  }
}
