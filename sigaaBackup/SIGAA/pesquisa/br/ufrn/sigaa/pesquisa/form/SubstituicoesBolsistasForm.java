/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/12/2006
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Form utilizado para o relatório de substituições de bolsistas
 *
 * @author ricardo
 *
 */
@SuppressWarnings("unchecked")
public class SubstituicoesBolsistasForm extends SigaaForm {

	/** Mínimo de substituições a ser buscado */
	private int mininoSubstituicoes;

	/** Opções do formulário de consulta */

	private int[] filtros = {};

	public static final int FILTRO_TIPO_BOLSA = 1;
	public static final int FILTRO_ATIVOS_INATIVOS = 2;
	public static final int FILTRO_DOCENTE = 3;

	/** Período a ser analisado */
	private String inicio;
	private String fim;

	private int tipoBolsa;

	private CotaBolsas cota;

	/** Somente indicações ou somente finalizações ou todos */
	private Boolean ativos;

	private Servidor orientador;

	/** Ordenação do relatório */
	private String ordenacao;

	public SubstituicoesBolsistasForm(){
		ordenacao = "dataIndicacao";
		cota = new CotaBolsas();
		orientador = new Servidor();
	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException {

		// Validar data de início
		validateRequired(inicio, "Início do período", req);
		if (inicio != null && !"".equals(inicio))
			validaData(inicio, "Início do período", req);

		// Validar data de fim
		validateRequired(fim, "Fim do período", req);
		if (fim != null && !"".equals(fim))
			validaData(fim, "Fim do período", req);

	}

	public int getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(int tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	/**
	 * @return the fim
	 */
	public String getFim() {
		return fim;
	}

	/**
	 * @return the inicio
	 */
	public String getInicio() {
		return inicio;
	}

	/**
	 * @return the mininoSubstituicoes
	 */
	public int getMininoSubstituicoes() {
		return mininoSubstituicoes;
	}

	/**
	 * @param fim the fim to set
	 */
	public void setFim(String fim) {
		this.fim = fim;
	}

	/**
	 * @param inicio the inicio to set
	 */
	public void setInicio(String inicio) {
		this.inicio = inicio;
	}

	/**
	 * @param mininoSubstituicoes the mininoSubstituicoes to set
	 */
	public void setMininoSubstituicoes(int mininoSubstituicoes) {
		this.mininoSubstituicoes = mininoSubstituicoes;
	}

	public String getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(String ordenacao) {
		this.ordenacao = ordenacao;
	}

	public Boolean getAtivos() {
		return this.ativos;
	}

	public void setAtivos(Boolean ativos) {
		this.ativos = ativos;
	}

	public CotaBolsas getCota() {
		return this.cota;
	}

	public void setCota(CotaBolsas cota) {
		this.cota = cota;
	}

	public int[] getFiltros() {
		return this.filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	public Servidor getOrientador() {
		return this.orientador;
	}

	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}



}
