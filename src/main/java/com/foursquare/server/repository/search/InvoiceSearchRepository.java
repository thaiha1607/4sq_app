package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.Invoice;
import com.foursquare.server.repository.InvoiceRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Invoice} entity.
 */
public interface InvoiceSearchRepository extends ElasticsearchRepository<Invoice, UUID>, InvoiceSearchRepositoryInternal {}

interface InvoiceSearchRepositoryInternal {
    Stream<Invoice> search(String query);

    Stream<Invoice> search(Query query);

    @Async
    void index(Invoice entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class InvoiceSearchRepositoryInternalImpl implements InvoiceSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final InvoiceRepository repository;

    InvoiceSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, InvoiceRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Invoice> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Invoice> search(Query query) {
        return elasticsearchTemplate.search(query, Invoice.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Invoice entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), Invoice.class);
    }
}
