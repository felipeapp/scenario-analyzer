/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 04/03/2009
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
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoRemoveStatusTipoMaterialETipoEmprestimo;


/**
 * MBean que gerencia a parte de criação e alteração dos tipos de materiais existentes no sistema.
 * <p><i>(Livos, Monografias, Partituras, CD, etc..)</i></p>
 * 
 * @author jadson
 *
 */
@Component("tipoMaterialMBean")
@Scope("request")
public class TipoMaterialMBean extends SigaaAbstractController <TipoMaterial> {
	
	/**
	 * Página para confirmar a remoção da coleção, e passar os materiais dessa coleção para alguma coleção que ficou ativa.
	 */
	public static final String PAGINA_CONFIRMA_REMOCAO_TIPO_MATERIAL = "/biblioteca/TipoMaterial/confirmaRemocaoTipoMaterial.jsp";
	
	public TipoMaterialMBean() {
		obj = new TipoMaterial();
	}
	
	
	/**
	 * Contém a lista de tipos de materiais ativos, para o usuário escolher qual tipo os materiais do tipo removido vão possuir.
	 */
	private List<TipoMaterial> tiposAtivos;
	
	/**
	 * O tipo que vai substituir o tipo removido, nos materiais que possuem o tipo removido
	 */
	private TipoMaterial novoTipo = new TipoMaterial(-1);
	

	/** Guarda os dados utilizados nos combobox para não ficar buscando sempre no banco */
	private Collection <TipoMaterial> tiposMateriaisCombo;
	
	/**
	 * Metodo que redireciona para a página onde o usuário vai confirmar remoção do tipo de material e escolher o novo tipo 
	 * que os materiais do antigo tipo vão possuir.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoMaterial/lista.jsp
	 * @throws SegurancaException 
	 * @throws SegurancaException 
	 */
	@Override
	public String preRemover()   {
		
		try {
		
			prepareMovimento(SigaaListaComando.REMOVER_STATUS_MATERIAL_TIPO_MATERIAL_E_TIPO_EMPRESTIMO);
			
			tiposAtivos = (	List <TipoMaterial>) getGenericDAO().findByExactField(TipoMaterial.class, "ativo", true);
			
			populateObj(true); // busca o objeto com o id passado com parâmetro
			
			return forward(PAGINA_CONFIRMA_REMOCAO_TIPO_MATERIAL);
			
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
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoMaterial/confirmaRemocaoTipoMaterial.jsp
	 * 
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	@Override
	public String remover() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		
		//populateObj(true);

		GenericDAO dao = getGenericDAO();
		
		obj = dao.refresh(obj);
		dao.detach(obj);

		// Se o objeto a remover foi encontrado, desativa
		if ( obj.isAtivo() ){
			
			try {
				
				MovimentoRemoveStatusTipoMaterialETipoEmprestimo mov = new MovimentoRemoveStatusTipoMaterialETipoEmprestimo(obj, novoTipo);
				mov.setCodMovimento(SigaaListaComando.REMOVER_STATUS_MATERIAL_TIPO_MATERIAL_E_TIPO_EMPRESTIMO);
				execute(mov);
				
				all = null;
				novoTipo = dao.findByPrimaryKey(novoTipo.getId(), TipoMaterial.class, new String[]{"id", "descricao"});
				
				addMensagemInformation("Tipo do Material Removido com Sucesso. Materiais migrados para o tipo: "+novoTipo.getDescricao());
				
				novoTipo = null;
				
			} catch (NegocioException ne) {
				addMensagens(ne.getListaMensagens());
				return null;
			}
			
		} else
			addMensagemErro("Tipo de Material já foi removido");


		return forward(getListPage());
	}
	
	
	/**
	 * 
	 * Volta para a página que lista os tipos de materiais.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoMaterial/form.jsp
	 *
	 * @return
	 */
	public String voltar(){
		return forward(getListPage());
	}
	
	
	/**
	 * Metodo que evita NullPointerException quando o usuario tenta
	 * alterar um objeto que já foi removido.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoMaterial/lista.jsp
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
		obj = new TipoMaterial();
		addMensagemErro("Este objeto foi removido.");
		return redirectJSF(getListPage());
	}
	
	
	/**
	 * Método que verifica se já existe um tipo de material ativo cadastrado com a mesma descrição.
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
			Collection<TipoMaterial> tiposMateriaisBanco = dao.findAtivosByExactField(TipoMaterial.class, "descricao", obj.getDescricao());
			
			for (TipoMaterial tipoMaterial : tiposMateriaisBanco) {
				
				if(obj.getId() == 0){
					// se está cadastrando, se existe no banco, ativo, não pode 
					if (  tipoMaterial != null && tipoMaterial.isAtivo()){
						throw new NegocioException ("Já existe um Tipo de Material com esta descrição.");
					}
				}else{
					// se está alterando e exitir outro tipo de material com a mesma descrição
					if (  tipoMaterial != null && tipoMaterial.getId() != obj.getId() && tipoMaterial.isAtivo() ){
						throw new NegocioException ("Já existe um Tipo de Material com esta descrição.");
					}
				}
			
			}
			
		}finally{
			if (dao != null) dao.close();
		}
	}
	
	
	
	/**
	 * Método que cadastra ou altera um objeto.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/TipoMaterial/form.jsp
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
					executeWithoutClosingSession(mov, getCurrentRequest());
					// Prepara o movimento para permitir a insercao de um novo objeto
					prepareMovimento(ArqListaComando.CADASTRAR);
				} else {
					/* Nao era operacao de cadastrar, entao e' de alterar */
					// Seta a operacao como alterar
					mov.setCodMovimento(ArqListaComando.ALTERAR);
					// Tenta executar a operacao
					executeWithoutClosingSession(mov, getCurrentRequest());
				}
	
				// Se chegou aqui, nao houve erros. Exibe a mensagem de sucesso.
				addMessage("Operação realizada com sucesso", TipoMensagemUFRN.INFORMATION);
			}
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		// Retorna para a pagina de listagem.
		return forward(getListPage());
	}
	
	
	
	@Override
	public Collection <TipoMaterial> getAll () throws DAOException{
		GenericDAO dao = getGenericDAO();
		try {
			return dao.findAllAtivos(TipoMaterial.class, "descricao");
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Método que retorna todos os tipos de materiais em forma de SelectItem.
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCombo () throws DAOException {
		
		if(tiposMateriaisCombo == null){
			GenericDAO dao =  null;
			try{
				dao = getGenericDAO();
				tiposMateriaisCombo = dao.findByExactField(TipoMaterial.class, "ativo", true);
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return toSelectItems(tiposMateriaisCombo, "id", "descricao");
	}

	
	/**
	 * 
	 * Retorna o quantidade de tipos de materiais ativos no sistema
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/TipoMaterial/lista.jsp</li>
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
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/TipoMaterial/confirRemocaoTiposMaterial.jsp</li>
	 *    
	 *   </ul>
	 *
	 * @return
	 */
	public  Collection <SelectItem> getTiposAtivos() {
		return toSelectItems(tiposAtivos, "id", "descricao");
		
	}
	

	public TipoMaterial getNovoTipo() {
		return novoTipo;
	}

	public void setNovoTipo(TipoMaterial novoTipo) {
		this.novoTipo = novoTipo;
	}
	
	
}
