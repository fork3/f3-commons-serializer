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
package f3.commons.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import f3.commons.serializer.converters.ArrayIndexConverter;
import f3.commons.serializer.types.Char;
import f3.commons.serializer.types.DataType;
import f3.commons.serializer.types.Dword;
import f3.commons.serializer.types.Opcode;
import f3.commons.serializer.types.Transient;
import f3.commons.serializer.types.Word;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author n3k0nation
 *
 */
@Slf4j
public class Serializer {
	/** Default buffer size for serialization. By default: 16kb */
	public static int defaultBufferSize = 16*1024;
	
	@Getter private final List<IConverter> converters;
	private final ByteOrder byteOrder;
	
	/** Create serializer with specified byte-order */
	public Serializer(ByteOrder byteOrder) {
		converters = new ArrayList<>(Converters.getConverters());
		this.byteOrder = byteOrder;
	}
	
	/** Create serializer with LE byte-order */ 
	public Serializer() {
		this(ByteOrder.LITTLE_ENDIAN);
	}
	
	/** Serialize object.
	 * @exception BufferOverflowException if class to big (more than {@link Serializer#defaultBufferSize}) */
	public ByteBuffer serialize(Object object) throws IllegalArgumentException, ReflectiveOperationException, BufferOverflowException {
		final ByteBuffer buffer = ByteBuffer.allocate(defaultBufferSize).order(byteOrder);
		serializeObject(object, buffer);
		return buffer;
	}
	
	/** Serialize object to buffer.
	 * @exception BufferOverflowException if class to big */
	public void serializeObject(Object object, ByteBuffer buffer) throws IllegalArgumentException, ReflectiveOperationException, BufferOverflowException {
		final Class<?> clazz = object.getClass();
		
		writeOpcode(clazz, buffer);
		
		final Field[] fields = clazz.getDeclaredFields();
		for(int i = 0; i < fields.length; i++) {
			final Field field = fields[i];
			
			if(field.isAnnotationPresent(Transient.class) || Modifier.isTransient(field.getModifiers()) || field.isSynthetic()) {
				if(log.isDebugEnabled()) {
					log.debug("Skip {}::{}", clazz.getCanonicalName(), field.getName());
				}
				continue;
			}
			
			final IConverter arrayConverter = ArrayIndexConverter.getInstance();
			if(arrayConverter.isSupport(field)) {
				if(log.isDebugEnabled()) {
					log.debug("Found array index in {}::{}", clazz.getCanonicalName(), field.getName());
				}
				
				arrayConverter.serialize(object, field, buffer, this);
			}
			
			final IConverter conv = converters.stream()
					.filter(converter -> converter.isSupport(field))
					.findAny()
					.orElseThrow(() -> new IllegalArgumentException("Unsupported data-type in " 
							+ object.getClass().getCanonicalName() + "::" + field.getName()));
			
			if(log.isDebugEnabled()) {
				log.debug("Serialize {}::{} with {}", clazz.getCanonicalName(), field.getName(), conv.getClass().getCanonicalName());
			}
			
			conv.serialize(object, field, buffer, this);
		}
	}
	
	private boolean writeOpcode(Class<?> clazz, ByteBuffer buffer) {
		final Opcode opcode = clazz.getAnnotation(Opcode.class);
		if(opcode == null) {
			return false;
		}
		
		if(!opcode.type().isAnnotationPresent(DataType.class)) {
			throw new IllegalArgumentException("Unknown opcode format in " + clazz.getCanonicalName());
		}
		
		if(opcode.type().equals(Char.class)) {
			buffer.put((byte) (opcode.value() & 0xff));
		} else if(opcode.type().equals(Word.class)) {
			buffer.putShort((short) (opcode.value() & 0xffff));
		} else if(opcode.type().equals(Dword.class)) {
			buffer.putInt(opcode.value());
		} else {
			throw new IllegalArgumentException("Opcode format " + opcode.type().getSimpleName() + " not supported");
		}
		
		return true;
	}
	
}
