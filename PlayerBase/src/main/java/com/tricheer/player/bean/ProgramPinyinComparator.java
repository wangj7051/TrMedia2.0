package com.tricheer.player.bean;

import java.util.Comparator;

/**
 * Program PinYin Comparator
 * 
 * @author Jun.Wang
 */
public class ProgramPinyinComparator implements Comparator<Program> {

	@Override
	public int compare(Program lhs, Program rhs) {
		if (lhs.sortLetter.equals("@") || rhs.sortLetter.equals("#")) {
			return -1;
		} else if (lhs.sortLetter.equals("#") || rhs.sortLetter.equals("@")) {
			return 1;
		} else {
			return lhs.sortLetter.compareTo(rhs.sortLetter);
		}
	}
}
