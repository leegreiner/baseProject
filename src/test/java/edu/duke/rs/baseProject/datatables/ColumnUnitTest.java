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
  
  @Test
  public void whenSearchValueIsSet_thenColumnSearchContainsSearchValue() {
    final Search search = new Search("d", false);
    final Column column = new Column("abc", "", true, true, search);
    
    search.setValue(search.getValue() + "1");
    column.setSearchValue(search.getValue());
    
    assertThat(column.getSearch().getValue(), equalTo(search.getValue()));
  }
}
