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
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;

/**
 * Managed bean responsável pelo cadastro, atualização das Modalidades de Educação. 
 * Quem pode ser por exemplo a distância, Presencial, Semi-Presencial.
 * 
 * @author 
 */
public class ModalidadeEducacaoMBean extends
		SigaaAbstractController<ModalidadeEducacao> {

	public ModalidadeEducacaoMBean() {
		clear();
	}

	/** Serve para inicializar o Managed Bean */
	public void clear() {
		obj = new ModalidadeEducacao();
	}
	
	/** Serve para direcionar o usuário logado para a listagem ao cadastrar uma nova modalidade de 
	 * educação.
	 * 
	 * JSP: Não invocado por jsp.
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(ModalidadeEducacao.class, "id", "descricao");
	}
	
	public boolean isSubSistemaGraduacao() {
		return getSubSistema().equals(SigaaSubsistemas.GRADUACAO);
	}
	
	@Override
	public String getFormPage() {
		return "/administracao/cadastro/ModalidadeEducacao/form.jsf";
	}
	
	@Override
	public String getListPage() {
		return "/administracao/cadastro/ModalidadeEducacao/lista.jsf";
	}

	@Override
	public Collection<ModalidadeEducacao> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}
	
	/**
	 * Serve para verificar se a modalidade de Educação informada e igual a uma já existe.
	 * 
	 * JSP: /sigaa.war/administracao/cadastro/ModalidadeEducacao/form.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<ModalidadeEducacao> mesmaModalidade = dao.findByExactField(
					ModalidadeEducacao.class, "descricao", obj.getDescricao());
		for (ModalidadeEducacao as : mesmaModalidade) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Modalidade Educação");
				return null;
			}
		}
		return super.cadastrar();
	}
}