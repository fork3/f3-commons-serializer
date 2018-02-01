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
import f3.commons.serializer.types.Char;

/**
 * @author n3k0nation
 *
 */
public class TestChar {
	private final Serializer serializer = new Serializer();
	private final Deserializer deserializer = new Deserializer();
	private final ThreadLocalRandom tlr = ThreadLocalRandom.current();

	@Test
	public void testByte() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Char byte value;
		}
		
		Data d = new Data();
		d.value = (byte)tlr.nextInt();
		ByteBuffer buffer = testSerialize(d, d.value);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertEquals(d.value, result.value);
	}
	
	@Test
	public void testChar() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Char char value;
		}
		
		Data d = new Data();
		d.value = (char)tlr.nextInt();
		ByteBuffer buffer = testSerialize(d, (byte)d.value);
		
		d.value = (char) ((byte)d.value);
		Data result = deserialize(Data.class, buffer);
		Assert.assertEquals(d.value, result.value);
	}
	
	@Test
	public void testShort() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Char short value;
		}
		
		Data d = new Data();
		d.value = (short)tlr.nextInt();
		ByteBuffer buffer = testSerialize(d, (byte)d.value);
		
		d.value = (byte) d.value;
		Data result = deserialize(Data.class, buffer);
		Assert.assertEquals(d.value, result.value);
	}
	
	@Test
	public void testInt() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Char int value;
		}
		
		Data d = new Data();
		d.value = tlr.nextInt();
		ByteBuffer buffer = testSerialize(d, (byte)d.value);
		
		d.value = (byte) d.value;
		Data result = deserialize(Data.class, buffer);
		Assert.assertEquals(d.value, result.value);
	}
	
	@Test
	public void testLong() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Char long value;
		}
		
		Data d = new Data();
		d.value = tlr.nextInt();
		ByteBuffer buffer = testSerialize(d, (byte)d.value);
		
		d.value = (byte) d.value;
		Data result = deserialize(Data.class, buffer);
		Assert.assertEquals(d.value, result.value);
	}
	
	@Test
	public void testFloat() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Char float value;
		}
		
		Data d = new Data();
		d.value = tlr.nextInt() / 2.5f;
		ByteBuffer buffer = testSerialize(d, (byte)d.value);
		
		d.value = (byte) d.value;
		Data result = deserialize(Data.class, buffer);
		Assert.assertEquals(d.value, result.value, 0.0001f);
	}
	
	@Test
	public void testDouble() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Char double value;
		}
		
		Data d = new Data();
		d.value = tlr.nextInt() / 2.5d;
		ByteBuffer buffer = testSerialize(d, (byte)d.value);
		
		d.value = (byte) d.value;
		Data result = deserialize(Data.class, buffer);
		Assert.assertEquals(d.value, result.value, 0.0001d);
	}
	
	@Test
	public void testBoolean() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Char boolean value;
		}
		
		Data d = new Data();
		d.value = tlr.nextBoolean();
		ByteBuffer buffer = testSerialize(d, (byte) (d.value ? 1 : 0));
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertEquals(d.value, result.value);
	}
	
	private ByteBuffer testSerialize(Object data, byte value) throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		ByteBuffer buffer = getBuffer();
		serializer.serializeObject(data, buffer);
		buffer.flip();
		Assert.assertEquals(value, buffer.get());
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
