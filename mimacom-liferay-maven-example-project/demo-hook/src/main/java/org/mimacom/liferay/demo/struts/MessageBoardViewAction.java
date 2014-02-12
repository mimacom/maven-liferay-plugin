package org.mimacom.liferay.demo.struts;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.struts.StrutsPortletAction;

public class MessageBoardViewAction extends com.liferay.portal.kernel.struts.BaseStrutsPortletAction {
	public void processAction(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		originalStrutsPortletAction.processAction(originalStrutsPortletAction,
				portletConfig, actionRequest, actionResponse);
	}

	public String render(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.info("Executing custom struts action...");
		return originalStrutsPortletAction.render(null, portletConfig,
				renderRequest, renderResponse);
	}

	public void serveResource(StrutsPortletAction originalStrutsPortletAction,
			PortletConfig portletConfig, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws Exception {
		originalStrutsPortletAction.serveResource(originalStrutsPortletAction,
				portletConfig, resourceRequest, resourceResponse);
	}

	Log _log = LogFactoryUtil.getLog(MessageBoardViewAction.class);
}
