package com.pedropadilha;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public final class SerialComm {

    OutputStream out;
    InputStream in;

    public SerialComm(String portName) throws IOException {
        SerialPort port = getSerialPort(portName);
        this.out = port.getOutputStream();
        this.in = port.getInputStream();
    }

    public SerialPort getSerialPort(String portName) {
        CommPort commPort = null;
        SerialPort serialPort = null;
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (portIdentifier.isCurrentlyOwned()) {
                System.err.println("Port is currently in use!");
            } else {
                commPort = portIdentifier.open(this.getClass().getName(), 2000);

                if (commPort instanceof SerialPort) {
                    serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unnable to open communication with Serial port!");
        }
        return serialPort;
    }

    public void write(String command) {
        try {
            out.write(command.getBytes());
            out.flush();
        } catch (IOException iOException) {
            iOException.printStackTrace();;
        }
    }

    public String read() {
        String answer = "";
        try {
            Thread.sleep(100);
            if (in.available() > 0) {
                while (in.available() > 0) {
                    answer += (char) in.read();
                }
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        return answer;
    }

    public InputStream getInputStream() {
        return this.in;
    }

    public OutputStream getOutputStream() {
        return this.out;
    }
}