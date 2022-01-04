package com.example.demo.entity;

import static com.example.demo.entity.Server.Type.JBOSS;

import com.example.demo.entity.Server.Type;
import com.example.demo.testutil.Builder;

public class ServerBuilder implements Builder<Server> {

  private String name = "server_name";
  private boolean switched = false;
  private Type type = JBOSS;

  private ServerBuilder() {
  }

  private ServerBuilder(ServerBuilder builder) {
    this.name = builder.name;
    this.switched = builder.switched;
    this.type = builder.type;
  }

  public static ServerBuilder aServer() {
    return new ServerBuilder();
  }

  public ServerBuilder withName(String name) {
    final var copy = new ServerBuilder(this);
    copy.name = name;
    return copy;
  }

  public ServerBuilder withSwitched(boolean switched) {
    final var copy = new ServerBuilder(this);
    copy.switched = switched;
    return copy;
  }

  public ServerBuilder withType(Type type) {
    final var copy = new ServerBuilder(this);
    copy.type = type;
    return copy;
  }

  @Override
  public Server build() {
    final var server = new Server();
    server.setName(name);
    server.setSwitched(switched);
    server.setType(type);
    return server;
  }
}