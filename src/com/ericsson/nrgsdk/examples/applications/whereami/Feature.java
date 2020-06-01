/*
 * **************************************************************************
 * *                                                                        *
 * * Ericsson hereby grants to the user a royalty-free, irrevocable,        *
 * * worldwide, nonexclusive, paid-up license to copy, display, perform,    *
 * * prepare and have prepared derivative works based upon the source code  *
 * * in this sample application, and distribute the sample source code and  *
 * * derivative works thereof and to grant others the foregoing rights.     *
 * *                                                                        *
 * * ERICSSON DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,        *
 * * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS.       *
 * * IN NO EVENT SHALL ERICSSON BE LIABLE FOR ANY SPECIAL, INDIRECT OR      *
 * * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS    *
 * * OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE  *
 * * OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE *
 * * OR PERFORMANCE OF THIS SOFTWARE.                                       *
 * *                                                                        *
 * **************************************************************************
 */

package com.ericsson.nrgsdk.examples.applications.whereami;

import com.ericsson.hosasdk.api.HOSAMonitor;
import com.ericsson.hosasdk.api.fw.P_UNKNOWN_SERVICE_TYPE;
import com.ericsson.hosasdk.api.hui.IpHosaUIManager;
import com.ericsson.hosasdk.api.mm.ul.IpUserLocation;
import com.ericsson.hosasdk.utility.framework.FWproxy;
import com.ericsson.nrgsdk.examples.tools.SDKToolkit;
import com.ericsson.nrgsdk.examples.applications.whereami.models.*;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This class implements the logic of the application. It uses processors to
 * interact with Ericsson Network Resource Gateway.
 */
public class Feature {

    private FWproxy itsFramework;

    private IpHosaUIManager itsHosaUIManager;

    private IpUserLocation itsOsaULManager;

    private SMSProcessor itsSMSProcessor;

    private MMSProcessor itsMMSProcessor;

    private LocationProcessor itsLocationProcessor;

    final private GUI theGUI;

    private Integer assignmentId;

    final private Service service;

    private static String locationCheck = "";


    /**
     * Initializes a new instance, without starting interaction with Ericsson
     * Network Resource Gateway (see start)
     *
     * @param aGUI the GUI of the application
     */
    public Feature(GUI aGUI, Service service) {
        theGUI = aGUI;
        this.service = service;
        aGUI.setTitle("Worker control application");
        aGUI.addTab("Description", getDescription());


    }

    /**
     * Starts interaction with the Ericsson Network Resource Gateway. Note: this
     * method is intended to be called at most once.
     */
    protected void start() {
        HOSAMonitor.addListener(SDKToolkit.LOGGER);
        itsFramework = new FWproxy(Configuration.INSTANCE);
        try {
            itsHosaUIManager = (IpHosaUIManager) itsFramework
                    .obtainSCF("SP_HOSA_USER_INTERACTION");
            itsOsaULManager = (IpUserLocation) itsFramework
                    .obtainSCF("P_USER_LOCATION");
        } catch (P_UNKNOWN_SERVICE_TYPE anException) {
            System.err.println("Service not found. Please refer to the Ericsson Network Resource Gateway User Guide for "
                    + "a list of which applications that are able to run on which test tools\n"
                    + anException);
        }
        itsSMSProcessor = new SMSProcessor(itsHosaUIManager, this);
        itsMMSProcessor = new MMSProcessor(itsHosaUIManager, this);
        itsLocationProcessor = new LocationProcessor(itsOsaULManager, this);
        System.out.println("Starting SMS notification");
        assignmentId = new Integer(itsSMSProcessor.startNotifications(Configuration.INSTANCE.getProperty("serviceNumber")));
    }

    /**
     * Stops interaction with the Ericsson Network Resource Gateway and disposes
     * of all resources allocated by this instance. Note: this method is
     * intended to be called at most once.
     */
    public void stop() {
        System.out.println("Stopping SMS notification");
        if (assignmentId != null) {
            itsSMSProcessor.stopNotifications(assignmentId.intValue());
        }
        assignmentId = null;
        System.out.println("Disposing processor");
        if (itsSMSProcessor != null) {
            itsSMSProcessor.dispose();
        }
        if (itsMMSProcessor != null) {
            itsMMSProcessor.dispose();
        }
        if (itsLocationProcessor != null) {
            itsLocationProcessor.dispose();
        }
        System.out.println("Disposing service manager");
        if (itsHosaUIManager != null) {
            itsFramework.releaseSCF(itsHosaUIManager);
        }
        if (itsOsaULManager != null) {
            itsFramework.releaseSCF(itsOsaULManager);
        }
        System.out.println("Disposing framework");
        if (itsFramework != null) {
            itsFramework.endAccess();
            itsFramework.dispose();
        }
        System.out.println("Stopping Parlay tracing");
        HOSAMonitor.removeListener(SDKToolkit.LOGGER);
        System.exit(0);
    }

