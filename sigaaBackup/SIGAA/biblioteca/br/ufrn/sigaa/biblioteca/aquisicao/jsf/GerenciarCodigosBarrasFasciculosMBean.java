/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 14/05/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.aquisicao.jsf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.aquisicao.negocio.MovimentoReorganizaCodigoBarrasFasciculos;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

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
* <p> MBean que gerencia os códigos de barras dos fascículos de uma assinatura do acervo. </p>
* 
* <p> Os códigos de barras dos fascículos seguem o padrão: "código de assinatura" + "-" + "número sequencial" quando são criados no sistema, contudo
* se eles são transferidos entre assinaturas, vão para asssinatura nova com o código de barras da assinatura antiga. 
* Esse caso de uso</p>
* 
* @author jadson
*/
@Component("gerenciarCodigosBarrasFasciculosMBean")
@Scope("request")
public class GerenciarCodigosBarrasFasciculosMBean extends SigaaAbstractController<Assinatura> implements PesquisarAcervoAssinaturas{

	
	/** Página que lista os códigos de barras de todos os fascículos ativos da assinatura. 
	 * Na ordem em que foram criados no sistema. 
	 * A partir dessa página o usuário pode executar a operação na qual os códigos de barras serão re organizados, como se tivesse sido criados todos agora.*/
	public static final String PAGINA_LISTA_CODIGOS_BARRAS_FASCICULOS = "/biblioteca/aquisicao/listaCodigosBarrasFasciculos.jsp";
	
	
	/** A assinatura selecionada na tela da busca padrão, vai ser a assinatura cujos códigos de barras dos fascículos vão ser gerenciados. */
	private Assinatura assinaturaSelecionada;

	
	/** Guarda os fascículos ativos da assinatura selecionada. São os fascículos cujos códigos de barras pode ser reorganizados pelo usuário. */
	private List<Fasciculo> fasciculosAssinatura ;
	
	/** O número sequencial que gera as código de barra do fascículo. Essa informação está presente em cada assinatura. */
	private int numeroSequencialFasciculos = 1;
	
	
	/** Guarda os fascículos ativos que possuem o código de barras formado pelo código da assinatura seleciona, mas estão localizados em outras assinaturas.*/
	private List<Fasciculo> fasciculosEmOutrasAssinaturas;
	
	/** Guarda o relatório em PDF impresso no final da mudança de forma temporária */
	private ByteArrayOutputStream outputSteam;
	
	/**
	 * <p>Diz se os códigos dos fascículos vão poder ser reorganizados ou não.</p>  
	 * <br/>
	 * <p>Geralmente não pode quando na assinatura não estão presentes o fascículos principal e o seu suplemento. Porque fica difícil calcular a sequencia dos os novos códigos de barras.</p>
	 */
	private boolean podeReorganizarCodigos = false;
	
