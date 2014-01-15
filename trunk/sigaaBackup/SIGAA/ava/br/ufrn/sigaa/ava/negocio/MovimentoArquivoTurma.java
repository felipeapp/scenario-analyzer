/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2008
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ava.dominio.ArquivoTurma;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe de negócio que auxilia na gerência de arquivos e suas associações com as turmas virtuais.
 * 
 * @author davidpereira
 *
 */
public class MovimentoArquivoTurma extends AbstractMovimentoAdapter {
	
	/**Arquivo que esta sendo cadastrado na turma.*/
	private ArquivoTurma arquivoTurma;
	
	/**Arquivo que esta sendo cadastrado no porta arquivo.*/
	private ArquivoUsuario arquivo;
	
	/**Lista de arquivos que estao sendo cadastrados */
	private List<ArquivoTurma> arquivosTurma;
	
	/**Usuario que esta cadastrando o arquivo. */
	private Usuario usuario;
	
	/**Turma em que o cadastro esta sendo efetuado. */
	private Turma turma;
	
	/**Lista de ids das turmas que o cadastro deve ser efetuado.*/
	private List<String> cadastrarEm;
	
	/**Indica se o arquivo estará ssociado pelo menos uma turma*/
	private boolean associarTurma;
	
	public ArquivoUsuario getArquivo() {
		return arquivo;
	}

	public void setArquivo(ArquivoUsuario arquivo) {
		this.arquivo = arquivo;
	}

	public ArquivoTurma getArquivoTurma() {
		return arquivoTurma;
	}

	public void setArquivosTurma(List<ArquivoTurma> arquivosTurma) {
		this.arquivosTurma = arquivosTurma;
	}
	
	public List<ArquivoTurma> getArquivosTurma() {
		return arquivosTurma;
	}

	public void setArquivoTurma(ArquivoTurma aula) {
		this.arquivoTurma = aula;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public List<String> getCadastrarEm() {
		return cadastrarEm;
	}

	public void setCadastrarEm(List<String> cadastrarEm) {
		this.cadastrarEm = cadastrarEm;
	}

	public void setAssociarTurma(boolean associarTurma) {
		this.associarTurma = associarTurma;
	}

	public boolean isAssociarTurma() {
		return associarTurma;
	}

}
