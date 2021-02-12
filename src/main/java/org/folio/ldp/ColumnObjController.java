package org.folio.ldp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ldp/db/columns")
public class ColumnObjController {
  @Autowired ColumnObjRepository columnRepository;
  
  @GetMapping
  public List<ColumnObj> getColumnsForTable(String table) {

    // TODO: Validate table string

    return (List<ColumnObj>) columnRepository.findByTableName(table);
  }

  @Cacheable(cacheNames="columns")
  public Map<String, String> getColumnsForTableAsMap(String table) {
    
    // TODO: Validate table string

    List<ColumnObj> columns = (List<ColumnObj>) columnRepository.findByTableName(table);
    Map<String, String> columnMap = new HashMap<String, String>();
    for(ColumnObj col : columns) {
      columnMap.put(col.columnName, col.data_type);
    }
    return columnMap;
  }
}
