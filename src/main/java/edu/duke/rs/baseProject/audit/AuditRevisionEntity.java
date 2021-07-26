package edu.duke.rs.baseProject.audit;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.envers.RevisionEntity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, of = {"userId"})
@NoArgsConstructor
@AllArgsConstructor
@RevisionEntity(AuditRevisionListener.class)
public class AuditRevisionEntity extends TrackingModifiedEntitiesRevisionEntity {
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1L;
  
  private Long userId;
  
  @Column(length = 100, nullable = false)
  private String initiator;
  
  @Column(length = 1000, nullable = true)
  private String reasonForChange;
}
