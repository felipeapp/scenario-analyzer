/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2011
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isAllEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.ensino.BancaDefesaDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.ensino.dominio.BancaDefesa;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MembroBanca;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.StatusBanca;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.mensagens.MensagensGerais;

/**
 * MBean Responsável por gerenciar o cadastro de banca de defesa de TCC.
 * 
 * @author arlindo
 *
 */
@Component("bancaDefesaMBean") @Scope("session")
public class BancaDefesaMBean extends SigaaAbstractController<BancaDefesa> implements OperadorDiscente {
	
	/** MBean auxiliar para o cadastro de membros */
	@Autowired
	private MembroBancaMBean membroBancaMBean;

	/** Discente cuja matrícula será efetuada */
	private DiscenteAdapter discente;	
	
	/** Coleção de selectItem de atividades que o discente está matriculado ou cumpriu. */
	private Collection<SelectItem> matriculasComponenteCombo;	
	
	/** Lista de SelectItem de subáreas de conhecimento. */
	private List<SelectItem> subAreas = new ArrayList<SelectItem>();

	/** Lista de SelectItem de áreas de conhecimento. */
	private List<SelectItem> areas = new ArrayList<SelectItem>();
	
	/** Indica se é cadastro de banca ou visualização */
	private boolean cadastro = false;
	
	/** Construtor padrão */
	public BancaDefesaMBean() {
		init();
	}
	
	/** Inicializa os objetos */
	private void init(){
		obj = new BancaDefesa();
		obj.setStatus(new StatusBanca(StatusBanca.ATIVO));
		obj.setMembrosBanca(new ArrayList<MembroBanca>());
		cadastro = false;
		matriculasComponenteCombo = null;		
	}	
		
	/**
	 * Inicia o cadastro de banca de defesa
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastro() throws ArqException {	
		init();
		prepareMovimento(SigaaListaComando.CADASTRAR_BANCA_DEFESA);	
		cadastro = true;
		return iniciar();
	}
	
	/**
	 * Inicia a o formulário para alteração dos dados da banca
	 * @return
	 * @throws ArqException
	 */
	protected String alterarBanca() throws ArqException {
		if (ValidatorUtil.isEmpty(obj)){
			addMensagemErro("Nenhuma Banca de Defesa foi selecionada");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.ALTERAR_BANCA_DEFESA);
		cadastro = true;
		return telaDadosBanca();
	}
	
	/**
	 * Remove a banca de defesa selecionada
	 * @return
	 * @throws ArqException
	 */
	protected String removerBanca(BancaDefesa banca) throws ArqException{
		setObj(banca);
		if (ValidatorUtil.isEmpty(obj)){
			addMensagemErro("Nenhuma Banca de Defesa foi selecionada");
			return null;
		}		
		prepareMovimento(SigaaListaComando.REMOVER_BANCA_DEFESA);
		return confirmar();		
	}
	
