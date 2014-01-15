/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/08/2008'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.caixa_postal.ASyncMsgDelegate;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.AtendimentoAlunoDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.AtendimentoAluno;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.StatusAtendimentoAluno;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Controller responsável pelas operações de atendimento das perguntas dos
 * discentes pelos coordenadores.
 * 
 * @author Henrique André
 * 
 */
@Component("atendimentoAluno")
@Scope("session")
public class AtendimentoAlunoMBean extends SigaaAbstractController<AtendimentoAluno> {
	
	/** Pergunta a ser respondida / enviada. */
	public AtendimentoAluno pergunta;
	/** Coleção de perguntas já respondidas. */
	private Collection<AtendimentoAluno> perguntasRespondidas = null;
	/** Coleção de perguntas ainda não respondidas. */
	private Collection<AtendimentoAluno> perguntasNaoRespondidas = null;

	private static final String LISTA_PERGUNTAS_NAO_RESPONDIDAS = "/geral/atendimento_aluno/listar_perguntas_nao_respondidas.jsp";
	
	/** Inicializa os atributos do controller. */
	public void init() {
		obj = new AtendimentoAluno();
		pergunta = new AtendimentoAluno();
	}

	/**
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getDirBase()
	 */
	@Override
	public String getDirBase() {
		return "/geral/atendimento_aluno/";
	}
	
	/**
	 * Inicia o procedimento para realizar uma nova pergunta.
	 * Chamado por /portais/discente/menu_discente.jsp
	 * @return
	 * @throws ArqException
	 */
	public String novaPergunta() throws ArqException {
		DiscenteAdapter discente = getDiscenteUsuario();
		
		if (discente.isGraduacao() && !discente.isRegular()){
			addMensagemErro("Na graduação, somente alunos regulares podem realizar esta operação.");
			return null;
		}
		
		if (!discente.isRegular()) {
			
		}
		
		init();
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward("/geral/atendimento_aluno/form.jsp");
	}
	
	/** Seta a data, o status e o discente da pergunta a enviar. 
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeCadastrarAfterValidate()
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,
			SegurancaException, DAOException {
			
		StatusAtendimentoAluno status = new StatusAtendimentoAluno(StatusAtendimentoAluno.ALUNO_PERGUNTOU);
		obj.setStatusAtendimento(status);

		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
		obj.setDiscente(discente.getDiscente());
		
		obj.setDataEnvio(new Date());		
		
	}
	
	/** Cadastra uma pergunta.
	 * Chamado por /geral/atendimento_aluno/form.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		if (obj.getTitulo() == null || obj.getTitulo().equals("")) {
			addMensagemErro("Título é obrigatório");
			return null;
		} else if(obj.getTitulo().length() >= 110) {
			addMensagemErro("Seja mais breve no título. Máximo de 110 caracteres.");
			return null;
		}

		if (obj.getPergunta() == null || obj.getPergunta().equals("")){
			addMensagemErro("Pergunta é obrigatório");
			return null;
		}
		
		return super.cadastrar();
	}
	
	/** Informa ao usuário qual a pergunta foi enviada para a coordenação do curso. 
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterCadastrar()
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		obj = null;
		addMensagemInformation("Sua pergunta foi enviada para a coordenação do seu curso. Você receberá a resposta por e-mail ou pode consultá-la pelo próprio SIGAA.");
		super.afterCadastrar();
	}
	
	/** Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return getSubSistema().getLink();
	}
	
	/** Redireciona para a listagem de perguntas feitas por alunos.
	 * Chamado por /geral/atendimento_aluno/comum_atendimento.jsp
	 * Chamado por /geral/atendimento_aluno/listar_perguntas_atendente.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() {
		return forward("/geral/atendimento_aluno/listar_perguntas.jsp");
	}
	
	/** Redireciona para a lista de perguntas respondidas.
	 * Chamado por /geral/atendimento_aluno/comum_atendimento.jsp
	 * @return
	 */
	public String telaTodasPerguntasRespondidasAtendente() {
		perguntasRespondidas = null;
		pergunta = null;
		return forward("/geral/atendimento_aluno/listar_perguntas_atendente.jsp");
	}
	

