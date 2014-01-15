package br.ufrn.comum.dominio;

import java.util.Date;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.rh.dominio.ProcessoAvaliacao;
import br.ufrn.rh.dominio.Servidor;

/**
 * Responsável de Avaliação de uma Unidade.
 * Dado utilizado na Gestão de Desempenho Humano do SIGRH (GDH)
 *
 * @author Rejane
 *
 */
public class ResponsavelUnidadeAvaliacao implements PersistDB {
	/** Identificador*/
	protected int id;
	
	/** Data de cadastro da atribuição da responsabilidade de avaliação */
	protected Date dataCadastro;
	
	/** Em mudança de responsável pela avaliação, registra a data da inativação do responsável anterior */
	protected Date dataInativacao;
	
	/** Servidor responsável pela avaliação na unidade */
	private Servidor servidorAvaliador;
	
	/** Processo GDH associado à chefia */
	private ProcessoAvaliacao processoGDH;
	
	/** Identificador do usuário que realizou o cadastro para atribuição de responsabilidade */
	private UsuarioGeral usuarioCadastro;
	
	private Unidade uniResponsavelAvaliacao;
		
	public Unidade getUniResponsavelAvaliacao() {
		return uniResponsavelAvaliacao;
	}

	public void setUniResponsavelAvaliacao(Unidade uniResponsavelAvaliacao) {
		this.uniResponsavelAvaliacao = uniResponsavelAvaliacao;
	}

	public UsuarioGeral getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(UsuarioGeral usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public ProcessoAvaliacao getProcessoGDH() {
		return processoGDH;
	}

	public void setProcessoGDH(ProcessoAvaliacao processoGDH) {
		this.processoGDH = processoGDH;
	}

	public ResponsavelUnidadeAvaliacao(){
		
	}
	
	public Servidor getServidorAvaliador() {
		return servidorAvaliador;
	}

	public void setServidorAvaliador(Servidor servidorAvaliador) {
		this.servidorAvaliador = servidorAvaliador;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataInativacao() {
		return dataInativacao;
	}

	public void setDataInativacao(Date dataInativacao) {
		this.dataInativacao = dataInativacao;
	}	
	
}