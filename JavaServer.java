import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class JavaServer {

	public static void main(String[] args) {
		System.out.println("Server start");
		System.out.println("Runtime Java: " + System.getProperty("java.runtime.version"));

		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		BufferedReader bufferedReader = null;
		PrintWriter printWriter = null;

		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();

		final GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[3];
		// provision gpio pin #0 to #6 as an output pin and turn on

		pins[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
		pins[2] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "MyLED", PinState.HIGH);

		try {

			//System.out.println(InetAddress.getLocalHost());
			serverSocket = new ServerSocket(8000);
			System.out.println("Server port: " + serverSocket.getLocalPort());

			clientSocket = serverSocket.accept();

			// Client connected
			final InputStream inputStream = clientSocket.getInputStream();
			final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);

			final OutputStream outputStream = clientSocket.getOutputStream();
			printWriter = new PrintWriter(outputStream, true);

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);

				final String[] splited = line.split("\\s+");

				if ((splited.length == 2) && splited[0].equals("GPIO") && isInteger(splited[1])) {
					final int GPIONumber = Integer.parseInt(splited[1]);
					lightLED(GPIONumber, pins[GPIONumber]);
				}

				printWriter.println(line); // echo back to sender
			}
			;

		} catch (final IOException ex) {
			System.err.println(ex.toString());
		} finally {

			if (printWriter != null) {
				printWriter.close();
				System.out.println("printWriter closed");
			}

			if (bufferedReader != null) {
				try {
					bufferedReader.close();
					System.out.println("bufferedReader closed");
				} catch (final IOException ex) {
					System.out.println(ex.toString());
				}
			}

			if (clientSocket != null) {
				try {
					clientSocket.close();
					System.out.println("clientSocket closed");
				} catch (final IOException ex) {
					System.out.println(ex.toString());
				}
			}

			if (serverSocket != null) {
				try {
					serverSocket.close();
					System.out.println("serverSocket closed");
				} catch (final IOException ex) {
					System.out.println(ex.toString());
				}
			}

		}

	}

	public static boolean isInteger(String str) {
		try {
			final int d = Integer.parseInt(str);
		} catch (final NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static void lightLED(int GPIONumber, GpioPinDigitalOutput pin) {
		System.out.println("Light LED : " + GPIONumber);

		pin.toggle();

	}

}
