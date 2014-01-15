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
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.StatusMaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoRemoveStatusTipoMaterialETipoEmprestimo;


/**
 *   MBean que gerencia a parte de criação e alteração dos status dos materiais informacionais do sistema.
 *
 * @author jadson
 *
 */
@Component("statusMaterialInformacionalMBean")
@Scope("request")
public class StatusMaterialInformacionalMBean extends SigaaAbstractController <StatusMaterialInformacional> {

	/**
	 * Página para confirmar a remoção do status, e passar os materiais desse status para algum status que ficou ativo.
	 */
	public static final String PAGINA_CONFIRMA_REMOCAO_STATUS = "/biblioteca/StatusMaterialInformacional/confirmaRemocaoStatus.jsp";
	
	
	/**
	 * Contém a lista de status ativos, para o usuário escolher qual status os materiais do status removido vão possuir.
	 */
	private List<StatusMaterialInformacional> statusAtivos;
	
	/**
	 * O status que vai substituir o status removido, nos materiais que possuem o status removido
	 */
	private StatusMaterialInformacional novoStatus = new StatusMaterialInformacional(-1);
	
	
	public StatusMaterialInformacionalMBean(){
		obj = new StatusMaterialInformacional();
	}
	
	
	/**
	 * Metodo que redireciona para a página onde o usuário vai confirmar remoção do status e escolher o novo status 
	 * que os materiais do antigo status vão possuir.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/StatusMaterialInformacional/lista.jsp
	 * @throws SegurancaException 
	 * @throws SegurancaException 
	 */
	@Override
	public String preRemover()   {
		
		try {
		
			prepareMovimento(SigaaListaComando.REMOVER_STATUS_MATERIAL_TIPO_MATERIAL_E_TIPO_EMPRESTIMO);
			
			statusAtivos = getDAO(StatusMaterialInformacionalDao.class).findAllStatusMaterialAtivos();
			
			populateObj(true); // busca o objeto com o id passado com parâmetro
			
			return forward(PAGINA_CONFIRMA_REMOCAO_STATUS);
			
		} catch (DAOException e) {
			addMensagemErro("Erros ao buscar os status ativos no sistema");
			return null;
		} catch (ArqException e) {
			addMensagemErro("Erros ao tentar remover o status");
			return null;
		}
	}
	
	/**
	 * Metodo que remove o objeto, verificando se o mesmo existe
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/StatusMaterialInformacional/confirmaRemocaoStatus.jsp
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
		
					MovimentoRemoveStatusTipoMaterialETipoEmprestimo mov = new MovimentoRemoveStatusTipoMaterialETipoEmprestimo(obj, novoStatus);
					mov.setCodMovimento(SigaaListaComando.REMOVER_STATUS_MATERIAL_TIPO_MATERIAL_E_TIPO_EMPRESTIMO);
					execute(mov);
					
					all = null;
					novoStatus = dao.findByPrimaryKey(novoStatus.getId(), StatusMaterialInformacional.class, new String[]{"descricao"});
					addMensagemInformation("Status do material removido com sucesso. Materiais migrados para o Status "+novoStatus.getDescricao());
					novoStatus = null;
					
					return forward(getListPage());
				
				}catch(NegocioException ne){
					addMensagens(ne.getListaMensagens());
					return null;
				}
				
			} else{
				addMensagemErro("Este objeto já foi removido");
				return null;
			}
		}finally{
			if(dao != null ) dao.close();
		}
	}

	
	
	
	/**
	 * Metodo que evita NullPointerException quando o usuario tenta
	 * alterar um objeto que já foi removido
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/StatusMaterialInformacional/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar () throws ArqException {
		// Tenta pegar o objeto do banco
		populateObj(true);

		prepareMovimento(SigaaListaComando.ALTERAR_STATUS_MATERIAL);

		// Se o objeto foi encontrado, exibe a tela de alterar
		if (obj != null)
			return super.atualizar();

		// Senao, exibe a mensagem de erro
		obj = new StatusMaterialInformacional();
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
		StatusMaterialInformacionalDao dao  = null;
		
			try{
			
			dao = getDAO(StatusMaterialInformacionalDao.class);
			StatusMaterialInformacional s = dao.findStatusMaterialAtivoByDescricao(obj.getDescricao());
			
			// se existe um status ativo com a mesma descrição e não é o status que está sendo alterado agora
			if ((s != null && s.getId() != obj.getId())){
				throw new NegocioException ("Já existe um Status com esta descrição.");
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
					
					mov.setCodMovimento(SigaaListaComando.ALTERAR_STATUS_MATERIAL);
					
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
	
	
	/**
	 * Retorna todos ATIVOS
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection <StatusMaterialInformacional> getAll () throws DAOException{
		if (all == null){
			GenericDAO dao =  null;
			try{
				dao = getGenericDAO();
				all = dao.findByExactField(StatusMaterialInformacional.class, "ativo", true);
			}finally{
				if (dao != null) dao.close();
			}
		}
		
		return all;
	}
	
	/**
	 * 
	 * Retorna o quantidade de status ativos no sistema
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/StatusMaterialInformacional/lista.jsp</li>
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
	 *    <li>/sigaa.war/biblioteca/StatusMaterialInformacional/confirRemocaoStatus</li>
	 *    
	 *   </ul>
	 *
	 * @return
	 */
	public Collection <SelectItem> getStatusAtivos() {
		return toSelectItems(statusAtivos, "id", "descricao");
	}


	public void setStatusAtivos(List<StatusMaterialInformacional> statusAtivos) {
		this.statusAtivos = statusAtivos;
	}


	public StatusMaterialInformacional getNovoStatus() {
		return novoStatus;
	}


	public void setNovoStatus(StatusMaterialInformacional novoStatus) {
		this.novoStatus = novoStatus;
	}
	
}
