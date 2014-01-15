/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/10/2011
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.TransferirMateriaisEntreSetoresDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoTransferirMateriaisEntreSetores;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * MBean que gerencia a alteração do motivo de baixa de um ou vários materiais
 * baixados.
 * 
 * @author felipe
 * @since 12/12/2011
 * @version 1.0 Criação da classe
 * 
 */
@Component("transferirMateriaisEntreSetoresBibliotecaMBean")
@Scope("request")
public class TransferirMateriaisEntreSetoresBibliotecaMBean extends SigaaAbstractController<MaterialInformacional> {

	/** Página para onde o fluxo retorna ao final da operação */
	public static final String PAGINA_VOLTAR = "/biblioteca/index.jsp";

	/** Página de busca e seleção dos materiais */
	public static final String PAGINA_BUSCA = "/biblioteca/processos_tecnicos/outras_operacoes/paginaPesquisaTransfereMateriaisEntreSetores.jsp";

	/** Página para exibir o relatório de materiais selecionados */
	public static final String PAGINA_IMPRESSAO = "/biblioteca/processos_tecnicos/outras_operacoes/paginaImpressaoTransfereMateriaisEntreSetores.jsp";

	/**
	 * Materias selecionados para alteração da situação
	 */
	private List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();

	/**
	 * Materias selecionados para impressão
	 */
	private List<SequenciaMateriais> materiaisImpressao = new ArrayList<SequenciaMateriais>();

	/**
	 * O id da situação selecionada pelo usuário.
	 */
	private int situacao;

	/**
	 * A situação selecionada pelo usuário.
	 */
	private SituacaoMaterialInformacional situacaoSelecionada;

	/**
	 * Combo com as situações possíveis para escolha do usuário.
	 */
	private Collection<SituacaoMaterialInformacional> situacoesComboBox;

	/**
	 * Flag que indica se a alteração na situação dos materiais foi realizada
	 * com sucesso.
	 */
	private boolean realizouAlteracaoMateriais;

	/**
	 * Guarda temporariamente o arquivo PDF gerado, só após voltar para a página
	 * do usuário, uma ação vai ser chamada via java script para, como o click
	 * de um link, realizar a ação de visualizar o arquivo.
	 */
	private ByteArrayOutputStream outputStreamTempArquivo;

	//////////////// A busca que o usuário pode realizar na página  ///////////////////

	/** Indica que a busca será individual. */
	public static final int BUSCA_INDIVIDUAL = 1;
	/** Indica que a busca será por faixa. */
	public static final int BUSCA_POR_FAIXA = 2;

	/** Utilizado na pesquisa por código de barras. */
	private String codigoBarras;
	/**
	 * Guarda o código de barras inicial, caso o usuário busque os materiais na
	 * página por intervalo de código de barras
	 */
	private String codigoBarrasInicial;
	/**
	 * Guarda o código de barras final, caso o usuário busque os materiais na
	 * página por intervalo de código de barras
	 */
	private String codigoBarrasFinal;
	
	/**
	 * Guarda se o usuário desejar exibir a mostra resumida dos materias quando eles encontram-se em sequência
	 */
	private boolean mostraResumido = false;

	/**
	 * Se vai ser uma busca por faixa de códigos ou individual, setado no
	 * comboBox da página.
	 */
	private int tipoBusca = BUSCA_INDIVIDUAL;

	/** Guarda a quantidade de materiais transferidos */
	private int qtdMateriaisTransferidos = 0;
	
	
	public TransferirMateriaisEntreSetoresBibliotecaMBean() {

	}

