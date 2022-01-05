package com.example.demo.service;

import static com.example.demo.entity.Server.Type.JBOSS;
import static com.example.demo.entity.Server.Type.TOMCAT;
import static com.example.demo.entity.Server.Type.WEB_LOGIC;
import static com.example.demo.entity.ServerBuilder.aServer;
import static com.example.demo.service.OperationStatus.ALLOWED;
import static com.example.demo.service.OperationStatus.RESTRICTED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.testutil.DBTest;
import com.example.demo.testutil.PostgresSuite;
import com.example.demo.testutil.TestDBFacade;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.UnexpectedRollbackException;

@DBTest
class ServerAllowedOperationsTest extends PostgresSuite {

  @Autowired
  private TestDBFacade db;
  @Autowired
  private ServerAllowedOperations allowedOperations;

  @BeforeEach
  void beforeEach() {
    db.cleanDatabase();
  }

  @Test
  @DisplayName("Should allow all servers to switch on")
  void shouldAllowAllServersToSwitchOn() {
    final var server =
        aServer().withSwitched(false).withType(WEB_LOGIC);
    final var s1 = db.save(server.withName("s1"));
    final var s2 = db.save(server.withName("s2"));
    final var s3 = db.save(server.withName("s3"));
    final var serversIds = List.of(s1.getId(), s2.getId(), s3.getId());

    final var operations = allowedOperations.getServersSwitchOnStatus(
        serversIds
    );

    for (Long serversId : serversIds) {
      assertEquals(ALLOWED, operations.get(serversId));
    }
  }

  @Test
  @DisplayName("Should throw UnexpectedRollbackException")
  void shouldThrowUnexpectedRollbackException() {
    assertThrows(
        UnexpectedRollbackException.class,
        () -> shouldNotAllowSomeServersToSwitchOn(allowedOperations::getServersSwitchOnStatus)
    );
  }

  @Test
  @DisplayName("Should succeed due to REQUIRES_NEW usage")
  void shouldSucceedDueToRequiresNewUsage() {
    assertDoesNotThrow(
        () -> shouldNotAllowSomeServersToSwitchOn(
            allowedOperations::getServersSwitchOnStatusRequiresNew
        )
    );
  }

  @Test
  @DisplayName("Should succeed due to noRollBackFor usage")
  void shouldSucceedDueToNoRollBackForUsage() {
    assertDoesNotThrow(
        () -> shouldNotAllowSomeServersToSwitchOn(
            allowedOperations::getServersSwitchOnStatusNoRollBackFor
        )
    );
  }

  @Test
  @DisplayName("Should succeed due to @READ_TRANSACTIONAL usage")
  void shouldSucceedDueToReadTransactionalUsage() {
    assertDoesNotThrow(
        () -> shouldNotAllowSomeServersToSwitchOn(
            allowedOperations::getServersSwitchOnStatusReadTransactional
        )
    );
  }

  private void shouldNotAllowSomeServersToSwitchOn(
      Function<Collection<Long>, Map<Long, OperationStatus>> businessLogic
  ) {
    final var s1 = db.save(
        aServer().withSwitched(true).withType(JBOSS)
    );
    final var s2 = db.save(
        aServer().withSwitched(false).withType(TOMCAT)
    );
    final var webLogic = aServer().withSwitched(false).withType(WEB_LOGIC);
    final var s3 = db.save(webLogic);
    db.saveAll(
        webLogic.withSwitched(true),
        webLogic.withSwitched(true),
        webLogic.withSwitched(true)
    );
    final var serversIds = List.of(s1.getId(), s2.getId(), s3.getId());

    final var operations = businessLogic.apply(serversIds);

    assertEquals(RESTRICTED, operations.get(s1.getId()));
    assertEquals(ALLOWED, operations.get(s2.getId()));
    assertEquals(RESTRICTED, operations.get(s3.getId()));
  }
}