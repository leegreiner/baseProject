package edu.duke.rs.baseProject.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuditQueryUtilsUnitTest {
  @Mock
  private AuditQuery auditQuery;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void whenNoResults_thenGetAuditQueryResultsReturnsEmptyList() {
    when(auditQuery.getResultList()).thenReturn(null);
    
    assertThat(AuditQueryUtils.getAuditQueryResults(auditQuery, String.class).size()).isEqualTo(0);
  }
  
  @Test
  public void whenResults_thenGetAuditQueryResultsReturnsResults() {
    final AuditRevisionEntity auditRevisionEntity1 = new AuditRevisionEntity(Long.valueOf(1), "John Smith");
    final Object[] item1 = new Object[] {"a test", auditRevisionEntity1, RevisionType.ADD};
    final AuditRevisionEntity auditRevisionEntity2 = new AuditRevisionEntity(Long.valueOf(2), "Jane Smith");
    final Object[] item2 = new Object[] {"another test", auditRevisionEntity2, RevisionType.MOD};
    final List<Object[]> resultList = List.of(item1, item2);
    when(auditQuery.getResultList()).thenReturn(resultList);
    
    final List<AuditQueryResult<String>> actual = AuditQueryUtils.getAuditQueryResults(auditQuery, String.class);
    
    for (int i = 0; i < resultList.size(); i++) {
      assertThat(actual.get(i).getEntity()).isEqualTo(resultList.get(i)[0]);
      assertThat(actual.get(i).getRevision()).isEqualTo(resultList.get(i)[1]);
      assertThat(actual.get(i).getType()).isEqualTo(resultList.get(i)[2]);
    }
  }
}
