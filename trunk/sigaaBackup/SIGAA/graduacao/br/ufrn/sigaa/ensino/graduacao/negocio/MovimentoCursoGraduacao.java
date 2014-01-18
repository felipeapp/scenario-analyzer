/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 03/07/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;

/**
 * Movimento para cadastro de cursos de gradua��o. Encapsula, al�m do curso,
 * o arquivo com o projeto pol�tico-pedag�gico.
 *
 * @author David Pereira
 *
 */
public class MovimentoCursoGraduacao extends MovimentoCadastro {

	/** Arquivo com o projeto pedag�gico do curso. */ 
	private UploadedFile arquivo;

	/** Lista de Institui��es de ensino associadas ao curso. */
	private List<InstituicoesEnsino> instituicoesEnsino;
	
	/** Indica que deve cadastrar o curso como inativo. � usado para cadastrar cursos antigos. */
	private boolean cadastrarCursoInativo = false;
	
	/** Retorna o arquivo com o projeto pedag�gico do curso.
	 * @return the arquivo
	 */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/** Seta o arquivo com o projeto pedag�gico do curso.
	 * @param arquivo the arquivo to set
	 */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/** Retorna a lista de Institui��es de ensino associadas ao curso. 
	 * @return the instituicoesEnsino
	 */
	public List<InstituicoesEnsino> getInstituicoesEnsino() {
		return instituicoesEnsino;
	}

	/** Seta a lista de Institui��es de ensino associadas ao curso. 
	 * @param instituicoesEnsino the instituicoesEnsino to set
	 */
	public void setInstituicoesEnsino(List<InstituicoesEnsino> instituicoesEnsino) {
		this.instituicoesEnsino = instituicoesEnsino;
	}

	/** Indica que deve cadastrar o curso como inativo. � usado para cadastrar cursos antigos. 
	 * @return
	 */
	public boolean isCadastrarCursoInativo() {
		return cadastrarCursoInativo;
	}

	/** Indica se deve cadastrar o curso como inativo. � usado para cadastrar cursos antigos. 
	 * @param cadastrarCursoInativo
	 */
	public void setCadastrarCursoInativo(boolean cadastrarCursoInativo) {
		this.cadastrarCursoInativo = cadastrarCursoInativo;
	}
	
	

}
