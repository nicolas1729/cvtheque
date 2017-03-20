package com.apside.repository;

import com.apside.domain.Competence;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Competence entity.
 */
@SuppressWarnings("unused")
public interface CompetenceRepository extends JpaRepository<Competence,Long> {

    @Query("select competence from Competence competence where competence.user.login = ?#{principal.username}")
    List<Competence> findByUserIsCurrentUser();

}
