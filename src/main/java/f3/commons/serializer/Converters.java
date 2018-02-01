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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import f3.commons.serializer.converters.CharConverter;
import f3.commons.serializer.converters.DwordConverter;
import f3.commons.serializer.converters.InlineConverter;
import f3.commons.serializer.converters.QwordConverter;
import f3.commons.serializer.converters.RealConverter;
import f3.commons.serializer.converters.UTF8Converter;
import f3.commons.serializer.converters.WordConverter;
import lombok.Getter;

/**
 * @author n3k0nation
 *
 */
class Converters {
	@Getter private final static List<IConverter> converters = Collections.unmodifiableList(Arrays.asList(
			QwordConverter.getInstance(),
			DwordConverter.getInstance(),
			WordConverter.getInstance(),
			CharConverter.getInstance(),
			RealConverter.getInstance(),
			UTF8Converter.getInstance(),
			InlineConverter.getInstance()
	));
	
	private Converters() {
	}
}
