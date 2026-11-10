package it.polito.extgol;

import java.util.Objects;
import java.util.Optional;

public class CellRepository  extends GenericExtGOLRepository<Cell, Long> {
    public CellRepository()  { super(Cell.class);  }

    /**
     * Load a cell ang retrieves its full state
     *
     * @param id the ID of the cell
     * @return the fully loaded Cell, or null if not found
     */
    public Optional<Cell> load(Long id, Game game, int step) {
        Objects.requireNonNull(game);

        Optional<Cell> pastCell = findById(id);

        if (pastCell.isEmpty() || game.getGenerations().get(step).getStateCell(pastCell.get()).isEmpty()){
            return Optional.empty();
        }

        Cell newCell = new Cell(pastCell.get().getCoordinates(), pastCell.get().tile, pastCell.get().board, pastCell.get().game);
        newCell.setType(pastCell.get().cellType);

        CellState cellState = game.getGenerations().get(step).getStateCell(pastCell.get()).get();
        newCell.setLifePoints(cellState.lifepoints);
        newCell.setMood(cellState.cellMood);
        newCell.setAlive(cellState.isAlive);

        return Optional.of(newCell);
    }

}
