package it.polito.extgol.test;

import it.polito.extgol.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static it.polito.extgol.test.TestBranchUtils.assumeBranch;
import static org.junit.Assert.assertEquals;

public class PersonalAddedR1Test {
    private ExtendedGameOfLife facade;
    private Game game;
    private Board board;

    /**
     * Close JPA after all tests.
     */
    @AfterClass
    public static void closeDB() {
        JPAUtil.close();
    }

    /**
     * Prepare a clean database and new game before each test.
     */
    @Before
    public void setUp() {
        clearDatabase();
        facade = new ExtendedGameOfLife();
        game  = Game.createExtended("TestGame", 3, 3);
        board = game.getBoard();
    }

    private void clearDatabase() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (String table : List.of("generation_state", "generation", "board", "game", "tile")) {
            try {
                em.createNativeQuery("DELETE FROM " + table).executeUpdate();
            } catch (Exception e) {
                // table not created yet: nothing to delete
            }
        }
        tx.commit();
        em.close();
    }

    // vampires and healers

    @Test
    public void interactionBetweenMoods(){
        assumeBranch("R1");

        Generation init = Generation.createInitial(game, game.getBoard(),List.of(
                new Coord(1, 1),
                new Coord(2, 1),
                new Coord(1, 2)));
        game.setMoods(CellMood.NAIVE, List.of(
                new Coord(1,1)));
        game.setMoods(CellMood.HEALER, List.of(
                new Coord(2,2)));
        game.setMoods(CellMood.VAMPIRE, List.of(
                new Coord(2,1)));

        Game result = facade.run(game, 1);
        Generation secondGeneration = result.getGenerations().get(1);

        assertEquals("naive should have 1 lifepoint because +1 for healer -1 for vampire and +1 for surviving", 1, secondGeneration.getBoard().getTile(new Coord(1, 1)).getCell().getLifePoints());

    }

    @Test
    public void deadCellInteraction(){
        assumeBranch("R1");

        Cell vampire = new Cell();
        vampire.setAlive(true);
        vampire.setMood(CellMood.VAMPIRE);
        vampire.setLifePoints(3);

        Cell healer = new Cell();
        healer.setAlive(true);
        healer.setMood(CellMood.HEALER);
        healer.setLifePoints(3);

        Cell deadCell = new Cell();
        deadCell.setMood(CellMood.NAIVE);

        vampire.interact(deadCell);
        healer.interact(deadCell);

        assertEquals(
                "Vampire interacting with dead cell: there should not be any lifePoints change",
                3,
                vampire.getLifePoints()
        );
        assertEquals(
                "healer interacting with dead cell: there should not be any lifePoints change",
                0,
                deadCell.getLifePoints()
        );
    }
}