	/**
	 * Retorna uma coleção com todas as perguntas que o discente já fez.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtendimentoAluno> getTodasPerguntas() throws DAOException {
		
		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
		
		AtendimentoAlunoDao dao = getDAO(AtendimentoAlunoDao.class);
		Collection<AtendimentoAluno> perguntas = dao.findAllByDiscente(discente);
		
		return perguntas;
	}	
	
	/**
	 * Retorna todas as perguntas que o aluno fez, e ainda não leu uma resposta 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtendimentoAluno> getListarPerguntas() throws DAOException {
		
		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
		
		AtendimentoAlunoDao dao = getDAO(AtendimentoAlunoDao.class);
		Collection<AtendimentoAluno> perguntas = dao.findAllPerguntasNaoLidas(discente);
		
		return perguntas;
	}
	
	/** Retorna todas perguntas respondidas pelo atendente.
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtendimentoAluno> getListarPerguntasRespondidasAtendente() throws DAOException {
		
		if (perguntasRespondidas == null) {
			
			AtendimentoAlunoDao dao = getDAO(AtendimentoAlunoDao.class);
			
			if (getSubSistema() != null && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR)) {
				perguntasRespondidas = dao.findAllPerguntasRespondidasGraduacao(getCursoAtualCoordenacao().getId());
			}
			
			if (getSubSistema() != null && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO)) {
				perguntasRespondidas = dao.findAllPerguntasRespondidasStricto(getProgramaStricto().getId());
			}			
		}
		
		return perguntasRespondidas;
	}
	
	
	/**
	 * Busca todas as perguntas sem respostas para um atendente.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtendimentoAluno> getListarPerguntasAtendente() throws DAOException {
	
		try {
			if (perguntasNaoRespondidas == null) {
				AtendimentoAlunoDao dao = getDAO(AtendimentoAlunoDao.class);
			
				if (getSubSistema() != null && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR)) {
					if(isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO)) 
						perguntasNaoRespondidas = perguntasNivelGraduacao(dao);
				}
			
				if (getSubSistema() != null && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO)) {
					if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS)) 
						perguntasNaoRespondidas = perguntasNivelStricto(dao);
				}
			}
		}
		catch (RuntimeNegocioException e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErro("Curso não informado.");
		}
		
		return perguntasNaoRespondidas;
	}
	

	/**
	 * Retorna as perguntas em aberto do nível graduação.
	 *  
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private Collection<AtendimentoAluno> perguntasNivelGraduacao(AtendimentoAlunoDao dao) throws DAOException, RuntimeNegocioException {		
		Curso curso = getCursoAtualCoordenacao();
		if(curso == null)
			throw new RuntimeNegocioException();		
		return dao.findAllPerguntasSemRespostasGraduacao(curso, true);		
	}

	/**
	 * Retorna as perguntas em aberto do nível Stricto.
	 * 
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private Collection<AtendimentoAluno> perguntasNivelStricto(AtendimentoAlunoDao dao) throws DAOException {
		Unidade unidadePrograma = getProgramaStricto();
		return dao.findAllPerguntasSemRespostasStricto(unidadePrograma, true);
	}

	/**
	 * Retorna uma pergunta pelo ID. 
	 * Se for servidor que estiver lendo a pergunta, atualiza o status para lida
	 * 
	 * Chamado por /sigaa.war/geral/atendimento_aluno/tela_atendente.jsp
	 * @return
	 * @throws DAOException
	 */
	public String getCarregarPergunta() throws DAOException {
		
		pergunta = getGenericDAO().findByPrimaryKey(getParameterInt("id"), AtendimentoAluno.class);
		pergunta.setDiscente(getDAO(DiscenteDao.class).findByPK(pergunta.getDiscente().getId()));
		
		if (getServidorUsuario() != null) {
			pergunta.setStatusAtendimento(new StatusAtendimentoAluno(StatusAtendimentoAluno.ATENDENTE_LEU));
			getGenericDAO().update(pergunta);
		}
		return null;
	}
	
	/** Carrega a pergunta selecionada.
	 * Chamado por /geral/atendimento_aluno/listar_perguntas_atendente.jsp
	 * @param event
	 * @throws DAOException
	 */
	public void carregarPergunta(ActionEvent event) throws DAOException {
		int id = getParameterInt("id", 0);
		pergunta = getGenericDAO().findByPrimaryKey(id, AtendimentoAluno.class);
	}
	
