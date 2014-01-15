package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.apedagogica.dominio.ParametrosAP;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;

/**
 * Controller que gerencia a emissão de certificados de participação em atividades de atualização
 * pedagógica.
 * @author Mário Rizzi
 *
 */
@Component("certificadoParticipacaoAP") @Scope("request")
public class CertificadoParticipacaoAtividadeAPMBean  extends
	SigaaAbstractController<ParticipanteAtividadeAtualizacaoPedagogica> 
	implements AutValidator,OperadorParticipanteAtividadeAP{

	/** Atributo utilizado para representar o nome do Certificado */
	public static final String CERTIFICADO = "CertificadoParticipanteAtualizacaoPedagogica";
	
	/** Atributo utilizado para representar o comprovante */
	private EmissaoDocumentoAutenticado comprovante;
	
	/** Atributo que define o participante do certificado */
	private ParticipanteAtividadeAtualizacaoPedagogica participante;
	
	/** Atributo que define o status de verificação do certificado. */
	private Boolean verificando = false;
	
	
	/**
	 * Efetua a consulta dos participantes.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
 	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String consultar() throws SegurancaException{
		
		checkChangeRole();
		ConsultaParticipanteAPMBean consultaMBean = (ConsultaParticipanteAPMBean) getMBean("consultaParticipanteAP");
		consultaMBean.clearFiltros();
		consultaMBean.setCodigoOperacao(OperacaoParticipanteAtividadeAP.EMITI_CERTIFICADO_PARTICIPACAO);
		consultaMBean.setAll(null);
		consultaMBean.setResultadosBusca(null);
		
		return forward( consultaMBean.getFormPage() );
	}
	
	/**
	 * Verifica se o usuário possui o papel informado.
	 * Método não invocado por JSP's.
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#checkChangeRole() 
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		isUserInRole(SigaaPapeis.GESTOR_PAP);
	}
	
	/**
	 * Valida o código do certificado.
	 * Método não invocado por JSP's.
	 */
	@Override
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		
		try {
			participante = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante
					.getIdentificador()), ParticipanteAtividadeAtualizacaoPedagogica.class);

			String codigoVerificacao = "";
			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO) {
				codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSementeCertificado());
			}

			if (codigoVerificacao.equals(comprovante.getCodigoSeguranca())) {
				return true;
			}

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}

		return false;
	}

	/**
	 * Exibe o certificado do participante.
	 * Método não invocado por JSP's.
	 */
	@Override
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {
		
		this.comprovante = comprovante;		
		verificando = true;

		try {

			participante = getGenericDAO().findByPrimaryKey(Integer.parseInt(comprovante
					.getIdentificador()), ParticipanteAtividadeAtualizacaoPedagogica.class);

			if ( comprovante.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO ) {
				emitirCertificado();
			}

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
		
	}
	
	/**
	 * Gera o certificado. 
	 * @throws DAOException
	 * @throws SegurancaException
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/lista.jsp</li>
 	 * </ul>   
	 * 
	 */
	public String emitirCertificado() throws DAOException, SegurancaException {
		
		try {
				
			Integer idParticipante = getParameterInt("id");
			
			if( !ValidatorUtil.isEmpty(idParticipante) )
				participante = getGenericDAO().findAndFetch(getParameterInt("id"), ParticipanteAtividadeAtualizacaoPedagogica.class, "atividade");
				participante.getAtividade().setGrupoAtividade(getGenericDAO().refresh(participante.getAtividade().getGrupoAtividade()));
				HashMap<String,String> parametros = new HashMap<String,String>();

				if(!verificando) {
					comprovante = geraEmissao(
							TipoDocumentoAutenticado.CERTIFICADO,
							((Integer) participante.getId()).toString(),
							gerarSementeCertificado(),
							null,
							SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_ATUALIZACAO_PEDAGOGICA,
							true);
				}

				// setando parâmetros
				parametros.put("docente", participante.getDocente().getNomeOficial());
				parametros.put("siape", String.valueOf(participante.getDocente().getSiape()));
				parametros.put("atividade", participante.getAtividade().getNome().toUpperCase());
				parametros.put("cargaHoraria", !isEmpty(participante.getAtividade().getCh())?participante.getAtividade().getCh().toString():"");
				parametros.put("periodo", participante.getAtividade().getDescricaoPeriodo());
				parametros.put("descricao", participante.getAtividade().getDescricao());
				parametros.put("cidadeEstado", RepositorioDadosInstitucionais.getAll().get("cidadeInstituicao") );
				parametros.put("ementa", "");
				parametros.put("proreitorGraduacao", ParametroHelper.getInstance().getParametro(ParametrosMonitoria.NOME_PRO_REITOR_GRADUACAO) );
				parametros.put("proreitorRecursosHumanos",  ParametroHelper.getInstance().getParametro(ParametrosGerais.NOME_PRO_REITOR_RESCURSOS_HUMANOS) );
				parametros.put("codigoSeguranca", comprovante.getCodigoSeguranca());
				parametros.put("numeroDocumento", comprovante.getNumeroDocumento());
				parametros.put("urlImagemCertificado",ParametroHelper.getInstance().getParametro(ParametrosAP.URL_IMAGEM_CERTIFICADO));
				parametros.put("siteVerificacao", ParametroHelper.getInstance()
						.getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));
				Collection<ParticipanteAtividadeAtualizacaoPedagogica> lista = new ArrayList<ParticipanteAtividadeAtualizacaoPedagogica>();
				lista.add(participante);
				
				JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
				JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(CERTIFICADO + ".jasper"), parametros, jrds);
		
				FacesContext.getCurrentInstance().responseComplete();
				
				
				if (prt.getPages().size() == 0) {
					addMensagemErro("Erro");
					return null;
				}
				
				JasperReportsUtil.exportar(prt, "CERTIFICADO_ATUALIZACAO_PEDAGOGICA_" + participante.getId()+ ".pdf",
							getCurrentRequest(), getCurrentResponse(), "pdf");

				FacesContext.getCurrentInstance().responseComplete();

			return null;

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informações da declaração.");
			return null;
		}

	}
	
	/**
	 * Gerar semente para validação do documento
	 * 
	 * @return
	 */
	private String gerarSementeCertificado() {
		StringBuilder builder = new StringBuilder();

		builder.append(participante.getId());
		builder.append(participante.getAtividade().getId());		
		builder.append(participante.getDocente().getSiape());
		builder.append(TipoDocumentoAutenticado.CERTIFICADO);

		return builder.toString();
	}
	
	
	/**	
	
	 * Realiza as consultas dos dados da atividades e seta para emissão
	 * do certificado.
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/ConsultaParticipanteAPedagogica/busca_participante.jsp</li>
 	 * </ul>
	 */
	@Override
	public String selecionaParticipante() {
		try {
			emitirCertificado();
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}


	/**
	 * Define o participante.
	 * Método não invocado por JSP's.
	 */
	@Override
	public void setParticipante(
			ParticipanteAtividadeAtualizacaoPedagogica participante) {
		this.participante = participante;
	}

	public ParticipanteAtividadeAtualizacaoPedagogica getParticipante() {
		return participante;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

}
