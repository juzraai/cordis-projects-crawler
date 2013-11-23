package hu.juranyi.zsolt.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Jurányi Zsolt
 */
public class StringTools {

	/**
	 * Finds the first matching part of a pattern in a string and returns the
	 * specified group.
	 * 
	 * @param in
	 *            Input text.
	 * @param pattern
	 *            Pattern.
	 * @param group
	 *            Needed group.
	 * @return Specified group of the matching part or null if there's no match
	 *         or if one of the first two parameters is null or empty, or group
	 *         is negative.
	 */
	public static String findFirstMatch(String in, String pattern, int group) {
		if (null == in || null == pattern || 0 == in.length()
				|| 0 == pattern.length() || group < 0) {
			return null;
		}
		Pattern p = Pattern.compile(pattern, Pattern.DOTALL
				| Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(in);
		return (m.find()) ? m.group(group) : null;
	}

	/**
	 * Finds all matching part of a pattern in a string, then returns their one
	 * group in a list.
	 * 
	 * @param in
	 *            Input text.
	 * @param pattern
	 *            Pattern.
	 * @param group
	 *            Needed group.
	 * @return List of the matching parts specified group or null if one of the
	 *         first two parameters is null or empty, or group is negative.
	 */
	public static List<String> findAllMatch(String in, String pattern, int group) {
		if (null == in || null == pattern || 0 == in.length()
				|| 0 == pattern.length() || group < 0) {
			return null;
		}
		Pattern p = Pattern.compile(pattern, Pattern.DOTALL
				| Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(in);
		ArrayList<String> a = new ArrayList<String>();
		while (m.find()) {
			a.add(m.group(group));
		}
		return a;
	}
}
