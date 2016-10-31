using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using System.Collections;

public class MenuCanvas : MonoBehaviour {

    public Text inputText;

    // Update is called once per frame
    void Update()
    {

        inputText.text = InputManager.getAction().ToString();

    }

    public void Jogar()
    {
        SceneManager.LoadScene("Tetris");
    }
}
