/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/11/2006
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSubAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.AtividadeExtensaoHelper;
import br.ufrn.sigaa.extensao.negocio.AtividadeExtensaoValidator;

/*******************************************************************************
 * MBean respons�vel pelo controle do fluxo de cadastro dos cursos e eventos de
 * extens�o. Funciona em conjunto com AtividadeExtensaoMBean.
 * 
 * @author Victor Hugo
 * 
 ******************************************************************************/
@Scope("session")
@Component("cursoEventoExtensao")
public class CursoEventoMBean extends
		SigaaAbstractController<AtividadeExtensao> {
	
	
	/** subAtividades sendo manipulada atualmente **/
    private SubAtividadeExtensao subAtividade = new SubAtividadeExtensao();
	
	/** Armazena as subAtividades associadas ao Evento/Curso. **/
    private Collection<SubAtividadeExtensao> subAtividadesInseridas = new ArrayList<SubAtividadeExtensao>();

	public CursoEventoMBean() {
		clear();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(CursoEventoExtensao.class, "id", "titulo");
	}

	/**
	 * M�todo utilizado para limpar o MBean cursoEventoExtensao
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JPS(s):
	 * <ul>
	 * 		<li>M�todo n�o � chamado por JSP(s)</li>
	 * </ul>
	 */
	public void clear() {
		obj = new AtividadeExtensao();
		obj.setPermanente(false);
		obj.setCursoEventoExtensao(new CursoEventoExtensao());
	}

	/**
	 * M�TODOS DE INICIALIZA��O DO FLUXO
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JPS(s):
	 * <ul>
	 * 		<li>sigaa.war/extensao/Atividade/seleciona_atividade.jsp</li>
	 * </ul>
	 */
	public String iniciarCurso() throws ArqException {
	    	if ((obj != null) && (!obj.isProjetoAssociado())) {
	    	    clear();
	    	}
		obj.setTipoAtividadeExtensao(new TipoAtividadeExtensao(
				TipoAtividadeExtensao.CURSO));
		getCursoEvento().setTipo(CursoEventoExtensao.CURSO);
		return iniciar();
	}

	/**
	 * 
	 * Inicia cadastro de Evento de Extens�o
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JPS(s):
	 * <ul>
	 * 		<li>sigaa.war/extensao/Atividade/seleciona_atividade.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarEvento() throws ArqException {
	    	if ((obj != null) && (!obj.isProjetoAssociado())) {
	    	    clear();
	    	}
		obj.setTipoAtividadeExtensao(new TipoAtividadeExtensao(TipoAtividadeExtensao.EVENTO));
		getCursoEvento().setTipo(CursoEventoExtensao.EVENTO);
		return iniciar();
	}

	/**
	 * M�todo utilizado para iniciar o cadastro de CursoEvento
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JPS(s):
	 * <ul>
	 * 		<li>M�todo n�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		AtividadeExtensaoHelper.getAtividadeMBean().prepararFormulario(this);
		return forward(ConstantesNavegacao.DADOS_GERAIS);
	}

	/**
	 * Submete os dados espec�ficos do curso/evento
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Atividade/curso_evento.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterDadosCursoEvento() throws DAOException {

		// Popular dados selecionados
		if (isCurso()) {
			getGenericDAO().initialize(getCursoEvento().getModalidadeEducacao());
		}
		getGenericDAO().initialize(getCursoEvento().getTipoCursoEvento());

		// validacao
		ListaMensagens mensagens = new ListaMensagens();
		AtividadeExtensaoValidator.validaDadosCursoEvento(obj, mensagens);
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		// s� um aviso para os cursos EAD aviso
		if (obj.getCursoEventoExtensao().getModalidadeEducacao() != null
				&& obj.getCursoEventoExtensao().getModalidadeEducacao().getId() == ModalidadeEducacao.A_DISTANCIA) {
			addMensagemInformation("Art. 10. S�o condi��es gerais para a realiza��o dos Cursos de Extens�o Universit�ria: <br/>"
					+ "...<br/>"
					+ "� 2o Quando se tratar de cursos de Extens�o Universit�ria semipresencial ou � dist�ncia, o <br/>"
					+ "Projeto do Curso dever� ser submetido � aprecia��o da Secretaria de Educa��o � Dist�ncia da <br/>"
					+ "UFRN - SEDIS, que emitir� parecer quanto a: <br/>"
					+ "a) adequa��o da proposta � modalidade;<br/> "
					+ "b) adequa��o dos materiais did�ticos e objetos de aprendizagem; <br/>"
					+ "c) adequa��o e viabilidade dos meios.");
		}

		// inicializacao
		GenericDAO dao = getGenericDAO();
		try {
			dao.initialize(getCursoEvento().getTipoCursoEvento());
			if (isCurso()) {
				dao.initialize(getCursoEvento().getModalidadeEducacao());
			}

		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("N�o foi possivel inicializar parametros. "
					+ e.getMessage());
			return null;
		}

		return AtividadeExtensaoHelper.getAtividadeMBean().proximoPasso();
	}

	public CursoEventoExtensao getCursoEvento() {
		return obj.getCursoEventoExtensao();
	}

	public boolean isCurso() {
		return obj.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO;
	}

	public boolean isEvento() {
		return obj.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.EVENTO;
	}
	
	/**
	 * Inicializa procedimento de altera��o de vagas dispon�veis do curso ou evento.
	 * Encaminha o usu�rio para tela de busca dos projetos.
	 * <br />
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JPS(s):
	 * <ul>
	 * 		<li>M�todo n�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 */
	public String iniciarAlterarVagas() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		return forward(ConstantesNavegacao.ALTERAR_VAGAS_CURSO_EVENTO_LISTA);
	}
	
	
	/**
	 * Prepara o formul�rio para altera��o do n�mero de vagas do curso ou evento.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JPS(s):
	 * <ul>
	 * 		<li>M�todo n�o � chamado por JSP(s)</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String alterarVagas() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		try {
			int id = getParameterInt("id", 0);
			obj = getGenericDAO().findByPrimaryKey(id, AtividadeExtensao.class);
			return forward(ConstantesNavegacao.ALTERAR_VAGAS_CURSO_EVENTO_FORM);
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}
	
	/**
	 * Confirma a altera��o de vaga do curso ou evento.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JPS(s):
	 * <ul>
	 * 		<li>M�todo n�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String confirmaAlterarVagas() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		return forward(ConstantesNavegacao.ALTERAR_VAGAS_CURSO_EVENTO_LISTA);
	}
	
	/**
	 * 
	 * Utilizado para mostrar os tipos de curso ativos
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllTiposSubAtividade() throws DAOException {
		Collection<TipoSubAtividadeExtensao> tipos = getGenericDAO().findAll(TipoSubAtividadeExtensao.class);
		return toSelectItems(tipos, "id", "descricao");
	}
	
	
	public String submeterSubAtividades() {
		
		subAtividade = new SubAtividadeExtensao();
		
		return AtividadeExtensaoHelper.getAtividadeMBean().proximoPasso();
	}
	
	public String adicionarSubAtividade() throws DAOException {
		
		ListaMensagens erros = subAtividade.validate();		
		
		for(SubAtividadeExtensao subAtv : obj.getSubAtividadesExtensao()) {
			if(subAtv.getTitulo().trim().equals(subAtividade.getTitulo().trim()) && subAtv.isAtivo()) {
				erros.addErro("N�o � permitido inserir duas mini atividades com o mesmo t�tulo");				
			}
		}
		
		GenericDAO dao = null;
		
		try{
			
			dao = getGenericDAO();
			
			AtividadeExtensao atividade  = dao.findByPrimaryKey(obj.getId(), AtividadeExtensao.class);
			
			if(subAtividade.getInicio() != null && subAtividade.getFim() != null){
				
				if(subAtividade.getInicio().before(atividade.getDataInicio()) 
						|| (subAtividade.getFim().after( atividade.getDataFim() )  ) ){
					erros.addErro("A data da mini atividade n�o pode estar fora do per�odo do projeto.");		
				}
				
			}
			
			
			if(!erros.isEmpty()) {
				addMensagens(erros);
				return null;
			}		
			
			SubAtividadeExtensao subAtv = new SubAtividadeExtensao();
			subAtv.setTitulo(subAtividade.getTitulo());
			subAtv.setTipoSubAtividadeExtensao(dao.findByPrimaryKey(subAtividade.getTipoSubAtividadeExtensao().getId(), TipoSubAtividadeExtensao.class));
			subAtv.setLocal(subAtividade.getLocal());
			subAtv.setInicio(subAtividade.getInicio());
			subAtv.setFim(subAtividade.getFim());
			subAtv.setHorario(subAtividade.getHorario());
			subAtv.setCargaHoraria(subAtividade.getCargaHoraria());
			subAtv.setNumeroVagas(subAtividade.getNumeroVagas());
			subAtv.setDescricao(subAtividade.getDescricao());
			subAtv.setAtividade(atividade );
			subAtv.setAtivo(true);
			
			obj.getSubAtividadesExtensao().add(subAtv);
			subAtividade = new SubAtividadeExtensao();
		
		}finally{
			if(dao != null) dao.close();
		}
		return null;
	}
	
	public String removerSubAtividade() {
		Integer idSubAtividade = getParameterInt("tituloSubAtividade");		
		List<SubAtividadeExtensao> list = new ArrayList<SubAtividadeExtensao>(getSubAtividadesAtivas());
		list.get(idSubAtividade).setAtivo(false);
		list.remove(idSubAtividade);
		subAtividade = new SubAtividadeExtensao();
		return null;
	}
	
	
	public Collection<SubAtividadeExtensao> getSubAtividadesAtivas() {
		
		Collection<SubAtividadeExtensao> ativas = new ArrayList<SubAtividadeExtensao>();
		
		for(SubAtividadeExtensao subAtv : obj.getSubAtividadesExtensao()) {
			if(subAtv.isAtivo()) {
				ativas.add(subAtv);
			}
		}
		return ativas;
	}

	public SubAtividadeExtensao getSubAtividade() {
		return subAtividade;
	}

	public void setSubAtividade(SubAtividadeExtensao subAtividade) {
		this.subAtividade = subAtividade;
	}
	
	
}
