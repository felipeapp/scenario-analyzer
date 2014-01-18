/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/05/2009
 *
 */
package br.ufrn.sigaa.diploma.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.ProcessoDAO;
import br.ufrn.comum.dao.ProcessoDAOImpl;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.ProcessoDTO;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.dao.prodocente.TrabalhoFimCursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaColetivoDao;
import br.ufrn.sigaa.diploma.dao.RegistroDiplomaDao;
import br.ufrn.sigaa.diploma.dao.ResponsavelAssinaturaDiplomasDao;
import br.ufrn.sigaa.diploma.dominio.DadosImpressaoDiploma;
import br.ufrn.sigaa.diploma.dominio.LogGeracaoDiploma;
import br.ufrn.sigaa.diploma.dominio.ObservacaoRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.RegistroDiplomaColetivo;
import br.ufrn.sigaa.diploma.dominio.ResponsavelAssinaturaDiplomas;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;

/** Controller responsável pela impressão de diplomas.
 * @author Édipo Elder F. Melo
 *
 */
@Component("impressaoDiploma")
@Scope("session")
public class ImpressaoDiplomaMBean extends SigaaAbstractController<RegistroDiplomaColetivo> implements OperadorDiscente {
	
	/** Curso da turma que terá a impressão de diplomas coletivo gerado. */
	private int idCurso;
	
	/** Ano da turma que terá a impressão de diplomas coletivo gerado. */
	private int ano;
	
	/** Período da turma que terá a impressão de diplomas coletivo gerado. */
	private int semestre;
	
	/** Indica se a impressão será de segunda via. */
	private boolean segundaVia;
	
	/** Discente que terá a impressão de diplomas gerado. */
	private DiscenteAdapter discente;

	/** Lista de discentes que terá a impressão de diplomas coletivo gerado. */
	private List<Integer> listaIdDiscente;

	/** Registro de diploma coletivo que terá a impressão de diplomas gerado. */
	private RegistroDiplomaColetivo registroDiplomaColetivo;

	/** Registro de Diploma a ser adicionado a observação de impressão de segunda via. */
	private RegistroDiploma registroSegundaVia;
	
	/** Número do processo de solicitação de segunda via. */
	private String processo;
	
	/** O nível de ensino para o qual está sendo impresso o diploma */
	private char nivelEnsino;
	
	/** Construtor padrão. */
	public ImpressaoDiplomaMBean() {
	}

	/**
	 * Inicia a impressão de diplomas coletivo.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarImpressaoDiplomaColetivo() {
		initObj();
		segundaVia = false;
		return forward("/diplomas/registro_diplomas/impressao_coletivo.jsp");
	}

	
	/**
	 * Inicia a impressão de diploma individual.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarImpressaoDiplomaIndividual() {
		initObj();
		segundaVia = false;
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.IMPRIMIR_DIPLOMA_GRADUACAO);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Inicia a impressão de segunda via do diploma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarImpressaoSegundaVia() {
		String retorno = iniciarImpressaoDiplomaIndividual();
		segundaVia = true;
		return retorno;
	}	
	
	/** Inicia os atributos do controller.
	 * 
	 */
	private void initObj() {
		if (getCalendarioVigente() != null) {
			this.ano = getCalendarioVigente().getAnoAnterior();
			this.semestre = getCalendarioVigente().getPeriodoAnterior();
		} else {
			// calcula o ano-período anterior
			this.ano = CalendarUtils.getMesAtual() < 7 ? CalendarUtils.getAnoAtual()  - 1: CalendarUtils.getAnoAtual();
			this.semestre = CalendarUtils.getMesAtual() < 7 ? 2 : 1;
		}
		this.listaIdDiscente = null;
		this.registroDiplomaColetivo = null;
		this.nivelEnsino = '0';
		obj = new RegistroDiplomaColetivo();
		obj.setCurso(new Curso());
		this.resultadosBusca = null;
	}
	
