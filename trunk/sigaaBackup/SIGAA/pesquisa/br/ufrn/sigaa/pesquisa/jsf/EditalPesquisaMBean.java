/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/08/2010
 *
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.pesquisa.dominio.CategoriaProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.Cotas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.EditalPesquisaValidator;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoEditalPesquisa;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Controlador responsável pelas operações de Editais de Pesquisa
 * 
 * @author Thalisson Muriel
 *
 */

@Component("editalPesquisaMBean") @Scope("session")
public class EditalPesquisaMBean extends SigaaAbstractController<EditalPesquisa> {
	
	/** Constantes das Views */
	public static final String JSP_FORM_FILE_EDITAL = "/pesquisa/EditalPesquisa/arquivoEdital.jsp";
	/** Arquivo que o edital pode ser inserido no edital */	
	private UploadedFile arquivoEdital;
	/** Armazena o tipo de bolsa de pesquisa do edital */
	private TipoBolsaPesquisa tipoBolsa;
	/** Quantidade de bolsas que o edital apresenta */
	private Integer quantidade;
	/** Boleano responsável pelas busca dos editais de Apoio ou de Bolsa */
	private boolean checkTipoEdital;
	/** Boleano responsável pelas busca por ano período */
	private boolean checkAno;
	/** Responsável pelo informação do tipo de bolsa a ser realizada */
	private char buscaTipoBolsa; 
	/** Responsável pelo informação do ano */
	private int ano;
	
	/**
	 * Construtor
	 */
	public EditalPesquisaMBean(){
		clear();
	}
	
	/**
	 * Limpa os atributos que serão utilizados na operação.
	 */
	private void clear(){
		obj = new EditalPesquisa();
		obj.setEdital(new Edital());
		obj.setCota(new CotaBolsas());
		obj.setCategoria(new CategoriaProjetoPesquisa());
		obj.setAno(getCalendarioVigente().getAno());
		obj.setAvaliacaoVigente(false);
		obj.setIndiceChecagem(new IndiceAcademico());
		obj.setFppiMinimo(0.0);
		tipoBolsa = new TipoBolsaPesquisa();
		arquivoEdital = null;
		obj.getEdital().getRestricaoCoordenacao().setMaxCoordenacoesAtivas(1);
		obj.getEdital().setTipo('P');
		quantidade = 0;
		erros = new ListaMensagens();
	}
	
	/**
	 * Verifica as permissões de acesso, popula as informações necessárias e
	 * redireciona para a página de listagem dos Editais de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/projetos.jsp</li>
	 *	</ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciar()throws SegurancaException, DAOException{
		checkChangeRole();
		clear();
		setConfirmButton("Cadastrar");
		resultadosBusca = getDAO(EditalPesquisaDao.class).findByAtivoAnoTipoEdital(CalendarUtils.getAnoAtual(), null);
		checkAno = false;
		checkTipoEdital = false;
		removeOperacaoAtiva();
		ano = CalendarUtils.getAnoAtual();
		return forward(getListPage());
	}
	
	@Override
	public String buscar() throws Exception {
		Boolean bolsa = null;
		Integer anoConsulta = null;
		
		if ( !checkAno && !checkTipoEdital ) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}
		
		if ( checkAno ) 
			anoConsulta = ano;
		
		if ( checkTipoEdital )
			bolsa = buscaTipoBolsa == 'B' ? true : false; 
			
		resultadosBusca = getDAO(EditalPesquisaDao.class).findByAtivoAnoTipoEdital(anoConsulta, bolsa);
		return forward(getListPage());
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
	}
	
	@Override
	public String getDirBase() {
		return "/pesquisa/EditalPesquisa";
	}
	
	/**
	 * Inicia o caso de uso de cadastro do Edital de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/lista.jsp</li>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/view.jsp</li>
	 *	</ul>
	 * @throws ArqException 
	 */
	public String preCadastrar() throws ArqException{
		checkChangeRole();
		clear();
		setOperacaoAtiva(SigaaListaComando.PUBLICAR_EDITAL_PESQUISA.getId());
		prepareMovimento(SigaaListaComando.PUBLICAR_EDITAL_PESQUISA);
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}
	
