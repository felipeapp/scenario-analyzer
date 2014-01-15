/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/09/2008
 *
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.jsf;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRRtfExporter;

import org.apache.commons.lang.WordUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.FichaCatalografica;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoCatalogacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoDocumentoNormalizacaoCatalogacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.informacao_referencia.negocio.MovimentoSolicitacaoCatalogacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.negocio.MovimentoSolicitacaoDocumento;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * <p>MBean responsável pelos atendimento e solicitação de catalogação (geração da ficha catalográfica) 
 * de obra realizada por discentes ou servidores à biblioteca.
 * </p>
 * 
 * <p>Obs.: A maior parte da geração da ficha é feita pelo sistema, mas ela deve ser editável antes de enviar para o usuário. Gerar um arquivo "txt".
 * Porque sempre existem detalhes na geração da ficha que não é possíevl automatizar
 * </p>
 * 
 * @author Felipe Rivas
 */
@Component("solicitacaoCatalogacaoMBean")
@Scope("request")
public class SolicitacaoCatalogacaoMBean extends AbstractSolicitacaoServicoDocumentoMBean<SolicitacaoCatalogacao> {
	
	/** Página onde o usuário faz uma nova solicitação de catalogação na fonte */
	public static final String PAGINA_NOVA_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoCatalogacao.jsp";
	
	/** Página onde o usuário pode visualizar os dados da sua solicitação */
	public static final String PAGINA_VISUALIZA_DADOS_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacao.jsp";
	
	/** O comprovante de solicitação de catalogação na fonte. */
	public static final String PAGINA_VISUALIZA_COMPROVANTE_SOLICITACAO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/comprovanteSolicitacaoCatalogacao.jsp";
	
	/** Página onde o bibliotecário pode visualizar os dados da solicitação selecionado e atender a solicitação */
	public static final String PAGINA_VISUALIZA_DADOS_SOLICITACAO_ATENDIMENTO =
			"/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp";
	
	/** O relatório Jasper que implementa uma ficha catalográfica. Caso o usuário já enviar a ficha gerada pelo sistema.*/
	public static final String FICHA_CATALOGRAFICA = "fichaCatalografica.jasper";
	
	/** Tipos de geração da ficha catalográfica */
	private enum TipoFicha {
		/** O biblitecário digita as informações da ficha uma-a-uma e o sistema gera a formatação */
		MANUAL,
		/** O bibliotecário informa um arquivo contendo a ficha catalográfica já gerada */
		ARQUIVO
	}
	
	/** Arquivo contendo a ficha catalográfica digitalizada */
	private UploadedFile arquivo;

	/** Lista das notas gerais informadas pelo bibliotecário para geração da ficha catalográfica */
	private List<String> notasGerais;
	/** Lista das notas teses informadas pelo bibliotecário para geração da ficha catalográfica */
	private List<String> notasTeses;
	/** Lista das notas bibliográficas informadas pelo bibliotecário para geração da ficha catalográfica */
	private List<String> notasBibliograficas;
	/** Lista das notas conteúdos informadas pelo bibliotecário para geração da ficha catalográfica */
	private List<String> notasConteudos;
	/** Lista dos assuntos pessoais informados pelo bibliotecário para geração da ficha catalográfica */
	private List<String> assuntosPessoais;
	/** Lista dos assuntos informados pelo bibliotecário para geração da ficha catalográfica */
	private List<String> assuntos;
	/** Lista dos autores secundários informados pelo bibliotecário para geração da ficha catalográfica */
	private List<String> autoresSecundarios;

