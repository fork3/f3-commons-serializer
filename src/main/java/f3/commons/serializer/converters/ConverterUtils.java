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
package f3.commons.serializer.converters;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import f3.commons.serializer.CasterAccessor;
import f3.commons.serializer.ICaster;

/**
 * @author n3k0nation
 *
 */
class ConverterUtils {
	
	private ConverterUtils() {
	}
	
	static int getArrayLength(Object array, Field field) {
		final f3.commons.serializer.types.Array arrayAnn = field.getAnnotation(f3.commons.serializer.types.Array.class);
		if(arrayAnn != null && arrayAnn.length() != -1) {
			return arrayAnn.length();
		} else {
			return Array.getLength(array);
		}
	}
	
	/**
	 * @param values read data
	 * @param array target array
	 */
	static void setArrayType(Object object, Field field, Object[] values, Object array) throws ReflectiveOperationException {
		final Class<?> type = field.getType();
		final Class<?> component = type.getComponentType();
		final Class<?> valuesType = values.getClass().getComponentType();
		
		final int length = values.length;
		
		if(!component.isPrimitive()) {
			final ICaster caster = CasterAccessor.getInstance().getCaster(valuesType, component);
			for (int i = 0; i < length; i++) {
				Object value = values[i];
				if(caster != null) {
					value = caster.cast(value);
				}
				Array.set(array, i, value);
			}
		} else if (Number.class.isAssignableFrom(valuesType)) {
			if (component.equals(boolean.class)) {
				for (int i = 0; i < length; i++) {
					Array.setBoolean(array, i, ((Number) values[i]).intValue() != 0);
				}
			} else if (component.equals(char.class)) {
				for (int i = 0; i < length; i++) {
					Array.setChar(array, i, (char) ((Number) values[i]).intValue());
				}
			} else if (component.equals(byte.class)) {
				for (int i = 0; i < length; i++) {
					Array.setByte(array, i, ((Number) values[i]).byteValue());
				}
			} else if (component.equals(short.class)) {
				for (int i = 0; i < length; i++) {
					Array.setShort(array, i, ((Number) values[i]).shortValue());
				}
			} else if (component.equals(int.class)) {
				for (int i = 0; i < length; i++) {
					Array.setInt(array, i, ((Number) values[i]).intValue());
				}
			} else if (component.equals(long.class)) {
				for (int i = 0; i < length; i++) {
					Array.setLong(array, i, ((Number) values[i]).longValue());
				}
			} else if (component.equals(float.class)) {
				for (int i = 0; i < length; i++) {
					Array.setFloat(array, i, ((Number) values[i]).floatValue());
				}
			} else if (component.equals(double.class)) {
				for (int i = 0; i < length; i++) {
					Array.setDouble(array, i, ((Number) values[i]).doubleValue());
				}
			}
		} else if (valuesType.equals(Boolean.class) || valuesType.equals(boolean.class)) {
			if (component.equals(boolean.class)) {
				for (int i = 0; i < length; i++) {
					Array.setBoolean(array, i, ((Boolean) values[i]).booleanValue());
				}
			} else if (component.equals(char.class)) {
				for (int i = 0; i < length; i++) {
					Array.setChar(array, i, (char) (((Boolean) values[i]) == Boolean.FALSE ? 0 : 1));
				}
			} else if (component.equals(byte.class)) {
				for (int i = 0; i < length; i++) {
					Array.setByte(array, i, (byte) (((Boolean) values[i]) == Boolean.FALSE ? 0 : 1));
				}
			} else if (component.equals(short.class)) {
				for (int i = 0; i < length; i++) {
					Array.setShort(array, i, (short) (((Boolean) values[i]) == Boolean.FALSE ? 0 : 1));
				}
			} else if (component.equals(int.class)) {
				for (int i = 0; i < length; i++) {
					Array.setInt(array, i, ((Boolean) values[i]) == Boolean.FALSE ? 0 : 1);
				}
			} else if (component.equals(long.class)) {
				for (int i = 0; i < length; i++) {
					Array.setLong(array, i, ((Boolean) values[i]) == Boolean.FALSE ? 0 : 1);
				}
			} else if (component.equals(float.class)) {
				for (int i = 0; i < length; i++) {
					Array.setFloat(array, i, ((Boolean) values[i]) == Boolean.FALSE ? 0 : 1);
				}
			} else if (component.equals(double.class)) {
				for (int i = 0; i < length; i++) {
					Array.setDouble(array, i, ((Boolean) values[i]) == Boolean.FALSE ? 0 : 1);
				}
			}
		} else if (valuesType.equals(Character.class) || valuesType.equals(char.class)) {
			if (component.equals(boolean.class)) {
				for (int i = 0; i < length; i++) {
					Array.setBoolean(array, i, ((Character) values[i]).charValue() != 0);
				}
			} else if (component.equals(char.class)) {
				for (int i = 0; i < length; i++) {
					Array.setChar(array, i, ((Character) values[i]).charValue());
				}
			} else if (component.equals(byte.class)) {
				for (int i = 0; i < length; i++) {
					Array.setByte(array, i, (byte) (((Character) values[i]).charValue() & 0xff));
				}
			} else if (component.equals(short.class)) {
				for (int i = 0; i < length; i++) {
					Array.setShort(array, i, (short) (((Character) values[i]).charValue() & 0xffff));
				}
			} else if (component.equals(int.class)) {
				for (int i = 0; i < length; i++) {
					Array.setInt(array, i, ((Character) values[i]).charValue() & 0xffff);
				}
			} else if (component.equals(long.class)) {
				for (int i = 0; i < length; i++) {
					Array.setLong(array, i, ((Character) values[i]).charValue() & 0xffff);
				}
			} else if (component.equals(float.class)) {
				for (int i = 0; i < length; i++) {
					Array.setFloat(array, i, ((Character) values[i]).charValue());
				}
			} else if (component.equals(double.class)) {
				for (int i = 0; i < length; i++) {
					Array.setDouble(array, i, ((Character) values[i]).charValue());
				}
			}
		}
	}
	
