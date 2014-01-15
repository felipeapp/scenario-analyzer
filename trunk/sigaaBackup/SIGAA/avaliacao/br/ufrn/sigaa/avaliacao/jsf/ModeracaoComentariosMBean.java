/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/05/2010
 * 
 */
package br.ufrn.sigaa.avaliacao.jsf;

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
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

@Component("moderadorObservacaoBean")
@Scope("request")
public class ModeracaoComentariosMBean extends SigaaAbstractController<ComentarioAvaliacaoModerado> {
	
	/** ID da unidade ao qual a busca por itens a ser moderados se restringe. */
	private int idDepartamento;
	/** Docente ao qual a busca por itens a ser moderados se restringe. */
	private Servidor docente;
	/** DocenteTurma selecionado para o qual os comentários destinados serão moderados. */ 
	private DocenteTurma docenteTurma;
	/** Turma selecionado para o qual os comentários de trancamento serão moderados. */ 
	private Turma turma;
	/** Ano Período ao qual a busca por itens a ser moderados se restringe. */
	private int anoPeriodo;
	/** Nome do componente curricular o qual a busca por itens a ser moderados se restringe. */
	private String nomeComponente;
	
	/** Indica se a busca por docentes deve ser filtrada por nome do docente. */
	private boolean checkBuscaNome;
	/** Indica se a busca por docentes deve ser filtrada por nome do componente. */
	private boolean checkBuscaComponente;
	/** Indica se a busca por docentes deve ser filtrada por departamento. */
	private boolean checkBuscaDepartamento;
	/** Indica se a busca por docentes deve ser filtrada por ano/período. */
	private boolean checkBuscaAnoPeriodo;
	/** Lista de docentes encontrados na busca por docentes. */
	private List<Map<String, Object>> listaDocentes;
	/** Coleção de Observações de um DocenteTurma a serem moderadas. */
	private Collection<ObservacoesDocenteTurma> observacoes;
	/** Coleção de Observações de Trancamento a serem moderadas. */
	private Collection<ObservacoesTrancamento> trancamentos;
	/** Indica se a operação será de moderação de observações sobre trancamento. */
	private boolean moderarTrancamentos;
	/** Indica se a operação será de moderação de observações realizadas por discentes. */
	private boolean discentes;
	
	/** Contrutor padrão. */
	public ModeracaoComentariosMBean() {
		this.docente = new Servidor();
		this.docenteTurma = new DocenteTurma();
		this.turma = new Turma();
	}
	
	/** Inicia a moderação de observações realizada por discentes sobre trancamentos.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Inicia a moderação de observações realizada por discentes sobre docente de uma turma.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Inicia a moderação de observações realizadas por docentes sobre uma turma.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Busca por docentes de acordo com os parâmetros especificados no
	 * formulário. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		int ano = anoPeriodo / 10, periodo = anoPeriodo % 10;
		if (isCheckBuscaDepartamento()) {
			idDepartamento = this.idDepartamento;
		} else {
			idDepartamento = 0;
		}
		if (isCheckBuscaComponente()) {
			nomeComponente = this.nomeComponente;
		} else {
			nomeComponente = null;
		}
		if (isCheckBuscaNome()) {
			nomeDocente = this.docente.getNome();
		} else {
			nomeDocente = null;
		}
		if (isCheckBuscaDepartamento()) {
			idDepartamento = this.idDepartamento;
		} else {
			idDepartamento = 0;
		}
		if (isCheckBuscaAnoPeriodo()) {
			ano = this.anoPeriodo / 10;
			periodo = this.anoPeriodo % 10;
		} else {
			ano = 0;
			periodo = 0;
		}
		if (nomeDocente == null && nomeComponente == null && idDepartamento == 0 && ano == 0 && periodo == 0) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			listaDocentes = null;
		} else {
			ComentarioAvaliacaoModeradoDao dao = getDAO(ComentarioAvaliacaoModeradoDao.class);
			if (moderarTrancamentos)
				listaDocentes = dao.findTurmasByAnoPeriodoNomeComponente(nomeComponente, idDepartamento, ano, periodo);
			else
				listaDocentes = dao.findDocenteByAnoPeriodoNomeDepartamento(nomeDocente, idDepartamento, ano, periodo, discentes);
			if (listaDocentes == null || listaDocentes.isEmpty()) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			}
		}
		return formBusca();
	}
	
	/** Copia a observação dada pelo discente para o campo de edição utilizado pelo usuário para moderar a observação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Copia a observação dada pelo discente para o campo de edição utilizado pelo usuário para moderar a observação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Persiste as observações de trancamento moderadas pelo usuário sem finalizar a operação de moderação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		addMensagemInformation("Observações gravadas com sucesso!");
		return null;
	}
	
	/** Persiste as observações de docente da turma moderadas pelo usuário sem finalizar a operação de moderação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		addMensagemInformation("Observações gravadas com sucesso!");
		return null;
	}
	
	/** Persiste as observações de trancamentos moderadas pelo usuário e finaliza a operação de moderação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		addMensagemInformation("Observações finalizadas com sucesso!");
		return buscar();
	}
	
	/** Persiste as observações de docente da turma moderadas pelo usuário e finaliza a operação de moderação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		addMensagemInformation("Observações finalizadas com sucesso!");
		return buscar();
	}
	
	/** Seleciona um docente de uma turma para moderar as observações dadas pelos discentes na Avaliação Institucional.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		observacoes = dao.findObservacoesDocenteTurmaByDocenteTurma(id, discentes);
		return formModerarObservacoes();
	}
	
	/** Seleciona um turma para moderar as observações dadas ao trancamentos pelos discentes na Avaliação Institucional.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Redireciona o usuário para o formulário de busca por turmas e docentes.<br>
	 * Método não chamado por JSP(s):
	 * 
	 * @return
	 */
	public String formBusca() {
		return forward("/avaliacao/moderacao/lista.jsp");
	}
	
