package com.apside.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.apside.domain.Experience;
import com.apside.domain.User;
import com.apside.repository.UserRepository;
import com.apside.security.SecurityUtils;
import com.apside.service.ExperienceService;
import com.apside.web.rest.util.HeaderUtil;
import com.apside.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Experience.
 */
@RestController
@RequestMapping("/api")
public class ExperienceResource {

    private final Logger log = LoggerFactory.getLogger(ExperienceResource.class);
        
    @Inject
    private ExperienceService experienceService;
    
    @Inject
    private UserRepository userRepository;

    /**
     * POST  /experiences : Create a new experience.
     *
     * @param experience the experience to create
     * @return the ResponseEntity with status 201 (Created) and with body the new experience, or with status 400 (Bad Request) if the experience has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/experiences")
    @Timed
    public ResponseEntity<Experience> createExperience(@Valid @RequestBody Experience experience) throws URISyntaxException {
        log.debug("REST request to save Experience : {}", experience);
        if (experience.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("experience", "idexists", "A new experience cannot already have an ID")).body(null);
        }
        
        //experienceService.findByUserIsCurrentUser
        String login = SecurityUtils.getCurrentUserLogin();
        Optional<User> user = userRepository.findOneByLogin(login);
        experience.setUser(user.get());
        Experience result = experienceService.save(experience);
        return ResponseEntity.created(new URI("/api/experiences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("experience", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /experiences : Updates an existing experience.
     *
     * @param experience the experience to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated experience,
     * or with status 400 (Bad Request) if the experience is not valid,
     * or with status 500 (Internal Server Error) if the experience couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/experiences")
    @Timed
    public ResponseEntity<Experience> updateExperience(@Valid @RequestBody Experience experience) throws URISyntaxException {
        log.debug("REST request to update Experience : {}", experience);
        if (experience.getId() == null) {
            return createExperience(experience);
        }
        Experience result = experienceService.save(experience);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("experience", experience.getId().toString()))
            .body(result);
    }

    /**
     * GET  /experiences : get all the experiences.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of experiences in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/experiences")
    @Timed
    public ResponseEntity<List<Experience>> getAllExperiences(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Experiences");
        Page<Experience> page = experienceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/experiences");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /experiences/:id : get the "id" experience.
     *
     * @param id the id of the experience to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the experience, or with status 404 (Not Found)
     */
    @GetMapping("/experiences/{id}")
    @Timed
    public ResponseEntity<Experience> getExperience(@PathVariable Long id) {
        log.debug("REST request to get Experience : {}", id);
        Experience experience = experienceService.findOne(id);
        return Optional.ofNullable(experience)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /experiences/:id : delete the "id" experience.
     *
     * @param id the id of the experience to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/experiences/{id}")
    @Timed
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
        log.debug("REST request to delete Experience : {}", id);
        experienceService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("experience", id.toString())).build();
    }

    /**
     * SEARCH  /_search/experiences?query=:query : search for the experience corresponding
     * to the query.
     *
     * @param query the query of the experience search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/experiences")
    @Timed
    public ResponseEntity<List<Experience>> searchExperiences(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Experiences for query {}", query);
        Page<Experience> page = experienceService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/experiences");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
