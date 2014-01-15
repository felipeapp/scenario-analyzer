/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 24/09/2008
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
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.ColecaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.negocio.MovimentoRemoveColecao;

/**
 *  MBean que gerencia a parte de cria��o e altera��es das cole��es do sistema
 *  
 * @author jadson
 *
 */
@Component("colecaoMBean")
@Scope("request")
public class ColecaoMBean extends SigaaAbstractController <Colecao> {

	/**
	 * P�gina para confirmar a remo��o da cole��o, e passar os materiais dessa cole��o para alguma cole��o que ficou ativa.
	 */
	public static final String PAGINA_CONFIRMA_REMOCAO_COLECAO = "/biblioteca/Colecao/confirmaRemocaoColecao.jsp";
	
	/**
	 * Cont�m a lista de cole��es ativas, para o usu�rio escolher qual a cole��o os materiais da cole��o removida v�o passar a possuir.
	 */
	private List<Colecao> colecoesAtivas;
	
	/**
	 * A cole��o que vai substituir a cole�a� removida, nos materiais que possuem a cole��o removida
	 */
	private Colecao novaColecao = new Colecao(-1);
	
	/** Guarda os dados utilizados nos combobox para n�o ficar buscando sempre no banco */
	private Collection <Colecao> colecoesCombo;
	
	
	public ColecaoMBean(){
		obj = new Colecao();
	}
	
	
	
