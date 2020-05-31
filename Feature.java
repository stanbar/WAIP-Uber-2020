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

import java.io.Console;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

import javax.swing.ImageIcon;

import com.ericsson.hosasdk.api.HOSAMonitor;
import com.ericsson.hosasdk.api.TpAddress;
import com.ericsson.hosasdk.api.TpHosaSendMessageError;
import com.ericsson.hosasdk.api.TpHosaSendMessageReport;
import com.ericsson.hosasdk.api.TpHosaUIMessageDeliveryStatus;
import com.ericsson.hosasdk.api.fw.P_UNKNOWN_SERVICE_TYPE;
import com.ericsson.hosasdk.api.hui.IpAppHosaUIManager;
import com.ericsson.hosasdk.api.hui.IpHosaUIManager;
import com.ericsson.hosasdk.api.mm.ul.IpUserLocation;
import com.ericsson.hosasdk.api.ui.IpAppUI;
import com.ericsson.hosasdk.api.ui.TpUIEventInfo;
import com.ericsson.hosasdk.api.ui.TpUIEventNotificationInfo;
import com.ericsson.hosasdk.api.ui.TpUIIdentifier;
import com.ericsson.hosasdk.utility.framework.FWproxy;
import com.ericsson.nrgsdk.examples.tools.SDKToolkit;
/**
 * This class implements the logic of the application. It uses processors to
 * interact with Ericsson Network Resource Gateway.
 */
public class Feature{

	private FWproxy itsFramework;

	private IpHosaUIManager itsHosaUIManager;

	private IpUserLocation itsOsaULManager;

	private SMSProcessor itsSMSProcessor;

	private MMSProcessor itsMMSProcessor;

	private LocationProcessor itsLocationProcessor;

	private GUI theGUI;

	private Integer assignmentId;

	private Service service;
	private ArrayList<Worker> allWorkers; // lista wszystkich abonentow
	private String locationCheck = "";

	private List<String> managementNumbers;

