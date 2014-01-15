/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 11, 2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.graduacao.MudancaCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.RetificacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MudancaCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoMudancaCurricular;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;

/**
 * MBeam responsável pelo caso de uso de exibir TODAS as informações do aluno durante o vínculo dele com a Universidade.
 * Mostra os afastamentos, aproveitamentos, mudanças de matrizes, prorrogações, retificações, etc
 * 
 * @author Victor Hugo
 *
 */
@Component("historicoDiscente")
@Scope("session")
public class HistoricoDiscenteMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente {

	/** Link da página com os dados do histórico do aluno. */
	private static final String VIEW_HISTORICO = "/graduacao/discente/view_all.jsp";

	/** Coleção de afastamentos do aluno. */
	private Collection<MovimentacaoAluno> afastamentos = new ArrayList<MovimentacaoAluno>();

	/** Coleção de prorrogações do aluno. */
	private Collection<MovimentacaoAluno> prorrogacoes = new ArrayList<MovimentacaoAluno>();

	/** Coleção de aproveitamentos do aluno. */
	private Collection<MatriculaComponente> aproveitamentos = new ArrayList<MatriculaComponente>();

	/** Coleção de retificações do aluno. */
	private Collection<RetificacaoMatricula> retificacoes = new ArrayList<RetificacaoMatricula>();

	/** Coleção de mudanças de matrizes curriculares do aluno. */
	private Collection<MudancaCurricular> matrizes = new ArrayList<MudancaCurricular>();

	/** Coleção de mudanças de currículos do aluno. */
	private Collection<MudancaCurricular> curriculos = new ArrayList<MudancaCurricular>();
	
	/** Coleção de mudanças de habilitações do aluno. */
	private Collection<MudancaCurricular> habilitacoes = new ArrayList<MudancaCurricular>();

	/** Coleção de observações do aluno. */
	private Collection<ObservacaoDiscente> observacoes = new ArrayList<ObservacaoDiscente>();

	/** Coleção de mudanças de modalidades do aluno. */
	private Collection<MudancaCurricular> modalidades = new ArrayList<MudancaCurricular>();

	/** Coleção de mudanças de cursos do aluno. */
	private Collection<MudancaCurricular> cursos = new ArrayList<MudancaCurricular>();

	/** Coleção de mudanças de turnos do aluno. */
	private Collection<MudancaCurricular> turnos = new ArrayList<MudancaCurricular>();

	/** Coleção de mudanças de graus acadêmicos do aluno. */
	private Collection<MudancaCurricular> grausAcademicos = new ArrayList<MudancaCurricular>();

	/**
	 * Construtor Padrão
	 */
	public HistoricoDiscenteMBean() {
		this.obj = new Discente();
	}

	/**
	 * Inicia o caso de uso de visualização do histórico completo do aluno.
	 * JSP: /sigaa.war/ead/menu.jsp
	 * /sigaa.war/graduacao/menus/aluno.jsp
	 * /sigaa.war/graduacao/menus/cdp.jsp
	 * /sigaa.war/graduacao/menus/consultas.jsp
	 * /sigaa.war/graduacao/menus/coordenacao.jsp
	 * /sigaa.war/portais/cpdi/abas/sitdepartamento.jsp
	 * /sigaa.war/portais/rh_plan/abas/graduacao.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException{
		checkRole(SigaaPapeis.SEDIS, SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.PORTAL_PLANEJAMENTO,
				SigaaPapeis.CONSULTADOR_ACADEMICO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.GESTOR_NEE);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");

		if ( getParameterBoolean("eadAluno") ) {
			buscaDiscenteMBean.setEad(true);
		}
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.HISTORICO_COMPLETO_DISCENTE);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Pega o discente selecionado e resgata todo o histórico de movimentação do aluno.
	 * JSP: Não invocado por JSP
	 *
	 */
	public String selecionaDiscente() {
		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class);
		MatriculaComponenteDao daoMC = getDAO(MatriculaComponenteDao.class);
		MudancaCurricularDao daoMudanca = getDAO(MudancaCurricularDao.class);
		DiscenteDao discenteDao = getDAO(DiscenteDao.class);

