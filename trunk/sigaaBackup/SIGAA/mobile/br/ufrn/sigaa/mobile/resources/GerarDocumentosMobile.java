package br.ufrn.sigaa.mobile.resources;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.arquivos.AssinaturaDigitalService;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.RegistroAcessoPublico;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Municipio;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Historico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MetropoleDigitalHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.negocio.MovimentoCalculoHistorico;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;

public class GerarDocumentosMobile extends SigaaGenericResource{

	
	private EmissaoDocumentoAutenticado comprovante;
	
	public GerarDocumentosMobile(HttpServletRequest requent){
		this.request = requent;
	}
	
	public File getHistoricoDiscente() throws ArqException, NegocioException, JRException, IOException{
		
		File hist = null;
		
		if (getDiscenteLogado() != null){
			
		
			if (getDiscenteLogado().getStatus() == StatusDiscente.EXCLUIDO) {
				return null;
			}

			if (!ValidatorUtil.isEmpty(getDiscenteLogado().getIdHistoricoDigital()))
				hist = recuperaHistoricoDigitalizado();
			else
				hist = computaHistoricoDiscente();
			
		}
		return hist;
	}

	private File computaHistoricoDiscente() throws JRException, IOException, ArqException, NegocioException {

		MovimentoCalculoHistorico mov = new MovimentoCalculoHistorico();
		mov.setUsuarioLogado(getUsuarioLogado());
		mov.setRecalculaCurriculo(false);
		mov.setDiscente(getDiscenteLogado());
		mov.setCodMovimento(SigaaListaComando.CALCULAR_HISTORICO_DISCENTE);
		
		//prepareMovimento(SigaaListaComando.CALCULAR_HISTORICO_DISCENTE);
		
		Historico historico = null;
		try {
			historico = (Historico) executarMovimento(mov);
		} catch(NegocioException ne) {
			return null;
		} catch (ArqException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if (comprovante != null) {
			historico.setDataHistorico(comprovante.getHoraEmissao());
		} 
		if (comprovante == null) {
			
			 comprovante = geraEmissao(TipoDocumentoAutenticado.HISTORICO,
				getDiscenteLogado().getMatricula().toString(),
				geraSemente(historico.getMatriculasDiscente()), Integer
				.toString(getDiscenteLogado().getId()), null, false);
 
		}
		
		IndiceAcademicoDao dao = DAOFactory.getInstance().getDAO(IndiceAcademicoDao.class);
		
		try {
			historico.getDiscente().getDiscente().setIndices(dao.findIndicesAcademicoDiscente(historico.getDiscente().getDiscente()));
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if ( historico.getDiscente().isGraduacao() )
			populaNotasFrequenciaPorSituacao(historico);
		
		/* Solicitação de tradução do histórico, quando o idioma for diferente do português.
		 */
		/*
		if (ValidatorUtil.isNotEmpty(idioma) && !IdiomasEnum.PORTUGUES.getId().equals(idioma)){
			traduzirElementosHistorico(historico);
			if (hasErrors()) return null;
		} else {
		}
		*/
		
		if (historico.getDiscente().isGraduacao()){
			ReflectionUtils.setProperty(historico.getDiscente(), "rendimentoAcademino", "Índices Acadêmicos");
		}
		
		historico.setStatusDiscenteI18n(ValidatorUtil.isEmpty(historico.getStatusDiscenteI18n()) ? getDiscenteLogado().getStatusString() : historico.getStatusDiscenteI18n());
		List<Historico> historicos = new ArrayList<Historico>();
		historicos.add(historico);

		String nivel = historico.getNivel();
		
		return geraArquivo(historicos, nivel);
	}
	
	private File geraArquivo(List<Historico> historicos, String nivel) throws JRException, DAOException, IOException {
		Historico historico = historicos.iterator().next();
		DiscenteAdapter discente = historico.getDiscente(); 
		int totalPendentes = historico.getDisciplinasPendentesDiscente().size();
		boolean autenticidadeAutomatica = true;
		
		Map<String, Object> hs = new HashMap<String, Object>();
		
		hs.put("nomeSistema", RepositorioDadosInstitucionais.get("siglaSigaa") + " - " + RepositorioDadosInstitucionais.get("nomeSigaa") );
		hs.put("nomeInstituicao", RepositorioDadosInstitucionais.get("siglaInstituicao") + " - " + RepositorioDadosInstitucionais.get("nomeInstituicao") );
		hs.put("enderecoInstituicao", RepositorioDadosInstitucionais.get("enderecoInstituicao") );		
		hs.put("logo_ifes", new URL(RepositorioDadosInstitucionais.get("logoInstituicao") ));
		hs.put("logo_adm_sistema", new URL(RepositorioDadosInstitucionais.get("logoInformatica") ) ); 
		hs.put("verificacaoAutomaticaAutenticidade", (discente.isStricto() ? ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.SIGLA_PRO_REITORIA_POS) : ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_DEPARTAMENTO_ADM_ESCOLAR) ) );

		
		
		hs.put("pathSubRelCurso", getReportSIGAA("HistoricoCurso" + nivel + ".jasper"));
		if(MetropoleDigitalHelper.isMetropoleDigital(discente))
			hs.put("pathSubRelMatriculadas", getReportSIGAA("HistoricoComponentesMatriculadosMetropole.jasper"));
		else
			hs.put("pathSubRelMatriculadas", getReportSIGAA("HistoricoComponentesMatriculados" + nivel + ".jasper"));

		if (discente.isGraduacao()) {
			hs.put("pathSubRelGradeCH", getReportSIGAA("HistoricoGradeCargasHorariasGraduacao.jasper"));
			hs.put("pathSubRelEquivalencias", getReportSIGAA("HistoricoComponentesEquivalencias.jasper"));
			hs.put("pathSubRelMobilidadeEstudantil", getReportSIGAA("HistoricoMobilidadeEstudantil.jasper"));		
		}
		if (discente.isTecnico()) {
			hs.put("pathSubRelGradeCH", getReportSIGAA("HistoricoGradeCargasHorariasTecnico.jasper"));
		}
		if (discente.isStricto()) { 
			DiscenteStricto ds = (DiscenteStricto) discente; 
			if (ds.getBancaDefesa() != null) { 
				hs.put("pathSubRelDadosBancaStricto", getReportSIGAA("HistoricoDadosBancaStricto.jasper"));			
			} 
			calcularTotalCreditos(ds);			
			hs.put("pathSubRelGradeCH", getReportSIGAA("HistoricoGradeCargasHorariasStricto.jasper"));		
		}
		if(discente.isLato()){
			DiscenteLato dl = (DiscenteLato) discente;
			if(dl.getTrabalhoFinal() != null){
				hs.put("pathSubRelDadosTrabalhoFinalLato", getReportSIGAA("HistoricoDadosTrabalhoFinalLato.jasper"));
			}
		}

		hs.put("pathSubRelPendentes",
				getReportSIGAA("HistoricoComponentesPendentes.jasper"));
		hs.put("pathSubRelObservacoes",
				getReportSIGAA("HistoricoDiscenteObservacoes.jasper"));

		if (comprovante != null)
			hs.put("codigoSeguranca", comprovante.getCodigoSeguranca());
		
		hs.put("siteVerificacao", ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));
		hs.put("totalPendentes", String.valueOf(totalPendentes));
		hs.put("stricto", String.valueOf(historico.isStricto()));
		hs.put("exigeNotaAproveitamento", String.valueOf(ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao().isExigeNotaAproveitamento()));

