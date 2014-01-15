/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/10/2012
 *
 */
package br.ufrn.sigaa.ensino.infantil.jsf;

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
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.infantil.FormularioEvolucaoDao;
import br.ufrn.sigaa.arq.dao.ensino.infantil.TurmaInfantilDao;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.DiarioClasse;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.infantil.dominio.Area;
import br.ufrn.sigaa.ensino.util.TurmaUtil;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Classe para gerar o diário de classe de uma série de ensino infantil.
 * 
 * @author Diego Jácome
 *
 */
public class GerarDiarioClasseInfantil {

	/** Parâmetro que indica que indica o total de datas exibidas na planilha de frequência. */
	public static final int TOTAL_DATAS = 31;
	
	/** Turma do diário gerado. */
	private Turma turma;
	
	/** Imagem do logo da UFRN */
	private URL ufrnImg = getClass().getResource("/br/ufrn/sigaa/relatorios/fontes/ufrn.gif"); 

	/** Imagem do logo da SINFO */
	private URL sinfoImg = getClass().getResource("/br/ufrn/sigaa/relatorios/fontes/logosuper.gif"); 
		
	/** Objeto que mapeia o Id do discente com suas frequências. */
	private Map<Integer, Set<FrequenciaAluno>> mapa;
	
	/** Lista que guarda os feriados */
	private List<Date> feriados;

	/**  Constante para criação de uma única página. */
	public static final Integer NOVA_PAGINA = 1;
	
	/** Contém todas as informações do diário concatenadas. */
	private String stringCodigo;
	
	/** Construtor padrão */
	public GerarDiarioClasseInfantil(Turma turma) {
		this.turma = turma;
	}	
	
