package com.apside.service;

import com.apside.domain.Experience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Experience.
 */
public interface ExperienceService {

    /**
     * Save a experience.
     *
     * @param experience the entity to save
     * @return the persisted entity
     */
    Experience save(Experience experience);

    /**
     *  Get all the experiences.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Experience> findAll(Pageable pageable);

    /**
     *  Get the "id" experience.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Experience findOne(Long id);

    /**
     *  Delete the "id" experience.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the experience corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Experience> search(String query, Pageable pageable);
}
