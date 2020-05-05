package jp.co.japantaxi.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
public class JsonMapperTest {

	@TestConfiguration
	public static class JsonMapperTestConfiguration {
		@Bean
		JsonMapper mapper() {
			return new JsonMapper();
		}
	}

	@Test
	public void test_toMap() {
		Map<String, String> elements = new HashMap<String, String>();
		elements.put("Key1", "Value1");
		elements.put("Key2", "Value2");
		elements.put("Key3", "Value3");

		JSONObject object = new JSONObject();
		object.put("Key1", "Value1");
		object.put("Key2", "Value2");
		object.put("Key3", "Value3");
		Assert.assertEquals(elements, JsonMapper.toMap(object));
	}

	@Test
	public void test_toList() {
		ObjectMapper mapper = JsonMapper.newMapper();

		String data = "[{\"userName\": \"sandeep\",\"age\":30},{\"userName\": \"vivan\",\"age\":5}]  ";
		JSONArray jsonArr = new JSONArray(data);

		List<Object> list = new ArrayList<Object>();
		JSONObject object = new JSONObject();
		object.put("userName", "sandeep");
		list.add(object);
		Assert.assertNotNull(mapper);
		Assert.assertNotEquals(list, JsonMapper.toList(jsonArr));
	}

}
