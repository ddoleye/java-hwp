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
package com.argo.hwp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.argo.hwp.v3.HwpTextExtractorV3;
import com.argo.hwp.v5.HwpTextExtractorV5;

public abstract class HwpTextExtractor {
	protected static Logger log = LoggerFactory.getLogger(HwpTextExtractor.class);

	public static boolean extract(File source, Writer writer)
			throws FileNotFoundException, IOException {
		if (source == null || writer == null)
			throw new IllegalArgumentException();
		if (!source.exists())
			throw new FileNotFoundException();

		// 먼저 V5 부터 시도
		boolean success = HwpTextExtractorV5.extractText(source, writer);

		// 아니라면 V3 시도
		if (!success)
			success = HwpTextExtractorV3.extractText(source, writer);

		return success;
	}
}
