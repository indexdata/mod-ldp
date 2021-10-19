package org.folio.ldp;

import schemacrawler.tools.utility.SchemaCrawlerUtility;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.Column;

import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;

public class SchemaUtil {

  public static List<TableObj> getTablesBySchemaName(Connection conn, List<String> schemaNameList)
  throws SchemaCrawlerException  {
    final Catalog catalog;
    final ArrayList<TableObj> tableObjList = new ArrayList<>();
    LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    
    if(schemaNameList != null) {
      for(String schemaName : schemaNameList ) {
        limitOptionsBuilder = limitOptionsBuilder.includeSchemas(
          new RegularExpressionInclusionRule(schemaName));
      }
    }
    
    final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder.builder()
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.minimum());
    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
      .withLoadOptions(loadOptionsBuilder.toOptions())
      .withLimitOptions(limitOptionsBuilder.toOptions());

    catalog = SchemaCrawlerUtility.getCatalog(conn, options);
    for( final Schema schema : catalog.getSchemas()) {
      if(schemaNameList != null && !schemaNameList.contains(schema.getName())) {
        //System.out.println(schema.getName() + " is not a valid schema");
        continue;
      } else {
        for( final Table table : catalog.getTables(schema)) {
          TableObj tableObj = new TableObj();
          tableObj.setTableName(table.getName());
          tableObj.setTableSchema(schema.getName());
          tableObjList.add(tableObj);
        }
      }
    }
    tableObjList.sort((t1, t2) -> t1.getTableSchema().compareTo(t2.getTableSchema()));
    return tableObjList;
  }

  public static List<ColumnObj> getColumnsByTableName(Connection conn, String schemaName,
   String tableName) throws SchemaCrawlerException {
    final Catalog catalog;
    final ArrayList<ColumnObj> columnObjList = new ArrayList<>();
    final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder.builder()
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.builder()
        .setRetrieveTables(true)
        .setRetrieveTableColumns(true)
        .setRetrieveAdditionalColumnAttributes(false)
        .setRetrieveIndexes(false)
        .setRetrieveForeignKeys(false)
        .setRetrievePrimaryKeys(false)
        .setRetrieveRoutines(false)
        .toOptions());
    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
      .withLoadOptions(loadOptionsBuilder.toOptions());
   
    catalog = SchemaCrawlerUtility.getCatalog(conn, options);
    for( final Schema schema : catalog.getSchemas()) {
      System.out.println("Schema: " + schema.getName());
      if(!schema.getName().equals(schemaName)) {
        continue;
      } else {
        for( final Table table : catalog.getTables(schema)) {
          System.out.println("Table: " + table.getName() + " has " + 
            table.getColumns().size() + " columns");
          if(!table.getName().equals(tableName)) {
            continue;
          } else {
            for( final Column column : table.getColumns()) {
              if(column.getName().equals("data")) {
                continue; //We don't return the data column per convention
              }
              ColumnObj columnObj = new ColumnObj();
              columnObj.columnName = column.getName();
              columnObj.data_type = column.getType().toString();
              columnObj.setTableSchema(schemaName);
              columnObj.setTableName(tableName);
              System.out.println("Column: " + column.getName());
              columnObjList.add(columnObj);
            }
          }
        }
      }
    }
    
    return columnObjList;
  }


  
}
