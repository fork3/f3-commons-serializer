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
package f3.commons.serializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import f3.commons.serializer.converters.ArrayIndexConverter;
import f3.commons.serializer.types.Opcode;
import f3.commons.serializer.types.Transient;
import sun.reflect.ReflectionFactory;

/**
 * @author n3k0nation
 *
 */
public class Deserializer {
	private final Map<Integer, Class<?>> classes = new HashMap<>();
	private final List<IConverter> converters;
	
	public Deserializer() {
		converters = new ArrayList<>(Converters.getConverters());
	}
	
	public void addType(Class<?> clazz) throws IllegalArgumentException, ReflectiveOperationException {
		if(clazz.isInterface() || clazz.isEnum() || clazz.isSynthetic() || Modifier.isAbstract(clazz.getModifiers())) {
			throw new IllegalArgumentException("Invalid modifiers");
		}
		
		final Opcode opcode = clazz.getAnnotation(Opcode.class);
		if(opcode == null) {
			throw new IllegalArgumentException("Opcode not found");
		}
		
		classes.put(opcode.value(), clazz);
		
		for(Field field : clazz.getDeclaredFields()) {
			if(checkModifiers(field)) {
				continue;
			}
			
			Class<?> type = field.getType();
			if(type.isArray()) {
				type = type.getComponentType();
			} else if(List.class.isAssignableFrom(type)) {
				type = (Class<?>) ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0];
			}
			
			if(type.isPrimitive()) {
				continue;
			}
			
			try {
				addType(type);
			} catch(IllegalArgumentException e) {
				//ignore
			}
		}
	}
	
	public <T> T deserialize(ByteBuffer buffer) throws RuntimeException, IllegalArgumentException, ReflectiveOperationException {
		final Class<?> type = getType(buffer);
		if(type == null) {
			throw new RuntimeException("Type not found");
		}
		
		return (T) deserializeObject(type, buffer);
	}
	
	private Class<?> getType(ByteBuffer buffer) {
		final int position = buffer.position();
		int opcode = buffer.get(position) & 0xff;
		Class<?> type = classes.get(opcode);
		if(type != null) {
			return type;
		}
		
		opcode = buffer.getShort(position) & 0xffff;
		type = classes.get(opcode);
		if(type != null) {
			return type;
		}
		
		opcode = buffer.getInt(position);
		return classes.get(opcode);
	}
	
	public <T> T deserializeObject(Class<T> type, ByteBuffer buffer) throws IllegalArgumentException, ReflectiveOperationException {
		final T object = createObject(type);
		
		for(Field field : type.getDeclaredFields()) {
			if(checkModifiers(field)) {
				continue;
			}
			
			final IConverter arrayConverter = ArrayIndexConverter.getInstance();
			if(arrayConverter.isSupport(field)) {
				arrayConverter.deserialize(object, field, buffer, this);
			}
			
			final IConverter conv = converters.stream()
					.filter(converter -> converter.isSupport(field))
					.findAny()
					.orElseThrow(() -> new IllegalArgumentException("Unsupported data-type in " 
							+ object.getClass().getCanonicalName() + "::" + field.getName()));
			
			conv.deserialize(object, field, buffer, this);
		}
		
		return object;
	}
	
	private <T> T createObject(Class<T> type) throws ReflectiveOperationException {
		final ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
		Constructor<?> ctor = Object.class.getDeclaredConstructor();
		ctor.setAccessible(true);
		ctor = rf.newConstructorForSerialization(type, ctor);
		return type.cast(ctor.newInstance());
	}
	
	private boolean checkModifiers(Field field) {
		return field.isAnnotationPresent(Transient.class) || field.isSynthetic() || Modifier.isFinal(field.getModifiers()) || Modifier.isTransient(field.getModifiers());
	}
}
