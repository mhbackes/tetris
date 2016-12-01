using UnityEngine;
using System.Collections;
using System;

public class PieceSpawner : MonoBehaviour {

    public GameObject[] PiecesPrefabs;

    private int numPrefabs;

    public int nextSpawn;

    public int maxNumPieces = 50;

    public int numSpawned;

    public Transform previewTransform;

    private GameObject futurePiece;

    public Grid grid;

	// Use this for initialization
	void Start () {

        nextSpawn = 0;

        numSpawned = 0;

        numPrefabs = PiecesPrefabs.Length;

        ShuffleArray<GameObject>(PiecesPrefabs);

        spawn();

    }

    public void spawn()
    {
        if (futurePiece)
        {
            Destroy(futurePiece);
        }

        GameObject piece = (GameObject)Instantiate(PiecesPrefabs[nextSpawn], transform.position, Quaternion.identity);

        Piece pieceScript = piece.GetComponent<Piece>();

        pieceScript.activate();

        nextSpawn++;

        if (nextSpawn >= PiecesPrefabs.Length)
        {
            ShuffleArray<GameObject>(PiecesPrefabs);
            nextSpawn = 0;
        }

        int futurePieceIndex = nextSpawn;

        if (futurePieceIndex >= PiecesPrefabs.Length)
        {
            futurePieceIndex = 0;
        }

        futurePiece = (GameObject)Instantiate(PiecesPrefabs[futurePieceIndex], previewTransform.position, Quaternion.identity);

        numSpawned++;

        if (numSpawned > maxNumPieces)
        {
            grid.endGame();
            Destroy(piece);
            Destroy(futurePiece);
        }

    }

    public static void ShuffleArray<T>(T[] arr)
    {
        for (int i = arr.Length - 1; i > 0; i--)
        {
            int r = UnityEngine.Random.Range(0, i);
            T tmp = arr[i];
            arr[i] = arr[r];
            arr[r] = tmp;
        }
    }
}
