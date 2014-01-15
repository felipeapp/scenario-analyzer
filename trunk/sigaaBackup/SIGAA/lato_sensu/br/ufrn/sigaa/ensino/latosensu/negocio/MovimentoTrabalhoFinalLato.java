/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/04/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.latosensu.dominio.TrabalhoFinalLato;

/**
 * Movimento utilizado para manipular trabalhos finais do ensino lato sensu
 * 
 * @author Leonardo
 *
 */
public class MovimentoTrabalhoFinalLato extends AbstractMovimentoAdapter {

	private TrabalhoFinalLato trabalhoFinal;
	
	/** informações do arquivo com o trabalho final */
	private byte[] dadosArquivo;
	private String contentType;
	private String nome;
	
	public MovimentoTrabalhoFinalLato(){
		
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getDadosArquivo() {
		return dadosArquivo;
	}

	public void setDadosArquivo(byte[] dadosArquivo) {
		this.dadosArquivo = dadosArquivo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public TrabalhoFinalLato getTrabalhoFinal() {
		return trabalhoFinal;
	}

	public void setTrabalhoFinal(TrabalhoFinalLato trabalhoFinal) {
		this.trabalhoFinal = trabalhoFinal;
	}
	
	
}