	/**
	 * Ver comentário na classe pai.<br/>
	 * 
	 * Método não invocado por JSP´s. <br/>
	 * 
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = discente;
	}
	
	/**
	 * Gera o diploma do discente selecionado.
	 * 
	 * <br/>Método não invocado por JSP´s.
	 */
	public String selecionaDiscente() throws ArqException {
		listaIdDiscente = new ArrayList<Integer>();
		ListaMensagens erros = VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(discente.getDiscente());
		
		if(erros.size() > 0){
			addMensagemWarning("Não foi possível emitir o diploma do discente "+discente.getDiscente().getMatriculaNome()+" pois ele possui empréstimos pendentes nas bibliotecas.");
			return null;
		}
		RegistroDiplomaDao dao = getDAO(RegistroDiplomaDao.class);
		TrabalhoFimCursoDao trabFimDao = getDAO(TrabalhoFimCursoDao.class);
		try {
			registroSegundaVia = dao.findByDiscente(discente.getId());
			if (discente.isGraduacao()) {
				MatrizCurricular matrizCurricular = getGenericDAO().findByPrimaryKey(discente.getId(), DiscenteGraduacao.class).getMatrizCurricular();
				getCurrentRequest().setAttribute("matrizCurricularDiscenteGraduacao", matrizCurricular);
			} else if (discente.isLato()){
				TrabalhoFimCurso trabalhoFimCurso = trabFimDao.findByOrientando(discente); 
				if (ValidatorUtil.isEmpty(trabalhoFimCurso)) {
					addMensagem(MensagensGerais.DISCENTE_SEM_TRABALHO_CONCLUSAO_CURSO_CADASTRADO);
					return null;
				}
			}
			if (registroSegundaVia == null) {
				addMensagem(MensagensGerais.DISCENTE_SEM_REGISTRO_DIPLOMA);
				return null;
			}
			
			this.nivelEnsino = discente.getNivel();
			if (this.segundaVia) {
				this.processo = "";
				getCurrentRequest().setAttribute("registro", registroSegundaVia);
				ResponsavelAssinaturaDiplomasMBean mBean = getMBean("responsavelAssinaturaDiplomasBean");
				mBean.getObj().setNivel(this.nivelEnsino);
				if (isEmpty(registroSegundaVia.getAssinaturaDiploma())) {
					registroSegundaVia.setAssinaturaDiploma(mBean.getResponsaveisAtual());
				}
				return forward("/diplomas/registro_diplomas/segunda_via.jsp");
			} else {
				listaIdDiscente.add(discente.getId());
				gerarPDF();
				return null;
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Registra a observação e imprime a segunda via do diploma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/segunda_via.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String gerarSegundaVia() throws ArqException, NegocioException{
		getCurrentRequest().setAttribute("discenteGraduacao", getGenericDAO().findByPrimaryKey(discente.getId(), DiscenteGraduacao.class));
		getCurrentRequest().setAttribute("registro", registroSegundaVia);
		if (!confirmaProcesso(this.processo)) {
			addMensagem(MensagensGerais.NUMERO_PROCESSO_NAO_ENCONTRADO);
			RegistroDiplomaDao dao = getDAO(RegistroDiplomaDao.class);
			registroSegundaVia = dao.findByDiscente(discente.getId());
			getCurrentRequest().setAttribute("registro", registroSegundaVia);
			return null;
		}
		// registra a impressão da segunda via nas observações.
		listaIdDiscente.add(discente.getId());
		ObservacaoRegistroDiploma obs = new ObservacaoRegistroDiploma();
		obs.setAdicionadoEm(new Date());
		if(isProtocoloAtivo()){
			obs.setObservacao("Segunda via solicitada pelo processo "+processo+".");
		} else {
			obs.setObservacao("Segunda via gerada em " +Formatador.getInstance().formatarData(new Date())+".");
		}
		obs.setRegistroDiploma(registroSegundaVia);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obs);
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		prepareMovimento(ArqListaComando.CADASTRAR);
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		execute(mov);
		gerarPDF();
		return null;
	}
	
	/** Gera o PDF do(s) diploma(s) para impressão.
	 * 
	 */
	private void gerarPDF() {
		try {
			// Popular parâmetros do relatório
			HashMap<String, Object> parametros = new HashMap<String, Object>();

			parametros.put("segunda_via", segundaVia);
			parametros.put("processo", processo);
			// nomes para assinaturas
			ResponsavelAssinaturaDiplomas assinatura = getDAO(ResponsavelAssinaturaDiplomasDao.class).findAtivo(nivelEnsino);
			if (assinatura == null) {
				addMensagemErro("Não há configuração ativa de responsáveis por assinar o diploma.");
				return;
			}
			// reitor
			parametros.put("nomeReitor"                             , assinatura.getNomeReitor());
			parametros.put("descricaoFuncaoReitor"                  , assinatura.getDescricaoFuncaoReitor());
			parametros.put("generoReitor"                           , assinatura.getGeneroReitor());
			// diretor da divisão de registro de diplomas 
			parametros.put("nomeDiretorUnidadeDiplomas"             , assinatura.getNomeDiretorUnidadeDiplomas());
			parametros.put("descricaoFuncaoDiretorUnidadeDiplomas"  , assinatura.getDescricaoFuncaoDiretorUnidadeDiplomas());
			parametros.put("generoDiretorUnidadeDiplomas"           , assinatura.getGeneroDiretorUnidadeDiplomas());
			// pro-reitor de graduação
			parametros.put("nomeDiretorGraduacao"                   , assinatura.getNomeDiretorGraduacao());
			parametros.put("descricaoFuncaoDiretorGraduacao"        , assinatura.getDescricaoFuncaoDiretorGraduacao());
			parametros.put("generoDiretorGraduacao"                 , assinatura.getGeneroDiretorGraduacao());
			// pro-reitor de pós-graduação
			parametros.put("nomeDiretorPosGraduacao"                , assinatura.getNomeDiretorPosGraduacao());
			parametros.put("descricaoFuncaoDiretorPosGraduacao"     , assinatura.getDescricaoFuncaoDiretorPosGraduacao());
			parametros.put("generoDiretorPosGraduacao"              , assinatura.getGeneroDiretorPosGraduacao());
			// lato-sensu
			parametros.put("nomeResponsavelCertificadosLatoSensu"                , assinatura.getNomeResponsavelCertificadosLatoSensu());
			parametros.put("descricaoFuncaoResponsavelCertificadosLatoSensu"     , assinatura.getDescricaoFuncaoResponsavelCertificadosLatoSensu());
			parametros.put("generoResponsavelCertificadosLatoSensu"              , assinatura.getGeneroResponsavelCertificadosLatoSensu());
			// parâmetros institucionais: nomes de instituição, departamentos, etc.
			for (String parametro : RepositorioDadosInstitucionais.getAll().keySet()) {
				parametros.put(parametro, RepositorioDadosInstitucionais.get(parametro));
			}
			parametros.put("idUfDF", ParametroHelper.getInstance().getParametroInt(ParametrosGerais.ID_UF_DISTRITO_FEDERAL));
			
			InputStream relatorio = null;
			String nomeArquivo = "Diploma.pdf";
			if (isGraduacao()) {
				relatorio = JasperReportsUtil.getReportSIGAA("Diploma_Graduacao_Modelo1.jasper");
				nomeArquivo = "Diploma_Graduacao.pdf";
			} else if (isLatoSensu()) {
				relatorio = JasperReportsUtil.getReportSIGAA("Certificado_Lato_Modelo1.jasper");
				nomeArquivo = "Certificado_Lato.pdf";
			} else {
				relatorio = JasperReportsUtil.getReportSIGAA("Diploma_Stricto_Modelo1.jasper");
				nomeArquivo = "Diploma_Stricto.pdf";
			}

			// dados dos discentes
			RegistroDiplomaDao dao = getDAO(RegistroDiplomaDao.class);
			Collection<DadosImpressaoDiploma> dados = dao.findDadosImpressaoDiplomaByIdDiscentes(listaIdDiscente);
			if (isLatoSensu()) {
				for (DadosImpressaoDiploma dado : dados) {
					CursoLato curso = (CursoLato) dado.getRegistro().getDiscente().getCurso();
					dado.setMetodoAvaliacao(curso.getPropostaCurso().getMetodoAvaliacao());
					if ( isEmpty(curso.getHabilitacaoEspecifica())){
						addMensagemErro("Não há habilitação específica definida para o curso: " + curso.getDescricao());
						return;
					}
					
					float media = (float) getDAO(IndiceAcademicoDao.class).calculaIraDiscente(dado.getRegistro().getDiscente().getId());
					dado.setMediaFinal(new BigDecimal(media));
				}
			}
			// Preencher relatório
			JRDataSource jrds = new JRBeanCollectionDataSource(dados);
			JasperPrint prt = JasperFillManager.fillReport(relatorio, parametros, jrds);
			if (prt.getPages().size() == 0) {
				addMensagem(MensagensGerais.DISCENTE_SEM_REGISTRO_DIPLOMA);
				return;
			}
			
			JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(),
					getCurrentResponse(), "pdf");
			FacesContext.getCurrentInstance().responseComplete();

			// loga a operação
			criaLog();
			
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			notifyError(e);
		}
	}
	
	/**
	 *  Cria um log da emissão do diploma.
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private void criaLog() throws ArqException, NegocioException {
		LogGeracaoDiploma log = new LogGeracaoDiploma();
		log.setData(new Date());
		log.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		log.setRegistros(listaIdDiscente);
		log.setSegundaVia(this.segundaVia);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(log);
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		prepareMovimento(ArqListaComando.CADASTRAR);
		execute(mov);
	}

	/**
	 * Busca por registro de diplomas coletivos.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/impressao_coletivo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String buscaRegistroDiplomaColetivo() throws DAOException {
		RegistroDiplomaDao registroDiplomaDao = getDAO(RegistroDiplomaDao.class);
		int anoInicio = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ANO_INICIO_REGISTRO_DIPLOMA);
		ValidatorUtil.validateRange(this.semestre, 1, 2, "Período", erros);
		ValidatorUtil.validateRange(this.ano, anoInicio, CalendarUtils.getAnoAtual(), "Ano", erros);
		if (nivelEnsino == '0')
			erros.addErro("Selecione um Nível de Ensino válido.");
		if (hasErrors())
			return null;
		this.resultadosBusca = registroDiplomaDao.findRegistroColetivoByCursoAnoPeriodo(idCurso, ano, semestre, getNivelEnsino());
		if (ValidatorUtil.isEmpty(resultadosBusca)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		return null;
	}

	/**
	 * Seleciona um registro de diploma coletivo para impressão de diplomas. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/impressao_coletivo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String selecionaDiplomaColetivo() throws ArqException, NegocioException {
		listaIdDiscente = new ArrayList<Integer>();
		Integer idRegistroColetivo = getParameterInt("id");
		if (idRegistroColetivo == null) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS, "Registro de Diploma Coletivo");
			return null;
		}
		RegistroDiplomaColetivoDao registroDiplomaDao = getDAO(RegistroDiplomaColetivoDao.class);
		registroDiplomaColetivo = registroDiplomaDao.findOtimizado(idRegistroColetivo);
		if (registroDiplomaColetivo == null || ValidatorUtil.isEmpty(registroDiplomaColetivo.getRegistrosDiplomas())) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS, "Registro de Diploma Coletivo");
			return null;
		}
	
		
		for (RegistroDiploma registro : registroDiplomaColetivo.getRegistrosDiplomas()) {
			
			ListaMensagens erros = VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(registro.getDiscente());
			if(erros.size() > 0){
				addMensagemWarning("Não é possível emitir o diploma do discente "+registro.getDiscente().getMatriculaNome()+" pois ele possui empréstimos pendentes nas bibliotecas.");
			}else{
				listaIdDiscente.add(registro.getDiscente().getId());
			}
			
		}
		RegistroDiplomaColetivoMBean mbean = getMBean("registroDiplomaColetivo");
		mbean.setObj(registroDiplomaColetivo);
		return forward("/diplomas/registro_diplomas/resumo_coletivo.jsp");
	}
	
	/**
	 * Retorna para a busca por registro de diplomas coletivos.. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/resumo_coletivo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String telaResultadoBuscaColetivo() {
		return forward("/diplomas/registro_diplomas/impressao_coletivo.jsp");
	}

	/**
	 * Gera o PDF com os diplomas de uma turma. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/registro_diplomas/resumo_coletivo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String gerarDiplomaColetivo() throws ArqException, NegocioException {
		gerarPDF();
		return null;
	}
	
	/** Valida os dados para geração do(s) diploma(s): Ano/Período.
	 * <br/>Método não invocado por JSP´s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		int anoInicial = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ANO_INICIO_REGISTRO_DIPLOMA);
		ValidatorUtil.validateRange(this.semestre, 1, 2, "Semestre", mensagens);
		ValidatorUtil.validateRange(this.semestre, anoInicial, CalendarUtils.getAnoAtual(), "Ano", mensagens);
		return hasErrors();
	}
	
	/** Verifica se existe algum processo como número informado.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 */
	public boolean confirmaProcesso(String numeroProtocolo) {
		if(isProtocoloAtivo()){
			ProcessoDTO processo = null;
			try {
				if (numeroProtocolo.length() < 7) {
					addMensagemErro("Número do processo inválido");
					return false;
				}
				String digitoAno = numeroProtocolo.substring(numeroProtocolo.length() - 7, numeroProtocolo.length());
				int numero = Integer.parseInt(numeroProtocolo.substring(6, numeroProtocolo.length()-8));
				int ano = Integer.parseInt(digitoAno.substring(0, digitoAno.length()-3));
				ProcessoDAO procDao = getDAO(ProcessoDAOImpl.class);
				processo = procDao.findByIdentificador(numero, ano, -1);
				if(processo == null) {
					return false;
				} else {
					return true;
				}
			}
			catch (StringIndexOutOfBoundsException e) { 
				return false;
			}
			catch (NumberFormatException e) {
				return false;
			}
			catch (DAOException e) {
				addMensagemErroPadrao();
				notifyError(e);
				return false;
			}
		} else {
			return true;
		}
	}

	/** Informa se o sistema de protocolo está ativo.
	 * @return
	 */
	public boolean isProtocoloAtivo() {
		return Sistema.isSipacAtivo() && Sistema.isProtocoloAtivo();
	}

	/** Retorna o ID do curso da turma que terá a impressão de diplomas coletivo gerado. 
	 * @return
	 */
	public int getIdCurso() {
		return idCurso;
	}

	/** Seta o ID do curso da turma que terá a impressão de diplomas coletivo gerado.
	 * @param idcurso
	 */
	public void setIdCurso(int idcurso) {
		this.idCurso = idcurso;
	}

	/** Retorna o ano da turma que terá a impressão de diplomas coletivo gerado. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano da turma que terá a impressão de diplomas coletivo gerado.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o semestre da turma que terá a impressão de diplomas coletivo gerado. 
	 * @return
	 */
	public int getSemestre() {
		return semestre;
	}

	/** Seta o semestre da turma que terá a impressão de diplomas coletivo gerado.
	 * @param periodo
	 */
	public void setSemestre(int periodo) {
		this.semestre = periodo;
	}

	/** Retorna o número do processo de solicitação de segunda via.
	 * @return
	 */
	public String getProcesso() {
		return processo;
	}

	/** Seta o número do processo de solicitação de segunda via. 
	 * @param processo
	 */
	public void setProcesso(String processo) {
		this.processo = processo;
	}

	public char getNivelEnsino() {
		return nivelEnsino;
	}

	public void setNivelEnsino(char nivelEnsino) {
		this.nivelEnsino = nivelEnsino;
	}

	public boolean isGraduacao() {
		return NivelEnsino.GRADUACAO == nivelEnsino;
	}
	
	public boolean isLatoSensu() {
		return NivelEnsino.LATO == nivelEnsino;
	}

	public RegistroDiplomaColetivo getRegistroDiplomaColetivo() {
		return registroDiplomaColetivo;
	}

	public void setRegistroDiplomaColetivo(
			RegistroDiplomaColetivo registroDiplomaColetivo) {
		this.registroDiplomaColetivo = registroDiplomaColetivo;
	}

	public List<Integer> getListaIdDiscente() {
		return listaIdDiscente;
	}

	public void setListaIdDiscente(List<Integer> listaIdDiscente) {
		this.listaIdDiscente = listaIdDiscente;
	}
	
}
