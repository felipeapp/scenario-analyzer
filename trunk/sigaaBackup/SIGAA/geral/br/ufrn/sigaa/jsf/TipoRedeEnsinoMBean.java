/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
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
 * Managed bean respons�vel pelo cadastro, atualiza��o dos Tipos de Rede de Ensino. 
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
	 * Verifica se o Tipo de Rede de Ensino j� foi removida, caso a mesma n�o tenha sido removida, 
	 * o sistema remover�, mais caso j� tenha sido removida o sistema exibir� uma mensagem.
	 *  
	 * JSP: /sigaa.war/administracao/cadastro/TipoRedeEnsino/lista.jsp
	 */
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);

		GenericDAO dao = getDAO(GenericDAOImpl.class);
		obj = dao.findByPrimaryKey(id, TipoRedeEnsino.class);

		if (obj == null) {
			addMensagemErro("A situa��o Diploma informado j� havia sido removida.");
			clear();
		}
		return super.remover();
	}

	/**
	 * Para que o usu�rio seja direcionado para a tela da listagem logo ap�s um novo cadastro. 
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
	 * Serve para verificar se o Tipo de Rede se Ensino informada j� n�o existe uma igual cadastrada.
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