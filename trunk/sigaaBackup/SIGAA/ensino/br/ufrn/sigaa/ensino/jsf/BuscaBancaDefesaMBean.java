/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/02/2011
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.BancaDefesaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.BancaDefesa;
import br.ufrn.sigaa.ensino.dominio.MembroBanca;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;

/**
 * MBean Respons�vel por gerenciar a busca banca de defesa de TCC.
 * 
 * @author arlindo
 *
 */
@Component("buscaBancaDefesaMBean") @Scope("request")
public class BuscaBancaDefesaMBean extends SigaaAbstractController<BancaDefesa> {
	
	/** MBean que auxilia nas opera��es 
	 * relacionadas as bancas de defesa */
	@Autowired
	private BancaDefesaMBean bancaDefesaMBean;
	
	/** Listagem com o resultado da busca */
	private List<BancaDefesa> listagem = new ArrayList<BancaDefesa>();
	
	/** Indica se ser� buscado pelo discente */
	private boolean checkDiscente;
	/** Indica se ser� buscado T�tulo do trabalho */
	private boolean checkTituloTrabalho;
	/** Indica se ser� buscado Docente */
	private boolean checkDocente;
	/** Indica se ser� buscado pela data de In�cio da banca */
	private boolean checkDataInicio;
	/** Indica se ser� buscado pela data de Fim da banca */
	private boolean checkDataFim;
	/** Discente que ser� consultado */
	private String discente;
	/** Docente que ser� consultado */
	private String docente;
	/** T�tulo que ser� consultado */
	private String tituloTrabalho;
	/** Data de In�cio ser� consultado */
	private Date dataInicio;
	/** Data de Fim ser� consultado */
	private Date dataFim;
	
	/**
	 * Construtor padr�o
	 */
	public BuscaBancaDefesaMBean() {
		initObj();
	}
	
	/**
	 * Inicializa os objetos
	 */
	private void initObj(){
		checkDiscente = false;
		checkTituloTrabalho = false;
		checkDocente = false;
		checkDataInicio = false;
		checkDataFim = false;
		
		discente = null;
		docente = null;
		tituloTrabalho = null;
		
		dataInicio = new Date();
		dataFim = new Date();
	}
	
	/**
	 * Inicia a consulta de bancas
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar(){
		return forward(getListPage());
	}
	
	/**
	 * Busca por banca de defesas
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/banca_defesa/busca.jsp</li>
	 * </ul>
	 */
	public String buscar() throws DAOException{
		
		if ( !validarParametros() ) 
			return null;

		BancaDefesaDao dao = getDAO(BancaDefesaDao.class);
		try {
			 listagem = dao.consultarBancas(
					 (isPortalCoordenadorGraduacao() ? getCursoAtualCoordenacao().getId() : null),
					 (checkDataInicio ? dataInicio : null),
					 (checkDataFim ? dataFim : null), 
					 (checkTituloTrabalho ? tituloTrabalho : null), 
					 (checkDocente ? docente : null), 
					 (checkDiscente ? discente : null));
			 if (ValidatorUtil.isEmpty(listagem)){
				 addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				 return null;
			 }			
		} finally {
			
		}
		return forward(getListPage()); 
	}
	
	/**
	 * Valida os dados do formul�rio.
	 */
	private boolean validarParametros() {
		boolean paramsValido = true;
				
		if (checkDataInicio == true) {
			if ( dataInicio == null ) {
				addMensagemErro("� necessario informar a Data In�cio, pois voc� especificou esse crit�rio na busca.");
				paramsValido = false;
			}
		}
		
		if (checkDataFim == true) {
			if ( dataFim == null ) {
				addMensagemErro("� necessario informar a Data Fim, pois voc� especificou esse crit�rio na busca.");
				paramsValido = false;
			}
		}
		
		if (checkTituloTrabalho == true ) {
				if (tituloTrabalho == null || tituloTrabalho.trim().isEmpty() ) {
					addMensagemErro("� necess�rio informar o T�tulo do trabalho, pois voc� especificou esse crit�rio na busca.");
					paramsValido = false;
				}
		} 
		
		if (!checkDataInicio) {
			dataInicio = null;
		}
		if (!checkDataFim) {
			dataFim = null;
		}
		if(checkDataInicio && checkDataFim
				&& dataFim != null && dataInicio != null
				&& dataFim.before(dataInicio)) {
			addMensagemErro("A data inicial n�o pode ser posterior � data final.");
			paramsValido = false;
		}
		
		if (checkDocente){
			if (docente == null || docente.trim().isEmpty()){
				addMensagemErro("� necess�rio informar o Docente, pois voc� especificou esse crit�rio na busca.");
				paramsValido = false;
			}
		}
		
		if (checkDiscente){
			if (discente == null || discente.trim().isEmpty()){
				addMensagemErro("� necess�rio informar o Discente, pois voc� especificou esse crit�rio na busca.");
				paramsValido = false;
			}
		}			
		
		if (!checkDataInicio &&
				!checkDataFim &&
				!checkTituloTrabalho &&
				!checkDiscente &&
				!checkDocente) {
			addMensagemErro("� necess�rio informar pelo menos um crit�rio na busca.");
			paramsValido = false;
		}
		
		hasErrors();
		
		return paramsValido;
	}	
	
