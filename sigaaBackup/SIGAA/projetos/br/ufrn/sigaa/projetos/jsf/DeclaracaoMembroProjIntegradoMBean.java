package br.ufrn.sigaa.projetos.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
import br.ufrn.sigaa.arq.dao.projetos.AvaliacaoDao;
import br.ufrn.sigaa.arq.dao.projetos.DiscenteProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * MBean responsável por emitir as declarações dos avaliadores dos projetos Integrados
 * 
 * @author Jean Guerethes
 */
@Component @Scope("session")
public class DeclaracaoMembroProjIntegradoMBean extends 
	SigaaAbstractController<MembroProjeto> implements AutValidator {

	/** Indica se está se verificando a autenticidade do documento */
	private boolean verificando = false;
	
	/** Atributo que representa o comprovante de autenticidade do documento */
	private EmissaoDocumentoAutenticado comprovante;
	
	/** Atributo que representa o nome do documento */
	public static final String DECLARACAO = "declaracao_membro_associados";

	/** Participações em projetos */
	private DataModel membros;

    /** Lista de discentes do projeto. */
	private Collection<DiscenteProjeto> discentesProjeto = new ArrayList<DiscenteProjeto>();

	public DeclaracaoMembroProjIntegradoMBean() {
		obj = new MembroProjeto();
	}

	/**
	 * Busca as participações em projetos de ações associadas do discente logado.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/sigaa.ear/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String participacoesDiscenteUsuarioLogado() throws DAOException, SegurancaException {
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
		DiscenteProjetoDao discenteDao = getDAO(DiscenteProjetoDao.class);
		try {
			if ((getDiscenteUsuario() != null) && (getDiscenteUsuario().getPessoa() != null)) {
				Discente d = getDiscenteUsuario().getDiscente();
				membros = new ListDataModel( dao.findMembrosAssociadosByDiscente(d.getId()) );
				discentesProjeto = discenteDao.findByDiscenteComPlanoTrabalho(getDiscenteUsuario().getId(), null);
			} else {
				membros = new ListDataModel();
			}
		} finally {
			dao.close();
			discenteDao.close();
		}
		
		return forward("/projetos/DeclaracaoCertificado/certificados_declaracoes_discente.jsp");
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
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
		try {
			setObj( dao.findByPrimaryKey(obj.getId(), MembroProjeto.class) );
			verificando = false;
		} finally {
			dao.close();
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
						SubTipoDocumentoAutenticado.DECLARACAO_MEMBROS_ASSOCIADOS, true);
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
			Collection<MembroProjeto> lista = new ArrayList<MembroProjeto>();
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
					"attachment; filename=CERTIFICADO_ACOES_ASSOCIADAS.pdf");
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
		semente.append( obj.getFuncaoMembro().getId() );
		semente.append( obj.isAtivo() );
		semente.append( obj.getDataInicio() );
		semente.append( obj.getDataFim() );
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
		if ( !isEmpty(obj.getServidor()) ) {
			texto.append("Certificamos que o(a) Professor(a) "
					+ obj.getServidor().getNome() );
			texto.append(", matrícula " + obj.getServidor().getSiape() );
		} else if ( !isEmpty(obj.getDiscente()) ) {
			texto.append("Certificamos que o(a) Discente(a) "
					+ obj.getDiscente().getNome() );
			texto.append(", matrícula " + obj.getDiscente().getMatricula() );
		} else {
			texto.append("Certificamos que o(a) " + obj.getPessoa().getNome() );
			texto.append(", CPF " + obj.getPessoa().getCpfCnpjFormatado() );
		}
		
		texto.append(", participou do Projeto \"" + obj.getProjeto().getTitulo() + "\"" );
		if ( !isEmpty( obj.getChExecucao() ) )
			texto.append(" com carga " + obj.getChExecucao() + " horas, " );
		texto.append(" promovido pelo(a) " + obj.getProjeto().getUnidade().getNome() );
		texto.append(" na função de " + obj.getFuncaoMembro().getDescricao() );
		texto.append(" com " + obj.getChCertificadoDeclaracao() + " horas de atividades desenvolvidas, " +
				"no período de " + Formatador.getInstance().formatarDataDiaMesAno(obj.getDataInicio() ) 
				+ " a " + Formatador.getInstance().formatarDataDiaMesAno(obj.getDataFim()) + ".");
		return texto.toString();
	}
	
	@Override
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
 		AvaliacaoDao dao = getDAO(AvaliacaoDao.class);
		try {
			String codVerificacao = "";

			if (comprovante.getSubTipoDocumento().equals(SubTipoDocumentoAutenticado.DECLARACAO_MEMBROS_ASSOCIADOS) ) {
				obj = dao.findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), MembroProjeto.class);
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
					MembroProjeto.class);
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

	public DataModel getMembros() {
		return membros;
	}

	public void setMembros(DataModel membros) {
		this.membros = membros;
	}

	public Collection<DiscenteProjeto> getDiscentesProjeto() {
		return discentesProjeto;
	}

	public void setDiscentesProjeto(Collection<DiscenteProjeto> discentesProjeto) {
		this.discentesProjeto = discentesProjeto;
	}
	
}