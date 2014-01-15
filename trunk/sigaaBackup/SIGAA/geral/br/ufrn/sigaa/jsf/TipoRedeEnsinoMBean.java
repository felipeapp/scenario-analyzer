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
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.TipoRedeEnsino;

/**
 * Managed bean responsável pelo cadastro, atualização dos Tipos de Rede de Ensino. 
 *
 * @author 
 */
public class TipoRedeEnsinoMBean extends SigaaAbstractController<TipoRedeEnsino> {
	
	public TipoRedeEnsinoMBean() {
		clear();
	}

	/** Serve para inicializar o Managed Bean */
	public void clear(){
		obj = new TipoRedeEnsino();
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoRedeEnsino.class, "id", "descricao");
	}
	
	/**
	 * Verifica se o Tipo de Rede de Ensino já foi removida, caso a mesma não tenha sido removida, 
	 * o sistema removerá, mais caso já tenha sido removida o sistema exibirá uma mensagem.
	 *  
	 * JSP: /sigaa.war/administracao/cadastro/TipoRedeEnsino/lista.jsp
	 */
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);

		GenericDAO dao = getDAO(GenericDAOImpl.class);
		obj = dao.findByPrimaryKey(id, TipoRedeEnsino.class);

		if (obj == null) {
			addMensagemErro("A situação Diploma informado já havia sido removida.");
			clear();
		}
		return super.remover();
	}

	/**
	 * Para que o usuário seja direcionado para a tela da listagem logo após um novo cadastro. 
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	@Override
	public Collection<TipoRedeEnsino> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	/**
	 * Serve para verificar se o Tipo de Rede se Ensino informada já não existe uma igual cadastrada.
	 * 
	 * JSP: /sigaa.war/administracao/cadastro/TipoRedeEnsino/form.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
	
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<TipoRedeEnsino> mesmaRedeEnsino = dao.findByExactField(TipoRedeEnsino.class, "descricao", obj.getDescricao());
		for (TipoRedeEnsino as : mesmaRedeEnsino) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Tipo Rede de Ensino");
				return null;
			}
		}
		return super.cadastrar();
	}
	
}