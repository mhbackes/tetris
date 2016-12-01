using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class Grid : MonoBehaviour {

    public int height;
    public int width;

    public Text textUI;

    public GameObject wallPrefab;

    public GameObject[,] grid;

    private GameCanvas gameCanvas;
    private GameManager gameManager;

    private bool callOnce = false;

    private bool playingAnimation = false;

    void Start()
    {
        grid = new GameObject[width, height];
        gameCanvas = GameObject.Find("GameCanvas").GetComponent<GameCanvas>();
        gameManager = GameObject.Find("GameManager").GetComponent<GameManager>();
    }

    public bool isPlayingAnimation()
    {
        return playingAnimation;
    }

    bool getGridValue(Vector2 position)
    {
        if (isInsideGrid(position))
        {
            return grid[(int)position.x, (int)position.y];
        }

        return false;
    }

    void setGridValue(Vector2 position, GameObject value)
    {
        Utils.floatVector2IntVectorRound(position, out position);

        if (isInsideGrid(position))
        {
            grid[(int)position.x, (int) position.y] = value;
        }

        printGrid();
    }

    bool isOccupied(Vector2 position)
    {
        if(grid[(int)position.x, (int)position.y] == null)
        {
            return false;
        }

        return true;
    }

    private bool isInsideGrid(Vector2 position)
    {
        Utils.floatVector2IntVectorRound(position, out position);

        return (position.x >= 0 && position.x < width && position.y >= 0 && position.y < height);
    }

    public bool isPositionAllowed(Vector2 position)
    {
        return isInsideGridNoHeight(position) && !isOverlapping(position);
    }

    private bool isInsideGridNoHeight(Vector2 position)
    {
        Utils.floatVector2IntVectorRound(position, out position);

        return (position.x >= 0 && position.x < width && position.y >= 0);
    }

    private bool isOverlapping(Vector2 position)
    {
        Utils.floatVector2IntVectorRound(position, out position);

        return getGridValue(position);
    }

    public void setPosition(GameObject block)
    {
        setGridValue(block.transform.position, block);
    }

    public void resetPosition(GameObject block)
    {
        setGridValue(block.transform.position, null);
    }

    public void checkLines()
    {
        bool lineFull = true;
        bool reCheck = true;

        do
        {
            reCheck = false;

            for (int i = 0; i < height; i++)
            {
                lineFull = true;

                for (int j = 0; j < width; j++)
                {
                    if (grid[j, i] == null)
                    {
                        lineFull = false;
                    }
                }

                if (lineFull)
                {
                    eraseLine(i);
                    moveGridDown(i);
                    gameManager.addScore(GameManager.SCORE_TYPE.fill);
                    reCheck = true;
                }
            }
        } while (reCheck);
    }

    void eraseLine(int height)
    {
        for (int i = 0; i < width; i++)
        {
            Destroy(grid[i, height]);
            grid[i, height] = null;
        }
    }

    void moveGridDown(int level)
    {
        for (int i = level; i < height-1; i++)
        {
            for (int j = 0; j < width; j++)
            {
                if(grid[j, i + 1])
                {
                    grid[j, i + 1].transform.position -= new Vector3(0f, 1f, 0f);
                    grid[j, i] = grid[j, i + 1];
                    grid[j, i + 1] = null;
                }
            }
        }
    }

    public bool checkEndGame(Vector2 position)
    {
        Utils.floatVector2IntVectorRound(position, out position);

        if (getGridValue(position))
        {
            StartCoroutine(endGameAnimation());

            return true;
        }

        return false;
    }

    public void endGame()
    {
        StartCoroutine(endGameAnimation());
    }

    IEnumerator endGameAnimation()
    {
        if(!callOnce)
        {
            gameManager.stopCouting();

            playingAnimation = true;

            callOnce = true;

            Debug.Log("endGame animation called");

            for (int i = 0; i < height; i++)
            {
                for (int j = 0; j < width; j++)
                {
                    grid[j, i] = (GameObject)Instantiate(wallPrefab, new Vector3(j, i, 0f), Quaternion.identity);
                    yield return new WaitForSeconds(0.0025f * Time.timeScale);
                }
            }

            yield return new WaitForEndOfFrame();

            gameManager.informEndGame();
        }
    }

    void printGrid()
    {
        textUI.text = "";

        for (int i = height-1; i >= 0; i--)
        {
            for(int j = 0; j < width; j++)
            {
                if(grid[j, i] != null)
                {
                    textUI.text += "1 ";
                }
                else
                {
                    textUI.text += "0 ";
                }
            }

            textUI.text += "\n";
        }
    }

}
