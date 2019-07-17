package edu.duke.rs.baseProject.datatables;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataTablesTestUtils {

  private DataTablesTestUtils() {}

  public static String toRequestParameters(final DataTablesInput input) {
    final Map<String, String> params = new LinkedHashMap<String, String>();
    
    if (input.getDraw() != null) {
      params.put("draw", input.getDraw().toString());
    }
    
    if (input.getLength() != null) {
      params.put("length", input.getLength().toString());
    }
    
    if (input.getStart() != null) {
      params.put("start", input.getStart().toString());
    }
    
    if (input.getSearch() != null) {
      params.put("search.regex", input.getSearch().getRegex() == null ? Boolean.FALSE.toString() : input.getSearch().getRegex().toString());
      params.put("search.value", input.getSearch().getValue() == null ? "" : input.getSearch().getValue());
    }
    
    for (int i = 0; i < input.getColumns().size(); i++) {
      final Column column = input.getColumns().get(i);
      params.put("columns[" + i + "].data", column.getData());
      params.put("columns[" + i + "].name", column.getName());
      params.put("columns[" + i + "].searchable", column.getSearchable().toString());
      params.put("columns[" + i + "].orderable", column.getOrderable().toString());
      params.put("columns[" + i + "].search.regex", column.getSearch().getRegex().toString());
      params.put("columns[" + i + "].search.value", column.getSearch().getValue());
    }
    
    for (int i = 0; i < input.getOrder().size(); i++) {
      final Order order = input.getOrder().get(i);
      params.put("order[" + i + "].column", Integer.toString(i));
      params.put("order[" + i + "].dir", order.getDir());
    }
    
    final StringBuffer buf = new StringBuffer();
    
    for (String key : params.keySet()) {
      buf.append(key).append('=').append(params.get(key)).append('&');
    }
    
    buf.setLength(buf.length() - 1);
    
    return buf.toString();
  }
}
