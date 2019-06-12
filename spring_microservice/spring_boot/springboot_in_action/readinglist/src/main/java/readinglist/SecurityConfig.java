package readinglist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.
builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Profile("production")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private ReaderRepository readerRepository;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                .antMatchers("/").access("hasRole('READER')") //需要读角色
                .antMatchers("/shutdown").access("hasRole('ADMIN')")//只有ADMIN角色才能ShutDown
                .antMatchers("/**").permitAll()//允许 所有人访问
            .and()
                .formLogin()  //设置登录路径
                    .loginPage("/login")
                    .failureUrl("/login?error=true");//带error属性表示出了什么错
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(new UserDetailsService() {
                @Override
                public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException {
                    return readerRepository.findById(username).get();//基于数据库，内存，LDAP
                }
            })
                    .and()
                    .inMemoryAuthentication()
                    .withUser("admin").password("s3cr3t")//添加内存管理员账号
                    .roles("ADMIN", "READER");
    }
}