package org.mimacom.liferay.demo.events;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Theme;
import com.liferay.portal.service.ThemeLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

public class PostRequestAction extends Action {

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ActionException {
		
		
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			Theme theme = ThemeLocalServiceUtil.getTheme(themeDisplay.getCompanyId(), "demotheme_WAR_demotheme", false);
		
		themeDisplay.setLookAndFeel(theme, themeDisplay.getColorScheme());
		
		request.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	Log _log = LogFactoryUtil.getLog(PostRequestAction.class);

}
