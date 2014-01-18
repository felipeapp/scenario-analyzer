/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 06/11/2007 
 *
 */
package br.ufrn.sigaa.processamento.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.graduacao.ProcessamentoMatriculaDAO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ConstantesTipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.processamento.batch.CalcularPossiveisFormandosThread;
import br.ufrn.sigaa.processamento.batch.GerarMatriculasEsperaThread;
import br.ufrn.sigaa.processamento.batch.ListaAlunosPreProcessamento;
import br.ufrn.sigaa.processamento.batch.ListaProcessamentoBatch;
import br.ufrn.sigaa.processamento.batch.ListaSolicitacoesPreProcessamento;
import br.ufrn.sigaa.processamento.batch.ProcessamentoBatchThread;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaGraduacaoDao;
import br.ufrn.sigaa.processamento.dominio.ExecucaoProcessamentoMatricula;
import br.ufrn.sigaa.processamento.dominio.ModoProcessamentoMatricula;

/**
 * Managed bean para realizar o pré-processamento da matrícula
 * dos alunos de graduação.
 *
 * @author David Pereira
 *
 */
@Component("preProcessamentoMatricula") @Scope("session")
public class PreProcessamentoMatriculaMBean extends SigaaAbstractController<Object> {

	/** Tipo de pré-processamento a ser realizado */
	public static final int MATRICULA_ESPERA = 1;
	/** Tipo de pré-processamento a ser realizado */
	public static final int POSSIVEIS_FORMANDOS = 2;
	/** Tipo de pré-processamento a ser realizado */
	public static final int CANCELAR_VINCULOS_ANTERIORES = 3;
	/** Tipo de pré-processamento a ser realizado */
	public static final int RELATORIO_CANCELAMENTO_VINCULOS_ANTERIORES = 4;
	
	/** Se o pré-processamento será referente a GRADUAÇÃO, GRADUAÇÃO FÉRIAS, MÚSICA ou EAD */
	private ModoProcessamentoMatricula modo; 
	
	/** Tipo de Pré-processamento. */
	private int tipo = MATRICULA_ESPERA; // gerar matrículas em espera ou calcular possíveis formandos

	/** Ano para o qual o pré-processamento está sendo rodado. */
	private int ano;
	
	/** Período para o qual o pré-processamento está sendo rodado. */
	private int periodo;
	
	/** Número de threads que rodarão a rotina de pré-processamento */
	private int numThreads = 5;
	
	/** Se o processamento tá sendo executado pra rematrícula */
	private boolean rematricula;
	
	/** Senha do processamento de matrícula */
	private String senhaProcessamento;
	
	/** E-mails dos alunos cancelados */
	private String emailsCancelamento;

	/** Lista de informações que serão processadas em lote */
	private ListaProcessamentoBatch<?> lista;
	
	/** Não está sendo utilizada */
	private boolean enabled;
	
	/**
	 * Método utilizado pra iniciar o bean e redirecionar para tela de formulário
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/administracao/Menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception
	 */
	public String iniciar() throws Exception {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
		
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = calendario.getAno();
		periodo = calendario.getPeriodo();
		enabled = false;
		lista = null;
		
		return forward("/processamento/preProcessamento/entrada.jsp");
	}

