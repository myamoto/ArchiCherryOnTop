package org.toolup.archi;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.toolup.archi.web.rest.gallery.ArchiGraphProxyService;
import org.toolup.secu.oauth.jwt.oidc.JWTBuilderOIDC;

@SpringBootApplication
public class ArchiApplication implements ApplicationRunner{

	@Autowired
	ArchiConf  conf;

	@Autowired
	ArchiGraphProxyService s;

	private static Logger logger = LoggerFactory.getLogger(ArchiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ArchiApplication.class, args);
	}

	
	@PostConstruct
	public void afterConstruct() throws Exception {
		logger.info("initializing Archi..");
		conf.checkConf();
		s.config(conf.getGitBaseUrl(),
				conf.getGitToken(),
				conf.getArchiProjectUrl(),
				conf.getGitWorkingDir());

		if(conf.isBearerFilterEnabled()) {
			String pubKeyUrl = conf.getOAuthBearerPubKeyUrl();
			System.setProperty(JWTBuilderOIDC.OAUTH_PUBLIC_KEY_URL_PARAM, pubKeyUrl);
			logger.info("security by OAuth Bearer is enabled.");
			logger.info("OOAuth Bearer token signature should match public keys at : {}", pubKeyUrl);
		}else {
			logger.info("security is disabled.");
		}
		logger.info("Initialization terminated smoothly.");
	}


	@Override
	public void run(ApplicationArguments args) throws Exception {
		// TODO Auto-generated method stub
		
	}

}