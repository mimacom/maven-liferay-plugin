package org.mimacom.liferay.demo.servlet.filters;

/*
 * Copyright (c) 2014 mimacom a.g.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.servlet.FilterChain;

import org.mimacom.liferay.demo.model.MimacomDemoObject;
import org.mimacom.liferay.demo.service.MimacomDemoObjectLocalServiceUtil;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class DemoServletFilter extends
		com.liferay.portal.kernel.servlet.BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response,
			FilterChain filterChain) throws Exception {

		// add object with Liferay Demo Service and show current object list
		// size
		MimacomDemoObject object = MimacomDemoObjectLocalServiceUtil
				.createMimacomDemoObject(CounterLocalServiceUtil
						.increment(MimacomDemoObject.class.getName()));
		MimacomDemoObjectLocalServiceUtil.addMimacomDemoObject(object);
		_log.info("Found objects : ..."
				+ MimacomDemoObjectLocalServiceUtil.getMimacomDemoObjects(-1,
						-1).size());

		processFilter(DemoServletFilter.class, request, response, filterChain);

	}

	Log _log = LogFactoryUtil.getLog(DemoServletFilter.class);

}
