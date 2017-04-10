package it.polito.tdp.meteo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Rilevamento;

public class MeteoDAO {

	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {

		return null;
	}

	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {

		return 0.0;
	}

	public String getUmiditaMedia(int mese) {
		final String sql = "SELECT Localita, AVG (Umidita) as avg FROM situazione WHERE data>=? AND data <= ? GROUP BY Localita";

		String ris = "";

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			String ausilio = "";
			
			if(mese <= 10)
				ausilio = "0";
			
			String inizio = "2013" + ausilio + Integer.toString(mese) + "01";
			String fine = "2013" + ausilio + Integer.toString(mese) + "31";
			
			st.setString(1, inizio);
			st.setString(2, fine);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				ris += rs.getString("Localita") + ": " + rs.getString("avg") + "\n";
			}

			conn.close();
			return ris.trim();

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