	/**
	 * Carrega os dados da banca de defesa
	 * @throws DAOException
	 * @throws ArqException
	 */
	private void carregarBanca() throws DAOException, ArqException {
		
		obj = new BancaDefesa();
		populateObj(true);		
		
		obj.getMembrosBanca().iterator();
		for (MembroBanca mb : obj.getMembrosBanca()){
			if( !isEmpty(mb.getTipo()) ) 
				mb.getTipo().getDescricao();
			if (mb.isInterno())
				mb.getServidor().getNome();
			if (mb.isExterno())
				mb.getDocenteExterno().getNome();
			if ( !isEmpty(mb.getMembroIdentificacao()) )
				mb.getMembroIdentificacao();
			if (mb.isExternoInstituicao()){
				mb.getPessoaMembroExterno().getNome();
				mb.getInstituicao().getSigla();
				mb.getMaiorFormacao().getDenominacao();
			}
		}
		
		RegistroAtividade registro = obj.getMatriculaComponente().getRegistroAtividade();
		if( !isEmpty(registro) ){
			obj.getMatriculaComponente().setRegistroAtividade( 
					getGenericDAO().findAndFetch( registro.getId(), RegistroAtividade.class,
						"orientacoesAtividade" ) );
		}
		
		bancaDefesaMBean.setObj(obj);
		
	}
	
	/**
	 * Visualiza os dados da banca
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/banca_defesa/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String view() throws ArqException {		
		carregarBanca();
		bancaDefesaMBean.setCadastro(false);
		return forward(bancaDefesaMBean.getResumoPage());
	}
	
	/**
	 * Altera os dados da banca
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/banca_defesa/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String alterar() throws ArqException {
		carregarBanca();		
		return bancaDefesaMBean.alterarBanca();
	}
	
	/**
	 * Remove (Desativa) a banca selecionada
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/banca_defesa/lista.jsp</li>
	 * </ul>
	 */
	public String remover() throws ArqException {
		int id = getParameterInt("id", 0);
		setObj( getGenericDAO().findByPrimaryKey(id, BancaDefesa.class) );
		bancaDefesaMBean.removerBanca(obj);
		return buscar();
	}
	
	/**
	 * Retorna o caminho da lista de bancas
	 * <br/><br/>
	 * M�todo n�o chamado por JSP
	 */	
	@Override
	public String getListPage() {
		return "/ensino/banca_defesa/busca.jsf";
	}

	public boolean isCheckDiscente() {
		return checkDiscente;
	}

	public void setCheckDiscente(boolean checkDiscente) {
		this.checkDiscente = checkDiscente;
	}

	public boolean isCheckTituloTrabalho() {
		return checkTituloTrabalho;
	}

	public void setCheckTituloTrabalho(boolean checkTituloTrabalho) {
		this.checkTituloTrabalho = checkTituloTrabalho;
	}

	public boolean isCheckDocente() {
		return checkDocente;
	}

	public void setCheckDocente(boolean checkDocente) {
		this.checkDocente = checkDocente;
	}

	public boolean isCheckDataInicio() {
		return checkDataInicio;
	}

	public void setCheckDataInicio(boolean checkDataInicio) {
		this.checkDataInicio = checkDataInicio;
	}

	public boolean isCheckDataFim() {
		return checkDataFim;
	}

	public void setCheckDataFim(boolean checkDataFim) {
		this.checkDataFim = checkDataFim;
	}

	public String getDiscente() {
		return discente;
	}

	public void setDiscente(String discente) {
		this.discente = discente;
	}

	public String getDocente() {
		return docente;
	}

	public void setDocente(String docente) {
		this.docente = docente;
	}

	public String getTituloTrabalho() {
		return tituloTrabalho;
	}

	public void setTituloTrabalho(String tituloTrabalho) {
		this.tituloTrabalho = tituloTrabalho;
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

	public List<BancaDefesa> getListagem() {
		return listagem;
	}

	public void setListagem(List<BancaDefesa> listagem) {
		this.listagem = listagem;
	}
}
