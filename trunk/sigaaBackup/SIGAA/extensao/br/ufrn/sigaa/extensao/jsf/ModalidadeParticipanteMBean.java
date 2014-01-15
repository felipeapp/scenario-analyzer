/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Collection;

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
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.extensao.dominio.ModalidadeParticipante;

/**
 *
 * <p>MBean que gerencia o cadastro de modalidades de participantes para cursos e eventos de extens�o. </p>
 *
 * <p> 
 * <i> As molalidade de participantes s�o usadas para definir o valor a ser cobrado na inscri��o do participante.<br/>
 * Geralmente nesse cursos e eventos quem � estudando de gradu��o paga menos de quem � estudando de p�s que por suas vez paga mesmo de quem � profissiconal da �rea.<br/>
 * Caso todos tenha o mesmo valor, o sistema j� vai vim com a modalidade "�NICA" salva no banco para esse casos.<br/>
 * O cordenador quando vai abrir as inscri��es � que informa quais modalidades o seu evento vai possuir<br/>
 * </i> 
 * </p>
 * 
 * @author Jadson Santos
 * @since 06/11/2012
 * @version 1.0 cria��o do cadastro
 */
@Component("modalidadeParticipanteMBean")
@Scope("request")
public class ModalidadeParticipanteMBean  extends AbstractControllerCadastro<ModalidadeParticipante>{

	
	/**
	 * P�gina de listagem
	 */
	public static final String PAGINA_LISTA = "/extensao/ModalidadeParticipante/lista.jsp";
	
	
	/**
	 * P�gina de cadatro e altera��o.
	 */
	public static final String PAGINA_FORM = "/extensao/ModalidadeParticipante/form.jsp";
	
	
	/**
	 * Prepara o cadatro de uma nova modalidade.
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/menu.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws ArqException
	 */
	public String preCadastrar() throws ArqException{
		
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		
		obj = new ModalidadeParticipante();

		prepareMovimento(ArqListaComando.CADASTRAR);

		return telaForm();
	}
	
	
	/**
	 *  M�todo chamado pelo cadatro e altera��o para confirmar o cadatro ou altera��o o objeto.
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/ModalidadeParticipante/form.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws ArqException
	 */
	public String cadastrar() throws ArqException{
		
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		
		// Prepara o movimento, setando o objeto
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		try {
			
			ListaMensagens erros =  obj.validate();
			
			if(erros.isErrorPresent()){
				throw new NegocioException(erros);
			}
			
			beforeCadastrarAfterValidate(); // Valida se o usu�rio tentar cadastrar duas modalidade com o mesmo nome
			
			
			
			// Se for operacao de cadastrar, a id do objeto sera' igual a zero
			if (obj.getId() == 0){
				// Seta a operacao como cadastrar
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				// Tenta executar a operacao
				execute(mov);
				
				// Se chegou aqui, nao houve erros. Exibe a mensagem de sucesso.
				addMensagemInformation("Modalidade de Participante cadastrada com sucesso");
				
			} else {
				/* Nao era operacao de cadastrar, entao e' de alterar */
				// Seta a operacao como alterar
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				// Tenta executar a operacao
				execute(mov);
				
				// Se chegou aqui, nao houve erros. Exibe a mensagem de sucesso.
				addMensagemInformation("Modalidade de Participante alterada com sucesso");
			}

			all = null; // para atualizar a listagem
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		// Retorna para a pagina de listagem.
		return telaListar();
		
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
			
			// Aqui n�o h� poroblema de buscar todos porque s�o poucos dados, ent�o foi usado o m�todo do GenericDAO em vez de criar um m�todo para isso.
			Collection<ModalidadeParticipante> modalidadesBanco = dao.findAtivosByExactField(ModalidadeParticipante.class, "nome", obj.getNome());
			
			for (ModalidadeParticipante modalidade : modalidadesBanco) {
				
				if(obj.getId() == 0){
					// se est� cadastrando, se existe no banco, ativo, n�o pode !
					if (  modalidade.isAtivo()){
						throw new NegocioException ("J� existe a Modalidade de Participante: '"+obj.getNome()+"' ");
					}
				}else{
					// se est� alterando e exitir outra modalidade com o mesmo nome, n�o pode!
					if (  modalidade.getId() != obj.getId() && modalidade.isAtivo() ){
						throw new NegocioException ("J� existe a Modalidade de Participante: '"+obj.getNome()+"' ");
					}
				}
			
			}
			
		}finally{
			if (dao != null) dao.close();
		}
	}
	
	
	/**
	 * Metodo que retorna a quantidade de modalidades cadastradas
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/menu.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar() throws ArqException{
		
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		
		obj = new ModalidadeParticipante();
		
		// Busca o objeto inteiro do banco, n�o h� problema porque o objeto n�o tem relacionamentos.
		populateObj(true);

		prepareMovimento(ArqListaComando.ALTERAR);

		// Se o objeto foi encontrado, exibe a tela de alterar
		if (obj == null || obj.getId() <= 0 ||! obj.isAtivo()){
			addMensagemErro("Essa Modalidade de Participante j�  foi removida.");
			return null;
		}
		
		return telaForm();
	}
	
	
	
	/**
	 * Metodo que retorna a quantidade de modalidades cadastradas
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/menu.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws ArqException
	 */
	public String listar(){
		return telaListar();
	}
	
	
	/**
	 * Metodo que retorna a quantidade de modalidades cadastradas
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/menu.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws ArqException
	 */
	public String remover() throws ArqException{
		
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		prepareMovimento(ArqListaComando.ALTERAR);
		
		GenericDAO dao = null; 
		

		try {
			
			dao = getGenericDAO();
			
			obj = new ModalidadeParticipante();
			obj.setId(getParameterInt("id"));
			
			obj = dao.refresh(obj);
			
			// Aqui impede dele executar 2 vezes //
			if (obj != null && obj.isAtivo()){
				
				obj.setAtivo(false);  // desativa, os coordenadore n�o v�o poder associar novos valores para esse modalidade, as antigas continuam
				
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				mov.setObjMovimentado(obj);
				execute(mov);
				
				all = null;  // para atualizar alistagem
			
				addMensagemInformation("Modalidade de Participante removida com sucesso. ");
				
				return telaListar();
				
			} else{
				addMensagemErro("Modalidade de Participante j� foi removida.");
			}

		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}finally{
			if(dao != null ) dao.close();
		}
		
		return telaListar();
	}
	
	
	
	
	
