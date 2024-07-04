package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.InvoiceStatus;
import com.foursquare.server.repository.InvoiceStatusRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link InvoiceStatus} entity.
 */
public interface InvoiceStatusSearchRepository
    extends ElasticsearchRepository<InvoiceStatus, Long>, InvoiceStatusSearchRepositoryInternal {}

interface InvoiceStatusSearchRepositoryInternal {
    Stream<InvoiceStatus> search(String query);

    Stream<InvoiceStatus> search(Query query);

    @Async
    void index(InvoiceStatus entity);

    @Async
    void deleteFromIndexById(Long id);
}

class InvoiceStatusSearchRepositoryInternalImpl implements InvoiceStatusSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final InvoiceStatusRepository repository;

    InvoiceStatusSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, InvoiceStatusRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<InvoiceStatus> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<InvoiceStatus> search(Query query) {
        return elasticsearchTemplate.search(query, InvoiceStatus.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(InvoiceStatus entity) {
        repository.findById(entity.getStatusCode()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), InvoiceStatus.class);
    }
}
