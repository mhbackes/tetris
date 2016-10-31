using UnityEngine;
using UnityEngine.UI;
using System;
using System.Collections;
using System.IO.Ports;
using System.Threading;

public class Bluetooth : MonoBehaviour
{
    private SerialPort serialPort = null;
    int baudRate = 115200;
    int readTimeOut = 100;
    int bufferSize = 32;                // Device sends 32 bytes per packet

    bool programActive = true;
    Thread thread;

    /// <summary>
    /// Setup the virtual port connection for the BT device at program start.
    /// </summary>
    /// 

    public bool StartBluetooth(string portName)
    {
        try
        {
            serialPort = new SerialPort();
            serialPort.PortName = portName;
            serialPort.BaudRate = baudRate;
            serialPort.ReadTimeout = readTimeOut;
            serialPort.Open();
        }
        catch (Exception e)
        {
            Debug.Log(e.Message);

            return false;
        }

        // Execute a thread to manage the incoming BT data
        programActive = true;
        thread = new Thread(new ThreadStart(ProcessData));
        thread.Start();

        return true;
    }

    public void StopBluetooth()
    {
        OnDisable();
    }

    /// <summary>
    /// Processes the incoming BT data on the virtual serial port.
    /// </summary>

    void ProcessData()
    {
        Byte[] buffer = new Byte[bufferSize];
        int bytesRead = 0;
        Debug.Log("Bluetooth started");

        while (programActive)
        {
            try
            {
                // Attempt to read data from the BT device
                // - will throw an exception if no data is received within the timeout period
                bytesRead = serialPort.Read(buffer, 0, bufferSize);

                // Use the appropriate SerialPort read method for your BT device e.g. ReadLine(..) for newline terminated packets 

                if (bytesRead > 0)
                {
                    // Do something with the data in the buffer
                    string text;
                    text = System.Text.Encoding.UTF8.GetString(buffer);
                    //Debug.Log("From Bluetooth: "+text);
                    InputManager.Parse(text);
                }
            }
            catch (TimeoutException)
            {
                // Do nothing, the loop will be reset
            }
        }

        Debug.Log("Thread stopped");
    }

    /// <summary>
    /// Update this instance.
    /// </summary>


    void Update()
    {
    }

    /// <summary>
    /// On program exit.
    /// </summary>


    public void OnDisable()
    {
        programActive = false;

        if (serialPort != null && serialPort.IsOpen)
            serialPort.Close();
    }

    void OnApplicationQuit()
    {
        OnDisable();
    }
}