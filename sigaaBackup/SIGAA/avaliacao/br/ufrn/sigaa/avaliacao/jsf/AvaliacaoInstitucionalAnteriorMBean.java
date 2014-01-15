/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Controller responsável pela visualização da Avaliação Institucional dada pelo
 * usuário no ano-período anterior ao atual.<br>
 * É usado outro controller a fim de evitar sobreposição de estados com o
 * controller utilizado no preenchimento da Avaliação Institucional do
 * ano-período atual.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("avaliacaoInstitucionalAnterior") @Scope("request")
public class AvaliacaoInstitucionalAnteriorMBean extends SigaaAbstractController<AvaliacaoInstitucional> {

	/** Grupo de perguntas da Avaliação Institucional. */
	private List<GrupoPerguntas> grupoPerguntas;

	/** Lista de docentes das turmas em que o discente está matriculado. */
	private List<DocenteTurma> docenteTurmasDiscente;

	/** Lista de turmas avaliadas. */
	private List<Turma> turmasDiscenteComMaisDeUmDocente;

	/** Lista de turmas que o docente leciona. */
	private List<DocenteTurma> turmasDocente;

	/** Lista de turmas que o discente trancou. */
	private List<Turma> trancamentosDiscente;
	
	/** Lista de IDs de perguntas não respondidas. */
	private List<Integer> perguntasNaoRespondidas;

	/** Ano da Avaliação Institucional */
	private Integer ano;
	
	/** Período da Avaliação Institucional. */
	private Integer periodo;
	

	/** Lista as avaliações preenchidas anteriormente pelo discente.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Exibe os dados da avaliação anterior feita pelo discente. <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/lista_anterior_discente.jsp</li>
	 * </ul>
	 * 
	 * @return Link para o formulário com os dados da avaliação anterior.
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
		// Verificar se existem avaliações já salvas
		ano = calendarioAvaliacao.getAno();
		periodo = calendarioAvaliacao.getPeriodo();
		this.obj = getDAO(AvaliacaoInstitucionalDao.class).findByDiscente(discente, ano, periodo, calendarioAvaliacao.getFormulario());
		
		if (this.obj == null) { // Não existiam avaliações anteriores. 
			addMensagemErro("Não foi encontrada uma Avaliação Institucional preenchida para o formulário "
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
	
	/** Retorna a lista de docentes das turmas em que o discente está matriculado. 
	 * @return Lista de docentes das turmas em que o discente está matriculado. 
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
	
	/** Retorna a lista de IDs de perguntas não respondidas. 
	 * @return Lista de IDs de perguntas não respondidas. 
	 */
	public Integer[] getPerguntasNaoRespondidas() {
		if (perguntasNaoRespondidas == null) return null;
		return perguntasNaoRespondidas.toArray(new Integer[perguntasNaoRespondidas.size()]);
	}
	
	/** Indica se o discente está apto para avaliar.<br/>
	 * Método não invocado por JSP´s
	 * @return
	 * @throws DAOException
	 */
	public boolean discentePodeAvaliar() throws DAOException {
		boolean pode = !isEmpty(getDocenteTurmasDiscente()) || !isEmpty(getTurmasDiscenteComMaisDeUmDocente());
		return pode;
	}
	
	/** Retorna o ano da Avaliação Institucional 
	 * @return
	 * @throws DAOException
	 */
	public int getAno() throws DAOException {
		return ano;
	}
	
	/** Retorna o período da Avaliação Institucional 
	 * @return
	 * @throws DAOException
	 */
	public int getPeriodo() throws DAOException {
		return periodo;
	}

	/** Verifica se o acesso se dá por usuário discente.
	 * @return
	 * @throws NegocioException
	 */
	public String getVerificaAcessoDiscente() throws NegocioException {
		if (obj == null) {
			throw new NegocioException(UFRNUtils.getMensagem(MensagensAvaliacaoInstitucional.ACESSO_INDIRETO_AVALIACAO, "Por favor, utilize os links do Portal Discente.").getMensagem());
		}
		return null;
	}
	
	/** Verifica se o acesso se dá por usuário docente.
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
