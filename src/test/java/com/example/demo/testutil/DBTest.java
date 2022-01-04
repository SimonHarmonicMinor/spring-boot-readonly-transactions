package com.example.demo.testutil;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@Retention(RUNTIME)
@SpringBootTest
@AutoConfigureTestEntityManager
@ContextConfiguration(classes = TestDBFacade.Config.class)
public @interface DBTest {

}
