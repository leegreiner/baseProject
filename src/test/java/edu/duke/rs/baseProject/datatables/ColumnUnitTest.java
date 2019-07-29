package edu.duke.rs.baseProject.datatables;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ColumnUnitTest {
  @Test
  public void whenSearchSet_thenSearchValueIsSet() {
    final Column column = new Column("abc", "234", true, true, new Search("searchForMe", false));
    
    column.setSearchValue("updatedSearch");
    
    assertThat("updatedSearch", equalTo(column.getSearch().getValue()));
    
  }
}
