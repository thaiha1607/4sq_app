package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.Tag;
import com.foursquare.server.repository.TagRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Tag} entity.
 */
public interface TagSearchRepository extends ElasticsearchRepository<Tag, UUID>, TagSearchRepositoryInternal {}

interface TagSearchRepositoryInternal {
    Stream<Tag> search(String query);

    Stream<Tag> search(Query query);

    @Async
    void index(Tag entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class TagSearchRepositoryInternalImpl implements TagSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TagRepository repository;

    TagSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TagRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Tag> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Tag> search(Query query) {
        return elasticsearchTemplate.search(query, Tag.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Tag entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), Tag.class);
    }
}