	/**
	 * 
	 * Método chamado para iniciar a pesquisa dos materiais para os quais se
	 * deseja alterar o motivo da baixa. <br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/biblioteca/menus/processos_tecnicos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarBuscaMaterial() throws ArqException {
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS,
				SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,
				SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);

		return telaBusca();
	}

	/**
	 * 
	 * Redireciona o fluxo de navegação para a página de busca do caso-de-uso.
	 * 
	 * Chamado a partir da jsp:
	 * <ul>
	 * <li>/sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/
	 * paginaImpressaoTransfereMateriaisEntreSetores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String telaBusca() {
		return forward(PAGINA_BUSCA);
	}

	/**
	 * Redireciona o fluxo de navegação para a página de busca do caso-de-uso.
	 * 
	 * Não é utilizado em nenhuma JSP.
	 * 
	 * @return
	 */
	public String telaImpressao() {
		return forward(PAGINA_IMPRESSAO);
	}

	/**
	 * Redireciona o fluxo de navegação para a página a partir da qual o
	 * caso-de-uso é iniciado.
	 * 
	 * Não é utilizado em nenhuma JSP.
	 * 
	 * @return
	 */
	public String telaVoltar() {
		return forward(PAGINA_VOLTAR);
	}

	/**
	 * Faz a busca do material de acordo com o código de barras digitado.
	 * 
	 * Chamado a partir da JSP:
	 * /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes
	 * /paginaPesquisaTransfereMateriaisEntreSetores.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public void pesquisarMaterial(ActionEvent evt) throws ArqException {

		prepareMovimento(SigaaListaComando.TRANSFERIR_MATERIAIS_ENTRE_SETORES); // ao pesquisar um material, prepara a próxima transferência

		TransferirMateriaisEntreSetoresDAO dao = null;
		MaterialInformacionalDao daoMaterial = null;
		
		try {
			dao = getDAO(TransferirMateriaisEntreSetoresDAO.class);
			daoMaterial = getDAO(MaterialInformacionalDao.class);
			
			if (tipoBusca == BUSCA_POR_FAIXA) {
				
				if (StringUtils.isNotEmpty(codigoBarrasInicial) && StringUtils.isEmpty(codigoBarrasFinal)) {

					addMensagemErroAjax("Informe o Código de Barras Final do Material.");

				} else if (StringUtils.isEmpty(codigoBarrasInicial) && StringUtils.isNotEmpty(codigoBarrasFinal)) {

					addMensagemErroAjax("Informe o Código de Barras Inicial do Material.");

				} else if (StringUtils.isEmpty(codigoBarrasInicial) && StringUtils.isEmpty(codigoBarrasFinal)) {
					addMensagemErroAjax("Informe o Código de Barras Inicial e o Código de Barras Final do Material.");
				} else {
						if (codigoBarrasFinal.compareTo(codigoBarrasInicial) < 0) {

							addMensagemErroAjax("O Código de Barras Final deve ser maior que o Código de Barras Inicial.");

						} else {

							Integer quantidadeMateriais = dao.countMateriaisAtivosByFaixaCodigosBarras(codigoBarrasInicial, codigoBarrasFinal);

							if (quantidadeMateriais > 100) {
								addMensagemErroAjax("Só podem ser adicionados 100 Códigos de Barras por vez");
							} else {

								List<MaterialInformacional> ms = dao.findMateriaisAtivosByFaixaCodigosBarras(codigoBarrasInicial, codigoBarrasFinal);

								if (ms != null) {
									
									for (MaterialInformacional material : ms) {
										if (!material.getSituacao().isSituacaoEmprestado()) {
											if (!material.getSituacao().isSituacaoDeBaixa()) {
												
												TituloCatalografico titulo = new TituloCatalografico( daoMaterial.findIdTituloMaterial(material.getId())  );
												material.setInformacao(new FormatosBibliograficosUtil().gerarFormatoReferencia(titulo, false));
											
												if (!materiais.contains(material)) {
													materiais.add(material);
													ordenaMateriaisByCodigoBarras();
													
												} else {
													addMensagemErroAjax("Material já se encontra na lista.");
												}
											} else {
												addMensagemErroAjax("Material se encontra baixado.");
											}
										}  else {
											addMensagemErroAjax("Material se encontra emprestado.");
										}
									}
								}

								if (ms.size() == 0) {
									addMensagemErroAjax("Materiais com o código de barras entre: "+ codigoBarrasInicial+ " e "+ codigoBarrasFinal+ " não encontrados.");
								}
								
								codigoBarrasInicial = null;
								codigoBarrasFinal = null;
								codigoBarras = null;

								
							}
						}

					}

				} else {

					if (!StringUtils.isEmpty(codigoBarras)) {

						MaterialInformacional material = dao.getMaterialByCodigoBarras(codigoBarras);
	
						if (material == null)
							addMensagemErroAjax("Material com o código de barras "+ codigoBarras + " não encontrado.");
						else {
	
							if (!materiais.contains(material)) {
								if (!material.getSituacao().isSituacaoEmprestado()) {
									if (!material.getSituacao().isSituacaoDeBaixa()) {
										
										TituloCatalografico titulo = new TituloCatalografico( daoMaterial.findIdTituloMaterial(material.getId())  );
										material.setInformacao(new FormatosBibliograficosUtil().gerarFormatoReferencia(titulo, false));
									
										materiais.add(material);
										ordenaMateriaisByCodigoBarras();
										
									} else {
										addMensagemErroAjax("Material com o código de barras "+ codigoBarras+ " se encontra baixado.");
									}
								} else {
									addMensagemErroAjax("Material com o código de barras "+ codigoBarras+ " se encontra emprestado.");
								}
							} else {
								addMensagemErroAjax("Material com o código de barras "+ codigoBarras+ " já se encontra na lista.");
							}
	
						}
					} else {
						addMensagemErroAjax("Informe o Código de Barras do Material.");
					}
				}
		} finally {
			if (dao != null)
				dao.close();
			if (daoMaterial != null)
				daoMaterial.close();
		}
	}

	/**
	 * Ordena os materiais da listagem do usuário pelo código de barras.
	 * 
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/</li>
	 * </ul>
	 *
	 */
	private void ordenaMateriaisByCodigoBarras() {
		Collections.sort(materiais,  new Comparator<MaterialInformacional>() {
			@Override
			public int compare(MaterialInformacional o1, MaterialInformacional o2) {
				return o1.getCodigoBarras().compareTo(o2.getCodigoBarras());
			}
		});
	}

	
	/**
	 * 
	 * Remove algum material que tenha sido adicionado a lista
	 * 
	 * Chamado a partir da JSP:
	 * /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes
	 * /paginaPesquisaTransfereMateriaisEntreSetores.jsp
	 * 
	 * @return
	 */
	public String removerMaterialSelecionadoDaLista() {

		int id = getParameterInt("idMaterialRemocao", 0);

		for (Iterator<MaterialInformacional> iterator = materiais.iterator(); iterator.hasNext();) {
			MaterialInformacional m = iterator.next();

			if (m.getId() == id) {
				iterator.remove();
			}
		}

		return telaBusca();
	}

