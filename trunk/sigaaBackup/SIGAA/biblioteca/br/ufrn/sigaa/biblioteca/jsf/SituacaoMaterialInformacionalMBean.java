/*
 * SituacaoMaterialInformacionalMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.negocio.MovimentoRemoveSituacao;

/**
 *
 *    <p>MBean do crud de situa��es dos materiais informacionais.</p>
 *    
 *
 * @author jadson
 * @see SituacaoMaterialInformacional
 * @since 27/08/2009
 * 
 * @version 1.0 criacao da classe
 * @version 2.0 adicionado a possiblidade do usu�rio pode cadastrar novas situa��es, editar e remover as j� existentes. 
 * Al�m de adicionar a informa��o se a situa��o ser� vis�vel pelo usu�rio.
 */
@Component("situacaoMaterialInformacionalMBean")
@Scope("request")
public class SituacaoMaterialInformacionalMBean  extends SigaaAbstractController <SituacaoMaterialInformacional>{

	/**
	 * P�gina para confirmar a remo��o do Situa��o, e passar os materiais desse Situa��o para algum Situa��o que ficou ativo.
	 */
	public static final String PAGINA_CONFIRMA_REMOCAO_SITUACOES = "/biblioteca/SituacaoMaterialInformacional/confirmaRemocaoSituacao.jsp";
	
	
	/** As situa��es que o usu�rio vai escolher para migrar os materiais para a situa��o que ser� removida. 
	 *  Guarda a lista de todas as situa��es ativas para n�o ficar buscando v�rias vezes. */
	private List<SituacaoMaterialInformacional> situacoesAtivas;
	
