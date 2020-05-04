package com.codeoftheweb.salvo;
/*Un repositorio en la API de persistencia (JPA) de Java es una clase que gestiona el almacenamiento
y la recuperación de instancias de clases en una base de datos.*/

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findByUserNameIgnoreCase(String userName);

}