	static void setSimpleType(Object object, Field field, Object value) throws ReflectiveOperationException {
		final Class<?> type = field.getType();
		final Class<?> valueType = value.getClass();
		
		if(type.isPrimitive()) {
			if(Number.class.isAssignableFrom(valueType)) {
				final Number number = (Number) value;
				if(type.equals(boolean.class)) {
					field.setBoolean(object, number.intValue() != 0);
				} else if(type.equals(byte.class)) {
					field.setByte(object, number.byteValue());
				} else if(type.equals(char.class)) {
					field.setChar(object, (char) number.intValue());
				} else if(type.equals(short.class)) {
					field.setShort(object, number.shortValue());
				} else if(type.equals(int.class)) {
					field.setInt(object, number.intValue());
				} else if(type.equals(long.class)) {
					field.setLong(object, number.longValue());
				} else if(type.equals(float.class)) {
					field.setFloat(object, number.floatValue());
				} else if(type.equals(double.class)) {
					field.setDouble(object, number.doubleValue());
				}
			} else if(valueType.equals(Boolean.class) || valueType.equals(boolean.class)) {
				final Boolean bool = (Boolean) value;
				final int intValue = bool == Boolean.FALSE ? 0 : 1;
				if (type.equals(boolean.class)) {
					field.setBoolean(object, bool.booleanValue());
				} else if (type.equals(byte.class)) {
					field.setByte(object, (byte) intValue);
				} else if (type.equals(char.class)) {
					field.setChar(object, (char) intValue);
				} else if (type.equals(short.class)) {
					field.setShort(object, (short) intValue);
				} else if (type.equals(int.class)) {
					field.setInt(object, intValue);
				} else if (type.equals(long.class)) {
					field.setLong(object, intValue);
				} else if (type.equals(float.class)) {
					field.setFloat(object, intValue);
				} else if (type.equals(double.class)) {
					field.setDouble(object, intValue);
				}
			} else if(valueType.equals(Character.class) || valueType.equals(char.class)) {
				final Character character = (Character) value;
				final int intValue = character.charValue() & 0xffff;
				if (type.equals(boolean.class)) {
					field.setBoolean(object, intValue != 0);
				} else if (type.equals(byte.class)) {
					field.setByte(object, (byte) intValue);
				} else if (type.equals(char.class)) {
					field.setChar(object, (char) intValue);
				} else if (type.equals(short.class)) {
					field.setShort(object, (short) intValue);
				} else if (type.equals(int.class)) {
					field.setInt(object, intValue);
				} else if (type.equals(long.class)) {
					field.setLong(object, intValue);
				} else if (type.equals(float.class)) {
					field.setFloat(object, intValue);
				} else if (type.equals(double.class)) {
					field.setDouble(object, intValue);
				}
			}
		} else {
			final ICaster caster = CasterAccessor.getInstance().getCaster(valueType, type);
			if(caster != null) {
				value = caster.cast(value);
			}
			field.set(object, value);
		}
	}
	
}
