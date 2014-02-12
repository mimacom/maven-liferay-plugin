package org.mimacom.liferay.demo.model.listeners;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class UserModelListener implements
		com.liferay.portal.model.ModelListener {

	public void onAfterAddAssociation(Object classPK,
			String associationClassName, Object associationClassPK)
			throws ModelListenerException {
		// TODO Auto-generated method stub

	}

	public void onAfterCreate(Object model) throws ModelListenerException {
		User user = (User) model;
		_log.info("Executing code after creation of user : "
				+ user.getEmailAddress());

	}

	public void onAfterRemove(Object model) throws ModelListenerException {
		// TODO Auto-generated method stub

	}

	public void onAfterRemoveAssociation(Object classPK,
			String associationClassName, Object associationClassPK)
			throws ModelListenerException {
		// TODO Auto-generated method stub

	}

	public void onAfterUpdate(Object model) throws ModelListenerException {
		User user = (User) model;
		_log.info("Executing code after update of user : "
				+ user.getEmailAddress());

	}

	public void onBeforeAddAssociation(Object classPK,
			String associationClassName, Object associationClassPK)
			throws ModelListenerException {
		// TODO Auto-generated method stub

	}

	public void onBeforeCreate(Object model) throws ModelListenerException {
		// TODO Auto-generated method stub

	}

	public void onBeforeRemove(Object model) throws ModelListenerException {
		// TODO Auto-generated method stub

	}

	public void onBeforeRemoveAssociation(Object classPK,
			String associationClassName, Object associationClassPK)
			throws ModelListenerException {
		// TODO Auto-generated method stub

	}

	public void onBeforeUpdate(Object model) throws ModelListenerException {
		// TODO Auto-generated method stub

	}

	Log _log = LogFactoryUtil.getLog(UserModelListener.class);
}
