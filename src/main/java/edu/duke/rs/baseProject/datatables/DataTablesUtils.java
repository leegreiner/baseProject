package edu.duke.rs.baseProject.datatables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class DataTablesUtils {
  private static int DEFAULT_START = 0;
  private static int DEFAULT_SIZE = 10;
  
  private DataTablesUtils() {}
  
  public static Pageable toPage(final DataTablesInput input, Map<String, String> renamedColumns, List<String> additionalOrders) {
    final List<Sort.Order> sortOrders = new ArrayList<Sort.Order>();
    
    for (final Order order : input.getOrder()) {
      String columnName = input.getColumns().get(order.getColumn()).getData();
      final Sort.Direction direction = order.getDir().equals(Order.DESC) ? Sort.Direction.DESC : Sort.Direction.ASC;
      
      if (renamedColumns != null && renamedColumns.containsKey(columnName)) {
        columnName = renamedColumns.get(columnName);
      }
      
      sortOrders.add(new Sort.Order(direction, columnName));
    }
    
    if (additionalOrders != null) {
      additionalOrders.stream().forEach(field -> sortOrders.add(new Sort.Order(Sort.Direction.ASC, field)));
    }
    
    final Sort sort = sortOrders.isEmpty() ? Sort.unsorted() : Sort.by(sortOrders);
    final int start = input.getStart() == null ? DEFAULT_START : input.getStart();
    final int size = input.getLength() == null ? DEFAULT_SIZE : input.getLength();
    
    return  PageRequest.of(start/size, input.getLength(), sort);
  }

  public static <T> DataTablesOutput<T> toDataTablesOutput(final DataTablesInput input, Page<T> page) {
    final DataTablesOutput<T> output = new DataTablesOutput<T>();
    
    output.setData(page.getContent());
    output.setDraw(input.getDraw());
    output.setRecordsFiltered(page.getTotalElements());
    output.setRecordsTotal(page.getTotalElements());
    
    return output;
  }
  
  public static Pageable addSort(Pageable page, Sort sort) {
    return PageRequest.of(page.getPageNumber(), page.getPageSize(), page.getSort().and(sort));
  }
}
