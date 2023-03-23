package org.toolup.archi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.toolup.io.properties.PropertiesUtilsException;
import org.toolup.secu.oauth.OAuthBearerFilter;
import org.toolup.secu.oauth.OAuthException;
import org.toolup.secu.oauth.jwt.forge.JWTForgeFactory;
import org.toolup.secu.oauth.jwt.parse.keys.UrlKeyCache;

@Service
public class ArchiConf {

	private static Logger logger = LoggerFactory.getLogger(ArchiConf.class);

	public enum OAuthBearerFilterStrategy {disabled, embeddedGenToken, externalGenToken};

	private enum PROP{
		
		gitBaseUrl, gitToken, gitProjectUrl, gitWorkingDirectory, bearerFilterStrategy, oauthBearerPubKeyUrl(false);
		
		boolean mandatory = true;
		private PROP(boolean mandatory) {
			this.mandatory = mandatory;
		}
		
		private PROP() {}
		
		public boolean isMandatory() {
			return mandatory;
		}
	};

	private final static String PROP_PFX = "archicherryontop.";

	@Autowired
	Environment  env;

	private boolean oauthBearerFilterInitialized;
	private OAuthBearerFilter oauthBearerFilter;
	private JWTForgeFactory jwtForge; 

	public void checkConf() throws PropertiesUtilsException {
		List<String> missing = new ArrayList<>();
		for (PROP p : PROP.values()) {
			if(!p.isMandatory()) continue;
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

	public OAuthBearerFilterStrategy getBearerFilterStrategy() throws PropertiesUtilsException {
		String value = getMandatoryString(PROP.bearerFilterStrategy);
		try {
			return OAuthBearerFilterStrategy.valueOf(value);
		}catch(IllegalArgumentException ex) {
			throw new PropertiesUtilsException(String.format("Invalid value '%s' for property %s. possible values are : %s"
					, value
					, PROP.bearerFilterStrategy
					, Arrays.asList(OAuthBearerFilterStrategy.values())), ex);
		}
	}

	private String getMandatoryString(PROP propKey) throws PropertiesUtilsException {
		String key = getKey(propKey);
		if (!env.containsProperty(key)) throw new PropertiesUtilsException("Required prop not found " + key);
		return env.getRequiredProperty(key);
	}

	private String getKey(PROP propKey) {
		return PROP_PFX + propKey.name();
	}

	public JWTForgeFactory getJWTForge() throws OAuthException {
		if(jwtForge == null) {
			jwtForge = JWTForgeFactory.newInstance();
		}
		return jwtForge;
	}

	public Filter getOAuthBearerFilter() throws OAuthException {
		try {
			if(!oauthBearerFilterInitialized) {
				oauthBearerFilterInitialized = true;
				
				switch(getBearerFilterStrategy()) {
					case disabled :
						oauthBearerFilter = null;
						logger.warn("API security : OAuth Bearer filter is disabled.");
						break;
					case embeddedGenToken :
						oauthBearerFilter = new OAuthBearerFilter();
						logger.info("API security -> OAuth Bearer filter is enabled and configured with embedded token generator.");
						logger.info("API security -> Signature check will be made against embedded public key.");
						logger.info("API security -> Pub key : {}", oauthBearerFilter.getDefaultPublicKey());
						
						break;
					case externalGenToken :
						String pubKeyUrl = getOAuthBearerPubKeyUrl();
	
						System.setProperty(UrlKeyCache.OAUTH_PUBLIC_KEY_URL_PARAM, pubKeyUrl);
						oauthBearerFilter = new OAuthBearerFilter();
						
						logger.info("API security -> OAuth Bearer filter is enabled and configured with external token generator.");
						logger.info("API security -> Signature check will be made against external public keys at url {}.", pubKeyUrl);
						logger.info("API security -> Pub key : {}", oauthBearerFilter.getDefaultPublicKey());
						
						break;
				}
			}
		}catch(PropertiesUtilsException ex) {
			throw new OAuthException(ex);
		}
		return oauthBearerFilter;
	}
}
