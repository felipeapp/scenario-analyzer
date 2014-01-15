/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.AvisoFaltaDocente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe que representa uma Movimenta��o sobre objetos FaltaDocente
 * 
 * @author Henrique Andr�
 * 
 */
public class MovimentoAvisoFaltaDocente extends AbstractMovimentoAdapter {

	/** {@link AvisoFaltaDocente} movimentado. */
	private AvisoFaltaDocente avisoFalta;
	/** Discente do {@link MovimentoAvisoFaltaDocente}. */
	private Discente discente;

	/** Indica se o departamento ligado ao {@link MovimentoAvisoFaltaDocente} tem um chefe associado. */
	private boolean temChefe = true;
	/** Indica se o departamento ligado ao {@link MovimentoAvisoFaltaDocente} tem um diretor associado. */
	private boolean temDiretor = true;

	public AvisoFaltaDocente getAvisoFalta() {
		return avisoFalta;
	}

	public void setAvisoFalta(AvisoFaltaDocente avisoFalta) {
		this.avisoFalta = avisoFalta;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public boolean isTemChefe() {
		return temChefe;
	}

	public void setTemChefe(boolean temChefe) {
		this.temChefe = temChefe;
	}

	public boolean isTemDiretor() {
		return temDiretor;
	}

	public void setTemDiretor(boolean temDiretor) {
		this.temDiretor = temDiretor;
	}

}
