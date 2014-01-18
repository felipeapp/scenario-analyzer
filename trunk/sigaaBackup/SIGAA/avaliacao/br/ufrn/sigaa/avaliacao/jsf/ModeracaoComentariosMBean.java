/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/05/2010
 * 
 */
package br.ufrn.sigaa.avaliacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.avaliacao.ComentarioAvaliacaoModeradoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.avaliacao.dominio.ComentarioAvaliacaoModerado;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesDocenteTurma;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesTrancamento;
import br.ufrn.sigaa.avaliacao.dominio.ParametroProcessamentoAvaliacaoInstitucional;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controller respons�vel pela modera��o de coment�rios na Avalia��o
 * Institucional.<br/>
 * A modera��o de coment�rios se faz necess�ria para que sejam divulgados sem
 * termos chulos, pejorativos, ou que possam ofender algu�m.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Component("moderadorObservacaoBean")
@Scope("request")
public class ModeracaoComentariosMBean extends SigaaAbstractController<ComentarioAvaliacaoModerado> {
	
	/** ID da unidade ao qual a busca por itens a ser moderados se restringe. */
	private int idDepartamento;
	/** Docente ao qual a busca por itens a ser moderados se restringe. */
	private Servidor docente;
	/** DocenteTurma selecionado para o qual os coment�rios destinados ser�o moderados. */ 
	private DocenteTurma docenteTurma;
	/** Turma selecionado para o qual os coment�rios de trancamento ser�o moderados. */ 
	private Turma turma;
	/** Ano Per�odo ao qual a busca por itens a ser moderados se restringe. */
	private int idParametroProcessamento;
	/** Nome do componente curricular o qual a busca por itens a ser moderados se restringe. */
	private String nomeComponente;
	
	/** Indica se a busca por docentes deve ser filtrada por nome do docente. */
	private boolean checkBuscaNome;
	/** Indica se a busca por docentes deve ser filtrada por nome do componente. */
	private boolean checkBuscaComponente;
	/** Indica se a busca por docentes deve ser filtrada por departamento. */
	private boolean checkBuscaDepartamento;
	/** Indica se a busca por docentes deve ser filtrada por ano/per�odo. */
	private boolean checkBuscaAnoPeriodo;
	/** Lista de docentes encontrados na busca por docentes. */
	private List<Map<String, Object>> listaDocentes;
	/** Cole��o de Observa��es de um DocenteTurma a serem moderadas. */
	private Collection<ObservacoesDocenteTurma> observacoes;
	/** Cole��o de Observa��es de Trancamento a serem moderadas. */
	private Collection<ObservacoesTrancamento> trancamentos;
	/** Indica se a opera��o ser� de modera��o de observa��es sobre trancamento. */
	private boolean moderarTrancamentos;
	/** Indica se a opera��o ser� de modera��o de observa��es realizadas por discentes. */
	private boolean discentes;
	
	/** Contrutor padr�o. */
	public ModeracaoComentariosMBean() {
		this.docente = new Servidor();
		this.docenteTurma = new DocenteTurma();
		this.turma = new Turma();
	}
	