	/**
	 * Método responsável pela publicação de um Edital de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/form.jsp</li>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/arquivoEdital.jsp</li>
	 *	</ul>
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	@Override
	public String cadastrar() throws ArqException, NegocioException{
		checkChangeRole();
		
		try {		
			if (isOperacaoAtiva(SigaaListaComando.PUBLICAR_EDITAL_PESQUISA.getId()) ){
				MovimentoEditalPesquisa movEdital = new MovimentoEditalPesquisa();
				erros = new ListaMensagens();
				EditalPesquisaValidator.validaEdital(obj, erros, getDAO(EditalPesquisaDao.class));
				
				if (!erros.isEmpty()) {
					addMensagens(erros);
					return null;
				}
				if(!obj.isDistribuicaoCotas() && obj.getCotas() != null)
					obj.getCotas().clear();
				
				if( obj.getIndiceChecagem() != null && obj.getIndiceChecagem().getId() <= 0 )
					obj.setIndiceChecagem(null);
				
				movEdital.setCodMovimento(SigaaListaComando.PUBLICAR_EDITAL_PESQUISA);
				
				obj.setUsuario(getUsuarioLogado());
				movEdital.setObjMovimentado(obj);

				execute(movEdital);

				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			} 
			else if (isOperacaoAtiva(SigaaListaComando.ENVIAR_ARQUIVO_EDITAL_PESQUISA.getId()) )
				 return enviarArquivo();
			else if(isOperacaoAtiva(SigaaListaComando.REMOVER_EDITAL_PESQUISA.getId())) 
				return remover();
			else {
				addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
				return cancelar();
			}
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		return iniciar();
	}
	
	/**
	 * Adiciona uma quantidade de Cotas do tipo informado para serem distribuídas através do Edital de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/form.jsp</li>
	 *	</ul>
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String adicionarCotas() throws SegurancaException, DAOException{
		checkChangeRole();
		
		if (tipoBolsa.getId() != -1) 
			tipoBolsa = getGenericDAO().findByPrimaryKey(tipoBolsa.getId(), TipoBolsaPesquisa.class);
		
		//valida dados
		erros = new ListaMensagens();
		EditalPesquisaValidator.validaAdicionaCota(obj, tipoBolsa, quantidade, erros);
		if (!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}
		
		Cotas cota = new Cotas();
		cota.setTipoBolsa(tipoBolsa);
		cota.setQuantidade(quantidade);
		obj.addCotas(cota);
		
		tipoBolsa = new TipoBolsaPesquisa();
		
		return forward(getFormPage());
	}
	
	/**
	 * Remove uma quantidade de Cotas do Edital de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/form.jsp</li>
	 *	</ul>
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void removerCotas() throws ArqException, NegocioException{
		checkChangeRole();
		
		int id = getParameterInt("idCotas");
		Cotas cota = new Cotas();
		
		if(id > 0){
			
			for(Cotas c : obj.getCotas()){
				if(c.getId() == id)
					cota = c;
			}
			obj.getCotas().remove(cota);
			
			prepareMovimento(ArqListaComando.REMOVER);
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(ArqListaComando.REMOVER);
			mov.setObjMovimentado(cota);
			
			executeWithoutClosingSession(mov);
		} else {
			String descricao = getParameter("descricao");
			for(Cotas c : obj.getCotas()){
				if(c.getTipoBolsa().getDescricaoResumida().equalsIgnoreCase(descricao))
					cota = c;
			}
			obj.getCotas().remove(cota);
		}

	}
	
	/**
	 * Método responsável pela visualização dos dados de um Edital de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException{
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), EditalPesquisa.class);
		return forward(getViewPage());
	}
	
	/**
	 * Método responsável pela edição de um Edital de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String alterar() throws ArqException{
		try {
			clear();
			checkChangeRole();
			setOperacaoAtiva(SigaaListaComando.PUBLICAR_EDITAL_PESQUISA.getId());
			prepareMovimento(SigaaListaComando.PUBLICAR_EDITAL_PESQUISA);
			setId();
			setReadOnly(false);
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), EditalPesquisa.class);
			if( obj.getIndiceChecagem() == null )
				obj.setIndiceChecagem(new IndiceAcademico());
			setConfirmButton("Alterar");
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
		return forward(getFormPage());
	}
	
	/**
	 * Redireciona para a página de envio do arquivo do Edital.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String preEnviarAqruivo() throws ArqException{
		checkChangeRole();
		setOperacaoAtiva(SigaaListaComando.ENVIAR_ARQUIVO_EDITAL_PESQUISA.getId());
		prepareMovimento(SigaaListaComando.ENVIAR_ARQUIVO_EDITAL_PESQUISA);
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), EditalPesquisa.class);
		if(obj.getCotas() != null)
			obj.getCotas().iterator();
		setConfirmButton("Enviar Arquivo");
		return forward(JSP_FORM_FILE_EDITAL);
	}
	
	/**
	 * Submete o arquivo do edital gravando-o na base de arquivos.
	 * <br>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private String enviarArquivo() throws ArqException, NegocioException {
		MovimentoEditalPesquisa movEdital = new MovimentoEditalPesquisa();
		if (obj == null || obj.getId() == 0) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return iniciar();
		}
		else {
			try {
				erros = new ListaMensagens();
				EditalPesquisaValidator.validaArquivoEdital(obj, arquivoEdital, erros);
				if (!erros.isEmpty()) {
					addMensagens(erros);
					return null;
				}

				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, arquivoEdital.getBytes(), arquivoEdital.getContentType(), arquivoEdital.getName());
				obj.setIdArquivo(idArquivo);

				movEdital.setArquivoEdital(arquivoEdital);

				movEdital.setCodMovimento(SigaaListaComando.ENVIAR_ARQUIVO_EDITAL_PESQUISA);

				obj.setUsuario(getUsuarioLogado());
				movEdital.setObjMovimentado(obj);

				execute(movEdital);
				
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			} catch (IOException e) {
				tratamentoErroPadrao(e);
				return null;
			}
		}
		
		return iniciar();
	}
	
	/**
	 * Inicia o caso de uso de remoção do Edital de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String preRemover(){
		try {
			checkChangeRole();
			setOperacaoAtiva(SigaaListaComando.REMOVER_EDITAL_PESQUISA.getId());
			prepareMovimento(SigaaListaComando.REMOVER_EDITAL_PESQUISA);
			setId();
			setReadOnly(false);
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), EditalPesquisa.class);
			setConfirmButton("Remover");
		} catch (SegurancaException e) {
			tratamentoErroPadrao(e);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		}
		
		return forward(getViewPage());
	}
	
	/**
	 * Método responsável pela remoção de um Edital de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/view.jsp</li>
	 *	</ul>
	 */
	@Override
	public String remover() throws ArqException{
		if (obj == null || obj.getId() == 0) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return iniciar();
		} else {
			try {
				MovimentoEditalPesquisa movEdital = new MovimentoEditalPesquisa();
				
				movEdital.setObjMovimentado(obj);
				movEdital.setCodMovimento(SigaaListaComando.REMOVER_EDITAL_PESQUISA);
				
				execute(movEdital);
				
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			}
		}
		return iniciar();
	}
	
	/**
	 * Retorna uma coleção de Cotas de Bolsas.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/form.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getCotas() throws ArqException{
		return toSelectItems(getGenericDAO().findAll(CotaBolsas.class), "id", "descricao");
	}

	/**
	 * Retorna uma coleção de Categorias de Projetos de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/form.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getCategorias() throws ArqException{
		return toSelectItems(getGenericDAO().findAllAtivos(CategoriaProjetoPesquisa.class, "ordem"), "id", "denominacao");
	}
	
	/**
	 * Retorna uma coleção de Tipos de Bolsa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/form.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getTiposBolsa() throws ArqException{
		return toSelectItems(getGenericDAO().findAll(TipoBolsaPesquisa.class, "descricao", "asc"), "id", "descricaoResumida");
	}

	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return toSelectItems(getDAO(EditalDao.class).findAllAtivosPesquisa(), "id", "edital.descricao");
	}
	
	/**
	 * Retorna os tipo de Edital 
	 *
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/EditalPesquisa/lista.jsp</li>
	 *	</ul>
	 *  
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getTiposEdital() throws ArqException{
		List<SelectItem> tiposBolsas = new ArrayList<SelectItem>();
		tiposBolsas.add(new SelectItem("B", "Bolsa"));
		tiposBolsas.add(new SelectItem("A", "Apoio"));
		return tiposBolsas;
	}
	
	public Collection<SelectItem> getEditaisCombo() throws ArqException{
		return toSelectItems(getDAO(EditalPesquisaDao.class).findAllAbertosTipo(false), "id", "edital.descricao");
	}
	
	/**
	 * Retorna uma coleção de Titulações.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/EditalPesquisa/form.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getTitulacoes(){
		return toSelectItems(EditalPesquisa.getTitulacoes());
	}
	
	public UploadedFile getArquivoEdital() {
		return arquivoEdital;
	}

	public void setArquivoEdital(UploadedFile arquivoEdital) {
		this.arquivoEdital = arquivoEdital;
	}

	public TipoBolsaPesquisa getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoBolsaPesquisa tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public boolean isCheckTipoEdital() {
		return checkTipoEdital;
	}

	public void setCheckTipoEdital(boolean checkTipoEdital) {
		this.checkTipoEdital = checkTipoEdital;
	}

	public boolean isCheckAno() {
		return checkAno;
	}

	public void setCheckAno(boolean checkAno) {
		this.checkAno = checkAno;
	}

	public char getBuscaTipoBolsa() {
		return buscaTipoBolsa;
	}

	public void setBuscaTipoBolsa(char buscaTipoBolsa) {
		this.buscaTipoBolsa = buscaTipoBolsa;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

}