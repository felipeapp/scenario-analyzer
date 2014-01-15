/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 30/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dao.SubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.GerenciarParticipantesCursosEventoExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.helper.EnviarEmailExtensaoHelper;
import br.ufrn.sigaa.extensao.jsf.ContatosExcelParticipanteAcaoExtensaoMBean;



/**
 * <p> MBean exclusivo para o coordenador realizar algumas opera��es sobre os participantes das atividade de extens�o</p>
 *
 * <p> <i> Esse Mbean permite listar os participantes, emitir a lista de presen�a, emitir a lista de contatos, etc... </i> </p>
 * 
 * @author jadson
 *
 */
@Component("listaAtividadesParticipantesExtensaoMBean")
@Scope("request")
public class ListaAtividadesParticipantesExtensaoMBean extends SigaaAbstractController<ParticipanteAcaoExtensao>{

	
	/** Lista atividade e sub atividade que podem ter seus participantes gerenciados. */
	private static final String LISTA_CURSOS_EVENTOS_PARA_GERENCIAR_PARTICIPANTES = "/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciarParticipantes.jsp";
	
	/** Para para a impress�o dos participantes da a��o de extens�o */
	public static final String LISTA_PARTICIPANTES_EXTENSAO_IMPRESSAO = "/extensao/ParticipanteAcaoExtensao/lista_participantes_extensao_impressao.jsp";
	
	/** Para para a impress�o da lista de presen�a dos participantes da a��o de extens�o */
	public static final String LISTA_PRESENCA_PARTICIPANTES_EXTENSAO_IMPRESSAO = "/extensao/ParticipanteAcaoExtensao/lista_presenca_participantes_extensao_impressao.jsp";
	
	/** Para para a impress�o das informa��es de contado dos participantes. */
	public static final String LISTA_LISTA_INFORMACOES_CONTADO_PARCICIPANTES = "/extensao/ParticipanteAcaoExtensao/lista_contatos_participantes_extensao_impressao.jsp";
	
	/** Para para a impress�o das informa��es de contado dos participantes. */
	public static final String NOTIFICACAO_PARTICIPANTES = "/extensao/ParticipanteAcaoExtensao/notificacao_participantes.jsp";
	
	
	
	
	/** A listagem geral de cursos que podem ter seus participantes gerenciados pelo coordenador. (Cont�m os dados extritamente necess�rios )*/
	private List<AtividadeExtensao> cursosEventosParaGerenciarParticipantes;

	/** A atividade dos participantes est�o inscritos. ( Cont�m informa��es extras para serem mostradas ao coordenador quando ele selecionada uma atividade ) */
	private AtividadeExtensao atividadeSelecionada;
	
	/** A mini atividade dos participantes est�o inscritos. ( Cont�m informa��es extras para serem mostradas ao coordenador quando ele selecionada uma mini atividade ) */
	private SubAtividadeExtensao subatividadeSelecionada;
	
	/** Lista os participantes selecionados para a atividade*/
	private List<ParticipanteAcaoExtensao> participantes;
	

	/** Mensagem de email enviada para os participantes digitada pelo coordenador da a��o. */
	private String mensagemEmailParticipantes;
	
	
	
