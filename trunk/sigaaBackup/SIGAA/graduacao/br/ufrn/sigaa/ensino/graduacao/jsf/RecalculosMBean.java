/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 17/11/2008 
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.FiltroDiscentesRecalculoGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.FiltroDiscentesRecalculoStricto;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.ListaDiscentesCalcular;
import br.ufrn.sigaa.ensino.graduacao.negocio.ListaEstruturasCalcular;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoRecalculoEstruturaCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.RecalculoDiscenteThread;
import br.ufrn.sigaa.ensino.graduacao.negocio.RecalculoEstruturaCurricularThread;

/**
 * MBean responsável por realizar chamadas ao processador que refaz os cálculos de um currículo
 * 
 * @author David Pereira
 */
@Component @Scope("session")
public class RecalculosMBean extends SigaaAbstractController<Object> {

	/** Se irá zerar as integralizações dos discentes antes de iniciar os cálculos */
	private boolean zerarIntegralizacoes;

	private int status;
	
	private String senha;
	
	private int numThreads;
	
	private boolean funcionando;
	
	/**
	 * Campo opcional para definir a condição de restrição dos discentes a serem calculados. Caso não informado, calculará os discentes pendentes de cálculo.
	 */
	private String sqlRestricao;
	
	private Curso curso;
	private MatrizCurricular matriz;
	private DataModel modelCurriculosEncontrados;
	private DataModel modelCurriculosAdicionados;	
	private List<Curriculo> curriculosEscolhidos;
	private List<Curriculo> curriculosEncontrados;
	private boolean ckeckRecalcularDiscente;
	
	private RecalculoDiscenteThread threads[];
	private ListaDiscentesCalcular reCalculo = new ListaDiscentesCalcular();
	
	private char nivelEnsino = NivelEnsino.GRADUACAO;
	
	public String getSqlRestricao() {
		return sqlRestricao;
	}

	public void setSqlRestricao(String sqlRestricao) {
		this.sqlRestricao = sqlRestricao;
	}

	/**
	 * Inicia o caso de uso para re-cálculo de um currículo
	 * JSP: /sigaa.war/graduacao/menus/administracao.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException {
		curriculosEscolhidos = new ArrayList<Curriculo>();
		curso = new Curso();
		matriz = new MatrizCurricular();
		
		return forward("/graduacao/recalcular_curriculo/form.jsp");
	}
	
	/**
	 * Monta combobox com as matrizes do curso selecionado
	 * JSP: /sigaa.war/graduacao/recalcular_curriculo/form.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllMatrizCombo() throws DAOException {
		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
		return toSelectItems(dao.findByCurso(curso.getId(), Boolean.TRUE), "id", "descricaoMin");
	}
	
	/**
	 * Busca os currículos por curso/matriz
	 * JSP: /sigaa.war/graduacao/recalcular_curriculo/form.jsp
	 * 
	 * @param evt
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 */
	public void buscar(ActionEvent evt) throws LimiteResultadosException, DAOException {

		if (isEmpty(curso.getId()))
			addMensagemErroAjax("Curso é obrigatório.");
		if (isEmpty(matriz.getId()))
			addMensagemErroAjax("Matriz é obrigatório.");
		
		if (hasErrors())
			return ;
		
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		curriculosEncontrados = CollectionUtils.toList(dao.findCompleto(curso.getId(), matriz.getId(), null, null));
		
		for (Curriculo curriculo : curriculosEncontrados) {
			curriculo.setQtdAlunos(dao.findQuantidadeDiscentesByCurriculo(curriculo.getId()));
		}
		
		modelCurriculosEncontrados = new ListDataModel(curriculosEncontrados);		
	}
	
