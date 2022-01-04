package com.example.demo.entity;

import static javax.persistence.GenerationType.IDENTITY;

import com.sun.istack.NotNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "server")
public class Server {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "server_id")
  private Long id;

  @NotNull
  private String name = "server_name";

  @NotNull
  private boolean switched = false;

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public boolean isSwitched() {
    return switched;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSwitched(boolean switched) {
    this.switched = switched;
  }
}
