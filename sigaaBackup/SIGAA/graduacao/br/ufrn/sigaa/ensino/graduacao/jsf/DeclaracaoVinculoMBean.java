/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 21, 2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.comum.dominio.Municipio;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean utilizado para a emissão de declarações de vínculos para discentes
 *
 * @author Victor Hugo
 * @author Ricardo Wendell
 *
 */
@Component("declaracaoVinculo") @Scope("request")
public class DeclaracaoVinculoMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente, AutValidator {

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
	
	/** Atributo que define o a declaração de vínculo. */
	private EmissaoDocumentoAutenticado comprovante;

	/** Atributo que define o estado da geração da declaração de vínculo. */
	private boolean verificando;

	public DeclaracaoVinculoMBean() throws SegurancaException {
	}

	/**
	 * Emitir declaração para o próprio discente
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String emitirDeclaracao() throws DAOException {

		// Validar discente do usuário
		if (getUsuarioLogado().getDiscenteAtivo() == null) {
			addMensagemErro("Somente discentes podem obter declarações de vínculo com a instituição.");
			return null;
		}

		setDiscente(getUsuarioLogado().getDiscenteAtivo());
		return emitir();
	}

	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/menus/aluno.jsp</li>
	 *	<li>/sigaa.war/graduacao/menus/coordenacao.jsp</li>
	 *	<li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 */
	public String buscarDiscente() throws SegurancaException{
		checkRole(new int[] { SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.DAE, SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.GESTOR_NEE,
				SigaaPapeis.GESTOR_TRADUCAO_DOCUMENTOS, SigaaPapeis.TRADUTOR_DADOS_ACADEMICOS});

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.DECLARACAO_VINCULO);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Redirecionar para a emissão de declaração
	 * Método não invocado por JSP's.
	 * @throws DAOException 
	 */
	public String selecionaDiscente() throws DAOException {
		return emitir();
	}

	/**
	 * Emitir a declaração de vínculo para o discente
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	private String emitir() throws DAOException {
		DiscenteAdapter discente = obj;

		// Validar discente
		if (discente == null) {
			addMensagemErro("É necessário definir o discente para emitir uma declaração de vínculo.");
			return null;
		}

		/** Validar status do discente
		 *  Para os discentes graduação com status cadastrado é permitido a emissão da declaração de cadastro.
		 */
		if ( (!discente.isGraduacao() && discente.getStatus() == StatusDiscente.CADASTRADO) || !StatusDiscente.getStatusComVinculo().contains(discente.getStatus()) || !discente.isRegular()) {
			addMensagemErro("O discente não possui um vínculo ativo com a instituição.");
			return null;
		}		
		
		if (discente.getCurso().getUnidade().getMunicipio() == null) {
			addMensagemErro("O município da unidade (" + discente.getCurso().getUnidade().getNome() + ") a que pertece o curso não foi definida.");
			return null;
		}

