/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 07/10/2010
 */

package br.ufrn.sigaa.estagio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validaInicioFim;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.estagio.OfertaEstagioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagio;
import br.ufrn.sigaa.estagio.dominio.ConvenioEstagio;
import br.ufrn.sigaa.estagio.dominio.OfertaEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusConvenioEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusOfertaEstagio;
import br.ufrn.sigaa.estagio.dominio.TipoConvenio;

/**
 * Este MBean tem como finalidade de auxiliar nas opera��es relacionadas a Ofertas de Est�gio
 * 
 * @author Arlindo Rodrigues
 *
 */
@Component("ofertaEstagioMBean") @Scope("session")
public class OfertaEstagioMBean extends SigaaAbstractController<OfertaEstagio> implements SeletorConvenioEstagio {
	/** Lista de Ofertas dispon�veis */
	private Collection<OfertaEstagio> listaOfertas = new ArrayList<OfertaEstagio>();
	/** Lista de Ofertas que o discente possui interesse */
	private Collection<OfertaEstagio> listaInteresse = new ArrayList<OfertaEstagio>();
	
	/** MBean auxiliar, utilizado na consulta de Concedente de Est�gio */
	@Autowired
	private ConvenioEstagioMBean convenioEstagioMBean;
	
	/** Conv�nio de Est�gio Selecionado */
	private ConcedenteEstagio concedente;
	
	/** Curso Selecionado */
	private Curso curso;
	
	/** Atributos que auxiliam na busca de ofertas de est�gio*/
	/** Indica que foi selecionado o concedente para realizar a busca */
	private boolean filtroEmpresa;
	/** Indica que foi selecionado o per�odo para realizar a busca */
	private boolean filtroPeriodo;
	/** Indica que foi selecionado o curso para realizar a busca */
	private boolean filtroCurso;
	/** Indica que foi selecionado o t�tulo da oferta para realizar a busca */
	private boolean filtroTitulo;
	/** Dados que foi informado na busca */
	private String empresa;
	/** Dados que foi informado na busca */
	private String titulo;
	/** Dados que foi informado na busca */
	private Date dataInicio;
	/** Dados que foi informado na busca */
	private Date dataFim;
	
	/** Atributo que auxilia para indicar se � cadastro ou n�o */
	private boolean cadastro;
	
	/** Atributo que indica se � para exibir apenas as ofertas que est�o abertas */
	public boolean exibirOfertasAbertas;
	/** Indica que � pra ser realizado um novo cadastro */
	private boolean novoCadastro;
	
	/** Cole��o de outras ofertas de est�gio que n�o s�o para o curso do discente. */
	private Collection<OfertaEstagio> outrasOfertas;
	
	/**
	 * Construtor Padr�o
	 */
	public OfertaEstagioMBean() {
		obj = new OfertaEstagio();

		if (isEmpty(obj.getConcedente()))
			obj.setConcedente(new ConcedenteEstagio());
		
		curso = new Curso();
		cadastro = false;
		
		exibirOfertasAbertas = false;
	}
	
	/**
	 * Inicia o Cadastro de Oferta de Est�gio
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException 
	 * @throws ArqException 
	 */
	public String iniciar() throws HibernateException, ArqException{			
		Integer[] statusConvenioEstagio = {StatusConvenioEstagio.APROVADO};
		ConvenioEstagioMBean convenioEstagioMBean = getMBean("convenioEstagioMBean");
		obj = new OfertaEstagio();
		concedente = new ConcedenteEstagio();
		return convenioEstagioMBean.iniciarSeletorEstagio(this, statusConvenioEstagio);
	}
	
