/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/05/2010
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Date;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoReposicaoAvaliacao;

/**
 * Classe que representa uma Movimentação sobre objetos de SolicitacaoReposicaoProva
 * 
 * @author Arlindo Rodrigues
 *
 */
public class MovimentoReposicaoProva extends AbstractMovimentoAdapter {
	
	private SolicitacaoReposicaoAvaliacao solicitacaoReposicaoProva;
	
	private int novoStatus;
	
	private List<SolicitacaoReposicaoAvaliacao> solicitacoes;
	
	private Date dataProva;
	
	private String observacao;
	
	private UploadedFile arquivo;

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public SolicitacaoReposicaoAvaliacao getSolicitacaoReposicaoProva() {
		return solicitacaoReposicaoProva;
	}

	public void setSolicitacaoReposicaoProva(
			SolicitacaoReposicaoAvaliacao solicitacaoReposicaoProva) {
		this.solicitacaoReposicaoProva = solicitacaoReposicaoProva;
	}

	public int getNovoStatus() {
		return novoStatus;
	}

	public void setNovoStatus(int novoStatus) {
		this.novoStatus = novoStatus;
	}

	public List<SolicitacaoReposicaoAvaliacao> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(List<SolicitacaoReposicaoAvaliacao> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public Date getDataProva() {
		return dataProva;
	}

	public void setDataProva(Date dataProva) {
		this.dataProva = dataProva;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}	
}
