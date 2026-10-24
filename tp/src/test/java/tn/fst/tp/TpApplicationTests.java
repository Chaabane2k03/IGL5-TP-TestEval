package tn.fst.tp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TpApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testConstructor() {
		TpApplication app = new TpApplication();
		assertNotNull(app);
	}

	@Test
	void testMain() {
		assertDoesNotThrow(() -> TpApplication.main(new String[]{}));
	}

}

