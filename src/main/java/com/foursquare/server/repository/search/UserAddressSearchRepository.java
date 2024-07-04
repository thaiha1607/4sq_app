package com.foursquare.server.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.foursquare.server.domain.UserAddress;
import com.foursquare.server.repository.UserAddressRepository;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link UserAddress} entity.
 */
public interface UserAddressSearchRepository extends ElasticsearchRepository<UserAddress, UUID>, UserAddressSearchRepositoryInternal {}

interface UserAddressSearchRepositoryInternal {
    Stream<UserAddress> search(String query);

    Stream<UserAddress> search(Query query);

    @Async
    void index(UserAddress entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class UserAddressSearchRepositoryInternalImpl implements UserAddressSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UserAddressRepository repository;

    UserAddressSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, UserAddressRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<UserAddress> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<UserAddress> search(Query query) {
        return elasticsearchTemplate.search(query, UserAddress.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(UserAddress entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), UserAddress.class);
    }
}
