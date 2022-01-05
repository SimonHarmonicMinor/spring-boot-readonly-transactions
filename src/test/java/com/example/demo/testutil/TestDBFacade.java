package com.example.demo.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.support.TransactionTemplate;

public class TestDBFacade {

  @Autowired
  private TestEntityManager testEntityManager;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void cleanDatabase() {
    transactionTemplate.execute(status -> {
      JdbcTestUtils.deleteFromTables(jdbcTemplate, "server");
      return null;
    });
  }

  public <T> Builder<T> persistedOnce(Builder<T> builder) {
    return new Builder<T>() {
      private T entity;

      @Override
      public T build() {
        if (entity == null) {
          entity = persisted(builder).build();
        }
        return entity;
      }
    };
  }

  public <T> T find(Object id, Class<T> entityClass) {
    return transactionTemplate.execute(
        status -> testEntityManager.find(entityClass, id)
    );
  }

  public <T> Builder<T> persisted(Builder<T> builder) {
    return () -> transactionTemplate.execute(status -> {
      final var entity = builder.build();
      testEntityManager.persistAndFlush(entity);
      return entity;
    });
  }

  public void saveAll(Builder<?>... builders) {
    transactionTemplate.execute(status -> {
      for (Builder<?> b : builders) {
        save(b);
      }
      return null;
    });
  }

  public <T> T save(Builder<T> builder) {
    return transactionTemplate.execute(
        status -> testEntityManager.persistAndFlush(builder.build())
    );
  }

  @TestConfiguration
  static class Config {

    @Bean
    public TestDBFacade testDBFacade() {
      return new TestDBFacade();
    }
  }
}
