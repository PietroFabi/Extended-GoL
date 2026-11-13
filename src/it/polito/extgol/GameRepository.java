package it.polito.extgol;

import jakarta.persistence.EntityManager;

public class GameRepository extends GenericExtGOLRepository<Game, Long> {
    public GameRepository()  { super(Game.class);  }


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
