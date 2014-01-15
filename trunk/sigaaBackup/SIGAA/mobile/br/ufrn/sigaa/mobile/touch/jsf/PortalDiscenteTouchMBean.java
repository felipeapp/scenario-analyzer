/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '28/09/2011'
 *
 */
package br.ufrn.sigaa.mobile.touch.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.mobile.touch.dao.TurmaTouchDao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.portal.jsf.PortalDiscenteMBean;

/**
 * MBean responsável por controlar operações ligadas as turmas do 
 * discente com acesso ao sistema via dispositivos móveis. 
 * 
 * @author Ilueny Santos
 * @author Fred Castro
 *
 */
@Component("portalDiscenteTouch")
@Scope("session")
public class PortalDiscenteTouchMBean extends TurmaVirtualTouchMBean<Discente> {

	/** Construtor Padrão.*/
	public PortalDiscenteTouchMBean() {
	}

	/**
	 * Retorna todas as turmas abertas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/login.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String acessarPortal () {
		if (ValidatorUtil.isNotEmpty(getDiscenteUsuario())) {
			getCurrentRequest().getSession(false).setAttribute("subsistema", SigaaSubsistemas.SIGAA_MOBILE);			
			return forward(getPaginaPrincipal());
		}else {
			addMensagemErro("Erro ao acessar o portal do discente.");
		}
		return null;
	}
	
	/** 
	 * Carrega todas as turmas do discente logado.
	 * Este método não é chamado por JSP.
	 */
	public void carregarTurmas(boolean todas){		
		TurmaTouchDao turmaDao = null;
		
		Integer ano = null;
		Integer periodo = null;
		
		if (!todas) {
			CalendarioAcademico cal = getCalendarioVigente();			
			ano = cal.getAno();
			periodo = cal.getPeriodo();
		}
				
		try {
			turmaDao = getDAO(TurmaTouchDao.class);
			
			setTurmas( turmaDao.findAllByDiscente(getDiscenteUsuario().getDiscente(), ano, periodo,	
					SituacaoMatricula.getSituacoesMatriculadoOuConcluido().toArray(new SituacaoMatricula[0]), 
						new SituacaoTurma[] {
								new SituacaoTurma(SituacaoTurma.CONSOLIDADA), 
								new SituacaoTurma(SituacaoTurma.ABERTA), 
								new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE)
						}
					)
			);
			
			// Passa por todas as turmas do aluno, trocando as subturmas por turmas agrupadoras.
			List <Turma> turmasARemover = new ArrayList <Turma> ();
			List <Turma> turmasATrocar = new ArrayList <Turma> ();
			List <Integer> indices = new ArrayList <Integer> ();
			TurmaDao tDao = getDAO(TurmaDao.class);;
			
			for (int i = 0; i < turmas.size(); i++){
				Turma t = turmas.get(i);
				
				if (t.getTurmaAgrupadora() != null){
					indices.add(i);
					turmasARemover.add(t);
					turmasATrocar.add(tDao.findByPrimaryKeyOtimizado(t.getTurmaAgrupadora().getId()));
				}
			}
						
			if (!turmasATrocar.isEmpty()){
				for (int i = 0; i < turmasATrocar.size(); i++){
					turmas.remove(turmasARemover.get(i));
					turmas.add(indices.get(i), turmasATrocar.get(i));
				}
			}	
			
		} catch (DAOException e) {
			notifyError(e);
		} finally {
			if (turmaDao != null)
				turmaDao.close();
		}
	}
	
	/**
	 * Chama a criação do histórico no SIGAA e adiciona as possíveis mensagens de erro geradas.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/portal_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String gerarHistorico() throws ArqException {
				
		redirect("/mobile/touch/gerarHistorico?sistema=" + getSistema());		
		
		if (getCurrentSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION) != null){
			addMensagens((ListaMensagens) getCurrentSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION));
			getCurrentSession().removeAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);
		}
		
		return null;
	}
	
	/**
	 * Chama o processo de gerar o atestado de matrícula, adicionando as possíveis mensagens de erro
	 * ocorridas durante o processamento.
	 * 
	 * <br/><br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/portal_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws IOException
	 */
	public String gerarAtestadoMatricula() throws DAOException, SegurancaException, IOException {
		PortalDiscenteMBean discenteBean = (PortalDiscenteMBean) getMBean("portalDiscente");
		
		discenteBean.atestadoMatricula();
		
		/*if (getCurrentSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION) != null)
			addMensagens((ListaMensagens) getCurrentSession().getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION));
		
		getCurrentSession().removeAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);*/
		
		return null;
	}
	
	@Override
	public String getPaginaPrincipal () {
		return "/mobile/touch/menu.jsp";
	}
	
	@Override
	public String getPaginaListarAulas () {
		return "/mobile/touch/ava/aulas_discente.jsf";
	}
	
	@Override
	public String getPaginaTurmas() {
		return "/mobile/touch/ava/turmas_discente.jsf";
	}
	
	@Override
	public String getPaginaTopicoAula () {
		return "/mobile/touch/ava/topico_aula_discente.jsf";
	}
	
}
