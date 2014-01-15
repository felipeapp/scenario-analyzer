/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/05/2007
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Form utilizado pelas operações de distribuição de projetos de pesquisa a
 * consultores
 *
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("unchecked")
public class DistribuicaoProjetosForm extends SigaaForm {


	private int consultoresProjeto;

	private Consultor consultor;

	/** Utilizado para distribuição manual */
	private CodigoProjetoPesquisa codigo;

	/** Coleção de projetos selecionados para a distribuição manual */
	private Collection<ProjetoPesquisa> projetos;

	/** Utilizados na consulta */
	private int tipoDistribuicao;
	private Unidade centro;
	private int[] filtros = {};

	public static final int CONSULTOR 			= 1;
	public static final int CENTRO 				= 2;
	public static final int TIPO_DISTRIBUICAO 	= 3;
	public static final int EDITAL			 	= 4;

	private Integer idEdital;
	
	public DistribuicaoProjetosForm() {
		codigo = new CodigoProjetoPesquisa();
		consultor = new Consultor();
		projetos = new ArrayList<ProjetoPesquisa>();

		centro = new Unidade();
	}

	public Consultor getConsultor() {
		return consultor;
	}

	public void setConsultor(Consultor consultor) {
		this.consultor = consultor;
	}

	public int getConsultoresProjeto() {
		return consultoresProjeto;
	}

	public void setConsultoresProjeto(int consultoresProjeto) {
		this.consultoresProjeto = consultoresProjeto;
	}

	public Collection<ProjetoPesquisa> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoPesquisa> projetos) {
		this.projetos = projetos;
	}

	public CodigoProjetoPesquisa getCodigo() {
		return codigo;
	}

	public void setCodigo(CodigoProjetoPesquisa codigo) {
		this.codigo = codigo;
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public int getTipoDistribuicao() {
		return tipoDistribuicao;
	}

	public void setTipoDistribuicao(int tipoDistribuicao) {
		this.tipoDistribuicao = tipoDistribuicao;
	}

	public int[] getFiltros() {
		return filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	public Integer getIdEdital() {
		return idEdital;
	}

	public void setIdEdital(Integer idEdital) {
		this.idEdital = idEdital;
	}

}

