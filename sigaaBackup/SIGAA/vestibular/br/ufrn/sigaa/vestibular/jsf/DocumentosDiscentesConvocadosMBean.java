package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.graduacao.OfertaVagasCursoDao;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.vestibular.DocumentosDiscentesConvocadosDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;
import br.ufrn.sigaa.vestibular.dao.EfetivacaoCadastramentoReservaDao;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.EfetivacaoCadastramentoReserva;
import br.ufrn.sigaa.vestibular.dominio.LinhaAuxiliar;
import br.ufrn.sigaa.vestibular.dominio.LinhaImpressaoDocumentosConvocados;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * MBean responsável pela emissão da documentação dos discentes convocados.
 */
@Component 
@Scope("session")
public class DocumentosDiscentesConvocadosMBean extends SigaaAbstractController<LinhaImpressaoDocumentosConvocados>{

	// constantes que determinam o relatório a ser gerado
	/** O controller deverá gerar o PDF com as etiquetas para impressão. */
	public static final int ETIQUETAS = 2;
	/** O controller deverá gerar o PDF com os documentos necessários ao cadastro dos discentes para impressão. */
	public static final int DOCUMENTOS_PARA_CADASTRO_DE_DISCENTES = 3;
	/** O controller deverá gerar o PDF com a lista de convocação dos aprovados para impressão. */
	public static final int LISTA_DE_CONVOCACAO_DE_APROVADOS = 4; 
	/** O controller deverá gerar o PDF com reconvocação de mudança de semestre para impressão. */
	public static final int DOCUMENTO_RECONVOCACAO_MUDANCA_SEMESTRE = 5;
	/** O controller deverá gerar o PDF com a lista para impressão dos candidatos convocados para remanejamento de semestre. */
	public static final int LISTA_DE_CONVOCACAO_MUDANCA_SEMESTRE_RECONVOCACAO_PRIMEIRA_OPCAO = 6;
	/** O controller deverá gerar o PDF com a lista de convocação dos aprovados por cotas para impressão. */
	public static final int LISTA_DE_CONVOCACAO_DE_APROVADOS_COTAS = 7;
	/** O controller deverá gerar o PDF com a lista de discentes com cadastro reserva que foram efetivados (cadastrados ou ativos). */
	public static final int LISTA_DE_EFETIVACAO_CADASTRO = 8;
	/** O controller deverá gerar o PDF com a lista de discentes com cadastro reserva que foram cadastrados na convocação. */
	public static final int LISTA_DISCENTES_CADASTRADOS_CONVOCACAO = 9;
	/** O controller deverá gerar o PDF com a lista de discentes com cadastro reserva */
	public static final int LISTA_DISCENTES_RESERVA = 10;
	
	/** Coleção que armazena os discente convocados */
	private Collection<LinhaImpressaoDocumentosConvocados> discentesConvocados = new ArrayList<LinhaImpressaoDocumentosConvocados>();
	
	/** Map que armazena as Matrizes Curriculares para gerar o relatório */
	private List<Collection<LinhaImpressaoDocumentosConvocados>> matrizesCurricular;
	
	/** ID do processo seletivo do relatório. */
	private int idProcessoSeletivo;

	/** Indica a fase que está da chamada */
	private int idFase;
	
	/** Matriz do relatório. */
	private int idMatriz;

	/** Relatório escolhido para geração da documentação */
	private Integer idRelatorio = ETIQUETAS;
	
	/** Armazena um seleção de cursos */
	private Collection<SelectItem> cursos = new ArrayList<SelectItem>(0);

	/** Armazena um seleção de chamadas */
	private Collection<SelectItem> fases = new ArrayList<SelectItem>(0);
	
	/** Constante que indica a página para seleção do processo seletivo e perfil. */
	private static final String IMPRESSAO_DOCUMENTO_CONVOCADOS_VEST = "/vestibular/relatorios/seleciona_curso_fase.jsp";

	/** Lista de ID de matrizes selecionadas para impressão dos documentos. */
	private List<Object> idsMatrizSelecionadas = new ArrayList<Object>();
	
