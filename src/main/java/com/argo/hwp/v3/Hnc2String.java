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
/*
 * 이 소스는 https://github.com/cogniti/libghwp/blob/master/src/hnc2unicode.inc 을 사용합니다.
 */
package com.argo.hwp.v3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Hnc2String {
	static Logger log = LoggerFactory.getLogger(Hnc2String.class);

	static final int NONE = 0xfd;
	static final int FILL = 0xff;

	/* HNC 조합형 11172 음절 --> 유니코드 변환용 표 */
	static final short L_MAP[] = { NONE, NONE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
			10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, NONE, NONE, NONE, NONE,
			NONE, NONE, NONE, NONE, NONE };

	static final short V_MAP[] = { NONE, NONE, NONE, 0, 1, 2, 3, 4, NONE, NONE,
			5, 6, 7, 8, 9, 10, NONE, NONE, 11, 12, 13, 14, 15, 16, NONE, NONE,
			17, 18, 19, 20, NONE, NONE };

	static final short T_MAP[] = { NONE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
			12, 13, 14, 15, 16, NONE, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
			27, NONE, NONE };

	/* HNC 자모 변환용 표 */
	static final char HNC_L1[] = { 0x3172, FILL, 0x3131, 0x3132, 0x3134,
			0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146,
			0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e,
			0x3173, 0x3144, 0x3176, 0x3178, 0x317a, 0x317c, 0x317d, 0x317e,
			0x317f, 0x3181, 0x3185 };

	static final char HNC_V1[] = { NONE, NONE, FILL, 0x314f, 0x3150, 0x3151,
			0x3152, 0x3153, NONE, NONE, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158,
			0x3159, NONE, 0x3189, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e,
			0x315f, 0x318a, 0x318c, 0x3160, 0x3161, 0x3162, 0x3163, 0x318d,
			0x318e };

	static final char HNC_T1[] = { 0x316d, FILL, 0x3131, 0x3132, 0x3133,
			0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c,
			0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3178, 0x3142, 0x3144,
			0x3145, 0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d,
			0x314e, 0x317f, 0x3181 };

	/* HNC 옛한글 조합용 표 */
	static final char HNC_L2[] = { 0x111e, 0x115f, 0x1100, 0x1101, 0x1102,
			0x1103, 0x1104, 0x1105, 0x1106, 0x1107, 0x1108, 0x1109, 0x110a,
			0x110b, 0x110c, 0x110d, 0x110e, 0x110f, 0x1110, 0x1111, 0x1112,
			0x1120, 0x1121, 0x1127, 0x112b, 0x112d, 0x112f, 0x1132, 0x1136,
			0x1140, 0x114c, 0x1158 };

	static final char HNC_V2[] = { NONE, NONE, 0x1160, 0x1161, 0x1162, 0x1163,
			0x1164, 0x1165, NONE, NONE, 0x1166, 0x1167, 0x1168, 0x1169, 0x116a,
			0x116b, NONE, 0x1188, 0x116c, 0x116d, 0x116e, 0x116f, 0x1170,
			0x1171, 0x1191, 0x1194, 0x1172, 0x1173, 0x1174, 0x1175, 0x119e,
			0x11a1 };

	static final char HNC_T2[] = { 0x11d9, NONE, 0x11a8, 0x11a9, 0x11aa,
			0x11ab, 0x11ac, 0x11ad, 0x11ae, 0x11af, 0x11b0, 0x11b1, 0x11b2,
			0x11b3, 0x11b4, 0x11b5, 0x11b6, 0x11b7, 0x11e6, 0x11b8, 0x11b9,
			0x11ba, 0x11bb, 0x11bc, 0x11bd, 0x11be, 0x11bf, 0x11c0, 0x11c1,
			0x11c2, 0x11eb, 0x11f0 };

	// /* 르ᇝ */
	// static final String C_BC1F = new String(
	// new char[] { 0x1105, 0x1173, 0x11dd });
	// /* 아ᇇ */
	// static final String C_D802 = new String(
	// new char[] { 0x110b, 0x1161, 0x11c7 });

	// static final String[] ksc5601_2uni_page4a = new String[4888];
	static final String[] map = new String[0x8000];
	static {
		// hnc2unicode.inc 에서 매핑 테이블 ksc5601_2uni_page4a와 hnc2uni_map 읽기
		// java 소스에 넣었더니 이클립스가 죽는다

		// 읽기는 하드코딩

		// hnc2unicode.inc 파일에서 매핑 데이터를 읽기 위해 패턴 사용
		Pattern UINT16MAP = Pattern.compile("0x([0-9a-f]{4})",
				Pattern.CASE_INSENSITIVE);
		Pattern UINT32MAP = Pattern.compile(
				"\\[0x([0-9a-f]{2,4})\\] = 0x([0-9a-f]{2,6})",
				Pattern.CASE_INSENSITIVE);

		char[] chars = new char[2]; // 코드변환용 임시 버퍼
		int status = 0; // 1 일 경우 ksc5601_2uni_page4a, 2일 경우 hnc2uni_map
		int count = 0;
		int lineNumber = 0; // 디버그

		InputStream resource = Hnc2String.class
				.getResourceAsStream("hnc2unicode.inc");

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					resource, "UTF-8"));

			for (;;) {
				String line = reader.readLine();
				if (line == null)
					break;

				lineNumber++;
				line = line.trim();
				if (line.length() == 0)
					continue;

				if (status == 1) {
					if ("};".equals(line)) {
						status = 0;
						log.debug("ksc5601_2uni_page4a 매핑 읽기 종료({}) {}건",
								lineNumber, count);
					} else {
						Matcher matcher = UINT16MAP.matcher(line);
						while (matcher.find()) {
							int code = Integer.parseInt(matcher.group(1), 16);
							// BMP(0x0000~0xFFFF)가 아닌 Supplementary Character
							// (0xFFFF 이상) 일 경우는 두개의 char(Surrogate pair)로 처리된다
							int len = Character.toChars(code, chars, 0);
							int index = 0x4000 + count;
							if (map[index] != null)
								log.warn("중복 코드 ? [{}]={}", index, map[index]);
							map[index] = new String(chars, 0, len);
							count++;
						}
					}
				} else if (status == 2) {
					if ("};".equals(line)) {
						status = 0;
						log.debug("hnc2uni_map 매핑 읽기 종료({}) {}건", lineNumber,
								count);
					} else {
						Matcher matcher = UINT32MAP.matcher(line);
						if (matcher.find()) {
							int code = Integer.parseInt(matcher.group(2), 16);
							// BMP(0x0000~0xFFFF)가 아닌 Supplementary Character
							// (0xFFFF 이상) 일 경우는 두개의 char로 처리된다
							int len = Character.toChars(code, chars, 0);
							int index = Integer.parseInt(matcher.group(1), 16);
							if (map[index] != null)
								log.warn("중복 코드 ? [{}]={}", index, map[index]);
							map[index] = new String(chars, 0, len);
							count++;
						} else {
							log.warn("라인 확인({})[{}]", lineNumber, line);
						}
					}
				} else if (line.indexOf("ksc5601_2uni_page4a") > 0) {
					// 지금부터 ksc5601_2uni_page4a 매핑이 시작됨
					status = 1;
					count = 0;
					log.trace("ksc5601_2uni_page4a 매핑 읽기 시작({})", lineNumber);
				} else if (line.indexOf("hnc2uni_map") > 0) {
					// 지금부터 hnc2uni_map 매핑이 시작됨
					status = 2;
					count = 0;
					log.trace("hnc2uni_map 매핑 읽기 시작({})", lineNumber);
				} else {
					// 무시
				}
			}

		} catch (IOException e) {
			log.error("hnc2unicode.inc 파일 읽기 실패", e);
			System.exit(-1);
		} finally {
			try {
				resource.close();
			} catch (IOException e) {
				log.warn("Ignore", e);
			}
		}
	}

	static String convert(int c) {
		String s = null;
		if (c >= 0x0020 && c <= 0x007e) {
			s = Character.toString((char) c);
			log.trace("ASCII printable (c >= 0x0020 && c <= 0x007e) {} => {}",
					c, s);
		} else if (c >= 0x007f && c <= 0x3fff) {
			s = map[c];
			log.trace("map (c >= 0x007f && c <= 0x3fff) {} => {}", c, s);
		} else if (c >= 0x4000 && c <= 0x5317) {
			s = map[c];
			log.trace("1수준 한자 (c >= 0x4000 && c <= 0x5317) {} => {}", c, s);
		} else if (c >= 0x5318 && c <= 0x7fff) {
			s = map[c];
			log.trace("2수준 한자 (c >= 0x5318 && c <= 0x7fff) {} => {}", c, s);
			// } else if (c == 0xbc1f) { /* 르ᇝ */
			// s = C_BC1F;
			// log.trace("르ᇝ? {} => {}", c, s);
			// } else if (c == 0xd802) { /* 아ᇇ */
			// s = C_D802;
			// log.trace("아ᇇ? {} => {}", c, s);
		} else if (c >= 0x8000 && c <= 0xffff) {
			/* 한글 영역 */

			int l = (c & 0x7c00) >> 10; /* 초성 */
			int v = (c & 0x03e0) >> 5; /* 중성 */
			int t = (c & 0x001f); /* 종성 */

			short lm = L_MAP[l];
			short vm = V_MAP[v];
			short tm = T_MAP[t];

			if (lm != NONE && vm != NONE && tm != NONE) {
				int syllable = 0xac00 + (lm * 21 * 28) + (vm * 28) + tm;
				s = new String(Character.toChars(syllable));
				log.trace("조합형 현대 한글 음절(11172)을 유니코드로 변환 {} => {}", c, s);
			} else {
				char lt = HNC_L1[l];
				char vt = HNC_V1[v];
				char tt = HNC_T1[t];
				if ((lt != FILL) && (vt == FILL || vt == NONE) && (tt == FILL)) {
					s = Character.toString(lt);
					log.trace("초성만 존재하는 경우 {} => {}", c, s);
				} else if ((lt == FILL) && (vt != FILL && vt != NONE)
						&& (tt == FILL)) {
					s = Character.toString(vt);
					log.trace("중성만 존재하는 경우 {} => {}", c, s);
				} else if ((lt == FILL) && (vt == FILL || vt == NONE)
						&& (tt != FILL)) {
					s = Character.toString(tt);
					log.trace("종성만 존재하는 경우 {} => {}", c, s);
				} else if ((lt != FILL) && (vt != FILL && vt != NONE)
						&& (tt == FILL)) {
					s = new String(new char[] { HNC_L2[l], HNC_V2[v] });
					log.trace("초성과 중성만 존재하는 조합형 옛한글 {} => {}", c, s);
					// } else if ((lt != FILL) && (vt != FILL && vt != NONE)
					// && (tt != FILL)) {
				} else if (lt != NONE && vt != NONE && tt != NONE) {
					// 첫가끝
					s = new String(
							new char[] { HNC_L2[l], HNC_V2[v], HNC_T2[t] });
					log.trace("초성, 중성, 종성 모두 존재하는 조합형 옛한글 {} => {}", c, s);
				} else {
					log.error("HNC code error: {}", c);
				}
			}
		} else {
			// int 형이지만 uint16으로 읽기 때문에 이 경우는 없을 것이다
			throw new IllegalArgumentException();
		}

		return s;
	}
}