	/**
	 * Inicia o processo de resposta ao aluno
	 * Chamado por /geral/atendimento_aluno/comum_atendimento.jsp
	 * Chamado por /geral/atendimento_aluno/tela_atendente.jsp
	 * @return
	 * @throws DAOException
	 */
	public String preResponder() throws DAOException {
		Integer id = getParameterInt("idPergunta");
		
		if (id == null || id.intValue() == 0) {
			addMensagemErro("Pergunta não encontrada");
			return null;
		}
		
		obj = getGenericDAO().findByPrimaryKey(id, AtendimentoAluno.class);

		obj.setStatusAtendimento(new StatusAtendimentoAluno(StatusAtendimentoAluno.ATENDENTE_LEU));
		getGenericDAO().update(obj);		
		
		return forward("/geral/atendimento_aluno/atendente_responder.jsp");
	}
	
	/**
	 * Responde a pergunta.
	 * Chamado por /geral/atendimento_aluno/atendente_responder.jsp
	 * @return
	 * @throws DAOException
	 */
	public String responder() throws DAOException {
		
		if (obj.getResposta() == null || obj.getResposta().equals("")) {
			addMensagemErro("Resposta é obrigatório");
			return null;
		}
		
		obj.setAtendente(getServidorUsuario());
		obj.setDataAtendimento(new Date());		
		obj.setStatusAtendimento(new StatusAtendimentoAluno(StatusAtendimentoAluno.ATENDENTE_RESPONDEU));
		
		getGenericDAO().update(obj);
		
		enviaRespostaParaAluno();
		
		addMensagemInformation("Pergunta respondida com sucesso. O aluno receberá por email a resposta.");
		obj = null;
		return cancelar();
	}
	
	/**
	 * Retorna os possíveis destinatários para a pergunta.
	 * 
	 * @return
	 */
	public String getDestinatario() {
		DiscenteAdapter discente = getDiscenteUsuario();
		
		if (discente.getNivel() == NivelEnsino.GRADUACAO)
			return "COORDENACAO DO CURSO DE " + discente.getCurso().getDescricao();
		else if (NivelEnsino.isAlgumNivelStricto(discente.getNivel()))
			return "COORDENACAO DO PROGRAMA DE " + discente.getGestoraAcademica().getNome();
		
		return null;
	}
	
	/**
	 * Emite histórico do discente.
	 * Método não invocado por JSP
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String detalhesDiscente() throws ArqException, NegocioException {
		Integer id = getParameterInt("id");
		if (id == null) throw new NegocioException("Nenhum discente foi selecionado");
		
		Discente discente = getGenericDAO().findByPrimaryKey(id, Discente.class);
		
		HistoricoMBean historico = new HistoricoMBean();
		historico.setDiscente(discente);
		return historico.selecionaDiscente();
	}
	
	/**
	 * Exibe a listagem de perguntas não respondidas
	 * Método chamado pela seguinte jsp: /geral/atendimento_aluno/comum_atendimento.jsp
	 * @return
	 */
	public String telaTotalPerguntasNaoRespondidas(){
		perguntasNaoRespondidas = null;
		pergunta = null;
		return forward(LISTA_PERGUNTAS_NAO_RESPONDIDAS);			
	}
	
	/**
	 * Desativa uma determinada pergunta.
	 * Método chamado pelas seguintes jsps:
	 * /geral/atendimento_aluno/comum_atendimento.jsp
	 * /geral/atendimento_aluno/listar_perguntas_nao_respondidas.jsp
	 */
	public String desativaPergunta() throws DAOException{
		Integer id = getParameterInt("idPergunta");
		obj = getGenericDAO().findByPrimaryKey(id, AtendimentoAluno.class);

		obj.setAtivo(false);
		getGenericDAO().update(obj);		
		addMensagemInformation("Pergunta removida com sucesso.");
		obj = null;
		return cancelar();
	}
		
	
	/**Envia uma resposta por e-mail para o aluno.
	 * 
	 */
	private void enviaRespostaParaAluno() {
		
		String titulo = "RE: " + obj.getTitulo();
		String emailDestinatario = obj.getDiscente().getPessoa().getEmail();
		StringBuilder conteudo = new StringBuilder();
		
		conteudo.append("Mensagem Original:\n");
		conteudo.append(obj.getPergunta() + "\n\n");
		conteudo.append("==================\n");
		conteudo.append("Resposta:\n");
		conteudo.append(obj.getResposta());
		
		
		disparaMsg(titulo, conteudo);
		
		disparaEmail(titulo, emailDestinatario, conteudo);
		
	}

