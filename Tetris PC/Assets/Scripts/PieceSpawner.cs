using UnityEngine;
using System.Collections;

public class PieceSpawner : MonoBehaviour {

    public GameObject[] PiecesPrefabs;

    private int numPrefabs;

	// Use this for initialization
	void Start () {

        numPrefabs = PiecesPrefabs.Length;
        spawn();

    }

    public void spawn()
    {
        Instantiate(PiecesPrefabs[Random.Range(0, numPrefabs - 1)], transform.position, Quaternion.identity);
    }
}
