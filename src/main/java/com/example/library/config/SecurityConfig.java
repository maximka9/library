package com.example.library.config;
import com.example.library.services.CustomReaderDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  extends  WebSecurityConfigurerAdapter{
    private final CustomReaderDetailsService readerDetailsService;
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .authorizeRequests()
                //.antMatchers("/formulary/new").hasRole("ADMIN")
                //.antMatchers("/formulary/**").hasRole("USER")
                //.antMatchers("/formulary/report").hasRole("ADMIN")
                .antMatchers("/registration").permitAll() // Разрешить доступ к странице /registration всем пользователям
                .antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();

    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws  Exception{
        auth.userDetailsService(readerDetailsService)
                .passwordEncoder(passwordEncoder());

    }
@Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(9);
    }
}