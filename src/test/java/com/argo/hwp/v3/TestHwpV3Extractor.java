package com.argo.hwp.v3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.StringTokenizer;

import org.junit.Test;

public class TestHwpV3Extractor {
	private String extract(String path) throws FileNotFoundException,
			IOException {
//		File file = new File(path);
		File file = new File(getClass().getResource("/"+path).getFile());
		// System.out.println(file.getAbsolutePath());
		StringWriter writer = new StringWriter(4096);
		HwpTextExtractorV3.extractText(file, writer);
		return writer.toString();
	}

	private String tokenize(String t) {
		StringWriter writer = new StringWriter(4096);
		StringTokenizer token = new StringTokenizer(t);
		while (token.hasMoreTokens()) {
			writer.append(token.nextToken()).append("\n");
		}
		return writer.toString();
	}

	private String extractIgnoreException(String path) {
		try {
			return extract(path);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	@Test
	public void testCharConversion() {
		// 조합형
		System.out.println(Hnc2String.convert(Integer.parseInt(
				"1000010001100010", 2)));
	}

	@Test
	public void testExtractText() throws IOException, ClassNotFoundException {
		System.out.println(extract("v3/4e00-62ff.hwp"));
		System.out.println(extract("v3/han_special_char_3.0.hwp"));
	}
}