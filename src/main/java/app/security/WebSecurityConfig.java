package app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import app.security.filters.TokenAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenAuthenticationFilter jwtAuthenticationTokenFilter() {
        return new TokenAuthenticationFilter();
    }


	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .addFilterBefore(jwtAuthenticationTokenFilter(), BasicAuthenticationFilter.class)
            .authorizeRequests()

            .antMatchers(HttpMethod.GET, "/photo/{fileName:.+}").permitAll()
            .antMatchers(HttpMethod.POST, "/register").permitAll()
            .antMatchers(HttpMethod.POST, "/login").permitAll()
            .antMatchers(HttpMethod.POST, "/authenticate").permitAll()
			.anyRequest().authenticated()
            .and()
            .csrf()
            .disable();
	}
}