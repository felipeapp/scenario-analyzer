/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/03/2009
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;

/*******************************************************************************
 * MBean respons�vel por gerar certificados para os membros da equipe
 * organizadora da a��o de extens�o. <br>
 * A autentica��o dos documentos � realizada pelo SIGAA atrav�s da gera��o de
 * um c�digo de documento e c�digo de verifica��o que pode ser validado atrav�s
 * do portal p�blico do SIGAA.
 * 
 * <i>
 * 		<p>Para emitir o comprante autenticado, � preciso implementar a interface <code>AutValidator</code> <br/>
 *    	criar um novo tipo de Documento em <code>TipoDocumentoAutenticado</code> e registrar essa classe 
 *    	no arquivo <code>/br/ufrn/arg/seguranca/autenticacao/validadores.properties</code>
 * 		</p>
 * 		<p>
 * 	   			A interface <code>AutValidator</code> possui 2 m�todos: <br/>
 * 				<ul>
 * 					<li>validaDigest:  valida se o documento de quita��o � verdadeiro </li>
 * 					<li>exibir: chamado apenas se o usu�rio desejar visualizar o documento novamente no momento da valida��o.</li>
 * 				</ul>
 * 		</p>
 * </i>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Component("certificadoMonitor")
@Scope("request")
public class CertificadoMonitorMBean extends SigaaAbstractController<DiscenteMonitoria> implements AutValidator {

	/** Atributo utilizado para representar os nomes dos documentos */
	public static final String CERTIFICADO = "trf14318_CertificadoMonitoria";
	/** Atributo utilizado para representar uma Collection de projetos */
	private Collection<ProjetoEnsino> projetos = new HashSet<ProjetoEnsino>();
	/** Atributo utilizado para representar uma Collection de discentes de monitoria */
	private Collection<DiscenteMonitoria> discentesMonitoria = new HashSet<DiscenteMonitoria>();
	/** Atributo utilizado para representar um �nico discente de monitoria */
	private DiscenteMonitoria discenteMonitoria = new DiscenteMonitoria();
	/** Atributo utilizado para representar o comprovante de autenticidade do certificado */
	private EmissaoDocumentoAutenticado comprovante;
	/** Atributos utilizados para representar as datas de in�cio e fim do per�odo de participa��o no projeto */
	private Date data_inicio, data_fim;
	/** Atributo utilizado para representar o Id gerado */
	private String idGerado;
	/** Atributo utilizado para representar se a monitoria pode ou n�o continuar */
	private boolean monitoriaContinua = false;
	/** Atributo utilizado para representar se o certificado est� sendo verificado */
	private boolean verificando = false;
	/**
	 * Construtor padr�o
	 */
	public CertificadoMonitorMBean(){}

	/**
	 * Exibe os membros da atividade selecionada para emiss�o dos certificados
	 * <br /><br />
	 * M�todo n�o invocado por JSP�s
	 *  
	 * @throws DAOException
	 * 
	 */
	public String selecionarDiscente() {

		verificando = false;
		int idProjeto = Integer.parseInt(getParameter("idProjeto"));
		GenericDAO dao = getGenericDAO();
		try {
			discentesMonitoria = dao.findByExactField(DiscenteMonitoria.class,	"projetoEnsino.id", idProjeto);
		} catch (DAOException e) {
			notifyError(e);
		}

		return forward(ConstantesNavegacaoMonitoria.DOCUMENTOS_AUTENTICADOS_LISTA_DISCENTES);
		
	}

	/**
	 * Gera semente para valida��o do documento (ISSO N�O � COMENT�RIO !!!)
	 * 
	 * <p>M�todo para gerar a semente do certificado de monitoria, usado para valida��o do documento
	 *  , um vez que se algumas das informa��es mudar, o documento � invalidado.</p>
	 *  
	 * <p>A semente � gerada com a seguinte l�gica:  id discente monitoria + id projeto ensino + id discente + tipo documento</p>
	 * 
	 * <br /><br />
	 * JSP: N�o Invocado.
	 * @return
	 */
	private String gerarSementeCertificado() {
		StringBuilder builder = new StringBuilder();

		if (monitoriaContinua)
			builder.append(idGerado);
		else	
			builder.append(discenteMonitoria.getId());
		
		builder.append(discenteMonitoria.getProjetoEnsino().getId());
		builder.append(discenteMonitoria.getDiscente().getId());
		builder.append(TipoDocumentoAutenticado.CERTIFICADO);
		
		return builder.toString();
	}

	/**
	 * Gera o certificado para o discente selecionado
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li>/monitoria/ConsultarMonitor/lista.jsp</li>
	 *   <lu>/monitoria/DocumentosAutenticados/lista_discentes.jsp</li>
	 * </ul>
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws JRException
	 * 
	 */
	public String emitirCertificado() throws DAOException, SegurancaException {

		GenericDAO dao = null;
		Connection con = null;
		
		try {
			
			dao =  getGenericDAO();
			
			// Quando vai imprimir para o discente selecionado
			if(discenteMonitoria == null || discenteMonitoria.getId() <= 0){
				Integer idMonitor = getParameterInt("id",0);
				discenteMonitoria = dao.findByPrimaryKey(idMonitor, DiscenteMonitoria.class);			
			}else{
				// quando est� reimprimindo para verificar a autenticidade, nesse caso j� vem populado.
			}
			
			Collection<DiscenteMonitoria> lista = new ArrayList<DiscenteMonitoria>();
			
			Collection<DiscenteMonitoria> listaTemp = dao.findByExactField(DiscenteMonitoria.class, "discente.id", discenteMonitoria.getDiscente().getId());
			
			// Recuperar todas as datas inicias das monitorias as quais o discente pertence.
			for (DiscenteMonitoria monitor : listaTemp) { 
				if (monitor.isFinalizado() && monitor.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA) {
					lista.add(monitor);
				}
			}
			
			DiscenteMonitoria[] monitorias = lista.toArray(new DiscenteMonitoria[lista.size()]);
			DiscenteMonitoria maior;
			
			// ordenar as monitorias pelas datas inicias.
			for (int i = monitorias.length - 1; i > 0; i--) {
				for (int j = 0; j < i; j++) {
					if (monitorias[j].getDataInicio().after(monitorias[j + 1].getDataInicio())) {
						maior = monitorias[j];
						monitorias[j] = monitorias[j + 1];
						monitorias[j + 1] = maior;
					}
				}
			}
			maior = null;
			lista = null;
			monitoriaContinua = false;
			idGerado = new String();
			/* 
			 * verificar nas monitorias do discente se existe continuidade entres as monitorias, sendo 
			 * que entre duas monitorias continuas, o tipo (remunerado, n�o remunerado) deve ser diferente. 
			 * O certificado deve ser �nico para monitorias cont�nuas.
			 */
			for (int i = 0; i < monitorias.length - 1; i++) {
				int dif = CalendarUtils.calculaQuantidadeDiasEntreDatasIntervaloFechado( monitorias[i].getDataFim(), monitorias[i + 1].getDataInicio());
				if ((dif == 0 || dif == 1)
					&& monitorias[i].getTipoVinculo() != monitorias[i + 1].getTipoVinculo()) {
					if (i == 0) {
						data_inicio = monitorias[i].getDataInicio();
						// gera identificador para monitorias continuas.
						idGerado += String.valueOf(monitorias[i].getId());
						monitoriaContinua = true;
					}
					// data final da monitoria ser� a data final da segunda monitoria.
					data_fim = monitorias[i + 1].getDataFim();
				} else
					break;
			}
			
			if (validarEmissaoCertificado()) {

				// montagem do corpo da declara��o
				String texto = getTextoCertificado();

				// gerar declara��o
				
				HashMap<Object, Object> parametros = new HashMap<Object, Object>();

				// gerando c�digo de autentica��o...
				if (!verificando) {
					comprovante = geraEmissao(
							TipoDocumentoAutenticado.CERTIFICADO,
							monitoriaContinua ? idGerado : ((Integer) discenteMonitoria.getId()).toString(),
							gerarSementeCertificado(),
							null,
							SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_MONITORIA,
							true);
				}

				// Configurando os par�metros
				parametros.put("codigoSeguranca", comprovante.getCodigoSeguranca());
				parametros.put("siteVerificacao", ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));
				
				parametros.put("universidade", RepositorioDadosInstitucionais.get("nomeInstituicao").toUpperCase());
				parametros.put("pro_reitoria", "PR�-REITORIA DE GRADUA��O"); // fixo por enquanto
				
				parametros.put("cidade", RepositorioDadosInstitucionais.get("cidadeInstituicao")+", " + Formatador.getInstance().formatarDataDiaMesAno(comprovante.getDataEmissao()));
				parametros.put("texto", texto);
				parametros.put("pro_reitor", ParametroHelper.getInstance().getParametro(ParametrosMonitoria.NOME_PRO_REITOR_GRADUACAO));
				parametros.put("coordenacao", ParametroHelper.getInstance().getParametro(ParametrosMonitoria.NOME_COORDENADOR_DIDATICO_PEDAGOGICO));
				
				parametros.put("unidadeCoordenador", ParametroHelper.getInstance().getParametro(ParametrosMonitoria.UNIDADE_DIDATICO_PEDAGOGICO));
				parametros.put("cargoCoordenador", ParametroHelper.getInstance().getParametro(ParametrosMonitoria.CARGO_DIDATICO_PEDAGOGICO));
				
				parametros.put("numero_documento", comprovante.getNumeroDocumento());

				parametros.put("caminhoLogoTipoInstituicao",   RepositorioDadosInstitucionais.get("logoInstituicao"));
				
				// Preencher declara��o

				con = Database.getInstance().getSigaaConnection();
				JasperPrint prt = JasperFillManager.fillReport(
						JasperReportsUtil.getReportSIGAA(CERTIFICADO + ".jasper"), parametros, con);

				getCurrentResponse().setContentType("application/pdf");
				getCurrentResponse().addHeader(
						"Content-Disposition",
						"attachment; filename=CERTIFICADO_MONITORIA_"
								+ discenteMonitoria.getDiscente().getPessoa()
										.getNomeAscii() + ".pdf");
				JasperExportManager.exportReportToPdfStream(prt,
						getCurrentResponse().getOutputStream());

				FacesContext.getCurrentInstance().responseComplete();

			}

			return null;

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informa��es do certificado.");
			return null;
		}finally{
			if(dao != null ) dao.close();
			if(con != null )
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}
	
	/**
	 * Retorna o texto(corpo) do certificado.
	 * 
	 * @return
	 */
	private String getTextoCertificado() {
		String result = "";

		if ((discenteMonitoria != null) && (discenteMonitoria.getId() > 0)) {

		    String localCoordenador = null;
		    if ((discenteMonitoria.getProjetoEnsino().getCoordenacao().getUnidade().isUnidadeAcademica()) 
			    && (discenteMonitoria.getProjetoEnsino().getCoordenacao().getUnidade().isDepartamento())){
			
			localCoordenador = " do " + discenteMonitoria.getProjetoEnsino().getCoordenacao().getUnidade().getNome() 
				+ " do "	+ discenteMonitoria.getProjetoEnsino().getUnidade().getNome();
		    }else {
			localCoordenador = " do " + discenteMonitoria.getProjetoEnsino().getUnidade().getNome();
		    }
			
			
			result = "Certificamos que o(a) discente "
					+ discenteMonitoria.getDiscente().getPessoa().getNomeOficial()
					+ ", matr�cula "
					+ discenteMonitoria.getDiscente().getMatricula()
					+ ", participou do Projeto de Ensino '"
					+ discenteMonitoria.getProjetoEnsino().getTitulo()
					+ "', coordenado pelo(a) Professor(a) "
					+ discenteMonitoria.getProjetoEnsino().getCoordenacao().getNome()
					+","
					+ localCoordenador
					+ ", no per�odo de ";
//			if (monitoriaContinua) 
//				result += Formatador.getInstance().formatarDataDiaMesAno(
//						data_inicio) + " a "
//						+ Formatador.getInstance().formatarDataDiaMesAno(
//								data_fim) + ", com carga hor�ria total de "
//								+ discenteMonitoria.getProjetoEnsino().getEditalMonitoria().getFatorCargaHoraria()* CalendarUtils.calculaQuantidadeSemanasEntreDatasIntervaloFechado(data_inicio, data_fim) 
//								+" horas.";
//			else
				result += Formatador.getInstance().formatarDataDiaMesAno(
						discenteMonitoria.getDataInicio())
						+ " a "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								discenteMonitoria.getDataFim()) + ", com carga hor�ria total de "
						+ discenteMonitoria.getProjetoEnsino().getEditalMonitoria().getFatorCargaHoraria()* CalendarUtils.calculaQuantidadeSemanasEntreDatasIntervaloFechado(discenteMonitoria.getDataInicio(), discenteMonitoria.getDataFim()) 
						+" horas.";

		}
		return result;
	}

	/** 
	 * Verifica a exist�ncia de certificado e se o discente que participa de um projeto de monitoria
	 * possui direito a receber o certificado.
	 * 
	 */
	private boolean validarEmissaoCertificado() {

		if (discenteMonitoria == null) {
			addMensagemErro("Certificado n�o encontrado.");
			return false;
		}

		if ((!discenteMonitoria.isRecebeCertificado())) {
			addMensagemErro("Usu�rio n�o tem direito ao certificado porque n�o est� finalizado no projeto ou n�o enviou os relat�rios exigidos.");
			return false;
		}

		return true;
	}

	/**
	 * Emite e exibi o comprovante.
	 * <br /><br />
	 * M�todo n�o invocado por JSP�s
	 * 
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante, HttpServletRequest req, HttpServletResponse res) {

		MembroProjetoDao dao = null;
		this.comprovante = comprovante;

		try {

			dao = getDAO(MembroProjetoDao.class);
			
			discenteMonitoria = dao.findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), DiscenteMonitoria.class);

			verificando = true;
			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO)
				emitirCertificado();

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}finally{
			if(dao != null) dao.close();
		}
	}

	/**
	 * Valida��o e gera c�digo de valida��o para o documento.
	 * <br /><br />
	 * M�todo n�o invocado por JSP�s
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);

		try {
			discenteMonitoria = dao.findByPrimaryKey(Integer
					.parseInt(comprovante.getIdentificador()),
					DiscenteMonitoria.class);

			String codigoVerificacao = "";

			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO)
				codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(
						comprovante, gerarSementeCertificado());

			if (codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
				return true;

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}

		return false;
	}

	public Collection<ProjetoEnsino> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoEnsino> projetos) {
		this.projetos = projetos;
	}

	public Collection<DiscenteMonitoria> getDiscentesMonitoria() {
		return discentesMonitoria;
	}

	public void setDiscentesMonitoria(
			Collection<DiscenteMonitoria> discentesMonitoria) {
		this.discentesMonitoria = discentesMonitoria;
	}

	public DiscenteMonitoria getDiscenteMonitoria() {
		return discenteMonitoria;
	}

	public void setDiscenteMonitoria(DiscenteMonitoria discenteMonitoria) {
		this.discenteMonitoria = discenteMonitoria;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	public boolean isVerificando() {
		return verificando;
	}

	public void setVerificando(boolean verificando) {
		this.verificando = verificando;
	}

}