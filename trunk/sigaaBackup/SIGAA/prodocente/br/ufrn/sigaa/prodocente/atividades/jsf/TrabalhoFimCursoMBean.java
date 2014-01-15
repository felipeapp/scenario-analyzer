/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.prodocente.TrabalhoFimCursoDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.TipoTrabalhoConclusao;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.RegistroAtividadeMBean;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * MBean responsável pelo cadastro de Orientações de Trabalhos de Conclusão de Curso
 * 
 * Gerado pelo CrudBuilder
 * 
 */

@Component("trabalhoFimCurso") @Scope("session")
public class TrabalhoFimCursoMBean extends AbstractControllerAtividades<TrabalhoFimCurso> implements OperadorDiscente{
	
	/** Discente utilizado na busca. */
	private DiscenteAdapter discenteBusca;
	
	/** Orientador utilizado na busca. */
	private Servidor orientador;
	
	/** Coleção de Trabalho de Fim de Curso, utilizada na busca. */
	private Collection<TrabalhoFimCurso> lista = new ArrayList<TrabalhoFimCurso>();

	/** Construtor padrão. */
	public TrabalhoFimCursoMBean() {
		clear();
	}
	
	@Override
	public String getDirBase() {
		// TODO Auto-generated method stub
		return "/prodocente/atividades/TrabalhoFimCurso";
	}

