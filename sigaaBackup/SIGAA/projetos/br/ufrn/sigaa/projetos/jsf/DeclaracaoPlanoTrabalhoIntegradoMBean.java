package br.ufrn.sigaa.projetos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dao.projetos.AvaliacaoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.PlanoTrabalhoProjetoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.projetos.dominio.PlanoTrabalhoProjeto;

/**
 * MBean responsável por emitir as declarações dos discentes com plano 
 * de trabalho nos projetos Integrados
 * 
 * @author Jean Guerethes
 */
@Component @Scope("session")
public class DeclaracaoPlanoTrabalhoIntegradoMBean extends 
	SigaaAbstractController<PlanoTrabalhoProjeto> implements AutValidator {

	/** Indica se está se verificando a autenticidade do documento */
	private boolean verificando = false;
	
	/** Atributo que representa o comprovante de autenticidade do documento */
	private EmissaoDocumentoAutenticado comprovante;
	
	/** Atributo que representa o nome do documento */
	public static final String DECLARACAO = "declaracao_associados";

	public DeclaracaoPlanoTrabalhoIntegradoMBean() {
		clear();
	}
	
	private void clear(){
		obj = new PlanoTrabalhoProjeto();
	}

	/**
	 * Responsável pelo carregamento das informações do Membro do Projeto, para que se possa emitir o certificado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/projetos/MembroProjeto/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String emitir() throws DAOException {
		PlanoTrabalhoProjetoDao dao = getDAO(PlanoTrabalhoProjetoDao.class);
		MembroProjetoDao membroDao = getDAO(MembroProjetoDao.class); 
		try {
			clear();
			setId();
			setObj( dao.findByPrimaryKey(obj.getId(), PlanoTrabalhoProjeto.class) );
			obj.getProjeto().setCoordenador( membroDao.findCoordenadorAtualProjeto(obj.getProjeto().getId()) );  
			verificando = false;
		} finally {
			dao.close();
			membroDao.close();
		}
		return emitirCertificado();
	}
	
	/**
	 * Criação do Certificado de Membro de Projeto de Ações Integradas
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>Não invocado por JSP</li>
	 *	</ul>
	 * @return
	 */
	public String emitirCertificado() {
		try {
			// texto da declaração
			String texto = getTextoDeclaracao();
			
			if ( !verificando ) {
				comprovante = geraEmissao(
						TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO,
						((Integer) obj.getId()).toString(), gerarSementeDeclaracao(), null, 
						SubTipoDocumentoAutenticado.DECLARACAO_PLANO_ASSOCIADOS, true);
			}
		
			// Setando os parametros
			HashMap<Object, Object> parametros = new HashMap<Object, Object>();
			parametros.put("codigoSeguranca", comprovante.getCodigoSeguranca());
			parametros.put("siteVerificacao", ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));

			parametros.put("cidade",  RepositorioDadosInstitucionais.get("cidadeInstituicao")+", " + Formatador.getInstance().formatarDataDiaMesAno( comprovante.getDataEmissao()));
			parametros.put("texto", texto);
			parametros.put("coordenacao", obj.getProjeto().getCoordenador().getNomeMembroProjeto());
			parametros.put("numero_documento", comprovante.getNumeroDocumento());
			
			// Preencher declaração
			Collection<PlanoTrabalhoProjeto> lista = new ArrayList<PlanoTrabalhoProjeto>();
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
					"attachment; filename=DECLARACAO_ASSOCIADAS.pdf");
			JasperExportManager.exportReportToPdfStream(prt,
					getCurrentResponse().getOutputStream());

			FacesContext.getCurrentInstance().responseComplete();
		
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informações da declaração.");
			return null;
		}
		
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
		semente.append( obj.getTipoVinculo().getId() );
		semente.append( obj.isAtivo() );
		semente.append( obj.getDataInicio() );
		semente.append( obj.getDataFim() );
		semente.append(TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO);
		semente.append(getTextoDeclaracao());
		return semente.toString();
	}

	/**
	 * Método que gera o texto da declaração de bolsista
	 * 
	 * @return
	 */
	private String getTextoDeclaracao() {
		StringBuilder texto = new StringBuilder();
			texto.append("Declaramos que o(a) Discente(a) "
					+ obj.getDiscenteProjeto().getDiscente().getNome() );
			texto.append(", matrícula " + obj.getDiscenteProjeto().getDiscente().getMatricula() );
		texto.append(", está participando do Projeto \"" + obj.getProjeto().getTitulo() + "\"" );
		texto.append(", promovido pelo(a) " + obj.getProjeto().getUnidade().getNome() );
		texto.append(", com vínculo de " + obj.getDiscenteProjeto().getTipoVinculo().getDescricao() );
		texto.append(" no período de " + Formatador.getInstance().formatarDataDiaMesAno(obj.getDataInicio() ) 
				+ " a " + Formatador.getInstance().formatarDataDiaMesAno(obj.getDataFim()) + ".");
		return texto.toString();
	}
	
	@Override
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
 		AvaliacaoDao dao = getDAO(AvaliacaoDao.class);
		try {
			String codVerificacao = "";

			if (comprovante.getSubTipoDocumento().equals(SubTipoDocumentoAutenticado.DECLARACAO_PLANO_ASSOCIADOS) ) {
				obj = dao.findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), PlanoTrabalhoProjeto.class);
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
					PlanoTrabalhoProjeto.class);
			verificando = true;
			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO)
				emitirCertificado();
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
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