	/**
	 * Metodo que redireciona para a p�gina onde o usu�rio vai confirmar remo��o da cole��o e  escolher a nova cole��o 
	 * que os materiais da antiga cole��o v�o possuir.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Colecao/lista.jsp
	 * @throws SegurancaException 
	 * @throws SegurancaException 
	 */
	@Override
	public String preRemover()   {
		
		try {
		
			prepareMovimento(SigaaListaComando.REMOVER_COLECAO);
			
			colecoesAtivas = (	List <Colecao>) getGenericDAO().findByExactField(Colecao.class, "ativo", true);
			
			populateObj(true); // busca o objeto com o id passado com par�metro
			
			return forward(PAGINA_CONFIRMA_REMOCAO_COLECAO);
			
		} catch (DAOException e) {
			addMensagemErro("Erros ao buscar as cole��es ativas no sistema");
			return null;
		} catch (ArqException e) {
			addMensagemErro("Erros ao tentar remover a cole��o");
			return null;
		}
	}
	
	
	/**
	 * Metodo que remove o objeto, verificando se o mesmo existe
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/FormaDocumento/confirmaRemocaoColecao.jsp
	 */
	@Override
	public String remover() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);

		GenericDAO dao = null; 
		

		try {
			
			dao = getGenericDAO();
			
			// Se o objeto a remover foi encontrado, desativa
			if (obj != null && obj.isAtivo()){
				
				MovimentoRemoveColecao mov = new MovimentoRemoveColecao(obj, novaColecao);
				mov.setCodMovimento(SigaaListaComando.REMOVER_COLECAO);
				execute(mov);
				
				all = null;
				novaColecao = getGenericDAO().findByPrimaryKey(novaColecao.getId(), Colecao.class, new String[]{"id", "descricao"});
				addMensagemInformation("Cole��o removida com sucesso. Materiais migrados para a cole��o: "+novaColecao.getDescricao());
				
				novaColecao = null;
				
				return forward(getListPage());
			} else
				addMensagemErro("Esta cole��o j� foi removida");

		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}finally{
			if(dao != null ) dao.close();
		}
		
		
		all = null;

		return forward(getListPage());
	}
	
	/**
	 * Metodo que evita NullPointerException quando o usuario tenta
	 * alterar um objeto que ja' foi removido
	 * 
	 * * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Colecao/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar () throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Tenta pegar o objeto do banco
		populateObj(true);

		prepareMovimento(ArqListaComando.ALTERAR);

		// Se o objeto foi encontrado, exibe a tela de alterar
		if (obj != null)
			return super.atualizar();

		// Senao, exibe a mensagem de erro
		obj = new Colecao();
		addMensagemErro("Essa cole��o foi removida.");
		return forward(getListPage());
	}
	
	
	/**
	 * M�todo que verifica se j� existe uma cole��o ativa cadastrada com a mesma descri��o e c�digo.
	 * 
	 * <br/><br/>
	 * N�o chamado de nenhum p�gina JSP.
	 * 
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException, SegurancaException, DAOException {
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO();
			
			Collection<Colecao> colecoesBanco = dao.findAtivosByExactField(Colecao.class, "descricao", obj.getDescricao());
			
			for (Colecao colecao : colecoesBanco) {
				
				if(obj.getId() == 0){
					// se est� cadastrando, se existe no banco, ativo, n�o pode 
					if (  colecao != null && colecao.isAtivo()){
						throw new NegocioException ("J� existe uma Cole��o com a mesma descri��o.");
					}
				}else{
					// se est� alterando e exitir outro tipo de material com a mesma descri��o
					if (  colecao != null && colecao.getId() != obj.getId() && colecao.isAtivo() ){
						throw new NegocioException ("J� existe uma Cole��o com a mesma descri��o.");
					}
				}
			
			}
			
			colecoesBanco = dao.findAtivosByExactField(Colecao.class, "codigo", obj.getCodigo());
			
			for (Colecao colecao : colecoesBanco) {
				
				if(obj.getId() == 0){
					// se est� cadastrando, se existe no banco, ativo, n�o pode 
					if (  colecao != null && colecao.isAtivo()){
						throw new NegocioException ("J� existe uma Cole��o com o mesmo c�digo.");
					}
				}else{
					// se est� alterando e exitir outro tipo de material com a mesma descri��o
					if (  colecao != null && colecao.getId() != obj.getId() && colecao.isAtivo() ){
						throw new NegocioException ("J� existe uma Cole��o com o mesmo c�digo.");
					}
				}
			
			}
			
		}finally{
			if (dao != null) dao.close();
		}
	}
	
	/**
	 * Metodo que cadastra ou altera um objeto
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Colecao/form.jsp
	 */
	@Override
	public String cadastrar() throws ArqException {

		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Verifica se o objeto nao foi removido
		if (obj == null){
			addMensagemErro("Essa cole��o foi removida.");
			return forward(getListPage());
		}
		
		// Valida o objeto
		ListaMensagens lista = obj.validate();
		
		
		// Se ocorreram erros, exiba-os e retorne.
		if (lista != null && !lista.isEmpty()){
			addMensagens(lista);
			return forward(getFormPage());
		}
	
		// Prepara o movimento, setando o objeto
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		try {
			
			beforeCadastrarAfterValidate(); // Valida se o usu�rio tentar cadastrar duas cole��es iguais no sistema
			
			// Se for operacao de cadastrar, a id do objeto sera' igual a zero
			if (obj.getId() == 0){
				// Seta a operacao como cadastrar
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				// Tenta executar a operacao
				execute(mov);
				// Prepara o movimento para permitir a insercao de um novo objeto
				prepareMovimento(ArqListaComando.CADASTRAR);
			} else {
				/* Nao era operacao de cadastrar, entao e' de alterar */
				// Seta a operacao como alterar
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				// Tenta executar a operacao
				execute(mov);
			}

			// Se chegou aqui, nao houve erros. Exibe a mensagem de sucesso.
			addMessage("Opera��o Realizada com sucesso", TipoMensagemUFRN.INFORMATION);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		// Retorna para a pagina de listagem.
		return forward(getListPage());
	}
	
	
	/**
	 * 
	 * Volta para a p�gina que lista as cole��es.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Colecao/form.jsp
	 *
	 * @return
	 */
	public String voltar(){
		return forward(getListPage());
	}
	
	/**
	 * Retorna todas as colecao ativas que pode ser alter�veis, ordenadas pela descricao
	 * Chamado na pagina /biblioteca/biblioteca/lista.jsp
	 */
	@Override
	public Collection <Colecao> getAll() throws DAOException{
		
		if (all == null)
			all = getDAO(ColecaoDao.class).findAllAtivos(Colecao.class, "codigo");
		
		return all;
	}

	
	
	/**
	 * 
	 * Retorna todas as cole��es cadastradas
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getAllCombo() throws DAOException{
		
		if(colecoesCombo == null){
			GenericDAO dao =  null;
			try{
				dao = getGenericDAO();
				colecoesCombo = dao.findByExactField(Colecao.class, "ativo", true);
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return toSelectItems(colecoesCombo, "id", "descricaoCompleta");
	}
	
	
	
	/**
	 * Metodo que retorna a quantidade de prazos cadastrados para exibir na listagem
	 * Chamado na pagina /biblioteca/biblioteca/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public int getSize () throws ArqException{
		// limpa a lista para que seja atualizada.
		return getAll().size();
	}

	/**
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/Colecao/confirRemocaoColecao.jsp</li>
	 *    
	 *   </ul>
	 *
	 * @return
	 */
	public  Collection <SelectItem> getColecoesAtivas() {
		return toSelectItems(colecoesAtivas, "id", "descricao");
		
	}

	public Colecao getNovaColecao() {
		return novaColecao;
	}

	public void setNovaColecao(Colecao novaColecao) {
		this.novaColecao = novaColecao;
	}
	
}
