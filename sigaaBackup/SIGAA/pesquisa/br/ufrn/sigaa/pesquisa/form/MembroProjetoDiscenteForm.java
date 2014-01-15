/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/12/2006
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * Form utilizado para consultas de discentes ligados a projetos de pesquisa
 *
 * @author ricardo
 *
 */
public class MembroProjetoDiscenteForm extends SigaaForm<MembroProjetoDiscente> {

	/** Tipos de Busca */
	public static final int BUSCA_MATRICULA = 1;
	public static final int BUSCA_DISCENTE = 2;
	public static final int BUSCA_ORIENTADOR = 3;
	public static final int BUSCA_TODOS_ATIVOS = 4;

	/** Finalidades de Busca */
	public static final int EMISSAO_DECLARACAO = 1;
	public static final int PARECER_RELATORIOS_PARCIAIS = 2;
	public static final int PARECER_RELATORIOS_FINAIS = 3;
	public static final int RELATORIO_ATIVOS= 4;


	/** Período da bolsa */
	private String dataInicio;
	private String dataFim;

	/** Parâmetro de Busca selecionado */
	private int tipoBusca;

	private int finalidadeBusca;

	private MembroProjeto membroProjeto;
	private Unidade centro = new Unidade();
	private Unidade unidade = new Unidade();
	private Integer idCota;

	private int[] filtros = {};

	public MembroProjetoDiscenteForm() {
		obj = new MembroProjetoDiscente();
		obj.setDiscente(new Discente());
		obj.setPlanoTrabalho(new PlanoTrabalho());
		obj.getPlanoTrabalho().setOrientador(new Servidor());
		obj.getPlanoTrabalho().setProjetoPesquisa(new ProjetoPesquisa());
		obj.getPlanoTrabalho().getProjetoPesquisa().setLinhaPesquisa(new LinhaPesquisa());
		
		obj.setTipoBolsa(new TipoBolsaPesquisa());

		obj.getDiscente().getPessoa().setContaBancaria( new ContaBancaria() );

		membroProjeto = new MembroProjeto();
		membroProjeto.setServidor( new Servidor() );
	}

	public MembroProjeto getMembroProjetoServidor() {
		return membroProjeto;
	}

	public void setMembroProjetoServidor(MembroProjeto membroProjeto) {
		this.membroProjeto = membroProjeto;
	}

	/**
	 * @return the tipoBusca
	 */
	public int getTipoBusca() {
		return tipoBusca;
	}

	/**
	 * @param tipoBusca the tipoBusca to set
	 */
	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	/**
	 * @return the finalidadeBusca
	 */
	public int getFinalidadeBusca() {
		return finalidadeBusca;
	}

	/**
	 * @param finalidadeBusca the finalidadeBusca to set
	 */
	public void setFinalidadeBusca(int finalidadeBusca) {
		this.finalidadeBusca = finalidadeBusca;
	}

	public int[] getFiltros() {
		return filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	public Unidade getCentro() {
		return centro;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public String getDataFim() {
		MembroProjetoDiscente membroDiscente = obj;
		if( membroDiscente.getDataFim() != null )
			dataFim = Formatador.getInstance().formatarData( membroDiscente.getDataFim() );
		return dataFim;
	}

	public String getDataInicio() {
		MembroProjetoDiscente membroDiscente = obj;
		if( membroDiscente.getDataInicio() != null )
			dataInicio = Formatador.getInstance().formatarData( membroDiscente.getDataInicio() );
		return dataInicio;
	}

	public void setDataFim(String dataFim) {
		MembroProjetoDiscente membroDiscente = obj;
		membroDiscente.setDataFim( parseDate(dataFim) );
		this.dataFim = dataFim;
	}

	public void setDataInicio(String dataInicio) {
		MembroProjetoDiscente membroDiscente = obj;
		membroDiscente.setDataInicio( parseDate(dataInicio) );
		this.dataInicio = dataInicio;
	}

	public Integer getIdCota() {
		return idCota;
	}

	public void setIdCota(Integer idCota) {
		this.idCota = idCota;
	}

	public boolean isRelatorioAtivos() {
	    return finalidadeBusca == RELATORIO_ATIVOS;
	}
}
