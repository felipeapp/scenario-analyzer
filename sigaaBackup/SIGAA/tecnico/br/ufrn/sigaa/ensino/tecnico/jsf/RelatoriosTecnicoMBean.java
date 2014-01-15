/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/08/2007
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.tecnico.dao.RelatoriosTecnicoDao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean responsável por gerar relatórios do Ensino Técnico.
 * @author Leonardo Campos
 *
 */
@Component("relatoriosTecnico") @Scope("request")
public class RelatoriosTecnicoMBean extends SigaaAbstractController<Object> {

	/** Constante de visualização da view de seleção dos ingressantes  */
	public final static String JSP_SELECIONA_INGRESSANTES = "/ensino/tecnico/relatorios/seleciona_ingressantes.jsp";
	/** Constante de visualização da view de seleção dos abandonos  */
	public final static String JSP_SELECIONA_ABANDONO = "/ensino/tecnico/relatorios/seleciona_abandono.jsp";
	/** Constante de visualização da view de seleção do quantitativo de matriculados  */
	public final static String JSP_SELECIONAQ_MATRICULADOS = "/ensino/tecnico/relatorios/selecionaq_matriculados.jsp";
	/** Constante de visualização da view de seleção dos ingressantes  */
	public final static String JSP_SELECIONA_MATRICULADOS = "/ensino/tecnico/relatorios/seleciona_matriculados.jsp";
	/** Constante de visualização da view de seleção dos tracamentos  */
	public final static String JSP_SELECIONA_TRANCAMENTOS = "/ensino/tecnico/relatorios/seleciona_trancamentos.jsp";
	/** Constante de visualização da view de seleção dos aprovados / Reprovados  */
	public final static String JSP_SELECIONA_APROVADOS_REPROVADOS = "/ensino/tecnico/relatorios/seleciona_aprovados_reprovados.jsp";
	/** Constante de visualização da view de seleção dos concluintes  */
	public final static String JSP_SELECIONA_CONCLUINTES = "/ensino/tecnico/relatorios/seleciona_concluintes.jsp";
	/** Constante de visualização da view de seleção dos concluidos  */
	public final static String JSP_SELECIONA_CONCLUIDOS = "/ensino/tecnico/relatorios/seleciona_concluidos.jsp";
	/** Constante de visualização da view da lista dos ingressantes  */
	public final static String JSP_LISTA_INGRESSANTES = "/ensino/tecnico/relatorios/lista_ingressantes.jsp";
	/** Constante de visualização da view da lista dos abandonos */
	public final static String JSP_LISTA_ABANDONO = "/ensino/tecnico/relatorios/lista_abandono.jsp";
	/** Constante de visualização da view da lista dos concluintes */
	public final static String JSP_LISTA_CONCLUINTES = "/ensino/tecnico/relatorios/lista_concluintes.jsp";
	/** Constante de visualização da view da lista dos concluidos */
	public final static String JSP_LISTA_CONCLUIDOS = "/ensino/tecnico/relatorios/lista_concluidos.jsp";
	/** Constante de visualização da view da lista do quantitativo de matriculados */
	public final static String JSP_QUANT_MATRICULADOS = "/ensino/tecnico/relatorios/quant_matriculados.jsp";
	/** Constante de visualização da view da lista dos matriculados */
	public final static String JSP_LISTA_MATRICULADOS = "/ensino/tecnico/relatorios/lista_matriculados.jsp";
	/** Constante de visualização da view da lista do quantitativo ano ingresso */
	public final static String JSP_QUANT_ANO_INGRESSO = "/ensino/tecnico/relatorios/quant_ano_ingresso.jsp";
	/** Constante de visualização da view da lista dos trancamentos */
	public final static String JSP_LISTA_TRANCAMENTOS = "/ensino/tecnico/relatorios/lista_trancamentos.jsp";
	/** Constante de visualização da view da lista dos matriculados por semestre */
	public final static String JSP_LISTA_TRANCAMENTOS_POR_SEMESTRE = "/ensino/tecnico/relatorios/lista_trancamentos_semestre.jsp";
	/** Constante de visualização da view da lista dos aprovados / reprovados */
	public final static String JSP_QUANT_APROVADOS_REPROVADOS = "/ensino/tecnico/relatorios/quant_aprovados_reprovados.jsp";
	/** Constante de visualização da view da seleção dos ativos matriculados orientados */
	public final static String JSP_SELECIONA_ATIVOS_MATRICULADOS_ORIENTADOS = "/ensino/tecnico/relatorios/seleciona_ativos_matriculados_orientados.jsp";
	/** Constante de visualização da view da seleção dos alunos por Cidade */
	public final static String JSP_SELECIONA_ALUNOS_POR_CIDADE = "/ensino/tecnico/relatorios/seleciona_alunos_por_cidade.jsp";
	/** Constante de visualização da view da seleção dos alunos com movimentação de cancelamento */
	public final static String JSP_LISTA_ALUNOS_MOVIMENTACAO_CANCELAMENTO = "/ensino/tecnico/relatorios/lista_alunos_movimentacao_cancelamento.jsp";
	/** Constante de visualização da view  do quantitativo de orientador por turma */
	public final static String JSP_QUANT_ORIENTADOR_TURMA = "/ensino/tecnico/relatorios/quant_orientador_turma.jsp";
	/** Constante de visualização da view do quantitativo dos alunos Tecnico */
	public final static String JSP_QUANT_ALUNOS_TECNICO = "/ensino/tecnico/relatorios/lista_alunos_mec.jsp";
	/** Constante de visualização da view da listagem dos alunos por curso */
	public final static String JSP_LISTAGEM_ALUNOS_CURSO = "/ensino/tecnico/relatorios/alunos_curso.jsp";
	/** Constante com o codigo da PRÓ-REITORIA DE PLANEJAMENTO E COORDENAÇÃO GERAL */
	public final static Integer PROPLAN = 1449;
	
