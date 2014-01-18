/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 15/08/2007 
 *
 */
package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.Curso;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.agenda.jsf.AgendaTurmaMBean;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.dao.portal.PortalTutorDao;
import br.ufrn.sigaa.arq.jsf.NotificacoesMBean;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ead.dominio.SemanaAvaliacao;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.negocio.EADHelper;
import br.ufrn.sigaa.ead.negocio.MetodologiaAvaliacaoHelper;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutorIMD;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed-Bean de controle principal do portal do tutor
 *
 * @author Rafael de A Silva
 * @author Rafael Barros
 */
@Component("portalTutoria") @Scope("session")
public class PortalTutoriaMBean extends SigaaAbstractController<TutorOrientador> {

	/** Link do portal do tutor. */
	public static final String PORTAL_TUTOR = "/metropole_digital/principal.jsp";

	/** Perfil do tutor. */
	private PerfilPessoa perfil;
	
	/** Representa as turmas de entrada em que o tutor do IMD exerce tutoria */
	private Collection<TurmaEntradaTecnico> turmasTutoria = new ArrayList<TurmaEntradaTecnico>();
	
	/** Representa as turmas de entrada em que o tutor do IMD exerce tutoria para um COMBO */
	private Collection<SelectItem> turmasTutoriaCombo = new ArrayList<SelectItem>();
	
	/** Representa a turma de Entrada selecionada na tela inicial do portal do tutor IMD */
	private TurmaEntradaTecnico turmaEntradaSelecionada;
	
	/** Construtor padrão. */
	public PortalTutoriaMBean() throws DAOException, NegocioException {
		
		try {
			TutorOrientador tutorUsuario = getTutorUsuario();
			turmasTutoria = Collections.emptyList();
			if (tutorUsuario != null && tutorUsuario.getIdPerfil() != null)
				perfil = PerfilPessoaDAO.getDao().get(tutorUsuario.getIdPerfil());
		} catch(EmptyResultDataAccessException e) {
			
		}
	}
	
	
	public void findTurmasTutoria() throws DAOException{
		TutoriaIMDDao tutoriaIMDDAO = new TutoriaIMDDao();
		try {
			turmasTutoria = tutoriaIMDDAO.findTurmasByTutor(getUsuarioLogado().getPessoa().getId());
			preencherTurmasTutoria();
		} finally {
			tutoriaIMDDAO.close();
		}
	}	
	
	
	public Collection<TurmaEntradaTecnico> getTurmasTutoria() throws DAOException {
		if(turmasTutoria.isEmpty()){
			findTurmasTutoria();
		}
		return turmasTutoria;
	}


	public void setTurmasTutoria(Collection<TurmaEntradaTecnico> turmasTutoria) {
		this.turmasTutoria = turmasTutoria;
	}


	public TurmaEntradaTecnico getTurmaEntradaSelecionada() {
		return turmaEntradaSelecionada;
	}
	

	public Collection<SelectItem> getTurmasTutoriaCombo() {
		return turmasTutoriaCombo;
	}


	public void setTurmasTutoriaCombo(Collection<SelectItem> turmasTutoriaCombo) {
		this.turmasTutoriaCombo = turmasTutoriaCombo;
	}


	public void setTurmaEntradaSelecionada(
			TurmaEntradaTecnico turmaEntradaSelecionada) {
		this.turmaEntradaSelecionada = turmaEntradaSelecionada;
	}
	
	public void preencherTurmasTutoria(){
		for(TurmaEntradaTecnico turma : turmasTutoria){
			SelectItem item = new SelectItem(turma.getId(), turma.getAnoReferencia() + "." + turma.getPeriodoReferencia() + " - " + turma.getEspecializacao().getDescricao() + " - " + turma.getCursoTecnico().getNome()  + " - " + turma.getOpcaoPoloGrupo().getDescricao());
			turmasTutoriaCombo.add(item);
		}
	}
	
	public String voltarTelaPortal(){
		return forward("/metropole_digital/principal.jsp");
	}


}
