/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/10/2010
 */
package br.ufrn.sigaa.estagio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.estagio.EstagiarioDao;
import br.ufrn.sigaa.arq.dao.estagio.InteresseOfertaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.estagio.dominio.InteresseOferta;
import br.ufrn.sigaa.estagio.dominio.OfertaEstagio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Este MBean tem como finalidade de auxiliar nas operações relacionadas a Interesse de Oferta de Estágio
 * 
 * @author Arlindo Rodrigues
 *
 */
@Component("interesseOfertaMBean") @Scope("session")
public class InteresseOfertaMBean extends SigaaAbstractController<InteresseOferta>  implements OperadorDiscente  {

	/** Discente Selecionado */
	private DiscenteAdapter discente;
	/** Oferta de Estágio Selecionada */
	private OfertaEstagio ofertaEstagio;
	
	/** Supervisor do Estágio */
	private Pessoa supervisor;
	
	/** Currículo Lattes do Perfil do Discente */
	private String curriculoLattes;
	
	/** Lista de Interesses Cadastrados */
	private Collection<InteresseOferta> listaInteresses = new ArrayList<InteresseOferta>();
	
	/** Construtor Padrão */
	public InteresseOfertaMBean() {
		obj = new InteresseOferta();
		obj.setOferta(new OfertaEstagio());
		obj.setDiscente(new DiscenteGraduacao());
	}
	
