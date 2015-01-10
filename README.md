# Risk Backend

    When you pull this, make sure maven is install on your computer, run `mvn test` after install

## Summary

This is where all the centralised magic happens. This includes

* Game simulation
* Player management
* Server
* Multiplayer networking

## The files

Most of the actual magic happens in 'src/main/java'. Within this, there are packages which contain different bits of
functionality. 

* 'GameEngine' contains classes for running the games including the possible states a game can be in. It essentially
pulls together functionality from all the other packages.
* 'GameState' contains classes which can keep track of the state of a game, including the map, players, armies, cards,
etc.
    * 'GameUtils' contains utilities for manipulating the state.
    * NB the current design tries to separate functionality and state. IE the actual state classes only really have 
    getters and setters
* 'PlayerInput' contains classes for developing player interfaces (web interfaces etc) and also AIs - since they are
abstracted in the same way. That is to say, the programme can't tell if it's asking a human or a computer to take a
turn.

## Maven

The project uses Maven for dependency management this means that rather than having jars cluttering up git, all you need
to do to add a dependency is to add the appropriate xml string to the pom.xml (see the current pom.xml file for
examples)

## Troubleshooting

* If there are warnings about missing classes like 'grapht' and 'simple-json', that's probably because your IDE hasn't
resolved the maven dependencies right, search [your IDE] + 'maven downlaod dependencies' and you should get
instructions.

## Tests

The tests are in 'src/test/java/'. There's only one right now.

## TODO

TODO: Write loadsa tests
TODO: Develop AIs
TODO: Finish game implementation
TODO: Add websocket support
TODO: Finish front-end (in a separate repository)
TODO: allow maps to be parsed form JSON
TODO: design a lobby which players can use to join, create games etc.
TODO: Add more todos
TODO: