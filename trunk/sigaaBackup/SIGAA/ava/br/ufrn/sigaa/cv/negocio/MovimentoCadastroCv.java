/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.cv.negocio;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.MembroComunidade;

/**
 * Movimento para realiza��o de cadastros no CV. Cont�m mensagem para 
 * ser colocada no registro de atividades.
 * 
 * @author David Pereira
 *
 */
public class MovimentoCadastroCv extends MovimentoCadastro {

	private String mensagem;

	private ComunidadeVirtual comunidade;
	
	private Specification specification;
	
	private MembroComunidade membroComunidade;

	private String hash;
	
	public MembroComunidade getMembroComunidade() {
		return membroComunidade;
	}

	public void setMembroComunidade(MembroComunidade membroComunidade) {
		this.membroComunidade = membroComunidade;
	}

	/**
	 * @return the mensagem
	 */
	public String getMensagem() {
		return mensagem;
	}

	/**
	 * @param mensagem the mensagem to set
	 */
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	/**
	 * @return the turma
	 */
	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	/**
	 * @param turma the turma to set
	 */
	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}

	public Specification getSpecification() {
		return specification;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
