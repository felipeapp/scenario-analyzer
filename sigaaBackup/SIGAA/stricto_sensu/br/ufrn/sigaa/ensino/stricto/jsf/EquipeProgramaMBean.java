/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 05/03/2007
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CorpoDocenteCursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.EquipeProgramaDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.LinhaPesquisaStrictoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.jsf.TurmaLatoSensuMBean;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.dominio.LinhaPesquisaStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.NivelEquipe;
import br.ufrn.sigaa.ensino.stricto.dominio.VinculoEquipe;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean para Associar Docentes a um Programa de Pós-graduação
 * @author Andre Dantas
 * @author Victor Hugo
 */
@Component("equipePrograma") @Scope("session")
public class EquipeProgramaMBean extends SigaaAbstractController<EquipePrograma> {

	/** Lista de equipe do programa */
	private List<EquipePrograma> equipeDoPrograma;

	/** 
	 * As possíveis linhas que é exibido no formulário para vincular o docente 
	 * */
	private List<LinhaPesquisaStricto> possiveisLinhas = new ArrayList<LinhaPesquisaStricto>(0);
	
	/**
	 * Construtor
	 * @throws DAOException
	 */
	public EquipeProgramaMBean() throws DAOException {
		initObj();
	}

	/**
	 * Inicializa a Equipe (<code>obj</code>) que está sendo manipulada por esse MBean.
	 * @throws DAOException
	 */
	private void initObj() throws DAOException {
		obj = new EquipePrograma();
		obj.iniciarNulos();
		obj.setVinculo(VinculoEquipe.PROFESSOR);
		obj.setNivel(NivelEquipe.PLENO);
		obj.setAreaConcentracaoPrincipal(new AreaConcentracao());
		obj.setAtivo(true);
		if (!isUserInRole(SigaaPapeis.PPG) || !isPortalPpg()) {
			obj.setPrograma( getProgramaStricto() );
		}
		carregarLinhas(null);
	}

	/**
	 * Retorna o link para o formulário de cadastro de docente do programa. <br>
	 * Método não invocado por JSP´s.
	 */
	@Override
	public String getFormPage() {
		return "/stricto/equipe_programa/form.jsp";
	}

	/** Retorna o link para a listagem de docentes do programa. <br>
	 * Método não invocado por JSP´s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/stricto/equipe_programa/form.jsp";
	}
	
	/**
	 * Retorna o formulário de alteração do limite de orientandos.
	 * 
	 * @return
	 */
	private String getLimiteOrientandosPage(){
		return "/stricto/equipe_programa/limite_orientandos.jsp";
	}

