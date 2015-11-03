package org.wit.myrent.activities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import org.wit.android.helpers.ContactHelper;
import org.wit.android.helpers.IntentHelper;
import org.wit.android.helpers.MapHelper;
import org.wit.myrent.R;
import org.wit.myrent.app.MyRentApp;
import org.wit.myrent.models.Portfolio;
import org.wit.myrent.models.Residence;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.DatePickerDialog;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;

import static org.wit.android.helpers.IntentHelper.sendEmail;
import static org.wit.android.helpers.IntentHelper.navigateUp;


public class ResidenceFragment extends Fragment implements TextWatcher,
    OnCheckedChangeListener,
    OnClickListener,
    DatePickerDialog.OnDateSetListener,
    GoogleMap.OnMarkerDragListener,
    GoogleMap.OnCameraChangeListener
{
  public static   final String  EXTRA_RESIDENCE_ID = "myrent.RESIDENCE_ID";

  private static  final int     REQUEST_CONTACT = 1;

  private EditText geolocation;
  private CheckBox rented;
  private Button   dateButton;
  private Button   tenantButton;
  private Button   reportButton;

  private Residence   residence;
  private Portfolio   portfolio;

  SupportMapFragment mapFragment;
  GoogleMap gmap;
  Marker marker;
  LatLng markerPosition;
  boolean markerDragged;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    UUID resId = (UUID)getArguments().getSerializable(EXTRA_RESIDENCE_ID);

    MyRentApp app = (MyRentApp) getActivity().getApplication();
    portfolio = app.portfolio;
    residence = portfolio.getResidence(resId);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
  {
    super.onCreateView(inflater, parent, savedInstanceState);
    View v = inflater.inflate(R.layout.fragment_residence, parent, false);

    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    addListeners(v);
    updateControls(residence);

    return v;
  }

  private void addListeners(View v)
  {
    geolocation = (EditText) v.findViewById(R.id.geolocation);
    dateButton  = (Button)   v.findViewById(R.id.registration_date);
    rented = (CheckBox) v.findViewById(R.id.isrented);
    tenantButton = (Button)  v.findViewById(R.id.tenant);
    reportButton = (Button)  v.findViewById(R.id.residence_reportButton);

    geolocation .addTextChangedListener(this);
    dateButton  .setOnClickListener(this);
    rented      .setOnCheckedChangeListener(this);
    tenantButton.setOnClickListener(this);
    reportButton.setOnClickListener(this);

  }

  public void updateControls(Residence residence)
  {
    geolocation.setText(residence.geolocation);
    rented.setChecked(residence.rented);
    dateButton.setText(residence.getDateString());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case android.R.id.home: navigateUp(getActivity());
        return true;
      default:                return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onPause()
  {
    super.onPause();
    portfolio.saveResidences();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if (resultCode != Activity.RESULT_OK)
    {
      return;
    }
    else
    if (requestCode == REQUEST_CONTACT)
    {
      String name = ContactHelper.getContact(getActivity(), data);
      residence.tenant = name;
      tenantButton.setText(name);
    }
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after)
  { }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count)
  {}



  @Override
  public void afterTextChanged(Editable c)
  {
    String thisGeolocation = c.toString();
    Log.i(this.getClass().getSimpleName(), "geolocation " + thisGeolocation);
    residence.geolocation = thisGeolocation;
    getActivity().setTitle(thisGeolocation);
    // using a flag, markerDragged, to avoid race condition
    if (markerDragged == true)
    {
      markerDragged = false;
    }
    else
    {
      renderMap(MapHelper.latLng(getActivity(), thisGeolocation));
    }
  }


  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
  {
    residence.rented = isChecked;
  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId())
    {
      case R.id.registration_date      : Calendar c = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog (getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpd.show();
        break;
      case R.id.tenant                 : Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, REQUEST_CONTACT);
        if (residence.tenant != null)
        {
          tenantButton.setText("Tenant: "+residence.tenant);
        }
        break;
      case R.id.residence_reportButton : sendEmail(getActivity(), "", getString(R.string.residence_report_subject), residence.getResidenceReport(getActivity()));
        break;

      case R.id.show_map               : IntentHelper.openPreferredLocationInMap(getActivity(), residence.geolocation);
        break;

    }
  }

  @Override
  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
  {
    Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
    residence.date = date;
    dateButton.setText(residence.getDateString());
  }

  /* GoogleMap code follows */
  private void initializeMapFragment()
  {
    FragmentManager fm = getChildFragmentManager();
    mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
    if (mapFragment == null)
    {
      mapFragment = SupportMapFragment.newInstance();
      fm.beginTransaction().replace(R.id.map, mapFragment).commit();
    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState)
  {
    super.onActivityCreated(savedInstanceState);
    initializeMapFragment();
  }

  /* GoogleMap.OnMarkerDragListener methods (three number) */
  @Override
  public void onMarkerDragStart(Marker marker)
  {

  }

  @Override
  public void onMarkerDrag(Marker marker)
  {

  }

  @Override
  public void onMarkerDragEnd(Marker marker)
  {
    residence.geolocation = MapHelper.latLng(marker.getPosition());
    getActivity().setTitle(residence.geolocation);
    gmap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    markerDragged = true;
    geolocation.setText(residence.geolocation);

  }
  /* GoogleMap.OnCameraChangeListener method (one number) */
  @Override
  public void onCameraChange(CameraPosition cameraPosition)
  {
    residence.zoom = cameraPosition.zoom;
    markerPosition = MapHelper.latLng(getActivity(), residence.geolocation);
    if (marker != null)
    {
      marker.remove();
      marker = null;
    }

    marker = gmap.addMarker(new MarkerOptions().position(markerPosition).draggable(true).title("Residence").alpha(0.7f)
        .snippet("GPS : " + markerPosition.toString()));
  }

  private void renderMap(LatLng markerPosition)
  {
    if (mapFragment != null)
    {
      gmap = mapFragment.getMap();
      if (gmap != null)
      {
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, (float) residence.zoom));
        gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        gmap.setOnMarkerDragListener(this);
        gmap.setOnCameraChangeListener(this);
      }
    }
  }

  @Override
  public void onStart()
  {
    super.onStart();
    renderMap(MapHelper.latLng(getActivity(), residence.geolocation));
    gmap.setOnCameraChangeListener(this);
  }
  /* End GoogleMap code */
}