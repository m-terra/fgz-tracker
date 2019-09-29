package com.baer.fgztracker;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created by andy on 5/11/17
 */
public class ContentAnalyzerTest {

	private ContentAnalyzer sut = new ContentAnalyzer();
	private String _KEYWORKD_ = ContentAnalyzer.KEYWORDS[0].toString();

	@Test
	public void firstRun() {
		Assert.assertFalse(sut.hasNewHouse(null, "barfoo"));
	}

	@Test
	public void newHouse() {
		Assert.assertTrue(sut.hasNewHouse("foo", "bar" + _KEYWORKD_ + "foo"));
	}

	@Test
	public void sameHouse() {
		Assert.assertFalse(sut.hasNewHouse("foo" + _KEYWORKD_ + "Bar", "foo" + _KEYWORKD_ + "Bar"));
	}

	@Test
	public void differentHouse() {
		Assert.assertTrue(sut.hasNewHouse("foo" + _KEYWORKD_ + "Bar", "bar" + _KEYWORKD_ + "Foo"));
	}

	@Test
	public void houseGone() {
		Assert.assertFalse(sut.hasNewHouse("foo" + _KEYWORKD_ + "Bar", "foobar"));
	}

	@Test
	public void noHouse() {
		Assert.assertFalse(sut.hasNewHouse("foobar", "barfoo"));
	}

}
