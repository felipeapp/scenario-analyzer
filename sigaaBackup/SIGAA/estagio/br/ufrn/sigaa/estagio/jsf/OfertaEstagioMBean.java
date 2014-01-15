/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Este MBean tem como finalidade de auxiliar nas operações relacionadas a Ofertas de Estágio
 * 
 * @author Arlindo Rodrigues
 *
 */
@Component("ofertaEstagioMBean") @Scope("session")
public class OfertaEstagioMBean extends SigaaAbstractController<OfertaEstagio> implements SeletorConvenioEstagio {
	/** Lista de Ofertas disponíveis */
	private Collection<OfertaEstagio> listaOfertas = new ArrayList<OfertaEstagio>();
	/** Lista de Ofertas que o discente possui interesse */
	private Collection<OfertaEstagio> listaInteresse = new ArrayList<OfertaEstagio>();
	
	/** MBean auxiliar, utilizado na consulta de Concedente de Estágio */
	@Autowired
	private ConvenioEstagioMBean convenioEstagioMBean;
	
	/** Convênio de Estágio Selecionado */
	private ConcedenteEstagio concedente;
	
	/** Curso Selecionado */
	private Curso curso;
	
	/** Atributos que auxiliam na busca de ofertas de estágio*/
	/** Indica que foi selecionado o concedente para realizar a busca */
	private boolean filtroEmpresa;
	/** Indica que foi selecionado o período para realizar a busca */
	private boolean filtroPeriodo;
	/** Indica que foi selecionado o curso para realizar a busca */
	private boolean filtroCurso;
	/** Indica que foi selecionado o título da oferta para realizar a busca */
	private boolean filtroTitulo;
	/** Dados que foi informado na busca */
	private String empresa;
	/** Dados que foi informado na busca */
	private String titulo;
	/** Dados que foi informado na busca */
	private Date dataInicio;
	/** Dados que foi informado na busca */
	private Date dataFim;
	
	/** Atributo que auxilia para indicar se é cadastro ou não */
	private boolean cadastro;
	
	/** Atributo que indica se é para exibir apenas as ofertas que estão abertas */
	public boolean exibirOfertasAbertas;
	/** Indica que é pra ser realizado um novo cadastro */
	private boolean novoCadastro;
	
	/** Coleção de outras ofertas de estágio que não são para o curso do discente. */
	private Collection<OfertaEstagio> outrasOfertas;
	
	/**
	 * Construtor Padrão
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
	 * Inicia o Cadastro de Oferta de Estágio
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Seleciona o Convênio encontrado na busca.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Inicia a análise do estágio
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Inicia a alteração da Oferta de Estágio.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Lista as Ofertas de Estágio Disponíveis para o Discente Logado
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
				/* Busca a Lista de ofertas disponíveis para o discente logado */
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
	 * Inicia a Consulta de Ofertas de Estágio
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Busca Ofertas de Estágios Cadastradas conforme os parâmetros informados.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Título");
		}		
		
		if (filtroPeriodo) {
			if (ValidatorUtil.isEmpty(dataInicio) || ValidatorUtil.isEmpty(dataFim))
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período de Publicação");
			else validaInicioFim(dataInicio, dataFim, "Período de Publicação", erros);
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
	 * Método não chamado por JSP.
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
	 * Redireciona para a tela do formulário de cadastro
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Redireciona para a tela de busca de Ofertas de estágios
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/interesse_oferta/view.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaLista(){
		return forward(getListPage());
	}
	
	/**
	 * Redireciona para a tela do Visualização dos dados
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Redireciona para a tela do Visualização dos dados
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Cadastra a oferta de estágio
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/view.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		try {
			
			if (getUltimoComando().equals(ArqListaComando.ALTERAR)) {
				validateRequired(obj.getStatus(), "Situação", erros);
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
	 * Notifica os coordenadores de curso da criação de uma oferta de estágio. 
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
						mail.setAssunto("SIGAA - Cadastro de Oferta de Estágio - MENSAGEM AUTOMÁTICA");
						mail.setMensagem("Caro(a) "
								+ coordenador.getServidor().getPessoa().getNome()
								+ ",<br/><br/>"
								+ "A oferta de estágio  "
								+ obj.getTitulo()
								+ ", cadastrada por "
								+ obj.getConcedente().getResponsavel().getPessoa().getNome()
								+ ", responsável pela empresa "
								+ obj.getConcedente().getPessoa().getNome()
								+ ", está pendente de sua análise.<br/><br/>"
								+ "Para realizar a análise siga o caminho: SIGAA -> Portal Coord. Graduação -> Estágio -> Oferta de Estagio -> Consultar Oferta de Estágio.<br/><br/>"
								+ "ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA SIGAA.<br/>"
								+ "POR FAVOR, NÃO RESPONDÊ-LO.");
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
	 * Método Chamado pela seguinte JSP:
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
	 * Método Chamado pela seguinte JSP:
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
	
	/** Indica se o usuário poderá selecionar um ou mais cursos para a oferta de estágios
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
	 * Retorna as situações de oferta de estágio em formato de combo
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
				lista.addErro("O Convênio selecionado não permite cadastro de oferta de vagas pela Coordenação do Curso.");
			if (isPortalConcedenteEstagio() && !convenioEstagio.getTipoOfertaVaga().isOfertadoEmpresa())
				lista.addErro("O Convênio selecionado não permite cadastro de oferta de vagas pela empresa. Por favor, entre em contato com o coordenador do curso para solicitar a oferta da vaga de estágio.");
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