	/** DataModel para preenchimento da tabela de notas gerais da jsp */
	private DataModel notasGeraisDataModel;
	/** DataModel para preenchimento da tabela de notas teses da jsp */
	private DataModel notasTesesDataModel;
	/** DataModel para preenchimento da tabela de notas bibliográficas da jsp */
	private DataModel notasBibliograficasDataModel;
	/** DataModel para preenchimento da tabela de notas conteúdos da jsp */
	private DataModel notasConteudosDataModel;
	/** DataModel para preenchimento da tabela de assuntos pessoais da jsp */
	private DataModel assuntosPessoaisDataModel;
	/** DataModel para preenchimento da tabela de assuntos da jsp */
	private DataModel assuntosDataModel;
	/** DataModel para preenchimento da tabela de autores secundários da jsp */
	private DataModel autoresSecundariosDataModel;
	
	/** Tipo de geração da ficha catalográfica selecionado pelo bibliotecário */
	private TipoFicha tipoFicha;
	
	/** Guarda a classificação utilizada na biblioteca da solicitação, a partir dela é que se sabe qual clasificação será impressa na ficha catalográfica. */
	private ClassificacaoBibliografica classificacaoUtilizada;
	
	/** Armazena o motivo informado pelo usuário para reenvio da solicitação. */
	private String motivoReenvio;
	
	public SolicitacaoCatalogacaoMBean() throws DAOException {
		super();
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	////// Ultima parte onde o usuário pode ver o resultado do atendimento da solicitação ///////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * <p> Exibe a ficha catalográfica gerada para impressão. </p>
	 * <p> Usado em:
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacao.jsp</li>
	 *   </ul>
	 * </p>
	 * @throws DAOException 
	 */
	public String imprimirFichaCatalograficaPDF() throws DAOException {

		FichaCatalografica ficha = getGenericDAO().refresh(obj.getFichaGerada());
		
		JRDataSource jrds = new JRBeanCollectionDataSource(Arrays.asList(new FichaCatalografica[] { ficha }));
		
		Map<String, String> parametros = new HashMap<String, String>();
		parametros.put("tituloFicha", getTituloFicha());
		parametros.put("siglaInstituicao", getSiglaInstituicao());
		parametros.put("biblioteca", obj.getBiblioteca().getDescricao());
		parametros.put("siglaBiblioteca", obj.getBiblioteca().getIdentificador());
		parametros.put("solicitante", obj.getSolicitante());
		parametros.put("conteudo", new FormatosBibliograficosUtil().gerarFichaCatalografica(ficha, false, 80));
		
		try {
			InputStream relatorio = JasperReportsUtil.getReportSIGAA(FICHA_CATALOGRAFICA);
			JasperPrint prt = JasperFillManager.fillReport(relatorio, parametros, jrds);
			//JasperPrint prt = JasperFillManager.fillReport(relatorio, parametros);
			getCurrentResponse().setContentType("application/pdf");
			getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=\"ficha_catalografica_" + obj.getId() + ".pdf\"");
			JasperExportManager.exportReportToPdfStream(prt,getCurrentResponse().getOutputStream());
		} catch (JRException e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErroPadrao();
		} catch (IOException e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErroPadrao();
		}

		FacesContext.getCurrentInstance().responseComplete();

		return null;
	}
	
			
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	
	
	/**
	 *  <p>Prepara os dados para ir para a tela de atendimento das solicitações.</p>
	 * @throws DAOException 
	 */
	@Override
	protected void prepararAtender(){
		
		
		tipoFicha = obj.getIdFichaDigitalizada() != null ? TipoFicha.ARQUIVO : TipoFicha.MANUAL;
		
		if (obj.getFichaGerada() == null) {
			FichaCatalografica ficha = new FichaCatalografica();
	
			ficha.setAutor(UFRNUtils.prepararNomeFormal(WordUtils.capitalize(obj.getSolicitante().toLowerCase()), false));
			ficha.setResponsabilidade(WordUtils.capitalize(obj.getSolicitante().toLowerCase()));
			ficha.setDescricaoFisica(obj.getNumeroPaginas() + "f");
			ficha.setDescricaoFisicaDetalhes("il");
			if(obj.getBiblioteca().getUnidade() != null && obj.getBiblioteca().getUnidade().getMunicipio() != null)
				ficha.setLocalPublicacao(WordUtils.capitalize(obj.getBiblioteca().getUnidade().getMunicipio().getNome().toLowerCase()));
			ficha.setAno(CalendarUtils.getAnoAtual());
			ficha.setBiblioteca(obj.getBiblioteca().getIdentificador());
			
			obj.setFichaGerada(ficha);
		}

		iniciaColecaoNotasGerais();
		iniciaColecaoNotasTeses();
		iniciaColecaoNotasBibliograficas();
		iniciaColecaoNotasConteudos();
		iniciaColecaoAssuntosPessoais();
		iniciaColecaoAssuntos();
		iniciaColecaoAutoresSecundarios();
	}

	
	
