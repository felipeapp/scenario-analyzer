/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 31/03/2010
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.TrapezoidIntegrator;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;

/**
 * Managed Bean para realizar o cadastro de índices acadêmicos.
 * 
 * @author David Pereira
 */
@Component
@Scope("request")
public class IndiceAcademicoMBean extends SigaaAbstractController<IndiceAcademico> implements OperadorDiscente {

	/** Página JSP destinado para o relatório de índices acadêmicos. */
	public static final String JSP_REL_INDICES_ALUNOS = "/graduacao/discente/relatorio_indices_discente.jsp";

	/**
	 * Valor máximo de iterações realizados pelo TrapezoidIntegrator na
	 * construção do gráfico de índices.
	 */
	public static final Integer MAX_ITERATION_COUNT = 64;
	/** Valor mínimo de desvio realizado no calculo do percentual do gráfico. */
	public static final double MIN_VALOR_TESTE_DESVIO = 0.00000001;

	/** Discente a ser utilizado na execução do relatório de índices acadêmicos. */
	private DiscenteAdapter discente;

	/** Utilizado para exibir o ano e semestre atual. */
	private String anoSemestre;

	/**
	 * Índices acadêmicos calculados para o discente selecionado.
	 */
	private List<IndiceAcademicoDiscente> indices;

	/** Valor do índice acadêmico Média de Conclusão */
	private double mc;
	
	/** Valor do índice acadêmico Índice de Eficiência Acadêmica */
	private double iea;

	/**
	 * Valor responsável pela média da Média de Conclusão dos discentes do
	 * curso.
	 */
	private double meanMc;

	/**
	 * Variável para manipular o desvio da Média de Conclusão dentre os
	 * discentes do curso.
	 */
	private double stdMc;

	/**
	 * Variável para manipular a maior Média de Conclusão dentre os discentes do
	 * curso.
	 */
	private double maiorMc;

	/**
	 * Variável para manipular a menor Média de Conclusão dentre os discentes do
	 * curso.
	 */
	private double menorMc;
	
	/**
	 * Valor responsável pela média do IEA dos discentes do
	 * curso.
	 */
	private double meanIea;

	/**
	 * Variável para manipular o desvio do IEA dentre os
	 * discentes do curso.
	 */
	private double stdIea;

	/**
	 * Variável para manipular o maior IEA dentre os discentes do
	 * curso.
	 */
	private double maiorIea;

	/**
	 * Variável para manipular a menor IEA dentre os discentes do
	 * curso.
	 */
	private double menorIea;

	/**
	 * Variável para manter a porcentagem da Média de Conclusão do aluno em
	 * relação a maior Média de Conclusão do curso.
	 */
	private double porcentagemMc;
	
	/**
	 * Variável para manter a porcentagem do IEA do aluno em
	 * relação a maior Média de Conclusão do curso.
	 */
	private double porcentagemIea;

	public IndiceAcademicoMBean() {
		obj = new IndiceAcademico();
		obj.setNivel(getNivelEnsino());
	}

	/**
	 * Redireciona o usuário para a busca de discente para iniciar a operação de
	 * antecipar o prazo de conclusão. <br>
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * <li>/sigaa.war/graduacao/discente/relatorio_indices_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String buscarIndicesDiscente() throws SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.GESTOR_NEE, SigaaPapeis.CONSULTADOR_ACADEMICO);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.RELATORIO_INDICES_DISCENTE);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Método responsável por exibir mensagem avisando que o relatório de
	 * índices está temporariamente indisponível devido ao portal discente estar
	 * em modo reduzido. <br>
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String relatorioInativo() {
		addMensagemWarning("Seus índices acadêmicos precisam ser atualizados para exibição, mas a atualização de índices encontra-se temporariamente indisponível.");
		return redirect("/portais/discente/discente.jsf");
	}

	/**
	 * Verifica se deve ser exibido o relatório de índices para o discente
	 * 
	 * @return
	 * @throws DAOException
	 */
	private boolean isPassivelEmissaoRelatorioIndices(DiscenteAdapter discente) throws DAOException {
		boolean passivelEmissaoRelatoriosIndices = false;

		//Garantir acesso do relatório aos coordenadores de curso.
		if(getCursoAtualCoordenacao() != null)
			return true;
		
		discente = discente != null ? discente : getDiscenteUsuario();
		if (discente != null && discente.getNivel() == NivelEnsino.GRADUACAO) {

			if (discente.isAtivo()) {
				return true;
			} else {
				MovimentacaoAluno movSaida = getDAO(MovimentacaoAlunoDao.class).findConclusaoByDiscente(
						discente.getId());
				discente.setMovimentacaoSaida(movSaida);
				passivelEmissaoRelatoriosIndices = discente.isGraduacao()
						&& ((DiscenteGraduacao) discente).isPassivelCalculoIndices();
			}
		}
		return passivelEmissaoRelatoriosIndices;
	}

