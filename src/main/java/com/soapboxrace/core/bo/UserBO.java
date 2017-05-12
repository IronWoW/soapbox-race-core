package com.soapboxrace.core.bo;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.soapboxrace.core.dao.UserDAO;
import com.soapboxrace.core.jpa.PersonaEntity;
import com.soapboxrace.core.jpa.UserEntity;
import com.soapboxrace.jaxb.http.ArrayOfProfileData;
import com.soapboxrace.jaxb.http.ProfileData;
import com.soapboxrace.jaxb.http.User;
import com.soapboxrace.jaxb.http.UserInfo;
import com.soapboxrace.xmpp.openfire.OpenFireRestApiCli;

@Stateless
public class UserBO {

	@EJB
	private UserDAO userDao;

	@EJB
	private OpenFireRestApiCli xmppRestApiCli;

	public void createXmppUser(UserInfo userInfo) {
		String securityToken = userInfo.getUser().getSecurityToken();
		String xmppPasswd = securityToken.substring(0, 16);
		List<ProfileData> profileData = userInfo.getPersonas().getProfileData();
		for (ProfileData persona : profileData) {
			createXmppUser(persona.getPersonaId(), xmppPasswd);
		}
	}

	public void createXmppUser(Long personaId, String xmppPasswd) {
		xmppRestApiCli.createUpdatePersona(personaId, xmppPasswd);
	}

	public void createUser(String email, String passwd) {
		UserEntity userEntity = new UserEntity();
		userEntity.setEmail(email);
		userEntity.setPassword(passwd);
		userDao.insert(userEntity);
	}

	public UserInfo secureLoginPersona(Long userId, Long personaId) {
		UserInfo userInfo = new UserInfo();
		userInfo.setPersonas(new ArrayOfProfileData());
		com.soapboxrace.jaxb.http.User user = new com.soapboxrace.jaxb.http.User();
		user.setUserId(userId);
		userInfo.setUser(user);
		return userInfo;
	}

	public UserInfo getUserById(Long userId) {
		UserEntity userEntity = userDao.findById(userId);
		UserInfo userInfo = new UserInfo();
		ArrayOfProfileData arrayOfProfileData = new ArrayOfProfileData();
		List<PersonaEntity> listOfProfile = userEntity.getListOfProfile();
		for (PersonaEntity personaEntity : listOfProfile) {
			// switch to apache beanutils copy
			ProfileData profileData = new ProfileData();
			profileData.setName(personaEntity.getName());
			profileData.setCash(personaEntity.getCash());
			profileData.setIconIndex(personaEntity.getIconIndex());
			profileData.setPersonaId(personaEntity.getPersonaId());
			profileData.setLevel(personaEntity.getLevel());
			arrayOfProfileData.getProfileData().add(profileData);
		}
		userInfo.setPersonas(arrayOfProfileData);
		User user = new User();
		user.setUserId(userId);
		userInfo.setUser(user);
		return userInfo;
	}

}