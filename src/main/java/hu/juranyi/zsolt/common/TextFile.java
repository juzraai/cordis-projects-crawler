package hu.juranyi.zsolt.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author Zsolt Juranyi
 */
public class TextFile {

	private final String fileName;
	private String content;

	public TextFile(String fileName) {
		this.fileName = fileName;
	}

	public boolean load() {
		boolean result = true;
		BufferedReader r = null;
		StringBuilder sb = new StringBuilder();
		try {
			r = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = r.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			content = sb.toString();
		} catch (IOException ex) {
			result = false;
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (IOException ex) {
				}
			}
		}
		return result;
	}

	public boolean save() {
		boolean result = true;
		BufferedWriter w = null;
		try {
			w = new BufferedWriter(new FileWriter(fileName));
			w.write(content);
		} catch (IOException ex) {
			result = false;
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException ex) {
				}
			}
		}
		return result;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
