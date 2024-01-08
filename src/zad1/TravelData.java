package zad1;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;

public class TravelData {
    private File dataDir;

    public TravelData(File dataDir) {
        this.dataDir = dataDir;
    }

    public List<String> getOffersDescriptionsList(String loc, String dateFormat) {
        List<String> offers = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String[] localeParts = loc.split("_");
        Locale givenLoc = new Locale(localeParts[0], localeParts[1]);
        Locale locale = new Locale(loc);
        try {
            for (File file : dataDir.listFiles()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\t");
                        String country = parts[1];
                        for(Locale l : Locale.getAvailableLocales()){
                            if(l.getDisplayCountry(locale).equals(country) || l.getDisplayCountry().equals(country)){
                                country = l.getDisplayCountry(givenLoc);
                                break;
                            }
                        }
                    String placeKey = parts[4]; // klucz miejsca
                    ResourceBundle bundle = ResourceBundle.getBundle("messages", givenLoc);
                    String place = bundle.getString(placeKey);




                        String departureDate = sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(parts[2]));
                        String returnDate = sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(parts[3]));
                        String price = parts[5];
                        String currency = parts[6];

                        offers.add(country + " " + departureDate + " " + returnDate + " " + place + " " + price + " " + currency);
                }
                reader.close();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return offers;
    }


}





