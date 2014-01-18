/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.GerenciarInscricoesCursosEventosExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoParticipanteAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoSubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ModalidadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ModalidadeParticipantePeriodoInscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.helper.EnviarEmailExtensaoHelper;
import br.ufrn.sigaa.extensao.jsf.BuscaPadraoProjetosDeExtensaoMBean;
import br.ufrn.sigaa.extensao.jsf.PesquisarProjetosExtensao;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoCadastrarAlteraPeriodoInscricaoAtividade;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoCadastrarAlteraPeriodoInscricaoSubAtividade;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.questionario.dominio.Questionario;

/**
 *
 * <p> MBean para gerencia as inscrições de cursos e eventos de extensão pelo coordenador </p>
 * 
 * <p> Realiza ***APENAS**** as 4 operações básicas para o período de inscrição: lista, cria, alterar, suspende. </>
 *
 * <p> <strong> PROCURAR DEIXAR A MENOR QUANTIDADE DE OPERAÇÕES AQUI, NOVAS FUCIONALIDADES CRIAR OUTROS MBEAN. POR FAVOR !!!! </strong> </p>
 * 
 * @author jadson
 *
 */
@Component("gerenciarInscricoesCursosEventosExtensaoMBean")
@Scope("request")
public class GerenciarInscricoesCursosEventosExtensaoMBean extends SigaaAbstractController<InscricaoAtividade> implements PesquisarProjetosExtensao {

	/** Lista atividade e sub atividade que podem ter o período de inscrição aberto */
	private static final String LISTA_CURSOS_EVENTOS_PARA_INSCRICAO = "/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp";
	
	/**  
	 * <p>Lista as inscrições existentes para a atividade ou sub atividade selecionada.</p>
	 * 
	 *  <p>As informações são quase a mesmas, então é melhor para a manutenção não duplicar </p>
	 *  */
	private static final String LISTA_INSCRICOES_ATIVIDADE = "/extensao/GerenciarInscricoes/listaInscricoesAtividades.jsp";
	
	
	/**  
	 * <p>Formulário para cadastrar ou alterar um período de inscrição de uma atividade ou mini atividade de extensão </p>
	 */
	private static final String FORM_PERIODO_INSCRICOES_ATIVIDADE = "/extensao/GerenciarInscricoes/formPeriodoInscricaoAtividade.jsp";
	
	
	/**  
	 * <p>Formulário para suspender o período de inscrição antes de final dele. </p>
	 */
	private static final String FORM_SUSPENDE_PERIODO_INSCRICOES_ATIVIDADE = "/extensao/GerenciarInscricoes/suspender_periodo_inscricao.jsp";
	
	
	
	/** Os cursos EM EXECUÇÃO que podem ter suas inscrições abertas pelo coordenador */
	private List<AtividadeExtensao> cursosEventosParaInscricao;
	
	
	/** Atividade selecionada para gerenciar as inscrições */
	private AtividadeExtensao atividadeSelecionada;
	
	
	/** SubAtividade selecionada para gerenciar as inscrições */
	private SubAtividadeExtensao subAtividadeSelecionada;
	
	/** A lista de inscrições a ser gerenciada pelo caso de uso, pode ser inscrições de uma atividade ou sub atividade.*/
	private List<InscricaoAtividade> inscricoes;
	
	/** O cado do caso de uso de cadastrar/alterar períodos de inscrição para guarda a 
	 * modalidade do participantes selecionada pelo usuário quando há cobrança de taxa de inscrição*/
	private int idMolidadeParticipanteSelecionada= -1;
	
	/** Guardas os questionário que foram cadastrados para a atividade */
	private List<Questionario> questionariosCadastradosParaAtividade;
	
	/** A lista de participantes da atividade ou sub atividade. Geralmente usado no cado de uso de suspensão. */
	private List<InscricaoAtividadeParticipante> inscricoesParticipante;
	
	/** O projeto de extensão selecionado pelo gestor, 
	 * usado quando o gestor de extensão  está usando o MBean para gerencirar inscrições de qualquer projeto.
	 */
	private Projeto projetoSelecionarGestor;
	