	/**
	 * Adicionar currículo para ser calculado
	 * JSP: /sigaa.war/graduacao/recalcular_curriculo/form.jsp
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void adicionarCurriculo(ActionEvent evt) throws DAOException {
		Curriculo c = (Curriculo) modelCurriculosEncontrados.getRowData();
		
		if (curriculosEscolhidos.contains(c)) { 
			addMensagemErroAjax("Currículo já adicionado.");
			return ;
		}
		
		curriculosEscolhidos.add(c);
		modelCurriculosAdicionados = new ListDataModel(curriculosEscolhidos);
		
		curriculosEncontrados.remove(c);
		modelCurriculosEncontrados = new ListDataModel(curriculosEncontrados);
	}
	
	/**
	 * Remove currículo da lista que vai ser calculada
	 * JSP: /sigaa.war/graduacao/recalcular_curriculo/form.jsp
	 * 
	 * @param evt
	 * @throws DAOException 
	 */
	public void removerCurriculo(ActionEvent evt) throws DAOException {
		Curriculo c = (Curriculo) modelCurriculosAdicionados.getRowData();
		
		Curriculo cursoSelecionado = getGenericDAO().findByPrimaryKey(c.getId(), Curriculo.class);
		
		if (cursoSelecionado.getCurso().equals(curso)) {
			curriculosEncontrados.add(c);
			modelCurriculosEncontrados = new ListDataModel(curriculosEncontrados);
		}
		curriculosEscolhidos.remove(c);
		modelCurriculosAdicionados = new ListDataModel(curriculosEscolhidos);
	}	
	
	/**
	 * Cria threads para processar o re-cálculo dos discentes de graduação.
	 * JSP: /sigaa.war/administracao/recalculo_discentes.jsp
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws ArqException 
	 */
	public String recalcularDiscentes() throws InterruptedException, ArqException {
		boolean autorizado = UserAutenticacao.autenticaUsuario(getCurrentRequest(), getUsuarioLogado().getLogin(), senha);
		try {
			if (autorizado) {
				threads = new RecalculoDiscenteThread[numThreads];
				
				if (NivelEnsino.isGraduacao(nivelEnsino))
					reCalculo.carregarDiscentes(new FiltroDiscentesRecalculoGraduacao(sqlRestricao));
				else
					reCalculo.carregarDiscentes(new FiltroDiscentesRecalculoStricto(sqlRestricao));
					
				funcionando = true;
				for (int a = 0; a < threads.length; a++) {
					threads[a] = new RecalculoDiscenteThread(getUsuarioLogado(), zerarIntegralizacoes, reCalculo);
					threads[a].start();
				}
				
			} else {
				addMensagemErro("Senha inválida!");
			}
		} catch (BadSqlGrammarException e) {
			addMensagemErro("Erro na restrição da consulta. Por favor, verifique o SQL informado e tente novamente.");
		}
		
		return null;
	}
	
	public String pararCalculoDiscentes() {
		for (RecalculoDiscenteThread thread : threads) {
			thread.finalizar();
		}
		
		funcionando = false;
		addMensagemInformation("Cálculos parados com sucesso!");
		return null;
	}
	
	/**
	 * Invoca o processador para realizar os cálculos
	 * JSP: /sigaa.war/graduacao/recalcular_curriculo/form.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String recalcularCurriculosDAE() throws ArqException {
		
		if (isEmpty(curriculosEscolhidos)) {
			addMensagemErro("Selecione no minímo um currículo.");
			return null;
		}
		
		MovimentoRecalculoEstruturaCurricular mov = new MovimentoRecalculoEstruturaCurricular(); 

		mov.setCodMovimento(SigaaListaComando.RECALCULAR_ESTRUTURA_CURRICULAR);
		mov.setRecalcularDiscentes(ckeckRecalcularDiscente);

		
		try {
			for (Curriculo curriculo : curriculosEscolhidos) {
				prepareMovimento(SigaaListaComando.RECALCULAR_ESTRUTURA_CURRICULAR);
				
				mov.setId(curriculo.getId());
				execute(mov);
				addMensagemInformation("O currículo " + curriculo.getDescricaoCursoCurriculo() + " foi recalculado com sucesso.");
			}
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		}
		
		return cancelar();		
	}
	
	/**
	 * Cria threads para recalcular todos os currículos
	 * JSP: /sigaa.war/administracao/recalculo_curriculos.jsp
	 * @return
	 * @throws InterruptedException
	 * @throws ArqException 
	 */
	public String recalcularCurriculos() throws InterruptedException, ArqException {

		boolean autorizado = UserAutenticacao.autenticaUsuario(getCurrentRequest(), getUsuarioLogado().getLogin(), senha);
		
		if (autorizado) {
			funcionando = true;
			RecalculoEstruturaCurricularThread threads[] = new RecalculoEstruturaCurricularThread[numThreads];
			
			// Cadastrar matrículas em espera
			ListaEstruturasCalcular.carregarEstruturas();
			for (int a = 0; a < threads.length; a++) {
				threads[a] = new RecalculoEstruturaCurricularThread(getUsuarioLogado());
				threads[a].start();
			}
			
		} else {
			addMensagemErro("Senha inválida!");
		}
		
		return null;
	}
	
