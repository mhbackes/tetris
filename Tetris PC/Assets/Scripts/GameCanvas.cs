using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;
using System.Collections;

public class GameCanvas : MonoBehaviour {

    public Text textScore;

    public void setText(string text)
    {
        textScore.text = text;
    }
}
