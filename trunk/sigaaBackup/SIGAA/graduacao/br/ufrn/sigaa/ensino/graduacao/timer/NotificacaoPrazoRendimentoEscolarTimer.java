/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 06/04/2010
 *
 */	

package br.ufrn.sigaa.ensino.graduacao.timer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;



/*********************************************************************************************
 * Thread que roda periodicamente (uma vez por dia) verificando
 * se foi realizado a divulga��o de rendimento acad�mico e faltas 
 * relacionadas a uma unidade de um dado componente curricular.
 * 
 * A verifica��o � baseada nas datas fins de cada unidade, cadastrada pelo docente
 * na Turma Virtual (Configura��es -> Turma). � conferido se as notas e/ou faltas
 * de uma unidade foram colocadas no SIGAA at� 10 dias �teis ap�s a data fim de
 * uma determinada unidade. Caso a unidade seja de a terceira o prazo cai para 3 dias �teis. 
 *
 * Ser� enviado e-mail para os docentes vinculados ao componente curricular, bem
 * como para o Chefe do Departamento ao qual o docente � vinculado.
 * o envio � di�rio at� que a situa��o seja regularizada.
 * 
 * @author Tiago Monteiro
 * 
 ***********************************************************************************************/


public class NotificacaoPrazoRendimentoEscolarTimer extends TarefaTimer {

	// constantes que definem as chaves da tabela hash utilizada para agrupar as informa��es
	/** Define a chave email para tabela hash. */
	private static final String EMAIL_KEY = "email";
	/** Define a chave de turmas para tabela hash. */
	private static final String LINHA_TABELA_KEY = "linhaTurmas";
	/** Define a chave para mais de um docente para tabela hash. */
	private static final String PLURAL_KEY = "maisDeUmDocente";

	/**
	 * Executa o Timer
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			notificarPrazoRendimentoEscolar();
		} catch (Exception e) {
//			error(e);
		}
	}
	
	/**
	 * Busca as turmas e docentes que n�o informaram o rendimento escolar no prazo estipulado.
	 * @throws DAOException
	 */
	private void notificarPrazoRendimentoEscolar() throws DAOException{
			
		ServidorDao servidorDao = DAOFactory.getInstance().getDAO(ServidorDao.class);	
		
		try {
			List<Map<String, Object>> unidades = servidorDao.findPrazoMaximoEstouradoRendimentoEscolar();
			Map<String, Map<String, String>> unidadePorDocente = agrupaPorDocente(unidades);
			Map<String, Map<String, String>> unidadePorChefes = agrupaPorChefeDepartamento(unidades);
			notificaDocentes(unidadePorDocente);
			notificaChefes(unidadePorChefes);
		} catch (Exception e) {
			// Se der algum erro nesta rotina manda email pra administra��o pra notificar do erro. 
//			error(e);

		}
	}

	/** Notifica, via e-mail, os docentes que o prazo para informar os rendimentos escolares foi extrapolado.
	 * @param unidadePorDocente
	 * @throws DAOException
	 */
	private void notificaDocentes(Map<String, Map<String, String>> unidadePorDocente) throws DAOException {
		if (unidadePorDocente == null)
			return;
		for (String nomeDocente : unidadePorDocente.keySet()) {
			//Mensagem de envio para os Docentes com pend�ncias
			StringBuilder msgDocentes = new StringBuilder( 												
				"Caro(a) "+nomeDocente+", <br><br><br>\n"+
				" O prazo m�ximo para divulga��o do rendimento escolar referente ao(s) componente(s) abaixo listado(s)" +
				" foi utrapassado de acordo com o Artigo 99 do Regulamento dos Cursos de Gradua��o (Resolu��o n� 227/2009 do CONSEPE), descrito mais abaixo." +
				"<br><br>\n"+
				"<table border=\"0\">" +
				"<thead>" +
				"<tr>" +
				"<td style=\"text-align: left;\"><b>Componente Curricular</b></td>" +
				"<td style=\"text-align: right;\"><b>Turma</b></td>" +
				"<td style=\"text-align: right;\"><b>Unidade</b></td>" +
				"</tr>\n" +
				"</thead>" +
				"<tbody>");
			msgDocentes.append(unidadePorDocente.get(nomeDocente).get(LINHA_TABELA_KEY));
			msgDocentes.append("</tbody></table>"+
				"<br><br>" +
				" As faltas dos discentes al�m das notas referente a(s) unidade(s) devem ser devidamente cadastradas no sistema de registro e controle acad�mico. " +
				"<br><br>"+
				" Por favor, regularize a situa��o de forma que estas notifica��es terminem."+
				"<br> "+ montaMsgArtigo());
			
			//Envia email para docentes
			String assunto= "NOTIFICA��O DE DIVULGA��O DE RENDIMENTO ESCOLAR";
			MailBody mail = new MailBody();
			mail.setEmail(unidadePorDocente.get(nomeDocente).get(EMAIL_KEY));
			mail.setAssunto(assunto);
			mail.setMensagem(msgDocentes.toString());				
			Mail.send(mail);
		}
	}
	