		try {
			obj = dao.findByPrimaryKey(obj.getId(), DiscenteGraduacao.class);

			afastamentos = dao.findByDiscenteOrTipoMovimentacao(obj.getId(), 0, true, 0, NivelEnsino.GRADUACAO, null);

			prorrogacoes = dao.findProrrogacoesByDiscente( obj.getDiscente());

			aproveitamentos = daoMC.findByDiscente( obj.getDiscente(), SituacaoMatricula.getSituacoesAproveitadasArray() );

			retificacoes = dao.findByExactField(RetificacaoMatricula.class, "matriculaAlterada.discente.id", obj.getId());

			matrizes = daoMudanca.findByDiscenteTipo(obj.getId(), TipoMudancaCurricular.MUDANCA_MATRIZ);
			
			modalidades = daoMudanca.findByDiscenteTipo(obj.getId(), TipoMudancaCurricular.MUDANCA_MODALIDADE);
			
			cursos = daoMudanca.findByDiscenteTipo(obj.getId(), TipoMudancaCurricular.MUDANCA_CURSO);
			
			turnos = daoMudanca.findByDiscenteTipo(obj.getId(), TipoMudancaCurricular.MUDANCA_TURNO);
			
			grausAcademicos = daoMudanca.findByDiscenteTipo(obj.getId(), TipoMudancaCurricular.MUDANCA_GRAU_ACADEMICO);

			curriculos = daoMudanca.findByDiscenteTipo(obj.getId(), TipoMudancaCurricular.MUDANCA_CURRICULO);
			
			habilitacoes = daoMudanca.findByDiscenteTipo(obj.getId(), TipoMudancaCurricular.MUDANCA_HABILITACAO);

			observacoes =  discenteDao.findObservacoesDiscente( obj.getDiscente());

		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Erro ao carregar as informações do discente selecionado. Contacte a administração do sistema. <br>" + e.getMessage());
		}
		return forward(VIEW_HISTORICO);
	}

	/**
	 * Exibe o histórico do discente selecionado
	 * JSP: /sigaa.war/graduacao/discente/view_all.jsp
	 * /sigaa.war/stricto/matricula/opcoes.jsp
	 * 
	 * @return
	 * @throws Exception
	 */
	public String historico() throws Exception {

		//if (obj.getNivel() == NivelEnsino.GRADUACAO) {
			HistoricoMBean historico = (HistoricoMBean) getMBean("historico");
			historico.setDiscente(obj);
			return historico.selecionaDiscente();
//		} else {
//			addMensagemErro("O discente selecionado precisa ser de graduação");
//			return null;
//		}

	}

	/**
	 * Exibe o atestado de matrícula do discente selecionado
	 * JSP: /sigaa.war/graduacao/discente/view_all.jsp
	 * 
	 * @return
	 * @throws Exception
	 */
	public String atestadoMatricula() throws Exception {

		//if (obj.getNivel() == NivelEnsino.GRADUACAO) {
			AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
			getCurrentSession().setAttribute("atestadoLiberado", obj.getId());
			atestado.setDiscente(obj);
			return atestado.selecionaDiscente();
		/*} else {
			addMensagemErro("O discente selecionado precisa ser de graduação");
			return null;
		}*/

	}

	/**
     * Redireciona para a tela com o resultado de discente da busca de discentes.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/discente/view_all.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltar() {
		return forward("/graduacao/busca_discente.jsp");
	}
	
	/**
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) {
		obj = discente;
	}

	/** Retorna uma coleção de afastamentos do aluno. 
	 * @return Coleção de afastamentos do aluno. 
	 */
	public Collection<MovimentacaoAluno> getAfastamentos() {
		return afastamentos;
	}

	/** Seta uma coleção de afastamentos do aluno.
	 * @param afastamentos Coleção de afastamentos do aluno. 
	 */
	public void setAfastamentos(Collection<MovimentacaoAluno> afastamentos) {
		this.afastamentos = afastamentos;
	}

	/** Retorna a coleção de aproveitamentos do aluno. 
	 * @return Coleção de aproveitamentos do aluno. 
	 */
	public Collection<MatriculaComponente> getAproveitamentos() {
		return aproveitamentos;
	}

	/** Seta a coleção de aproveitamentos do aluno.
	 * @param aproveitamentos
	 */
	public void setAproveitamentos(Collection<MatriculaComponente> aproveitamentos) {
		this.aproveitamentos = aproveitamentos;
	}

	/** Retorna a coleção de prorrogações do aluno. 
	 * @return Coleção de prorrogações do aluno. 
	 */
	public Collection<MovimentacaoAluno> getProrrogacoes() {
		return prorrogacoes;
	}

	/** Seta a coleção de prorrogações do aluno.
	 * @param prorrogacoes Coleção de prorrogações do aluno. 
	 */
	public void setProrrogacoes(Collection<MovimentacaoAluno> prorrogacoes) {
		this.prorrogacoes = prorrogacoes;
	}

	/** Retorna a coleção de retificações do aluno.
	 * @return Coleção de retificações do aluno. 
	 */
	public Collection<RetificacaoMatricula> getRetificacoes() {
		return retificacoes;
	}

	/** Seta a coleção de retificações do aluno. 
	 * @param retificacoes Coleção de retificações do aluno. 
	 */
	public void setRetificacoes(Collection<RetificacaoMatricula> retificacoes) {
		this.retificacoes = retificacoes;
	}

	/** Retorna a coleção de mudanças de matrizes curriculares do aluno. 
	 * @return Coleção de mudanças de matrizes curriculares do aluno. 
	 */
	public Collection<MudancaCurricular> getMatrizes() {
		return matrizes;
	}

	/** Seta a coleção de mudanças de matrizes curriculares do aluno.
	 * @param matrizes Coleção de mudanças de matrizes curriculares do aluno. 
	 */
	public void setMatrizes(Collection<MudancaCurricular> matrizes) {
		this.matrizes = matrizes;
	}

	/** Retorna a coleção de mudanças de currículos do aluno.
	 * @return Coleção de mudanças de currículos do aluno. 
	 */
	public Collection<MudancaCurricular> getCurriculos() {
		return curriculos;
	}

	/** Seta a coleção de mudanças de currículos do aluno. 
	 * @param curriculos Coleção de mudanças de currículos do aluno. 
	 */
	public void setCurriculos(Collection<MudancaCurricular> curriculos) {
		this.curriculos = curriculos;
	}

	/** Retorna a coleção de observações do aluno. 
	 * @return Coleção de observações do aluno. 
	 */
	public Collection<ObservacaoDiscente> getObservacoes() {
		return this.observacoes;
	}

	/** Seta a coleção de observações do aluno.
	 * @param observacoes Coleção de observações do aluno. 
	 */
	public void setObservacoes(Collection<ObservacaoDiscente> observacoes) {
		this.observacoes = observacoes;
	}

	/** Retorna a coleção de mudanças de habilitações do aluno. 
	 * @return Coleção de mudanças de habilitações do aluno. 
	 */
	public Collection<MudancaCurricular> getHabilitacoes() {
		return habilitacoes;
	}

	/** Seta a coleção de mudanças de habilitações do aluno.
	 * @param habilitacoes Coleção de mudanças de habilitações do aluno. 
	 */
	public void setHabilitacoes(Collection<MudancaCurricular> habilitacoes) {
		this.habilitacoes = habilitacoes;
	}

	/** Retorna a coleção de mudanças de modalidades do aluno. 
	 * @return Coleção de mudanças de modalidades do aluno. 
	 */
	public Collection<MudancaCurricular> getModalidades() {
		return modalidades;
	}

	/** Seta a coleção de mudanças de modalidades do aluno.
	 * @param modalidades Coleção de mudanças de modalidades do aluno. 
	 */
	public void setModalidades(Collection<MudancaCurricular> modalidades) {
		this.modalidades = modalidades;
	}

	/** Retorna a coleção de mudanças de cursos do aluno. 
	 * @return Coleção de mudanças de cursos do aluno. 
	 */
	public Collection<MudancaCurricular> getCursos() {
		return cursos;
	}

	/** Seta a coleção de mudanças de cursos do aluno.
	 * @param cursos Coleção de mudanças de cursos do aluno. 
	 */
	public void setCursos(Collection<MudancaCurricular> cursos) {
		this.cursos = cursos;
	}

	/** Retorna a coleção de mudanças de turnos do aluno. 
	 * @return Coleção de mudanças de turnos do aluno. 
	 */
	public Collection<MudancaCurricular> getTurnos() {
		return turnos;
	}

	/** Seta a coleção de mudanças de turnos do aluno.
	 * @param turnos Coleção de mudanças de turnos do aluno. 
	 */
	public void setTurnos(Collection<MudancaCurricular> turnos) {
		this.turnos = turnos;
	}

	/** Retorna a coleção de mudanças de graus acadêmicos do aluno. 
	 * @return Coleção de mudanças de graus acadêmicos do aluno. 
	 */
	public Collection<MudancaCurricular> getGrausAcademicos() {
		return grausAcademicos;
	}

	/** Seta a coleção de mudanças de graus acadêmicos do aluno.
	 * @param grausAcademicos Coleção de mudanças de graus acadêmicos do aluno. 
	 */
	public void setGrausAcademicos(Collection<MudancaCurricular> grausAcademicos) {
		this.grausAcademicos = grausAcademicos;
	}

}