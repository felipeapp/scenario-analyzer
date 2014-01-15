/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 17/12/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dao.EmailsNotificacaoServicosBibliotecaDao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.EmailsNotificacaoServicosBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * <p> Gerencia o cadastros dos emais de notifica��o de cada biblioteca no sistema. </p>
 *
 * <p> <i> Usado principalmente nos casos de uso de informa��o e refer�ncia. Quando o usu�rio 
 * solicita algum servi�o o sistema envia um email para quem precisa atender.</i> 
 * </p>
 * 
 * @author jadson
 *
 */
@Component("emailsNotificacaoServicosBibliotecaMBean")
@Scope("request")
public class EmailsNotificacaoServicosBibliotecaMBean extends SigaaAbstractController<EmailsNotificacaoServicosBiblioteca> {

	
	/**
	 * P�gina de listagem
	 */
	public static final String PAGINA_LISTA = "/biblioteca/EmailsNotificacaoServicosBiblioteca/lista.jsp";
	
	
	/**
	 * P�gina de cadatro e altera��o.
	 */
	public static final String PAGINA_FORM = "/biblioteca/EmailsNotificacaoServicosBiblioteca/form.jsp";
	
	
	/** Para o usu�rio escolher no combo box se ele quer visualizar de uma biblioteca espec�fica. */
	private int idBibliotecaFiltragem = -1;
	
	
	/**
	 * Inicia o caso de uso listando os emails cadatrados no sistema.
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menu.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public String listar() throws DAOException{ 
		return telaListar();
	}
	
	
	
	/**
	 * Redireciona para a p�gina de atualza��o carregando as informa��es.
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
		
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		
		int valorTipoServico = getParameterInt("valorTipoServico");
		int idBilioteca = getParameterInt("idBilioteca");
		
		obj = new EmailsNotificacaoServicosBiblioteca(TipoServicoInformacaoReferencia.getTipoServico(valorTipoServico), new Biblioteca(idBilioteca));
		
		for (EmailsNotificacaoServicosBiblioteca emailNotificacao : all) {
			if( emailNotificacao.equals(obj) )
				obj = emailNotificacao;
		}
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		prepareMovimento(ArqListaComando.ALTERAR);
		
		
		return telaForm();
	}
	
	
	/**
	 *  M�todo chamado pela altera��o para confirmar a altera��o o objeto.
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmailsNotificacaoServicosBiblioteca/form.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws ArqException
	 */
	public String atualizar() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
		
		// Prepara o movimento, setando o objeto
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);

		try {
			
			ListaMensagens erros =  obj.validate();
			
			if(erros.isErrorPresent()){
				throw new NegocioException(erros);
			}
			
			// tira os espa�os em branco //
			obj.setEmailsNotificacao(  obj.getEmailsNotificacao().replaceAll("\\s", "") );
			
			
			// Se for operacao de cadastrar, a id do objeto sera' igual a zero
			if (obj.getId() == 0){
				// Seta a operacao como cadastrar
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				// Tenta executar a operacao
				execute(mov);
				
				// Se chegou aqui, nao houve erros. Exibe a mensagem de sucesso.
				addMensagemInformation("E-mails de notifica��o cadatrados com sucesso.");
				
			} else {
				/* Nao era operacao de cadastrar, entao e' de alterar */
				// Seta a operacao como alterar
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				// Tenta executar a operacao
				execute(mov);
				
				// Se chegou aqui, nao houve erros. Exibe a mensagem de sucesso.
				addMensagemInformation("E-mails de notifica��o alterados com sucesso.");
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
	 * Verifica se o usu�rio pode alterar os e mails de notifica��o da biblioteca.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmailsNotificacaoServicosBiblioteca/form.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public boolean isPodeAlterarEmailsBiblioteca(){
		if(isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL))
			return true;
		else{
			
			List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
					getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
		
			if(idUnidades.contains(obj.getBiblioteca().getUnidade().getId()))
				return true;
			else
				return false;
		}
	}
	
	
	/**
	 * Retorna todas as modalidades ativas, aqui n�o precisa fazer proje��o porque s�o poucas 
	 * inforama��es e a entidade n�o tem relacioanmentos.
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmailsNotificacaoServicosBiblioteca/lista.jsp</li>
	 *   </ul>
	 *
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	public Collection <EmailsNotificacaoServicosBiblioteca> getAll() throws DAOException{
		
		BibliotecaDao dao = null;
		EmailsNotificacaoServicosBibliotecaDao dao2 = null;
		
		try{
			
			if (all == null){
				dao = getDAO(BibliotecaDao.class);
				dao2 = getDAO(EmailsNotificacaoServicosBibliotecaDao.class);
				
				if(idBibliotecaFiltragem <=0 )
					all = dao2.findAllEmailsNotificacaoServicosBiblioteca();
				else
					all = dao2.findEmailsNotificacaoServicosBibliotecaByBiblioteca(idBibliotecaFiltragem);
				
				List<Biblioteca>  bibliotecasAtivas = new ArrayList<Biblioteca>();
				
				if(idBibliotecaFiltragem <=0 ){
					bibliotecasAtivas = dao.findAllBibliotecasInternasAtivas();
				}else
					bibliotecasAtivas.add( dao.findByPrimaryKey(idBibliotecaFiltragem, Biblioteca.class, " id, identificador, descricao, unidade.id "));
				
				List<TipoServicoInformacaoReferencia> tiposServicos =  TipoServicoInformacaoReferencia.getTipoServicoInformacaoReferencia();
				
				
				/*
				 * Percorre os bibliotecas internas ativas e os tipos de servi�o e preenche    
				 * a listagem com aqueles que ainda n�o est�o persistidos                    
				 */
				for(Biblioteca biblioteca : bibliotecasAtivas){
					for(TipoServicoInformacaoReferencia tipoServico : tiposServicos){
						EmailsNotificacaoServicosBiblioteca emailNotificacao =  new EmailsNotificacaoServicosBiblioteca(tipoServico, biblioteca);
						if( ! all.contains(emailNotificacao))
							all.add(emailNotificacao);
					}
				}
			}
		
		}finally{
			if(dao != null) dao.close();
			if(dao2 != null) dao2.close();
		}
		
		return all;
	}
	
	
	/**
	 * Atualiza os resultados quando o usu�rio seleciona uma biblioteca espec�fica.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmailsNotificacaoServicosBiblioteca/lista.jsp</li>
	 *   </ul>
	 *
	 * @param evnt
	 */
	public void filtarResultadosPorBiblioteca(ValueChangeEvent evnt){
		all = null;
	}
	
	
	public int getIdBibliotecaFiltragem() {
		return idBibliotecaFiltragem;
	}

	public void setIdBibliotecaFiltragem(int idBibliotecaFiltragem) {
		this.idBibliotecaFiltragem = idBibliotecaFiltragem;
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
