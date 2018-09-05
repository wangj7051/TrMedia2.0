package com.lib.view.contacts;

import java.util.Comparator;

/**
 * PinyinComparator
 * 
 * @author Jun.Wang
 */
public class PinyinComparator implements Comparator<GroupMemberBean> {

	public int compare(GroupMemberBean gmb1, GroupMemberBean gmb2) {
		if (gmb1.getSortLetters().equals("@") || gmb2.getSortLetters().equals("#")) {
			return -1;
		} else if (gmb1.getSortLetters().equals("#") || gmb2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return gmb1.getSortLetters().compareTo(gmb2.getSortLetters());
		}
	}
}
