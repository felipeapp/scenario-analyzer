/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/07/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.awt.Color;
import java.io.OutputStream;
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

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.DiarioClasse;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;

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
 * Classe para gerar o diário de classe de uma turma.
 * 
 * @author David Pereira
 *
 */
public class GerarDiarioClasse {

	/** Parâmetro que indica que indica o total de datas exibidas na planilha de frequência. */
	public static final int TOTAL_DATAS = 75;
	
	/** Turma do diário gerado. */
	private Turma turma;
	
	/** Calendário acadêmico atual. */
	private CalendarioAcademico calendarioVigente;
	
	/** Objeto que guardo o DAO da turma. */
	private TurmaDao dao;
	
	/** Objeto que guardo o DAO de Frequência Aluno. */
	private FrequenciaAlunoDao freqDao;
	
	/** Objeto que guardo o DAO de avaliação. */
	private AvaliacaoDao avDao;	
	
	/** Objeto que guarda as informações sobre os parâmetros da unidade gestora da turma. */
	private ParametrosGestoraAcademica param;
	
	/** Objeto para formatação de datas */
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	/** Objeto que mapeia o Id do discente com suas frequências. */
	private Map<Integer, Set<FrequenciaAluno>> mapa;
	
	/** Lista que guarda os feriados */
	private List<Date> feriados;

	/** Objeto que guardo o DAO do usuário. */
	private UsuarioDao usrDao;
	
	/** Indica se o mapa de frequência deve ser adicionado no diário de classe. */
	private boolean gerarTabelaFrequencia = true;
	
	/**  Constante para criação de uma única página. */
	public static final Integer NOVA_PAGINA = 1;
	
	/** Contém todas as informações do diário concatenadas. */
	private String stringCodigo;
	
	public GerarDiarioClasse(Turma turma, CalendarioAcademico calendarioVigente, ParametrosGestoraAcademica param, TurmaDao dao, FrequenciaAlunoDao freqDao, AvaliacaoDao avDao, UsuarioDao usrDao, boolean gerarTabelaFrequencia) {
		this.turma = turma;
		this.calendarioVigente = calendarioVigente;
		this.dao = dao;
		this.param = param;
		this.freqDao = freqDao;
		this.avDao = avDao;
		this.usrDao = usrDao;
		this.gerarTabelaFrequencia = gerarTabelaFrequencia;
	}	
	
