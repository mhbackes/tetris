using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using System.Collections;

public class EndGameCanvas : MonoBehaviour {

    public Text ValueScore;
    public Text TimeScore;

    public void backToMenu()
    {
        SceneManager.LoadScene("Menu");
    }

    public void setScore(int score)
    {
        ValueScore.text = score.ToString() + " points";
    }

    public void setTime(int seconds)
    {
        TimeScore.text = seconds.ToString() + " seconds";
    }

}
