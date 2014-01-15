package br.ufrn.sigaa.ouvidoria.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ouvidoria.dao.AssuntoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.AssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;
/**
 * MBean responsável pela manutenção dos Assuntos
 * 
 * @author sueltom
 *
 */
@Component(value="assuntoManifestacao") @Scope(value="request")
public class AssuntoManifestacaoMBean  extends SigaaAbstractController<AssuntoManifestacao> {

	/**
	 *Lista de assuntos. 
	 */
	private Collection<AssuntoManifestacao> assuntos;
	
	/**
	 * Lista de assuntos de uma determinada categoria. 
	 */
	private Collection<AssuntoManifestacao> assuntosPorCategoria;
	
	/**
	 * Contrutor padrao
	 */
	public AssuntoManifestacaoMBean() {
    	init();
    }
    
	/**
     * Inicia os dados da {@link AssuntoManifestacao}
     */
    private void init() {
    	obj = new AssuntoManifestacao();
    	obj.setCategoriaAssuntoManifestacao(new CategoriaAssuntoManifestacao());
    }
    
    /**
     * Inicializa o objeto e redireciona para a pagina de cadastro.
     * @return o caminho para a jsp de cadastro
     * Metodo chamado pela seguinte JSP:
     * <ul>
	 * <li> sigaa.war/ouvidoria/menu.jsp</li>
	 * </ul>
     */
	public String iniciarCadastro()  throws ArqException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		setConfirmButton("Cadastrar");
		init();
    	return forward(getDirBase() + "/form.jsp");
    }
    
    /**
     *Retorna o caminho para o diretorio base. 
     */
    @Override
    public String getDirBase() {
    	return super.getDirBase() + "/ouvidor";
    }

    /**
     * Carrega a lista de Assuntos e redireciona para a jsp que mostra esta lista.
     * <ul>
	 * <li> sigaa.war/ouvidoria/menu.jsp</li>
	 * </ul>
     */
    public String listar() throws HibernateException, DAOException {
    	popularAssuntoManifestacao();
    	
    	if(isEmpty(assuntos)) {
    	    addMensagemWarning("Não existem manifestações pendentes cadastradas no sistema.");
    	    redirectMesmaPagina();
    	}
    	
    	return forward(getDirBase() + "/lista.jsp");
    }
    
    /**
	 * Inicializa assuntos. 
	 * @throws DAOException
	 */
    private void popularAssuntoManifestacao() throws DAOException {
    	AssuntoManifestacaoDao dao = getDAO(AssuntoManifestacaoDao.class);
    	assuntos = new ArrayList<AssuntoManifestacao>();
    	
    	try {
    		assuntos = dao.getAllAssuntos();
    	} finally {
    	    dao.close();
    	}
    }
    
    /**
     * Cadastra o assunto da manifestacao e redireciona para o menu do portal atual.
     * Metodo chamado pela seguinte JSP:
     * <ul>
	 * <li> sigaa.war/ouvidoria/AssuntoManifestacao/ouvidor/form.jsp</li>
	 * </ul>
     */
    @Override
    public String cadastrar() throws ArqException {
    	
    	if(isEmpty(obj.getCategoriaAssuntoManifestacao()))
    		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Assunto");
    	
    	if(isEmpty(obj.getDescricao()))
    		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Categoria");
    	
    	if (obj.getId() > 0)
        	assuntosPorCategoria.remove(obj);
        	
        if (assuntosPorCategoria.contains(obj))
           	addMensagemErro("Esse assunto já existe na categoria selecionada.");
    	
    	
    	if (hasErrors())
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
    	
		
		if (mov.getCodMovimento() == ArqListaComando.ALTERAR)
			return listar();
		else {
			listar();
			return iniciarCadastro();
		}
	}

    /**
     * Prepara para uma atualizacao. 
     * <ul>
	 * <li> sigaa.war/ouvidoria/AssuntoManifestacao/ouvidor/lista.jsp</li>
	 * </ul> 
     */
    public String preAtualizar() throws ArqException {
    	prepareMovimento(ArqListaComando.ALTERAR);
    	setConfirmButton("Alterar");
    	int id = getParameterInt("id", 0);
     	
    	if (id != 0) 
			obj = getGenericDAO().findByPrimaryKey(id, AssuntoManifestacao.class);
    	    	
    	    	return forward(getDirBase() + "/form.jsp");
    }
        
    /**
     * Ativa ou inativa uma Assunto de acordo com seu estado atual.
     * <ul>
	 * <li> sigaa.war/ouvidoria/AssuntoManifestacao/ouvidor/listar.jsp</li>
	 * </ul> 
     */
    public String inativarOuAtivar() throws ArqException {
    	prepareMovimento(ArqListaComando.ALTERAR);
    	int id = getParameterInt("id", 0);
     	
	    	if (id != 0) { 
				obj = getGenericDAO().findByPrimaryKey(id, AssuntoManifestacao.class);
	    	
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
	    	
	    	addMensagemErro("O Elemento Selecionado não Existe.");
	    	return cancelar();
    }

    
    /**
     * Remove o AssuntoManifestacao manifestacao selecionado.
     * * <ul>
	 * <li> sigaa.war/ouvidoria/AssuntoManifestacao/ouvidor/listar.jsp</li>
	 * </ul> 
     */
    @Override
	public String remover() throws ArqException {
    	prepareMovimento(ArqListaComando.REMOVER);
    	int id = getParameterInt("id", 0);
    	
		
		if (id != 0) 
			obj = getGenericDAO().findByPrimaryKey(id, AssuntoManifestacao.class);
		
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

    /**
     * Pegar todos os assuntos cadastrados
     * @return Collection<AssuntoManifestacao>
     * @throws DAOException
     */
	public Collection<AssuntoManifestacao> getAssuntos() throws DAOException {
		if (isEmpty(assuntos)) {
			popularAssuntoManifestacao();
		}
		return assuntos;
	}
	
	/**
	 * Pega todas os assuntos cadastrados que pertencem a uma determinada categoria.
	 * @return Collection<AssuntoManifestacao>
	 * @throws DAOException
	 */
	public Collection<AssuntoManifestacao> getAssuntosPorCategoria() throws DAOException {
		
		assuntosPorCategoria = new ArrayList<AssuntoManifestacao>();
		if (isEmpty(assuntos)) {
			popularAssuntoManifestacao();
		}
				
		for(AssuntoManifestacao assunto : assuntos ) {
			if (assunto.getCategoriaAssuntoManifestacao().getId() == obj.getCategoriaAssuntoManifestacao().getId() ) {
				assuntosPorCategoria.add(assunto);
			}
		}
				
		return assuntosPorCategoria;
	} 

	public void setAssuntos(Collection<AssuntoManifestacao> assuntos) {
		this.assuntos = assuntos;
	}
	   
}
