package br.ufrn.sigaa.pesquisa.relatorios;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Linha auxiliar por armazenar as informações do relatório quantitativo de bolsistas por centro/departamento.
 * 
 * @author Jean Guerethes
 */
public class LinhaRelatorioCentroDepartamento {

	/** Nome do departamento */
	private String departamento;
	/** Quantidade de bolsistas voluntários */
	private int voluntario;
	/** Quantidade de bolsistas PIBIC */
	private int pibic;
	/** Quantidade de bolsistas PIBIT */
	private int pibit;
	/** Quantidade de bolsistas PIBIC AA */
	private int pibicAA;
	/** Quantidade de bolsistas PIBIC EM */
	private int pibicEM;
	/** Quantidade de bolsistas balcão */
	private int balcao;
	/** Quantidade de bolsistas PROPESQ IC */
	private int propesqIC;
	/** Quantidade de bolsistas PROPESQ IT */
	private int propesqIT;
	/** Quantidade de bolsistas PROPESQ Indução */
	private int propesqInd;
	/** Quantidade de bolsistas REUNI IC */
	private int reuniIC;
	/** Quantidade de bolsistas REUNI IT */
	private int reuniIT;
	/** Quantidade de bolsistas REUNI Nuplam */
	private int reuniNuplam;
	/** Quantidade de bolsistas REUNI Dinter */
	private int reuniDinter;
	/** Quantidade de bolsistas DINTER IC */
	private int dinterIC;
	/** Quantidade de bolsistas DINTER IT */
	private int dinterIT;
	/** Quantidade de bolsistas FAPERN IC */
	private int fapernIC;
	/** Quantidade de bolsistas PRH ANP */
	private int prhAnp;
	/** Quantidade de bolsistas PIC ME */
	private int picme;
	/** Quantidade de bolsistas PNAES IC */
	private int pnaesIC;
	/** Quantidade de bolsistas PNAES IT */
	private int pnaesIT;
	/** Quantidade de bolsistas Pesquisa PET */
	private int pesquisaPet;

	/**
	 * Método responsável por setar os valores da linha, passado no parâmetro.
	 * @param linha
	 * @param values
	 * @return
	 */
	public static LinhaRelatorioCentroDepartamento setValores(LinhaRelatorioCentroDepartamento linha, Object[] values) {
		for (int i = 1; i <= 22; i++) {
			switch ( i ) {
				case 1 : linha.setDepartamento(((String) values[i])); break;
				case 2 : linha.setVoluntario(((BigInteger) values[i]).intValue()); break;
				case 3 : linha.setPibic(((BigInteger) values[i]).intValue()); break;
				case 4 : linha.setPibit(((BigInteger) values[i]).intValue()); break;
				case 5 : linha.setPibicAA(((BigInteger) values[i]).intValue()); break;
				case 6 : linha.setPibicEM(((BigInteger) values[i]).intValue()); break;
				case 7 : linha.setBalcao(((BigInteger) values[i]).intValue()); break;
				case 8 : linha.setPropesqIC(((BigInteger) values[i]).intValue()); break;
				case 9 : linha.setPropesqIT(((BigInteger) values[i]).intValue()); break;
				case 10 : linha.setPropesqInd(((BigInteger) values[i]).intValue()); break;
				case 11 : linha.setReuniIC(((BigInteger) values[i]).intValue()); break;
				case 12 : linha.setReuniIT(((BigInteger) values[i]).intValue()); break;
				case 13 : linha.setReuniNuplam(((BigInteger) values[i]).intValue()); break;
				case 14 : linha.setReuniDinter(((BigInteger) values[i]).intValue()); break;
				case 15 : linha.setDinterIC(((BigInteger) values[i]).intValue()); break;
				case 16 : linha.setDinterIT(((BigInteger) values[i]).intValue()); break;
				case 17 : linha.setFapernIC(((BigInteger) values[i]).intValue()); break;
				case 18 : linha.setPrhAnp(((BigInteger) values[i]).intValue()); break;
				case 19 : linha.setPicme(((BigInteger) values[i]).intValue()); break;
				case 20 : linha.setPnaesIC(((BigInteger) values[i]).intValue()); break;
				case 21 : linha.setPnaesIT(((BigInteger) values[i]).intValue()); break;
				case 22 : linha.setPesquisaPet(((BigInteger) values[i]).intValue()); break;
			}
		}
		return linha;
	}
	