	/**
	 * Inicia o pré-procesamento escolhido.
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/processamento/preProcessamento/entrada.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String processar() throws Exception {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
		
		ProcessamentoMatriculaDAO daoP = getDAO(ProcessamentoMatriculaDAO.class);
		boolean autenticado = daoP.autenticacaoProcessamento(senhaProcessamento);
		
		if (autenticado) {
			
			if (tipo == CANCELAR_VINCULOS_ANTERIORES || tipo == RELATORIO_CANCELAMENTO_VINCULOS_ANTERIORES) {
				ProcessamentoMatriculaGraduacaoDao dao = new ProcessamentoMatriculaGraduacaoDao();
				List<Integer> pessoas = dao.buscarAlunosComMaisDeUmVinculo();
				List<Discente[]> discentesCancelados = new ArrayList<Discente[]>();
				
				TipoMovimentacaoAluno efetivacaoNovoCadastro = dao.findByPrimaryKey(ConstantesTipoMovimentacaoAluno.CANCELAMENTO_POR_EFETIVACAO_NOVO_CADASTRO, TipoMovimentacaoAluno.class);

				
				for (Integer pessoa : pessoas) {
					
					if (dao.vinculoMaisNovoPossuiMatriculas(pessoa)) {
						Discente vinculoAnterior = dao.buscarVinculoAtivoMaisAntigo(pessoa);
						Discente vinculoAtual = dao.buscarVinculoAtivoMaisNovo(pessoa);
						
						if (tipo == CANCELAR_VINCULOS_ANTERIORES) {
							prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
							
							MovimentacaoAluno cancelamento = new MovimentacaoAluno();
							cancelamento.setAnoOcorrencia(ano);
							cancelamento.setAnoReferencia(ano);
							cancelamento.setPeriodoOcorrencia(periodo);
							cancelamento.setPeriodoReferencia(periodo);
							cancelamento.setAtivo(true);
							cancelamento.setDataOcorrencia(new Date());
							cancelamento.setDiscente(vinculoAnterior);
							cancelamento.setObservacao("Cancelamento automático no pré-processamento de matrículas devido a existência de um vínculo posterior.");
							cancelamento.setTipoMovimentacaoAluno(efetivacaoNovoCadastro);
							cancelamento.setUsuarioCancelamento(getUsuarioLogado());
							
							execute(new MovimentoCadastro(cancelamento, SigaaListaComando.AFASTAR_ALUNO));
							
							
							String msg = "Caro " + vinculoAnterior.getPessoa().getNome() + ", <br> Gostaríamos de informar que a sua matrícula " + vinculoAnterior.getMatricula() + " foi CANCELADA automaticamente pela "
									+ RepositorioDadosInstitucionais.getSiglaInstituicao() + " durante o processamento de matrículas devido à existência de uma nova matrícula em curso de graduação, a saber, "
									+ vinculoAtual.getMatricula() + " - " + vinculoAtual.getCurso().getNome() + ". <br> Atenciosamente, <br> Pro-Reitoria de Graduação - PROGRAD";
							
							List<Usuario> usrs = getDAO(UsuarioDao.class).findByPessoa(vinculoAnterior.getPessoa());
							if (!isEmpty(usrs)) {
								Mail.sendMessage(vinculoAnterior.getPessoa().getNome(), usrs.iterator().next().getEmail(), "Cancelamento de Matrícula", msg);
							}
							
						}
						
						//discentesCancelados.add(new Discente[] { vinculoAnterior, vinculoAtual});
					}
				}
				
				if (tipo == RELATORIO_CANCELAMENTO_VINCULOS_ANTERIORES) {
					getCurrentRequest().setAttribute("discentes", discentesCancelados);
					return forward("/processamento/preProcessamento/rel_cancelamento.jsf");
				}
				
				//enviarEmailsCancelamento(discentesCancelados);
				
				addMensagemInformation("Cancelamento realizado com sucesso!");
			} else {
			
				ProcessamentoBatchThread<?> threads[] = null;
				Comando comando = null;
				
				if (tipo == MATRICULA_ESPERA) {
					registrarProcessamentoMatricula();
	
					// Cadastrar matrículas em espera
					threads = new GerarMatriculasEsperaThread[numThreads];
					lista = new ListaSolicitacoesPreProcessamento();
					comando = SigaaListaComando.PRE_PROCESSAR_MATRICULA;
				} else {
					// Calcular possíveis formandos
					threads = new CalcularPossiveisFormandosThread[numThreads];
					lista = new ListaAlunosPreProcessamento();
					comando = SigaaListaComando.CALCULAR_POSSIVEIS_FORMANDOS;
				}
	
				// Carrega os elementos a serem processados em batch
				lista.carregar(ano, periodo, rematricula, modo.getClasseDao().newInstance());
				enabled = true;	/** Tipo de pré-processamento */

				
				// Inicia threads do processamento
				for (int a = 0; a < threads.length; a++) {
					if (tipo == MATRICULA_ESPERA)
						threads[a] = new GerarMatriculasEsperaThread((ListaProcessamentoBatch<SolicitacaoMatricula>) lista, ano, periodo, rematricula, modo, comando);
					else
						threads[a] = new CalcularPossiveisFormandosThread((ListaProcessamentoBatch<Integer>) lista, ano, periodo, modo, comando);
					threads[a].start();
				}
	
