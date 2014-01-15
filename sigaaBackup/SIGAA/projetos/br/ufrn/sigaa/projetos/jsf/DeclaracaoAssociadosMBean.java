package br.ufrn.sigaa.projetos.jsf;

import java.sql.Connection;
import java.util.Calendar;
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

import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * MBean respons�vel por gerar declara��es para os membros da equipe organizadora dos projeto associados.
 * 
 * @author guerethes
 */
@Component @Scope("session")
public class DeclaracaoAssociadosMBean extends SigaaAbstractController<MembroProjeto> implements	AutValidator {

	/** Tela de listagem das a��es pass�veis de emiss�o de certificados ou declara��es */
	public static final String MEMBROS_ATIVIDADE = "/projetos/DeclaracaoCertificado/certificados_declaracoes_discente.jsp";

	/** Nome do arquivo do JasperReports a ser carregado para emiss�o da declara��o. */
	public static final String DECLARACAO = "declaracaoAssociados";

	/** Participa��es em projetos */
	private Collection<MembroProjeto> membros = new HashSet<MembroProjeto>();
	
	/** Participa��o no projeto selecionado. */
	private MembroProjeto membro = new MembroProjeto();
	
	/** Comprovante de emiss�o de documento autenticado. */
	private EmissaoDocumentoAutenticado comprovante;

	/** Indica se o usu�rio est� verificando a atenticidade de uma declara��o emitida. */
	private boolean verificando = false;
	
	/**
	 * Exibe os membros da atividade selecionada
	 * para emiss�o dos certificados.
	 * 
	 * Chamado por: /sigaa.war/extensao/DocumentosAutenticados/form.jsp
	 * 
	 * @throws DAOException
	 * 
	 */
	public String selecionarMembro() {
		verificando = false;
		int id = Integer.parseInt(getParameter("id"));
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
		try {
		    membros = dao.findAtivosByProjeto(id);
		} catch (DAOException e) {
		    notifyError(e);
		}
		return forward(MEMBROS_ATIVIDADE);
	}
	
	/**
	 * Gera a semente utilizada no c�digo de autentica��o da declara��o.
	 * Deve-se utilizar para gera��o da semente, todos os dados vari�veis da 
	 * declara��o.
	 * 
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>N�o invocado por JSP</li>
	 *	</ul>
	 * 
	 * @return
	 */
	private String gerarSementeDeclaracao() {
		StringBuilder builder = new StringBuilder();
		builder.append(membro.getId());
		builder.append(membro.getProjeto().getId());
		builder.append(membro.getFuncaoMembro().getId());
		builder.append(membro.getDataInicio());
		builder.append(membro.getDataFim());
		builder.append(membro.isAtivo());
		builder.append(TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO);
		return builder.toString();
	}

	/**
	 * Gera a declara��o para o membro selecionado.
	 * 
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/sigaa.war/projetos/DeclaracaoCertificado/certificados_declaracoes_discente.jsp</li>
	 *	</ul>
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws JRException
	 * 
	 */
	public String emitirDeclaracao() throws DAOException, SegurancaException {

		try {
		    
			GenericDAO dao = getGenericDAO();
			membro = dao.findByPrimaryKey(membro.getId(), MembroProjeto.class);
			dao.refresh(membro.getProjeto().getCoordenador());

			if (validarEmissaoDeclaracao()) {

				// montagem do corpo da declara��o
				String texto = getTextoDeclaracao();

				// Gerar declara��o
				Connection con = null;
				HashMap<Object, Object> parametros = new HashMap<Object, Object>();

				// gerando c�digo de autentica��o...
				if (!verificando) {					
					comprovante = geraEmissao(
							TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO,
							((Integer) membro.getId()).toString(),
							gerarSementeDeclaracao(), 
							null,  
							SubTipoDocumentoAutenticado.DECLARACAO_PARTICIPANTE_MEMBRO_ASSOCIADOS, true);

				}

				// setando par�metros
				parametros.put("codigoSeguranca", comprovante.getCodigoSeguranca());
				parametros.put("siteVerificacao", ParametroHelper.getInstance()
						.getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));

				parametros.put("cidade", "Natal, "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								comprovante.getDataEmissao()));
				parametros.put("texto", texto);
				parametros.put("pro_reitor_extensao", ParametroHelper.getInstance().getParametro(ParametrosExtensao.NOME_PRO_REITOR_EXTENSAO));
				parametros.put("pro_reitor_pesquisa", ParametroHelper.getInstance().getParametro(ParametrosPesquisa.NOME_PRO_REITOR_PESQUISA));
				parametros.put("pro_reitor_monitoria",ParametroHelper.getInstance().getParametro(ParametrosMonitoria.NOME_PRO_REITOR_GRADUACAO));
				parametros.put("coordenacao", membro.getProjeto().getCoordenador().getPessoa().getNomeAbreviado());
				parametros.put("numero_documento", comprovante.getNumeroDocumento());

				// Preencher declara��o
				con = Database.getInstance().getSigaaConnection();
				JasperPrint prt = JasperFillManager.fillReport(
						JasperReportsUtil.getReportSIGAA(DECLARACAO + ".jasper"),
						parametros, con);

				getCurrentResponse().setContentType("application/pdf");
				getCurrentResponse().addHeader("Content-Disposition",
						"attachment; filename=DECLARACAO_"+ membro.getPessoa().getNomeAscii() +".pdf");
				JasperExportManager.exportReportToPdfStream(prt,
						getCurrentResponse().getOutputStream());

				FacesContext.getCurrentInstance().responseComplete();

			}

