/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Cria��o: 21/05/2009
 */
package br.ufrn.comum.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.dominio.PassaporteLogon;
import br.ufrn.comum.dao.QuestionarioAplicadoDao;
import br.ufrn.comum.dao.TelaAvisoLogonDao;
import br.ufrn.comum.dominio.QuestionarioAplicadoDTO;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.TelaAvisoLogon;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Managed Bean para que os usu�rios visualizem as telas de aviso
 * ao logarem nos sistemas.
 * 
 * @author David Pereira
 *
 */
@Component @Scope("session")
public class VerTelaAvisoLogonMBean extends ComumAbstractController<TelaAvisoLogon> {

	private String urlInicio;
	
	private List<TelaAvisoLogon> telas; 
	
	private int currentIndex = 0;
	
	private TelaAvisoLogon atual;

	private List<QuestionarioAplicadoDTO> questionarios;
	
	private QuestionarioAplicadoDTO questionarioAtual;

	/**
	 * M�todo chamado pelos controllers de logon dos sistemas. Busca
	 * as telas de logon que devem ser exibidas.
	 * JSP: N�o � chamado por nenhuma JSP.
	 * @param urlInicio
	 * @throws DAOException 
	 * @throws IOException 
	 */
	public void iniciar(String urlInicio, UsuarioGeral usuarioLogado, Integer sistema, HttpServletRequest req, HttpServletResponse res) throws DAOException {
		this.urlInicio = urlInicio;
				
		TelaAvisoLogonDao dao = new TelaAvisoLogonDao();
		telas = dao.findTelasAtivas(usuarioLogado.getId(), sistema);
		
		QuestionarioAplicadoDao qdao = DAOFactory.getInstance().getDAO(QuestionarioAplicadoDao.class, Sistema.COMUM, req);
		UsuarioGeral usuario = (UsuarioGeral) req.getSession().getAttribute("usuario");
		
		if (Sistema.isQuestionariosAtivos(sistema))
			questionarios = qdao.buscarQuestionariosAtivos(usuario.getId());
		
		if (!isEmpty(questionarios)) {
			questionarioAtual = questionarios.iterator().next();
			
			try {
				String context = req.getContextPath();
				String url = "/questionarios.jsf";
				res.sendRedirect(context + url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			proxima(req, res);
		}
	}
	
	/**
	 * Busca a pr�xima tela aviso de logon a ser exibida ao usu�rio. 
	 * JSP: /telaAvisoLogon.jsp
	 * @return
	 * @throws IOException 
	 */
	public String proxima() throws IOException {
		proxima(getCurrentRequest(), getCurrentResponse());
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}
	
	/**
	 * Direciona o usu�rio para a pr�xima tela de aviso.
	 * @param req
	 * @param res
	 */
	private void proxima(HttpServletRequest req, HttpServletResponse res) {
		try {
			String url = null;
			if (telas != null && currentIndex < telas.size()) {
				atual = telas.get(currentIndex++);
				url = "/telaAvisoLogon.jsf";
			} else {
				url = urlInicio;
			}

			String context = req.getContextPath();
			if (url != null && (url.startsWith(context) || url.startsWith("http://")))
				context = "";

			res.sendRedirect(context + url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Redireciona o usu�rio para a tela de responder a um question�rio.
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String responderQuestionario() throws NegocioException, ArqException {
		UsuarioGeral usr = getUsuarioLogado();
		PassaporteLogon passaporte = new PassaporteLogon();
		passaporte.setLogin(usr.getLogin());
		passaporte.setIdUsuario(usr.getId());
		passaporte.setSistemaAlvo(Sistema.SIGADMIN);
		passaporte.setSistemaOrigem(getSistema());
		passaporte.setHora(new Date());

		passaporte.setCodMovimento(ArqListaComando.CADASTRAR_PASSAPORTE);
		prepareMovimento(ArqListaComando.CADASTRAR_PASSAPORTE);

		execute(passaporte);
		
		String identificador = getParameter("identificador");
		String urlQuestionario = "/admin/questionario/responder/" + identificador;
		String urlAdmin = "/admin/logar.jsf?passaporte=true&login="+ usr.getLogin()+"&url="+urlQuestionario;
		
		return redirectSemContexto(RepositorioDadosInstitucionais.getLinkSigadmin() + urlAdmin);
	}
	
	/**
	 * Ignora o question�rio aplicado para o usu�rio.
	 * Esta opera��o indisponibiliza definitivamente o question�rio para o usu�rio.
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws IOException 
	 */
	public String ignorarQuestionario() throws NegocioException, ArqException, IOException {
		
		QuestionarioAplicadoDao qdao = DAOFactory.getInstance().
			getDAO(QuestionarioAplicadoDao.class, Sistema.COMUM, getCurrentRequest());
		
		//Verifica se o question�rio j� foi ignorado, caso contr�rio persiti
		if( !qdao.verificaQuestionarioIgnorado(questionarioAtual.getIdentificador(), getUsuarioLogado().getId() ) ){
			try{
				qdao.ignorarQuestionarioAplicado( questionarioAtual.getIdentificador(), getUsuarioLogado().getId() );
			}finally{
				qdao.close();
			}
		}
		
		return proxima();
	}

	public String getUrlInicio() {
		return urlInicio;
	}

	public void setUrlInicio(String urlInicio) {
		this.urlInicio = urlInicio;
	}

	public List<TelaAvisoLogon> getTelas() {
		return telas;
	}

	public void setTelas(List<TelaAvisoLogon> telas) {
		this.telas = telas;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public TelaAvisoLogon getAtual() {
		return atual;
	}

	public void setAtual(TelaAvisoLogon atual) {
		this.atual = atual;
	}
	
	public QuestionarioAplicadoDTO getQuestionarioAtual() {
		return questionarioAtual;
	}

}