	/**
	 * 
	 * <p>Inicia o caso de uso, listando as atividades e mini atividades cujo o usu�rio logado � coordenador.</p>
	 *  
	 *  <p>Observa��o: Tras as atividades e mini atividades numa consulta s�. Sem precisa fazer duas buscas e mostrar duas 
	 *  listagens para o coordendador.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaInscritosCursosEventosExtensao.jsp</li>
	 *    <li>/sigaa.war/portais/docentes/menu_docente.jsp</li>
	 *    <li>/sigaa.war/extensao/menu.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String listarAtividadesComParticipantesCoordenador() throws DAOException {
		
		if (!getAcessoMenu().isCoordenadorExtensao()) {
			addMensagemErro("Usu�rio n�o autorizado a realizar esta opera��o");
			return null;
		}
		
		GerenciarParticipantesCursosEventoExtensaoDao dao = null;
		
		try{
			
			dao = getDAO(GerenciarParticipantesCursosEventoExtensaoDao.class);
			
			cursosEventosParaGerenciarParticipantes = dao
					.findAtividadesExtensaoGlobaGerenciarParticipantesByCoordenador(getUsuarioLogado().getServidor());
			
		}finally{
			if(dao != null ) dao.close();
		}
		
		return telaListaAtividadeParaGerenciarParticipantes();
	}
	
	
	
	//////////////////////// As opera��es que o usu�rio pode fazer ////////////////////////////
	
	
	
	/**
	 * Lista os participantes da atividade ou sub atividade em forma de relat�rio para impress�o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciarParticipantes.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String listarParticipantesEmFormatoRelatorio() throws ArqException {
		
		Integer idAtividadeExtensao = getParameterInt("idAtividadeSelecionada");
		Integer idSubAtividadeExtensao = getParameterInt("idSubAtividadeSelecionada");
		
		ParticipanteAcaoExtensaoDao daoParticipantes = null;
		AtividadeExtensaoDao daoAtividade = null;
		SubAtividadeExtensaoDao daoSubAtividade = null;
		
		try{
			daoParticipantes = getDAO(ParticipanteAcaoExtensaoDao.class);
			daoAtividade = getDAO(AtividadeExtensaoDao.class);
			daoSubAtividade = getDAO(SubAtividadeExtensaoDao.class);
			
			if(idAtividadeExtensao != null){
				atividadeSelecionada = daoAtividade.findInformacoesAlteracaoAtividadeExtensao(idAtividadeExtensao);
				subatividadeSelecionada = null;
			}else{
				subatividadeSelecionada = daoSubAtividade.findInformacoesAlteracaoSubAtividadeExtensao(idSubAtividadeExtensao);
				atividadeSelecionada = null;
			}
			
			participantes =  daoParticipantes.findAllParticipantesParaListagem(idAtividadeExtensao, idSubAtividadeExtensao);
	    
	    //se participantes � nulo, nao redireciona para pagina de listagem
	    if(participantes == null || participantes.size() <= 0){
	    	addMensagemErro("N�o existem participantes cadastrados para esta a��o de extens�o.");
	    	return null;
	    }
	    
		}finally{
			if(daoParticipantes != null) daoParticipantes.close();
			if(daoAtividade != null) daoAtividade.close();
			if(daoSubAtividade != null) daoSubAtividade.close();
		}
	    
	    return telaListaParticipantesParaImpressao();
	}
	
	
	
	/**
	 * Lista os participantes da atividade ou sub atividade em forma de lista de presen�a.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciarParticipantes.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String listarParticipantesEmFormatoListaPresenca() throws ArqException {
		
		Integer idAtividadeExtensao = getParameterInt("idAtividadeSelecionada");
		Integer idSubAtividadeExtensao = getParameterInt("idSubAtividadeSelecionada");
		
		ParticipanteAcaoExtensaoDao daoParticipantes = null;
		AtividadeExtensaoDao daoAtividade = null;
		SubAtividadeExtensaoDao daoSubAtividade = null;
		
		try{
			daoParticipantes = getDAO(ParticipanteAcaoExtensaoDao.class);
			daoAtividade = getDAO(AtividadeExtensaoDao.class);
			daoSubAtividade = getDAO(SubAtividadeExtensaoDao.class);
			
			if(idAtividadeExtensao != null){
				atividadeSelecionada = daoAtividade.findInformacoesAlteracaoAtividadeExtensao(idAtividadeExtensao);
				subatividadeSelecionada = null;
			}else{
				subatividadeSelecionada = daoSubAtividade.findInformacoesAlteracaoSubAtividadeExtensao(idSubAtividadeExtensao);
				atividadeSelecionada = null;
			}
			
			participantes =  daoParticipantes.findAllParticipantesParaListaPresenca(idAtividadeExtensao, idSubAtividadeExtensao);
	    
	    //se participantes � nulo, nao redireciona para pagina de listagem
	    if(participantes == null || participantes.size() <= 0){
	    	addMensagemErro("N�o existem participantes cadastrados para esta a��o de extens�o.");
	    	return null;
	    }
	    
		}finally{
			if(daoParticipantes != null) daoParticipantes.close();
			if(daoAtividade != null) daoAtividade.close();
			if(daoSubAtividade != null) daoSubAtividade.close();
		}
	    
	    return telaListaPresencaParticipantes();
	}
	
	
	/**
	 * Lista as informa��es de contado(email, telefone, celular) dos participantes da atividade 
	 * ou sub atividade em formato de relat�rio para o coordenador.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciarParticipantes.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String listarInformacoesContatosParticipantesEmFormatoRelatorio() throws ArqException {
		
		Integer idAtividadeExtensao = getParameterInt("idAtividadeSelecionada");
		Integer idSubAtividadeExtensao = getParameterInt("idSubAtividadeSelecionada");
		
		ParticipanteAcaoExtensaoDao daoParticipantes = null;
		AtividadeExtensaoDao daoAtividade = null;
		SubAtividadeExtensaoDao daoSubAtividade = null;
		
		try{
			daoParticipantes = getDAO(ParticipanteAcaoExtensaoDao.class);
			daoAtividade = getDAO(AtividadeExtensaoDao.class);
			daoSubAtividade = getDAO(SubAtividadeExtensaoDao.class);
			
			if(idAtividadeExtensao != null){
				atividadeSelecionada = daoAtividade.findInformacoesAlteracaoAtividadeExtensao(idAtividadeExtensao);
				subatividadeSelecionada = null;
			}else{
				subatividadeSelecionada = daoSubAtividade.findInformacoesAlteracaoSubAtividadeExtensao(idSubAtividadeExtensao);
				atividadeSelecionada = null;
			}
			
			participantes =  daoParticipantes.findInformacoesContatoAllParticipantes(idAtividadeExtensao, idSubAtividadeExtensao);
	    
	    //se participantes � nulo, nao redireciona para pagina de listagem
	    if(participantes == null || participantes.size() <= 0){
	    	addMensagemErro("N�o existem participantes cadastrados para esta a��o de extens�o.");
	    	return null;
	    }
	    
		}finally{
			if(daoParticipantes != null) daoParticipantes.close();
			if(daoAtividade != null) daoAtividade.close();
			if(daoSubAtividade != null) daoSubAtividade.close();
		}
	    
	    return telaListaInformacoesContatosParticipantes();
	}
	
	
	
	/**
	 *  Lista as informa��es de contado(email, telefone, celular) dos participantes da atividade 
	 * ou sub atividade em formato de arquivo xml para o coordenador.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciarParticipantes.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String listarInformacoesContatosParticipantesEmFormatoPlanilha() throws ArqException {
		
		Integer idAtividadeExtensao = getParameterInt("idAtividadeSelecionada");
		Integer idSubAtividadeExtensao = getParameterInt("idSubAtividadeSelecionada");
		
		ParticipanteAcaoExtensaoDao daoParticipantes = null;
		AtividadeExtensaoDao daoAtividade = null;
		SubAtividadeExtensaoDao daoSubAtividade = null;
		
		try{
			daoParticipantes = getDAO(ParticipanteAcaoExtensaoDao.class);
			daoAtividade = getDAO(AtividadeExtensaoDao.class);
			daoSubAtividade = getDAO(SubAtividadeExtensaoDao.class);
			
			if(idAtividadeExtensao != null){
				atividadeSelecionada = daoAtividade.findInformacoesAlteracaoAtividadeExtensao(idAtividadeExtensao);
				subatividadeSelecionada = null;
			}else{
				subatividadeSelecionada = daoSubAtividade.findInformacoesAlteracaoSubAtividadeExtensao(idSubAtividadeExtensao);
				atividadeSelecionada = null;
			}
			
			participantes =  daoParticipantes.findInformacoesContatoAllParticipantes(idAtividadeExtensao, idSubAtividadeExtensao);
	    
		    //se participantes � nulo, nao redireciona para pagina de listagem
		    if(participantes == null || participantes.size() <= 0){
		    	addMensagemErro("N�o existem participantes cadastrados para esta a��o de extens�o.");
		    	return null;
		    }
		    
		    ContatosExcelParticipanteAcaoExtensaoMBean contatosSheet = new ContatosExcelParticipanteAcaoExtensaoMBean(
		    		atividadeSelecionada != null ? atividadeSelecionada.getTitulo() : subatividadeSelecionada.getTitulo()
		    		, participantes);
		    
			contatosSheet.buildSheet();
	    
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErro("Erro ao exportar os dados dos particiapantes para uma planilha");
		}finally{
			if(daoParticipantes != null) daoParticipantes.close();
			if(daoAtividade != null) daoAtividade.close();
			if(daoSubAtividade != null) daoSubAtividade.close();
		}
	    
		return null;
	}
	
	
	
	/**
     * Redireciona para tela de notifica��o dos participantes. Onde o coordenador vai digitar o texto do email e ser enviado
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  	<li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciarParticipantes.jsp</li>
     * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String preNotificarParticipantes() throws ArqException{

		Integer idAtividadeExtensao = getParameterInt("idAtividadeSelecionada");
		Integer idSubAtividadeExtensao = getParameterInt("idSubAtividadeSelecionada");
		
		ParticipanteAcaoExtensaoDao daoParticipantes = null;
		AtividadeExtensaoDao daoAtividade = null;
		SubAtividadeExtensaoDao daoSubAtividade = null;
		
		try{
			daoParticipantes = getDAO(ParticipanteAcaoExtensaoDao.class);
			daoAtividade = getDAO(AtividadeExtensaoDao.class);
			daoSubAtividade = getDAO(SubAtividadeExtensaoDao.class);
			
			if(idAtividadeExtensao != null){
				atividadeSelecionada = daoAtividade.findInformacoesAlteracaoAtividadeExtensao(idAtividadeExtensao);
				subatividadeSelecionada = null;
			}else{
				subatividadeSelecionada = daoSubAtividade.findInformacoesAlteracaoSubAtividadeExtensao(idSubAtividadeExtensao);
				atividadeSelecionada = null;
			}
			
			participantes =  daoParticipantes.findEmailAllParticipantes(idAtividadeExtensao, idSubAtividadeExtensao);
	    
		    //se participantes � nulo, nao redireciona para pagina de listagem
		    if(participantes == null || participantes.size() <= 0){
		    	addMensagemErro("N�o existem participantes cadastrados para esta a��o de extens�o.");
		    	return null;
		    }
		    
		    mensagemEmailParticipantes = "";
		    
		}finally{
			if(daoParticipantes != null) daoParticipantes.close();
			if(daoAtividade != null) daoAtividade.close();
			if(daoSubAtividade != null) daoSubAtividade.close();
		}
	    
		return telaNotificacaoParticipantes();

	}
	
	
	/**
	 * 
	 * Email um email a todos os participantes da a��o de extens�o. Com o texto digitado pelo coordenador.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/ParticipanteAcaoExtensao/notificacao_participantes.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws NegocioRemotoException
	 */
   public String notificarParticipantes() {
	
	   String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
	   
	   // Manda a mensagem com emails ocultos para nenhum participante ficar sabendo o email do outro.
	   List<String> bccs = new ArrayList<String>();
	   
	   for (ParticipanteAcaoExtensao participante : participantes) {
		   bccs.add(participante.getCadastroParticipante().getEmail() );
	   }
	   
	   if(StringUtils.isEmpty(mensagemEmailParticipantes)){
		   addMensagemErro("Digite o texto do email a ser enviado aos participantes ");
		   return null;
	   }
	   
	   // Envia para todos os participantes, com c�pia para o coordenador
	   EnviarEmailExtensaoHelper.enviarEmail(
			   "["+siglaSistema+"]"+" Notifica��o de Participantes da A��o Extens�o ", 
			   " Notifica��o de Participantes da A��o Extens�o Enviada pelo Coordenador ", null, null, mensagemEmailParticipantes, null, bccs);
	   
	   addMensagemInformation("E-mails enviados com sucesso!");
	   
	   return telaListaAtividadeParaGerenciarParticipantes();	
	
   }
	
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	////////////////////////  Telas de Navega��o /////////////////////////////
	
	
	/**
	 * Tela que lista as atividades para gerenciar participantes.
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaListaAtividadeParaGerenciarParticipantes() {
		return forward(LISTA_CURSOS_EVENTOS_PARA_GERENCIAR_PARTICIPANTES);
	}

	
	
	/**
	 * Tela que lista os participantes para impress�o
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaListaParticipantesParaImpressao() {
		return forward(LISTA_PARTICIPANTES_EXTENSAO_IMPRESSAO);
	}
	
	/**
	 * Tela que lista os participantes para impress�o
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaListaPresencaParticipantes() {
		return forward(LISTA_PRESENCA_PARTICIPANTES_EXTENSAO_IMPRESSAO);
	}
	
	
	/**
	 * Tela que lista os participantes para impress�o
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaListaInformacoesContatosParticipantes() {
		return forward(LISTA_LISTA_INFORMACOES_CONTADO_PARCICIPANTES);
	}
	
	/**
	 * Tela que lista os participantes para impress�o
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaNotificacaoParticipantes() {
		return forward(NOTIFICACAO_PARTICIPANTES);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	/**
	 * Retorna a quantidade de cursos e eventos encontrados.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdCursosEventosParaGerenciarParticipantes() {
		if(cursosEventosParaGerenciarParticipantes == null)
			return 0;
		else
			return cursosEventosParaGerenciarParticipantes.size();
	}
	
	/** Retorna a quantidade de participantes. */
	public int getQtdParticipantes() {
		if(participantes == null)
			return 0;
		else
			return participantes.size();
	}
	
