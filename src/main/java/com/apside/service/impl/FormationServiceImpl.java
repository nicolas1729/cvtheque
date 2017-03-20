package com.apside.service.impl;

import com.apside.service.FormationService;
import com.apside.domain.Formation;
import com.apside.repository.FormationRepository;
import com.apside.repository.search.FormationSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Formation.
 */
@Service
@Transactional
public class FormationServiceImpl implements FormationService{

    private final Logger log = LoggerFactory.getLogger(FormationServiceImpl.class);
    
    @Inject
    private FormationRepository formationRepository;

    @Inject
    private FormationSearchRepository formationSearchRepository;

    /**
     * Save a formation.
     *
     * @param formation the entity to save
     * @return the persisted entity
     */
    public Formation save(Formation formation) {
        log.debug("Request to save Formation : {}", formation);
        Formation result = formationRepository.save(formation);
        formationSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the formations.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Formation> findAll(Pageable pageable) {
        log.debug("Request to get all Formations");
        Page<Formation> result = formationRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one formation by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Formation findOne(Long id) {
        log.debug("Request to get Formation : {}", id);
        Formation formation = formationRepository.findOne(id);
        return formation;
    }

    /**
     *  Delete the  formation by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Formation : {}", id);
        formationRepository.delete(id);
        formationSearchRepository.delete(id);
    }

    /**
     * Search for the formation corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Formation> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Formations for query {}", query);
        Page<Formation> result = formationSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