	/** Mapa que associa o ID de municípios selecionados com todos os IDs de matrizes 
	 * curriculares de cursos dos municípios correspondentes. Utilizado para permitir
	 * a seleção de todos os cursos de determinado município.
	 */
	private Map<Integer, List<Integer>> mapaCursosCidade = new HashMap<Integer, List<Integer>>();
	/** Indica que o usuário poderá escolher um ou mais curso para gerar o relatório. */
	private boolean escolheCurso;
	/** Indica que o usuário poderá escolher a convocação. */
	private boolean escolheChamada;
	
	/**
	 * Inicia aimpressão de documentos utilizados para o cadastramento de alunos
	 * aprovados.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/cadastros.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 * */
	public String iniciarImpressaoDocumentos() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		init();
		setOperacaoAtiva(1);
		return forward(IMPRESSAO_DOCUMENTO_CONVOCADOS_VEST);
	}

	/** Inicializador das variáveis utilizadas na geração dos documentos */ 
	private void init(){
		idFase = 0;
		idMatriz = 0;
		idProcessoSeletivo = 0;
		idRelatorio = 0;
		cursos = new ArrayList<SelectItem>();
		fases = new ArrayList<SelectItem>();
		mapaCursosCidade = new HashMap<Integer, List<Integer>>();
		escolheCurso = false;
		escolheChamada = true;
	}
	
	/**
	 * Atualiza o form de acordo com o relatório selecionado.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_curso_fase.jsp</li>
	 * </ul>
	 * 
	 * @param vce
	 * @throws DAOException
	 */
	public String relatorioListener(ValueChangeEvent evt) throws DAOException {
		idRelatorio = (Integer) evt.getNewValue();
		if (idRelatorio == LISTA_DE_EFETIVACAO_CADASTRO) {
			escolheCurso = false;
			escolheChamada = true;
		} else if (idRelatorio == LISTA_DISCENTES_RESERVA) {
			escolheCurso = false;
			escolheChamada = false;
		} else {
			escolheCurso = true;
			escolheChamada = true;
		}
		return null;
	}
	
	/**
	 * Tem função de carregar os cursos e as chamadas do processo seletivo selecionado.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_curso_fase.jsp</li>
	 * </ul>
	 * 
	 * @param vce
	 * @throws DAOException
	 */
	public String carregarCursosFase(ValueChangeEvent evt) throws DAOException {
		idProcessoSeletivo = (Integer) evt.getNewValue();
		idsMatrizSelecionadas = new ArrayList<Object>();
		cursos = new ArrayList<SelectItem>(0);
		fases = new ArrayList<SelectItem>(0);
		idFase = 0;
		int idUnidade = 0;
		if (idProcessoSeletivo != 0) {
			try {
				EfetivacaoCadastramentoReservaDao dao = getDAO(EfetivacaoCadastramentoReservaDao.class);
				ProcessoSeletivoVestibular psv = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
				if (idRelatorio == LISTA_DE_EFETIVACAO_CADASTRO || idRelatorio == LISTA_DISCENTES_RESERVA) {
					Collection<EfetivacaoCadastramentoReserva> efetivacoes = dao.findByProcessoSeletivo(idProcessoSeletivo);
					for (EfetivacaoCadastramentoReserva efetivacao : efetivacoes) {
						fases.add(new SelectItem(efetivacao.getId(), "Processado em " + Formatador.getInstance().formatarData(efetivacao.getDataCadastro())));
					}
					// ordena a coleção
					Collections.sort((ArrayList<SelectItem>) cursos, new Comparator<SelectItem>() {
						@Override
						public int compare(SelectItem o1, SelectItem o2) {
							return o1.getLabel().compareTo(o2.getLabel());
						}
					});
				} else {
					OfertaVagasCursoDao ofertaDao = getDAO(OfertaVagasCursoDao.class);
					ConvocacaoProcessoSeletivoDao convocacaoDao = getDAO(ConvocacaoProcessoSeletivoDao.class);
					
					Collection<OfertaVagasCurso> cursoOfertados = ofertaDao.findAllByAnoFormaIngressoUnidadeProcessoSeletivo(psv.getAnoEntrada(), psv.getPeriodoEntrada(), psv.getFormaIngresso().getId(), idUnidade, null, null);
					// a fim de evitar a ordenação pelo método toSelectItems:
					int idMunicipioAnterior = 0;
					for (OfertaVagasCurso oferta : cursoOfertados) {
						if (idMunicipioAnterior != oferta.getCurso().getMunicipio().getId()) {
							cursos.add(new SelectItem(oferta.getCurso().getMunicipio().getId(), "-- " + oferta.getCurso().getMunicipio().getNome()+" --"));
							idMunicipioAnterior = oferta.getCurso().getMunicipio().getId();
							mapaCursosCidade.put(oferta.getCurso().getMunicipio().getId(), new ArrayList<Integer>());
						}
						cursos.add(new SelectItem(oferta.getMatrizCurricular().getId(), oferta.getMatrizCurricular().getDescricao()));
						mapaCursosCidade.get(oferta.getCurso().getMunicipio().getId()).add(oferta.getMatrizCurricular().getId());
					}
					
					Collection<ConvocacaoProcessoSeletivo> convocacoesProcessoSeletivo = convocacaoDao.findByProcessoSeletivo(idProcessoSeletivo);
					fases.addAll(toSelectItems(convocacoesProcessoSeletivo, "id", "descricaoCompleta"));
				}
				
			} catch (Exception e) {
				tratamentoErroPadrao(e);
			}
		}
		return null;
	}
	
	/**
	 * Tem função de carregar as chamadas do processo seletivo selecionado.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/graduacao/relatorios/discente/seleciona_aluno_ingressante_outro_curso.jsp</li>
	 * </ul>
	 * 
	 * @param vce
	 * @throws DAOException
	 */
	public void carregarChamadas(ValueChangeEvent vce) throws DAOException {
		
		ConvocacaoProcessoSeletivoDao convocacaoDao = getDAO(ConvocacaoProcessoSeletivoDao.class);
		try {
			ProcessoSeletivoVestibular psv = getGenericDAO().findByPrimaryKey((Integer) vce.getNewValue(), ProcessoSeletivoVestibular.class);
			
			Collection<ConvocacaoProcessoSeletivo> convocacoesProcessoSeletivo = convocacaoDao.findByProcessoSeletivo(psv.getId());
			fases = new ArrayList<SelectItem>(0);
			fases.addAll(toSelectItems(convocacoesProcessoSeletivo, "id", "descricao"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Indica se deve ser dado ao usuário a opção de selecionar o curso a ser gerado o relatório.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_curso_fase.jsp</li>
	 * </ul>
	 * 
	 * @param vce
	 * @throws DAOException
	 */
	public boolean isSelecionaCurso() {
		return !(idRelatorio == LISTA_DE_CONVOCACAO_DE_APROVADOS);
	}
	
	/**
	 * Metódo utilizado na geração dos relatórios e/ou documentos dos candidatos aprovados.
	 *
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_curso_fase.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws JRException
	 * @throws IOException
	 * @throws SQLException
	 * @throws DAOException 
	 */
	public String gerarRelatorio() throws JRException, IOException, SQLException, DAOException{
		if(!checkOperacaoAtiva(1))
			return cancelar();
		validateRequiredId(idRelatorio, "Documento", erros);
		validateRequiredId(idProcessoSeletivo, "Processo Seletivo", erros);
		if (escolheChamada)
			validateRequiredId(idFase, "Chamada", erros);
		if (escolheCurso)
			validateRequired(idsMatrizSelecionadas, "Curso(s)", erros);
		if (hasOnlyErrors()) 
			return null;
		
		DocumentosDiscentesConvocadosDao dao = getDAO(DocumentosDiscentesConvocadosDao.class);
		OfertaVagasCursoDao ofertaDao = getDAO(OfertaVagasCursoDao.class);
		Collection<LinhaAuxiliar> linhas = new LinkedList<LinhaAuxiliar>();
		String nomeCurso = "";
		List<Integer> idsMatrizCurricular = new LinkedList<Integer>();
		
		for (Object idMatrizSelecionada : idsMatrizSelecionadas) {
			int idSelecionado =  Integer.parseInt(idMatrizSelecionada.toString()) ;
			
			if( mapaCursosCidade.containsKey(idSelecionado) ){
				for(Integer idMatriz: mapaCursosCidade.get(idSelecionado))
					idsMatrizCurricular.add(idMatriz);
			} else
				idsMatrizCurricular.add(idSelecionado);
		} 
		int ano = 0, periodo = 0, idFormaIngresso = 0, idUnidade = 0;
		ProcessoSeletivoVestibular ps = ofertaDao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
		idFormaIngresso = ps.getFormaIngresso().getId();
		if ( idsMatrizCurricular.contains(0) ){
			Collection<OfertaVagasCurso> cursoOfertados = ofertaDao.findAllByAnoFormaIngressoUnidadeProcessoSeletivo(ano, periodo, idFormaIngresso, idUnidade, null ,null);
			idsMatrizCurricular = new ArrayList<Integer>();
			for (OfertaVagasCurso curso : cursoOfertados) {
				idsMatrizCurricular.add(curso.getMatrizCurricular().getId());
			}
		}
		
		if (idRelatorio == DOCUMENTOS_PARA_CADASTRO_DE_DISCENTES
				|| idRelatorio == ETIQUETAS
				|| idRelatorio == LISTA_DE_CONVOCACAO_DE_APROVADOS
				|| idRelatorio == LISTA_DE_CONVOCACAO_DE_APROVADOS_COTAS
				|| idRelatorio == LISTA_DE_CONVOCACAO_MUDANCA_SEMESTRE_RECONVOCACAO_PRIMEIRA_OPCAO
				|| idRelatorio == DOCUMENTO_RECONVOCACAO_MUDANCA_SEMESTRE) {
			try {
				discentesConvocados = dao.findAllCandidatosConvocados(idsMatrizCurricular, null, idProcessoSeletivo, idFase, idRelatorio);
				for (LinhaImpressaoDocumentosConvocados linhaImpressao : discentesConvocados) {
					LinhaAuxiliar linhaAux = new LinhaAuxiliar();
					linhaAux.getLinha().add(linhaImpressao);
					if (nomeCurso.equals("")) 
						nomeCurso = linhaImpressao.getCurso();
					linhas.add(linhaAux);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				dao.close();
			}
		}
		
		Map<String, Object> hs = new HashMap<String, Object>();
		hs.put("SUBREPORT_DIR", JasperReportsUtil.PATH_RELATORIOS_SIGAA);
		JRDataSource jrds = null;
		JasperPrint prt = null;
		String nomeArquivo = "";

		if (idRelatorio == DOCUMENTOS_PARA_CADASTRO_DE_DISCENTES) {
			hs.put("declaracao", getReportSIGAA("declaracao_2011.jasper"));
			hs.put("comprovante", getReportSIGAA("comprovante_2011.jasper"));
			hs.put("lista_aprovados", getReportSIGAA("lista_dos_aprovados_por_curso.jasper"));
			hs.put("lista_assinaturas_aprovados", getReportSIGAA("lista_de_assinatura_dos_aprovados_por_curso.jasper"));
			hs.put("discentesConvocados", discentesConvocados);
			hs.put("processoSeletivo", ps.getNome());
			hs.put("ano", new Integer(ps.getAnoEntrada()));
			
			jrds = new JRBeanCollectionDataSource(linhas);
			prt = JasperFillManager.fillReport(getReportSIGAA("relatorios_convocacao_2011.jasper"), hs, jrds);
			if ( idsMatrizSelecionadas.size() == 1 )
				nomeArquivo = "Convocados_" + nomeCurso.toLowerCase() + ".pdf";
			else
				nomeArquivo = "Convocados_processo_seletivo.pdf";

		} else if (idRelatorio == ETIQUETAS) {
			jrds = new JRBeanCollectionDataSource(discentesConvocados);
			prt = JasperFillManager.fillReport(getReportSIGAA("etiquetas.jasper"), hs, jrds);
			nomeArquivo = "Etiquetas_" + nomeCurso.toLowerCase() + ".pdf";
		
		} else if (idRelatorio == LISTA_DE_CONVOCACAO_DE_APROVADOS) {
			ConvocacaoProcessoSeletivo convocacao = dao.findByPrimaryKey(idFase, ConvocacaoProcessoSeletivo.class);
			hs.put("listaIdMatriz", UFRNUtils.gerarStringIn(idsMatrizCurricular));
			hs.put("convocacao", convocacao.getDescricao());
			hs.put("idProcessoSeletivo", idProcessoSeletivo);
			jrds = new JRBeanCollectionDataSource(discentesConvocados);
			prt = JasperFillManager.fillReport(getReportSIGAA("lista_convocacao.jasper"), hs, jrds);
			nomeArquivo = "Lista_Convocacao.pdf";
			
		} else if (idRelatorio == LISTA_DE_CONVOCACAO_DE_APROVADOS_COTAS) {
			if (isEmpty(discentesConvocados)) {
				addMensagemErro("Não há discentes convocados por cotas");
				return null;
			}
			ConvocacaoProcessoSeletivo convocacao = dao.findByPrimaryKey(idFase, ConvocacaoProcessoSeletivo.class);
			hs.put("listaIdMatriz", UFRNUtils.gerarStringIn(idsMatrizCurricular));
			hs.put("convocacao", convocacao.getDescricao());
			hs.put("idProcessoSeletivo", idProcessoSeletivo);
			jrds = new JRBeanCollectionDataSource(discentesConvocados);
			prt = JasperFillManager.fillReport(getReportSIGAA("lista_convocacao_cotas.jasper"), hs, jrds);
			nomeArquivo = "Lista_Convocacao_Cotas.pdf";
			
		} else if (idRelatorio == DOCUMENTO_RECONVOCACAO_MUDANCA_SEMESTRE) {
			jrds = new JRBeanCollectionDataSource(discentesConvocados);
			prt = JasperFillManager.fillReport(getReportSIGAA("declaracao_de_cadastro.jasper"), hs, jrds);
			nomeArquivo = "Documento_Discentes_Mudança_Semestre";
		} else if (idRelatorio == LISTA_DE_CONVOCACAO_MUDANCA_SEMESTRE_RECONVOCACAO_PRIMEIRA_OPCAO) {
			ConvocacaoProcessoSeletivo convocacao = dao.findByPrimaryKey(idFase, ConvocacaoProcessoSeletivo.class);
			hs.put("listaIdMatriz", UFRNUtils.gerarStringIn(idsMatrizCurricular));
			hs.put("convocacao", convocacao.getDescricao());
			hs.put("idProcessoSeletivo", idProcessoSeletivo);
			jrds = new JRBeanCollectionDataSource(discentesConvocados);
			prt = JasperFillManager.fillReport(getReportSIGAA("lista_mudanca_semestre.jasper"), hs, jrds);
			nomeArquivo = "Lista_Mudanca_Semestre.pdf";
		} else if (idRelatorio == LISTA_DE_EFETIVACAO_CADASTRO) {
			discentesConvocados = dao.findAllEfetivacaoCadastro(idsMatrizCurricular, idProcessoSeletivo, idFase);
			EfetivacaoCadastramentoReserva efetivacao = new EfetivacaoCadastramentoReserva(idFase);
			dao.initialize(efetivacao);
			hs.put("listaIdMatriz", UFRNUtils.gerarStringIn(idsMatrizCurricular));
			hs.put("idProcessoSeletivo", idProcessoSeletivo);
			hs.put("convocacao", "Efetivação o Cadastramento de Discentes Processado em " + Formatador.getInstance().formatarData(efetivacao.getDataCadastro()));
			jrds = new JRBeanCollectionDataSource(discentesConvocados);
			prt = JasperFillManager.fillReport(getReportSIGAA("lista_efetivacao_cadastro.jasper"), hs, jrds);
			nomeArquivo = "lista_efetivacao_cadastro.pdf";
		} else if (idRelatorio == LISTA_DISCENTES_CADASTRADOS_CONVOCACAO) {
			discentesConvocados = dao.findAllDiscentesCadastrados(idsMatrizCurricular, idProcessoSeletivo, idFase);
			ConvocacaoProcessoSeletivo convocacao = dao.findByPrimaryKey(idFase, ConvocacaoProcessoSeletivo.class);
			hs.put("listaIdMatriz", UFRNUtils.gerarStringIn(idsMatrizCurricular));
			hs.put("idProcessoSeletivo", idProcessoSeletivo);
			hs.put("convocacao", convocacao.getDescricao());
			jrds = new JRBeanCollectionDataSource(discentesConvocados);
			prt = JasperFillManager.fillReport(getReportSIGAA("lista_efetivacao_cadastro.jasper"), hs, jrds);
			nomeArquivo = "lista_efetivacao_cadastro.pdf";
		} else if (idRelatorio == LISTA_DISCENTES_RESERVA) {
			discentesConvocados = dao.findAllCadastroReserva(idsMatrizCurricular, idProcessoSeletivo);
			EfetivacaoCadastramentoReserva efetivacao = new EfetivacaoCadastramentoReserva(idFase);
			dao.initialize(efetivacao);
			hs.put("listaIdMatriz", UFRNUtils.gerarStringIn(idsMatrizCurricular));
			hs.put("idProcessoSeletivo", idProcessoSeletivo);
			hs.put("convocacao", "Lista de Discentes em Cadastro Reserva - " + ps.getNome());
			jrds = new JRBeanCollectionDataSource(discentesConvocados);
			prt = JasperFillManager.fillReport(getReportSIGAA("lista_efetivacao_cadastro.jasper"), hs, jrds);
			nomeArquivo = "lista_cadastro_reserva.pdf";
		}
		
		
		if (prt == null || prt.getPages().size() == 0) {
			addMensagemWarning("Não há alunos convocados.");
			return null;
		}
		
		JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), "pdf");
		FacesContext.getCurrentInstance().responseComplete();
		
		return null;
	}
	
	/**
	 * Adiciona os relatórios que podem ser impresso.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllRelatorio() throws ArqException {
		Collection<SelectItem> relatorios = new ArrayList<SelectItem>();
		relatorios.add(new SelectItem(new Integer(ETIQUETAS), "Etiquetas"));
		relatorios.add(new SelectItem(new Integer(DOCUMENTOS_PARA_CADASTRO_DE_DISCENTES), "Documentos para Cadastro de Discentes"));
		relatorios.add(new SelectItem(new Integer(LISTA_DE_CONVOCACAO_DE_APROVADOS), "Lista de Convocação de Aprovados"));
		relatorios.add(new SelectItem(new Integer(LISTA_DE_CONVOCACAO_DE_APROVADOS_COTAS), "Lista de Convocação de Aprovados por Cotas"));
		relatorios.add(new SelectItem(new Integer(LISTA_DE_CONVOCACAO_MUDANCA_SEMESTRE_RECONVOCACAO_PRIMEIRA_OPCAO), "Lista de Convocação para Mudança de Semestre e Reconvocação para 1ª opção"));
		relatorios.add(new SelectItem(new Integer(DOCUMENTO_RECONVOCACAO_MUDANCA_SEMESTRE), "Documentos dos Discentes com Mudança de Semestre"));
		relatorios.add(new SelectItem(new Integer(LISTA_DE_EFETIVACAO_CADASTRO), "Lista de Efetivação de Cadastro de Discentes de CADASTRO RESERVA"));
		relatorios.add(new SelectItem(new Integer(LISTA_DISCENTES_CADASTRADOS_CONVOCACAO), "Lista de Discentes Convocados que se Cadastraram"));
		relatorios.add(new SelectItem(new Integer(LISTA_DISCENTES_RESERVA), "Lista de Discentes do Cadastro Reserva"));
		return relatorios;
	}
	
	/** Popula a lista de dados necessários para gerar os documentos.<br/>
	 * Método não invocado por JSP´s
	 * @param linhaConvocados
	 * @return
	 */
	public List<LinhaAuxiliar> popularLista(Collection<LinhaImpressaoDocumentosConvocados> linhaConvocados){
		List<LinhaAuxiliar> lista = new ArrayList<LinhaAuxiliar>();
		
		LinhaAuxiliar linhaAuxiliar = new LinhaAuxiliar();
		List<LinhaImpressaoDocumentosConvocados> listAuxiliar = new ArrayList<LinhaImpressaoDocumentosConvocados>();
		
		int idMatriz = 0;
		
		for (LinhaImpressaoDocumentosConvocados linha : linhaConvocados) {
			if ( idMatriz != linha.getMatrizCurricular() ){
				idMatriz = linha.getMatrizCurricular();
				
				for (LinhaImpressaoDocumentosConvocados linhaAux : linhaConvocados) {
					if ( linhaAux.getMatrizCurricular() == linha.getMatrizCurricular() ) 
						listAuxiliar.add(linhaAux);
				}
				linhaAuxiliar.setLinha(listAuxiliar);
				lista.add(linhaAuxiliar);
			}	
		}
		
		return lista;
	}
	
	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	public void setIdProcessoSeletivo(int idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	public int getIdFase() {
		return idFase;
	}

	public void setIdFase(int idFase) {
		this.idFase = idFase;
	}

	public int getIdMatriz() {
		return idMatriz;
	}

	public void setIdMatriz(int idMatriz) {
		this.idMatriz = idMatriz;
	}

	public Collection<LinhaImpressaoDocumentosConvocados> getDiscentesConvocados() {
		return discentesConvocados;
	}

	public void setDiscentesConvocados(
			Collection<LinhaImpressaoDocumentosConvocados> discentesConvocados) {
		this.discentesConvocados = discentesConvocados;
	}

	public List<Collection<LinhaImpressaoDocumentosConvocados>> getMatrizesCurricular() {
		return matrizesCurricular;
	}

	public void setMatrizesCurricular(
			List<Collection<LinhaImpressaoDocumentosConvocados>> matrizesCurricular) {
		this.matrizesCurricular = matrizesCurricular;
	}

	public Collection<SelectItem> getCursos() {
		return cursos;
	}
	
	public void setCursos(Collection<SelectItem> cursos) {
		this.cursos = cursos;
	}

	public Collection<SelectItem> getFases() {
		return fases;
	}

	public void setFases(Collection<SelectItem> fases) {
		this.fases = fases;
	}

	public List<Object> getIdsMatrizSelecionadas() {
		return idsMatrizSelecionadas;
	}

	public void setIdsMatrizSelecionadas(List<Object> idsMatrizSelecionadas) {
		this.idsMatrizSelecionadas = idsMatrizSelecionadas;
	}

	public Integer getIdRelatorio() {
		return idRelatorio;
	}

	public void setIdRelatorio(Integer idRelatorio) {
		this.idRelatorio = idRelatorio;
	}

	public Map<Integer, List<Integer>> getMapaCursosCidade() {
		return mapaCursosCidade;
	}

	public void setMapaCursosCidade(Map<Integer, List<Integer>> mapaCursosCidade) {
		this.mapaCursosCidade = mapaCursosCidade;
	}

	public boolean isEscolheCurso() {
		return escolheCurso;
	}

	public void setEscolheCurso(boolean escolheCurso) {
		this.escolheCurso = escolheCurso;
	}

	public boolean isEscolheChamada() {
		return escolheChamada;
	}

	public void setEscolheChamada(boolean escolheChamada) {
		this.escolheChamada = escolheChamada;
	}

}