	/** Verifica se est� gerenciando os participantes de uma atividade ou sub atividade */
	public boolean isGerenciandoParticipantesAtividade(){
		if(atividadeSelecionada != null && subatividadeSelecionada == null)
			return true;
		else
			return false;
	}
	
	/////////////////////////// sets e gets ///////////////////////////
	
	public List<AtividadeExtensao> getCursosEventosParaGerenciarParticipantes() {
		return cursosEventosParaGerenciarParticipantes;
	}
	public void setCursosEventosParaGerenciarParticipantes(List<AtividadeExtensao> cursosEventosParaGerenciarParticipantes) {
		this.cursosEventosParaGerenciarParticipantes = cursosEventosParaGerenciarParticipantes;
	}
	public List<ParticipanteAcaoExtensao> getParticipantes() {
		return participantes;
	}
	public void setParticipantes(List<ParticipanteAcaoExtensao> participantes) {
		this.participantes = participantes;
	}
	public AtividadeExtensao getAtividadeSelecionada() {
		return atividadeSelecionada;
	}
	public void setAtividadeSelecionada(AtividadeExtensao atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}
	public SubAtividadeExtensao getSubatividadeSelecionada() {
		return subatividadeSelecionada;
	}
	public void setSubatividadeSelecionada(SubAtividadeExtensao subatividadeSelecionada) {
		this.subatividadeSelecionada = subatividadeSelecionada;
	}
	public String getMensagemEmailParticipantes() {
		return mensagemEmailParticipantes;
	}
	public void setMensagemEmailParticipantes(String mensagemEmailParticipantes) {
		this.mensagemEmailParticipantes = mensagemEmailParticipantes;
	}
	
	

	
	
}
