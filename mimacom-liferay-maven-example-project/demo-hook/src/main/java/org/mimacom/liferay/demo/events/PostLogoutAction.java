package org.mimacom.liferay.demo.events;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class PostLogoutAction extends Action {

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ActionException {
		_log.info("Running logout post action....");

	}

	Log _log = LogFactoryUtil.getLog(PostLogoutAction.class);

}
