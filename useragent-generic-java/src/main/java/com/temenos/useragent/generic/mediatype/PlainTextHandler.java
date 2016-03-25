package com.temenos.useragent.generic.mediatype;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.util.Collections;
import java.util.List;

import com.temenos.useragent.generic.Link;
import com.temenos.useragent.generic.PayloadHandler;
import com.temenos.useragent.generic.internal.EntityWrapper;
import com.temenos.useragent.generic.internal.NullEntityWrapper;

public class PlainTextHandler implements PayloadHandler {

	private String plainText = null;
	private String parameter;

	@Override
	public boolean isCollection() {
		return false;
	}

	@Override
	public List<Link> links() {
		return Collections.emptyList();
	}

	@Override
	public List<EntityWrapper> entities() {
		return Collections.emptyList();
	}

	@Override
	public EntityWrapper entity() {
		return new NullEntityWrapper();
	}

	@Override
	public void setPayload(String payload) {
		plainText = payload;
	}

	@Override
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