	/**
	 * Seleciona o Conv�nio encontrado na busca.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/concedente_estagio/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */	
	public String iniciarCadastro() throws ArqException{	
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, 
				SigaaPapeis.PORTAL_CONCEDENTE_ESTAGIO, SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN);
		prepareMovimento(ArqListaComando.CADASTRAR);
		obj.setConcedente(concedente);
		cadastro = true;
		if (isPortalCoordenadorGraduacao() && !isEmpty(getCursoAtualCoordenacao()) && 
				(isEmpty(obj.getCursosOfertados()) || 
				!obj.getCursosOfertados().contains(getCursoAtualCoordenacao()))){
			obj.adicionarCurso(getCursoAtualCoordenacao());				
		}
		return telaForm();
	}
	
	/**
	 * Inicia a an�lise do est�gio
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */	
	public String iniciarAnalise() throws ArqException{
		populateObj();
		obj.getCursosOfertados();
		prepareMovimento(ArqListaComando.ALTERAR);		
		cadastro = true;
		return forward(getFormAnalise());		
	}	
	
	/**
	 * Inicia a altera��o da Oferta de Est�gio.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */	
	public String alterar() throws ArqException{	
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, 
				SigaaPapeis.PORTAL_CONCEDENTE_ESTAGIO, SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN);
		populateObj();
		prepareMovimento(ArqListaComando.ALTERAR);	
		cadastro = true;
		return telaForm();
	}	
	
	/**
	 * Lista as Ofertas de Est�gio Dispon�veis para o Discente Logado
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String listarOfertasDisponiveis() throws HibernateException, DAOException{		
		OfertaEstagioDao dao = getDAO(OfertaEstagioDao.class);
		try {
			outrasOfertas = new LinkedList<OfertaEstagio>();
			if (isPortalDiscente()){
				Date hoje = new Date();
				/* Busca a Lista de ofertas dispon�veis para o discente logado */
				listaOfertas = dao.findGeral(getDiscenteUsuario().getCurso(), null, null, null, hoje, hoje, null, true);
				outrasOfertas = dao.findGeral(null, null, null, null, hoje, hoje, null, true);
				// filtra as listas por repetidos
				if (!isEmpty(listaOfertas) && !isEmpty(outrasOfertas)){
					for (OfertaEstagio minha : listaOfertas) {
						Iterator<OfertaEstagio> iterator = outrasOfertas.iterator();
						while (iterator.hasNext()) 
							if (iterator.next().getId() == minha.getId()) 
								iterator.remove();
					}
				}
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		return forward(getListPage());
	}
	
	public Collection<OfertaEstagio> getOutrasOfertas() {
		return outrasOfertas;
	}

	public void setOutrasOfertas(Collection<OfertaEstagio> outrasOfertas) {
		this.outrasOfertas = outrasOfertas;
	}

	/**
	 * Inicia a Consulta de Ofertas de Est�gio
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarConsulta() throws SegurancaException, HibernateException, DAOException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.DOCENTE, 
				SigaaPapeis.PORTAL_CONCEDENTE_ESTAGIO, SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN, SigaaPapeis.COORDENADOR_ESTAGIOS);
		if (isPortalConcedenteEstagio() || isPortalDocente()){
			exibirOfertasAbertas = true;
			return filtrar();
		}
		return forward(getListPage());
	}
	
	/**
	 * Busca Ofertas de Est�gios Cadastradas conforme os par�metros informados.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String buscar() throws HibernateException, DAOException{	
		if (!isSelecionaCurso()) {
			filtroCurso = true;
			curso = getCursoAtualCoordenacao();
		}
		
		if (isPortalDiscente()) {
			filtroCurso = true;
			curso = getDiscenteUsuario().getCurso();
		}
		
		if (!filtroEmpresa && !filtroPeriodo && !filtroCurso && !filtroTitulo && !exibirOfertasAbertas){
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		}
		
		if (filtroEmpresa && ValidatorUtil.isEmpty(empresa)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Concedente");
		}
		
		if (filtroTitulo && ValidatorUtil.isEmpty(titulo)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "T�tulo");
		}		
		
		if (filtroPeriodo) {
			if (ValidatorUtil.isEmpty(dataInicio) || ValidatorUtil.isEmpty(dataFim))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Per�odo de Publica��o");
			else validaInicioFim(dataInicio, dataFim, "Per�odo de Publica��o", erros);
		}
		
		if (filtroCurso && curso == null){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		}	
		
		if (hasErrors()) return null;
		
		return filtrar();
	}	
	
	/**
	 * Atribui os Filtros a Consulta
	 * <br/><br/>
	 * M�todo n�o chamado por JSP.
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String filtrar() throws HibernateException, DAOException{
		OfertaEstagioDao dao = getDAO(OfertaEstagioDao.class);
		try {			
			listaOfertas = dao.findGeral(
					(filtroCurso || isPortalCoordenadorGraduacao() ? ( isPortalCoordenadorGraduacao() ? getCursoAtualCoordenacao() : curso ): null), 
					(filtroEmpresa ? empresa : null),
					(isPortalConcedenteEstagio() ? getUsuarioLogado().getPessoa().getCpf_cnpjString() : null),
					(filtroTitulo ? titulo : null), 
					(filtroPeriodo && dataInicio != null ? dataInicio : null),
					(filtroPeriodo && dataFim != null ? dataFim : null), null,
					(isPortalDiscente() || exibirOfertasAbertas ? true : false));			
		} finally {
			if (dao != null)
				dao.close();
		}		
		return telaLista();		
	}
	
	/**
	 * Redireciona para a tela do formul�rio de cadastro
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/view.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaForm(){
		if (isCadastro())
			return forward(getFormPage());		
		return telaLista();
	}
	
	/**
	 * Redireciona para a tela de busca de Ofertas de est�gios
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/interesse_oferta/view.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaLista(){
		return forward(getListPage());
	}
	
	/**
	 * Redireciona para a tela do Visualiza��o dos dados
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String confirmarDados() throws DAOException{
		erros = new ListaMensagens();
		erros.addAll(obj.validate().getMensagens());
		if (hasErrors())
			return null;
		return forward(getViewPage());
	}
	/**
	 * Redireciona para a tela do Visualiza��o dos dados
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String view() throws DAOException{
		populateObj();
		cadastro = false;
		return forward(getViewPage());
	}
	
	/**
	 * Cadastra a oferta de est�gio
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/view.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		try {
			
			if (getUltimoComando().equals(ArqListaComando.ALTERAR)) {
				validateRequired(obj.getStatus(), "Situa��o", erros);
				if (hasErrors()) return null;
			}
			if (getUltimoComando().equals(ArqListaComando.CADASTRAR)){
				if (isPortalCoordenadorGraduacao() || isUserInRole(SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN))
					obj.setStatus(new StatusOfertaEstagio( StatusOfertaEstagio.APROVADO ));
				else
					obj.setStatus(new StatusOfertaEstagio( StatusOfertaEstagio.CADASTRADO ));				
			}

			// Prepara o movimento, setando o objeto
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(getUltimoComando());
			execute(mov);
			
			if (isPortalConcedenteEstagio() && obj.isPendenteAnalise())
				notificarCadastroOferta();
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, TipoMensagemUFRN.INFORMATION);	
			
			if (isPortalDiscente())
				return listarOfertasDisponiveis();

			return filtrar();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return telaForm();
		}				
	}
	
	/**
	 * Notifica os coordenadores de curso da cria��o de uma oferta de est�gio. 
	 * @throws DAOException 
	 */
	private void notificarCadastroOferta() throws DAOException {
		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class);
		for (Curso curso : obj.getCursosOfertados()) {
			Collection<CoordenacaoCurso> coordenadores = dao.findByCurso(curso.getId(), 0, (char) 0, null);
			if (!isEmpty(coordenadores)) {
				for (CoordenacaoCurso coordenador : coordenadores ) {
					if (coordenador.isAtivo() && coordenador.isVigente() && !isEmpty(coordenador.getServidor().getPessoa().getEmail())) {
						MailBody mail = new MailBody();
						mail.setContentType(MailBody.HTML);
						mail.setAssunto("SIGAA - Cadastro de Oferta de Est�gio - MENSAGEM AUTOM�TICA");
						mail.setMensagem("Caro(a) "
								+ coordenador.getServidor().getPessoa().getNome()
								+ ",<br/><br/>"
								+ "A oferta de est�gio  "
								+ obj.getTitulo()
								+ ", cadastrada por "
								+ obj.getConcedente().getResponsavel().getPessoa().getNome()
								+ ", respons�vel pela empresa "
								+ obj.getConcedente().getPessoa().getNome()
								+ ", est� pendente de sua an�lise.<br/><br/>"
								+ "Para realizar a an�lise siga o caminho: SIGAA -> Portal Coord. Gradua��o -> Est�gio -> Oferta de Estagio -> Consultar Oferta de Est�gio.<br/><br/>"
								+ "ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA SIGAA.<br/>"
								+ "POR FAVOR, N�O RESPOND�-LO.");
						mail.setEmail(coordenador.getServidor().getPessoa().getEmail());
						mail.setNome(coordenador.getServidor().getPessoa().getNome());
						Mail.send(mail);
					}
				}
			}
		}
	}

	/**
	 * Adiciona o Curso a Lista de Cursos Ofertados
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/form.jsp</li>
	 * </ul>
	 * @param event
	 */
	public void adicionarCurso(ActionEvent event){
		if (!isEmpty(curso)) {
			obj.adicionarCurso( curso );
			curso = new Curso();
		} 
	}
	
	/**
	 * Remove o Curso da lista de Cursos Ofertados
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/form.jsp</li>
	 * </ul>
	 * @param event
	 */
	public void removerCurso(ActionEvent event){
		curso = (Curso) event.getComponent().getAttributes().get("curso");
		if (!isEmpty(curso)) {
			obj.removerCurso( curso );
			curso = new Curso();
		}		
	}
	
	/** Indica se o usu�rio poder� selecionar um ou mais cursos para a oferta de est�gios
	 * @return
	 */
	public boolean isSelecionaCurso() {
		if (isPortalCoordenadorGraduacao()) {
			if (isUserInRole(SigaaPapeis.COORDENADOR_ESTAGIOS))
				return true;
			else 
				return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Retorna as situa��es de oferta de est�gio em formato de combo
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/form_analise.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getStatusCombo() throws DAOException{
		return toSelectItems(getGenericDAO().findAll(StatusOfertaEstagio.class), "id", "descricao");		
	}
	
	@Override
	public String getFormPage() {
		return "/estagio/oferta_estagio/form.jsp";
	}
	
	@Override
	public String getViewPage() {
		return "/estagio/oferta_estagio/view.jsp";
	}
	
	@Override
	public String getListPage() {
		return "/estagio/oferta_estagio/lista.jsp";
	}
	
	public String getFormAnalise(){
		return "/estagio/oferta_estagio/form_analise.jsp";
	}	
	
	public boolean isBolsaObrigatoria() {
		return obj.getConcedente().getConvenioEstagio().isOrgaoFederal() && 
				obj.getConcedente().getConvenioEstagio().getTipoConvenio().getId() == TipoConvenio.ESTAGIO_CURRICULAR_NAO_OBRIGATORIO;
	}
	
	public boolean isAuxilioTransporteObrigatorio(){
		return obj.getConcedente().getConvenioEstagio().isOrgaoFederal() && 
				obj.getConcedente().getConvenioEstagio().getTipoConvenio().getId() == TipoConvenio.ESTAGIO_CURRICULAR_NAO_OBRIGATORIO;
	}

	public ConcedenteEstagio getConcedente() {
		return concedente;
	}

	public void setConcedente(ConcedenteEstagio concedente) {
		this.concedente = concedente;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean isCadastro() {
		return cadastro;
	}

	public void setCadastro(boolean cadastro) {
		this.cadastro = cadastro;
	}

	public Collection<OfertaEstagio> getListaOfertas() {
		return listaOfertas;
	}

	public void setListaOfertas(Collection<OfertaEstagio> listaOfertas) {
		this.listaOfertas = listaOfertas;
	}

	public boolean isFiltroEmpresa() {
		return filtroEmpresa;
	}

	public void setFiltroEmpresa(boolean filtroEmpresa) {
		this.filtroEmpresa = filtroEmpresa;
	}

	public boolean isFiltroPeriodo() {
		return filtroPeriodo;
	}

	public void setFiltroPeriodo(boolean filtroPeriodo) {
		this.filtroPeriodo = filtroPeriodo;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public boolean isFiltroTitulo() {
		return filtroTitulo;
	}

	public void setFiltroTitulo(boolean filtroTitulo) {
		this.filtroTitulo = filtroTitulo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Collection<OfertaEstagio> getListaInteresse() {
		return listaInteresse;
	}

	public void setListaInteresse(Collection<OfertaEstagio> listaInteresse) {
		this.listaInteresse = listaInteresse;
	}

	public boolean isNovoCadastro() {
		return novoCadastro;
	}

	public void setNovoCadastro(boolean novoCadastro) {
		this.novoCadastro = novoCadastro;
	}

	public boolean isExibirOfertasAbertas() {
		return exibirOfertasAbertas;
	}

	public void setExibirOfertasAbertas(boolean exibirOfertasAbertas) {
		this.exibirOfertasAbertas = exibirOfertasAbertas;
	}

	@Override
	public ListaMensagens validaConvenioSelecionado(ConvenioEstagio convenioEstagio) {
		ListaMensagens lista = new ListaMensagens();
		if (!isConvenioEstagio()) {
			if (isPortalCoordenadorGraduacao() && !convenioEstagio.getTipoOfertaVaga().isOfertadoCoordenacaoCurso())
				lista.addErro("O Conv�nio selecionado n�o permite cadastro de oferta de vagas pela Coordena��o do Curso.");
			if (isPortalConcedenteEstagio() && !convenioEstagio.getTipoOfertaVaga().isOfertadoEmpresa())
				lista.addErro("O Conv�nio selecionado n�o permite cadastro de oferta de vagas pela empresa. Por favor, entre em contato com o coordenador do curso para solicitar a oferta da vaga de est�gio.");
		}
		return lista;
	}

	@Override
	public String selecionaConvenioEstagio(ConvenioEstagio convenioEstagio) throws ArqException {
		concedente = new ConcedenteEstagio();
		concedente.setConvenioEstagio(convenioEstagio);
		concedente = convenioEstagio.getConcedente();
		return iniciarCadastro();
	}

	@Override
	public String seletorConvenioEstagioVoltar() {
		return cancelar();
	}
	
}
