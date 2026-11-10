package it.polito.extgol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

/**
 * Entity representing a single square on the Game of Life board.
 * Holds coordinate position, occupying Cell, and link back to its Board.
 */
@Entity
public class Tile implements Interactable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Coordinates of the tile on the board. */
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x",
            column = @Column(name = "tile_x", nullable = false)),
        @AttributeOverride(name = "y",
            column = @Column(name = "tile_y", nullable = false))
    })
    private Coord tileCoord;

    /** Reference to the board containing this tile. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    /** Reference to the owning game. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false, updatable = false)
    private Game game;

    /**
     * The cell occupying this tile.
     */
    @OneToOne(
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY
    )
    @JoinColumn(name = "cell_id", nullable = false, unique = true)
    private Cell cell;

    /**
     * The life point modifier applied to the cell.
     */
    private Integer lifePointModifier = 0;

    /** Neighboring tiles for interactions. Not persisted. */
    @Transient
    private Set<Tile> neighbors = new HashSet<Tile>();

    /**
     * Default constructor required by JPA.
     */
    public Tile() {
    }

    /**
     * Constructs a tile at the given coordinates,
     * and the respective cell initialized as not alive
     * @param x column index (0-based)
     * @param y row index (0-based)
     */
    public Tile(int x, int y, Board b, Game g) {
        this.tileCoord = new Coord(x,y);
        this.board = b;
        this.game = g;
        this.cell = new Cell(this.tileCoord, this, b, g);
        this.cell.setLifePoints(this.lifePointModifier);
    }

    /**
     * Retrieves the Board to which this tile belongs.
     *
     * @return the parent Board instance
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Associates this tile with the given Board.
     *
     * @param board the Board on which this tile resides
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Retrieves the Cell currently occupying this tile.
     *
     * @return the Cell on this tile, or null if the tile is empty
     */
    public Cell getCell() {
        return cell;
    }

    /**
     * Places or removes a Cell on this tile.
     *
     * @param cell the Cell to assign to this tile, or null to clear it
     */
    public void setCell(Cell cell) {
        this.cell = cell;
    }

    /**
     * Checks whether this tile currently hosts a live cell.
     *
     * @return true if a non-null, alive Cell occupies this tile; false otherwise
     */
    public boolean hasCell() {
        return cell != null && cell.isAlive();
    }

    /**
     * Initializes the neighbor relationships for this tile.
     *
     * @param neighborsList the Set of tiles adjacent to this one
     */
    public void initializeNeighbors(Set<Tile> neighborsList) {
        this.neighbors = neighborsList;
    }

    /**
     * Provides the set of neighboring tiles around this tile.
     *
     * @return a Set of adjacent Tile instances
     */
    public Set<Tile> getNeighbors() {
        return this.neighbors;
    }

    /**
     * Method that retrieves the neighbors of a tile which he can interact with.
     * The logic behind this is that, if you obtain the neighbors normally you end
     * up interacting twice with 2 cells.
     *
     * e.g. suppose you have the tile (0, 0) its normal neighbors are (1, 0), (0, 1) and (1, 1)
     * and you make them interact with each other, if now you calculate the neighbors of (1, 0)
     * they are (0, 0), (2, 0), (0,  1), (1, 1) and (2, 1) then you make them interact and the result
     * is that the tiles (0, 0) and (1, 0) have interacted twice because they are respectively normal
     * neighbor of the other.
     *
     * So to avoid this problem you can select only some of its neighbor, in order to avoid double interaction
     * while guaranteeing that all tiles interact once with each of its neighbor.
     *
     * The neighbors are, respectively to a tile, its: north-east, east, south-east and south.
     * (t = tile target, x = neighbor to interact, 0 = tiles disregarded)
     *
     *  0 0 x 0
     *  0 t x 0
     *  0 x x 0
     *
     * for every tile if we choose its neighbors like this we avoid any problems.
     *
     * @return set of tiles to interact with
     */
    public List<Tile> getInteractNeighbors() {
        List<Tile> adj = new ArrayList<>();
        int x, y, i;

        for (x = getX() + 1, y = getY() - 1, i = 0; i < 3; i++){
            if (i == 2){
                if (x-1>=0 && y>=0 && x-1<board.getWidth() && y<board.getHeight()){
                    adj.add(board.getTile(new Coord(x-1, y)));
                }
            }
            if (x>=0 && y>=0 && x<board.getWidth() && y<board.getHeight()){
                adj.add(board.getTile(new Coord(x, y)));
            }
            y = y + 1;
        }
        return adj;
    }

    /**
     * Returns the database identifier for this Tile.
     *
     * @return the unique ID of this tile
     */
    public Long getId() {
        return id;
    }

    /**
     * Retrieves the coordinate object representing this tile's position.
     *
     * @return a Coord containing the tile’s x and y values
     */
    public Coord getCoordinates() {
        return this.tileCoord;
    }

    /**
     * Returns the X‐coordinate (column index) of this tile.
     *
     * @return the x value of this tile’s coordinate
     */
    public int getX() {
        return this.tileCoord.getX();
    }

    /**
     * Returns the Y‐coordinate (row index) of this tile.
     *
     * @return the y value of this tile’s coordinate
     */
    public int getY() {
        return this.tileCoord.getY();
    }

    // EXTENDED BEHAVIORS
    
    /**
     * Retrieves the life point modifier that this tile applies to any cell occupying it.
     * 
     * A positive value grants extra energy to the cell each generation, while
     * a negative value drains energy. A zero value has no effect.
     *
     * @return the integer modifier to a cell’s lifePoints when evolving
     */
    public Integer getLifePointModifier() {
        return lifePointModifier;
    }

    /**
     * Update the lifePointModifier of this tile.
     *
     * @param lifePointModifier the new value of lifePointModifier to assign to the tile
     */
    public void setLifePointModifier(Integer lifePointModifier) {
        this.lifePointModifier = lifePointModifier;
    }

    @Override
    public void interact(Cell other) {
        other.setLifePoints(other.getLifePoints() + lifePointModifier);
    }

}
