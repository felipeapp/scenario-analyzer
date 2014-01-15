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
import br.ufrn.sigaa.ensino.dominio.NaturezaCurso;


/**
 * Managed bean responsável pelo cadastro, atualização das Naturezas do Curso. 
 * Que pode ser por exemplo: curso de Graduação, Outros, Sequencial de formação específica ou 
 * Sequencial de complementação de estudos.
 * 
 * @author 
 */
public class NaturezaCursoMBean extends AbstractControllerCadastro<NaturezaCurso> {
	
	public NaturezaCursoMBean() {
		clear();
	}

	/** Serve para inicializar o Managed Bean */
	public void clear() {
		obj = new NaturezaCurso();		
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(NaturezaCurso.class, "id", "descricao");
	}
	
	/**
	 * Para ir para a tela de listagem logo após o cadastro.
	 * 
	 * JSP: Não invocado por jsp.
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	/**
	 * Verifica se a Natureza do Curso já foi removida, caso a mesma não tenha sido removida, 
	 * o sistema removerá, mais caso já tenha sido removida o sistema exibirá uma mensagem.
	 *  
	 * JSP: /sigaa.war/administracao/cadastro/NaturezaCurso/lista.jsp
	 */
	@Override
	public String remover() throws ArqException {
		
		Integer id = getParameterInt("id", 0);
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		obj = dao.findByPrimaryKey(id, NaturezaCurso.class);

		if (obj == null) {
			addMensagemErro("A Natureza do Curso já foi removida.");
			clear();
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}

	@Override
	public Collection<NaturezaCurso> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	/**
	 * Serve para verificar se Natureza do Curso informada já não existe uma igual cadastrada.
	 * 
	 * JSP: /sigaa.war/administracao/cadastro/NaturezaCurso/form.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<NaturezaCurso> mesmaNatureza = dao.findByExactField(NaturezaCurso.class, "descricao", 
							obj.getDescricao());
		for (NaturezaCurso as : mesmaNatureza) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Natureza do Curso");
				return null;
			}
		}
		return super.cadastrar();
	} 														
	
}