	/**
	 * Initializes a new instance, without starting interaction with Ericsson
	 * Network Resource Gateway (see start)
	 *
	 * @param aGUI
	 *            the GUI of the application
	 */
	public Feature(GUI aGUI) {
		theGUI = aGUI;
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
		try
		{
			itsHosaUIManager = (IpHosaUIManager) itsFramework
					.obtainSCF("SP_HOSA_USER_INTERACTION");
			itsOsaULManager = (IpUserLocation) itsFramework
					.obtainSCF("P_USER_LOCATION");
		}
		catch (P_UNKNOWN_SERVICE_TYPE anException)
		{
			System.err.println("Service not found. Please refer to the Ericsson Network Resource Gateway User Guide for "
					+ "a list of which applications that are able to run on which test tools\n"
					+ anException);
		}
		itsSMSProcessor = new SMSProcessor(itsHosaUIManager, this);
		itsMMSProcessor = new MMSProcessor(itsHosaUIManager, this);
		itsLocationProcessor = new LocationProcessor(itsOsaULManager, this);
		System.out.println("Starting SMS notification");
		assignmentId = new Integer(itsSMSProcessor.startNotifications(Configuration.INSTANCE.getProperty("serviceNumber")));

		managementNumbers = Arrays.asList("0001", "0002", "0003", "0004");
		allWorkers = new ArrayList<Worker>();
		service = new Service(this);
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
	 * @throws Exception
	 */
	protected void smsReceived(String aSender, String aReceiver,
							   String aMessageContent) {
		System.out.println("Odebrano SMS-a o tresci: " + aMessageContent);

		Worker worker = checkList(aSender); // dostajemy naszego pracownika, ktory wyslal SMS'a
		//Rejestracja uzytkownika
		if (aMessageContent.toLowerCase().matches("imie:(.*)") && worker == null ) {
			worker = new Worker(aSender, getName(aMessageContent), 8, itsLocationProcessor);
			service.addWorker(worker);
			System.out.println("Dodano pracownika o numerze: " + worker.getNumer());
			itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Jestes nowym uzytkownikiem serwisu");
		} else if(aMessageContent.toLowerCase().equals("rejestracja") && worker != null) {
			itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Nie musisz sie rejestrowac, jestes juz czlonkiem serwisu");
		}

		//worker chce zaczac monitorowac czas pracy
		//jezeli wszystko git, zaczynamy liczenie czasu od momentu request'a
		if (aMessageContent.toLowerCase().equals("start") && worker != null ) { //sprawdzamy pracownika
			locationCheck="";
			itsLocationProcessor.requestLocation(aSender); //sprawdzamy lokalizacje - nie mamy zwrotki od funkcji, trzeba dorobic!
			if(locationCheck.matches(aSender+":"+"at_work")){
				LocalDateTime workerStartedAt = LocalDate.now();
				worker.setStartedWorkAt(workerStartedAt);
				itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"),aSender,"Witaj w pracy!");
				locationCheck="";
			}
			else{
				itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"),aSender,"Nie znajdujesz sie w pracy!");
				locationCheck="";
			}
		}

		//Zatrzymanie rejestrowania czasu pracy przez pracownika
		if (aMessageContent.toLowerCase().equals("stop") && worker != null ) {
			LocalDateTime workerEndedAt = LocalDateTime.now();
			worker.setStartedWorkAt(workerEndedAt);
			itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"),aSender,"Do zobaczenia jutro :>!");
		}

		/* 15 minut przerwy
		   zacznij rejestrowac czas pracy po czasie przerwy  - sprawdzajac najpierw lokalizacje, czy pracownik jest w pracy
		   jezeli nie ma go w pracy po przerwie, zakoncz prace */
		if (aMessageContent.toLowerCase().equals("pauza") && worker != null ) {
			LocalDateTime pauseStartedAt = LocalDateTime.now();
			itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"),aSender,"Zaczynasz pauze, odpocznij, masz 15 minut! :>!");
			/*pytanie, jak po tych "15 minutach" sprawdzic, czy pracownik wrocil do firmy, bo interesuje nas jego polozenie,
			czy robimy thread.sleep i czekamy, czy wychodzimy stad i za jakis czas powrot do sprawdzenia?
			*/
			if(ChronoUnit.MINUTES.between(pauseStartedAt, LocalDateTime.now()) >= 15){
				itsLocationProcessor.requestLocation(aSender); //sprawdzamy lokalizacje - nie mamy zwrotki od funkcji, trzeba dorobic!
				//jezeli jest w robocie, to nic sie nie dzieje, czas leci sobie dalej
				//jezeli patalacha nie ma w robocie, to stopujemy czas pracy i czekamy az sie pojawi, zeby mu go wystartowac
				//TODO: obsluga pauzowania
			}
		}

		if (aMessageContent.toLowerCase().equals("lokalizacja") && worker != null ) {
			itsLocationProcessor.requestLocation(aSender);
		}

		if (aMessageContent.toLowerCase().matches("zapkalendarz:(.*)") && worker != null){
			String day = getDay(aMessageContent);
			String hour = getHour(aMessageContent);
			if(worker.setCalendar(Integer.parseInt(day),Integer.parseInt(hour)) == 0){
				System.out.println("Pomyślnie dokonano wpisu do kalendarza dnia "+day+" o godzinie "+hour);
				itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Pomyślnie dokonano wpisu do kalendarza dnia "+day+" o godzinie "+hour);
			}else{
				System.out.println("Termin dnia "+day+" o godzinie "+hour+" jest już zajęty");
				itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender, "Termin dnia "+day+" o godzinie "+hour+" jest już zajęty");
			}
		}

		if (aMessageContent.toLowerCase().matches("sprkalendarz:(.*)") && worker != null){
			String day = getDay(aMessageContent);
			String hour = getHour(aMessageContent);
			if(worker.setCalendar(Integer.parseInt(day),Integer.parseInt(hour)) == 0){
				System.out.println("Termin dnia "+day+" o godzinie "+hour+" jest wolny");
				itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender,"Termin dnia "+day+" o godzinie "+hour+" jest wolny");
			}else{
				System.out.println("Termin dnia "+day+" o godzinie "+hour+" jest zajęty");
				itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender,"Termin dnia "+day+" o godzinie "+hour+" jest zajęty");
			}
		}

		if (aMessageContent.toLowerCase().equals("status") && worker != null ) {
			//musimy zwrocic informacje od klasy Worker ile czasu zostalo do konca pracy, czy to procentowo, czy w godzinach
		}

		if (aMessageContent.toLowerCase().matches("gdzie:(.*)") && worker != null){ //zapytanie o lokalizację danego numeru
			if (worker.getNumer() == "2222") { //sprawdzamy czy numer danej osoby ma uprawnienia
				//wez
				String reqNum = aMessageContent.split(":")[1];
				if (checkList(reqNum) != null){
                    itsLocationProcessor.requestLocation(reqNum);
                    if (locationCheck != ""){
                        itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender,"Pracownik używający numeru " + reqNum + " jest w pracy");
                    } else {
                        itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender,"Pracownika używającego numeru " + reqNum + " nie ma w pracy");
                    }
                } else {
				    System.out.println("Nie ma takiego numeru");
					itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender,"Nie ma takiego numeru");
				}
			} else {
                itsSMSProcessor.sendSMS(Configuration.INSTANCE.getProperty("serviceNumber"), aSender,"Nie masz uprawnien do tych danych!");
			}
		}

		if(worker == null){
			// TODO: rzucamy wyjatek, ale gdzie go zlapiemy? ;)
		}
	}

	private String getName(String aMessageContent){
		return aMessageContent.substring(5);
	}
	private String getDay(String aMessageContent){
		return aMessageContent.substring(13,15);
	}
	private String getHour(String aMessageContent){
		return aMessageContent.substring(16,18);
	}

	private Worker checkList(String numer)
	{
		for (Worker w : service.getUserOfService())
			if (w.getNumer().equalsIgnoreCase(numer))
				return w;

		return null;
	}

	//TODO: funkcja ta musi jakos zwracac, czy uzytkownik jest w pracy, czy nie, aby mozna bylo egzekwowac czas pracy
	public void locationReceived(String user, float latitude, float longitude) {
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
			itsMMSProcessor.sendMMS(Configuration.INSTANCE.getProperty("serviceNumber"), user, messageContent
					.getBinaryContent(), "Current location");


			if(latitude > 0.59 && latitude < 0.68 && longitude > 0.28 && longitude < 0.4) {
				System.out.println("Witaj w pracy korposzczurku!");
				locationCheck = user.toString() + ":" + "at_work";
			}
			else{
				System.out.println("Nie znajdujesz się w pracy!");
				locationCheck = user.toString() + ":" + "not_at_work";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return a descriptive text that explains the application and its
	 *         configuration.
	 */
	private String getDescription() {
		String s = "Nacisnij START, aby sie polaczyc z symulatorem";
		s += "\n";
		s += "Pracownik moze wysylac SMS na numer " + Configuration.INSTANCE.getProperty("serviceNumber") + " z nastepujacymi poleceniami ";
		s += "\n-------------------------------------------\n";
		s += "\"imie:TWOJE_IMIE\" pozwala uzytkownikowi na rejestracje w systemie \n";
		s += "\"start\" pozwala uzytkownikowi na rozpoczecie rejestrowania czasu pracy \n";
		s += "\"stop\" pozwala uzytkownikowi na zakonczenie rejestrowania czasu pracy \n";
		s += "\"pauza\" pozwala uzytkownikowi rozpoczecie 15 minutowej przerwy \n";
		s += "\"status\" pozwala uzytkownikowi na sprawdzenie czasu pracy do konca dnia \n";
		s += "\"lokalizacja \" pozwala uzytkownikowi na zwrocenie aktualnej lokalizacji \n";
		s += "\"zapkalendarz:DZIEN_MIESIACA(DD),GODZINA(HH) \" pozwala uzytkownikowi na zajęcie terminu w kalendarzu(np. zapkalendarz:02,14) \n";
		s += "\"sprkalendarz:DZIEN_MIESIACA(DD),GODZINA(HH) \" pozwala uzytkownikowi na sprawdzenie czy w danym terminie jest zajęty (np. sprkalendarz:31,06)\n";
        s += "\"gdzie:NUMER_PRACOWNIKA \" pozwala uzytkownikowi będącemu w zarządzie na sprawdzenie czy pracownik jest w pracy\n";
		s += "\n-------------------------------------------\n";
		s += "Nacisnij STOP, aby zatrzymac aplikacje.\n";
		return s;
	}


}
