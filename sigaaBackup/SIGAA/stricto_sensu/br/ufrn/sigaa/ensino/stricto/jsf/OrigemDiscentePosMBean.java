/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA - Sistema Integrado de Gest�o de Atividades Acad�micas
 * Criado em: 17/12/2008
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.stricto.dominio.OrigemDiscentePos;

/** 
 * MBean para realizar as opera��es sobre origem de discente de p�s
 * @author Victor Hugo
 */
@Component("origemDiscentePosBean") @Scope("request") 
public class OrigemDiscentePosMBean extends SigaaAbstractController<OrigemDiscentePos> {

	public OrigemDiscentePosMBean() {
		obj = new OrigemDiscentePos();
	}
	
}
