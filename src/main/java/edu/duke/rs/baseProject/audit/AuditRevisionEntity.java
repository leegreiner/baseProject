package edu.duke.rs.baseProject.audit;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.envers.DefaultTrackingModifiedEntitiesRevisionEntity;
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
@RevisionEntity(UserRevisionListener.class)
public class AuditRevisionEntity extends DefaultTrackingModifiedEntitiesRevisionEntity {
  private static final long serialVersionUID = 1L;
  
  @Column(name = "user_id")
  private Long userId;
  
  @Column(name = "initiator", length = 100, nullable = false)
  private String initiator;
}
