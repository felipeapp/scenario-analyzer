/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA - Sistema Integrado de Gest�o de Atividades Acad�micas
 * Criado em: 17/03/2009
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.dominio.AbstractMovimento;

/** 
 * Movimento utilizado para realizar o recalculo dos totais dos curr�culos
 * @author Victor Hugo
 */
public class MovimentoRecalculoEstruturaCurricular extends AbstractMovimento{

	private int id;
	
	/**
	 * este atributo indica se deve setar a data da ultima atualiza��o dos 
	 * totais dos discente vinculados as estruturas que foram recalculadas.
	 * Ser� recalculado os discentes no caso de a estrutura ser re-calculada devido a 
	 * altera��o de carga hor�ria de componentes curriculares
	 */
	private boolean recalcularDiscentes = false; 
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isRecalcularDiscentes() {
		return recalcularDiscentes;
	}

	public void setRecalcularDiscentes(boolean recalcularDiscentes) {
		this.recalcularDiscentes = recalcularDiscentes;
	}

}