			return null;

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informa��es da declara��o.");
			return null;
		}

	}

	/**
	 * Retorna o texto(corpo) da declara��o.
	 * 
	 * @return
	 */
	private String getTextoDeclaracao() {
		String result = "";
		if ((membro != null) && (membro.getId() > 0)) {
			
		    String participouOuParticipa = membro.getDataFim().after(new Date()) ? "participa": "participou";
			String cargaHorariaSeCursoEvento = " com carga hor�ria de " + membro.getChCertificadoDeclaracao() + " horas";
			
			String dataInicioFormatada = null;
			if (membro.getDataInicio() != null) {
			    dataInicioFormatada  = Formatador.getInstance().formatarDataDiaMesAno(membro.getDataInicio());
			}
			
			String dataFimFormatada = null;
			if (membro.getDataFim() != null) {
			    Date hoje =  DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			    if (hoje.before(membro.getDataFim())) {
				dataFimFormatada  = Formatador.getInstance().formatarDataDiaMesAno(new Date());
			    }else {
				dataFimFormatada  = Formatador.getInstance().formatarDataDiaMesAno(membro.getDataFim());
			    }
			}

			
			switch (membro.getCategoriaMembro().getId()) {
				case CategoriaMembro.DOCENTE:

				result = "Declaramos, que o(a) Professor(a) "
						+ membro.getPessoa().getNome()
						+ ", matr�cula "
						+ membro.getServidor().getSiape()
						+ ", "+ participouOuParticipa +" do Projeto '"
						+ membro.getProjeto().getTitulo()
						+ "'" + cargaHorariaSeCursoEvento + ", promovida pelo(a) "
						+ membro.getProjeto().getUnidade().getNome()
						+ " na fun��o de "
						+ membro.getFuncaoMembro().getDescricao()
						+ ", no per�odo de "
						+ dataInicioFormatada
						+ " a "
						+ dataFimFormatada + ".";
	
				break;
	
				case CategoriaMembro.DISCENTE:
	
				result = "Declaramos, que o(a) Discente "
						+ membro.getPessoa().getNome()
						+ ", matr�cula "
						+ membro.getDiscente().getMatricula()
						+ ", "+ participouOuParticipa +" do Projeto '"
						+ membro.getProjeto().getTitulo()
						+ "'" + cargaHorariaSeCursoEvento + ", promovida pelo(a) "
						+ membro.getProjeto().getUnidade().getNome()
						+ " na fun��o de "
						+ membro.getFuncaoMembro().getDescricao()
						+ ", no per�odo de "
						+ dataInicioFormatada
						+ " a "
						+ dataFimFormatada + ".";
	
				break;
	
				case CategoriaMembro.EXTERNO:
	
				result = "Declaramos, que "
						+ membro.getPessoa().getNome()
						+ ", CPF "
						+ membro.getPessoa().getCpfCnpjFormatado()
						+ ", "+ participouOuParticipa +" do Projeto '"
						+ membro.getProjeto().getTitulo()
						+ "'" + cargaHorariaSeCursoEvento + ", promovida pelo(a) "
						+ membro.getProjeto().getUnidade().getNome()
						+ " na fun��o de "
						+ membro.getFuncaoMembro().getDescricao()
						+ ", no per�odo de "
						+ dataInicioFormatada
						+ " a "
						+ dataFimFormatada + ".";
	
				break;
	
				case CategoriaMembro.SERVIDOR:
	
				result = "Declaramos, que o(a) Servidor(a) "
						+ membro.getPessoa().getNome()
						+ ", matr�cula "
						+ membro.getServidor().getSiape()
						+ ", "+ participouOuParticipa +" do Projeto '"
						+ membro.getProjeto().getTitulo()
						+ "'" + cargaHorariaSeCursoEvento + ", promovida pelo(a) "
						+ membro.getProjeto().getUnidade().getNome()
						+ " na fun��o de "
						+ membro.getFuncaoMembro().getDescricao()
						+ ", no per�odo de "
						+ dataInicioFormatada
						+ " a "
						+ dataFimFormatada + ".";
	
				break;
			}

		}

		return result;
	}

	/**
	 * Utilizado na valida��o para emiss�o da declara��o.
	 * Verifica se o usu�rio atual pode imprimir a declara��o.
	 * 
	 * N�o chamado por jsp.
	 * 
	 * @return
	 */
	private boolean validarEmissaoDeclaracao() {

		if (membro == null) {
			addMensagemErro("Declara��o n�o encontrada.");
			return false;
		}	
		return true;
	}

	public Collection<MembroProjeto> getMembros() {
		return membros;
	}

	public void setMembros(Collection<MembroProjeto> membros) {
		this.membros = membros;
	}

	public MembroProjeto getMembro() {
		return membro;
	}

	public void setMembro(MembroProjeto membro) {
		this.membro = membro;
	}

	/**
	 * Utilizado na valida��o da autenticidade da declara��o
	 * a partir do portal p�blico.
	 * 
	 * N�o chamado diretamente por jsp.
	 * 
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {

		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
		this.comprovante = comprovante;
		verificando = true;

		try {

			membro = dao.findByPrimaryKey(Integer.parseInt(comprovante
					.getIdentificador()), MembroProjeto.class);

			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO)
				emitirDeclaracao();

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
	}

	/**
	 * Utilizado na valida��o da autenticidade da declara��o
	 * a partir do portal p�blico. Valida o c�digo de autentica��o e o n�mero 
	 * do documento.
	 *  
	 * N�o � chamado diretamento por jsps. 
	 * 
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);

		try {
			membro = dao.findByPrimaryKey(Integer.parseInt(comprovante
					.getIdentificador()), MembroProjeto.class);

			String codigoVerificacao = "";
			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO)
				codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(
						comprovante, gerarSementeDeclaracao());

			if (codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
				return true;

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}

		return false;
	}

}
