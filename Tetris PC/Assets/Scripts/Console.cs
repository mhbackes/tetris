using UnityEngine;
using UnityEngine.UI;
using System.Collections;

[RequireComponent(typeof(Bluetooth))]
public class Console : MonoBehaviour {

    public Text portText;
    public Text buttonText;
    public Text notificationText;

    private Bluetooth bluetoothRef;

    private enum STATUS
    {
        notRunning,
        Running
    };

    private STATUS status;

	// Use this for initialization
	void Start () {

        bluetoothRef = gameObject.GetComponent<Bluetooth>();

        status = STATUS.notRunning;
    }

    public void StartButton()
    {
        switch(status)
        {
            case STATUS.notRunning:
                if (portText.text != "")
                {
                    if (bluetoothRef.StartBluetooth(portText.text))
                    {
                        status = STATUS.Running;
                        StartCoroutine(ShowMessage("Ingoing Port is running", 2));
                        buttonText.text = "STOP";
                    }
                    else
                    {
                        StartCoroutine(ShowMessage("Invalid COM Port", 2));
                    }

                }
                else
                {
                    StartCoroutine(ShowMessage("Please insert a COM Port", 2));
                }

                break;

            case STATUS.Running:

                bluetoothRef.StopBluetooth();
                StartCoroutine(ShowMessage("Ingoing Port closed", 2));
                status = STATUS.notRunning;
                buttonText.text = "START";

                break;

            default: break;
        }
    }

    IEnumerator ShowMessage(string message, float delay)
    {
        notificationText.text = message;
        notificationText.enabled = true;
        yield return new WaitForSeconds(delay);
        notificationText.enabled = false;
    }
}
