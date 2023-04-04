package edu.duke.rs.baseProject.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
@Audited
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
  @Column(nullable = false, updatable = false)
  @CreatedDate
  @JsonIgnore
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Setter // for testing
  @JsonIgnore
  private LocalDateTime lastModifiedDate;
  
  @Column(name = "alternate_id", length = 16, nullable = false, updatable = false, unique = true)
  @Include
  private UUID alternateId = UUID.randomUUID();
  
  @Version
  @Column(nullable = false)
  private Long version;
}
