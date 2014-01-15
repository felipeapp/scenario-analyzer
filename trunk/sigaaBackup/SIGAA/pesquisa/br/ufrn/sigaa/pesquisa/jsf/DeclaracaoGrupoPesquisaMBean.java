package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
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
import br.ufrn.sigaa.arq.dao.pesquisa.MembroGrupoPesquisaDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;

/**
 * @author Jean Guerethes
 */
@Scope("session") @Component
public class DeclaracaoGrupoPesquisaMBean extends SigaaAbstractController<MembroGrupoPesquisa>  implements AutValidator {

	private EmissaoDocumentoAutenticado comprovante;
	
	private boolean verificando = false;
	
	/** Código de segurança para validação do relatório */
	private String codigoSeguranca;
	
	private String textoDeclaracao;
	
	// nomes dos documentos
	public static final String DECLARACAO = "DeclaracaoGrupoPesquisa";
	
	public DeclaracaoGrupoPesquisaMBean() {
		clear();
	}

	private void clear() {
		obj = new MembroGrupoPesquisa();
		comprovante = new EmissaoDocumentoAutenticado();
		verificando = false;
	}
	
	@Override
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);

		try {
			obj = dao.findByPrimaryKey(Integer.parseInt(comprovante
					.getIdentificador()), MembroGrupoPesquisa.class);
			
			String codigoVerificacao = "";
			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO)
				codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(
						comprovante, gerarSementeDeclaracao());

			if ( codigoVerificacao.equals(comprovante.getCodigoSeguranca()) && obj.isAtivo() )
				return true;

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}

		return false;
	}

	/**
	 * Gera a semente utilizada no código de autenticação da declaração.
	 * Deve-se utilizar para geração da semente, todos os dados variáveis da 
	 * declaração.
	 * 
	 * Não é chamado por jsp.
	 * 
	 * @return
	 */
	private String gerarSementeDeclaracao() {
		StringBuilder builder = new StringBuilder();

		builder.append(obj.getId());
		builder.append(obj.getPessoa().getId());
		builder.append(obj.getGrupoPesquisa().getId());
		builder.append(getTextoDeclaracao().hashCode());
		builder.append(TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO);
		return builder.toString();
	}
	
	public void selecionarMembroGrupoPesquisa() throws DAOException, SegurancaException {
 		clear();
		setId();
		MembroGrupoPesquisaDao dao = getDAO(MembroGrupoPesquisaDao.class);
		try {
			obj = dao.findByMembroGrupoPesq(obj);
			
			emitirDeclaracao();
			
		} finally {
			dao.close();
		}
	}
	
	@Override
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {

		MembroGrupoPesquisaDao dao = getDAO(MembroGrupoPesquisaDao.class);
		this.comprovante = comprovante;
		verificando = true;
		
		try {
			obj = dao.findByPrimaryKey(Integer.parseInt(comprovante
					.getIdentificador()), MembroGrupoPesquisa.class);

			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO)
				emitirDeclaracao();

		} catch (DAOException e) {
			notifyError(e);
			addMensagemErroPadrao();
		} catch (SegurancaException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
	}

	private String emitirDeclaracao() throws DAOException, SegurancaException {
		MembroGrupoPesquisaDao dao = getDAO(MembroGrupoPesquisaDao.class);

		try {
			if (validarEmissaoDeclaracao()) {
				// montagem do corpo da declaração
				textoDeclaracao = getTextoDeclaracao();

				// Gerar declaração
				Connection con = null;
				HashMap<Object, Object> parametros = new HashMap<Object, Object>();

				// gerando código de autenticação...
				if (!verificando) {					
					comprovante = geraEmissao(
							TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO,
							((Integer) obj.getId()).toString(),
							gerarSementeDeclaracao(), obj.getDataFim() != null ? obj.getDataFim().toString() : null, 
							SubTipoDocumentoAutenticado.DECLARACAO_GRUPO_PESQUISA, true);
				}

				// setando parâmetros
				parametros.put("codigoSeguranca", comprovante.getCodigoSeguranca());
				parametros.put("siteVerificacao", ParametroHelper.getInstance()
						.getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));

				parametros.put("cidade", "Natal, "
						+ Formatador.getInstance().formatarDataDiaMesAno(
								comprovante.getDataEmissao()));
				parametros.put("texto", textoDeclaracao);
				parametros.put("numero_documento", comprovante.getNumeroDocumento());

				// Preencher declaração
				con = Database.getInstance().getSigaaConnection();
				JasperPrint prt = JasperFillManager.fillReport(
						JasperReportsUtil.getReportSIGAA(DECLARACAO+".jasper"),
						parametros, con);

				getCurrentResponse().setContentType("application/pdf");
				
				String nomeDeclaracao = obj.getPessoa().getNomeResumido().replace(" ", "_"); 
				getCurrentResponse().addHeader("Content-Disposition",
						"attachment; filename=DECLARACAO_"+nomeDeclaracao+".pdf");
				JasperExportManager.exportReportToPdfStream(prt,
						getCurrentResponse().getOutputStream());

				FacesContext.getCurrentInstance().responseComplete();
			}
			
			return null;
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informações da declaração.");
		} finally {
			dao.close();
		}
		return null;
	}
	
	/**
	 * Utilizado na validação para emissão da declaração.
	 * Verifica se o usuário atual pode imprimir a declaração.
	 * 
	 * Não chamado por jsp.
	 * 
	 * @return
	 */
	private boolean validarEmissaoDeclaracao() {

		if (obj == null) {
			addMensagemErro("Declaração não encontrada.");
			return false;
		}	

		return true;
	}

	/**
	 * Retorna o texto(corpo) da declaração.
	 * 
	 * @return
	 */
	private String getTextoDeclaracao() {
		String result = "";

		if ((obj != null) && (obj.getId() > 0)) {

			switch (obj.getCategoriaMembro().getId()) {
				case CategoriaMembro.DOCENTE:
	
					result = "Declaramos, que o(a) Professor(a) "
							+ obj.getPessoa().getNome()
							+ ", matrícula "
							+ obj.getServidor().getSiape() + ",";
					
				if ( isEmpty(obj.getDataFim()) )
					result += " participa";
				else
					result += " participou";
						
					result += " do Grupo de Pesquisa "
							+ obj.getGrupoPesquisa().getNomeCompacto()
							+ " na função de "
							+ obj.getTipoMembroGrupoPesqString()
							+ " no período de "
							+ Formatador.getInstance().formatarData(obj.getDataInicio());
							
					if ( isEmpty(obj.getDataFim()) )
						result += " até o presente momento.";
					else
						result += " até " + Formatador.getInstance().formatarData(obj.getDataFim());
					
					break;

			case CategoriaMembro.DISCENTE:

					result = "Declaramos, que o(a) Discente "
							+ obj.getPessoa().getNome()
							+ ", matrícula "
							+ obj.getDiscente().getMatricula()+ ",";
					
					if ( isEmpty(obj.getDataFim()) )
						result += " participa";
					else
						result += " participou";
					
					result += " do Grupo de Pesquisa "
						+ obj.getGrupoPesquisa().getNomeCompacto()
						+ " na função de "
						+ obj.getTipoMembroGrupoPesqString()
						+ " no período de "
						+ Formatador.getInstance().formatarData(obj.getDataInicio());
						
					if ( isEmpty(obj.getDataFim()) )
						result += " até o presente momento.";
					else
						result += " até " + Formatador.getInstance().formatarData(obj.getDataFim());
					
					break;
							
			case CategoriaMembro.EXTERNO:

					result = "Declaramos, que "
							+ obj.getPessoa().getNome()
							+ ", CPF "
							+ obj.getPessoa().getCpfCnpjFormatado()+ ",";
					
					if ( isEmpty(obj.getDataFim()) )
						result += " participa";
					else
						result += " participou";
					
					result += " do Grupo de Pesquisa "
						+ obj.getGrupoPesquisa().getNomeCompacto()
						+ " na função de "
						+ obj.getTipoMembroGrupoPesqString()
						+ " no período de "
						+ Formatador.getInstance().formatarData(obj.getDataInicio());
						
					if ( isEmpty(obj.getDataFim()) )
						result += " até o presente momento.";
					else
						result += " até " + Formatador.getInstance().formatarData(obj.getDataFim());
					
					break;

			case CategoriaMembro.SERVIDOR:

					result = "Declaramos, que o(a) Servidor(a) "
							+ obj.getPessoa().getNome()
							+ ", matrícula "
							+ obj.getServidor().getSiape()+ ",";
					
					if ( isEmpty(obj.getDataFim()) )
						result += " participa";
					else
						result += " participou";
					
					result += " do Grupo de Pesquisa "
						+ obj.getGrupoPesquisa().getNomeCompacto()
						+ " na função de "
						+ obj.getTipoMembroGrupoPesqString()
						+ " no período de "
						+ Formatador.getInstance().formatarData(obj.getDataInicio());
						
					if ( isEmpty(obj.getDataFim()) )
						result += " até o presente momento.";
					else
						result += " até " + Formatador.getInstance().formatarData(obj.getDataFim());
					
					break;
		    }

		}

		return result;
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

	public String getCodigoSeguranca() {
		return codigoSeguranca;
	}

	public void setCodigoSeguranca(String codigoSeguranca) {
		this.codigoSeguranca = codigoSeguranca;
	}

	public void setTextoDeclaracao(String textoDeclaracao) {
		this.textoDeclaracao = textoDeclaracao;
	}

}