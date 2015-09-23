/*
   Copyright [2015] argonet.co.kr

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
/*
 * This software has been developed with reference to
 * the HWP file format open specification by Hancom, Inc.
 * http://www.hancom.co.kr/userofficedata.userofficedataList.do?menuFlag=3
 * 한글과컴퓨터의 한/글 문서 파일(.hwp) 공개 문서를 참고하여 개발하였습니다.
 * 
 * 본 제품은 다음의 소스를 참조하였습니다.
 * https://github.com/cogniti/ruby-hwp/
 */
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
		// File file = new File(path);
		File file = new File(getClass().getResource("/" + path).getFile());
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