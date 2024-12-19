package com.oddsmart.database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import org.apache.commons.lang3.tuple.Pair;

public class DataBase {
    public Connection connection = null;
    public DataBase(String url, String database, String user, String password){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + url + "/" + database, user, password);
            if(connection != null){
                System.out.println("Connessione al database riuscita");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Pair<Integer, String>> getBookmakerEndpoints(int bookmakerId) {
        String query = "SELECT league, url FROM endpoints WHERE bookmaker = " + bookmakerId;
        ArrayList<Pair<Integer, String>> endpoints = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                endpoints.add(Pair.of(rs.getInt("league"), rs.getString("url")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endpoints;
    }

    // Match
    public Match findMatch(int leagueId, String homeTeam, String awayTeam, Date date) {
        // per ora non considero la data perchè dà problemi
        String query = "SELECT id FROM matches WHERE league = " + leagueId + " AND home_team = '" + homeTeam + "' AND away_team = '" + awayTeam + "'" /*AND start_time = '" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date) + "'"*/;
        Match match;
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                match = new Match(rs.getInt("id"), leagueId, homeTeam, awayTeam, date);
            }else{
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
        return match;
    }
    public Match findMatch(int id) {
        String query = "SELECT * FROM matches WHERE id = " + id;
        Match match = new Match();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                match = new Match(rs.getInt("id"), rs.getInt("league"), rs.getString("home_team"), rs.getString("away_team"), rs.getDate("start_time"));
            }else{
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
        return match;
    }
    public Match findMatch(Match match) {
        return findMatch(match.leagueId, match.homeTeam, match.awayTeam, match.date);
    }
    public int insertMatch(String table, Match match){
        String query = "INSERT INTO " + table + " (league, home_team, away_team, start_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, match.leagueId);
            ps.setString(2, match.homeTeam);
            ps.setString(3, match.awayTeam);
            ps.setTimestamp(4, new java.sql.Timestamp(match.date.getTime()));
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    // Odds
    public Integer findOdd(int bookmakerId, int matchId, String website, int oddTypeId, String oddOption, double value, Timestamp retrievedAt) {
        String query = "SELECT id FROM odds WHERE bookmaker_id = ? AND match_id = ? AND odd_type_id = ? AND odd_option = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, bookmakerId);
            ps.setInt(2, matchId);
            ps.setInt(3, oddTypeId);
            ps.setString(4, oddOption);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public Odd findOdd(int id) {
        String query = "SELECT * FROM odds WHERE id = " + id;
        Odd odd = new Odd();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                odd = new Odd(id, rs.getInt("bookmaker_id"), rs.getInt("match_id"), rs.getString("website"), rs.getInt("odd_type_id"), rs.getString("odd_option"), rs.getDouble("value"), rs.getTimestamp("retrieved_at"));
            }
        } catch (SQLException e) {
            return null;
        }
        return odd;
    }
    public Integer findOdd(Odd odd) {
        return findOdd(odd.bookmakerId, odd.matchId, odd.website, odd.oddTypeId, odd.oddOption, odd.value, odd.retrievedAt);
    }
    public int insertOdd(String table, Odd odd){
        String query = "INSERT INTO " + table + " (match_id, bookmaker_id, website, odd_type_id, odd_option, value, retrieved_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, odd.matchId);
            ps.setInt(2, odd.bookmakerId);
            ps.setString(3, odd.website);
            ps.setInt(4, odd.oddTypeId);
            ps.setString(5, odd.oddOption);
            ps.setDouble(6, odd.value);
            ps.setTimestamp(7, new java.sql.Timestamp(odd.retrievedAt.getTime()));

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
    public boolean UpdateOdd(String table, Odd newOdd){
        String query = "UPDATE " + table + " SET value = ?, retrieved_at = ?, website = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, newOdd.value);
            ps.setTimestamp(2, new java.sql.Timestamp(newOdd.retrievedAt.getTime()));
            ps.setString(3, newOdd.website);
            ps.setInt(4, newOdd.id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

}
