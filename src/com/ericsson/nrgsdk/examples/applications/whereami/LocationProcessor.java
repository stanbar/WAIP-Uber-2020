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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.ericsson.hosasdk.api.TpAddress;
import com.ericsson.hosasdk.api.mm.TpLocationPriority;
import com.ericsson.hosasdk.api.mm.TpLocationRequest;
import com.ericsson.hosasdk.api.mm.TpLocationResponseIndicator;
import com.ericsson.hosasdk.api.mm.TpLocationResponseTime;
import com.ericsson.hosasdk.api.mm.TpLocationType;
import com.ericsson.hosasdk.api.mm.TpMobilityDiagnostic;
import com.ericsson.hosasdk.api.mm.TpMobilityError;
import com.ericsson.hosasdk.api.mm.TpUserLocation;
import com.ericsson.hosasdk.api.mm.TpUserLocationExtended;
import com.ericsson.hosasdk.api.mm.ul.IpAppUserLocation;
import com.ericsson.hosasdk.api.mm.ul.IpAppUserLocationAdapter;
import com.ericsson.hosasdk.api.mm.ul.IpUserLocation;
import com.ericsson.nrgsdk.examples.applications.whereami.models.Location;
import com.ericsson.nrgsdk.examples.tools.SDKToolkit;

/**
 * This class is responsible for all Ericsson Network Resource Gateway
 * interation regarding User Location needed for this application
 */
public class LocationProcessor extends IpAppUserLocationAdapter {

	private IpUserLocation itsULManager;

	private Feature itsParent;

	/**
	 * Creates a new instance.
	 *
	 * @param anULManager
	 *            a User Location manager
	 * @param aParent
	 *            the Feature that will receive location updates
	 */
	public LocationProcessor(IpUserLocation anULManager, Feature aParent) {
		itsULManager = anULManager;
		itsParent = aParent;
	}

	/**
	 * Requests a location update for a set of workers. Location updates will be
	 * returned asynchronously, using Feature.locationReceived.
	 *
	 * @param aUser
	 *            the user who's location is requested.
	 */
	public void requestLocation(String aUser, final BiConsumer<String, Location> callback) {
		TpAddress[] users = new TpAddress[] { SDKToolkit.createTpAddress(aUser) };
		TpLocationResponseTime responseTime = new TpLocationResponseTime(
				TpLocationResponseIndicator.P_M_NO_DELAY, -1); // timer
		// value,
		// not
		// applicable
		float accuracy = 100f; // in meters
		boolean altitudeRequested = false;
		String locationMethod = "NETWORK";
		TpLocationRequest request = new TpLocationRequest(accuracy,
				responseTime, altitudeRequested, TpLocationType.P_M_CURRENT,
				TpLocationPriority.P_M_NORMAL, locationMethod);
		itsULManager.extendedLocationReportReq(new IpAppUserLocation() {

			/**
			 * Invoked by Ericsson Network Resource Gateway to present the application
			 * with location reports. The geographical position for each user for which
			 * location information is available is reported to the callback.
			 */

			@Override
			public void extendedLocationReportRes(int anAssignmentId,
					TpUserLocationExtended[] reports) {
				for (int i = 0; i != reports.length; i++) {
					String user = reports[i].UserID.AddrString;
					if (reports[i].StatusCode == TpMobilityError.P_M_OK) {
						float latitude = reports[i].Locations[0].GeographicalPosition.Latitude;
						float longtitude = reports[i].Locations[0].GeographicalPosition.Longitude;
						callback.accept(user, new Location(latitude, longtitude));
					}
				}
			}

			@Override
			public void extendedLocationReportErr(int arg0, TpMobilityError arg1, TpMobilityDiagnostic arg2) {}
			@Override
			public void locationReportErr(int arg0, TpMobilityError arg1, TpMobilityDiagnostic arg2) {}
			@Override
			public void locationReportRes(int arg0, TpUserLocation[] arg1) {}
			@Override
			public void periodicLocationReport(int arg0, TpUserLocationExtended[] arg1) {}
			@Override
			public void periodicLocationReportErr(int arg0, TpMobilityError arg1, TpMobilityDiagnostic arg2) {}

		}, users, request);
	}
}
