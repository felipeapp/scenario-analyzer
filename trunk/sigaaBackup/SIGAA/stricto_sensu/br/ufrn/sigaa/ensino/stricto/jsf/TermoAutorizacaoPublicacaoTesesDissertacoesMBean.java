/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.BolsistaDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.TermoAutorizacaoPublicacaoTeseDissertacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.bolsas.dominio.Bolsista;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.TermoAutorizacaoPublicacaoTeseDissertacao;
import br.ufrn.sigaa.ensino.stricto.negocio.MovimentoTermoPublicacaoTeseDissertacao;
import br.ufrn.sigaa.parametros.dominio.MensagensStrictoSensu;
import br.ufrn.sigaa.prodocente.atividades.dominio.InstituicaoFomento;
import br.ufrn.sigaa.prodocente.producao.dominio.BolsaObtida;

/**
 * Controller para auxiliar o preenchimento do Termo de Autorização de Publicação
 * de Teses e Dissertação - TEDE
 * 
 * @author Édipo Elder F. Melo
 * @author Arlindo Rodrigues
 * 
 */
@Component("termoPublicacaoTD") @Scope("request")
public class TermoAutorizacaoPublicacaoTesesDissertacoesMBean extends SigaaAbstractController<TermoAutorizacaoPublicacaoTeseDissertacao> implements OperadorDiscente, AutValidator {

	/** Formulários usados no caso de uso */
	public static final String PAGINA_ERRO_AUTENTICACAO = "/public/termos_autorizacoes/erro_autenticacao.jsp";
	public static final String JSP_PUBLICACAO = "/stricto/termos_autorizacoes/publicacao.jsp";
	
	/** Tipo do Documento (Tese ou Dissertação)*/
	private String tipoDocumento;
	
	/** Titulação (Doutor ou Mestre)*/
	private String titulacao;
	
	/** Arquivo enviado pelo discente */
	private UploadedFile arquivo;
	
	/** Comando que será executado no processador */
	private Comando comando;
	
	/** Lista de Termos */
	private Collection<TermoAutorizacaoPublicacaoTeseDissertacao> listagemTermos;
	
	/** Guarda o código que vai autenticar o documento do SIGAA. */
	private String codigoSeguranca;
	
	/** Mantém uma cópia do comprovante para evitar gerar mais de uma vez se o usuário ficar atualizando a página. */
	private EmissaoDocumentoAutenticado comprovante;	
	
	/** Indica o total de Trabalhos Pendentes de Publicação */
	private int pendentePublicacao = 0;
	
	/**
	 * Inicializa variáveis
	 */
	private void init() {
		if (obj == null)
			obj = new TermoAutorizacaoPublicacaoTeseDissertacao();
		
		if (isEmpty(obj.getDiscente()))
			obj.setDiscente(new DiscenteStricto());
		
		if (isEmpty(obj.getInstituicaoFomento()))
			obj.setInstituicaoFomento(new InstituicaoFomento());
	}	
	
