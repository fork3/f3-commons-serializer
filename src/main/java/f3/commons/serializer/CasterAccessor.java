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

import java.util.HashMap;
import java.util.Map;

import f3.commons.reflection.Primitive;
import f3.commons.serializer.casters.BoolByteCaster;
import f3.commons.serializer.casters.BoolCharCaster;
import f3.commons.serializer.casters.BoolDoubleCaster;
import f3.commons.serializer.casters.BoolFloatCaster;
import f3.commons.serializer.casters.BoolIntCaster;
import f3.commons.serializer.casters.BoolLongCaster;
import f3.commons.serializer.casters.BoolShortCaster;
import f3.commons.serializer.casters.CharBoolCaster;
import f3.commons.serializer.casters.CharByteCaster;
import f3.commons.serializer.casters.CharDoubleCaster;
import f3.commons.serializer.casters.CharFloatCaster;
import f3.commons.serializer.casters.CharIntCaster;
import f3.commons.serializer.casters.CharLongCaster;
import f3.commons.serializer.casters.CharShortCaster;
import f3.commons.serializer.casters.NumberBoolCaster;
import f3.commons.serializer.casters.NumberByteCaster;
import f3.commons.serializer.casters.NumberCharCaster;
import f3.commons.serializer.casters.NumberDoubleCaster;
import f3.commons.serializer.casters.NumberFloatCaster;
import f3.commons.serializer.casters.NumberIntCaster;
import f3.commons.serializer.casters.NumberShortCaster;
import lombok.Getter;

/**
 * @author n3k0nation
 *
 */
public class CasterAccessor {
	
	@Getter private final static CasterAccessor instance = new CasterAccessor();
	
	private final Map<Class<?>, Map<Class<?>, ICaster<?, ?>>> casters = new HashMap<>();
	
	private CasterAccessor() {
		getSubmap(Boolean.class).put(Byte.class, new BoolByteCaster());
		getSubmap(Boolean.class).put(Character.class, new BoolCharCaster());
		getSubmap(Boolean.class).put(Double.class, new BoolDoubleCaster());
		getSubmap(Boolean.class).put(Float.class, new BoolFloatCaster());
		getSubmap(Boolean.class).put(Integer.class, new BoolIntCaster());
		getSubmap(Boolean.class).put(Long.class, new BoolLongCaster());
		getSubmap(Boolean.class).put(Short.class, new BoolShortCaster());
		getSubmap(Character.class).put(Boolean.class, new CharBoolCaster());
		getSubmap(Character.class).put(Byte.class, new CharByteCaster());
		getSubmap(Character.class).put(Double.class, new CharDoubleCaster());
		getSubmap(Character.class).put(Float.class, new CharFloatCaster());
		getSubmap(Character.class).put(Integer.class, new CharIntCaster());
		getSubmap(Character.class).put(Long.class, new CharLongCaster());
		getSubmap(Character.class).put(Short.class, new CharShortCaster());
		getSubmap(Number.class).put(Boolean.class, new NumberBoolCaster());
		getSubmap(Number.class).put(Byte.class, new NumberByteCaster());
		getSubmap(Number.class).put(Character.class, new NumberCharCaster());
		getSubmap(Number.class).put(Double.class, new NumberDoubleCaster());
		getSubmap(Number.class).put(Float.class, new NumberFloatCaster());
		getSubmap(Number.class).put(Integer.class, new NumberIntCaster());
		getSubmap(Number.class).put(Short.class, new NumberShortCaster());
	}
	
	private Map<Class<?>, ICaster<?, ?>> getSubmap(Class<?> originType) {
		Map<Class<?>, ICaster<?, ?>> map = casters.get(originType);
		if(map == null) {
			casters.put(originType, map = new HashMap<>());
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public <Origin, Target> ICaster<Origin, Target> getCaster(Class<Origin> _originType, Class<Target> targetType) {
		Class<?> originType = _originType;
		if(originType.isPrimitive()) {
			originType = Primitive.getWrap(originType);
		}
		
		final Map<Class<?>, ICaster<?, ?>> map = casters.get(originType);
		if(map == null) {
			return null;
		}
		
		return (ICaster<Origin, Target>) map.get(targetType);
	}
}
