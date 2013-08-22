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
		File file = new File(path);
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
		// Assert.assertEquals("", extract("empty-v3.hwp"));
		System.out.println(extract("4e00-62ff.hwp"));
		System.out.println(extract("한글 특수문자표 3.0.hwp"));
	}
}