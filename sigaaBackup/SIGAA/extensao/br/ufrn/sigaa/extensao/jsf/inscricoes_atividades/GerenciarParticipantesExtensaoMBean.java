/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 03/12/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.TipoParticipacaoAcaoExtensaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.CadastroParticipanteAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoParticipacaoAcaoExtensao;
import br.ufrn.sigaa.extensao.helper.EnviarEmailExtensaoHelper;

/**
 *
 * <p>MBean que gerenciar os participantes de uma atividade ou mini atividad de extensão</p>
 *
 *  <p> <i> Gerenciar participantes é cadatrar um novo sem ter feito inscrição, atribuir frequência, emitir 
 *   certificados e declarações, etc... </i> </p>
 * 
 * @author jadson
 *
 */
@Component("gerenciarParticipantesExtensaoMBean")
@Scope("request")
public class GerenciarParticipantesExtensaoMBean extends AbstractGerenciarParticipantesExtensaoMBean implements PesquisarParticipanteExtensao{

	
	/** Lista TODOS os participantes da atividade ou sub atividade selecionada. */
	private static final String LISTA_PARTICIPANTES_PARA_GERENCIAR = "/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp";
	
	/** Para para o coordenador inscrever um participante na atividade, se que o participantes realize a inscrição. */
	private static final String PAGINA_ADICIONA_PARTICIPANTE_ATIVIDADE_COORDENADOR = "/extensao/GerenciarInscricoes/paginaAdicionaParticipanteAtividadeCoordenador.jsp";

	
	/** Para para o coordenador confirmar se deseja mesmo remover o participante da atividade. */
	private static final String PAGINA_CONFIRMA_REMOCAO_PARTICIPANTE_ATIVIDADE_COORDENADOR = "/extensao/GerenciarInscricoes/paginaConfirmaRemocaoParticipanteAtividadeCoordenador.jsp";
	
	
	/** O cadastro de um aparticipantes selecionado pelo coordenador para incluir um novo participante a atividade ou mini atividade */
	private CadastroParticipanteAtividadeExtensao cadastroSelecionadoCoordenador;
	
	

	/** Mensagem de email enviada para os participantes digitada pelo coordenador da ação. */
	protected String mensagemEmailParticipantes;
	
	
	/** Indica se está adicionando ou alterando as informações de um participante. */
	private boolean alterandoParticipante  = false;
	
	
	/**
	 * 
	 * Lista os participantes da atividades selecionada para emitir certificados, etc...
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciarParticipantes.jsp</li>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String listarParticipantesAtividade() throws DAOException{
		
		Integer idAtividadeExtensao = getParameterInt("idAtividadeSelecionada");
		Integer idSubAtividadeExtensao = getParameterInt("idSubAtividadeSelecionada");
		
		return listarParticipantesAtividade(idAtividadeExtensao, idSubAtividadeExtensao);
	}
	
	
	
	/**
	 *  <p>Atualiza a listagem de particpantes.</p> 
	 *  <p>Usado quando o coordenador adiciona ou remove participante, deve-se buscar novamente.</p>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @param idAtividadeExtensao
	 * @param idSubAtividadeExtensao
	 * @return
	 * @throws DAOException
	 */
	public String listarParticipantesAtividade(Integer idAtividadeExtensao, Integer idSubAtividadeExtensao) throws DAOException{
		super.listarParticipantesAtividade(idAtividadeExtensao, idSubAtividadeExtensao);
		return telaListaParticipantesParaGerenciar();
	}
	
	
	
