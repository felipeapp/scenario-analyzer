/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/11/2008
 *
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaHelper;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;

/**
 * Managed bean responsável pelo envio de e-mails para turmas, permitindo
 * a seleção da turma desejada para a notificação.
 * 
 * Será utilizado fora do contexto da turma virtual.
 * 
 * @author Diego Jácome
 *
 */
@Component("envioEmailTurma") @Scope("request")
public class EnvioEmailTurmaMBean extends ControllerTurmaVirtual {

	/** Turma na qual será enviado os e-mails */
	private Turma turma;

	/** Titulo do e-mail */
	private String titulo;
	
	/** texto do e-mail */
	private String texto;
	
	public EnvioEmailTurmaMBean() {
		turma = new Turma();
	}
	
	/**
	 * Exibe o formulário para selecionar a turma para enviar um e-mail.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ensino/turma/busca_turma.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String selecionarTurma() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, 
					SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, 
					SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO);

		turma.setId( getParameterInt("id", 0) );
		
		// Validar acesso do usuário à turma, no caso de coordenadores
		TurmaDao turmaDao = getDAO(TurmaDao.class);
		Curso curso = SigaaHelper.getCursoAtualCoordenacao();
		
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) && curso == null)
			throw new NegocioException("Não foi possível definir qual o curso do qual você é coordenador ou secretário. Por favor, acesse a consulta de turmas a partir do Portal do Coordenador.");
		
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) && !turmaDao.existsAlunosCurso(getTurma(), curso)){
			addMensagem(MensagensTurmaVirtual.NAO_HA_ALUNOS_DO_CURSO_MATRICULADOS, curso.getDescricao());
			return null;
		}
		
		// Popular dados da turma selecionada
		turma = getGenericDAO().refresh(turma);
		if (turma == null) {
			addMensagem(MensagensTurmaVirtual.NENHUMA_TURMA_SELECIONADA);
			return null;
		}
				
		return forward(getFormPage());
	}
		
	/**
	 * Envia e-mails para os discentes da turma selecionada.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ensino/turma/busca_turma.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String enviarEmail() throws ArqException, NegocioException {
	
		if (isEmpty(titulo))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Título");
		if (isEmpty(texto))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Texto");
		
		if (hasErrors())
			return null;
		
		// Validar acesso do usuário à turma, no caso de coordenadores
		TurmaVirtualDao tDao = getDAO(TurmaVirtualDao.class);
		Curso curso = null; 
		
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) && curso == null)
			curso = SigaaHelper.getCursoAtualCoordenacao();
				
		Collection<MatriculaComponente> matriculas = tDao.findEmailsParticipantesTurmasCurso(turma,curso,SituacaoMatricula.MATRICULADO);
		
		titulo = "Nova Notificação para Turma - " +turma.getDescricaoSemDocente()+ ": " + titulo;
		
		if (curso != null)
			notificarDiscentes(titulo, texto, matriculas);
		else
			notificarTurma(turma, titulo, texto,  ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE , ControllerTurmaVirtual.AUTORIZADO , ControllerTurmaVirtual.DOCENCIA_ASSISTIDA);
	
		flash("E-mail enviado com sucesso.");

		return forward("/ensino/turma/busca_turma.jsf");
	}
	
	public String getFormPage() {
		return "/ava/EmailAva/_form_email.jsf";
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
}