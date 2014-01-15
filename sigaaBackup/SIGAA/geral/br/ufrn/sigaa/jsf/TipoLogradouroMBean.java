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
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;

public class TipoLogradouroMBean extends
SigaaAbstractController<br.ufrn.sigaa.pessoa.dominio.TipoLogradouro> {
	public TipoLogradouroMBean() {
		obj = new TipoLogradouro();
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoLogradouro.class, "id", "descricao");
	}
	
	@Override
	public Collection<TipoLogradouro> getAllPaginado() throws ArqException {
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
		
		Collection<TipoLogradouro> logradouro = getGenericDAO().findByExactField(TipoLogradouro.class, "descricao", obj.getDescricao());
		
		if (!logradouro.isEmpty()) {
		addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO,"Tipo Logradouro");
		return null;
		}
		return super.cadastrar();
	}

	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, TipoLogradouro.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	
}