/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 31/01/2008
 * 
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularProgramaDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.GrupoDiscentesDao;
import br.ufrn.sigaa.ava.dao.TarefaTurmaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.AtividadeAvaliavel;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.GrupoDiscentes;
import br.ufrn.sigaa.ava.dominio.RespostaTarefaTurma;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.negocio.AvaliacaoHelper;
import br.ufrn.sigaa.ava.negocio.GrupoDiscentesHelper;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.negocio.RegistroAtividadeAvaHelper;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.TamanhoArquivo;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.jsf.ConsolidarTurmaMBean;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;

/**
 * Managed bean para cadastro de tarefas para uma turma.
 *
 * @author David Pereira
 *
 */
@Component("tarefaTurma") @Scope("session")
public class TarefaTurmaMBean extends CadastroTurmaVirtual<TarefaTurma> {

	/** Turma em que tarefa est� sendo cadastrada. */
	private Turma turma = new Turma();
	
	/** Arquivo de Upload, arquivo que pode ser enviado com a tarefa. */
	private UploadedFile arquivo;
	
	/** Tamanho m�ximo do Arquivo. */
	private int tamanhoMaximoArquivo = 0;
	
	/** Se deve ser enviado um e-mail para os alunos ao cadastrar uma nova tarefa. */
	private boolean notificarAlunos = true;
	
	/** Se est� inserindo uma tarefa atrav�s de um t�pico de aula npo menu principal */
	private boolean inserirTarefaEmTopico = false;
	
	/** Se a tarefa possui avalia��es */
	private boolean possuiAvaliacoes;
	
	/** Tipo da tarefa � de envio de arquivo */
	public static final int TIPO_TAREFA_ENVIO_ARQUIVO = 1;
	/** Tipo da tarefa � de texto online */
	public static final int TIPO_TAREFA_TEXTO_ONLINE = 2;
	/** Tipo da tarefa � de trabalho off-line */
	public static final int TIPO_TAREFA_TRABALHO_OFFLINE = 3;
	
	/** A forma com que a tarefa dever� ser respondida. */
	private int tipoTarefa = TIPO_TAREFA_ENVIO_ARQUIVO;
	
	/** Lista de tarefas individuais */
	private ArrayList<TarefaTurma> tarefasIndividuais;
	/** Lista de tarefas em grupo */
	private ArrayList<TarefaTurma> tarefasEmGrupo;
	
