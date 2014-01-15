/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
* <p> MBean que gerencia os c�digos de barras dos fasc�culos de uma assinatura do acervo. </p>
* 
* <p> Os c�digos de barras dos fasc�culos seguem o padr�o: "c�digo de assinatura" + "-" + "n�mero sequencial" quando s�o criados no sistema, contudo
* se eles s�o transferidos entre assinaturas, v�o para asssinatura nova com o c�digo de barras da assinatura antiga. 
* Esse caso de uso</p>
* 
* @author jadson
*/
@Component("gerenciarCodigosBarrasFasciculosMBean")
@Scope("request")
public class GerenciarCodigosBarrasFasciculosMBean extends SigaaAbstractController<Assinatura> implements PesquisarAcervoAssinaturas{

	
	/** P�gina que lista os c�digos de barras de todos os fasc�culos ativos da assinatura. 
	 * Na ordem em que foram criados no sistema. 
	 * A partir dessa p�gina o usu�rio pode executar a opera��o na qual os c�digos de barras ser�o re organizados, como se tivesse sido criados todos agora.*/
	public static final String PAGINA_LISTA_CODIGOS_BARRAS_FASCICULOS = "/biblioteca/aquisicao/listaCodigosBarrasFasciculos.jsp";
	
	
	/** A assinatura selecionada na tela da busca padr�o, vai ser a assinatura cujos c�digos de barras dos fasc�culos v�o ser gerenciados. */
	private Assinatura assinaturaSelecionada;

	
	/** Guarda os fasc�culos ativos da assinatura selecionada. S�o os fasc�culos cujos c�digos de barras pode ser reorganizados pelo usu�rio. */
	private List<Fasciculo> fasciculosAssinatura ;
	
	/** O n�mero sequencial que gera as c�digo de barra do fasc�culo. Essa informa��o est� presente em cada assinatura. */
	private int numeroSequencialFasciculos = 1;
	
	
	/** Guarda os fasc�culos ativos que possuem o c�digo de barras formado pelo c�digo da assinatura seleciona, mas est�o localizados em outras assinaturas.*/
	private List<Fasciculo> fasciculosEmOutrasAssinaturas;
	
	/** Guarda o relat�rio em PDF impresso no final da mudan�a de forma tempor�ria */
	private ByteArrayOutputStream outputSteam;
	
	/**
	 * <p>Diz se os c�digos dos fasc�culos v�o poder ser reorganizados ou n�o.</p>  
	 * <br/>
	 * <p>Geralmente n�o pode quando na assinatura n�o est�o presentes o fasc�culos principal e o seu suplemento. Porque fica dif�cil calcular a sequencia dos os novos c�digos de barras.</p>
	 */
	private boolean podeReorganizarCodigos = false;
	