	/**
	 * Remover todos os elementos atualmente na lista de materiais.
	 * 
	 * Chamado a partir da JSP:
	 * /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes
	 * /paginaPesquisaTransfereMateriaisEntreSetores.jsp
	 * 
	 * @return
	 */
	public String removerTodosMateriaisDaLista() {
		if (materiais != null)
			materiais.clear();

		codigoBarras = null;

		return telaBusca();
	}

	/**
	 * Realiza a ação de alterar as situações dos materiais que foram
	 * selecionados pelo usuário.
	 * 
	 * Chamado a partir da JSP:
	 * /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes
	 * /paginaPesquisaTransfereMateriaisEntreSetores.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	public String realizarAlteracaoMateriais() throws ArqException {

		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS,
				SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,
				SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);

		GenericDAO dao = null;

		try {

			dao = getGenericDAO();

			try {

				validarCampos();

				situacaoSelecionada = dao.findByPrimaryKey(situacao, SituacaoMaterialInformacional.class, new String[] {"id", "descricao" });

				if (materiais != null && materiais.size() > 0) {

					// Guarda a lista anterior para imprimir a situação atual no
					// relatório //
					materiaisImpressao = organizaMateriais(materiais, mostraResumido);
					
					
					//materiaisImpressao.addAll(materiais);

					MovimentoTransferirMateriaisEntreSetores mov = new MovimentoTransferirMateriaisEntreSetores(materiais, situacaoSelecionada);

					mov.setCodMovimento(SigaaListaComando.TRANSFERIR_MATERIAIS_ENTRE_SETORES);

					execute(mov);

					qtdMateriaisTransferidos = materiais.size();
					
					//addMensagemInformation("Alteração realizada com sucesso.");

					realizouAlteracaoMateriais = true;

					limparCampos();

					outputStreamTempArquivo = new ByteArrayOutputStream();

					geraArquivoPDFRelatorioTransferencia(outputStreamTempArquivo, materiaisImpressao);

					outputStreamTempArquivo.flush();
					outputStreamTempArquivo.close();

					return telaImpressao();

				} else {
					addMensagemErro("Nenhum material adicionado à lista.");
					return null;
				}

			} catch (NegocioException ne) {
				addMensagens(ne.getListaMensagens());
				ne.printStackTrace();
			} catch (IOException e) {
				addMensagemErro("Não foi possível gerar o relatório de transferência");
			} catch (DocumentException e) {
				addMensagemErro("Não foi possível gerar o relatório de transferência");
			}

			return null;

		} finally {
			if (dao != null)
				dao.close();
		}

	}

	/**
	 * <p>
	 * Gera o Arquivo em PDF para ser enviado em anexo no email enviado para o
	 * usuário.
	 * </p>
	 * 
	 * <p>
	 * Com toda a formatação para ficar igual ao informativo visualizado pelo
	 * sistema
	 * </p>
	 * 
	 * @param byteArray
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private Object geraArquivoPDFRelatorioTransferencia(ByteArrayOutputStream output, List<SequenciaMateriais> materiaisImpressao) throws DocumentException, MalformedURLException, IOException {

		// margin top, margin bottom, margin left, margin rigth
		Document doc = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);
		PdfWriter.getInstance(doc, output);
		doc.open();

		// A cor de background do cabeçado da tabela //
		Color corCabecalhoTabela = new Color(222, 223, 227); // #DEDFE3

		Font fonteNegrito = new Font();
		fonteNegrito.setStyle(Font.BOLD);
		fonteNegrito.setSize(10);
		
		Font fonteNormal = new Font();
		fonteNormal.setStyle(Font.NORMAL);
		fonteNormal.setSize(8);
		
		Font fonteNormalGrande = new Font();
		fonteNormalGrande.setStyle(Font.NORMAL);
		fonteNormalGrande.setSize(10);
		
		/* ***********************************************************************************
		 * Gera o cabeçalho do relatório com as imagens do sistema, etc...
		 * ***********************************************************************************
		 */