	public TarefaTurmaMBean (){
		tamanhoMaximoArquivo = ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_ARQUIVO);
	}
	
	/**
	 * Retorna a lista de tarefas da turma atual.<br/><br/>
	 * N�o � chamado em JSPs.
	 * @throws DAOException 
	 */
	@Override
	public List<TarefaTurma> lista(){
		
		if (listagem == null){
			TarefaTurmaDao dao = null;
			GrupoDiscentesDao gDao = null;
			
			try {

				dao = getDAO(TarefaTurmaDao.class);
				gDao = getDAO(GrupoDiscentesDao.class);
				Usuario u = getUsuarioLogado();
				GrupoDiscentes g = null;
				TurmaVirtualMBean tBean = getMBean("turmaVirtual");
				
				if ( u.getDiscente() != null )
					g  = gDao.findGrupoDiscenteAtivosByDiscenteTurma(u.getDiscente().getId(), turma().getId());
				
				listagem = dao.findByTurma(turma(),tBean.isPermissaoDocente());
				setQtdRespostasTarefa(listagem,dao);
				
				for (TarefaTurma t : listagem){
					
					if ( t.getRespostas() != null ){
					
						// Apenas para o discente
						if ( u.getDiscente() != null) {
							for ( RespostaTarefaTurma r : t.getRespostas() ){
								if ( (!t.isEmGrupo() && r.getUsuarioEnvio().getId() == u.getId()) || (g != null && r.getGrupoDiscentes() != null 
										&& GrupoDiscentesHelper.isAncestral(g, r.getGrupoDiscentes(), tBean.getGrupos()))){
									t.setAlunoJaEnviou(true);
									t.setProfessorJaCorrigiu(r.getDataCorrecao() != null);
									break;
								}			
							}
						}
						
					}	
					else
						t.setQtdRespostas(0);
			}
				
			} catch (DAOException e){
				notifyError(e);
			} finally {
				if (dao != null)
					dao.close();
				if(gDao != null)
					gDao.close();
			}
		}
		
		return listagem;
	}
	
	private void setQtdRespostasTarefa(List<TarefaTurma> listagem, TarefaTurmaDao dao) throws HibernateException, DAOException {

		HashMap<Integer,Integer> mapaQtd = dao.findQtdRespostaTarefas(listagem, turma());
				
		if (mapaQtd == null) 
			for (TarefaTurma t : listagem){
				if (t.getRespostas()==null)
					t.setQtdRespostas(0);
				else
					t.setQtdRespostas(t.getRespostas().size());
			}	
		else {
			for (TarefaTurma t : listagem){
				if (t.getRespostas()==null)
					t.setQtdRespostas(0);
				else {
					if (t.isEmGrupo())
						t.setQtdRespostas(t.getRespostas().size());
					else {
						Integer qtdResp = mapaQtd.get(t.getId());
						if (qtdResp!=null)
							t.setQtdRespostas(qtdResp);
						else
							t.setQtdRespostas(0);
					}	
				}	
			}	
		}		
	}

	/**
	 * Inicia o cadastro de uma tarefa.<br/><br/>
	 * M�todo chamado pela seguinte JSP: /ava/TarefaTurma/listar.jsp
	 */
	@Override
	public String novo() {
		try {
			
			if (!inserirTarefaEmTopico)
				instanciar();
			
			inserirTarefaEmTopico = false;
			
			if (cadastrarEmVariasTurmas()) {
				cadastrarEm = new ArrayList<String>();
				cadastrarEm.add(String.valueOf(turma().getId()));
				object.setHoraEntrega(23);
				object.setMinutoEntrega(59);
			}
			turma = turma();			
			prepare(SigaaListaComando.CADASTRAR_AVA);
			return forward("/ava/" + getClasse().getSimpleName() + "/novo.jsp");
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Cadastra uma tarefa.<br/><br/>
	 * M�todo chamado pela seguinte JSP: /ava/TarefaTurma/_form.jsp
	 * @throws DAOException 
	 * 
	 */
	@Override
	public String cadastrar() throws DAOException{
		
		identificarTipoTarefa();
		validarTarefa(true);
		
		if(hasErrors()){
			return null;
		}
		// Registra a tentativa de cadastrar a tarefa.
		registrarAcao(object.getTitulo(), EntidadeRegistroAva.TAREFA, AcaoAva.INICIAR_INSERCAO, true);
		
		antesPersistir();
		
		if (!hasErrors()){
			if (object.isPossuiNota()){
				
				ConfiguracoesAva config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma);
				
				if (config == null || (config != null && config.getTipoMediaAvaliacoes(object.getUnidade()) == null) ){
					addMensagemWarning("N�o � poss�vel cadastrar uma avalia��o porque o tipo de c�lculo da m�dia da avalia��o n�o foi definido. "
							+ "Para defini-lo, realize a configura��o da turma no formul�rio abaixo.");
					ConfiguracoesAvaMBean bean = getMBean("configuracoesAva");
					bean.setCadastroAvaliacao(false);
					bean.setCadastroTarefa(true);
					return bean.configurar(getTurma());
				}
			}
			
			try {
				prepare(SigaaListaComando.CADASTRAR_TAREFA);
				
				MovimentoCadastroAva mov = new MovimentoCadastroAva();
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_TAREFA);
				mov.setObjMovimentado(object);
				mov.setTurma(turma());
				mov.setSpecification(getEspecificacaoCadastro());
				mov.setMensagem(object.getMensagemAtividade());
				mov.setCadastrarEm(cadastrarEm);
				mov.setCadastrarEmVariasTurmas(cadastrarEmVariasTurmas());
				mov.setUsuario(getUsuarioLogado());
				
				
				List <Turma> turmasSucesso = execute(mov, getCurrentRequest());
				for (Turma t : turmasSucesso)
					RegistroAtividadeAvaHelper.getInstance().registrarAtividade(t, object.getMensagemAtividade());
				
				// Registra a inser��o da tarefa.
				registrarAcao(object.getTitulo(), EntidadeRegistroAva.TAREFA, AcaoAva.INSERIR, turmasSucesso, object.getId());
				
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return null;
			} catch (ArqException e) {
				tratamentoErroPadrao(e);
				return null;
			}
						
			if (notificarAlunos){
				
				GenericDAO dao = null;
				
				try{
					dao = getGenericDAO();
					object.setAula(dao.refresh(object.getAula()));
					object.getAula().setTurma(dao.refresh(object.getAula().getTurma()));
				}finally{
					if( dao != null )
						dao.close();
				}
				
				String turma = object.getAula().getTurma().getDescricaoSemDocente();
				
				String conteudo =
					"Uma nova tarefa foi cadastrada na turma <b>" + turma + "</b> do SIGAA"; 
				
				String assunto = "Nova Tarefa Cadastrada: " + object.getNome() + " - " + turma;
				
				// String mensagem = "Uma nova tarefa foi adicionada � turma.\nAcesse o SIGAA para visualiz�-la.";
				notificarTurmas(cadastrarEm, assunto , conteudo, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO, ControllerTurmaVirtual.DOCENCIA_ASSISTIDA );
			}
			
			clearCacheTopicosAula();
			
			aposPersistir();
			flash("Tarefa cadastrada com sucesso.");
			
			return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
			
		}
		return null;
	}
	
	/**
	 * Valida os dados digitados e adiciona as mensagens de erro, caso existam.
	 * @throws DAOException 
	 */
	private void validarTarefa( boolean cadastrar ) throws DAOException {		
		if(isEmpty(object.getTitulo()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "T�tulo");
		if(isEmpty(object.getConteudo()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Texto");
		if(isEmpty(object.getAula()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "T�pico de Aula");
		if(isEmpty(object.getDataInicio()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Abertura");
		if(isEmpty(object.getDataEntrega()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Fechamento");
		if(object.isPossuiNota() && isEmpty(object.getUnidade()) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade");
		if(object.isPossuiNota() && isEmpty(object.getAbreviacao())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Abrevia��o");
		if (!object.isEnvioArquivo() && object.isPermiteNovoEnvio())
			addMensagemErro("\"Permitir novo envio\" s� pode ser selecionado se o tipo da tarefa for \"Envio de Arquivo\"");
		if( cadastrar && isEmpty(cadastrarEm))
			addMensagemErro("Nenhuma turma selecionada.");
		
		// Valida a avalia��o que vai ser criada com a tarefa.
		AvaliacaoHelper.validarAvaliacao(object,turma(),erros,cadastrar);	
	}

	/**
	 * Remove uma tarefa.<br/><br/>
	 * M�todo chamado pela seguinte JSP: /ava/TarefaTurma/listar.jsp
	 * @throws DAOException 
	 */
	@Override
	public String remover() {
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			object = dao.findByPrimaryKey(getParameterInt("id"), getClasse());
			dao.detach(object);
			prepare(SigaaListaComando.REMOVER_TAREFA);
			List<RespostaTarefaTurma> respostas =  (List<RespostaTarefaTurma>) dao.findByExactField(RespostaTarefaTurma.class,"tarefa.id",object.getId());
			
			RespostaTarefaTurmaMBean rBean = getMBean("respostaTarefaTurma");
			
			for ( RespostaTarefaTurma r : respostas )
			{
				rBean.setRemocaoTarefa(true);
				rBean.setObj(r);
				rBean.remover();				
			}
			
			// Registra a tentativa de remover a tarefa.
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.TAREFA, AcaoAva.INICIAR_REMOCAO, object.getId());
			
			// Tenta remover a tarefa
			Notification notification = execute(SigaaListaComando.REMOVER_TAREFA, object, getEspecificacaoRemocao());
					
			TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			turma = tBean.getTurma();
			turma.setDisciplina(getGenericDAO().refresh(turma.getDisciplina()));
			
			if (!turma.isMedio()){
				ConsolidarTurmaMBean bean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
				bean.setTurma(turma);
				bean.prepararTurma(turma);
				bean.recarregarMatriculas();
				bean.salvarNotas(true);
			}
			
			if (notification.hasMessages())
				return notifyView(notification);
		
			// Registra o sucesso da remo��o da tarefa
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.TAREFA, AcaoAva.REMOVER, object.getId());
			
			aposPersistir();		
			clearMensagens();
			flash("Removido com sucesso.");
		
			// limpa o cache dos t�picos aula
			clearCacheTopicosAula();
		
			return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
		} catch (ArqException e){
			tratamentoErroPadrao(e);
		} catch (NegocioException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if ( dao != null )
				dao.close();
		}
		
		return null;
	}
	
	/**
	 * Exibe o formul�rio para se editar uma tarefa.<br/><br/>
	 * M�todo chamado pela seguinte JSP: /ava/TarefaTurma/listar.jsp
	 */
	@Override
	public String editar() {
		if (getParameterInt("id") == null) {
			throw new RuntimeNegocioException("Nenhuma tarefa foi selecionada para altera��o.");
		}
		
		try {
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), getClasse());
			if (object == null)
				throw new RuntimeNegocioException("A tarefa selecionada foi removida.");
			
			if ( object.getRespostas() != null && !object.getRespostas().isEmpty() ){
				addMensagemWarning("N�o � poss�vel alterar a forma de grupo da tarefa (individual ou em grupo) " +
						" depois que algum aluno j� submeteu uma resposta para tarefa. Neste caso, � necess�rio criar uma outra tarefa.");
			}
			
			object.setAula(getGenericDAO().refresh(object.getAula()));
			object.setMaterial(getGenericDAO().refresh(object.getMaterial()));
			object.getMaterial().setTopicoAula(getGenericDAO().refresh(object.getMaterial().getTopicoAula()));
			
			// Identifica o tipo da tarefa.
			if (object.isEnvioArquivo() && ! object.isRespostaOnline())
				tipoTarefa = TIPO_TAREFA_ENVIO_ARQUIVO;
			else if (!object.isEnvioArquivo() && object.isRespostaOnline())
				tipoTarefa = TIPO_TAREFA_TEXTO_ONLINE;
			else if (!object.isEnvioArquivo() && !object.isRespostaOnline())
				tipoTarefa = TIPO_TAREFA_TRABALHO_OFFLINE;
					
			instanciarAposEditar();
			aposPersistir();
			turma.setDisciplina(getGenericDAO().refresh(turma().getDisciplina()));
			turma.getDisciplina().setUnidade(getGenericDAO().refresh(turma().getDisciplina().getUnidade()));
			prepare(SigaaListaComando.ALTERAR_TAREFA);
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} 

		return forward("/ava/" + getClasse().getSimpleName() + "/editar.jsp");
	}
	
	/**
	 * Atualiza uma tarefa.<br/><br/>
	 * M�todo chamado pela seguinte JSP: /ava/TarefaTurma/editar.jsp
	 */
	@Override
	public String atualizar() {
		
		try {
			
			validarTarefa(false);		
			if (hasErrors())
				return null;
			
			erros = new ListaMensagens();
			erros.addAll(object.validate().getMensagens());								
			if (hasErrors())
				return null;
					
			// Se uma avalia��o ir� ser cadastrada a turma precissa ser configurada.
			if (object.isPossuiNota()){
				
				ConfiguracoesAva config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma);
				
				if (config == null || (config != null && config.getTipoMediaAvaliacoes(object.getUnidade()) == null) ){
					addMensagemWarning("N�o � poss�vel cadastrar uma avalia��o porque o tipo de c�lculo da m�dia da avalia��o n�o foi definido. "
							+ "Para defini-lo, realize a configura��o da turma no formul�rio abaixo.");
					ConfiguracoesAvaMBean bean = getMBean("configuracoesAva");
					bean.setCadastroAvaliacao(false);
					bean.setEdicaoTarefa(true);
					return bean.configurar(getTurma());
				}
			}
			
			// Registra a tentativa de atualizar a tarefa.
			registrarAcao(object.getTitulo(), EntidadeRegistroAva.TAREFA, AcaoAva.INICIAR_ALTERACAO, object.getId());
	
			antesPersistir();
				
			if (!hasErrors()){
				Specification specification = getEspecificacaoAtualizacao();
				if (specification == null) specification = getEspecificacaoCadastro();
				
				prepare(SigaaListaComando.ALTERAR_TAREFA);
				Notification notification = execute(SigaaListaComando.ALTERAR_TAREFA, object, specification);
				
				TurmaVirtualMBean tBean = getMBean("turmaVirtual");
				turma = tBean.getTurma();
				turma.setDisciplina(getGenericDAO().refresh(turma.getDisciplina()));
				Integer numMatriculas = getDAO(MatriculaComponenteDao.class).countMatriculasByTurma(turma,SituacaoMatricula.getSituacoesAtivasArray());
				
				if (numMatriculas > 0 && !turma.isMedio()){
					ConsolidarTurmaMBean bean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
					bean.setTurma(turma);
					bean.prepararTurma(turma);
					bean.recarregarMatriculas();
					bean.salvarNotas(true);
				}
				
				// Registra atividade para a turma
				RegistroAtividadeAvaHelper.getInstance().registrarAtividade(turma, "Tarefa alterada.");
				
				if (notificarAlunos){
					
					String turmaDesc = turma.getDescricaoSemDocente();
					String conteudo =
						"Uma tarefa foi atualizada na turma <b>" + turmaDesc + "</b> do SIGAA"; 
					String assunto = "Tarefa Atualizada: " + object.getNome() + " - " + turmaDesc;
					notificarTurmas(cadastrarEm, assunto , conteudo, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO, ControllerTurmaVirtual.DOCENCIA_ASSISTIDA );
				}
				
				if (notification.hasMessages())
					return notifyView(notification);
		
				listagem = null;
				flash("Atualizado com sucesso.");
				aposPersistir();
				
				// Registra o sucesso da atualiza��o.
				registrarAcao(object.getTitulo(), EntidadeRegistroAva.TAREFA, AcaoAva.ALTERAR, object.getId());
				
				return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsp");
			}
		} catch (ArqException e){
			tratamentoErroPadrao(e);
		} catch (NegocioException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public List<TarefaTurma> getListagem() {
		return lista();
	}
	
	/**
	 * Inst�ncia o objeto.
	 * Public por causa da arquitetura.
	 * 
	 * M�todo n�o invocado por JSPs. 
	 */
	@Override
	public void instanciar() {
		object = new TarefaTurma();
		object.setAula(new TopicoAula());
		ConfiguracoesAva config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma());
		if (config != null && config.getTipoMediaAvaliacoes(object.getUnidade()) != null) {
			object.setMediaPonderada(config.isAvaliacoesMediaPonderada(object.getUnidade()));
		}
		
		object.setDataInicio(new Date());
		object.setHoraInicio(0);
		object.setMinutoInicio(0);
		
		object.setDataEntrega(CalendarUtils.adicionaDias(new Date(), 7));
		object.setHoraEntrega(23);
		object.setMinutoEntrega(59);
	}
	
	/**
	 * Inst�ncia o objeto depois de editar.<br/><br/>
	 * Public por causa da arquitetura.
	 * 
	 * M�todo n�o invocado por JSPs.
	 */
	@Override
	public void instanciarAposEditar() {
		turma = turma();
		if (object != null && object.getAula() == null)
			object.setAula(new TopicoAula());
		ConfiguracoesAva config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma());
		if (config != null && config.getTipoMediaAvaliacoes(object.getUnidade()) != null) {
			object.setMediaPonderada(config.isAvaliacoesMediaPonderada(object.getUnidade()));
		}
	}
	
	/**
	 * Configura o objeto antes de salv�-lo.<br/><br/>
	 * Public por causa da arquitetura.
	 * 
	 * M�todo n�o invocado por JSPs.  
	 */
	@Override
	public void antesPersistir() {
		
		object.setTipoAtividade(AtividadeAvaliavel.TIPO_TAREFA);
		if ( !isEmpty( turma().getSubturmas() ))
		if (object.getAula().getId() == 0 && object.getAula() != null)
			object.setAula(new TopicoAula(0));
		if (object.getNotaMaxima() != null && object.getNotaMaxima() < 0)
			object.setNotaMaxima(null);
		
		identificarTipoTarefa();
		
		// Verifica a data inicial e final da tarefa.
		if (object.getDataInicio () == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Abertura");
		else if (object.getDataEntrega () == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Fechamento");
		else if (
				object.getDataInicio().after(object.getDataEntrega())
				|| (object.getDataInicio().equals(object.getDataEntrega()) &&
					(
							object.getHoraInicio() > object.getHoraEntrega()
							|| object.getHoraInicio() == object.getHoraEntrega() && object.getMinutoInicio() > object.getMinutoEntrega()
					)
				)
			)
				addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Data de Abertura e Fechamento");
		
		// Se um arquivo for selecionado para envio,
		if (arquivo != null) {
			if (arquivo.getSize() > tamanhoMaximoArquivo * TamanhoArquivo.MEGA_BYTE){
				addMensagem(MensagensTurmaVirtual.TAMANHO_ARQUIVO_EXCEDEU_MAXIMO, tamanhoMaximoArquivo);
				return;
			}
			object.setArquivo(arquivo);
		}
	}

	/** 
	 * Atualiza o objeto com o tipo de tarefa selecionada pelo usu�rio na view.
	 * M�todo n�o chamado por JSP. 
	 */
	private void identificarTipoTarefa() {
		// Identifica o tipo da tarefa.
		switch (tipoTarefa){
			case TIPO_TAREFA_ENVIO_ARQUIVO:
				object.setEnvioArquivo(true);
				object.setRespostaOnline(false);
			break;
			case TIPO_TAREFA_TEXTO_ONLINE:
				object.setEnvioArquivo(false);
				object.setRespostaOnline(true);
			break;
			case TIPO_TAREFA_TRABALHO_OFFLINE:
				object.setEnvioArquivo(false);
				object.setRespostaOnline(false);
			break;
		}
	}

	/**
	 * Configura o objeto ap�s de salv�-lo.<br/><br/>
	 * Public por causa da arquitetura.
	 * 
	 * M�todo n�o invocado por JSPs.
	 *  
	 */
	@Override
	public void aposPersistir(){
		listagem = null;
		tarefasIndividuais = null;
		tarefasEmGrupo = null;
	}
	
	/**
	 * Verifica se existe tarefas individuais.<br/><br/>
  	 * M�todo chamado pela seguinte JSP: 
	 * <ul>
	 *   <li>/ava/TarefaTurma/listar.jsp</li>
	 * </ul>
 
	 */
	public boolean isPossuiTarefaIndividual()
	{
		if ( listagem == null )
			listagem = lista();
		
		for ( TarefaTurma t : listagem )
		{
			if ( !t.isEmGrupo() )
				return true;
		}	
		return false;
	}
	
	/**
	 * Verifica se existe tarefas em grupo.<br/><br/>
	 * M�todo chamado pela seguinte JSP: /ava/TarefaTurma/listar.jsp
	 */
	public boolean isPossuiTarefaEmGrupo()
	{
		if ( listagem == null )
			listagem = lista();
		
		for ( TarefaTurma t : listagem )
		{
			if ( t.isEmGrupo() )
				return true;
		}	
		return false;
	}
	
	/**
	 * Retorna a lista de notas poss�veis para uma tarefa em uma turma.<br/><br/>
	 * JSP: /ava/TarefaTurma/_form.jsp
	 * @return
	 */
	public List<SelectItem> getNotas() {
		List<SelectItem> notas = new ArrayList<SelectItem>();
		notas.add(new SelectItem("-1.0", "Sem Nota"));
		for (int i = 100; i > 0; --i){
			double nota = i/10.0;
			notas.add(new SelectItem(String.valueOf(nota), String.valueOf(nota)));
		}
		return notas;
	}

	/**
	 * Retorna a lista de unidades poss�veis para uma tarefa em uma turma.<br/><br/>
	 * JSP: /ava/TarefaTurma/_form.jsp
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public List<SelectItem> getUnidades() throws ArqException, NegocioException {

		turma = turma();
		
		GenericDAO dao = null;
		ComponenteCurricularProgramaDao pDao = null;
		
		try {
			dao = getGenericDAO();
			pDao = getDAO(ComponenteCurricularProgramaDao.class);
			Integer numUnidades = 0;
			
			turma.getDisciplina().setPrograma(pDao.findAtualByComponente(turma.getDisciplina().getId()));

			numUnidades = TurmaUtil.getNumUnidadesDisciplina(turma);
			
			List<SelectItem> lista = new ArrayList<SelectItem>();
			lista.add(new SelectItem("0", "Escolha"));
			
			for (int i = 1; i <= numUnidades; i++)
				lista.add(new SelectItem(String.valueOf(i), String.valueOf(i)));
			
			dao.detach(turma);
			
			return lista;
		
		} finally {
			if (dao != null)
				dao.close();
			if (pDao != null)
				pDao.close();
		}
	}
	
	/**
	 * Realiza a valida��o do cadastro de tarefas para uma turma.<br/><br/>
	 * JSP: N�o � chamado por JSPs.
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				TarefaTurma tt = (TarefaTurma) objeto;
				Date hoje = new Date();
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(hoje);
				
				int horas = cal.get(Calendar.HOUR_OF_DAY);
				int minutos = cal.get(Calendar.MINUTE);
				
				
				if (tt.isPossuiNota() && tt.getPeso() <= 0 && tt.isMediaPonderada()) {
					notification.addError("� necess�rio escolher um peso para a tarefa. O peso ser� utilizado para o c�lculo da m�dia do aluno na consolida��o da turma.");
				}
				
				if (tt.getAula() == null || tt.getAula().getId() == 0) {
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "T�pico de Aula");
					object.setAula(new TopicoAula(0));
				}
				if (isEmpty(tt.getNome()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome da Tarefa");
				if (!isEmpty(tt.getTitulo()) && tt.getTitulo().length() > 250)
					notification.addError("O tamanho m�ximo do nome � de 250 caracteres. Voc� digitou " + tt.getTitulo().length() + " caracteres.");
				if (tt.getDataEntrega() == null)
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Entrega");
				
				if (tt.getDataEntrega() != null && !DateUtils.isSameDay(hoje, tt.getDataEntrega()) && hoje.after(tt.getDataEntrega()))
					notification.addError("A data para entrega n�o pode ser anterior � data atual.");
				if (tt.getDataEntrega() != null && DateUtils.isSameDay(hoje, tt.getDataEntrega()) && (horas > tt.getHoraEntrega() || (horas == tt.getHoraEntrega() && minutos > tt.getMinutoEntrega())))
					notification.addError("A data para entrega n�o pode ser anterior � data atual.");
				
				if (!tt.isEnvioArquivo() && tt.isPermiteNovoEnvio())
					notification.addError("\"Permitir novo envio\" s� pode ser selecionado se o tipo da tarefa for \"Envio de Arquivo\"");
				if (tt.isPossuiNota() && tt.getUnidade() <= 0)
					notification.addError("Como a tarefa tem uma nota associada, � necess�rio escolher a unidade correspondente.");
				if (tt.isPossuiNota() && isEmpty(tt.getAbreviacao()))
					notification.addError("� obrigat�rio informar uma abrevia��o se a tarefa possui nota. A abrevia��o ser� utilizada para identificar a terafa no cadastro de notas da turma.");
				return !notification.hasMessages();
			}
		
		};
	}

	/**
	 * Seta a aula do objeto.<br/><br/>
	 * M�todo chamado pela seguinte JSP: /portais/turma/turma.jsp
	 * 
	 * @param idTopicoSelecionado
	 */
	public void inserirTarefa(Integer idTopicoSelecionado) {
		instanciar();
		object.setAula(new TopicoAula(idTopicoSelecionado));
		inserirTarefaEmTopico = true;
		novo();
	}
	
	/**
	 * Indica se o material a ser cadastrado pode ser cadastrado em v�rias
	 * turmas do mesmo componente curricular.
	 * JSP: N�o � chamado por JSPs.
	 * @return
	 */
	@Override
	protected boolean cadastrarEmVariasTurmas() {
		return true;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public int getTipoTarefa() {
		return tipoTarefa;
	}

	public void setTipoTarefa(int tipoTarefa) {
		this.tipoTarefa = tipoTarefa;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public int getTamanhoMaximoArquivo() {
		return tamanhoMaximoArquivo;
	}

	public void setTamanhoMaximoArquivo(int tamanhoMaximoArquivo) {
		this.tamanhoMaximoArquivo = tamanhoMaximoArquivo;
	}

	public boolean isNotificarAlunos() {
		return notificarAlunos;
	}

	public void setNotificarAlunos(boolean notificarAlunos) {
		this.notificarAlunos = notificarAlunos;
	}

	public void setTarefasEmGrupo(ArrayList<TarefaTurma> tarefasEmGrupo) {
		this.tarefasEmGrupo = tarefasEmGrupo;
	}
	
	/**
	 * Retorna uma lista com as tarefas em grupo.
	 * <br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/TarefaTurma/listar.jsp</li>
	 * </ul>
	 */
	public ArrayList<TarefaTurma> getTarefasEmGrupo() {
		
		listagem = getListagem();
		
		if ( listagem != null )	{
			
			if ( tarefasEmGrupo == null || tarefasEmGrupo.size() <= 0 ) {
				tarefasEmGrupo = new ArrayList<TarefaTurma>();
			
				for ( TarefaTurma t : listagem ) {
					if (t.isEmGrupo())
						tarefasEmGrupo.add(t);
				}
			}
		}	
		return tarefasEmGrupo;
	}

	public void setTarefasIndividuais(ArrayList<TarefaTurma> tarefasIndividuais) {
		this.tarefasIndividuais = tarefasIndividuais;
	}

	/**
	 * Retorna uma lista com as tarefas individuais.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/TarefaTurma/listar.jsp</li>
	 * </ul>
	 */
	public ArrayList<TarefaTurma> getTarefasIndividuais() {

		listagem = getListagem();
		
		if ( listagem != null )	{
			
			if ( tarefasIndividuais == null || tarefasIndividuais.size() <= 0 ) {
				tarefasIndividuais = new ArrayList<TarefaTurma>();
			
				for ( TarefaTurma t : listagem ) {
					if (!t.isEmGrupo())
						tarefasIndividuais.add(t);
				}
			}
		}
		
		return tarefasIndividuais;
	}
	
	/**
	 * Exibe a p�gina que lista os objetos.
	 * <br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/TarefaTurma/avaliarTarefas.jsp</li>
	 * 		<li>sigaa.war/ava/TarefaTurma/editar.jsp</li>
	 * 		<li>sigaa.war/ava/TarefaTurma/enviarTarefa.jsp</li>
	 * 		<li>sigaa.war/ava/TarefaTurma/listarTarefas.jsp</li>
	 * 		<li>sigaa.war/ava/TarefaTurma/novo.jsp</li>   
	 * </ul>
	 * 
	 * @return
	 */
	@Override
	public String listar() {
		listagem = null;
		tarefasIndividuais = null;
		tarefasEmGrupo = null;
		return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsp");
	}

	/**
	 * Verifica se a tarefa possui avalia��es.
	 * Usada para exibir uma mensagem ao tentar excluir uma tarefa com avalia��es.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/TarefaTurma/_form.jsp</li>
	 * </ul>
	 */
	public boolean isPossuiAvaliacoes() throws DAOException
	{
		ArrayList<Avaliacao> avaliacoes = (ArrayList<Avaliacao>) getGenericDAO().findByExactField(Avaliacao.class, "atividadeQueGerou.id", object.getId());
		
		if (avaliacoes != null && !avaliacoes.isEmpty() )
			possuiAvaliacoes = true;
		else
			possuiAvaliacoes = false;
		
		return possuiAvaliacoes;
	}
		
	public void setPossuiAvaliacoes(boolean possuiAvaliacoes) {
		this.possuiAvaliacoes = possuiAvaliacoes;
	}

	/**
	 * Define a nota m�xima para a tarefa.
	 */
	public void setNotaMaxima( Double notaMaxima ) {
		if ( notaMaxima == -1 )
			object.setNotaMaxima(null);
		else
			object.setNotaMaxima(notaMaxima);
	}
	
	/**
	 * Retorna a nota m�xima para a tarefa.
	 */
	public Double getNotaMaxima() {
		if ( object.getNotaMaxima() == null )
			return -1.0;
		else
			return object.getNotaMaxima();
	}
	
	/**
	 * Retorna as configura��es da turma.
	 * 
	 * @return
	 */
	public ConfiguracoesAva getConfig (){
		return getDAO(TurmaVirtualDao.class).findConfiguracoes(turma());
	}
		
	/**
	 * Verifica se a unidade possui nota.
	 * M�todo n�o invocado por JSP(s)
	 * @return
	 */
	public boolean isUnidadePossuiNota() throws DAOException {
		AvaliacaoDao aDao = null;
		try {
			aDao = getDAO(AvaliacaoDao.class);	
			Integer unidade = object.getUnidade();
			boolean possuiNotas = aDao.possuiNotasCadastradasNaUnidade(turma(), unidade);
			boolean possuiAvaliacoes = aDao.possuiAvaliacoesNaUnidade(turma(), unidade);
			return possuiNotas && !possuiAvaliacoes;
		} finally {
			if (aDao != null)
				aDao.close();
		}
	}
	
}