	/**
	 * Retornas os trabalhos de fim de curso em uma Collection de SelectItem.
	 * 
	 * Método não invocado por JSP´s
	 */
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(TrabalhoFimCurso.class, "id", "titulo");
	}

	/**
	 * Método a ser invocado após o cadastro ser realizado.
	 * 
	 * @throws ArqException
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();
		clear();
	}

	/** 
	 * Limpa os atributos deste controller.
	 */
	public void clear() {
		
		obj= new TrabalhoFimCurso();
		obj.setEntidadeFinanciadora( new EntidadeFinanciadora() );
		obj.setArea( new AreaConhecimentoCnpq() );
		obj.setDepartamento( new Unidade() );
		obj.setServidor( new Servidor() );
		obj.setSubArea( new AreaConhecimentoCnpq() );
		obj.setOrientando( new Discente() );
		obj.setIes( new InstituicoesEnsino() );
		obj.setTipoTrabalhoConclusao( new TipoTrabalhoConclusao() );
		obj.setIes(new InstituicoesEnsino()); 		
		obj.setDepartamento( getUsuarioLogado().getVinculoAtivo().getUnidade() );
		
		discenteBusca = new Discente();
		orientador = new Servidor();
		lista = new ArrayList<TrabalhoFimCurso>();
	}

	/**
	 * Verifica se o usuário logado possui algum papel descrito no método.
	 * 
	 * Método não invocado por JSP´s
	 * Método Chamado pela Classe AbstractControllerAtividades.Cadastrar().
	 * @throws SegurancaException
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		if(getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO) 
				|| getSubSistema().equals(SigaaSubsistemas.LATO_SENSU)
				|| getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR)
				|| getSubSistema().equals(SigaaSubsistemas.GRADUACAO))
			checkRole(SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO,
					SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_CURSO, 
					SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.DAE);
		else
			super.checkDocenteRole();
	}

	/**
	 * Seta o servidor que está associado ao usuário logado.
	 * Método não invocado por JSP´s
	 */
	@Override
	public String preCadastrar() {
		if( isEmpty( obj.getServidor() ) ) 
			obj.setServidor(getServidorUsuario());
		return super.preCadastrar();
	}
	
	/**
	 * Inicializa os atributos antes de redirecionar para o formulário de atualização. 
	 * Método não invocado por JSP´s
	 */
	@Override
	public void afterAtualizar() {
		obj.iniciarAtributosTransient();
		
		/** Indisponibiliza a alteração do orientando, instituição */
		if( !isEmpty(obj.getMatricula()) )
			setPreCadastroParaGraduacao(true);

		obj.setTitulo( StringUtils.unescapeHTML(obj.getTitulo()) );
		
		try {
			carregaSubAreas();
		} catch (DAOException e) {
			e.printStackTrace();
			notifyError(e);
		}
	}

	/**
	 * Carrega as sub-áreas da área cadastrada
	 * 
	 * Método não invocado por JSP´s
	 * 
	 * @throws DAOException
	 */
	@Override
	public void carregaSubAreas() throws DAOException {

		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if (obj.getArea() != null && obj.getArea().getId() != 0 ) {
			subArea = toSelectItems(dao.findAreas(obj.getArea()), "id", "nome");
			AbstractControllerProdocente<Object> bean = new AbstractControllerProdocente<Object>();
			bean.setSubArea(subArea);
			resetBean("producao");
		}

	}

	/**
	 * Cancela a Operação.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/prodocente/atividades/TrabalhoFimCurso/arquivo.jsp</li>
	 *  <li>/prodocente/atividades/TrabalhoFimCurso/form.jsp</li>
	 *  <li>/prodocente/atividades/TrabalhoFimCurso/view.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {		
		resetBean();
		if (isPortalCoordenadorGraduacao())
			return forward("/graduacao/coordenador.jsf");
		else {
			if (preCadastroParaGraduacao || atualizacaoParaGraduacao) {
				String mbean = "registroAtividade";
				resetBean(mbean);
				return ((RegistroAtividadeMBean)getMBean(mbean)).cancelar();
			}
			return forward("/portais/docente/docente.jsf");			
		}
	}

	/**
	 * Prepara o envio do arquivo.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/prodocente/atividades/TrabalhoFimCurso/lista.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	@Override
	public String preEnviarArquivo() throws DAOException {			
		try {
			prepareMovimento(ArqListaComando.ALTERAR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		populateObj(true);

		return forward( getDirBase() + "/arquivo.jsp" );
	}

	/**
	 * Envia o arquivo. 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/prodocente/atividades/TrabalhoFimCurso/arquivo.jsp</li>
	 * </ul>
	 * @throws SQLException, IOException
	 */
	@Override
	public String enviarArquivo() throws SQLException, IOException,
			NegocioException, ArqException {
		UploadedFile arquivo = getArquivo();
		if (getArquivo() != null && !isExcedeTamanhoMaximo(arquivo)) {

			populateObj();
			
			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();

			EnvioArquivoHelper.inserirArquivo(idArquivo,
					arquivo.getBytes(), arquivo.getContentType(), arquivo
							.getName());

			obj.setIdArquivo(idArquivo);

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(ArqListaComando.ALTERAR);

			execute(mov, getCurrentRequest());

			addMessage("Arquivo gravado com sucesso", TipoMensagemUFRN.INFORMATION);
			atividades = null; // Em AbstractControllerAtvidades essa variável guarda as atividades que foram retornadas pelo Dao. Estou forçando o dao abuscar novamente no banco, para atualizar a listagem. @Author: Edson Anibal (ambar@info.ufrn.br)
			return forward(getListPage());

		} else if ( !hasErrors() ) {
			addMensagemErro("Informe o arquivo para enviar");
		}
		return null;
	}
	
	/**
	 * Iniciar fluxo da alteração de trabalho de fim de curso
	 * por alguns gestores de ensino como, por exemplo, 
	 * coordenadores de curso
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/graduacao/menu_coordenador.jsp</li>
	 *    <li>/lato/menu_coordenador.jsp</li>
	 *    <li>/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarAlteracao() throws SegurancaException {
		clear();		
		checkRole(new int[] { SigaaPapeis.COORDENADOR_CURSO,SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.DAE, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO});
		
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_TRABALHO_FIM_CURSO);
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Método não invocado por JSP´s
	 * 
	 * @throws ArqException
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj.setOrientando(discente.getDiscente());
	}

	/**
	 * Busca o trabalho de fim de curso do discente. 
	 * 
	 * Método não invocado por JSP´s
	 * 
	 * @throws ArqException
	 */
	public String selecionaDiscente() throws ArqException {
		
		TrabalhoFimCursoDao tfcDao = getDAO(TrabalhoFimCursoDao.class);
		
		// Verificar se o discente possui um trabalho de fim de curso
		TrabalhoFimCurso tfc = tfcDao.findByOrientando(obj.getOrientando());
		if (tfc == null) {
			addMensagemErro("Não foi encontrado um registro de trabalho de fim de curso para o discente selecionado.");
			return null;
		} else {
			// Devido o atributo possuir fetchType.LAZY, popula os dados da matricula no componente
			tfc.setMatricula( getGenericDAO().refresh( tfc.getMatricula() ) );
			
			GenericDAO dao = getGenericDAO();
			
			this.obj = tfc;
			obj.setDiscenteExterno(false);
			obj.setIes(dao.findByPrimaryKey(InstituicoesEnsino.UFRN, InstituicoesEnsino.class));
			
			setReadOnly(false);
			setConfirmButton("Alterar");
			afterAtualizar();
			
			prepareMovimento(ArqListaComando.ALTERAR);
			return forward( getDirBase() + "/form.jsp" );
		}
		
	}
	
	/**
	 * Redireciona para o subsistema se o cadastro está sendo realizado pelo módulo de graduação.
	 * Método não invocado por JSP´s
	 */
	@Override
	public String forwardCadastrar() {
		if (isPreCadastroParaGraduacao()) {
			return getSubSistema().getLink();
		}
		return super.forwardCadastrar();
	}

	/**
	 * Lista todos os Trabalhos de Fim de Curso de forma ordenada pela Data de Início.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/prodocente/atividades/TrabalhoFimCurso/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<TrabalhoFimCurso> getAllOrdenado() throws DAOException{
		if (isGestor()){
			return lista;
		} else {
			TrabalhoFimCursoDao dao = getDAO(TrabalhoFimCursoDao.class);
			int usuario =  getServidorUsuario().getId();
			return	dao.findDados(usuario);						
		}
	}

	/**
	 * Faz-se um teste para verificar se a data de início de maior de que a data de Defesa, caso
	 * seja, o cadastro será efetuado.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/prodocente/atividades/TrabalhoFimCurso/form.jsp</li>
	 *   <li>/prodocente/atividades/TrabalhoFimCurso/view.jsp</li>
	 * </ul>
	 * @throws SegurancaException, ArqException
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		/**
		 * Quando o acesso é a partir do módulo do Portal do Coord. de Graduação. 
 		 * Verifica a situação da matrícula do discente para o componente 
 		 * com {@link TipoAtividade#TRABALHO_CONCLUSAO_CURSO} 
		 */ 
		if( getUsuarioLogado().isUserInRole( SigaaPapeis.COORDENADOR_CURSO ) 
				&& obj.getId() != 0 && !isEmpty( obj.getMatricula() ) 
				&& obj.getMatricula().getDiscente().isGraduacao() 
				&& !obj.getMatricula().isMatriculado() ){
			addMensagem( MensagensGraduacao.PERMITIDO_ALTERAR_ATIVIDADE_SITUACAO_MATRICULADO );
			return cancelar();
		}
		
		// Testa-se pra evitar o NullPointerException.
		if (obj.getDataInicio() != null && obj.getDataDefesa() != null) {
			if((obj.getDataInicio().compareTo(obj.getDataDefesa()) > 0)) {
				addMensagemErro("Data de Inicio posterior a Data Defesa.");
				return null;
			}
		}
		
		if( !obj.getDiscenteExterno() && obj.getOrientando() != null && obj.getOrientando().getId() == 0){
			addMensagemErro("O Discente informado não se encontra com o status de " + StatusDiscente.getDescricao(StatusDiscente.CONCLUIDO));
			return null;
		}
				
		if ( ( obj.getDocenteExterno() != null && obj.getDocenteExterno().getId() == 0 ) || 
				( obj.getServidor() != null && obj.getServidor().getId() == 0 ) ) {
			if ( getUsuarioLogado().getVinculoAtivo().isVinculoServidor() )
				obj.setServidor( getUsuarioLogado().getVinculoAtivo().getServidor() );
			if ( getUsuarioLogado().getVinculoAtivo().isVinculoDocenteExterno() )
				obj.setDocenteExterno( getUsuarioLogado().getVinculoAtivo().getDocenteExterno() );
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		Iterator<FacesMessage> messages = context.getMessages();
		
		if (messages.hasNext()) {
			FacesMessage facesMessage = messages.next();
			addMensagemErro(facesMessage.getSummary());
		}
		if (hasErrors())
			return null;

		prepareMovimento(ArqListaComando.CADASTRAR);
		
		return super.cadastrar();
	}
	
	/**
	 * Realiza a busca de trabalho de fim de curso para coordenação do curso.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/prodocente/atividades/TrabalhoFimCurso/lista.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 */
	public String buscar() throws DAOException{
		isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
		
		lista = new ArrayList<TrabalhoFimCurso>();		
		String param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o critério de busca.");
			return null;
		}

		if (discenteBusca.getId() != 0)
			discenteBusca = getGenericDAO().findByPrimaryKey(discenteBusca.getId(), Discente.class);
		
		Curso curso = null;
		
		if (isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE))
			curso = discenteBusca.getCurso();
		else
			curso  = getCursoAtualCoordenacao();
		
		TrabalhoFimCursoDao dao = getDAO(TrabalhoFimCursoDao.class);
		if ("discente".equalsIgnoreCase(param)){			
			lista = dao.findbyCurso(curso.getId(), getNivelEnsino(), discenteBusca.getId(), null);
		}else if ("orientador".equalsIgnoreCase(param)){
			lista = dao.findbyCurso(curso.getId(), getNivelEnsino(), null, orientador.getId());
		}else if ("todos".equalsIgnoreCase(param)) {
			lista = dao.findbyCurso(curso.getId(), getNivelEnsino(), null, null);
		} 

		discenteBusca = new Discente();
		orientador = new Servidor();

		if ( ValidatorUtil.isEmpty(lista) ) {
			addMensagemErro("Nenhum Trabalho de Fim de Curso foi encontrado de acordo com os critérios de busca");
			return null;
		}
		
		return forward(getListPage());
	}

	public DiscenteAdapter getDiscenteBusca() {
		return discenteBusca;
	}

	public void setDiscenteBusca(DiscenteAdapter discenteBusca) {
		this.discenteBusca = discenteBusca;
	}

	public Collection<TrabalhoFimCurso> getLista() {
		return lista;
	}

	public void setLista(Collection<TrabalhoFimCurso> lista) {
		this.lista = lista;
	}

	public Servidor getOrientador() {
		return orientador;
	}

	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}
	
	public boolean isGestor() {
		if (isPortalCoordenadorGraduacao() || isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE))
			return true;
		return false;
				
	}
}