	/**
	 * Retorna o total de registros encontrados.
	 * @param relatorio
	 * @return
	 */
	public static int getTotal(Map<String, Collection<LinhaRelatorioCentroDepartamento>> relatorio) {
		int total = 0;
		Set<String> chaves = relatorio.keySet();
		for (String chave : chaves) {
			total += relatorio.get(chave).size();
		}
		return total;
	}
	
	public String getDepartamento() {
		return departamento;
	}
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	public int getVoluntario() {
		return voluntario;
	}
	public void setVoluntario(int voluntario) {
		this.voluntario = voluntario;
	}
	public int getPibic() {
		return pibic;
	}
	public void setPibic(int pibic) {
		this.pibic = pibic;
	}
	public int getPibit() {
		return pibit;
	}
	public void setPibit(int pibit) {
		this.pibit = pibit;
	}
	public int getPibicAA() {
		return pibicAA;
	}
	public void setPibicAA(int pibicAA) {
		this.pibicAA = pibicAA;
	}
	public int getPibicEM() {
		return pibicEM;
	}
	public void setPibicEM(int pibicEM) {
		this.pibicEM = pibicEM;
	}
	public int getBalcao() {
		return balcao;
	}
	public void setBalcao(int balcao) {
		this.balcao = balcao;
	}
	public int getPropesqIC() {
		return propesqIC;
	}
	public void setPropesqIC(int propesqIC) {
		this.propesqIC = propesqIC;
	}
	public int getPropesqIT() {
		return propesqIT;
	}
	public void setPropesqIT(int propesqIT) {
		this.propesqIT = propesqIT;
	}
	public int getPropesqInd() {
		return propesqInd;
	}
	public void setPropesqInd(int propesqInd) {
		this.propesqInd = propesqInd;
	}
	public int getReuniIC() {
		return reuniIC;
	}
	public void setReuniIC(int reuniIC) {
		this.reuniIC = reuniIC;
	}
	public int getReuniIT() {
		return reuniIT;
	}
	public void setReuniIT(int reuniIT) {
		this.reuniIT = reuniIT;
	}
	public int getReuniNuplam() {
		return reuniNuplam;
	}
	public void setReuniNuplam(int reuniNuplam) {
		this.reuniNuplam = reuniNuplam;
	}
	public int getReuniDinter() {
		return reuniDinter;
	}
	public void setReuniDinter(int reuniDinter) {
		this.reuniDinter = reuniDinter;
	}
	public int getDinterIC() {
		return dinterIC;
	}
	public void setDinterIC(int dinterIC) {
		this.dinterIC = dinterIC;
	}
	public int getDinterIT() {
		return dinterIT;
	}
	public void setDinterIT(int dinterIT) {
		this.dinterIT = dinterIT;
	}
	public int getFapernIC() {
		return fapernIC;
	}
	public void setFapernIC(int fapernIC) {
		this.fapernIC = fapernIC;
	}
	public int getPrhAnp() {
		return prhAnp;
	}
	public void setPrhAnp(int prhAnp) {
		this.prhAnp = prhAnp;
	}
	public int getPicme() {
		return picme;
	}
	public void setPicme(int picme) {
		this.picme = picme;
	}
	public int getPnaesIC() {
		return pnaesIC;
	}
	public void setPnaesIC(int pnaesIC) {
		this.pnaesIC = pnaesIC;
	}
	public int getPnaesIT() {
		return pnaesIT;
	}
	public void setPnaesIT(int pnaesIT) {
		this.pnaesIT = pnaesIT;
	}
	public int getPesquisaPet() {
		return pesquisaPet;
	}
	public void setPesquisaPet(int pesquisaPet) {
		this.pesquisaPet = pesquisaPet;
	}

}