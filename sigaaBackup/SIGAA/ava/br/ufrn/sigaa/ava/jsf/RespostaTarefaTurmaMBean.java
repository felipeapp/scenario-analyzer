/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 * 
 * Criado em: 31/08/2008
 * 
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.GrupoDiscentesDao;
import br.ufrn.sigaa.ava.dao.TarefaTurmaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.GrupoDiscentes;
import br.ufrn.sigaa.ava.dominio.RespostaTarefaTurma;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.ava.negocio.AvaliacaoTarefaMov;
import br.ufrn.sigaa.ava.negocio.GrupoDiscentesHelper;
import br.ufrn.sigaa.ava.negocio.MovimentoCorrigirTarefa;
import br.ufrn.sigaa.ava.negocio.MovimentoRemoverResposta;
import br.ufrn.sigaa.dominio.TamanhoArquivo;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.jsf.ConsolidarTurmaMBean;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed bean para que o aluno possa enviar sua resposta a
 * uma tarefa passada pelo professor no portal da turma.
 *
 * @author David Pereira
 *
 */
@Component("respostaTarefaTurma")
@Scope("request")
public class RespostaTarefaTurmaMBean extends SigaaAbstractController<RespostaTarefaTurma>{
	
	/** Link para a resposta de uma tarefa que é um texto on-line. */
	public static final String PAGINA_VISUALIZAR_RESPOSTA = "/ava/TarefaTurma/visualizarResposta.jsp";
	/** Link para o formulário de correção da tarefa. */
	public static final String PAGINA_CORRIGIR = "/ava/TarefaTurma/corrigir.jsp";

	/** tamanho do arquivo enviado pelo aluno */
	private int tamanhoUploadAluno = 0;
	
	/** Preenchido se a tarefa for para o aluno responder com um arquivo. */
	private UploadedFile arquivo;
	
	/** Preenchido se a tarefa for para o aluno responder com um texto. */
	private String textoResposta;
	
	/** Comentário enviados pelo aluno para o professor */
	private String comentarios;

	/** Lista das respostas de uma tarefa */
	private List<RespostaTarefaTurma> respostas;
	
	/** Lista das respostas de uma tarefa */
	private List<RespostaTarefaTurma> respostasPaginadas;
	
	/** Resposta que será corrigida pelo professor */
	private RespostaTarefaTurma listaTarefaEnviada;

	/** Resposta que será enviada pelo aluno */
	private RespostaTarefaTurma listaRespostas;
	
	/** Indica se é para remover o arquivo enviado. */
	private boolean removerArquivo = false;
	
	/** Resposta a ser exibida no popup. */
	private RespostaTarefaTurma respostaPopup;

	/** Caso o aluno já tenha enviado a resposta */
	private boolean usuarioJaEnviou;

	/** Indica se a remoção está sendo realizada pela Tarefa **/
	private boolean remocaoTarefa;
	
	/** Indica que é pra enviar e-mail para os alunos **/
	private boolean notificar;
	
	/** Resposta atual na lista de respostas paginadas. */
	private Integer respostaAtual;
	
	/** Primeira resposta acessada ao entrar no caso de uso de corrigir respostas. */
	private Integer primeiraResposta;
	
	/** Se a resposta foi corrigida com sucesso. */
	private Boolean ok;
	
	/** Tamanho máximo do Arquivo. */
	private int tamanhoMaximoArquivo = 0;
	
	public boolean isUsuarioJaEnviou(){
		return usuarioJaEnviou;
	}

	public void setUsuarioJaEnviou(boolean usuarioJaEnviou) {
		this.usuarioJaEnviou = usuarioJaEnviou;
	}

	public RespostaTarefaTurmaMBean() {
		resetBean();
	}

