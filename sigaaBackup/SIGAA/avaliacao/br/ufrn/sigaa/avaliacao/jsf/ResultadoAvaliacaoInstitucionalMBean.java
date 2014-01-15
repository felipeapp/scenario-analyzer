/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 21/08/2008 
 */
package br.ufrn.sigaa.avaliacao.jsf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoResposta;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoTurma;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.util.TurmaUtil;

/**
 * Managed Bean para gerar os relatórios com o resultado da avaliação Institucional.
 * 
 * @author David Pereira
 *
 */
@SuppressWarnings("serial")
@Component @Scope("request")
public class ResultadoAvaliacaoInstitucionalMBean extends AbstractController {

	/** Ano da avaliação. */
	private int ano;
	
	/** Período da avaliação. */
	private int periodo;
	
	/** Cache de calendário de turmas, utilizado para diminuir as consultas ao banco. */
	private Map<Integer, CalendarioAcademico> cacheCalendarioTurma = new TreeMap<Integer, CalendarioAcademico>();
	
	/** Cache de aulas-turmas, utilizado para diminuir as consultas ao banco. */
	private Map<Integer, Set<Date>> cacheAulasTurma = new TreeMap<Integer, Set<Date>>();
	
	/** Cache de turmas, utilizado para diminuir as consultas ao banco. */
	private Map<Integer, Turma> cacheTurma = new HashMap<Integer, Turma>();
	
	/** Inicia a geração dos arquivos de resultado.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/menu_administracao.jsp
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciar() throws DAOException {
		CalendarioAcademico c = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = c.getAno();
		periodo = c.getPeriodo();
		return forward("/avaliacao/resultado.jsp");
	}

	/**
	 * Gerar resultados da avaliação discente.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Chamado por/avaliacao/relatorios.jsp </li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws IOException
	 */
	public String resultado() throws DAOException, IOException {
		validacaoDados(erros.getMensagens());
		if (hasErrors())
			return null;
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		List<ResultadoAvaliacao> resDiscente = dao.findResultadosDiscentes(ano, periodo);
		if (resDiscente == null || resDiscente.size() == 0) {
			addMensagemWarning("Não há resultados para o ano/período informados");
			return null;
		}
		PrintWriter out = getCurrentResponse().getWriter();
		
		getCurrentResponse().setContentType("text/csv");
		getCurrentResponse().setCharacterEncoding("iso-8859-15");
		getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\"resultado_discente_" + ano + "_" + periodo + ".csv\"");
		
		out.println("ANO PERIODO;MATRICULA;CENTRO;PROFESSOR;DISCIPLINA;CODIGO DISCIPLINA;TURMA;HORARIO;LOCAL;CURSO;NOTA;SITUACAO MATRICULA;QUARTA PROVA;PORCENTAGEM FALTAS;1.1;1.2;1.3;1.4;1.5;1.6;1.7;1.8;1.9;2.1;2.2;2.3;2.4;2.5;2.6;2.7;3.1;3.2;3.3;3.4;3.5;3.6;3.7;4.1;4.2;4.3;4.4;4.5;4.6;4.7.1;4.7.2;4.7.3;4.7.4;4.7.5;4.7.6;4.7.7;4.7.8;4.7.9;4.8;OBSERVACOES;TRANCOU;QTD. TRANCADA;");
		
		int i = 0;
		
		TurmaDao turmaDao = getDAO(TurmaDao.class);
		
		for (Turma turma : turmaDao.findByAnoPeriodo(ano, periodo)) {
			cacheTurma.put(turma.getId(), turma);
		}
		
