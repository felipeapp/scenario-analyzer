package br.ufrn.sigaa.assistencia.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.ParametroDiscenteCadastroUnico;
import br.ufrn.sigaa.assistencia.dominio.RestricaoDiscenteCadastroUnico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoAgregadorBolsas;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Permite por alunos que estão inscritos no cadastro único
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
@Component("buscaDiscenteCadastroUnico") @Scope("request")
public class BuscaDiscenteCadastroUnicoMBean extends SigaaAbstractController<Discente> {

	/**
	 * Página de redirecionamento para tela de busca de aluno
	 */
	private static final String BUSCA_ALUNO = "/sae/cadastro_unico/busca_aluno.jsp";

	/**
	 * Aluno escolhido da busca
	 */
	private Discente discente = null;
	
	/**
	 * Resultado da busca de alunos
	 */
	private List<Discente> resultado = null;
	
	/**
	 * Mensagem a ser enviada ao aluno
	 */
	private Mensagem mensagem = new Mensagem(); 
	
	/**
	 * Mapea os filtros da busca de discente do cadastro único
	 */
	private RestricaoDiscenteCadastroUnico restricao = new RestricaoDiscenteCadastroUnico();
	
	/**
	 * Encapsula os parâmetros vindos do formulário
	 */
	private ParametroDiscenteCadastroUnico parametro = new ParametroDiscenteCadastroUnico();

	/**
	 * Construtor Padrão
	 */
	public BuscaDiscenteCadastroUnicoMBean() {
		restricao = new RestricaoDiscenteCadastroUnico();
		parametro = new ParametroDiscenteCadastroUnico();
	}
	
	
	/**
	 * Inicia o caso de uso para busca de alunos com inscrição no cadastro único
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarBusca() throws SegurancaException {
		restricao = new RestricaoDiscenteCadastroUnico();
		parametro = new ParametroDiscenteCadastroUnico();
		return telaBusca();
	}
	
	/**
	 * Método para iniciar busca ao vir do SIPAC.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP´s</li>
	 * </ul>
	 * @return
	 */
	public String iniciarBuscaIntegracao() {
		restricao = new RestricaoDiscenteCadastroUnico();
		parametro = new ParametroDiscenteCadastroUnico();
		redirect("/sae/cadastro_unico/busca_aluno.jsf");
		return null;
	}

	/**
	 * Tela de busca de alunos
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/sae/cadastro_unico/busca_aluno.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaBusca() {
		return forward(BUSCA_ALUNO);
	}
	
	/**
	 * Realiza a busca de acordo com os parâmetros informados
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/sae/cadastro_unico/busca_aluno.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public void buscar(ActionEvent event) throws ArqException {
		
		validarBusca();
		
		if(hasErrors())
			return;
		
		DiscenteDao dao = getDAO(DiscenteDao.class);
		resultado = dao.findDiscentes(restricao, parametro);
	}
	
	/**
	 * Valida a busca
	 */
	private void validarBusca() {
		
		ValidatorUtil.validateRequiredId(parametro.getCentro().getId(), "Centro", erros);
		
		if (restricao.isBuscaCurso())
			ValidatorUtil.validateRequiredId(parametro.getCurso().getId(), "Curso", erros);
	}


	/**
	 * Carrega os cursos de um centro
	 * JSP: /sae/cadastro_unico/busca_aluno.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getComboCursos() throws DAOException {
		
		CursoDao dao = getDAO(CursoDao.class);
		Collection<Curso> cursosGraduacao = new ArrayList<Curso>();
		
		if (!isEmpty(parametro.getCentro().getId())) 
			cursosGraduacao = dao.findByCentro(parametro.getCentro().getId(), NivelEnsino.GRADUACAO);
		
		Collection<Curso> cursosTecnicos = dao.findByCentro(parametro.getCentro().getId(), NivelEnsino.TECNICO);
		for (Curso cursoTec : cursosTecnicos)
			cursosGraduacao.add(cursoTec);
		
		return toSelectItems(cursosGraduacao, "id", "descricao");
	}
	
	/**
	 * Pega o aluno escolhido na busca
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/sae/cadastro_unico/busca_aluno.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String escolherDiscente() throws ArqException {
		
		mensagem = new Mensagem();
		prepareMovimento(SigaaListaComando.ENVIAR_MENSAGEM_COORDENAOR);
		Integer id = getParameterInt("id");
		
		if (isEmpty(id)) {
			addMensagemErro("Ocorreu um erro ao tentar selecionar o aluno.");
			return null;
		}
		discente = getGenericDAO().findByPrimaryKey(id, Discente.class);
		PerfilPessoa perfil = PerfilPessoaDAO.getDao().get(discente.getIdPerfil());
		
		if (isEmpty(perfil)) {
			addMensagemErro("Aluno não possui perfil");
			return null;
		}
		
		discente.setPerfil(perfil);

		return forward("/sae/cadastro_unico/perfil_cadunico.jsp");
	}
	
	/**
	 * Envia uma mensagem para o aluno
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/sae/cadastro_unico/perfil_cadunico.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String enviarMensagem() throws ArqException {
		
		Usuario usuario = getDAO(UsuarioDao.class).findByDiscente(discente);
		
		mensagem.setAutomatica(false);
		mensagem.setLeituraObrigatoria(true);
		mensagem.setUsuario(getUsuarioLogado());
		mensagem.setRemetente(getUsuarioLogado());
		mensagem.setUsuarioDestino(usuario);
		
		MovimentoAgregadorBolsas mov = new MovimentoAgregadorBolsas();
		mov.setCodMovimento(SigaaListaComando.ENVIAR_MENSAGEM_COORDENAOR);
		mov.setMensagem(mensagem);
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMensagemInformation("Mensagem enviada com sucesso.");
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		}
		
		return telaBusca();
	}
	
	/**
	 * Verifica se veio do administrativo
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/sae/cadastro_unico/busca_aluno.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isOrigemAdministrativo() {
		if (getSubSistema() == null)
			return true;
		return false;
	}
	
	/**
	 * Volta para o administrativo
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/sae/cadastro_unico/busca_aluno.jsp</li>
	 * </ul>
	 * @return
	 */
	public String voltarAdministrativo() {
		if (isOrigemAdministrativo()) 
			redirect("/entrarSistema.do?sistema=sipac&url=/sipac/portal_administrativo/index.jsf");
		return null;
	}
	
	

	public RestricaoDiscenteCadastroUnico getRestricao() {
		return restricao;
	}

	public void setRestricao(RestricaoDiscenteCadastroUnico restricao) {
		this.restricao = restricao;
	}

	public ParametroDiscenteCadastroUnico getParametro() {
		return parametro;
	}

	public void setParametro(ParametroDiscenteCadastroUnico parametro) {
		this.parametro = parametro;
	}

	public List<Discente> getResultado() {
		return resultado;
	}

	public void setResultado(List<Discente> resultado) {
		this.resultado = resultado;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Mensagem getMensagem() {
		return mensagem;
	}

	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}
	
}
