package com.scrapers;

import com.oddsmart.database.DataBase;
import com.oddsmart.database.Match;
import com.oddsmart.database.Odd;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScrapersUtils {

    /**
     * Formatta una data e un'ora in un oggetto Date.
     * Se l'anno non  presente, viene preso l'anno corrente se la data è futura, altrimenti l'anno successivo.
     * @param date la data nel formato "dd/MM"
     * @param time l'ora nel formato "HH:mm"
     * @return l'oggetto Date con la data e l'ora formattate
     * @throws ParseException se la data o l'ora non sono nel formato corretto
     */
    public static java.util.Date formatDate(String date, String time) throws ParseException {
        Date now = Calendar.getInstance().getTime();
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(now));
        Date plusYear = new SimpleDateFormat("dd/MM/yyyy").parse(date + "/" + year);
        if (now.after(plusYear)) {
            year++;
        }
        date += "/" + year;
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date + " " + time);
    }

    public static boolean manageOdd(String odd, int bookmaker, int match_id, String url, int odd_type_id, String odd_option, DataBase dataBase) {
        try{
            Odd o = new Odd(bookmaker, match_id, url, odd_type_id, odd_option,
                    Double.parseDouble(odd),
                    new Timestamp(System.currentTimeMillis()));

            Integer id = dataBase.findOdd(o);
            if(id != null){
                o.id = id;
                return dataBase.UpdateOdd("odds", o);
            }else{
                return dataBase.insertOdd("odds", o) != -1;
            }
        }catch (Exception e){
            return false;
        }
    }
}