/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/07/2010
 *
 */
package br.ufrn.sigaa.pesquisa.relatorios;

/**
 * Classe auxiliar utilizada na geração do relatório quantitativo de bolsas de pesquisa ativas.
 * Representa os parâmetros passados para realizar a consulta no banco de dados.
 * 
 * @author Thalisson Muriel
 *
 */
public class ParametrosRelatorioBolsaPesquisa {
	
	/** Váriaveis que representam os tipos de bolsas requeridas no relatório. */
	private boolean propesqIC;
	private boolean propesqIT;
	private boolean propesqInducaoIC;
	private boolean pibic;
	private boolean pibicAf;
	private boolean balcao;
	private boolean pibit;
	private boolean prhAnp;
	private boolean dinterIC;
	private boolean dinterIT;
	private boolean reuniIC;
	private boolean reuniIT;
	private boolean reuniDinterIT;
	private boolean reuniNuplamIT;
	private boolean pnaesIC;
	private boolean pnaesIT;
	private boolean picme;
	private boolean pet;
	private boolean voluntarioIC;
	private boolean voluntarioIT;
	private boolean aDefinir;
	private boolean outros;
	
	public boolean isPropesqIC() {
		return propesqIC;
	}

	public void setPropesqIC(boolean propesqIC) {
		this.propesqIC = propesqIC;
	}

	public boolean isPropesqIT() {
		return propesqIT;
	}

	public void setPropesqIT(boolean propesqIT) {
		this.propesqIT = propesqIT;
	}

	public boolean isPropesqInducaoIC() {
		return propesqInducaoIC;
	}

	public void setPropesqInducaoIC(boolean propesqInducaoIC) {
		this.propesqInducaoIC = propesqInducaoIC;
	}

	public boolean isPibic() {
		return pibic;
	}

	public void setPibic(boolean pibic) {
		this.pibic = pibic;
	}

	public boolean isPibicAf() {
		return pibicAf;
	}

	public void setPibicAf(boolean pibicAf) {
		this.pibicAf = pibicAf;
	}

	public boolean isBalcao() {
		return balcao;
	}

	public void setBalcao(boolean balcao) {
		this.balcao = balcao;
	}

	public boolean isPibit() {
		return pibit;
	}

	public void setPibit(boolean pibit) {
		this.pibit = pibit;
	}

	public boolean isPrhAnp() {
		return prhAnp;
	}

	public void setPrhAnp(boolean prhAnp) {
		this.prhAnp = prhAnp;
	}

	public boolean isDinterIC() {
		return dinterIC;
	}

	public void setDinterIC(boolean dinterIC) {
		this.dinterIC = dinterIC;
	}

	public boolean isDinterIT() {
		return dinterIT;
	}

	public void setDinterIT(boolean dinterIT) {
		this.dinterIT = dinterIT;
	}

	public boolean isReuniIC() {
		return reuniIC;
	}

	public void setReuniIC(boolean reuniIC) {
		this.reuniIC = reuniIC;
	}

	public boolean isReuniIT() {
		return reuniIT;
	}

	public void setReuniIT(boolean reuniIT) {
		this.reuniIT = reuniIT;
	}

	public boolean isReuniDinterIT() {
		return reuniDinterIT;
	}

	public void setReuniDinterIT(boolean reuniDinterIT) {
		this.reuniDinterIT = reuniDinterIT;
	}

	public boolean isReuniNuplamIT() {
		return reuniNuplamIT;
	}

	public void setReuniNuplamIT(boolean reuniNuplamIT) {
		this.reuniNuplamIT = reuniNuplamIT;
	}

	public boolean isPnaesIC() {
		return pnaesIC;
	}

	public void setPnaesIC(boolean pnaesIC) {
		this.pnaesIC = pnaesIC;
	}

	public boolean isPnaesIT() {
		return pnaesIT;
	}

	public void setPnaesIT(boolean pnaesIT) {
		this.pnaesIT = pnaesIT;
	}

	public boolean isPicme() {
		return picme;
	}

	public void setPicme(boolean picme) {
		this.picme = picme;
	}

	public boolean isPet() {
		return pet;
	}

	public void setPet(boolean pet) {
		this.pet = pet;
	}

	public boolean isVoluntarioIC() {
		return voluntarioIC;
	}

	public void setVoluntarioIC(boolean voluntarioIC) {
		this.voluntarioIC = voluntarioIC;
	}

	public boolean isVoluntarioIT() {
		return voluntarioIT;
	}

	public void setVoluntarioIT(boolean voluntarioIT) {
		this.voluntarioIT = voluntarioIT;
	}

	public boolean isaDefinir() {
		return aDefinir;
	}

	public void setaDefinir(boolean aDefinir) {
		this.aDefinir = aDefinir;
	}

	public boolean isOutros() {
		return outros;
	}

	public void setOutros(boolean outros) {
		this.outros = outros;
	}

}
