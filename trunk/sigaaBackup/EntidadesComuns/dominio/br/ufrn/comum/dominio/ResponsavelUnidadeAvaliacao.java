package br.ufrn.comum.dominio;

import java.util.Date;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.rh.dominio.ProcessoAvaliacao;
import br.ufrn.rh.dominio.Servidor;

/**
 * Respons�vel de Avalia��o de uma Unidade.
 * Dado utilizado na Gest�o de Desempenho Humano do SIGRH (GDH)
 *
 * @author Rejane
 *
 */
public class ResponsavelUnidadeAvaliacao implements PersistDB {
	/** Identificador*/
	protected int id;
	
	/** Data de cadastro da atribui��o da responsabilidade de avalia��o */
	protected Date dataCadastro;
	
	/** Em mudan�a de respons�vel pela avalia��o, registra a data da inativa��o do respons�vel anterior */
	protected Date dataInativacao;
	
	/** Servidor respons�vel pela avalia��o na unidade */
	private Servidor servidorAvaliador;
	
	/** Processo GDH associado � chefia */
	private ProcessoAvaliacao processoGDH;
	
	/** Identificador do usu�rio que realizou o cadastro para atribui��o de responsabilidade */
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