	/**
	 * Limpa a data da última atualização dos totais dos alunos
	 * JSP: /sigaa.war/administracao/resetar_calculo_discentes.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String resetarDiscentes() throws ArqException {
		
		boolean autorizado = UserAutenticacao.autenticaUsuario(getCurrentRequest(), getUsuarioLogado().getLogin(), senha);
		JdbcTemplate jt = new JdbcTemplate(Database.getInstance().getSigaaDs());
		int total = 0;
		
		if (autorizado) {
			if (status == 0) {
				total = jt.update("update graduacao.discente_graduacao set ultima_atualizacao_totais = null where id_discente_graduacao in (select id_discente from discente where status in " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos()) + ")");
			} else {
				total = jt.update("update graduacao.discente_graduacao set ultima_atualizacao_totais = null where id_discente_graduacao in (select id_discente from discente where status = " + status + ")");
			}
			addMensagemInformation("Última atualização de totais resetada com sucesso! " + total + " discentes afetados.");
		} else {
			addMensagemErro("Senha inválida!");
		}
		
		return null;
	}

	public int getTotalCurriculo() {
		return ListaEstruturasCalcular.totalEstruturas;
	}

	public int getAtualCurriculo() {
		return ListaEstruturasCalcular.totalProcessadas;
	}
	
	public int getTotalDiscente() {
		return reCalculo.getTotalDiscentes();
	}

	public int getAtualDiscente() {
		
		if (funcionando) {
			if (reCalculo.getTotalProcessados() < reCalculo.getTotalDiscentes())
				return reCalculo.getTotalProcessados();
			else {
				funcionando = false;
				return reCalculo.getTotalDiscentes() + 1;
			} 
		} else {
			return 0;
		}
		
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	public boolean isFuncionando() {
		return funcionando;
	}

	public void setFuncionando(boolean funcionando) {
		this.funcionando = funcionando;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public MatrizCurricular getMatriz() {
		return matriz;
	}

	public void setMatriz(MatrizCurricular matriz) {
		this.matriz = matriz;
	}

	public DataModel getModelCurriculosEncontrados() {
		return modelCurriculosEncontrados;
	}

	public void setModelCurriculosEncontrados(DataModel modelCurriculosEncontrados) {
		this.modelCurriculosEncontrados = modelCurriculosEncontrados;
	}

	public List<Curriculo> getCurriculosEscolhidos() {
		return curriculosEscolhidos;
	}

	public void setCurriculosEscolhidos(List<Curriculo> curriculosEscolhidos) {
		this.curriculosEscolhidos = curriculosEscolhidos;
	}

	public DataModel getModelCurriculosAdicionados() {
		return modelCurriculosAdicionados;
	}

	public void setModelCurriculosAdicionados(DataModel modelCurriculosAdicionados) {
		this.modelCurriculosAdicionados = modelCurriculosAdicionados;
	}

	public boolean isCkeckRecalcularDiscente() {
		return ckeckRecalcularDiscente;
	}

	public void setCkeckRecalcularDiscente(boolean ckeckRecalcularDiscente) {
		this.ckeckRecalcularDiscente = ckeckRecalcularDiscente;
	}

	public List<Curriculo> getCurriculosEncontrados() {
		return curriculosEncontrados;
	}

	public void setCurriculosEncontrados(List<Curriculo> curriculosEncontrados) {
		this.curriculosEncontrados = curriculosEncontrados;
	}

	public boolean isZerarIntegralizacoes() {
		return zerarIntegralizacoes;
	}

	public void setZerarIntegralizacoes(boolean zerarIntegralizacoes) {
		this.zerarIntegralizacoes = zerarIntegralizacoes;
	}

	public char getNivelEnsino() {
		return nivelEnsino;
	}

	public void setNivelEnsino(char nivelEnsino) {
		this.nivelEnsino = nivelEnsino;
	}

}
