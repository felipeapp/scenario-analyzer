package br.ufrn.sigaa.projetos.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dao.projetos.AvaliacaoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;

/**
 * MBean responsável por emitir as declarações dos avaliadores dos projetos Integrados
 * 
 * @author Jean Guerethes
 */
@Component @Scope("session")
public class DeclaracaoAvaliadorMBean extends 
	SigaaAbstractController<Avaliacao> implements AutValidator {

	/** Representa todas as avaliações disponíveis para o avaliador. */
	private DataModel avaliacoes;

	/** Indica se está se verificando a autenticidade do documento */
	private boolean verificando = false;
	
	/** Atributo que representa o comprovante de autenticidade do documento */
	private EmissaoDocumentoAutenticado comprovante;
	
	/** Atributo que representa o nome do documento */
	public static final String DECLARACAO = "declaracao_avaliadores_associados";
	
	public DeclaracaoAvaliadorMBean() {
		obj = new Avaliacao();
		avaliacoes = new ListDataModel();
	}

	/**
	 *  Lista todas as avaliações de projetos para o usuário logado.
	 *  Chamado por Jsp:
	 *  <ul>
	 *  	<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *  </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String listarAvaliacoes() throws DAOException {
		avaliacoes = new ListDataModel( getDAO(AvaliacaoDao.class).findByAvaliador(getUsuarioLogado(), true) );
		return forward("/projetos/DeclaracaoCertificado/lista.jsf");
	}

	/**
	 *  Método utilizado para a emissão do certificado de Avaliador dos projeto Integrados.
	 *  Chamado por Jsp:
	 *  <ul>
	 *  	<li>/SIGAA/app/sigaa.ear/sigaa.war/projetos/DeclaracaoCertificado/lista.jsp</li>
	 *  </ul>
	 * 
	 * @return
	 * @throws JRException
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws IOException
	 */
	public String emitirCertificado() throws JRException, ArqException, NegocioException, IOException {
		if ( !avaliacoes.isRowAvailable() ) {
			verificando = true;
		} else {
			setObj( (Avaliacao) avaliacoes.getRowData() );
		}
		
		// texto da declaração
		String texto = getTextoDeclaracao();
		
		if ( !verificando ) {
			comprovante = geraEmissao(
					TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO,
					((Integer) obj.getId()).toString(), gerarSementeDeclaracao(), null, 
					SubTipoDocumentoAutenticado.DECLARACAO_AVALIADOR_ASSOCIADOS, true);
		}
	
		// Setando os parametros
		HashMap<Object, Object> parametros = new HashMap<Object, Object>();
		parametros.put("codigo_seguranca", comprovante.getCodigoSeguranca());
		parametros.put("numero_documento", comprovante.getNumeroDocumento());
		parametros.put("site_verificacao",
				ParametroHelper.getInstance().getParametro(
						ConstantesParametro.ENDERECO_AUTENTICIDADE));
		parametros.put("texto", texto);
		parametros.put(
				"cidade",
				"Natal, "
						+ Formatador.getInstance()
								.formatarDataDiaMesAno(
										comprovante.getDataEmissao()));
		parametros.put(
				"pro_reitor_pesquisa",
				ParametroHelper.getInstance().getParametro(
						ParametrosPesquisa.NOME_PRO_REITOR_PESQUISA));
		parametros.put(
				"pro_reitor_extensao",
				ParametroHelper.getInstance().getParametro(
						ParametrosExtensao.NOME_PRO_REITOR_EXTENSAO));

		parametros.put(
				"pro_reitor_monitoria",
				ParametroHelper.getInstance().getParametro(
						ParametrosMonitoria.NOME_PRO_REITOR_GRADUACAO));

		// Preencher declaração
		Collection<Avaliacao> lista = new ArrayList<Avaliacao>();
		lista.add(obj);
		JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(
				lista);
		JasperPrint prt = JasperFillManager.fillReport(
				JasperReportsUtil
						.getReportSIGAA(DECLARACAO + ".jasper"),
				parametros, jrds);
	
		if (prt.getPages().size() == 0) {
			addMensagemErro("Não foi encontrado nenhum vínculo com plano passível de emissão de declaração.");
			return null;
		}
	
		getCurrentResponse().setContentType("application/pdf");
		getCurrentResponse().addHeader(
				"Content-Disposition",
				"attachment; filename=DECLARACAO_AVALIADOR_PROJETO.pdf");
		JasperExportManager.exportReportToPdfStream(prt,
				getCurrentResponse().getOutputStream());

		FacesContext.getCurrentInstance().responseComplete();
		
		return null;
	}

	/**
	 * Método que gera a semente para validação da declaração
	 * 
	 * @return
	 */
	private String gerarSementeDeclaracao() {
		StringBuilder semente = new StringBuilder();
		semente.append( obj.getId());
		semente.append( obj.getParecer() );
		semente.append( obj.isAtivo() );
		semente.append( obj.getDataAvaliacao() );
		semente.append( obj.getSituacao().getId() );
		semente.append(TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO);
		return semente.toString();
	}

	/**
	 * Método que gera o texto da declaração de bolsista
	 * 
	 * @return
	 */
	private String getTextoDeclaracao() {

		StringBuilder texto = new StringBuilder();
		texto.append("Certificamos que o senhor(a) "
				+ obj.getAvaliador().getNome() );
		texto.append(" realizou avaliação de "
				+ obj.getDistribuicao().getModeloAvaliacao().getTipoAvaliacao().getDescricao() );
		texto.append(" sendo o mesmo(a) um(a) avaliador(a) do tipo "
				+ obj.getDistribuicao().getTipoAvaliador().getDescricao() );
		texto.append(" do(a) "
				+ obj.getAvaliador().getUnidade().getNome() );
		texto.append(" e, em  "
				+ Formatador.getInstance().formatarDataDiaMesAno(
						obj.getDataAvaliacao()));
		texto.append(", emitiu parecer no projeto " 
				+ obj.getProjeto().getTitulo() );
		return texto.toString();
	}
	
	@Override
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
 		AvaliacaoDao dao = getDAO(AvaliacaoDao.class);
		try {
			String codVerificacao = "";

			if (comprovante.getSubTipoDocumento().equals(SubTipoDocumentoAutenticado.DECLARACAO_AVALIADOR_ASSOCIADOS) ) {
				obj = dao.findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), Avaliacao.class);
				if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
					codVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSementeDeclaracao());
				}
			}
				
			if (codVerificacao.equals(comprovante.getCodigoSeguranca()))
				return true;

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
		return false;
	}

	@Override
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {
		AvaliacaoDao dao = getDAO(AvaliacaoDao.class);
		this.comprovante = comprovante;

		try {
			obj = dao.findByPrimaryKey(
					Integer.parseInt(comprovante.getIdentificador()),
					Avaliacao.class);
			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO)
				emitirCertificado();
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
	}

	public DataModel getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(DataModel avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public boolean isVerificando() {
		return verificando;
	}

	public void setVerificando(boolean verificando) {
		this.verificando = verificando;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}
	
}