package edu.duke.rs.baseProject.datatables;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

import org.junit.Test;

public class DataTablesInputUnitTest {
  @Test
  public void whenColumnsExist_thenGetColumnsAsMapReturnsColumns() {
    final DataTablesInput input = new DataTablesInput();
    final Column column1 = new Column("abc", "", true, true, new Search("d", false));
    final Column column2 = new Column("efg", "", true, true, new Search("h", false));
    input.getColumns().add(column1);
    input.getColumns().add(column2);
    
    final Map<String, Column> columns = input.getColumnsAsMap();
    
    assertThat(columns.size(), equalTo(input.getColumns().size()));
    assertThat(columns.get(column1.getData()), equalTo(column1));
    assertThat(columns.get(column2.getData()), equalTo(column2));
  }
  
  @Test
  public void whenColumnsExist_thenGetColumnReturnsColumn() {
    final DataTablesInput input = new DataTablesInput();
    final Column column1 = new Column("abc", "", true, true, new Search("d", false));
    final Column column2 = new Column("efg", "", true, true, new Search("h", false));
    input.getColumns().add(column1);
    input.getColumns().add(column2);
    
    assertThat(input.getColumn(column1.getData()), equalTo(column1));
    assertThat(input.getColumn(column2.getData()), equalTo(column2));
  }
}
