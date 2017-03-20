package com.apside.service.impl;

import com.apside.service.ExperienceService;
import com.apside.domain.Experience;
import com.apside.repository.ExperienceRepository;
import com.apside.repository.search.ExperienceSearchRepository;
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
 * Service Implementation for managing Experience.
 */
@Service
@Transactional
public class ExperienceServiceImpl implements ExperienceService{

    private final Logger log = LoggerFactory.getLogger(ExperienceServiceImpl.class);
    
    @Inject
    private ExperienceRepository experienceRepository;

    @Inject
    private ExperienceSearchRepository experienceSearchRepository;

    /**
     * Save a experience.
     *
     * @param experience the entity to save
     * @return the persisted entity
     */
    public Experience save(Experience experience) {
        log.debug("Request to save Experience : {}", experience);
        Experience result = experienceRepository.save(experience);
        experienceSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the experiences.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Experience> findAll(Pageable pageable) {
        log.debug("Request to get all Experiences");
        Page<Experience> result = experienceRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one experience by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Experience findOne(Long id) {
        log.debug("Request to get Experience : {}", id);
        Experience experience = experienceRepository.findOne(id);
        return experience;
    }

    /**
     *  Delete the  experience by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Experience : {}", id);
        experienceRepository.delete(id);
        experienceSearchRepository.delete(id);
    }

    /**
     * Search for the experience corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Experience> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Experiences for query {}", query);
        Page<Experience> result = experienceSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
