package edu.duke.rs.baseProject.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
@Audited
public class ESignedBaseEntity extends BaseEntity {
  @Column(name = "reason_for_change", length = 1000, nullable = true)
  @Size(max = 1000)
  private String reasonForChange;
}
