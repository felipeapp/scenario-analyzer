/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 14/01/2010
 *
 */
package br.ufrn.sigaa.avaliacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.GrupoPerguntas;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.mensagens.MensagensAvaliacaoInstitucional;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Controller respons�vel pela visualiza��o da Avalia��o Institucional dada pelo
 * usu�rio no ano-per�odo anterior ao atual.<br>
 * � usado outro controller a fim de evitar sobreposi��o de estados com o
 * controller utilizado no preenchimento da Avalia��o Institucional do
 * ano-per�odo atual.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Component("avaliacaoInstitucionalAnterior") @Scope("request")
public class AvaliacaoInstitucionalAnteriorMBean extends SigaaAbstractController<AvaliacaoInstitucional> {

	/** Grupo de perguntas da Avalia��o Institucional. */
	private List<GrupoPerguntas> grupoPerguntas;

	/** Lista de docentes das turmas em que o discente est� matriculado. */
	private List<DocenteTurma> docenteTurmasDiscente;

	/** Lista de turmas avaliadas. */
	private List<Turma> turmasDiscenteComMaisDeUmDocente;

	/** Lista de turmas que o docente leciona. */
	private List<DocenteTurma> turmasDocente;

	/** Lista de turmas que o discente trancou. */
	private List<Turma> trancamentosDiscente;
	
	/** Lista de IDs de perguntas n�o respondidas. */
	private List<Integer> perguntasNaoRespondidas;

	/** Ano da Avalia��o Institucional */
	private Integer ano;
	
	/** Per�odo da Avalia��o Institucional. */
	private Integer periodo;
	

	/** Lista as avalia��es preenchidas anteriormente pelo discente.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String listaAnterior() {
		CalendarioAvaliacaoInstitucionalMBean mBean = getMBean("calendarioAvaliacaoInstitucionalBean");
		mBean.setAllPreenchidosDiscentes(null);
		return forward("/avaliacao/calendario/lista_anterior.jsp");
	}
	
	/**
	 * Exibe os dados da avalia��o anterior feita pelo discente. <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/lista_anterior_discente.jsp</li>
	 * </ul>
	 * 
	 * @return Link para o formul�rio com os dados da avalia��o anterior.
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String reverAnterior() throws ArqException, NegocioException {
		init();
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		CalendarioAvaliacao calendarioAvaliacao = dao.findByPrimaryKey(getParameterInt("id"), CalendarioAvaliacao.class);
		if (calendarioAvaliacao == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		Discente discente = getUsuarioLogado().getDiscenteAtivo().getDiscente();

		if (discente == null || !discente.isGraduacao() || discente.isDiscenteEad()) {
			throw new SegurancaException();
		} 
		// Verificar se existem avalia��es j� salvas
		ano = calendarioAvaliacao.getAno();
		periodo = calendarioAvaliacao.getPeriodo();
		this.obj = getDAO(AvaliacaoInstitucionalDao.class).findByDiscente(discente, ano, periodo, calendarioAvaliacao.getFormulario());
		
		if (this.obj == null) { // N�o existiam avalia��es anteriores. 
			addMensagemErro("N�o foi encontrada uma Avalia��o Institucional preenchida para o formul�rio "
					+ calendarioAvaliacao.getFormulario().getTitulo() + " em " + ano + "." + periodo);
			return null;
		} 

		if (this.obj.getRespostas() != null) this.obj.getRespostas().iterator();
		if (this.obj.getTrancamentos() != null) this.obj.getTrancamentos().iterator();
		if (this.obj.getObservacoesDocenteTurma() != null) this.obj.getObservacoesDocenteTurma().iterator();
		
		grupoPerguntas = calendarioAvaliacao.getFormulario().getGrupoPerguntas();
		
		return forward("/avaliacao/view_discente.jsp");
	}

	/** Retorna o grupo de perguntas para o discente.
	 * @return Grupo de perguntas para o discente. 
	 * @throws DAOException
	 */
	public List<GrupoPerguntas> getGrupoPerguntas() throws DAOException {
		return grupoPerguntas;
	}
	
	/** Retorna o grupo de perguntas para o discente.
	 * @return Grupo de perguntas para o discente. 
	 * @throws DAOException
	 */
	public List<GrupoPerguntas> getGruposDiscente() throws DAOException {
		return grupoPerguntas;
	}
	