	/**
	 * Método responsável por direcionar e calcular os índices do discente de
	 * graduação a serem listados na consulta de Índices Acadêmicos por aluno. <br>
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * <li>/sigaa.war/graduacao/discente/relatorio_indices_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String selecionaDiscente() throws ArqException {

		if (!isPassivelEmissaoRelatorioIndices(discente)) {
			addMensagemErro("Não há índices calculados para este discente.");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.RELATORIO_INDICES_DISCENTE);
		anoSemestre = CalendarUtils.getAnoAtual() + "." + getPeriodoAtual();
		IndiceAcademicoDao dao = getDAO(IndiceAcademicoDao.class);

		if (discente == null || discente.getId() == 0) {
			discente = getDAO(DiscenteDao.class).findByPK(getUsuarioLogado().getDiscente().getId());
		}

		boolean atualizarIndices = false;

		if (discente.isGraduacao()
				&& (discente.getStatus() == StatusDiscente.CONCLUIDO || StatusDiscente.getStatusComVinculo().contains(
						discente.getStatus()))) {

			DiscenteGraduacao dg = (DiscenteGraduacao) discente;
			discente.getDiscente().setIndices(dao.findIndicesAcademicoDiscente(dg));
			if (discente.getDiscente().getIndices().isEmpty() || dg.getUltimaAtualizacaoTotais() == null) {
				atualizarIndices = true;
			}
		}

		/*
		 * Caso os índices do discente esteja desatualizado, faz-se o calculo
		 * dos índices do aluno.
		 */
		if (atualizarIndices) {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.RELATORIO_INDICES_DISCENTE);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setObjMovimentado(discente);
			mov.setObjAuxiliar(new Object[] { false, true });

			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
			} catch (NegocioException ne) {
				addMensagemErro(ne.getMessage());
				return null;
			} catch (ArqException e) {
				addMensagemErroPadrao();
				notifyError(e);
				return null;
			}
			discente.getDiscente().setIndices(dao.findIndicesAcademicoDiscente(discente.getDiscente()));
		}
		

		if ( discente.getDiscente().getIndices().isEmpty() ) {
			addMensagemErro("Não há índices calculados para este discente.");
			return null;
		}
		
		calcularGraficoMediaConclusaoNormalizada(dao);
		calcularGraficoIndiceEficienciaAcademicaNormalizado(dao);
		

		return forward(JSP_REL_INDICES_ALUNOS);
	}
	
	/**
	 * Este método serve para criar o gráfico da média de conclusão normalizada e gerar informações que serão exibidas junto ao gráfico na JSP. 
	 * @param dao
	 */
	private void calcularGraficoMediaConclusaoNormalizada(IndiceAcademicoDao dao) {
		
		
		if( isPossivelCalcularGraficoMediaConclusaoNormalizada() ) {
			boolean temConcluidos = dao.temConcluidos(CalendarUtils.getAnoAtual(), discente.getId());
			this.meanMc = dao.calculaMediaMc(temConcluidos, CalendarUtils.getAnoAtual(), discente.getId());
			this.stdMc = dao.calculaDesvioMc(temConcluidos, CalendarUtils.getAnoAtual(), discente.getId(), meanMc);
			this.maiorMc = dao.calculaMaiorMc(temConcluidos, discente.getId());
			this.menorMc = dao.calculaMenorMc(temConcluidos, discente.getId());
			this.menorMc = this.menorMc == this.maiorMc ? this.menorMc - MIN_VALOR_TESTE_DESVIO : this.menorMc;
			this.mc = dao.calculaMcDiscente(discente.getId());
			
			final NormalDistributionFunction2D functionMc = new NormalDistributionFunction2D(meanMc, stdMc);
			
			TrapezoidIntegrator integMc = new TrapezoidIntegrator(new UnivariateRealFunction() {
				public double value(double x) throws FunctionEvaluationException {
					return functionMc.getValue(x);
				}
			});
			integMc.setMaximalIterationCount(MAX_ITERATION_COUNT);
			
			try {
				if (mc < maiorMc && stdMc > MIN_VALOR_TESTE_DESVIO)
					this.porcentagemMc = integMc.integrate(mc, maiorMc) * 100;
				else
					this.porcentagemMc = 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}		
		
	}
	/**
	 * Este método serve para criar Gráfico do Índice de Eficiência Acadêmica Normalizada e gerar informações que serão exibidas junto ao gráfico na JSP.
	 * @param dao
	 */
	private void calcularGraficoIndiceEficienciaAcademicaNormalizado(IndiceAcademicoDao dao) {
		
		if ( isPossivelCalcularGraficoIndiceEficienciaAcademicaNormalizado() ) {
		
			this.meanIea = dao.calculaMediaIea(CalendarUtils.getAnoAtual(), discente.getId());
			this.stdIea = dao.calculaDesvioIea(CalendarUtils.getAnoAtual(), discente.getId(), meanIea);
			this.maiorIea = dao.calculaMaiorIea(discente.getId());
			this.menorIea = dao.calculaMenorIea(discente.getId());
			this.menorIea = this.menorIea == this.maiorIea ? this.menorIea - MIN_VALOR_TESTE_DESVIO : this.menorIea;
			
			this.iea = dao.buscaIeaDiscente(discente.getId());
		
			final NormalDistributionFunction2D functionIea = new NormalDistributionFunction2D(meanIea, stdIea);
			TrapezoidIntegrator integIea = new TrapezoidIntegrator(new UnivariateRealFunction() {
				public double value(double x) throws FunctionEvaluationException {
					return functionIea.getValue(x);
				}
			});
			
			integIea.setMaximalIterationCount(MAX_ITERATION_COUNT);
		
			try {
				if (iea < maiorIea && stdIea > MIN_VALOR_TESTE_DESVIO)
					this.porcentagemIea = integIea.integrate(iea, maiorIea) * 100;
				else
					this.porcentagemIea = 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
	
	/**
	 * Informa se é possível calcular o Gráfico de Media Conclusão Normalizada
	 * 
	 * @return
	 */
	public boolean isPossivelCalcularGraficoMediaConclusaoNormalizada() {		
		IndiceAcademicoDao dao = getDAO(IndiceAcademicoDao.class);
		boolean temConcluidos = dao.temConcluidos(CalendarUtils.getAnoAtual(), discente.getId());
		this.meanMc = dao.calculaMediaMc(temConcluidos, CalendarUtils.getAnoAtual(), discente.getId());
		this.stdMc = dao.calculaDesvioMc(temConcluidos, CalendarUtils.getAnoAtual(), discente.getId(), meanMc);
		if(discente.getCurso() != null && stdMc > 0)
			return true;
		return false;
	}
	
	/**
	 * Informa se é possível calcular o Gráfico de Índice de Eficiência Acadêmica Normalizada
	 * 
	 * @return
	 */
	public boolean isPossivelCalcularGraficoIndiceEficienciaAcademicaNormalizado() {		
		/*IndiceAcademicoDao dao = getDAO(IndiceAcademicoDao.class);
		this.meanIea = dao.calculaMediaIea(CalendarUtils.getAnoAtual(), discente.getId());
		this.stdIea = dao.calculaDesvioIea(CalendarUtils.getAnoAtual(), discente.getId(), meanIea);
		if(discente.getCurso() != null && stdIea > 0 )
			return true; */
		return false;
	}

	/**
	 * AbstractControllerCadastro Método não invocado por JSPs
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	@Override
	public String getFormPage() {
		return "/ensino/IndiceAcademico/form.jsf";
	}

	@Override
	public String getListPage() {
		return "/ensino/IndiceAcademico/lista.jsf";
	}

	@Override
	public Collection<IndiceAcademico> getAll() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CDP);
		return getDAO(IndiceAcademicoDao.class).findIndicesAtivos(getNivelEnsino());
	}

	/**
	 * AbstractControllerCadastro Método não invocado por JSPs
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CDP);
	}

	public List<IndiceAcademicoDiscente> getIndices() {
		return indices;
	}

	public void setIndices(List<IndiceAcademicoDiscente> indices) {
		this.indices = indices;
	}

	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = discente;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public String getAnoSemestre() {
		return anoSemestre;
	}

	public void setAnoSemestre(String anoSemestre) {
		this.anoSemestre = anoSemestre;
	}

	public double getMc() {
		return mc;
	}

	public void setMc(double mc) {
		this.mc = mc;
	}

	public double getMaiorMc() {
		return maiorMc;
	}

	public void setMaiorMc(double maiorMc) {
		this.maiorMc = maiorMc;
	}

	public double getMenorMc() {
		return menorMc;
	}

	public void setMenorMc(double menorMc) {
		this.menorMc = menorMc;
	}

	public double getPorcentagemMc() {
		return porcentagemMc;
	}

	public void setPorcentagemMc(double porcentagemMc) {
		this.porcentagemMc = porcentagemMc;
	}

	public double getPorcentagemIea() {
		return porcentagemIea;
	}

	public void setPorcentagemIea(double porcentagemIea) {
		this.porcentagemIea = porcentagemIea;
	}

	public double getIea() {
		return iea;
	}

	public void setIea(double iea) {
		this.iea = iea;
	}

	public double getMeanMc() {
		return meanMc;
	}

	public void setMeanMc(double meanMc) {
		this.meanMc = meanMc;
	}

	public double getStdMc() {
		return stdMc;
	}

	public void setStdMc(double stdMc) {
		this.stdMc = stdMc;
	}

	public double getMeanIea() {
		return meanIea;
	}

	public void setMeanIea(double meanIea) {
		this.meanIea = meanIea;
	}

	public double getStdIea() {
		return stdIea;
	}

	public void setStdIea(double stdIea) {
		this.stdIea = stdIea;
	}

	public double getMaiorIea() {
		return maiorIea;
	}

	public void setMaiorIea(double maiorIea) {
		this.maiorIea = maiorIea;
	}

	public double getMenorIea() {
		return menorIea;
	}

	public void setMenorIea(double menorIea) {
		this.menorIea = menorIea;
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(IndiceAcademico.class, "id", "nome");
	}
}
