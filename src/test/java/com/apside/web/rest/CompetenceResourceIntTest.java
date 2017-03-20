package com.apside.web.rest;

import com.apside.CvthequeApp;

import com.apside.domain.Competence;
import com.apside.repository.CompetenceRepository;
import com.apside.service.CompetenceService;
import com.apside.repository.search.CompetenceSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Ignore;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CompetenceResource REST controller.
 *
 * @see CompetenceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CvthequeApp.class)
public class CompetenceResourceIntTest {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Integer DEFAULT_NIVEAU = 1;
    private static final Integer UPDATED_NIVEAU = 2;

    @Inject
    private CompetenceRepository competenceRepository;

    @Inject
    private CompetenceService competenceService;

    @Inject
    private CompetenceSearchRepository competenceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restCompetenceMockMvc;

    private Competence competence;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CompetenceResource competenceResource = new CompetenceResource();
        ReflectionTestUtils.setField(competenceResource, "competenceService", competenceService);
        this.restCompetenceMockMvc = MockMvcBuilders.standaloneSetup(competenceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Competence createEntity(EntityManager em) {
        Competence competence = new Competence()
                .nom(DEFAULT_NOM)
                .niveau(DEFAULT_NIVEAU);
        return competence;
    }

    @Before
    public void initTest() {
        competenceSearchRepository.deleteAll();
        competence = createEntity(em);
    }

    @Test
    @Transactional
    @Ignore
    public void createCompetence() throws Exception {
        int databaseSizeBeforeCreate = competenceRepository.findAll().size();

        // Create the Competence

        restCompetenceMockMvc.perform(post("/api/competences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competence)))
                .andExpect(status().isCreated());

        // Validate the Competence in the database
        List<Competence> competences = competenceRepository.findAll();
        assertThat(competences).hasSize(databaseSizeBeforeCreate + 1);
        Competence testCompetence = competences.get(competences.size() - 1);
        assertThat(testCompetence.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testCompetence.getNiveau()).isEqualTo(DEFAULT_NIVEAU);

        // Validate the Competence in ElasticSearch
        Competence competenceEs = competenceSearchRepository.findOne(testCompetence.getId());
        assertThat(competenceEs).isEqualToComparingFieldByField(testCompetence);
    }

    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = competenceRepository.findAll().size();
        // set the field null
        competence.setNom(null);

        // Create the Competence, which fails.

        restCompetenceMockMvc.perform(post("/api/competences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competence)))
                .andExpect(status().isBadRequest());

        List<Competence> competences = competenceRepository.findAll();
        assertThat(competences).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCompetences() throws Exception {
        // Initialize the database
        competenceRepository.saveAndFlush(competence);

        // Get all the competences
        restCompetenceMockMvc.perform(get("/api/competences?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(competence.getId().intValue())))
                .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
                .andExpect(jsonPath("$.[*].niveau").value(hasItem(DEFAULT_NIVEAU)));
    }

    @Test
    @Transactional
    public void getCompetence() throws Exception {
        // Initialize the database
        competenceRepository.saveAndFlush(competence);

        // Get the competence
        restCompetenceMockMvc.perform(get("/api/competences/{id}", competence.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(competence.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.niveau").value(DEFAULT_NIVEAU));
    }

    @Test
    @Transactional
    public void getNonExistingCompetence() throws Exception {
        // Get the competence
        restCompetenceMockMvc.perform(get("/api/competences/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCompetence() throws Exception {
        // Initialize the database
        competenceService.save(competence);

        int databaseSizeBeforeUpdate = competenceRepository.findAll().size();

        // Update the competence
        Competence updatedCompetence = competenceRepository.findOne(competence.getId());
        updatedCompetence
                .nom(UPDATED_NOM)
                .niveau(UPDATED_NIVEAU);

        restCompetenceMockMvc.perform(put("/api/competences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCompetence)))
                .andExpect(status().isOk());

        // Validate the Competence in the database
        List<Competence> competences = competenceRepository.findAll();
        assertThat(competences).hasSize(databaseSizeBeforeUpdate);
        Competence testCompetence = competences.get(competences.size() - 1);
        assertThat(testCompetence.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testCompetence.getNiveau()).isEqualTo(UPDATED_NIVEAU);

        // Validate the Competence in ElasticSearch
        Competence competenceEs = competenceSearchRepository.findOne(testCompetence.getId());
        assertThat(competenceEs).isEqualToComparingFieldByField(testCompetence);
    }

    @Test
    @Transactional
    public void deleteCompetence() throws Exception {
        // Initialize the database
        competenceService.save(competence);

        int databaseSizeBeforeDelete = competenceRepository.findAll().size();

        // Get the competence
        restCompetenceMockMvc.perform(delete("/api/competences/{id}", competence.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean competenceExistsInEs = competenceSearchRepository.exists(competence.getId());
        assertThat(competenceExistsInEs).isFalse();

        // Validate the database is empty
        List<Competence> competences = competenceRepository.findAll();
        assertThat(competences).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCompetence() throws Exception {
        // Initialize the database
        competenceService.save(competence);

        // Search the competence
        restCompetenceMockMvc.perform(get("/api/_search/competences?query=id:" + competence.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(competence.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].niveau").value(hasItem(DEFAULT_NIVEAU)));
    }
}
