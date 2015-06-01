/*
  Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.

  The MySQL Connector/J is licensed under the terms of the GPLv2
  <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>, like most MySQL Connectors.
  There are special exceptions to the terms and conditions of the GPLv2 as it is applied to
  this software, see the FLOSS License Exception
  <http://www.mysql.com/about/legal/licensing/foss-exception.html>.

  This program is free software; you can redistribute it and/or modify it under the terms
  of the GNU General Public License as published by the Free Software Foundation; version 2
  of the License.

  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with this
  program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth
  Floor, Boston, MA 02110-1301  USA

 */

package com.mysql.cj.core.io;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

import com.mysql.cj.api.io.ValueFactory;
import com.mysql.cj.core.exception.NumberOutOfRange;

/**
 * Tests for {@link MysqlTextValueDecoder}.
 */
public class MysqlTextValueDecoderTest {
    private MysqlTextValueDecoder valueDecoder = new MysqlTextValueDecoder();

    @Test
    public void testIntValues() {
        ValueFactory<String> vf = new StringValueFactory();
        assertEquals(String.valueOf(Integer.MIN_VALUE), this.valueDecoder.decodeInt4(String.valueOf(Integer.MIN_VALUE).getBytes(), 0, 11, vf));
        assertEquals(String.valueOf(Integer.MAX_VALUE), this.valueDecoder.decodeInt4(String.valueOf(Integer.MAX_VALUE).getBytes(), 0, 10, vf));

        assertEquals(String.valueOf(Integer.MAX_VALUE), this.valueDecoder.decodeUInt4(String.valueOf(Integer.MAX_VALUE).getBytes(), 0, 10, vf));
        assertEquals("2147483648",
                this.valueDecoder.decodeUInt4(BigInteger.valueOf(Integer.MAX_VALUE).add(BigInteger.valueOf(1)).toString().getBytes(), 0, 10, vf));
        try {
            this.valueDecoder.decodeInt4(BigInteger.valueOf(Integer.MAX_VALUE).add(BigInteger.valueOf(1)).toString().getBytes(), 0, 10, vf);
            fail("Exception should be thrown for decodeInt4(Integer.MAX_VALUE + 1)");
        } catch (NumberOutOfRange ex) {
            // expected
        }

        byte[] uint8LessThanMaxLong = "8223372036854775807".getBytes();
        ValueFactory<String> fromLongOnly = new DefaultValueFactory() {
            public String createFromLong(long l) {
                return Long.valueOf(l).toString();
            }

            public String getTargetTypeName() {
                return null;
            }
        };
        assertEquals("8223372036854775807", this.valueDecoder.decodeUInt8(uint8LessThanMaxLong, 0, uint8LessThanMaxLong.length, fromLongOnly));
        byte[] uint8MoreThanMaxLong1 = "9223372036854775807".getBytes();
        byte[] uint8MoreThanMaxLong2 = "18223372036854775807".getBytes();
        assertEquals("9223372036854775807", this.valueDecoder.decodeUInt8(uint8MoreThanMaxLong1, 0, uint8MoreThanMaxLong1.length, vf));
        assertEquals("18223372036854775807", this.valueDecoder.decodeUInt8(uint8MoreThanMaxLong2, 0, uint8MoreThanMaxLong2.length, vf));
    }
}
