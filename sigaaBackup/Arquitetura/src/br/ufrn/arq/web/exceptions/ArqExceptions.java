/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 23/04/2009
 */
package br.ufrn.arq.web.exceptions;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.JasperException;

import br.ufrn.arq.dao.PermissaoDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.erros.TokenNaoValidadoException;
import br.ufrn.arq.web.conversation.ConversationNotActiveException;
import br.ufrn.comum.dominio.Papel;

/**
 * Classe para tratar as exceções da arquitetura.
 * 
 * @author David Pereira
 *
 */
public class ArqExceptions extends ExceptionChain {

	@Override
	public ExceptionHandlerResult processar(Throwable e, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		Boolean device =  (Boolean) req.getSession().getAttribute("device");

		/*
		 * Erro de solicitação já processada 
		 */
		if (e != null && e instanceof ArqException && ((ArqException) e).getCodErro() == ConstantesErro.SOLICITACAO_JA_PROCESSADA) {
			if(device == null) {
				req.getSession().setAttribute("alertErro", "O procedimento que voc\\352 tentou realizar j\\341 foi processado anteriormente. Para realiz\\341-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
				redirectSubSistema(req, res);
			}
			
			return ExceptionHandlerResult.STOP;
		}
		
		if (e != null && e instanceof ArqException && "Usuário não logado".equals(((ArqException) e).getMessage())) {
			if(device == null)
				res.sendRedirect("/sigaa/expirada.jsp");
			
			return ExceptionHandlerResult.STOP;
		}
		
		if (e != null && e instanceof ConversationNotActiveException) {
			if(device == null) {
				req.getSession().setAttribute("alertErro", e.getMessage());
				redirectSubSistema(req, res);
			}
			
			return ExceptionHandlerResult.STOP;
		}
		
		if (e != null && (e instanceof SegurancaException)) {
			SegurancaException se = (SegurancaException) e;
			PermissaoDAO dao = new PermissaoDAO();
			if (!isEmpty(se.getPapeis()) && device == null) {
				List<Papel> papeis = dao.findPapeis(se.getPapeis());
				
				if ( papeis != null )
					req.setAttribute("papeis", papeis);
			}
			
			if(device == null)
				req.getRequestDispatcher("/WEB-INF/jsp/include/erros/autorizacao.jsp").forward(req, res);
			
			return ExceptionHandlerResult.STOP;
		} 
		
		if (e != null && (e instanceof TokenNaoValidadoException)) {
			if(device == null)
				req.getRequestDispatcher("/WEB-INF/jsp/include/erros/autorizacao.jsp").forward(req, res);
			
			return ExceptionHandlerResult.STOP;
		} 
		
		if (e != null && (e instanceof NegocioException || e instanceof RuntimeNegocioException || "NegocioRemotoException".equals(e.getClass().getSimpleName()))) {
			if(device == null) {
				req.setAttribute("erro", e);
				req.getRequestDispatcher("/WEB-INF/jsp/include/erros/negocio.jsp").forward(req, res);
			}
			
			return ExceptionHandlerResult.STOP;
		}
		
		if (e != null && e.getCause() != null && (e.getCause() instanceof NegocioException || e.getCause() instanceof RuntimeNegocioException || "NegocioRemotoException".equals(e.getCause().getClass().getSimpleName()))) {
			if(device == null) {
				req.setAttribute("erro", e.getCause());
				req.getRequestDispatcher("/WEB-INF/jsp/include/erros/negocio.jsp").forward(req, res);
			}
			
			return ExceptionHandlerResult.STOP;
		}

		if (e != null && (e instanceof ConfiguracaoAmbienteException)) {
			if(device == null) {
				req.setAttribute("erro", e);
				req.getRequestDispatcher("/WEB-INF/jsp/include/erros/negocio.jsp").forward(req, res);
			}
			
			return ExceptionHandlerResult.STOP;
		}
		
		if (e != null && e.getCause() != null && e.getCause() instanceof ConfiguracaoAmbienteException) {
			if(device == null) {
				req.setAttribute("erro", e.getCause());
				req.getRequestDispatcher("/WEB-INF/jsp/include/erros/negocio.jsp").forward(req, res);
			}
			
			return ExceptionHandlerResult.STOP;
		}
		
		if (verificaSequenceDesatualizada(e, req, res, device)) {
			return ExceptionHandlerResult.STOP;
		}
			
		if (e != null && (e instanceof ArqException) && ((ArqException) e).isReadable()) {
			if(device == null) {
				req.setAttribute("erro", e);
				req.getRequestDispatcher("/WEB-INF/jsp/include/erros/negocio.jsp").forward(req, res);
			}
			
			return ExceptionHandlerResult.STOP;
		}
		
		if (e != null && (e instanceof JasperException || e instanceof NullPointerException) && isEmpty(e.getStackTrace())) {
			return ExceptionHandlerResult.IGNORE;
		}

		if (e != null && e instanceof IllegalStateException && e.getMessage().contains("Session already invalidated")) {
			return ExceptionHandlerResult.IGNORE;
		}
		
		if (e != null && (String.valueOf(e).contains("SocketException: Broken pipe") || String.valueOf(e).contains("SocketException: Pipe quebrado"))) {
			return ExceptionHandlerResult.IGNORE;
		}
		
		if (e != null && e.getCause() != null && (String.valueOf(e.getCause()).contains("SocketException: Broken pipe") || String.valueOf(e.getCause()).contains("SocketException: Pipe quebrado"))) {
			return ExceptionHandlerResult.IGNORE;
		}

		return ExceptionHandlerResult.CONTINUE;
	}

	private boolean verificaSequenceDesatualizada(Throwable e, HttpServletRequest req, HttpServletResponse res, Boolean device) throws IOException, ServletException {
		String pkeyError = "duplicate key value violates unique constraint ";
		boolean erro = verificaErroViolacaoChavePrimaria(e, req, res, pkeyError, device);
		
		if (!erro) {
			pkeyError = "duplicar valor da chave viola a restrição de unicidade ";
			erro = verificaErroViolacaoChavePrimaria(e, req, res, pkeyError, device);
		}
		
		return erro;
	}

	private boolean verificaErroViolacaoChavePrimaria(Throwable e, HttpServletRequest req, HttpServletResponse res, String pkeyError, Boolean device) throws ServletException, IOException {
		String msgException = null;
		
		if (e != null && e.getMessage() != null && e.getMessage().contains(pkeyError)) {
			msgException = e.getMessage();
		} else if (e != null && e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().contains(pkeyError)) {
			msgException = e.getCause().getMessage();
		} else if (e != null && e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause().getMessage() != null && e.getCause().getCause().getMessage().contains(pkeyError)) {
			msgException = e.getCause().getCause().getMessage();
		}
			
		if (msgException != null) {
			int beginIndex = msgException.indexOf(pkeyError) + pkeyError.length();
			String seqName = msgException.substring(beginIndex);
			if (seqName.contains(";")) {
				seqName = seqName.substring(0, seqName.indexOf(";"));
			}
			
			if(device == null) {
				req.setAttribute("erro", new NegocioException("ATENÇÃO: Erro ao pegar o valor da sequence associada à chave primária " + seqName + ". Por favor, atualize o valor da sequence."));
				req.getRequestDispatcher("/WEB-INF/jsp/include/erros/negocio.jsp").forward(req, res);
			}
			
			return true;
		}
	
		return false;
	}
	
}