	/**
	 * Salva o estado atual do atendimento da solicitação de catalogação (persiste sem alterar o status para 'Atendido').
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li>
	 * </ul>
	 * 
	 */
	public String salvar() {

		MovimentoCadastro mov = instanciarMovimento();
		
		mov.setObjMovimentado(obj);
		String msgConfirmacao = null;

		try {
			if (isAtender()) {
				confirmarAtender(mov);
				
				mov.setCodMovimento(SigaaListaComando.SALVAR_SOLICITACAO_CATALOGACAO);
				execute(mov);
				
				msgConfirmacao = "Atendimento salvo com sucesso.";
			}
		
			addMensagemInformation(msgConfirmacao);
			
			return verSolicitacoes();
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		} catch (ArqException arqex) {
			addMensagemErro(arqex.getMessage());
			arqex.printStackTrace();
			return null;
		}
		
	}
	
	@Override
	public String atenderSolicitacao() throws ArqException {
		
		prepareMovimento(SigaaListaComando.SALVAR_SOLICITACAO_CATALOGACAO);
		
		ClassificacaoBibliograficaDao dao = null;
		
		try{
			dao = getDAO(ClassificacaoBibliograficaDao.class);
			classificacaoUtilizada = dao.findClassificacaoUtilizadaPelaBiblioteca(obj.getBiblioteca().getId());
			
			if(classificacaoUtilizada == null){
				addMensagemErro("Não é possível atender a solicitação da catalogação porque a biblioteca para qual a solicitação foi enviada não possui uma classificação bibliográfica definida.");
				return null;
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		String retorno = super.atenderSolicitacao();
			
		// Guarda a descrição da classificação que foi utilizada para gerar a ficha
		if( obj.getFichaGerada() != null)
			obj.getFichaGerada().setDescricaoClassificacao(classificacaoUtilizada.getDescricao());
		
		return retorno;
	}
	
	@Override
	protected void prepararVisualizar() throws ArqException {
		if (obj.isAtendido()) {
			prepareMovimento(SigaaListaComando.REENVIAR_SOLICITACAO_CATALOGACAO);
		}
	}
	
	/**
	 * Reenvia a solicitação selecionada para o bibliotecário para correção.
	 * O usuário pode reenviar solicitações que estão com status "Atendido".
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String reenviarSolicitacao() throws ArqException {
		
		getCurrentResponse().reset();
		
		if (StringUtils.isEmpty(motivoReenvio)) {
			addMensagemErro("Informe o motivo relacionado ao problema encontrado na catalogação.");
			
			return null;
		}
		
		try {
			obj.setMotivoReenvio(motivoReenvio);
			
			MovimentoSolicitacaoCatalogacao mov = new MovimentoSolicitacaoCatalogacao();
			
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.REENVIAR_SOLICITACAO_CATALOGACAO);
			
			execute(mov);
			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, "Solicitação");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}
		
		return verMinhasSolicitacoes();
	}
	
	
	
	/**
	 * <p>Gerar um arquivo de texto para o usuário com as informações da ficha catalográfica.</p>
	 *  
	 * <p>Para o bibliotecário poder editar as informações geradas pelo sistema e enviar para o usuário. 
	 * Já que não é possível fazer o sistema gerá 100% correto a ficha catalográfica.
	 * </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/atendimento/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li>
	 *   </ul>
	 *
	 * @param evt
	 * @throws IOException 
	 */
	public void gerarFichaEmArquivo(ActionEvent evt) throws IOException{
		
		HttpServletResponse response = getCurrentResponse();
			
		DataOutputStream dos  = new DataOutputStream(response.getOutputStream());
		dos.write(getFichaCatalografica().replaceAll("&nbsp;", " ").replaceAll("<br/>", "\r\n").getBytes());
		
		response.setContentType("Content-Type: text/html; charset=ISO2709");
		response.addHeader("Content-Disposition", "attachment; filename=ficha.txt");
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	
	
	/**
	 * <p>Gerar um arquivo de texto para o usuário com as informações da ficha catalográfica.</p>
	 * </p>
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>Método não é chamado por nenhuma JSP</li>
	 *   </ul>
	 *
	 * @return 
	 */
	public String getFichaCatalografica() {
		if(obj.getFichaGerada() != null){
			return new FormatosBibliograficosUtil().gerarFichaCatalografica(obj.getFichaGerada(), true, 60);
		}else{
			return null;
		}
	}
	
	public String getFichaDigitalizada() {
		return null;
	}	
	
	//////////////////////////   telas de navegação  //////////////////////////////////////
	
	@Override
	protected String telaNovaSolicitacaoServico(){
		return forward(PAGINA_NOVA_SOLICITACAO);
	}
	
	@Override
	protected String telaVisualizarSolicitacao() {
		return forward(PAGINA_VISUALIZA_DADOS_SOLICITACAO);
	}
	
	@Override
	protected String telaVisualizarDadosSolicitacaoAtendimento() {		
		return forward(PAGINA_VISUALIZA_DADOS_SOLICITACAO_ATENDIMENTO);
	}
	
	/**
	 * Exibe a tela com o comprovante da solicitação.
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacao.jsp
	 */
	@Override
	public String telaVisualizaComprovante() {
		return forward(PAGINA_VISUALIZA_COMPROVANTE_SOLICITACAO);
	}
		
	///////////////////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////// MÉTODOS DE COLEÇÕES /////////////////////////////////////
	
	/**
	 * Método que inicializa a lista temporária de notas gerais
	 */
	private void iniciaColecaoNotasGerais(){
		if (notasGerais == null) {
			notasGerais = obj.getFichaGerada().getNotasGerais();
		}
		
		notasGeraisDataModel = new ListDataModel(notasGerais);
	}

	/**
	 * Método que inicializa a lista temporária de notas de teses
	 */
	private void iniciaColecaoNotasTeses(){
		if (notasTeses == null) {
			notasTeses = obj.getFichaGerada().getNotasTeses();
		}
		
		notasTesesDataModel = new ListDataModel(notasTeses);
	}

	/**
	 * Método que inicializa a lista temporária de notas bibliográficas
	 */
	private void iniciaColecaoNotasBibliograficas(){
		if (notasBibliograficas == null) {
			notasBibliograficas = obj.getFichaGerada().getNotasBibliograficas();
		}
		
		notasBibliograficasDataModel = new ListDataModel(notasBibliograficas);
	}

	/**
	 * Método que inicializa a lista temporária de notas de conteúdos
	 */
	private void iniciaColecaoNotasConteudos(){
		if (notasConteudos == null) {
			notasConteudos = obj.getFichaGerada().getNotasConteudo();
		}
		
		notasConteudosDataModel = new ListDataModel(notasConteudos);
	}

	/**
	 * Método que inicializa a lista temporária de assuntos pessoais
	 */
	private void iniciaColecaoAssuntosPessoais(){
		if (assuntosPessoais == null) {
			assuntosPessoais = obj.getFichaGerada().getAssuntosPessoais();
		}
		
		assuntosPessoaisDataModel = new ListDataModel(assuntosPessoais);
	}

	/**
	 * Método que inicializa a lista temporária de assuntos
	 */
	private void iniciaColecaoAssuntos(){
		if (assuntos == null) {
			assuntos = obj.getFichaGerada().getAssuntos();
		}
		
		assuntosDataModel = new ListDataModel(assuntos);
	}

	/**
	 * Método que inicializa a lista temporária de autores secundários
	 */
	private void iniciaColecaoAutoresSecundarios(){
		if (autoresSecundarios == null) {
			autoresSecundarios = obj.getFichaGerada().getAutoresSecundarios();
		}
		
		autoresSecundariosDataModel = new ListDataModel(autoresSecundarios);
	}

	/**
	 * AJAX - Adiciona uma nova nota geral.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void adicionarNotaGeral(ActionEvent evt) {
		notasGerais.add("");
	}

	/**
	 * AJAX - Adiciona uma nova nota de tese.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void adicionarNotaTese(ActionEvent evt) {
		notasTeses.add("");
	}

	/**
	 * AJAX - Adiciona uma nova nota bibliográfica.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void adicionarNotaBibliografica(ActionEvent evt) {
		notasBibliograficas.add("");
	}

	/**
	 * AJAX - Adiciona uma nova nota de conteúdo.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void adicionarNotaConteudo(ActionEvent evt) {
		notasConteudos.add("");
	}

	/**
	 * AJAX - Adiciona um novo assunto pessoal.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void adicionarAssuntoPessoal(ActionEvent evt) {
		assuntosPessoais.add("");
	}

	/**
	 * AJAX - Adiciona um novo assunto.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void adicionarAssunto(ActionEvent evt) {
		assuntos.add("");
	}
	
	/**
	 * AJAX - Adiciona um novo autor secundário.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void adicionarAutorSecundario(ActionEvent evt) {
		autoresSecundarios.add("");
	}
	
	/**
	 * AJAX - Remove uma nota geral.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void removerNotaGeral(ActionEvent evt) {
		notasGerais.remove(notasGeraisDataModel.getRowIndex());
	}
	
	/**
	 * AJAX - Remove uma nota de tese.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void removerNotaTese(ActionEvent evt) {
		notasTeses.remove(notasTesesDataModel.getRowIndex());
	}
	
	/**
	 * AJAX - Remove uma nota bibliográfica.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void removerNotaBibliografica(ActionEvent evt) {
		notasBibliograficas.remove(notasBibliograficasDataModel.getRowIndex());
	}

	/**
	 * AJAX - Remove um nota de conteúdo.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void removerNotaConteudo(ActionEvent evt) {
		notasConteudos.remove(notasConteudosDataModel.getRowIndex());
	}
	
	/**
	 * AJAX - Remove um assunto pessoal.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void removerAssuntoPessoal(ActionEvent evt) {
		assuntosPessoais.remove(assuntosPessoaisDataModel.getRowIndex());
	}
	
	/**
	 * AJAX - Remove um assunto.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void removerAssunto(ActionEvent evt) {
		assuntos.remove(assuntosDataModel.getRowIndex());
	}
	
	/**
	 * AJAX - Remove um autor secundário.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 */
	public void removerAutorSecundario(ActionEvent evt) {
		autoresSecundarios.remove(autoresSecundariosDataModel.getRowIndex());
	}
	//////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////// parte do arquivo em formato digital ///////////////////////////////
	
	
	/**
	 * Valida o arquivo que o bibliotecário informou no atendimento.
	 *
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li></ul>
	 * 
	 * @throws ArqException 
	 */
	public void validaFormatoArquivo() throws NegocioException {
		if( arquivo != null ) {
			if(  !arquivo.getName().endsWith(".pdf")&& !arquivo.getName().endsWith(".doc") 
				&& !arquivo.getName().endsWith(".docx")&& !arquivo.getName().endsWith(".odt")){
				arquivo = null;
				throw new NegocioException("O arquivo digitalizado precisa ser do formato pdf,doc,docx ou odt.");
			}
			
			if(arquivo != null && arquivo.getName().length() > 100){
				arquivo = null;
				throw new NegocioException("O tamanho máximo permito para o nome de arquivo é de 100 caracteres.");
			}
			
		}
	}
	
	/**
	 *  <p>Informa se dos dados de Email, e telefone OU celular existem para o usuário logado</p>
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoCatalogacao.jsp</li></ul>
	 * @throws DAOException 
	 */
	public boolean isDadosComunicacaoPessoaDefinidos() throws DAOException {
		PessoaDao daop = null;
		
		try {	
		
			daop = getDAO(PessoaDao.class);
			
			// PS.: É a pessoa do usuário logado, normalmente não era para ser nula, mas pode ocorrer //
			if (obj.getPessoa() == null) return false;
			
			obj.setPessoa(daop.findByPrimaryKey(obj.getPessoa().getId(), Pessoa.class, "id", "nome", "email", "telefone", "celular"));
		
			if ( StringUtils.notEmpty(obj.getPessoa().getEmail()) && 
					( StringUtils.notEmpty( obj.getPessoa().getTelefone()) || StringUtils.notEmpty( obj.getPessoa().getCelular())  ) // ou telefone ou celular precisam estar definidos
						) // fim if
				{
					return true;
				}
			else
				return false;
				
		}finally {
			if (daop != null) daop.close();
			
		}
			
	}
	
	//////    sets e gets   ////////

	/*/**
	 * Retorna o parâmetro que indica a quantidade de dias que o material pode permanecer na biblioteca após o atendimento 
	 * antes de ser descartado.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoCatalogacao.jsp</li></ul>
	 *
	public int getQuantidadeDiasDiscarteMaterial(){
		 return ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.TEXTO_TITULO_FICHA_CATALOGRAFICA);
	}*/
	
	
	/**
	 * Retorna o parâmetro que indica a quantidade de dias que o material pode permanecer na biblioteca após o atendimento 
	 * antes de ser descartado.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoCatalogacao.jsp</li></ul>
	 */
	public String getTituloFicha(){
		 return ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.TEXTO_TITULO_FICHA_CATALOGRAFICA);
	}
	
	public Collection<Biblioteca> getBibliotecas() {
		return bibliotecas;
	}

	public void setBibliotecas(Collection<Biblioteca> bibliotecas) {
		this.bibliotecas = bibliotecas;
	}

	public Collection<SelectItem> getBibliotecasCombo(){
		return toSelectItems(bibliotecas, "id", "descricao");
	}

	public List<String> getNotasGerais() {
		return notasGerais;
	}

	public List<String> getNotasTeses() {
		return notasTeses;
	}

	public List<String> getNotasBibliograficas() {
		return notasBibliograficas;
	}

	public List<String> getNotasConteudos() {
		return notasConteudos;
	}

	public List<String> getAssuntosPessoais() {
		return assuntosPessoais;
	}

	public List<String> getAssuntos() {
		return assuntos;
	}

	public List<String> getAutoresSecundarios() {
		return autoresSecundarios;
	}

	public DataModel getNotasGeraisDataModel() {
		return notasGeraisDataModel;
	}

	public DataModel getNotasTesesDataModel() {
		return notasTesesDataModel;
	}

	public DataModel getNotasBibliograficasDataModel() {
		return notasBibliograficasDataModel;
	}

	public DataModel getNotasConteudosDataModel() {
		return notasConteudosDataModel;
	}

	public DataModel getAssuntosPessoaisDataModel() {
		return assuntosPessoaisDataModel;
	}

	public DataModel getAssuntosDataModel() {
		return assuntosDataModel;
	}

	public DataModel getAutoresSecundariosDataModel() {
		return autoresSecundariosDataModel;
	}
	
	public TipoFicha getTipoFicha() {
		return tipoFicha;
	}
	
	public void setTipoFicha(TipoFicha tipoFicha) {
		this.tipoFicha = tipoFicha;
	}
	
	public TipoFicha getTipoFichaManual() {
		return TipoFicha.MANUAL;
	}
	
	public TipoFicha getTipoFichaArquivo() {
		return TipoFicha.ARQUIVO;
	}

	public String getSiglaInstituicao() {
		return RepositorioDadosInstitucionais.get("siglaInstituicao");
	}

	@Override
	protected Comando getMovimentoCadastrar() {
		return SigaaListaComando.CADASTRAR_SOLICITACAO_CATALOGACAO;
	}

	@Override
	protected Comando getMovimentoAlterar() {
		return SigaaListaComando.ALTERAR_SOLICITACAO_CATALOGACAO;
	}

	/*@Override
	protected Comando getMovimentoValidar() {
		return SigaaListaComando.VALIDAR_SOLICITACAO_CATALOGACAO;
	}*/

	@Override
	protected Comando getMovimentoAtender() {
		return SigaaListaComando.ATENDER_SOLICITACAO_CATALOGACAO;
	}

	@Override
	protected Comando getMovimentoCancelar() {
		return SigaaListaComando.CANCELAR_SOLICITACAO_CATALOGACAO;
	}

	@Override
	public String getPropriedadeServico() {
		return "realizaCatalogacaoNaFonte";
	}

	@Override
	protected void preencherDados() {
		obj.setPalavrasChave( new ArrayList<String>() );
		
		String[] palavras = obj.getPalavrasChaveString().split(",");
		
		for( String s : palavras ){
			if( StringUtils.notEmpty(s))
				obj.getPalavrasChave().add(s.trim());
		}
	}

	@Override
	protected void inicializarDados() {
		obj.setTipoDocumento(new TipoDocumentoNormalizacaoCatalogacao());		
	}

	@Override
	protected void sincronizarDados(GenericDAO dao) throws DAOException {
		obj.setTipoDocumento(dao.refresh(obj.getTipoDocumento()));
	}

	@Override
	public TipoServicoInformacaoReferencia getTipoServico() {
		return TipoServicoInformacaoReferencia.CATALOGACAO_NA_FONTE;
	}

	@Override
	protected MovimentoSolicitacaoDocumento instanciarMovimento() {
		return new MovimentoSolicitacaoCatalogacao();
	}
	
	@Override
	protected void confirmarAtender(MovimentoCadastro mov) throws NegocioException {
		if (tipoFicha == TipoFicha.ARQUIVO) {
			validaFormatoArquivo();
			
			if (arquivo != null) {
				((MovimentoSolicitacaoCatalogacao)mov).setIdFichaDigitalizadaAnterior(obj.getIdFichaDigitalizada());
			}
			
			if (obj.getFichaGerada() != null && obj.getFichaGerada().getId() != 0) {
				((MovimentoSolicitacaoCatalogacao)mov).setFichaCatalograficaAnterior(obj.getFichaGerada());
			}
			
			((MovimentoSolicitacaoCatalogacao)mov).setArquivoFichaDigitalizada(arquivo);
			obj.setFichaGerada(null);
		} else {
			if (obj.getIdFichaDigitalizada() != null) {
				((MovimentoSolicitacaoCatalogacao)mov).setIdFichaDigitalizadaAnterior(obj.getIdFichaDigitalizada());
			}
		}
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}


	public String getMotivoReenvio() {
		return motivoReenvio;
	}


	public void setMotivoReenvio(String motivoReenvio) {
		this.motivoReenvio = motivoReenvio;
	}
	
}