	/**
	 * Cadastra o Interesse na Oferta de Estágio selecionada 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{	
		
		ofertaEstagio = getGenericDAO().refresh(ofertaEstagio);
		
		if (!CalendarUtils.isDentroPeriodo(ofertaEstagio.getDataInicioPublicacao(), 
				ofertaEstagio.getDataFimPublicacao(), new Date())){
			addMensagemErro("Finalizou o Período de Publicação da Oferta: "+
					Formatador.getInstance().formatarData(ofertaEstagio.getDataInicioPublicacao()) + " a "+
					Formatador.getInstance().formatarData(ofertaEstagio.getDataFimPublicacao()));
			return null;
		}

		if (isPortalDiscente()){
			discente = getDiscenteUsuario();	
			if (!discente.isAtivo()){
				addMensagemErro("Somente alunos ativos podem se candicadar a vagas de Estágio.");
				return null;
			}
			boolean ofertadaAoCurso = isEmpty(ofertaEstagio.getCursosOfertados()) ? true : false;
			for (Curso curso : ofertaEstagio.getCursosOfertados())
				if (curso.getId() == discente.getCurso().getId()) {
					ofertadaAoCurso = true; break;
				}
			if (!ofertadaAoCurso) {
				addMensagemErro("Esta oferta de Estágio não foi direcionada para o seu curso.");
				return null;
			}
			EstagiarioDao dao = getDAO(EstagiarioDao.class);	
			List<Map<String, Object>> estagios = dao.findEstagiosComRelatoriosPendente(discente.getId());
			for (Map<String, Object> e : estagios){
				if (discente.getMatricula().equals(e.get("matricula"))) {
					addMensagemErro("O discente não poderá se inscrever em outro estágio se tiver relatórios de estágios pendentes de preenchimento.");
					return null;
				}
			}		
			return selecionaDiscente();
		} 
		return informaDiscente();
	}	
	
	/**
	 * Lista os interesses em aberto
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws SegurancaException 
	 */
	public String gerenciarInteresses() throws HibernateException, DAOException, SegurancaException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.PORTAL_CONCEDENTE_ESTAGIO);
		InteresseOfertaDao dao = getDAO(InteresseOfertaDao.class);
		try {
			listaInteresses = dao.findInteresses(ofertaEstagio, 
					(isPortalCoordenadorGraduacao() ? getCursoAtualCoordenacao() : null),
					(isPortalConcedenteEstagio() ? getUsuarioLogado().getPessoa() : null), true);
			if (ValidatorUtil.isEmpty(listaInteresses)){
				addMensagemErro("Não existe interesses em aberto.");
				return null;				
			}
			return forward(getListPage());		
		} finally {
			if (dao != null)
				dao.close();
		}			
	}
	
	/**
	 * Cancelar o Interesse do Discente informado.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cancelarInteresse() throws HibernateException, SegurancaException, ArqException, NegocioException{
		if (isPortalDiscente())
			discente = getDiscenteUsuario();
		
		if (ValidatorUtil.isEmpty(discente)){
			addMensagemErro("Discente não informado!");
			return null;
		}		
		
		InteresseOfertaDao dao = getDAO(InteresseOfertaDao.class);
		try {
			obj = dao.findInteresseByDiscenteOferta(discente, ofertaEstagio, true);
			if (ValidatorUtil.isEmpty(obj)){
				addMensagemErro("O Discente informado não possui Interesse na Oferta Selecionada!");
				return null;				
			}
			
			if (obj.isSelecionado()){
				addMensagemErro("Não é possível cancelar o Interesse, pois o discente já foi selecionado como Estagiário");
				return null;								
			}		
			
			prepareMovimento(SigaaListaComando.CANCELAR_INTERESSE_OFERTA_ESTAGIO);		
			setOperacaoAtiva(SigaaListaComando.CANCELAR_INTERESSE_OFERTA_ESTAGIO.getId());
			return cadastrar();
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Seleciona o interessado a uma vaga de estágio
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/interesseOferta/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarInteressado() throws ArqException{
		populateObj();
		if (obj.getOferta().getInteresses() != null)
			obj.getOferta().getInteresses().iterator();
		if (obj.getOferta().getCursosOfertados() != null)
			obj.getOferta().getCursosOfertados().iterator();
		obj.getOferta().getConcedente().getPessoa().getNome();
		prepareMovimento(SigaaListaComando.SELECIONAR_INTERESSADO);
		setOperacaoAtiva(SigaaListaComando.SELECIONAR_INTERESSADO.getId());
		supervisor = new Pessoa();
		return forward(getFormSelecaoEstagio());
	}
	
	/**
	 * Redireciona para selecionar um discente.
	 * <br/><br/>
	 * Método não chamado por JSP.
	 * @return
	 * @throws ArqException 
	 */
	public String informaDiscente() throws ArqException{
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");				
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.INTERESSE_OFERTA_ESTAGIO);
		return buscaDiscenteMBean.popular();
	}	
	
	/**
	 * Lista os Interesses da Oferta Selecionada
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/oferta_estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String view() throws HibernateException, DAOException{
		InteresseOfertaDao dao = getDAO(InteresseOfertaDao.class);
		try {
			listaInteresses = dao.findInteresses(ofertaEstagio, null, null, false);
			if (ValidatorUtil.isEmpty(listaInteresses)){
				addMensagemErro("Não existe interesse para oferta selecionada.");
				return null;				
			}
			// precarrega os perfis
			for (InteresseOferta interesse : listaInteresses) {
				PerfilPessoa perfil = PerfilPessoaDAO.getDao().findByDiscente(obj.getDiscente().getId());
				interesse.getDiscente().setPerfil(perfil);
			}
			return forward(getViewPage());			
		} finally {
			if (dao != null)
				dao.close();
		}		
	}
	
	/**
	 * Busca o Responsável pelo CPF
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/interesse_oferta/selecionar_estagiario.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void buscarCPF(ActionEvent e) throws DAOException{
		Long cpf = StringUtils.extractLong(String.valueOf(supervisor.getCpf_cnpj()));
		PessoaDao dao = null;
		try {
			if (cpf != null && cpf > 0){
				dao = getDAO(PessoaDao.class);
				supervisor = dao.findByCpf(cpf);
				if (ValidatorUtil.isEmpty( supervisor ))
					supervisor = new Pessoa();
			}
		} finally {
			if (dao != null)
				dao.close();
		}		
	}			
	
	/**
	 * Cadastra o Interesse a Oferta
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/estagio/interesse_oferta/resumo.jsp</li>
	 *   <li>/sigaa.war/estagio/interesse_oferta/selecionar_estagiario.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		if (getUltimoComando() == null || !checkOperacaoAtiva(SigaaListaComando.CADASTRAR_INTERESSE_OFERTA_ESTAGIO.getId(),
				SigaaListaComando.CANCELAR_INTERESSE_OFERTA_ESTAGIO.getId(),
				SigaaListaComando.SELECIONAR_INTERESSADO.getId(),
				SigaaListaComando.CADASTRAR_INTERESSE_OFERTA_ESTAGIO.getId())) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}
		try {
			// Prepara o movimento, setando o objeto
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(getUltimoComando());
			
			if (getUltimoComando().equals(SigaaListaComando.CADASTRAR_INTERESSE_OFERTA_ESTAGIO))
				mov.setObjAuxiliar(curriculoLattes);
			else if (getUltimoComando().equals(SigaaListaComando.SELECIONAR_INTERESSADO))
				mov.setObjAuxiliar(supervisor);
			
			execute(mov);			
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, TipoMensagemUFRN.INFORMATION);
			removeOperacaoAtiva();
			
			if (isPortalConcedenteEstagio()) {
				return view();
			} else {
				OfertaEstagioMBean mBean = getMBean("ofertaEstagioMBean");
				if (isPortalDiscente()){						
					mBean.listarOfertasDisponiveis();
				} else {
					mBean.filtrar();
				}
			}
			resetBean();
			return null;
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}				
	}	
	
	/**
	 * Redireciona para a tela de visualização
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/interesse_oferta/selecionar_estagiario.jsp</li>
	 * </ul>
	 * @return
	 */
	public String redirectView(){
		return forward(getViewPage());
	}

	/** 
	 * Chamado a partir do BuscaDiscenteMBean<br/><br/>
	 * Método não invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */	
	@Override
	public String selecionaDiscente() throws ArqException {
		if (ValidatorUtil.isEmpty(discente)){
			addMensagemErro("Discente não informado!");
			return null;
		}		
		
		if (ValidatorUtil.isEmpty(ofertaEstagio)){
			addMensagemErro("Oferta de Estágio não informada!");
			return null;
		}		
		
		InteresseOfertaDao dao = getDAO(InteresseOfertaDao.class);
		try {
			obj = dao.findInteresseByDiscenteOferta(discente, ofertaEstagio, false);
			if (!ValidatorUtil.isEmpty(obj) && obj.isAtivo()){
				addMensagemErro("O discente já está inscrito na Oferta de Estágio selecionada.");
				return null;				
			} else if (ValidatorUtil.isEmpty(obj)) {
				obj = new InteresseOferta();
				obj.setOferta(ofertaEstagio);					
			}
			
			obj.setOferta( getGenericDAO().refresh(obj.getOferta()) );
			
			obj.setDiscente(discente.getDiscente());				
			curriculoLattes = PerfilPessoaDAO.getDao().findLattesByDiscente(discente.getId());
			prepareMovimento(SigaaListaComando.CADASTRAR_INTERESSE_OFERTA_ESTAGIO);
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_INTERESSE_OFERTA_ESTAGIO.getId());
			
			return forward(getFormResumo());					
		} finally {
			if (dao != null)
				dao.close();
		}			
	}

	/** 
	 * Seta o discente selecionado na busca por discente.<br/><br/>
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 * Método não invocado por JSP.
	 */	
	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {		
		try {
			this.discente = getDAO(DiscenteDao.class).findByPK(discente.getId());
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Não foi possível carregar o discente escolhido");
		}	
	}
	
	/**
	 * Visualiza o currículo lattes do discente selecionado.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/interesse_oferta/view.jsp</li>
	 * </ul>
	 * @return
	 */
	public String visualizarLattes(){
		String lattes = PerfilPessoaDAO.getDao().findLattesByDiscente(obj.getDiscente().getId());
		// remove "http://" do início da string, para verificar se há um endereço válido
		if (lattes == null || isEmpty(lattes.replace("http://", ""))){
			addMensagemErro("O Discente não possui Currículo cadastrado.");
			return null;
		}
		return redirect(lattes);
	}	
	
	/**
	 * Verifica se o usuário pode selecionar um discente a uma vaga de estágio
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/interesse_oferta/view.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPermiteSelecionaDiscente(){
		return isPortalConcedenteEstagio() || isUserInRole(SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN); 
	}	
	
	@Override
	public String getViewPage() {
		return "/estagio/interesse_oferta/view.jsp";
	}
	
	public String getFormResumo(){
		return "/estagio/interesse_oferta/resumo.jsp";
	}
	
	@Override
	public String getListPage() {
		return "/estagio/interesse_oferta/lista.jsp";
	}
	
	public String getFormSelecaoEstagio(){
		return "/estagio/interesse_oferta/selecionar_estagiario.jsp"; 
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public OfertaEstagio getOfertaEstagio() {
		return ofertaEstagio;
	}

	public void setOfertaEstagio(OfertaEstagio ofertaEstagio) {
		this.ofertaEstagio = ofertaEstagio;
	}

	public Collection<InteresseOferta> getListaInteresses() {
		return listaInteresses;
	}

	public void setListaInteresses(Collection<InteresseOferta> listaInteresses) {
		this.listaInteresses = listaInteresses;
	}

	public String getCurriculoLattes() {
		return curriculoLattes;
	}

	public void setCurriculoLattes(String curriculoLattes) {
		this.curriculoLattes = curriculoLattes;
	}

	public Pessoa getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Pessoa supervisor) {
		this.supervisor = supervisor;
	}

}
