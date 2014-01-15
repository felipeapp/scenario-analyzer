/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas
 * Criado em: 21/01/2009
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;

/**
 * Movimento para realizar cadastro e alteração sobre DadosDefesa
 * 
 * @author Victor Hugo
 */
public class MovimentoBancaAlunoConcluido extends AbstractMovimentoAdapter {

	private BancaPos banca;
	private UploadedFile arquivo;

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public BancaPos getBanca() {
		return banca;
	}

	public void setBanca(BancaPos banca) {
		this.banca = banca;
	}

}
