using UnityEngine;
using System.Collections;

[RequireComponent(typeof(Action))]
public class Piece : MonoBehaviour {

    public bool canSpin;
    public float naturalFallSpeed;
    public float directFallSpeed;

    private Action actionRef;
    private PieceSpawner pieceSpawnerRef;
    public float naturalFallSpeed_begin;
    public bool isDropped;

    public bool isActive = false;

    public float currentFallSpeedCycle;

    void Awake()
    {
        actionRef = gameObject.GetComponent<Action>();
        pieceSpawnerRef = GameObject.Find("PieceSpawner").GetComponent<PieceSpawner>();
    }

    void Start()
    {
        naturalFallSpeed_begin = naturalFallSpeed;
        currentFallSpeedCycle = naturalFallSpeed_begin;
        isDropped = false;

        actionRef.checkEndGamePiece();
    }

    public void activate()
    {
        isActive = true;
    }
	
	void Update () {

        if (!isActive)
        {
            return;
        }

        if (InputManager.Fall())
        {
            doFall();
        }
        else if(InputManager.StopFall())
        {
            stopFall();
        }

        naturalFallSpeed -= Time.deltaTime;

        if(naturalFallSpeed <= 0)
        {
            if(!actionRef.Move(Action.MOVE.Down))
            {
                // Piece can't naturally move down
                actionRef.FreezePiece();
                pieceSpawnerRef.spawn();
                enabled = false;
            }
            naturalFallSpeed = currentFallSpeedCycle;
        }

        if(InputManager.M_Horizontal_L())
        {
            actionRef.Move(Action.MOVE.Left);
        }
        else if(InputManager.M_Horizontal_R())
        {
            actionRef.Move(Action.MOVE.Right);
        }

        if(canSpin)
        {
            if (InputManager.R_Horizontal_L())
            {
                actionRef.Rotate(Action.ROTATE.Left);
            }
            else if (InputManager.R_Horizontal_R())
            {
                actionRef.Rotate(Action.ROTATE.Right);
            }
        }

    }

    public void doFall()
    {
        naturalFallSpeed = 0f;
        currentFallSpeedCycle = directFallSpeed;

        isDropped = true;
    }

    public void stopFall()
    {
        naturalFallSpeed = 0f;
        currentFallSpeedCycle = naturalFallSpeed_begin;

        isDropped = false;
    }
}
