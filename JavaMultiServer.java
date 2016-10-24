import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

class JavaMultiServer {
	public static void main(String[] args) throws IOException {

		final int portNumber = 8000;
		final boolean listening = true;

		System.out.println("Port: " + portNumber);

		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

			// create gpio controller
			final GpioController gpio = GpioFactory.getInstance();

			final GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[3];
			// provision gpio pin #0 to #6 as an output pin and turn on

			pins[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
			pins[2] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "MyLED", PinState.HIGH);

			while (listening) {
				new ServerThread(serverSocket.accept(), pins).start();
			}
		} catch (final IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
	}
}

class ServerThread extends Thread {
	private Socket socket = null;
	private final GpioPinDigitalOutput[] pins;

	public ServerThread(Socket socket, GpioPinDigitalOutput[] pins) {
		super("ServerThread");
		this.socket = socket;
		this.pins = pins;
	}

	@Override
	public void run() {

		System.out.println("New client");

		try (PrintWriter printWriter = new PrintWriter(this.socket.getOutputStream(), true);
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(this.socket.getInputStream()));) {
			String inputLine;
			final String outputLine;

			while ((inputLine = bufferedReader.readLine()) != null) {
				System.out.println(inputLine);
				
				/*if(inputLine.equals("DISCONNECT")) {
					break;
				}*/

				final String[] splited = inputLine.split("\\s+");

				if ((splited.length == 2) && splited[0].equals("GPIO") && isInteger(splited[1])) {
					final int GPIONumber = Integer.parseInt(splited[1]);
					lightLED(GPIONumber, this.pins[GPIONumber]);
				}

				printWriter.println(inputLine); // echo back to sender
			}
			this.socket.close();
			System.out.println("Client disconnected");
			
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void lightLED(int GPIONumber, GpioPinDigitalOutput pin) {
		System.out.println("Light LED : " + GPIONumber);

		pin.toggle();

	}

	public static boolean isInteger(String str) {
		try {
			final int d = Integer.parseInt(str);
		} catch (final NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
