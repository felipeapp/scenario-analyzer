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

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeLetivo;

public class TipoRegimeLetivoMBean extends AbstractControllerCadastro<TipoRegimeLetivo> {
	
	public TipoRegimeLetivoMBean() {
		clear();
	}
	
	private void clear() {
		obj = new TipoRegimeLetivo();
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoRegimeLetivo.class, "id", "descricao");
	}
	
	/**
	 * Para redirecionar o usuário para a tela da listagem logo após uma nova inserção.
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	
	/**
	 * Verifica se o objeto já foi removido, para evitar o nullPointer
	 */
	@Override
	public String remover() throws ArqException {

		Integer id = getParameterInt("id", 0);
		GenericDAO dao = getDAO(GenericDAOImpl.class);		
		obj = dao.findByPrimaryKey(id, TipoRegimeLetivo.class);

		if (obj == null) {
			clear();
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}

	@Override
	public Collection<TipoRegimeLetivo> getAllPaginado() throws ArqException {
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
		Collection<TipoRegimeLetivo> mesmaRegimeLetivo = dao.findByExactField(TipoRegimeLetivo.class, "descricao", obj.getDescricao());
		for (TipoRegimeLetivo as : mesmaRegimeLetivo) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Tipo Regime Letivo");
				return null;
			}
		}
		return super.cadastrar();
	}
	
}