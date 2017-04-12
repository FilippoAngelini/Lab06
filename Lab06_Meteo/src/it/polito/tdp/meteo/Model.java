package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 50;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	public Model() {

	}

	public String getUmiditaMedia(int mese) {
		
		MeteoDAO dao = new MeteoDAO();

		return dao.getUmiditaMedia(mese);
	}

	public String trovaSequenza(int mese) {
		
		List <SimpleCity> parziale = new ArrayList<SimpleCity>();
		List <SimpleCity> best = new ArrayList <SimpleCity>();
		
		int step = 1;
		
		recursive(parziale,best,mese,step);

		return best.toString();
	}
	
	public void recursive (List<SimpleCity> parziale, List<SimpleCity> best, int mese, int step){
		
		MeteoDAO dao = new MeteoDAO();
		
		if(parziale.size()==NUMERO_GIORNI_TOTALI){
			if(punteggioSoluzione(parziale)<punteggioSoluzione(best)){
				best.clear();
				best.addAll(parziale);
			}
			return;
		}
		
		List <Rilevamento> rilevamentiGiorno = dao.getRilevamentiGiorno(mese,step);
		
		for(Rilevamento r : rilevamentiGiorno)
			if(controllaParziale(parziale)){
				SimpleCity ct = new SimpleCity (r.getLocalita(),r.getUmidita());
				parziale.add(ct);
				step++;
				recursive(parziale,best,mese,step);
				parziale.remove(ct);
			}
			else
				return;
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		double score = 0.0;
		
		for(SimpleCity sc : soluzioneCandidata){
			score += sc.getCosto();
		}
		
		for(int i = 1; i<soluzioneCandidata.size();i++)
			if(!soluzioneCandidata.get(i).getNome().equals(soluzioneCandidata.get(i-1).getNome()))
				score += 100;
		
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {
		
		if(parziale.size() > 15)
			return false;
		
		int contaTorino=0;
		int contaMilano=0;
		int contaGenova=0;
		
		for(SimpleCity ct : parziale){
			if(ct.getNome().equals("Torino"))
				contaTorino++;
			if(ct.getNome().equals("Milano"))
				contaMilano++;
			if(ct.getNome().equals("Genova"))
				contaGenova++;
		}
		
		if(contaTorino>6 || contaMilano >6 || contaGenova >6)
			return false;
		/*
		if(parziale.size()>=3)
			for(int i =2; i<parziale.size();i++)
				if(!parziale.get(i).getNome().equals(parziale.get(i-1).getNome()) || !parziale.get(i-1).getNome().equals(parziale.get(i-2).getNome()) || !parziale.get(i).getNome().equals(parziale.get(i-2).getNome()))
					return false;
		*/
		/*
		int contTemp = 0;
		boolean cambio = false;
		for(int j = 0; j<parziale.size()-2;j++){
			cambio = false;
			if(parziale.get(j).getNome().equals(parziale.get(j).getNome()))
					contTemp++;
			if(!parziale.get(j).getNome().equals(parziale.get(j).getNome())){
				contTemp = 0;
				cambio = true;
			}
			if(contTemp<3 && cambio == true)
				return false;
		}
		*/
		if(parziale.size()>=3){
			int cont = 0;
			for(int z = 0; z<parziale.size()-1;z++){
				if(!parziale.get(z).getNome().equals(parziale.get(z+1).getNome()) && cont<3)
					return false;
				if(!parziale.get(z).getNome().equals(parziale.get(z+1).getNome()))
					cont = 0;
			}
		}
		
		return true;
	}

}
