/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 09/07/2010
 * 
 */
package br.ufrn.sigaa.pesquisa.relatorios;

import java.util.Map;
import java.util.TreeMap;

/**
 * Classe auxiliar para a visualiza��o do relat�rio quantitativo de bolsas de pesquisa ativas.
 *
 * @author Thalisson Muriel
 */
public class LinhaRelatorioBolsaPesquisa {
	
	/** Indica o nome do docente. */
	private String docente;
	
	/** Indica o nome do departamento. */
	private String departamento;
		
	/** Indica a sigla do centro. */
	private String centro;
	
	/** As seguintes v�riaveis indicam as quantidades de bolsas dos respectivos tipos de bolsa. */
	private int contPropesqIC;
	private int contPropesqIT;
	private int contPropesqInducaoIC;
	private int contPibic;
	private int contBalcao;
	private int contPibit;
	private int contPrhAnp;
	private int contDinterIC;
	private int contDinterIT;
	private int contReuniIC;
	private int contReuniIT;
	private int contReuniDinterIT;
	private int contPnaesIC;
	private int contPnaesIT;
	private int contPicme;
	private int contPet;
	private int contVoluntarioIC;
	private int contVoluntarioIT;
	private int contADefinir;
	private int contOutros;
	
	/** Indica os dados do relat�rio. */
	private Map<Integer, LinhaRelatorioBolsaPesquisa> linhas = new TreeMap<Integer, LinhaRelatorioBolsaPesquisa>();

	/** Construtor padr�o. */
	public LinhaRelatorioBolsaPesquisa(){
		
	}

	public String getDocente() {
		return docente;
	}

	public void setDocente(String docente) {
		this.docente = docente;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getCentro() {
		return centro;
	}

	public void setCentro(String centro) {
		this.centro = centro;
	}

	public int getContPropesqIC() {
		return contPropesqIC;
	}

	public void setContPropesqIC(int contPropesqIC) {
		this.contPropesqIC = contPropesqIC;
	}

	public int getContPropesqIT() {
		return contPropesqIT;
	}

	public void setContPropesqIT(int contPropesqIT) {
		this.contPropesqIT = contPropesqIT;
	}

	public int getContPropesqInducaoIC() {
		return contPropesqInducaoIC;
	}

	public void setContPropesqInducaoIC(int contPropesqInducaoIC) {
		this.contPropesqInducaoIC = contPropesqInducaoIC;
	}

	public int getContPibic() {
		return contPibic;
	}

	public void setContPibic(int contPibic) {
		this.contPibic = contPibic;
	}

	public int getContBalcao() {
		return contBalcao;
	}

	public void setContBalcao(int contBalcao) {
		this.contBalcao = contBalcao;
	}

	public int getContPibit() {
		return contPibit;
	}

	public void setContPibit(int contPibit) {
		this.contPibit = contPibit;
	}

	public int getContPrhAnp() {
		return contPrhAnp;
	}

	public void setContPrhAnp(int contPrhAnp) {
		this.contPrhAnp = contPrhAnp;
	}

	public int getContDinterIC() {
		return contDinterIC;
	}

	public void setContDinterIC(int contDinterIC) {
		this.contDinterIC = contDinterIC;
	}

	public int getContDinterIT() {
		return contDinterIT;
	}

	public void setContDinterIT(int contDinterIT) {
		this.contDinterIT = contDinterIT;
	}

	public int getContReuniIC() {
		return contReuniIC;
	}

	public void setContReuniIC(int contReuniIC) {
		this.contReuniIC = contReuniIC;
	}

	public int getContReuniIT() {
		return contReuniIT;
	}

	public void setContReuniIT(int contReuniIT) {
		this.contReuniIT = contReuniIT;
	}

	public int getContReuniDinterIT() {
		return contReuniDinterIT;
	}

	public void setContReuniDinterIT(int contReuniDinterIT) {
		this.contReuniDinterIT = contReuniDinterIT;
	}

	public int getContPnaesIC() {
		return contPnaesIC;
	}

	public void setContPnaesIC(int contPnaesIC) {
		this.contPnaesIC = contPnaesIC;
	}

	public int getContPnaesIT() {
		return contPnaesIT;
	}

	public void setContPnaesIT(int contPnaesIT) {
		this.contPnaesIT = contPnaesIT;
	}

	public int getContPicme() {
		return contPicme;
	}

	public void setContPicme(int contPicme) {
		this.contPicme = contPicme;
	}

	public int getContPet() {
		return contPet;
	}

	public void setContPet(int contPet) {
		this.contPet = contPet;
	}

	public int getContVoluntarioIC() {
		return contVoluntarioIC;
	}

	public void setContVoluntarioIC(int contVoluntarioIC) {
		this.contVoluntarioIC = contVoluntarioIC;
	}

	public int getContVoluntarioIT() {
		return contVoluntarioIT;
	}

	public void setContVoluntarioIT(int contVoluntarioIT) {
		this.contVoluntarioIT = contVoluntarioIT;
	}

	public int getContADefinir() {
		return contADefinir;
	}

	public void setContADefinir(int contADefinir) {
		this.contADefinir = contADefinir;
	}

	public int getContOutros() {
		return contOutros;
	}

	public void setContOutros(int contOutros) {
		this.contOutros = contOutros;
	}

	public Map<Integer, LinhaRelatorioBolsaPesquisa> getLinhas() {
		return linhas;
	}

	public void setLinhas(Map<Integer, LinhaRelatorioBolsaPesquisa> linhas) {
		this.linhas = linhas;
	}
	
	
	
}
