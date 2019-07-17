package edu.duke.rs.baseProject.audit;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.envers.DefaultTrackingModifiedEntitiesRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@RevisionEntity(UserRevisionListener.class)
public class AuditRevisionEntity extends DefaultTrackingModifiedEntitiesRevisionEntity {
  private static final long serialVersionUID = 1L;
  
  @Column(name = "user_id")
  private Long userId;
}
