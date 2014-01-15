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
import java.util.Collection;
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
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/*******************************************************************************
 * MBean respons�vel por gerar declara��es para os membros da
 * equipe organizadora da a��o de extens�o.
 * <br>
 * A autentica��o dos documentos � realizada pelo SIGAA atrav�s da gera��o de
 * um c�digo de documento e c�digo de verifica��o que pode ser validado atrav�s
 * do portal p�blico do SIGAA.
 * 
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/

@Component("declaracaoMonitoria")
@Scope("request")
public class DeclaracaoMonitoriaMBean extends SigaaAbstractController<Object> implements
		AutValidator {

	/** constantes navega��o */
	public static final String MEMBROS_ATIVIDADE = "/extensao/DocumentosAutenticados/membros.jsp";

	/** nomes dos documentos */
	public static final String DECLARACAO = "trf10189_DeclaracaoProex";

	private Collection<AtividadeExtensao> atividades = new HashSet<AtividadeExtensao>();
	private Collection<MembroProjeto> membros = new HashSet<MembroProjeto>();
	private MembroProjeto membro = new MembroProjeto();

	private EmissaoDocumentoAutenticado comprovante;

	private boolean verificando = false;
	
	/**
	 * Exibe os membros da atividade selecionada
	 * para emiss�o dos certificados
	 * 
	 * M�todo n�o invocado por JSP�s
	 * 
	 * @throws DAOException
	 * 
	 */
	public String selecionarMembro() {

		verificando = false;
		int idAtividade = Integer.parseInt(getParameter("idAtividade"));
		GenericDAO dao = getGenericDAO();
		try {
			membros = dao.findByExactField(MembroProjeto.class, "atividade.id",idAtividade);
		} catch (DAOException e) {
			notifyError(e);
		}

		return forward(MEMBROS_ATIVIDADE);

	}

	/**
	 * Dados para gerar declara��o �nica.
	 * @return
	 */
	private String gerarSementeDeclaracao() {
		StringBuilder builder = new StringBuilder();

		builder.append(membro.getId());
		builder.append(membro.getProjeto().getId());
		builder.append(membro.getFuncaoMembro().getId());
		builder.append(TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO);

		return builder.toString();
	}

	/**
	 * Gera a declara��o para o membro selecionado
	 * 
	 * M�todo n�o invocado por JSP�s
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws JRException
	 * 
	 */
	public String emitirDeclaracao() throws DAOException, SegurancaException {

		try {
			
			GenericDAO dao = getGenericDAO();
			membro = dao.findByPrimaryKey(membro.getId(),MembroProjeto.class);
			dao.refresh(membro.getProjeto().getCoordenador());

			if (validarEmissaoDeclaracao()) {

				// montagem do corpo da declara��o
				String texto = getTextoDeclaracao();

				// Gerar declara��o
				Connection con = null;
				HashMap<String, String> parametros = new HashMap<String, String>();

				// gerando c�digo de autentica��o...
				if (!verificando) {

					
					comprovante = geraEmissao(
							TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO,
							((Integer) membro.getId()).toString(),
							gerarSementeDeclaracao(), 
							null,  
							SubTipoDocumentoAutenticado.DECLARACAO_DISCENTE_MONITORIA, true);

				}

				// configurando par�metros
				parametros.put("codigoSeguranca", comprovante
						.getCodigoSeguranca());
				parametros.put("siteVerificacao", ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));

				parametros.put("cidade", "Natal, "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								comprovante.getDataEmissao()));
				parametros.put("texto", texto);
				parametros.put("pro_reitor", ParametroHelper.getInstance().getParametro(ParametrosExtensao.NOME_PRO_REITOR_EXTENSAO));
				parametros.put("coordenacao", membro.getProjeto().getCoordenador().getPessoa().getNome());
				parametros.put("numero_documento", comprovante
						.getNumeroDocumento());

				// Preencher declara��o
				con = Database.getInstance().getSigaaConnection();
				JasperPrint prt = JasperFillManager.fillReport(
						JasperReportsUtil
								.getReportSIGAA(DECLARACAO + ".jasper"),
						parametros, con);

				getCurrentResponse().setContentType("application/pdf");
				getCurrentResponse().addHeader("Content-Disposition",
						"attachment; filename=DECLARACAO_PROEX_"+ membro.getPessoa().getNomeAscii() +".pdf");
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

			switch (membro.getCategoriaMembro().getId()) {
			case CategoriaMembro.DOCENTE:

				result = "Declaramos, que o(a) Professor(a) "
						+ membro.getPessoa().getNome()
						+ ", Matr�cula "
						+ membro.getServidor().getSiape()
						+ ", participou da A��o de Extens�o '"
						+ membro.getProjeto().getTitulo()
						+ "', promovida pelo(a) "
						+ membro.getProjeto().getUnidade().getNome()
						+ " na fun��o de "
						+ membro.getFuncaoMembro().getDescricao()
						+ " com "
						+ membro.getChDedicada()
						+ " horas de atividades desenvolvidas, no per�odo de "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								membro.getProjeto().getDataInicio())
						+ " a "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								membro.getProjeto().getDataFim()) + ".";

				break;

			case CategoriaMembro.DISCENTE:

				result = "Declaramos, que o(a) Discente "
						+ membro.getPessoa().getNome()
						+ ", Matr�cula "
						+ membro.getDiscente().getMatricula()
						+ ", participou da A��o de Extens�o '"
						+ membro.getProjeto().getTitulo()
						+ "', promovida pelo(a) "
						+ membro.getProjeto().getUnidade().getNome()
						+ " na fun��o de "
						+ membro.getFuncaoMembro().getDescricao()
						+ " com "
						+ membro.getChDedicada()
						+ " horas de atividades desenvolvidas, no per�odo de "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								membro.getProjeto().getDataInicio())
						+ " a "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								membro.getProjeto().getDataFim()) + ".";

				break;

			case CategoriaMembro.EXTERNO:

				result = "Declaramos, que "
						+ membro.getPessoa().getNome()
						+ ", CPF "
						+ membro.getPessoa().getCpfCnpjFormatado()
						+ ", participou da A��o de Extens�o '"
						+ membro.getProjeto().getTitulo()
						+ "', promovida pelo(a) "
						+ membro.getProjeto().getUnidade().getNome()
						+ " na fun��o de "
						+ membro.getFuncaoMembro().getDescricao()
						+ " com "
						+ membro.getChDedicada()
						+ " horas de atividades desenvolvidas, no per�odo de "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								membro.getProjeto().getDataInicio())
						+ " a "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								membro.getProjeto().getDataFim()) + ".";

				break;

			case CategoriaMembro.SERVIDOR:

				result = "Declaramos, que o(a) Servidor(a) "
						+ membro.getPessoa().getNome()
						+ ", Matr�cula "
						+ membro.getServidor().getSiape()
						+ ", participou da A��o de Extens�o '"
						+ membro.getProjeto().getTitulo()
						+ "', promovida pelo(a) "
						+ membro.getProjeto().getUnidade().getNome()
						+ " na fun��o de "
						+ membro.getFuncaoMembro().getDescricao()
						+ " com "
						+ membro.getChDedicada()
						+ " horas de atividades desenvolvidas, no per�odo de "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								membro.getProjeto().getDataInicio())
						+ " a "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								membro.getProjeto().getDataFim()) + ".";

				break;

			}
		}

		return result;
	}
	
	/** 
	 * Valida o membro do projeto
	 * 
	 */
	private boolean validarEmissaoDeclaracao() {

		if (membro == null) {
			addMensagemErro("Declara��o n�o encontrada.");
			return false;
		}

		/*
		 * TODO: verificar permiss�es de impress�o da declara��o
		 */

		return true;

	}


	public Collection<AtividadeExtensao> getAtividades() {
		return atividades;
	}

	public void setAtividades(Collection<AtividadeExtensao> atividades) {
		this.atividades = atividades;
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
	 * M�todo n�o invocado por JSP�s, usado para emitir Declara��o.
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
	 * Usado na valida��o de um documento autenticado.
	 * <br/>
	 * M�todo n�o invocado por JSP�s
	 * 
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