		PdfPTable tabelaTituloRelatorio = new PdfPTable(new float[] { 0.2f,
				0.6f, 0.2f });
		tabelaTituloRelatorio.setWidthPercentage(100); // o tamanho da tela

		com.lowagie.text.Image imageInstituicao = com.lowagie.text.Image.getInstance(RepositorioDadosInstitucionais.get("logoInstituicao"));
		
		PdfPCell logoInstituicao = new PdfPCell(imageInstituicao);
		logoInstituicao.setHorizontalAlignment(Element.ALIGN_CENTER);
		logoInstituicao.setVerticalAlignment(Element.ALIGN_CENTER);
		logoInstituicao.setBorderWidthRight(0);
		tabelaTituloRelatorio.addCell(logoInstituicao);

		Paragraph nomeInstituicao = new Paragraph(RepositorioDadosInstitucionais.get("nomeInstituicao"), fonteNegrito);
		nomeInstituicao.setAlignment(Element.ALIGN_CENTER);
		Paragraph nomeSistema = new Paragraph(RepositorioDadosInstitucionais.get("nomeSigaa"), fonteNegrito);
		nomeSistema.setAlignment(Element.ALIGN_CENTER);
		Paragraph dataEmissao = new Paragraph("Emitido em "+ new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
		dataEmissao.setAlignment(Element.ALIGN_CENTER);

		PdfPCell central = new PdfPCell();
		central.setHorizontalAlignment(Element.ALIGN_CENTER);
		central.addElement(nomeInstituicao);
		central.addElement(nomeSistema);
		central.addElement(dataEmissao);
		central.setBorderWidthRight(0);
		central.setBorderWidthLeft(0);
		tabelaTituloRelatorio.addCell(central);

		com.lowagie.text.Image imageInformatica = com.lowagie.text.Image.getInstance(RepositorioDadosInstitucionais.get("logoInformatica"));
		PdfPCell logoInformatica = new PdfPCell(imageInformatica);
		logoInformatica.setHorizontalAlignment(Element.ALIGN_CENTER);
		logoInformatica.setVerticalAlignment(Element.ALIGN_CENTER);
		logoInformatica.setBorderWidthLeft(0);
		tabelaTituloRelatorio.addCell(logoInformatica);

		doc.add(tabelaTituloRelatorio);

		/* ***********************************************************************************
		 * Gera o cabeçalho da tabela
		 * *************************************************************************************
		 */

		PdfPTable tabelaDadosRelatorio = new PdfPTable(new float[] { 0.15f, 0.55f,0.15f, 0.15f }); // Cria uma tebla de 3 Colunas de largura diferente
		tabelaDadosRelatorio.setWidthPercentage(100); // o tamanho da tela
		tabelaDadosRelatorio.setSpacingBefore(20);

		PdfPCell titulo = new PdfPCell(new Paragraph("Materiais Transferidos ( " + qtdMateriaisTransferidos + " )",fonteNegrito));
		titulo.setColspan(4);
		titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		titulo.setBorderWidthTop(2);
		titulo.setBorderWidthBottom(0);
		titulo.setBorderWidthLeft(0);
		titulo.setBorderWidthRight(0);

		tabelaDadosRelatorio.addCell(titulo);
		
		PdfPCell header1 = new PdfPCell(new Paragraph("Código de Barras", fonteNegrito));
		header1.setHorizontalAlignment(Element.ALIGN_LEFT);
		header1.setBackgroundColor(corCabecalhoTabela);
		tabelaDadosRelatorio.addCell(header1);

		PdfPCell header2 = new PdfPCell(new Paragraph("Referência", fonteNegrito));
		header2.setHorizontalAlignment(Element.ALIGN_LEFT);
		header2.setBackgroundColor(corCabecalhoTabela);
		tabelaDadosRelatorio.addCell(header2);

		PdfPCell header3 = new PdfPCell(new Paragraph("Situação Anterior", fonteNegrito));
		header3.setHorizontalAlignment(Element.ALIGN_LEFT);
		header3.setBackgroundColor(corCabecalhoTabela);

		tabelaDadosRelatorio.addCell(header3);

		PdfPCell header4 = new PdfPCell(new Paragraph("Nova Situação",fonteNegrito));
		header4.setHorizontalAlignment(Element.ALIGN_LEFT);
		header4.setBackgroundColor(corCabecalhoTabela);

		tabelaDadosRelatorio.addCell(header4);

		/* ***********************************************************************************
		 * Preenche a tabela com o formato de referência dos títulos
		 * **********************************************************************************
		 */

		for (SequenciaMateriais seq : materiaisImpressao) {
			
			PdfPCell celula1 = new PdfPCell();
			PdfPCell celula2 = new PdfPCell();
			PdfPCell celula3 = new PdfPCell();
			PdfPCell celula4 = new PdfPCell();
			
			if (seq.material != null) {
				
				Phrase phrase1 = new Phrase(seq.material.getCodigoBarras(), fonteNormal);
				celula1.addElement(phrase1);
				
				Phrase phrase2 = new Phrase(seq.material.getInformacao().replace("<strong>", "").replace("</strong>", ""), fonteNormal);
				celula2.addElement(phrase2);
	
				Phrase phrase3 = new Phrase(seq.material.getSituacao().getDescricao(), fonteNormal);
				celula3.addElement(phrase3);
	
				Phrase phrase4 = new Phrase(situacaoSelecionada.getDescricao(), fonteNormal);
				celula4.addElement(phrase4);
			
			} else {
				Phrase phrase1 = new Phrase(seq.primeiroCodigoBarras+" até "+seq.ultimoCodigoBarras, fonteNormal);
				celula1.addElement(phrase1);

				Phrase phrase2 = new Phrase("Vários títulos", fonteNormal);
				celula2.addElement(phrase2);

				Phrase phrase3 = new Phrase(seq.situacaoAtual, fonteNormal);
				celula3.addElement(phrase3);
				
				Phrase phrase4 = new Phrase(situacaoSelecionada.getDescricao(), fonteNormal);
				celula4.addElement(phrase4);
			}

			tabelaDadosRelatorio.addCell(celula1); // A descrição do materail
			tabelaDadosRelatorio.addCell(celula2); // A situção anterior
			tabelaDadosRelatorio.addCell(celula3); // A nova Situação para a
			tabelaDadosRelatorio.addCell(celula4); // qual ele foi alterado.

		}

		doc.add(tabelaDadosRelatorio);

		/* ***********************************************************************************
		 * Adiciona os dados do usuário que realizou a tranferência
		 * ***********************************************************************************/

		PdfPTable tableOperador = new PdfPTable(1);
		tableOperador.setWidthPercentage(100);
		tableOperador.setSpacingBefore(5);

		Phrase labelOperador = new Phrase("Transferência Realizada por: ",fonteNegrito);
		Phrase operador = new Phrase(" " + getUsuarioLogado().getNome(), fonteNormalGrande);

		Paragraph paragrafoAutenticacao = new Paragraph();
		paragrafoAutenticacao.add(labelOperador);
		paragrafoAutenticacao.add(operador);
		paragrafoAutenticacao.setAlignment(Element.ALIGN_CENTER);

		PdfPCell colunaAutenticacao = new PdfPCell();
		colunaAutenticacao.setBorderWidthTop(0);
		colunaAutenticacao.setBorderWidthBottom(0);
		colunaAutenticacao.setBorderWidthLeft(0);
		colunaAutenticacao.setBorderWidthRight(0);
		colunaAutenticacao.addElement(paragrafoAutenticacao);
		tableOperador.addCell(colunaAutenticacao);
		doc.add(tableOperador);

		/* ***********************************************************************************
		 * Adiciona as informaões do Rodapé do Documento
		 * **********************************************************************************
		 */
		PdfPTable tableRodape = new PdfPTable(1); // Cria uma tebla de 2 Colunas igual ao Relatório impresso na pagina.
		PdfPCell rodape = new PdfPCell(new Paragraph(RepositorioDadosInstitucionais.get("siglaSigaa") + " | "+ RepositorioDadosInstitucionais.get("rodapeSistema"), fonteNormal) );
		rodape.setBorderWidthTop(2);
		rodape.setHorizontalAlignment(Element.ALIGN_CENTER);
		rodape.setMinimumHeight(20f);
		tableRodape.addCell(rodape);
		tableRodape.setSpacingBefore(5);
		tableRodape.setWidthPercentage(100);

		doc.add(tableRodape);

		doc.close();

		return doc;
	}

	
	/**
	 * Limpa os campos para uma nova execução do caso-de-uso.
	 */
	private void limparCampos() {
		materiais.clear();
		situacao = -1;
	}

