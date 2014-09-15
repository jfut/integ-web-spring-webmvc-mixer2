package jp.integ.web.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Jun Futagawa
 */
public class MavenUtilTest {

	@Test
	public void testGetValue() {
		assertThat(MavenPomUtil.getValue(null, 0), is(nullValue()));

		assertThat(MavenPomUtil.getValue("unkown_name", 0), is(nullValue()));
		assertThat(MavenPomUtil.getValue("unkown_name", 1), is(nullValue()));

		assertThat(MavenPomUtil.getValue("name", 0), not(nullValue()));
		assertThat(MavenPomUtil.getValue("name", 1), not(nullValue()));

		assertThat(MavenPomUtil.getValue("version", 0), not(nullValue()));
		assertThat(MavenPomUtil.getValue("version", 1), not(nullValue()));
	}

}
