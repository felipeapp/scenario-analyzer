package br.ufrn.sigaa.assistencia.relatorios;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.sigaa.pessoa.dominio.Discente;

public class LinhaDadosIndiceAcademico {

	private Discente discente;
	private Double iechCurso;
	private Double ieplCurso;
	private Double iechDiscente;
	private Double ieplDiscente;
	private String tipoBolsa;

	public static boolean isIEPL ( int tipoIndice ){
		return tipoIndice == 5;
	}
	
	public static List<LinhaDadosIndiceAcademico> returnDiscentesBaixoIndice( Collection<LinhaDadosIndiceAcademico> array ){
		List<LinhaDadosIndiceAcademico> result = new ArrayList<LinhaDadosIndiceAcademico>();
		for (LinhaDadosIndiceAcademico linha : array) {
			if ( linha.getIechCurso() > linha.getIechDiscente() || linha.getIeplCurso() > linha.getIeplDiscente() ) {
				result.add(linha);
			}
		}
		return result;
	}
	
	public Discente getDiscente() {
		return discente;
	}
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}
	public Double getIechCurso() {
		return iechCurso;
	}
	public void setIechCurso(Double iechCurso) {
		this.iechCurso = iechCurso;
	}
	public Double getIeplCurso() {
		return ieplCurso;
	}
	public void setIeplCurso(Double ieplCurso) {
		this.ieplCurso = ieplCurso;
	}
	public Double getIechDiscente() {
		return iechDiscente;
	}
	public void setIechDiscente(Double iechDiscente) {
		this.iechDiscente = iechDiscente;
	}
	public Double getIeplDiscente() {
		return ieplDiscente;
	}
	public void setIeplDiscente(Double ieplDiscente) {
		this.ieplDiscente = ieplDiscente;
	}

	public String getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(String tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}
	
}