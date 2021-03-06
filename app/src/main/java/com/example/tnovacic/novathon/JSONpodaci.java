package com.example.tnovacic.novathon;



import android.os.AsyncTask;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Zvjerka on 23.9.2017..
 */

public class JSONpodaci {


    static final String[][] elementi = {{"MINER MIVEL","PICE"},
            {"CIPS K PLUS","HRANA"},
            {"SOK","PICE"},
            {"BOM","HRANA"},
            {"NES CL","HRANA"},
            {"SNACK LED","HRANA"},
            {"DIZEL","GORIVO"},
            {"K PLUS MLIJE","HRANA"},
            {"STRUJA","RACUNI"},
            {"MAJICA","OSTALO"}};
    static String slanje_str;

    public static void parsiranje(ArrayList<Proizvod> listaProizvoda) {
        String str1 = "";
        String str2 = "";
        String str3 = "";
        String str4 = "";
        double cifra = 0;
        int i = 0;
        ArrayList<Double> listaCifri = new ArrayList<>();

        for (Proizvod proizvod : listaProizvoda) {
            str1 = proizvod.getNazivProizvoda();
            str2 = proizvod.getKolicina();
            if(proizvod.getCijena().equals("")) {
                str3 = proizvod.getIznos();
            }
            if(proizvod.getIznos().equals("")) {
                str4 = proizvod.getCijena();
            }
            if(proizvod.getIznos().equals("") || proizvod.getCijena().equals("")) {
                if (proizvod.getNazivProizvoda().contains("MINER MIVELA")) {
                    str3 = "5,99";
                    str4 = "5,99";
                }
                if (proizvod.getNazivProizvoda().contains("CIPS K PLUS")) {
                    str3 = "6,99";
                    str4 = "6,99";
                }
                if (proizvod.getNazivProizvoda().contains("BOM")) {
                    str3 = "7,99";
                    str4 = "7,99";
                }
                if (proizvod.getNazivProizvoda().contains("K PLUS MLIJE")) {
                    str3 = "3,29";
                    str4 = "3,29";
                }
                if (proizvod.getNazivProizvoda().contains("SNACK LED")) {
                    str3 = "6,00";
                    str4 = "6,00";
                }
                if (proizvod.getNazivProizvoda().contains("NES CL")) {
                    str3 = "2,69";
                    str4 = "2,69";
                }
            }

        i = 0;
            cifra = 0;
            if(str3.contains(",")) {
                String vrij = str3;
                str3 = str3.substring(0,1);
                str3 = str3 + "." + vrij.substring(2,3);
            }
            if(str4.contains(",")) {
                String vrij = str4;
                str4 = str4.substring(0,1);
                str4 = str4 + "." + vrij.substring(2,3);
            }

            try {
                cifra = ((double) Double.parseDouble(str4) / 100) * Double.parseDouble(str2);
                listaCifri.add(cifra);
            } catch (Exception e) {
                e.printStackTrace();
            }
      }
        if (str1.contains(elementi[0][i++])) {
            slanje_str = "cifra="+listaCifri+"&kategorija="+elementi[1][i];
            new slanje().execute();
        }
    }



    static class slanje extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL("http://10.20.0.89/web-api/data");
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestMethod("POST");
                OutputStreamWriter out = new OutputStreamWriter(
                        httpCon.getOutputStream());
                out.write(slanje_str);
                out.flush();
                System.out.println(httpCon.getResponseCode());
                System.out.println(httpCon.getResponseMessage());
                out.close();
            }catch (Exception e){System.out.println("greska pri primanju");}
            return null;
        }
    }


}
