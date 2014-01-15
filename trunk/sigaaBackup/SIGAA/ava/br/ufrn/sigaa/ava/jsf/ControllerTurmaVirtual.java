/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2008
 *
 */

package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.log.LogProcessorDelegate;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.acesso.DadosAcesso;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.PlanoDocenciaAssistidaDao;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Layer Supertype (http://martinfowler.com/eaaCatalog/layerSupertype.html) 
 * Controller que contém métodos que auxiliam os managed beans da turma virtual.
 * 
 * @author David Pereira
 * 
 */
public abstract class ControllerTurmaVirtual extends AbstractController {

	/** Representa os discentes das turmas, usado para notificar os discentes */	
	public static final int DISCENTE = 1;
	/** Representa os docentes, usado para notificar os docentes */
	public static final int DOCENTE = 2;
	/** Representa usuários com permissão, usado para notificar os usuários autorizados  */
	public static final int AUTORIZADO = 3;
	/** Representa a docênca assistida, usado para notificar a docência assistida  */
	public static final int DOCENCIA_ASSISTIDA = 4;
	
	/** Lista de turmas que serão cadastrados os objetos */
	protected List<String> cadastrarEm;
	
	/** Página de origem do tópico de aula */
	protected String paginaOrigem;
	
	/** Lista de datas que são feriados da turma */
	private List<Date> feriadosTurma;

	/**
	 * Adiciona a lista de mensagem guardada pelo notification para ser exibida pelo sistema.
	 * Não é utilizado por JSPs.
	 * @param notification
	 */
	protected String notifyView(Notification notification) {
		addMensagens(notification.getMessages());
		return null;
	}

	/**
	 * Registra o acesso de um discente a uma entidade da turma virtual.
	 * @param entidadeLida
	 * @param idEntidade
	 * @param idTurmaVirtual
	 */
	protected void registrarLogAcessoDiscenteTurmaVirtual(String entidadeLida, int idEntidade, int idTurmaVirtual) {
	    if (getUsuarioLogado()!= null && getUsuarioLogado().getDiscente() != null) {
	        Integer sistema = getSistema();
	        Usuario user = getUsuarioLogado();
	        LogProcessorDelegate.getInstance().writeAcessoLog(entidadeLida, idEntidade, user, sistema, idTurmaVirtual);
	    }
	}

	/**
	 * Retorna a turma virtual com a qual se está trabalhando atualmente. 
	 * Cada vez que se entra em uma turma virtual, este dado é modificado.
	 * @return
	 */
	public Turma turma() {
		TurmaVirtualMBean turmaMBean = (TurmaVirtualMBean) getMBean("turmaVirtual");
		if (turmaMBean.getTurma() == null) {
			if (getParameterInt("tid") == null) {
				throw new RuntimeNegocioException(
						"Nenhuma turma virtual foi selecionada.");
			} else {
				int id = getParameterInt("tid");
				try {
					Turma t =  getDAO(TurmaDao.class).findByPrimaryKeyOtimizado(id);
					feriadosTurma = TurmaUtil.getFeriados(t);
					// Inicializa o componente curricular.
					if (t.getDisciplina() != null && t.getDisciplina().getDetalhes() != null)
						t.getDisciplina().getDetalhes().getId();
					
					// Inicializa a unidade.
					t.getDisciplina().getUnidade();
					
					// Inicializa o programa da disciplina.
					t.getDisciplina().getPrograma();
					
					// Garante a inicialização dos atributos necessários para exibir a descrição da turma.
					t.getDescricao();
					
					turmaMBean.setTurma(t);
					
					return t;
					
				} catch (DAOException e) {
					throw new TurmaVirtualException(e);
				}
			}
		}
		
		return turmaMBean.getTurma();
	}

	/**
	 * Retorna os parâmetros da unidade gestora acadêmica da turma atual.
	 * @return
	 * @throws DAOException
	 */
	protected ParametrosGestoraAcademica params() throws DAOException {
		return ParametrosGestoraAcademicaHelper.getParametros(turma());
	}

	/**
	 * Realiza um prepareMovimento de um comando.
	 * @param comando
	 */
	protected void prepare(Comando comando) {
		try {
			prepareMovimento(comando);
		} catch (ArqException e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Acessa um processador para cadastro de alguma entidade da turma virtual.
	 * @param comando
	 * @param object
	 * @param specification
	 * @return
	 */
	protected Notification execute(Comando comando, DominioTurmaVirtual object, Specification specification) {
		if (object != null) {
			try {
				MovimentoCadastroAva mov = new MovimentoCadastroAva();
				mov.setCodMovimento(comando);
				mov.setObjMovimentado(object);
				mov.setTurma(turma());
				mov.setSpecification(specification);
				mov.setMensagem(object.getMensagemAtividade());
				mov.setCadastrarEm(cadastrarEm);
				mov.setCadastrarEmVariasTurmas(cadastrarEmVariasTurmas());
				
				return (Notification) execute(mov, getCurrentRequest());
			} catch (Exception e) {
				throw new TurmaVirtualException(e);
			}
		} else {
			throw new RuntimeNegocioException("Nenhum objeto foi informado. Por favor, reinicie a operação.");
		}
	}

	/** Adiciona uma mensagem de erro do tipo INFORMATION para ser exibida no sistema.
	 * Não é utilizada por JSPs
	 * @param mensagem
	 */
	protected void flash(String mensagem) {
		addMessage(mensagem, TipoMensagemUFRN.INFORMATION);
	}
	
	/**
	 * Envia um e-mail aos participantes das turmas passadas como parâmetro com o assunto
	 * e o texto passados como parâmetro.  
	 * @param cadastrarEm
	 * @param assunto
	 * @param texto
	 * @throws DAOException
	 */
	public void notificarTurmas(List<String> cadastrarEm, String assunto, String texto, int ... tipoUsuario) throws DAOException {
		
		List <Integer> ids = new ArrayList <Integer> ();
		
		TurmaDao dao = null;
		PlanoDocenciaAssistidaDao daoDocenciaAssistida = null;
		try {
			// Prepara as turmas.
			dao = getDAO(TurmaDao.class);
			if (!isEmpty(cadastrarEm)) {
				for (String tid : cadastrarEm)
					ids.add(Integer.valueOf(tid));
				
				List <Turma> ts = dao.findByPrimaryKeyOtimizado(ids);
				List <Integer> idsTurmas = new ArrayList <Integer> ();
				
				for (Turma t : ts){
					//Se for turma agrupadora, utilizar as turmas que estão agrupadas, pois são elas que possuem MatriculaComponente
					//e não a turma agrupadora.
					if(t.isAgrupadora()) {
						Collection<Turma> turmasAgrupadas = dao.findByExactField(Turma.class, "turmaAgrupadora.id", t.getId());
						for(Turma agrupada : turmasAgrupadas) {
							idsTurmas.add(agrupada.getId());
						}
					} 
					else {
						//Neste caso, a turma não é agrupadora logo é ela contém as MatriculaComponente
						idsTurmas.add(t.getId());
					}
				}
			
				boolean notificarDiscentes = false;
				boolean notificarDocentes = false;
				boolean notificarAutorizados = false;
				boolean notificarDocenciaAssistida = false;
				
				for (int t : tipoUsuario)
					if (t == DISCENTE)
						notificarDiscentes = true;
					else if (t == DOCENTE)
						notificarDocentes = true;
					else if (t == AUTORIZADO)
						notificarAutorizados = true;
					else if ( t == DOCENCIA_ASSISTIDA )
						notificarDocenciaAssistida = true;
				
				// Se for para enviar aos discentes das turmas,
				if (notificarDiscentes && !notificarAutorizados ){
					Collection<MatriculaComponente> matriculas = dao.findEmailsParticipantesTurmas(idsTurmas);
					notificarDiscentes(assunto, texto, matriculas);
				}
				
				// Se for para enviar aos usuários com permissão nas turmas,
				if (notificarAutorizados && !notificarDiscentes ){
					List<Usuario> autorizados = dao.findEmailsUsuariosAutorizadosByTurmas(idsTurmas);
					notificarUsuariosAutorizados(assunto, texto, autorizados);
				}
				
				// Se for pra enviar aos discentes e aos usuários com permissões nas turmas sem enviar o mesmo e-mail duas vezes caso um discente possua permissão.
				if ( notificarDiscentes && notificarAutorizados ) {
					
					Collection<MatriculaComponente> matriculas = dao.findEmailsParticipantesTurmas(idsTurmas);
					List<Usuario> autorizados = dao.findEmailsUsuariosAutorizadosByTurmas(idsTurmas);
					List<Usuario> autorizadosEDiscentes = new ArrayList<Usuario>( autorizados );
					
					if ( matriculas != null ){
						for ( MatriculaComponente m : matriculas ){
							
							if (m.getDiscente().getUsuario() != null){
								boolean isMesmoUsuario = false;
								
								if ( autorizados != null && autorizados.size() > 0 ) {
									for ( Usuario u : autorizados )
										if ( u.getEmail().equals(m.getDiscente().getUsuario().getEmail()) )
											isMesmoUsuario = true;
								}
								
								if ( !isMesmoUsuario )
									autorizadosEDiscentes.add(m.getDiscente().getUsuario());
							}	
						}
					}	
					
					notificarUsuariosAutorizados(assunto, texto, autorizadosEDiscentes);
				}
				
				// Se for pra enviar aos discentes e aos usuários com permissões nas turmas sem enviar o mesmo e-mail duas vezes caso um discente possua permissão.
				if ( notificarAutorizados && notificarDocenciaAssistida) {
					
					daoDocenciaAssistida = getDAO(PlanoDocenciaAssistidaDao.class);
				
					List<PlanoDocenciaAssistida> docenciaAssistida = daoDocenciaAssistida.findEmailsDocenciaAssistidaByTurmas(idsTurmas);
					List<Usuario> autorizados = dao.findEmailsUsuariosAutorizadosByTurmas(idsTurmas);
					List<PlanoDocenciaAssistida> docenciaAssistidaSemAutorizados = new ArrayList<PlanoDocenciaAssistida>();
					
					if ( !isEmpty(docenciaAssistida) ){
						for ( PlanoDocenciaAssistida da : docenciaAssistida ){
	
								boolean isMesmoUsuario = false;
								
								if ( !isEmpty(autorizados)) {
									for ( Usuario u : autorizados )
										if ( u.getEmail().equals(da.getDiscente().getPessoa().getEmail()) )
											isMesmoUsuario = true;
								}
								
								if ( !isMesmoUsuario )
									docenciaAssistidaSemAutorizados.add(da);
							}	
						}
					
					notificarDocenciaAssistida(assunto, texto, docenciaAssistidaSemAutorizados);
				}
				
				// Se for para enviar a docência assistida,
				if (notificarDocenciaAssistida && !notificarAutorizados){
					daoDocenciaAssistida = getDAO(PlanoDocenciaAssistidaDao.class);
					List<PlanoDocenciaAssistida> docenciaAssistida = daoDocenciaAssistida.findEmailsDocenciaAssistidaByTurmas(idsTurmas);
					notificarDocenciaAssistida(assunto, texto, docenciaAssistida);
				}
				
				// Se for para enviar aos docentes das turmas,
				if (notificarDocentes){
					List<DocenteTurma> docentes = dao.findEmailsDocentesByTurmas(idsTurmas);
					notificarDocentes(assunto, texto, docentes);
				}
			}
			
		} finally {
			if (dao != null)
				dao.close();
			if (daoDocenciaAssistida != null)
				daoDocenciaAssistida.close();
		}
	}


	/**
	 * Notifica a turma toda sobre um determinado assunto
	 * 
	 * @param assunto
	 * @param texto
	 * @throws DAOException 
	 */
	public void notificarTurma(String assunto, String texto, int ... tipoUsuario) throws DAOException {
		notificarTurma(turma(), assunto, texto, tipoUsuario);
	}

	/**
	 * Envia um e-mail aos participantes da turma com o assunto
	 * e o texto passados como parâmetro.  
	 * @param turma a turma principal ou a agrupadora
	 * @param assunto
	 * @param texto
	 * @throws DAOException 
	 */
	public void notificarTurma(Turma turma, String assunto, String texto, int ... tipoUsuario) throws DAOException {
	
		String turmaId = String.valueOf(turma.getId());
		ArrayList<String> cadastrarEm = new ArrayList<String>();
		
		cadastrarEm.add(turmaId);
		
		notificarTurmas(cadastrarEm, assunto, texto, tipoUsuario);
		
	}

	/**
	 * Envia email para os discentes passados.
	 * 
	 * @param assunto
	 * @param texto
	 * @param matriculas
	 */
	protected void notificarDiscentes(String assunto, String texto, Collection<MatriculaComponente> matriculas) {
		if (!isEmpty(matriculas)) {
			for (MatriculaComponente mat : matriculas) {

				if (mat.isMatriculado()) {

					MailBody body = new MailBody();

					body.setAssunto(assunto);
					body.setMensagem((texto));
					body.setFromName("SIGAA - Turma Virtual");
					body.setContentType(MailBody.HTML);
					if (mat.getDiscente().getUsuario() != null) {
						body.setEmail(mat.getDiscente().getUsuario().getEmail());
						Mail.send(body);
					}

				}
			}
		}
	}
	
	/**
	 * Envia email para os docentes passados.
	 * 
	 * @param assunto
	 * @param texto
	 * @param docentes
	 */
	private void notificarDocentes(String assunto, String texto, List<DocenteTurma> docentes) {
		for ( DocenteTurma  dt : docentes ) {
		
			MailBody body = new MailBody();
			body.setAssunto(assunto);
			body.setMensagem((texto));
			body.setFromName("SIGAA - Turma Virtual");
			body.setContentType(MailBody.HTML);
			if (dt.getDocente().getPrimeiroUsuario() != null) {
				body.setEmail(dt.getDocente().getPrimeiroUsuario().getEmail());
				Mail.send(body);
			}
		}
	}
	
	/**
	 * Envia email para a docência assistida.
	 * 
	 * @param assunto
	 * @param texto
	 * @param docentes
	 */
	private void notificarDocenciaAssistida(String assunto, String texto, List<PlanoDocenciaAssistida> docenciaAssistida) {
		if (!isEmpty(docenciaAssistida)) {
			for ( PlanoDocenciaAssistida  da : docenciaAssistida ) {
			
				MailBody body = new MailBody();
				body.setAssunto(assunto);
				body.setMensagem((texto));
				body.setFromName("SIGAA - Turma Virtual");
				body.setContentType(MailBody.HTML);
				if (da.getDiscente().getPessoa() != null) {
					body.setEmail(da.getDiscente().getPessoa().getEmail());
					Mail.send(body);
				}
			}
		}	
	}
	
	/**
	 * Envia emails para os usuários passados.
	 * 
	 * @param assunto
	 * @param texto
	 * @param usuarios
	 */
	private void notificarUsuariosAutorizados (String assunto, String texto, List<Usuario> usuarios) {
		for ( Usuario u : usuarios ) {
			if ( u != null ){
				MailBody body = new MailBody();
	
				body.setAssunto(assunto);
				body.setMensagem(texto);
				body.setFromName("SIGAA - Turma Virtual");
				body.setEmail(u.getEmail());
				body.setContentType(MailBody.HTML);
				Mail.send(body);
			}	
		}
	}
	
	/**
	 * Retorna o usuário logado
	 */
	@SuppressWarnings("unchecked")
	public Usuario getUsuarioLogado() {
		return (Usuario) getCurrentRequest().getSession().getAttribute(
				"usuario");
	}

	/**
	 * Retorna o servidor do usuário logado
	 */
	public Servidor getServidorUsuario() {
		return getUsuarioLogado().getServidor();
	}

	/**
	 * Retorna o discente do usuário logado
	 */
	public DiscenteAdapter getDiscenteUsuario() {
		return getUsuarioLogado().getDiscenteAtivo();
	}
	
	/**
	 * Usado para que após remover ou modificar algum tópico aula o sistema busque-os novamente
	 * juntamente com seus materiais.
	 */
	public void clearCacheTopicosAula() {
		
		TopicoAulaMBean topico = getMBean("topicoAula");
		topico.setTopicosAulas(null);
		
	}
	
	/**
	 * retorna a entidade DadosAcesso, que contém as informações da sessão do usuário, com informações de contexto
	 * @return
	 */
	public DadosAcesso getAcessoMenu() {
		return (DadosAcesso) getCurrentSession().getAttribute("acesso");
	}

	/**
	 * Indica se o material a ser cadastrado pode ser cadastrado em várias
	 * turmas do mesmo componente curricular.
	 * @return
	 */
	protected boolean cadastrarEmVariasTurmas() {
		return false;
	}

	public List<String> getCadastrarEm() {
		return cadastrarEm;
	}

	public void setCadastrarEm(List<String> cadastrarEm) {
		this.cadastrarEm = cadastrarEm;
	}

	/**
	 * Retorna true se usuário possuir alguns dos papeis passados como
	 * parâmetro
	 * 
	 * @param papeis
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public boolean isUserInRole(int... papeis) {

		UsuarioGeral user = getUsuarioLogado();
		for (int papel : papeis) {
			if (user.isUserInRole(papel))
				return true;
		}

		return false;
	}

	/**
	 * Verifica se o usuário tem um determinado papel
	 *
	 * @param papel
	 * @return
	 */
	public boolean isUserInRole(int papel) {
		return  getUsuarioLogado() != null && getUsuarioLogado().isUserInRole(papel);
	}
	
	
	
	/**
	 * Registra uma ação sobre uma entidade da turma virtual.
	 * 
	 * @param entidade o tipo da entidade que sofreu a ação
	 * @param acao a ação realizada sobre a entidade
	 * @param ids as ids dos objetos que sofreram a ação.
	 */
	public void registrarAcao (String descricao, EntidadeRegistroAva entidade, AcaoAva acao,  int ... ids){
		//Somente é registrado a ação, se o acesso não for realizado através da área pública.
		if( !isAcessoAreaPublica() ){ 
			RegistroAcaoAvaMBean rBean = getMBean("registroAcaoAva");
			rBean.registrarAcao(descricao, entidade, acao, ids);
		}
	}
	
	/**
	 * Registra uma ação sobre uma entidade em mais de uma turma virtual.
	 * 
	 * @param entidade o tipo da entidade que sofreu a ação.
	 * @param acao a ação realizada sobre a entidade.
	 * @param turmas as turmas em que a ação será cadastradas.
	 * @param ids as ids dos objetos que sofreram a ação.
	 */
	public void registrarAcao (String descricao, EntidadeRegistroAva entidade, AcaoAva acao, List<Turma> turmas ,  int ... ids){
		//Somente é registrado a ação, se o acesso não for realizado através da área pública.
		if( !isAcessoAreaPublica() ){ 
			RegistroAcaoAvaMBean rBean = getMBean("registroAcaoAva");
			rBean.setTurmasSucesso(turmas);
			rBean.registrarAcao(descricao, entidade, acao, ids);
		}
	}
	
	/**
	 * Registra uma ação sobre uma entidade na turma atual ou nas turmas virtuais do cadastrarEm.
	 * 
	 * @param entidade o tipo da entidade que sofreu a ação
	 * @param acao a ação realizada sobre a entidade
	 * @param turmasCadastrarEm se a ação deve ser registrada nas turmas do cadastrarEm.
	 * @param ids as ids dos objetos que sofreram a ação.
	 */
	public void registrarAcao (String descricao, EntidadeRegistroAva entidade, AcaoAva acao, boolean turmasCadastrarEm, int ... ids){
		//Somente é registrado a ação, se o acesso não for realizado através da área pública.
		if( !isAcessoAreaPublica() ){ 
			RegistroAcaoAvaMBean rBean = getMBean("registroAcaoAva");
			
			if ( turmasCadastrarEm  )
				rBean.setTurmasSucesso(cadastrarEmToTurmas());
			
			rBean.registrarAcao(descricao, entidade, acao, ids);
		}
	}
		
	public String getPaginaOrigem() {
		return paginaOrigem;
	}

	public void setPaginaOrigem(String paginaOrigem) {
		this.paginaOrigem = paginaOrigem;
	}

	public List<Date> getFeriadosTurma() {
		return feriadosTurma;
	}

	public void setFeriadosTurma(List<Date> feriadosTurma) {
		this.feriadosTurma = feriadosTurma;
	}
	
	/**
	 * Método utlizando nos MBean's que herdam,
	 * para verifica se o acesso é realizado através da
	 * área pública do SIGAA.
	 * @return
	 */
	public boolean isAcessoAreaPublica(){
		return getCurrentRequest().getRequestURI().contains("/public/") && isEmpty( getUsuarioLogado() );
	}
	
	/** Transforma o cadastrarEm numa lista de turma.<br/><br/>
	 *
	 * <ul>
	 * 	<li>Não é utilizado por JSPs.</li>
	 * </ul>
	 * 	 
	 * @param acao
	 * @return 
	 */
	protected List<Turma> cadastrarEmToTurmas ( ){
		
		List<Turma> turmas = new ArrayList<Turma>();
		if ( !isEmpty(cadastrarEm)){
			for (String tid : cadastrarEm) {
				int idTurma = Integer.valueOf(tid);
				Turma t = new Turma();
				t.setId(idTurma);
				turmas.add(t);
			}
		}	
		return turmas;
	}
	
}