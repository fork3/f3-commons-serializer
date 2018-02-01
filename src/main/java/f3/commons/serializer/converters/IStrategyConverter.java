/*
 * Copyright (c) 2010-2017 fork3
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
package f3.commons.serializer.converters;

import java.nio.ByteBuffer;

import f3.commons.serializer.IConverter;

/**
 * @author n3k0nation
 *
 */
public interface IStrategyConverter extends IConverter {
	
	static interface IWriteStrategy {
		void write(Object value, ByteBuffer buffer);
	}
	
	default IWriteStrategy getWriteArrayStrategy(Class<?> type) {
		final Class<?> arrayType = type.getComponentType();
		final IWriteStrategy strategy;
		if(arrayType.equals(Boolean.class) || arrayType.equals(boolean.class)) {
			strategy = getWriteStrategy(Boolean.class);
		} else if(arrayType.equals(Character.class) || arrayType.equals(char.class)) {
			strategy = getWriteStrategy(Character.class);
		} else {
			strategy = getWriteStrategy(Number.class);
		}
		
		if(!arrayType.isPrimitive()) {
			return (value, buffer) -> {
				final Object[] array = (Object[]) value;
				for(int i = 0; i < array.length; i++) {
					strategy.write(array[i], buffer);
				}
			};
		} else if(arrayType.equals(byte.class)) { //shit-code but is it faster then native call in Array 
			return (value, buffer) -> {
				final byte[] array = (byte[]) value;
				for(int i = 0; i < array.length; i++) {
					strategy.write(array[i], buffer);
				}
			};
		} else if(arrayType.equals(char.class)) {
			return (value, buffer) -> {
				final char[] array = (char[]) value;
				for(int i = 0; i < array.length; i++) {
					strategy.write(array[i], buffer);
				}
			};
		} else if(arrayType.equals(short.class)) {
			return (value, buffer) -> {
				final short[] array = (short[]) value;
				for(int i = 0; i < array.length; i++) {
					strategy.write(array[i], buffer);
				}
			};
		} else if(arrayType.equals(int.class)) {
			return (value, buffer) -> {
				final int[] array = (int[]) value;
				for(int i = 0; i < array.length; i++) {
					strategy.write(array[i], buffer);
				}
			};
		} else if(arrayType.equals(long.class)) {
			return (value, buffer) -> {
				final long[] array = (long[]) value;
				for(int i = 0; i < array.length; i++) {
					strategy.write(array[i], buffer);
				}
			};
		} else if(arrayType.equals(boolean.class)) {
			return (value, buffer) -> {
				final boolean[] array = (boolean[]) value;
				for(int i = 0; i < array.length; i++) {
					strategy.write(array[i], buffer);
				}
			};
		} else if(arrayType.equals(float.class)) {
			return (value, buffer) -> {
				final float[] array = (float[]) value;
				for(int i = 0; i < array.length; i++) {
					strategy.write(array[i], buffer);
				}
			};
		} else {
			return (value, buffer) -> {
				final double[] array = (double[]) value;
				for(int i = 0; i < array.length; i++) {
					strategy.write(array[i], buffer);
				}
			};
		}
	}
	
	IWriteStrategy getWriteStrategy(Class<?> type);
}