	/** Envia uma mensagem para a caixa postal do discente.
	 * @param titulo
	 * @param conteudo
	 */
	private void disparaMsg(String titulo, StringBuilder conteudo) {
		Mensagem mensagem = new Mensagem();
		mensagem.setAutomatica(true);
		mensagem.setTitulo(titulo);
		mensagem.setMensagem(StringUtils.stripHtmlTags(conteudo.toString()));

		
		List<UsuarioGeral> destinatario = new ArrayList<UsuarioGeral>();
		UsuarioDao uDao = getDAO(UsuarioDao.class);
		UsuarioGeral usuario = uDao.findByDiscente(obj.getDiscente().getId());
		destinatario.add(usuario);
		
		UsuarioGeral remetente = usuario;
		
		
		ASyncMsgDelegate.getInstance().enviaMensagemUsuarios(mensagem,
				remetente  , destinatario );
	}

	/** Envia um e-mail para o destinatário especificado.
	 * @param titulo
	 * @param email
	 * @param conteudo
	 */
	private void disparaEmail(String titulo, String email, StringBuilder conteudo) {		
		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto( titulo );
		mail.setMensagem( conteudo.toString().replaceAll("\\n", "<br/>") );
		Mail.send(mail);
	}

	/** Retorna a Pergunta a ser respondida / enviada. 
	 * @return Pergunta a ser respondida / enviada. 
	 */
	public AtendimentoAluno getPergunta() {
		return pergunta;
	}

	/** Seta a pergunta a ser respondida / enviada. 
	 * @param pergunta Pergunta a ser respondida / enviada. 
	 */
	public void setPergunta(AtendimentoAluno pergunta) {
		this.pergunta = pergunta;
	}
	
	/**
	 * Retorna o total de perguntas não respondidas.
	 * Método chamado pela seguinte jsp: /geral/atendimento_aluno/comum_atendimento.jsp
	 * @return
	 * @throws DAOException
	 */	
	public int getTotalPerguntas() throws DAOException {
		
		int totalPerguntas = 0;
		Curso curso = getCursoAtualCoordenacao();
		
		AtendimentoAlunoDao dao = getDAO(AtendimentoAlunoDao.class);	
		if (getSubSistema() != null && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR)) {
			if(curso == null)
				throw new RuntimeNegocioException("Não há um curso atualmente definido para execução das operações.");
			totalPerguntas = dao.findAllPerguntasSemRespostasGraduacao(curso, false).size();
		}		
		if (getSubSistema() != null && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO)) {
		totalPerguntas = dao.findAllPerguntasSemRespostasStricto(getProgramaStricto(), false).size();
		}			
		return totalPerguntas;
	}
	
	/**
	 * Retorna todas as perguntas não respondidas.
	 * Método chamado pela seguinte jsp: /geral/atendimento_aluno/listar_perguntas_nao_respondidas.jsp
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtendimentoAluno> getPerguntasNaoRespondidas() throws DAOException {
		
		if (perguntasNaoRespondidas == null){
			Curso curso = getCursoAtualCoordenacao();				
			AtendimentoAlunoDao dao = getDAO(AtendimentoAlunoDao.class);						
			if (getSubSistema() != null && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR)) {
				
				if(curso == null)
					throw new RuntimeNegocioException("Não há um curso atualmente definido para execução das operações.");
				
				perguntasNaoRespondidas = dao.findAllPerguntasSemRespostasGraduacao(curso, false);
			}			
			if (getSubSistema() != null && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO)) {
				perguntasNaoRespondidas = dao.findAllPerguntasSemRespostasStricto(getProgramaStricto(), false);
			}	   		   		  
		}
		
		
		return perguntasNaoRespondidas;
	}

	public void setPerguntasNaoRespondidas(
			Collection<AtendimentoAluno> perguntasNaoRespondidas) {
		this.perguntasNaoRespondidas = perguntasNaoRespondidas;
	}


}
