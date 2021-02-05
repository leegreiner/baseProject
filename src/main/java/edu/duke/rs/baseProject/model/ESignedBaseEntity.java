package edu.duke.rs.baseProject.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
@Getter
@Setter
public class ESignedBaseEntity extends BaseEntity {
  @Column(name = "change_reason", length = 1000, nullable = true)
  @Size(max = 1000)
  private String changeReason;
}