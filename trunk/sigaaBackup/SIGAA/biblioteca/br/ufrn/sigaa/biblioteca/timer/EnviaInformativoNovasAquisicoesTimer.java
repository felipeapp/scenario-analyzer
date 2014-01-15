/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/12/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.timer;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioNovasAquisicoesDAO;
import br.ufrn.sigaa.biblioteca.dao.DisseminacaoDaInformacaoDao;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.RelatorioNovasAquisicoesMBean;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

import com.lowagie.text.Chunk;
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
 *
 * <p>Timer que roda 1 vez por mês, todo dia 1º, enviando os usuários da biblioteca que solicitaram o informativo de novas aquisições.</p>
 *
 * <p> <i> O informativo de novas aquisições é um relatório que mostra os Títulos que possuem os materiais mais recente do acervo.
 * Esse relatório está disponível na área pública do sistema e permite o usuário consultar as novas aquisições dos últimos 6 meses.
 * No caso do informativo enviado mensalmente ao usuário, contém as novas aquisições do último mês.</i> </p>
 * 
 * <p>
 *     <strong>Observação: </strong> inserir um registro na tabela INFRA.REGISTRO_TIMER (Banco SISTEMAS_COMUM). Vai possuir 2 parâmetros: 
 *     horaExecucao: 1h 
 *     tipoReplicacao: M = Mensal
 *     diaExecucao: 1
 *     <pre>
 *     insert into INFRA.REGISTRO_TIMER (id, hora_execucao, tempo, tipo_repeticao, classe, ativa, servidor_execucao, servidor_restricao, dia_mes_execucao) 
 *     values (24, 1, 0, 'M', 'br.ufrn.sigaa.biblioteca.timer.EnviaInformativoNovasAquisicoesTimer', true, 'sistemas1', 'sistemas1i1', 1);
 *      </pre>
 * </p>
 *     
 * <p>Para executa a classe diretamente utilize.
 *     <pre>
 *      Executors.newSingleThreadExecutor().execute(new EnviaInformativoNovasAquisicoesTimer());
 *     </pre>
 * </p>
 * 
 * @author jadson
 *
 */
