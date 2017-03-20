package com.apside.service;

import com.apside.domain.Formation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Formation.
 */
public interface FormationService {

    /**
     * Save a formation.
     *
     * @param formation the entity to save
     * @return the persisted entity
     */
    Formation save(Formation formation);

    /**
     *  Get all the formations.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Formation> findAll(Pageable pageable);

    /**
     *  Get the "id" formation.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Formation findOne(Long id);

    /**
     *  Delete the "id" formation.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the formation corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Formation> search(String query, Pageable pageable);
}