	/** Notifica, via e-mail, que h� docentes do departamento com o prazo para informar os rendimentos escolares extrapolado.
	 * @param unidadePorChefe
	 * @throws DAOException
	 */
	private void notificaChefes(Map<String, Map<String, String>> unidadePorChefe) throws DAOException {
		if (unidadePorChefe == null)
			return;
		for (String nomeChefe : unidadePorChefe.keySet()) {
			boolean plural = Boolean.parseBoolean(unidadePorChefe.get(nomeChefe).get(PLURAL_KEY));
			//Mensagem de envio para os Chefes de Departamento vinculados aos Docentes com pend�ncias
			StringBuilder msgChefes = new StringBuilder( 											
				"Caro(a) "+nomeChefe+", <br><br>\n"+
				(plural ? "Os seguintes docentes extrapolaram":"O seguinte docente extrapolou") +
				" o prazo m�ximo de divulga��o do rendimento escolar utrapassado de acordo com o Artigo 99" +
				" do Regulamento dos Cursos de Gradua��o (Resolu��o n� 227/2009 - CONSEPE), descrito mais abaixo:" +
				"<br><br>"+
				"<table border=\"0\">\n" +
				"<thead>\n" +
				"<tr>" +
				"<td style=\"text-align: left;\"><b>Departamento</b></td>" +
				"<td style=\"text-align: left;\"><b>Docente</b></td>" +
				"<td style=\"text-align: left;\"><b>Componente Curricular</b></td>" +
				"<td style=\"text-align: right;\"><b>Turma</b></td>" +
				"<td style=\"text-align: right;\"><b>Unidade</b></td>" +
				"</tr>\n" +
				"</thead>\n" +
				"<tbody>\n");
			msgChefes.append(unidadePorChefe.get(nomeChefe).get(LINHA_TABELA_KEY));
			msgChefes.append("</tbody></table>\n" + "<br><br>");
			msgChefes.append("As  faltas dos discentes al�m das notas referente �(s) unidade(s) " +
		    " devem ser devidamente cadastradas no sistema de registro e controle acad�mico. " +
			"<br><br>"+
			" Por favor, solicite "+(plural?"aos referidos docentes":"ao referido docente")+" que regularize a situa��o de forma que estas notifica��es terminem."+
			"<br> "+ montaMsgArtigo());
			
			//Envia email para docentes
			String assunto= "NOTIFICA��O DE DIVULGA��O DE RENDIMENTO ESCOLAR";
			MailBody mail = new MailBody();
			mail.setEmail(unidadePorChefe.get(nomeChefe).get(EMAIL_KEY));
			mail.setAssunto(assunto);
			mail.setMensagem(msgChefes.toString());				
			Mail.send(mail);
		}
	}
	
	/**
	 * Agrupa o resultado da consulta de docentes de gradua��o que tem pend�ncias na divulga��o do rendimento
	 * escolar, por docente.
	 * 
	 * @param responsaveis
	 * @return
	 */
	private Map<String, Map<String, String>> agrupaPorDocente(List<Map<String, Object>> responsaveis) {
		Map<String, Map<String, String>> grupo = new HashMap<String, Map<String,String>>();
		Set<String> distintos = new HashSet<String>();
		for (Map<String, Object> responsavel : responsaveis){

			String nomeDocente = (String) responsavel.get("docente_nome");
			String emailDocente = (String) responsavel.get("docente_email");
			String codigoComponente = (String) responsavel.get("codigo");
			String nomeComponente = (String) responsavel.get("componente");
			String turmaComponente = (String) responsavel.get("turma");
			Short unidade = (Short) responsavel.get("unidade");
		
			// agrupa
			Map<String, String> grupoDocente = grupo.get(nomeDocente);
			if (grupoDocente == null) grupoDocente = new HashMap<String, String>();
			String linhaTurmas = grupoDocente.get(LINHA_TABELA_KEY) == null ? "" : grupoDocente.get(LINHA_TABELA_KEY);

				if ( !distintos.contains( nomeDocente + codigoComponente + unidade ) ) {
					distintos.add( nomeDocente + codigoComponente + unidade );
					linhaTurmas += "<tr>" +
							"<td style=\"text-align: left;\">"+codigoComponente+" - "+nomeComponente+"</td>" +
							"<td style=\"text-align: right;\">"+turmaComponente+"</td>" +
							"<td style=\"text-align: right;\">"+(unidade == null ? "N�O CADASTRADA" : unidade)+"</td>" +
							"</tr>\n";

					
					grupoDocente.put(LINHA_TABELA_KEY, linhaTurmas);
					grupoDocente.put(EMAIL_KEY, emailDocente);
					grupo.put(nomeDocente, grupoDocente);
				}
		}
		return grupo;
	}

