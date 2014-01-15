/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/03/2010
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;

/*******************************************************************************
 * MBean respons�vel por gerar declara��es para os discentes de monitoria.
 * A autentica��o dos documentos � realizada pelo SIGAA atrav�s da gera��o de
 * um c�digo de documento e c�digo de verifica��o que pode ser validado atrav�s
 * do portal p�blico do SIGAA.
 * 
 * 
 * @author Igor Linnik
 * 
 ******************************************************************************/
@Component("declaracaoDiscenteMonitoria")
@Scope("session")
public class DeclaracaoDiscenteMonitoriaMBean extends SigaaAbstractController<DiscenteExtensao> implements AutValidator {

	// nomes dos documentos
	public static final String DECLARACAO = "trf25396_DeclaracaoPrograd";

	private Collection<ProjetoEnsino> projetosMonitoria = new HashSet<ProjetoEnsino>();

	private Collection<DiscenteMonitoria> discentes = new HashSet<DiscenteMonitoria>();

	private DiscenteMonitoria discente = new DiscenteMonitoria();

	private EmissaoDocumentoAutenticado comprovante;

	private boolean verificando = false;


	/**
	 * Gera a semente utilizada no c�digo de autentica��o da declara��o.
	 * Deve-se utilizar para gera��o da semente, todos os dados vari�veis da 
	 * declara��o.
	 * 
	 * @return String com a semente gerada.
	 */
	private String gerarSementeDeclaracao() {
		StringBuilder builder = new StringBuilder();

		builder.append(discente.getId());
		builder.append(discente.getProjetoEnsino().getId());	
		// PALESTRANTE,COLABORADOR,	COORDENADOR,
		builder.append(discente.getTipoVinculo().getId()); 
		builder.append(TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO);

		return builder.toString();
	}

