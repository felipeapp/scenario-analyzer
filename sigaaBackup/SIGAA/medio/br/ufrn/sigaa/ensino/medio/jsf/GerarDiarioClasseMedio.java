/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/09/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.awt.Color;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.DiarioClasse;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dao.RegraNotaDao;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.RegraNota;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dao.CalendarioRegraDao;
import br.ufrn.sigaa.ensino.medio.dao.NotaSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CalendarioRegra;
import br.ufrn.sigaa.ensino.medio.dominio.NotaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.MultiColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Classe para gerar o diário de classe de uma série de ensino médio.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class GerarDiarioClasseMedio {

	/** Parâmetro que indica que indica o total de datas exibidas na planilha de frequência. */
	public static final int TOTAL_DATAS = 75;
	
	/** Indica o total de linha que cabem em uma página com 2 colunas */
	private final int LINHAS = 88;
	
	/** Turma do diário gerado. */
	private Turma turma;
	
	/** Calendário acadêmico atual. */
	private CalendarioAcademico calendarioVigente;
	
	/** Imagem do logo da UFRN */
	private URL ufrnImg = getClass().getResource("/br/ufrn/sigaa/relatorios/fontes/ufrn.gif"); 

	/** Imagem do logo da SINFO */
	private URL sinfoImg = getClass().getResource("/br/ufrn/sigaa/relatorios/fontes/logosuper.gif"); 
	
	/** Objeto que guarda as informações sobre os parâmetros da unidade gestora da turma. */
	private ParametrosGestoraAcademica param;
	
	/** Objeto para formatação de datas */
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	/** Objeto que mapeia o Id do discente com suas frequências. */
	private Map<Integer, Set<FrequenciaAluno>> mapa;
	
	/** Lista que guarda os feriados */
	private List<Date> feriados;

	/**  Constante para criação de uma única página. */
	public static final Integer NOVA_PAGINA = 1;
	
	/** Contém todas as informações do diário concatenadas. */
	private String stringCodigo;
	
	/** Construtor padrão */
	public GerarDiarioClasseMedio(Turma turma, CalendarioAcademico calendarioVigente, ParametrosGestoraAcademica param) {
		this.turma = turma;
		this.calendarioVigente = calendarioVigente;
		this.param = param;
	}	
	
	/**
	 * Gera mapa de frequência em PDF para uma turma. 
	 * @param os
	 * @throws Exception
	 */
	public void gerarMapa(OutputStream os) throws Exception {

		Document document = new Document(PageSize.A4.rotate(), 25, 25, 30, 30);
		PdfWriter.getInstance(document, os);
		
		document.open();

		List <Object> es = adicionarMapaFrequencia(getMatriculas());
		
		if (es.isEmpty())
			throw new NegocioException("A turma não está com os horários definidos.");
		
		adicionaElementos(document, es);

		document.close();
	}
	
	/**
	 * Gera o diário de turma em pdf para uma turma e o envia ao OutputStream passado. Além disso, gera o código hash que identificará o documento.
	 * @param os
	 * @throws Exception
	 */
	public void gerar(OutputStream os) throws Exception {
		
		Collection<MatriculaComponente> matriculas = getMatriculas();
		
		Document document = new Document(PageSize.A4, 25, 25, 30, 30);
		PdfWriter.getInstance(document, os);
		
		document.open();
		
		// Capa do Diário 
		List <Object> es = adicionarCapa();
		adicionaElementos(document, es);
		
		es = new ArrayList <Object> ();
		
		// Versões do documento
		es.addAll(adicionarVersoes());
		
		// Lista de Notas e Faltas
		es.addAll(adicionarNotasEFaltas(matriculas));
		
		// Mapa de Frequência 
		es.addAll(adicionarMapaFrequencia(matriculas));

		// Conteúdo Programático
		es.addAll(adicionarConteudoProgramatico(document));
		
		// Exibe o código de verificação
		float [] larguras = {0.25f,0.5f};
		PdfPTable tableCodigoHash = new PdfPTable(larguras);
		tableCodigoHash.addCell(buildCell(" ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
		tableCodigoHash.addCell(buildCell(" ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
		tableCodigoHash.addCell(new Paragraph("Código de Verificação:", FontFactory.getFont("Verdana", 13, Font.NORMAL)));
		tableCodigoHash.addCell(new Paragraph(UFRNUtils.toSHA1Digest(stringCodigo).substring(0, 10), FontFactory.getFont("Verdana", 13, Font.BOLD)));

		document.add(tableCodigoHash);
		
		//Adiciona o restante do documento
		adicionaElementos(document, es);	
		document.close();
	}

	/**
	 * Retorna as matrículas da turma selecionada
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private Collection<MatriculaComponente> getMatriculas() throws DAOException,
			NegocioException {
		
		Collection<MatriculaComponente> matriculas = null;
		
		TurmaMedioDao turmaDao = getDAO(TurmaMedioDao.class);
		try {
			matriculas = turmaDao.findMatriculasAConsolidar(turma);
			
			if (!isEmpty(matriculas)) {
				Collections.sort((List<MatriculaComponente>) matriculas, new Comparator<MatriculaComponente>() {
					public int compare(MatriculaComponente o1, MatriculaComponente o2) {
						return o1.getDiscente().getPessoa().getNomeAscii().compareToIgnoreCase(o2.getDiscente().getPessoa().getNomeAscii());
					}
				});
			
				for (MatriculaComponente matricula : matriculas) {
					if (matricula.isTrancado()) {
						String nome = matricula.getDiscente().getPessoa().getNome();
						matricula.getDiscente().getPessoa().setNome(nome + " (TRANCADO)");
					}
				}
			} else {
				throw new NegocioException("Não há alunos matriculados nessa turma.");
			}
			
			return matriculas;
			
		} finally {
			if (turmaDao != null)
				turmaDao.close();
		}
	}
	
	/**
	 * Adiciona os elementos passados ao documento passado.
	 * 
	 * @param document
	 * @param elementos
	 * @throws DocumentException
	 */
	private void adicionaElementos(Document document, List <Object> elementos) throws DocumentException{
		for (Object e :elementos){
			if (e.equals(NOVA_PAGINA))
				document.newPage();
			else if (e instanceof Rectangle)
				document.setPageSize((Rectangle) e);
			else if (e instanceof Element)
				document.add((Element) e);
		}
	}
	
	/**
	 * Cria a parte do documento que possui os conteúdos cadastrados na turma.
	 * 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	private List <Object> adicionarConteudoProgramatico (Document document) throws Exception{
		
		List <Object> elementos = new ArrayList <Object> ();
		
		elementos.add(PageSize.A4);
		
		TurmaDao dao = getDAO(TurmaDao.class);
		CalendarioRegraDao calRegraDao = null;
		try {
			List<TopicoAula> conteudos = dao.findConteudosByTurma(turma);
			calRegraDao = getDAO(CalendarioRegraDao.class);
			
			Map<Date, List<TopicoAula>> mapa = new TreeMap<Date, List<TopicoAula>>();
			
			Collection<CalendarioRegra> calRegras = calRegraDao.findByCalendario(calendarioVigente);
			if (ValidatorUtil.isNotEmpty(calRegras)){			
			
				Set<Date> datas = TurmaUtil.getDatasAulasTruncate(turma, calendarioVigente);
				
				// percorre por todas as regras definidas no calendário, assim dividindo o 
				// mapa por unidade. Ex.: 1º Bimestre, 2º Bimestre.. cada um em uma folha
				for (CalendarioRegra cal : calRegras){
					
					List<Date> datasUnidade = new ArrayList<Date>();
					for (Date data : datas){
						if (cal.getDataInicio().getTime() <= data.getTime() &&
								cal.getDataFim().getTime() >= data.getTime())
							datasUnidade.add(data);
					}	
					
					for (Date data : datasUnidade) {
						List<TopicoAula> lista = new ArrayList<TopicoAula>();
						for (TopicoAula topico : conteudos) {
							Date dataInicioTopico = CalendarUtils.descartarHoras(topico.getData());
							Date dataFimTopico = CalendarUtils.descartarHoras(topico.getFim());
							data = CalendarUtils.descartarHoras(data);
							
							if (data.equals(dataInicioTopico) || data.equals(dataFimTopico) || (data.after(dataInicioTopico) && data.before(dataFimTopico)))
								lista.add(topico);
						}
						mapa.put(data, lista);
					}
					
					elementos.add(NOVA_PAGINA);
					elementos.add(buildHeader(false));
					elementos.add(buildSection("Conteúdo Programado - "+cal.getRegra().getTitulo()));
					
					MultiColumnText mct = new MultiColumnText();
					mct.addRegularColumns(document.left(), document.right(), 10f, 2);
					
					PdfPTable table = null;
					int i = 0;
					
					for (Date data : mapa.keySet()) {
					
						if (i % LINHAS == LINHAS / 2 || i == 0) {
							if (i != 0)
								mct.addElement(table);
							
							table = new PdfPTable(new float[] { 0.2f, 0.8f });
							table.setWidthPercentage(100);
							table.setWidthPercentage(100);
							table.addCell(buildHeader("Data"));
							table.addCell(buildCell("Descrição",8,Element.ALIGN_LEFT,true));
						}
						
						PdfPCell cell = buildHeader(sdf.format(data));
						cell.setFixedHeight(15.0f);
						if (table != null)
							table.addCell(cell);
						
						StringBuilder descricao = new StringBuilder();
						for (TopicoAula aula : mapa.get(data))
							descricao.append(aula.getDescricao() + "\n");				
			
						if (table != null)
							table.addCell(buildCell(descricao.toString(), true));
							
						i++;
					}
			
					if (table != null)
						mct.addElement(table);
					
					elementos.add(mct);
				}
			}
			
			return elementos;
		} finally {
			dao.close();
			if (calRegraDao != null)
				calRegraDao.close();
		}
	}
	
	/**
	 * Adiciona a capa ao diário.
	 * @param document
	 * @throws Exception
	 */
	private List <Object> adicionarCapa() throws Exception{
		
		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class);
		
		try {
			TurmaSerieAno tsAno = tsDao.findByTurma(turma);
			
			List <Object> elementos = new ArrayList <Object> ();
			
			elementos.add(buildHeader(false));
			
			PdfPTable table = new PdfPTable(1);
			PdfPCell cell = new PdfPCell(new Paragraph("Diário de Classe", FontFactory.getFont("Verdana", 22, Font.BOLD)));
			cell.setBorder(0);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPaddingTop(140);
			cell.setPaddingBottom(100);
			table.addCell(cell);
			elementos.add(table);
			
			table = new PdfPTable(new float[] { 0.25f, 0.75f });
			table.addCell(buildCell("Centro: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));		
			table.addCell(buildCell(turma.getDisciplina().getUnidade().getGestora().getNome(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			table.addCell(buildCell("Departamento: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
				
			table.addCell(buildCell(turma.getDisciplina().getUnidade().getNome(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			table.addCell(buildCell("Código: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			table.addCell(buildCell(turma.getDisciplina().getCodigo(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			table.addCell(buildCell("Disciplina: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			table.addCell(buildCell(turma.getDisciplina().getNome(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			table.addCell(buildCell("Carga Horária: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			table.addCell(buildCell(String.valueOf(turma.getDisciplina().getChTotal())+"hrs", 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			elementos.add(table);
			
			Paragraph p = new Paragraph("");
			p.setSpacingBefore(100);
			elementos.add(p);
			
			table = new PdfPTable(4);
			table.addCell(buildCell("Série: ", 11, Element.ALIGN_CENTER, 0, Font.NORMAL, false));
			table.addCell(buildCell("Turma: ", 11, Element.ALIGN_CENTER, 0, Font.NORMAL, false));
			table.addCell(buildCell("Ano: ", 11, Element.ALIGN_CENTER, 0, Font.NORMAL, false));
			table.addCell(buildCell("Horário: ", 11, Element.ALIGN_CENTER, 0, Font.NORMAL, false));
			table.addCell(buildCell(tsAno.getTurmaSerie().getSerie().getDescricaoCompleta(), 11, Element.ALIGN_CENTER, 0, Font.BOLD, true));
			table.addCell(buildCell(turma.getCodigo(), 11, Element.ALIGN_CENTER, 0, Font.BOLD, true));
			table.addCell(buildCell(turma.getAnoPeriodo(), 11, Element.ALIGN_CENTER, 0, Font.BOLD, true));
			table.addCell(buildCell(turma.getDescricaoHorario() == null ? "" : turma.getDescricaoHorario(), 11, Element.ALIGN_CENTER, 0, Font.BOLD, true));
			elementos.add(table);
			
			elementos.add(p);
			
			table = new PdfPTable(new float[] { 0.3f, 0.7f });
			table.addCell(buildCell("Matrícula", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			table.addCell(buildCell("Docente(s)", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			
			List<DocenteTurma> dts = null;
			dts = CollectionUtils.toList(turma.getDocentesTurmas());
			
			for (DocenteTurma d : dts) {
				if (!isEmpty(d.getDocenteExterno())) {
					// Docente Externo
					table.addCell(buildCell(d.getDocenteExterno().getIdentificacao(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
					table.addCell(buildCell(d.getDocenteExterno().getNome() + (d.getChDedicadaPeriodo() == null ? "":" - " + d.getChDedicadaPeriodo()+"h"), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
				} else {
					table.addCell(buildCell(d.getDocente().getIdentificacao(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
					table.addCell(buildCell(d.getDocente().getNome() + (d.getChDedicadaPeriodo() == null ? "":" - " + d.getChDedicadaPeriodo()+"h"), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
				}
			}
			
			elementos.add(table);
			
			return elementos;
		} finally {
			if (tsDao != null)
				tsDao.close();
		}
	}
	
	/**
	 * Adiciona as versões do documento.
	 * @param document
	 * @throws Exception
	 */
	private List <Object> adicionarVersoes() throws Exception{
		
		List <Object> elementos = new ArrayList <Object> ();
		
		TurmaDao dao = getDAO(TurmaDao.class);
		try {
			List <DiarioClasse> versoes = (List<DiarioClasse>) dao.findByExactField(DiarioClasse.class, "turma.id", turma.getId());
			
			if (!versoes.isEmpty()){
				elementos.add(NOVA_PAGINA);
				elementos.add(buildHeader(false));
				
				elementos.add(buildSection("Versões do documento"));
				
				float [] larguras = {0.07f,0.4f, 0.08f};
				
				PdfPTable table = new PdfPTable(larguras);
				
				table.addCell(buildHeader("Data"));
				table.addCell(buildCell("Usuário", false));
				table.addCell(buildHeader("Código"));
		
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				
				for (DiarioClasse d : versoes){
					table.addCell(buildCell(sdf.format(d.getDataCriacao()), 9, Element.ALIGN_CENTER, false));
					table.addCell(buildCell(d.getRegistroEntradaCriacao().getUsuario().getPessoa().getNome(), false));
					table.addCell(buildCell(d.getCodigoHash(), 9, Element.ALIGN_CENTER, false));
				}
				
				elementos.add(table);
			}
			
			return elementos;
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Adiciona a planilha com as notas e faltas.
	 * @param document
	 * @param matriculas
	 * @throws Exception
	 */
	private List <Object> adicionarNotasEFaltas(Collection<MatriculaComponente> matriculas) throws Exception{
		
		NotaSerieDao notaDao = getDAO(NotaSerieDao.class);
		RegraNotaDao regraDao = getDAO(RegraNotaDao.class); 
		try {
			List <Object> elementos = new ArrayList <Object> ();
			
			if (matriculas == null)
				return elementos;

			// pega todas as regras do curso
			List<RegraNota> regras = regraDao.findByCurso(turma.getCurso());
			
			if (ValidatorUtil.isNotEmpty(regras)){
				
				elementos.add(PageSize.A4.rotate());
				elementos.add(NOVA_PAGINA);
				elementos.add(buildHeader(false));
				elementos.add(buildSection("Lista de Notas e Faltas"));				
				
				/* numero de unidade será a quantidade de regras x 2 
				 * (pois será exibido média e falta para cada regra de unidade) */
				Integer numUnidades = 0;
				boolean possuiProvaFinal = false;
				for (RegraNota regra : regras)
					if (!regra.isProvaFinal())
						numUnidades++;
					else
						possuiProvaFinal = true;
				
				numUnidades = numUnidades * 2;				
				Integer numCampos = 6 + numUnidades;
				
				if (possuiProvaFinal)
					numCampos+= 2;
				
				float[] widths = new float[numCampos];
				
				int campoAtual = 0;
				
				widths[campoAtual++] = 0.025f;
				widths[campoAtual++] = 0.1f;
				widths[campoAtual++] = 0.38f;	
				
				/* adiciona os campos das unidades */
				for (RegraNota regra : regras){
					if (!regra.isProvaFinal()){
						float width = 0.65f;
						if (regra.isRecuperacao())
							width = 0.3f;
						widths[campoAtual++] = width / numUnidades;
						widths[campoAtual++] = width / numUnidades;
					}
				}
				
				widths[campoAtual++] = 0.06f;
				if (possuiProvaFinal)
					widths[campoAtual++] = 0.05f;				
				widths[campoAtual++] = 0.05f;				
				widths[campoAtual++] = 0.05f;
				widths[campoAtual++] = 0.05f;
				
				PdfPTable table = new PdfPTable(widths);
				
				// Cabeçalhos
				PdfPCell numCell = buildCell("",7,Element.ALIGN_RIGHT,true);
				numCell.setRowspan(2);
				table.addCell(numCell);
				
				PdfPCell matCell = buildCell("Matrícula",7,Element.ALIGN_CENTER, true);
				matCell.setRowspan(2);
				table.addCell(matCell);
				
				PdfPCell nomeCell = buildCell("Nome",8,Element.ALIGN_LEFT, true);
				nomeCell.setRowspan(2);
				table.addCell(nomeCell);
				
				for (RegraNota regra : regras){
					if (!regra.isProvaFinal()){
						PdfPCell unCell = buildCell(regra.getTitulo(),7,Element.ALIGN_CENTER,true);
						unCell.setColspan(2);
						table.addCell(unCell);
					}
				}			

				PdfPCell medPCell = buildCell("Parcial",8,Element.ALIGN_CENTER,true);
				medPCell.setRowspan(2);
				table.addCell(medPCell);
				
				if (possuiProvaFinal){
					PdfPCell finalCell = buildCell("Rec.",8,Element.ALIGN_CENTER,true);
					finalCell.setRowspan(2);
					table.addCell(finalCell);					
				}
				
				PdfPCell medFCell = buildCell("Média",8,Element.ALIGN_CENTER,true);
				medFCell.setRowspan(2);
				table.addCell(medFCell);
				
				PdfPCell falCell = buildCell("Faltas",8,Element.ALIGN_CENTER, true);
				falCell.setRowspan(2);
				table.addCell(falCell);
				
				PdfPCell sitCell = buildCell("Sit.",8,Element.ALIGN_CENTER, true);
				sitCell.setRowspan(2);
				table.addCell(sitCell);
				
				/* define o cabeçalho para cada regra */
				for (RegraNota regra : regras){

					if (!regra.isProvaFinal()){
						PdfPCell medCell = buildCell("Média",8,Element.ALIGN_CENTER,true);
						
						if (!regra.isNota()){
							medCell = buildCell("",8,Element.ALIGN_CENTER,true);
							medCell.setColspan(2);
						}
	
						table.addCell(medCell);
						
						if (regra.isNota())
							table.addCell(buildCell("Faltas",8,Element.ALIGN_CENTER,true));
					}
					
				}
				
				if ( turma.getMatriculasDisciplina() == null || turma.getMatriculasDisciplina().isEmpty() )
					turma.setMatriculasDisciplina(matriculas);
				
				/* retorna a estratégia atual, conforme a turma informada */
				EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
				EstrategiaConsolidacao estrategia = factory.getEstrategia(turma, param);				
				
				int j = 0;
				for (MatriculaComponente matricula : matriculas) {

					//estratégia
					matricula.setEstrategia(estrategia);
					
					// No. de ordem
					table.addCell(buildCell(String.valueOf(++j), true));
					// Matrícula
					table.addCell(buildCell(String.valueOf(matricula.getDiscente().getMatricula()), 8, Element.ALIGN_CENTER, true));
					// Nome
					table.addCell(buildCell(matricula.getDiscente().getNome(), true));
					
					int numUndPrint = 0;
					// monta as notas para cara regra de unidade
					for (RegraNota regra : regras){
					
						if (!regra.isProvaFinal()){
							// pega todas as notas da série e turma e da regra da unidade corrente
							List<NotaSerie> notasSerie = notaDao.findNotasByMatriculaComponente(matricula, regra);
						
							if (notasSerie != null && !notasSerie.isEmpty()) {
								
								for (NotaSerie nota : notasSerie) {
									
									PdfPCell medCell = buildCell("", 8, Element.ALIGN_CENTER, true);
									if (nota.getNotaUnidade().getNota() != null)
										medCell = buildCell(Formatador.getInstance().formatarDecimal1(nota.getNotaUnidade().getNota()), 8, Element.ALIGN_CENTER, true);
									
									if (!nota.getRegraNota().isNota())
										medCell.setColspan(2);
									
									table.addCell(medCell);
									
									if (nota.getRegraNota().isNota() && nota.getNotaUnidade().getFaltas() > 0)
										table.addCell(buildCell(Formatador.getInstance().formatarDecimal1(nota.getNotaUnidade().getFaltas()), 8, Element.ALIGN_CENTER, true));
									else if (nota.getRegraNota().isNota())
										table.addCell("");
									
									numUndPrint++;
								}								
								
							} else {
								
								//caso não possua nota imprime o campo vazio
								
								PdfPCell medCell = buildCell("", 8, Element.ALIGN_CENTER, true);
								
								if (!regra.isNota()) // senão for nota imprime somente a média 
									medCell.setColspan(2);							
								else {
									table.addCell("");
								}
								
								table.addCell(medCell);
								
								numUndPrint++;
								
							}
						} 
					}
					
					// completa os campos que não fora impressos
					if (possuiProvaFinal)
						numUndPrint++;
					
					if (numUndPrint < regras.size()){
						for (int i = 1; i <= (regras.size() - numUndPrint); i++)
							table.addCell("");		
					}
					
					// Média 
					if (matricula.getMediaParcial() != null) {
						table.addCell(buildCell(String.valueOf( matricula.getMediaParcial() ), 8, Element.ALIGN_CENTER, true));
					} else {
						table.addCell("");
					}					
					
					// Recuperação final
					if (possuiProvaFinal && matricula.getRecuperacao() != null && turma.getSituacaoTurma().getId() == SituacaoTurma.CONSOLIDADA){
						PdfPCell finalCell =  buildCell(String.valueOf( matricula.getRecuperacao() ), 8, Element.ALIGN_CENTER, true);
						table.addCell(finalCell);
					} else {
						table.addCell("");							
					}
					
					// Média Final
					if (matricula.getMediaFinal() != null && turma.getSituacaoTurma().getId() == SituacaoTurma.CONSOLIDADA) {
						table.addCell(buildCell(matricula.getMediaFinalDesc(), 8, Element.ALIGN_CENTER, true));
					} else {
						table.addCell("");
					}
					
					// Número de Faltas
					if (turma.getSituacaoTurma().getId() == SituacaoTurma.CONSOLIDADA)
						table.addCell(buildCell(matricula.getNumeroFaltas() == null ? "" : String.valueOf(matricula.getNumeroFaltas()), 8, Element.ALIGN_CENTER, true));
					else
						table.addCell("");
					
					// Situação da Matrícula
					if (turma.getSituacaoTurma().getId() == SituacaoTurma.CONSOLIDADA)
						table.addCell(buildCell(matricula.getSituacaoAbrev(), 8, Element.ALIGN_CENTER, true));
					else 
						table.addCell("");
				}
				
				table.setWidthPercentage(100);
				elementos.add(table);
			}
			return elementos;
		} finally {
			if (notaDao != null)
				notaDao.close();
		}
	}

	/**
	 *  Realiza cálculos para criação do mapa de frequência de uma turma.
	 * @param matriculas
	 * @param document
	 * @throws Exception
	 */
	private List <Object> adicionarMapaFrequencia(Collection<MatriculaComponente> matriculas) throws Exception {
		
		TurmaVirtualDao dao = null;
		TopicoAulaDao topDao = null;
		FrequenciaAlunoDao freqDao = null;
		CalendarioRegraDao calRegraDao = null;
		
		try{
			
			PdfPTable table;
			
			List <Object> elementos = new ArrayList <Object> ();
	
			dao = getDAO(TurmaVirtualDao.class);
			topDao = getDAO(TopicoAulaDao.class);
			freqDao = getDAO(FrequenciaAlunoDao.class);
			calRegraDao = getDAO(CalendarioRegraDao.class);
			
			Collection<CalendarioRegra> calRegras = calRegraDao.findByCalendario(calendarioVigente);
			if (ValidatorUtil.isNotEmpty(calRegras)){
				
				feriados = TurmaUtil.getFeriados(turma);
				// Busca todas as aulas que o professor cancelou
				List<Date> aulasCanceladas = topDao.findDatasDeTopicosSemAula(turma.getId());
				
				// Lista de Frequencia
				Set<Date> datasSet = new TreeSet<Date>();
				datasSet.addAll(TurmaUtil.getDatasAulasTruncate(turma, calendarioVigente));
				
				List<Date> datas = CollectionUtils.toArrayList(datasSet);
				Collections.sort((List<Date>) datas, new Comparator<Date>() {
					public int compare(Date o1, Date o2) {
						return (o1.getTime() - o2.getTime()) > 0 ? 1 : 0;
					}
				});
				
				mapa = freqDao.findMatriculaFrequenciasByTurma(turma);
				
				// percorre por todas as regras definidas no calendário, assim dividindo o 
				// mapa por unidade. Ex.: 1º Bimestre, 2º Bimestre.. cada um em uma folha
				for (CalendarioRegra cal : calRegras){
					
					if (cal.getDataInicio() == null || cal.getDataFim() == null)
						throw new NegocioException("Não foram definidas todas as datas das unidades no calendário acadêmico.");
					
					List<Date> datasUnidade = new ArrayList<Date>();
					for (Date data : datas){
						if (cal.getDataInicio().getTime() <= data.getTime() &&
								cal.getDataFim().getTime() >= data.getTime())
							datasUnidade.add(data);
					}
	
					int numPaginas = 1;
					if (!isEmpty(datasUnidade)) {
						numPaginas = datasUnidade.size() / TOTAL_DATAS;
						if (datasUnidade.size() % TOTAL_DATAS != 0)
							numPaginas++;
					} else {
						//throw new NegocioException("A turma não está com os horários definidos.");
						// Se não possui horários definidos, não exibe o mapa de frequência.
						return new ArrayList <Object> ();
					}
					
					elementos.add(PageSize.A4.rotate());
					
					for (int i = 0; i < numPaginas; i++) {
						elementos.add(NOVA_PAGINA);
						
						elementos.add(buildHeader(true));
						elementos.add(buildSection("Lista de Frequência - "+cal.getRegra().getTitulo()));
			
						table = new PdfPTable(new float[] { 0.07f, 0.2f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 
								0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f,
								0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 
								0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f,
								0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f,
								0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f,
								0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f });//31
						
						// Cabecalhos
						table.addCell("");
						table.addCell("");
						
						// Monta um mapa com os meses e o número de dias que estão nesses meses
						Map<Integer, Integer> mapaMeses = new  LinkedHashMap<Integer, Integer>();
						mapaMeses.put(Integer.MAX_VALUE, 0);
						for (int l = TOTAL_DATAS * i; l < TOTAL_DATAS * i + TOTAL_DATAS; l++) {
							if (l >= datasUnidade.size()) {
								// Campos sobrando (tem espaço mas não tem aulas para preencher)
								mapaMeses.put(Integer.MAX_VALUE, mapaMeses.get(Integer.MAX_VALUE) + 1);
							} else {
								Date data = datasUnidade.get(l);
								int mes = getMes(data);
								if (mapaMeses.get(mes) == null) mapaMeses.put(mes, 0);
								mapaMeses.put(mes, mapaMeses.get(mes) + 1);
							}
						}
						
						for (Integer mes : mapaMeses.keySet()) {
							if (mes != Integer.MAX_VALUE) {
								PdfPCell cellMes = buildCell(Formatador.getInstance().formatarMes(mes - 1), 6, Element.ALIGN_CENTER, true);
								cellMes.setColspan(mapaMeses.get(mes));
								table.addCell(cellMes);
							}
						}
						
						for (int l = 0; l < mapaMeses.get(Integer.MAX_VALUE); l++) {
							table.addCell("");
						}
						
						table.addCell(buildCell("Matrícula", 6, Element.ALIGN_CENTER, true));
						table.addCell(buildCell("Nome", 6, Element.ALIGN_LEFT, true));
						for (int l = 0; l < TOTAL_DATAS; l++) {
							int indice = TOTAL_DATAS * i + l;
							if (indice >= datasUnidade.size())
								table.addCell("");
							else
								table.addCell(buildHeaderData(datasUnidade.get(indice)));
						}
						
						//j = 0;
						if (matriculas != null) {
							for (MatriculaComponente matricula : matriculas) {
								table.addCell(buildCell(String.valueOf(matricula.getDiscente().getMatricula()), 6, Element.ALIGN_CENTER, true));
								table.addCell(buildCell(matricula.getDiscente().getNome(), 6, Element.ALIGN_LEFT, true));
								
								for (int l = TOTAL_DATAS * i; l < TOTAL_DATAS * i + TOTAL_DATAS; l++) {
									if (l >= datasUnidade.size()) {
										table.addCell("");
									} else {
										int faltas = frequentou(matricula.getDiscente().getId(), datasUnidade.get(l), mapa);
										PdfPCell cell = null;
										if (faltas == 0)
											cell = buildCell("*", 6, Element.ALIGN_CENTER, true);
										else if (faltas > 0)
											cell = buildCell(String.valueOf(faltas), 6, Element.ALIGN_CENTER, true);
										else if (matricula.isTrancado()) {
											
											Date dataTrancamento = dao.findDataTrancamento(matricula.getDiscente().getId(), matricula.getTurma().getId());
											if ( datasUnidade.get(l).equals(dataTrancamento) || datasUnidade.get(l).after(dataTrancamento) )
												cell = buildCellTrancado("T", true);
											else
												cell = buildCell("", false);
										}
										else
											cell = buildCell("", false);
										
										/** colocando a coluna dos fériados na cor cinza */
										if( feriados != null && feriados.contains(datasUnidade.get(l)) )
											cell.setBackgroundColor(Color.LIGHT_GRAY);
										
										if( aulasCanceladas != null && aulasCanceladas.contains(datasUnidade.get(l)) )
											cell.setBackgroundColor(Color.LIGHT_GRAY);
										
										table.addCell( cell );
									}
								}
							}
						}
						
						table.setWidthPercentage(100);
						elementos.add(table);
					}
				}
			}
			return elementos;
		} finally {
			if ( dao != null )
				dao.close();
			if ( topDao != null )
				topDao.close();
			if (freqDao != null)
				freqDao.close();
			if (calRegraDao != null)
				calRegraDao.close();
		}
	}
	
	/**
	 * Identifica se um aluno frequentou a aula em uma determinada data,
	 * retornando o número de faltas do aluno para essa data.
	 * @param discente
	 * @param data
	 * @param frequencias
	 * @return
	 */
	private int frequentou(int discente, Date data, Map<Integer, Set<FrequenciaAluno>> frequencias) {
		Set<FrequenciaAluno> freqAluno = frequencias.get(discente);
		if (freqAluno != null) {
			for (FrequenciaAluno f : freqAluno) {
				
				if (DateUtils.isSameDay(f.getData(), data))
					return f.getFaltas();
			}
		}
		return -1;
	}

	/**
	 * Constrói o cabecalho de cada seção 
	 * 
	 * @param paisagem
	 * @return
	 * @throws Exception
	 */
	private Element buildHeader(boolean paisagem) throws Exception {
		PdfPTable table = null;
		
		if (!paisagem)
			table = new PdfPTable(new float[] { 0.22f, 0.65f, 0.13f });
		else
			table = new PdfPTable(new float[] { 0.15f, 0.76f, 0.09f });
		
		PdfPCell cell = new PdfPCell();
		cell.addElement(Image.getInstance(ufrnImg));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(0);
		
		table.addCell(cell);
		
		cell = new PdfPCell();
		cell.setExtraParagraphSpace(0);
		cell.setPadding(0);
		cell.setPaddingLeft(10);
		cell.setBorder(0);
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		
		Paragraph p = null;
		
		String siglaNomeInstituicao = RepositorioDadosInstitucionais.getSiglaNomeInstituicao();
		
		p = new Paragraph( siglaNomeInstituicao + "\n"
				+ turma.getDisciplina().getUnidade().getUnidadeResponsavel().getNome() + "\n"
				+ turma.getDisciplina().getUnidade().getNome(), 
				FontFactory.getFont("Verdana", 10, Font.BOLD));
		
		p.setSpacingBefore(0);
		p.setSpacingAfter(0);
		cell.addElement(p);
				
		table.addCell(cell);
		
		cell = new PdfPCell();
		cell.addElement(Image.getInstance(sinfoImg));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(0);
		
		table.addCell(cell);
		
		table.setWidthPercentage(100);
		return table;
	}
	
	/**
	 * Imprime o nome da sessão juntamente com os dados da turma
	 * @param nome
	 * @return
	 * @throws Exception
	 */
	private Element buildSection(String nome) throws Exception {
		PdfPTable table = new PdfPTable(1);
		
		PdfPCell cell = new PdfPCell(new Paragraph(nome, FontFactory.getFont("Verdana", 10, Font.BOLD)));
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setBorder(0);
		cell.setPaddingBottom(5);
		cell.setPaddingTop(5);
		cell.setBorderWidthBottom(1);
		table.addCell(cell);		
		
		cell = new PdfPCell(new Paragraph("Turma: " + turma.getCodigo() + " - " + turma.getAnoPeriodo() 
				+ "    Status: " + turma.getSituacaoTurma() + (turma.getDescricaoHorario() != null ? "    Horário: " + turma.getDescricaoHorario() : "") + "\n"
				+ "Disciplina: " + turma.getDisciplina().getCodigoNome(), 
				FontFactory.getFont("Verdana", 8)));
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setPaddingBottom(5);
		cell.setPaddingTop(5);
		cell.setBorder(0);
		table.addCell(cell);
		
		table.setWidthPercentage(100);
		return table;
	}
	
	/**
	 *  Retorna uma célula com um texto com formatação de header da tabela
	 * @param nome
	 * @return
	 */
	private PdfPCell buildHeader(String nome) {
		PdfPCell cell = new PdfPCell(new Paragraph(nome, FontFactory.getFont("Verdana", 8)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return cell;
	}
	
	/**
	 *  Retorna uma célula com texto em fonte verdana, tamanho 8 e alinhamento a esquerda
	 * @param texto
	 * @return
	 */
	private PdfPCell buildCell(String texto, boolean logar) {
		return buildCell(texto, 8, Element.ALIGN_LEFT, logar);
	}
	
	/**
	 *  Retorna uma célula com a formatação adequada para quando a matrícula do aluno está trancada.
	 * @param texto
	 * @return
	 */
	private PdfPCell buildCellTrancado(String texto, boolean logar) {
		return buildCell(texto, 6, Element.ALIGN_LEFT, logar);
	}
	
	/**
	 *  Retorna uma célula com texto em fonte verdana, mas tamanho e alinhamento configuráveis
	 * @param texto
	 * @param tamanho
	 * @param alinhamento
	 * @return
	 */
	private PdfPCell buildCell(String texto, int tamanho, int alinhamento, boolean logar) {
		if (logar)
			stringCodigo += texto;
		
		PdfPCell cell = new PdfPCell(new Paragraph(texto, FontFactory.getFont("Verdana", tamanho)));
		cell.setHorizontalAlignment(alinhamento);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return cell;
	}
	
	/**
	 * Retorna uma célula com texto em fonte verdana, mas tamanho, borda e alinhamento configuráveis
	 * @param texto
	 * @param tamanho
	 * @param alinhamento
	 * @param borda
	 * @param font
	 * @return
	 */
	private PdfPCell buildCell(String texto, int tamanho, int alinhamento, int borda, int font, boolean logar) {
		if (logar)
			stringCodigo += texto;
		
		PdfPCell cell = new PdfPCell(new Paragraph(texto, FontFactory.getFont("Verdana", tamanho, font)));
		cell.setHorizontalAlignment(alinhamento);
		cell.setBorderWidth(borda);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return cell;
	}
	
	/**
	 * Cria uma célula para a informação de datas no cabeçalho das
	 * tabelas do diário de turma.
	 */
	public PdfPCell buildHeaderData(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		PdfPCell cell;
		cell = buildCell(String.valueOf(c.get(Calendar.DAY_OF_MONTH)), 6, Element.ALIGN_CENTER, true);
		
		cell.setPadding(0);
		// colocando a coluna dos feriados na cor cinza
		if( feriados != null && feriados.contains(data) )
			cell.setBackgroundColor(Color.LIGHT_GRAY);
		
		return cell;
	}
	
	/**
	 * Retorna o mês de um objeto data.
	 * @param data
	 * @return
	 */
	public int getMes(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		return c.get(Calendar.MONTH) + 1;
	}

	/**
	 * Retorna o código hash para o documento gerado.
	 * @return
	 */
	public String getStringCodigo() {
		return stringCodigo;
	}
	
	/**
	 * Retorna um DAO de uma classe.
	 * @param dao
	 * @return
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}
}