	/** Retorna a lista de docentes das turmas em que o discente est� matriculado. 
	 * @return Lista de docentes das turmas em que o discente est� matriculado. 
	 * @throws DAOException
	 */
	public List<DocenteTurma> getDocenteTurmasDiscente() throws DAOException {
		if (docenteTurmasDiscente == null) {
			docenteTurmasDiscente = getDAO(AvaliacaoInstitucionalDao.class).buscarTurmasComApenasUmDocenteNoSemestrePorDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente(), getAno(), getPeriodo());
		}
		return docenteTurmasDiscente;
	}
	
	/** Retorna a lista de turmas avaliadas.  
	 * @return Lista de turmas avaliadas. 
	 * @throws DAOException
	 */
	public List<Turma> getTurmasDiscenteComMaisDeUmDocente() throws DAOException {
		if (turmasDiscenteComMaisDeUmDocente == null)
			turmasDiscenteComMaisDeUmDocente = getDAO(AvaliacaoInstitucionalDao.class).buscarTurmasComMaisDeUmDocenteNoSemestrePorDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente(), getAno(), getPeriodo());
		return turmasDiscenteComMaisDeUmDocente;
	}
	
	/** Retorna a lista de turmas que o docente leciona. 
	 * @return Lista de turmas que o docente leciona. 
	 * @throws DAOException
	 */
	public List<DocenteTurma> getTurmasDocente() throws DAOException {
		if (turmasDocente == null) {
			if (getUsuarioLogado().getVinculoAtivo().getDocenteExterno() == null)
				turmasDocente = getDAO(TurmaDao.class).findTurmasSemestreByDocente(getUsuarioLogado().getServidorAtivo(), getAno(), getPeriodo(), null);
			else
				turmasDocente = getDAO(TurmaDao.class).findTurmasSemestreByDocenteExterno(getUsuarioLogado().getVinculoAtivo().getDocenteExterno(), getAno(), getPeriodo(), null);
			for(DocenteTurma dt: turmasDocente)
				dt.getTurma().getId();
		}
		return turmasDocente;
	}

	/** Retorna a lista de turmas que o discente trancou. 
	 * @return Lista de turmas que o discente trancou. 
	 * @throws DAOException
	 */
	public List<Turma> getTrancamentosDiscente() throws DAOException {
		if (trancamentosDiscente == null)
			trancamentosDiscente = getDAO(AvaliacaoInstitucionalDao.class).findTrancamentosSemestreByDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente(), getAno(), getPeriodo());
		return trancamentosDiscente;
	}
	
	/** Retorna a lista de IDs de perguntas n�o respondidas. 
	 * @return Lista de IDs de perguntas n�o respondidas. 
	 */
	public Integer[] getPerguntasNaoRespondidas() {
		if (perguntasNaoRespondidas == null) return null;
		return perguntasNaoRespondidas.toArray(new Integer[perguntasNaoRespondidas.size()]);
	}
	
	/** Indica se o discente est� apto para avaliar.<br/>
	 * M�todo n�o invocado por JSP�s
	 * @return
	 * @throws DAOException
	 */
	public boolean discentePodeAvaliar() throws DAOException {
		boolean pode = !isEmpty(getDocenteTurmasDiscente()) || !isEmpty(getTurmasDiscenteComMaisDeUmDocente());
		return pode;
	}
	
	/** Retorna o ano da Avalia��o Institucional 
	 * @return
	 * @throws DAOException
	 */
	public int getAno() throws DAOException {
		return ano;
	}
	
	/** Retorna o per�odo da Avalia��o Institucional 
	 * @return
	 * @throws DAOException
	 */
	public int getPeriodo() throws DAOException {
		return periodo;
	}

	/** Verifica se o acesso se d� por usu�rio discente.
	 * @return
	 * @throws NegocioException
	 */
	public String getVerificaAcessoDiscente() throws NegocioException {
		if (obj == null) {
			throw new NegocioException(UFRNUtils.getMensagem(MensagensAvaliacaoInstitucional.ACESSO_INDIRETO_AVALIACAO, "Por favor, utilize os links do Portal Discente.").getMensagem());
		}
		return null;
	}
	
	/** Verifica se o acesso se d� por usu�rio docente.
	 * @return
	 * @throws NegocioException
	 */
	public String getVerificaAcessoDocente() throws NegocioException {
		if (obj == null) {
			throw new NegocioException(UFRNUtils.getMensagem(MensagensAvaliacaoInstitucional.ACESSO_INDIRETO_AVALIACAO, "Por favor, utilize os links do Portal Docente.").getMensagem());
		}
		return null;
	}
	
	/** Inicializa os atributos deste controller. */
	private void init() {
		this.docenteTurmasDiscente = null;
		this.grupoPerguntas = null;
		this.perguntasNaoRespondidas = null;
		this.perguntasNaoRespondidas = null;
		this.trancamentosDiscente = null;
		this.turmasDiscenteComMaisDeUmDocente = null;
		this.turmasDocente = null;
		this.ano = null;
		this.periodo = null;
	}

}
