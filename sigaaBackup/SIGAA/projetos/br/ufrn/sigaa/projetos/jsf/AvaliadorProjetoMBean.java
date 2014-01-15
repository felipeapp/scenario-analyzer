package br.ufrn.sigaa.projetos.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import javax.faces.component.html.HtmlDataTable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.AvaliadorProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.projetos.dominio.AvaliadorProjeto;

/**
 * MBean que gerencia um Avaliador de um Projeto
 *
 */
@Component("avaliadorProjetoMbean") 
@Scope("session")
public class AvaliadorProjetoMBean extends SigaaAbstractController<AvaliadorProjeto>  {

	/** Atributo utilizado para armazenar os avaliadores */
    private HtmlDataTable avaliadores = new HtmlDataTable();
    
    public AvaliadorProjetoMBean() {
	obj = new AvaliadorProjeto();
    }

    @Override
    public String preCadastrar() throws ArqException, NegocioException {
    	resetBean();
    	obj = new AvaliadorProjeto();
    	return super.preCadastrar();
    }

    @Override
    public String getDirBase() {     
        return "/projetos/Avaliacoes/Avaliadores";
    }
    
    /**
     * Lista todos os avaliadores ativos cadastrados. <br/>
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	  <li>sigaa.war/projetos/menu.jsp</li>
     * </ul>
     * @throws ArqException 
     * 
     */
    @Override
    public String listar() throws ArqException {
    	checkChangeRole();
    	resultadosBusca = getDAO(AvaliadorProjetoDao.class).findByAreaConhecimento(null);
    	return forward(ConstantesNavegacaoProjetos.CADASTRO_AVALIADOR_PROJETOS_LISTA);
    }

    
    /**
     * Remove um avaliadore já cadastrado. <br/>
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/projetos/Avaliacoes/Avaliadores/lista.jsp</li>
     * </ul>
     * @throws ArqException 
     * 
     */
    @Override
    public String inativar() throws ArqException, NegocioException {
    	
    	int id = getParameterInt("id", 0);
		Integer idInt = new Integer(id);
		obj.setId(idInt);
    	//obj = (AvaliadorProjeto) getAvaliadores().getRowData();
    	
    	// Verifica se o avaliador já não foi excluido
    	GenericDAO dao = getGenericDAO();
    	obj = dao.findByPrimaryKey(obj.getId(), AvaliadorProjeto.class);
    	if(obj == null || !obj.getAtivo()){
    		addMensagemErro("Este avaliador não está ativo no sistema. Escolha um avaliador ativo.");
    		return listar();
    	}
    	
    	prepareMovimento(ArqListaComando.DESATIVAR);
    	MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);
    	try {
    		execute(mov);
    		addMensagem(OPERACAO_SUCESSO);
    		return listar();
    	} catch (NegocioException e) {
    		addMensagens(e.getListaMensagens());
    		return forward(getFormPage());
    	} catch (Exception e) {
    		tratamentoErroPadrao(e);
    		return forward(getFormPage());
    	}
    }
    
    /**
     * Método que redireciona para página de listagem após cadastrar
     * 
     * Método não é chamado por JSP(s).
     */
    @Override
    public String forwardCadastrar() {
    	try {
    		resultadosBusca = getDAO(AvaliadorProjetoDao.class).findByAreaConhecimento(null);
    		return ConstantesNavegacaoProjetos.CADASTRO_AVALIADOR_PROJETOS_LISTA;
    	} catch (DAOException e) {
    		tratamentoErroPadrao(e);
    		return null;
    	}
    }
    
    /**
     * Método que realiza validação da 2ª Área de Conhecimento antes de validar o Objeto todo.
     * 
     * Método não é chamado por JSP(s).
     */
    @Override
    public void beforeCadastrarAndValidate() throws NegocioException,	SegurancaException, DAOException {
    	if (ValidatorUtil.isEmpty(obj.getAreaConhecimento2())){
    		obj.setAreaConhecimento2(null);
    	}
    	super.beforeCadastrarAndValidate();
    }
    
    @Override
    protected void doValidate() throws ArqException {
    	AvaliadorProjetoDao dao = getDAO(AvaliadorProjetoDao.class);
		if ((obj.getId() == 0)
				&& (dao.isAvaliadorCadastrado(obj.getUsuario().getId()))) {
			addMensagemErro("Usuário(a) já cadastrado(a) como avaliador(a).");
		}
    	super.doValidate();
    }
    
    /**
     * Seleciona o avaliador e exibe o formulário para alteração do mesmo. <br/>
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/projetos/Avaliacoes/Avaliadores/lista.jsp</li>
     * </ul>
     * @throws ArqException 
     * 
     */
    public String alterar() throws ArqException {
    	checkChangeRole();
    	obj = (AvaliadorProjeto) getAvaliadores().getRowData();
    	obj = getGenericDAO().findByPrimaryKey(obj.getId(), AvaliadorProjeto.class);

    	if (ValidatorUtil.isEmpty(obj.getAreaConhecimento1())){
    		obj.setAreaConhecimento1(new AreaConhecimentoCnpq());
    	}

    	if (ValidatorUtil.isEmpty(obj.getAreaConhecimento2())){
    		obj.setAreaConhecimento2(new AreaConhecimentoCnpq());
    	}

    	setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
    	prepareMovimento(ArqListaComando.ALTERAR);
    	setReadOnly(false);
    	setConfirmButton("Alterar");
    	return forward(ConstantesNavegacaoProjetos.CADASTRO_AVALIADOR_PROJETOS_FORM);	
    }
    
    @Override
    public void checkChangeRole() throws SegurancaException {
    	checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_PESQUISA);
        super.checkChangeRole();
    }


    public HtmlDataTable getAvaliadores() {
        return avaliadores;
    }

    public void setAvaliadores(HtmlDataTable avaliadores) {
        this.avaliadores = avaliadores;
    }

}
