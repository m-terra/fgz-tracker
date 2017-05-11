package com.baer.fgztracker;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by andy on 5/11/17
 */
public class ContentAnalyzerTest {

	private ContentAnalyzer sut = new ContentAnalyzer();

	@Test
	public void newHouse() {
		Assert.assertTrue(sut.hasNewHouse("foo", "barEFHfoo"));
	}

	@Test
	public void sameHouse() {
		Assert.assertFalse(sut.hasNewHouse("fooHausBar", "fooHausBar"));
	}

	@Test
	public void differentHouse() {
		Assert.assertTrue(sut.hasNewHouse("fooHausBar", "barHausFoo"));
	}

	@Test
	public void houseGone() {
		Assert.assertFalse(sut.hasNewHouse("fooHausBar", "foobar"));
	}

	@Test
	public void noHouse() {
		Assert.assertFalse(sut.hasNewHouse("foobar", "barfoo"));
	}

}
