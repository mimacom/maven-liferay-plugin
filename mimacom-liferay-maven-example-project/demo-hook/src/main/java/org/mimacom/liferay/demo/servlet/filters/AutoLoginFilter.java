package org.mimacom.liferay.demo.servlet.filters;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.AutoLogin;
import com.liferay.portal.security.auth.AutoLoginException;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;


public class AutoLoginFilter implements AutoLogin {

    @Override
    public String[] handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws AutoLoginException {
        return new String[0];        //TODO
    }

    public String[] login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws AutoLoginException {
        try {

            User user = PortalUtil.getUser(httpServletRequest);
            if (user == null && httpServletRequest.getRequestURI().contains("/autologin")) {
                user = UserLocalServiceUtil.getUserByScreenName(PortalUtil.getCompanyId(httpServletRequest), "test");
                String[] credentials = new String[]{String.valueOf(user.getUserId()), user.getPassword(), "true"};
                _log.info("login(): credentials=" + Arrays.asList(credentials));

                return credentials;
            }

        } catch (Exception e) {
            _log.error(e);
        }
        return null;
    }

    Log _log = LogFactoryUtil.getLog(AutoLoginFilter.class);

}
