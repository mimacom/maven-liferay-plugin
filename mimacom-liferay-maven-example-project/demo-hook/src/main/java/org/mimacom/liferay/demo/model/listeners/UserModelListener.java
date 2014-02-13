package org.mimacom.liferay.demo.model.listeners;

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