	/**
	 * Agrupa o resultado da consulta de docentes de gradua��o que tem pend�ncias na divulga��o do rendimento
	 * escolar, por chefe de departamento.
	 * 
	 * @param responsaveis
	 * @return
	 */
	private Map<String, Map<String, String>> agrupaPorChefeDepartamento(List<Map<String, Object>> responsaveis) {
		Map<String, Map<String, String>> grupo = new HashMap<String, Map<String,String>>();
		for (Map<String, Object> responsavel : responsaveis){
			String nomeDepartamento = (String) responsavel.get("departamento");
			String nomeChefe = (String) responsavel.get("chefe_nome");
			String emailChefe = (String) responsavel.get("chefe_email");				
			String nomeDocente = (String) responsavel.get("docente_nome");
			String codigoComponente = (String) responsavel.get("codigo");
			String nomeComponente = (String) responsavel.get("componente");
			String turmaComponente = (String) responsavel.get("turma");
			Short unidade = (Short) responsavel.get("unidade");
			// agrupa
			Map<String, String> grupoChefeDepartamento = grupo.get(nomeChefe);
			if (grupoChefeDepartamento == null) grupoChefeDepartamento = new HashMap<String, String>();
			String linhaTurmas = grupoChefeDepartamento.get(LINHA_TABELA_KEY) == null ? "" : grupoChefeDepartamento.get(LINHA_TABELA_KEY);
			// verifica se h� mais de um docente nesta situa��o
			boolean plural = grupoChefeDepartamento.get(PLURAL_KEY) == null ? false : Boolean.parseBoolean(grupoChefeDepartamento.get(PLURAL_KEY));
			grupoChefeDepartamento.put(PLURAL_KEY, String.valueOf(plural || linhaTurmas.length() > 0 && linhaTurmas.indexOf(nomeDocente) < 0));
			// cria a linha da tabela a ser listada no e-mail
			linhaTurmas += "<tr>" +
					"<td style=\"text-align: left;\">"+nomeDepartamento+"</td>" +
					"<td style=\"text-align: left;\">"+nomeDocente+"</td>" +
					"<td style=\"text-align: left;\">"+codigoComponente+" - "+nomeComponente+"</td>" +
					"<td style=\"text-align: right;\">"+turmaComponente+"</td>" +
					"<td style=\"text-align: right;\">"+(unidade == null ? "N�O CADASTRADA" : unidade)+"</td>" +
					"</tr>\n";
			grupoChefeDepartamento.put(LINHA_TABELA_KEY, linhaTurmas);
			grupoChefeDepartamento.put(EMAIL_KEY, emailChefe);
			grupo.put(nomeChefe, grupoChefeDepartamento);
		}
		return grupo;
	}

	
	/**
	 * M�todo que adiciona o texto do artigo 99 do Regulamento dos Cursos Regulares de Gradua��o, 
	 * Resolu��o N� 227/2009-CONSEPE, de 3 de dezembro de 2009 aos emails enviados aos Docentes
	 * e Chefes de Departamento.
	*/
	private String montaMsgArtigo(){
		//
		String msgArtigo = 
			"<br>" +
			" Art. 99. O rendimento escolar de cada unidade � calculado a partir dos resultados obtidos nas avalia��es da" +
			" aprendizagem realizadas na unidade, c�lculo este definido previamente pelo professor e divulgado no programa da disciplina." +
			" <br><br>" +
			" � 1o A divulga��o do rendimento escolar deve ser obrigatoriamente feita atrav�s do sistema de registro e controle acad�mico." +
			" <br>" +
			" � 2o � obrigat�ria a divulga��o do rendimento escolar da unidade, pelo professor da disciplina, no prazo m�ximo de 10 (dez)" +
			" dias �teis, contado este prazo a partir da realiza��o da �ltima avalia��o da unidade, ressalvados os limites de datas do" +
			" Calend�rio Universit�rio. No caso de ser a �ltima unidade, o prazo fica reduzido para 03 (tr�s) dias �teis." +
			" <br>" +
			" � 3o N�o deve ser realizada nenhuma avalia��o relativa a uma determinada unidade, sem que o rendimento escolar" +
			" da unidade anterior tenha sido divulgado pelo professor, sob pena da referida avalia��o ser anulada." +
			" <br>" +
			" � 4o O pedido de anula��o dever� ser protocolado, por qualquer aluno da turma, no Departamento ou Unidade Acad�mica Especializada," +
			" no prazo m�ximo de at� 03 (tr�s) dias �teis ap�s a realiza��o da avalia��o objeto da anula��o." +
			" <br>" +
			" � 5o Constatada a n�o divulga��o dos resultados da unidade anterior, o chefe de Departamento ou diretor da Unidade Acad�mica Especializada" +
			" dever� anular a avalia��o e determinar a publica��o dos resultados da unidade anterior no prazo m�ximo de 03 (tr�s) dias �teis."+
			" <br>" +
			" � 6o No ato da divulga��o do rendimento escolar de uma unidade, o professor j� deve ter registrado no sistema de" +
			" registro e controle acad�mico as faltas do aluno naquela unidade. "+
			"<br><br>"+
			" ESTA MENSAGEM FOI GERADA AUTOMATICAMENTE PELO SISTEMA E N�O DEVE SER RESPONDIDA. "+
			"<br>";
		return msgArtigo;
	}
}


