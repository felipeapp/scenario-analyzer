package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
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
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.negocio.MovimentoRemoveFormatoDocumento;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.FormaDocumentoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;

/**
 * Classe que gerência o CRUD da forma de documento do material informacional.
 * 
 * @author Mário Rizzi
 *
 */
@Component(value="formaDocumentoMBean")
@Scope(value="request")
public class FormaDocumentoMBean extends SigaaAbstractController<FormaDocumento> {

	/**
	 * Página para confirmar a remoção da forma de documento, e passar os materiais dessa forma para alguma forma que ficou ativa.
	 */
	public static final String PAGINA_CONFIRMA_REMOCAO_FORMA_DOCUMENTO = "/biblioteca/FormaDocumento/confirmaRemocaoFormaDocumento.jsp";
	
	/**
	 * Contém a lista de formas de Documento ativas, para o usuário escolher qual a forma do Documento os materiais da forma removida vão passar a possuir.
	 */
	private List<FormaDocumento> formaDocumentoAtivas;
	
	/**
	 * A forma que vai substituir a forma removida, nos materiais que possuem a forma removida.
	 */
	private FormaDocumento novaFormaDocumento = new FormaDocumento(-1);
	
	/**
	 * Construtor
	 */
	public FormaDocumentoMBean(){
		obj = new FormaDocumento();
	}
	
	
	
	
//	/**
//	 * Define a raiz do diretório que contêm todas as view's associadas as ações de CRUD
//	 * da Formas de Documento
//	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getDirBase()
//	 */
//	@Override
//	public String getDirBase() {
//		return "/biblioteca/FormaDocumento";
//	}
	
	

//	/**
//	 * Método que verifica se já existe um objeto cadastrado com este nome.
//	 * 
//	 * <br/><br/>
//	 * Não chamado de nenhum página JSP.
//	 * 
//	 */
//	@Override
//	public void beforeCadastrarAfterValidate() throws SegurancaException, DAOException {
//		GenericDAO dao = getGenericDAO();
//		FormaDocumento f = dao.findByExactField(FormaDocumento.class, "denominacao", obj.getDenominacao(), true);
//		
//		if ((f != null && obj.getId() == 0) || (f != null && f.getId() != obj.getId())){
//			addMensagemErro("Já existe uma forma com este nome.");
//		}
//	}
//	
//	/**
//	 * Inicializa o objeto antes de preparar o formulário para atualização
//	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeAtualizar()
//	 */
//	@Override
//	protected void beforeAtualizar() throws ArqException {
//		obj = new FormaDocumento();
//	}
	
	//////////////////////     A parte de cadastrar e alterar /////////////////////////////
	
	
	/**
	 * Metodo que evita NullPointerException quando o usuario tenta
	 * alterar um objeto que já foi removido
	 * 
	 * <br/><br/>
	 * Chamado a partir da página:  /sigaa.war/biblioteca/FormaDocumento/lista.jsp
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
		obj = new FormaDocumento();
		addMensagemErro("Este objeto foi removido.");
		return forward(getListPage());
	}
	
	
	/**
	 * Método que verifica se já existe um objeto cadastrado com este nome.
	 * 
	 * <br/><br/>
	 * Não chamado de nenhuma página JSP.
	 * 
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException, SegurancaException, DAOException {
		FormaDocumentoDao dao = null;
		
		try{
			dao = getDAO(FormaDocumentoDao.class);
			FormaDocumento f = dao.findFormaDocumentoMaterialAtivoByDenominacao(obj.getDenominacao());
	
		
			// se existe um status ativo com a mesma descrição e não é o status que está sendo alterado agora
			if ((f != null && f.getId() != obj.getId())){
				throw new NegocioException ("Já existe uma Forma de Documento com esta denominação.");
			}
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	
	/**
	 * Metodo que cadastra ou altera um objeto
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/StatusMaterialInformacional/form.jsp
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
					/* Nao era operação de cadastrar, entao é de alterar */
					
					mov.setCodMovimento(ArqListaComando.ALTERAR);
					
					execute(mov);
					
				}
	
				// Se chegou aqui, nao houve erros. Exibe a mensagem de sucesso.
				addMensagemInformation("Operação realizada com sucesso");
			}
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		all = null;
		
		// Retorna para a pagina de listagem.
		return forward(getListPage());
	}
	
	
	/////////////////////////////////////////////////////////////////////////

	
	
	
	
	
	
	