	/**
	 * Verifica se o campos foram preenchidos corretamente.
	 * 
	 * @throws NegocioException
	 */
	private void validarCampos() throws NegocioException {
		if (situacao == -1) {
			throw new NegocioException("Selecione uma situação.");
		}
	}

	/**
	 * Abre a página de impressão do relatório de materiais selecionados.
	 * 
	 * Utilizado na JSP:
	 * /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes
	 * /paginaPesquisaTransfereMateriaisEntreSetores.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws IOException
	 */
	public void visualizarRelatorio(ActionEvent evt) throws ArqException,
			IOException {

		if (outputStreamTempArquivo != null) {

			DataOutputStream dos = new DataOutputStream(getCurrentResponse().getOutputStream());
			dos.write(outputStreamTempArquivo.toByteArray());
			getCurrentResponse().setContentType("Content-Type: application/pdf;");
			getCurrentResponse().addHeader("Content-Disposition","attachment; filename=RelatorioTransferencia.pdf");
			FacesContext.getCurrentInstance().responseComplete();

			// outputStreamTempArquivo = null;
			realizouAlteracaoMateriais = false;
		}
	}

	/**
	 * Retorna os itens de combobox representando as situações permitidas no
	 * caso-de-uso.
	 * 
	 * Utilizado na JSP:
	 * /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes
	 * /paginaPesquisaTransfereMateriaisEntreSetores.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getSituacoesCombo() throws DAOException {

		if (situacoesComboBox == null) {
			SituacaoMaterialInformacionalDao dao = null;
			try {
				dao = getDAO(SituacaoMaterialInformacionalDao.class);
				situacoesComboBox = dao.findByExactField(SituacaoMaterialInformacional.class, new String[] {"situacaoDeBaixa", "situacaoEmprestado" },
						new Object[] { false, false });
			} finally {
				if (dao != null)
					dao.close();
			}
		}

		return toSelectItems(situacoesComboBox, "id", "descricao");

	}
	
	
	/**
	 * 
	 * Organiza materiais em uma lista de sequências para exibir as informações do materiais de forma adequada para impressão
	 * 
	 * Chamado a partir da JSP:
	 * /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes
	 * /paginaImpressaoTransfereMateriaisEntreSetores.jsp
	 * 
	 * @return
	 */
	private List<SequenciaMateriais> organizaMateriais (List<MaterialInformacional> materiais, boolean mostraResumido){
		List<SequenciaMateriais> lista = new ArrayList<SequenciaMateriais>();
		
		if ( !mostraResumido || materiais.size() == 1 ){
			for (MaterialInformacional material: materiais){
				SequenciaMateriais seq = new SequenciaMateriais();
				seq.material = material;
				lista.add(seq);
			}
		} else {
			
			SequenciaMateriais seq = new SequenciaMateriais();
			String tempCodigoBarras = null;
			seq.primeiroCodigoBarras = materiais.get(0).getCodigoBarras();
			seq.ultimoCodigoBarras = "000";
			seq.situacaoAtual = materiais.get(0).getSituacao().getDescricao();
		
			for (int i = 1; i < materiais.size(); i++) {
				
				if ( !isFaixaCodigoBarras(materiais, i) || (!isMesmaSituacao(materiais, i)) ) {
					
					if ( !seq.ultimoCodigoBarras.equals(materiais.get(i-1).getCodigoBarras()) ){
						seq.material = materiais.get(i-1);
					}
					lista.add(seq);
					if (i == materiais.size()-1){
						seq = new SequenciaMateriais();
						seq.material = materiais.get(i);
						lista.add(seq);
						return lista;
					}
					tempCodigoBarras = seq.ultimoCodigoBarras;
					seq = new SequenciaMateriais();
					seq.material = null;
					seq.primeiroCodigoBarras = materiais.get(i).getCodigoBarras();
					seq.situacaoAtual = materiais.get(i).getSituacao().getDescricao();
					seq.ultimoCodigoBarras = tempCodigoBarras;
					
				} else { // se tá em sequência e com o anterior
					
					// Esse material passa a ser o último a lista
					seq.ultimoCodigoBarras = materiais.get(i).getCodigoBarras();
					if (i == materiais.size()-1){
						lista.add(seq);
					}
				}
			}
		}
		return lista;
	}