	/** Inicia a modera��o de observa��es realizada por discentes sobre trancamentos.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarModeracaoTrancamentos() throws SegurancaException {
		this.moderarTrancamentos = true;
		this.discentes = true;
		checkRole(SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL);
		return formBusca();
	}
	
	/** Inicia a modera��o de observa��es realizada por discentes sobre docente de uma turma.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarModeracaoDocenteTurma() throws SegurancaException {
		this.moderarTrancamentos = false;
		this.discentes = true;
		checkRole(SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL);
		return formBusca();
	}
	
	/** Inicia a modera��o de observa��es realizadas por docentes sobre uma turma.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarModeracaoTurma() throws SegurancaException {
		this.moderarTrancamentos = false;
		this.discentes = false;
		checkRole(SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL);
		return formBusca();
	}
	
	/**
	 * Busca por docentes de acordo com os par�metros especificados no
	 * formul�rio. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/moderacao/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String buscar() throws DAOException {
		int idDepartamento = 0;
		String nomeDocente = null;
		String nomeComponente = null;
		int idFormulario = 0;
		int ano = 0;
		int periodo = 0;
		ComentarioAvaliacaoModeradoDao dao = getDAO(ComentarioAvaliacaoModeradoDao.class);
		if (isCheckBuscaDepartamento()) {
			idDepartamento = this.idDepartamento;
			validateRequiredId(idDepartamento, "Departamento", erros);
		}
		if (isCheckBuscaAnoPeriodo()) {
			validateRequiredId(idParametroProcessamento, "Ano-Per�odo", erros);
			if (idParametroProcessamento > 0) {
				ParametroProcessamentoAvaliacaoInstitucional parametro = dao.findByPrimaryKey(this.idParametroProcessamento, ParametroProcessamentoAvaliacaoInstitucional.class);
				ano = parametro.getAno();
				periodo = parametro.getPeriodo();
				idFormulario = parametro.getFormulario().getId();
			}
		}
		if (isCheckBuscaComponente()) {
			nomeComponente = this.nomeComponente;
			validateRequired(nomeComponente, "Componente Curricular", erros);
		}
		if (isCheckBuscaNome()) {
			nomeDocente = this.docente.getNome();
			validateRequired(nomeDocente, "Docente", erros);
		}
		if (nomeDocente == null && nomeComponente == null && idDepartamento == 0 && idFormulario == 0) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			listaDocentes = null;
		} 
		if (hasErrors()) return null;
		if (moderarTrancamentos)
			listaDocentes = dao.findTurmasByAnoPeriodoNomeComponente(nomeComponente, idDepartamento, ano, periodo, idFormulario);
		else
			listaDocentes = dao.findDocenteByAnoPeriodoNomeDepartamento(nomeDocente, idDepartamento, ano, periodo, idFormulario, discentes);
		if (listaDocentes == null || listaDocentes.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		return formBusca();
	}
	
	/** Copia a observa��o dada pelo discente para o campo de edi��o utilizado pelo usu�rio para moderar a observa��o.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/moderacao/observacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String copiarObservacaoDocenteTurma(){
		int id = getParameterInt("idObservacao", 0);  
		for (ObservacoesDocenteTurma obs : observacoes) {
			if (obs.getId() == id) {
				obs.setObservacoesModeradas(obs.getObservacoes() + obs.getObservacoesModeradas());
				break;
			}
		}
		return null;
	}
	
	/** Copia a observa��o dada pelo discente para o campo de edi��o utilizado pelo usu�rio para moderar a observa��o.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/moderacao/observacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String copiarTodasObservacaoDocenteTurma(){
		for (ObservacoesDocenteTurma obs : observacoes) {
			if (isEmpty(obs.getObservacoesModeradas())) {
				obs.setObservacoesModeradas(obs.getObservacoes());
			}
		}
		return null;
	}
	
	/** Copia a observa��o dada pelo discente para o campo de edi��o utilizado pelo usu�rio para moderar a observa��o.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/moderacao/trancamentos.jsp</li>
	 * </ul>
	 * @return
	 */
	public String copiarObservacaoTrancamento(){
		int id = getParameterInt("idObservacao", 0);  
		for (ObservacoesTrancamento obs : trancamentos) {
			if (obs.getId() == id) {
				obs.setObservacoesModeradas(obs.getObservacoes() + obs.getObservacoesModeradas());
				break;
			}
		}
		return null;
	}
	
	/** Copia todas observa��es dada pelo discente para o campo de edi��o utilizado pelo usu�rio para moderar a observa��o.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/moderacao/trancamentos.jsp</li>
	 * </ul>
	 * @return
	 */
	public String copiarTodasObservacaoTrancamento(){
		for (ObservacoesTrancamento obs : trancamentos) {
			if (isEmpty(obs.getObservacoesModeradas())) {
				obs.setObservacoesModeradas(obs.getObservacoes());
			}
		}
		return null;
	}
	
