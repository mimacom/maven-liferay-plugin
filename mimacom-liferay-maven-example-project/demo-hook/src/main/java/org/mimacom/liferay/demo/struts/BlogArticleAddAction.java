package org.mimacom.liferay.demo.struts;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.struts.StrutsPortletAction;
import com.liferay.portlet.blogs.EntryTitleException;

import javax.portlet.*;

public class BlogArticleAddAction extends com.liferay.portal.kernel.struts.BaseStrutsPortletAction {

    private static final String[] BANNED_WORDS = {"beer", "vodka", "alcohol"};

    @Override
    public void processAction(StrutsPortletAction originalStrutsPortletAction,
                              PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse)
            throws Exception {

        String title = actionRequest.getParameter("title").toLowerCase();

        for (String s : BANNED_WORDS) {
            if (title.contains(s)) {
                SessionErrors.add(actionRequest, EntryTitleException.class.getCanonicalName());
                return;
            }
        }

        originalStrutsPortletAction.processAction(originalStrutsPortletAction, portletConfig, actionRequest, actionResponse);
    }

    @Override
    public String render(StrutsPortletAction originalStrutsPortletAction,
                         PortletConfig portletConfig, RenderRequest renderRequest,
                         RenderResponse renderResponse) throws Exception {
        return originalStrutsPortletAction.render(null, portletConfig,
                renderRequest, renderResponse);
    }


    @Override
    public void serveResource(StrutsPortletAction originalStrutsPortletAction,
                              PortletConfig portletConfig, ResourceRequest resourceRequest,
                              ResourceResponse resourceResponse) throws Exception {
        originalStrutsPortletAction.serveResource(originalStrutsPortletAction,
                portletConfig, resourceRequest, resourceResponse);
    }

    Log _log = LogFactoryUtil.getLog(BlogArticleAddAction.class);
}