	/**
	 * Inicia o caso de uso a partir do discente
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDiscente() throws ArqException {
		TermoAutorizacaoPublicacaoTeseDissertacaoDao dao = getDAO(TermoAutorizacaoPublicacaoTeseDissertacaoDao.class);
		try {
			obj = dao.findByDiscente((DiscenteStricto) getDiscenteUsuario());
			if (!isEmpty(obj) && obj.isAtivo()){
				return forward(getListPage());				
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		init();				
		
		obj.setDiscente( getGenericDAO().findByPrimaryKey(getDiscenteUsuario().getId(),	DiscenteStricto.class) );	
		obj.setStatus(TermoAutorizacaoPublicacaoTeseDissertacao.APROVADO);
		
		validarDiscenteStricto();
		
		if (isEmpty( obj.getBanca() )){
			addMensagemErro("Não é possível emitir o TEDE, pois não existe Banca de Defesa cadastrada.");
			return null;
		}
		
		comando = SigaaListaComando.CADASTRAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO;
		prepareMovimento(comando);
		
		return forward(getFormPage());
	}
	
	/**
	 * Aprova o termo selecionado.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/termos_autorizacoes/lista_tede.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String aprovarTermo() throws SegurancaException, ArqException, NegocioException{
		if (isEmpty(obj)){
			addMensagemErro("Termo não encontrado");
			return null;
		}
		populateObj();
		comando = SigaaListaComando.HOMOLOGAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO;
		prepareMovimento(comando);
		obj.setStatus(TermoAutorizacaoPublicacaoTeseDissertacao.APROVADO);
		return cadastrar();		
	}
	
	/**
	 * Reprova o termo selecionado.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/termos_autorizacoes/lista_tede.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String reprovarTermo() throws SegurancaException, ArqException, NegocioException{
		if (isEmpty(obj)){
			addMensagemErro("Termo não encontrado");
			return null;
		}		
		populateObj();
		comando = SigaaListaComando.HOMOLOGAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO;
		prepareMovimento(comando);		
		obj.setStatus(TermoAutorizacaoPublicacaoTeseDissertacao.REPROVADO);
		return cadastrar();		
	}	
	
	/**
	 * Altera o termo selecionado.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/termos_autorizacoes/lista_tede.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String alterar() throws SegurancaException, ArqException, NegocioException{
		if (isEmpty(obj)){
			addMensagemErro("Termo não encontrado");
			return null;
		}		
		comando = SigaaListaComando.ALTERAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO;
		prepareMovimento(comando);
		
		if (isEmpty(obj.getInstituicaoFomento()))
			obj.setInstituicaoFomento(new InstituicaoFomento());		
		
		obj.setBanca(getGenericDAO().refresh(obj.getBanca()));
		//obj.getBanca().setDadosDefesa(getGenericDAO().refresh(obj.getBanca().getDadosDefesa()));
		
		setTipoDocumentoTitulacao();
		
		return forward(getFormPage());
	}		
	
	
	
	/**
	 * Chamado a partir do BuscaDiscenteMBean
	 * <br/><br/>
	 * Método não chamado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 * @return /sigaa/stricto/termos_autorizacoes/form_tede.jsp
	 */
	public String selecionaDiscente() throws ArqException {	
		TermoAutorizacaoPublicacaoTeseDissertacaoDao dao = getDAO(TermoAutorizacaoPublicacaoTeseDissertacaoDao.class);
		try {
			TermoAutorizacaoPublicacaoTeseDissertacao termo = dao.findByDiscente(obj.getDiscente());
			if (isEmpty(termo)){
				addMensagemErro("O Discente selecionado não possui Termo cadastrado.");
				return null;
			}
			listagemTermos = new ArrayList<TermoAutorizacaoPublicacaoTeseDissertacao>();
			listagemTermos.add(termo);
		} finally {
			if (dao != null)
				dao.close();
		}			
		return forward(getListPage());
	}

