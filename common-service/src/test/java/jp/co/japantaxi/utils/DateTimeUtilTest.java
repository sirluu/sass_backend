package jp.co.japantaxi.utils;

import java.sql.Timestamp;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DateTimeUtilTest {
  private String fromDateString = "2020-01-21 11:26:52";
  private String toDateString = "2020-01-22 11:26:52";
  private Date fromDate = DateTimeUtil.getDateFromString(fromDateString, DateTimeUtil.DATE_TIME_FM);
  private Date toDate = DateTimeUtil.getDateFromString(toDateString, DateTimeUtil.DATE_TIME_FM);
  private Timestamp fromTimestamp =
      DateTimeUtil.getTimestampFromString(fromDateString, DateTimeUtil.DATE_TIME_FM);
  private Timestamp toTimestamp =
      DateTimeUtil.getTimestampFromString(toDateString, DateTimeUtil.DATE_TIME_FM);

  @TestConfiguration
  public static class DateTimeUtilTestConfiguration {
    @Bean
    DateTimeUtil dtUtil() {
      return new DateTimeUtil();
    }
  }

  @Test
  public void test_getDateFromString_withValueIsEmpty() {
    Assert.assertEquals(DateTimeUtil.getDateFromString("", DateTimeUtil.DATE_TIME_FM), null);
  }

  @Test
  public void test_getDateFromString_withValue() {
    Assert.assertEquals(DateTimeUtil.getDateFromString(fromDateString, DateTimeUtil.DATE_TIME_FM),
        fromDate);
  }

  @Test
  public void test_getDateFromString_withValue_returnException() {
    Assert.assertNotEquals(
        DateTimeUtil.getDateFromString(fromDateString, DateTimeUtil.DATE_TIME_FM), toDate);
  }

  @Test
  public void test_getDateFromStringWithTimeZone_withValueIsEmpty() {
    Assert.assertEquals(DateTimeUtil.getDateFromString("", DateTimeUtil.DATE_TIME_FM), null);
  }

  @Test
  public void test_getDateFromStringWithTimeZone_withValue() {
    Assert.assertNotEquals(
        DateTimeUtil.getDateFromString(fromDateString, DateTimeUtil.DATE_TIME_FM), fromDate);
  }

  @Test
  public void test_getDateFromStringWithTimeZone_returnException() {
    Assert.assertNotEquals(
        DateTimeUtil.getDateFromString(fromDateString, DateTimeUtil.DATE_TIME_FM), toDate);
  }

  @Test
  public void test_getStringFromDate_withValue() {
    Assert.assertEquals(DateTimeUtil.getStringFromDate(fromDate, DateTimeUtil.DATE_TIME_FM),
        fromDateString);
  }

  @Test
  public void test_getStringFromTimestamp_withValue() {
    Assert.assertEquals(DateTimeUtil.getStringFromTimestamp(
        DateTimeUtil.getTimestampFromString(fromDateString, DateTimeUtil.DATE_TIME_FM),
        DateTimeUtil.DATE_TIME_FM), fromDateString);
  }

  @Test
  public void test_getTimestampFromString_withValueIsEmpty() {
    Assert.assertEquals(DateTimeUtil.getTimestampFromString("", DateTimeUtil.DATE_TIME_FM), null);
  }

  @Test
  public void test_getTimestampFromString_returnParseException() {
    Assert.assertEquals(DateTimeUtil.getTimestampFromString("123", DateTimeUtil.DATE_TIME_FM),
        null);
  }

  @Test
  public void test_getTimestampFromString_withValue() {
    Assert.assertEquals(
        DateTimeUtil.getTimestampFromString(fromDateString, DateTimeUtil.DATE_TIME_FM),
        fromTimestamp);
  }

  @Test
  public void test_getTimestampFromString_returnException() {
    Assert.assertNotEquals(
        DateTimeUtil.getTimestampFromString(fromDateString, DateTimeUtil.DATE_TIME_FM),
        toTimestamp);
  }

  @Test
  public void test_isValid() {
    Assert.assertEquals(DateTimeUtil.isValid(""), false);
    Assert.assertEquals(DateTimeUtil.isValid(fromDateString), true);
  }
}
