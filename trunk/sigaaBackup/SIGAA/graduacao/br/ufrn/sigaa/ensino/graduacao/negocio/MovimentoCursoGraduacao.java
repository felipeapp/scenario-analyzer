/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/07/03 - 13:29:17
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;

/**
 * Movimento para cadastro de cursos de graduação. Encapsula, além do curso,
 * o arquivo com o projeto político-pedagógico.
 *
 * @author David Pereira
 *
 */
public class MovimentoCursoGraduacao extends MovimentoCadastro {

	private UploadedFile arquivo;

	private List<InstituicoesEnsino> instituicoesEnsino;
	
	/**
	 * @return the arquivo
	 */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/**
	 * @param arquivo the arquivo to set
	 */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/**
	 * @return the instituicoesEnsino
	 */
	public List<InstituicoesEnsino> getInstituicoesEnsino() {
		return instituicoesEnsino;
	}

	/**
	 * @param instituicoesEnsino the instituicoesEnsino to set
	 */
	public void setInstituicoesEnsino(List<InstituicoesEnsino> instituicoesEnsino) {
		this.instituicoesEnsino = instituicoesEnsino;
	}
	
	

}
