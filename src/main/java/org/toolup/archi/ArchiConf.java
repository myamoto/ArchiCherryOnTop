package org.toolup.archi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.toolup.io.properties.PropertiesUtilsException;

@Service
public class ArchiConf {
	
	private enum PROP{gitBaseUrl, gitToken, gitProjectUrl, gitWorkingDirectory, oauthBearerFilterEnabled, oauthBearerPubKeyUrl};
	
	private final static String PROP_PFX = "archicherryontop.";
	
	@Autowired
	Environment  env;
	
	public void checkConf() throws PropertiesUtilsException {
		List<String> missing = new ArrayList<>();
		for (PROP p : PROP.values()) {
			String key = getKey(p);
			if (!env.containsProperty(key))
				missing.add(key);
				
		}
		if(!missing.isEmpty())
			throw new PropertiesUtilsException("Required properties not found : " + missing);
	}
	
	public String getGitBaseUrl() throws PropertiesUtilsException {
		return getMandatoryString(PROP.gitBaseUrl);
	}
	
	public String getGitToken() throws PropertiesUtilsException {
		return getMandatoryString(PROP.gitToken);
	}
	
	public String getArchiProjectUrl() throws PropertiesUtilsException {
		return getMandatoryString(PROP.gitProjectUrl);
	}
	
	public String getGitWorkingDir() throws PropertiesUtilsException {
		return getMandatoryString(PROP.gitWorkingDirectory);
	}
	
	public String getOAuthBearerPubKeyUrl() throws PropertiesUtilsException {
		return getMandatoryString(PROP.oauthBearerPubKeyUrl);
	}
	
	public boolean isBearerFilterEnabled() throws PropertiesUtilsException {
		return getMandatoryBoolean(PROP.oauthBearerFilterEnabled);
	}
	
	private String getMandatoryString(PROP propKey) throws PropertiesUtilsException {
		String key = getKey(propKey);
		if (!env.containsProperty(key)) throw new PropertiesUtilsException("Required prop not found " + key);
		return env.getRequiredProperty(key);
	}
	
	private String getKey(PROP propKey) {
		return PROP_PFX + propKey.name();
	}

	private boolean getMandatoryBoolean(PROP propKey) throws PropertiesUtilsException {
		String key = getKey(propKey);
		if (!env.containsProperty(key)) throw new PropertiesUtilsException("Required prop not found " + key);
		if (!Arrays.asList("FALSE", "TRUE").contains(env.getRequiredProperty(key).toUpperCase())) 
			throw new PropertiesUtilsException("Required prop not found " + key);
		return Boolean.parseBoolean(env.getRequiredProperty(key));
	}
}
