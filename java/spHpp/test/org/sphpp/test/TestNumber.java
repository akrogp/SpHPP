package org.sphpp.test;

import java.text.ParseException;

import org.junit.Test;

import es.ehubio.Numbers;

public class TestNumber {

	@Test
	public void test() throws ParseException {
		System.out.println(Numbers.parseDouble("3.5e+03"));
	}

}
