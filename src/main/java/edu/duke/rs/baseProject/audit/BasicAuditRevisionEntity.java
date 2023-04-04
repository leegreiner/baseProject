package edu.duke.rs.baseProject.audit;

import java.text.DateFormat;
import java.util.Date;

import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

@MappedSuperclass
public class BasicAuditRevisionEntity {
  @SuppressWarnings("unused")
  private static final long serialVersionUID = -1;

  @Id
  @GeneratedValue
  @RevisionNumber
  private Long id;

  @RevisionTimestamp
  private Long timestamp;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Transient
  public Date getRevisionDate() {
    return new Date( timestamp );
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if ( this == o ) {
      return true;
    }
    if ( !(o instanceof BasicAuditRevisionEntity) ) {
      return false;
    }

    final BasicAuditRevisionEntity that = (BasicAuditRevisionEntity) o;
    return id.equals(that.id)
        && timestamp.equals(that.timestamp);
  }

  @Override
  public int hashCode() {
    int result;
    result = id.hashCode();
    result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "DefaultRevisionEntity(id = " + id
        + ", revisionDate = " + DateFormat.getDateTimeInstance().format( getRevisionDate() ) + ")";
  }
}
