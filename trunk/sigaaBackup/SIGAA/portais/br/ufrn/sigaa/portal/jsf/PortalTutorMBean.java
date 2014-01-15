/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 15/08/2007 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.NotificacoesMBean;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ead.dominio.SemanaAvaliacao;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.negocio.EADHelper;
import br.ufrn.sigaa.ead.negocio.MetodologiaAvaliacaoHelper;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed-Bean de controle principal do portal do tutor
 *
 * @author David Pereira
 * @author Victor Hugo
 */
@Component("portalTutor") @Scope("session")
public class PortalTutorMBean extends SigaaAbstractController<TutorOrientador> {

	/** Link do portal do tutor. */
	public static final String PORTAL_TUTOR = "/portais/tutor/tutor.jsp";

	/** Lista de semanas de avaliação. */
	private List<SemanaAvaliacao> semanas;

	/** Coleção de discentes orientados. */
	private Collection<DiscenteGraduacao> alunosOrientados;

	/** Perfil do tutor. */
	private PerfilPessoa perfil;

	/** Indica se a metodologia é de uma avaliação. */
	private Boolean administracao;
	
	/** Define a lista de turmas virtuais associadas ao tutor. */
	private List<Turma> turmasVirtuaisHabilitadas;
	
	/** Indica se há um usuário da aministração da EAD logado como o usuário atual. Se sim, este deve visualizar todas as semanas. */
	private boolean administracaoEAD;
	
	/** Representa as solicitações de matrícula */
	private Collection<Discente> solicitacoesMatricula;
	
	/** Número total de pré-matrículas realizadas */
	private int totalPreMatriculas;
	
	/** Metodologia utilizada */
	private MetodologiaAvaliacao metodologia;
	
	/** Lista de pólo-cursos. */
	private List<SelectItem> poloCursosCombo;
	
	/** Total de alunos orientandos. */
	private Integer qtdAlunosOrientandos;
	
	/** Indica o polo e o curso selecionado no portal do tutor. */
	private Integer idPoloCursoEscolhido = 0;
	
	/** Construtor padrão. */
	public PortalTutorMBean() throws DAOException, NegocioException {
		
		if (getCurrentSession().getAttribute("ADMINISTRACAO_EAD") != null)
			administracaoEAD = true;
		
		try {
			TutorOrientador tutorUsuario = getTutorUsuario();
			
			if (tutorUsuario != null && tutorUsuario.getIdPerfil() != null)
				perfil = PerfilPessoaDAO.getDao().get(tutorUsuario.getIdPerfil());
		} catch(EmptyResultDataAccessException e) {
			
		}
	}

	
	/** Retorna a quantidade de alunos orientados.
	 * @return
	 * @throws DAOException
	 */
	public int getQtdAlunosOrientandos() throws DAOException {

		if (qtdAlunosOrientandos == null) {
			Collection<DiscenteGraduacao> lista = EADHelper.findDiscentesByTutor(getUsuarioLogado().getTutor().getId(),getUsuarioLogado().getPessoa().getId(),null,null);
			if (lista!=null)
				qtdAlunosOrientandos = lista.size();
			else
				qtdAlunosOrientandos = 0;
		}
		return qtdAlunosOrientandos;
	}

	/** Retorna o link para a página de detalhes do aluno.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 *	<ul>
	 *		<li>sigaa.war/portais/tutor/menu_tutor.jsp</li>
	 *		<li>sigaa.war/portais/tutor/tutor.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String verAlunos() {
		return forward("/portais/tutor/alunos.jsp");
	}

	/** Retorna a coleção de discentes orientados. 
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> getAlunosOrientados() throws DAOException {
		if (alunosOrientados == null) {
			alunosOrientados = EADHelper.findDiscentesByTutor(getUsuarioLogado().getTutor().getId(),getUsuarioLogado().getPessoa().getId(),null,null);
		}
		return alunosOrientados;
	}

	/** Retorna o link para o formulário da busca de turmas.
	 * Consultar turma
	 * Método não é chamado por nenhuma JSP
	 * @param evt
	 * @throws DAOException
	 * @throws IOException
	 */
	public void consultaTurma(ActionEvent evt) throws DAOException, IOException {
		forward("/ensino/turma/busca_turma.jsp");
	}
	
	
	
