/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/10/2006
 *
 */
package br.ufrn.sigaa.extensao.jsf;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.extensao.dominio.TipoGrupoPrestServico;
/**
 *  Responsável pelo gerenciamento de Grupos de pestação de serviços.
 *  
 *  Gerado pelo CrudBuilder
 **/
@Component("tipoGrupoPrestServico")
@Scope("request")
public class TipoGrupoPrestServicoMBean extends AbstractControllerCadastro<br.ufrn.sigaa.extensao.dominio.TipoGrupoPrestServico> {
    
    public TipoGrupoPrestServicoMBean() { 
	obj = new TipoGrupoPrestServico();
    }
    
    /**
     *  Utilizado para mostrar os tipos de grupos de prestação de serviços.
     */
    public Collection<SelectItem> getAllCombo() { 
	return getAll(TipoGrupoPrestServico.class, "id", "descricao");
    }
    
	/**
	 * Inicia o cadastro de tipos de Prestação de Serviços.
	 * Chamado por:
	 * <ul><li>/sigaa.war/extensao/menu.jsp</li></ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastro() throws ArqException {
	    prepareMovimento(ArqListaComando.CADASTRAR);
	    return forward("/extensao/TipoGrupoPrestServico/form.jsp");
	}

}
