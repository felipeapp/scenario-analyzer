/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
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
 * Managed Bean para realizar o cadastro de �ndices acad�micos.
 * 
 * @author David Pereira
 */
@Component
@Scope("request")
public class IndiceAcademicoMBean extends SigaaAbstractController<IndiceAcademico> implements OperadorDiscente {

	/** P�gina JSP destinado para o relat�rio de �ndices acad�micos. */
	public static final String JSP_REL_INDICES_ALUNOS = "/graduacao/discente/relatorio_indices_discente.jsp";

	/**
	 * Valor m�ximo de itera��es realizados pelo TrapezoidIntegrator na
	 * constru��o do gr�fico de �ndices.
	 */
	public static final Integer MAX_ITERATION_COUNT = 64;
	/** Valor m�nimo de desvio realizado no calculo do percentual do gr�fico. */
	public static final double MIN_VALOR_TESTE_DESVIO = 0.00000001;

	/** Discente a ser utilizado na execu��o do relat�rio de �ndices acad�micos. */
	private DiscenteAdapter discente;

	/** Utilizado para exibir o ano e semestre atual. */
	private String anoSemestre;

	/**
	 * �ndices acad�micos calculados para o discente selecionado.
	 */
	private List<IndiceAcademicoDiscente> indices;

	/** Valor do �ndice acad�mico M�dia de Conclus�o */
	private double mc;
	
	/** Valor do �ndice acad�mico �ndice de Efici�ncia Acad�mica */
	private double iea;

	/**
	 * Valor respons�vel pela m�dia da M�dia de Conclus�o dos discentes do
	 * curso.
	 */
	private double meanMc;

	/**
	 * Vari�vel para manipular o desvio da M�dia de Conclus�o dentre os
	 * discentes do curso.
	 */
	private double stdMc;

	/**
	 * Vari�vel para manipular a maior M�dia de Conclus�o dentre os discentes do
	 * curso.
	 */
	private double maiorMc;

	/**
	 * Vari�vel para manipular a menor M�dia de Conclus�o dentre os discentes do
	 * curso.
	 */
	private double menorMc;
	
	/**
	 * Valor respons�vel pela m�dia do IEA dos discentes do
	 * curso.
	 */
	private double meanIea;

	/**
	 * Vari�vel para manipular o desvio do IEA dentre os
	 * discentes do curso.
	 */
	private double stdIea;

	/**
	 * Vari�vel para manipular o maior IEA dentre os discentes do
	 * curso.
	 */
	private double maiorIea;

	/**
	 * Vari�vel para manipular a menor IEA dentre os discentes do
	 * curso.
	 */
	private double menorIea;

	/**
	 * Vari�vel para manter a porcentagem da M�dia de Conclus�o do aluno em
	 * rela��o a maior M�dia de Conclus�o do curso.
	 */
	private double porcentagemMc;
	
	/**
	 * Vari�vel para manter a porcentagem do IEA do aluno em
	 * rela��o a maior M�dia de Conclus�o do curso.
	 */
	private double porcentagemIea;

	public IndiceAcademicoMBean() {
		obj = new IndiceAcademico();
		obj.setNivel(getNivelEnsino());
	}

	/**
	 * Redireciona o usu�rio para a busca de discente para iniciar a opera��o de
	 * antecipar o prazo de conclus�o. <br>
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
	 * M�todo respons�vel por exibir mensagem avisando que o relat�rio de
	 * �ndices est� temporariamente indispon�vel devido ao portal discente estar
	 * em modo reduzido. <br>
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String relatorioInativo() {
		addMensagemWarning("Seus �ndices acad�micos precisam ser atualizados para exibi��o, mas a atualiza��o de �ndices encontra-se temporariamente indispon�vel.");
		return redirect("/portais/discente/discente.jsf");
	}

	/**
	 * Verifica se deve ser exibido o relat�rio de �ndices para o discente
	 * 
	 * @return
	 * @throws DAOException
	 */
	private boolean isPassivelEmissaoRelatorioIndices(DiscenteAdapter discente) throws DAOException {
		boolean passivelEmissaoRelatoriosIndices = false;

		//Garantir acesso do relat�rio aos coordenadores de curso.
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
	 * M�todo respons�vel por direcionar e calcular os �ndices do discente de
	 * gradua��o a serem listados na consulta de �ndices Acad�micos por aluno. <br>
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
			addMensagemErro("N�o h� �ndices calculados para este discente.");
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
		 * Caso os �ndices do discente esteja desatualizado, faz-se o calculo
		 * dos �ndices do aluno.
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
			addMensagemErro("N�o h� �ndices calculados para este discente.");
			return null;
		}
		
		calcularGraficoMediaConclusaoNormalizada(dao);
		calcularGraficoIndiceEficienciaAcademicaNormalizado(dao);
		

		return forward(JSP_REL_INDICES_ALUNOS);
	}
	
	/**
	 * Este m�todo serve para criar o gr�fico da m�dia de conclus�o normalizada e gerar informa��es que ser�o exibidas junto ao gr�fico na JSP. 
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
	 * Este m�todo serve para criar Gr�fico do �ndice de Efici�ncia Acad�mica Normalizada e gerar informa��es que ser�o exibidas junto ao gr�fico na JSP.
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
	 * Informa se � poss�vel calcular o Gr�fico de Media Conclus�o Normalizada
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
	 * Informa se � poss�vel calcular o Gr�fico de �ndice de Efici�ncia Acad�mica Normalizada
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
	 * AbstractControllerCadastro M�todo n�o invocado por JSPs
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
	 * AbstractControllerCadastro M�todo n�o invocado por JSPs
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
