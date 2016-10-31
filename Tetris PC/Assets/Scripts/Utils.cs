using UnityEngine;
using System.Collections;

public static class Utils {

	public static void floatVector2IntVectorRound(Vector2 input, out Vector2 output)
    {
        output.x = Mathf.Round(input.x);
        output.y = Mathf.Round(input.y);
    }
}
