package br.ufrn.arq.tests;

import static br.ufrn.arq.util.ValidatorUtil.isAllEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isAllNotEmpty;

import java.util.Date;

import junit.framework.TestCase;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Testes para classe ValidatorUtil
 * 
 * @see ValidatorUtil
 * @author Henrique André
 * 
 */

public class ValidatorUtilTest extends TestCase {

	public void testIsAllNotEmpty() throws Exception {

		assertEquals(false, isAllNotEmpty(null));
		assertEquals(false, isAllNotEmpty(null, null));
		assertEquals(false, isAllNotEmpty(null, "UFRN"));
		assertEquals(false, isAllNotEmpty(""));
		assertEquals(false, isAllNotEmpty("", "  "));
		assertEquals(false, isAllNotEmpty("UFRN", null));

		assertEquals(true, isAllNotEmpty("UFRN"));
		assertEquals(true, isAllNotEmpty("UFRN", new Date()));
	}

	public void testIsAllEmpty() throws Exception {
		assertEquals(false, isAllEmpty("UFRN"));
		assertEquals(false, isAllEmpty("UFRN", new Date()));

		assertEquals(true, isAllEmpty(null));
		assertEquals(true, isAllEmpty(null, null));
		assertEquals(true, isAllEmpty(""));
		assertEquals(true, isAllEmpty("", "  "));

	}

}
