package org.toolup.archi.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.toolup.archi.ArchiConf;
import org.toolup.archi.ArchiConf.OAuthBearerFilterStrategy;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	ArchiConf  conf;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
		.antMatchers(HttpMethod.OPTIONS, "/**")
		.antMatchers("/app/**/*.{js,html}")
		.antMatchers("/bower_components/**")
		.antMatchers("/i18n/**")
		.antMatchers("/content/**")
		.antMatchers("/swagger-ui/index.html")
		.antMatchers("/test/**")
		.antMatchers("/h2-console/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http =
		http
		.csrf()
		.disable()
		.headers()
		.frameOptions()
		.disable()		
		.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and();
		
		if(conf.getBearerFilterStrategy() != OAuthBearerFilterStrategy.disabled) {
			http
				.authorizeRequests()
				.antMatchers("/api/**").authenticated()
				.antMatchers("/configuration/ui").permitAll()
			.and()
				.antMatcher("/api/**").addFilterBefore(conf.getOAuthBearerFilter(), UsernamePasswordAuthenticationFilter.class);
		}
	}
	
	
}



