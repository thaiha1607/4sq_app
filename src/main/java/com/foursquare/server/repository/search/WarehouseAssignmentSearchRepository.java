package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.WarehouseAssignment;
import com.foursquare.server.repository.WarehouseAssignmentRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link WarehouseAssignment} entity.
 */
public interface WarehouseAssignmentSearchRepository
    extends ElasticsearchRepository<WarehouseAssignment, UUID>, WarehouseAssignmentSearchRepositoryInternal {}

interface WarehouseAssignmentSearchRepositoryInternal {
    Stream<WarehouseAssignment> search(String query);

    Stream<WarehouseAssignment> search(Query query);

    @Async
    void index(WarehouseAssignment entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class WarehouseAssignmentSearchRepositoryInternalImpl implements WarehouseAssignmentSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final WarehouseAssignmentRepository repository;

    WarehouseAssignmentSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, WarehouseAssignmentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<WarehouseAssignment> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<WarehouseAssignment> search(Query query) {
        return elasticsearchTemplate.search(query, WarehouseAssignment.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(WarehouseAssignment entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), WarehouseAssignment.class);
    }
}
