package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.Message;
import com.foursquare.server.repository.MessageRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Message} entity.
 */
public interface MessageSearchRepository extends ElasticsearchRepository<Message, UUID>, MessageSearchRepositoryInternal {}

interface MessageSearchRepositoryInternal {
    Stream<Message> search(String query);

    Stream<Message> search(Query query);

    @Async
    void index(Message entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class MessageSearchRepositoryInternalImpl implements MessageSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final MessageRepository repository;

    MessageSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, MessageRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Message> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Message> search(Query query) {
        return elasticsearchTemplate.search(query, Message.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Message entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), Message.class);
    }
}