	/**
	 * Retorna se dois códigos de barras estão em faixa o que implica serem 
	 * códigos unicamente numéricos e em sequência 
	 * 
	 * @return
	 */
	private boolean isFaixaCodigoBarras(List<MaterialInformacional> materiais, int i) {
		if (isCodigoBarrasNumerico(materiais,i-1) && isCodigoBarrasNumerico(materiais,i)){
			return Integer.parseInt(materiais.get(i).getCodigoBarras())-1 == Integer.parseInt(materiais.get(i-1).getCodigoBarras());
		}
		else {
			return false;
		}
	}
	
	/**
	 * Retorna se dois códigos de barras estão em mesma situação
	 * 
	 * @return
	 */
	private boolean isMesmaSituacao(List<MaterialInformacional> materiais, int i) {
		return materiais.get(i).getSituacao().getDescricao().equals(materiais.get(i-1).getSituacao().getDescricao());
	}
	
	/**
	 * Retorna se um código de barras é completamente numérico
	 * 
	 * @return
	 */
	private boolean isCodigoBarrasNumerico(List<MaterialInformacional> materiais, int i) {
		return materiais.get(i).getCodigoBarras().matches("[0-9]+");
	}
	
	
	
	
	/// sets e gets ///

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public List<MaterialInformacional> getMateriais() {
		return materiais;
	}

