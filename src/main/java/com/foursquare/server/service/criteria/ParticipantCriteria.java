package com.foursquare.server.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.foursquare.server.domain.Participant} entity. This class is used
 * in {@link com.foursquare.server.web.rest.ParticipantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /participants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private UUIDFilter id;

    private BooleanFilter isAdmin;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter userId;

    private UUIDFilter conversationId;

    private UUIDFilter messageId;

    private UUIDFilter seenMessageId;

    private Boolean distinct;

    public ParticipantCriteria() {}

    public ParticipantCriteria(ParticipantCriteria other) {
        this.id = other.optionalId().map(UUIDFilter::copy).orElse(null);
        this.isAdmin = other.optionalIsAdmin().map(BooleanFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.conversationId = other.optionalConversationId().map(UUIDFilter::copy).orElse(null);
        this.messageId = other.optionalMessageId().map(UUIDFilter::copy).orElse(null);
        this.seenMessageId = other.optionalSeenMessageId().map(UUIDFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ParticipantCriteria copy() {
        return new ParticipantCriteria(this);
    }

    public UUIDFilter getId() {
        return id;
    }

    public Optional<UUIDFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public UUIDFilter id() {
        if (id == null) {
            setId(new UUIDFilter());
        }
        return id;
    }

    public void setId(UUIDFilter id) {
        this.id = id;
    }

    public BooleanFilter getIsAdmin() {
        return isAdmin;
    }

    public Optional<BooleanFilter> optionalIsAdmin() {
        return Optional.ofNullable(isAdmin);
    }

    public BooleanFilter isAdmin() {
        if (isAdmin == null) {
            setIsAdmin(new BooleanFilter());
        }
        return isAdmin;
    }

    public void setIsAdmin(BooleanFilter isAdmin) {
        this.isAdmin = isAdmin;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public Optional<StringFilter> optionalCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            setCreatedBy(new StringFilter());
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public Optional<InstantFilter> optionalCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            setCreatedDate(new InstantFilter());
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Optional<StringFilter> optionalLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    public StringFilter lastModifiedBy() {
        if (lastModifiedBy == null) {
            setLastModifiedBy(new StringFilter());
        }
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Optional<InstantFilter> optionalLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            setLastModifiedDate(new InstantFilter());
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public UUIDFilter getConversationId() {
        return conversationId;
    }

    public Optional<UUIDFilter> optionalConversationId() {
        return Optional.ofNullable(conversationId);
    }

    public UUIDFilter conversationId() {
        if (conversationId == null) {
            setConversationId(new UUIDFilter());
        }
        return conversationId;
    }

    public void setConversationId(UUIDFilter conversationId) {
        this.conversationId = conversationId;
    }

    public UUIDFilter getMessageId() {
        return messageId;
    }

    public Optional<UUIDFilter> optionalMessageId() {
        return Optional.ofNullable(messageId);
    }

    public UUIDFilter messageId() {
        if (messageId == null) {
            setMessageId(new UUIDFilter());
        }
        return messageId;
    }

    public void setMessageId(UUIDFilter messageId) {
        this.messageId = messageId;
    }

    public UUIDFilter getSeenMessageId() {
        return seenMessageId;
    }

    public Optional<UUIDFilter> optionalSeenMessageId() {
        return Optional.ofNullable(seenMessageId);
    }

    public UUIDFilter seenMessageId() {
        if (seenMessageId == null) {
            setSeenMessageId(new UUIDFilter());
        }
        return seenMessageId;
    }

    public void setSeenMessageId(UUIDFilter seenMessageId) {
        this.seenMessageId = seenMessageId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ParticipantCriteria that = (ParticipantCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(isAdmin, that.isAdmin) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(conversationId, that.conversationId) &&
            Objects.equals(messageId, that.messageId) &&
            Objects.equals(seenMessageId, that.seenMessageId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            isAdmin,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            userId,
            conversationId,
            messageId,
            seenMessageId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalIsAdmin().map(f -> "isAdmin=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalConversationId().map(f -> "conversationId=" + f + ", ").orElse("") +
            optionalMessageId().map(f -> "messageId=" + f + ", ").orElse("") +
            optionalSeenMessageId().map(f -> "seenMessageId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
