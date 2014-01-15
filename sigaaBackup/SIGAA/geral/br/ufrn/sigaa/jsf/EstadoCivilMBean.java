/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;

public class EstadoCivilMBean extends
		SigaaAbstractController<br.ufrn.sigaa.pessoa.dominio.EstadoCivil> {
	public EstadoCivilMBean() {
		obj = new EstadoCivil();
	}

	public Collection<SelectItem> getAllCombo() {
		Collection<SelectItem> itens = getAll(EstadoCivil.class, "id",
				"descricao");
		// removendo o item [-1, "Não informado"]
		for (SelectItem item : itens) {
			if (item.getValue().equals("-1")) {
				itens.remove(item);
				break;
			}
		}
		return itens;
	}
	
	@Override
	public Collection<EstadoCivil> getAllPaginado() throws ArqException {
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
	
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<EstadoCivil> mesmoEstado = dao.findByExactField(EstadoCivil.class, "descricao", obj.getDescricao());
		for (EstadoCivil as : mesmoEstado) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Estado Civil");
				return null;
			}
		}
		return super.cadastrar();
	}
	
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, EstadoCivil.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}

}