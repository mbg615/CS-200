package chocan.controller;


import chocan.database.CredentialsDatabase;
import chocan.database.ServiceDatabase;

/**
 * AbstractController.java
 * Abstract class for all controllers.

  Defines the interface for all user controllers.

 */

public abstract class AbstractController {
    protected final CredentialsDatabase userDatabase;
    protected final ServiceDatabase serviceDatabase;

    AbstractController(CredentialsDatabase userDatabase) {
        this.userDatabase = userDatabase;
        this.serviceDatabase = null;
    }
}