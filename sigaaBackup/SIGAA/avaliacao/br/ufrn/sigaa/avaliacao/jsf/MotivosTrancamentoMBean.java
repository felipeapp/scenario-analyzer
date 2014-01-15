/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 27/03/2009
 */
package br.ufrn.sigaa.avaliacao.jsf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * Managed bean para gerar o relatório de motivos de trancamentos
 * da avaliação institucional.
 *  
 * @author David Pereira
 *
 */
@SuppressWarnings("serial")
@Component @Scope("request")
public class MotivosTrancamentoMBean extends AbstractController {

	/** Ano da avaliação. */
	private int ano;
	
	/** Período da avaliação. */
	private int periodo;
	
	/** Inicia a geração dos arquivos de resultado.
	 * Chamado por/administracao/menu_administracao.jsp
	 * @return
	 * @throws DAOException
	 */
	public String iniciar() throws DAOException {
		CalendarioAcademico c = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = c.getAno();
		periodo = c.getPeriodo();
		return forward("/avaliacao/trancamentos.jsp");
	}
	
	/** Gera o arquivo CSV com os motivos de trancamentos.
	 * Chamado por /avaliacao/trancamentos.jsp
	 * @return
	 * @throws IOException
	 */
	public String motivos() throws IOException {
		HttpServletResponse res = getCurrentResponse();
		PrintWriter out = res.getWriter();
		
		res.setContentType("text/csv");
		res.setCharacterEncoding("iso-8859-15");
		res.setHeader("Content-disposition", "attachment; filename=\"trancamentos_" + ano + "_" + periodo + ".csv\"");
		
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		
		List<Map<String, Object>> result = dao.findMotivosTrancamento(ano, periodo);
		
		out.print("ANO;PERIODO;MATRICULA;CENTRO;PROFESSOR(ES);DISCIPLINA;CODIGO DISCIPLINA;TURMA;HORARIO;LOCAL;CURSO;MOTIVOS;\n");
		
		for (Map<String, Object> linha : result) {
			out.print(linha.get("ano") + ";" + linha.get("periodo") + ";" + linha.get("matricula") + ";" 
					+ linha.get("centro") + ";" + linha.get("professores") + ";" + linha.get("disciplina") + ";" 
					+ linha.get("codigo") + ";" + linha.get("turma") + ";" + linha.get("horario") + ";" + linha.get("local") + ";" 
					+ linha.get("curso") + ";" + StringUtils.removeLineBreakAndPV(linha.get("motivo").toString()) + ";");
			out.println();
		}
		
		FacesContext.getCurrentInstance().responseComplete();
		return null;
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
