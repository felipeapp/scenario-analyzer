/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 22/02/2008
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;

/**
 * Movimento para registrar homologação de trabalho final 
 * de pós stricto sensu.
 * 
 * @author David Pereira
 *
 */
public class MovimentoHomologacao extends AbstractMovimentoAdapter {

	private HomologacaoTrabalhoFinal homologacao;
	
	private UploadedFile arquivo;

	public HomologacaoTrabalhoFinal getHomologacao() {
		return homologacao;
	}

	public void setHomologacao(HomologacaoTrabalhoFinal homologacao) {
		this.homologacao = homologacao;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}
	
}
