/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.TipoEtnia;

public class TipoEtniaMBean extends SigaaAbstractController<TipoEtnia> {
	public TipoEtniaMBean() {
		obj = new TipoEtnia();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoEtnia.class, "id", "descricao");
	}
	
	@Override
	public Collection<TipoEtnia> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
	
		Collection<TipoEtnia> etnia = getGenericDAO().findByExactField(TipoEtnia.class, "descricao", obj.getDescricao());
		
		if (!etnia.isEmpty()) {
		addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO,"Tipo Etnia");
		return null;
		}
		return super.cadastrar();
	}

	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, TipoEtnia.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	
}