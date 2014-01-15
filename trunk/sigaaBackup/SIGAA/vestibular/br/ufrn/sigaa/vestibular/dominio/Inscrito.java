/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 07/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe auxiliar para realizar a importação dos candidatos do vestibular. 
 * 
 * @author Rafael Gomes
 *
 */
public class Inscrito {

	private Integer numeroInscricao;
	private char situacao;
	private Integer opcaoAprovacao;
	private Integer semestreAprovacao;
	private InscricaoVestibular inscricaoVestibular;
	private boolean existeResultadoClassificacao;
	private boolean aprovadoTurnoDistinto;
	private boolean aprovadoAma;
	private Integer classificacaoAprovado;
	/** Grupo de Cotas pelo qual o discente foi convocado. */
	private Integer  grupoCotaConvocado;
	/** Indica que o candidato foi classificado para preenchimento de vagas remanescente de grupo de cotas. */
	private Boolean grupoCotaRemanejado;
	private List<Opcao> opcoes = new ArrayList<Opcao>();
	private List<Prova> provas = new ArrayList<Prova>();
	

	public void addOpcao(Opcao opcao) {
		this.opcoes.add(opcao);
	}
 	
	public List<Opcao> getOpcoes() {
		return this.opcoes;
	}
	
	public void addProvas(Prova prova) {
		this.provas.add(prova);
	}
 
	public List<Prova> getProvas() {
		return this.provas;
	}

	/**
	 * @return the numeroInscricao
	 */
	public Integer getNumeroInscricao() {
		return numeroInscricao;
	}

	/**
	 * @param numeroInscricao the numeroInscricao to set
	 */
	public void setNumeroInscricao(Integer numeroInscricao) {
		this.numeroInscricao = numeroInscricao;
	}

	/**
	 * @return the situacao
	 */
	public char getSituacao() {
		return situacao;
	}

	/**
	 * @param situacao the situacao to set
	 */
	public void setSituacao(char situacao) {
		this.situacao = situacao;
	}

	/**
	 * @return the opcaoAprovacao
	 */
	public Integer getOpcaoAprovacao() {
		return opcaoAprovacao;
	}

	/**
	 * @param opcaoAprovacao the opcaoAprovacao to set
	 */
	public void setOpcaoAprovacao(Integer opcaoAprovacao) {
		this.opcaoAprovacao = opcaoAprovacao;
	}

	/**
	 * @return the semestreAprovacao
	 */
	public Integer getSemestreAprovacao() {
		return semestreAprovacao;
	}

	/**
	 * @param semestreAprovacao the semestreAprovacao to set
	 */
	public void setSemestreAprovacao(Integer semestreAprovacao) {
		this.semestreAprovacao = semestreAprovacao;
	}

	/**
	 * @return the inscricaoVestibular
	 */
	public InscricaoVestibular getInscricaoVestibular() {
		return inscricaoVestibular;
	}

	/**
	 * @param inscricaoVestibular the inscricaoVestibular to set
	 */
	public void setInscricaoVestibular(InscricaoVestibular inscricaoVestibular) {
		this.inscricaoVestibular = inscricaoVestibular;
	}

	/**
	 * @return the existeResultadoClassificacao
	 */
	public boolean isExisteResultadoClassificacao() {
		return existeResultadoClassificacao;
	}

	/**
	 * @param existeResultadoClassificacao the existeResultadoClassificacao to set
	 */
	public void setExisteResultadoClassificacao(boolean existeResultadoClassificacao) {
		this.existeResultadoClassificacao = existeResultadoClassificacao;
	}

	public boolean isAprovadoTurnoDistinto() {
		return aprovadoTurnoDistinto;
	}

	public void setAprovadoTurnoDistinto(boolean aprovadoTurnoDistinto) {
		this.aprovadoTurnoDistinto = aprovadoTurnoDistinto;
	}

	public boolean isAprovadoAma() {
		return aprovadoAma;
	}

	public void setAprovadoAma(boolean aprovadoAma) {
		this.aprovadoAma = aprovadoAma;
	}

	public Integer getClassificacaoAprovado() {
		return classificacaoAprovado;
	}

	public void setClassificacaoAprovado(Integer classificacaoAprovado) {
		this.classificacaoAprovado = classificacaoAprovado;
	}

	public Integer getGrupoCotaConvocado() {
		return grupoCotaConvocado;
	}

	public void setGrupoCotaConvocado(Integer grupoCotaConvocado) {
		this.grupoCotaConvocado = grupoCotaConvocado;
	}

	public Boolean getGrupoCotaRemanejado() {
		return grupoCotaRemanejado;
	}

	public void setGrupoCotaRemanejado(Boolean grupoCotaRemanejado) {
		this.grupoCotaRemanejado = grupoCotaRemanejado;
	}

}