	/**
	 * Inicia o caso de uso para alterar o c�digo de barras dos fasc�culos da assinatura, selecionando primeiramente as asssinaturas.
	 * <br><br>
	 * Chamado a partir da p�gina:<ul><li> /sigaa.war/biblioteca/menus/aquisicoes.jsp </li></ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciaReOrganizarCodigosBarras() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		return bean.iniciarPesquisaSelecionarAssinatura(this);
		
	}
	
	
	
	//////// M�todos da interface de busca padr�o do sistema ///////
	
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
	 * M�todo que cont�m a l�gica que reorganiza os c�digo de barras dos fasc�culos.
	 *
	 * @void
	 */
	private void reorganizaCodigoBarrasFasciculos() throws NegocioException {
		
		numeroSequencialFasciculos = 1;
		
		for (Fasciculo fasciculo : fasciculosAssinatura) { // Funciona se os fasc�culos estiverem ordenados pela data de cria��o, porque o fasc�culo principal sempre � criado antes dos anexos.
			
			fasciculo.setNumeroGeradorCodigoBarrasAnexos(0);
			
			
			if(fasciculo.isSuplemento()){
				
				Fasciculo principal = fasciculo.getFasciculoDeQuemSouSuplemento();
				
				if(fasciculosAssinatura.contains(principal)){
				
					int numeroGeradorAnexos = principal.getNumeroGeradorCodigoBarrasAnexos();
					
					fasciculo.setInformacao(assinaturaSelecionada.getCodigo());
					fasciculo.setInformacao2(principal.getInformacao2()+""+BibliotecaUtil.geraCaraterCorespondente( numeroGeradorAnexos ) );
					
					fasciculo.getFasciculoDeQuemSouSuplemento().setNumeroGeradorCodigoBarrasAnexos(++numeroGeradorAnexos);
				
				}else{
					addMensagemErro("N�o � poss�vel reorganizar o c�digo de barras dos fasc�culos porque o fasc�culo principal do suplemento "
							+fasciculo.getCodigoBarras()+" n�o est� presente na assinatura.");
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
					addMensagemErro("N�o � poss�vel reorganizar o c�digo de barras dos fasc�culos porque o suplemento do fasc�culo "
							+fasciculoOutraAssinatura.getFasciculoDeQuemSouSuplemento().getCodigoBarras()+" n�o est� presente na assinatura.");
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
	 * <p>Metodo que realiza a a��o desse caso de uso. Chamando o processador para alterar o c�digo dos fasc�culos. </p>
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/listaCodigosBarrasFasciculos.jsp
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
				
				addMensagemInformation("Reorganiza��o dos C�digos de Barra realizados com Sucesso!");
				
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
	 * <p>M�todo respons�vel por gerar um relat�rio final em PDF sobre as altera��es realizadas nos c�digos de barras dos fasc�culos.</p>
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
			 *           Gera o cabe�alho do relat�rio com as imagens do sistema, etc...
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
			 *           Gera a tabela com as informa��es da altera��es dos c�digos de barras
			 * ***********************************************************************************/
			
			
			/* ***********************************************************************************
			 *                         Gera o cabe�alho da tabela
			 * ***********************************************************************************/
			
			PdfPTable tabelaDadosRelatorio = new PdfPTable(new float[] { 0.5f, 0.5f }); // Cria uma tebla de 3 Colunas de largura diferente
			tabelaDadosRelatorio.setWidthPercentage(100); // o tamanho da tela
			tabelaDadosRelatorio.setSpacingBefore(20);
			
			PdfPCell titulo = new PdfPCell(new Paragraph("Fasc�culos cujos C�digos de Barra Foram Alterados ( "+fasciculosAssinatura.size()+" )", fonteNegrito));
			titulo.setColspan(3);
			titulo.setHorizontalAlignment(Element.ALIGN_CENTER);  
			titulo.setBorderWidthTop(2);
			titulo.setBorderWidthBottom(0);
			titulo.setBorderWidthLeft(0);
			titulo.setBorderWidthRight(0);
			
			tabelaDadosRelatorio.addCell(titulo);  
			
			
			
			PdfPCell header1 = new PdfPCell(new Paragraph("C�digos de Barras Anteriores", fonteNegrito));
			header1.setHorizontalAlignment(Element.ALIGN_LEFT); 
			header1.setBackgroundColor(corCabecalhoTabela);
			tabelaDadosRelatorio.addCell(header1);  
			
			
			PdfPCell header2 = new PdfPCell(new Paragraph("	Novos C�digos Barras", fonteNegrito));
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
				
				tabelaDadosRelatorio.addCell(celula1); // O c�digo de barras antigo
				tabelaDadosRelatorio.addCell(celula2); // O c�digo de barras novo
				
				
			}
			
			doc.add(tabelaDadosRelatorio);
			
			/* ***********************************************************************************
			 *                        Adiciona as informa�es do Rodap� do Documento
			 * ***********************************************************************************/
			PdfPTable tableRodape = new PdfPTable(1); // Cria uma tebla de 2 Colunas igual ao Relat�rio impresso na pagina.
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
	 * <p>Envia o PDF para a sa�da do usu�rio.</p>
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
	 * M�todo invocado de nenhuma p�gina jsp
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
	 * Retorna quantidade de fasc�culos em outras assinaturas com o c�digo de barra iniciando com a c�digo da assinatura selecionada.
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
	 * Retorna quantidade de fasc�culos da assinatura selecionada.
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
