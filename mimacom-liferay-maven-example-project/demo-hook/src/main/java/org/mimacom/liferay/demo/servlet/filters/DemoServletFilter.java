package org.mimacom.liferay.demo.servlet.filters;

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