	/** Persiste as observa��es de trancamento moderadas pelo usu�rio sem finalizar a opera��o de modera��o.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/moderacao/trancamentos.jsp</li>
	 * </ul>
	 * @return
	 */
	public String gravarTrancamentos() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.GRAVAR_OBSERVACOES_DOCENTE_TURMA_MODERADA);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(trancamentos);
		mov.setCodMovimento(SigaaListaComando.GRAVAR_OBSERVACOES_DOCENTE_TURMA_MODERADA);
		execute(mov);
		addMensagemInformation("Observa��es gravadas com sucesso!");
		return null;
	}
	
	/** Persiste as observa��es de docente da turma moderadas pelo usu�rio sem finalizar a opera��o de modera��o.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/moderacao/observacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String gravarObservacoes() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.GRAVAR_OBSERVACOES_DOCENTE_TURMA_MODERADA);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(observacoes);
		mov.setCodMovimento(SigaaListaComando.GRAVAR_OBSERVACOES_DOCENTE_TURMA_MODERADA);
		execute(mov);
		addMensagemInformation("Observa��es gravadas com sucesso!");
		return null;
	}
	
	/** Persiste as observa��es de trancamentos moderadas pelo usu�rio e finaliza a opera��o de modera��o.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/moderacao/trancamentos.jsp</li>
	 * </ul>
	 * @return
	 */
	public String finalizarTrancamentos() throws ArqException, NegocioException {
		if (trancamentos == null)
			throw new ArqException(MensagensArquitetura.ACAO_JA_EXECUTADA);
		prepareMovimento(SigaaListaComando.FINALIZAR_OBSERVACOES_TRANCAMENTO_MODERADA);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(trancamentos);
		mov.setCodMovimento(SigaaListaComando.FINALIZAR_OBSERVACOES_TRANCAMENTO_MODERADA);
		execute(mov);
		trancamentos = null;
		addMensagemInformation("Observa��es finalizadas com sucesso!");
		return buscar();
	}
	
	/** Persiste as observa��es de docente da turma moderadas pelo usu�rio e finaliza a opera��o de modera��o.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/moderacao/observacoes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String finalizarObservacoes() throws ArqException, NegocioException {
		if (observacoes == null)
			throw new ArqException(MensagensArquitetura.ACAO_JA_EXECUTADA);
		prepareMovimento(SigaaListaComando.FINALIZAR_OBSERVACOES_DOCENTE_TURMA_MODERADA);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(observacoes);
		mov.setObjAuxiliar(this.discentes);
		mov.setCodMovimento(SigaaListaComando.FINALIZAR_OBSERVACOES_DOCENTE_TURMA_MODERADA);
		execute(mov);
		observacoes = null;
		addMensagemInformation("Observa��es finalizadas com sucesso!");
		return buscar();
	}
	
	/** Seleciona um docente de uma turma para moderar as observa��es dadas pelos discentes na Avalia��o Institucional.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/moderacao/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String selecionarDocenteTurma() throws DAOException{
		int id = getParameterInt("idDocenteTurma", 0);
		ComentarioAvaliacaoModeradoDao dao = getDAO(ComentarioAvaliacaoModeradoDao.class);
		docenteTurma = dao.refresh(new DocenteTurma(id));
		observacoes = dao.findObservacoesDocenteTurmaByDocenteTurma(id, discentes, false);
		return formModerarObservacoes();
	}
	
	/** Seleciona um turma para moderar as observa��es dadas ao trancamentos pelos discentes na Avalia��o Institucional.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/moderacao/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String selecionarTurma() throws DAOException{
		int id = getParameterInt("idTurma", 0);
		ComentarioAvaliacaoModeradoDao dao = getDAO(ComentarioAvaliacaoModeradoDao.class);
		turma = dao.refresh(new Turma(id));
		trancamentos = dao.findObservacoesTrancamentosByDocenteTurma(id);
		return formModerarTrancamentos();
	}
	
	/** Redireciona o usu�rio para o formul�rio de busca por turmas e docentes.<br>
	 * M�todo n�o chamado por JSP(s):
	 * 
	 * @return
	 */
	public String formBusca() {
		return forward("/avaliacao/moderacao/lista.jsp");
	}
	
	/** Redireciona o usu�rio para o formul�rio modera��o de observa��es sobre docente de uma turma.<br>
	 * M�todo n�o chamado por JSP(s):
	 * 
	 * @return
	 */
	public String formModerarObservacoes() {
		return forward("/avaliacao/moderacao/observacoes.jsp");
	}
	
	/** Redireciona o usu�rio para o formul�rio modera��o de observa��es sobre trancamentos.<br>
	 * M�todo n�o chamado por JSP(s):
	 * 
	 * @return
	 */
	public String formModerarTrancamentos() {
		return forward("/avaliacao/moderacao/trancamentos.jsp");
	}


	/** Indica se a busca por docentes deve ser filtrada por nome do docente.
	 * @return
	 */
	public boolean isCheckBuscaNome() {
		return checkBuscaNome;
	}

	/** Seta se a busca por docentes deve ser filtrada por nome do docente.
	 * @param checkBuscaNome
	 */
	public void setCheckBuscaNome(boolean checkBuscaNome) {
		this.checkBuscaNome = checkBuscaNome;
	}

	/** Indica se a busca por docentes deve ser filtrada por departamento.
	 * @return
	 */
	public boolean isCheckBuscaDepartamento() {
		return checkBuscaDepartamento;
	}

	/** Seta se a busca por docentes deve ser filtrada por departamento.
	 * @param checkBuscaDepartamento
	 */
	public void setCheckBuscaDepartamento(boolean checkBuscaDepartamento) {
		this.checkBuscaDepartamento = checkBuscaDepartamento;
	}

	/** Indica se a busca por docentes deve ser filtrada por ano/per�odo.
	 * @return
	 */
	public boolean isCheckBuscaAnoPeriodo() {
		return checkBuscaAnoPeriodo;
	}

	/** Seta se a busca por docentes deve ser filtrada por ano/per�odo.
	 * @param checkBuscaAnoPeriodo
	 */
	public void setCheckBuscaAnoPeriodo(boolean checkBuscaAnoPeriodo) {
		this.checkBuscaAnoPeriodo = checkBuscaAnoPeriodo;
	}

	/** Retorna o docente ao qual a busca por itens a ser moderados se restringe.
	 * @return
	 */
	public Servidor getDocente() {
		return docente;
	}

	/** Seta o docente ao qual a busca por itens a ser moderados se restringe.
	 * @param docente
	 */
	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	/** Retorna o ID da unidade ao qual a busca por itens a ser moderados se restringe.
	 * @return
	 */
	public int getIdDepartamento() {
		return idDepartamento;
	}

	/** Seta o ID da unidade ao qual a busca por itens a ser moderados se restringe.
	 * @param idDepartamento
	 */
	public void setIdDepartamento(int idDepartamento) {
		this.idDepartamento = idDepartamento;
	}

	/** Retorna a lista de docentes encontrados na busca por docentes.
	 * @return
	 */
	public List<Map<String, Object>> getListaDocentes() {
		return listaDocentes;
	}

	/** Seta a lista de docentes encontrados na busca por docentes.
	 * @param listaDocentes
	 */
	public void setListaDocentes(List<Map<String, Object>> listaDocentes) {
		this.listaDocentes = listaDocentes;
	}

	/** Retorna a cole��o de Observa��es de um DocenteTurma a serem moderadas.
	 * @return
	 */
	public Collection<ObservacoesDocenteTurma> getObservacoes() {
		return observacoes;
	}

	/** Seta a cole��o de Observa��es de um DocenteTurma a serem moderadas.
	 * @param observacoes
	 */
	public void setObservacoes(Collection<ObservacoesDocenteTurma> observacoes) {
		this.observacoes = observacoes;
	}

	/** Retorna a cole��o de Observa��es de Trancamento a serem moderadas.
	 * @return
	 */
	public Collection<ObservacoesTrancamento> getTrancamentos() {
		return trancamentos;
	}

	/** Seta a cole��o de Observa��es de Trancamento a serem moderadas.
	 * @param trancamentos
	 */
	public void setTrancamentos(Collection<ObservacoesTrancamento> trancamentos) {
		this.trancamentos = trancamentos;
	}

	/** Retorna o docenteTurma selecionado para o qual os coment�rios destinados ser�o moderados.
	 * @return
	 */
	public DocenteTurma getDocenteTurma() {
		return docenteTurma;
	}

	/** Seta o docenteTurma selecionado para o qual os coment�rios destinados ser�o moderados.
	 * @param docenteTurma
	 */
	public void setDocenteTurma(DocenteTurma docenteTurma) {
		this.docenteTurma = docenteTurma;
	}

	/** Retorna a turma selecionado para o qual os coment�rios de trancamento ser�o moderados.
	 * @return
	 */
	public Turma getTurma() {
		return turma;
	}

	/** Seta a turma selecionado para o qual os coment�rios de trancamento ser�o moderados.
	 * @param turma
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/** Indica se a opera��o ser� de modera��o de observa��es sobre trancamento.
	 * @return
	 */
	public boolean isModerarTrancamentos() {
		return moderarTrancamentos;
	}

	/** Seta se a opera��o ser� de modera��o de observa��es sobre trancamento.
	 * @param moderarTrancamentos
	 */
	public void setModerarTrancamentos(boolean moderarTrancamentos) {
		this.moderarTrancamentos = moderarTrancamentos;
	}

	/** Indica se a busca por docentes deve ser filtrada por nome do componente.
	 * @return
	 */
	public boolean isCheckBuscaComponente() {
		return checkBuscaComponente;
	}

	/** Seta se a busca por docentes deve ser filtrada por nome do componente.
	 * @param checkBuscaComponente
	 */
	public void setCheckBuscaComponente(boolean checkBuscaComponente) {
		this.checkBuscaComponente = checkBuscaComponente;
	}

	/** Retorna o nome do componente curricular o qual a busca por itens a ser moderados se restringe.
	 * @return
	 */
	public String getNomeComponente() {
		return nomeComponente;
	}

	/** Seta o nome do componente curricular o qual a busca por itens a ser moderados se restringe.
	 * @param nomeComponente
	 */
	public void setNomeComponente(String nomeComponente) {
		this.nomeComponente = nomeComponente;
	}

	public boolean isDiscentes() {
		return discentes;
	}

	public void setDiscentes(boolean discentes) {
		this.discentes = discentes;
	}

	public int getIdParametroProcessamento() {
		return idParametroProcessamento;
	}

	public void setIdParametroProcessamento(int idParametroProcessamento) {
		this.idParametroProcessamento = idParametroProcessamento;
	}
	
	
}
