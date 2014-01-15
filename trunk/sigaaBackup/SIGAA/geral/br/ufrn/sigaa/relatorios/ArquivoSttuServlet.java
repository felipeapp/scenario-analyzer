/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on '02/10/2007'
 *
 */
package br.ufrn.sigaa.relatorios;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dao.CarteiraEstudanteDao;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * Servlet para gerar o arquivo no formato requerido pela STTU para importa��o.
 *
 * @author leonardo
 *
 */
public class ArquivoSttuServlet extends HttpServlet {

	/**
	 * Repassa as requisi��es oriundas via Get para o m�todo processRequest.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}

	/**
	 * Repassa as requisi��es oriundas via Post para o m�todo processRequest.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}

	/**
	 * M�todo para tratar todas as requisi��es, vindas tanto via m�todo GET como via m�todo POST
	 * @param req
	 * @param resp
	 * @throws ServletException
	 */
	private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		UsuarioGeral user = (UsuarioGeral) req.getSession(false).getAttribute("usuario");
		if (user == null) {
			throw new ServletException("Usu�rio n�o logado");
		}
		if (!user.isUserInRole(SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.GESTOR_LATO, SigaaPapeis.DAE, SigaaPapeis.PPG)) {
			throw new ServletException("Usu�rio n�o autorizado a realizar a opera��o");
		}

		char nivel = req.getParameter("nivel") != null ? req.getParameter("nivel").charAt(0) : '0';
		String log = req.getParameter("log");
		String consulta = req.getParameter("consulta");
		int idUnidade = 0;
		String unidade = req.getParameter("unidade");
		if(unidade != null)
			idUnidade = Integer.parseInt(unidade);
		
