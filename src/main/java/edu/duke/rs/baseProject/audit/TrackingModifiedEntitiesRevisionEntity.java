package edu.duke.rs.baseProject.audit;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.ModifiedEntityNames;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class TrackingModifiedEntitiesRevisionEntity extends BasicAuditRevisionEntity {
  @ElementCollection(fetch = FetchType.EAGER)
  @JoinTable(name = "REVCHANGES", joinColumns = @JoinColumn(name = "REV"))
  @Column(name = "ENTITYNAME")
  @Fetch(FetchMode.JOIN)
  @ModifiedEntityNames
  private Set<String> modifiedEntityNames = new HashSet<>();

  public Set<String> getModifiedEntityNames() {
    return modifiedEntityNames;
  }

  public void setModifiedEntityNames(Set<String> modifiedEntityNames) {
    this.modifiedEntityNames = modifiedEntityNames;
  }

  @Override
  public boolean equals(Object o) {
    if ( this == o ) {
      return true;
    }
    if ( !(o instanceof TrackingModifiedEntitiesRevisionEntity) ) {
      return false;
    }
    if ( !super.equals( o ) ) {
      return false;
    }

    final TrackingModifiedEntitiesRevisionEntity that = (TrackingModifiedEntitiesRevisionEntity) o;

    if ( modifiedEntityNames != null ? !modifiedEntityNames.equals( that.modifiedEntityNames )
        : that.modifiedEntityNames != null ) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (modifiedEntityNames != null ? modifiedEntityNames.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TrackingModifiedEntitiesRevisionEntity(" + super.toString() + ", modifiedEntityNames = " + modifiedEntityNames + ")";
  }
}
