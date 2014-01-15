/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Jun 24, 2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.dominio.CalendarioAcademico;

/**
 *
 * @author Victor Hugo
 */
public class MovimentoAfastamentoAluno extends MovimentoCadastro {

	/**
	 * este atributo deve ser utilizando no opera��o de retorno de afastamento
	 * APENAS quando o pr�prio aluno esta se retornando para ativo.
	 * Neste caso o aluno deve possuir trancamento no ano/per�odo corrente e opcionalmente nos futuros
	 * e deve estar no per�odo de matricula
	 */
	private boolean autoRetorno = false;

	private int anoPassado;

	private int periodoPassado;

	/** o calend�rio vigente que � passado para o processador */
	private CalendarioAcademico calendario;

	public boolean isAutoRetorno() {
		return autoRetorno;
	}

	public void setAutoRetorno(boolean autoRetorno) {
		this.autoRetorno = autoRetorno;
	}

	public CalendarioAcademico getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioAcademico calendario) {
		this.calendario = calendario;
	}

	public int getAnoPassado() {
		return anoPassado;
	}

	public void setAnoPassado(int anoPassado) {
		this.anoPassado = anoPassado;
	}

	public int getPeriodoPassado() {
		return periodoPassado;
	}

	public void setPeriodoPassado(int periodoPassado) {
		this.periodoPassado = periodoPassado;
	}


}
