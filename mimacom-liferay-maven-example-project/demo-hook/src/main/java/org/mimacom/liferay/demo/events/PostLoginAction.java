package org.mimacom.liferay.demo.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PostLoginAction extends Action {

    @Override
    public void run(HttpServletRequest request, HttpServletResponse response)
            throws ActionException {
		_log.info("Running....");

      /*  try {
            User user = PortalUtil.getUser(request);
            response.sendRedirect( "/user/" + user.getScreenName());

        } catch (SystemException e) {
            _log.error(e);
        } catch (IOException e) {
            _log.error(e);
        } catch (PortalException e) {
            _log.error(e);
        }*/
    }

    Log _log = LogFactoryUtil.getLog(PostLoginAction.class);

}
