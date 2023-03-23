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
<<<<<<< HEAD
		logger.info("initializing Archi..");
		conf.checkConf();
		s.config(conf.getGitBaseUrl(),
				conf.getGitToken(),
				conf.getArchiProjectUrl(),
				conf.getGitWorkingDir());

		conf.getOAuthBearerFilter();
		
		logger.info("Initialization terminated smoothly.");
=======
		try {
			logger.info("initializing Archi..");
			conf.checkConf();
			logger.info("configuration looks OK. configuring ArchiGraphProxyService.");
			s.config(conf.getGitBaseUrl(),
					conf.getGitToken(),
					conf.getArchiProjectUrl(),
					conf.getGitWorkingDir());
			
			logger.info("configuring OAuthBearerFilter.");
			conf.getOAuthBearerFilter();
			
			logger.info("Initialization terminated smoothly.");
		}catch(Exception ex) {
			logger.error("initializing failure {}", ex);
			throw ex;
		}
>>>>>>> 8dd7364c810eace2210ac18a9d73d6d316bd774d
	}


	public void run(ApplicationArguments args) throws Exception {}

}