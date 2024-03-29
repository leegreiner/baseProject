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

import edu.duke.rs.baseProject.AbstractBaseTest;

public class AuditQueryUtilsUnitTest extends AbstractBaseTest {
  @Mock
  private AuditQuery auditQuery;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  public void whenNoResults_thenGetAuditQueryResultsReturnsEmptyList() {
    when(auditQuery.getResultList()).thenReturn(null);
    
    assertThat(AuditQueryUtils.getAuditQueryResults(auditQuery, String.class).size()).isEqualTo(0);
  }
  
  @Test
  public void whenResults_thenGetAuditQueryResultsReturnsResults() {
    final AuditRevisionEntity auditRevisionEntity1 = new AuditRevisionEntity(easyRandom.nextLong(), easyRandom.nextObject(String.class), null);
    final Object[] item1 = new Object[] {easyRandom.nextObject(String.class), auditRevisionEntity1, RevisionType.ADD};
    final AuditRevisionEntity auditRevisionEntity2 = new AuditRevisionEntity(easyRandom.nextLong(), easyRandom.nextObject(String.class), null);
    final Object[] item2 = new Object[] {easyRandom.nextObject(String.class), auditRevisionEntity2, RevisionType.MOD};
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
