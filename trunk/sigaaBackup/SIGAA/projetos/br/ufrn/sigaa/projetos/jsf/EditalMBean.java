/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/09/2009
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Gerado pelo CrudBuilder
 * Controlador para cadastro de editais associados
 * 
 * @author Leonardo
 * @author Ilueny Santos
 */
@Component
@Scope("request")
public class EditalMBean extends SigaaAbstractController<Edital> {

	/** Atributo utilizado para filtro de Tipo */
	private boolean filtroTipo;
	/** Atributo utilizado para filtro de Ano */
	private boolean filtroAno;
	/** Atributo utilizado para filtro de Descrição */
	private boolean filtroDescricao;
	/** Informa se está alterando ou cadastrando */
	private boolean alterar;
	
	/** Atributo utilizado para representar o arquivo */
	private UploadedFile file;
	
	/** Atributo utilizado para representar vários editais */
	Collection<Edital> editais = new ArrayList<Edital>();
	
	/**
	 * Método utilizado para iniciar o Edital, inicializando suas variáveis.
	 */
	private void iniciar() {
	    obj = new Edital();
	    obj.setTipo(Edital.ASSOCIADO);
	    obj.setAtivo(true);
	    obj.setAno(CalendarUtils.getAnoAtual());
	    obj.setSemestre(getPeriodoAtual());
	}

	/**
	 * Construtor padrão
	 */
	public EditalMBean() {
	    iniciar();
	}

	/**
	 * Método utilizado para redirecionar para página de listagem
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String forwardCadastrar() {
	    return getListPage();
	}

	@Override
	public String getFormPage() {
	    return "/projetos/Edital/form.jsf";
	}

	@Override
	public String getListPage() {
	    return "/projetos/Edital/lista.jsf";
	}
	
	public String getBusca(){
		return forward("/projetos/Edital/buscar.jsf");
	}
	
	/**
	 * Método utilizado para informar todos os Editais ativos do tipo em sessão.
	 */
	@Override
	public Collection<Edital> getAll() throws ArqException {
	    return getDAO(EditalDao.class).findAllAtivosByTipo(getTipoEdital());
	}

	/**
	 * Método utilizado para checar os papeis do usuario que acessa os Editais
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	}
	
	/**
	 * Método utilizado para realizar ações antes de cadastrar e depois de validar os dados
	 *
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 */
	@Override
	public void beforeCadastrarAfterValidate()  {
	   if(obj.getId() == 0){ 
		obj.setUsuario(getUsuarioLogado());
	    obj.setDataCadastro(new Date());	    
	    cadastrarArquivoEdital();
	  } else {
		  // Mantendo informações originais do objeto que não serão alteradas
		  try {			  
			Edital e = getGenericDAO().findByPrimaryKey(obj.getId(), Edital.class);
			obj.setDataCadastro(e.getDataCadastro());
			obj.setUsuario(e.getUsuario()); // usuario que cadastrou a ação
			if(file != null)
				cadastrarArquivoEdital(); // caso haja arquivo de upload, salva novo arquivo de edital
			else obj.setIdArquivo(e.getIdArquivo()); // Para tornar o envio de arquivo opcional na edição
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
	  }
	}
	
	/**
	 * Método utilizado para realizar a inserção do arquivo e a captura do id do arquivo inserido
	 *  Chamado pelo método beforeCadastrarAfterValidate();
	 */
	public void cadastrarArquivoEdital(){
		try {
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, file.getBytes(), file.getContentType(), file.getName());
				obj.setIdArquivo(idArquivo);
		    } catch (IOException e) {
		    	tratamentoErroPadrao(e);
		    }
		
	}
	
	/**
	 * Lista todos os editais abertos.
	 * <br>
	 * Método utilizado para informar os Editais de ações associadas abertos
	 * <ul>
	 * 		<li>/sigaa.war/projetos/ProjetoBase/dados_gerais.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getEditaisCombo() throws DAOException {
		return toSelectItems(getDAO(EditalDao.class).findAbertos(Edital.ASSOCIADO), "id", "descricao");
	}

	/**
	 * Lista todos os editais ativos de projetos associados.
	 * <br>
	 * Método utilizado para informar os Editais de ações associadas abertos
	 * <ul>
	 * 		<li>/sigaa.war/projetos/form_busca_discentes.jsp</li>
	 * 		<li>/sigaa.war/projetos/form_busca_projetos.jsp</li>
	 * 		<li>/sigaa.war/projetos/Avaliacoes/ModeloAvaliacao/form.jsp</li>
	 * 		<li>/sigaa.war/projetos/Relatorios/dados_bancarios_discentes_form.jsp</li>
	 * 		<li>/sigaa.war/projetos/Relatorios/seleciona_quant_proj_sub_reuni.jsp</li>
	 * </ul>
	 * 
	 */
	public Collection<SelectItem> getAllCombo() throws ArqException {
	    return toSelectItems(getAll(), "id", "descricao");
	}
	
