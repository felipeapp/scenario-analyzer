/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '03/12/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.ensino.InscricaoSelecaoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.HistoricoEnvioRelatorioLatoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.latosensu.dominio.ArquivoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.HistoricoEnvioRelatorioLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.RelatorioFinalLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.TipoStatusRelatorioFinalLato;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaComponenteCursoLato;

/**
 * MBean usado para realizar cadastros de relat�rios finais
 * de cursos lato sensu
 * 
 * @author Leonardo Campos
 *
 */
public class RelatorioFinalLatoMBean extends SigaaAbstractController<RelatorioFinalLato> {
	
	public static final String MENU_COORDENADOR_LATO = "/lato/coordenador.jsf";
	public static final String JSP_FORM = "/lato/relatorio_final/form.jsf";
	public static final String JSP_LISTA = "/lato/relatorio_final/lista.jsf";
	public static final String JSP_RESUMO = "/lato/relatorio_final/resumo.jsf";
	
	/**
	 * Arquivo contendo o Relat�rio Final que ser� armazenado (uploaded).
	 */
	private UploadedFile file;
	
	/**
	 * Descri��o do arquivo que cont�m o Relat�rio Final.
	 */
	private String descricaoArquivo;
	
	/**
	 * Vari�vel que permite ou n�o a visualiza��o do Relat�rio Final.
	 */
	private boolean view;
	
	/**
	 * Hist�rico do Relat�rio final de Lato Sensu.
	 */
	private HistoricoEnvioRelatorioLato historico;
	
	/** Armazenas as discplinas para ser exibido no relat�rio final */
	private Collection<LinhaComponenteCursoLato> disciplinas;
	
	private Collection<MatriculaComponente> componentes;
	
	private HashMap<String, Collection<Integer>> quantitativoProcessoSeletivo; 
	
	public RelatorioFinalLatoMBean(){
		initObj();
	}
	
	/**
	 * Inicializa os objetos necess�rios para a realiza��o do caso de uso.
	 */
	private void initObj(){
		obj = new RelatorioFinalLato();
		obj.setCurso( new CursoLato() );
	}
	
	/**
	 * Popula as informa��es necess�rias para a submiss�o do relat�rio final de curso lato sensu.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\menu_coordenador.jsp</li>
	 *   </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciar() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.COORDENADOR_LATO);
		
		GenericDAO dao = getGenericDAO();
		CursoLato cursoLato = (CursoLato) getCursoAtualCoordenacao();
		String forward = null;
		HistoricoEnvioRelatorioLatoDao historicoDao = null;
		
		carregarDisciplinas(cursoLato);
		carregarFrequenciaDiscente(cursoLato);
		carregarInformacoesProcessoSeletivo(cursoLato);
		
		try {
			//verifica se j� foi submetido relat�rio
			Collection<RelatorioFinalLato> col = dao.findByExactField(RelatorioFinalLato.class, "curso.id", cursoLato.getId());
			obj.setCurso( dao.findByPrimaryKey(cursoLato.getId(), CursoLato.class) );
			if( col.isEmpty() ){
				obj.setStatus( TipoStatusRelatorioFinalLato.SUBMETIDO );
				forward = getFormPage();
				prepareMovimento(ArqListaComando.CADASTRAR);
			}else{
				RelatorioFinalLato rel = col.iterator().next(); 
				obj = dao.findByPrimaryKey(rel.getId(), RelatorioFinalLato.class);
				if(obj.getStatus() == TipoStatusRelatorioFinalLato.INCOMPLETO){
					forward = getFormPage();
					prepareMovimento(ArqListaComando.ALTERAR);
					setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
				} else if (obj.getStatus() == TipoStatusRelatorioFinalLato.NAO_APROVADO) {
					forward = getFormPage();
					prepareMovimento(ArqListaComando.ALTERAR);
					setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
					historicoDao = getDAO(HistoricoEnvioRelatorioLatoDao.class);
					historico = new HistoricoEnvioRelatorioLato();
					historico = historicoDao.findByLastHistorico(obj.getId());
				} else {
					setView(true);
					forward = JSP_RESUMO;
					prepareMovimento(ArqListaComando.ALTERAR);
					setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
				}
			}
			
			obj.setAnoProcesso(cursoLato.getPropostaCurso().getAnoProcesso());
			obj.setNumeroPortaria(cursoLato.getPropostaCurso().getNumeroPortaria());
			obj.setDataPortaria(cursoLato.getPropostaCurso().getDataPublicacaoPortaria());
			
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			return forward(MENU_COORDENADOR_LATO);
		}
		
		return forward( forward );
	}
	
	/**
	 * Carregar as Disciplinas do Curso Lato Sensu.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>M�todo n�o invocado por JSP.</li>
	 *   </ul>
	 *   
	 * @param cursoLato
	 * @throws DAOException
	 */
	private void carregarDisciplinas(CursoLato cursoLato) throws DAOException{
		CursoLatoMBean mbean = getMBean("cursoLatoMBean");
		mbean.setObj(cursoLato);
		disciplinas = mbean.getAllDisciplinasCursoLato();
	}
	
