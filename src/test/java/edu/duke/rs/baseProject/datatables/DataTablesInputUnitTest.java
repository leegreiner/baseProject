package edu.duke.rs.baseProject.datatables;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIn.in;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.duke.rs.baseProject.AbstractBaseTest;

public class DataTablesInputUnitTest extends AbstractBaseTest {
  @Test
  public void whenColumnsExist_thenGetColumnsAsMapReturnsColumns() {
    final DataTablesInput input = new DataTablesInput();
    final Column column1 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    final Column column2 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
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
    final Column column1 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    final Column column2 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    input.getColumns().add(column1);
    input.getColumns().add(column2);
    
    assertThat(input.getColumn(column1.getData()), equalTo(column1));
    assertThat(input.getColumn(column2.getData()), equalTo(column2));
  }
  
  @Test
  public void whenColumnsDoesntExist_thenGetColumnReturnsNull() {
    final DataTablesInput input = new DataTablesInput();
    final Column column1 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    final Column column2 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    input.getColumns().add(column1);
    input.getColumns().add(column2);
    
    assertThat(input.getColumn(column1.getData() + "abc"), equalTo(null));
  }
  
  @Test
  public void whenNullPassedToGetColumn_thenGetColumnReturnsNull() {
    final DataTablesInput input = new DataTablesInput();
    final Column column1 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    final Column column2 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    input.getColumns().add(column1);
    input.getColumns().add(column2);
    
    assertThat(input.getColumn(null), equalTo(null));
  }
  
  @Test
  public void whenParametersPassedToAddColumn_thenGetColumnIsAdded() {
    final DataTablesInput input = new DataTablesInput();
    final Column column1 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    final Column column2 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    input.addColumn(column1.getData(), column1.getSearchable(), column1.getOrderable(), column1.getSearch().getValue());
    input.getColumns().add(column2);
    
    assertThat(column1, in(input.getColumns()));
  }
  
  @Test
  public void whenNullColumnNamePassed_thenAddOrderReturnsDoesNothing() {
    final DataTablesInput input = new DataTablesInput();
    final Column column1 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    final Column column2 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    input.getColumns().add(column1);
    input.getColumns().add(column2);
    
    final List<Order> expected = input.getOrder();
    
    input.addOrder(null, true);
    
    assertThat(input.getOrder(), containsInAnyOrder(expected.toArray()));
  }
  
  @Test
  public void whenOrderPassed_thenAddOrderAddsOrder() {
    final DataTablesInput input = new DataTablesInput();
    final Column column1 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    final Column column2 = new Column(easyRandom.nextObject(String.class), "", true, true, new Search(easyRandom.nextObject(String.class), false));
    input.getColumns().add(column1);
    input.getColumns().add(column2);
    final List<Order> beforeOrders = input.getOrder();
    
    input.addOrder(column1.getData(), true);
    
    assertThat(input.getOrder().size(), equalTo(beforeOrders.size()));
  }
}