	/**
	 * Seta o discente selecionado na busca por discente.
	 * <br /><br />
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 * JSP: Não invocado por JSP
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		// carrega mais informações
		try {
			init();
			
			// discente stricto
			this.obj.setDiscente( (DiscenteStricto) discente );
			
			// valida o aluno
			validarDiscenteStricto();
			
		} catch (Exception e) {
			discente = null;
			e.printStackTrace();
		}
	}
	
	/**
	 * Cadastra a Solicitação de Publicação de Tese ou Dissertação
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/termos_autorizacoes/form_tede.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		validacaoDados(erros.getMensagens());
		
		if (comando.equals(SigaaListaComando.CADASTRAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO) || 
				comando.equals(SigaaListaComando.ALTERAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO))
			confirmaSenha();
		
		if( hasErrors())
			return null;
		
		try {
			MovimentoTermoPublicacaoTeseDissertacao mov = new MovimentoTermoPublicacaoTeseDissertacao();
			mov.setTermoAutorizacaoPublicacao(obj);
			mov.setArquivo(arquivo);
			mov.setCodMovimento(comando);
			execute(mov);				

			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			if (comando.equals(SigaaListaComando.CADASTRAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO) || 
					comando.equals(SigaaListaComando.ALTERAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO))
				return emitirTEDE();
			else {
				listagemTermos = null;
				if (comando.equals(SigaaListaComando.PUBLICAR_TESE_DISSERTACAO))
					getCurrentRequest().setAttribute("publicacao", true);			
				getListagemTermos();
				return exibirPendentes();
			}
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		}
	}

	/**
	 * Verifica se o discente pode pedir o TEDE
	 * 
	 * @throws DAOException
	 */
	private void validarDiscenteStricto() throws DAOException {
		if (obj != null) {
			BancaPosDao bancaPosDao = getDAO(BancaPosDao.class);
			
			// Orientador
			if (isEmpty(obj.getDiscente().getOrientacao()))
				obj.getDiscente().setOrientacao(DiscenteHelper.getUltimoOrientador(obj.getDiscente().getDiscente()));
			
			// Banca
			if (isEmpty(obj.getBanca())){
				BancaPos banca = bancaPosDao.findMaisRecenteByTipo(obj.getDiscente(),	BancaPos.BANCA_DEFESA);
				obj.setBanca(banca);
			}  else {
				//Recarrega dados da banca
				obj.setBanca(bancaPosDao.findByPrimaryKey(obj.getBanca().getId(), BancaPos.class, "id", "dadosDefesa.titulo", 
						"dadosDefesa.palavrasChave", "data", "dadosDefesa.idArquivo") );
				
				obj.getBanca().setMembrosBanca(bancaPosDao.findMembrosByBanca(obj.getBanca()));
			}
			
			// Dados Pessoais
			obj.getDiscente().setPessoa(getGenericDAO().refresh(obj.getDiscente().getPessoa()));
			obj.getDiscente().getPessoa().setUnidadeFederativa(getGenericDAO().refresh(obj.getDiscente().getPessoa().getUnidadeFederativa()));
			
			setTipoDocumentoTitulacao();
			
			// Data de publicação padrão: data da defesa
			if (obj.getBanca() != null && obj.getDataPublicacao() == null)
				obj.setDataPublicacao( obj.getBanca().getData() );
			
			if(obj.getDiscente().getPessoa().getIdentidade() != null && obj.getDiscente().getPessoa().getIdentidade().getUnidadeFederativa() != null 
					&& (obj.getDiscente().getPessoa().getIdentidade().getUnidadeFederativa().getId()>0 )){
				obj.getDiscente().getPessoa().getIdentidade().setUnidadeFederativa(getGenericDAO().findByPrimaryKey(
					obj.getDiscente().getPessoa().getIdentidade().getUnidadeFederativa().getId(), UnidadeFederativa.class));
			}
				
			if (isEmpty(obj.getInstituicaoFomento())){
				// Instituição de fomento
				BolsistaDao bolsistaDao = getDAO(BolsistaDao.class);
				Collection<Bolsista> bolsistas = bolsistaDao.findByDiscente(obj.getDiscente());
				for (Bolsista bolsista : bolsistas) {
					BolsaObtida bolsa = getGenericDAO().findByPrimaryKey(bolsista
							.getIdBolsa(), BolsaObtida.class);
					if (bolsa != null && bolsa.getInstituicaoFomento() != null)
						obj.setInstituicaoFomento( bolsa.getInstituicaoFomento() );
				}				

				if (obj.getInstituicaoFomento() == null)
					obj.setInstituicaoFomento( new InstituicaoFomento() );
			}
		}
	}

	/**
	 * Atribui o tipo do documento e titulação do docentes
	 */
	private void setTipoDocumentoTitulacao() {
		// Tipo de documento e titulação
		if (obj.getDiscente().getNivel() == NivelEnsino.MESTRADO) {
			this.tipoDocumento = "Dissertação";
			this.titulacao = "Mestre";
		} else if (obj.getDiscente().getNivel() == NivelEnsino.DOUTORADO) {
			this.tipoDocumento = "Tese";
			this.titulacao = "Doutor";
		} else {
			this.tipoDocumento = "";
			this.titulacao = "";
		}
	}

	/** 
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 */
	public String buscarDiscente() throws SegurancaException {
		checkRole(SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);
		return buscaDiscente(OperacaoDiscente.EMISSAO_TEDE);
	}
		
	/**
	 * Inicia a busca de discente conforme a operacao informada
	 * @param operacao
	 * @return
	 */
	private String buscaDiscente(int operacao){
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(operacao);
		return buscaDiscenteMBean.popular();		
	}
	
	/**
	 * Exibe todos os termos que não foram aprovados.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String exibirPendentes() throws SegurancaException{
		return forward(getListPage());
	}
	
	/**
	 * Visualizar a Publicação da Tese/Dissertação na BDTD (Biblioteca Digital de Teses e Dissertações).
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/termos_autorizacoes/lista_tede.jsp</li>
	 * </ul>
	 * @return
	 */
	public String visualizarPublicacao(){
		if (isEmpty(obj))
			return null;		
		return redirect(obj.getBanca().getDadosDefesa().getLinkArquivo());
	}

