package com.baer.fgztracker;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by andy on 5/11/17
 */
public class ContentPreparerTest {

	private ContentPreparer sut = new ContentPreparer();

	@Test
	public void sameWithJsDiffs() throws IOException {
		String html1 = new String(Files.readAllBytes(
				Paths.get("app/src/test/resources/test1.html")), StandardCharsets.UTF_8);
		String html2 = new String(Files.readAllBytes(
				Paths.get("app/src/test/resources/test2.html")), StandardCharsets.UTF_8);
		Assert.assertFalse(html1.equals(html2));
		Assert.assertEquals(sut.prepare(html1), sut.prepare(html2));
	}

	@Test
	public void same() throws IOException {
		String html = new String(Files.readAllBytes(
				Paths.get("app/src/test/resources/test1.html")), StandardCharsets.UTF_8);
		Assert.assertEquals(sut.prepare(html), sut.prepare(html));
	}

	@Test
	public void removeNlWhitespace() throws IOException {
		String html = new String(Files.readAllBytes(
				Paths.get("app/src/test/resources/test1.html")), StandardCharsets.UTF_8);
		String prepared = sut.prepare(html);
		Assert.assertFalse(StringUtils.contains(prepared, " "));
		Assert.assertFalse(StringUtils.contains(prepared, "\n"));
	}

}
