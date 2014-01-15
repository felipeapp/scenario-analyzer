/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 31/10/2007
 */
package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.LogonProgress;
import br.ufrn.sigaa.arq.acesso.AcessoMenu;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.dominio.SigaaConstantes;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ead.negocio.EADHelper;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Managed bean para que os gestores de ensino a distância possam logar como
 * tutores presenciais.
 *
 * @author David Pereira
 *
 */
@SuppressWarnings("unchecked")
@Component("logarComoDiscente") @Scope("request")
public class LogarComoDiscenteMBean extends SigaaAbstractController {

	/** Indica se o filtro de busca por matrícula foi selecionado. */
	private boolean buscaMatricula;
	
	/** Indica se o filtro de busca por nome foi selecionado. */
	private boolean buscaNome;
	
	/** Indica se o filtro de busca por curso foi selecionado. */
	private boolean buscaCurso;
	
	/** Indica se o filtro de busca por tutor foi selecionado. */
	private boolean buscaTutor;
	
	/** Matrícula utilizada na busca. */
	private Long matricula;
	
	/** Nome utilizado na busca. */
	private String nome;
	
	/** Curso selecionado na busca. */
	private String curso;	
	
	/** Tutor utilizado na busca. */
	private Usuario usuario = new Usuario();
	
	/** Lista de tutores. */
	private List<Usuario> usuarios;
	
	/** Lista de {@link DiscenteGraduacao} encontrados na busca. */
	private Collection<DiscenteGraduacao> alunosOrientados;
	
	/** Número de resultados encontrados na busca. */
	private int numResultados;

	public LogarComoDiscenteMBean() {
		usuario = new Usuario();
	}

	/**
	 * Direciona o usuário para o portal discente.
	 * <br />
	 * Método não chamado por JSPs.
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String pesquisar() throws SegurancaException {			
		return redirect("/portais/discente/discente.jsf");		
	}

	/**
	 * Busca os discentes de acordo com a matrícula, nome do discente,
	 * nome do curso ou tutor orientador.
	 * <br />
	 * Chamado pela JSP: 
	 * <ul>
	 * <li>/sigaa.war/ead/TutorOrientador/logarComoDiscente.jsp</li>
	 * </ul>
	 */
	public String localizarDiscentesPorTutor() throws DAOException {
		validarBuscaDiscentesPorTutor();
		
		if(hasErrors())
			return null;
		
		if (!buscaMatricula) matricula = null;
		if (!buscaNome) nome = null;
		if (!buscaCurso) curso = null;
		if (!buscaTutor) usuario = null;
		
		if (alunosOrientados == null) {
			if(buscaTutor)
				usuario = getGenericDAO().refresh(usuario);
			
			alunosOrientados = EADHelper.findDiscentesParaLogarComo( matricula, nome, curso, usuario );
			
			numResultados = alunosOrientados.size();
			
			if(numResultados == 0)
				addMensagemErro("Nenhum discente encontrado com os parâmetros definidos.");
		}
			
		return null;
	}
	
	/**
	 * Efetua a validação dos dados da busca de discente e adiciona as respectivas mensagens de erro.
	 */
	private void validarBuscaDiscentesPorTutor() {
		if (!buscaMatricula && !buscaNome && !buscaCurso && !buscaTutor) {
			addMensagemErro("Selecione um critério de busca.");
			return;
		}
		if(buscaMatricula && isEmpty(matricula))
			addMensagemErro("Informe uma matrícula.");
		if(buscaNome && isEmpty(nome))
			addMensagemErro("Informe o nome do discente.");
		if(buscaCurso && isEmpty(curso))
			addMensagemErro("Informe um curso.");
		if(buscaTutor && isEmpty(usuario))
			addMensagemErro("Selecione um tutor.");
	}
	
