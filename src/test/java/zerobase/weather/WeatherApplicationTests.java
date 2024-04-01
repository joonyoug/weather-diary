package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class WeatherApplicationTests {

	@Test
	void equalsTest(){
		assertEquals(1,1);
	}
	@Test
	void nullTest(){
		assertNull(null);

	}
	@Test
	void TrueTest(){
		assertTrue(true);
	}




}
