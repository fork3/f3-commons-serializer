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

import static f3.commons.serializer.converters.ConverterUtils.setArrayType;
import static f3.commons.serializer.converters.ConverterUtils.setSimpleType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import f3.commons.serializer.Deserializer;
import f3.commons.serializer.IConverter;
import f3.commons.serializer.Serializer;
import f3.commons.serializer.types.Char;
import f3.commons.serializer.types.DataType;
import lombok.Getter;

/**
 * @author n3k0nation
 *
 */
public class CharConverter implements IStrategyConverter {
	
	@Getter private final static IConverter instance = new CharConverter();
	
	private CharConverter() {
	}

	@Override
	public boolean isSupport(Field field) {
		final Class<?> type = field.getType();
		if(field.isAnnotationPresent(Char.class)) {
			if(type.isArray()) {
				return isSupported(type.getComponentType());
			}
			return isSupported(type);
		}
		
		final Annotation[] anns = field.getAnnotations();
		for(int i = 0; i < anns.length; i++) {
			final Annotation ann = anns[i];
			if(ann.annotationType().isAnnotationPresent(DataType.class)) {
				return false;
			}
		}
		
		return type.equals(byte.class) || type.equals(Byte.class);
	}
	
	private boolean isSupported(Class<?> type) {
		return type.equals(byte.class) || type.equals(Byte.class) 
				|| type.equals(char.class) || type.equals(Character.class)
				|| type.equals(short.class) || type.equals(Short.class)
				|| type.equals(int.class) || type.equals(Integer.class)
				|| type.equals(long.class) || type.equals(Long.class)
				|| type.equals(float.class) || type.equals(Float.class)
				|| type.equals(double.class) || type.equals(Double.class)
				|| type.equals(boolean.class) || type.equals(Boolean.class);
	}

	@Override
	public void serialize(Object object, Field field, ByteBuffer buffer, Serializer serializer) throws ReflectiveOperationException, BufferOverflowException {
		field.setAccessible(true);
		
		final IWriteStrategy strategy = getWriteStrategy(field.getType());
		strategy.write(field.get(object), buffer);
	}
	
	@Override
	public void deserialize(Object object, Field field, ByteBuffer buffer, Deserializer deserializer) throws ReflectiveOperationException, BufferOverflowException {
		field.setAccessible(true);
		
		final Class<?> type = field.getType();
		
		if(type.isArray()) {
			final Object array = field.get(object);
			final int length = ConverterUtils.getArrayLength(array, field);
			Byte[] values = new Byte[length];
			for(int i = 0; i < length; i++) {
				values[i] = buffer.get();
			}
			setArrayType(object, field, values, array);
		} else {
			final Byte value = buffer.get();
			setSimpleType(object, field, value);
		}
	}
	
	@Override
	public IWriteStrategy getWriteStrategy(Class<?> type) {
		if(type.isArray()) {
			return getWriteArrayStrategy(type);
		} else if(type.equals(Boolean.class)) {
			return (value, buffer) -> buffer.put((byte) (value == Boolean.TRUE ? 1 : 0));
		} else if(type.equals(boolean.class)) {
			return (value, buffer) -> buffer.put((byte) ((boolean)value == true ? 1 : 0));
		} else if(type.equals(Character.class) || type.equals(char.class)) {
			return (value, buffer) -> buffer.put((byte)((Character) value).charValue());
		} else {
			return (value, buffer) -> buffer.put(value == null ? 0 : ((Number) value).byteValue());
		}
	}
	
}
