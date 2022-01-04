package com.example.demo.service;

import static com.example.demo.entity.Server.Type.WEB_LOGIC;
import static com.example.demo.entity.ServerBuilder.aServer;
import static com.example.demo.service.OperationStatus.ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.demo.testutil.DBTest;
import com.example.demo.testutil.PostgresSuite;
import com.example.demo.testutil.TestDBFacade;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}