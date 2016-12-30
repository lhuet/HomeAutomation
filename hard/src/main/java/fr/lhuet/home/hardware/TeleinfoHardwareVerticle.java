package fr.lhuet.home.hardware;

import gnu.io.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by lhuet on 30/12/15.
 */
public class TeleinfoHardwareVerticle extends AbstractVerticle{

    private static Logger logger = LoggerFactory.getLogger(TeleinfoHardwareVerticle.class);

    @Override
    public void start() {

        String serialPort =  this.config().getString("teleinfo.serialPort");//"/dev/ttyO1"; //"/dev/ttyUSB0";

        try {
            startListeningTeleinfo(serialPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startListeningTeleinfo(String serialPort) throws PortInUseException, UnsupportedCommOperationException, NoSuchPortException, IOException {

        // Workaround to force serial port detection on Linux
        System.setProperty("gnu.io.rxtx.SerialPorts", serialPort);

        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(serialPort);
        if( portId.isCurrentlyOwned() ) {
            logger.error("Error: Port is currently in use");
        } else {
            logger.info("Opening port " + portId.getName());
        }

        // Port settings -> 1200 7E1
        SerialPort port = (SerialPort) portId.open(TeleinfoHardwareVerticle.class.getName(), 2000);
        port.setSerialPortParams(1200, SerialPort.DATABITS_7, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);

        SerialReader sr = new SerialReader(port.getInputStream(), vertx.eventBus());
        Thread serialThread = new Thread(sr);
        serialThread.start();

    }


    public static class SerialReader implements Runnable {

        private BufferedReader br;
        private EventBus eb;

        public SerialReader(InputStream in, EventBus eb) {
            this.br = new BufferedReader(new InputStreamReader(in));
            this.eb = eb;
        }

        public void run() {

            ArrayList<String> trame = null;
            try {
                while (true) {
                    String line = br.readLine();
                    // A new teleinfo trame begin with bytes '3' and '2'
                    if ((line.length()>1) && (line.codePointAt(0)==3 && line.codePointAt(1)==2)) {
                        // Trame complete (null on starting)
                        if (trame!=null) {
                            // Send the trame (JSON) on the Vertx Event Bus
                            eb.send("teleinfo.trame", decodeTrame(trame));
                        }
                        // New trame starting
                        trame = new ArrayList<>();
                    } else {
                        if (trame != null) {
                            trame.add(line);
                        }
                    }
                }
            } catch(IOException e ) {
                e.printStackTrace();
            }

        }

        private JsonObject decodeTrame(ArrayList<String> trame) {

            JsonObject result = new JsonObject();

            for (int i = 0; i < trame.size(); i++) {
                String line = trame.get(i);
                int sum = 0;
                for (int j=0; j < line.length()-2; j++) {
                    sum += line.codePointAt(j);
                }
                sum = (sum & 63) + 32;
                if (sum == line.codePointAt(line.length()-1)) {
                    String[] items = line.split(" ");
                    switch (items[0]) {
                        case "BASE": // Index Tarif bleu
                        case "HCHC": // Index Heures creuses
                        case "HCHP": // Index Heures pleines
                        case "EJPH": // Index EJP (HN et HPM)
                        case "BBRH": // Index Tempo (HC/HP en jours Blanc, Bleu et Rouge)
                        case "ISOU": // Intensité souscrite
                        case "IINST": // Intensité instantannée (1/2/3 pour triphasé)
                        case "ADPS": // Avertissement de dépassement
                        case "IMAX": // Intensité max appelée (1/2/3 pour triphasé)
                        case "PAPP": // Puissance apparente
                        case "PMAX": // Puissance max triphasée atteinte
                            result.put(items[0], Integer.valueOf(items[1]));
                            break;
                        default:
                            result.put(items[0], items[1]);
                    }
                }
            }
            return result;
        }
    }

}