	/**
	 * Exibe a tela para se avaliarem as tarefas.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/listar.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String avaliarTarefas() throws ArqException {
		int id = getParameterInt("id", 0);
		
		if (id > 0){
			GenericDAO dao = getGenericDAO();
			TarefaTurma tarefa = dao.findByPrimaryKey(id, TarefaTurma.class);
			obj.setTarefa(tarefa);
		}

		respostaPopup = new RespostaTarefaTurma();
		
		return forward("/ava/TarefaTurma/avaliarTarefas.jsp");
	}
	
	/**
	 * Exibe a tela para o aluno responder a tarefa.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/listar.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String enviarTarefa() throws ArqException{
		Integer id = getParameterInt("id");
		
		if (id == null){
			if (obj == null || obj.getTarefa() == null || obj.getTarefa().getId() <= 0){
				addMensagem(MensagensTurmaVirtual.NENHUMA_TAREFA_SELECIONADA);
				return null;
			}
			
		} else {
			GenericDAO dao = getGenericDAO();
			TarefaTurma tarefa = dao.findByPrimaryKey(id, TarefaTurma.class);
			
			if (!tarefa.isDentroPeriodoEntrega()){
				addMensagemErro("Esta tarefa só estará disponível no período de "+CalendarUtils.format(tarefa.getDataInicio(), getPadraoData())+" às "+tarefa.getHoraInicio()+"h"+tarefa.getMinutoInicio()+" até "+CalendarUtils.format(tarefa.getDataEntrega(), getPadraoData())+" às "+tarefa.getHoraEntrega()+"h"+tarefa.getMinutoEntrega()+".");
				return null;
			}
			obj.setTarefa(tarefa);
		}
		
		TarefaTurmaDao tarefaTurmaDAO = getDAO(TarefaTurmaDao.class);
		GrupoDiscentesDao gDao = null;
		TurmaVirtualMBean tBean = getMBean("turmaVirtual"); 
		Usuario u = getUsuarioLogado();
		
		if ( u.getDiscente() != null && obj.getTarefa().isEmGrupo())
		{
			if ( !usuarioJaEnviou ) {
				gDao = getDAO(GrupoDiscentesDao.class);
				GrupoDiscentes g  = gDao.findGrupoDiscenteAtivosByDiscenteTurma(u.getDiscente().getId(), tBean.getTurma().getId());
				GrupoDiscentesHelper.carregarAncestrais(g, tBean.getGrupos());
				if ( g == null ){
					addMensagemErro("É nescessário ter um grupo para responder esta tarefa! " +
							"Solicite ao docente da turma que crie um grupo para você ou o inclua em algum grupo já criado.");
					return null;
				}				
				RespostaTarefaTurma r = tarefaTurmaDAO.findRespostaByTarefaGrupoComAncestrais(obj.getTarefa(),g);	
				obj.setGrupoDiscentes(g);
				if (r != null && !obj.getTarefa().isPermiteNovoEnvio())
					usuarioJaEnviou = true;
				else 
					usuarioJaEnviou = false;
			}	
		}
		else
			usuarioJaEnviou = tarefaTurmaDAO.findRespostaByAluno( u, obj.getTarefa() );
		
		ConfiguracoesAva config = getDAO(TurmaVirtualDao.class).findConfiguracoes(obj.getTarefa().getAula().getTurma());
		
		if (config == null)
			tamanhoUploadAluno = ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_PADRAO_ARQUIVO_ALUNO);
		else
			tamanhoUploadAluno = config.getTamanhoUploadAluno();

		if ( usuarioJaEnviou )
		{
			TarefaTurmaDao tDao = getDAO(TarefaTurmaDao.class);
			listaTarefaEnviada = tDao.findRespostaByTarefaAluno(obj.getTarefa(), getUsuarioLogado());
		}
		return forward("/ava/TarefaTurma/enviarTarefa.jsp");
		
	}

	/**
	 * Marca a resposta selecionada de uma tarefa como lida.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/avaliarTarefas.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String marcarLida() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.MARCAR_TAREFA_LIDA);
		GenericDAO dao = getGenericDAO();
		obj = dao.findByPrimaryKey(getParameterInt("id"), RespostaTarefaTurma.class);
		execute(new MovimentoCadastro(obj, SigaaListaComando.MARCAR_TAREFA_LIDA));
		respostas = null;
		return forward("/ava/TarefaTurma/avaliarTarefas.jsp");
	}
	
	/**
	 * Retorna o arquivo da resposta
	 * @return the arquivo
	 */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/**
	 * Seta o arquivo da resposta
	 * @param arquivo the arquivo to set
	 */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/**
	 * Verifica se o arquivo enviado não tem tamanho maior que o máximo permitido.<br/><br/>
	 * Não é usado em jsps. Public por causa da arquitetura.
	 */
	public void beforeCadastrarAndValidate () throws NegocioException,	SegurancaException, DAOException {
		if (obj.getTarefa().isEnvioArquivo() && arquivo != null){
			GenericDAO dao = null;
			try {
				dao = getGenericDAO();
				TarefaTurma tarefa = dao.refresh(obj.getTarefa());
				
				int tamanho = 0;
				
				ConfiguracoesAva config = getDAO(TurmaVirtualDao.class).findConfiguracoes(tarefa.getAula().getTurma());
				if (config == null)
					tamanho = ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_PADRAO_ARQUIVO_ALUNO);
				else
					tamanho = config.getTamanhoUploadAluno();
				
				if (arquivo.getSize() > tamanho * TamanhoArquivo.MEGA_BYTE)
					throw new NegocioException ("O arquivo deve ter tamanho menor ou igual a " + tamanho + " MB.");
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}
	
	/**
	 * Configura a resposta antes de salvá-la.<br/><br/>
	 * Não é usado em jsps. Public por causa da arquitetura.
	 * @throws DAOException 
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws SQLException 
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws DAOException {
		obj.setDataEnvio(new Date());
		obj.setUsuarioEnvio(getUsuarioLogado());
		
		GenericDAO dao = null;

		try {
			dao = getGenericDAO();
			obj.setTarefa(dao.refresh(obj.getTarefa()));
			
			if (obj.getTarefa().isEnvioArquivo() && arquivo != null){
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				
				EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
				obj.setIdArquivo(idArquivo);
				obj.setNomeArquivo( arquivo.getName() );
			}
			
			if (obj.getTarefa().isRespostaOnline())
				obj.setTextoResposta(textoResposta.replaceAll("\n", "<br />"));
			
			obj.setComentarios(comentarios);
			
			
		} catch (IOException e){
			throw new DAOException(e);
		} finally {
			if (dao != null)
			dao.close();
		}
	}

	/**
	 * Remove uma resposta.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/listar.jsp
	 */
	@Override
	public String remover() throws ArqException {
		
		TarefaTurmaMBean tBean = getMBean("tarefaTurma");
		
		if ( !remocaoTarefa )
		{	
			setId();
			beforeRemover();
			tBean.registrarAcao(obj.getTarefa().getNome() + " - " + obj.getUsuarioEnvio().getPessoa().getNome(), EntidadeRegistroAva.TAREFA, AcaoAva.REMOVER_RESPOSTA, obj.getTarefa().getId(), obj.getId());
		}
		MovimentoRemoverResposta mov = new MovimentoRemoverResposta(obj);
		obj.setAtivo(false);
		mov.setObjMovimentado(obj);

		if (obj.getId() == 0) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		
		mov.setCodMovimento(SigaaListaComando.REMOVER_RESPOSTA);
		try {
			
			boolean tarefaComNota = obj.getTarefa().getNotaMaxima() != null;
			prepareMovimento(SigaaListaComando.REMOVER_RESPOSTA);
			execute(mov, (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
			
			TurmaVirtualMBean turmaBean = getMBean("turmaVirtual");	
			Turma turma = turmaBean.getTurma();
			if (tarefaComNota && !turma.isMedio()) {
				ConsolidarTurmaMBean bean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
				bean.setTurma(turma);
				bean.prepararTurma(turmaBean.getTurma());
				bean.recarregarMatriculas();
				bean.salvarNotas(true);
			}
			tBean.setListagem(null);
			respostas = null;
			
			if( !remocaoTarefa )
			{	
				tBean.registrarAcao(obj.getTarefa().getNome() + " - " + obj.getUsuarioEnvio().getPessoa().getNome(), EntidadeRegistroAva.TAREFA, AcaoAva.REMOVER_RESPOSTA, obj.getTarefa().getId(), obj.getId());
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			}
			remocaoTarefa = false;
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return forward(getFormPage());
		} catch (Exception e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return forward(getFormPage());
		}
		setResultadosBusca(null);

//			afterRemover();

		return forward("/ava/TarefaTurma/avaliarTarefas.jsf");
	}
	
	/**
	 * Prepara o objeto antes de remover.<br/><br/>
	 * Não é usado em jsps. Public por causa da arquitetura.
	 */
	@Override
	public void beforeRemover() throws DAOException {
		GenericDAO dao = getGenericDAO();
		int id = getParameterInt("id");
		obj = dao.findByPrimaryKey(id, RespostaTarefaTurma.class);
	}
	
	/**
	 * Cadastra uma resposta para uma tarefa.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/enviarTarefa.jsp
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		GrupoDiscentesDao gdDao = null;
		TarefaTurmaDao dao = getDAO(TarefaTurmaDao.class);
		boolean dataValida = false;
			
		TarefaTurma tarefaTurma = dao.findTarefaTurma(obj.getTarefa().getId());		
		TarefaTurmaMBean tBean = getMBean("tarefaTurma");		
		TurmaVirtualMBean turmaBean = getMBean("turmaVirtual");
		
		if (tarefaTurma != null)
		{	
			tBean.registrarAcao(obj.getTarefa().getNome(), EntidadeRegistroAva.TAREFA, AcaoAva.INICIAR_RESPOSTA, tarefaTurma.getId(), obj.getId());
			dataValida = tarefaTurma.isDentroPeriodoEntrega();
		}
		if (dataValida == true) {
		
			try {
				if (obj.getTarefa().isEmGrupo() )
				{
					gdDao = getDAO(GrupoDiscentesDao.class);
					int idTurma = tBean.turma().getId();
					
					if (getUsuarioLogado().getDiscente() == null)
						throw new NegocioException("É nescessário ser um discente para responder uma tarefa! ");
					
					// Carrega o grupo e todos seus ancestrais
					GrupoDiscentes g = gdDao.findGrupoDiscenteAtivosByDiscenteTurma(getUsuarioLogado().getDiscente().getId(),idTurma);
					GrupoDiscentesHelper.carregarAncestrais(g, turmaBean.getGrupos());
					obj.setGrupoDiscentes(g);
					
					if ( obj.getGrupoDiscentes() == null )
						throw new NegocioException("É nescessário ter um grupo para responder esta tarefa! " +
									"Solicite ao docente da turma que crie um grupo para você ou o inclua em algum grupo já criado.");
				}
				
				if (!obj.getTarefa().isEmGrupo())
					listaRespostas = dao.findRespostaByTarefaAluno(obj.getTarefa(), getUsuarioLogado());
				else	
					listaRespostas = dao.findRespostaByTarefaGrupoComAncestrais(obj.getTarefa(),obj.getGrupoDiscentes());

				if (listaRespostas != null ) { 
					if ( !listaRespostas.getTarefa().isPermiteNovoEnvio())				
						throw new NegocioException("Sua resposta para essa tarefa já foi enviada!");

					dao.removerRespostaByTarefaAluno(listaRespostas.getId());
					
				}
	
				if (obj.getTarefa().isEnvioArquivo() && arquivo == null)
					throw new NegocioException("Nenhum arquivo selecionado para envio.");
				
				if (obj.getTarefa().isRespostaOnline() && StringUtils.isEmpty(textoResposta))
						throw new NegocioException("A resposta deve ser preenchida.");
				
				obj.setNumeroComprovante( getDAO(TarefaTurmaDao.class).getNextSeq("ava", "comprovante_tarefa_seq") );
				
				tBean.aposPersistir();
				
				beforeCadastrarAndValidate();
				beforeCadastrarAfterValidate();
				
				MovimentoCadastro mov = new MovimentoCadastro();
				obj.setAtivo(true);
						
				mov.setObjMovimentado(obj);
				prepareMovimento(ArqListaComando.CADASTRAR);
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				execute(mov);
				
				if ( obj.getTarefa().isEmGrupo())
				{			
					String turma = turmaBean.getTurma().getDescricaoSemDocente();
					
					String conteudo =
						"A tarefa <b>" + obj.getTarefa().getTitulo() + "</b> da Turma Virtual <b>" + turma + "</b> foi enviada para o SIGAA pelo usuário: " + getUsuarioLogado().getNome();
					
					String assunto = "Envio da Tarefa: " + obj.getTarefa().getTitulo() + " - " + turma;
					
					for ( Discente d : obj.getGrupoDiscentes().getDiscentes() ) 
					{
						if ( d.getUsuario().getId() != getUsuarioLogado().getId() )
						{	
							String destinatario = d.getPessoa().getEmail();
							MailBody mail = new MailBody();
							mail.setAssunto(assunto);
							mail.setFromName("SIGAA - Turma Virtual");
							mail.setMensagem(conteudo);
							mail.setEmail(destinatario);
							Mail.send(mail);
						}
					}					
				}	
				
				if (tarefaTurma != null)
					tBean.registrarAcao(obj.getTarefa().getNome(), EntidadeRegistroAva.TAREFA, AcaoAva.RESPONDER, tarefaTurma.getId(), obj.getId());
				
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
						
				return forward("/ava/TarefaTurma/comprovante_envio.jsf");
			} catch (NegocioException e) {	
				addMensagens(e.getListaMensagens());
				return null;
			}
		}
			
		addMensagem(MensagensTurmaVirtual.PRAZO_EXPIRADO_TAREFA);

		return forward("/ava/TarefaTurma/listar.jsf");
	}

	/**
	 * Verifica se a tarefa já foi enviada.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/listar.jsp
	 * <ul>
	 *   <li>/sigaa.war/ava/enviarTarefa.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String verificarTarefasJaEnviadas() throws DAOException {
		if ( listaTarefaEnviada == null )
			buscarTarefaEnviadaPorDiscente();
		return forward("/ava/TarefaTurma/listarTarefas.jsp");
	}
	
	/**
	 * Exibe a correção da tarefa.<br/><br/>
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/ava/avaliarTarefas.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String visualizarCorrecao() throws DAOException {
		
		Integer id = getParameterInt("id");
		RespostaTarefaTurma r = getGenericDAO().findByPrimaryKey( id, RespostaTarefaTurma.class);
		TarefaTurmaDao tarefaDAO = null;
		
		try {
			
			tarefaDAO = getDAO(TarefaTurmaDao.class);

			listaTarefaEnviada = tarefaDAO.findRespostaByTarefaAluno(r.getTarefa(), r.getUsuarioEnvio() );
		}finally{
			if ( tarefaDAO != null )
				tarefaDAO.close();	
		}
		return forward("/ava/TarefaTurma/listarTarefas.jsp");
	}
	
	/**
	 * Retorna a lista de respostas de uma tarefa.<br/><br/>
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/ava/avaliarTarefas.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public List<RespostaTarefaTurma> getRespostas() throws DAOException {
		if (isEmpty(respostas)) {
			TarefaTurmaDao dao = getDAO(TarefaTurmaDao.class);
			TurmaVirtualMBean tvBean = getMBean("turmaVirtual");
			
			if (obj.getTarefa() == null)
				throw new RuntimeNegocioException("A tarefa selecionada foi removida.");
			
			boolean existeGrupoAntigo = dao.existeGrupoAntigo(obj.getTarefa());
			
			if ( !obj.getTarefa().isEmGrupo() )
				respostas = dao.findRespostasNaoAvaliadasByTarefa(obj.getTarefa(),tvBean.getTurma());
			else {		
				respostas = dao.findRespostasNaoAvaliadasByTarefaGrupoTurma(obj.getTarefa(),tvBean.getTurma());
				if (existeGrupoAntigo)
					respostas.addAll(dao.findRespostasNaoAvaliadasByTarefa(obj.getTarefa(),tvBean.getTurma()));
				configurarRespostasEmGrupo(existeGrupoAntigo,obj.getTarefa());
			}	
		}
		return respostas;
		
	}

	/**
	 * Para uma dada tarefa, busca todas as respostas enviadas pelo aluno (usuário logado).
	 * 
	 * @throws DAOException
	 */
	private void buscarTarefaEnviadaPorDiscente() throws DAOException {
		
		Integer id = getParameterInt("id");
		TarefaTurma tarefa = null;
		GrupoDiscentesDao gdDao = null;
		TarefaTurmaDao tarefaDAO = null;
		GenericDAO dao = null;
		
		try {
		
			dao = getGenericDAO();
			if ( id == null && obj.getTarefa() != null )
				tarefa = obj.getTarefa();
			else
				tarefa = dao.findByPrimaryKey( id, TarefaTurma.class);
					
			TarefaTurmaMBean tBean = getMBean("tarefaTurma");
			TurmaVirtualMBean turmaBean = getMBean("turmaVirtual");
			tarefaDAO = getDAO(TarefaTurmaDao.class);
			
			tBean.registrarAcao(tarefa.getTitulo(), EntidadeRegistroAva.RESPOSTA_TAREFA, AcaoAva.ACESSAR, tarefa.getId() );

			Usuario u = (Usuario) getCurrentSession().getAttribute("usuario");
			
			if ( tarefa.isEmGrupo() )
			{
				gdDao = getDAO(GrupoDiscentesDao.class);
				int idTurma = tBean.turma().getId();
				GrupoDiscentes g = gdDao.findGrupoDiscenteAtivosByDiscenteTurma(getUsuarioLogado().getDiscente().getId(),idTurma);
				GrupoDiscentesHelper.carregarAncestrais(g, turmaBean.getGrupos());
				if ( g == null ){
					addMensagemErro("É nescessário ter um grupo para responder esta tarefa! " +
							"Solicite ao docente da turma que crie um grupo para você ou o inclua em algum grupo já criado.");
					return;
				}
				listaTarefaEnviada = tarefaDAO.findRespostaByTarefaGrupoComAncestrais(tarefa, g);
			}	
			
			if ( listaTarefaEnviada == null )
				listaTarefaEnviada = tarefaDAO.findRespostaByTarefaAluno(tarefa, u );
			
			if ( listaTarefaEnviada != null && listaTarefaEnviada.getUsuarioEnvio() != null )
				listaTarefaEnviada.setUsuarioEnvio(tarefaDAO.findAndFetch(listaTarefaEnviada.getUsuarioEnvio().getId(), Usuario.class, "pessoaSigaa"));

		}finally{
			if ( dao != null )
				dao.close();
			if ( tarefaDAO != null )
				tarefaDAO.close();
			if ( gdDao != null )
				gdDao.close();
		}
	}
	
	/**
	 * Seta as respostas de uma tarefa.
	 * @param respostas the respostas to set
	 */
	public void setRespostas(List<RespostaTarefaTurma> respostas) {
		this.respostas = respostas;
	}

	/**
	 * Salva a avaliação da tarefa.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/listar.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String avaliar() throws ArqException {
		AvaliacaoTarefaMov mov = new AvaliacaoTarefaMov();
		mov.setRespostas(respostas);
		mov.setCodMovimento(SigaaListaComando.AVALIAR_RESPOSTAS_TAREFA);

		try {
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			this.obj = new RespostaTarefaTurma();
			respostas = null;
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		return forward("/ava/TarefaTurma/listar.jsp");
	}
	
	/**
	 * Exibe o formulário para que o docente avalie a resposta do aluno.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/avaliarTarefas.jsp
	 * @return
	 * @throws ArqException
	 * @throws DAOException
	 */
	public String preCorrigir () throws ArqException , DAOException{
		populateObj(true);
		if ( obj.getTarefa().isEmGrupo() ){
			int i = respostas.indexOf(obj);
			
			if ( i >= 0 ){
				obj.setGrupoDiscentes(respostas.get(i).getGrupoDiscentes());
				obj.setExisteGrupo(respostas.get(i).isExisteGrupo());
			}	
		}
		obj.setNota(getNota(obj));
		respostaAtual = null;
		respostasPaginadas = respostas;
		
		mensagemCorrecao(obj);
		
		prepareMovimento(SigaaListaComando.CORRIGIR_TAREFA);		
		return forward(PAGINA_CORRIGIR);
	}
	
	/**
	 * Retorna a próxima resposta da lista circular de respostas.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/corrigir.jsp
	 * @return
	 * @throws DAOException 
	 */
	public String proximo () throws DAOException {	
	
		// Entrando no caso de uso.	
		if ( respostaAtual == null ) {
			respostaAtual = respostasPaginadas.indexOf(obj);
			primeiraResposta = respostaAtual;
		}
		
		if ( respostaAtual == respostasPaginadas.size()-1 ) 
			respostaAtual = 0;		
		else		
			respostaAtual++;	
		
		if (respostaAtual != null && primeiraResposta != null && respostaAtual == primeiraResposta)
			addMensagemWarning("Esta foi a primeira resposta conferida.");
		
		obj = respostasPaginadas.get(respostaAtual); 
		obj = getGenericDAO().refresh(obj);
		if (obj.getTarefa().isEmGrupo()) {
			obj.setGrupoDiscentes(respostasPaginadas.get(respostaAtual).getGrupoDiscentes());
			obj.setExisteGrupo(respostasPaginadas.get(respostaAtual).isExisteGrupo());
		}	
		obj.setNota(getNota(obj));
		
		mensagemCorrecao(obj);
		
		return forward(PAGINA_CORRIGIR);
	}
	/**
	 * Retorna a resposta anterior da lista de respostas.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/corrigir.jsp
	 * @return
	 * @throws DAOException 
	 */
	public String anterior () throws DAOException {	
				
		if ( respostaAtual == null ){
			respostaAtual = respostasPaginadas.indexOf(obj);
			primeiraResposta = respostaAtual;
		}
		
		if ( respostaAtual == 0 )
			respostaAtual = respostasPaginadas.size()-1;
		else		
			respostaAtual--;	
		
		if (respostaAtual != null && primeiraResposta != null && respostaAtual == primeiraResposta)
			addMensagemWarning("Esta foi a primeira resposta conferida.");		
		
		obj = respostasPaginadas.get(respostaAtual); 
		obj = getGenericDAO().refresh(obj);
		if (obj.getTarefa().isEmGrupo()) {
			obj.setGrupoDiscentes(respostasPaginadas.get(respostaAtual).getGrupoDiscentes());
			obj.setExisteGrupo(respostasPaginadas.get(respostaAtual).isExisteGrupo());
		}	
		obj.setNota(getNota(obj));
		
		mensagemCorrecao(obj);
		
		return forward(PAGINA_CORRIGIR);
	}
	/**
	 * Corrigi uma resposta e retorna a próxima resposta da lista de respostas.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/corrigir.jsp
	 * @return
	 * @throws ArqException
	 */
	public String corrigirProximo () throws ArqException {	
		
		corrigir();
		if ( ok )
			return proximo();
		else
			return null;
	}
	
	/**
	 * Corrigi uma resposta e retorna a resposta anterior da lista de respostas.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/corrigir.jsp
	 * @return
	 * @throws ArqException
	 */
	public String corrigirAnterior () throws ArqException {	
		
		corrigir();
		if ( ok )
			return anterior();
		else
			return null;
	}
	
	/**
	 * Salva a correção feita pelo professor.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/corrigir.jsp
	 * @return
	 * @throws ArqException
	 */
	public String corrigir () throws ArqException{
		
		TarefaTurmaDao dao = null;
		TurmaVirtualDao tvDao = null;
		TarefaTurmaMBean tBean = null;
		TarefaTurma tarefaTurma = null;
		
		try {
			dao = getDAO(TarefaTurmaDao.class);
			tarefaTurma = dao.findTarefaTurma(obj.getTarefa().getId());
			
			tBean = getMBean("tarefaTurma");
			tBean.registrarAcao(obj.getTarefa().getNome() + " - " + obj.getUsuarioEnvio().getPessoa().getNome(), EntidadeRegistroAva.TAREFA, AcaoAva.INICIAR_CORRECAO, tarefaTurma.getId(), obj.getId());
		} finally {
			if( dao != null )
				dao.close();
		}
		
		obj.setDataCorrecao(new Date());
		MovimentoCorrigirTarefa mov = new MovimentoCorrigirTarefa(obj);
		mov.setCodMovimento(SigaaListaComando.CORRIGIR_TAREFA);
		prepareMovimento(SigaaListaComando.CORRIGIR_TAREFA);
		
		int idArquivo = 0;
		
		// Indica se a correção foi enviada com sucesso para poder remover o arquivo enviado se ocorrer algum erro.
		ok = false;
		
		if (StringUtils.isEmpty(obj.getTextoCorrecao()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Comentário");
				
		if ( obj.getTarefa().isPossuiNota() && obj.getNota() == null )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nota");
		
		if (obj.getTarefa().isPossuiNota() && obj.getNota() != null && (obj.getNota() > 10 || obj.getNota() < 0))
			addMensagemErro("A nota da tarefa deve ser uma valor entre 0 e 10.");
		
		if ( obj.getNota() != null ) {
			Double notaMaxima = obj.getTarefa().getNotaMaxima();
			if ( notaMaxima != null ) {
				if ( notaMaxima > 10 )
					notaMaxima = notaMaxima/10;
				if ( obj.getNota() > notaMaxima )
					addMensagemErro("Nota: A nota não deve ser maior que a nota máxima cadastrada na tarefa.");
			}
		}
		
		if (arquivo != null && arquivo.getSize() > tamanhoMaximoArquivo* TamanhoArquivo.MEGA_BYTE){
			addMensagem(MensagensTurmaVirtual.TAMANHO_ARQUIVO_EXCEDEU_MAXIMO, tamanhoMaximoArquivo);
		}
		
		if (obj.getTarefa().isEmGrupo() && obj.isExisteGrupo()){
			boolean possuiDiscentes = false;
			for ( Discente d : mov.getResposta().getGrupoDiscentes().getDiscentes() )
				if ( !d.isRemovidoGrupo() ){
					possuiDiscentes = true;
					break;
				}	
			if (!possuiDiscentes)
				addMensagemErro("O grupo não possui alunos.");
		}
		
		if (!hasErrors())
			try {
				
				if (!removerArquivo){
					if (arquivo != null){
						idArquivo = EnvioArquivoHelper.getNextIdArquivo();
						EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
						
						if (obj.getIdArquivoCorrecao() != null && obj.getIdArquivoCorrecao() > 0)
							EnvioArquivoHelper.removeArquivo(obj.getIdArquivoCorrecao());
						
						obj.setIdArquivoCorrecao(idArquivo);
						obj.setNomeArquivoCorrecao(arquivo.getName());
					}
				} else if (obj.getIdArquivoCorrecao() != null &&  obj.getIdArquivoCorrecao() > 0) {
					EnvioArquivoHelper.removeArquivo(obj.getIdArquivoCorrecao());
					obj.setIdArquivoCorrecao(0);
					obj.setNomeArquivoCorrecao(null);
				}
			
				// Marca a tarefa como lida.
				obj.setLida(true);
				
				execute(mov);
				
				TurmaVirtualMBean turmaBean = getMBean("turmaVirtual");	
				Turma t = turmaBean.getTurma();
				
				// Turmas de ensino médio não possuem avaliações
				if (!t.isMedio()){
					if (t.isAgrupadora()){
						tvDao =  getDAO(TurmaVirtualDao.class);
						t = tvDao.findSubturmaByUsuarioTurmaAgrupadora(t, obj.getUsuarioEnvio());
					}
					t.setDisciplina(getGenericDAO().refresh(t.getDisciplina()));
					ConsolidarTurmaMBean bean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
					bean.setTurma(t);
					bean.prepararTurma(t);
					bean.recarregarMatriculas();
					bean.salvarNotas(true);
				}
				
				ok = true;
				respostas = null;
				
				if ( notificar )
				{			
					String nota = "";
					if ( obj.getNota() != null )
						nota += "<p>Nota: " + obj.getNota() + "</p>";
										
					addMensagemAjax(MensagensArquitetura.OPERACAO_SUCESSO);
					String turma = turmaBean.getTurma().getDescricaoSemDocente();
					
					String conteudo =
						"A tarefa <b>" + obj.getTarefa().getTitulo() + "</b> da Turma Virtual <b>" + turma + "</b> foi corrigida." +
						nota +
						 "<p> Comentários do Professor: " + obj.getTextoCorrecao() + "</p>" +
						"<p>Você pode verificar a correção através do SIGAA, acessando o link da Turma Virtual.</p>";
					
					String assunto = "Correção da Tarefa: " + obj.getTarefa().getTitulo() + " - " + turma;
					
					MailBody mail = new MailBody();
					mail.setAssunto(assunto);
					mail.setFromName("SIGAA - Turma Virtual");
					mail.setMensagem(conteudo);
					String destinatario = "";
					
					if ( obj.getTarefa().isEmGrupo() && obj.isExisteGrupo() )
					{	
					
						for ( Discente d : obj.getGrupoDiscentes().getDiscentes() ) 
						{
							destinatario = d.getPessoa().getEmail();
							mail.setEmail(destinatario);
							Mail.send(mail);
						}
					}else
					{
						destinatario = obj.getUsuarioEnvio().getEmail();
						mail.setEmail(destinatario);
						Mail.send(mail);
					}	
				}	
				
				tBean.registrarAcao(obj.getTarefa().getNome() + " - " + obj.getUsuarioEnvio().getPessoa().getNome(), EntidadeRegistroAva.TAREFA, AcaoAva.CORRIGIR, tarefaTurma.getId(), obj.getId());
				// Volta para a lista de respostas enviadas para a tarefa.
				return forward("/ava/TarefaTurma/corrigir.jsp");
				
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			} catch (IOException e) {
				tratamentoErroPadrao(e);
			} finally {
				if (tvDao!=null)
					tvDao.close();	
				// Se a correção não tiver sido cadastrada com sucesso mas o arquivo tiver sido enviado, remove-o.
				if (!ok && idArquivo > 0)
					EnvioArquivoHelper.removeArquivo(idArquivo);
			}
		
		return null;
		
	}

	/**
	 * Cancela a tarefa atual.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/form.jsp
	 */
	@Override
	public String cancelar() {
		return forward("/portais/turma/turma.jsf");
	}
	
	/**
	 * Exibe a resposta enviada pelo aluno.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/avaliarTarefas.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String visualizarResposta () throws DAOException{
		populateObj(true);
		
		return forward(PAGINA_VISUALIZAR_RESPOSTA); 
	}
	
	/**
	 * Realiza as configurações necessárias para uma resposta de tarefa em grupo.
	 * <br />
	 * Método não invocado por JSPs.
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public void configurarRespostasEmGrupo( boolean possuiNovosGrupos , TarefaTurma tarefa) throws HibernateException, DAOException	{
		
		if ( respostas != null ){
			
			TarefaTurmaDao dao = getDAO(TarefaTurmaDao.class);
			List<RespostaTarefaTurma> respostasGruposAntigos = dao.findRepostasEmGrupoSemGrupo(tarefa);
			Date dataFim = CalendarUtils.configuraTempoDaData(tarefa.getDataEntrega(),tarefa.getHoraEntrega(),tarefa.getMinutoEntrega(),0,0);
			
			// Verifica se a tarefa em grupo foi cadastrada antes de existir o uc de cadastrar grupo.
			// Caso a tarefa for em grupo e não possuir grupo - a tarefa em grupo existia antes do uc e está sendo tratada
			// Ou então ocorreu um erro e o aluno cadastrou uma resposta sem grupo.
			for ( RespostaTarefaTurma r : respostas) {			
				if ( respostasGruposAntigos != null && respostasGruposAntigos.contains(r) )
					r.setExisteGrupo(false);
				else
					r.setExisteGrupo(true);
			}

			// Verifica se existe alunos que saíram do grupo depois de respoderem a tarefa.
			for ( RespostaTarefaTurma r : respostas) {
				
				GrupoDiscentes grupo = r.getGrupoDiscentes();
				GrupoDiscentes grupoAux = dao.findByPrimaryKey(grupo.getId(), GrupoDiscentes.class);
				GrupoDiscentes grupoFilho = null;
				
				if (grupo != null)
					grupoFilho = grupo.getGrupoFilho();
								
				if ( grupo != null && grupoFilho != null ) {
					for ( Discente d : grupo.getDiscentes() ) {	
						if (!d.isRemovidoGrupo()){
							if (!GrupoDiscentesHelper.discentePertenceGruposFilhos(grupoAux,d,dataFim))
								d.setRemovidoGrupo(true);				
							else 
								d.setRemovidoGrupo(false);
						}
					}
				}
				// Adicionar condição pra ver se o grupo foi desativado antes ou depois do fim do prazo da tarefa
				if ( grupo != null && grupoFilho == null && !grupo.isAtivo() ) {
					if ( grupoAux.getDataAtualizacao().before(dataFim) ){
						for ( Discente d : grupo.getDiscentes() ) {
							d.setRemovidoGrupo(true);
						}
					}	
				}
			}
		}	
	}	
	
	/**
	 * Visualiza o arquivo enviado pelo aluno .<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/TarefaTurma/avaliarTarefas.jsp
	 * 
	 * @return
	 */
	public String visualizarArquivo (){

		int idArquivo = getParameterInt("idArquivo");
		String idArquivoKey = UFRNUtils.generateArquivoKey(idArquivo);
		
		String nomeArquivo = EnvioArquivoHelper.recuperaNomeArquivo(idArquivo);

		if ( nomeArquivo == null )
		{	
			addMensagemErro("Arquivo não encontrado!");
			return forward("/ava/TarefaTurma/avaliarTarefas.jsp"); 
		}
		else
			 return redirect("/sigaa/verProducao?idProducao=" +idArquivo+ "&key="+idArquivoKey);
		
	}	
	/**
	 * Exibe a nota da resposta da tarefa.<br/><br/>
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/sigaa.war/ava/listarTarefas.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String getNota() throws DAOException
	{
		AvaliacaoDao aDao = null;
		try{
			
			aDao = getDAO(AvaliacaoDao.class);
			List<Avaliacao> a = aDao.findByRespostaTarefa(listaTarefaEnviada);
			String result = "";
			
			if ( a != null && a.size() > 0 )
			{	
				Double n = a.get(0).getNota();
				if ( n != null ){
					DecimalFormat df = new DecimalFormat("0.0");
					result  = df.format(n);
				}	
			}	
			return result;
						
		}finally
		{
			if ( aDao != null )
				aDao.close();
		}		
	}
	
	/**
	 * Exibe mensagens ao entrar na tela de correção.<br/><br/>
	 * Não invocado por JSPs.
	 * @return
	 * @throws DAOException
	 */
	private void mensagemCorrecao ( RespostaTarefaTurma resposta ){
		
		if (resposta.getTarefa().isEmGrupo() && !resposta.isExisteGrupo() && resposta.getTarefa().isPossuiNota())
			addMensagemWarning("Esta tarefa foi criada antes do "+RepositorioDadosInstitucionais.get("siglaSigaa")+" possuir a opção de Gerenciar Grupos de Alunos. "
				+ "Neste caso a nota será cadastrada para apenas o discente que enviou a tarefa.");
	}
	
	/**
	 * Retorna a nota da resposta da tarefa.<br/><br/>
	 * Não invocado por JSPs.
	 * @return
	 * @throws DAOException
	 */
	public Double getNota( RespostaTarefaTurma r ) throws DAOException{
		AvaliacaoDao aDao = null;
		try{
			
			aDao = getDAO(AvaliacaoDao.class);
			List<Avaliacao> a = aDao.findByRespostaTarefa(r);
			Double n = null;
			
			if ( a != null && a.size() > 0 ){	
				n = a.get(0).getNota();
			}	
			return n;
						
		}finally{
			if ( aDao != null )
				aDao.close();
		}		
	}
	
	/**
	 * Reseta o bean.<br/><br/>
	 * Não é usado em jsps. Public por causa da arquitetura.
	 */
	@Override
	public void resetBean() {
		obj = new RespostaTarefaTurma();
		obj.setTarefa(new TarefaTurma());
		setTamanhoMaximoArquivo(ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_ARQUIVO));
	}

	public RespostaTarefaTurma getListaTarefaEnviada() {
		return listaTarefaEnviada;
	}

	public void setListaTarefaEnviada(RespostaTarefaTurma listaTarefaEnviada) {
		this.listaTarefaEnviada = listaTarefaEnviada;
	}

	public RespostaTarefaTurma getListaRespostas() {
		return listaRespostas;
	}

	public void setListaRespostas(RespostaTarefaTurma listaRespostas) {
		this.listaRespostas = listaRespostas;
	}

	public String getTextoResposta() {
		return textoResposta;
	}

	public void setTextoResposta(String textoResposta) {
		this.textoResposta = textoResposta;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public RespostaTarefaTurma getRespostaPopup() {
		return respostaPopup;
	}

	public void setRespostaPopup(RespostaTarefaTurma respostaPopup) {
		this.respostaPopup = respostaPopup;
	}

	public boolean isRemoverArquivo() {
		return removerArquivo;
	}

	public void setRemoverArquivo(boolean removerArquivo) {
		this.removerArquivo = removerArquivo;
	}

	public int getTamanhoUploadAluno() {
		return tamanhoUploadAluno;
	}

	public void setTamanhoUploadAluno(int tamanhoUploadAluno) {
		this.tamanhoUploadAluno = tamanhoUploadAluno;
	}

	
	public boolean isRemocaoTarefa() {
		return remocaoTarefa;
	}

	public void setRemocaoTarefa(boolean remocaoTarefa) {
		this.remocaoTarefa = remocaoTarefa;
	}

	public void setNotificar(boolean notificar) {
		this.notificar = notificar;
	}

	public boolean isNotificar() {
		return notificar;
	}

	public void setOk(Boolean ok) {
		this.ok = ok;
	}

	public Boolean getOk() {
		return ok;
	}
	
	public void setRespostasPaginadas(List<RespostaTarefaTurma> respostasPaginadas) {
		this.respostasPaginadas = respostasPaginadas;
	}

	public List<RespostaTarefaTurma> getRespostasPaginadas() {
		return respostasPaginadas;
	}

	public void setTamanhoMaximoArquivo(int tamanhoMaximoArquivo) {
		this.tamanhoMaximoArquivo = tamanhoMaximoArquivo;
	}

	public int getTamanhoMaximoArquivo() {
		return tamanhoMaximoArquivo;
	}

}