	/**
	 * Emite o Termo de Autorização para Publicação de Teses e Dissertações - TEDE.
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/termos_autorizacoes/form_tede.jsp</li>
	 * </ul>
	 * @return /sigaa/stricto/termos_autorizacoes/view_tede.jsp
	 * @throws ArqException 
	 */
	public String emitirTEDE() throws ArqException {
		
		if (isEmpty(obj)){
			addMensagemErro("Termo não encontrado");
			return null;
		}		
		
		populateObj();
		
		validarDiscenteStricto();
		
		codigoSeguranca = null;
		gerarCodigoSeguranca();
		
		getCurrentRequest().setAttribute("liberaEmissao", true);
		
		return forward(getViewPage());
	}
	
	/**
	 * Iniciar o caso de uso para publicação da Tese/Dissertação 
	 * na BDTD (Biblioteca Digital de Teses e Dissertações). 
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/biblioteca/menus/cadastros.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarPublicacao() throws ArqException{
		getCurrentRequest().setAttribute("publicacao", true);
		return exibirPendentes();
	}
	
	/**
	 * Redireciona para o formulário de publicação
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/termos_autorizacoes/lista_tede.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String publicar() throws ArqException{
		populateObj();
		if (isEmpty(obj))
			return null;
		
		comando = SigaaListaComando.PUBLICAR_TESE_DISSERTACAO;
		prepareMovimento(comando);		
		
		obj.setDataPublicacaoBDTD(new Date());
		return forward(JSP_PUBLICACAO);
	}
	
	@Override
	public String getViewPage() {
		return "/public/termos_autorizacoes/view_tede.jsp";
	}
	
	@Override
	public String getFormPage() {
		return "/stricto/termos_autorizacoes/form_tede.jsp";
	}
	
	@Override
	public String getListPage() {
		return "/stricto/termos_autorizacoes/lista_tede.jsp";		
	}

	/** Retorna o tipo do documento a autorizar: Dissertação ou Tese.
	 * @return
	 */
	public String getTipoDocumento() {
		return tipoDocumento;
	}

	/** Seta o tipo do documento a autorizar: Dissertação ou Tese.
	 * @return
	 */
	public String getTitulacao() {
		return titulacao;
	}

	/**
	 * Válida os dados: restrições à publicação (caso publicação parcial), data de publicação.
	 * JSP: Não invocado por JSP
	 */
	@Override
	public boolean validacaoDados(Collection<MensagemAviso> mensagens) {	
		if (comando.equals(SigaaListaComando.CADASTRAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO) || 
				comando.equals(SigaaListaComando.ALTERAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO)){
			if (obj.isParcial() && isEmpty(obj.getRestricoes())) {
				mensagens.add(new MensagemAviso(
						"Informe as restrições de publicação",
						TipoMensagemUFRN.ERROR));
			}
			if (obj.getDataPublicacao() == null) {
				mensagens.add(new MensagemAviso("Informe uma data válida",
						TipoMensagemUFRN.ERROR));
			} 
			
			if (!ValidatorUtil.isEmpty(obj.getCNPJAfiliacao()))
				ValidatorUtil.validateCPF_CNPJ(obj.getCNPJAfiliacao(), "CNPJ", erros);
			
			if (comando.equals(SigaaListaComando.CADASTRAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO)){			
				if (obj.getDataPublicacao() != null && obj.getDataPublicacao().before(obj.getBanca().getData())) {
					mensagens.add(new MensagemAviso("A data de publicação não pode ser anterior à data de defesa.",
							TipoMensagemUFRN.ERROR));
				}			
				if (arquivo == null)
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Arquivo");
			}
			if (arquivo != null){
				String extensao = (arquivo.getName().lastIndexOf(".") > -1 ? arquivo.getName().substring(arquivo.getName().lastIndexOf(".")) : "");
				if (!extensao.equals(".pdf")){
					addMensagem(MensagensArquitetura.ARQUIVO_UPLOAD_INVALIDO);
				}				
			}			
		}
		
		if (comando.equals(SigaaListaComando.PUBLICAR_TESE_DISSERTACAO)) {
			if (isEmpty(obj.getUrlBDTD()))				
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "URL");
			
			if (obj.getDataPublicacaoBDTD() == null)
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de Publicação");
		}
		