	/**
	 * Inicia o cadastro de equipe do programa de pós.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menus/cadastro.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException {
		checkRole(SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);
		prepareMovimento(SigaaListaComando.CADASTRAR_EQUIPE_PROGRAMA);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_EQUIPE_PROGRAMA.getId());
		initObj();
		if (!isUserInRole(SigaaPapeis.PPG) || !isPortalPpg()) {
			obj.setPrograma( getProgramaStricto() );
			carregarEquipeDoPrograma(null);
		}
		obj.setAtivo(true);
		
		AreaConcentracaoMBean areaBean = getMBean("areaConcentracao");
		areaBean.setPrograma( getProgramaStricto() );
		carregarLinhas(null);
		
		return forward(getFormPage());
	}
	
	/**
	 * Lista a equipe de docentes do programa.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		checkRole(SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);
		removeOperacaoAtiva();
		initObj();
		if (!isUserInRole(SigaaPapeis.PPG) || !isPortalPpg()) {
			obj.setPrograma( getProgramaStricto() );
			carregarEquipeDoPrograma(null);
		}
		obj.setAtivo(true);
		
		AreaConcentracaoMBean areaBean = getMBean("areaConcentracao");
		areaBean.setPrograma( getProgramaStricto() );
		carregarLinhas(null);
		
		return forward(getFormPage());
	}

	/**
	 * Diz se pode alterar o programa, caso o usuário seja ppg então ele pode
	 * cadastrar um membro em qualquer programa.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/equipe_programa/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isPodeAlterarPrograma() {
		return getAcessoMenu().isPpg();
	}

	/**
	 * Carrega a equipe do programa dado o id do programa passado
	 * @param evt
	 */
	private void carregarEquipeDoPrograma( ActionEvent evt ) {
		if( evt != null ){
			Integer id = (Integer) evt.getComponent().getAttributes().get("tipoBuscaDiscente");
			obj.setPrograma( new Unidade(id) );
		}
		try {
			EquipeProgramaDao dao = getDAO(EquipeProgramaDao.class);
			equipeDoPrograma = dao.findByPrograma(obj.getPrograma().getId());
			Collections.sort(equipeDoPrograma);
		} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErroPadrao();
		}
	}

	/**
	 * Carrega as linhas de pesquisa que o docente pode ser vinculado
	 * Inicialmente mostra todas as linhas do programa que não estão vinculadas
	 * a nenhuma área Quando o usuário seleciona uma área é adicionado as opções
	 * as linhas que estão vinculadas a área que o usuário selecionou .<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/equipe_programa/form.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarLinhas(ValueChangeEvent e) throws DAOException {
		LinhaPesquisaStrictoDao dao = getDAO(LinhaPesquisaStrictoDao.class);
		possiveisLinhas = new ArrayList<LinhaPesquisaStricto>();
		if (e != null) 
			possiveisLinhas = (List<LinhaPesquisaStricto>) dao.findByExactField(LinhaPesquisaStricto.class, "area.id", e.getNewValue());
		else if( !isEmpty( obj.getPrograma() ) ){
			possiveisLinhas.addAll( dao.findByProgramaSemArea( obj.getPrograma() ) );
			possiveisLinhas.addAll( dao.findByExactField( LinhaPesquisaStricto.class, "area.id", obj.getAreaConcentracaoPrincipal().getId() ) );
		} else if( getProgramaStricto() != null )
			possiveisLinhas.addAll( dao.findByProgramaSemArea( getProgramaStricto() ) );
		
		/** marcando as que já estão vinculadas ao docente */
		if( !isEmpty( obj.getLinhasPesquisa() ) ){
			for( LinhaPesquisaStricto l : possiveisLinhas ){
				if( obj.getLinhasPesquisa().contains(l) )
					l.setSelecionado(true);
			}
		}
	}

	/**
	 * Chama o processador e cadastra o vínculo entre o docente e o programa
	 * (EquipePrograma).<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/equipe_programa/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_EQUIPE_PROGRAMA.getId(), SigaaListaComando.ALTERAR_EQUIPE_PROGRAMA.getId()))
			return null;
		
		String tipoDocente = getParameter("tipoAjaxDocente_1");
		if( "externo".equals(tipoDocente) ){
			EquipeProgramaDao dao = getDAO(EquipeProgramaDao.class);
			obj.setDocenteExterno( dao.findByPrimaryKey(obj.getServidor().getId(), DocenteExterno.class));			
			obj.setServidor( new Servidor() );
		}

		erros = new ListaMensagens();
		erros.addAll(obj.validate());
		if (hasErrors())
			return null;

		if( obj.getAreaConcentracaoPrincipal() != null && obj.getAreaConcentracaoPrincipal().getId() == 0)
			obj.setAreaConcentracaoPrincipal(null);

		if( obj.getLinhasPesquisa() == null )
			obj.setLinhasPesquisa( new HashSet<LinhaPesquisaStricto>() );
		
		/**
		 * Adicionando as linhas de pesquisa que foram selecionadas
		 */
		for( LinhaPesquisaStricto linha : possiveisLinhas ){
			if( linha.isSelecionado() && !obj.getLinhasPesquisa().contains(linha) )
				obj.getLinhasPesquisa().add(linha);
			else if( !linha.isSelecionado() && obj.getLinhasPesquisa().contains(linha) ){
				obj.getLinhasPesquisa().remove(linha);
			}
		}
		
		/**
		 * Removendo as linhas que não estão mais vinculadas ao membro
		 */
		forExterno : for (Iterator<LinhaPesquisaStricto> it = obj.getLinhasPesquisa().iterator(); it.hasNext();) {
			LinhaPesquisaStricto linhaObj = it.next();
			for( LinhaPesquisaStricto linha : possiveisLinhas ){
				if( linhaObj.getId() == linha.getId() ){
					if( !linha.isSelecionado() ){ /** se a linha estiver desmarcada então deve remover */
						it.remove();
						continue forExterno;
					}else{ /** se a linha estiver marcada então NÃO deve remover */
						continue forExterno;
					}
				}
			}
			/** 
			 * Se não encontrar a linha de pesquisa na lista de possíveis linhas então deve remover, 
			 * possivelmente porque a área de concentração principal foi alterada
			 */
			it.remove();
		}		
		
		try {

			MovimentoCadastro mov = new MovimentoCadastro();
			String msgConfirmacao = "Docente";
			if (obj.getId() == 0) {
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_EQUIPE_PROGRAMA);
			}
			else {
				mov.setCodMovimento(SigaaListaComando.ALTERAR_EQUIPE_PROGRAMA);
				msgConfirmacao = "Membro";
			}

			mov.setObjMovimentado(obj);
			executeWithoutClosingSession(mov, getCurrentRequest());