	/** List que armazena o resultado das consultas */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
	/** Informação do ano e do período para ser usado nas consultas */
	private Integer ano, periodo;
	/** Informação do curso para ser usado nas consultas */
	private Integer idCurso;
	/** Armazena um combo com todos os curso desejados */
	private Collection<SelectItem> cursosCombo = new ArrayList<SelectItem>();
	/** Informação do formato, do nome e do título do relatório */
	private String formato, nomeRelatorio, tituloRelatorio;
	/** Armazena a informação da unidade */
	private Unidade unidade;
	/** Armazena um coleção de discentes */
	private Collection<Discente> discentes = new ArrayList<Discente>();
	/** Map que armazena o resultado das consultas */
	private Map relatorio;
	
	/**
	 * Construtor padrão.
	 */
	public RelatoriosTecnicoMBean() {
		unidade = new Unidade();
		formato = "pdf";
	}

	/**
	 * Método auxiliar para validação de parâmetros submetidos aos relatórios. 
	 */
	private void validateAnoPeriodo(){
		ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequired(periodo, "Período", erros);
	}
	
	/**
	 * Método auxiliar para validação de parâmetros submetidos aos relatórios. 
	 * @throws ArqException 
	 */
	private void validateUnidade() throws ArqException{
		erros = new ListaMensagens();
		if(isProplan())
		 ValidatorUtil.validateRequiredId(unidade.getId(), "Unidade", erros);
	}
	
	
	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do relatório de alunos ingressantes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String iniciarRelatorioAlunosIngressantes() {
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		return forward(JSP_SELECIONA_INGRESSANTES);
	}
	
	/**
	 * Gera o relatório de alunos ingressantes do ensino técnico.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/seleciona_ingressantes.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRelatorioAlunosIngressantes() throws DAOException, ArqException {
		RelatoriosTecnicoDao dao = getDAO(RelatoriosTecnicoDao.class);
		lista = dao.findListaAlunosIngressantes(getUnidadeGestora(), idCurso, ano, periodo, getNivelEnsino());
		return forward(JSP_LISTA_INGRESSANTES);
	}
	
	/**
	 * Redireciona para o formulário com os parâmetros do relatório de alunos ingressantes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioAlunosConcluintes() throws ArqException{
		return forward(JSP_SELECIONA_CONCLUINTES);
	}

	/**
	 * lista das unidade gestoras
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/medio/relatorios/seleciona_matriculados.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException 
	 */
	public List<SelectItem> getUnidades() throws DAOException {
		List<SelectItem> unidades = new ArrayList<SelectItem>(0);
		UnidadeDao dao = getDAO(UnidadeDao.class);
		unidades = toSelectItems(dao.findAllGestorasAcademicas(NivelEnsino.TECNICO), "id", "nome");
		return unidades;
	}
	
	/**
	 * verifica se a unidade do usiario logado é proplan 
	 * @throws ArqException
	 * @return 
	 */
	public boolean isProplan() throws ArqException{
		if(getUsuarioLogado().getUnidade().getId() == PROPLAN)
			return true;
		return false;
	}
	
