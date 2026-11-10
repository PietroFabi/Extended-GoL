# Extended Game of Life

A Java implementation of [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life) extended with cell types, energy, moods, interactive tiles and global events, with every generation of a game saved to a database through JPA/Hibernate.

Built as the team project (four students) for the Object-Oriented Programming course at Politecnico di Torino, 2025. The course provided a basic Game of Life, the requirements and the acceptance tests; the team implemented the extensions described below.

## What it does

On top of the classic survival, death and respawn rules, the simulation adds:

- **Life points.** Every cell has an energy level that rises when it survives, drops when it dies and resets on respawn. A cell needs non-negative life points to stay alive.
- **Cell types.**
  - `HIGHLANDER` survives three consecutive generations of otherwise fatal conditions.
  - `LONER` survives with a single neighbour.
  - `SOCIAL` survives with up to eight neighbours.
- **Moods and interactions.** Cells are `NAIVE`, `HEALER` or `VAMPIRE`. Healers give a life point to naive neighbours; vampires drain a life point from healers and naive cells, and turn naive cells into vampires.
- **Interactive tiles.** Each board tile can add or remove life points from the cell on it at the start of every generation.
- **Global events**, each lasting one generation:

  | Event | Effect |
  | --- | --- |
  | Cataclysm | resets every cell's life points to 0 |
  | Famine | every tile takes 1 life point from its cell |
  | Bloom | every tile gives 2 life points to its cell |
  | Blood Moon | vampires can also turn healers into vampires |
  | Sanctuary | healers gain 1 life point, vampires become naive |

- **Board analytics** written with Java streams: alive-cell count, highest-energy cell, cells grouped by energy level or by number of alive neighbours, counts by cell type, top-N cells by energy, energy statistics per generation and as a time series.
- **Text visualization** of any generation of the board.
- **Persistence.** Games, boards, tiles, cells, generations and events are JPA entities stored in an in-memory H2 database. A saved game can be reloaded with its full timeline, including the per-generation state of each cell (alive or dead, life points, mood) and the scheduled events.

The complete rules are in [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md) (Italian version: [docs/REQUIREMENTS_it.md](docs/REQUIREMENTS_it.md)).

## Project structure

```
├── pom.xml                         Maven build (Java 17)
├── resources/META-INF/
│   └── persistence.xml             Hibernate + in-memory H2 configuration
├── src/it/polito/extgol/
│   ├── ExtendedGameOfLife.java     Facade: evolve, run, visualize, save, load events
│   ├── Game.java                   A simulation: board, generations, events
│   ├── Generation.java             Snapshot of the board at one step
│   ├── Board.java                  Grid of tiles, analytics, visualization
│   ├── Tile.java                   Board square holding a cell; can modify life points
│   ├── Cell.java                   Evolution rules, life points, interactions
│   ├── CellState.java              Per-generation state of a cell
│   ├── CellType.java, CellMood.java, EventType.java
│   ├── Coord.java                  (x, y) value object
│   ├── Evolvable.java, Interactable.java
│   ├── *Repository.java            JPA repositories
│   └── JPAUtil.java                EntityManagerFactory helper
├── test/it/polito/extgol/test/     JUnit 4 tests (61 test methods)
└── docs/                           Requirements and code review checklist from the course
```

## Build and test

Requirements: Java 17 or newer and Maven.

```bash
mvn test
```

The tests use an in-memory database, so nothing needs to be installed or configured. The database user and empty password in `persistence.xml` are H2's defaults for a throwaway in-memory database, not real credentials.

## Example

```java
ExtendedGameOfLife facade = new ExtendedGameOfLife();

Game game = Game.createExtended("demo", 5, 5);
Board board = game.getBoard();

// Start with a horizontal line of three alive cells
Generation start = Generation.createInitial(game, board,
        List.of(new Coord(1, 2), new Coord(2, 2), new Coord(3, 2)));

// Run four generations, with a Bloom event at step 2
Game result = facade.run(game, 4, Map.of(2, EventType.BLOOM));

for (Generation g : result.getGenerations()) {
    System.out.println(facade.visualize(g));
}

facade.saveGame(result);
```

## Tech stack

Java 17, Maven, Jakarta Persistence 3.1, Hibernate ORM 6.4, H2, JUnit 4.

## Credits

The starting code, the requirements documents in `docs/` and most of the test suite were provided by the course teaching staff.
