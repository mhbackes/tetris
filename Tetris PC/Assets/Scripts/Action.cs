using UnityEngine;
using System.Collections;

public class Action : MonoBehaviour {

    public bool can360;

    private Grid gridRef;

    void Awake()
    {
        gridRef = GameObject.Find("Grid").GetComponent<Grid>();
    }

    public enum MOVE
    {
        Up,
        Down,
        Right,
        Left
    };

    public enum ROTATE
    {
        Right,
        Left
    };

    public bool Move(MOVE movement)
    {
        switch (movement)
        {
            case MOVE.Right:

                return tryToMove(new Vector3(1f, 0f, 0f));

            case MOVE.Left:

                return tryToMove(new Vector3(-1f, 0f, 0f));

            case MOVE.Up:

                return tryToMove(new Vector3(0f, 1f, 0f));

            case MOVE.Down:

                return tryToMove(new Vector3(0f, -1f, 0f));

            default: return false;
        }
    }

    public void Rotate(ROTATE rotation)
    {
        if(!can360)
        {
            if (transform.rotation.z < 0)
            {
                tryToRotate(new Vector3(0f, 0f, 90f));
            }
            else
            {
                tryToRotate(new Vector3(0f, 0f, -90f));
            }
        }
        else
        {
            switch (rotation)
            {
                case ROTATE.Right:

                    tryToRotate(new Vector3(0f, 0f, -90f));

                    break;

                case ROTATE.Left:

                    tryToRotate(new Vector3(0f, 0f, 90f));

                    break;

                default: break;
            }
        }
    }

    bool tryToMove(Vector3 offset)
    {
        resetGrid();

        foreach (Transform child in transform)
        {
            Vector3 futurePosition = child.transform.position + offset;

            if (!gridRef.isPositionAllowed(futurePosition))
            {
                return false;
            }
        }

        transform.position += offset;

        setGrid();

        return true;
    }

    bool tryToRotate(Vector3 rotation)
    {
        // First apply the rotation
        resetGrid();
        transform.Rotate(rotation);

        // If not all blocks are inside the grid, redo-rotation
        if (!tryToRotateAux(rotation))
        {
            transform.Rotate(-rotation);
            return false;
        }

        setGrid();

        return true;
    }

    bool tryToRotateAux(Vector3 rotaton)
    {
        foreach (Transform child in transform)
        {
            if (!gridRef.isPositionAllowed(child.transform.position))
            {
                return false;
            }
        }

        return true;
    }

    public void setGrid()
    {
        foreach (Transform child in transform)
        {
            gridRef.setPosition(child.gameObject);
        }
    }

    void resetGrid()
    {
        foreach (Transform child in transform)
        {
            gridRef.resetPosition(child.gameObject);
        }
    }

    public void FreezePiece()
    {
        setGrid();
        gridRef.checkLines();
    }

    public void checkEndGamePiece()
    {
        foreach (Transform child in transform)
        {
            if(gridRef.checkEndGame(child.transform.position))
            {
                Destroy(gameObject);
            }
        }
    }


}
