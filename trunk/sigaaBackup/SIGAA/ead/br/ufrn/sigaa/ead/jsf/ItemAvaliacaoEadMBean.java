
package br.ufrn.sigaa.ead.jsf;

import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ead.dominio.ItemAvaliacaoEad;

/**
 * Managed bean para cadastro de itens de avaliação para a
 * avaliação de discentes de ensino a distância
 * @author David Pereira
 *
 */
@Component("itemAvaliacaoEad") @Scope("request")
public class ItemAvaliacaoEadMBean extends SigaaAbstractController<ItemAvaliacaoEad> {

	public ItemAvaliacaoEadMBean() {
		obj = new ItemAvaliacaoEad();
		obj.setAtivo(true);
	}

	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {
		obj.setData(new Date());
		obj.setUsuario(getUsuarioLogado());
	}

	@Override
	public String forwardCadastrar() {
		return forward("/ead/ItemAvaliacaoEad/lista.jsp");
	}


}
