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

import f3.commons.serializer.Deserializer;
import f3.commons.serializer.IConverter;
import f3.commons.serializer.Serializer;
import f3.commons.serializer.types.Char;
import f3.commons.serializer.types.DataType;
import f3.commons.serializer.types.Dword;
import f3.commons.serializer.types.Qword;
import f3.commons.serializer.types.Real;
import f3.commons.serializer.types.UTF8;
import f3.commons.serializer.types.Word;
import lombok.Getter;

/**
 * @author n3k0nation
 *
 */
public class UTF8Converter implements IConverter {
	
	@Getter private final static IConverter instance = new UTF8Converter();
	
	private UTF8Converter() {
	}

	@Override
	public boolean isSupport(Field field) {
		final Class<?> type = field.getType();
		if(field.isAnnotationPresent(UTF8.class)) {
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
		
		return isSupported(type);
	}
	
	private boolean isSupported(Class<?> type) {
		return CharSequence.class.isAssignableFrom(type);
	}

	@Override
	public void serialize(Object object, Field field, ByteBuffer buffer, Serializer serializer) throws ReflectiveOperationException, BufferOverflowException {
		field.setAccessible(true);
		
		final boolean isCStr = isCStr(field); 
		
		if(field.getType().isArray()) {
			final f3.commons.serializer.types.Array arrayAnn = field.getAnnotation(f3.commons.serializer.types.Array.class);
			final CharSequence[] sequences = (CharSequence[]) field.get(object);
			final int length = arrayAnn != null && arrayAnn.length() != -1 ? arrayAnn.length() : sequences.length;
			for(int i = 0; i < length; i++) {
				if(isCStr) {
					writeCStr(sequences[i], buffer);
				} else {
					writeLength(object, field, buffer, sequences[i].length());
					write(sequences[i], buffer);
				}
			}
		} else {
			final CharSequence sequence = (CharSequence) field.get(object);
			if(isCStr) {
				writeCStr(sequence, buffer);
			} else {
				writeLength(object, field, buffer, sequence.length());
				write(sequence, buffer);
			}
		}
	}
	
	@Override
	public void deserialize(Object object, Field field, ByteBuffer buffer, Deserializer deserializer) throws ReflectiveOperationException, BufferOverflowException {
		field.setAccessible(true);
		
		final Class<?> type = field.getType();
		final boolean isCStr = isCStr(field);
		
		if(type.isArray()) {
			final Object[] array = (Object[]) field.get(object);
			final f3.commons.serializer.types.Array arrayAnn = field.getAnnotation(f3.commons.serializer.types.Array.class);
			final int length = arrayAnn != null && arrayAnn.length() != -1 ? arrayAnn.length() : array.length;
			for(int i = 0; i < length; i++) {
				if(isCStr) {
					array[i] = readCStr(buffer);
				} else {
					final int sequenceLength = readLength(object, field, buffer);
					array[i] = read(buffer, sequenceLength);
				}
			}
		} else {
			if(isCStr) {
				field.set(object, readCStr(buffer));
			} else {
				final int sequenceLength = readLength(object, field, buffer);
				field.set(object, read(buffer, sequenceLength));
			}
		}
	}
	
	private static boolean isCStr(Field field) {
		final UTF8 utf8Ann = field.getAnnotation(UTF8.class);
		return utf8Ann != null ? utf8Ann.nullTerminate() : true;
	}
	
	private static int readLength(Object object, Field field, ByteBuffer buffer) {
		final UTF8 utf8Ann = field.getAnnotation(UTF8.class);
		Class<? extends Annotation> lengthType = utf8Ann != null ? utf8Ann.lengthType() : Dword.class; 
		
		final int sequenceLength;
		if(lengthType == Char.class) {
			sequenceLength = buffer.get() & 0xff;
		} else if(lengthType == Word.class) {
			sequenceLength = buffer.getShort() & 0xffff;
		} else if(lengthType == Dword.class) {
			sequenceLength = buffer.getInt();
		} else if(lengthType == Qword.class) {
			sequenceLength = (int) buffer.getLong();
			if(sequenceLength < 0) {
				throw new IllegalArgumentException("UTF8 annotation in " + object.getClass().getCanonicalName() + "::" + field.getName()
					+ " have length type Qword but maximal length lies within Dword type.");
			}
		} else if(lengthType == Real.class) {
			sequenceLength = (int) buffer.getDouble();
		} else {
			throw new IllegalArgumentException("UTF8 annotation in " + object.getClass().getCanonicalName() + "::" + field.getName()
				+ " have unsupported length type");
		}
		
		if(sequenceLength < 0) {
			throw new IllegalArgumentException("Negative UTF8 length in " + object.getClass().getCanonicalName() + "::" + field.getName() + ".");
		}
		
		return sequenceLength;
	}
	
	private static void writeLength(Object object, Field field, ByteBuffer buffer, int length) {
		length <<= 1; //swift to jbyte count
		
		final UTF8 utf8Ann = field.getAnnotation(UTF8.class);
		Class<? extends Annotation> lengthType = utf8Ann != null ? utf8Ann.lengthType() : Dword.class; 
		
		if(lengthType == Char.class) {
			buffer.put((byte)length);
		} else if(lengthType == Word.class) {
			buffer.putShort((short)length);
		} else if(lengthType == Dword.class) {
			buffer.putInt(length);
		} else if(lengthType == Qword.class) {
			buffer.putLong(length);
		} else if(lengthType == Real.class) {
			buffer.putDouble(length);
		} else {
			throw new IllegalArgumentException("UTF8 annotation in " + object.getClass().getCanonicalName() + "::" + field.getName()
				+ " have unsupported length type");
		}
	}
	
	private static String readCStr(ByteBuffer buffer) {
		char ch;
		final StringBuilder sb = new StringBuilder(32);
		while((ch = buffer.getChar()) != '\000') {
			sb.append(ch);
		}
		return sb.toString();
	}
	
	private static String read(ByteBuffer buffer, int length) {
		length >>= 1; //shift to jchar size
		final StringBuilder sb = new StringBuilder(length);
		for(int i = 0; i < length; i++) {
			sb.append(buffer.getChar());
		}
		return sb.toString();
	}
	
	private static void writeCStr(CharSequence sequence, ByteBuffer buffer) {
		for(int j = 0; j < sequence.length(); j++) {
			buffer.putChar(sequence.charAt(j));
		}
		buffer.putChar('\000');
	}
	
	private static void write(CharSequence sequence, ByteBuffer buffer) {
		for(int j = 0; j < sequence.length(); j++) {
			buffer.putChar(sequence.charAt(j));
		}
	}

}
