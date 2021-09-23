package edu.duke.rs.baseProject.datatables;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import edu.duke.rs.baseProject.AbstractBaseTest;

public class ColumnUnitTest extends AbstractBaseTest {
  @Test
  public void whenSearchSet_thenSearchValueIsSet() {
    final Column column = new Column(easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), true, true,
        new Search(easyRandom.nextObject(String.class), false));
    
    column.setSearchValue("updatedSearch");
    
    assertThat("updatedSearch", equalTo(column.getSearch().getValue()));
    
  }
  
  @Test
  public void whenSearchValueIsSet_thenColumnSearchContainsSearchValue() {
    final Search search = new Search(easyRandom.nextObject(String.class), false);
    final Column column = new Column("abc", "", true, true, search);
    
    search.setValue(search.getValue() + "1");
    column.setSearchValue(search.getValue());
    
    assertThat(column.getSearch().getValue(), equalTo(search.getValue()));
  }
}
