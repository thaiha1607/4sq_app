package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.Participant;
import com.foursquare.server.repository.ParticipantRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Participant} entity.
 */
public interface ParticipantSearchRepository extends ElasticsearchRepository<Participant, UUID>, ParticipantSearchRepositoryInternal {}

interface ParticipantSearchRepositoryInternal {
    Stream<Participant> search(String query);

    Stream<Participant> search(Query query);

    @Async
    void index(Participant entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class ParticipantSearchRepositoryInternalImpl implements ParticipantSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ParticipantRepository repository;

    ParticipantSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ParticipantRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Participant> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Participant> search(Query query) {
        return elasticsearchTemplate.search(query, Participant.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Participant entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), Participant.class);
    }
}
