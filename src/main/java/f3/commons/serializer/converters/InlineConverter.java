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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.List;

import f3.commons.reflection.Primitive;
import f3.commons.serializer.Deserializer;
import f3.commons.serializer.IConverter;
import f3.commons.serializer.Serializer;
import f3.commons.serializer.stub.ExArrayList;
import f3.commons.serializer.types.Inline;
import lombok.Getter;

/**
 * @author n3k0nation
 *
 */
public class InlineConverter implements IConverter {
	
	@Getter private final static IConverter instance = new InlineConverter();
	
	private InlineConverter() {
	}

	@Override
	public boolean isSupport(Field field) {
		final Class<?> type = field.getType();
		if(!field.isAnnotationPresent(Inline.class) || type.isPrimitive()) {
			return false;
		}
		
		if(field.getType().isArray()) {
			final Class<?> component = type.getComponentType();
			return !component.isPrimitive() && Primitive.getPrimitive(component) == null;
		}
		
		return true;
	}

	@Override
	public void serialize(Object object, Field field, ByteBuffer buffer, Serializer serializer) throws ReflectiveOperationException, BufferOverflowException {
		field.setAccessible(true);
		
		final Object value = field.get(object);
		if(value == null) {
			throw new NullPointerException("Inline value in " + object.getClass().getCanonicalName() + " is null!");
		}
		
		if(field.getType().isArray()) {
			final Object[] array = (Object[]) value;
			for(int i = 0; i < array.length; i++) {
				serializer.serializeObject(array[i], buffer);
			}
		} else if(List.class.isAssignableFrom(field.getType())) {
			final List list = (List) value;
			for(int i = 0; i < list.size(); i++) {
				serializer.serializeObject(list.get(i), buffer);
			}
		} else {
			serializer.serializeObject(value, buffer);
		}
	}
	
	@Override
	public void deserialize(Object object, Field field, ByteBuffer buffer, Deserializer deserializer) throws ReflectiveOperationException, BufferOverflowException {
		field.setAccessible(true);
		
		final Object value = field.get(object);
		
		final Class<?> type = field.getType();
		if(type.isArray()) {
			final Class<?> component = type.getComponentType();
			final Object[] array = (Object[]) value;
			for(int i = 0; i < array.length; i++) {
				array[i] = deserializer.deserializeObject(component, buffer);
			}
		} else if(List.class.isAssignableFrom(type)) {
			ExArrayList list = (ExArrayList) value;
			
			final Class<?> genericType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
			for(int i = 0; i < list.getInitialCapacity(); i++) {
				final Object o = deserializer.deserializeObject(genericType, buffer);
				list.add(o);
			}
		} else {
			final Object o = deserializer.deserializeObject(type, buffer);
			field.set(object, o);
		}
	}

}