	/**
	 * Gera o relatório de alunos concluintes do ensino técnico.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/seleciona_concluintes.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRelatorioConcluintes() throws DAOException, ArqException {
		RelatoriosTecnicoDao dao = getDAO(RelatoriosTecnicoDao.class);
		lista = dao.findListaAlunosConcluintes(getUnidadeGestora(), idCurso);
		return forward(JSP_LISTA_CONCLUINTES);
	}

	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do relatório de alunos concluídos.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioAlunosConcluidos() throws ArqException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		return forward(JSP_SELECIONA_CONCLUIDOS);
	}
	
	/**
	 * Gera o relatório de alunos concluídos.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/seleciona_concluidos.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRelatorioConcluidos() throws DAOException, ArqException {
	    if(ano == null || periodo == null) {
	        addMensagemErro("Ano-Período de Saída: Campo obrigatório não informado.");
	        return null;
	    }
		RelatoriosTecnicoDao dao = getDAO(RelatoriosTecnicoDao.class);
		lista = dao.findListaAlunosConcluidos(getUnidadeGestora(), idCurso, ano, periodo);
		return forward(JSP_LISTA_CONCLUIDOS);
	}
	
	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do 
	 * relatório de quantitativo de alunos matriculados por ano-período e faixa etária.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioQuantitativoAlunosMatriculadosByAnoPeriodoFaixaEtaria() throws ArqException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		nomeRelatorio = "Tecnico_trf3927_Quantitativo_de_alunos_habilitacao_faixa_etaria_periodo";
		tituloRelatorio = "Quantitativo de Alunos Matriculados Por Ano, Período, Faixa Etária e Habilitação";
		unidade = getGenericDAO().findByPrimaryKey(getUnidadeGestora(), Unidade.class);
		return forward(JSP_SELECIONAQ_MATRICULADOS);
	}

	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do 
	 * relatório de alunos que podem tirar certificado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioAlunosCertificados() throws ArqException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		nomeRelatorio = "Tecnico_trf1073_Alunos_que_podem_tirar_certificado";
		tituloRelatorio = "Relatório de Alunos que podem tirar Certificados";
		unidade = getGenericDAO().findByPrimaryKey(getUnidadeGestora(), Unidade.class);
		return forward(JSP_SELECIONAQ_MATRICULADOS);
	}
	
	
	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do 
	 * relatório de alunos ativos x matriculados x orientados.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioAlunosAtivosMatriculadosOrientados() throws ArqException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		nomeRelatorio = "trf8172_Ativos_X_Matriculados_X_Orientados";
		tituloRelatorio = "Quantitativo de Alunos Ativos, Matriculados e Orientados";
		return forward(JSP_SELECIONA_ATIVOS_MATRICULADOS_ORIENTADOS);
	}
	
	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do relatório de alunos por cidade.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioAlunosPorCidade() throws ArqException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		nomeRelatorio = "trf9419_Tecnico_PorCidade";
		tituloRelatorio = "Relatório de Alunos do Ensino Técnico por Cidade";
		unidade = getGenericDAO().findByPrimaryKey(getUnidadeGestora(), Unidade.class);
		return forward(JSP_SELECIONA_ALUNOS_POR_CIDADE);
	}
	
	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do 
	 * relatório de alunos enquadrados no artigo 29 do regimento da Escola de Música.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRelatorioArtigo29() throws ArqException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		nomeRelatorio = "trf8433_Tecnico_Art29";
		tituloRelatorio = "Relatório do Artigo 29";
		unidade = getGenericDAO().findByPrimaryKey(getUnidadeGestora(), Unidade.class);
		return forward(JSP_SELECIONA_ALUNOS_POR_CIDADE);
	}

	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do relatório de alunos matriculados.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 */
	public String iniciarRelatorioListaAlunosMatriculados() throws DAOException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		return forward(JSP_SELECIONA_MATRICULADOS);
	}

	/**
	 * Gera o relatório de alunos matriculados
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/seleciona_matriculados.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRelatorioListaAlunosMatriculados() throws DAOException, ArqException {
		validateUnidade();
		validateAnoPeriodo();
		if(hasErrors()){
			addMensagens(erros);
			return null;
		}
		UnidadeDao uDao = getDAO(UnidadeDao.class);
		RelatoriosTecnicoDao dao =  getDAO(RelatoriosTecnicoDao.class);
		if(unidade.getId() == 0)
			unidade.setId(getUnidadeGestora());
		
		unidade= uDao.findById(unidade.getId());
		
		lista = dao.findListaAlunosMatriculados(ano, periodo, unidade.getId());
		return forward(JSP_LISTA_MATRICULADOS);
	}

	/**
	 * Gera o relatório quantitativo de alunos por ano de ingresso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRelatorioQuantitativoAlunosAnoIngresso() throws DAOException, ArqException {
		RelatoriosTecnicoDao dao = getDAO(RelatoriosTecnicoDao.class);
		lista = dao.findQuantitativoAlunosByAnoIngresso(getUnidadeGestora());
		return forward(JSP_QUANT_ANO_INGRESSO);
	}

	/**
	 * Gera o relatório de alunos que possuem movimentação de cancelamento.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRelatorioListaAlunosComMovimentacaoCancelamento() throws DAOException, ArqException {
		RelatoriosTecnicoDao dao = getDAO(RelatoriosTecnicoDao.class);
		lista = dao.findListaAlunosComMovimentacaoCancelamento(getUnidadeGestora());
		return forward(JSP_LISTA_ALUNOS_MOVIMENTACAO_CANCELAMENTO);
	}
	
	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do relatório de alunos com trancamento.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarRelatorioListaTrancamentoAluno() throws DAOException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		return forward(JSP_SELECIONA_TRANCAMENTOS);
	}

	/**
	 * Gera o relatório de alunos com trancamento.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/seleciona_trancamentos.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerarRelatorioListaTrancamentoAluno() throws ArqException{
		RelatoriosTecnicoDao dao =  getDAO(RelatoriosTecnicoDao.class);
		lista = dao.findListaTrancamentosByDisciplina(ano, periodo, getUnidadeGestora());
		return forward(JSP_LISTA_TRANCAMENTOS);
	}

	/**
	 * Gera o relatório de trancamentos por semestre.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerarRelatorioListaTrancamentosSemestre() throws ArqException{
		RelatoriosTecnicoDao dao = getDAO(RelatoriosTecnicoDao.class);
		lista = dao.findListaTrancamentosByAnoSemestre(getUnidadeGestora());
		return forward(JSP_LISTA_TRANCAMENTOS_POR_SEMESTRE);
	}

	/**
	 * Gera o relatório de abandono.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/seleciona_abandono.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerarRelatorioAbandono() throws ArqException{
		RelatoriosTecnicoDao dao = getDAO(RelatoriosTecnicoDao.class);
		lista = dao.findListaAlunosSemMatriculaComponente(ano, periodo, getUnidadeGestora());
		return forward(JSP_LISTA_ABANDONO);
	}

	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do relatório de abandonos.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarRelatorioAbandono() throws DAOException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		return forward(JSP_SELECIONA_ABANDONO);
	}

	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do 
	 * relatório de aprovados e reprovados por disciplina.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarRelatorioAprovadosReprovadosDisciplina() throws DAOException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		return forward(JSP_SELECIONA_APROVADOS_REPROVADOS);
	}

	/**
	 * Gera o relatório de aprovados e reprovados por disciplina.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/seleciona_aprovados_reprovados.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String gerarRelatorioAprovadosReprovadosDisciplina() throws ArqException{
		RelatoriosTecnicoDao dao = getDAO(RelatoriosTecnicoDao.class);
		if (ValidatorUtil.isEmpty(ano)) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if(ValidatorUtil.isEmpty(periodo))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");
			
		if (hasOnlyErrors()) 
			return null;
		
		relatorio = dao.findAprovadosReprovadosDisciplina(ano, periodo, getUnidadeGestora());
		return forward(JSP_QUANT_APROVADOS_REPROVADOS);
	}

	/**
	 * Popula os dados e redireciona para o formulário com os parâmetros do relatório de orientadores por turma.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarRelatorioOrientadoresPorTurma() throws DAOException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		return forward(JSP_QUANT_ORIENTADOR_TURMA);
	}	
	
	/**
	 * Gera o relatório de orientadores por turma.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/quant_orientador_turma.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 * @throws SQLException
	 * @throws JRException
	 * @throws IOException
	 */
	public String gerarRelatorioOrientadoresPorTurma() throws ArqException, SQLException, JRException, IOException{

		Connection con = Database.getInstance().getSigaaConnection();
		
		// Parâmetros
		Map<String, Object> hs = new HashMap<String, Object>();
		hs.put("ano", ano);
		hs.put("subSistema", getSubSistema().getNome());
        hs.put("subSistemaLink", getSubSistema().getLink());
		
		JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("trf8041_AlunosPorOrientador.jasper"), hs, con);
		
		if (prt.getPages().isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
        String nomeArquivo = "RelatorioOrietadorPorTurma" + formato;
        JasperReportsUtil.exportarPdf(prt, nomeArquivo, getCurrentResponse());
        FacesContext.getCurrentInstance().responseComplete();
		return null;
	}
	
	/**
	 * Gera um Relatório quantitativo dos alunos reprovados por nota e falta, agrupando os mesmo por curso
	 * tendo com base os dados informados na busca_relatorio.jsp 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String listagemTodosAlunos() throws DAOException, ArqException {
		RelatoriosTecnicoDao dao = getDAO(RelatoriosTecnicoDao.class);
		lista = dao.findAlunosTecnico(getUsuarioLogado().getVinculoAtivo().getUnidade().getId());
		return forward(JSP_QUANT_ALUNOS_TECNICO);
	}

	/**
	 * Lista todos os discentes cadastrados em uma determinada unidade.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String listagemAlunosCadastrados() throws DAOException, ArqException{
		checkRole(SigaaPapeis.GESTOR_TECNICO);
		RelatoriosTecnicoDao dao = getDAO(RelatoriosTecnicoDao.class);
		lista = dao.findListaAlunosCadastrados(getUnidadeGestora(), getNivelEnsino());
		return forward(JSP_LISTAGEM_ALUNOS_CURSO);
	}
	
	/**
	 * Retorna a quantidade de registros encontrados na lista
	 * 
	 * @return
	 */
	public int getNumeroRegistrosEncontrados() {
		if(lista!=null)
			return lista.size();
		else
			return 0;
	}

	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Map getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(Map relatorio) {
		this.relatorio = relatorio;
	}

	public Integer getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

	public Collection<SelectItem> getCursos() {
		return cursosCombo;
	}

	public void setCursos(Collection<SelectItem> cursos) {
		this.cursosCombo = cursos;
	}

	/** Retorna todos os cursos da unidade gestora do usuário logado */
	public Collection<SelectItem> getCursosCombo() throws DAOException, ArqException {
		return toSelectItems(getGenericDAO().findByExactField(Curso.class, "unidade.id", getUnidadeGestora()), "id", "nome");
	}

	public void setCursosCombo(Collection<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}
	
	
	 /**
     * Realizar a geração do relatório, de acordo com os critérios selecionados
     * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/seleciona_alunos_por_cidade.jsp</li>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/seleciona_ativos_matriculados_orientados.jsp</li>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/selecionaq_matriculados.jsp</li>
	 *	</ul> 
     * @return
     * @throws DAOException
     */
    public String gerarRelatorio() throws DAOException {

    	// Validar campos do formulário
        if ( !validate() ) {
            return null;
        }

        // Gerar relatório
        Connection con = null;
        try {
        	
            // Popular parâmetros do relatório
            HashMap<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("SUBREPORT_DIR", JasperReportsUtil.getReportSIGAA(nomeRelatorio + ".jasper") );
            parametros.put("gestora", unidade.getId() );
            parametros.put("ano", ano );
            parametros.put("periodo", periodo );
            parametros.put("curso", idCurso );
            parametros.put("subSistema", getSubSistema().getNome());
	        parametros.put("subSistemaLink", getSubSistema().getLink());
	        parametros.put("unidade", getUsuarioLogado().getVinculoAtivo().getUnidade().getId());
	        
            // Preencher relatório
            con = Database.getInstance().getSigaaConnection();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(nomeRelatorio + ".jasper"), parametros, con );

	        if (prt.getPages().size() == 0) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
            
            // Exportar relatório de acordo com o formato escolhido
            String nomeArquivo = nomeRelatorio+"."+formato;
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e);
            addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor, contacte o suporte através do \"Abrir Chamado\"");
            return null;
        } finally {
            Database.getInstance().close(con);
        }

        return null;
    }

    /**
     * Validar dados do formulário
     *
     * @return
     */
    private boolean validate() {
        ListaMensagens erros = new ListaMensagens();

      	ValidatorUtil.validaInt(ano, "Ano", erros);
      	ValidatorUtil.validateRange(periodo, 1, 2, "Período", erros);
      	
        addMensagens(erros);
        return erros.isEmpty();
    }

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public String getTituloRelatorio() {
		return tituloRelatorio;
	}

	public void setTituloRelatorio(String tituloRelatorio) {
		this.tituloRelatorio = tituloRelatorio;
	}

	public Collection<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}

}