	/**
	 *  Realiza efetivamente a busca de participantes.
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String buscarParticipantesAtividadePaginado() throws DAOException{
		super.buscarParticipantesAtividadePaginado();
		return telaListaParticipantesParaGerenciar();
	}
	
	
	/**
	 * 
	 * Usado quando o usuário busca a partir do furmulário na página, nesse caso deve-se será as 
	 * informações de paginação. Porque o usuário pode alterar a quantidade de resultados por página.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaParticipantesParaGerenciar.jsp</li>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String filtarParticipantesAtividade() throws DAOException{
		super.filtarParticipantesAtividade();
		return telaListaParticipantesParaGerenciar();
	}
	
	
	/**
	 *  <p>Começa o cadatro de uma nova participante na atividad ou mini atividade por parte do coordenador.</p>
	 *  
	 *  <p>O primeiro passo é o coordenador busca os cadatros já existentes, ou realizar um novo cadastro para o participante.</p>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String preCadastrarNovoParticipante(){
		
		BuscaPadraoParticipanteExtensaoMBean mBean = (BuscaPadraoParticipanteExtensaoMBean) getMBean("buscaPadraoParticipanteExtensaoMBean");
		return mBean.iniciaBuscaSelecaoParticipanteExtensao(this, "Adicionar Participante",
				" <p>Caro(a) Coordenador(a), </p>"
				+" <p>Esse opção permite adicionar um novo participante à atividade ou mini atividade de extensão. </p>"
				+" <p>Caso o participante já possua cadastro no sistema devido a atividade anteriores, basta apenas selecionar o participante existente. " +
				" Caso o participante nunca tenha participando de uma atividade de extensão, será necessário realizar o seu cadastro completo " +
				"para manter a consistência e bom funcionamento do sistema. </p>"+
				" <br/>"+
				"<p> Se não for possível obter todas informações necessárias para o cadastro do participante como CPF, endereço e e-mail infelizmente " +
				"o participante não poderá ser associado a atividade de extensão no sistema, esse controle deverá ser feito manualmente. </p>", true, false);
	}
	
	

	///////////////////////// Métodos da interface de busca de participantes ////////////////////////////

	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.extensao.jsf.inscricoes_atividades.PesquisarParticipanteExtensao#setParticipanteExtensao(br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao)
	 */
	@Override
	public void setParticipanteExtensao(CadastroParticipanteAtividadeExtensao participante) {
		this.cadastroSelecionadoCoordenador = participante;
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.extensao.jsf.inscricoes_atividades.PesquisarParticipanteExtensao#selecionouParticipanteExtensao()
	 */
	@Override
	public String selecionouParticipanteExtensao() throws ArqException {
		
		prepareMovimento(SigaaListaComando.INCLUIR_PARTICIPANTE_ATIVIDADE_PELO_COORDENADOR);
		prepareMovimento(SigaaListaComando.INCLUIR_PARTICIPANTE_MINI_ATIVIDADE_PELO_COORDENADOR);
		
		obj = new ParticipanteAcaoExtensao();
		obj.setInscricaoAtividadeParticipante(null); // não tem inscrição
		
		CadastroParticipanteAtividadeExtensaoDao dao = getDAO(CadastroParticipanteAtividadeExtensaoDao.class);
		obj.setCadastroParticipante(dao.findCadastroParticipanteById(cadastroSelecionadoCoordenador.getId()));
		
		if(isGerenciandoParticipantesAtividade())
			obj.setAtividadeExtensao(atividadeSelecionada);
		else
			obj.setSubAtividade(subatividadeSelecionada);
		
		obj.setTipoParticipacao(new TipoParticipacaoAcaoExtensao());
		obj.setAtivo(true);
		
		alterandoParticipante  = false;
		
		return telaAdicionaParticipanteAtividadeCoordenador();
	}
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.extensao.jsf.inscricoes_atividades.PesquisarParticipanteExtensao#cancelarPesquiasParticipanteExtensao()
	 */
	public String cancelarPesquiasParticipanteExtensao(){
		return telaListaParticipantesParaGerenciar();
	}
	
	//////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * <p>Confima a adicição do participante de atividade ou sub atividade de extensão.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/paginaAdicionaParticipanteAtividadeCoordenador.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String confirmaIncluscaoParticipanteAtividade() throws ArqException{
	
		MovimentoCadastro movimento = new MovimentoCadastro();
		
		movimento.setObjMovimentado(obj);
		
		if(isGerenciandoParticipantesAtividade()){
			movimento.setCodMovimento(SigaaListaComando.INCLUIR_PARTICIPANTE_ATIVIDADE_PELO_COORDENADOR);
		}else{
			movimento.setCodMovimento(SigaaListaComando.INCLUIR_PARTICIPANTE_MINI_ATIVIDADE_PELO_COORDENADOR);
		}
		
		try {
			execute(movimento);
			
			addMensagemInformation("Participante adicionado com sucesso.");
			
			notificarParticipanteIncluidoAtividade(obj.getCadastroParticipante());
			
			// atualiza a listegem com a nova quantidade de participantes inscritos //
			return listarParticipantesAtividade( atividadeSelecionada != null ? atividadeSelecionada.getId() : null
					, subatividadeSelecionada != null ? subatividadeSelecionada.getId() : null);
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		
	}
	
	
	/**
	 * <p>Método que permite ao coordenador alterar as informações do participante como tipo e a observação no certificado.</p>
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciar.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String preAlterarParticipante() throws ArqException{
	
		prepareMovimento(SigaaListaComando.ALTERA_PARTICIPANTE_EXTENSAO_PELO_COORDENADOR);
		
		CadastroParticipanteAtividadeExtensaoDao dao =  null;
		
		try{
			
			dao = getDAO(CadastroParticipanteAtividadeExtensaoDao.class);
			
			// busca apenas as inforamação que serão alteradas na página.
			obj = dao.findByPrimaryKey(getParameterInt("idParticipante"), ParticipanteAcaoExtensao.class
					, "id, tipoParticipacao.id, frequencia, autorizacaoCertificado, autorizacaoDeclaracao, observacaoCertificado");
		
			if(isGerenciandoParticipantesAtividade())
				obj.setAtividadeExtensao(atividadeSelecionada);
			else
				obj.setSubAtividade(subatividadeSelecionada);
			
			alterandoParticipante  = true;
			
		}finally{
			if( dao != null) dao.close();
		}
		
		return telaAdicionaParticipanteAtividadeCoordenador();
		
	}
	
	
	/**
	 * <p>Confima a alteração das informações do participante de atividade ou sub atividade de extensão.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/paginaAdicionaParticipanteAtividadeCoordenador.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String confirmaAlteracaoParticipante() throws ArqException{
	
		MovimentoCadastro movimento = new MovimentoCadastro();
		
		movimento.setObjMovimentado(obj);
		
		movimento.setCodMovimento(SigaaListaComando.ALTERA_PARTICIPANTE_EXTENSAO_PELO_COORDENADOR);
		
		
		try {
			execute(movimento);
			
			addMensagemInformation("Participante alterado com sucesso.");
			
			// atualiza a listegem com a nova quantidade de participantes inscritos //
			return listarParticipantesAtividade( atividadeSelecionada != null ? atividadeSelecionada.getId() : null
					, subatividadeSelecionada != null ? subatividadeSelecionada.getId() : null);
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * <p>Método que remove o participante na atividade / mini atividade</p> 
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciar.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String preRemoverParticipanteAtividade() throws ArqException{

		prepareMovimento(SigaaListaComando.REMOVE_PARTICIPANTE_ATIVIDADE_PELO_COORDENADOR);
		prepareMovimento(SigaaListaComando.REMOVE_PARTICIPANTE_MINI_ATIVIDADE_PELO_COORDENADOR);
		
		MovimentoCadastro movimento = new MovimentoCadastro();
		
		int idParticipante = getParameterInt("idParticipante");
		
		obj = null;
		
		for (ParticipanteAcaoExtensao participante : participantes) {
			if(participante.getId() == idParticipante){
				obj = participante;
				break;
			}
		}
		
		
		if(obj == null){
			addMensagemErro("Participante selecionado não está na listagem");
			return null;
		}
		
		CadastroParticipanteAtividadeExtensaoDao dao =  null;
		
		try{
			
			dao = getDAO(CadastroParticipanteAtividadeExtensaoDao.class);
			
			// busca mais algunas informações para complementar a visualização.
			ParticipanteAcaoExtensao temp = dao.findByPrimaryKey(obj.getId(), ParticipanteAcaoExtensao.class
					, " tipoParticipacao.id, observacaoCertificado");
			
			obj.setTipoParticipacao(temp.getTipoParticipacao());
			obj.setObservacaoCertificado(temp.getObservacaoCertificado());
			
			dao.detach(temp);
			
		}finally{
			if( dao != null) dao.close();
		}
		
		movimento.setObjMovimentado(obj);
		
		if(isGerenciandoParticipantesAtividade()){
			movimento.setCodMovimento(SigaaListaComando.REMOVE_PARTICIPANTE_ATIVIDADE_PELO_COORDENADOR);
			obj.setAtividadeExtensao(atividadeSelecionada);
		}else{
			movimento.setCodMovimento(SigaaListaComando.REMOVE_PARTICIPANTE_MINI_ATIVIDADE_PELO_COORDENADOR);
			obj.setSubAtividade(subatividadeSelecionada);
		}
		
		return telaConfirmarRemoverParticipanteAtividadeCoordenador();
		
	}
	
	
	/**
	 * <p>Método que remove o participante na atividade / mini atividade</p> 
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciar.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String removerParticipanteAtividade() throws ArqException{
		
		MovimentoCadastro movimento = new MovimentoCadastro();
		movimento.setObjMovimentado(obj);
		
		if(isGerenciandoParticipantesAtividade()){
			movimento.setCodMovimento(SigaaListaComando.REMOVE_PARTICIPANTE_ATIVIDADE_PELO_COORDENADOR);
			obj.setAtividadeExtensao(atividadeSelecionada);
		}else{
			movimento.setCodMovimento(SigaaListaComando.REMOVE_PARTICIPANTE_MINI_ATIVIDADE_PELO_COORDENADOR);
			obj.setSubAtividade(subatividadeSelecionada);
		}
		
		try {
			execute(movimento);
			
			addMensagemInformation("Participante removido com sucesso.");
			
			// atualiza a listegem com a nova quantidade de participantes inscritos //
			return listarParticipantesAtividade( atividadeSelecionada != null ? atividadeSelecionada.getId() : null
					, subatividadeSelecionada != null ? subatividadeSelecionada.getId() : null);
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	
	
	
	/**
	 * 
	 * Email um email a todos os participantes da ação de extensão. Com o texto digitado pelo coordenador.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
   public void notificarParticipanteIncluidoAtividade(CadastroParticipanteAtividadeExtensao cadastro) {
	
	   String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
	   
	   // Exemplo: www.sigaa.ufrn.br/sigaa/link/public/extensao/acessarAreaInscrito //
	   String enderecoTelaLoginAreaInternaExtensao = RepositorioDadosInstitucionais.get("linkSigaa")+"/"+StringUtils.lowerCase(siglaSistema)+"/link/public/extensao/acessarAreaInscrito";
	   
	   String tipoAtividade = "";
	   String tituloAtividade = "";
	   if(isGerenciandoParticipantesAtividade()){
		   tipoAtividade = "Atividade";
		   tituloAtividade = atividadeSelecionada.getTitulo();
	   }else{
		   tipoAtividade = "Mini Atividade";
		   tituloAtividade = subatividadeSelecionada.getTitulo();
	   }
	   
	   
	   String mensagem = " <p>Você foi inscrito na "+tipoAtividade+": \""+tituloAtividade+"\" pelo coordenador da ação de extensão. </p>"+
	                     " <br/>"+
			             " <p>Para gerenciar suas informações acesse o sistema pelo endereço abaixo informando seu e-mail e senha. "+
			             " <br/><br/>"+ 
			             "<a href=\""+enderecoTelaLoginAreaInternaExtensao+"\">"+enderecoTelaLoginAreaInternaExtensao+"</a>" +
			              "</p>"+
			              "<br/>";
	   
	   // Envia para todos os participantes, com cópia para o coordenador
	   EnviarEmailExtensaoHelper.enviarEmail(
			   "["+siglaSistema+"]"+" Notificação de Participação na Ação Extensão ", 
			   " Notificação de Participação na Ação Extensão Enviada pelo Coordenador "
			   , cadastro.getNome(), cadastro.getEmail(), mensagem, null, null);
	   
	   addMensagemInformation("E-mail enviado para o participante, informando do cadastro na atividade!");
	
   }
	
	
   
	////////////////////////////////////////////////////////////
		
	/**
	* Redireciona para tela de notificação dos participantes. Onde o coordenador vai digitar o texto do email e ser enviado
	* <br />
	* Método chamado pela(s) seguinte(s) JSP(s):
	* <ul>
	*  	<li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaGerenciarParticipantes.jsp</li>
	* </ul>
	* @return
	* @throws ArqException
	*/
	public String preNotificarParticipante() throws ArqException{
	
		Integer idCadastroParticipante = getParameterInt("idCadastroParticipante");
		
		ParticipanteAcaoExtensaoDao daoParticipantes = null;
		
		try{
			daoParticipantes = getDAO(ParticipanteAcaoExtensaoDao.class);
		
		
			cadastroSelecionadoCoordenador =  daoParticipantes.findByPrimaryKey(idCadastroParticipante, CadastroParticipanteAtividadeExtensao.class, "nome", "email");
		
			mensagemEmailParticipantes = "";
		
		}finally{
			if(daoParticipantes != null) daoParticipantes.close();
		}
		
		return telaNotificacaoParticipantes();
	
	}
	
	
	/**
	* 
	* Email um email a todos os participantes da ação de extensão. Com o texto digitado pelo coordenador.
	*  
	*  <br/>
	*  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		if(StringUtils.isEmpty(mensagemEmailParticipantes)){
			addMensagemErro("Digite o texto do email a ser enviado aos participantes ");
			return null;
		}
		
		// Envia para todos os participantes, com cópia para o coordenador
		EnviarEmailExtensaoHelper.enviarEmail(
		"["+siglaSistema+"]"+" Notificação de Participantes da Ação Extensão ", 
		" Notificação de Participantes da Ação Extensão Enviada pelo Coordenador ", cadastroSelecionadoCoordenador.getNome(), cadastroSelecionadoCoordenador.getEmail(), mensagemEmailParticipantes, null, null);
		
		addMensagemInformation("E-mails enviados com sucesso!");
		
		return telaListaParticipantesParaGerenciar();	
	
	}
	
	/////////////////////////////////////////////////////////////////////
   
	
   	/**
	 * Lista os tipos de participação compatíveis com a ação do participante.
	 * <br>
	 * Método chamadado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp</li>
	 * </ul>
	 * 
	 * @return
   	 * @throws DAOException 
	 */
	public Collection<SelectItem> getTiposParticipacaoCombo() throws DAOException {

		TipoAtividadeExtensao tipoAtividadeExtensao = null;
		
		if(isGerenciandoParticipantesAtividade()){
			tipoAtividadeExtensao = atividadeSelecionada.getTipoAtividadeExtensao();
		}else{
			tipoAtividadeExtensao = subatividadeSelecionada.getAtividade().getTipoAtividadeExtensao();
		}
		
		List<TipoParticipacaoAcaoExtensao> lista = new ArrayList<TipoParticipacaoAcaoExtensao>();
		
		TipoParticipacaoAcaoExtensaoDao extDao = null;
		
		try{
			extDao = getDAO(TipoParticipacaoAcaoExtensaoDao.class);
			lista.addAll(extDao.findAllTipoParticipacaoAtivosFixosDoSistema() );
			lista.addAll(extDao.findAllTipoParticipacaoAtivosExclusivoTipoAcaoExtensao(tipoAtividadeExtensao) );
		
		}finally{
			if(extDao  != null) extDao.close();
		}
		
		return toSelectItems(lista, "id", "descricao");
		
	}
	
	
	
	

	//////////////////////              Tela de navegação   ///////////////////////////////////////
	
	/**
	 * Tela que lista os participantes para gerenciá-los.
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaListaParticipantesParaGerenciar() {
		return forward(LISTA_PARTICIPANTES_PARA_GERENCIAR);
	}
	
	/**
	 * Tela para o coordenador incluir um novo participante na atividade.
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaAdicionaParticipanteAtividadeCoordenador() {
		return forward(PAGINA_ADICIONA_PARTICIPANTE_ATIVIDADE_COORDENADOR);
	}
	
	/**
	 * Tela para confirma a remoção do participante.
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaConfirmarRemoverParticipanteAtividadeCoordenador() {
		return forward(PAGINA_CONFIRMA_REMOCAO_PARTICIPANTE_ATIVIDADE_COORDENADOR);
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	public boolean isAlterandoParticipante() {
		return alterandoParticipante;
	}

	public void setAlterandoParticipante(boolean alterandoParticipante) {
		this.alterandoParticipante = alterandoParticipante;
	}

	public CadastroParticipanteAtividadeExtensao getCadastroSelecionadoCoordenador() {
		return cadastroSelecionadoCoordenador;
	}

	public void setCadastroSelecionadoCoordenador(CadastroParticipanteAtividadeExtensao cadastroSelecionadoCoordenador) {
		this.cadastroSelecionadoCoordenador = cadastroSelecionadoCoordenador;
	}

	public String getMensagemEmailParticipantes() {
		return mensagemEmailParticipantes;
	}

	public void setMensagemEmailParticipantes(String mensagemEmailParticipantes) {
		this.mensagemEmailParticipantes = mensagemEmailParticipantes;
	}
	
	
	
}
