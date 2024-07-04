package com.foursquare.server.config.audit;

import com.foursquare.server.config.Constants;
import com.foursquare.server.security.SecurityUtils;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.stereotype.Component;

@Component
public class JaversAuthorProvider implements AuthorProvider {

    @Override
    public String provide() {
        return SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM);
    }
}