	/**
	 * Método utilizado para realizar algumas ações antes de cadastrar e validar
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,  SegurancaException, DAOException {
	    
		if(file == null && obj.getId() == 0){
	    	addMensagemErro("Arquivo do Edital: Campo obrigatório não informado.");
	    }
	    if (obj.getRestricaoCoordenacao() != null) {
	    	if (!obj.getRestricaoCoordenacao().isPermitirCoordenadorTecnico()) {
	    		obj.getRestricaoCoordenacao().setApenasTecnicoSuperiorCoordena(false);	    		
	    	}
	    }
	    
	    // Verificação de Datas de Submissões 
	    if(obj.getInicioSubmissao() != null && obj.getFimSubmissao() != null && obj.getInicioSubmissao().compareTo(obj.getFimSubmissao())>0) 
	    	addMensagemErro("O fim do período de submissões não pode ser anterior ao início do período de submissões.");	    
	    //Verificação de Datas de Realizações
	    if(obj.getInicioRealizacao() != null && obj.getFimRealizacao() != null && obj.getInicioRealizacao().compareTo(obj.getFimRealizacao())>0) addMensagemErro("O fim do período de realização dos projetos não pode ser anterior ao início do período de realização.");	    	    
	    
	    //Verificação de Datas de Reconsideração
	    if(obj.getDataInicioReconsideracao() != null && obj.getDataFimReconsideracao() != null && obj.getDataInicioReconsideracao().compareTo(obj.getDataFimReconsideracao())>0) addMensagemErro("O fim do período de reconsideração não pode ser anterior ao início do período de reconsideração."); 
	    
    
	    
	    
	    super.beforeCadastrarAndValidate();
	}
	
	/**
	 * Visualiza o arquivo do edital
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/EditalExtensao/lista.jsp
	 * sigaa.war/monitoria/EditalMonitoria/lista.jsp
	 * 
	 * @param event
	 * @throws ArqException
	 */
	public String viewArquivo() throws ArqException {
	    setId();
	    obj = getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());

	    FacesContext faces = FacesContext.getCurrentInstance();
	    HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();

	    try {
		Integer idArquivo = obj.getIdArquivo();
		if (ValidatorUtil.isEmpty(idArquivo)) {
		    addMensagemErro("Edital selecionado não possui arquivo anexo.");
		} else {
		    EnvioArquivoHelper.recuperaArquivo(response, idArquivo, false);
		    faces.responseComplete();
		}
		return null;
	    } catch (IOException e) {
		tratamentoErroPadrao(e);
		return null;
	    }
	}

	/**
	 * Realizar uma busca nos editais pelas informações passadas no formulário.
	 * 
	 * Chamado por:
	 * 		/SIGAA/app/sigaa.ear/sigaa.war/projetos/Edital/buscar.jsp
	 */
	@Override
	public String buscar() throws Exception {
		if (!filtroAno && !filtroDescricao && !filtroTipo) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}
		
		if (filtroDescricao && obj.getDescricao().equals("")) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descrição");
		
		if (filtroAno){
			if (obj.getAno() == null || obj.getAno().equals("") || obj.getSemestre() == 0) 
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano-Período");
		}

		if (hasOnlyErrors()) 
			return null;
		
		Character tipo = null;
		Integer ano = null, semestre = null;
		String descricao = null;
		
		if (filtroDescricao) 
			descricao = obj.getDescricao();
		if (filtroTipo) 
			tipo = obj.getTipo();
		if (filtroAno) {
			ano = obj.getAno();
			semestre = obj.getSemestre();
		}

		EditalDao dao = getDAO(EditalDao.class);
		editais = dao.findEdital(tipo, ano, semestre, descricao);
		if (editais.size() == 0) 
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		return null;
	}

	@Override
	public void afterAtualizar() throws ArqException {
		alterar = true;
	}
	
	@Override
	public String getAtributoOrdenacao() {
	    return "inicioSubmissao";
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
	    this.file = file;
	}

	public boolean isFiltroTipo() {
		return filtroTipo;
	}

	public void setFiltroTipo(boolean filtroTipo) {
		this.filtroTipo = filtroTipo;
	}

	public boolean isFiltroAno() {
		return filtroAno;
	}

	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}

	public boolean isFiltroDescricao() {
		return filtroDescricao;
	}

	public void setFiltroDescricao(boolean filtroDescricao) {
		this.filtroDescricao = filtroDescricao;
	}

	public Collection<Edital> getEditais() {
		return editais;
	}

	public void setEditais(Collection<Edital> editais) {
		this.editais = editais;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}
	
}