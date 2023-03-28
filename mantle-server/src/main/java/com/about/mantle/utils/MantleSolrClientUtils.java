package com.about.mantle.utils;

public class MantleSolrClientUtils {

	/**
	 * This method is copied from org.apache.solr.client.solrj.util.ClientUtils We
	 * only use following method from SOLR's client java. Having this here, makes us
	 * remove solr client jar altogether.
	 * 
	 * See: <a href=
	 * "https://www.google.com/?gws_rd=ssl#q=lucene+query+parser+syntax">Lucene
	 * query parser syntax</a> for more information on Escaping Special Characters
	 */
	public static String escapeQueryChars(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			// These characters are part of the query syntax and must be escaped
			if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^'
					|| c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?'
					|| c == '|' || c == '&' || c == ';' || c == '/' || Character.isWhitespace(c)) {
				sb.append('\\');
			}
			sb.append(c);
		}
		return sb.toString();
	}

}