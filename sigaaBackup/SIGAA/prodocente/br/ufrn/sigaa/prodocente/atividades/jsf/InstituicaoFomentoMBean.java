/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.prodocente.atividades.dominio.InstituicaoFomento;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;


/**
 * MBean responsável por carregar as informações das páginas que mostram as informações 
 * sobre afastamento.
 * 
 * @author Gleydson
 */
@Scope("request")
@Component("instituicaoFomento")
public class InstituicaoFomentoMBean extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.atividades.dominio.InstituicaoFomento> {

	
	public InstituicaoFomentoMBean() {
		obj = new InstituicaoFomento();
	}

	/**
	 * Retorna os tipos de InstituicaoFomento existentes na base de dados.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/producao/BolsaObtida/form.jsp</li>
	 * </ul>
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(InstituicaoFomento.class, "id", "nome");
	}
}
