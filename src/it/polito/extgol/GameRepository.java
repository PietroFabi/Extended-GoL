package it.polito.extgol;

import jakarta.persistence.EntityManager;

public class GameRepository extends GenericExtGOLRepository<Game, Long> {
    public GameRepository()  { super(Game.class);  }

    // find
    public Game findGameByName(String name) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("SELECT g FROM GAME g WHERE g.name = :name", Game.class)
                .setParameter("name", name).getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Load a game
     *
     * @param id the ID of the game
     * @return the fully loaded Game or null
     */
    public Game load(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT g FROM Game g WHERE g.id = :id", Game.class)
                .setParameter("id", id)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
    
}
