package edu.duke.rs.baseProject.datatables;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import edu.duke.rs.baseProject.AbstractBaseTest;

public class DataTablesUtilsTest extends AbstractBaseTest {
  @Test
  public void whenInputtOnlyHasStartAndLength_thenResultIsUnsorted() {
    final DataTablesInput input = new DataTablesInput();
    input.setStart(20);
    input.setLength(10);
    
    final Pageable result = DataTablesUtils.toPage(input, null, null);
    
    assertThat(result.getOffset(), equalTo(input.getStart().longValue()));
    assertThat(result.getPageNumber(), equalTo(input.getStart() / input.getLength()));
    assertThat(result.getPageSize(),equalTo(input.getLength()));
    assertThat(result.getSort().isSorted(), equalTo(false));
    assertThat(result.getSort(), equalTo(Sort.unsorted()));
  }
  
  @Test
  public void whenInputHasSort_thenResultIsSorted() {
    final String colName1 = easyRandom.nextObject(String.class);
    final String colName2 = easyRandom.nextObject(String.class);
    final DataTablesInput input = new DataTablesInput();
    input.setStart(20);
    input.setLength(10);
    input.addColumn(colName1, false, true, "");
    input.addColumn(colName2, false, true, "");
    input.addOrder(colName1, false);
    input.addOrder(colName2, true);
    
    final Pageable result = DataTablesUtils.toPage(input, null, null);
    
    assertThat(result.getOffset(), equalTo(input.getStart().longValue()));
    assertThat(result.getPageNumber(), equalTo(input.getStart() / input.getLength()));
    assertThat(result.getPageSize(),equalTo(input.getLength()));
    assertThat(result.getSort().isSorted(), equalTo(true));
    
    Sort.Order sortOrder = result.getSort().getOrderFor(colName1);
    assertThat(sortOrder, notNullValue());
    assertThat(sortOrder.getDirection(), equalTo(Sort.Direction.DESC));
    
    sortOrder = result.getSort().getOrderFor(colName2);
    assertThat(sortOrder, notNullValue());
    assertThat(sortOrder.getDirection(), equalTo(Sort.Direction.ASC));
  }
  
  @Test
  public void whenInputHasRenamedColumns_thenResultHasRenamedColumns() {
    final String colName1 = easyRandom.nextObject(String.class);
    final String colName2 = easyRandom.nextObject(String.class);
    final DataTablesInput input = new DataTablesInput();
    input.setStart(20);
    input.setLength(10);
    input.addColumn(colName1, false, true, "");
    input.addColumn(colName2, false, true, "");
    input.addOrder(colName1, false);
    input.addOrder(colName2, true); 
    
    final Map<String, String> renamedColumns = new HashMap<String, String>(input.getColumns().size());
    final String renamedColName1 = easyRandom.nextObject(String.class);
    final String renamedColName2 = easyRandom.nextObject(String.class);
    renamedColumns.put(colName1, renamedColName1);
    renamedColumns.put(colName2, renamedColName2);
    
    final Pageable result = DataTablesUtils.toPage(input, renamedColumns, null);
    
    assertThat(result.getOffset(), equalTo(input.getStart().longValue()));
    assertThat(result.getPageNumber(), equalTo(input.getStart() / input.getLength()));
    assertThat(result.getPageSize(),equalTo(input.getLength()));
    assertThat(result.getSort().isSorted(), equalTo(true));
    
    Sort.Order sortOrder = result.getSort().getOrderFor(renamedColName1);
    assertThat(sortOrder, notNullValue());
    assertThat(sortOrder.getDirection(), equalTo(Sort.Direction.DESC));
    
    sortOrder = result.getSort().getOrderFor(renamedColName2);
    assertThat(sortOrder, notNullValue());
    assertThat(sortOrder.getDirection(), equalTo(Sort.Direction.ASC));
  }
  
  @Test
  public void whenAdditionalOrderFieldGiven_thenResultHasAdditionalSortOrders() {
    final String colName1 = easyRandom.nextObject(String.class);
    final String colName2 = easyRandom.nextObject(String.class);
    final DataTablesInput input = new DataTablesInput();
    input.setStart(20);
    input.setLength(10);
    input.addColumn(colName1, false, true, "");
    input.addColumn(colName2, false, true, "");
    input.addOrder(colName1, false);
    input.addOrder(colName2, true);
    
    final Map<String, String> renamedColumns = new HashMap<String, String>(input.getColumns().size());
    final String renamedColName1 = easyRandom.nextObject(String.class);
    final String renamedColName2 = easyRandom.nextObject(String.class);
    renamedColumns.put(colName1, renamedColName1);
    renamedColumns.put(colName2, renamedColName2);
    
    final List<String> additionalOrders = new ArrayList<String>();
    final String orderName1 = easyRandom.nextObject(String.class);
    final String orderName2 = easyRandom.nextObject(String.class);
    additionalOrders.add(orderName1);
    additionalOrders.add(orderName2);
    
    final Pageable result = DataTablesUtils.toPage(input, renamedColumns, additionalOrders);
    
    assertThat(result.getOffset(), equalTo(input.getStart().longValue()));
    assertThat(result.getPageNumber(), equalTo(input.getStart() / input.getLength()));
    assertThat(result.getPageSize(),equalTo(input.getLength()));
    assertThat(result.getSort().isSorted(), equalTo(true));
    
    Sort.Order sortOrder = result.getSort().getOrderFor(renamedColName1);
    assertThat(sortOrder, notNullValue());
    assertThat(sortOrder.getDirection(), equalTo(Sort.Direction.DESC));
    
    sortOrder = result.getSort().getOrderFor(renamedColName2);
    assertThat(sortOrder, notNullValue());
    assertThat(sortOrder.getDirection(), equalTo(Sort.Direction.ASC));
    
    sortOrder = result.getSort().getOrderFor(orderName1);
    assertThat(sortOrder, notNullValue());
    assertThat(sortOrder.getDirection(), equalTo(Sort.Direction.ASC));
    
    sortOrder = result.getSort().getOrderFor(orderName2);
    assertThat(sortOrder, notNullValue());
    assertThat(sortOrder.getDirection(), equalTo(Sort.Direction.ASC));
  }
  
  @Test
  public void whenPageIsPopulated_thenOutputIsPopulated() {
    final DataTablesInput input = new DataTablesInput();
    input.setStart(2);
    input.setLength(10);
    
    final Pageable pageable = DataTablesUtils.toPage(input, null, null);
    final List<String> strings = new ArrayList<String>();
    strings.add(easyRandom.nextObject(String.class));
    strings.add(easyRandom.nextObject(String.class));
    final Page<String> page = new PageImpl<String>(strings, pageable, 100L);
    final DataTablesOutput<String> output = DataTablesUtils.toDataTablesOutput(input, page);
    
    assertThat(output.getData(), equalTo(page.getContent()));
    assertThat(output.getDraw(), equalTo(input.getDraw()));
    assertThat(output.getRecordsTotal(), equalTo(page.getTotalElements()));
  }
  
  @Test
  public void whenSortAdded_thenSortIsPresent() {
    final Pageable page = PageRequest.of(1, 100);
    final Sort sort = Sort.by(easyRandom.nextObject(String.class));
    
    final Pageable result = DataTablesUtils.addSort(page, sort);
    
    assertThat(result.getPageNumber(), equalTo(page.getPageNumber()));
    assertThat(result.getOffset(), equalTo(page.getOffset()));
    assertThat(result.getPageSize(), equalTo(page.getPageSize()));
    assertThat(result.getSort(), equalTo(sort));
  }
}
