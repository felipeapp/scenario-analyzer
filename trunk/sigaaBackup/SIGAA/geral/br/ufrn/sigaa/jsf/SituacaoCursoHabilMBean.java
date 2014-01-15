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
import br.ufrn.sigaa.ensino.dominio.SituacaoCursoHabil;

/**
 * Managed Bean responsável pelo cadastro, atualização das Situações dos Cursos Hábeis, descreve a situação na qual o curso se 
 * encontra no momento, se está Paralisado(a), Em Extinção ou Não foi Informado. 
 * 
 * @author 
 */
public class SituacaoCursoHabilMBean extends AbstractControllerCadastro<SituacaoCursoHabil> {
	
	public SituacaoCursoHabilMBean() {
		clear();
	}

	/** Serve para inicializar o Managed Bean */
	public void clear(){
		obj = new SituacaoCursoHabil();	
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(SituacaoCursoHabil.class, "id", "descricao");
	}
	
	
	/**
	 * Verifica se a Situação do Curso já foi removida, caso a mesma não tenha sido removida, 
	 * o sistema removerá, mais caso já tenha sido removida o sistema exibirá uma mensagem.
	 *  
	 * JSP: /sigaa.war/administracao/cadastro/SituacaoCursoHabil/lista.jsp
	 */
	@Override
	public String remover() throws ArqException {

		Integer id = getParameterInt("id", 0);
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		obj = dao.findByPrimaryKey(id, SituacaoCursoHabil.class);

		if (obj == null) {
			addMensagemErro("A Situação Curso Habil válida já havia sido removida.");
			clear();
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}

	/**
	 * Para quando o usuário cadastrar ir diretamente para a tela da listagem.
	 * 
	 * JSP: Não invocado por jsp.
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	@Override
	public Collection<SituacaoCursoHabil> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	/**
	 * Serve para verificar se a Situação do Curso informada já não existe uma igual cadastrada.
	 * 
	 * JSP: /sigaa.war/administracao/cadastro/SituacaoCursoHabil/form.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<SituacaoCursoHabil> mesmaSituacao = dao.findByExactField(SituacaoCursoHabil.class, "descricao", 
							obj.getDescricao());
		for (SituacaoCursoHabil as : mesmaSituacao) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Situação do Curso");
				return null;
			}
		}
		return super.cadastrar();
	} 														

}