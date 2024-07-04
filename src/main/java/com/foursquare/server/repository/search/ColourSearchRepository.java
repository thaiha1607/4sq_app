package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.Colour;
import com.foursquare.server.repository.ColourRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Colour} entity.
 */
public interface ColourSearchRepository extends ElasticsearchRepository<Colour, UUID>, ColourSearchRepositoryInternal {}

interface ColourSearchRepositoryInternal {
    Stream<Colour> search(String query);

    Stream<Colour> search(Query query);

    @Async
    void index(Colour entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class ColourSearchRepositoryInternalImpl implements ColourSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ColourRepository repository;

    ColourSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ColourRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Colour> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Colour> search(Query query) {
        return elasticsearchTemplate.search(query, Colour.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Colour entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), Colour.class);
    }
}
