package jp.co.japantaxi.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UtilityTest {

	@TestConfiguration
	public static class UtilityTestConfiguration {
		@Bean
		Utility utility() {
			return new Utility();
		}
	}

	@Test
	public void test_difference() {
		List<String> list1 = new ArrayList<>();
		list1.add(0, "a");
		list1.add(1, "b");
		list1.add(2, "c");
		list1.add(3, "d");

		List<String> list2 = new ArrayList<>();
		list2.add(0, "b");
		list2.add(1, "c");
		list2.add(2, "e");

		List<String> list3 = new ArrayList<>();
		list3.add(0, "e");

		Assert.assertEquals(list3, Utility.difference(list1, list2));
		list1 = new ArrayList<>();
		Assert.assertEquals(list1, Utility.difference(list1, list2));
		list1.add(0, "a");
		list1.add(1, "b");
		list1.add(2, "c");
		list1.add(3, "d");
		list2 = new ArrayList<>();
		Assert.assertEquals(list1, Utility.difference(list1, list2));
	}

	@Test
	public void test_intersection() {
		List<String> list1 = new ArrayList<>();
		list1.add(0, "a");
		list1.add(1, "b");
		list1.add(2, "c");
		list1.add(3, "d");

		List<String> list2 = new ArrayList<>();
		list2.add(0, "b");
		list2.add(1, "c");
		list2.add(2, "e");

		List<String> list3 = new ArrayList<>();
		list3.add(0, "b");
		list3.add(1, "c");

		Assert.assertEquals(list3, Utility.intersection(list1, list2));
		list1 = new ArrayList<>();
		Assert.assertEquals(list1, Utility.intersection(list1, list2));
		list1.add(0, "a");
		list1.add(1, "b");
		list1.add(2, "c");
		list1.add(3, "d");
		list2 = new ArrayList<>();
		Assert.assertEquals(Utility.difference(list1, list2), Utility.intersection(list1, list2));

	}

	@Test
	public void test_parseFloat() {
		Assert.assertEquals(Float.valueOf(1F), Float.valueOf(Utility.parseFloat("1")));
	}

	@Test
	public void test_parseInt() {
		Assert.assertEquals(Integer.valueOf(1), Integer.valueOf(Utility.parseInt("1")));
	}

	@Test
	public void test_parseBoolean() {
		Assert.assertEquals(true, Utility.parseBoolean("True"));
	}

	@Test
	public void test_parseLong() {
		Assert.assertEquals(Long.valueOf(1), Long.valueOf(Utility.parseLong("1")));
	}

	@Test
	public void test_parseLong_return_null() {
		Assert.assertEquals(null, Utility.parseLong("str"));
	}
	
	@Test
	public void test_parseDouble() {
		Assert.assertEquals(Double.valueOf(1), Double.valueOf(Utility.parseFloat("1")));
	}

	@Test
	public void test_parseString() {
		Assert.assertEquals("'2020-02-15 17:50:01'", Utility.parseString("2020-02-15 17:50:01"));
	}

}