//			addMessage(msgConfirmacao, TipoMensagemUFRN.INFORMATION);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, msgConfirmacao);
			afterCadastrar();			

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} 
		return forward( getFormPage() );
	}
	
	/**
	 * Verifica se exibe o formulário ou não
	 * @return
	 */
	public boolean isExibeFormulario() {
		Integer operacaoAtiva = (Integer) getCurrentSession().getAttribute("operacaoAtiva");
		if (operacaoAtiva != null && (
				operacaoAtiva == SigaaListaComando.CADASTRAR_EQUIPE_PROGRAMA.getId() ||
				operacaoAtiva ==  SigaaListaComando.ALTERAR_EQUIPE_PROGRAMA.getId()))
			return true;
		else
			return false;
	}

	/**
	 * Inicializa a edição de uma EquipePrograma.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/equipe_programa/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String editar() throws ArqException {
		obj = getGenericDAO().findByPrimaryKey( getParameterInt("id") , EquipePrograma.class);
		if (obj.getId() == 0 || !obj.isAtivo()) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO, "Membro do programa");
			return cancelar();
		}
		obj.iniciarNulos();
		setConfirmButton("Alterar");
		carregarLinhas(null);
		prepareMovimento( SigaaListaComando.ALTERAR_EQUIPE_PROGRAMA );
		setOperacaoAtiva( SigaaListaComando.ALTERAR_EQUIPE_PROGRAMA.getId() );
		return forward(getFormPage());
	}

	/**
	 * Executado depois de cadastrar
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		carregarEquipeDoPrograma(null);
		Integer idProgramaAtual = obj.getPrograma().getId();
		initObj();
		obj.setPrograma(getGenericDAO().findByPrimaryKey(idProgramaAtual, Unidade.class));
		prepareMovimento(SigaaListaComando.CADASTRAR_EQUIPE_PROGRAMA);		
		setConfirmButton("Cadastrar");
	}

	/**
	 * Remove um membro do programa.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/equipe_programa/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		EquipePrograma ep = getGenericDAO().findByPrimaryKey(getParameterInt("id"), EquipePrograma.class);
		if (ep.getId() == 0 || !ep.isAtivo()) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO, "Membro do programa");
			return cancelar();
		} else {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(ep);
			prepareMovimento(SigaaListaComando.REMOVER_EQUIPE_PROGRAMA);
			mov.setCodMovimento(SigaaListaComando.REMOVER_EQUIPE_PROGRAMA);
			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
//				addMessage("Membro do programa removido com sucesso!", TipoMensagemUFRN.INFORMATION);
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Membro do programa");
			} catch (Exception e) {
				addMensagemErroPadrao();
				e.printStackTrace();
				return null;
			}
			carregarEquipeDoPrograma(null);
			return null;
		}
	}

	/**
	 * Ação realizada quando o usuário seleciona o programa. Carrega a equipe do
	 * programa selecionado e atualiza o filtro no MBean das áreas de
	 * concentração.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/equipe_programa/form.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void selecionarPrograma(ValueChangeEvent e) throws DAOException {
		AreaConcentracaoMBean areaMBean = getMBean("areaConcentracao");
		
		if (e.getNewValue() != null && !e.getNewValue().toString().trim().equals("0")) {
			obj.setPrograma(getGenericDAO().findByPrimaryKey((Integer) e.getNewValue(), Unidade.class));
			carregarEquipeDoPrograma(null);
			areaMBean.setPrograma( obj.getPrograma() );
		} else {
			equipeDoPrograma = null;
			obj.setPrograma(new Unidade());
			areaMBean.setPrograma( null );
		}
	}

	/**
	 * Método chamado ao ser submetido o formulário de busca realiza a busca de
	 * acordo com os critérios informados.<br>
	 * Método não invocado por JSP´s.
	 */
	@Override
	public String buscar() throws Exception {
		String param = getParameter("paramBusca");
		if (param == null) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}

		GenericDAO dao = getGenericDAO();
		dao.addOrder("servidor.nome", "asc");
		if ("programa".equalsIgnoreCase(param))
			setResultadosBusca(dao.findByLikeField(EquipePrograma.class, "programa.nome", obj.getPrograma().getNome()));
		else if ("servidor".equalsIgnoreCase(param))
			setResultadosBusca(dao.findByLikeField(EquipePrograma.class, "servidor.pessoa.nome", obj.getServidor().getNome()));
		else
			setResultadosBusca(null);

		initObj();
		return null;
	}

	/**
	 * Vai para o form que exibe o limite de orientandos do membro do programa
	 * selecionado.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarLimitesOrientandos() throws ArqException{
		checkRole(SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);

		EquipeProgramaDao dao = getDAO( EquipeProgramaDao.class );
		equipeDoPrograma = dao.findByPrograma( getProgramaStricto().getId() );


		prepareMovimento(SigaaListaComando.CADASTRAR_LIMITE_BOLSISTAS_EQUIPE);
		return forward( getLimiteOrientandosPage() );
	}

	/**
	 * Persiste os limites de orientandos e bolsistas dos docentes do programa,
	 * testando se o limite modificado não está gerando conflito com a
	 * quantidade de orientandos por docente já existente.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/equipe_programa/limite_orientandos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String cadastrarLimitesOrientandos() throws DAOException {
		
		for(EquipePrograma e : equipeDoPrograma) {
			
			Integer maxOrientandosRegularesMestrado = ValidatorUtil.isEmpty( e.getMaxOrientandoRegularMestrado() ) ? 0 : e.getMaxOrientandoRegularMestrado(); 
			Integer maxOrientandosEspeciaisMestrado = ValidatorUtil.isEmpty( e.getMaxOrientandoEspecialMestrado() ) ? 0 : e.getMaxOrientandoEspecialMestrado();
			if( maxOrientandosRegularesMestrado+maxOrientandosEspeciaisMestrado == 0) {
				if(e.getMaxOrientandoRegularMestrado()!=null || e.getMaxOrientandoEspecialMestrado()!=null) {				
					e.setMaxOrientadosMestrado(0);
				} else {
					e.setMaxOrientadosMestrado(null);
				}
			} else {
				e.setMaxOrientadosMestrado(maxOrientandosRegularesMestrado+maxOrientandosEspeciaisMestrado);
			}
			
			
			Integer maxOrientandosRegularesDoutorado = ValidatorUtil.isEmpty( e.getMaxOrientandoRegularDoutorado() ) ? 0 : e.getMaxOrientandoRegularDoutorado(); 
			Integer maxOrientandosEspeciaisDoutorado = ValidatorUtil.isEmpty( e.getMaxOrientandoEspecialDoutorado() ) ? 0 : e.getMaxOrientandoEspecialDoutorado();
			if( maxOrientandosRegularesDoutorado+maxOrientandosEspeciaisDoutorado == 0) {
				if(e.getMaxOrientandoRegularDoutorado()!=null || e.getMaxOrientandoEspecialDoutorado()!=null) {				
					e.setMaxOrientadosDoutorado(0);
				} else {
					e.setMaxOrientadosDoutorado(null);
				}
			} else {
				e.setMaxOrientadosDoutorado(maxOrientandosRegularesDoutorado + maxOrientandosEspeciaisDoutorado);
			}
			
		}
		

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento( SigaaListaComando.CADASTRAR_LIMITE_BOLSISTAS_EQUIPE );
		mov.setColObjMovimentado( equipeDoPrograma );

		try {
			execute(mov);
//			addMessage("Limite de bolsistas e orientandos cadastrado com sucesso!", TipoMensagemUFRN.INFORMATION);
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Limite de orientandos");
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			return null;
		}catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}

		return cancelar();
	}

	/**
	 * Retorna uma coleção de SelectItem com os membros do programa do usuário
	 * logado. Se o usuário logado não for coordenador ou secretário de programa
	 * retorna os membros da equipe do programa setado em obj.getPrograma().<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/orientacao/form.jsp</li>
	 * <li>/sigaa.war/graduacao/turma/docentes.jsp</li>
	 * <li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public List<SelectItem> getDocentesProgramaCombo() throws ArqException {
		if( isUserInRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO)
				&& !isEmpty(getProgramaStricto())){
			EquipeProgramaDao dao = getDAO( EquipeProgramaDao.class );
			List<EquipePrograma> equipe = dao.findByPrograma( getProgramaStricto().getId() );
			Collections.sort(equipe);
			return toSelectItems( equipe, "id", "nome");
		} else if ( isUserInRole(SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO ) && isLatoSensu() ) {
			return getDocentePropostaCursoLato();
		}
		else
			return getDocentesProgramaObjCombo();
	}
    
	/**
	 * Retorna os Docentes da Proposta de Curso Lato Sensu
	 */
	private List<SelectItem> getDocentePropostaCursoLato() throws ArqException {
		CorpoDocenteCursoLatoDao dao = getDAO(CorpoDocenteCursoLatoDao.class);
		ArrayList<SelectItem> itens = new ArrayList<SelectItem>();
		try {
			Collection<CorpoDocenteCursoLato> corpoDocente;
			if ( getCursoAtualCoordenacao() != null ) {
				corpoDocente = dao.findByAllDocenteCurso( getCursoAtualCoordenacao() );  
			} else {
				TurmaLatoSensuMBean mBean = getMBean("turmaLatoSensuBean");
				corpoDocente = dao.findByAllDocenteCurso( mBean.getObj().getCurso() );
			}
			for (CorpoDocenteCursoLato corpoDocenteCursoLato : corpoDocente) {
				SelectItem item = new SelectItem();
				item.setValue( corpoDocenteCursoLato.getId() );
				item.setLabel( corpoDocenteCursoLato.getServidor() != null 
					? corpoDocenteCursoLato.getServidor().getNome() 
							: corpoDocenteCursoLato.getDocenteExterno().getNome() );
				itens.add(item);
			}
		} finally {
			dao.close();
		}
		
		return itens;
	}

	/**
	 * Retorna uma coleção de SelectItem com os membros do programa setado em
	 * obj.getPrograma().<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/docentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getDocentesProgramaObjCombo() throws DAOException {
		if( obj == null || obj.getPrograma() == null)
			return new ArrayList<SelectItem>(0);

		EquipeProgramaDao dao = getDAO( EquipeProgramaDao.class );
		List<EquipePrograma> equipe; 
		if( obj.getPrograma().getId() > 0 )
			equipe = dao.findByPrograma( obj.getPrograma().getId() );
		else
			equipe = new ArrayList<EquipePrograma>(0);
		return toSelectItems(equipe, "id", "nome");
	}

	/**
	 * Retorna os docentes permanentes do programa do usuário logado. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/turma/docentes.jsp</li>
	 * <li>/sigaa.war/stricto/orientacao/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getDocentesPermanentesProgramaCombo() throws DAOException {
		if( isUserInRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO) ){
			EquipeProgramaDao dao = getDAO( EquipeProgramaDao.class );
			return toSelectItems(dao.findByPrograma( getProgramaStricto().getId(), VinculoEquipe.PROFESSOR), "id", "descricao");
		}
		return  new ArrayList<SelectItem>(0);
	}

	public List<EquipePrograma> getEquipeDoPrograma() {
		return equipeDoPrograma;
	}

	public void setEquipeDoPrograma(List<EquipePrograma> equipeDoPrograma) {
		this.equipeDoPrograma = equipeDoPrograma;
	}

	public List<LinhaPesquisaStricto> getPossiveisLinhas() {
		return possiveisLinhas;
	}

	public void setPossiveisLinhas(List<LinhaPesquisaStricto> possiveisLinhas) {
		this.possiveisLinhas = possiveisLinhas;
	}

}