/*
 * This file is part of the Soapbox Race World core source code.
 * If you use any of this code for third-party purposes, please provide attribution.
 * Copyright (c) 2020.
 */

package com.soapboxrace.core.bo;

import com.soapboxrace.core.dao.LoginAnnouncementDAO;
import com.soapboxrace.core.jpa.LoginAnnouncementEntity;
import com.soapboxrace.jaxb.http.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class LoginAnnouncementBO {

    @EJB
    private LoginAnnouncementDAO loginAnnoucementDao;

    @EJB
    private ParameterBO parameterBO;

    public LoginAnnouncementsDefinition getLoginAnnouncements(String language) {
        ArrayOfLoginAnnouncementDefinition arrayOfLoginAnnouncementDefinition =
                new ArrayOfLoginAnnouncementDefinition();

        List<LoginAnnouncementEntity> listOfLoginAnnoucement = loginAnnoucementDao.findAllByLanguage(language);
        for (LoginAnnouncementEntity entity : listOfLoginAnnoucement) {
            LoginAnnouncementDefinition loginAnnouncementDefinition = new LoginAnnouncementDefinition();
            loginAnnouncementDefinition.setContext(LoginAnnouncementContext.fromValue(entity.getContext()));
            loginAnnouncementDefinition.setId(entity.getId());
            loginAnnouncementDefinition.setImageChecksum(-1);
            loginAnnouncementDefinition.setImageUrl(entity.getImageUrl());
            loginAnnouncementDefinition.setType(LoginAnnouncementType.fromValue(entity.getType()));
            loginAnnouncementDefinition.setTarget(entity.getTarget());
            arrayOfLoginAnnouncementDefinition.getLoginAnnouncementDefinition().add(loginAnnouncementDefinition);
        }

        LoginAnnouncementsDefinition loginAnnouncementsDefinition = new LoginAnnouncementsDefinition();
        loginAnnouncementsDefinition.setAnnouncements(arrayOfLoginAnnouncementDefinition);
        loginAnnouncementsDefinition.setImagesPath(parameterBO.getStrParam("ANNOUNCEMENT_DOMAIN"));
        return loginAnnouncementsDefinition;
    }
}