		return true;
	}
	
	/**
	 *   Gera o Código de Segurança para emitir o TEDE.
	 */
	private void gerarCodigoSeguranca() throws ArqException{		
		// Só gera outro código de segurança e, consequentemente, outro documento se ele já
		// não foi emitido antes, para evitar gerações desnecessárias de código de segurança.		
		if (codigoSeguranca == null  ){			
			try {			
				
				String instituicao = "";
				if (!ValidatorUtil.isEmpty( obj.getInstituicaoFomento() ))
					instituicao = obj.getInstituicaoFomento().getNome();				

				comprovante = geraEmissao(
						TipoDocumentoAutenticado.TERMO_PUBLICACAO_TESE_DISSERTACAO,
						String.valueOf( obj.getDiscente().getMatricula() ),  // identificador
						obj.getDiscente().getMatriculaNome()+
						obj.getAfiliacao()+
						obj.getCNPJAfiliacao()+
						instituicao+
						String.valueOf( obj.isParcial() )+
						String.valueOf( Formatador.getInstance().formatarData( obj.getDataPublicacao() ))+
						String.valueOf( obj.getBanca().getDadosDefesa().getIdArquivo()),    // semente 
						String.valueOf( new Date() ), // informações complementares
						SubTipoDocumentoAutenticado.TERMO_PUBLICACAO_TESE_DISSERTACAO,
						false
				);		
				codigoSeguranca = comprovante != null ? comprovante.getCodigoSeguranca() : null;				
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			}
		}
	}	

	/** Retorna o arquivo enviado pelo o aluno */
	public UploadedFile getArquivo() {
		return arquivo;
	}

	/** Seta o arquivo enviado pelo aluno */
	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	/**
	 * Retorna a lista de Termo pendentes de aprovação.
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/termos_autorizacoes/lista_tede.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<TermoAutorizacaoPublicacaoTeseDissertacao> getListagemTermos() throws DAOException {
		if (isEmpty(listagemTermos)){
			TermoAutorizacaoPublicacaoTeseDissertacaoDao dao = getDAO(TermoAutorizacaoPublicacaoTeseDissertacaoDao.class);
			try {
				if (isPortalDocente())					
					listagemTermos = dao.findByOrientador(getServidorUsuario(), getDocenteExternoUsuario(), TermoAutorizacaoPublicacaoTeseDissertacao.EM_ANALISE);
				else if (isPortalDiscente()){
					obj = dao.findByDiscente((DiscenteStricto) getDiscenteUsuario());
					if (!isEmpty(obj)){
						listagemTermos = new ArrayList<TermoAutorizacaoPublicacaoTeseDissertacao>();
						listagemTermos.add(obj);						
					}
				} else {
					if (getCurrentRequest().getAttribute("publicacao") != null && (Boolean) getCurrentRequest().getAttribute("publicacao"))
						listagemTermos = dao.findAllPublicacao();										
				}
			} finally {
				if (dao != null)
					dao.close();
			}			
		}		
		return listagemTermos;
	}
	
	/**
	 * Seta a lista de termos
	 * @param listagemTermos
	 */
	public void setListagemTermos(
			List<TermoAutorizacaoPublicacaoTeseDissertacao> listagemTermos) {
		this.listagemTermos = listagemTermos;
	}

	/**
	 * Reemite o TEDE no momento da verificação se o usuário que está fazendo a validação do comprovante
	 * desejar visualizar o documento emitido pelo sistema.
	 * 
	 * @see br.ufrn.arq.seguranca.autenticacao.AutValidator#exibir(br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * 
	 * <br>
	 * <br>
	 * Método não invocado por JSP´s é public por causa da arquitetura.
	 * 
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */	
	@Override
	public void exibir(EmissaoDocumentoAutenticado comprovante,	HttpServletRequest req, HttpServletResponse res) {
		TermoAutorizacaoPublicacaoTeseDissertacaoDao dao = getDAO(TermoAutorizacaoPublicacaoTeseDissertacaoDao.class);
		try {
			long matricula = Long.parseLong( comprovante.getIdentificador() );
			obj = dao.findByMatricula(matricula);
			if (!isEmpty(obj)){
				validarDiscenteStricto();							
				/*  Pega o código de segurança do comprovante para reexibir ao usuário */
				codigoSeguranca = comprovante != null ? comprovante.getCodigoSeguranca() : null;
				/* *************************************************************************************
				 *  Segurança inserida para caso o usuário tente acessar a página do documento diretamente.
				 * *************************************************************************************/
				getCurrentRequest().setAttribute("termoPublicacaoTD", this);
				getCurrentRequest().setAttribute("liberaEmissao", true); 
				/* *************************************************************************************
				 *           IMPORTANTE TEM QUE REDIRECIONAR PARA A PÁGINA DO COMPROVANTE
				 * *************************************************************************************/
				getCurrentRequest().getRequestDispatcher(getViewPage()).forward(getCurrentRequest(), getCurrentResponse());
			} else {
				getCurrentRequest().getRequestDispatcher(PAGINA_ERRO_AUTENTICACAO).forward(getCurrentRequest(), getCurrentResponse());
			}
		}catch (Exception  e) {
			e.printStackTrace();
			
			try {
				getCurrentRequest().getRequestDispatcher(PAGINA_ERRO_AUTENTICACAO).forward(getCurrentRequest(), getCurrentResponse());
			} catch (ServletException e1) {
				e1.printStackTrace();
				tratamentoErroPadrao(e1);
			} catch (IOException e1) {
				e1.printStackTrace();
				tratamentoErroPadrao(e1);
			}
		}finally{

			
		}				
	}

	/**
	 * <p> Realizar a validação do comprovante de autenticação do documento.</p>
	 * <p> <strong> IMPORTANTE: </strong> caso o usuário solicite um novo termo, o comprovante DEVE ser invalidado. </p>
	 * 
	 * @see br.ufrn.arq.seguranca.autenticacao.AutValidator#validaDigest(br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado)
	 * 
	 * <br><br>
	 * Método não invocado por JSP´s
	 * é public por causa da arquitetura.
	 */
	@Override
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		TermoAutorizacaoPublicacaoTeseDissertacaoDao dao = getDAO(TermoAutorizacaoPublicacaoTeseDissertacaoDao.class);
		try {
			long matricula = Long.parseLong( comprovante.getIdentificador() );
			TermoAutorizacaoPublicacaoTeseDissertacao termo = dao.findByMatricula(matricula);
			
			if (isEmpty(termo))
				return false;
			
			String instituicao = "";
			if (!ValidatorUtil.isEmpty( termo.getInstituicaoFomento() ))
				instituicao = termo.getInstituicaoFomento().getNome();
			
			String semente = termo.getDiscente().getMatriculaNome()+
					termo.getAfiliacao()+
					termo.getCNPJAfiliacao()+
					instituicao+
					String.valueOf( termo.isParcial() )+
					String.valueOf( Formatador.getInstance().formatarData( termo.getDataPublicacao() ))+
					String.valueOf( termo.getBanca().getDadosDefesa().getIdArquivo());

			String codigoVerificacao;
			codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, semente);

			if (codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
				return true;							
		}catch (DAOException daoExt) {
			daoExt.printStackTrace();
			return false;
		} catch (ArqException ae) {
			ae.printStackTrace();
			return false;
		}finally{
			if( dao != null ) 
				dao.close(); 
		}			
		return false;
	}

	public String getCodigoSeguranca() {
		return codigoSeguranca;
	}

	public void setCodigoSeguranca(String codigoSeguranca) {
		this.codigoSeguranca = codigoSeguranca;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	/**
	 * Carrega o total de Trabalhos Pendentes de Publicação
	 * <br/><br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/biblioteca/menus/teses_dissertacoes.jsp</li>
	 * </ul>
	 */	
	public int getPendentePublicacao() throws DAOException {
		TermoAutorizacaoPublicacaoTeseDissertacaoDao dao = getDAO(TermoAutorizacaoPublicacaoTeseDissertacaoDao.class);
		try {
			pendentePublicacao = dao.findTotalPendentesPublicacao();
		} finally {
			if (dao != null)
				dao.close();
		}		
		return pendentePublicacao;
	}

	public void setPendentePublicacao(int pendentePublicacao) {
		this.pendentePublicacao = pendentePublicacao;
	}
	
	
	public String getTextoEmissaoTEDE() {		
		return UFRNUtils.getMensagem(MensagensStrictoSensu.TEXTO_EMISSAO_TEDE,CalendarUtils.format(obj.getDataPublicacao(), "dd/MM/yyyy")).getMensagem();
	}
	
	
}
