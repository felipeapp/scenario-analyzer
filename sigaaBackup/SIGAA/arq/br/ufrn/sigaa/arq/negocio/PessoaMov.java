/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 21/09/2008
 *
 */
package br.ufrn.sigaa.arq.negocio;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.PessoaJuridica;

/**
 * @author Andre M Dantas
 *
 */
public class PessoaMov extends MovimentoCadastro {

	private int tipoValidacao;

	private Pessoa pessoa;

	private PessoaJuridica pessoaJuridica;

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public PessoaJuridica getPessoaJuridica() {
		return pessoaJuridica;
	}

	public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
		this.pessoaJuridica = pessoaJuridica;
	}

	/* 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return 0;
	}

	/* 
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {

	}

	public int getTipoValidacao() {
		return tipoValidacao;
	}

	public void setTipoValidacao(int tipoValidacao) {
		this.tipoValidacao = tipoValidacao;
	}


}
