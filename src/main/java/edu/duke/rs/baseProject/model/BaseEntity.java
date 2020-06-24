package edu.duke.rs.baseProject.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
  @Getter @Setter
  @Column(nullable = false, updatable = false)
  @CreatedDate
  @JsonIgnore
  private LocalDateTime createdDate = LocalDateTime.now();

  @Getter @Setter
  @LastModifiedDate
  @JsonIgnore
  private LocalDateTime lastModifiedDate;
  
  @Getter
  @Column(name = "alternate_id", nullable = false, updatable = false)
  @Include
  private UUID alternateId = UUID.randomUUID();
  
  @Getter
  @Version
  private Long version;
}
