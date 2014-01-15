/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 06/11/2007 
 *
 */
package br.ufrn.sigaa.processamento.jsf;

import java.util.Date;

import org.apache.velocity.texen.util.FileUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dominio.MovimentoAcademico;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.processamento.batch.ListaBlocosProcessar;
import br.ufrn.sigaa.processamento.batch.ListaCoRequisitosProcessar;
import br.ufrn.sigaa.processamento.batch.ListaProcessamentoBatch;
import br.ufrn.sigaa.processamento.batch.ListaTurmasProcessar;
import br.ufrn.sigaa.processamento.batch.PosProcessamentoThread;
import br.ufrn.sigaa.processamento.batch.ProcessamentoThread;
import br.ufrn.sigaa.processamento.batch.ResultadoThread;
import br.ufrn.sigaa.processamento.dominio.ExecucaoProcessamentoMatricula;
import br.ufrn.sigaa.processamento.dominio.ModoProcessamentoMatricula;
import br.ufrn.sigaa.processamento.dominio.ResultadoProcessamento;

/**
 * Managed bean para realizar o processamento da matrícula
 * das turmas de graduação.
 *
 * @author David Pereira
 *
 */
@Component("processamentoMatricula") @Scope("session")
public class ProcessamentoMatriculaMBean extends SigaaAbstractController<Object> {

	public static final int PROCESSAMENTO = 1;
	
	public static final int POS_PROCESSAMENTO_BLOCOS = 2;
	
	public static final int POS_PROCESSAMENTO_COREQUISITOS = 3;
	
	public static final int POS_PROCESSAMENTO_ENSINO_INDIVIDUAL= 4;
	
	public static final int POS_PROCESSAMENTO_ATIVAR_ALUNOS_CADASTRADOS = 5;
	
	/** Indica se o processamento foi iniciado ou não. */
	private boolean processamentoIniciado;
	
	/** Ano do processamento */
	private int ano;
	
	/** Período do processamento */
	private int periodo;
	
	/** Senha para realizar o processamento */
	private String senhaProcessamento;
	
	/** Modo do processamento de matrícula (se é graduação, férias, etc) */
	private ModoProcessamentoMatricula modo;
	
	/** Se é matrícula ou rematrícula */
	private boolean rematricula;
	
	/** Número de matrículas a serem processadas */
	private int count;
	
	/** Tipo de pré-processamento, se é processamento ou pós-processamento */
	private int tipo = PROCESSAMENTO;
	
	/** Número de threads usadas no processamento */
	private int numThreads = 2;
	
	private ListaProcessamentoBatch<?> lista;

	/**
	 * Direciona o usuário para o formulário de início do processamento
	 * de matrículas.
	 * JSP: /administracao/menu_administracao.jsp
	 * @return
	 * @throws Exception
	 */
	public String iniciar() throws Exception {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
		
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = calendario.getAno();
		periodo = calendario.getPeriodo();
		
		lista = null;
		
		return forward("/processamento/processamento/entrada.jsp");
	}

	/**
	 * Realiza o processamento ou pós-processamento de matrículas
	 * de acordo com o ano, período e modo selecionados.
	 * 
	 * JSP: /processamento/processamento/entrada.jsp
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String processar() throws Exception {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
		
		boolean autenticado = true;//dao.autenticacaoProcessamento(senhaProcessamento);
		
		if (autenticado) {
			
			registrarProcessamentoMatricula();
			
			if (tipo == PROCESSAMENTO) {
				processamentoIniciado = true;
				
				//if (modo.equals(ModoProcessamentoMatricula.GRADUACAO)) {
					lista = new ListaTurmasProcessar();
					lista.carregar(ano, periodo, rematricula, modo.getClasseDao().newInstance());
					count = lista.total;
					
					for ( int a = 0; a < 1; a++) {
						ProcessamentoThread pThread = new ProcessamentoThread(modo, rematricula, (ListaProcessamentoBatch<Turma>) lista);
						pThread.start();
					}
				//} 
				
			} else if (tipo == POS_PROCESSAMENTO_BLOCOS){
				// TRATAR BLOCOS!!!
				
				ListaBlocosProcessar.carregarDiscentes(ano, periodo, 'G', rematricula);
				for (int i = 0; i < 3; i++) {
					PosProcessamentoThread pThread = new PosProcessamentoThread(tipo, ano, periodo, rematricula);
					pThread.start();
				}
				
				
			} else if (tipo == POS_PROCESSAMENTO_ATIVAR_ALUNOS_CADASTRADOS) {
				prepareMovimento(SigaaListaComando.POS_PROCESSAR_MATRICULA);
				
				MovimentoAcademico mov = new MovimentoAcademico();
				mov.setCodMovimento(SigaaListaComando.POS_PROCESSAR_MATRICULA);
				mov.setAcao(POS_PROCESSAMENTO_ATIVAR_ALUNOS_CADASTRADOS);
				mov.setAno(ano);
				mov.setPeriodo(periodo);
				
				execute(mov);
			}
			else {
				// TRATAR CO-REQUISITOS
				ListaCoRequisitosProcessar.carregarDiscentes(ano, periodo, 'G', rematricula);
				for (int i = 0; i < 3; i++) {
					PosProcessamentoThread pThread = new PosProcessamentoThread(tipo, ano, periodo, rematricula);
					pThread.start();
				}
			}
			addMensagemInformation("Processamento em segundo plano foi iniciado com sucesso.");
			return null;
		} else {
			addMensagemErro("A senha digitada é inválida!");
			return null;
		}
	}
	
	private void registrarProcessamentoMatricula() throws ArqException, NegocioException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		ExecucaoProcessamentoMatricula exec = new ExecucaoProcessamentoMatricula();
		exec.setAno(ano);
		exec.setPeriodo(periodo);
		exec.setData(new Date());
		exec.setRematricula(rematricula);
		exec.setModo(modo.name());
		exec.setTipo(tipo);
		exec.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(exec);
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		execute(mov);
	}

	public String getCriarArquivosResultado() throws DAOException {
		FileUtil.mkdir(ResultadoProcessamento.getCaminhoCompleto());
		
		for ( int a = 0; a < 3; a++) {
			String servletContextPath = getCurrentSession().getServletContext().getRealPath("");
			ResultadoThread rThread = new ResultadoThread(rematricula, servletContextPath);
			rThread.start();
		}
		
		return null;
	}
	
	public void setSenhaProcessamento(String senhaProcessamento) {
		this.senhaProcessamento = senhaProcessamento;
	}

	public String getSenhaProcessamento() {
		return senhaProcessamento;
	}

	public boolean isProcessamentoIniciado() {
		return processamentoIniciado;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public boolean isRematricula() {
		return rematricula;
	}

	public void setRematricula(boolean rematricula) {
		this.rematricula = rematricula;
	}

	public ModoProcessamentoMatricula getModo() {
		return modo;
	}

	public void setModo(ModoProcessamentoMatricula modo) {
		this.modo = modo;
	}

	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}
	
}