	/**
	 * Busca todos os Cursos e Eventos dos quais o docente logado seja coordenador e que possa abrir inscrições.
	 * Ou seja, esteja em execução.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/portais/docente/menu_docente.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String listarCursosEventosParaGerenciarInscricao() throws DAOException {		
		
		atualizaCursosEventosParaInscricao();
		
		return telaListaCursosEventosParaInscricao();
	}

	
	/**
	 * <p>Inicia o caso de uso pela busca de atividades de extensão.</p>
	 * 
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String iniciarGerenciamentoInscricoesByGestor() throws ArqException {
		
		checkRole(SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO );
		
		BuscaPadraoProjetosDeExtensaoMBean buscaMbean = getMBean ("buscaPadraoProjetosDeExtensaoMBean");
		return buscaMbean.iniciaBuscaSelecaoProjeto(this, "Buscar Projetos de Extensão Para Gerenciar Inscrições", 
				"<p>Seleciona um projeto de extensão para gerenciar as inscrições realizadas para esse projeto. </p>" +
				"<p><strong>Observação:</strong> Todas as operações disponíveis aqui podem ser realizadas pelo coordenador da ação.</p>");
	}
	

	
	
	////////////////////////////Métodos da interface de Busca /////////////////
	
	@Override
	public void setProjetoExtensao(Projeto projeto) {
		this.projetoSelecionarGestor = projeto;
	}
	
	
	
	@Override
	public String selecionouProjetExtensao() throws ArqException {
		atualizaCursosEventosParaInscricao();
		return telaListaCursosEventosParaInscricao();
	}
	
	
	
	@Override
		public String cancelarBuscaProjetExtensao() {
		return super.cancelar();
	}
	
	///////////////////////////////////////////////////////////////////////////////
	
	
	
	
	/**
	 * Busca no banco os cursos e eventos para os quais podem ser abertos inscrições
	 *  
	 *
	 * @throws DAOException
	 */
	public void atualizaCursosEventosParaInscricao() throws DAOException {
		GerenciarInscricoesCursosEventosExtensaoDao dao = null;
		
		try{
			dao =getDAO(GerenciarInscricoesCursosEventosExtensaoDao.class);
			if(projetoSelecionarGestor != null)
				cursosEventosParaInscricao  = dao.findCursoEventoAtivosEmExecucaoByProjetoExtensao(projetoSelecionarGestor.getId());
			else{
				if(getUsuarioLogado().getServidorAtivo() == null){
					addMensagemErro("Apenas servidores ativos podem gerenciar inscrições.");
				}else
					cursosEventosParaInscricao  = dao.findCursoEventoAtivosEmExecucaoByCoordenador(getUsuarioLogado().getServidorAtivo());
			}
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	

	
	
	
	
	/**
	 * <p>Inicia a crição alteração da inscrições de atividades.</p>
	 * 
	 * <p>Redireciona para uma página com todas as inscrições abertas, na qual o coodernador vai pode criar, alterar, cancelar, ect... </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String listarInscricaoAtividades() throws DAOException{
		
		int idAtividadeSelecionada = getParameterInt("idAtividadeSelecionada");
		
		atividadeSelecionada = null;
		subAtividadeSelecionada = null;
		
		for (AtividadeExtensao atividade : cursosEventosParaInscricao) {
			if(atividade.getId() == idAtividadeSelecionada){
				atividadeSelecionada = atividade;
				break;
			}
		}
		
		if(atividadeSelecionada == null){
			addMensagemErro("Atividade não foi selecionada corretamente");
			return null;
		}
		
		atualizaInscricoesAtividade();
		
		
		return telaListaInscricoesAtividade();
	}


	/**
	 * Busca no banco as inscrições para atividades.
	 *
	 * @throws DAOException
	 */
	private void atualizaInscricoesAtividade() throws DAOException {
		InscricaoAtividadeExtensaoDao dao = null;
				
		try{
			dao = getDAO(InscricaoAtividadeExtensaoDao.class);
			
			inscricoes = dao.findAllPeriodoInscricaoDaAtividade(atividadeSelecionada.getId());
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	/**
	 *  <p>Inicia a crição alteração da inscrições de sub atividades.</p>
	 *  
	 *   <p>Redireciona para uma página com todas as inscrições abertas, na qual o coodernador vai pode criar, alterar, cancelar, ect...</p> 
	 *  
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String listarInscricaoSubAtividades() throws DAOException{
		
		int idSubAtividadeSelecionada = getParameterInt("idSubAtividadeSelecionada");
		
		atividadeSelecionada = null;
		subAtividadeSelecionada = null;
		
		for (AtividadeExtensao atividade : cursosEventosParaInscricao) {
			
			if(atividade.getSubAtividadesExtensao() != null){
				for (SubAtividadeExtensao subatividade : atividade.getSubAtividadesExtensao()) {
					if(subatividade.getId() == idSubAtividadeSelecionada){
						subAtividadeSelecionada = subatividade;
						atividadeSelecionada = atividade;
						break;
					}
				}
			}
		}
		
		
		if(subAtividadeSelecionada == null){
			addMensagemErro("Mini Atividade não foi selecionada corretamente");
			return null;
		}
		
		atualizaInscricaoSubAtividades();
		
		return telaListaInscricoesAtividade();
	}


	/**
	 * Busca no banco as inscrições para sub atividades.
	 *
	 * @throws DAOException
	 */
	private void atualizaInscricaoSubAtividades() throws DAOException {
		InscricaoSubAtividadeExtensaoDao dao = null;
				
		try{
			dao = getDAO(InscricaoSubAtividadeExtensaoDao.class);
			
			inscricoes = dao.findAllPeriodosInscricoesDaSubAtividade(subAtividadeSelecionada.getId());
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	/**
	 * <p>Redireciona para a tela de cadastro das informações de um período de inscrição.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaInscricoesAtividades.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public String preCadastrarPeriodoInscricao() throws ArqException{
		obj = new InscricaoAtividade();
		obj.setQuestionario(new Questionario());
		
		
		
		if(isGerenciandoInscricaAtividade()){
			prepareMovimento(SigaaListaComando.CADASTRA_ALTERA_PERIODO_INSCRICAO_ATIVIDADE);
			obj.setAtividade(atividadeSelecionada);
		}else{
			prepareMovimento(SigaaListaComando.CADASTRA_ALTERA_PERIODO_INSCRICAO_SUB_ATIVIDADE);
			obj.setSubAtividade(subAtividadeSelecionada);
		}
		
		carregaQuestionariosInscricaoAtividadeDaUnidade();
		
		setConfirmButton("Abrir Período de Inscrição");
		
		return telaPeriodoInscricaoCursoEventoExtensao();
	}


	
	
	
	/**
	 *  <p>Cadastra um novo período de inscrições no banco.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/formPeriodoInscricaoAtividade.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String cadastrarPeriodoInscricao() throws ArqException{
		
		try{
			
			String mensagem = "";
			if(obj.getId() == 0){
				mensagem= " Período de Inscrição aberto com sucesso!";
			}else{
				mensagem= " Período de Inscrição alterado com sucesso!";
			}
			
			if(isGerenciandoInscricaAtividade()){
				MovimentoCadastrarAlteraPeriodoInscricaoAtividade 
				mov = new MovimentoCadastrarAlteraPeriodoInscricaoAtividade(obj, getAcessoMenu().isCoordenadorExtensao(), obj.getId() > 0);
				
				execute(mov);
				
				atualizaInscricoesAtividade();
				
			}else{
				MovimentoCadastrarAlteraPeriodoInscricaoSubAtividade 
				mov = new MovimentoCadastrarAlteraPeriodoInscricaoSubAtividade(obj, atividadeSelecionada, getAcessoMenu().isCoordenadorExtensao(), obj.getId() > 0);
				execute(mov);
				
				atualizaInscricaoSubAtividades();
			}
			
			atualizaCursosEventosParaInscricao();
			
			addMensagemInformation(mensagem);
			
			return telaListaInscricoesAtividade();
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		
	}
	
	
	/**
	 * <p>Redireciona para a tela de cadastros/alteração com as informações da inscrição selecioanda carregadas.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaInscricoesAtividades.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public String preAlterarPeriodoInscricao() throws ArqException{
		
		obj = new InscricaoAtividade();
		
		int idInscricaoSelecionada = getParameterInt("idInscricaoSelecionada");
		
		if(isGerenciandoInscricaAtividade()){
			prepareMovimento(SigaaListaComando.CADASTRA_ALTERA_PERIODO_INSCRICAO_ATIVIDADE);
			
			InscricaoAtividadeExtensaoDao dao = null;
			try{
				dao = getDAO(InscricaoAtividadeExtensaoDao.class);
				
				obj = dao.findInformacoesInscricoesAtividadeParaAlteracao(idInscricaoSelecionada);
				obj.setAtividade(atividadeSelecionada);
			}finally{
				if(dao != null) dao.close();
			}
		}else{
			prepareMovimento(SigaaListaComando.CADASTRA_ALTERA_PERIODO_INSCRICAO_SUB_ATIVIDADE);
			
			InscricaoSubAtividadeExtensaoDao dao = null;
			try{
				dao = getDAO(InscricaoSubAtividadeExtensaoDao.class);
			
				obj = dao.findInformacoesInscricoesSubAtividadeParaAlteracao(idInscricaoSelecionada);
				obj.setSubAtividade(subAtividadeSelecionada);
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		if(obj.getQuestionario() == null)
			obj.setQuestionario(new Questionario(-1));
		
		setConfirmButton("Alterar Período de Inscrição");
		
		carregaQuestionariosInscricaoAtividadeDaUnidade();
		
		return telaPeriodoInscricaoCursoEventoExtensao();
	}
	
	
	/**
	 * <p>Redireciona para a tela para confirma a suspensão da inscrição.</p> 
	 * 
	 * <p>O sistema deve cancelar todos os inscritos que enviar um email de aviso para eles</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaInscricoesAtividades.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public String preSuspenderPeriodoInscricao() throws ArqException{
		
		int idInscricaoSelecionada = getParameterInt("idInscricaoSelecionada");
		
		StatusInscricaoParticipante[] statusInscrito = new StatusInscricaoParticipante[1];
		statusInscrito[0] = new StatusInscricaoParticipante(StatusInscricaoParticipante.INSCRITO);
		
		if(isGerenciandoInscricaAtividade()){
			prepareMovimento(SigaaListaComando.SUSPENDER_PERIODO_INSCRICAO_ATIVIDADE);
			
			InscricaoAtividadeExtensaoDao dao = null;
			InscricaoParticipanteAtividadeExtensaoDao daoAtividade = null;
			
			try{
				dao = getDAO(InscricaoAtividadeExtensaoDao.class);
				daoAtividade = getDAO(InscricaoParticipanteAtividadeExtensaoDao.class);
				
				obj = dao.findInformacoesInscricoesAtividadeParaAlteracao(idInscricaoSelecionada);
				obj.setAtividade(atividadeSelecionada);
				
				inscricoesParticipante = daoAtividade.findAllParticipantesByPeriodoInscricao(obj.getId(), statusInscrito);
				
			}finally{
				if(dao != null) dao.close();
				if(daoAtividade != null) daoAtividade.close();
			}
		}else{
			prepareMovimento(SigaaListaComando.SUSPENDER_PERIODO_INSCRICAO_SUB_ATIVIDADE);
			
			InscricaoSubAtividadeExtensaoDao dao = null;
			InscricaoParticipanteAtividadeExtensaoDao daoAtividade = null;
			
			try{
				dao = getDAO(InscricaoSubAtividadeExtensaoDao.class);
				daoAtividade = getDAO(InscricaoParticipanteAtividadeExtensaoDao.class);
				
				obj = dao.findInformacoesInscricoesSubAtividadeParaAlteracao(idInscricaoSelecionada);
				obj.setSubAtividade(subAtividadeSelecionada);
				
				inscricoesParticipante = daoAtividade.findAllParticipantesByPeriodoInscricao(obj.getId(), statusInscrito);
				
			}finally{
				if(dao != null) dao.close();
				if(daoAtividade != null) daoAtividade.close();
			}
		}
		
		return telaSuspendePeriodoInscricaoCursoEventoExtensao();
	}
	
	
	
	/**
	 * Confirma a suspensão do período de inscrição selecionado pelo usuário.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String suspenderPeriodoInscricao() throws ArqException{
	
		MovimentoCadastro movimento = new MovimentoCadastro();
		movimento.setObjMovimentado(obj);
		movimento.setColObjMovimentado(inscricoesParticipante);
		
		if(isGerenciandoInscricaAtividade()){
			movimento.setCodMovimento(SigaaListaComando.SUSPENDER_PERIODO_INSCRICAO_ATIVIDADE);
		}else{
			movimento.setCodMovimento(SigaaListaComando.SUSPENDER_PERIODO_INSCRICAO_SUB_ATIVIDADE);
		}
		
		try{
			
			execute(movimento);
			
			addMensagemInformation("Período de Inscrição Suspenso com sucesso!");
			
			notificaInscricoesParticipantesCanceladas();
			
			if(isGerenciandoInscricaAtividade()){ 
				atualizaInscricoesAtividade(); 
			}else{ 
				atualizaInscricaoSubAtividades(); 
			}
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return telaListaInscricoesAtividade();
	}
	
	
	
	
	/**
	 * Envia um email para os participantes que tiveram suas inscrições canceladas.
	 *  
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @param inscricoesParticipante2
	 */
	private void notificaInscricoesParticipantesCanceladas() {
		
		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		   
	   
		String tipoAtividade = "";
		String tituloAtividade = "";
	   
		if(isGerenciandoInscricaAtividade()){
			tipoAtividade = " Atividade ";
			tituloAtividade = atividadeSelecionada.getTitulo();
		}else{
			tipoAtividade = " Mini Atividade ";
			tituloAtividade = subAtividadeSelecionada.getTitulo();
		}
	   
		List<String> bcc = new ArrayList<String>();
		
		for (InscricaoAtividadeParticipante participante : inscricoesParticipante) {
			bcc.add(participante.getCadastroParticipante().getEmail());
		}
		
	   
		String mensagem = " <p>O período de inscrição da "+tipoAtividade+": \""+tituloAtividade+"\" foi suspenso pelo coordenador, consequentemente sua inscrição foi cancelada. </p>"+
	                     " <br/>"+
	                     " <p>Motivo do cancelamento: "+
			             " <br/>"+
			             " <i>"+
			             obj.getMotivoCancelamento()+
			              " </i>"+
			             " <br/>"+ 
			             "</p>";
		   
		// Envia para todos os participantes, com cópia para o coordenador
		EnviarEmailExtensaoHelper.enviarEmail(
			   "["+siglaSistema+"]"+" Notificação de Cancelamento do Período de Inscrição da Ação de Extensão ", 
			   " Notificação Cancelamento do Período de Inscrição da Ação Extensão Realizado pelo Coordenador "
			   , null, null, mensagem, null, bcc);
	   
		addMensagemInformation("E-mail enviado para os participantes, informando o cancelamento das suas inscrições !");
		
	}


	/**
	 * Adiciona uma nova modalidade para o usuário informar o valor que ela vai pagar na inscrição.
	 * 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/formPeriodoInscricaoAtividade.jsp</li>
	 *   </ul>
	 *
	 *
	 * @param evet
	 * @throws DAOException 
	 */
	public void adicionarTaxaParaModalidade(ActionEvent evet) throws DAOException{
		
		ModalidadeParticipantePeriodoInscricaoAtividade molidadeInscricao 
			= new ModalidadeParticipantePeriodoInscricaoAtividade(); 

		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO();
			molidadeInscricao.setModalidadeParticipante(dao.findByPrimaryKey(idMolidadeParticipanteSelecionada, ModalidadeParticipante.class, "id, nome"));
			molidadeInscricao.setPeriodoInscricao(obj);
			
			if(obj.getModalidadesParticipantes() == null || ! obj.getModalidadesParticipantes().contains(molidadeInscricao))
				obj.adicionaMolidadeParticipante(molidadeInscricao);
			else{
				addMensagemErroAjax("Modalidade de Participante já incluída na listagem.");
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	
	
	
	/**
	 * Remove uma modalidade de participante adicionado pelo usuário 
	 *  
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/formPeriodoInscricaoAtividade.jsp</li>
	 *   </ul>
	 *
	 * @param evet
	 */
	public void removerTaxaParaModalidade(ActionEvent evet){
		obj.getModalidadesParticipantes().remove( obj.getModalidadesParticipantesDataModel().getRowIndex() );
	}
	
	
	/**
	 * <p>Retorna todos os questionário para atividade de extensão que são da mesma unidade do vínculo atual do usuário.</p>
	 *
	 * <p>Os questionários ficam associados à unidade do usuário logado.</p>
	 *
	 * @throws DAOException
	 */
	private void carregaQuestionariosInscricaoAtividadeDaUnidade()throws DAOException {
		QuestionarioDao questionarioDao = null;
		try{
			questionarioDao = getDAO(QuestionarioDao.class);
			
			Unidade unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
			if(unidade != null){
				questionariosCadastradosParaAtividade = (List<Questionario>) questionarioDao.findQuestionariosInscricaoAtividadeByUnidade(unidade, "id", "titulo");
			}
		}finally{
			if(questionarioDao != null) questionarioDao.close();
		}
	}
	
	
	/**
	 * Retorna os questionários existentes na unidade do usuário para ele escolher se vai usar algum no processo de inscrição.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/formPeriodoInscricaoAtividade.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public List<SelectItem> getQuestionariosDaUnidadeCombo(){
		List<SelectItem> questionariosCombo = new ArrayList<SelectItem>();
		questionariosCombo.add(new SelectItem(-1, "NÃO APLICAR QUESTIONÁRIO"));
		
		if(questionariosCadastradosParaAtividade != null){
			
			for (Questionario questionario : questionariosCadastradosParaAtividade) {
				questionariosCombo.add(new SelectItem(questionario.getId(), questionario.getTitulo()));
			}
		}
		
		return questionariosCombo;
	}
	
	
	
	/**
	 * Verifica qual o tipo de inscrição que está sendo gerenciana no momento
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaInscricoesAtividades.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public boolean isGerenciandoInscricaAtividade() {
		if(atividadeSelecionada != null && subAtividadeSelecionada == null)
			return true;
		else
			return false;
	}
	
	
	/**
	 * Retorna a quantidade de cursos e eventos encontrados.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdCursosEventosParaInscricao() {
		if(cursosEventosParaInscricao == null)
			return 0;
		else
			return cursosEventosParaInscricao.size();
	}
	
	
	/**
	 * Retorna a quantidade de cursos e eventos encontrados.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaCursosEventosParaInscricao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdInscricoes() {
		if(inscricoes == null)
			return 0;
		else
			return inscricoes.size();
	}
	
	/**
	 * Retorna a quantidade de partcipantes de uma determinada atividade ou sub atividade.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/suspender_periodo_inscricoes.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQtdInscricoesParticipante() {
		if(inscricoesParticipante == null)
			return 0;
		else
			return inscricoesParticipante.size();
	}
	
	
	
	 ////////////////////// Telas de Navegação ///////////////////////////
	/***
	 * Tela de navegação.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaListaCursosEventosParaInscricao(){
		return forward(LISTA_CURSOS_EVENTOS_PARA_INSCRICAO);
	}

	/***
	 * Tela de navegação.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaListaInscricoesAtividade(){
		return forward(LISTA_INSCRICOES_ATIVIDADE);
	}
	
	/***
	 * Tela de navegação.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaPeriodoInscricaoCursoEventoExtensao(){
		return forward(FORM_PERIODO_INSCRICOES_ATIVIDADE);
	}
	
	
	/***
	 * Tela de navegação.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaSuspendePeriodoInscricaoCursoEventoExtensao(){
		return forward(FORM_SUSPENDE_PERIODO_INSCRICOES_ATIVIDADE);
	}
	//////////////////////////////////////////////////////////////////
	
	
	
	
	
	//// sets e gets ///

	
	public List<AtividadeExtensao> getCursosEventosParaInscricao() {
		return cursosEventosParaInscricao;
	}
	public void setCursosEventosParaInscricao(List<AtividadeExtensao> cursosEventosParaInscricao) {
		this.cursosEventosParaInscricao = cursosEventosParaInscricao;
	}
	public AtividadeExtensao getAtividadeSelecionada() {
		return atividadeSelecionada;
	}
	public void setAtividadeSelecionada(AtividadeExtensao atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}
	public SubAtividadeExtensao getSubAtividadeSelecionada() {
		return subAtividadeSelecionada;
	}
	public void setSubAtividadeSelecionada(	SubAtividadeExtensao subAtividadeSelecionada) {
		this.subAtividadeSelecionada = subAtividadeSelecionada;
	}
	public List<InscricaoAtividade> getInscricoes() {
		return inscricoes;
	}
	public void setInscricoes(List<InscricaoAtividade> inscricoes) {
		this.inscricoes = inscricoes;
	}
	public int getIdMolidadeParticipanteSelecionada() {
		return idMolidadeParticipanteSelecionada;
	}
	public void setIdMolidadeParticipanteSelecionada(int idMolidadeParticipanteSelecionada) {
		this.idMolidadeParticipanteSelecionada = idMolidadeParticipanteSelecionada;
	}
	public List<InscricaoAtividadeParticipante> getInscricoesParticipante() {
		return inscricoesParticipante;
	}
	public void setInscricoesParticipante(List<InscricaoAtividadeParticipante> inscricoesParticipante) {
		this.inscricoesParticipante = inscricoesParticipante;
	}

	public Projeto getProjetoSelecionarGestor() {
		return projetoSelecionarGestor;
	}

	public void setProjetoSelecionarGestor(Projeto projetoSelecionarGestor) {
		this.projetoSelecionarGestor = projetoSelecionarGestor;
	}
	
	
}
