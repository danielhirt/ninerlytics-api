package com.group6.api;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/*
 * Disables Spring security features to allow cross origin requests. 
 * NOTE: This removes protection against cross-site forgery. Only for use in development. 
 */
@EnableWebSecurity
@EnableWebMvc
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		 http.headers().addHeaderWriter(
                 new StaticHeadersWriter("Access-Control-Allow-Origin", "*"));
	}
	
}