	/**
	 * Gera mapa de frequência em PDF para uma turma. 
	 * @param os
	 * @throws Exception
	 */
	public void gerarMapa(OutputStream os) throws Exception {
		Collection<MatriculaComponente> matriculas = null;
		
		if (isEmpty(turma.getSubturmas())) {
			matriculas = dao.findParticipantesTurma(turma.getId());
		} else {
			matriculas = new ArrayList<MatriculaComponente>();
			for (Turma st : turma.getSubturmas()) {
				Collection<MatriculaComponente> matriculasSubturmas = null;
				matriculasSubturmas = dao.findParticipantesTurma(st.getId());
				if ( matriculasSubturmas != null )
					matriculas.addAll(matriculasSubturmas);
			}
		}
		
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
		
		Document document = new Document(PageSize.A4.rotate(), 25, 25, 30, 30);
		PdfWriter.getInstance(document, os);
		
		document.open();

		List <Object> es = adicionarMapaFrequencia(matriculas);
		
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
		
		Collection<MatriculaComponente> matriculas = null;
		
		if (isEmpty(turma.getSubturmas())) {
			matriculas = dao.findParticipantesTurma(turma.getId(), null, true, false, 
					SituacaoMatricula.APROVADO , SituacaoMatricula.REPROVADO , SituacaoMatricula.REPROVADO_FALTA , 
					SituacaoMatricula.REPROVADO_MEDIA_FALTA , SituacaoMatricula.MATRICULADO , SituacaoMatricula.TRANCADO);
		} else {
			matriculas = new ArrayList<MatriculaComponente>();
			for (Turma st : turma.getSubturmas()) {
				Collection<MatriculaComponente> participantesSubturma =  dao.findParticipantesTurma(st.getId());
				//Evitando erro caso uma subturma não possua discentes.
				if(participantesSubturma != null) {				 
					matriculas.addAll(participantesSubturma);
				}
			}
			
			Collections.sort((List<MatriculaComponente>) matriculas, new Comparator<MatriculaComponente>() {
				public int compare(MatriculaComponente o1, MatriculaComponente o2) {
					return o1.getDiscente().getPessoa().getNomeAscii().compareToIgnoreCase(o2.getDiscente().getPessoa().getNomeAscii());
				}
			});
		}
		
		if (!isEmpty(matriculas)) {
			for (MatriculaComponente matricula : matriculas) {
				if (matricula.isTrancado()) {
					String nome = matricula.getDiscente().getPessoa().getNome();
					matricula.getDiscente().getPessoa().setNome(nome + " (TRANCADO)");
				}
				if (matricula.getDiscente().getNivel() == NivelEnsino.LATO) {
					CursoLato cursoLato = dao.findByPrimaryKey(matricula.getDiscente().getCurso().getId(), CursoLato.class);
					int metodoAvaliacao = cursoLato.getPropostaCurso().getMetodoAvaliacao();
					matricula.setMetodoAvaliacao(metodoAvaliacao);
				}
			}
		} else {
			throw new NegocioException("Não há alunos matriculados nessa turma.");
		}
		
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
		
		// Mapa de Frequência (Se for uma turma de EAD, não tem frequência, então dá uma exception que deve ser ignorada)
		try {
			if (gerarTabelaFrequencia)
				es.addAll(adicionarMapaFrequencia(matriculas));
		} catch (Exception e){
			// A turma não tem frequência.
		}

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
		
		final int LINHAS = 88;
		
		List <Object> elementos = new ArrayList <Object> ();
		
		elementos.add(PageSize.A4);
		
		List<TopicoAula> conteudos = dao.findConteudosByTurma(turma);
		Map<Date, List<TopicoAula>> mapa = new TreeMap<Date, List<TopicoAula>>();
		
		if (isEmpty(turma.getSubturmas())) {
			Set<Date> datas = TurmaUtil.getDatasAulasTruncate(turma, calendarioVigente);
			for (Date data : datas) {
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
		} else {
			Collections.sort(conteudos, new Comparator<TopicoAula>() {
				public int compare(TopicoAula o1, TopicoAula o2) {
					return o1.getData().compareTo(o2.getData());
				}
			});
			
			for (TopicoAula topico : conteudos) {
				Date dataInicioTopico = CalendarUtils.descartarHoras(topico.getData());

				if( mapa.get(dataInicioTopico) == null) mapa.put(dataInicioTopico, new ArrayList<TopicoAula>());
				mapa.get(dataInicioTopico).add(topico);
			}			
		}
		
		elementos.add(NOVA_PAGINA);
		elementos.add(buildHeader(false));
		elementos.add(buildSection("Conteúdo Programado"));
		
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
		
		// Lista os tópicos de aula de turma ead
		if ( turma.isEad() && mapa.isEmpty() ){
			for (TopicoAula aula : conteudos) {
				if (i % LINHAS == LINHAS / 2 || i == 0) {
					if (i != 0)
						mct.addElement(table);
					
						table = new PdfPTable(new float[] { 0.2f, 0.8f });
						table.setWidthPercentage(100);
						table.setWidthPercentage(100);
						table.addCell(buildHeader("Data"));
						table.addCell(buildCell("Descrição",8,Element.ALIGN_LEFT,true));
				}
				
				StringBuilder descricao = new StringBuilder();

				PdfPCell cell = buildHeader(sdf.format(aula.getData()));
				cell.setFixedHeight(15.0f);
				if (table != null)
					table.addCell(cell);
				
				descricao.append(aula.getDescricao() + "\n");				

				if (table != null)
					table.addCell(buildCell(descricao.toString(), true));
					
				i++;
			}
		}			
		
		if (table != null)
			mct.addElement(table);
		
		elementos.add(mct);
		
		return elementos;
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
			
			table = new PdfPTable(new float[] { 0.25f, 0.75f });
			table.addCell(buildCell("Centro: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			
			if (NivelEnsino.isAlgumNivelStricto(turma.getDisciplina().getNivel()))
				table.addCell(buildCell(turma.getDisciplina().getUnidade().getUnidadeResponsavel().getNome(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			else 
				table.addCell(buildCell(turma.getDisciplina().getUnidade().getGestora().getNome(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
				
			if (NivelEnsino.isAlgumNivelStricto(turma.getDisciplina().getNivel()))
				table.addCell(buildCell("Programa: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			else 
				table.addCell(buildCell("Departamento: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
				
			table.addCell(buildCell(turma.getDisciplina().getUnidade().getNome(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			table.addCell(buildCell("Código: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			table.addCell(buildCell(turma.getDisciplina().getCodigo(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			table.addCell(buildCell("Disciplina: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			table.addCell(buildCell(turma.getDisciplina().getNome(), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			table.addCell(buildCell("Créditos: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			table.addCell(buildCell(String.valueOf(turma.getDisciplina().getCrTotal()), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			table.addCell(buildCell("Carga Horária: ", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			table.addCell(buildCell(String.valueOf(turma.getDisciplina().getChTotal()), 11, Element.ALIGN_LEFT, 0, Font.BOLD, true));
			elementos.add(table);
			
			Paragraph p = new Paragraph("");
			p.setSpacingBefore(100);
			elementos.add(p);
			
			table = new PdfPTable(3);
			table.addCell(buildCell("Turma: ", 11, Element.ALIGN_CENTER, 0, Font.NORMAL, false));
			table.addCell(buildCell("Ano/Semestre: ", 11, Element.ALIGN_CENTER, 0, Font.NORMAL, false));
			table.addCell(buildCell("Horário: ", 11, Element.ALIGN_CENTER, 0, Font.NORMAL, false));
			table.addCell(buildCell(turma.getCodigo(), 11, Element.ALIGN_CENTER, 0, Font.BOLD, true));
			table.addCell(buildCell(turma.getAnoPeriodo(), 11, Element.ALIGN_CENTER, 0, Font.BOLD, true));
			table.addCell(buildCell(turma.getDescricaoHorario() == null ? "" : turma.getDescricaoHorario(), 11, Element.ALIGN_CENTER, 0, Font.BOLD, true));
			elementos.add(table);
			
			elementos.add(p);
			
			table = new PdfPTable(new float[] { 0.3f, 0.7f });
			table.addCell(buildCell("Matrícula", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			table.addCell(buildCell("Docente(s)", 11, Element.ALIGN_LEFT, 0, Font.NORMAL, false));
			
			List<DocenteTurma> dts = null;
			if (isEmpty(turma.getSubturmas())) {
				dts = CollectionUtils.toList(turma.getDocentesTurmas());
			} else {
				dts = new TurmaVirtualMBean().getDocentesTurma(turma, usrDao, dDao);
			}
			
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
	}
	
	/**
	 * Adiciona a planilha com as notas e faltas.
	 * @param document
	 * @param matriculas
	 * @throws Exception
	 */
	private List <Object> adicionarNotasEFaltas(Collection<MatriculaComponente> matriculas) throws Exception{
		
		boolean competenciaOuStricto = NivelEnsino.isAlgumNivelStricto(turma.getDisciplina().getNivel()) || param.getMetodoAvaliacao() == MetodoAvaliacao.COMPETENCIA;
		
		if ( turma.getDisciplina().isLato() )
			turma.setCurso(dao.refresh(turma.getCurso()));
			
		List <Object> elementos = new ArrayList <Object> ();
		
		elementos.add(NOVA_PAGINA);
		
		elementos.add(buildHeader(false));
		elementos.add(buildSection("Lista de Notas e Faltas"));
		
		Integer numUnidades = TurmaUtil.getNumUnidadesDisciplina(turma);
		
		if (numUnidades == null) numUnidades = Integer.valueOf(param.getQtdAvaliacoes());
		
		// Calcula quantas células vai ter a tabela
		int numCampos = 0;
		if (competenciaOuStricto) 
			numCampos = 6;
		 else
			 numCampos = 8 + numUnidades;
		
		float[] widths = new float[numCampos];
		
		int campoAtual = 0;

		widths[campoAtual++] = 0.038f;
		widths[campoAtual++] = 0.1f;
		widths[campoAtual++] = 0.44f;
		if (!competenciaOuStricto) {
			for (int i = 1; i <= numUnidades; i++) 
				widths[campoAtual++] = 0.15f / numUnidades;
	
			widths[campoAtual++] = 0.055f;
			widths[campoAtual++] = 0.057f;
		}
		if ( param.getMetodoAvaliacao() != MetodoAvaliacao.COMPETENCIA )
			widths[campoAtual++] = 0.051f;
		else
			widths[campoAtual++] = 0.1f;
		widths[campoAtual++] = 0.05f;
		widths[campoAtual++] = 0.095f;
		
		PdfPTable table = new PdfPTable(widths);
		
		// Cabeçalhos
		table.addCell("");
		table.addCell(buildHeader("Matrícula"));
		table.addCell(buildCell("Nome",8,Element.ALIGN_LEFT,true));
		if (!competenciaOuStricto) {
			for (int i = 1; i <= numUnidades; i++) 
				table.addCell(buildCell(i + "a Aval.",8,Element.ALIGN_RIGHT,true));

			table.addCell(buildCell("Média Parcial",8,Element.ALIGN_RIGHT,true));
			table.addCell(buildCell("Recup.",8,Element.ALIGN_RIGHT,true));
		}
		
		if (!competenciaOuStricto) 
			table.addCell(buildCell("Média Final",8,Element.ALIGN_RIGHT,true));
		else {
			if (NivelEnsino.isAlgumNivelStricto(turma.getDisciplina().getNivel()))
				table.addCell(buildHeader("Conceito"));
			else
				table.addCell(buildHeader("Competência"));
		}
		table.addCell(buildCell("Total Faltas",8,Element.ALIGN_RIGHT,true));
		table.addCell(buildCell("Resultado",8,Element.ALIGN_LEFT,true));
		
		int j = 0;
		if (matriculas != null) {
			
			if ( turma.getMatriculasDisciplina() == null || turma.getMatriculasDisciplina().isEmpty() )
				turma.setMatriculasDisciplina(matriculas);
			
			EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
			EstrategiaConsolidacao estrategia = factory.getEstrategia(turma, param);
			
			for (MatriculaComponente matricula : matriculas) {
				// No. de ordem
				table.addCell(buildCell(String.valueOf(++j), true));
				// Matrícula
				table.addCell(buildCell(String.valueOf(matricula.getDiscente().getMatricula()), 8, Element.ALIGN_CENTER, true));
				// Nome
				table.addCell(buildCell(matricula.getDiscente().getNome(), true));
				
				matricula.setNotas(avDao.findNotasByMatricula(matricula));
				
				if (!competenciaOuStricto) {
					// Notas das Unidades e Média Parcial
					if (matricula.getNotas() != null && !matricula.getNotas().isEmpty()) {
						int k = 0;
						String[] pesos = TurmaUtil.getArrayPesosUnidades(turma);
						for (NotaUnidade nota : matricula.getNotas()) {
							nota.setPeso(pesos[k++]);
							if (nota.getNota() != null)
								table.addCell(buildCell(Formatador.getInstance().formatarDecimal1(nota.getNota()), 8, Element.ALIGN_CENTER, true));
							else
								table.addCell("");							
						}	
						
						if (numUnidades > matricula.getNotas().size()){
							int numUnidadesRestantes = numUnidades-matricula.getNotas().size();
							for (int i = 1; i <= numUnidadesRestantes; i++)
								table.addCell("");
						}
						
						if (matricula.getMediaParcial() != null)
							table.addCell(buildCell(Formatador.getInstance().formatarDecimal1(matricula.getMediaParcial()), 8, Element.ALIGN_CENTER, true));
						else
							table.addCell("");
						
					} else {
						for (int i = 1; i <= numUnidades; i++)
							table.addCell("");
						table.addCell("");
					}
				}
				
				if (!competenciaOuStricto) {
					// Recuperação
					if (matricula.getRecuperacao() != null)
						table.addCell(buildCell(Formatador.getInstance().formatarDecimal1(matricula.getRecuperacao()), 8, Element.ALIGN_CENTER, true));
					else
						table.addCell("");
				}
				// Média Final
				if (matricula.getMediaFinal() != null && turma.getSituacaoTurma().getId() == SituacaoTurma.CONSOLIDADA) {
					if (!competenciaOuStricto)
						table.addCell(buildCell(matricula.getMediaFinalDesc(), 8, Element.ALIGN_CENTER, true));
					else {
						if (NivelEnsino.isAlgumNivelStricto(turma.getDisciplina().getNivel()))
							table.addCell(buildCell(matricula.getConceitoChar().toString(), 8, Element.ALIGN_CENTER, true));
						else {
							String competenciaDescricao = "";
							if ( matricula.getApto() != null )
								competenciaDescricao = matricula.getCompetenciaDescricao();
							table.addCell(buildCell(competenciaDescricao, 8, Element.ALIGN_CENTER, true));
						}	
					}	
				} else {
					table.addCell("");
				}
				
				// Número de Faltas
				if (turma.getSituacaoTurma().getId() == SituacaoTurma.CONSOLIDADA)
					table.addCell(buildCell(matricula.getNumeroFaltas() == null ? "" : String.valueOf(matricula.getNumeroFaltas()), 8, Element.ALIGN_CENTER, true));
				else
					table.addCell("");
				
				// Situação da Matrícula
				if (matricula.getSituacaoMatricula().equals(SituacaoMatricula.MATRICULADO) || matricula.getSituacaoMatricula().equals(SituacaoMatricula.TRANCADO))
					table.addCell("");
				else {
					matricula.setEstrategia(estrategia);
					table.addCell(buildCell(matricula.getSituacaoAbrev(), 8, Element.ALIGN_CENTER, true));
				}
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
		
		try{
			
			PdfPTable table;
			
			List <Object> elementos = new ArrayList <Object> ();
	
			dao = getDAO(TurmaVirtualDao.class);
			
			feriados = TurmaUtil.getFeriados(turma);
			
			// Lista de Frequencia
			Set<Date> datasSet = new TreeSet<Date>();
			
			if (isEmpty(turma.getSubturmas())) {
				datasSet.addAll(TurmaUtil.getDatasAulasTruncate(turma, calendarioVigente));
			} else {
				for (Turma st : turma.getSubturmas()) {
					st = dao.findByPrimaryKey(st.getId(), Turma.class);
					datasSet.addAll(TurmaUtil.getDatasAulasTruncate(st, calendarioVigente));
				}
			}
			
			List<Date> datas = CollectionUtils.toArrayList(datasSet);
			
			int numPaginas = 1;
			if (!isEmpty(datas)) {
				numPaginas = datas.size() / TOTAL_DATAS;
				if (datas.size() % TOTAL_DATAS != 0)
					numPaginas++;
			} else {
				//throw new NegocioException("A turma não está com os horários definidos.");
				// Se não possui horários definidos, não exibe o mapa de frequência.
				return new ArrayList <Object> ();
			}
			
			if (isEmpty(turma.getSubturmas()))
				mapa = freqDao.findMatriculaFrequenciasByTurma(turma);
			else
				mapa = freqDao.findMatriculaFrequenciasBySubTurmas(turma);
			
			elementos.add(PageSize.A4.rotate());
			
			for (int i = 0; i < numPaginas; i++) {
				elementos.add(NOVA_PAGINA);
				
				elementos.add(buildHeader(true));
				elementos.add(buildSection("Lista de Freqüência"));
	
				table = new PdfPTable(new float[] { 0.05f, 0.2f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 
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
				Map<Integer, Integer> mapaMeses = new LinkedHashMap<Integer, Integer>();
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
								else if (matricula.isTrancado()) 
									cell = buildCellTrancado("T", true);
								else
									cell = buildCell("", false);
								
								/** colocando a coluna dos fériados na cor cinza */
								if( feriados != null && feriados.contains(datas.get(l)) )
									cell.setBackgroundColor(Color.LIGHT_GRAY);
								
								table.addCell( cell );
							}
						}
					}
				}
				
				table.setWidthPercentage(100);
				elementos.add(table);
			}
			
			return elementos;
		} finally {
			if ( dao != null )
				dao.close();
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
		cell.addElement(Image.getInstance(RepositorioDadosInstitucionais.get("logoInstituicao")));
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
		
		
		if (param.getNivel() == NivelEnsino.GRADUACAO) {
			
			String deptoAdmEscolar = ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_ADM_ESCOLAR);
			String gestaoGraduacao = ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO);
			
			p = new Paragraph( siglaNomeInstituicao + "\n"
					+ gestaoGraduacao + "\n"
					+ deptoAdmEscolar, FontFactory.getFont("Verdana", 10, Font.BOLD));
		} else if (param.getNivel() == NivelEnsino.LATO) {
			
			String siglaGestaoPos = ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.SIGLA_PRO_REITORIA_POS);
			
		    Unidade unidade = dao.findByPrimaryKey(turma.getDisciplina().getUnidade().getId(), Unidade.class);
            p = new Paragraph( siglaNomeInstituicao + "\n"
                    + unidade.getUnidadeResponsavel().getNome() + "\n"
                    + siglaGestaoPos + unidade.getNome(), 
                    FontFactory.getFont("Verdana", 10, Font.BOLD));
		}
		else {
			Unidade unidade = dao.findByPrimaryKey(turma.getDisciplina().getUnidade().getId(), Unidade.class);
			p = new Paragraph( siglaNomeInstituicao + "\n"
					+ unidade.getUnidadeResponsavel().getNome() + "\n"
					+ unidade.getNome(), 
					FontFactory.getFont("Verdana", 10, Font.BOLD));
		}
		
		p.setSpacingBefore(0);
		p.setSpacingAfter(0);
		cell.addElement(p);
				
		table.addCell(cell);
		
		cell = new PdfPCell();
		cell.addElement(Image.getInstance(RepositorioDadosInstitucionais.get("logoInformatica")));
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
	
	public boolean isGerarTabelaFrequencia() {
		return gerarTabelaFrequencia;
	}

	public void setGerarTabelaFrequencia(boolean gerarTabelaFrequencia) {
		this.gerarTabelaFrequencia = gerarTabelaFrequencia;
	}
}