	/**
	 * Gera mapa de frequência em PDF para uma turma. 
	 * @param os
	 * @throws Exception
	 */
	public void gerarMapa(OutputStream os) throws Exception {

		Document document = new Document(PageSize.A4, 25, 25, 30, 30);
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
		
		// Lista de Alunos
		es.addAll(adicionarAlunos(matriculas));
		
		// Mapa de Frequência 
		es.addAll(adicionarMapaFrequencia(matriculas));
		
		if (isFundamental()){
			// Registro dos Conteúdos Vivenciados
			es.addAll(adicionarRegistroConteudo());
			
			// Notas dos Alunos
			es.addAll(adicionarNotas(matriculas));
		}
		
		if (!isFundamental()){
			// Conteúdo Programático
			es.addAll(adicionarPlanejamento());
		}
		
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
		
		TurmaInfantilDao turmaDao = getDAO(TurmaInfantilDao.class);
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
	 * Adiciona a capa ao diário.
	 * @param document
	 * @throws Exception
	 */
	private List <Object> adicionarCapa() throws Exception{
		
		DocenteTurmaDao dDao = null;
		
		try {
			dDao = getDAO(DocenteTurmaDao.class);
			
			List <Object> elementos = new ArrayList <Object> ();
			
			elementos.add(buildHeader(false));
			
			PdfPTable table = new PdfPTable(1);
			PdfPCell cell = new PdfPCell(new Paragraph("Diário de Turma", FontFactory.getFont("Verdana", 22, Font.BOLD)));
			cell.setBorder(0);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPaddingTop(140);
			cell.setPaddingBottom(100);
			table.addCell(cell);
			elementos.add(table);
						
			List<DocenteTurma> dts = null;
			dts = CollectionUtils.toList(turma.getDocentesTurmas());
			
			table = new PdfPTable(new float[] { 0.3f, 0.7f });
			table.addCell(buildCell("Professor(es):", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			boolean first = true;
			for (DocenteTurma d : dts) {
				if (!isEmpty(d.getDocenteExterno())) {
					// Docente Externo
					if (!first)
						table.addCell(buildCell("", 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
					table.addCell(buildCell(d.getDocenteExterno().getNome(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
				} else {
					if (!first)
						table.addCell(buildCell("", 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
					table.addCell(buildCell(d.getDocente().getNome(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
				}
				first = false;
			}
			table.addCell(buildCell("Turma: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			table.addCell(buildCell(turma.getDescricaoTurmaInfantilResumida(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			table.addCell(buildCell("Turno: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			if (turma.getDescricaoHorario().equals("M"))
				table.addCell(buildCell("Manhã", 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			if (turma.getDescricaoHorario().equals("T"))
				table.addCell(buildCell("Tarde", 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			elementos.add(table);
			
			Paragraph p = new Paragraph("");
			p.setSpacingBefore(100);
			elementos.add(p);
			
			table = new PdfPTable(1);
			table.addCell(buildCell(turma.getAnoPeriodo(), 11, Element.ALIGN_CENTER, 0, Font.BOLD, true));
			elementos.add(table);
			
			elementos.add(p);
					
			return elementos;
		} finally {
			if (dDao != null)
				dDao.close();
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
	private List <Object> adicionarAlunos(Collection<MatriculaComponente> matriculas) throws Exception{
					
		List <Object> elementos = new ArrayList <Object> ();
		
		elementos.add(NOVA_PAGINA);
		
		elementos.add(buildHeader(false));
		elementos.add(buildSection("Lista de Alunos"));
				
		float[] widths = new float[6];
		
		int campoAtual = 0;

		widths[campoAtual++] = 0.01f;
		widths[campoAtual++] = 0.04f;
		widths[campoAtual++] = 0.1f;
		widths[campoAtual++] = 0.1f;
		widths[campoAtual++] = 0.03f;
		widths[campoAtual++] = 0.03f;
		
		PdfPTable table = new PdfPTable(widths);
		table.setSpacingBefore(10);
		// Cabeçalhos
		table.addCell("");
		table.addCell(buildHeader("Matrícula"));
		table.addCell(buildCell("Aluno",8,Element.ALIGN_LEFT,true));
		table.addCell(buildCell("Filiação",8,Element.ALIGN_LEFT,true));
		table.addCell(buildCell("Nascimento",8,Element.ALIGN_LEFT,true));
		table.addCell(buildCell("Telefone",8,Element.ALIGN_LEFT,true));

		
		int j = 0;
		if (matriculas != null) {
			
			if ( turma.getMatriculasDisciplina() == null || turma.getMatriculasDisciplina().isEmpty() )
				turma.setMatriculasDisciplina(matriculas);
			
			for (MatriculaComponente matricula : matriculas) {
				// No. de ordem
				table.addCell(buildCell(String.valueOf(++j), true));
				// Matrícula
				table.addCell(buildCell(String.valueOf(matricula.getDiscente().getMatricula()), 8, Element.ALIGN_CENTER, true));
				// Nome
				table.addCell(buildCell(matricula.getDiscente().getNome(), true));
				// Filiação
				table.addCell(buildCell(matricula.getDiscente().getPessoa().getNomeMae()+"\n"+matricula.getDiscente().getPessoa().getNomePai(), true));
				// Nascimento
				table.addCell(buildCell(matricula.getDiscente().getPessoa().getDataNascimento().toString(), true));
				// Telefone
				table.addCell(buildCell(matricula.getDiscente().getPessoa().getTelefone(), true));
	
			}
		}

		table.setWidthPercentage(100);
		elementos.add(table);
		
		return elementos;
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
		
		try{
			
			PdfPTable table;
			
			List <Object> elementos = new ArrayList <Object> ();
	
			dao = getDAO(TurmaVirtualDao.class);
			topDao = getDAO(TopicoAulaDao.class);
			
			feriados = TurmaUtil.getFeriados(turma);
			// Busca todas as aulas que o professor cancelou
			List<Date> aulasCanceladas = topDao.findDatasDeTopicosSemAula(turma.getId());
			
			// Lista de Frequencia
			Set<Date> datasSet = new TreeSet<Date>();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			for ( int i = 1 ; i<=cal.getMaximum(Calendar.DAY_OF_MONTH) ; i++ ){
				datasSet.add(cal.getTime());
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			List<Date> datas = CollectionUtils.toArrayList(datasSet);
			
			int numPaginas = 1;
			
			freqDao = getDAO(FrequenciaAlunoDao.class);
			if (isEmpty(turma.getSubturmas()))
				mapa = freqDao.findMatriculaFrequenciasByTurma(turma);
			else
				mapa = freqDao.findMatriculaFrequenciasBySubTurmas(turma);
						
			for (int i = 0; i < numPaginas; i++) {
				elementos.add(NOVA_PAGINA);
				
				elementos.add(buildHeader(true));
				elementos.add(buildSection("Lista de Freqüência"));
	
				table = new PdfPTable(new float[] { 0.05f, 0.2f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 
						0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f,
						0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 
						0.01f });//31
				
				// Cabecalhos
				table.setSpacingBefore(10);
				table.addCell("");
				table.addCell("");
				
				// Monta um mapa com os meses e o número de dias que estão nesses meses
				Map<Integer, Integer> mapaMeses = new TreeMap<Integer, Integer>();
				mapaMeses.put(Integer.MAX_VALUE, 0);
				for (int l = TOTAL_DATAS * i; l < TOTAL_DATAS * i + TOTAL_DATAS; l++) {
					if (l >= datas.size()) {
						// Campos sobrando (tem espaço mas não tem aulas para preencher)
						mapaMeses.put(Integer.MAX_VALUE, mapaMeses.get(Integer.MAX_VALUE) + 1);
					} else {
						Date data = datas.get(l);
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
					if (indice >= datas.size())
						table.addCell("");
					else
						table.addCell(buildHeaderData(datas.get(indice)));
				}
				
				//j = 0;
				if (matriculas != null) {
					for (MatriculaComponente matricula : matriculas) {
						table.addCell(buildCell(String.valueOf(matricula.getDiscente().getMatricula()), 6, Element.ALIGN_CENTER, true));
						table.addCell(buildCell(matricula.getDiscente().getNome(), 6, Element.ALIGN_LEFT, true));
						
						for (int l = TOTAL_DATAS * i; l < TOTAL_DATAS * i + TOTAL_DATAS; l++) {
							if (l >= datas.size()) {
								table.addCell("");
							} else {
								int faltas = frequentou(matricula.getDiscente().getId(), datas.get(l), mapa);
								PdfPCell cell = null;
								if (faltas == 0)
									cell = buildCell("*", 6, Element.ALIGN_CENTER, true);
								else if (faltas > 0)
									cell = buildCell(String.valueOf(faltas), 6, Element.ALIGN_CENTER, true);
								else if (matricula.isTrancado()) {
									
									Date dataTrancamento = dao.findDataTrancamento(matricula.getDiscente().getId(), matricula.getTurma().getId());
									if ( datas.get(l).equals(dataTrancamento) || datas.get(l).after(dataTrancamento) )
										cell = buildCellTrancado("T", true);
									else
										cell = buildCell("", false);
								}
								else
									cell = buildCell("", false);
								
								/** colocando a coluna dos fériados na cor cinza */
								if( feriados != null && feriados.contains(datas.get(l)) )
									cell.setBackgroundColor(Color.LIGHT_GRAY);
								
								if( aulasCanceladas != null && aulasCanceladas.contains(datas.get(l)) )
									cell.setBackgroundColor(Color.LIGHT_GRAY);
								
								table.addCell( cell );
							}
						}
					}
				}
				table.setWidthPercentage(100);
				elementos.add(table);
			}
			
			if (isFundamental())
				addAssinatura(elementos);
			
			return elementos;
		} finally {
			if ( dao != null )
				dao.close();
			if ( topDao != null )
				topDao.close();
			if ( freqDao != null )
				freqDao.close();
		}
	}
	
	/**
	 *  Realiza cálculos para criação do mapa de frequência de uma turma.
	 * @param matriculas
	 * @param document
	 * @throws Exception
	 */
	private List <Object> adicionarRegistroConteudo() throws Exception {
		
		TurmaVirtualDao dao = null;
		TopicoAulaDao topDao = null;
		FormularioEvolucaoDao fDao = null;
		
		try{
			
			PdfPTable table;
			
			List <Object> elementos = new ArrayList <Object> ();
	
			dao = getDAO(TurmaVirtualDao.class);
			topDao = getDAO(TopicoAulaDao.class);
			fDao = getDAO(FormularioEvolucaoDao.class);
			
			List<Area> areas = fDao.findAreasByComponente(turma.getDisciplina().getId());

			elementos.add(NOVA_PAGINA);
			elementos.add(buildHeader(true));
			elementos.add(buildSection("Registros dos Conteúdos Vivenciados"));
			
			table = new PdfPTable(new float[] { 0.4f, 0.8f });
			table.setSpacingBefore(10);
			final int rowspan = 4;
			// Cabecalhos
			table.addCell(buildCell("ÁREA DE CONHECIMENTO", 9, Element.ALIGN_CENTER, true));
			table.addCell(buildCell("DESCRIÇÃO", 9, Element.ALIGN_CENTER,  true));
			
			for (Area a : areas) {
				PdfPCell cell = buildCell(a.getDescricao(), 6, Element.ALIGN_CENTER, true);
				cell.setRowspan(rowspan);
				table.addCell(cell);
				for (int i = 0 ; i<rowspan ; i++){
					PdfPCell cellLinha = buildCell("", 6, Element.ALIGN_CENTER, true);
					cellLinha.setPadding(10);
					table.addCell(cellLinha);
				}	
			}
			
										
			table.setWidthPercentage(100);
			elementos.add(table);
	
			addAssinatura(elementos);
			
			return elementos;
		} finally {
			if ( dao != null )
				dao.close();
			if ( topDao != null )
				topDao.close();
			if ( fDao != null )
				fDao.close();
		}
	}
	
	/**
	 *  Realiza cálculos para criação do mapa de frequência de uma turma.
	 * @param matriculas
	 * @param document
	 * @throws Exception
	 */
	private List <Object> adicionarNotas(Collection<MatriculaComponente> matriculas) throws Exception {
		
		TurmaVirtualDao dao = null;
		TopicoAulaDao topDao = null;
		FormularioEvolucaoDao fDao = null;
		
		try{
			
			PdfPTable table;
			
			List <Object> elementos = new ArrayList <Object> ();
	
			dao = getDAO(TurmaVirtualDao.class);
			topDao = getDAO(TopicoAulaDao.class);
			fDao = getDAO(FormularioEvolucaoDao.class);
			
			List<Area> areas = fDao.findAreasByComponente(turma.getDisciplina().getId());
					
			int numPaginas = 1;
									
			for (int i = 0; i < numPaginas; i++) {
				elementos.add(NOVA_PAGINA);
				
				elementos.add(buildHeader(true));
				elementos.add(buildSection("Lista de Notas"));
	
				table = new PdfPTable(1);
				
				table = new PdfPTable(new float[] { 0.5f, 0.5f });
				PdfPCell cell1 = buildCell("PARECER PARCIAL:____________", 7, Element.ALIGN_CENTER, 0, Font.NORMAL, false);
				cell1.setPaddingTop(10);
				cell1.setPaddingBottom(10);
				table.addCell(cell1);
				PdfPCell cell2 = buildCell("BIMESTRE:_________________", 7, Element.ALIGN_CENTER, 0, Font.NORMAL, true);
				cell2.setPaddingTop(10);
				cell2.setPaddingBottom(10);
				table.addCell(cell2);
				elementos.add(table);
				
				float[] tf = new float[areas.size()+4];
				int linha = 0;
				tf[linha++] = 0.005f;
				tf[linha++] = 0.015f;
				tf[linha++] = 0.04f;
				for (int j = 0; j < areas.size() ; j++){
					tf[linha++] = 0.005f;
				}
				tf[linha++] = 0.007f;
				table = new PdfPTable(tf);
				
				// Cabecalhos
				table.addCell("");
				table.addCell("");
				table.addCell("");
				PdfPCell cellArea = buildCell("CONCEITO", 6, Element.ALIGN_CENTER, true);
				cellArea.setColspan(areas.size());
				table.addCell(cellArea);
				table.addCell("");
				
				table.addCell(buildCell("", 6, Element.ALIGN_CENTER, true));			
				table.addCell(buildCell("Matrícula", 6, Element.ALIGN_CENTER, true));
				table.addCell(buildCell("Nome", 6, Element.ALIGN_LEFT, true));
				for (Area a : areas) {
					PdfPCell cell = buildCell(a.getDescricao(), 6, Element.ALIGN_CENTER, true);
					cell.setPadding(0);
					cell.setRotation(90);
					table.addCell(cell);
				}
				table.addCell(buildCell("Faltas", 6, Element.ALIGN_CENTER, true));
				
				if (matriculas != null) {
					linha = 1;
					for (MatriculaComponente matricula : matriculas) {
						table.addCell(buildCell(String.valueOf(linha++), 6, Element.ALIGN_CENTER, true));
						table.addCell(buildCell(String.valueOf(matricula.getDiscente().getMatricula()), 6, Element.ALIGN_CENTER, true));
						table.addCell(buildCell(matricula.getDiscente().getNome(), 6, Element.ALIGN_LEFT, true));
						for (int j=0; j<areas.size(); j++) {
							table.addCell("");
						}
						table.addCell("");
					}
				}	
								
				table.setWidthPercentage(100);
				elementos.add(table);
				
				table = new PdfPTable(1);
				PdfPCell cell = new PdfPCell(new Paragraph("LEGENDA", FontFactory.getFont("Verdana", 7, Font.NORMAL)));
				cell.setBorder(0);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setPaddingTop(20);
				cell.setPaddingBottom(5);
				table.addCell(cell);
				elementos.add(table);
				
				table = new PdfPTable(new float[] { 0.1f, 0.5f });
				table.addCell(buildCell("A", 7, Element.ALIGN_LEFT, 1, Font.NORMAL, false));
				table.addCell(buildCell("O Aluno demonstrou ter construído as competências e habilidades trabalhadas.", 7, Element.ALIGN_LEFT, 1, Font.NORMAL, false));
				table.addCell(buildCell("B", 7, Element.ALIGN_LEFT, 1, Font.NORMAL, false));
				table.addCell(buildCell("O Aluno demonstrou ter construído a maioria das competências e habilidades trabalhadas.", 7, Element.ALIGN_LEFT, 1, Font.NORMAL, false));
				table.addCell(buildCell("C", 7, Element.ALIGN_LEFT, 1, Font.NORMAL, false));
				table.addCell(buildCell("O Aluno apresenta-se em fase de construção da maioria das competências e habilidades trabalhadas.", 7, Element.ALIGN_LEFT, 1, Font.NORMAL, false));
				table.addCell(buildCell("D", 7, Element.ALIGN_LEFT, 1, Font.NORMAL, false));
				table.addCell(buildCell("Embora tenham sido propostas diferentes situações de aprendizagem, o aluno ainda apresenta fragilidades na maioria das competências e habilidades trabalhadas.", 7, Element.ALIGN_LEFT, 1, Font.NORMAL, false));
				elementos.add(table);
			}
			
			addAssinatura(elementos);
			
			return elementos;
		} finally {
			if ( dao != null )
				dao.close();
			if ( topDao != null )
				topDao.close();
			if ( fDao != null )
				fDao.close();
		}
	}

	private void addAssinatura(List<Object> elementos) {
		PdfPTable table;
		table = new PdfPTable(new float[] { 0.1f, 0.5f });
		PdfPCell cell1 = buildCell("___/___/___", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, false);
		cell1.setPaddingTop(10);
		table.addCell(cell1);
		PdfPCell cell2 = buildCell("_________________________", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, true);
		cell2.setPaddingTop(10);
		table.addCell(cell2);
		
		table.addCell(buildCell("Data", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, false));
		table.addCell(buildCell("Assinatura do Professor", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, true));
		elementos.add(table);
		
		table = new PdfPTable(new float[] { 0.1f, 0.5f });
		table.addCell(buildCell("", 9, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
		PdfPCell cell3 = buildCell("_________________________", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, true);
		cell3.setPaddingTop(20);
		table.addCell(cell3);
		table.addCell(buildCell("", 9, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
		table.addCell(buildCell("Assinatura do Coordenador Pedagógico", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, true));
		elementos.add(table);
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
	 * Adiciona o planejamento pedagógico do aluno.
	 * @param document
	 * @throws Exception
	 */
	private List <Object> adicionarPlanejamento() throws Exception{
		
		DocenteTurmaDao dDao = null;
		
		try {
			dDao = getDAO(DocenteTurmaDao.class);
			
			List <Object> elementos = new ArrayList <Object> ();
			
			elementos.add(NOVA_PAGINA);
			elementos.add(buildHeader(false));
			elementos.add(buildSection("Planejamento Pedagógico"));
			
			PdfPTable table = new PdfPTable(1);
			
			table = new PdfPTable(new float[] { 0.1f, 0.5f });
			PdfPCell cell1 = buildCell("___/___/___", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, false);
			cell1.setPaddingTop(60);
			table.addCell(cell1);
			PdfPCell cell2 = buildCell("_________________________", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, true);
			cell2.setPaddingTop(60);
			table.addCell(cell2);
			
			table.addCell(buildCell("Data", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, false));
			table.addCell(buildCell("Assinatura do Professor", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, true));
			elementos.add(table);
			
			table = new PdfPTable(new float[] { 0.1f, 0.5f });
			table.addCell(buildCell("", 9, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			PdfPCell cell3 = buildCell("_________________________", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, true);
			cell3.setPaddingTop(20);
			table.addCell(cell3);
			table.addCell(buildCell("", 9, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			table.addCell(buildCell("Assinatura do Coordenador Pedagógico", 9, Element.ALIGN_CENTER, 0, Font.NORMAL, true));
			elementos.add(table);

			String siglaNomeInstituicao = RepositorioDadosInstitucionais.getSiglaNomeInstituicao();
			Unidade u = dDao.refresh(turma.getDisciplina().getUnidade());
			
			table = new PdfPTable(new float[] { 1f, 0.7f });
			table.setSpacingBefore(40);
			
			String dados = siglaNomeInstituicao
							+"\n"+u.getNome() 
							+"\nANO LETIVO: "+turma.getAnoPeriodo()
							+"\nBIMESTRE:_________________";
			
			table.addCell(buildCell( dados, 9, Element.ALIGN_LEFT, 0, Font.BOLD, false));
			
			String reg = "REGISTRO DOS TEMAS DE PESQUISA COM OS RESPECTIVOS CONTEÚDOS TRABALHADOS AO LONGO DO BIMESTRE";
			table.addCell(buildCell(reg, 8, Element.ALIGN_JUSTIFIED, 1, Font.BOLD, true));
			elementos.add(table);
			
			Paragraph p = new Paragraph("");
			p.setSpacingBefore(70);
			elementos.add(p);
			
			table = new PdfPTable(1);
			PdfPCell cell4 = buildCell("PLANEJAMENTO PEDAGÓGICO", 11, Element.ALIGN_CENTER, 0, Font.BOLD, true);
			cell4.setPaddingBottom(15);
			table.addCell(cell4);
			String linha = "______________________________________________________________________";
			for (int i = 0; i<25;i++)
				table.addCell(buildCell(linha, 11, Element.ALIGN_CENTER, 0, Font.BOLD, true));

			elementos.add(table);		
			
			elementos.add(p);
					
			return elementos;
		} finally {
			if (dDao != null)
				dDao.close();
		}
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
		
		cell = new PdfPCell(new Paragraph("Turma: " + turma.getDescricaoTurmaInfantilResumida() 
				+ "    Status: " + turma.getSituacaoTurma()
				+ "\nProfessores(es): " +turma.getDocentesNomes(), 
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
	 * Retorna o código hash para o documento gerado.
	 * @return
	 */
	public boolean isFundamental() {
		boolean fundamental = false;
		// TODO: Refatorar, quando o diário foi criado o único método de diferenciar se
		// uma turma é infantil ou fundamental era através do componente curricular
		if (turma.getDisciplina().getCodigo().equals("NEI0005") 
			|| turma.getDisciplina().getCodigo().equals("NEI0006") 
			|| turma.getDisciplina().getCodigo().equals("NEI0007")
			)
			fundamental = true;
		return fundamental;
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
