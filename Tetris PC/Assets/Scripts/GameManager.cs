using UnityEngine;
using System.Collections;
using System.IO;
using System;

public class GameManager : MonoBehaviour {

    public PieceSpawner pieceSpawner;
    public Piece piece;
    public Action action;

    private IEnumerator coroutine;

    public enum SCORE_TYPE
    {
        fill,
        timePenalty
    };

    private int enumSize;

    public int[] scoreValues;

    public int score;
    public int time;

    public GameCanvas gameCanvas;
    public EndGameCanvas endGameCanvas;

	public void disableSpawner()
    {
        pieceSpawner.enabled = false;
    }

    public void disablePiece()
    {
        piece.enabled = false;
        action.enabled = false;
    }

    void Start()
    {

        InputManager.Reset();

        endGameCanvas.gameObject.SetActive(false);
        gameCanvas.gameObject.SetActive(true);

        time = 0;

        enumSize = System.Enum.GetValues(typeof(SCORE_TYPE)).Length;

        setScore(0);

        if(scoreValues.Length != enumSize)
        {
            Debug.Log("WARNING: provide score values for each score type");
        }

        coroutine = WaitAndDo(1f);
        StartCoroutine(coroutine);
    }

    void Update()
    {
        if (Input.GetKey(KeyCode.Escape))
        {
            endGameCanvas.backToMenu();
        }
    }

    private IEnumerator WaitAndDo(float waitTime) {
        while (true) {
            yield return new WaitForSeconds(waitTime);
            addScore(SCORE_TYPE.timePenalty);
            time++;
        }
    }

    public void addScore(SCORE_TYPE scoreType)
    {
        setScore(score + scoreValues[(int) scoreType]);
    }

    public void setScore(int newScore)
    {
        score = newScore;

        gameCanvas.setText(score.ToString());
    }

    public int getScore()
    {
        return score;
    }

    public void stopCouting()
    {
        StopCoroutine(coroutine);
    }

    public void informEndGame()
    {
        Debug.Log("Game has ended");

        writeToFile();

        gameCanvas.gameObject.SetActive(false);
        endGameCanvas.gameObject.SetActive(true);

        endGameCanvas.setScore(getScore());
        endGameCanvas.setTime(time);
    }

    private void writeToFile()
    {
        File.AppendAllText("output.txt", DateTime.Now.ToString() + " - Score: " + getScore() + " " + "Time played: " + time + Environment.NewLine);

    }


}
