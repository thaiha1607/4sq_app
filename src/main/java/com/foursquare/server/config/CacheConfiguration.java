package com.foursquare.server.config;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.redisson.Redisson;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;

    @Bean
    public javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration(JHipsterProperties jHipsterProperties) {
        MutableConfiguration<Object, Object> jcacheConfig = new MutableConfiguration<>();

        URI redisUri = URI.create(jHipsterProperties.getCache().getRedis().getServer()[0]);

        Config config = new Config();
        // Fix Hibernate lazy initialization https://github.com/jhipster/generator-jhipster/issues/22889
        config.setCodec(new org.redisson.codec.SerializationCodec());
        if (jHipsterProperties.getCache().getRedis().isCluster()) {
            ClusterServersConfig clusterServersConfig = config
                .useClusterServers()
                .setMasterConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize())
                .setMasterConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .addNodeAddress(jHipsterProperties.getCache().getRedis().getServer());

            if (redisUri.getUserInfo() != null) {
                clusterServersConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        } else {
            SingleServerConfig singleServerConfig = config
                .useSingleServer()
                .setConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize())
                .setConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .setAddress(jHipsterProperties.getCache().getRedis().getServer()[0]);

            if (redisUri.getUserInfo() != null) {
                singleServerConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        }
        jcacheConfig.setStatisticsEnabled(true);
        jcacheConfig.setExpiryPolicyFactory(
            CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, jHipsterProperties.getCache().getRedis().getExpiration()))
        );
        return RedissonConfiguration.fromInstance(Redisson.create(config), jcacheConfig);
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cm) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cm);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return cm -> {
            createCache(cm, com.foursquare.server.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            createCache(cm, com.foursquare.server.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.User.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Authority.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.EntityAuditEvent.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.UserDetails.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.StaffInfo.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Address.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Colour.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Conversation.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Conversation.class.getName() + ".participants", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Participant.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Participant.class.getName() + ".messages", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Participant.class.getName() + ".seenMessages", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.InvoiceStatus.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Invoice.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Invoice.class.getName() + ".childInvoices", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Invoice.class.getName() + ".shipments", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Message.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Message.class.getName() + ".seenParticipants", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.OrderItem.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.OrderItem.class.getName() + ".internalOrderItems", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.OrderStatus.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Order.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Order.class.getName() + ".invoices", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Order.class.getName() + ".orderItems", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Order.class.getName() + ".childOrders", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Order.class.getName() + ".internalOrders", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Order.class.getName() + ".shipments", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Order.class.getName() + ".histories", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.ProductQuantity.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.ProductCategory.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Product.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Product.class.getName() + ".productCategories", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Product.class.getName() + ".productImages", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Product.class.getName() + ".comments", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Product.class.getName() + ".tags", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.ProductImage.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.ShipmentAssignment.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.ShipmentItem.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.ShipmentStatus.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Shipment.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Shipment.class.getName() + ".assignments", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Shipment.class.getName() + ".items", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Tag.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Tag.class.getName() + ".products", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.UserAddress.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.WarehouseAssignment.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.WorkingUnit.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.Comment.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.OrderHistory.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.InternalOrder.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.InternalOrder.class.getName() + ".histories", jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.InternalOrderItem.class.getName(), jcacheConfiguration);
            createCache(cm, com.foursquare.server.domain.InternalOrderHistory.class.getName(), jcacheConfiguration);
            // jhipster-needle-redis-add-entry
        };
    }

    private void createCache(
        javax.cache.CacheManager cm,
        String cacheName,
        javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration
    ) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
