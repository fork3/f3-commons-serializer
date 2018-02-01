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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.List;

import f3.commons.serializer.Deserializer;
import f3.commons.serializer.IConverter;
import f3.commons.serializer.Serializer;
import f3.commons.serializer.stub.ExArrayList;
import f3.commons.serializer.types.Array;
import f3.commons.serializer.types.Char;
import f3.commons.serializer.types.DataType;
import f3.commons.serializer.types.Dword;
import f3.commons.serializer.types.Qword;
import f3.commons.serializer.types.Real;
import f3.commons.serializer.types.Word;
import lombok.Getter;

/**
 * @author n3k0nation
 *
 */
public class ArrayIndexConverter implements IConverter {
	
	@Getter private final static IConverter instance = new ArrayIndexConverter();
	
	private ArrayIndexConverter() {
	}
	
	@Override
	public boolean isSupport(Field field) {
		return field.isAnnotationPresent(Array.class) && (field.getType().isArray() || List.class.isAssignableFrom(field.getType()));
	}

	@Override
	public void serialize(Object object, Field field, ByteBuffer buffer, Serializer serializer) throws ReflectiveOperationException, BufferOverflowException {
		final Array ann = field.getAnnotation(Array.class);
		final Class<? extends Annotation> typeClass = ann.sizeType();
		if(!typeClass.isAnnotationPresent(DataType.class)) {
			throw new IllegalArgumentException("Array annotation in " + object.getClass().getCanonicalName() + "::" + field.getName()
					+ " have wrong size type");
		}
		
		field.setAccessible(true);
		
		final Array arrayAnn = field.getAnnotation(Array.class);
		final boolean fixedLength = arrayAnn != null && arrayAnn.length() != -1;
		
		final int length;
		final Object value = field.get(object);
		if(fixedLength) {
			length = arrayAnn.length();
		} else if(field.getType().isArray()) {
			length = java.lang.reflect.Array.getLength(value);
		} else {
			length = ((List) value).size();
		}
		
		if(!fixedLength) {
			if(typeClass.equals(Char.class)) {
				buffer.put((byte) length);
			} else if(typeClass.equals(Word.class)) {
				buffer.putShort((short) length);
			} else if(typeClass.equals(Dword.class)) {
				buffer.putInt(length);
			} else if(typeClass.equals(Qword.class)) {
				buffer.putLong(length & 0xffffffffl);
			} else if(typeClass.equals(Real.class)) {
				buffer.putDouble(length);
			} else {
				throw new IllegalArgumentException("Array annotation in " + object.getClass().getCanonicalName() + "::" + field.getName()
						+ " have unsupported size type");
			}
		}
	}
	
	@Override
	public void deserialize(Object object, Field field, ByteBuffer buffer, Deserializer deserializer) throws ReflectiveOperationException, BufferOverflowException {
		final Array ann = field.getAnnotation(Array.class);
		final Class<? extends Annotation> typeClass = ann.sizeType();
		if(!typeClass.isAnnotationPresent(DataType.class)) {
			throw new IllegalArgumentException("Array annotation in " + object.getClass().getCanonicalName() + "::" + field.getName()
					+ " have wrong size type");
		}
		
		field.setAccessible(true);
		
		final Array arrayAnn = field.getAnnotation(Array.class);
		
		final int length;
		if(arrayAnn != null && arrayAnn.length() != -1) {
			length = arrayAnn.length();
		} else if(typeClass.equals(Char.class)) {
			length = buffer.get() & 0xff;
		} else if(typeClass.equals(Word.class)) {
			length = buffer.getShort() & 0xffff;
		} else if(typeClass.equals(Dword.class)) {
			length = buffer.getInt();
		} else if(typeClass.equals(Qword.class)) {
			length = (int)buffer.getLong();
		} else if(typeClass.equals(Real.class)) {
			length = (int)buffer.getDouble();
		} else {
			throw new IllegalArgumentException("Array annotation in " + object.getClass().getCanonicalName() + "::" + field.getName()
					+ " have unsupported size type");
		}
		
		field.setAccessible(true);
		if(field.getType().isArray()) {
			field.set(object, java.lang.reflect.Array.newInstance(field.getType().getComponentType(), length));
		} else {
			field.set(object, new ExArrayList<>(length));
		}
	}

}