	/**
	 * Inicia o caso de uso para alterar o código de barras dos fascículos da assinatura, selecionando primeiramente as asssinaturas.
	 * <br><br>
	 * Chamado a partir da página:<ul><li> /sigaa.war/biblioteca/menus/aquisicoes.jsp </li></ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciaReOrganizarCodigosBarras() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		return bean.iniciarPesquisaSelecionarAssinatura(this);
		
	}
	
	
	
	//////// Métodos da interface de busca padrão do sistema ///////
	
	@Override
	public void setAssinatura(Assinatura assinatura) throws ArqException {
		this.assinaturaSelecionada = assinatura;
	}

	@Override
	public String selecionaAssinatura() throws ArqException {
		
		prepareMovimento(SigaaListaComando.PROCESSADOR_REORGANIZA_CODIGO_BARRAS_FASCICULOS);
		
		FasciculoDao dao = null;
		
		try{
			dao = getDAO(FasciculoDao.class);
			
			podeReorganizarCodigos = true;
			
			assinaturaSelecionada = dao.refresh(new Assinatura( assinaturaSelecionada.getId()  ));
			
			fasciculosAssinatura = dao.findAllFasciculosCriadosDaAssinatura(assinaturaSelecionada.getId());
	
			fasciculosEmOutrasAssinaturas = dao.findAllFasciculosEmOutraAssinaturaComOCodigoAssinaturaPassada(assinaturaSelecionada.getCodigo(), assinaturaSelecionada.getId());
			
			reorganizaCodigoBarrasFasciculos();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}finally{
			if(dao != null) dao.close();
		}
		
		return telaListaCodigosBarrasFasciculos();
	}


	/**
	 * Método que contém a lógica que reorganiza os código de barras dos fascículos.
	 *
	 * @void
	 */
	private void reorganizaCodigoBarrasFasciculos() throws NegocioException {
		
		numeroSequencialFasciculos = 1;
		
		for (Fasciculo fasciculo : fasciculosAssinatura) { // Funciona se os fascículos estiverem ordenados pela data de criação, porque o fascículo principal sempre é criado antes dos anexos.
			
			fasciculo.setNumeroGeradorCodigoBarrasAnexos(0);
			
			
			if(fasciculo.isSuplemento()){
				
				Fasciculo principal = fasciculo.getFasciculoDeQuemSouSuplemento();
				
				if(fasciculosAssinatura.contains(principal)){
				
					int numeroGeradorAnexos = principal.getNumeroGeradorCodigoBarrasAnexos();
					
					fasciculo.setInformacao(assinaturaSelecionada.getCodigo());
					fasciculo.setInformacao2(principal.getInformacao2()+""+BibliotecaUtil.geraCaraterCorespondente( numeroGeradorAnexos ) );
					
					fasciculo.getFasciculoDeQuemSouSuplemento().setNumeroGeradorCodigoBarrasAnexos(++numeroGeradorAnexos);
				
				}else{
					addMensagemErro("Não é possível reorganizar o código de barras dos fascículos porque o fascículo principal do suplemento "
							+fasciculo.getCodigoBarras()+" não está presente na assinatura.");
					podeReorganizarCodigos = false;
				}
				
			}else{
				
				fasciculo.setInformacao(assinaturaSelecionada.getCodigo());
				fasciculo.setInformacao2(""+numeroSequencialFasciculos++);
			}
			
		}
		
		for (Fasciculo fasciculoOutraAssinatura : fasciculosEmOutrasAssinaturas) {
			
			if(fasciculoOutraAssinatura.isSuplemento()){
				if(fasciculosAssinatura.contains(fasciculoOutraAssinatura.getFasciculoDeQuemSouSuplemento())){
					addMensagemErro("Não é possível reorganizar o código de barras dos fascículos porque o suplemento do fascículo "
							+fasciculoOutraAssinatura.getFasciculoDeQuemSouSuplemento().getCodigoBarras()+" não está presente na assinatura.");
					podeReorganizarCodigos = false;
				}
			}
		}
		
		numeroSequencialFasciculos--; // volta 1 que andou a mais.
	}
	
	
	@Override
	public String voltarBuscaAssinatura() throws ArqException {
		return null;
	}

	@Override
	public boolean isUtilizaVoltarBuscaAssinatura() {
		return false;
	}
	
