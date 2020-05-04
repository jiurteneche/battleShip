package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//ESTA CLASE ES LA QUE TE AUTENTIFICA (es decir, dice quién está autentificado como ADMIN y quién como USER)
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    //Este @Autowired lo hacemos porque queremos que copie la tabla de Player de la base de datos para poder determinar las autorizaciones
    @Autowired
    PlayerRepository playerRepository;

    //En este método estoy diciendo quiénes van a ser USER y quién va a ser ADMIN (estoy diferenciando ROLES, es decir AUTENTICANDO,
    // para luego poder autorizarlos correspondientemente)
    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService( inputUserName -> {
            Player player = playerRepository.findByUserNameIgnoreCase(inputUserName);
            if (player != null) {

                if(player.getUserName().equals("jiurteneche@gmail.com")) {
                    return new User(player.getUserName(), player.getPassword(), AuthorityUtils.createAuthorityList("ADMIN"));
                } else {
                    return new User(player.getUserName(), player.getPassword(), AuthorityUtils.createAuthorityList("USER"));
                }

            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputUserName);
            }
        });
    }
}
