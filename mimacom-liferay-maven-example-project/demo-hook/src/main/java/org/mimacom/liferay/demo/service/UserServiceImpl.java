package org.mimacom.liferay.demo.service;

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

import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.model.*;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserService;
import com.liferay.portlet.announcements.model.AnnouncementsDelivery;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.Locale;


public class UserServiceImpl extends com.liferay.portal.service.UserServiceWrapper {

    public UserServiceImpl(UserService userService) {
        super(userService);
    }


    @Override
    public User addUser(long companyId, boolean autoPassword, String password1, String password2, boolean autoScreenName,
                        String screenName, String emailAddress, long facebookId, String openId, Locale locale,
                        String firstName, String middleName, String lastName, int prefixId, int suffixId, boolean male,
                        int birthdayMonth, int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
                        long[] organizationIds, long[] roleIds, long[] userGroupIds, boolean sendEmail,
                        ServiceContext serviceContext)
            throws PortalException, SystemException {

        sendEmailToAdministrator(screenName);
        return super.addUser(companyId, autoPassword, password1, password2, autoScreenName, screenName, emailAddress,
                facebookId, openId, locale, firstName, middleName, lastName, prefixId, suffixId, male, birthdayMonth,
                birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds, roleIds, userGroupIds, sendEmail,
                serviceContext);
    }

    @Override
    public User addUser(long companyId, boolean autoPassword, String password1, String password2, boolean autoScreenName,
                        String screenName, String emailAddress, long facebookId, String openId, Locale locale,
                        String firstName, String middleName, String lastName, int prefixId, int suffixId, boolean male,
                        int birthdayMonth, int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
                        long[] organizationIds, long[] roleIds, long[] userGroupIds, List<Address> addresses,
                        List<EmailAddress> emailAddresses, List<Phone> phones, List<Website> websites,
                        List<AnnouncementsDelivery> announcementsDelivers, boolean sendEmail,
                        ServiceContext serviceContext)
            throws PortalException, SystemException {

        sendEmailToAdministrator(screenName);
        return super.addUser(companyId, autoPassword, password1, password2, autoScreenName, screenName, emailAddress,
                facebookId, openId, locale, firstName, middleName, lastName, prefixId, suffixId, male, birthdayMonth,
                birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds, roleIds, userGroupIds, addresses,
                emailAddresses, phones, websites, announcementsDelivers, sendEmail, serviceContext);
    }

    private void sendEmailToAdministrator(String screenName) {

        _log.warn(screenName);
        try {

            MailMessage mail = new MailMessage();
            mail.setFrom(new InternetAddress("dev@mimacom.org"));
            mail.setTo(new InternetAddress("administrator.liferay@mimacom.com"));
            mail.setSubject("new user added");
            mail.setBody("adding new user: " + screenName);

            MailServiceUtil.sendEmail(mail);

        } catch (AddressException e) {
            _log.error("cannot send email");
        }
    }

    Log _log = LogFactoryUtil.getLog(UserLocalServiceImpl.class);

}