	/////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * <p>Metodo que realiza a ação desse caso de uso. Chamando o processador para alterar o código dos fascículos. </p>
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/listaCodigosBarrasFasciculos.jsp
	 * @throws ArqException 
	 */
	public String reorganizaCodigoFasciculos() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO);
		
		try{
			if(podeReorganizarCodigos){
				
				MovimentoReorganizaCodigoBarrasFasciculos movimento 
					= new MovimentoReorganizaCodigoBarrasFasciculos(fasciculosAssinatura, assinaturaSelecionada, numeroSequencialFasciculos, fasciculosEmOutrasAssinaturas);
				movimento.setCodMovimento(SigaaListaComando.PROCESSADOR_REORGANIZA_CODIGO_BARRAS_FASCICULOS);
				
				execute(movimento);
				
				geraRelatorioAlteracoesCodigoBarras();
				
				addMensagemInformation("Reorganização dos Códigos de Barra realizados com Sucesso!");
				
				System.gc();
			
			}
			
			return selecionaAssinatura();
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}finally{
			
		}
	}
	
	
	
	/**
	 * <p>Método responsável por gerar um relatório final em PDF sobre as alterações realizadas nos códigos de barras dos fascículos.</p>
	 *
	 * @void
	 */
	private void geraRelatorioAlteracoesCodigoBarras() {
		
		outputSteam = new ByteArrayOutputStream();
		
		Font fonteNegrito= new Font();
		fonteNegrito.setStyle(Font.BOLD);
		
		Color corCabecalhoTabela = new Color(222, 223, 227);   // #DEDFE3
		
		// margin top, margin bottom, margin left, margin rigth
		Document doc = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);
		
		try {
			
			PdfWriter.getInstance(doc, outputSteam);
			doc.open();
			
			/* ***********************************************************************************
			 *           Gera o cabeçalho do relatório com as imagens do sistema, etc...
			 * ***********************************************************************************/
			
			PdfPTable tabelaTituloRelatorio = new PdfPTable(new float[] { 0.2f, 0.6f, 0.2f }); 
			tabelaTituloRelatorio.setWidthPercentage(100); // o tamanho da tela
			
			com.lowagie.text.Image imageInstituicao = com.lowagie.text.Image.getInstance(RepositorioDadosInstitucionais.get("logoInstituicao"));
			PdfPCell logoInstituicao = new PdfPCell(imageInstituicao);
			logoInstituicao.setHorizontalAlignment(Element.ALIGN_CENTER);
			logoInstituicao.setVerticalAlignment(Element.ALIGN_CENTER);
			logoInstituicao.setBorderWidthRight(0);
			tabelaTituloRelatorio.addCell(logoInstituicao);
			
			Paragraph nomeInstituicao = new Paragraph(RepositorioDadosInstitucionais.get("nomeInstituicao"), fonteNegrito);
			nomeInstituicao.setAlignment(Element.ALIGN_CENTER);
			Paragraph nomeSistema =new Paragraph(RepositorioDadosInstitucionais.get("nomeSigaa"), fonteNegrito);
			nomeSistema.setAlignment(Element.ALIGN_CENTER);
			Paragraph dataEmissao = new Paragraph("Emitido em "+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
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
			 *           Gera a tabela com as informações da alterações dos códigos de barras
			 * ***********************************************************************************/
			
			
			/* ***********************************************************************************
			 *                         Gera o cabeçalho da tabela
			 * ***********************************************************************************/
			
			PdfPTable tabelaDadosRelatorio = new PdfPTable(new float[] { 0.5f, 0.5f }); // Cria uma tebla de 3 Colunas de largura diferente
			tabelaDadosRelatorio.setWidthPercentage(100); // o tamanho da tela
			tabelaDadosRelatorio.setSpacingBefore(20);
			
			PdfPCell titulo = new PdfPCell(new Paragraph("Fascículos cujos Códigos de Barra Foram Alterados ( "+fasciculosAssinatura.size()+" )", fonteNegrito));
			titulo.setColspan(3);
			titulo.setHorizontalAlignment(Element.ALIGN_CENTER);  
			titulo.setBorderWidthTop(2);
			titulo.setBorderWidthBottom(0);
			titulo.setBorderWidthLeft(0);
			titulo.setBorderWidthRight(0);
			
			tabelaDadosRelatorio.addCell(titulo);  
			
			
			
			PdfPCell header1 = new PdfPCell(new Paragraph("Códigos de Barras Anteriores", fonteNegrito));
			header1.setHorizontalAlignment(Element.ALIGN_LEFT); 
			header1.setBackgroundColor(corCabecalhoTabela);
			tabelaDadosRelatorio.addCell(header1);  
			
			
			PdfPCell header2 = new PdfPCell(new Paragraph("	Novos Códigos Barras", fonteNegrito));
			header2.setHorizontalAlignment(Element.ALIGN_LEFT);  
			header2.setBackgroundColor(corCabecalhoTabela);
			
			tabelaDadosRelatorio.addCell(header2); 
			
			
			
			/* ***********************************************************************************
			 *                         Gera o corpo da tabela
			 * ***********************************************************************************/
			
			
			for (Fasciculo fasciculo : fasciculosAssinatura) {
				
				Phrase phrase1 = new Phrase();
				phrase1.add(fasciculo.getCodigoBarras());
				
				PdfPCell celula1 = new PdfPCell();
				celula1.addElement(phrase1);
				
				Phrase phrase2 = new Phrase();
				phrase2.add(fasciculo.getInformacao()+"-"+fasciculo.getInformacao2());
				
				PdfPCell celula2 = new PdfPCell();
				celula2.addElement(phrase2);
				
				tabelaDadosRelatorio.addCell(celula1); // O código de barras antigo
				tabelaDadosRelatorio.addCell(celula2); // O código de barras novo
				
				
			}
			
			doc.add(tabelaDadosRelatorio);
			
			/* ***********************************************************************************
			 *                        Adiciona as informaões do Rodapé do Documento
			 * ***********************************************************************************/
			PdfPTable tableRodape = new PdfPTable(1); // Cria uma tebla de 2 Colunas igual ao Relatório impresso na pagina.
			PdfPCell rodape = new PdfPCell(new Paragraph(RepositorioDadosInstitucionais.get("siglaSigaa") +" | "+ RepositorioDadosInstitucionais.get("rodapeSistema")));
			rodape.setBorderWidthTop(2);
			rodape.setHorizontalAlignment(Element.ALIGN_CENTER); 
			rodape.setMinimumHeight(20f);
			tableRodape.addCell(rodape);
			tableRodape.setSpacingBefore(20);
			tableRodape.setWidthPercentage(100);
			
			doc.add(tableRodape);
			
			
			doc.close();
			
			outputSteam.flush();
			outputSteam.close();
			
			
			
		} catch (DocumentException e) {
			
		} catch (IOException e) {
			
		}
		
	}

	/**
	 * <p>Envia o PDF para a saída do usuário.</p>
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/listaCodigosBarrasFasciculos.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws IOException 
	 */
	public void visualizarRelatorioGerado(ActionEvent evt) {
		if(outputSteam != null){
			
			try {
				
				DataOutputStream dos = new DataOutputStream(getCurrentResponse().getOutputStream());
			
				dos.write(outputSteam.toByteArray());
				getCurrentResponse().setContentType("Content-Type: application/pdf;");
				getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=FasciculosAlterados.pdf");
				FacesContext.getCurrentInstance().responseComplete();
				
				outputSteam = null;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	
	

	/**
	 * Redireciona para a tela de listagem.
	 * <br/><br/>
	 * Método invocado de nenhuma página jsp
	 */
	public String telaListaCodigosBarrasFasciculos(){
		return forward(PAGINA_LISTA_CODIGOS_BARRAS_FASCICULOS);
	}
	
	
	
	
	////// Sets e Gets ///////
	
	public Assinatura getAssinaturaSelecionada() {
		return assinaturaSelecionada;
	}

	public void setAssinaturaSelecionada(Assinatura assinaturaSelecionada) {
		this.assinaturaSelecionada = assinaturaSelecionada;
	}

	public List<Fasciculo> getFasciculosAssinatura() {
		return fasciculosAssinatura;
	}

	public void setFasciculosAssinatura(List<Fasciculo> fasciculosAssinatura) {
		this.fasciculosAssinatura = fasciculosAssinatura;
	}

	public int getNumeroSequencialFasciculos() {
		return numeroSequencialFasciculos;
	}

	public void setNumeroSequencialFasciculos(int numeroSequencialFasciculos) {
		this.numeroSequencialFasciculos = numeroSequencialFasciculos;
	}

	public List<Fasciculo> getFasciculosEmOutrasAssinaturas() {
		return fasciculosEmOutrasAssinaturas;
	}

	public void setFasciculosEmOutrasAssinaturas(List<Fasciculo> fasciculosEmOutrasAssinaturas) {
		this.fasciculosEmOutrasAssinaturas = fasciculosEmOutrasAssinaturas;
	}

	/**
	 * 
	 * Retorna quantidade de fascículos em outras assinaturas com o código de barra iniciando com a código da assinatura selecionada.
	 *
	 * @int
	 */
	public int getQuantidadeFasciculosEmOutrasAssinaturas(){
		if(fasciculosEmOutrasAssinaturas != null )
			return fasciculosEmOutrasAssinaturas.size();
		else
			return 0;
	}
	
	/**
	 * 
	 * Retorna quantidade de fascículos da assinatura selecionada.
	 *
	 * @int
	 */
	public int getQuantidadeFasciculosAssinatura(){
		if(fasciculosAssinatura != null )
			return fasciculosAssinatura.size();
		else
			return 0;
	}


	public ByteArrayOutputStream getOutputSteam() {
		return outputSteam;
	}

	public void setOutputSteam(ByteArrayOutputStream outputSteam) {
		this.outputSteam = outputSteam;
	}

	public boolean isPodeReorganizarCodigos() {
		return podeReorganizarCodigos;
	}

	public void setPodeReorganizarCodigos(boolean podeReorganizarCodigos) {
		this.podeReorganizarCodigos = podeReorganizarCodigos;
	}
	
	
	
}