		try {
			
			if ( comprovante == null && !verificando) {
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
			
			hs.put("cgcInstituicao", getGenericDAO().findByPrimaryKey(UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class).getCnpjFormatado() );
			hs.put("nomeDepartamento", getNomeDepartamento());
			hs.put("contatoDepartamento", getContatoDepartamento());
			hs.put("hoje", Formatador.getInstance().formatarDataExtenso(new Date()) );
			
			hs.put("cidadeInstituicao", RepositorioDadosInstitucionais.getAll().get("cidadeInstituicao"));
			hs.put("admEscolar", ParametroHelper.getInstance().getParametro(ParametrosGraduacao.NOME_ADM_ESCOLAR));
			hs.put("proReitoria", ParametroHelper.getInstance().getParametro(ParametrosGraduacao.NOME_GESTAO_GRADUACAO));
			hs.put("siglaAdmEscolar", ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_ADM_ESCOLAR).toUpperCase());

			if (discente.isGraduacao()){
				DiscenteDao dao = getDAO(DiscenteDao.class);
				List<DiscenteGraduacao> discentesGrad = new ArrayList<DiscenteGraduacao>();
				DiscenteGraduacao dg =  (DiscenteGraduacao) dao.findByPK(obj.getDiscente().getId());
				discentesGrad.add(dg);	
				// Preparar emissão do PDF
				JRDataSource jrds = new JRBeanCollectionDataSource(discentesGrad);
				// Emitir PDF
				JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(getModeloDeclaracao()) , hs, jrds);
				getCurrentResponse().setContentType("application/pdf");
				getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=declaracao_"+discente.getMatricula()+".pdf");
				JasperExportManager.exportReportToPdfStream(prt,getCurrentResponse().getOutputStream());
			}else{
				List<DiscenteAdapter> discentes = new ArrayList<DiscenteAdapter>();
				discentes.add(discente);
				// Preparar emissão do PDF
				JRDataSource jrds = new JRBeanCollectionDataSource(discentes);
				// Emitir PDF
				JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(getModeloDeclaracao()) , hs, jrds);
				getCurrentResponse().setContentType("application/pdf");
				getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=declaracao_"+discente.getMatricula()+".pdf");
				JasperExportManager.exportReportToPdfStream(prt,getCurrentResponse().getOutputStream());			
			}

			FacesContext.getCurrentInstance().responseComplete();
		} catch(Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagemErroPadrao();
		}
		return null;
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
	 * Método não invocado por JSP's.
	 * @return
	 */
	private String getModeloDeclaracao() {
		switch (obj.getNivel()) {
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
	 * Método não invocado por JSP's.
	 * @return
	 */
	private String getNomeDepartamento() {
		ParametroHelper paramHelper = ParametroHelper.getInstance();
		switch (obj.getNivel()) {
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
	 * Método não invocado por JSP's.
	 * @return
	 * @throws DAOException 
	 */
	private String getContatoDepartamento() throws DAOException {
		ParametroHelper paramHelper = ParametroHelper.getInstance();
		ParametrosGestoraAcademica paramGestoraAcademic = ParametrosGestoraAcademicaHelper.getParametros(obj);
		switch (obj.getNivel()) {
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

	/**
	 * Gerar semente para validação do documento
	 * Método não invocado por JSP's.
	 * @return
	 */
	private String gerarSemente() throws DAOException {
		StringBuilder builder = new StringBuilder();

		// Utilizar curso, município do curso e status do discente
		builder.append( obj.getCurso().getId());
		
		if ( !obj.isLato() && !obj.isResidencia() && !obj.isMedio() && !obj.isTecnico() )
			builder.append( obj.getCurso().getMunicipio().getId());
		
		if ( obj.isTecnico() || obj.isMedio() )
			builder.append( obj.getMatricula() );

		if ( obj.isGraduacao() ) {
			DiscenteDao dao = getDAO(DiscenteDao.class);
			DiscenteGraduacao dg =  (DiscenteGraduacao) dao.findByPK(obj.getDiscente().getId());
			builder.append( dg.getMatrizCurricular().getDescricao());
		}
		
		builder.append( StatusDiscente.getStatusComVinculo().contains(obj.getStatus()) );

		return builder.toString();
	}
	
	/**
	 * Visualiza o comprovante.
	 * Método não invocado por JSP's.
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {

		this.comprovante = comprovante;
		verificando = true;

		try {
			if (validaDigest(comprovante)) {
				emitir();
			}
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
	}

	/**
	 * Valida o comprovante.
	 * Método não invocado por JSP's.
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			Collection<Discente> discentes = dao.findByExactField(Discente.class, "matricula", new Long(comprovante.getIdentificador()));
			for ( Discente discente : discentes ) {
				obj = discente;
				String codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSemente());
				if (codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
					return true;
			}
		} catch (Exception e ) {
			addMensagemErroPadrao();
			notifyError(e);
		}
		return false;
	}

	/**
	 * Método não invocado por JSP's.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(DiscenteAdapter)
	 */
	public void setDiscente(DiscenteAdapter discente) throws DAOException {
		this.obj = discente;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return this.comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	public boolean isVerificando() {
		return this.verificando;
	}

	public void setVerificando(boolean verificando) {
		this.verificando = verificando;
	}
}
