package edu.duke.rs.baseProject.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
  @Column(name = "created_date", nullable = false, updatable = false)
  @CreatedDate
  @JsonIgnore
  private LocalDateTime createdDate = LocalDateTime.now();

  @Column(name = "modified_date")
  @LastModifiedDate
  @JsonIgnore
  private LocalDateTime lastModifiedDate;
  
  @Version
  private Long version;
}
