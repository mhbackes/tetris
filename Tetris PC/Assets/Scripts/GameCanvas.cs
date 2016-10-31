using UnityEngine;
using UnityEngine.SceneManagement;
using System.Collections;

public class GameCanvas : MonoBehaviour {

    public void MainMenu()
    {
        SceneManager.LoadScene("Menu");
    }
}
