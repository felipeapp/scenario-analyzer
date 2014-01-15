/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/06/2007
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaFinal;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Form para controle dos casos de uso relacionados a relatórios Final de bolsas de pesquisa.
 * @author ilueny
 *
 */
@SuppressWarnings("serial")
public class RelatorioBolsaFinalForm extends SigaaForm<RelatorioBolsaFinal> {

	public static final int BUSCA_CENTRO = 1;
	public static final int BUSCA_DEPARTAMENTO = 2;
	public static final int BUSCA_MODALIDADE = 3;
	public static final int BUSCA_ORIENTADOR = 4;
	public static final int BUSCA_COTA = 5;
	public static final int BUSCA_ALUNO = 6;
	public static final int BUSCA_PARECER = 7;
	public static final int BUSCA_SUBMETIDO = 8;

	@Deprecated
	private int tipoBusca;

	private int[] filtros = {};

	private Unidade centro;
	private Unidade unidade;
	private int modalidade;
	private Servidor orientador;
	private Discente discente;
	private CotaBolsas cota;

	private Boolean parecer;
	private Boolean submetido;

	public RelatorioBolsaFinalForm() {
		obj = new RelatorioBolsaFinal();
		obj.setPlanoTrabalho(new PlanoTrabalho());
		obj.getPlanoTrabalho().setMembroProjetoDiscente(new MembroProjetoDiscente());
		obj.setMembroDiscente(new MembroProjetoDiscente());

		centro = new Unidade();
		unidade = new Unidade();
		orientador = new Servidor();
		discente = new Discente();
		cota = new CotaBolsas();
		discente.setPessoa(new Pessoa());
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public CotaBolsas getCota() {
		return cota;
	}

	public void setCota(CotaBolsas cota) {
		this.cota = cota;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public int getModalidade() {
		return modalidade;
	}

	public void setModalidade(int modalidade) {
		this.modalidade = modalidade;
	}

	public Servidor getOrientador() {
		return orientador;
	}

	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}

	public Boolean getParecer() {
		return parecer;
	}

	public void setParecer(Boolean parecer) {
		this.parecer = parecer;
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public int[] getFiltros() {
		return this.filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	public Boolean getSubmetido() {
		return submetido;
	}

	public void setSubmetido(Boolean submetido) {
		this.submetido = submetido;
	}

}
