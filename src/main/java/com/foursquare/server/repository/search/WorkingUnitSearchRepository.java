package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.WorkingUnit;
import com.foursquare.server.repository.WorkingUnitRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link WorkingUnit} entity.
 */
public interface WorkingUnitSearchRepository extends ElasticsearchRepository<WorkingUnit, UUID>, WorkingUnitSearchRepositoryInternal {}

interface WorkingUnitSearchRepositoryInternal {
    Stream<WorkingUnit> search(String query);

    Stream<WorkingUnit> search(Query query);

    @Async
    void index(WorkingUnit entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class WorkingUnitSearchRepositoryInternalImpl implements WorkingUnitSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final WorkingUnitRepository repository;

    WorkingUnitSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, WorkingUnitRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<WorkingUnit> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<WorkingUnit> search(Query query) {
        return elasticsearchTemplate.search(query, WorkingUnit.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(WorkingUnit entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), WorkingUnit.class);
    }
}
