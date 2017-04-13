package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private double costoBest;
	private List <Rilevamento> rilevamentiMese;

	public Model() {
		costoBest = 0;
		rilevamentiMese = new ArrayList<Rilevamento>();
	}

	public String getUmiditaMedia(int mese) {
		
		MeteoDAO dao = new MeteoDAO();

		return dao.getUmiditaMedia(mese);
	}

	public String trovaSequenza(int mese) {
		
		MeteoDAO dao = new MeteoDAO();
		
		List <SimpleCity> parziale = new ArrayList<SimpleCity>();
		List <SimpleCity> best = new ArrayList <SimpleCity>();
		
		rilevamentiMese = dao.getRilevamentiMese(mese);
		
		int step = 1;
		
		recursive(parziale,best/*,mese*/,step);
		
		String ris = best.toString() + "  Costo: " + costoBest + " €";

		return ris;
	}
	
	public void recursive (List<SimpleCity> parziale, List<SimpleCity> best/*, int mese*/, int step){
		
		//MeteoDAO dao = new MeteoDAO();
		
		if(parziale.size()==NUMERO_GIORNI_TOTALI && controllaParziale(parziale)){
			//System.out.print(parziale);
			//System.out.println("  " + punteggioSoluzione(parziale));
			if(best.size()==0){
				best.addAll(parziale);
				costoBest = punteggioSoluzione(best);
				System.out.print(best);
				System.out.println("  " + costoBest);
				return;
			}
			if(punteggioSoluzione(parziale)<costoBest){
				best.clear();
				best.addAll(parziale);
				costoBest = punteggioSoluzione(best);
				System.out.print(best);
				System.out.println("  " + costoBest);
			}
			return;
		}
		
		//List <Rilevamento> rilevamentiGiorno = dao.getRilevamentiGiorno(mese,step);
		
		List <Rilevamento> temp = new ArrayList <Rilevamento>();
		
		for(Rilevamento r : rilevamentiMese){
			Calendar cal = Calendar.getInstance();
			cal.setTime(r.getData());
			if(cal.get(Calendar.DAY_OF_MONTH)==step)
				temp.add(r);
			if(cal.get(Calendar.DAY_OF_MONTH)>step)
				break;
		}
			
		
		for(Rilevamento r : /*rilevamentiGiorno*/ temp){
			//System.out.println(parziale);
			if(controllaParziale(parziale)){
				SimpleCity ct = new SimpleCity (r.getLocalita(),r.getUmidita());
				parziale.add(ct);
				recursive(parziale,best/*,mese*/,step+1);
				parziale.remove(parziale.size()-1);
			}
			else
				return;
		}
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		double score = 0.0;
		
		for(SimpleCity sc : soluzioneCandidata){
			score += sc.getCosto();
		}
		
		for(int i = 1; i<soluzioneCandidata.size();i++)
			if(!soluzioneCandidata.get(i).getNome().equals(soluzioneCandidata.get(i-1).getNome()))
				score += COST;
		
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {
		
		if(parziale.size() > NUMERO_GIORNI_TOTALI)
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
		
		if(contaTorino>NUMERO_GIORNI_CITTA_MAX || contaMilano >NUMERO_GIORNI_CITTA_MAX || contaGenova >NUMERO_GIORNI_CITTA_MAX)
			return false;
		if((parziale.size() == NUMERO_GIORNI_TOTALI) && (contaTorino==0 || contaMilano ==0 || contaGenova ==0))
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
		if(parziale.size()>=NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN){
			int cont = 1;
			for(int z = 0; z<parziale.size()-1;z++){
				if(parziale.get(z).getNome().equals(parziale.get(z+1).getNome()))
					cont++;
				if(!parziale.get(z).getNome().equals(parziale.get(z+1).getNome()) && cont<3)
					return false;
				if((parziale.size() == NUMERO_GIORNI_TOTALI) && (!parziale.get(z).getNome().equals(parziale.get(z+1).getNome())) && z>=(parziale.size()-NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN))
					return false;
				if(!parziale.get(z).getNome().equals(parziale.get(z+1).getNome()))
					cont = 1;
			}
		}
		
		return true;
	}

}
