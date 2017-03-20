package com.apside.repository;

import com.apside.domain.Experience;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Experience entity.
 */
@SuppressWarnings("unused")
public interface ExperienceRepository extends JpaRepository<Experience,Long> {

    @Query("select experience from Experience experience where experience.user.login = ?#{principal.username}")
    List<Experience> findByUserIsCurrentUser();

}