public class EnviaInformativoNovasAquisicoesTimer  extends TarefaTimer{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			gerarEEnviarInformativo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * <p>Busca todos os usuários da biblioteca ativos que cadastraram interesse em receber o informativo. </p>
	 * 
	 * <p>Caso exista algum, busca os seus endereços eletrônicos, gera o relatório em PDF para o último mês
	 * e envia esse PDF para os usuários.</p>  
	 *
	 */
	private void gerarEEnviarInformativo() {
		
		DisseminacaoDaInformacaoDao dao = null;
		RelatorioNovasAquisicoesDAO daoNovasAquisicoes = null;
		AreaConhecimentoCnpqDao areaDao = null;
		
		try {
			
			dao = DAOFactory.getInstance().getDAO(DisseminacaoDaInformacaoDao.class);
			daoNovasAquisicoes = DAOFactory.getInstance().getDAO(RelatorioNovasAquisicoesDAO.class);
			areaDao = DAOFactory.getInstance().getDAO(AreaConhecimentoCnpqDao.class);
			
			
			///////////////////// Busca os usuários com interesse e as grandes áreas do CNPq////////////////////
			
			List<Object[]> infoUsuariosInteressados = dao.findUsuariosAtivosInteresseReceberInformativo();
			
			List<AreaConhecimentoCnpq> areasCNPQ = (List<AreaConhecimentoCnpq>) areaDao.findGrandeAreasConhecimentoCnpq();
			
			////////////////////////////////////////////////////////////////////////////////////
			
			
			
			
			
			
			
			///////////////////// A parte que gera o arquivo a ser enviado //////////////////////////////
			
			final int diasDeRetardoAConsiderar = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.DIAS_RETARDO_MATERIAL_DISPONIVEL_ACERVO); 
			
			Date ontem = CalendarUtils.adicionaDias(new Date(), -1); // volta para o último dia do mês anterior
			Calendar cTemp = Calendar.getInstance();
			cTemp.setTime(ontem);
			Date inicioMes = CalendarUtils.primeiroDataMes(cTemp.get(Calendar.MONTH)); // pega o primeiro dia do mês anterior.
			
			SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
			
			String inicioMesFormatado = formatador.format(inicioMes);
			String ontemFormatado = formatador.format(ontem);
			
			if(diasDeRetardoAConsiderar > 0){
				inicioMes = CalendarUtils.adicionaDias(inicioMes, -diasDeRetardoAConsiderar);
				ontem = CalendarUtils.adicionaDias(ontem, -diasDeRetardoAConsiderar);
			}
			
			
			/*  Map que guarda: <IdAreaConhecimento, Arquivo com os Títulos específicos da Área >   */
			Map<Integer, Object[]> arquivos = new HashMap<Integer, Object[]>();
			
			
			if(infoUsuariosInteressados.size() > 0){  // Se tem alguém interessado gera o arquivo
				
				// Vai gerar um informativo para cada grande área e outro com todas e vai enviar de cada arquivo de acordo com o perfil do usuário //
				for (AreaConhecimentoCnpq areaConhecimentoCnpq : areasCNPQ) {
					
					ByteArrayOutputStream outputStreamTempArquivo = new ByteArrayOutputStream(); 
					String[] registros = new RelatorioNovasAquisicoesMBean(daoNovasAquisicoes).geraDadosRelatorioNovasAquisicoes(null, areaConhecimentoCnpq, inicioMes, ontem, false);
					
					if(registros.length > 0 ){
						
						geraArquivoPDFInformativo(outputStreamTempArquivo, registros, areaConhecimentoCnpq, inicioMesFormatado, ontemFormatado); 
						outputStreamTempArquivo.flush();
						outputStreamTempArquivo.close();
						arquivos.put(areaConhecimentoCnpq.getId(), new Object[] { "Informativo Novas Aquisições.pdf", outputStreamTempArquivo.toByteArray()} );
					}
				}
			
				
				// Gera o arquivo para todas as áreas //
				
				ByteArrayOutputStream outputStreamTempArquivo = new ByteArrayOutputStream(); 
				String[] registros = new RelatorioNovasAquisicoesMBean(daoNovasAquisicoes).geraDadosRelatorioNovasAquisicoes(null, null, inicioMes, ontem, false);
				
				if(registros.length > 0 ){
					geraArquivoPDFInformativo(outputStreamTempArquivo, registros, null, inicioMesFormatado, ontemFormatado); 
					outputStreamTempArquivo.flush();
					outputStreamTempArquivo.close();
					arquivos.put(-1, new Object[] { "Informativo Novas Aquisições.pdf", outputStreamTempArquivo.toByteArray()} );
				}
				
			}
			//////////////////////////////////////////////////////////////////////////////////
			
			
			
			
			
			
			/////////////////// A parte que envia os emails para usuários com interesse /////////////////////
		
			EnvioEmailBiblioteca sender = new EnvioEmailBiblioteca();
			
			String assuntoEmail = " Informativo Mensal de Novas Aquisições ";
			String tituloEmail = " Informativo de Novas Aquisições ";
			String mensagemNivel1 = "Em anexo seque o Informativo de Novas Aquisições das bibliotecas.";
			String mensagemFechamento = "O informativo contém os novos materiais que foram incluídos no acervo durante o período de "+inicioMesFormatado+" a "+ontemFormatado+".";
			String mensagemAlertaRodape = "Você optou por receber este e-mail. Se não desejar mais recebê-lo, atualize o seu Perfil de Interesse na Biblioteca ";
			
			int qtdEmailsEnviaos = 0;
			
			for (Object[] objects : infoUsuariosInteressados) { // Para cada usuário interessado
				
				String nomeUsuario  = (String )objects[0];
				String emailUsuario = (String )objects[1];
				String emailPessoa  = (String )objects[2];
				Integer idAreaEscolhida =  (Integer) objects[3];
				
				if(StringUtils.isEmpty(nomeUsuario) || ( StringUtils.isEmpty(emailUsuario) && StringUtils.isEmpty(emailUsuario) ) )
					continue; // passsa para o próximo
				
				Object[] arquivo = null;
				
				if(idAreaEscolhida == null) // Se o usuário não escolhei a área, recebe o arquivo geral
					arquivo = arquivos.get(-1);
				else
					arquivo = arquivos.get(idAreaEscolhida); // Se escolheu recebe o arquivo com materiais apenas da área escolhida.
				
				if(arquivo != null){ // se tem algo para enviar
				
					sender.enviaEmail( 
							nomeUsuario, (StringUtils.notEmpty(emailUsuario) ? emailUsuario : emailPessoa)
							, assuntoEmail, tituloEmail
							, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA
							, null
							, mensagemNivel1
							, null, null, null, null
							, mensagemFechamento
							, null
							, mensagemAlertaRodape
							, arquivo);
					
					qtdEmailsEnviaos++;
				}
			}
			
			
			
			
			//////////////////////////////////////////////////////////////////////////////////////
			
			
			enviaEmailSucessoEnvioInformativo(qtdEmailsEnviaos);
			
			
		} catch (Exception ex) {
		
			enviaEmailErroEnvioInformativo(ex);
			
		}finally{
			if(dao != null) { dao.close(); dao = null; }
			if(daoNovasAquisicoes != null) { daoNovasAquisicoes.close(); daoNovasAquisicoes = null; }
			if(areaDao != null) {  areaDao.close(); areaDao = null; }
		}
		
	}

	/**
	 * <p>Gera o Arquivo em PDF para ser enviado em anexo no email enviado para o usuário.</p>
	 * 
	 * <p>Com toda a formatação para ficar igual ao informativo visualizado pelo sistema</p> 
	 *  
	 * @param byteArray
	 * @return
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	 */
	private Object geraArquivoPDFInformativo(ByteArrayOutputStream output, String[] registros, AreaConhecimentoCnpq areaConhecimento, String inicioMesFormatado, String ontemFormatado ) throws DocumentException, FileNotFoundException {
		
		// margin top, margin bottom, margin left, margin rigth
		Document doc = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);
		PdfWriter.getInstance(doc, output);
		doc.open();
		
		Color corCabecalho = new Color(64, 78, 130);   // #404E82
		Color corBordaRodape = new Color(217, 156, 68);   // #D99C44
		Color corRegistroPar = new Color(235, 237, 239); // #EBEDEF
		Color corRegistroImpar = Color.WHITE;
		
		Font fonteCabecalho = new Font();
		fonteCabecalho.setStyle(Font.BOLD);
		fonteCabecalho.setColor(Color.WHITE);
		
		Font fonteTitulo= new Font();
		fonteTitulo.setColor(corBordaRodape);
		fonteTitulo.setStyle(Font.BOLDITALIC);
		
		/* ***********************************************************************************
		 *                        Adiciona o Título
		 * ***********************************************************************************/
		
		
		Paragraph tituloRelatorio = new Paragraph("Informativo De Novas Aquisições ", fonteTitulo);
		tituloRelatorio.setAlignment(Element.ALIGN_CENTER);
		tituloRelatorio.setSpacingAfter(20);
		
		doc.add(tituloRelatorio);
		
		
		/* ***********************************************************************************
		 *                        Adiciona as informaões dos Filtros
		 * ***********************************************************************************/
		Font fonteNegrito = new Font();
		fonteNegrito.setStyle(Font.BOLD);
		
		if(areaConhecimento != null){
			Phrase labelFiltroArea = new Phrase("Área de Conhecimento: ", fonteNegrito);
			Phrase dadosFiltroArea = new Phrase(areaConhecimento.getNome());
			Paragraph filtrosArea = new Paragraph();
			filtrosArea.add(labelFiltroArea);
			filtrosArea.add(dadosFiltroArea);
			filtrosArea.setSpacingAfter(10);
			doc.add(filtrosArea );
		}
		
		Phrase labelFiltroPeriodo = new Phrase("Período: ", fonteNegrito);
		Phrase dadosFiltroPeriodo = new Phrase(" "+inicioMesFormatado+" a "+ontemFormatado);
		Paragraph filtrosPeriodo = new Paragraph();
		filtrosPeriodo .add(labelFiltroPeriodo);
		filtrosPeriodo .add(dadosFiltroPeriodo);
		filtrosPeriodo .setSpacingAfter(20);
		doc.add(filtrosPeriodo );
		
		
		
		/* ***********************************************************************************
		 *                         Gera o cabeçalho da tabela
		 * ***********************************************************************************/
		
		PdfPTable tabelaFormatosReferencia = new PdfPTable(2); // Cria uma tebla de 2 Colunas igual ao Relatório impresso na pagina.
		tabelaFormatosReferencia.setWidthPercentage(100);
		
		PdfPCell header = new PdfPCell(new Paragraph("Novas Aquisições das Bibliotecas ("+registros.length+")", fonteCabecalho));
		header.setColspan(2);
		header.setBackgroundColor(corCabecalho); 
		header.setHorizontalAlignment(Element.ALIGN_CENTER);  
		
		tabelaFormatosReferencia.addCell(header);  
		
		
		/* ***********************************************************************************
		 *       Preenche a tabela com o formato de referência dos títulos
		 * ***********************************************************************************/
		boolean impremeCorFunto = true;
		
		int contador = 1; // Começa de 1 porque os 2 primeiros troca depois de 1, o restante a cada 2 muda a cor de fundo.
		
		Pattern pattern = Pattern.compile(FormatosBibliograficosUtil.INICIO_NEGRITO);  
		
		for (String registro : registros) {
			
			Matcher matcher = pattern.matcher(registro);  
			
			int contadorOcorrencias = 0;
			while(matcher.find()){
				contadorOcorrencias++;
			}
			
			PdfPCell celula = new PdfPCell();
			
			trataDadosEmNegritoParaPDF(registro, contadorOcorrencias, celula, fonteNegrito);
			
			if(impremeCorFunto)
				celula.setBackgroundColor(corRegistroPar);
			else
				celula.setBackgroundColor(corRegistroImpar);
			
			tabelaFormatosReferencia.addCell(celula);  // As informações do Título em formato de referência.
			
			
			contador = (contador+1) % 3;  // contador vai de 0 a 2.
			
			if(contador == 2){
				impremeCorFunto = ! impremeCorFunto; // Só muda a cada 2 células
				contador = 0;
			}
			
		}
		
		doc.add(tabelaFormatosReferencia);
		
		/* ***********************************************************************************
		 *                        Adiciona as informaões do Rodapé do Documento
		 * ***********************************************************************************/
		PdfPTable tableRodape = new PdfPTable(1); // Cria uma tebla de 2 Colunas igual ao Relatório impresso na pagina.
		PdfPCell rodape = new PdfPCell(new Paragraph(RepositorioDadosInstitucionais.get("siglaSigaa") +" | "+ RepositorioDadosInstitucionais.get("rodapeSistema"), fonteCabecalho));
		rodape.setBorderColorTop(corBordaRodape);
		rodape.setBorderWidthTop(3);
		rodape.setBackgroundColor(corCabecalho); 
		rodape.setHorizontalAlignment(Element.ALIGN_CENTER); 
		rodape.setMinimumHeight(20f);
		tableRodape.addCell(rodape);
		tableRodape.setSpacingBefore(20);
		tableRodape.setWidthPercentage(100);
		
		doc.add(tableRodape);
		
		
		doc.close();
		
		return doc;
	}

	
	/**
	 * <p>Trata a exibição de dadas em negrito para a exibição em PDF.</p>
	 * <p>Como java não tem um caracter específico para negrito é preciso tratar isso para cada tipo de saída que o 
	 * formato de referência for mostrado para o usuário.<p>
	 *
	 * @param registro
	 * @param contadorOcorrencias
	 * @param celula
	 */
	private void trataDadosEmNegritoParaPDF(String registro, int contadorOcorrencias, PdfPCell celula, Font fonteNegrito) {
		
		if(contadorOcorrencias == 2){ // Possui o Título e Número de Chamada em Negrito
			
			String[] separados = registro.split(FormatosBibliograficosUtil.INICIO_NEGRITO);
			
			String antesTituloReferencia = separados[0];
			String meio = separados[1];
			String numeroChamadaReferencia = separados[2];
			
			numeroChamadaReferencia = numeroChamadaReferencia.replace(FormatosBibliograficosUtil.FINAL_NEGRITO, "");
			
			String[] teste = meio.split(FormatosBibliograficosUtil.FINAL_NEGRITO);
			
			String tituloReferencia = teste[0];
			String restoReferencia = teste[1];
			
			// Trata a exibição dos dados em negritono PDF //
			Chunk fraseAntesDoTitulo = new Chunk(antesTituloReferencia);
			Chunk fraseTitulo = new Chunk(tituloReferencia, fonteNegrito);
			Chunk fraseRestoReferencia = new Chunk(restoReferencia);
			Chunk fraseNumeroChamada = new Chunk(numeroChamadaReferencia, fonteNegrito);
			
			
			Phrase phrase = new Phrase();
			
			phrase.add(fraseAntesDoTitulo);
			phrase.add(fraseTitulo);
			phrase.add(fraseRestoReferencia);
			phrase.add(fraseNumeroChamada);
			celula.addElement(phrase);
		}else{ 
			if(contadorOcorrencias == 1){ // Possui apeans o e Número de Chamada em Negrito
				
				String[] separados = registro.split(FormatosBibliograficosUtil.INICIO_NEGRITO);
				String restoReferencia = separados[0];
				String numeroChamadaReferencia = separados[1].replace(FormatosBibliograficosUtil.FINAL_NEGRITO, "");
				
				Chunk fraseRestoReferencia = new Chunk(restoReferencia);
				Chunk fraseNumeroChamada = new Chunk(numeroChamadaReferencia, fonteNegrito);
				
				Phrase phrase = new Phrase();
				phrase.add(fraseRestoReferencia);
				phrase.add(fraseNumeroChamada);
				
				celula.addElement(phrase);
			}else{ 
				// Não possui nada engrito
			}
		}
	}
	
	

	/**
	 * Envia um email para os adminsitrados dos sistema informando que a rotina executou normalmente.
	 *
	 */
	private void enviaEmailSucessoEnvioInformativo(int qtdEmailsEnviados) {
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "["+RepositorioDadosInstitucionais.get("siglaSigaa")+"]"+"  Sucesso no envio Informativo Mensal de Novas Aquisições da Biblioteca ";
		String mensagem = "Server: " + NetworkUtils.getLocalName() 
		+"<br/><br/>O Informativo de Novas Aquisições da Biblioteca foi executado com sucesso em: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())    
		+"<br/><br/>Total de email enviados: " + qtdEmailsEnviados; 
		MailBody mail = new MailBody();
		mail.setEmail(email);
		mail.setAssunto(assunto);
		mail.setMensagem(mensagem);
		
		Mail.send(mail);
		
	}

	
	/**
	 * Envia um email para os adminsitrados dos sistema informando que houve um erro na execução da rotina.
	 */
	private void enviaEmailErroEnvioInformativo(Exception ex) {
		String email =  ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "["+RepositorioDadosInstitucionais.get("siglaSigaa")+"]"+" Erro no envio do Informativo Mensal de Novas Aquisições da Biblioteca ";
		String mensagem =  "Server: " + NetworkUtils.getLocalName() + "<br>" +
		ex.getMessage() + "<br><br><br>" + Arrays.toString(ex.getStackTrace()).replace(",", "\n") +
		(ex.getCause() != null ? Arrays.toString(ex.getCause().getStackTrace()).replace(",", "\n") : "");
		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem );
		Mail.send(mail);
		
	}

}
