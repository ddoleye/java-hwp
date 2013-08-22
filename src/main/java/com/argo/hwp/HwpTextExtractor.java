/*
 * Copyright (C) 2013 argonet.co.kr <ddoleye@gmail.com>
 * 
 * This library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * 본 제품은 한글과컴퓨터의 한글 문서 파일(.hwp) 공개 문서를 참고하여 개발하였습니다.
 * 
 * 본 제품은 다음의 소스를 참조하였습니다.
 * https://github.com/cogniti/ruby-hwp/
 * https://github.com/cogniti/libghwp/
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
