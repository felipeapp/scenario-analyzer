package br.ufrn.sigaa.extensao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.TipoDesignacaoFuncaoProjeto;

@Component @Scope("request")
public class TipoDesignacaoFuncaoProjetoMBean extends SigaaAbstractController<TipoDesignacaoFuncaoProjeto> {

	public TipoDesignacaoFuncaoProjetoMBean() {
		obj = new TipoDesignacaoFuncaoProjeto();
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return super.getAllCombo();
	}
	
}