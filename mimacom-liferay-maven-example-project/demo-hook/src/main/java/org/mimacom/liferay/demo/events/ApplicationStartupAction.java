package org.mimacom.liferay.demo.events;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ApplicationStartupAction extends com.liferay.portal.kernel.events.SimpleAction {

	@Override
	public void run(String[] ids) throws ActionException {
		_log.info("Running....");

	}

	Log _log = LogFactoryUtil.getLog(ApplicationStartupAction.class);

}