	/** Redireciona o usuário para o formulário moderação de observações sobre docente de uma turma.<br>
	 * Método não chamado por JSP(s):
	 * 
	 * @return
	 */
	public String formModerarObservacoes() {
		return forward("/avaliacao/moderacao/observacoes.jsp");
	}
	
	/** Redireciona o usuário para o formulário moderação de observações sobre trancamentos.<br>
	 * Método não chamado por JSP(s):
	 * 
	 * @return
	 */
	public String formModerarTrancamentos() {
		return forward("/avaliacao/moderacao/trancamentos.jsp");
	}

	/** Retorna o Ano Período ao qual a busca por itens a ser moderados se restringe.
	 * @return
	 */
	public int getAnoPeriodo() {
		return anoPeriodo;
	}

	/** Seta o Ano Período ao qual a busca por itens a ser moderados se restringe.
	 * @param anoPeriodo
	 */
	public void setAnoPeriodo(int anoPeriodo) {
		this.anoPeriodo = anoPeriodo;
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

	/** Indica se a busca por docentes deve ser filtrada por ano/período.
	 * @return
	 */
	public boolean isCheckBuscaAnoPeriodo() {
		return checkBuscaAnoPeriodo;
	}

	/** Seta se a busca por docentes deve ser filtrada por ano/período.
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

	/** Retorna a coleção de Observações de um DocenteTurma a serem moderadas.
	 * @return
	 */
	public Collection<ObservacoesDocenteTurma> getObservacoes() {
		return observacoes;
	}

	/** Seta a coleção de Observações de um DocenteTurma a serem moderadas.
	 * @param observacoes
	 */
	public void setObservacoes(Collection<ObservacoesDocenteTurma> observacoes) {
		this.observacoes = observacoes;
	}

	/** Retorna a coleção de Observações de Trancamento a serem moderadas.
	 * @return
	 */
	public Collection<ObservacoesTrancamento> getTrancamentos() {
		return trancamentos;
	}

	/** Seta a coleção de Observações de Trancamento a serem moderadas.
	 * @param trancamentos
	 */
	public void setTrancamentos(Collection<ObservacoesTrancamento> trancamentos) {
		this.trancamentos = trancamentos;
	}

	/** Retorna o docenteTurma selecionado para o qual os comentários destinados serão moderados.
	 * @return
	 */
	public DocenteTurma getDocenteTurma() {
		return docenteTurma;
	}

	/** Seta o docenteTurma selecionado para o qual os comentários destinados serão moderados.
	 * @param docenteTurma
	 */
	public void setDocenteTurma(DocenteTurma docenteTurma) {
		this.docenteTurma = docenteTurma;
	}

	/** Retorna a turma selecionado para o qual os comentários de trancamento serão moderados.
	 * @return
	 */
	public Turma getTurma() {
		return turma;
	}

	/** Seta a turma selecionado para o qual os comentários de trancamento serão moderados.
	 * @param turma
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/** Indica se a operação será de moderação de observações sobre trancamento.
	 * @return
	 */
	public boolean isModerarTrancamentos() {
		return moderarTrancamentos;
	}

	/** Seta se a operação será de moderação de observações sobre trancamento.
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
	
	
}
