/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas
 * Criado em: 17/12/2008
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.stricto.dominio.OrigemDiscentePos;

/** 
 * MBean para realizar as operações sobre origem de discente de pós
 * @author Victor Hugo
 */
@Component("origemDiscentePosBean") @Scope("request") 
public class OrigemDiscentePosMBean extends SigaaAbstractController<OrigemDiscentePos> {

	public OrigemDiscentePosMBean() {
		obj = new OrigemDiscentePos();
	}
	
}