	public void setMateriais(List<MaterialInformacional> materiais) {
		this.materiais = materiais;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}

	public SituacaoMaterialInformacional getSituacaoSelecionada() {
		return situacaoSelecionada;
	}

	public void setSituacaoSelecionada(
			SituacaoMaterialInformacional situacaoSelecionada) {
		this.situacaoSelecionada = situacaoSelecionada;
	}

	public boolean isRealizouAlteracaoMateriais() {
		return realizouAlteracaoMateriais;
	}

	public List<SequenciaMateriais> getMateriaisImpressao() {
		return materiaisImpressao;
	}

	public ByteArrayOutputStream getOutputStreamTempArquivo() {
		return outputStreamTempArquivo;
	}

	public void setOutputStreamTempArquivo(
			ByteArrayOutputStream outputStreamTempArquivo) {
		this.outputStreamTempArquivo = outputStreamTempArquivo;
	}

	public boolean isMostraResumido() {
		return mostraResumido;
	}

	public void setMostraResumido(boolean mostrarResumido) {
		this.mostraResumido = mostrarResumido;
	}
	
	public int getTipoBusca() {
		return tipoBusca;
	}

	public String getCodigoBarrasInicial() {
		return codigoBarrasInicial;
	}

	public void setCodigoBarrasInicial(String codigoBarrasInicial) {
		this.codigoBarrasInicial = codigoBarrasInicial;
	}

	public String getCodigoBarrasFinal() {
		return codigoBarrasFinal;
	}

	public void setCodigoBarrasFinal(String codigoBarrasFinal) {
		this.codigoBarrasFinal = codigoBarrasFinal;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public int getQtdMateriaisTransferidos() {
		return qtdMateriaisTransferidos;
	}

	public void setQtdMateriaisTransferidos(int qtdMateriaisTransferidos) {
		this.qtdMateriaisTransferidos = qtdMateriaisTransferidos;
	}
	
	

}