	/**
	 * Gera a declara��o para o participante selecionado.
	 * 
	 * Chamado por: /sigaa.war/extensao/DocumentosAutenticados/lista.jsp
	 * Chamado por: /sigaa.war/extensao/ParticipanteAcaoExtensao/form.jsp
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 * 
	 */
	public String emitirDeclaracao() throws DAOException, SegurancaException {

		GenericDAO dao = null;
		Connection con = null;
		
		try {

			dao = getGenericDAO();
			
			// Na emiss�o recupera o valor passado como par�metro.
			if(discente == null || discente.getId() <= 0){
				discente = dao.findByPrimaryKey(getParameterInt("id"), DiscenteMonitoria.class);
			}else{
				// Valida��o, � preencido pelo m�todo exibir
			}
			
			discente.getProjetoEnsino().setEquipeDocentes(dao.findByExactField(EquipeDocente.class, "projetoEnsino.id", discente.getProjetoEnsino().getId()));
			dao.refresh(discente.getProjetoEnsino().getProjeto().getCoordenador());

			if (validarEmissaoDeclaracao()) {

				// montagem do corpo da declara��o
				String texto = getTextoDeclaracao();

				// Gerar declara��o
				
				HashMap<Object, Object> parametros = new HashMap<Object, Object>();

				// gerando c�digo de autentica��o...
				if (!verificando) {

					/*
					 * checkRole(
					 * SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
					 * SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
					 * SigaaPapeis.GESTOR_EXTENSAO);
					 */

					comprovante = geraEmissao(
							TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO,
							((Integer) discente.getId()).toString(),
							gerarSementeDeclaracao(),
							null,
							SubTipoDocumentoAutenticado.DECLARACAO_DISCENTE_MONITORIA,
							true);

				}

				// setando par�metros
				parametros.put("codigoSeguranca", comprovante
						.getCodigoSeguranca());
				parametros.put("siteVerificacao", ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_AUTENTICIDADE));

				
				parametros.put("ministerio", RepositorioDadosInstitucionais.get("ministerio").toUpperCase());
				parametros.put("universidade", RepositorioDadosInstitucionais.get("nomeInstituicao").toUpperCase());
				parametros.put("pro_reitoria", "PR�-REITORIA DE GRADUA��O");  // fixo por enquanto
				
				parametros.put("cidade", RepositorioDadosInstitucionais.get("cidadeInstituicao")+", "+ Formatador.getInstance().formatarDataDiaMesAno(comprovante.getDataEmissao()));
				parametros.put("texto", texto);
				parametros.put("numero_documento", comprovante.getNumeroDocumento());
				
				parametros.put("coordenacao_didatico_pedagogica", ParametroHelper.getInstance().getParametro(ParametrosMonitoria.NOME_COORDENADOR_DIDATICO_PEDAGOGICO));
				parametros.put("unidadeCoordenador", ParametroHelper.getInstance().getParametro(ParametrosMonitoria.UNIDADE_DIDATICO_PEDAGOGICO));
				parametros.put("cargoCoordenador", ParametroHelper.getInstance().getParametro(ParametrosMonitoria.CARGO_DIDATICO_PEDAGOGICO));
				
				parametros.put("caminhoLogoTipoInstituicao",   RepositorioDadosInstitucionais.get("logoInstituicao"));
				
				// Preencher declara��o
				con = Database.getInstance().getSigaaConnection();
				JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(DECLARACAO + ".jasper"),parametros, con);

				getCurrentResponse().setContentType("application/pdf");
				getCurrentResponse().addHeader(
						"Content-Disposition",
						"attachment; filename=DECLARACAO_DISCENTE_PROGRAD_"+ discente.getId() + ".pdf");
				JasperExportManager.exportReportToPdfStream(prt,getCurrentResponse().getOutputStream());

				FacesContext.getCurrentInstance().responseComplete();
				con.close();
			}

			
			return null;

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao buscar as informa��es da declara��o.");
			return null;
		}finally{
			if(dao != null) dao.close();
			if(con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}

	public Collection<ProjetoEnsino> getProjetosMonitoria() {
		return projetosMonitoria;
	}

	public void setProjetosMonitoria(Collection<ProjetoEnsino> projetosMonitoria) {
		this.projetosMonitoria = projetosMonitoria;
	}

	public Collection<DiscenteMonitoria> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<DiscenteMonitoria> discentes) {
		this.discentes = discentes;
	}

	public DiscenteMonitoria getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteMonitoria discente) {
		this.discente = discente;
	}

	/**
	 * Retorna o texto(corpo) da declara��o.
	 * 
	 * @return texto utilizado na declara��o.
	 */
	private String getTextoDeclaracao() {
		String result = "";

		if ((discente != null) && (discente.getId() > 0)) {
			
				result = "Declaramos, para os devidos fins, que " +
						 discente.getDiscente().getNome() +
						 ", matr�cula " +
						 discente.getDiscente().getMatricula() +
						 ", exerce as fun��es de monitor no Projeto de Ensino " + "\"" +
						 discente.getProjetoEnsino().getTitulo() + "\"" +
						 ", coordenado pelo(a) Prof.(a) " +
						 discente.getProjetoEnsino().getCoordenacao().getPessoa().getNome() +					 
						 ", do(a) " +
						 discente.getProjetoEnsino().getCoordenacao().getUnidade().getNome() +
						 ", do " +
						 discente.getProjetoEnsino().getProjeto().getUnidade().getNome() +
						 ", com data in�cio de " +
						 Formatador.getInstance().formatarDataDiaMesAno(discente.getDataInicio()) +
						 ", permanecendo vigente at� a data atual.";
		
		}
		
		return result;		
	}

	/**
	 * Utilizado na valida��o para emiss�o da declara��o.
	 * Verifica se o usu�rio atual pode imprimir a declara��o.
	 * 
	 * N�o chamado por jsp.
	 * 
	 * @return <code>true</code> se o usu�rio puder emitir.
	 */
	private boolean validarEmissaoDeclaracao() {

		if (discente == null) {
			addMensagemErro("Declara��o n�o encontrada.");
			return false;
		}

		/*
		 * TODO: verificar permiss�es de impress�o da declara��o
		 * 
		 * if ((!membro.isReceberDeclaracao)){ addMensagemErro("Usu�rio n�o tem
		 * direito ao certificado porque n�o apresentou ou n�o participou da
		 * elabora��o do resumo do SID"); return false; }
		 */

		return true;

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

		MembroProjetoDao dao = null;
		this.comprovante = comprovante;
		verificando = true;

		try {

			dao = getDAO(MembroProjetoDao.class);
			
			discente = dao.findByPrimaryKey(Integer.parseInt(comprovante.getIdentificador()), DiscenteMonitoria.class);

			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
				emitirDeclaracao();
			}

		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}finally{
			if(dao != null) dao.close();
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
			discente = dao.findByPrimaryKey(Integer.parseInt(comprovante
					.getIdentificador()), DiscenteMonitoria.class);

			String codigoVerificacao = "";
			if (comprovante.getTipoDocumento() == TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) {
				codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSementeDeclaracao());
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
