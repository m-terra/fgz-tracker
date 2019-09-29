package com.baer.fgztracker;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by andy on 5/11/17
 */
public class ContentPreparer {


	String prepare(String content) {
		content = StringUtils.substringBetween(content, "Freie Objekte", "<script");
		content = StringUtils.remove(content, " ");
		content = StringUtils.remove(content, "\n");
		content = StringUtils.remove(content, "\r");
		return content;
	}

}
