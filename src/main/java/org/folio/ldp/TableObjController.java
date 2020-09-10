package org.folio.ldp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/ldp/db/tables")
public class TableObjController {
  @Autowired TableObjRepository tableRepository;
  private List<TableObj> tables;
  
  @GetMapping
  public List<TableObj> getTableObjs() {
    if(tables == null) {
      tables = (List<TableObj>) tableRepository.findAll();
    }
    return tables;
  }
}