	/** 
	 * Carrega as informa��es das disciplinas cadastradas no curso.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>M�todo n�o invocado por JSP.</li>
	 *   </ul>
	 * 
	 * @param cursoLato
	 * @throws DAOException
	 */
	private void carregarFrequenciaDiscente(CursoLato cursoLato) throws DAOException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		TurmaDao turmaDao = getDAO(TurmaDao.class);
		try {
			
			Collection<Turma> turmas = turmaDao.findByCursoLato( cursoLato.getId() );

			componentes = new ArrayList<MatriculaComponente>();
			for (Turma t : turmas)
				componentes.addAll( dao.findByTurma( t.getId()) );

		} finally {
			dao.close();
			turmaDao.close();
		}
	}

	/**
	 * Carrega as informa��es dos trabalhos de conclus�o de curso.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>M�todo n�o invocado por JSP.</li>
	 *   </ul>
	 * @throws DAOException 
	 * 
	 */
	private void carregarInformacoesProcessoSeletivo(CursoLato curso) throws DAOException{
		InscricaoSelecaoDao dao = getDAO(InscricaoSelecaoDao.class);
		try {
			quantitativoProcessoSeletivo = dao.findByQuantInscricoes(curso.getId());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Carrega as informa��es dos trabalhos de conclus�o de curso.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>M�todo n�o invocado por JSP.</li>
	 *   </ul>
	 * 
	 */
	private void carregarTrabalhoConclusao(){
		
	}
	
	@Override
	protected void afterCadastrar() {
		initObj();
	}

	/**
	 * Redireciona para menu inicial, depois de realizar uma dada opera��o sobre o relatporio.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\relatorio_final\lista.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	@Override
	public String forwardCadastrar() {
		if(getAcessoMenu().isLato())
			return "/ensino/menus/menu_lato.jsf";
		else
			return MENU_COORDENADOR_LATO;
	}
	
	@Override
	public String getFormPage() {
		return JSP_FORM;
	}
	
	@Override
	public String getListPage() {
		return JSP_LISTA;
	}
	
	/**
	 * Retorna a quantidade de docentes pertencentes ao quadro da pr�pria institui��o que participaram
	 * do curso lato sensu ao qual o relat�rio se refere.
	 * @return
	 */
	public long getNumeroDocentesUFRN(){
		ServidorDao dao = getDAO(ServidorDao.class);
		try{
			return dao.findQtdDocentesCursoLato(false, obj.getCurso().getId());
		}catch (Exception e) {
			notifyError(e);
		}
		return 0;
	}
	
	/**
	 * Retorna a quantidade de docentes que n�o pertencem � institui��o e que participaram
	 * do curso lato sensu ao qual o relat�rio se refere.
	 * @return
	 */
	public long getNumeroDocentesOutrasIes(){
		ServidorDao dao = getDAO(ServidorDao.class);
		try{
			return dao.findQtdDocentesCursoLato(true, obj.getCurso().getId());
		}catch (Exception e) {
			notifyError(e);
		}
		return 0;
	}
	
	/**
	 * Busca registros de relat�rios de acordo com os par�metros informados.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\ensino\menus\menu_lato.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	@Override
	public String buscar() throws Exception {
		String param = getParameter("paramBusca");
		if (param == null) {
			param = "todos";
		}

		GenericDAO  dao = getGenericDAO();
		if ("nome".equalsIgnoreCase(param))
			setResultadosBusca( dao.findByLikeField(RelatorioFinalLato.class, "curso.nome", obj.getCurso().getDescricao()) );
		else if ("todos".equalsIgnoreCase(param))
			setResultadosBusca(dao.findAll(RelatorioFinalLato.class));
		else
			setResultadosBusca(null);

		initObj();
		return forward(JSP_LISTA);
	}
	
	/**
	 * Visualizar resumo do relat�rio.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\relatorio_final\resumo.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String verResumo(){
		setConfirmButton("Submeter Relat�rio");
		return forward(JSP_RESUMO);
	}
	
	/**
	 * Retorna para a tela anterior de acordo com a opera��o vigente.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\relatorio_final\resumo.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String voltarForm(){
		if (isPortalCoordenadorLato()) 
			return forward(MENU_COORDENADOR_LATO);
		if(isView())
			return forward(getListPage());
		else
			return forward(getFormPage());
	}
	
	/**
	 * Mostrar detalhes de um relat�rio final.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\relatorio_final\lista.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String detalhar() throws ArqException {
		GenericDAO dao = getGenericDAO();
		String id = getParameter("id");
		obj.setId( Integer.parseInt(id) );

		setView(true);
		
		obj = dao.findByPrimaryKey(obj.getId(), RelatorioFinalLato.class);
		
		if(getAcessoMenu().isLato() && getSubSistema().equals(SigaaSubsistemas.LATO_SENSU)){
			prepareMovimento(ArqListaComando.ALTERAR);
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		}
				
		return forward(JSP_RESUMO);
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile fileRecursos) {
		this.file = fileRecursos;
	}
	
	/**
	 * Adiciona um arquivo ao relat�rio final.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\relatorio_final\form.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String anexarArquivo() {

		try {

			if ((descricaoArquivo == null) || ("".equals(descricaoArquivo.trim()))){
				addMensagemErro("Descri��o do arquivo � obrigat�ria!");
				return null;
			}

			if ((file == null)){
				addMensagemErro("Informe o caminho completo do arquivo.");
				return null;
			}

			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idArquivo, file.getBytes(), file.getContentType(), file.getName());

			ArquivoLato arquivo = new ArquivoLato();
			arquivo.setDescricao(descricaoArquivo);
			arquivo.setIdArquivo(idArquivo);

			obj.addArquivo(arquivo);
			
			// grava no banco...
			gravar();
			
			addMessage("Arquivo Anexado com Sucesso!", TipoMensagemUFRN.INFORMATION);
			descricaoArquivo = "";

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		return null;
	}
		
	/**
	 * Remove o arquivo da lista de anexos da atividade.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\relatorio_final\form.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String removeAnexo() {
		ArquivoLato arquivo = new ArquivoLato();
		arquivo.setIdArquivo( Integer.parseInt( getParameter("idArquivo") ) );
		obj.getArquivos().remove( arquivo );
		return null;
	}

	/**
	 * Grava um relat�rio final, seja o cadastro de um novo ou altera��o de um j� existente.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\relatorio_final\form.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String gravar() throws SegurancaException, ArqException, NegocioException{
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		obj.setStatus(TipoStatusRelatorioFinalLato.INCOMPLETO);

		obj.setDataPortaria(obj.getCurso().getPropostaCurso().getDataPublicacaoPortaria());
		obj.setNumeroPortaria(obj.getCurso().getPropostaCurso().getNumeroPortaria());
		
		if (obj.getId() == 0)
			mov.setCodMovimento(ArqListaComando.CADASTRAR);
		else
			mov.setCodMovimento(ArqListaComando.ALTERAR);
		
		executeWithoutClosingSession(mov, getCurrentRequest());
		addMessage("Relat�rio gravado com sucesso", TipoMensagemUFRN.INFORMATION);
		prepareMovimento(ArqListaComando.ALTERAR);
		return forward(JSP_FORM);
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		obj.validate();
		if (hasErrors())
			return null;

		super.cadastrar();
		
		if (hasErrors()) {
			return null;
		}
		
		List<RelatorioFinalLato> relatorio = 
				(List<RelatorioFinalLato>) getGenericDAO().findByExactField( RelatorioFinalLato.class, "curso.id",  
						getCursoAtualCoordenacao() != null ? ((CursoLato) getCursoAtualCoordenacao()).getId() : obj.getCurso().getId());
		
		if ( !relatorio.isEmpty() ) {
			setObj( relatorio.get(0) );
				if ( isLatoSensu() )
					notificarSituacaoHistoricoCoordenador(obj);
			cadastrarHistoricoParecer(obj);
		}
		
		return redirectJSF(getSubSistema().getLink());  
	}
	
	/**
	 * Pr�-Cadastro do relat�rio: define a situa��o do relat�rio como submetido.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\relatorio_final\lista.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,
			SegurancaException, DAOException {
		if(!getAcessoMenu().isLato())
			obj.setStatus(TipoStatusRelatorioFinalLato.SUBMETIDO);
	}

	/**
	 * Cadastro do hist�rico do relat�rio final dos curso de Lato Sensu. 
	 * 
	 * @param relatorio
	 * @throws DAOException
	 */
	private void cadastrarHistoricoParecer(RelatorioFinalLato relatorio) throws DAOException {
		HistoricoEnvioRelatorioLato historico = new HistoricoEnvioRelatorioLato();
		historico.setParecer(relatorio.getParecer());
		historico.setDataCadastro(new Date());
		historico.setStatusAtual(relatorio.getStatus());
		historico.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada().getId());
		historico.setRelatorio(relatorio);
		getGenericDAO().create(historico);
	}
	
	/**
	 * Notifica o coordenador do curso Lato sensu sobre o parecer dado ao relat�rio final do curso. 
	 * 
	 * @param relatorio
	 */
	private static void notificarSituacaoHistoricoCoordenador(RelatorioFinalLato relatorio){
		  StringBuilder mensagem = new StringBuilder("Caro(a) Coordenador(a) "+ relatorio.getCurso().getCoordenacao().getServidor().getPessoa().getNome() +", <br> " +
		  "o relat�rio final do curso: " + relatorio.getCurso().getNomeCompleto());
		  if (!relatorio.getParecer().equals(""))
			  mensagem.append(" teve o seguinte parecer: " + relatorio.getParecer() + " � ");
		  mensagem.append(" mudou para a situa��o: " + relatorio.getStatusString() + "<br> ");
		   
		  if (!relatorio.getParecer().equals("") 
				  && relatorio.getStatus() == TipoStatusRelatorioFinalLato.NAO_APROVADO)
			 mensagem.append("Para visualizar o parecer basta seguir o caminho: <br>" +
			 		" SIGAA -> Portal Coord. Lato Sensu -> Curso -> Submeter Relat�rio Final");
		  
		  // enviando e-mail.
		  MailBody email = new MailBody();
		  email.setAssunto("[SIGAA] Notifica��o de Mudan�a da Situa��o do Relat�rio Final Curso Lato");
		  email.setContentType(MailBody.HTML);
		  email.setNome(relatorio.getCurso().getCoordenacao().getServidor().getPessoa().getNome());
		  email.setEmail(relatorio.getCurso().getCoordenacao().getServidor().getPessoa().getEmail());
		  email.setMensagem(mensagem.toString());
		  Mail.send(email);
	}
	
	/**
	 * Visualizar um arquivo anexo do relat�rio.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\lato\relatorio_final\lista.jsp</li>
	 *   </ul>
	 *
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void viewArquivo() {
		try {
			int idArquivo = getParameterInt("idArquivo");
			EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo,
					false);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Arquivo n�o encontrado!");
		}
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	public String getDescricaoArquivo() {
		return descricaoArquivo;
	}

	public void setDescricaoArquivo(String descricaoArquivo) {
		this.descricaoArquivo = descricaoArquivo;
	}
	
	public boolean isView() {
		return view;
	}

	public void setView(boolean view) {
		this.view = view;
	}
	
	public Collection<SelectItem> getTiposCombo(){
		return toSelectItems(TipoStatusRelatorioFinalLato.getTipos());
	}

	public HistoricoEnvioRelatorioLato getHistorico() {
		return historico;
	}

	public void setHistorico(HistoricoEnvioRelatorioLato historico) {
		this.historico = historico;
	}

	public Collection<LinhaComponenteCursoLato> getDisciplinas() {
		return disciplinas;
	}

	public void setDisciplinas(Collection<LinhaComponenteCursoLato> disciplinas) {
		this.disciplinas = disciplinas;
	}

	public Collection<MatriculaComponente> getComponentes() {
		return componentes;
	}

	public void setComponentes(Collection<MatriculaComponente> componentes) {
		this.componentes = componentes;
	}

	public HashMap<String, Collection<Integer>> getQuantitativoProcessoSeletivo() {
		return quantitativoProcessoSeletivo;
	}

	public void setQuantitativoProcessoSeletivo(
			HashMap<String, Collection<Integer>> quantitativoProcessoSeletivo) {
		this.quantitativoProcessoSeletivo = quantitativoProcessoSeletivo;
	}

	@Override
	protected void doValidate() throws ArqException {
		// Se for o Gestor do Lato Sensu ignora as mensagens de valida��o.
		if(getAcessoMenu().isLato() && getSubSistema().equals(SigaaSubsistemas.LATO_SENSU) && hasErrors()){
			erros.getMensagens().clear();
			getCurrentSession().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, erros);
		}
	}

}