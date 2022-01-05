package com.example.demo.service;

import static com.example.demo.entity.Server.Type.WEB_LOGIC;
import static com.example.demo.entity.ServerBuilder.aServer;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.entity.Server;
import com.example.demo.exception.OperationRestrictedException;
import com.example.demo.testutil.DBTest;
import com.example.demo.testutil.PostgresSuite;
import com.example.demo.testutil.TestDBFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DBTest
class ServerUpdateServiceTest extends PostgresSuite {

  @Autowired
  private TestDBFacade db;
  @Autowired
  private ServerUpdateService server;

  @BeforeEach
  void beforeEach() {
    db.cleanDatabase();
  }

  @Test
  void shouldRollbackIfServerCannotBeSwitchedOn() {
    final var jboss = aServer().withType(WEB_LOGIC).withSwitched(true);
    db.saveAll(jboss, jboss, jboss);
    final var serverId = db.save(jboss.withSwitched(false)).getId();

    assertThrows(OperationRestrictedException.class, () -> server.switchOnServer(serverId));

    final var server = db.find(serverId, Server.class);
    assertFalse(server.isSwitched());
  }
}