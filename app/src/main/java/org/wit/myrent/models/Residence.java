package org.wit.myrent.models;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import org.wit.myrent.R;
import android.content.Context;


public class Residence
{
  public UUID id;

  public String  geolocation;
  public Date    date;
  public boolean rented;
  public String  tenant;
  public double  zoom;//zoom level of accompanying map

  private static final String JSON_ID             = "id"            ;
  private static final String JSON_GEOLOCATION    = "geolocation"   ;
  private static final String JSON_DATE           = "date"          ;
  private static final String JSON_RENTED         = "rented"        ;
  private static final String JSON_TENANT         = "tenant";
  private static final String JSON_ZOOM           = "zoom"          ; //map zoom level

  public Residence()
  {
    id          = UUID.randomUUID();
    date        = new Date();
    geolocation = "52.253456,-7.187162";
    tenant      = ": none presently";
    zoom        = 16.0f;
  }

  // Note: new Date object contains original registration date
  public Residence(JSONObject json) throws JSONException
  {
    id            = UUID.fromString(json.getString(JSON_ID));
    geolocation   = json.getString(JSON_GEOLOCATION);
    date          = new Date(json.getLong(JSON_DATE));
    rented        = json.getBoolean(JSON_RENTED);
    tenant        = json.getString(JSON_TENANT);
    zoom          = json.getDouble(JSON_ZOOM);
  }

  public JSONObject toJSON() throws JSONException
  {
    JSONObject json = new JSONObject();
    json.put(JSON_ID            , id.toString());
    json.put(JSON_GEOLOCATION   , geolocation);
    json.put(JSON_DATE          , date.getTime());
    json.put(JSON_RENTED        , rented);
    json.put(JSON_TENANT        , tenant);
    json.put(JSON_ZOOM          , zoom);
    return json;
  }

  public String getDateString()
  {
    return "Registered: " + DateFormat.getDateTimeInstance().format(date);
  }

  public void setGeolocation(String geolocation)
  {
    this.geolocation = geolocation;
  }

  public String getResidenceReport(Context context)
  {
    String rentedString = null;
    if (rented)
    {
      rentedString = context.getString(R.string.residence_report_rented);
    }
    else
    {
      rentedString = context.getString(R.string.residence_report_not_rented);
    }
    String dateFormat = "EEE, MMM dd";
    String dateString = android.text.format.DateFormat.format(dateFormat, date).toString();
    String prospectiveTenant = tenant;
    if (tenant == null)
    {
      prospectiveTenant = context.getString(R.string.residence_report_nobody_interested);
    }
    else
    {
      prospectiveTenant = context.getString(R.string.residence_report_prospective_tenant, tenant);
    }
    String report =  "Location " + geolocation + " Date: " + dateString + " " + rentedString + " " + prospectiveTenant;
    return report;
  }
}