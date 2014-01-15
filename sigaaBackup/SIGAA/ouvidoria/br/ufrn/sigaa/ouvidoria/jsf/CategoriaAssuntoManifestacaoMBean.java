package br.ufrn.sigaa.ouvidoria.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ouvidoria.dao.CategoriaAssuntoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;

/**
 * MBean responsável pela manutenção das Categorias dos Assuntos   
 * 
 * @author Bernardo
 */
@Component(value="categoriaAssuntoManifestacao") @Scope(value="request")
public class CategoriaAssuntoManifestacaoMBean extends SigaaAbstractController<CategoriaAssuntoManifestacao> {

	/**
	 * Lista das categorias.
	 */
	private Collection<CategoriaAssuntoManifestacao> categorias;
	
	/**
	 * Contrutor Padrao
	 */
	public CategoriaAssuntoManifestacaoMBean() {
    	init();
    }
   
	/**
     *Retorna o caminho para o diretorio base. 
     */   
    @Override
    public String getDirBase() {
    	return super.getDirBase() + "/ouvidor";
    }
	
	/**
     * Inicia os dados da {@link CategoriaAssuntoManifestacao}
     */
    private void init() {
    	obj = new CategoriaAssuntoManifestacao();
    }
	
    /**
     * Inicializa o objeto e redireciona para a pagina de cadastro.
     * @return o caminho para a jsp de cadastro
	 * Metodo chamado pela seguinte JSP:
     * <ul>
	 * <li> sigaa.war/ouvidoria/menu.jsp</li>
	 * </ul>
     */
	public String iniciarCadastro() throws ArqException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		init();
     	return forward(getDirBase() + "/form.jsp");
    }
	
    /**
     * Cadastra a categoria do assunto da manifestacao e redireciona para o menu do portal atual.
     * Metodo chamado pela seguinte JSP:
     * <ul>
	 * <li> sigaa.war/ouvidoria/CategoriaAssuntoManifestacao/ouvidor/cadastrar.jsp</li>
	 * </ul>
     */        
    @Override
    public String cadastrar() throws ArqException {
    	
    	if(isEmpty(obj.getDescricao()))
    		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Categoria do Assunto");

    	popularCategoriaAssuntoManifestacao();
    	    	
    	if (obj.getId() > 0)
    	categorias.remove(obj);
    	
    	if (categorias.contains(obj))
        	addMensagemErro("Essa categoria já existe.");
   
    	   	
    	if(hasErrors())
    		return null;
    	
    	
    	MovimentoCadastro mov = new MovimentoCadastro();
    	mov.setObjMovimentado(obj);
    	
    	if (obj.getId() > 0)
    		mov.setCodMovimento(ArqListaComando.ALTERAR);
    	else
    		mov.setCodMovimento(ArqListaComando.CADASTRAR);
    	
    	try {
			execute(mov);
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		
		if (!hasErrors())
			addMensagem(OPERACAO_SUCESSO);    	
		
		if (mov.getCodMovimento() == ArqListaComando.ALTERAR) {
			return listar();
		}
		else {
			listar();
			return iniciarCadastro();
		}
    }
    
    /**
     * Carrega a lista de Categorias e redireciona para a jsp que mostra esta lista.
     * <ul>
	 * <li> sigaa.war/ouvidoria/menu.jsp</li>
	 * </ul>
     */
    public String listar() throws HibernateException, DAOException {
    	popularCategoriaAssuntoManifestacao();
    	
    	if(isEmpty(categorias)) {
    	    addMensagemWarning("Não existem manifestações pendentes cadastradas no sistema.");
    	    redirectMesmaPagina();
    	}
    	
    	
    	return forward(getDirBase() + "/lista.jsp");
    }

    /**
	 * Pega todas as categorias.
	 * @return Collection<CategoriaAssuntoManifestacao>
	 * @throws DAOException
	 */
    public Collection<CategoriaAssuntoManifestacao> getCategorias() throws DAOException {
    	if (isEmpty(categorias))
    		popularCategoriaAssuntoManifestacao();
		return categorias;
	}

	public void setCategorias(Collection<CategoriaAssuntoManifestacao> categorias) {
		this.categorias = categorias;
	}

	/**
	 * Inicializa categorias. 
	 * @throws DAOException
	 */
	private void popularCategoriaAssuntoManifestacao() throws DAOException {
    	CategoriaAssuntoManifestacaoDao dao = getDAO(CategoriaAssuntoManifestacaoDao.class);
    	categorias = new ArrayList<CategoriaAssuntoManifestacao>();
    	
    	try {
    		categorias = dao.getAllCategorias();
    	} finally {
    	    dao.close();
    	}
    	 	
    }
    
    /**
     *  Pega Todas as Categorias ativas.  
     * @return Collection<CategoriaAssuntoManifestacao>
     * @throws DAOException
     */
    public Collection<CategoriaAssuntoManifestacao> getAllCategoriasAtivas() throws DAOException {
		CategoriaAssuntoManifestacaoDao dao = getDAO(CategoriaAssuntoManifestacaoDao.class);
		Collection<CategoriaAssuntoManifestacao> categorias = new ArrayList<CategoriaAssuntoManifestacao>();
		
		try {
		    categorias = dao.getAllCategoriasAtivas();
		} finally {
		    dao.close();
		}
		
		return categorias;
    }
    
    /**
     * Monta combo de categorias de assunto de manifestações. <br/><br/>
     * 
     * @return Collection<SelectItem>
     * @throws DAOException
     */
    public Collection<SelectItem> getAllCategoriasAtivasCombo() throws DAOException {
    	return toSelectItems(getAllCategoriasAtivas(), "id", "descricao");
    }
    
    /**
     * Pega Todas as Categorias ativas por unidade.  
     * 
     * @return Collection<CategoriaAssuntoManifestacao>
     * @throws DAOException
     */
    public Collection<CategoriaAssuntoManifestacao> getAllCategoriasByUnidade() throws DAOException {
		CategoriaAssuntoManifestacaoDao dao = getDAO(CategoriaAssuntoManifestacaoDao.class);
		Collection<CategoriaAssuntoManifestacao> categorias = new ArrayList<CategoriaAssuntoManifestacao>();
		
		try {
		    UnidadeGeral unidadeResponsavel = getUsuarioLogado().getVinculoAtivo().getUnidadeResponsavel();
			Unidade u = isEmpty(unidadeResponsavel) ? getUsuarioLogado().getUnidade() : new Unidade(unidadeResponsavel.getId());
			categorias = dao.getAllCategoriasByUnidade(u);
		} finally {
		    dao.close();
		}
		
		return categorias;
    }
    
    /**
     * Monta combo de todas as categorias por unidade.
     * @return Collection<SelectItem>
     * @throws DAOException
     */
    public Collection<SelectItem> getAllCategoriasByUnidadeCombo() throws DAOException {
    	return toSelectItems(getAllCategoriasByUnidade(), "id", "descricao");
    }
    
    /**
     * Pega Todas as Categorias ativas por unidade.  
     * 
     * @return Collection<CategoriaAssuntoManifestacao>
     * @throws DAOException
     */
    public Collection<CategoriaAssuntoManifestacao> getAllCategoriasByDesignado() throws DAOException {
		CategoriaAssuntoManifestacaoDao dao = getDAO(CategoriaAssuntoManifestacaoDao.class);
		Collection<CategoriaAssuntoManifestacao> categorias = new ArrayList<CategoriaAssuntoManifestacao>();
		
		try {
			categorias = dao.getAllCategoriasByDesignado(getUsuarioLogado().getPessoa());
		} finally {
		    dao.close();
		}
		
		return categorias;
    }
    
    /**
     * Monta combo de todas as categorias por unidade.
     * @return Collection<SelectItem>
     * @throws DAOException
     */
    public Collection<SelectItem> getAllCategoriasByDesignadoCombo() throws DAOException {
    	return toSelectItems(getAllCategoriasByDesignado(), "id", "descricao");
    }
    
    /**
     * Prepara para uma atualizacao. 
     * <ul>
	 * <li> sigaa.war/ouvidoria/CategoriaAssuntoManifestacao/ouvidor/lista.jsp</li>
	 * </ul> 
     */
    public String preAtualizar() throws ArqException {
    	setConfirmButton("Alterar");
    	prepareMovimento(ArqListaComando.ALTERAR);
    	    	
    	int id = getParameterInt("id", 0);
     	
    	if (id != 0) 
			obj = getGenericDAO().findByPrimaryKey(id, CategoriaAssuntoManifestacao.class);
    	    	
    	
    	return forward(getDirBase() + "/form.jsp");
    }
       
    /**
     * Ativa ou inativa uma Categoria de acordo com seu estado atual.
     * <ul>
	 * <li> sigaa.war/ouvidoria/CategoriaAssuntoManifestacao/ouvidor/listar.jsp</li>
	 * </ul> 
     */
    public String inativarOuAtivar() throws ArqException {
    	prepareMovimento(ArqListaComando.ALTERAR);
    	int id = getParameterInt("id", 0);
     	
	    	if (id != 0) { 
				obj = getGenericDAO().findByPrimaryKey(id, CategoriaAssuntoManifestacao.class);
	    	
		    	MovimentoCadastro mov = new MovimentoCadastro();
		    	mov.setObjMovimentado(obj);
		    	mov.setCodMovimento(ArqListaComando.ALTERAR);
		    	
		    	if (obj.isAtivo())
		    		obj.setAtivo(false);
		    	else
		    		obj.setAtivo(true);
		    	
		    	
		    	try {
					execute(mov);
				} catch (NegocioException e) {
					tratamentoErroPadrao(e);
				}	
				
					if (!hasErrors())
					addMensagem(OPERACAO_SUCESSO);
		        	
		        	return listar();
		    	   	
	    	}
	    	addMensagemErro("O elemento Selecionado não Existe.");
	    	return cancelar();
    }
    
    /**
     * Remove uma CategoriaAssuntoManifestacao que não possua nenhum Assunto associado.
     * <ul>
	 * <li> sigaa.war/ouvidoria/CategoriaAssuntoManifestacao/ouvidor/listar.jsp</li>
	 * </ul> 
     */
    @Override
	public String remover() throws ArqException {
    	prepareMovimento(ArqListaComando.REMOVER);
    	int id = getParameterInt("id", 0);
		
		if (id != 0) 
			obj = getGenericDAO().findByPrimaryKey(id, CategoriaAssuntoManifestacao.class);
		
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return forward(getListPage());
		}
		
		
		MovimentoCadastro mov = new MovimentoCadastro();
    	mov.setObjMovimentado(obj);
    	mov.setCodMovimento(ArqListaComando.REMOVER);
		
    	try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		if (!hasErrors())
		addMensagem(OPERACAO_SUCESSO);	
		
		return listar();
	}
    
}
