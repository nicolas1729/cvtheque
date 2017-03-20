package com.apside.repository;

import com.apside.domain.Formation;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Formation entity.
 */
@SuppressWarnings("unused")
public interface FormationRepository extends JpaRepository<Formation,Long> {

    @Query("select formation from Formation formation where formation.user.login = ?#{principal.username}")
    List<Formation> findByUserIsCurrentUser();

}
