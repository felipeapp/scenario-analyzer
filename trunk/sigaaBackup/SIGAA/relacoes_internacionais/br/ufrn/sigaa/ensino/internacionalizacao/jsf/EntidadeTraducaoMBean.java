/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 09/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.EntidadeTraducao;

/**
 * Managed Bean para realizar o cadastro e gerenciamento das entidades dos objetos que serão traduzidos.
 * 
 * @author Rafael Gomes
 *
 */
@Component
@Scope("request")
public class EntidadeTraducaoMBean extends SigaaAbstractController<EntidadeTraducao>{

	public EntidadeTraducaoMBean() {
		clear();
	}
	
	/**
	 * Inicializa os campos do objeto para ser
	 * manipulado durante as operações.
	 * <br>Método não invocado por JSP´s.
	 * @throws NegocioException 
	 */
	public void clear() {
		obj = new EntidadeTraducao();
		setTamanhoPagina(20);
	}
	
	@Override
	public String getDirBase() {
		return "/relacoes_internacionais/cadastro";
	}
	
	@Override
	public String getFormPage() {
		return getDirBase() + "/form_entidade.jsf";
	}
	
	@Override
	public String getListPage() {
		return getDirBase() + "/lista_entidade.jsf";
	}
	
	/**
	 * Para que o usuário seja direcionado para a tela da listagem logo após um novo cadastro. 
	 * <br>Método não invocado por JSP´s.
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	@Override
	public String listar() throws ArqException {
		checkChangeRole();
		getPaginacao().setPaginaAtual(0);
		return super.listar();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "nome";
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return super.preCadastrar();
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		ValidatorUtil.validateRequired(obj.getNome(), "Nome", erros);
		ValidatorUtil.validateRequired(obj.getClasse(), "Classe", erros);
		if(hasErrors()) return null;
		return super.cadastrar();
	}
	
	@Override
	public String remover() throws ArqException {
		checkChangeRole();
		setOperacaoAtiva(ArqListaComando.REMOVER.getId());
		prepareMovimento(ArqListaComando.REMOVER);
		setId();
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());
		if (obj == null) {
			addMensagemErro("A entidade para tradução informada já havia sido removida.");
			clear();
		}
		return super.remover();
	}
	
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Object classe;
		try {
			if (obj.getId() == 0){
				Collection<EntidadeTraducao> mesmaEntidade = dao.findByExactField(EntidadeTraducao.class, "classe", obj.getClasse());
				for (EntidadeTraducao et : mesmaEntidade) {
					if(et.getClasse().equals(obj.getClasse()) && et.getNome().equals(obj.getNome())){
						addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Entidade para Tradução");
					
					}
				}
				if(hasErrors()) return;
			}	
			classe = ReflectionUtils.classForName(obj.getClasse()).newInstance();
			obj.setNomeClasse(classe.getClass().getSimpleName());
		} catch (InstantiationException e) {
			notifyError(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			notifyError(e);
			e.printStackTrace();
		} catch (Exception e) {
			addMensagemErro( e.getMessage() );
			e.printStackTrace();
		}	
	}
	
	@Override
	public Collection<SelectItem> getAllCombo(){
		return  getAllAtivo(EntidadeTraducao.class, "id", "nome");
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
	}

}
