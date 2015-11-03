package org.wit.android.helpers;
import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;

public class IntentHelper
{
  public static void startActivity (Activity parent, Class classname)
  {
    Intent intent = new Intent(parent, classname);
    parent.startActivity(intent);
  }

  public static void startActivityWithData (Activity parent, Class classname, String extraID, Serializable extraData)
  {
    Intent intent = new Intent(parent, classname);
    intent.putExtra(extraID, extraData);
    parent.startActivity(intent);
  }

  public static void startActivityWithDataForResult (Activity parent, Class classname, String extraID, Serializable extraData, int idForResult)
  {
    Intent intent = new Intent(parent, classname);
    intent.putExtra(extraID, extraData);
    parent.startActivityForResult(intent, idForResult);
  }

  public static void navigateUp(Activity parent)
  {
    Intent upIntent = NavUtils.getParentActivityIntent(parent);
    NavUtils.navigateUpTo(parent, upIntent);
  }

  public static void selectContact(Activity parent, int id)
  {
    Intent selectContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    parent.startActivityForResult(selectContactIntent, id);
  }

  public static void sendEmail(Context context, String email, String subject, String body)
  {
    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    emailIntent.putExtra(Intent.EXTRA_TEXT, body);
    context.startActivity(Intent.createChooser(emailIntent, "Sending Email"));
  }

  public static void openPreferredLocationInMap(Activity parent, String location)
  {
    Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", location).build();

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(geoLocation);

    if (intent.resolveActivity(parent.getPackageManager()) != null)
    {
      parent.startActivity(intent);
    }
    else
    {
      LogHelpers.info(parent, "Couldn't call " + location + ", no receiving apps installed!");
    }
  }

}