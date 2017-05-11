package com.baer.fgztracker;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by andy on 5/11/17
 */
public class ContentAnalyzer {

	private static final CharSequence[] KEYWORDS = {"EFH", "amilienhaus"};

	boolean hasNewHouse(String prevContent, String newContent) {
		return !StringUtils.equals(prevContent, newContent)
				&& StringUtils.containsAny(newContent, KEYWORDS);
	}

}
