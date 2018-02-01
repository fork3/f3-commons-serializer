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
import f3.commons.serializer.types.Char;
import f3.commons.serializer.types.Dword;
import f3.commons.serializer.types.Qword;
import f3.commons.serializer.types.Real;
import f3.commons.serializer.types.Word;

/**
 * @author n3k0nation
 *
 */
public class TestArray {
	private final Serializer serializer = new Serializer();
	private final Deserializer deserializer = new Deserializer();
	private final ThreadLocalRandom tlr = ThreadLocalRandom.current();
	
	@Test
	public void testByte() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Array @Char byte[] values;
		}
		
		Data d = new Data();
		d.values = new byte[tlr.nextInt(16, 32)];
		tlr.nextBytes(d.values);
		ByteBuffer buffer = testSerialize(d, d.values);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertArrayEquals(d.values, result.values);
	}
	
	@Test
	public void testShort() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Array @Word short[] values;
		}
		
		Data d = new Data();
		d.values = new short[tlr.nextInt(16, 32)];
		for(int i = 0; i < d.values.length; i++) {
			d.values[i] = (short)tlr.nextInt();
		}
		ByteBuffer buffer = testSerialize(d, d.values);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertArrayEquals(d.values, result.values);
	}
	
	@Test
	public void testChar() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Array @Word char[] values;
		}
		
		Data d = new Data();
		d.values = new char[tlr.nextInt(16, 32)];
		for(int i = 0; i < d.values.length; i++) {
			d.values[i] = (char)tlr.nextInt();
		}
		ByteBuffer buffer = testSerialize(d, d.values);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertArrayEquals(d.values, result.values);
	}
	
	@Test
	public void testInt() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Array @Dword int[] values;
		}
		
		Data d = new Data();
		d.values = new int[tlr.nextInt(16, 32)];
		for(int i = 0; i < d.values.length; i++) {
			d.values[i] = tlr.nextInt();
		}
		ByteBuffer buffer = testSerialize(d, d.values);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertArrayEquals(d.values, result.values);
	}
	
	@Test
	public void testLong() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Array @Qword long[] values;
		}
		
		Data d = new Data();
		d.values = new long[tlr.nextInt(16, 32)];
		for(int i = 0; i < d.values.length; i++) {
			d.values[i] = tlr.nextInt();
		}
		ByteBuffer buffer = testSerialize(d, d.values);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertArrayEquals(d.values, result.values);
	}
	
	@Test
	public void testFloat() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Array @Real float[] values;
		}
		
		Data d = new Data();
		d.values = new float[tlr.nextInt(16, 32)];
		for(int i = 0; i < d.values.length; i++) {
			d.values[i] = tlr.nextFloat();
		}
		ByteBuffer buffer = testSerialize(d, d.values);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertArrayEquals(d.values, result.values, 0.0001f);
	}
	
	@Test
	public void testDouble() throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		class Data {
			@Array @Real double[] values;
		}
		
		Data d = new Data();
		d.values = new double[tlr.nextInt(16, 32)];
		for(int i = 0; i < d.values.length; i++) {
			d.values[i] = tlr.nextDouble();
		}
		ByteBuffer buffer = testSerialize(d, d.values);
		
		Data result = deserialize(Data.class, buffer);
		Assert.assertArrayEquals(d.values, result.values, 0.0001d);
	}
	
	private ByteBuffer testSerialize(Object data, byte[] values) throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		ByteBuffer buffer = getBuffer();
		serializer.serializeObject(data, buffer);
		buffer.flip();
		Assert.assertEquals(values.length, buffer.getInt());
		for(int i = 0; i < values.length; i++) {
			Assert.assertEquals(values[i], buffer.get());
		}
		Assert.assertFalse(buffer.hasRemaining());
		buffer.position(0);
		return buffer;
	}
	
	private ByteBuffer testSerialize(Object data, short[] values) throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		ByteBuffer buffer = getBuffer();
		serializer.serializeObject(data, buffer);
		buffer.flip();
		Assert.assertEquals(values.length, buffer.getInt());
		for(int i = 0; i < values.length; i++) {
			Assert.assertEquals(values[i], buffer.getShort());
		}
		Assert.assertFalse(buffer.hasRemaining());
		buffer.position(0);
		return buffer;
	}
	
	private ByteBuffer testSerialize(Object data, char[] values) throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		ByteBuffer buffer = getBuffer();
		serializer.serializeObject(data, buffer);
		buffer.flip();
		Assert.assertEquals(values.length, buffer.getInt());
		for(int i = 0; i < values.length; i++) {
			Assert.assertEquals(values[i], buffer.getChar());
		}
		Assert.assertFalse(buffer.hasRemaining());
		buffer.position(0);
		return buffer;
	}
	
	private ByteBuffer testSerialize(Object data, int[] values) throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		ByteBuffer buffer = getBuffer();
		serializer.serializeObject(data, buffer);
		buffer.flip();
		Assert.assertEquals(values.length, buffer.getInt());
		for(int i = 0; i < values.length; i++) {
			Assert.assertEquals(values[i], buffer.getInt());
		}
		Assert.assertFalse(buffer.hasRemaining());
		buffer.position(0);
		return buffer;
	}
	
	private ByteBuffer testSerialize(Object data, long[] values) throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		ByteBuffer buffer = getBuffer();
		serializer.serializeObject(data, buffer);
		buffer.flip();
		Assert.assertEquals(values.length, buffer.getInt());
		for(int i = 0; i < values.length; i++) {
			Assert.assertEquals(values[i], buffer.getLong());
		}
		Assert.assertFalse(buffer.hasRemaining());
		buffer.position(0);
		return buffer;
	}
	
	private ByteBuffer testSerialize(Object data, float[] values) throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		ByteBuffer buffer = getBuffer();
		serializer.serializeObject(data, buffer);
		buffer.flip();
		Assert.assertEquals(values.length, buffer.getInt());
		for(int i = 0; i < values.length; i++) {
			Assert.assertEquals(values[i], (float)buffer.getDouble(), 0.0001f);
		}
		Assert.assertFalse(buffer.hasRemaining());
		buffer.position(0);
		return buffer;
	}
	
	private ByteBuffer testSerialize(Object data, double[] values) throws IllegalArgumentException, BufferOverflowException, ReflectiveOperationException {
		ByteBuffer buffer = getBuffer();
		serializer.serializeObject(data, buffer);
		buffer.flip();
		Assert.assertEquals(values.length, buffer.getInt());
		for(int i = 0; i < values.length; i++) {
			Assert.assertEquals(values[i], buffer.getDouble(), 0.0001d);
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
