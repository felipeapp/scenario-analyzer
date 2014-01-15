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
import br.ufrn.sigaa.ensino.dominio.SituacaoDiploma;

/**
 * Managed bean responsável pelo cadastro, atualização das Situações do Diploma. 
 * 
 * @author 
 */
public class SituacaoDiplomaMBean extends
		AbstractControllerCadastro<br.ufrn.sigaa.ensino.dominio.SituacaoDiploma> {
	
	public SituacaoDiplomaMBean() {
		clear();
	}
	
	public void clear(){
		obj = new SituacaoDiploma();
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(SituacaoDiploma.class, "id", "descricao");
	}

	/**
	 * Verifica se a Situação do Curso já foi removida, caso a mesma não tenha sido removida, 
	 * o sistema removerá, mais caso já tenha sido removida o sistema exibirá uma mensagem.
	 *  
	 * JSP: /sigaa.war/administracao/cadastro/SituacaoDiploma/lista.jsp
	 */
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);

		GenericDAO dao = getDAO(GenericDAOImpl.class);
		obj = dao.findByPrimaryKey(id, SituacaoDiploma.class);

		if (obj == null) {
			addMensagemErro("A situação Diploma informado já havia sido removida.");
			clear();
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}

	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	@Override
	public Collection<SituacaoDiploma> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}
	
	/**
	 * Serve para verificar se Situação do Diploma informada já não existe uma igual cadastrada.
	 * 
	 * JSP: /sigaa.war/administracao/cadastro/SituacaoDiploma/form.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<SituacaoDiploma> mesmaDiploma = dao.findByExactField(SituacaoDiploma.class, "descricao", 
							obj.getDescricao());
		for (SituacaoDiploma as : mesmaDiploma) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Situação Diploma");
				return null;
			}
		}
		return super.cadastrar();
	} 														

}