/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/11/2007
 *
 */
package br.ufrn.sigaa.ead.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ead.PoloCursoDao;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;
import br.ufrn.sigaa.ead.dominio.HorarioTutor;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.PoloCurso;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.dominio.VinculoTutor;
import br.ufrn.sigaa.ead.negocio.MovimentoUsuarioTutor;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaPessoaMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoPessoa;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorPessoa;
import br.ufrn.sigaa.mensagens.MensagensEAD;
import br.ufrn.sigaa.parametros.dominio.ParametrosEAD;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * MBean para operações de cadastro de tutores orientadores em pólos de curso.
 *
 * @author Andre M Dantas
 *
 */
@Component("tutorOrientador") @Scope("session")
public class TutorOrientadorMBean extends SigaaAbstractController<TutorOrientador> implements OperadorPessoa {

	/** Atalho para a view do menu SEDIS. */
	private static final String JSP_MENU_SEDIS = "menuSEDIS";

	/** Armazena a lista de {@link SelectItem} de cursos a distância. */
	private List<SelectItem> cursos;
	
	/** Armazena a lista de {@link SelectItem} contendo os pólos de ensino. */
	private List<SelectItem> polos;
	
	/** Curso selecionado para a busca de tutores. */
	private Curso curso;
	/** Pólo selecionado para a busca de tutores. */
	private Polo polo;
	/** Nome indicado para realizar a busca de tutores. */
	private String nome;

	/** Indica se o usuário irá realizar a busca de tutores pelo curso. */
	private boolean buscaCurso;
	/** Indica se o usuário irá realizar a busca de tutores pelo pólo. */
	private boolean buscaPolo;
	/** Indica se o usuário irá realizar a busca de tutores pelo nome do tutor. */
	private boolean buscaNome;
	/** Indica se o usuário irá realizar a busca somente de tutores ativos.*/
	private boolean buscaSomenteAtivos;
	/** Indica se o usuário irá realizar a busca somente de tutores com orientandos.*/
	private boolean buscaSomenteComOrientados;
	/** Indica se o tutor está sendo reativado.*/
	private boolean reativar;
	/** Cursos no qual o tutor será associado.  */
	private List<String> associarEm;
	/** Lista de cursos dependendo do pólo. */
	private List<SelectItem> cursosCombo;
	/** Armazena os horários de um tutor. */
	private HorarioTutor horario = new HorarioTutor();
	
	/** DataModel associado aos horários de um tutor. */
	private DataModel horarioDM;

	/** Armazena um {@link TutorOrientador}. */
	private TutorOrientador tutorOrient = new TutorOrientador();
	
	/** Armazena uma lista de {@link SelectItem} contendo as possíveis horas. */
	private static List<SelectItem> horas = new ArrayList<SelectItem>();
	/** Armazena uma lista de {@link SelectItem} contendo os dias da semana (segunda a sábado). */
	private static List<SelectItem> diasSemana = new ArrayList<SelectItem>();
	
	/** Mensagem de sucesso exibida no formulário de cadastro de horários do tutor.*/
	private String mensagemSucesso;
	
	/** Mensagem de erro exibida no formulário de cadastro de horarios do tutor.*/
	private String mensagemErro;
	
	static {
		for (int i = 7; i < 24; i++) horas.add(new SelectItem(String.valueOf(i),String.valueOf(i)+"h"));
		diasSemana.add(new SelectItem(String.valueOf(Calendar.MONDAY), "Segunda-feira"));
		diasSemana.add(new SelectItem(String.valueOf(Calendar.TUESDAY), "Terça-feira"));
		diasSemana.add(new SelectItem(String.valueOf(Calendar.WEDNESDAY), "Quarta-feira"));
		diasSemana.add(new SelectItem(String.valueOf(Calendar.THURSDAY), "Quinta-feira"));
		diasSemana.add(new SelectItem(String.valueOf(Calendar.FRIDAY), "Sexta-feira"));
		diasSemana.add(new SelectItem(String.valueOf(Calendar.SATURDAY), "Sábado"));
	}
	