	/**
	 * O Situa��o que vai substituir a situa��o removido, nos materiais que possuem o Situa��o removido
	 */
	private SituacaoMaterialInformacional novaSituacao = new SituacaoMaterialInformacional(-1);
	
	
	/**
	 * Construtor padr�o, tem que inicializar o objeto sen�o dar erro na arquitetura
	 */
	public SituacaoMaterialInformacionalMBean(){
		obj = new SituacaoMaterialInformacional();
	}
	
	
	/**
	 * Metodo que redireciona para a p�gina onde o usu�rio vai confirmar remo��o do Situa��o e escolher o novo Situa��o 
	 * que os materiais do antigo Situa��o v�o possuir.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/SituacaoMaterialInformacional/lista.jsp
	 * @throws SegurancaException 
	 * @throws SegurancaException 
	 */
	@Override
	public String preRemover()   {
		
		SituacaoMaterialInformacionalDao dao = null;
		
		try {
		
			dao = getDAO(SituacaoMaterialInformacionalDao.class);
			
			prepareMovimento(SigaaListaComando.REMOVER_SITUACAO);
			
			situacoesAtivas = dao.findAllSituacoesAtivas();
			
			populateObj(true); // busca o objeto com o id passado com par�metro
			
			return forward(PAGINA_CONFIRMA_REMOCAO_SITUACOES);
			
		} catch (DAOException e) {
			addMensagemErro("Erros ao buscar as situa��o ativos no sistema");
			return null;
		} catch (ArqException e) {
			addMensagemErro("Erros ao tentar remover a Situa��o");
			return null;
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	/**
	 * Metodo que remove o objeto, verificando se o mesmo existe
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/SituacaoMaterialInformacional/confirmaRemocaoSituacao.jsp
	 */
	@Override
	public String remover() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);

		GenericDAO dao = null; 
		

		try {
			
			dao = getGenericDAO();
			
			// Se o objeto a remover foi encontrado, desativa o mesmo
			if (obj != null && obj.isAtivo()){
				
				try{
		
					MovimentoRemoveSituacao mov = new MovimentoRemoveSituacao(obj, novaSituacao);
					mov.setCodMovimento(SigaaListaComando.REMOVER_SITUACAO);
					execute(mov);
					
					all = null;
					novaSituacao = dao.findByPrimaryKey(novaSituacao.getId(), SituacaoMaterialInformacional.class, new String[]{"descricao"});
					addMensagemInformation("Situa��o removida com sucesso. Materiais migrados para a situa��o "+novaSituacao.getDescricao());
					novaSituacao = null;
					
					return forward(getListPage());
				
				}catch(NegocioException ne){
					addMensagens(ne.getListaMensagens());
					return null;
				}
				
			} else{
				addMensagemErro("Este objeto j� foi removido");
				return null;
			}
		}finally{
			if(dao != null ) dao.close();
		}
	}
	
	
	/**
	 * Metodo que evita NullPointerException quando o usuario tenta
	 * alterar um objeto que j� foi removido
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/SituacaoMaterialInformacional/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar () throws ArqException {
		// Tenta pegar o objeto do banco
		populateObj(true);

		prepareMovimento(ArqListaComando.ALTERAR);

		// Se o objeto foi encontrado, exibe a tela de alterar
		if (obj != null)
			return super.atualizar();

		// Senao, exibe a mensagem de erro
		obj = new SituacaoMaterialInformacional();
		addMensagemErro("Este objeto foi removido.");
		return forward(getListPage());
	}
	
	
	/**
	 * M�todo que verifica se j� existe um objeto cadastrado com este nome.
	 * 
	 * <br/><br/>
	 * N�o chamado de nenhuma p�gina JSP.
	 * 
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException, SegurancaException, DAOException {
		SituacaoMaterialInformacionalDao dao = null;
		
		try{
			dao = getDAO(SituacaoMaterialInformacionalDao.class);
			SituacaoMaterialInformacional s = dao.findSituacaoMaterialAtivoByDescricao(obj.getDescricao());
	
		
			// se existe um Situa��o ativo com a mesma descri��o e n�o � o Situa��o que est� sendo alterado agora
			if ((s != null && s.getId() != obj.getId())){
				throw new NegocioException ("J� existe uma Situa��o com esta descri��o.");
			}
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	
	/**
	 * Metodo que cadastra ou altera um objeto
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/SituacaoMaterialInformacional/form.jsp
	 * 
	 * @throws NegocioException 
	 */
	@Override
	public String cadastrar() throws ArqException {

		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		try {
			
			// Verifica se o objeto nao foi removido
			if (obj == null){
				addMensagemErro("Este objeto foi removido.");
				return forward(getListPage());
			}
			
			// Valida o objeto
			ListaMensagens lista = obj.validate();
			
			beforeCadastrarAfterValidate();
		
			// Se ocorreram erros, exiba-os e retorne.
			if (lista != null && !lista.isEmpty()){
				addMensagens(lista);
				return forward(getFormPage());
			}
		
			if (!hasErrors()){
				// Prepara o movimento, setando o objeto
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(obj);
				
				
				// Se for operacao de cadastrar, a id do objeto sera' igual a zero
				if (obj.getId() == 0){
					// Seta a operacao como cadastrar
					mov.setCodMovimento(ArqListaComando.CADASTRAR);
					// Tenta executar a operacao
					execute(mov);
					
				} else {
					/* Nao era opera��o de cadastrar, entao � de alterar */
					
					mov.setCodMovimento(ArqListaComando.ALTERAR);
					
					execute(mov);
					
				}
	
				// Se chegou aqui, nao houve erros. Exibe a mensagem de sucesso.
				addMensagemInformation("Opera��o realizada com sucesso");
			}
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		all = null;
		
		// Retorna para a pagina de listagem.
		return forward(getListPage());
	}
	
	
	/**
	 * Retorna todas ATIVOS
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection <SituacaoMaterialInformacional> getAll () throws DAOException{
		if (all == null){
			SituacaoMaterialInformacionalDao dao =  null;
			try{
				dao = getDAO(SituacaoMaterialInformacionalDao.class);
				all = dao.findAllSituacoesAtivas();
			}finally{
				if (dao != null) dao.close();
			}
			
		}
		
		return all;
	}
	
	/**
	 * 
	 * Retorna o quantidade de Situa��o ativos no sistema
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/SituacaoMaterialInformacional/lista.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public int getSize () throws DAOException{
		return getAll().size();
	}


	/**
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/SituacaoMaterialInformacional/confirRemocaoSituacao</li>
	 *    
	 *   </ul>
	 *
	 * @return
	 */
	public Collection <SelectItem> getSituacoesAtivas() {
		return toSelectItems(situacoesAtivas, "id", "descricao");
	}


	public SituacaoMaterialInformacional getNovaSituacao() {return novaSituacao;}
	public void setNovaSituacao(SituacaoMaterialInformacional novaSituacao) {this.novaSituacao = novaSituacao;}
	public void setSituacoesAtivas(List<SituacaoMaterialInformacional> situacoesAtivas) {	this.situacoesAtivas = situacoesAtivas;}
	
}