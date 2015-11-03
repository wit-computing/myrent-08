package org.wit.myrent.models;

import static org.wit.android.helpers.LogHelpers.info;

import java.util.ArrayList;
import java.util.UUID;

import android.util.Log;

public class Portfolio
{
  public  ArrayList<Residence>  residences;
  private PortfolioSerializer   serializer;

  public Portfolio(PortfolioSerializer serializer)
  {
    this.serializer = serializer;
    try
    {
      residences = serializer.loadResidences();
    }
    catch (Exception e)
    {
      info(this, "Error loading residences: " + e.getMessage());
      residences = new ArrayList<Residence>();
    }
  }

  public boolean saveResidences()
  {
    try
    {
      serializer.saveResidences(residences);
      info(this, "Residences saved to file");
      return true;
    }
    catch (Exception e)
    {
      info(this, "Error saving residences: " + e.getMessage());
      return false;
    }
  }

  public void addResidence(Residence residence)
  {
    residences.add(residence);
  }

  public Residence getResidence(UUID id)
  {
    Log.i(this.getClass().getSimpleName(), "UUID parameter id: "+ id);

    for (Residence res : residences)
    {
      if(id.equals(res.id))
      {
        return res;
      }
    }
    info(this, "failed to find residence. returning first element array to avoid crash");
    return null;
  }

  public void deleteResidence(Residence c)
  {
    residences.remove(c);
  }
}
