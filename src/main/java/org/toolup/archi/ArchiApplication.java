package org.toolup.archi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.toolup.archi.web.rest.gallery.ArchiGraphProxyService;
import org.toolup.io.properties.PropertiesUtilsException;

@SpringBootApplication
public class ArchiApplication implements ApplicationRunner{
	

	@Autowired
	Environment  env;
	
	@Autowired
	ArchiGraphProxyService s;
	
	private static Logger logger = LoggerFactory.getLogger(ArchiApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(ArchiApplication.class, args);
	}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("initializing Archi..");
		s.config(getMandatoryString("archicherryontop.git.base.url"),
				getMandatoryString("archicherryontop.git.token"),
				getMandatoryString("archicherryontop.git.project.url"),
				getMandatoryString("archicherryontop.git.workingdirectory"));
		logger.info("Initialization terminated smoothly.");
	}

	
	public String getMandatoryString(String val) throws PropertiesUtilsException {
		if (!env.containsProperty(val)) throw new PropertiesUtilsException("Required prop not found " + val);
		return env.getRequiredProperty(val);
	}
	
}