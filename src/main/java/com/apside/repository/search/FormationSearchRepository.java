package com.apside.repository.search;

import com.apside.domain.Formation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Formation entity.
 */
public interface FormationSearchRepository extends ElasticsearchRepository<Formation, Long> {
}
