package br.ufrn.sigaa.mobile.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.UFRNException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.RegistroAcaoAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Resource genérico a ser extendido pelos resources do sistema.
 * 
 * @author Bernardo
 *
 */
public class SigaaGenericResource {

	/** Request HTTP do contexto da aplicação. */
	@Context
	protected HttpServletRequest request;

	/**
	 * Retorna o calendário acadêmico vigente.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	protected CalendarioAcademico getCalendarioVigente() throws DAOException {
		CalendarioAcademico cal = (CalendarioAcademico) request.getSession().getAttribute("calendarioAcademico");
		if (cal == null) {
			if (getDiscenteLogado() != null) {
				cal = CalendarioAcademicoHelper.getCalendario(getDiscenteLogado());
			} else {
				cal = CalendarioAcademicoHelper.getCalendario(getUsuarioLogado());
			}
		}
		return cal;
	}

	/**
	 * Retorna o usuário logado na aplicação.
	 * 
	 * @return
	 */
	protected Usuario getUsuarioLogado() {
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		return usuario;
	}

	/**
	 * Retorna o discente associado ao usuário logado.
	 * 
	 * @return
	 */
	protected Discente getDiscenteLogado() {
		return getUsuarioLogado().getDiscenteAtivo() != null ? getUsuarioLogado().getDiscenteAtivo().getDiscente() : null;
	}

	/**
	 * Registra uma ação da turma de acordo com os parâmetros passados.
	 * 
	 * @param turma
	 * @param descricao
	 * @param entidade
	 * @param acao
	 * @param ids
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	protected void registrarAcao(Turma turma, String descricao, EntidadeRegistroAva entidade, AcaoAva acao, int ... ids) throws ArqException, NegocioException {
		RegistroAcaoAva registroAcao = new RegistroAcaoAva(getUsuarioLogado(), descricao, entidade, acao, turma.getId(), ids);

		MovimentoCadastro mov = new MovimentoCadastro(registroAcao);

		mov.setCodMovimento(ArqListaComando.CADASTRAR);

		executarMovimento(mov);
	}

	/**
	 * Realiza o preparo e execução de um movimento.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	protected Object executarMovimento(Movimento mov) throws NegocioException, ArqException {
		String jndiName = (String) request.getSession().getServletContext().getAttribute("jndiName");

		FacadeDelegate facade = (FacadeDelegate) request.getSession().getAttribute("facadeDelegate");

		if ( facade == null ) { 
			facade = new FacadeDelegate(jndiName);
			request.getSession().setAttribute("facadeDelegate", facade);
		}

		facade.prepare(mov.getCodMovimento().getId(), request);
		return facade.execute(mov, request);
	} 

	/**
	 * Retorna um objeto {@link Specification} vazio, para permitir a execução de alguns movimentos.
	 * 
	 * @return
	 */
	protected Specification getEmptySpecification() {
		return new Specification() {
			Notification notification = new Notification();

			@Override
			public boolean isSatisfiedBy(Object objeto) {
				return true;
			}

			@Override
			public Notification getNotification() {
				return notification;
			}
		};
	}

	/**
	 * Monta a response usando a mensagem da exception passada.
	 * 
	 * @param e
	 * @return
	 */
	protected Response montarResponseException(UFRNException e) {
		return Response.serverError().entity(e.getMessage()).build();
	}
	
	
	protected String readStream(Reader rd) throws IOException {
		BufferedReader reader = new BufferedReader(rd);
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		
		return stringBuilder.toString();
	}


}