		for (ResultadoAvaliacao r : resDiscente) {
			for (ResultadoTurma t : r.getTurmas()) {

				out.print(StringUtils.toAscii(ano + "." + periodo) + ";");
				
				System.out.println(++i % 10 == 0 ? i : "");
				out.print(r.getDiscente().getMatricula() + ";");
				out.print(StringUtils.toAscii(r.getCentro()) + ";");
				out.print(StringUtils.toAscii(t.getDocente()) + ";");
				out.print(StringUtils.toAscii(t.getDisciplina()) + ";");
				out.print(StringUtils.toAscii(t.getCodigoDisciplina()) + ";");
				out.print(StringUtils.toAscii(t.getTurma()) + ";");
				out.print(StringUtils.toAscii(t.getHorario()) + ";");
				out.print(StringUtils.toAscii(t.getLocal().replaceAll(";", " ")) + ";");
				out.print(StringUtils.toAscii(r.getCurso()) + ";");
				
				out.print(t.getMediaFinal() + ";");
				out.print(StringUtils.toAscii(t.getSituacaoMatricula()) + ";");
				out.print(StringUtils.toAscii(t.getRecuperacao()) + ";");
				
				Turma turma = null;
				if (cacheTurma.containsKey(t.getIdTurma())) {
					turma = cacheTurma.get(t.getIdTurma());
					System.out.println("pegou turma no cache");
				} else {
					turma = dao.findByPrimaryKey(t.getIdTurma(), Turma.class);
					cacheTurma.put(t.getIdTurma(), turma);
				}
				
				out.print(StringUtils.toAscii(calcularFaltas(turma, t.getNumeroFaltas())) + ";");				
				
				for (ResultadoResposta d : t.getDados()) {
					out.print(StringUtils.toAscii(d.toString()) + ";");
				}
				
				out.print(StringUtils.removeLineBreakAndPV(StringUtils.toAscii(r.getComentarios())) + ";");
				
				if (r.getNumTrancamentos() > 0) {
					out.print("Sim;");
				} else {
					out.print(StringUtils.toAscii("Não;"));
				}
				
				out.print(r.getNumTrancamentos() + ";");
				
				out.println();				
			}
		}
		
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}
	
	/**
	 * Gerar resultados da avaliação docente.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/relatorios.jsp </li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws IOException
	 */
	public String resultadoDocente() throws DAOException, IOException {
		validacaoDados(erros.getMensagens());
		if (hasErrors())
			return null;
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		List<ResultadoAvaliacao> resDocente = dao.findResultadosDocentes(ano, periodo);
		if (resDocente == null || resDocente.size() == 0) {
			addMensagemWarning("Não há resultados para o ano/período informados");
			return null;
		}	
		PrintWriter out = getCurrentResponse().getWriter();
		
		getCurrentResponse().setContentType("text/csv");
		getCurrentResponse().setCharacterEncoding("iso-8859-15");
		getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\"resultado_docente_" + ano + "_" + periodo + ".csv\"");
		
		out.println("CENTRO;PROFESSOR;DEPARTAMENTO;CATEGORIA;DISCIPLINA;CODIGO DISCIPLINA;TURMA;HORARIO;LOCAL;1.1;1.2;1.3;1.4;1.5;1.6;1.7;1.8;1.9;2.1;2.2;2.3;2.4;3.1.1;3.1.2;3.1.3;3.1.4;3.1.5;3.1.6;3.2.1;3.2.2;3.2.3;3.2.4;3.2.5;3.2.6;3.2.7;3.3.1;3.3.2;3.3.3;3.3.4;3.3.5;3.3.6;3.3.7;4.1;4.2;4.3;4.4;4.5;4.6;OBSERVACOES;");
		
		for (ResultadoAvaliacao r : resDocente) {
			for (ResultadoTurma t : r.getTurmas()) {
				out.print(StringUtils.toAscii(r.getCentro()) + ";");
				out.print(StringUtils.toAscii(r.getNomeProfessor()) + ";");
				out.print(StringUtils.toAscii(r.getDepartamento()) + ";");
				out.print(StringUtils.toAscii(r.getCategoria()) + ";");
				out.print(StringUtils.toAscii(t.getDisciplina()) + ";");
				out.print(StringUtils.toAscii(t.getCodigoDisciplina()) + ";");
				out.print(StringUtils.toAscii(t.getTurma()) + ";");
				out.print(StringUtils.toAscii(t.getHorario()) + ";");
				out.print(StringUtils.toAscii(t.getLocal().replaceAll(";", " ")) + ";");
				for (ResultadoResposta d : t.getDados()) {
					out.print(StringUtils.toAscii(d.toString()) + ";");
				}
				out.print(StringUtils.removeLineBreak(StringUtils.toAscii(r.getComentarios())) + ";");
				out.println();				
			}
		}
		
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}

	/** Retorna um arquivo CSV com os comentários específicos para docente.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/relatorios.jsp </li>
	 * </ul>
	 * @return
	 * @throws IOException
	 * @throws DAOException
	 */
	public String comentariosRelativosDocente() throws IOException, DAOException {
		validacaoDados(erros.getMensagens());
		if (hasErrors())
			return null;
		PrintWriter out = getCurrentResponse().getWriter();
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		
		List<Map<String, Object>> comentarios = dao.findComentariosRelativosDocente(ano, periodo);
		if (comentarios == null || comentarios.size() == 0) {
			addMensagemWarning("Não há comentários para o ano/período informados");
			return null;
		}	
		getCurrentResponse().setContentType("text/csv");
		getCurrentResponse().setCharacterEncoding("iso-8859-15");
		getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\"comentarios_" + ano + "_" + periodo + ".csv\"");
		
		out.println("ANO-PERÍODO;CENTRO DO CURSO;CURSO;CENTRO DO COMPONENTE;DEPARTAMENTO DO COMPONENTE;DOCENTE;CÓDIGO;COMPONENTE;TURMA;HORÁRIO;LOCAL;MATRÍCULA;COMENTÁRIO;COMENTÁRIO MODERADO; COMENTÁRIO GERAL;");
		for (Map<String, Object> mapa : comentarios) {
			out.print("\"" + StringUtils.toAscii(ano + "."+ periodo) + "\";");
			out.print("\"" + StringUtils.toAscii((String) mapa.get("centro_curso")) + "\";");
			out.print("\"" + StringUtils.toAscii((String) mapa.get("curso")) + "\";");
			out.print("\"" + StringUtils.toAscii((String) mapa.get("centro_componente")) + "\";");
			out.print("\"" + StringUtils.toAscii((String) mapa.get("departamento_componente")) + "\";");
			out.print("\"" + StringUtils.toAscii((String) mapa.get("docente")) + "\";");
			out.print("\"" + StringUtils.toAscii((String) mapa.get("codigo")) + "\";");
			out.print("\"" + StringUtils.removeLineBreak(StringUtils.toAscii((String) mapa.get("componente"))) + "\";");
			out.print("\"" + StringUtils.toAscii((String) mapa.get("turma")) + "\";");
			out.print("\"" + StringUtils.toAscii((String) mapa.get("horario")) + "\";");
			out.print("\"" + StringUtils.toAscii((String) mapa.get("local")) + "\";");
			out.print(StringUtils.toAscii(((Long) mapa.get("matricula")).toString()) + ";");
			out.print("\"" + StringUtils.removeLineBreak(StringUtils.toAscii((String) mapa.get("observacoes"))) + "\";");
			out.print("\"" + StringUtils.removeLineBreak(StringUtils.toAscii((String) mapa.get("observacoes_moderadas"))) + "\";");
			out.print("\"" + StringUtils.removeLineBreak(StringUtils.toAscii((String) mapa.get("observacoes_gerais"))) + "\";");
			out.println();
		}
		
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}
	
	/**
	 * Calcula o número de faltas do aluno em %
	 * 
	 * @param turma
	 * @param numeroFaltas
	 * @return
	 * @throws DAOException
	 */
	private String calcularFaltas(Turma turma, int numeroFaltas) throws DAOException {

		CalendarioAcademico c ;
		if(cacheCalendarioTurma.containsKey(turma.getId())) {
			c = cacheCalendarioTurma.get(turma.getId());
		} else {		
			c = CalendarioAcademicoHelper.getCalendario(turma);
			cacheCalendarioTurma.put(turma.getId(), c);
		}
		
		Set<Date> aulas;
		if (cacheAulasTurma.containsKey(turma.getId())) {
			aulas = cacheAulasTurma.get(turma.getId());
		} else {
			aulas = TurmaUtil.getDatasAulasTruncate(turma, c);
			cacheAulasTurma.put(turma.getId(), aulas);
		}
		
		Double valor = new Double(numeroFaltas)*100/aulas.size();
		
		return Formatador.getInstance().formatarDecimal1(valor);
	}

	/** Valida os dados informados pelo usuário.
	 * Não invocado por JSPs
	 * @param mensagens
	 * @return
	 */
	public boolean validacaoDados(Collection<MensagemAviso> mensagens) {
		if (ano < 1900) {
			mensagens.add(new MensagemAviso("Informe um ano válido", TipoMensagemUFRN.ERROR));
		}
		if (periodo != 1 && periodo != 2) {
			mensagens.add(new MensagemAviso("Informe um período válido", TipoMensagemUFRN.ERROR));
		}
		return mensagens.size() == 0;
	}	
	
	/** Retorna o ano da avaliação.
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano da avaliação.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o período da avaliação.
	 * @return
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o período da avaliação.
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	
}