//	/**
//	 * retorna para a página de listagem depois que cadastrar
//	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeAtualizar()
//	 * 
//	 * <br/><br/>
//	 * Chamado a partir da página: /sigaa.war/biblioteca/FormaDocumento/lista.jsp
//	 * 
//	 */
//	@Override
//	public String forwardCadastrar() {
//		return getListPage();
//	}

	////////////////////////  A parte de remoção /////////////////////////////
	
	
	/**
	 * Metodo que redireciona para a página onde o usuário vai confirmar remoção da forma do documento e  escolher a nova forma do documento 
	 * que os materiais da antiga forma  vão possuir.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/FormaDocumento/lista.jsp
	 * @throws SegurancaException 
	 * @throws SegurancaException 
	 */
	@Override
	public String preRemover()   {
		
		try {
			
			prepareMovimento(SigaaListaComando.REMOVER_FORMA_DOCUMENTO);
			
			formaDocumentoAtivas = ( List <FormaDocumento>) getGenericDAO().findByExactField(FormaDocumento.class, "ativo", true);
			
			populateObj(true); // busca o objeto com o id passado com parâmetro
			
			return forward(PAGINA_CONFIRMA_REMOCAO_FORMA_DOCUMENTO);
			
		} catch (DAOException e) {
			addMensagemErro("Erros ao buscar os status ativos no sistema");
			return null;
		} catch (ArqException e) {
			addMensagemErro("Erros ao tentar remover o status");
			return null;
		}
	}
	
	
	/**
	 * Metodo que remove o objeto, verificando se o mesmo existe.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/FormaDocumento/confirmaRemocaoFormaDocumento.jsp
	 * 
	 * @throws NegocioException 
	 */
	@Override
	public String remover() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);

		GenericDAO dao  = null;

		try{
			dao = getGenericDAO();
	
			// Se o objeto a remover foi encontrado, desativa
			if ( obj != null && obj.isAtivo() ){
	
				
				try {
					MovimentoRemoveFormatoDocumento mov = new MovimentoRemoveFormatoDocumento(obj, novaFormaDocumento);
					mov.setCodMovimento(SigaaListaComando.REMOVER_FORMA_DOCUMENTO);
					execute(mov);
					
					all = null;
					novaFormaDocumento = dao.findByPrimaryKey(novaFormaDocumento.getId(), FormaDocumento.class, new String[]{"id", "denominacao"});
					
					novaFormaDocumento = null;
					addMensagemInformation("Forma do Documento removida com sucesso. " 
							+(novaFormaDocumento != null ? " Materiais migrados para a forma "+novaFormaDocumento.getDenominacao() : ""));
					
				} catch (NegocioException ne) {
					ne.printStackTrace();
					addMensagens(ne.getListaMensagens());
					return null;
				}
				
			} else
				addMensagemErro("Forma de Documento já foi removido");

		}finally{
			if(dao != null ) dao.close();
		}

		return forward(getListPage());
	}

	//////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * Popula o combo contendo todas as formas de documento atualmente
	 * persistidas na base de dados.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		Collection<SelectItem> selectItens = new ArrayList<SelectItem>();
		selectItens.add(new SelectItem("-1", "-- SELECIONE --"));
		selectItens.addAll(toSelectItems(getAll(), "id", "denominacao"));
		
		return selectItens;
	}
	
	/**
	 * Retorna todas formas de documentos ativas a serem listadas.	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection <FormaDocumento> getAll() throws DAOException{
		GenericDAO dao = null;
		
		if(all == null){
			try {
				dao = getGenericDAO();
				all =  dao.findAllAtivos(FormaDocumento.class, "denominacao");
			} finally {
				dao.close();
			}
		}
		return all;
	}
	
	
	
	/**
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/FormaDocumento/confirRemocaoFormaDocumento.jsp</li>
	 *    
	 *   </ul>
	 *
	 * @return
	 */
	public  Collection <SelectItem> getFormaDocumentoAtivas() {
		return toSelectItems(formaDocumentoAtivas, "id", "denominacao");
		
	}

	public FormaDocumento getNovaFormaDocumento() {
		return novaFormaDocumento;
	}

	public void setNovaFormaDocumento(FormaDocumento novaFormaDocumento) {
		this.novaFormaDocumento = novaFormaDocumento;
	}



	
	
	
}
