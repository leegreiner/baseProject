package edu.duke.rs.baseProject.audit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.duke.rs.baseProject.datatables.Column;
import edu.duke.rs.baseProject.datatables.Search;

public class ColumnUnitTest {
  @Test
  public void whenSearchValueIsSet_thenColumnSearchContainsSearchValue() {
    final Search search = new Search("d", false);
    final Column column = new Column("abc", "", true, true, search);
    
    search.setValue(search.getValue() + "1");
    column.setSearchValue(search.getValue());
    
    assertThat(column.getSearch().getValue(), equalTo(search.getValue()));
  }
}