	/**
	 * Loga como o discente selecionado após a busca.
	 * <br />
	 * Chamado pelas JSPs: 
	 * <ul>
	 * <li>/sigaa.war/ead/TutorOrientador/logarComoDiscente.jsp</li>
	 * <li>/sigaa.war/portais/cpolo/listaDiscentesPorTutor.jsp</li>
	 * </ul> 
	 */
	public String logarDiscente() throws NegocioException, ArqException {
		
		checkRole(SigaaPapeis.SEDIS);
		
		prepareMovimento(SigaaListaComando.LOGAR_COMO_TUTOR);
			
		int idDiscente = getParameterInt("discente");
		UsuarioDao dao = getDAO(UsuarioDao.class);
		usuario = dao.findByDiscente(idDiscente);
			
		if (usuario == null) {
			addMensagemErro("Não é possível logar como este discente porque ele não possui usuário cadastrado no sistema.");
			return null;
		}
			
		UsuarioMov mov = new UsuarioMov();
		mov.setCodMovimento(SigaaListaComando.LOGAR_COMO_TUTOR);
		mov.setUsuario( usuario );
		mov.setIP(getCurrentRequest().getRemoteAddr());
		mov.setUsuarioLogado( usuario );
		execute(mov);

		removerAtributosSessao();
			
		getCurrentSession().setAttribute("usuarioAnterior", getCurrentSession().getAttribute("usuario"));
		getCurrentSession().setAttribute("acessoAnterior",	getCurrentSession().getAttribute("acesso"));
		
		usuario.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		getCurrentSession().setAttribute(SigaaConstantes.USUARIO_SESSAO, usuario);
		
		List<VinculoUsuario> vinculos = VinculoUsuario.processarVinculosUsuario(usuario, getCurrentRequest());
		usuario.setVinculos(vinculos);
		
		VinculoUsuario vinculoDiscente = null;
		for (VinculoUsuario vinculo : vinculos) {
			if (vinculo.isVinculoDiscente() && vinculo.getDiscente().isDiscenteEad())
				vinculoDiscente = vinculo;
		}
		
		usuario.setVinculoAtivo(vinculoDiscente);
		
		VinculoUsuario.popularVinculoAtivo(usuario);
		
		LogonProgress progress = (LogonProgress) WebApplicationContextUtils.getWebApplicationContext(getCurrentSession().getServletContext()).getBean("logonProgress");
		AcessoMenu acesso = new AcessoMenu(usuario, progress);
		acesso.executar(getCurrentRequest());
		
		setSubSistemaAtual(SigaaSubsistemas.PORTAL_DISCENTE);
		getCurrentSession().setAttribute("acesso", acesso.getDados());
		carregarParametrosCalendarioAtual();
		resetBean("portalDiscente");
		
		return redirect("/verPortalDiscente.do");
	}

	/**
	 * Retorna uma lista de {@link SelectItem} referentes aos tutores armazenados.
	 * <br />
	 * Método não chamado por JSPs.
	 * @return
	 * @throws ArqException
	 */
	public List<SelectItem> getTutores() throws ArqException {
		return toSelectItems(usuarios, "id", "nomeLogin");
	}

	public Collection<DiscenteGraduacao> getAlunosOrientados() {
		return alunosOrientados;
	}

	public void setAlunosOrientados(Collection<DiscenteGraduacao> alunosOrientados) {
		this.alunosOrientados = alunosOrientados;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public boolean isBuscaMatricula() {
		return buscaMatricula;
	}

	public void setBuscaMatricula(boolean buscaMatricula) {
		this.buscaMatricula = buscaMatricula;
	}

	public boolean isBuscaNome() {
		return buscaNome;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}

	public boolean isBuscaCurso() {
		return buscaCurso;
	}

	public void setBuscaCurso(boolean buscaCurso) {
		this.buscaCurso = buscaCurso;
	}

	public boolean isBuscaTutor() {
		return buscaTutor;
	}

	public void setBuscaTutor(boolean buscaTutor) {
		this.buscaTutor = buscaTutor;
	}

	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public int getNumResultados() {
		return numResultados;
	}

	public void setNumResultados(int numResultados) {
		this.numResultados = numResultados;
	}

}
