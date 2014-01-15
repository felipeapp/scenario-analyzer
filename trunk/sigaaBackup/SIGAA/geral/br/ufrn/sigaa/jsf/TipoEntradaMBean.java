/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.TipoEntrada;

public class TipoEntradaMBean extends
		AbstractControllerCadastro<br.ufrn.sigaa.ensino.dominio.TipoEntrada> {
	
	public TipoEntradaMBean() {
		clear();
	}
	
	private void clear() {
		obj = new TipoEntrada();
	}
	
	/**
	 * Para quando o usuário cadastrar imediatamente após ir para a tela da listagem.
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	/**
	 * Verifica se o objeto já foi removido, para evitar o nullPointer.
	 */
	@Override
	public String remover() throws ArqException {

		Integer id = getParameterInt("id", 0);
		GenericDAO dao = getDAO(GenericDAOImpl.class);		
		obj = dao.findByPrimaryKey(id, TipoEntrada.class);

		if (obj == null) {
			clear();
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}

	@Override
	public Collection<TipoEntrada> getAllPaginado() throws ArqException {
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
		Collection<TipoEntrada> mesmaEntrada = dao.findByExactField(TipoEntrada.class, "descricao", obj.getDescricao());
		for (TipoEntrada as : mesmaEntrada) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Tipo de Entrada");
				return null;
			}
		}
		return super.cadastrar();
	}
	
}