    /**
     * Invoked by the SMSProcessor, when a notification is received.
     *
     * @throws Exception
     */
    protected void smsReceived(String aSender, String aReceiver,
                               String aMessageContent) {
        System.out.println("Odebrano SMS-a o tresci: " + aMessageContent);

        // Driver registration
        if (aMessageContent.toLowerCase().matches("register-driver:(.*)")) {
            if (service.getDriver(aSender).isPresent()) {
                Driver driver = service.getDriver(aSender).get();
                itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Nie musisz sie rejestrowac, jestes juz czlonkiem serwisu");
                System.out.println("Driver already registered: " + driver.number);
                return;
            }
            Driver driver = new Driver(aSender, getName(aMessageContent));
            service.drivers.add(driver);
            System.out.println("Dodano drivera o numerze: " + driver.number);
            itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Jestes nowym driverem serwisu");
        }

        // Client registration
        if (aMessageContent.toLowerCase().matches("register-client:(.*)")) {
            if (service.getClient(aSender).isPresent()) {
                Client client = service.getClient(aSender).get();
                itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Nie musisz sie rejestrowac, jestes juz czlonkiem serwisu");
                System.out.println("Client already registered: " + client.number);
                return;
            }
            Client client = new Client(aSender, getName(aMessageContent));
            service.clients.add(client);
            System.out.println("Dodano clienta o numerze: " + client.number);
            itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Jestes nowym clientem serwisu");
        }

        if (aMessageContent.toLowerCase().equals("request-driver")) { //sprawdzamy pracownika
            final Client client = service.getClient(aSender).get();
            final Ride ride = new Ride(client);
            itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Zgłoszenie zostało przyjęte");
            itsLocationProcessor.requestLocation(aSender, new BiConsumer<String, Location>() {
                @Override
                public void accept(String s, Location location) {
                    sendLocalizationMMS(ride.client.number, "Zgłoszenie zostało przyjęte 👍", location);
                    service.drivers.forEach(new Consumer<Driver>() {
                        @Override
                        public void accept(final Driver driver) {
                            sendLocalizationMMS(
                                    driver.number,
                                    "Pojawiło się nowe zgłoszenie o numberze: " + ride.number + " odpowiedz na smsa o tresci 'biere:" + ride.number + "' aby przyjąć zgłoszenie",
                                    location);
                        }
                    });
                }

            });
        }

        if (aMessageContent.toLowerCase().equals("biere:(.*)")) { //sprawdzamy pracownika
            String rideNumber = aMessageContent.split(":")[1];
            Optional<Ride> opRide = service.getRide(Integer.parseInt(rideNumber));
            if (!opRide.isPresent()) {
                itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Nie ma takiego zgłoszenia 🤷‍");
                return;
            }
            Ride ride = opRide.get();
            if (ride.active) {
                itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Zgłoszenie zostało już przyjęte 🤷‍");
                return;
            }

            Driver driver = service.getDriver(aSender).get();
            ride.driver = driver;
            ride.active = true;

            itsLocationProcessor.requestLocation(ride.driver.number, new BiConsumer<String, Location>() {
                @Override
                public void accept(String s, Location location) {
                    sendLocalizationMMS(ride.client.number, "Zgłoszenie zostało przyjęte 👍", location);
                }
            });
            itsLocationProcessor.requestLocation(ride.client.number, new BiConsumer<String, Location>() {
                @Override
                public void accept(String s, Location location) {
                    sendLocalizationMMS(ride.driver.number, "Zgłoszenie zostało przyjęte 👍", location);
                }
            });
        }

        if (aMessageContent.toLowerCase().equals("stop")) { //sprawdzamy pracownika
            Optional<Ride> opRide = service.getActiveRideForClientOrDriver(aSender);
            if (!opRide.isPresent()) {
                itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Nie masz aktywnej jazdy 🤷‍");
                return;
            }
            Ride ride = opRide.get();
            if (ride.active) {
                itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), ride.driver.number, "Zgłoszenie zostało anulowane 👍");
                itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), ride.client.number, "Zgłoszenie zostało anulowane 👍");
                ride.active = false;
                return;
            }
            itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Jazda została już anulowana 🤷‍");
        }
    }

    private String getName(String aMessageContent) {
        return aMessageContent.split(":")[1];
    }

    public void sendLocalizationMMS(String to, String message, Location location) {
        float latitude = location.latitude;
        float longitude = location.longitude;
        try {

            //Map
            ImageIcon map = Configuration.INSTANCE.getImage("map.gif");
            int wm = map.getIconWidth();
            int hm = map.getIconHeight();

            //Phone
            ImageIcon phone = Configuration.INSTANCE.getImage("phone.png");
            int wp = phone.getIconWidth();
            int hp = phone.getIconHeight();

            if (latitude < 0) {
                latitude = 0;
            }
            if (latitude > 1) {
                latitude = 1;
            }
            if (longitude < 0) {
                longitude = 0;
            }
            if (longitude > 1) {
                longitude = 1;
            }


            int x = (int) (latitude * wm - wp / 2);
            int y = (int) (longitude * hm - hp / 2);
            Plotter plotter = new Plotter(wm, hm);
            plotter.drawImage(map.getImage(), 0, 0, theGUI);
            plotter.drawImage(phone.getImage(), x, y, theGUI);
            MMSMessageContent messageContent = new MMSMessageContent();
            messageContent.addMedia(plotter.createDataSource());
            messageContent.addTextMedia(message);
            itsMMSProcessor.sendMMS(Configuration.INSTANCE.getProperty("serviceNumber"), to, messageContent
                    .getBinaryContent(), "Current location");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return a descriptive text that explains the application and its
     * configuration.
     */
    private String getDescription() {
        String s = "Nacisnij START, aby sie polaczyc z symulatorem";
        s += "\n";
        s += "Pracownik moze wysylac SMS na numer " + Configuration.INSTANCE.getProperty("serviceNumber") + " z nastepujacymi poleceniami ";
        s += "\n-------------------------------------------\n";
        s += "\"registrer-driver:NUMBER\" pozwala uzytkownikowi na rejestracje w systemie jako driver\n";
        s += "\"registrer-client:NUMBER\" pozwala uzytkownikowi na rejestracje w systemie jako client\n";
        s += "\"request-driver\" tworzy zlecenie\n";
        s += "\"biere:RIDE_NUMBER\" rezerwuje sesje \n";
        s += "\"stop\" zatrzymuje wycieczkę \n";
        s += "\n-------------------------------------------\n";
        s += "Nacisnij STOP, aby zatrzymac aplikacje.\n";
        return s;
    }


}
