package edu.duke.rs.baseProject.audit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.hibernate.envers.RevisionType;
import org.junit.jupiter.api.Test;

import edu.duke.rs.baseProject.AbstractBaseTest;

public class AuditQueryResultUtilsUnitTest extends AbstractBaseTest { 
  @Test
  public void whenNullResultsPassedToGetAuditQueryResult_thenNullReturned() {
    assertThat(AuditQueryResultUtils.getAuditQueryResult(null, String.class)).isNull();
  }
  
  @Test
  public void whenNotEnoughParametersPassedToGetAuditQueryResult_thenNullReturned() {
    assertThat(AuditQueryResultUtils.getAuditQueryResult(new Object[1], String.class)).isNull();
  }
  
  @Test
  public void whenTypeDoesntMatch_thenEntityIsNull() {
    final AuditRevisionEntity auditRevisionEntity = new AuditRevisionEntity(easyRandom.nextLong(), easyRandom.nextObject(String.class), null);
    final Object[] item = new Object[] {LocalDateTime.now(), auditRevisionEntity, RevisionType.ADD};
    final AuditQueryResult<String> result = AuditQueryResultUtils.getAuditQueryResult(item, String.class);
    
    assertThat(result.getEntity()).isNull();
    assertThat(result.getRevision()).isEqualTo(item[1]);
    assertThat(result.getType()).isEqualTo(item[2]);
  }
  
  @Test
  public void whenRevisionEntityDoesntMatch_thenRevisionIsNull() {
    final Object[] item = new Object[] {easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), RevisionType.ADD};
    final AuditQueryResult<String> result = AuditQueryResultUtils.getAuditQueryResult(item, String.class);
    
    assertThat(result.getEntity()).isEqualTo(item[0]);
    assertThat(result.getRevision()).isNull();
    assertThat(result.getType()).isEqualTo(item[2]);
  }
  
  @Test
  public void whenRevisionTypeDoesntMatch_thenRevisionIsNull() {
    final AuditRevisionEntity auditRevisionEntity = new AuditRevisionEntity(easyRandom.nextLong(), easyRandom.nextObject(String.class), null);
    final Object[] item = new Object[] {easyRandom.nextObject(String.class), auditRevisionEntity, easyRandom.nextObject(String.class)};
    final AuditQueryResult<String> result = AuditQueryResultUtils.getAuditQueryResult(item, String.class);
    
    assertThat(result.getEntity()).isEqualTo(item[0]);
    assertThat(result.getRevision()).isEqualTo(item[1]);
    assertThat(result.getType()).isNull();
  }
  
  @Test
  public void whenItemValueValid_thenAllFieldsPopulated() {
    final AuditRevisionEntity auditRevisionEntity = new AuditRevisionEntity(easyRandom.nextLong(), easyRandom.nextObject(String.class), null);
    final Object[] item = new Object[] {easyRandom.nextObject(String.class), auditRevisionEntity, RevisionType.ADD};
    final AuditQueryResult<String> result = AuditQueryResultUtils.getAuditQueryResult(item, String.class);
    
    assertThat(result.getEntity()).isEqualTo(item[0]);
    assertThat(result.getRevision()).isEqualTo(item[1]);
    assertThat(result.getType()).isEqualTo(item[2]);
  }
}