	/**
	 * Inicia o formulário para cadastro ou alteração de banca
	 * @return
	 * @throws ArqException
	 */
	private String iniciar() throws ArqException {
		checkChangeRole();
				
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");		
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.BANCA_DEFESA);		
		return buscaDiscenteMBean.popular();					
	}	
	
	/**
	 * Verifica se o usuário possui permissão para acessar o cadastro de banca
	 * <br/>
	 * Método não chamado por JSPs.
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.COORDENADOR_TECNICO,
				SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.SECRETARIA_COORDENACAO);
	}
	
	/** 
	 * Carrega as {@link AreaConhecimentoCnpq áreas de conhecimento}.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
     *   <li>/ensino/banca_defesa/form.jsp</li>
     * </ul>  
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaAreas(ValueChangeEvent evt) throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		try {
			areas = new ArrayList<SelectItem>();
			subAreas = new ArrayList<SelectItem>();
			if (evt == null) {
				areas = toSelectItems(dao.findAreas(obj.getGrandeArea()), "id", "nome");
			} else if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
				AreaConhecimentoCnpq grandeArea = new AreaConhecimentoCnpq((Integer) evt.getNewValue());
				areas = toSelectItems(dao.findAreas(grandeArea), "id", "nome");
			}
		}finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/** 
	 * Carrega as {@link AreaConhecimentoCnpq subáreas de conhecimento}.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
     *   <li>/ensino/banca_defesa/form.jsp</li>
     * </ul>  
	 * @param evt
	 * @throws DAOException
	 */
	public void carregaSubAreas(ValueChangeEvent evt) throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		try {
			subAreas = new ArrayList<SelectItem>();
			if (evt == null) {
				subAreas = toSelectItems(dao.findSubAreas(obj.getArea().getId()), "id", "nome");
			} else if (((Integer) evt.getNewValue()) != null && ((Integer) evt.getNewValue()) != 0) {
				subAreas = toSelectItems(dao.findSubAreas((Integer) evt.getNewValue()), "id", "nome");
			} 
		}finally {
			if (dao != null)
				dao.close();
		}		
	}
	
	/** 
	 * Submete os dados gerais da banca.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
     *   <li>/ensino/banca_defesa/form.jsp</li>
     * </ul>  
	 * @return
	 * @throws DAOException
	 */
	public String submeterDadosGerais() throws DAOException {
		
		erros = new ListaMensagens();
		erros.addAll(obj.validate());
		
		if (hasErrors())
			return null;
		
		if (obj.getSubArea() != null && obj.getSubArea().getId() != 0){
			obj.setSubArea(getGenericDAO().findByPrimaryKey(obj.getSubArea().getId(), AreaConhecimentoCnpq.class));
		} 
		if (obj.getArea() != null && obj.getArea().getId() != 0){
			obj.setArea(getGenericDAO().findByPrimaryKey(obj.getArea().getId(), AreaConhecimentoCnpq.class));
		}

		obj.setMatriculaComponente(getGenericDAO().findByPrimaryKey(obj.getMatriculaComponente().getId(), MatriculaComponente.class, 
				"id","componente.detalhes.nome","componente.codigo", "componente.detalhes.chTotal", "ano", 
				"periodo", "situacaoMatricula.descricao"));		
		
		if (!isEmpty(obj.getHora())){
			Calendar cHora = Calendar.getInstance();
			cHora.setTime( obj.getHora());
			
			obj.setDataDefesa( CalendarUtils.configuraTempoDaData(obj.getDataDefesa(), cHora.get(Calendar.HOUR_OF_DAY), cHora.get(Calendar.MINUTE), 0, 0));
		} else {
			obj.setDataDefesa( CalendarUtils.configuraTempoDaData(obj.getDataDefesa(), 0, 0, 0, 0));
		}	
		
		return telaMembros();
	}
	
	/** 
	 * Submete os dados dos membros da banca.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
     *   <li>/ensino/banca_defesa/membros.jsp</li>
     * </ul>  
	 * @return
	 * @throws DAOException
	 */
	public String submeterMembros() throws DAOException {
		
		if (obj.getMembrosBanca() == null || obj.getMembrosBanca().size() == 0)
			addMensagemErro("É necessário adicionar ao menos um membro na banca");

		if (hasErrors())
			return null;		
		
		return forward(getResumoPage());
	}	
	
	/**
	 * Adiciona o membro selecionado na banca
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/ensino/banca_defesa/membros.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 */
	public String addMembro() throws DAOException{
		return membroBancaMBean.addMembroBanca(obj);
	}
	
	/**
	 * Remove o membro selecionado da banca
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/ensino/banca_defesa/membros.jsp</li>
	 * </ul>
	 */
	public String removerMembro(){
		return membroBancaMBean.removeMembroBanca(obj, getParameterInt("indice",0));
	}
	
	/**
	 * Retorna o membro atual
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/ensino/banca_defesa/membros.jsp</li>
	 * </ul>
	 * @return
	 */
	public MembroBanca getMembro(){
		return membroBancaMBean.getObj();
	}
	
	/** Processa os dados e persiste em banco.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
     *   <li>/ensino/banca_defesa/resumo.jsp</li>
     * </ul>   
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {
		try {					
			
			if (ValidatorUtil.isEmpty(obj.getSubArea()))
				obj.setSubArea(null);
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(getUltimoComando());
			mov.setObjMovimentado(obj);
			
			execute(mov, getCurrentRequest());
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			
			return cancelar();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
	}	
	
	/**
	 * Cancela a operação
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
     *   <li>/ensino/banca_defesa/resumo.jsp</li>
     * </ul>   
	 */
	@Override
	public String cancelar() {
		if (!isCadastro() || (getUltimoComando().equals(SigaaListaComando.ALTERAR_BANCA_DEFESA)) 
				|| (getUltimoComando().equals(SigaaListaComando.REMOVER_BANCA_DEFESA))){
			try{
				BuscaBancaDefesaMBean buscaBancaMBean = getMBean("buscaBancaDefesaMBean");
				buscaBancaMBean.setListagem(null);
				return buscaBancaMBean.buscar();
			} catch (DAOException e) {
				tratamentoErroPadrao(e);
				return null;
			}
		}else
			return super.cancelar();
	}
	
	/**
	 * Seta o discente selecionado na busca por discente.
	 * <br /><br />
	 * Método não invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */	
	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		if (discente.isGraduacao())
			this.discente = (DiscenteGraduacao) discente;
		else if (discente.isLato())
			this.discente = (DiscenteLato) discente;
		else if (discente.isTecnico())
			this.discente = (DiscenteTecnico) discente;
	}

	/** 
	 * Chamado a partir do BuscaDiscenteMBean
	 * Método não invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	@Override
	public String selecionaDiscente() throws ArqException {	
		
		if (isEmpty(discente)){
			addMensagem( MensagensGerais.DISCENTE_NAO_SELECIONADO );
			return null;				
		}
		
		BancaDefesaDao dao = getDAO(BancaDefesaDao.class);
		BancaDefesa banca = dao.findByDiscente(discente);
		if (!ValidatorUtil.isEmpty(banca)){
			addMensagemErro("Já foi cadastrada uma Banca de Defesa para o Discente selecionado");
			return null;
		}				
		
		popularOrientacaoBanca();
		
		if( hasErrors() )
			return null;
		
		obj.setDiscente(discente.getDiscente());
		
		return telaDadosBanca();
	}
	
	/**
	 * Popular orientação da atividade do tipo {@link TipoAtividade#TRABALHO_CONCLUSAO_CURSO} 
	 * para o discente selecionado.
	 * Método não invocado por JSP's.
	 * @throws DAOException
	 */
	private void popularOrientacaoBanca() throws DAOException{
		SituacaoMatricula situacoes[] = {SituacaoMatricula.APROVADO, SituacaoMatricula.APROVEITADO_CUMPRIU,
				SituacaoMatricula.APROVEITADO_DISPENSADO, SituacaoMatricula.APROVEITADO_TRANSFERIDO,
				SituacaoMatricula.MATRICULADO};
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		Collection<MatriculaComponente> matriculas =
			matriculaDao.findAtividades(discente, new TipoAtividade( TipoAtividade.TRABALHO_CONCLUSAO_CURSO ), 
					situacoes);
		
		if( !isAllEmpty( matriculas ) ){
			
			obj.setMatriculaComponente(  matriculas.iterator().next() );
			
			if( !isEmpty(obj.getMatriculaComponente().getRegistroAtividade()) ){
				obj.getMatriculaComponente().setRegistroAtividade( 
					getGenericDAO().findAndFetch( obj.getMatriculaComponente().getRegistroAtividade().getId(),
							RegistroAtividade.class, "orientacoesAtividade" ) );
			}else{
				addMensagemErro("O Discente selecionado não possui orientação cadastrada.");
			}	
			
		}else{
			addMensagemErro("Não é possível cadastrar uma banca sem o discente estar" +
					" matriculado ou ter concluído uma atividade desse tipo.");
		}
		
	}

	/** 
	 * Carrega as {@link AreaConhecimentoCnpq áreas de conhecimento}.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
     *   <li>/ensino/banca_defesa/form.jsp</li>
     * </ul>  
	 * @param evt
	 * @throws DAOException
	 */	
	private void carregarAreas() throws DAOException {
		if (isEmpty(obj.getGrandeArea()))
			obj.setGrandeArea( new AreaConhecimentoCnpq() );
		
		if (obj.getArea() != null && obj.getArea().getGrandeArea() != null)
			obj.setGrandeArea( obj.getArea().getGrandeArea() );
		else
			obj.setArea(new AreaConhecimentoCnpq());
		
		if (obj.getSubArea() == null)
			obj.setSubArea(new AreaConhecimentoCnpq());
		
		/*if (!isEmpty(obj.getSubArea()) && obj.getSubArea().getArea() != null){
			obj.setArea(obj.getSubArea().getArea());
			if (obj.getSubArea().getGrandeArea() != null)
				obj.setGrandeArea( obj.getSubArea().getGrandeArea() );
		} else if (obj.getArea() != null && obj.getArea().getSubarea() != null)
			obj.setSubArea( obj.getArea().getSubarea() );		
		else if (obj.getArea() != null && obj.getArea().getGrandeArea() != null)
			obj.setGrandeArea( obj.getArea().getGrandeArea() );*/
		
		carregaAreas(null);
		carregaSubAreas(null);
	}
	
	/** 
	 * Retorna a coleção de selectItem de atividades que o discente está matriculado ou cumpriu.
	 * <br /><br />
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * <li>/ensino/banca_defesa/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getMatriculasComponenteCombo() throws DAOException{
		matriculasComponenteCombo = null;
		if (matriculasComponenteCombo == null) {
			MatriculaComponenteDao mdao = getDAO(MatriculaComponenteDao.class);
			TipoAtividade tipo = new TipoAtividade(TipoAtividade.TRABALHO_CONCLUSAO_CURSO);
			SituacaoMatricula[] situacoes = SituacaoMatricula.getSituacoesMatriculadoOuConcluido().toArray(new SituacaoMatricula[0]);
			Collection<MatriculaComponente> matriculas = mdao.findAtividades(obj.getDiscente(), tipo, situacoes);
			matriculasComponenteCombo = new ArrayList<SelectItem>();
			for (MatriculaComponente matricula : matriculas) {
				matricula.setSituacaoMatricula(mdao.refresh(matricula.getSituacaoMatricula()));
				matriculasComponenteCombo.add(new SelectItem(matricula.getId(), matricula.getComponenteCodigoNome()+ " (" +
						matricula.getAnoPeriodo() + ") - " + matricula.getSituacaoMatricula().getDescricao()));
			}
		}
		return matriculasComponenteCombo;
	}	
	
	/**
	 * Redireciona para a tela de dados da banca
	 * <br /><br />
	 * Método chamado pelas seguintes JSPs: 
	 * <ul>
	 * 	<li>/ensino/banca_defesa/membros.jsp</li>
	 * 	<li>/ensino/banca_defesa/resumo.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String telaDadosBanca() throws DAOException{
		obj.setHora(obj.getDataDefesa());
		carregarAreas();		
		return forward(getFormPage());
	}
	
	/**
	 * Redireciona para a tela de membros
	 * <br /><br />
	 * Método chamado pela seguinte JSP: 
	 * <ul>
	 * 	<li>/ensino/banca_defesa/resumo.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaMembros(){
		return forward(getMembrosPage());
	}
	
	/**
	 * Retorna o caminho do formulário de cadastro da banca
	 * <br/><br/>
	 * Método não chamado por JSP
	 */
	@Override
	public String getFormPage() {
		return "/ensino/banca_defesa/form.jsf";
	}

	/**
	 * Retorna o caminho do formulário de cadastro dos membros da banca
	 * <br/><br/>
	 * Método não chamado por JSP
	 */	
	public String getMembrosPage(){
		return membroBancaMBean.getFormPage();
	}
	
	/**
	 * Retorna o caminho da tela de resumo do cadastro da banca
	 * <br/><br/>
	 * Método não chamado por JSP
	 */	
	public String getResumoPage(){
		return "/ensino/banca_defesa/resumo.jsf";
	}
	
	public List<SelectItem> getSubAreas() {
		return subAreas;
	}

	public void setSubAreas(List<SelectItem> subAreas) {
		this.subAreas = subAreas;
	}

	public List<SelectItem> getAreas() {
		return areas;
	}

	public void setAreas(List<SelectItem> areas) {
		this.areas = areas;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public boolean isCadastro() {
		return cadastro;
	}

	public void setCadastro(boolean cadastro) {
		this.cadastro = cadastro;
	}
}
