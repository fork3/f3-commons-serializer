/*
 * Copyright (c) 2010-2018 fork3
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package f3.commons.serializer.test;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import f3.commons.serializer.Deserializer;
import f3.commons.serializer.Serializer;
import f3.commons.serializer.types.Array;
import f3.commons.serializer.types.UTF8;

/**
 * @author n3k0nation
 *
 */
public class TestUTF8 {
	
	private final Serializer serializer = new Serializer();
	private final Deserializer deserializer = new Deserializer();
	private final ThreadLocalRandom tlr = ThreadLocalRandom.current();
	
	private final static char[] chars = {'a', 'z', 'A', 'Z', '0', '9'};
	
	@Test
	public void testString() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@UTF8 public String value;
		}
		
		Data data = new Data();
		data.value = generateString(tlr.nextInt(16, 64));
		ByteBuffer buffer = test(data, data.value);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertEquals(data.value, result.value);
	}
	
	@Test
	public void testStringArray() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Array @UTF8 public String[] values;
		}
		
		Data data = new Data();
		data.values = new String[tlr.nextInt(1, 10)];
		for(int i = 0; i < data.values.length; i++) {
			data.values[i] = generateString(tlr.nextInt(4, 32));
		}
		ByteBuffer buffer = test(data, data.values);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertArrayEquals(data.values, result.values);
	}
	
	private String generateString(int length) {
		StringBuilder sb = new StringBuilder(length);
		for(int i = 0; i < length; i++) {
			int type = tlr.nextInt(chars.length >>> 1);
			char character = (char)tlr.nextInt(chars[type << 1], chars[(type << 1) + 1] + 1);
			sb.append(character);
		}
		return sb.toString();
	}
	
	private ByteBuffer test(Object data, CharSequence value) throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		ByteBuffer buffer = getBuffer();
		serializer.serializeObject(data, buffer);
		buffer.flip();
		
		StringBuilder sb = new StringBuilder();
		char character;
		while((character = buffer.getChar()) != '\000') {
			sb.append(character);
		}
		
		Assert.assertEquals(value, sb.toString());
		Assert.assertFalse(buffer.hasRemaining());
		buffer.position(0);
		return buffer;
	}
	
	private ByteBuffer test(Object data, String[] values) throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		ByteBuffer buffer = getBuffer();
		serializer.serializeObject(data, buffer);
		buffer.flip();
		
		Assert.assertEquals(values.length, buffer.getInt());
		
		for(int i = 0; i < values.length; i++) {
			StringBuilder sb = new StringBuilder();
			char character;
			while((character = buffer.getChar()) != '\000') {
				sb.append(character);
			}
			
			Assert.assertEquals(values[i], sb.toString());
		}
		Assert.assertFalse(buffer.hasRemaining());
		buffer.position(0);
		return buffer;
	}
	
	private <T> T deserialize(Class<T> type, ByteBuffer buffer) throws IllegalArgumentException, ReflectiveOperationException {
		return deserializer.deserializeObject(type, buffer);
	}
	
	private ByteBuffer getBuffer() {
		return ByteBuffer.allocate(Serializer.defaultBufferSize).order(ByteOrder.LITTLE_ENDIAN);
	}
	
}
