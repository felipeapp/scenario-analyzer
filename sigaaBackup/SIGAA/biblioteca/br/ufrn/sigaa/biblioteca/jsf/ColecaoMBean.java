/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 *  MBean que gerencia a parte de criação e alterações das coleções do sistema
 *  
 * @author jadson
 *
 */
@Component("colecaoMBean")
@Scope("request")
public class ColecaoMBean extends SigaaAbstractController <Colecao> {

	/**
	 * Página para confirmar a remoção da coleção, e passar os materiais dessa coleção para alguma coleção que ficou ativa.
	 */
	public static final String PAGINA_CONFIRMA_REMOCAO_COLECAO = "/biblioteca/Colecao/confirmaRemocaoColecao.jsp";
	
	/**
	 * Contém a lista de coleções ativas, para o usuário escolher qual a coleção os materiais da coleção removida vão passar a possuir.
	 */
	private List<Colecao> colecoesAtivas;
	
	/**
	 * A coleção que vai substituir a coleçaõ removida, nos materiais que possuem a coleção removida
	 */
	private Colecao novaColecao = new Colecao(-1);
	
	/** Guarda os dados utilizados nos combobox para não ficar buscando sempre no banco */
	private Collection <Colecao> colecoesCombo;
	
	
	public ColecaoMBean(){
		obj = new Colecao();
	}
	
	
	
	/**
	 * Metodo que redireciona para a página onde o usuário vai confirmar remoção da coleção e  escolher a nova coleção 
	 * que os materiais da antiga coleção vão possuir.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/Colecao/lista.jsp
	 * @throws SegurancaException 
	 * @throws SegurancaException 
	 */
	@Override
	public String preRemover()   {
		
		try {
		
			prepareMovimento(SigaaListaComando.REMOVER_COLECAO);
			
			colecoesAtivas = (	List <Colecao>) getGenericDAO().findByExactField(Colecao.class, "ativo", true);
			
			populateObj(true); // busca o objeto com o id passado com parâmetro
			
			return forward(PAGINA_CONFIRMA_REMOCAO_COLECAO);
			
		} catch (DAOException e) {
			addMensagemErro("Erros ao buscar as coleções ativas no sistema");
			return null;
		} catch (ArqException e) {
			addMensagemErro("Erros ao tentar remover a coleção");
			return null;
		}
	}
	
	
	/**
	 * Metodo que remove o objeto, verificando se o mesmo existe
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/FormaDocumento/confirmaRemocaoColecao.jsp
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
				addMensagemInformation("Coleção removida com sucesso. Materiais migrados para a coleção: "+novaColecao.getDescricao());
				
				novaColecao = null;
				
				return forward(getListPage());
			} else
				addMensagemErro("Esta coleção já foi removida");

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
	 * Chamado a partir da página: /sigaa.war/biblioteca/Colecao/lista.jsp
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
		addMensagemErro("Essa coleção foi removida.");
		return forward(getListPage());
	}
	
	
	/**
	 * Método que verifica se já existe uma coleção ativa cadastrada com a mesma descrição e código.
	 * 
	 * <br/><br/>
	 * Não chamado de nenhum página JSP.
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
					// se está cadastrando, se existe no banco, ativo, não pode 
					if (  colecao != null && colecao.isAtivo()){
						throw new NegocioException ("Já existe uma Coleção com a mesma descrição.");
					}
				}else{
					// se está alterando e exitir outro tipo de material com a mesma descrição
					if (  colecao != null && colecao.getId() != obj.getId() && colecao.isAtivo() ){
						throw new NegocioException ("Já existe uma Coleção com a mesma descrição.");
					}
				}
			
			}
			
			colecoesBanco = dao.findAtivosByExactField(Colecao.class, "codigo", obj.getCodigo());
			
			for (Colecao colecao : colecoesBanco) {
				
				if(obj.getId() == 0){
					// se está cadastrando, se existe no banco, ativo, não pode 
					if (  colecao != null && colecao.isAtivo()){
						throw new NegocioException ("Já existe uma Coleção com o mesmo código.");
					}
				}else{
					// se está alterando e exitir outro tipo de material com a mesma descrição
					if (  colecao != null && colecao.getId() != obj.getId() && colecao.isAtivo() ){
						throw new NegocioException ("Já existe uma Coleção com o mesmo código.");
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
	 * Chamado a partir da página: /sigaa.war/biblioteca/Colecao/form.jsp
	 */
	@Override
	public String cadastrar() throws ArqException {

		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Verifica se o objeto nao foi removido
		if (obj == null){
			addMensagemErro("Essa coleção foi removida.");
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
			
			beforeCadastrarAfterValidate(); // Valida se o usuário tentar cadastrar duas coleções iguais no sistema
			
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
			addMessage("Operação Realizada com sucesso", TipoMensagemUFRN.INFORMATION);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		// Retorna para a pagina de listagem.
		return forward(getListPage());
	}
	
	
	/**
	 * 
	 * Volta para a página que lista as coleções.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/Colecao/form.jsp
	 *
	 * @return
	 */
	public String voltar(){
		return forward(getListPage());
	}
	
	/**
	 * Retorna todas as colecao ativas que pode ser alteráveis, ordenadas pela descricao
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
	 * Retorna todas as coleções cadastradas
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
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