		if(log == null)
			gerarArquivoLista(req, resp, nivel, idUnidade, consulta);
		else
			gerarArquivoLog(req, resp, nivel, idUnidade, consulta);		
	}

	/**
	 * Gera o arquivo de log com os problemas detectados nos dados dos alunos
	 * @param req
	 * @param res
	 * @param nivel
	 */
	private void gerarArquivoLog(HttpServletRequest req, HttpServletResponse res, char nivel, int idUnidade, String consulta) {
		CarteiraEstudanteDao dao = null;
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		StringBuffer linha = new StringBuffer();
		linha.append(" Problemas detectados na listagem dos alunos para STTU ");
		switch (nivel) {
		case 'T':
			linha.append(" - Ensino T�cnico ");
			break;
		case 'G':
			linha.append(" - Ensino Gradua��o ");
			break;
		case 'L':
			linha.append(" - Ensino Lato Sensu ");
			break;
		case 'S':
			linha.append(" - Ensino Stricto Sensu ");
			break;
		case 'R':
			linha.append(" - M�dicos Residentes ");
			break;
		}
		linha.append(System.getProperty("line.separator"));
		linha.append(" Matr�cula ;");
		linha.append(" Nome ;");
		linha.append(" Curso/Turno/Cidade ;");
		linha.append(" Problema(s) ;");
		
		List<Map<String, Object>> list = null;
		try {
			
			
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
			
//			if(consulta == null){
				dao = new CarteiraEstudanteDao();
				list = dao.findInfoEstudantes(nivel, cal.getAno(), cal.getPeriodo(), idUnidade, consulta);
//			}else{
//				DataSource ds = Database.getInstance().getSigaaDs();
//				JdbcTemplate template = new JdbcTemplate(ds);
//				list = template.queryForList(consulta);
//			}

			res.setContentType("application/csv");
			res.setHeader("Content-Disposition", "inline; filename=log_sttu_"+nivel+"_"+sdf.format(new Date())+".csv");
			
			PrintWriter out = res.getWriter();
			out.println(linha.toString());

			String problemas = null;
			for(Map<String, Object> l: list){
				problemas = verificarProblemas(l);
				if(problemas != null && !(problemas.length() == 0)){
					linha = new StringBuffer();
					linha.append(l.get("matricula"));
					linha.append(" ; ");
					linha.append(l.get("nome_ascii"));
					linha.append(" ; ");
					linha.append(l.get("curso") != null ? l.get("curso")+" - "+ (l.get("sigla") != null ? l.get("sigla") +" - " : "") + l.get("cidade_curso") : "Aluno Especial - "+ l.get("unidade"));
					linha.append(" ; ");
					linha.append(problemas);
					linha.append(" ;");
					
					out.println(linha.toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(dao != null)	dao.close();
		}
	}


	/**
	 * M�todo auxiliar para verificar certos problemas no registro recuperado do banco
	 * @param linha - Uma linha recuperada do banco
	 * @return
	 */
	private String verificarProblemas(Map<String, Object> linha) {
		StringBuffer problemas = new StringBuffer();
		
		String nomeMae = (String) linha.get("nome_mae");
		Date data = (Date) linha.get("data_nascimento");
		String rg = (String) linha.get("numero_identidade");
		Long cpf = (Long) linha.get("cpf");
		
		if(nomeMae == null || nomeMae.length() == 0){
			problemas.append(", Nome da m�e em branco");
		}else if(nomeMae.equalsIgnoreCase("nao informado") || nomeMae.equalsIgnoreCase("n�o informado")){
			problemas.append(", Nome da m�e inv�lido");
		}
		
		if(data == null || verficarErros(data)){
			problemas.append(", Data de nascimento inv�lida");
		}
		
		if(rg == null || rg.length() == 0){
			problemas.append(", RG em branco");
		}else if(soNumeros(rg).length() == 0){
			problemas.append(", RG inv�lido");
		}
		
		if(cpf == null) {
		    problemas.append(", CPF n�o informado");
		}else if(!ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpf)) {
		    problemas.append(", CPF inv�lido");
		}
		
		return problemas.toString().replaceFirst(", ", "");
	}

	/**
	 * M�todo auxiliar para verificar erros numa data
	 * @param data
	 * @return
	 */
	private boolean verficarErros(Date data) {
		if(data != null){
			if(!data.before(new Date()))
				return true;
			Calendar c = Calendar.getInstance();
			c.setTime(data);
			int ano = c.get(Calendar.YEAR);
			c.setTime(new Date());
			if(c.get(Calendar.YEAR) - ano > 100)
				return true;
			return false;
		}
		return true;
	}

	/**
	 * Gera a listagem dos alunos no formato requerido para importa��o pelo programa da STTU.
	 * @param req
	 * @param res
	 * @param nivel
	 * @param idUnidade
	 * @param consulta
	 */
	private void gerarArquivoLista(HttpServletRequest req, HttpServletResponse res, char nivel, int idUnidade,  String consulta) {
		CarteiraEstudanteDao dao = null;
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

		// numero sequencial dos registros
		int seq = 1;

		// tipos de registros
		final String TIPO_HEADER = "0";
		final String TIPO_DETALHES = "1";
		final String TIPO_THRILLER = "9";
		
		// registros
		String header;
		StringBuffer detalhes;
		String thriller;

		final String DATA_GERACAO_ARQUIVO = sdf.format(new Date());
		
		// dados espec�ficos da UFRN
		final String CGC_UFRN = "24365710000183";
		final String NOME_UFRN = "UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE - UFRN";
		final int CODIGO_UFRN = 348;
		
		// dados de enfermagem
		final String CGC_ENFERMAGEM = "24365710000183";
		final String NOME_ENFERMAGEM = "ESCOLA DE ENFERMAGEM DE NATAL";
		final int CODIGO_ENFERMAGEM = 594;

		// dados de m�sica
		final String CGC_MUSICA = "24365710000183";
		final String NOME_MUSICA = "ESCOLA DE MUSICA DA UFRN";
		final int CODIGO_MUSICA = 636;
		
		//monta o primeiro registro do arquivo
		if(idUnidade == 205)
			header = TIPO_HEADER + DATA_GERACAO_ARQUIVO + CGC_ENFERMAGEM + NOME_ENFERMAGEM + completaEspacos(null, 357, false) + completaEspacos(seq++, 10, true);
		else if(idUnidade == 284)
			header = TIPO_HEADER + DATA_GERACAO_ARQUIVO + CGC_MUSICA + NOME_MUSICA + completaEspacos(null, 362, false) + completaEspacos(seq++, 10, true);
		else
			header = TIPO_HEADER + DATA_GERACAO_ARQUIVO + CGC_UFRN + NOME_UFRN + completaEspacos(null, 336, false) + completaEspacos(seq++, 10, true);

		String codigos = "";
		switch(nivel){
		case 'T':
			codigos = "07398";
			break;
		case 'L':
			codigos = "05499";
			break;
		case 'S':
			codigos = "05499";
			break;
		case 'R':
			codigos = "05499";
			break;
		case 'G':
			codigos = "05498";
		}

		List<Map<String, Object>> list = null;
		try {
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
//			if(consulta == null){
				dao = new CarteiraEstudanteDao();
				list = dao.findInfoEstudantes(nivel, cal.getAno(), cal.getPeriodo(), idUnidade, consulta);
//			}
//			else{
//				DataSource ds = Database.getInstance().getSigaaDs();
//				JdbcTemplate template = new JdbcTemplate(ds);
//				list = template.queryForList(consulta);
//			}

			res.setContentType("text/plain");
			res.setHeader("Content-Disposition", "inline; filename=lista_sttu_"+nivel+"_"+sdf.format(new Date())+".txt");
			
			PrintWriter out = res.getWriter();
			out.println(header);

			for(Map<String, Object> l: list){
				detalhes = new StringBuffer();
				detalhes.append(TIPO_DETALHES);
				//nome
				detalhes.append(completaEspacos(tratarNome((String) l.get("nome_ascii")), 50, false));
				//matricula
				detalhes.append(completaEspacos(l.get("matricula"), 20, false));
				//data de nascimento
				Date data = (Date) l.get("data_nascimento");
				detalhes.append( completaEspacos(data != null ? sdf.format(data) : "", 8, false) );
				//endere�o
				detalhes.append(completaEspacos(l.get("endereco"), 50, false));
				//numero
				String numero = (String) l.get("numero");
				if(numero != null) numero = numero.substring(numero.length() - 5);
				detalhes.append(completaEspacos(StringUtils.extractInteger(numero), 5, true));
				//complemento
				detalhes.append(completaEspacos(l.get("complemento"), 20, false));
				//bairro
				detalhes.append(completaEspacos(l.get("bairro"), 30, false));
				//cidade
				detalhes.append(completaEspacos(l.get("cidade"), 30, false));
				//cep
				detalhes.append(completaEspacos(soNumeros((String) l.get("cep")), 8, true));
				//telefone
				String fixo = (String) l.get("telefone_fixo");
//				String celular = (String) l.get("telefone_celular");
				detalhes.append(completaEspacos(soNumeros(fixo), 20, false));
				//rg
				detalhes.append(completaEspacos(soNumeros((String) l.get("numero_identidade")), 10, false));
				//email
				detalhes.append(completaEspacos(l.get("email"), 50, false));
				//nome da m�e
				detalhes.append(completaEspacos(tratarNome((String) l.get("nome_mae")), 50, false));
				//c�digos
				detalhes.append(codigos);
				//SeriePeriodo_outros
				detalhes.append(completaEspacos("---",23, true));
				//turno
				String turno = (String) l.get("sigla");
				if(turno == null || turno.equalsIgnoreCase("I")) turno = "MTN";
				detalhes.append(completaEspacos(turno, 3, false));
				//c�digo da escola
				if(idUnidade == 205)
					detalhes.append(completaEspacos(CODIGO_ENFERMAGEM, 6, true));
				else if(idUnidade == 284)
					detalhes.append(completaEspacos(CODIGO_MUSICA, 6, true));
				else
					detalhes.append(completaEspacos(CODIGO_UFRN, 6, true));
				//RU do aluno (em branco, pois n�o h� essa informa��o na base)
				detalhes.append(completaEspacos(l.get("ru"), 10, false));
				//CPF (somente para estrangeiros, no caso de null ou inv�lido, define um cpf v�lido "gen�rico"
				Boolean internacional = (Boolean) l.get("internacional");
			    if(internacional != null && internacional && l.get("cpf") == null)
			        detalhes.append("22222222222");
			    else if(internacional != null && internacional && !ValidadorCPFCNPJ.getInstance().validaCpfCNPJ((Long) l.get("cpf")))
			        detalhes.append("22222222222");
			    else
                    detalhes.append(completaZeros(l.get("cpf"), 11, true));
				//n�mero sequencial do registro 
				detalhes.append(completaEspacos(seq++, 9, true));

				out.println(detalhes.toString());
			}

			// monta o ultimo registro do arquivo
			thriller = TIPO_THRILLER + completaEspacos(null, 408, false) + completaEspacos(seq, 10, true);

			out.println(thriller);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(dao != null) dao.close();
		}
	}


	/**
	 * m�todo para completar com espa�os em branco uma string
	 * @param valor
	 * @param totalEspacos
	 * @param brancoAntes
	 * @return
	 */
	private String completaEspacos(Object valor, int totalEspacos, boolean brancoAntes) {
		int size = 0;
		String informacao = "";
		if(valor != null){
			informacao = valor.toString();
			size = informacao.length();
			if(size > totalEspacos) informacao = informacao.substring(0, totalEspacos);
		}
		String branco = "";
		for (int a = 0; a < totalEspacos - size; a++) {
			branco += " ";
		}
		return brancoAntes ? branco+informacao : informacao+branco;
	}
	
	/**
	 * m�todo para completar com zeros uma string
	 * @param valor
	 * @param tamanhoTotal
	 * @param zeroAntes
	 * @return
	 */
	private String completaZeros(Object valor, int tamanhoTotal, boolean zeroAntes) {
	    int size = 0;
	    String informacao = "";
	    if(valor != null){
	        informacao = valor.toString();
	        size = informacao.length();
	        if(size > tamanhoTotal) informacao = informacao.substring(0, tamanhoTotal);
	    }
	    String zeros = "";
	    for (int a = 0; a < tamanhoTotal - size; a++) {
	        zeros += "0";
	    }
	    return zeroAntes ? zeros+informacao : informacao+zeros;
	}

	/**
	 * m�todo para retirar caracteres n�o num�ricos de uma string
	 * @param oldString
	 * @return
	 */
	private String soNumeros(String oldString){
		if(oldString != null){
			oldString = oldString.replaceAll("\\D", "");
			return oldString.trim().equals("")? "0" : oldString;
		} else
			return "0";
	}

	/**
	 * m�todo para retirar abrevia��es com ou sem ponto (.) e ap�strofo (D'ARC)
	 * @param oldString
	 * @return
	 */
	private String tratarNome(String oldString){
		if(oldString != null){
			if(oldString.length() > 50)
				oldString = oldString.substring(0, 50);
			oldString = " " + oldString + " ";
			oldString = oldString.replaceAll("[�']", "");
			oldString = oldString.replaceAll("[.]", " ");
			oldString = oldString.replaceAll("\\s\\S\\s" , " ");
			oldString = oldString.replaceAll("\\s\\S\\s" , " ");
			oldString = oldString.replaceAll("\\s\\S\\s" , " ");
			return oldString.trim();
		} else
			return "";
	}

}