		if (historico.isGraduacao()) {
			ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO);
			hs.put("cabecalhoLinha1", ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO));
			hs.put("cabecalhoLinha2", ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_ADM_ESCOLAR));
		} else if (historico.isTecnico()) {
			hs.put("cabecalhoLinha1", "");
			hs.put("cabecalhoLinha2", "");
		} else if (historico.isStricto()) {
			hs.put("cabecalhoLinha1", ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.SIGLA_NOME_PRO_REITORIA_POS));
			hs.put("cabecalhoLinha2", "");
		} else if (historico.isResidencia()) {
			hs.put("cabecalhoLinha1", "Residências em Saúde");
			hs.put("cabecalhoLinha2", "");
		} else if (historico.isLato()) {
			hs.put("cabecalhoLinha1", ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.SIGLA_NOME_PRO_REITORIA_POS));
			hs.put("cabecalhoLinha2", "");
		}
		
		/*
		if (ValidatorUtil.isNotEmpty(idioma) && !IdiomasEnum.PORTUGUES.getId().equals(idioma)){
			Locale locale = new Locale(idioma);
			hs.put(JRParameter.REPORT_LOCALE, locale);
		}	
		*/
		hs.put("autenticidadeAutomatica", autenticidadeAutomatica);
		
		JRDataSource jrds = new JRBeanCollectionDataSource(historicos);
		JasperPrint prt = JasperFillManager.fillReport(getReportSIGAA("HistoricoDiscente.jasper"), hs, jrds);
		
		File tempFile = File.createTempFile("historico_" + discente.getMatricula(), ".pdf");
		JasperExportManager.exportReportToPdfFile(prt, tempFile.getAbsolutePath());
		
		File arquivo = null;
		try {
			arquivo = AssinaturaDigitalService.assinarPdf(tempFile);
		} catch(Exception e) {
			arquivo = tempFile;		
		}
		
		return arquivo;
	}

	private File recuperaHistoricoDigitalizado() throws ArqException, NegocioException, JRException, IOException {
		return null;
	}

	/**
	 * Gera a semente de verificação utilizada na identificação única do documento
	 * autenticado emitido pelo sistema. No caso do histórico é composta pelas
	 * matrículas nos componentes curriculares, suas médias finais e situações respectivas.
	 */
	private String geraSemente(Collection<MatriculaComponente> matriculas) {
		StringBuffer bufferDigest = new StringBuffer();
		for (MatriculaComponente comp : matriculas)
			bufferDigest.append(comp.getId() + "_" + comp.getMediaFinal() + "_"
					+ comp.getSituacaoMatricula().getId() + "_");

		return bufferDigest.toString();
	}
	
		
	/**
	* Método responsável por popular as notas e frequências das matrículas, 
	* de acordo com a regra de visualização conforme a situação da matrícula.
	* @param historico
	* @throws DAOException 
	*/
	private void populaNotasFrequenciaPorSituacao(Historico historico) throws DAOException{
		List<MatriculaComponente> matriculasDiscente = new ArrayList<MatriculaComponente>();
		matriculasDiscente.addAll(historico.getMatriculasDiscente());
		
		String mesAno = ParametroHelper.getInstance().getParametro(ParametrosGraduacao.MES_ANO_LIMITE_ALUNOS_ANTIGOS_INATIVOS);
		Calendar dataBase = CalendarUtils.getInstance("MM/yyyy", mesAno);
		
		boolean exigeNotaAproveitamento = ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao().isExigeNotaAproveitamento();
		
		DiscenteAdapter discente = historico.getDiscente();
		boolean discenteAntigo = false;
		/* Exibe apenas os valores de notas e frequência para discente antigos ao ano registrado 
		 * no parâmetro: ParametrosGraduacao.MES_ANO_LIMITE_ALUNOS_ANTIGOS_INATIVOS. */
		if(discente.getDiscente().getMovimentacaoSaida() != null){
			if(discente.getDiscente().getMovimentacaoSaida().getTipoMovimentacaoAluno().isPermanente() 
					&& discente.getDiscente().getMovimentacaoSaida().getDataRetorno() == null 
					&& discente.getDiscente().getMovimentacaoSaida().getDataOcorrencia().before(dataBase.getTime())){
				discenteAntigo = true;
			}
		}
		
		for (MatriculaComponente mc : matriculasDiscente) {
			int situacao = mc.getSituacaoMatricula().getId();
			
			if (situacao == SituacaoMatricula.APROVEITADO_DISPENSADO.getId()) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.APROVEITADO_CUMPRIU.getId()) {
				if (!exigeNotaAproveitamento){  
					mc.setAno(null);
					mc.setPeriodo(null);
					if (!discenteAntigo){
						mc.setMediaFinal(null);
					}
				}
			}
			else if (situacao == SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId()) {
				if (!exigeNotaAproveitamento){  
					mc.setAno(null);
					mc.setPeriodo(null);
					if (!discenteAntigo){
						mc.setMediaFinal(null);
					}
				}
			}
			else if (situacao == SituacaoMatricula.APROVADO.getId()) {
				if (!discenteAntigo && !exigeNotaAproveitamento && mc.getMediaFinal() == null)  
					mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.TRANCADO.getId() ) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.MATRICULADO.getId()) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.CANCELADO.getId()) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.NAO_CONCLUIDO.getId()) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.EM_ESPERA.getId()) {
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.EXCLUIDA.getId()) { 
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.INDEFERIDA.getId()) { 
				mc.setMediaFinal(null);
			}
			else if (situacao == SituacaoMatricula.DESISTENCIA.getId()) { 
				mc.setMediaFinal(null);
			}
			else {
				if ( mc.getComponente().isAtividade() 
						&& !mc.getComponente().isNecessitaMediaFinal()
						|| (exigeNotaAproveitamento == false && mc.getMediaFinal() == null) )
					mc.setMediaFinal(null);
			}
			
		}
		
		if (!exigeNotaAproveitamento){  
			Comparator<MatriculaComponente> matriculaComparator = new Comparator<MatriculaComponente>() {
				public int compare(MatriculaComponente mc1, MatriculaComponente mc2) {
					Integer ano1 = new Integer((mc1.getAno() != null ? mc1.getAno() : 0));
					Integer ano2 = new Integer((mc2.getAno() != null ? mc2.getAno() : 0));
					return ano1.compareTo(ano2);
					
				}
			};
			Collections.sort((List<MatriculaComponente>)historico.getMatriculasDiscente(), matriculaComparator);
		}
	}
	
	/**
	 * Calcula o total de créditos exigidos no currículo do curso.
	 * Considera os componentes específicos da área de concentração do discente e 
	 * os componentes comuns à todas as áreas. 
	 * 
	 * @param d
	 * @throws DAOException
	 */
	private void calcularTotalCreditos(DiscenteStricto d) throws DAOException { 
		//Adicionar validação para contabilizar em chNaoAtividadeObrig apenas as disciplinas da área de concentração do aluno, 
		//caso não tenha área de concentração específica, realiza a soma todos componentes do currículo. 
		DiscenteStricto ds = DAOFactory.getGeneric(Sistema.SIGAA).findByPrimaryKey(d.getId(),DiscenteStricto.class); 
		if(!ValidatorUtil.isEmpty(ds) && !ValidatorUtil.isEmpty(ds.getArea()) && !ValidatorUtil.isEmpty(ds.getDiscente()) && 
				!ValidatorUtil.isEmpty(ds.getDiscente().getCurriculo())) { 
			Discente discente= ds.getDiscente(); 
			Curriculo curriculo = discente.getCurriculo(); 
			AreaConcentracao area = ds.getArea(); 
			int chAtividadeObrig = 0; 
			int chNaoAtividadeObrig = 0; 
			int crNaoAtividadeObrig = 0; 
			
			for (CurriculoComponente cc : curriculo.getCurriculoComponentes()) { 
				ComponenteCurricular comp = DAOFactory.getGeneric(Sistema.SIGAA).findByPrimaryKey(cc.getComponente().getId(), ComponenteCurricular.class); 
				if (cc.getObrigatoria() && (comp.isAtividade() || comp.isAtividadeColetiva())) { 
					chAtividadeObrig += comp.getChTotal();					
				} else if (cc.getObrigatoria() && !comp.isAtividade() && !comp.isAtividadeColetiva() && 
						(ValidatorUtil.isEmpty(cc.getAreaConcentracao()) || cc.getAreaConcentracao().getId() == area.getId()) ) { 
					chNaoAtividadeObrig += comp.getChTotal(); 
					crNaoAtividadeObrig += comp.getCrTotal();				
				} 
				DAOFactory.getGeneric(Sistema.SIGAA).detach(comp); 
			
			} 
			curriculo.setChTotalMinima(curriculo.getChOptativasMinima() + chAtividadeObrig + chNaoAtividadeObrig); 
			int crOptativasMinima = curriculo.getChOptativasMinima() / ParametrosGestoraAcademicaHelper.getParametros(ds.getDiscente()).getHorasCreditosAula(); 
			d.setTotalCreditoCalculado(crNaoAtividadeObrig + crOptativasMinima); 
		
		} 
	
	}
	
	private EmissaoDocumentoAutenticado geraEmissao(int tipoDocumento,
			String ident, String semente, String complemento, Integer subTipo, boolean controleNumero )
			throws ArqException, NegocioException {

		EmissaoDocumentoAutenticado emissao = new EmissaoDocumentoAutenticado();
		emissao.setDataEmissao(new Date());
		emissao.setIdentificador(ident);
		emissao.setTipoDocumento(tipoDocumento);
		if ( !controleNumero )
			emissao.setPrng(UFRNUtils.toSHA1Digest(String.valueOf(Math.random())));
		else {
//			ResourceBundle bundle = new ValidadoresResourceBundle();
//			emissao.setPrng(bundle.getString("prng_documento").trim());
		}
		
		emissao.setCodigoSeguranca(AutenticacaoUtil.geraCodigoValidacao(emissao, semente));
		emissao.setDadosAuxiliares(complemento);
		emissao.setSubTipoDocumento(subTipo);
		emissao.setEmissaoDocumentoComNumero(controleNumero);

		MovimentoCadastro cad = new MovimentoCadastro();
		cad.setCodMovimento(ArqListaComando.GERAR_EMISSAO_DOCUMENTO_AUTENTICADO);
		cad.setObjMovimentado(emissao);
		cad.setRegistroEntrada(getRegistroEntrada());
		cad.setRegistroAcessoPublico(getAcessoPublico());
		cad.setSistema(Sistema.COMUM);

		//prepareMovimento(ArqListaComando.GERAR_EMISSAO_DOCUMENTO_AUTENTICADO);

		// chama o execute do controlador filho
		Object sistemaSolicitante = request.getAttribute("sistema");
		request.setAttribute("sistema", Sistema.COMUM);
		request.setAttribute("sistema", sistemaSolicitante);
		
		return (EmissaoDocumentoAutenticado) executarMovimento(cad);

	}
	
	/**
	 * Retorna o registro de entrada do usuário logado.
	 * @return
	 */
	private RegistroEntrada getRegistroEntrada() {
		UsuarioGeral usuarioLogado = getUsuarioLogado();
		if (usuarioLogado != null)
			return usuarioLogado.getRegistroEntrada();
		else
			return null;
	}
	
	/**
	 * Retorna o registro de acesso público da pessoa que está
	 * acessando a área pública dos sistemas.
	 * @return
	 */
	private RegistroAcessoPublico getAcessoPublico() {
		return (RegistroAcessoPublico) request.getSession().getAttribute("REGISTRO_ACESSO_PUBLICO");
	}
	
	/*-------- Declaração de Vinculo --------*/
	

	/** Constante que define a JSP de declaração de vínculo de graduação.  */
	private static final String MODELO_GRADUACAO = "declaracao_vinculo_graduacao.jasper";
	/** Constante que define a JSP de declaração de vínculo de stricto sensu.  */
	private static final String MODELO_STRICTO = "declaracao_vinculo_stricto.jasper";
	/** Constante que define a JSP de declaração de vínculo de lato sensu.  */
	private static final String MODELO_LATO = "declaracao_vinculo_lato.jasper";
	/** Constante que define a JSP de declaração de vínculo de Técnico.  */
	private static final String MODELO_TECNICO = "declaracao_vinculo_tecnico.jasper";
	/** Constante que define a JSP de declaração de vínculo de Médio.  */
	private static final String MODELO_MEDIO = "declaracao_vinculo_medio.jasper";
	
	/**
	 * Emitir declaração para o próprio discente
	 *
	 * @return
	 * @throws DAOException 
	 */
	public File getDeclaracaoVinculo() throws DAOException {

		// Validar discente do usuário
		if (getUsuarioLogado().getDiscenteAtivo() == null) {
			return null;
		}

		return emitir();
	}
	
	/**
	 * Emitir a declaração de vínculo para o discente
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private File emitir() throws DAOException {
		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();

		// Validar discente
		if (discente == null) {
			//addMensagemErro("É necessário definir o discente para emitir uma declaração de vínculo.");
			return null;
		}

		/** Validar status do discente
		 *  Para os discentes graduação com status cadastrado é permitido a emissão da declaração de cadastro.
		 */
		if ( (!discente.isGraduacao() && discente.getStatus() == StatusDiscente.CADASTRADO) || !StatusDiscente.getStatusComVinculo().contains(discente.getStatus()) || !discente.isRegular()) {
			//addMensagemErro("O discente não possui um vínculo ativo com a instituição.");
			return null;
		}		
		
		if (discente.getCurso().getUnidade().getMunicipio() == null) {
			//addMensagemErro("O município da unidade (" + discente.getCurso().getUnidade().getNome() + ") a que pertece o curso não foi definida.");
			return null;
		}

		try {
			
			if ( comprovante == null) {
				comprovante = geraEmissao(TipoDocumentoAutenticado.DECLARACAO_COM_IDENTIFICADOR,
						discente.getMatricula().toString(),
						gerarSemente(), null, SubTipoDocumentoAutenticado.DECLARACAO_VINCULO_INSTITUICAO, false);
			}
			
			// Parâmetros
			Map<String, String> hs = new HashMap<String, String>();
			hs.put("codigoSeguranca", comprovante.getCodigoSeguranca());
			hs.put("siteVerificacao", ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));
			hs.put("nomeInstituicao", RepositorioDadosInstitucionais.getAll().get("nomeInstituicao"));
			
			if ( ( discente.isTecnico() || discente.isMedio() ) && discente.getCurso().getUnidade().getMunicipio() != null && discente.getCurso().getUnidade().getMunicipio().getId() == Municipio.NATAL )
				hs.put("enderecoInstituicao", getEnderecoUnidade(discente) );
			else
				hs.put("enderecoInstituicao", RepositorioDadosInstitucionais.getAll().get("enderecoInstituicao"));
			
			hs.put("cgcInstituicao",DAOFactory.getInstance().getDAO(GenericDAOImpl.class, request).findByPrimaryKey(UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class).getCnpjFormatado() );
			hs.put("nomeDepartamento", getNomeDepartamento());
			hs.put("contatoDepartamento", getContatoDepartamento());
			hs.put("hoje", Formatador.getInstance().formatarDataExtenso(new Date()) );
			
			hs.put("cidadeInstituicao", RepositorioDadosInstitucionais.getAll().get("cidadeInstituicao"));
			hs.put("admEscolar", ParametroHelper.getInstance().getParametro(ParametrosGraduacao.NOME_ADM_ESCOLAR));
			hs.put("proReitoria", ParametroHelper.getInstance().getParametro(ParametrosGraduacao.NOME_GESTAO_GRADUACAO));
			hs.put("siglaAdmEscolar", ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_ADM_ESCOLAR).toUpperCase());

			if (discente.isGraduacao()){
				DiscenteDao dao = DAOFactory.getInstance().getDAO(DiscenteDao.class);
				List<DiscenteGraduacao> discentesGrad = new ArrayList<DiscenteGraduacao>();
				DiscenteGraduacao dg =  (DiscenteGraduacao) dao.findByPK(discente.getDiscente().getId());
				discentesGrad.add(dg);	
				// Preparar emissão do PDF
				JRDataSource jrds = new JRBeanCollectionDataSource(discentesGrad);
				// Emitir PDF
				JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(getModeloDeclaracao()) , hs, jrds);
				
				
				File tempFile = File.createTempFile("declaracao_" + discente.getMatricula(), ".pdf");
				JasperExportManager.exportReportToPdfFile(prt, tempFile.getAbsolutePath());
				
				File arquivo = null;
				try {
					arquivo = AssinaturaDigitalService.assinarPdf(tempFile);
				} catch(Exception e) {
					arquivo = tempFile;		
				}finally{
					dao.close();
				}
				return arquivo;
			}else{
				List<DiscenteAdapter> discentes = new ArrayList<DiscenteAdapter>();
				DiscenteDao dao = DAOFactory.getInstance().getDAO(DiscenteDao.class);
				DiscenteAdapter disc = dao.findDetalhesByDiscente(discente);
				discentes.add(disc.getDiscente());
				// Preparar emissão do PDF
				JRDataSource jrds = new JRBeanCollectionDataSource(discentes);
				// Emitir PDF
				JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(getModeloDeclaracao()) , hs, jrds);
				File tempFile = File.createTempFile("historico_" + discente.getMatricula(), ".pdf");
				JasperExportManager.exportReportToPdfFile(prt, tempFile.getAbsolutePath());
				
				File arquivo = null;
				try {
					arquivo = AssinaturaDigitalService.assinarPdf(tempFile);
				} catch(Exception e) {
					arquivo = tempFile;		
				}finally{
					dao.close();
				}
				return arquivo;
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String gerarSemente() throws DAOException {
		StringBuilder builder = new StringBuilder();
		DiscenteAdapter obj = getUsuarioLogado().getDiscenteAtivo();
		
		// Utilizar curso, município do curso e status do discente
		builder.append( obj.getCurso().getId());
		
		if ( !obj.isLato() && !obj.isResidencia() && !obj.isMedio() && !obj.isTecnico() )
			builder.append( obj.getCurso().getMunicipio().getId());
		
		if ( obj.isTecnico() || obj.isMedio() )
			builder.append( obj.getMatricula() );

		if ( obj.isGraduacao() ) {
			DiscenteDao dao = DAOFactory.getInstance().getDAO(DiscenteDao.class);
			DiscenteGraduacao dg =  (DiscenteGraduacao) dao.findByPK(obj.getDiscente().getId());
			builder.append( dg.getMatrizCurricular().getDescricao());
			dao.close();
		}
		
		builder.append( StatusDiscente.getStatusComVinculo().contains(obj.getStatus()) );

		return builder.toString();
	}
	
	/** Monta o Endereço da Unidade de acordo com o Curso */
	private String getEnderecoUnidade( DiscenteAdapter discente ) {
		String endereco = discente.getCurso().getUnidade().getEndereco();  
			   endereco += " - " + discente.getCurso().getUnidade().getMunicipio().getNome();
			   endereco += " - " + discente.getCurso().getUnidade().getCep();
		return endereco;
	}
	
	/**
	 * Retorna o modelo da declaração de acordo
	 * com o nível do discente
	 * @return
	 */
	private String getModeloDeclaracao() {
		switch (getUsuarioLogado().getDiscenteAtivo().getNivel()) {
			case NivelEnsino.GRADUACAO: return MODELO_GRADUACAO;
			case NivelEnsino.DOUTORADO: return MODELO_STRICTO;
			case NivelEnsino.MESTRADO: return MODELO_STRICTO;
			case NivelEnsino.LATO: return MODELO_LATO;
			case NivelEnsino.TECNICO: return MODELO_TECNICO;
			case NivelEnsino.MEDIO: return MODELO_MEDIO;
			default: return "";
		}
	}
	
	/**
	 * Retorna o nome do departamento de acordo com nível.
	 * com o nível do discente
	 * @return
	 */
	private String getNomeDepartamento() {
		ParametroHelper paramHelper = ParametroHelper.getInstance();
		switch (getUsuarioLogado().getDiscenteAtivo().getNivel()) {
			case NivelEnsino.GRADUACAO: return paramHelper.getParametro(ParametrosGraduacao.NOME_PRO_REITORIA_GRADUACAO);
			case NivelEnsino.DOUTORADO: return paramHelper.getParametro(ParametrosStrictoSensu.NOME_PRO_REITORIA_POS_GRADUACAO);
			case NivelEnsino.MESTRADO: return paramHelper.getParametro(ParametrosStrictoSensu.NOME_PRO_REITORIA_POS_GRADUACAO);
			case NivelEnsino.LATO: return paramHelper.getParametro(ParametrosStrictoSensu.NOME_PRO_REITORIA_POS_GRADUACAO);
			default: return "";
		}
	}
	
	/**
	 * Retorna o nome do departamento de acordo com nível.
	 * com o nível do discente
	 * @return
	 * @throws DAOException 
	 */
	private String getContatoDepartamento() throws DAOException {
		ParametroHelper paramHelper = ParametroHelper.getInstance();
		ParametrosGestoraAcademica paramGestoraAcademic = ParametrosGestoraAcademicaHelper.getParametros(getUsuarioLogado().getDiscenteAtivo());
		switch (getUsuarioLogado().getDiscenteAtivo().getNivel()) {
			case NivelEnsino.GRADUACAO: return paramHelper.getParametro(ParametrosGraduacao.CONTATO_PRO_REITORIA_GRADUACAO);
			case NivelEnsino.DOUTORADO: return paramHelper.getParametro(ParametrosStrictoSensu.CONTATO_PRO_REITORIA_POS_GRADUACAO);
			case NivelEnsino.MESTRADO: return paramHelper.getParametro(ParametrosStrictoSensu.CONTATO_PRO_REITORIA_POS_GRADUACAO);
			case NivelEnsino.LATO: return paramHelper.getParametro(ParametrosStrictoSensu.CONTATO_PRO_REITORIA_POS_GRADUACAO);
			case NivelEnsino.TECNICO: return paramGestoraAcademic.getTelefoneContato() != null ? paramGestoraAcademic.getTelefoneContato() : "" 
					+ " - "  + paramGestoraAcademic.getEmailContato() != null ? paramGestoraAcademic.getEmailContato() : "";
			case NivelEnsino.MEDIO: return paramGestoraAcademic.getTelefoneContato() != null ? paramGestoraAcademic.getTelefoneContato() : "" 
					+ " - "  + paramGestoraAcademic.getEmailContato() != null ? paramGestoraAcademic.getEmailContato() : "";
			default: return "";
		}
	}
	
}