	/**
	 * Retorna todas as modalidades ativas, aqui n�o precisa fazer proje��o porque s�o poucas 
	 * inforama��es e a entidade n�o tem relacioanmentos.
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/ModalidadeParticipante/lista.jsp</li>
	 *   </ul>
	 *
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	public Collection <ModalidadeParticipante> getAll() throws DAOException{
		
		if (all == null)
			all = getGenericDAO().findAllAtivos(ModalidadeParticipante.class, "nome");
		
		return all;
	}
	
	/**
	 * Metodo que retorna a quantidade de modalidades cadastradas
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/ModalidadeParticipante/lista.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws ArqException
	 */
	public int getSize () throws ArqException{
		// limpa a lista para que seja atualizada.
		return getAll().size();
	}
	
	
	/**
	 *    <p>M�todo usado para combo box simples de modalidades e participantes, n�o h� problema 
     * de semprem buscar tudo porque essa tabela n�o deve passar de 10 linhas e n�o tem 
     * relacionamento com outras tabelas, as outras tabelas que tem com ela</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/formPeriodoInscricaoAtividade.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getAllModalidadeParticipantesAtivasCombo() throws DAOException{
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO();
			return toSelectItems(  dao.findAllAtivos(ModalidadeParticipante.class, "nome"), "id", "nome");
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	
	
	
	
	/**
	 *  <p>A tela de listagem.</p>
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaListar(){
		return forward(PAGINA_LISTA);
	}
	
	/**
	 *  <p>A tela de form.</p>
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaForm(){
		return forward(PAGINA_FORM);
	}
	
}
