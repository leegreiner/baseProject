package edu.duke.rs.baseProject.audit;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.envers.RevisionType;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class AuditQueryResultUtilsUnitTest {
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
    final AuditRevisionEntity auditRevisionEntity = new AuditRevisionEntity(Long.valueOf(1), "John Smith", null);
    final Object[] item = new Object[] {LocalDateTime.now(), auditRevisionEntity, RevisionType.ADD};
    final AuditQueryResult<String> result = AuditQueryResultUtils.getAuditQueryResult(item, String.class);
    
    assertThat(result.getEntity()).isNull();
    assertThat(result.getRevision()).isEqualTo(item[1]);
    assertThat(result.getType()).isEqualTo(item[2]);
  }
  
  @Test
  public void whenRevisionEntityDoesntMatch_thenRevisionIsNull() {
    final Object[] item = new Object[] {"a test", new String("abc"), RevisionType.ADD};
    final AuditQueryResult<String> result = AuditQueryResultUtils.getAuditQueryResult(item, String.class);
    
    assertThat(result.getEntity()).isEqualTo(item[0]);
    assertThat(result.getRevision()).isNull();
    assertThat(result.getType()).isEqualTo(item[2]);
  }
  
  @Test
  public void whenRevisionTypeDoesntMatch_thenRevisionIsNull() {
    final AuditRevisionEntity auditRevisionEntity = new AuditRevisionEntity(Long.valueOf(1), "John Smith", null);
    final Object[] item = new Object[] {"a test", auditRevisionEntity, new String("abc")};
    final AuditQueryResult<String> result = AuditQueryResultUtils.getAuditQueryResult(item, String.class);
    
    assertThat(result.getEntity()).isEqualTo(item[0]);
    assertThat(result.getRevision()).isEqualTo(item[1]);
    assertThat(result.getType()).isNull();
  }
  
  @Test
  public void whenItemValueValid_thenAllFieldsPopulated() {
    final AuditRevisionEntity auditRevisionEntity = new AuditRevisionEntity(Long.valueOf(1), "John Smith", null);
    final Object[] item = new Object[] {"a test", auditRevisionEntity, RevisionType.ADD};
    final AuditQueryResult<String> result = AuditQueryResultUtils.getAuditQueryResult(item, String.class);
    
    assertThat(result.getEntity()).isEqualTo(item[0]);
    assertThat(result.getRevision()).isEqualTo(item[1]);
    assertThat(result.getType()).isEqualTo(item[2]);
  }
}
