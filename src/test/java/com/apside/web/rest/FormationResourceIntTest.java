package com.apside.web.rest;

import com.apside.CvthequeApp;

import com.apside.domain.Formation;
import com.apside.repository.FormationRepository;
import com.apside.service.FormationService;
import com.apside.repository.search.FormationSearchRepository;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Ignore;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FormationResource REST controller.
 *
 * @see FormationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CvthequeApp.class)
public class FormationResourceIntTest {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_ETABLISSEMENT = "AAAAAAAAAA";
    private static final String UPDATED_ETABLISSEMENT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATEDEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATEDEBUT = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATEFIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATEFIN = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private FormationRepository formationRepository;

    @Inject
    private FormationService formationService;

    @Inject
    private FormationSearchRepository formationSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restFormationMockMvc;

    private Formation formation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FormationResource formationResource = new FormationResource();
        ReflectionTestUtils.setField(formationResource, "formationService", formationService);
        this.restFormationMockMvc = MockMvcBuilders.standaloneSetup(formationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Formation createEntity(EntityManager em) {
        Formation formation = new Formation()
                .nom(DEFAULT_NOM)
                .etablissement(DEFAULT_ETABLISSEMENT)
                .description(DEFAULT_DESCRIPTION)
                .datedebut(DEFAULT_DATEDEBUT)
                .datefin(DEFAULT_DATEFIN);
        return formation;
    }

    @Before
    public void initTest() {
        formationSearchRepository.deleteAll();
        formation = createEntity(em);
    }

    @Test
    @Transactional
    @Ignore
    public void createFormation() throws Exception {
        int databaseSizeBeforeCreate = formationRepository.findAll().size();

        // Create the Formation

        restFormationMockMvc.perform(post("/api/formations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(formation)))
                .andExpect(status().isCreated());

        // Validate the Formation in the database
        List<Formation> formations = formationRepository.findAll();
        assertThat(formations).hasSize(databaseSizeBeforeCreate + 1);
        Formation testFormation = formations.get(formations.size() - 1);
        assertThat(testFormation.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testFormation.getEtablissement()).isEqualTo(DEFAULT_ETABLISSEMENT);
        assertThat(testFormation.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFormation.getDatedebut()).isEqualTo(DEFAULT_DATEDEBUT);
        assertThat(testFormation.getDatefin()).isEqualTo(DEFAULT_DATEFIN);

        // Validate the Formation in ElasticSearch
        Formation formationEs = formationSearchRepository.findOne(testFormation.getId());
        assertThat(formationEs).isEqualToComparingFieldByField(testFormation);
    }

    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = formationRepository.findAll().size();
        // set the field null
        formation.setNom(null);

        // Create the Formation, which fails.

        restFormationMockMvc.perform(post("/api/formations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(formation)))
                .andExpect(status().isBadRequest());

        List<Formation> formations = formationRepository.findAll();
        assertThat(formations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFormations() throws Exception {
        // Initialize the database
        formationRepository.saveAndFlush(formation);

        // Get all the formations
        restFormationMockMvc.perform(get("/api/formations?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(formation.getId().intValue())))
                .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
                .andExpect(jsonPath("$.[*].etablissement").value(hasItem(DEFAULT_ETABLISSEMENT.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].datedebut").value(hasItem(DEFAULT_DATEDEBUT.toString())))
                .andExpect(jsonPath("$.[*].datefin").value(hasItem(DEFAULT_DATEFIN.toString())));
    }

    @Test
    @Transactional
    public void getFormation() throws Exception {
        // Initialize the database
        formationRepository.saveAndFlush(formation);

        // Get the formation
        restFormationMockMvc.perform(get("/api/formations/{id}", formation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(formation.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.etablissement").value(DEFAULT_ETABLISSEMENT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.datedebut").value(DEFAULT_DATEDEBUT.toString()))
            .andExpect(jsonPath("$.datefin").value(DEFAULT_DATEFIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFormation() throws Exception {
        // Get the formation
        restFormationMockMvc.perform(get("/api/formations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFormation() throws Exception {
        // Initialize the database
        formationService.save(formation);

        int databaseSizeBeforeUpdate = formationRepository.findAll().size();

        // Update the formation
        Formation updatedFormation = formationRepository.findOne(formation.getId());
        updatedFormation
                .nom(UPDATED_NOM)
                .etablissement(UPDATED_ETABLISSEMENT)
                .description(UPDATED_DESCRIPTION)
                .datedebut(UPDATED_DATEDEBUT)
                .datefin(UPDATED_DATEFIN);

        restFormationMockMvc.perform(put("/api/formations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedFormation)))
                .andExpect(status().isOk());

        // Validate the Formation in the database
        List<Formation> formations = formationRepository.findAll();
        assertThat(formations).hasSize(databaseSizeBeforeUpdate);
        Formation testFormation = formations.get(formations.size() - 1);
        assertThat(testFormation.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testFormation.getEtablissement()).isEqualTo(UPDATED_ETABLISSEMENT);
        assertThat(testFormation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFormation.getDatedebut()).isEqualTo(UPDATED_DATEDEBUT);
        assertThat(testFormation.getDatefin()).isEqualTo(UPDATED_DATEFIN);

        // Validate the Formation in ElasticSearch
        Formation formationEs = formationSearchRepository.findOne(testFormation.getId());
        assertThat(formationEs).isEqualToComparingFieldByField(testFormation);
    }

    @Test
    @Transactional
    public void deleteFormation() throws Exception {
        // Initialize the database
        formationService.save(formation);

        int databaseSizeBeforeDelete = formationRepository.findAll().size();

        // Get the formation
        restFormationMockMvc.perform(delete("/api/formations/{id}", formation.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean formationExistsInEs = formationSearchRepository.exists(formation.getId());
        assertThat(formationExistsInEs).isFalse();

        // Validate the database is empty
        List<Formation> formations = formationRepository.findAll();
        assertThat(formations).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFormation() throws Exception {
        // Initialize the database
        formationService.save(formation);

        // Search the formation
        restFormationMockMvc.perform(get("/api/_search/formations?query=id:" + formation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(formation.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].etablissement").value(hasItem(DEFAULT_ETABLISSEMENT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].datedebut").value(hasItem(DEFAULT_DATEDEBUT.toString())))
            .andExpect(jsonPath("$.[*].datefin").value(hasItem(DEFAULT_DATEFIN.toString())));
    }
}