	public TutorOrientadorMBean() {
		initObj();
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	/**
	 * Retorna lista de SelectItem para gerar ComboBox de todos os cursos a distância.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getCursos() throws DAOException {
		if (cursos == null) {
			CursoDao dao = getDAO(CursoDao.class);
			cursos = toSelectItems(dao.findAllCursosADistancia(), "id", "descricao");
		}
		return cursos;
	}

	public void setCursos(List<SelectItem> curso) {
		this.cursos = curso;
	}

	/**
	 * Retorna todos os pólos.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPolos() throws DAOException {
		if (polos == null) {
			PoloDao dao = getDAO(PoloDao.class);
			polos = toSelectItems(dao.findAllPolos(), "id", "cidade.nomeUF");
		}
		return polos;
	}

	public void setPolos(List<SelectItem> polos) {
		this.polos = polos;
	}

	/**
	 * Retorna todos o vínculos do tutor parar gerar SelectItem .
	 * 
	 * @return
	 */
	public Collection<SelectItem> getAllVinculos() {
		return getAll(VinculoTutor.class, "id", "nome");
	}

	/**
	 * Inicia o objeto de domínio para se usado no bean.
	 */
	private void initObj() {
		obj = new TutorOrientador();
		obj.setVinculo(new VinculoTutor());
		obj.setPoloCurso(new PoloCurso());
		obj.getPoloCurso().setCurso(new Curso());
		obj.getPoloCurso().setPolo(new Polo());
		curso = new Curso();
		polo = new Polo();
		nome = "";
		buscaCurso = false;
		buscaPolo = false;
		buscaNome = false;
		buscaSomenteAtivos = true;
		buscaSomenteComOrientados = false;
		cursosCombo = new ArrayList<SelectItem>();
		associarEm = new ArrayList<String>();
		mensagemErro = mensagemSucesso = null;
		setResultadosBusca(new ArrayList<TutorOrientador>());
	}

	/**
	 * Faz o pré-processamento para cadastrar o tutor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		initObj();
		checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD });
		BuscaPessoaMBean mbean = (BuscaPessoaMBean) getMBean("buscaPessoa");
		mbean.setCodigoOperacao(OperacaoPessoa.TUTOR_ORIENTADOR);
		setConfirmButton("Cadastrar");
		return mbean.popular();
	}
	
	/**
	 * Faz o pré-processamento para remover.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ead/menu.jsp
	 */
	@Override
	public String preRemover() {
		try {
			checkRole(SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD);
		} catch (SegurancaException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		return super.preRemover();
	}

	/**
	 * Seta a operação para inativar o objeto.
	 */
	@Override
	protected void beforeInativar() {

			try {
				prepareMovimento( ArqListaComando.DESATIVAR );
			} catch (ArqException e) {
				e.printStackTrace();
				notifyError(e);
			}
			setOperacaoAtiva( ArqListaComando.DESATIVAR.getId() );
			super.beforeInativar();
	}
	
	/**
	 * Remove o tutor orientador.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/ead/TutorOrientador/form.jsp</li>
	 * 		<li>/ead/TutorOrientador/horario.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		setId();
		super.remover();
		initObj();
		redirect(getListPage());
		return null;
	}

	/**
	 * Atualiza o tutor orientador.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ead/TutorOrientador/lista.jsp
	 */
	@Override
	public String atualizar() throws ArqException {

		try {
			checkRole(SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD);
		} catch (SegurancaException e) {
			addMensagemErro(e.getMessage());
			return null;
		}

		super.atualizar();

		polo = obj.getPoloCurso().getPolo();
		carregarCursosCombo(polo.getId());
		associarEm = new ArrayList<String>();
		for (PoloCurso pc : obj.getPoloCursos())
			associarEm.add(Integer.toString(pc.getId()));
		
		return forward(getFormPage());
	}

	/**
	 * Cadastra o tutor orientador.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/ead/TutorOrientador/cadastro_usuario.jsp</li>
	 * 		<li>/ead/TutorOrientador/form.jsp</li>
	 * 		<li>/ead/TutorOrientador/horario.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws NegocioException, ArqException {
		
		checkRole(SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD);

		inserirPoloCursosTutor();			

		if (obj.getPessoa() == null) {
			addMensagem(MensagensEAD.TUTOR_INVALIDO);
			return redirect("/verMenuSedis.do");
		}
		
		validarTutor();
		
		if(hasErrors())
			return null;
		
		try {
			//Mudar para ativo apenas no cadastro.
			if (obj.getId() == 0){
				obj.setAtivo(true);
				obj.setTipo(TutorOrientador.TIPO_TUTOR_PRESENCIAL);
			}
			super.cadastrar();
		} catch(Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
			return null;
		}
		return null;

	}

	/**
	 * Valida o tutor.<br/><br/>
	 * 
	 * Método não invocado por JSPs. 
	 */
	private void validarTutor() throws DAOException {
		
		TutorOrientadorDao tutorDao = null;
		try {
			tutorDao = getDAO(TutorOrientadorDao.class);
			
			TutorOrientador t = tutorDao.findByExactField(TutorOrientador.class, new String[]{"pessoa.id","tipo","ativo"},
					new Object[]{obj.getPessoa().getId(),TutorOrientador.TIPO_TUTOR_PRESENCIAL,true}, true);
			
			if (obj.getId() == 0 && t != null) { 
				if (t.isPresencial())
					addMensagemErro("Esta pessoa já é um tutor presencial.");
			}		
			if (obj.getPoloCursos().isEmpty())
				addMensagemErro("É necessário escolher pelo menos um pólo e um curso.");
			
			obj.validate();
		} finally {
			if (tutorDao!=null)
				tutorDao.close();
		}
	}
	
	/**
	 * Busca os Pólos-Cursos através dos ids e os insere no objeto.<br/><br/>
	 * 
	 * Método não invocado por JSPs. 
	 */
	private void inserirPoloCursosTutor() throws DAOException {
		
		PoloCursoDao pDao = null; 
		
		try {
			pDao = getDAO(PoloCursoDao.class);
			
			List<Integer> idsPCurso = new ArrayList<Integer>();
			for (String idPCurso : associarEm)
				idsPCurso.add(Integer.valueOf(idPCurso));
			
			if (!idsPCurso.isEmpty()){
				ArrayList<PoloCurso> pcursos = pDao.findAllByIds(idsPCurso);
				if (pcursos != null && !pcursos.isEmpty()){
					obj.setPoloCursos(pcursos);
					obj.setPoloCurso(pcursos.iterator().next());
				}
			}
			
		} finally {
			if (pDao != null)
				pDao.close();
		}
	}
	
	/**
	 * Método chamado para ativar novamente um tutor.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/TutorOrientador/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String reativarTutor() throws Exception {
		
		TutorOrientadorDao tutorDao = null;
		try {
				tutorDao = getDAO(TutorOrientadorDao.class);
												
				int id = getParameterInt("id");
				this.obj.setId(id);				
				populateObj();
				
				if (obj.getPoloCursos().isEmpty())
					addMensagemErro("É necessário escolher pelo menos um pólo e um curso.");
				
				this.obj.setAtivo(true); 
				prepareMovimento(ArqListaComando.ALTERAR);
				setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			
				reativar = true;
				super.cadastrar();
		} finally {
			reativar = false;
			if (tutorDao!=null)
				tutorDao.close();
		}

		buscar();
		return redirect(getContextPath() + "/ead/TutorOrientador/lista.jsf");
	}

	/**
	 * Após cadastrar, reseta o objeto de domínio para novas operação.
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		if (!reativar)
			initObj();
	}

	/**
	 * Cancela a operação atual.<br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/ead/relatorios/relatorio_horario_form.jsp</li>
	 * 		<li>/sigaa.war/ead/TutorOrientador/cadastro_usuario.jsp</li>
	 * 		<li>/sigaa.war/ead/TutorOrientador/form.jsp</li>
	 * 		<li>/sigaa.war/ead/TutorOrientador/horario.jsp</li>
	 * 		<li>/sigaa.war/ead/TutorOrientador/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		super.cancelar();
		if (obj.getId() == 0)
			return JSP_MENU_SEDIS;
		else
			return forward(getListPage());
	}

	/**
	 * Após a pessoa ser selecionada, redireciona para o formulário de cadastro do tutor.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É public porque precisa ser acessado pela classe BuscarPessoaMBean, no método redirecionarPessoa(Pessoa)
	 */
	public String selecionaPessoa() {
		cursosCombo = new ArrayList<SelectItem>();
		return forward(getFormPage());
	}

	/**
	 * Seta a pessoa do {@link TutorOrientador} trabalhado.
	 */
	public void setPessoa(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	/**
	 * Define a página de formulário de tutor orientador
	 */
	@Override
	public String getFormPage() {
		return "/ead/TutorOrientador/form.jsp";
	}

	/**
	 * Define a página de listagem de tutor orientador
	 */
	@Override
	public String getListPage() {
		return "/ead/TutorOrientador/lista.jsf";
	}
	
	/**
	 * Direciona para a página de listagem de tutor orientador. 
	 * 
	 * Chamado por:
	 * sigaa.war/ead/TutorOrientador/lista.jsp
	 */
	public String listar() {
		initObj();
		return forward(getListPage());
	}
	

	/**
	 * Define a página que será exibida após o cadastramento.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É public por causa da arquitetura.
	 */
	@Override
	public String forwardCadastrar() {
		return "/ead/menu.jsf";
	}

	/**
	 * Realiza busca de tutores de acordo com o curso, polo ou nome da pessoa.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorOrientador/lista.jsp
	 */
	@Override
	public String buscar() throws Exception {
		TutorOrientadorDao dao = getDAO(TutorOrientadorDao.class);
		setResultadosBusca(dao.findByCursoPoloNome(buscaCurso ? curso : null, buscaPolo ? polo : null, buscaNome ? nome : null, buscaSomenteAtivos, buscaSomenteComOrientados));
		
		if((getResultadosBusca()!=null && getResultadosBusca().isEmpty()) || getResultadosBusca()==null) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		return forward(getListPage());
	}

	/**
	 * Procedimento realizado após inativar.<br/><br/>
	 * 
	 * Não invocado por JSP's
	 */
	@Override
	public void afterInativar() {
		try {
			buscar();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Redireciona para página de cadastro de usuários.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorOrientador/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String formUsuario() throws ArqException {
		populateObj(true);
		obj.setUsuario(new Usuario());
		prepareMovimento(SigaaListaComando.CADASTRAR_USUARIO_TUTOR);
		return forward("/ead/TutorOrientador/cadastro_usuario.jsp");
	}

	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		mensagens = new ListaMensagens();
		if (obj.getUsuario().getLogin().contains(" ")) 
			mensagens.addMensagem(MensagensEAD.LOGIN_COM_ESPACO);
		ValidatorUtil.validateRequired(obj.getUsuario().getLogin(), "Login", mensagens);
		if (obj.getUsuario().getEmail() == null || obj.getUsuario().getEmail().equals(""))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Email");
		ValidatorUtil.validateEmail(obj.getUsuario().getEmail(), "Email", mensagens);
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return true;
		}
		return false;
	}
	
	/**
	 * Realiza o cadastramento de usuário para o tutor selecionado.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorOrientador/cadastro_usuario.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String cadastrarUsuario() throws DAOException {
		
		if (validacaoDados(erros))
			return null;
		
		MovimentoUsuarioTutor mov = new MovimentoUsuarioTutor();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_USUARIO_TUTOR);
		mov.setUsuario(obj.getUsuario());
		mov.setTutor(obj);

		try {
			executeWithoutClosingSession(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			
			buscar();
			
			redirect(getContextPath() + "/ead/TutorOrientador/lista.jsf");
			
		} catch(Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
		return null;

	}

	/**
	 * Reativa o usuário do Tutor
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorOrientador/cadastro_usuario.jsp
	 *  
	 * @return
	 * @throws ArqException 
	 */
	public String reativarUsuario() throws ArqException{
		populateObj(true);
		prepareMovimento(SigaaListaComando.REATIVAR_USUARIO_TUTOR);
		MovimentoUsuarioTutor mov = new MovimentoUsuarioTutor();
		mov.setCodMovimento(SigaaListaComando.REATIVAR_USUARIO_TUTOR);
		mov.setUsuario(obj.getUsuario());
		mov.setTutor(obj);

		try {
			executeWithoutClosingSession(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			
			buscar();
			redirect(getContextPath() + "/ead/TutorOrientador/lista.jsf");
			
		} catch(Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}
		return null;
	}
	
	/**
	 * Define o horário da tutoria desse tutor.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSP:
	 * <ul>
	 * 		<li>ead/TutorOrientador/horario.jsp</li>
	 * 		<li>ead/TutorOrientador/lista.jsp</li>
	 * 		<li>portais/tutor/menu_tutor.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String definirHorario() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CADASTRAR_HORARIO_TUTOR);
		
		if( isUserInRole( SigaaPapeis.SEDIS ) ){
			setId();
		} else if( getAcessoMenu().isTutorEad() ){
			obj.setId( getUsuarioLogado().getTutor().getId() );
		}else {
			throw new SegurancaException( "Usuário não autorizado a realizar esta operação." );
		}
		
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), TutorOrientador.class);
		obj.getHorarios().iterator();
		tutorOrient = obj;
		horarioDM = new ListDataModel(obj.getHorarios());
		return forward("/ead/TutorOrientador/horario.jsp");
	}
	
	
	/**
	 * Retorna uma coleção de {@link SelectItem} contendo todos os tutores 
	 * de acordo com o coordenador do pólo e o tutor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/TutorOrientador/horario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllTutoresCombo() throws DAOException {
		TutorOrientadorDao tOrientadorDao = getDAO(TutorOrientadorDao.class);
		
		CoordenacaoPolo polo = getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo();
		
		ArrayList<TutorOrientador> lista = (ArrayList<TutorOrientador>) tOrientadorDao.findByPolo(polo.getPolo().getId());
		
		List<SelectItem> tutores = toSelectItems(lista, "id", "nome");
		
		tutores.add(0, new SelectItem(0, "---"));
		tutores.add(0, new SelectItem(tutorOrient.getId(), tutorOrient.getNome()));
		
		return tutores;
	}
	
	
	/**
	 * Coordenador de pólo usa esse método para definir os horários.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorOrientador/horario.jsp
	 * 
	 * @param e
	 * @return
	 * @throws ArqException
	 */
	//código duplicado, EXTRAIR METODO
	public String definirHorarioTutorPeloCoordenadorPolo(ValueChangeEvent e) throws ArqException {
		
		prepareMovimento(SigaaListaComando.CADASTRAR_HORARIO_TUTOR);

		Integer idTutor = Integer.valueOf( e.getNewValue().toString() );	
		obj = getGenericDAO().findByPrimaryKey(idTutor, TutorOrientador.class);
		
		if (getAcessoMenu().isTutorEad() || getAcessoMenu().isCoordenadorPolo()) {
			if (obj == null) {
				obj = new TutorOrientador();
				horarioDM = new ListDataModel(new ArrayList<HorarioTutor>());
				return null;
			}
		} else	if( isUserInRole( SigaaPapeis.SEDIS) ){
			setId();
		} else {
			throw new SegurancaException( "Usuário não autorizado a realizar esta operação." );
		}
		
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), TutorOrientador.class);
		obj.getPoloCurso().getDescricao();
		if (obj.getHorarios() != null) {
			obj.getHorarios().iterator();
			horarioDM = new ListDataModel(obj.getHorarios());
		} else {
			horarioDM = new ListDataModel(new ArrayList<HorarioTutor>());
		}
		return null;
	}

	/**
	 * Redireciona usuário para página de definição de horário do coordenador de pólo.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É public pois é chamado pelo método definirHorarioTutorPeloCoordenadorPolo() na classe PortalCoordenadorPoloMBean. 
	 * 
	 * @param idTutor
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String abrirPaginaHorarioCoordenadorPolo(int idTutor) throws DAOException, SegurancaException {

		tutorOrient = getGenericDAO().findByPrimaryKey(idTutor, TutorOrientador.class);
		
		if( getAcessoMenu().isTutorEad() || getAcessoMenu().isCoordenadorPolo() ){
			obj.setId( tutorOrient.getId() );
		} else if( isUserInRole( SigaaPapeis.SEDIS) ){
			setId();
		}else {
			throw new SegurancaException( "Usuário não autorizado a realizar esta operação." );
		}
		
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), TutorOrientador.class);
		obj.getPoloCurso().getDescricao();
		if (obj.getHorarios() != null) {
			obj.getHorarios().iterator();
			horarioDM = new ListDataModel(obj.getHorarios());
		} else {
			horarioDM = new ListDataModel(new ArrayList<HorarioTutor>());
		}
		
		return forward("/ead/TutorOrientador/horario.jsp");
	}
	
	/**
	 * Adiciona um horário ao objeto de domínio.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorOrientador/horario.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String adicionarHorario(ActionEvent evt) throws ArqException {		
		mensagemErro = mensagemSucesso = null;
		if (horario.getHoraInicio() >= horario.getHoraFim()) {
			mensagemErro = UFRNUtils.getMensagem(MensagensEAD.HORA_INICIAL_MENOR_QUE_FINAL).getMensagem();
			return null;
		}

		if (obj.hasHorario(horario)) {
			mensagemErro = UFRNUtils.getMensagem(MensagensEAD.HORARIO_JA_INFORMADO).getMensagem();
			return null;
		}
		
		if (obj.getId() == 0) {
			mensagemErro = UFRNUtils.getMensagem(MensagensEAD.TUTOR_INVALIDO).getMensagem();
			return null;
		}
		
		obj.addHorario(horario);
		horarioDM = new ListDataModel(obj.getHorarios());
		horario = new HorarioTutor();
		mensagemSucesso = "Horário Adicionado com Sucesso!";
		
		return null;
	}

	/**
	 * Remove um horário do objeto de domínio.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorOrientador/horario.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String removerHorario(ActionEvent evt) {
		
		mensagemSucesso = mensagemErro = null;
		
		Integer id = getParameterInt("idHorario");
		
		HorarioTutor horario = new HorarioTutor();
				
		for(HorarioTutor h: obj.getHorarios()) {
			 if (h.getId() == id ) {
				 horario = h;
				 break;
			 }
		}
		
		obj.removeHorario(horario);
		horarioDM = new ListDataModel(obj.getHorarios());
		horario = new HorarioTutor();
		mensagemSucesso = "Horário Removido com Sucesso!";
		return null;
	}
	
	/**
	 * Valida o estado do objeto.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É public pois o é chamado pelo método cadastrarHorario() na classe TutorOrientadorMBean.
	 * 
	 * @return
	 */
	public void validarHorarios() {
		
		if ( obj == null || obj.getId() == 0 ) {
			addMensagem(MensagensEAD.TUTOR_INVALIDO);
		}
		
		if ( obj != null && (obj.getHorarios() == null || obj.getHorarios().isEmpty()) ) {
			addMensagemErro("É necessário definir pelo menos uma data!");
		}
		
	}

	/**
	 * Cadastra o horário do tutor.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorOrientador/horario.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarHorario() throws ArqException {
		
		mensagemErro = mensagemSucesso = null;
		validarHorarios();
		
		if ( !hasErrors() ) { 
				
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_HORARIO_TUTOR);
			try {
				execute(mov, getCurrentRequest());
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				if( isUserInRole( SigaaPapeis.SEDIS ) ){
					redirect("/ead/TutorOrientador/lista.jsf");
					return null;
				}else if( getAcessoMenu().isTutorEad() )
					return cancelar();
			} catch(NegocioException e) {
				addMensagemErro(e.getMessage());
				return null;
			}
		}
		
		return null;
	}

	/**
	 * Carrega o combo com os cursos de um pólo.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/TutorOrientador/form.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public void carregarCursosCombo( ValueChangeEvent evt ) throws DAOException{
		Integer idPolo = (Integer) evt.getNewValue();
		carregarCursosCombo(idPolo);
	}

	/**
	 * Carrega o combo com os cursos de um pólo.<br/><br/>
	 * 
	 * Método não invocado pos JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	private void carregarCursosCombo(Integer idPolo) throws DAOException {
		ArrayList<PoloCurso> cursos =  getDAO(PoloCursoDao.class).findPoloCursosByPolo(idPolo);
		
		cursosCombo = new ArrayList<SelectItem>();
		for ( PoloCurso pcurso : cursos )
			cursosCombo.add(new SelectItem(pcurso.getId(), pcurso.getCurso().getDescricao()));
	}
	
	/**
	 * Verifica se o tutor utiliza tutoria.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: ead/menu.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public boolean isPermiteTutoria(){
		boolean permiteTutoria = ParametroHelper.getInstance().getParametroBoolean(ParametrosEAD.UTILIZAR_TUTORIA_EAD);
		return permiteTutoria;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isBuscaCurso() {
		return buscaCurso;
	}

	public void setBuscaCurso(boolean buscaCurso) {
		this.buscaCurso = buscaCurso;
	}

	public boolean isBuscaPolo() {
		return buscaPolo;
	}

	public void setBuscaPolo(boolean buscaPolo) {
		this.buscaPolo = buscaPolo;
	}

	public boolean isBuscaNome() {
		return buscaNome;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}

	public boolean isBuscaSomenteAtivos() {
		return buscaSomenteAtivos;
	}

	public void setBuscaSomenteAtivos(boolean buscaSomenteAtivos) {
		this.buscaSomenteAtivos = buscaSomenteAtivos;
	}

	public boolean isBuscaSomenteComOrientados() {
		return buscaSomenteComOrientados;
	}

	public void setBuscaSomenteComOrientados(boolean buscaSomenteComOrientados) {
		this.buscaSomenteComOrientados = buscaSomenteComOrientados;
	}

	public List<SelectItem> getHoras() {
		return horas;
	}

	public List<SelectItem> getDiasSemana() {
		return diasSemana;
	}

	public HorarioTutor getHorario() {
		return horario;
	}

	public void setHorario(HorarioTutor horario) {
		this.horario = horario;
	}

	public DataModel getHorarios() {
		return horarioDM;
	}

	public TutorOrientador getTutorOrient() {
		return tutorOrient;
	}

	public void setTutorOrient(TutorOrientador tutorOrient) {
		this.tutorOrient = tutorOrient;
	}

	public void setCursosCombo(List<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}

	public List<SelectItem> getCursosCombo() {
		return cursosCombo;
	}

	public void setAssociarEm(List<String> associarEm) {
		this.associarEm = associarEm;
	}

	public List<String> getAssociarEm() {
		return associarEm;
	}

	public String getMensagemSucesso() {
		return mensagemSucesso;
	}

	public void setMensagemSucesso(String mensagemSucesso) {
		this.mensagemSucesso = mensagemSucesso;
	}

	public String getMensagemErro() {
		return mensagemErro;
	}

	public void setMensagemErro(String mensagemErro) {
		this.mensagemErro = mensagemErro;
	}
	
	
}