				// Espera threads acabarem
				for (int a = 0; a < threads.length; a++) {
					threads[a].join();
				}
			
			}
		} else {
			addMensagemErro("A senha digitada é inválida!");
		}

		return forward("/processamento/preProcessamento/entrada.jsf");
	}
	
	/**
	 * Cadastra o registro da execução do processamento.
	 * Método não chamado por JSPs:<br/>
	 * @return
	 * @throws Exception
	 */
	private void registrarProcessamentoMatricula() throws ArqException, NegocioException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		ExecucaoProcessamentoMatricula exec = new ExecucaoProcessamentoMatricula();
		exec.setAno(ano);
		exec.setPeriodo(periodo);
		exec.setData(new Date());
		exec.setRematricula(rematricula);
		exec.setModo(modo.name());
		exec.setTipo(0);
		exec.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(exec);
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		//execute(mov);
	}

	/**
	 * Envia e-mails aos discentes que foram cancelados no pré-processamento.
	 * Método não chamado por JSPs:<br/>
	 * @return
	 * @throws Exception
	 */
	private void enviarEmailsCancelamento(List<Discente[]> discentesCancelados) throws DAOException {
		for (Discente[] discente : discentesCancelados) {
			
			String msg = "Caro " + discente[0].getPessoa().getNome() + ", <br> Gostaríamos de informar que a sua matrícula " + discente[0].getMatricula() + " foi CANCELADA automaticamente pela "
					+ RepositorioDadosInstitucionais.getSiglaInstituicao() + " durante o processamento de matrículas devido à existência de uma nova matrícula em curso de graduação, a saber, "
					+ discente[1].getMatricula() + " - " + discente[1].getCurso().getNome() + ". <br> Atenciosamente, <br> Pro-Reitoria de Graduação - PROGRAD";
			
			List<Usuario> usrs = getDAO(UsuarioDao.class).findByPessoa(discente[0].getPessoa());
			if (!isEmpty(usrs)) {
				Mail.sendMessage(discente[0].getPessoa().getNome(), usrs.iterator().next().getEmail(), "Cancelamento de Matrícula", msg);
			}
		}
	}
	
	public int getTotal() {
		return lista != null ? lista.total : 0;
	}
	
	public int getAtual() {
		return lista != null ? lista.processados : 0;
	}
	
	public ModoProcessamentoMatricula getModo() {
		return modo;
	}

	public void setModo(ModoProcessamentoMatricula modo) {
		this.modo = modo;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
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

	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}

	public boolean isRematricula() {
		return rematricula;
	}

	public void setRematricula(boolean rematricula) {
		this.rematricula = rematricula;
	}

	public String getSenhaProcessamento() {
		return senhaProcessamento;
	}

	public void setSenhaProcessamento(String senhaProcessamento) {
		this.senhaProcessamento = senhaProcessamento;
	}
	
	public List<SelectItem> getModos() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		for (ModoProcessamentoMatricula modo : ModoProcessamentoMatricula.values()) {
			itens.add(new SelectItem(modo));
		}
		return itens;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getEmailsCancelamento() {
		return emailsCancelamento;
	}

	public void setEmailsCancelamento(String emailsCancelamento) {
		this.emailsCancelamento = emailsCancelamento;
	}

	
	
}
