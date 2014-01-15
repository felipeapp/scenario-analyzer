/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 03/01/2008 
 *
 */
package br.ufrn.sigaa.processamento.dominio;

import java.io.FileWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.texen.util.FileUtil;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;

/**
 * Classe para gerar arquivos com os relatórios do resultado
 * de processamento de matrícula para cada turma.
 * 
 * @author David Pereira
 *
 */
public class ResultadoProcessamento {

	public static String caminho;
	
	public static String caminhoCompleto;
	
	/**
	 * Cria o relatório de processamento de matrícula (ou rematrícula) 
	 * para a turma passada como parâmetro.
	 * @param turma
	 * @param rematricula
	 * @throws Exception
	 */
	public static void criar(TurmaProcessada turma, boolean rematricula, String servletContextPath) throws Exception {
		System.out.println("Gerando: " + getCaminhoCompleto() + "/" + turma.getId() + ".html");
		FileWriter writer = new FileWriter(FileUtil.file(getCaminhoCompleto() + "/" + turma.getId() + ".html"));

		VelocityEngine ve = new VelocityEngine();
		ve.setProperty("file.resource.loader.path", servletContextPath + "/processamento/processamento");
		ve.init();

		VelocityContext context = new VelocityContext();
		context.put("cc", turma.getTurma().getDisciplina().getDetalhes().getCodigo() + " - " + turma.getTurma().getDisciplina().getDetalhes().getNome());
		context.put("turma", turma.getTurma().getCodigo());
		context.put("horario", turma.getTurma().getDescricaoHorario() == null ? "" : turma.getTurma().getDescricaoHorario());
		context.put("capacidade", turma.getTurma().getCapacidadeAluno());
		context.put("ocupacaoMatricula", turma.getTurma().getTotalMatriculados());
		context.put("totalDesistencias", turma.getTurma().getTotalDesistencias());
		context.put("reservas", turma.getReservas().values());
		context.put("docentes", turma.getDocentes());
		context.put("matrizes", turma.getMatriculas());
		context.put("desistencias", turma.getDesistencias());
		
		System.out.println("Template: " + getNomeArquivoTemplate(turma, rematricula));
		Template t = ve.getTemplate(getNomeArquivoTemplate(turma, rematricula));
		t.setEncoding("ISO-8859-1");
		t.merge(context, writer);
		
		writer.flush();
		writer.close();
	}

	private static String getNomeArquivoTemplate(TurmaProcessada turma, boolean rematricula) {
		String nomeArquivo = "resultado";
		if (turma.getTurma().isTurmaFerias()) {
			nomeArquivo += "_ferias";
		} else {
			nomeArquivo += (rematricula ? "_rematricula" : "");
		}
		
		return nomeArquivo + ".vm";
		
	}	
	
	/**
	 * Retorna o caminho do diretório para armazenamento dos relatórios gerados.
	 */
	public static String getCaminho() throws DAOException {
		if (caminho == null)
			caminho = ParametroHelper.getInstance().getParametro(ConstantesParametro.CAMINHO_RESULTADO_PROCESSAMENTO);
		return caminho;
	}
	
	/**
	 *  Retorna o caminho completo (com ano e período do processamento) do 
	 *  diretório para armazenamento dos relatórios gerados.
	 */
	public static String getCaminhoCompleto() throws DAOException {
		if (caminhoCompleto == null)
			caminhoCompleto = ParametroHelper.getInstance().getParametro(ConstantesParametro.CAMINHO_COMPLETO_RESULTADO_PROCESSAMENTO);
		return caminhoCompleto;
	}
	
}
