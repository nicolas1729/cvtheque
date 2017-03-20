package com.apside.web.rest;

import com.apside.CvthequeApp;

import com.apside.domain.Experience;
import com.apside.repository.ExperienceRepository;
import com.apside.service.ExperienceService;
import com.apside.repository.search.ExperienceSearchRepository;

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
 * Test class for the ExperienceResource REST controller.
 *
 * @see ExperienceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CvthequeApp.class)
public class ExperienceResourceIntTest {

    private static final String DEFAULT_INTITULE = "AAAAAAAAAA";
    private static final String UPDATED_INTITULE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATEDEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATEDEBUT = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATEFIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATEFIN = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_ENTREPRISE = "AAAAAAAAAA";
    private static final String UPDATED_ENTREPRISE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Inject
    private ExperienceRepository experienceRepository;

    @Inject
    private ExperienceService experienceService;

    @Inject
    private ExperienceSearchRepository experienceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restExperienceMockMvc;

    private Experience experience;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ExperienceResource experienceResource = new ExperienceResource();
        ReflectionTestUtils.setField(experienceResource, "experienceService", experienceService);
        this.restExperienceMockMvc = MockMvcBuilders.standaloneSetup(experienceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Experience createEntity(EntityManager em) {
        Experience experience = new Experience()
                .intitule(DEFAULT_INTITULE)
                .datedebut(DEFAULT_DATEDEBUT)
                .datefin(DEFAULT_DATEFIN)
                .entreprise(DEFAULT_ENTREPRISE)
                .description(DEFAULT_DESCRIPTION);
        return experience;
    }

    @Before
    public void initTest() {
        experienceSearchRepository.deleteAll();
        experience = createEntity(em);
    }

    @Test
    @Transactional
    @Ignore
    public void createExperience() throws Exception {
        int databaseSizeBeforeCreate = experienceRepository.findAll().size();

        // Create the Experience

        restExperienceMockMvc.perform(post("/api/experiences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(experience)))
                .andExpect(status().isCreated());

        // Validate the Experience in the database
        List<Experience> experiences = experienceRepository.findAll();
        assertThat(experiences).hasSize(databaseSizeBeforeCreate + 1);
        Experience testExperience = experiences.get(experiences.size() - 1);
        assertThat(testExperience.getIntitule()).isEqualTo(DEFAULT_INTITULE);
        assertThat(testExperience.getDatedebut()).isEqualTo(DEFAULT_DATEDEBUT);
        assertThat(testExperience.getDatefin()).isEqualTo(DEFAULT_DATEFIN);
        assertThat(testExperience.getEntreprise()).isEqualTo(DEFAULT_ENTREPRISE);
        assertThat(testExperience.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Experience in ElasticSearch
        Experience experienceEs = experienceSearchRepository.findOne(testExperience.getId());
        assertThat(experienceEs).isEqualToComparingFieldByField(testExperience);
    }

    @Test
    @Transactional
    public void checkIntituleIsRequired() throws Exception {
        int databaseSizeBeforeTest = experienceRepository.findAll().size();
        // set the field null
        experience.setIntitule(null);

        // Create the Experience, which fails.

        restExperienceMockMvc.perform(post("/api/experiences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(experience)))
                .andExpect(status().isBadRequest());

        List<Experience> experiences = experienceRepository.findAll();
        assertThat(experiences).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDatedebutIsRequired() throws Exception {
        int databaseSizeBeforeTest = experienceRepository.findAll().size();
        // set the field null
        experience.setDatedebut(null);

        // Create the Experience, which fails.

        restExperienceMockMvc.perform(post("/api/experiences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(experience)))
                .andExpect(status().isBadRequest());

        List<Experience> experiences = experienceRepository.findAll();
        assertThat(experiences).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllExperiences() throws Exception {
        // Initialize the database
        experienceRepository.saveAndFlush(experience);

        // Get all the experiences
        restExperienceMockMvc.perform(get("/api/experiences?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(experience.getId().intValue())))
                .andExpect(jsonPath("$.[*].intitule").value(hasItem(DEFAULT_INTITULE.toString())))
                .andExpect(jsonPath("$.[*].datedebut").value(hasItem(DEFAULT_DATEDEBUT.toString())))
                .andExpect(jsonPath("$.[*].datefin").value(hasItem(DEFAULT_DATEFIN.toString())))
                .andExpect(jsonPath("$.[*].entreprise").value(hasItem(DEFAULT_ENTREPRISE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getExperience() throws Exception {
        // Initialize the database
        experienceRepository.saveAndFlush(experience);

        // Get the experience
        restExperienceMockMvc.perform(get("/api/experiences/{id}", experience.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(experience.getId().intValue()))
            .andExpect(jsonPath("$.intitule").value(DEFAULT_INTITULE.toString()))
            .andExpect(jsonPath("$.datedebut").value(DEFAULT_DATEDEBUT.toString()))
            .andExpect(jsonPath("$.datefin").value(DEFAULT_DATEFIN.toString()))
            .andExpect(jsonPath("$.entreprise").value(DEFAULT_ENTREPRISE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingExperience() throws Exception {
        // Get the experience
        restExperienceMockMvc.perform(get("/api/experiences/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateExperience() throws Exception {
        // Initialize the database
        experienceService.save(experience);

        int databaseSizeBeforeUpdate = experienceRepository.findAll().size();

        // Update the experience
        Experience updatedExperience = experienceRepository.findOne(experience.getId());
        updatedExperience
                .intitule(UPDATED_INTITULE)
                .datedebut(UPDATED_DATEDEBUT)
                .datefin(UPDATED_DATEFIN)
                .entreprise(UPDATED_ENTREPRISE)
                .description(UPDATED_DESCRIPTION);

        restExperienceMockMvc.perform(put("/api/experiences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedExperience)))
                .andExpect(status().isOk());

        // Validate the Experience in the database
        List<Experience> experiences = experienceRepository.findAll();
        assertThat(experiences).hasSize(databaseSizeBeforeUpdate);
        Experience testExperience = experiences.get(experiences.size() - 1);
        assertThat(testExperience.getIntitule()).isEqualTo(UPDATED_INTITULE);
        assertThat(testExperience.getDatedebut()).isEqualTo(UPDATED_DATEDEBUT);
        assertThat(testExperience.getDatefin()).isEqualTo(UPDATED_DATEFIN);
        assertThat(testExperience.getEntreprise()).isEqualTo(UPDATED_ENTREPRISE);
        assertThat(testExperience.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Experience in ElasticSearch
        Experience experienceEs = experienceSearchRepository.findOne(testExperience.getId());
        assertThat(experienceEs).isEqualToComparingFieldByField(testExperience);
    }

    @Test
    @Transactional
    public void deleteExperience() throws Exception {
        // Initialize the database
        experienceService.save(experience);

        int databaseSizeBeforeDelete = experienceRepository.findAll().size();

        // Get the experience
        restExperienceMockMvc.perform(delete("/api/experiences/{id}", experience.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean experienceExistsInEs = experienceSearchRepository.exists(experience.getId());
        assertThat(experienceExistsInEs).isFalse();

        // Validate the database is empty
        List<Experience> experiences = experienceRepository.findAll();
        assertThat(experiences).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchExperience() throws Exception {
        // Initialize the database
        experienceService.save(experience);

        // Search the experience
        restExperienceMockMvc.perform(get("/api/_search/experiences?query=id:" + experience.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(experience.getId().intValue())))
            .andExpect(jsonPath("$.[*].intitule").value(hasItem(DEFAULT_INTITULE.toString())))
            .andExpect(jsonPath("$.[*].datedebut").value(hasItem(DEFAULT_DATEDEBUT.toString())))
            .andExpect(jsonPath("$.[*].datefin").value(hasItem(DEFAULT_DATEFIN.toString())))
            .andExpect(jsonPath("$.[*].entreprise").value(hasItem(DEFAULT_ENTREPRISE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
