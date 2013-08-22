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
package com.argo.hwp.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.util.LittleEndian;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HwpStreamReader {
	private Logger log = LoggerFactory.getLogger(getClass());
	private InputStream input;
	private byte[] buf;

	public HwpStreamReader(InputStream inputStream) {
		this.input = inputStream;
		buf = new byte[4];
	}

	/**
	 * 읽을 데이터가 더 있는가?
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean available() throws IOException {
		return input.available() > 0;
	}

	/**
	 * unsigned 1 byte
	 * 
	 * @return
	 * @throws IOException
	 */
	public short uint8() throws IOException {
		if (ensure(1) == 0)
			return -1;

		return LittleEndian.getUByte(buf);
	}

	/**
	 * unsigned 2 byte
	 * 
	 * @return
	 * @throws IOException
	 */
	public int uint16() throws IOException {
		if (ensure(2) == 0)
			return -1;

		return LittleEndian.getUShort(buf);
	}

	/**
	 * unsigned 2 byte array
	 * 
	 * @param i
	 * @return
	 * @throws IOException
	 */
	public int[] uint16(int i) throws IOException {
		if (i <= 0)
			throw new IllegalArgumentException();

		int[] uints = new int[i];
		for (int ii = 0; ii < i; ii++) {
			if (ensure(2) == 0)
				throw new EOFException();

			uints[ii] = LittleEndian.getUShort(buf);
		}

		return uints;
	}

	/**
	 * unsigned 4 byte
	 * 
	 * @return
	 * @throws IOException
	 */
	public long uint32() throws IOException {
		if (ensure(4) == 0)
			return -1;

		return LittleEndian.getUInt(buf);
	}

	/**
	 * 
	 * @param n
	 * @return
	 * @throws IOException
	 */
	public long skip(long n) throws IOException {
		return input.skip(n);
	}

	/**
	 * n만큼 skip 하지 못할 경우 IOException 을 발생한다
	 * 
	 * @param n
	 * @throws IOException
	 */
	public void ensureSkip(long n) throws IOException {
		long skipped = skip(n);
		if (n != skipped) {
			log.error("Skip failed {} => {}", n, skipped);
			throw new IOException();
		}
	}

	/**
	 * count만큼 바이트를 읽는다. InflaterInputStream의 경우 한번에 count만큼 read가 안되는 경우가 있다.
	 * 그래서 count만큼 읽을 때까지 루프를 실행한다
	 * 
	 * @param count
	 * @return
	 * @throws IOException
	 * @throws EOFException
	 */
	private int ensure(int count) throws IOException, EOFException {
		int total = 0;
		while (total < count) {
			// if (total > 0) {
			// log.warn("한번에 읽기 실패 {}/{}. 다시 읽기 시도함 {}", total, count, input);
			// }

			int read = input.read(buf, total, count - total);
			if (read <= 0)
				break;

			total += read;
		}

		if (total == 0) {
			// end
		} else if (total < count) {
			// unexpected end
			throw new EOFException();
		}

		return total;
	}
}