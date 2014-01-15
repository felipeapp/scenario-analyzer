/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/08/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf; 

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.extensao.AvaliacaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;
import br.ufrn.sigaa.extensao.jsf.helper.CertificadoExtensaoHelper;

/**
 * MBean respons�vel por gerar certificados para os membros do
 * comit� de extens�o e para avaliadores Ad Hoc.  
 * 
 * 
 * @author Igor Linnik
 *
 */
@Component("certificadoAvaliadorExtensao")
@Scope("session")
public class CertificadoAvaliadorExtensaoMBean extends SigaaAbstractController<AtividadeExtensao> {

	/*
	 * Construtor padr�o
	 */
	public CertificadoAvaliadorExtensaoMBean() {

	}

	private Boolean verificando = false;

	public Collection<AvaliacaoAtividade> avaliacoesComoAdHoc = new ArrayList<AvaliacaoAtividade>();

	public Collection<AvaliacaoAtividade> avaliacoesComoMembroComissao = new ArrayList<AvaliacaoAtividade>();

	public AvaliacaoAtividade avaliacao = new AvaliacaoAtividade();

	private EmissaoDocumentoAutenticado comprovante;


	/**
	 * Leva para tela onde � poss�vel a impress�o de certificados de avaliadores. 
	 * 
	 * sigaa.war/portais/docente/menu_docente.jsp
	 * 
	 * @return
	 */
	public String irTelaCertificadoAvaliadorAdHoc() {
		AvaliacaoExtensaoDao dao = getDAO(AvaliacaoExtensaoDao.class);
		try {
			avaliacoesComoAdHoc = dao.findByAvaliadorAdHoc(getServidorUsuario().getId(),null,null);
			avaliacoesComoMembroComissao = dao.findByAvaliadorMembroComissao(getServidorUsuario().getId(),null,null);
		}
		catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
		}

		return forward(ConstantesNavegacao.AVALIACAO_LISTA_AD_HOC);
	}

	/**
	 * Gera o certificado para o avaliador selecionado. 
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/AvaliacaoAtividade/lista_avaliacoes_ad_hoc.jsp
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 * 
	 */
	public String emitirCertificado() throws DAOException, SegurancaException {

		try {

			GenericDAO dao = getGenericDAO();
			
			if(!verificando) {
				avaliacao = dao.findByPrimaryKey(getParameterInt("idAvaliacao"), AvaliacaoAtividade.class);
				dao.refresh(avaliacao.getAtividade().getProjeto().getCoordenador());
			}

			if (validarEmissaoCertificado()) {

				// montagem do corpo da declara��o
				String texto = getTextoCertificado();

				if(!verificando) {


					comprovante = geraEmissao(
							TipoDocumentoAutenticado.CERTIFICADO,
							((Integer) avaliacao.getId()).toString(),
							gerarSementeCertificado(),
							null,
							SubTipoDocumentoAutenticado.CERTIFICADO_AVALIADOR_EXTENSAO,
							true);

				}
				
				CertificadoExtensaoHelper.emitirCertificado(comprovante.getCodigoSeguranca(), texto, comprovante.getNumeroDocumento()
						, avaliacao.getAtividade().getCoordenacao().getPessoa().getNome()
						, comprovante.getDataEmissao(), ""+avaliacao.getId(), getCurrentResponse());

			}

			return null;

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informa��es da declara��o.");
			return null;
		}

	}

	/**
	 * Utilizado na valida��o para emiss�o do certificado.
	 * Verifica se o usu�rio atual pode imprimir o certificado.
	 * 
	 * N�o chamado por jsp.
	 * 
	 * @return <code>true</code> se o usu�rio puder emitir.
	 */
	private boolean validarEmissaoCertificado() {
		if (avaliacao == null) {
			addMensagemErro("Avalia��o n�o encontrada.");
			return false;
		}	
		return true;
	}

	/**
	 * Retorna o texto(corpo) do certificado.
	 * 
	 * N�o � chamado por JSPs.
	 * 
	 * @return
	 */
	private String getTextoCertificado() {

		String texto = "";

		if ((avaliacao != null) && (avaliacao.getId() > 0)) {
			texto = "Certificamos que o senhor(a) "
				+ avaliacao.getAvaliador().getPessoa().getNome()
				+ ", siape "
				+ avaliacao.getAvaliador().getSiape()
				+ ", � membro do  " 
				+ avaliacao.getTipoAvaliadorString()
				+ "  da PR�-REITORIA DE EXTENS�O DA UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE "
				+ "e, em "
				+ Formatador.getInstance().formatarDataDiaMesAno(avaliacao.getDataAvaliacao())
				+ ", emitiu parecer na a��o de extens�o: '"
				+ avaliacao.getAtividade().getTitulo().toUpperCase()
				+ "'" + " promovida pelo(a) "
				+ avaliacao.getAtividade().getUnidade().getNome();
		}

		return texto;	    
	}

	/**
	 * Gerar semente para valida��o do documento
	 * 
	 * @return
	 */
	private String gerarSementeCertificado() {
		StringBuilder builder = new StringBuilder();

		builder.append(avaliacao.getId());
		builder.append(avaliacao.getAtividade().getId());		
		builder.append(avaliacao.getAvaliador().getSiape());
		builder.append(avaliacao.getDataAvaliacao());		
		builder.append(TipoDocumentoAutenticado.CERTIFICADO);

		return builder.toString();
	}

	/**
	 * Utilizado na valida��o da autenticidade da declara��o
	 * a partir do portal p�blico. Valida o c�digo de autentica��o e o n�mero 
	 * do documento.
	 *  
	 * N�o � chamado diretamente por jsps. 
	 * 
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);

		try {
			avaliacao = dao.findByPrimaryKey((Integer.parseInt(comprovante.getIdentificador())), AvaliacaoAtividade.class);

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
	 * Utilizado na valida��o da autenticidade de certificado
	 * a partir do portal p�blico.
	 * 
	 * N�o chamado diretamente por jsp.
	 * 
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {

		AvaliacaoExtensaoDao dao = getDAO(AvaliacaoExtensaoDao.class);
		this.comprovante = comprovante;		
		verificando = true;


		try {

			avaliacao = dao.findByPrimaryKey(Integer.parseInt(comprovante
					.getIdentificador()), AvaliacaoAtividade.class);


			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.CERTIFICADO) {
				emitirCertificado();
			}

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
	}

	public Collection<AvaliacaoAtividade> getAvaliacoesComoAdHoc() {
		return avaliacoesComoAdHoc;
	}

	public void setAvaliacoesComoAdHoc(Collection<AvaliacaoAtividade> avaliacoes) {
		this.avaliacoesComoAdHoc = avaliacoes;
	}

	public Collection<AvaliacaoAtividade> getAvaliacoesComoMembroComissao() {
		return avaliacoesComoMembroComissao;
	}

	public void setAvaliacoesComoMembroComissao(
			Collection<AvaliacaoAtividade> avaliacoesComoMembroComissao) {
		this.avaliacoesComoMembroComissao = avaliacoesComoMembroComissao;
	}

	public AvaliacaoAtividade getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(AvaliacaoAtividade avaliacao) {
		this.avaliacao = avaliacao;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	public Boolean getVerificando() {
		return verificando;
	}

	public void setVerificando(Boolean verificando) {
		this.verificando = verificando;
	}

}
