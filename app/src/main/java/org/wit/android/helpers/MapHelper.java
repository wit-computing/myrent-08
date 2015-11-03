package org.wit.android.helpers;

import android.content.Context;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import java.lang.NumberFormatException;
import static org.wit.android.helpers.LogHelpers.info;

public class MapHelper
{
  public static LatLng latLng(Context context, String geolocation)
  {
    String[] g = geolocation.split(",");
    try
    {
      if (g.length == 2)
      {
        return new LatLng(Double.parseDouble(g[0]), Double.parseDouble(g[1]));
      }
    }
    catch (NumberFormatException e)
    {
      info(context, "Number format exception: invalid geolocation: " + e.getMessage());
    }
    Toast.makeText(context, "An invalid geolocation has been entered: defaulting to 0,0", Toast.LENGTH_SHORT).show();
    return new LatLng(0, 0);

  }

  public static String latLng(LatLng geo)
  {

    return String.format("%.6f", geo.latitude) + ", " + String.format("%.6f", geo.longitude);
  }

}