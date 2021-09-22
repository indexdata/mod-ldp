package org.folio.ldp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.ClassRule;

import java.util.List;

import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {TableObjControllerTest.Initializer.class})
@Sql({"/drop-schema.sql", "/schema.sql","/data.sql"})

@AutoConfigureMockMvc
public class TableObjControllerTest {

  @ClassRule
  public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:12-alpine")
    .withDatabaseName("integration-tests-db")
    .withUsername("sa")
    .withPassword("sa");


  static class Initializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
      public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
          TestPropertyValues.of(
            "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
            "spring.datasource.username=" + postgreSQLContainer.getUsername(),
            "spring.datasource.password=" + postgreSQLContainer.getPassword()
          ).applyTo(configurableApplicationContext.getEnvironment());
      }
  }

  @Autowired
  private MockMvc mvc;

  @Autowired
  private TableObjRepository tableObjRepository;
  
  public final static String QUERY_PATH = "/ldp/db/tables";

  @Test 
  public void getMVCTables() throws Exception {
    mvc.perform(get(QUERY_PATH)
      .contentType("application/json"))
        .andExpect(status().isOk());
  }

  @Test
  public void getTables() throws Exception {
    List<TableObj> tableList = tableObjRepository.findAll();
    boolean foundTable = false;
    for(TableObj to : tableList) {
      if(to.tableName.equals("user_users")) {
        foundTable = true;
      }
    }
    assertTrue(foundTable);
  }



  
}
