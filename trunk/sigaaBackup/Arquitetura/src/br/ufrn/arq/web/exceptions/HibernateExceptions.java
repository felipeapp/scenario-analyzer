/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 23/04/2009
 */
package br.ufrn.arq.web.exceptions;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.NonUniqueResultException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.exception.ConstraintViolationException;

import br.ufrn.arq.erros.DAOException;


/**
 * Classe para tratar as exceções causadas pelo Hibernate.
 * 
 * @author Gleydson Lima 
 * @author David Pereira
 *
 */
public class HibernateExceptions extends ExceptionChain {

	@Override
	public ExceptionHandlerResult processar(Throwable e, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		if (e instanceof StaleStateException || (e.getCause() != null && e.getCause() instanceof StaleStateException) || (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof StaleStateException)) {
			addMensagemErro("O elemento selecionado não se encontra mais no banco de dados.", req, res);
			return ExceptionHandlerResult.STOP;
		} 
		
		if (e instanceof StaleObjectStateException) {
			addMensagemErro("O elemento selecionado foi modificado por outra pessoa e não pode mais ser utilizado. Por favor, reinicie a operação.", req, res);
			return ExceptionHandlerResult.STOP;
		}
		
		if (e instanceof ConstraintViolationException || (e instanceof DAOException && e.getCause() != null && e.getCause() instanceof ConstraintViolationException)) {
			addMensagemErro("O elemento selecionado não pode ser removido porque ainda está sendo referenciado pelo sistema.", req, res);
			return ExceptionHandlerResult.STOP;
		}
		
		if (e instanceof NonUniqueResultException || (e instanceof DAOException && e.getCause() != null && e.getCause() instanceof NonUniqueResultException)) {
			String msg = "Caro usuário, apenas um resultado era esperado na operação realizada porém, o banco de dados retornou ";
			
			NonUniqueResultException nure = null;
			if (e instanceof NonUniqueResultException) {
				nure = (NonUniqueResultException) e;
			} else {
				nure = (NonUniqueResultException) e.getCause();
			}

			msg += nure.getMessage().split(":")[1] + ". Isto pode ser um problema de migração ou inconsistência de dados. Entre em contato com a informática através do Abrir Chamado.";
			addMensagemErro(msg, req, res);
			return ExceptionHandlerResult.STOP;
		}
		
		if (e instanceof SQLException || (e.getCause() != null && e.getCause() instanceof SQLException)) {
			String message = null;
			if (e instanceof SQLException ) {
				message = e.getMessage();
			} else {
				message = e.getCause().getMessage();
			}
			
			if (message.contains("does not exist")) {
				StackTraceElement[] stackTrace = e.getStackTrace();
				StackTraceElement ultimo = null;
				for (StackTraceElement element : stackTrace) {
					if (element.getClassName().startsWith("br.ufrn.")) {
						ultimo = element;
						break;
					}
				}
				
				if (message.contains("relation")) {
					String relation = message.replaceAll("does not exist", "").replaceAll("ERROR: relation", "");
					String msg = "ERRO: A tabela " + relation + " não existe. ";
					if (ultimo != null) msg += "Ver método " + ultimo.getMethodName() + " da classe " + ultimo.getClassName() + ".";
					addMensagemErro(msg, req, res);
					return ExceptionHandlerResult.STOP;
				} else if (message.contains("column")) {
					String column = message.replaceAll("does not exist", "").replaceAll("ERROR: column", "");
					String msg = "ERRO: A coluna " + column + "não existe. ";
					if (ultimo != null) msg += "Ver método " + ultimo.getMethodName() + " da classe " + ultimo.getClassName() + ".";
					addMensagemErro(msg, req, res);
					return ExceptionHandlerResult.STOP;
				}
			}
		}
		
		
		
		return ExceptionHandlerResult.CONTINUE;
	}

}
