using UnityEngine;
using System.Collections;

public class GameManager : MonoBehaviour {

    public PieceSpawner pieceSpawner;
    public Piece piece;
    public Action action;

	public void disableSpawner()
    {
        pieceSpawner.enabled = false;
    }

    public void disablePiece()
    {
        piece.enabled = false;
        action.enabled = false;
    }
}
