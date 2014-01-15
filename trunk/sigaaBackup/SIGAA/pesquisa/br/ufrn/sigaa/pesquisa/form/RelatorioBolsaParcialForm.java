/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/02/2007
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaParcial;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Formulário para controle dos casos de uso relacionados a relatórios parciais
 * de bolsas de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("serial")
public class RelatorioBolsaParcialForm extends
		SigaaForm<RelatorioBolsaParcial> {

	// Filtros usados na busca 
	/** Indica se filtra a busca por nível de ensino. */
	private boolean filtroCentro;
	private boolean filtroDepartamento;
	private boolean filtroModalidade;
	private boolean filtroOrientador;
	private boolean filtroCota;
	private boolean filtroAluno;
	private boolean filtroTodos;

	private int centro;

	private Unidade unidade;

	private int modalidade;

	private Servidor orientador;

	private Discente discente;

	private int cota;

	private boolean parecer;

	public RelatorioBolsaParcialForm() {
		obj = new RelatorioBolsaParcial();
		obj.setPlanoTrabalho(new PlanoTrabalho());
		obj.getPlanoTrabalho().setMembroProjetoDiscente(new MembroProjetoDiscente());

		unidade = new Unidade();
		orientador = new Servidor();
		discente = new Discente();
		discente.setPessoa(new Pessoa());
	}

	@Override
	public void checkRole(HttpServletRequest req) throws ArqException {
	}

	/**
	 * @return the centro
	 */
	public int getCentro() {
		return centro;
	}

	/**
	 * @param centro the centro to set
	 */
	public void setCentro(int centro) {
		this.centro = centro;
	}

	/**
	 * @return the departamento
	 */
	public Unidade getUnidade() {
		return unidade;
	}

	/**
	 * @param departamento the departamento to set
	 */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	/**
	 * @return the modalidade
	 */
	public int getModalidade() {
		return modalidade;
	}

	/**
	 * @param modalidade the modalidade to set
	 */
	public void setModalidade(int modalidade) {
		this.modalidade = modalidade;
	}

	/**
	 * @return the orientador
	 */
	public Servidor getOrientador() {
		return orientador;
	}

	/**
	 * @param orientador the orientador to set
	 */
	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}

	/**
	 * @return the cota
	 */
	public int getCota() {
		return cota;
	}

	/**
	 * @param cota the cota to set
	 */
	public void setCota(int cota) {
		this.cota = cota;
	}

	/**
	 * @return the discente
	 */
	public Discente getDiscente() {
		return discente;
	}

	/**
	 * @param discente the discente to set
	 */
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/**
	 * @return the parecer
	 */
	public boolean isParecer() {
		return parecer;
	}

	/**
	 * @param parecer the parecer to set
	 */
	public void setParecer(boolean parecer) {
		this.parecer = parecer;
	}

	public boolean isFiltroCentro() {
		return filtroCentro;
	}

	public void setFiltroCentro(boolean filtroCentro) {
		this.filtroCentro = filtroCentro;
	}

	public boolean isFiltroDepartamento() {
		return filtroDepartamento;
	}

	public void setFiltroDepartamento(boolean filtroDepartamento) {
		this.filtroDepartamento = filtroDepartamento;
	}

	public boolean isFiltroModalidade() {
		return filtroModalidade;
	}

	public void setFiltroModalidade(boolean filtroModalidade) {
		this.filtroModalidade = filtroModalidade;
	}

	public boolean isFiltroOrientador() {
		return filtroOrientador;
	}

	public void setFiltroOrientador(boolean filtroOrientador) {
		this.filtroOrientador = filtroOrientador;
	}

	public boolean isFiltroCota() {
		return filtroCota;
	}

	public void setFiltroCota(boolean filtroCota) {
		this.filtroCota = filtroCota;
	}

	public boolean isFiltroAluno() {
		return filtroAluno;
	}

	public void setFiltroAluno(boolean filtroAluno) {
		this.filtroAluno = filtroAluno;
	}

	public boolean isFiltroTodos() {
		return filtroTodos;
	}

	public void setFiltroTodos(boolean filtroTodos) {
		this.filtroTodos = filtroTodos;
	}

}