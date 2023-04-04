package edu.duke.rs.baseProject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreRemove;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@MappedSuperclass
//All entities must have the following 2 annotations with tableName and idName replaced
//  @SQLDelete(sql = "UPDATE tableName SET deleted = 'Y' WHERE idName = ? AND version = ?", check = ResultCheckStyle.COUNT)
//  @Where(clause = "deleted is not null")
//For associations you may need the following depending on if a join table is involved
//  @Where(clause = "deleted is null")
//  @WhereJoinTable(clause = "deleted is null")
//  @SQLDeleteAll(sql=)
//If a join table is involved you must create a manual join table that extends from this class and
// the above annotations for associations apply.
//You may also want to index the deleted field if many entities exist
public class SoftDeleteBaseEntity extends BaseEntity {
  @Column(name = "deleted", nullable = true)
  @Convert(converter = org.hibernate.type.TrueFalseConverter.class)
  private Boolean deleted;
  
  public void undelete() {
    deleted = null;
  }
  
  @PreRemove
  public void remove() {
    deleted = Boolean.TRUE;
  }
}