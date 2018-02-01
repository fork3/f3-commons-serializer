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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

import f3.commons.serializer.Deserializer;
import f3.commons.serializer.Serializer;
import f3.commons.serializer.types.Array;
import f3.commons.serializer.types.Inline;
import lombok.EqualsAndHashCode;

/**
 * @author n3k0nation
 *
 */
public class TestInline {
	private final Serializer serializer = new Serializer();
	private final Deserializer deserializer = new Deserializer();
	private final ThreadLocalRandom tlr = ThreadLocalRandom.current();
	
	@Test
	public void testInlineArray() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		@EqualsAndHashCode
		class InlineData {
			byte b;
			int i;
		}
		
		@EqualsAndHashCode
		class Data {
			@Array @Inline InlineData[] values;
		}
		
		Data d = new Data();
		d.values = new InlineData[tlr.nextInt(16, 32)];
		for(int i = 0; i < d.values.length; i++) {
			d.values[i] = new InlineData();
			d.values[i].b = (byte) tlr.nextInt();
			d.values[i].i = tlr.nextInt();
		}
		ByteBuffer buffer = serialize(d);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertEquals(d, result);
	}
	
	@Test
	public void testInlineList() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		@EqualsAndHashCode
		class InlineData {
			byte b;
			int i;
		}
		
		@EqualsAndHashCode
		class Data {
			@Array @Inline List<InlineData> values;
		}
		
		Data d = new Data();
		d.values = new ArrayList<>();
		for(int i = 0; i < tlr.nextInt(16, 32); i++) {
			final InlineData data = new InlineData();
			data.b = (byte) tlr.nextInt();
			data.i = tlr.nextInt();
			d.values.add(data);
		}
		ByteBuffer buffer = serialize(d);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertEquals(d, result);
	}
	
	private ByteBuffer serialize(Object data) throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		ByteBuffer buffer = getBuffer();
		serializer.serializeObject(data, buffer);
		buffer.flip();
		return buffer;
	}
	
	private <T> T deserialize(Class<T> type, ByteBuffer buffer) throws IllegalArgumentException, ReflectiveOperationException {
		return deserializer.deserializeObject(type, buffer);
	}
	
	private ByteBuffer getBuffer() {
		return ByteBuffer.allocate(Serializer.defaultBufferSize).order(ByteOrder.LITTLE_ENDIAN);
	}
	
}