	/**
	 * Retorna uma lista de Turmas Virtuais habilitadas para o tutor
	 * referente a um determinado usuário.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li> sigaa.war/portais/tutor/tutor.jsp </li>
	 * </ul>
	 */
	public List<Turma> getTurmasVirtuaisHabilitadas() throws DAOException {
		if (turmasVirtuaisHabilitadas == null) {
			TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class);
			turmasVirtuaisHabilitadas = dao.findTurmasEADHabilitadasByPessoa(getUsuarioLogado().getPessoa());
			
			Collections.sort (turmasVirtuaisHabilitadas, new Comparator<Turma>() {  
	            public int compare(Turma t1, Turma t2) {      		                 
					return new CompareToBuilder()
					.append(t2.getAnoPeriodo(), t1.getAnoPeriodo())
					.append(t1.getNome(), t2.getNome())
					.toComparison();    		                 
	            }
	       });              		            			
		}
		
		return turmasVirtuaisHabilitadas;
	}
	
	
	
	/**
	 * <p>Método referente ao envio de email para os alunos de um determinado tutor, 
	 * baseado na seleção que o mesmo realizar.</p>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 		<li> sigaa.war/portais/tutor/tutor.jsp </li>
	 * 	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String enviarMensagemTodosAlunos() throws ArqException{
		NotificacoesMBean notificacao = getMBean("notificacoes");
		
		if (alunosOrientados.size() > 0){
			ArrayList<Destinatario> destinatarios = new ArrayList<Destinatario>();
			for (DiscenteGraduacao lista : alunosOrientados) {					
				String nome =   lista.getPessoa().getNome();
				String email =  lista.getPessoa().getEmail();
				Usuario u = lista.getUsuario();	
				
				Destinatario destinatario = new Destinatario(nome, email);
				destinatario.setUsuario(u);
				
				destinatarios.add(destinatario);
			}			
			
			notificacao.setDestinatarios(destinatarios);
			
			notificacao.setRemetente( getUsuarioLogado().getNome() );
			
			notificacao.setTitulo("Enviar mensagem aos Orientandos");
			
			notificacao.setDescricao("Caro Tutor, "+
              " Esta operação tem o intuito de facilitar a comunicação junto aos seus alunos. "+
              " Através desta página você poderá enviar uma mensagem para todos os seus alunos. "+
              " Os alunos notificados receberão uma mensagem na Caixa Postal do SIGAA ou via Email. De acordo com a seleção abaixo.");
			
			notificacao.iniciar();
			
		} else {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
			
		return null;
	}
	
	/**
	 * <p>Método referente ao envio de email para os orientandos de um determinado tutor, 
	 * baseado na seleção que o mesmo realizar.</p>
	 * <br> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 		<li> sigaa.war/portais/tutor/tutor.jsp </li>
	 * 	</ul>
	 * @return
	 * @throws NumberFormatException
	 * @throws ArqException
	 */
	public String enviarMensagemAluno() throws NumberFormatException, ArqException{
		
		Integer id = getParameterInt("discente",0);
		UsuarioDao dao = getDAO(UsuarioDao.class);
				
		String nome = null;
		String email = null;
		int idUsuario = 0;
				
		Usuario usuario = dao.findByDiscente(id);
		if(isEmpty(usuario)){
			addMensagemErro("Não é possível enviar email para este discente, porque ele não possui usuário cadastrado no sistema.");
			return null;
		}	
		
		nome =   usuario.getPessoa().getNome();
		email =  usuario.getPessoa().getEmail();
		idUsuario = usuario.getId();
		
		NotificacoesMBean notificacao = getMBean("notificacoes");
		
		ArrayList<Destinatario> destinatarios = new ArrayList<Destinatario>();	
			
		Destinatario destinatario = new Destinatario(nome, email);
		destinatario.setIdusuario(idUsuario);			
		destinatarios.add(destinatario);
		
		notificacao.setDestinatarios(destinatarios);
		
		notificacao.setRemetente( getUsuarioLogado().getNome() );
		
		notificacao.setTitulo("Enviar mensagem aos Orientandos");
		
		notificacao.setDescricao("Caro Tutor, "+
          " Esta operação tem o intuito de facilitar a comunicação junto aos seus alunos. "+
          " Através desta página você poderá enviar uma mensagem para todos os seus alunos. "+
          " Os alunos notificados receberão uma mensagem na Caixa Postal do SIGAA ou via Email. De acordo com a seleção abaixo.");
		
		notificacao.iniciar();
		
		return null;
	}
	

	/** Retorna o perfil do tutor.
	 * @return
	 */
	public PerfilPessoa getPerfil() {
		return perfil;
	}

	/** Seta o perfil do tutor.
	 * @param perfil
	 */
	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}
	
	/** Indica se a metodologia é de uma avaliação. 
	 * @return
	 * @throws DAOException
	 */
	public boolean isAdministracao() throws DAOException { 
		if (administracao == null) {		
			if (getTutorUsuario() != null) {
				metodologia = MetodologiaAvaliacaoHelper.getMetodologia(getTutorUsuario().getPoloCurso().getCurso(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
				if (metodologia == null)
					administracao = false;
				else
					administracao = metodologia.isUmaProva();
			} else if (getDiscenteUsuario() != null) {
				metodologia = MetodologiaAvaliacaoHelper.getMetodologia(getDiscenteUsuario().getCurso(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
				if (metodologia == null)
					administracao = false;
				else
					administracao = metodologia.isUmaProva();
			} else {
				return false;
			}
		}
		return administracao;
	}

	public boolean isAdministracaoEAD() {
		return administracaoEAD;
	}

	public void setAdministracaoEAD(boolean administracaoEAD) {
		this.administracaoEAD = administracaoEAD;
	}
	
	public void setSolicitacoesMatricula(Collection<Discente> solicitacoesMatricula) {
		this.solicitacoesMatricula = solicitacoesMatricula;
	}

	
	/**
	 * Retorna todos as solicitações de matrícula.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/portais/tutor/tutor.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> getSolicitacoesMatricula() throws DAOException {

		if(solicitacoesMatricula == null /*&& isUserInRole(SigaaPapeis.COORDENADOR_CURSO)*/ && getCalendarioVigente().isPeriodoAnaliseCoordenador()){
			SolicitacaoMatriculaDao dao = getDAO( SolicitacaoMatriculaDao.class );
			int ano = getCalendarioVigente().getAno();
			int periodo = getCalendarioVigente().getPeriodo();

			solicitacoesMatricula = dao.findByTutorAnoPeriodo(getUsuarioLogado().getPessoa(), ano, periodo, true, true, true);
			totalPreMatriculas = (dao.getNumeroSolicitacoesMatricula(getUsuarioLogado().getPessoa(), ano, periodo, false)).intValue();
			
		}

		return solicitacoesMatricula;
	}

	public int getTotalPreMatriculas() {
		return totalPreMatriculas;
	}

	public void setTotalPreMatriculas(int totalPreMatriculas) {
		this.totalPreMatriculas = totalPreMatriculas;
	}
	
	public boolean isAlunoEadFazMatriculaOnline(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.ALUNO_EAD_FAZ_MATRICULA_ONLINE);
	}

	public void setTurmasVirtuaisHabilitadas(List<Turma> turmasVirtuaisHabilitadas) {
		this.turmasVirtuaisHabilitadas = turmasVirtuaisHabilitadas;
	}
	
	public void setPCursosCombo(List<SelectItem> poloCursosCombo) {
		this.poloCursosCombo = poloCursosCombo;
	}

	/**
	 * Retorna um combo com os cursos no qual o tutor presencial está associado.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/portais/tutor/tutor.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPoloCursosCombo() throws DAOException {
		
		if (isEmpty(poloCursosCombo)){
			poloCursosCombo = new ArrayList<SelectItem>();
			List<PoloCurso> pcursos = getUsuarioLogado().getTutor().getPoloCursos();
			for (PoloCurso pcurso : pcursos)
				poloCursosCombo.add(new SelectItem(pcurso.getId(), pcurso.getDescricao()));
		}
		
		return poloCursosCombo;
	}	
	
	/**
	 * Carrega os alunos orientados pelo tutor.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/portais/tutor/tutor.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public void carregarAlunos(ValueChangeEvent evt) throws DAOException {
		
		TutorOrientadorDao tDao = null;
		
		try {
			tDao = getDAO(TutorOrientadorDao.class);
			PoloCurso poloCurso = tDao.findByPrimaryKey(Integer.valueOf(evt.getNewValue().toString()), PoloCurso.class);
			if (poloCurso != null)
				alunosOrientados = EADHelper.findDiscentesByTutor(getUsuarioLogado().getTutor().getId(),getUsuarioLogado().getPessoa().getId(),poloCurso.getPolo().getId(),poloCurso.getCurso().getId());
			else
				alunosOrientados = EADHelper.findDiscentesByTutor(getUsuarioLogado().getTutor().getId(),getUsuarioLogado().getPessoa().getId(),null,null);
		} finally {
			if (tDao != null)
				tDao.close();
		}
	}


	public int getQtdTurmasOrientadas () {
		if (turmasVirtuaisHabilitadas!=null)
			return turmasVirtuaisHabilitadas.size();
		else
			return 0;
	}
	
	public void setIdPoloCursoEscolhido(Integer idPoloCursoEscolhido) {
		this.idPoloCursoEscolhido = idPoloCursoEscolhido;
	}


	public Integer getIdPoloCursoEscolhido() {
		return idPoloCursoEscolhido;
	}	
}
