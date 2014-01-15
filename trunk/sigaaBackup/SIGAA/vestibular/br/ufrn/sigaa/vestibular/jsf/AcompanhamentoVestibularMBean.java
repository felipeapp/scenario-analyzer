/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/06/2010
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateCPF_CNPJ;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.questionario.PerguntaQuestionarioDao;
import br.ufrn.sigaa.arq.dao.vestibular.EscolaInepDao;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoVestibularDao;
import br.ufrn.sigaa.arq.dao.vestibular.PessoaVestibularDao;
import br.ufrn.sigaa.arq.dao.vestibular.ProcessoSeletivoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosVestibular;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.vestibular.dominio.EscolaInep;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.negocio.MovimentoPessoaVestibular;

/**
 * Controller para acesso � �rea restrita do candidato ao Vestibular.<br>
 * Em sua �rea restrita, o candidato poder� realizar: 
 * <ul>
 * <li>Inscri��o no vestibular,</li>
 * <li>Altera��o de dados pessoais,</li>
 * <li>Altera��o/reimpress�o de dados de pagamento,</li>
 * <li>Obten��o do comprovante de inscri��o,</li>
 * <li>Consulta do andamento do processo e</li>
 * <li>Outras op��es</li>
 * </ul>
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Component("acompanhamentoVestibular")
@Scope("session")
public class AcompanhamentoVestibularMBean extends SigaaAbstractController<PessoaVestibular> {
	
	// constantes utilizadas para definir o estado do controller
	/** Define a opera��o como sendo a de alterar dados pessoais do candidato. */
	public final static int ALTERAR_DADOS_PESSOAIS = 1;
	/** Define a opera��o como sendo a de alterar a senha de acesso � �rea pessoal do candidato. */
	public final static int ALTERAR_SENHA = 2;
	/** Define a opera��o como sendo a de alterar a foto do candidato. */
	public final static int ALTERAR_FOTO = 3;
	/** Define a opera��o como sendo a de nova inscri��o no Vestibular. */
	public final static int NOVA_INSCRICAO = 4;
	/** Define a opera��o como sendo a de nova inscri��o e alterar dados pessoais do candidato. */
	public final static int NOVA_INSCRICAO_ATUALIZAR_DADOS_PESSOAIS = 5;
	/** Define a opera��o como sendo a de novo cadastro de dados pessoais do candidato. */
	public final static int NOVO_CADASTRO = 6;
	
	/** Indica qual a opera��o atual do controller.  */
	private int operacao;
	/** Senha redigitada para confirma��o. */
	private String confirmacaoSenha;
	/** Senha atual do candidato, para confirma��o ao alterar a senha. */
	private String senhaAtual;
	/** Nova senha a ser definida ao alterar a senha. */
	private String novaSenha;
	/** Arquivo de foto 3x4 do candidato. */
	private UploadedFile foto;
	/** Arquivo enviado pelo usu�rio, a ser utilizado como foto 3x4. */
	private UploadedFile arquivoUpload;
	/** Cole��o de inscri��es realizadas pelo candidato para o Processo Seletivo. */
	private Collection<InscricaoVestibular> inscricoesRealizadas;
	/** Processo Seletivo o qual o candidato est� acompanhando. */
	private ProcessoSeletivoVestibular processoSeletivo;
	/** C�digo de verifica��o de seguran�a*/
	private String captcha;

	/**
	 * Indica se o pa�s selecionado no formul�rio de dados pessoais � o brasil. Se for false n�o � pra
	 * renderizar estado e nem munic�pio.
	 */
	private boolean brasil = true;
	/** Lista de SelectItem para escolha do munic�pio de naturalidade. */
	private Collection<SelectItem> municipiosNaturalidade = new ArrayList<SelectItem>(0);
	/** Lista de SelectItem para escolha do munic�pio do endere�o. */
	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
	/** Mapa de dados da foto */
	private Map<String,Object> dadosFoto;
	/** Cole��o de SelectItem de necessidades especiais. */
	private List<SelectItem> tipoNecessidadeCombo;
	/** Cole��o de escolas utilizada para autocomplete no formul�rio. */
	private Collection<EscolaInep> escolasInep;

	/** Respons�vel pela altera��o ou n�o da foto */
	private boolean permiteAlterarFoto = false;
	/** Inscri��o do Vestibular o qual o candidato est� inscrito. */
	private InscricaoVestibular inscricaoVestibular = null;
	
	/** Construtor padr�o. 
	 * @throws DAOException */
	public AcompanhamentoVestibularMBean() throws DAOException {
		inicializaAtributos();
	}
	
	// formul�rios

	/**
	 * Redireciona o usu�rio para o formul�rio de instru��es gerais para
	 * candidatos que realizam a primeira inscri��o. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/view.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String instrucoesGeraisInscricao() throws DAOException {
		int idProcessoSeletivo = getParameterInt("id", 0);
		GenericDAO dao = getGenericDAO();
		processoSeletivo = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
		return forward("/public/vestibular/acompanhamento/instrucoes_nova_inscricao.jsp");
	}
	
	/**
	 * Redireciona o usu�rio para a p�gina principal de acompanhamento do
	 * vestibular. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/inscricao/comprovante.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String paginaAcompanhamento() {
		return forward("/public/vestibular/acompanhamento/acompanhamento.jsp");
	}
	
	/** Redireciona o usu�rio para o formul�rio de login.<br/>M�todo n�o invocado por JSP�s.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>N�o invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public String formLogon() {
		return forward("/public/vestibular/acompanhamento/logon.jsp");
	}
	
	/** Redireciona o usu�rio para a p�gina de confirma��o de dados pessoais.<br/>M�todo n�o invocado por JSP�s.
	 * 
 	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>N�o invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String confirmaDadosPessoais() {
		return forward("/public/vestibular/acompanhamento/confirma_dados_pessoais.jsp");
	}
	
	/**
	 * Redireciona o usu�rio para o formul�rio de dados pessoais ou para o question�rio se houver<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	  <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/inscricao/opcao_curso.jsp</li>
	 * </ul>
	 */
	public String voltarPassoSelecaoCurso() throws DAOException {
		Questionario questionario = getGenericDAO().findAndFetch(processoSeletivo.getId(), ProcessoSeletivoVestibular.class, "questionario").getQuestionario();
		if (isEmpty(questionario))
			return forward("/public/vestibular/acompanhamento/confirma_dados_pessoais.jsp");
		else
			return forward("/public/vestibular/inscricao/form_questionario.jsp");
	}
	
	/**
	 * Redireciona o usu�rio para o formul�rio de dados pessoais.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>
	 * /sigaa.war/public/vestibular/acompanhamento/confirma_dados_pessoais.jsp</li>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/senha.jsp</li>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/upload_foto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String formDadosPessoais() {
		obj.prepararDados();
		return forward("/public/vestibular/acompanhamento/novo_cadastro.jsp");
	}
	
	/**
	 * Redireciona o usu�rio para a p�gina principal de acompanhamento do
	 * vestibular.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/confirma_dados_pessoais.jsp</li>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/senha.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String formUploadFoto(){
		return forward("/public/vestibular/acompanhamento/upload_foto.jsp");
	}
	
	/** Redireciona o usu�rio para a lista de Processos Seletivos Vestibular cadastrados.<br/>M�todo n�o invocado por JSP�s.
	 * 
 	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>N�o invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String listaVestibulares() {
		return forward("/public/vestibular/lista.jsp");
	}
	
	/** Redireciona o usu�rio para o formul�rio de recupera��o de senha.<br/>M�todo n�o invocado por JSP�s.
	 * 
 	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>N�o invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String formRecuperarSenha(){
		return forward("/public/vestibular/acompanhamento/recuperar_senha.jsp");
	}
	
	/** Redireciona o usu�rio para a formul�rio de defini��o de senha.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/confirma_dados_pessoais.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formSenha() {
		return forward("/public/vestibular/acompanhamento/senha.jsp");
	}
	
	// m�todos de controle

	/**
	 * Redefine a senha do candidato, enviando um e-mail com instru��es de como
	 * proceder para mudar a senha atual.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/nova_senha.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String redefinirSenha() throws ArqException, NegocioException {
		ValidatorUtil.validateRequired(novaSenha, "Senha", erros);
		ValidatorUtil.validateRequired(confirmacaoSenha, "Confirma��o de Senha", erros);
		if (novaSenha != null && !novaSenha.equals(this.confirmacaoSenha))
			addMensagemErro("As senhas informadas n�o s�o iguais. Por favor, digite-as novamente.");
		if (getParameterInt("id") == null) {
			addMensagemErro("N�o foi encontrada uma solicita��o de recupera��o de senha ativa. Por favor, reinicie o processo.");
		}
		if (hasErrors()) return null;
		int id = getParameterInt("id");
		PessoaVestibular pessoa = getGenericDAO().findByPrimaryKey(id, PessoaVestibular.class);
		if (pessoa == null) {
			addMensagemErro("N�o foi encontrada uma solicita��o de recupera��o de senha ativa. Por favor, reinicie o processo.");
			return null;
		}
		pessoa.setSenha(UFRNUtils.toMD5(confirmacaoSenha, ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.SALT_SENHAS_USUARIOS)));
		MovimentoPessoaVestibular mov = new MovimentoPessoaVestibular();
		mov.setPessoaVestibular(pessoa);
		mov.setSenhaAberta(confirmacaoSenha);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR);
		prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR);
		execute(mov);
		addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Senha");
		removeOperacaoAtiva();
		obj = null;
		return cancelar();
	}
	
	/**
	 * Inicia uma nova inscri��o no processo seletivo. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String realizarNovaInscricao() throws ArqException{
		if (!isLogado()) return usuarioNaoLogado();
		if (!processoSeletivo.isInscricoesCandidatoAbertas()) {
			addMensagemErro(String.format("N�o est� no per�odo de inscri��es: de %1$td/%1$tm/%1$tY � %2$td/%2$tm/%2$tY", processoSeletivo.getInicioInscricaoCandidato(), processoSeletivo.getFimInscricaoCandidato()));
			return null;
		}
		operacao = NOVA_INSCRICAO;
		obj = getGenericDAO().refresh(obj);
		if (obj.getMunicipio() != null ) 
			setBrasil(true);
		else
			setBrasil(false);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR);
		return confirmaDadosPessoais();
	}
	
	/** Permite ao candidato atualizar seus dados pessoais.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/confirma_dados_pessoais.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String atualizarDadosPessoais() throws ArqException{
		if (!isLogado()) return usuarioNaoLogado();
		obj = getGenericDAO().findAndFetch(obj.getId(), PessoaVestibular.class, "enderecoContato", "pais", "estadoCivil", "tipoRaca");
		obj.prepararDados();
		if(obj.getPais().getId() == Pais.BRASIL)
			setBrasil(true);
		else
			setBrasil(false);
		getGenericDAO().refresh(obj.getTituloEleitor().getUnidadeFederativa());
		getGenericDAO().refresh(obj.getIdentidade().getUnidadeFederativa());
		carregarMunicipiosEndereco(obj.getEnderecoContato().getUnidadeFederativa().getId());
		carregarMunicipiosNaturalidade(obj.getUnidadeFederativa().getId());
		if (obj.getUfConclusaoEnsinoMedio() != null){
			Integer idUfEnsinoMedio = obj.getUfConclusaoEnsinoMedio().getId();
			obj.setUfConclusaoEnsinoMedio(new UnidadeFederativa());
			obj.getUfConclusaoEnsinoMedio().setId(idUfEnsinoMedio);
		}
		if(obj.getMunicipio() != null){
			Integer idMunicipio = obj.getMunicipio().getId();
			obj.setMunicipio(new Municipio());
			obj.getMunicipio().setId(idMunicipio);
		}
		if(obj.getEnderecoContato() != null && obj.getEnderecoContato().getMunicipio() != null){
			Integer idMunicipio = obj.getEnderecoContato().getMunicipio().getId();
			obj.getEnderecoContato().setMunicipio(new Municipio());
			obj.getEnderecoContato().getMunicipio().setId(idMunicipio);
		}
		if (obj.getUfConclusaoEnsinoMedio() == null)
			obj.setUfConclusaoEnsinoMedio(new UnidadeFederativa());
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR);
		if (operacao == NOVA_INSCRICAO)
			operacao = NOVA_INSCRICAO_ATUALIZAR_DADOS_PESSOAIS;
		else
			operacao = ALTERAR_DADOS_PESSOAIS;
		return formDadosPessoais();
	}
	
	/** Inicializa os atributos do controller.
	 * @throws DAOException 
	 * @throws DAOException
	 */
	private void inicializaAtributos() throws DAOException {
		obj = new PessoaVestibular();
		this.brasil = true;
		obj.setUfConclusaoEnsinoMedio(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		obj.getTituloEleitor().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		foto = null;
		obj.setSexo(Pessoa.SEXO_MASCULINO);
		confirmacaoSenha = null;
		inscricoesRealizadas = null;
		popularMunicipios();
	}
	
	/**
	 * Popular campos de munic�pios que ser�o utilizados no formul�rio
	 * 
	 * @throws DAOException
	 */
	private void popularMunicipios() throws DAOException {
		// Popular munic�pios para campo de naturalidade
		int uf = UnidadeFederativa.ID_UF_PADRAO;
		if (obj.getUnidadeFederativa() != null && obj.getUnidadeFederativa().getId() > 0)
			uf = obj.getUnidadeFederativa().getId();
		carregarMunicipiosNaturalidade(uf);	
		
		//Popular munic�pios para campo de endere�o
		uf = UnidadeFederativa.ID_UF_PADRAO;
		if (obj.getEnderecoContato()  != null 
				&& obj.getEnderecoContato().getUnidadeFederativa() != null 
				&& obj.getEnderecoContato().getUnidadeFederativa().getId() > 0) {
			uf = obj.getEnderecoContato().getUnidadeFederativa().getId();
		}
		carregarMunicipiosEndereco(uf);
	}
	
	/** Realiza o logon na �rea pessoal do candidato.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/logon.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String logon() throws DAOException, NegocioException{
		ValidatorUtil.validateRequired(obj.getCpf_cnpj(), "CPF", erros);
		if (obj.getCpf_cnpj() != null)
			ValidatorUtil.validateCPF_CNPJ(obj.getCpf_cnpj(), "CPF", erros);
		ValidatorUtil.validateRequired(obj.getSenha(), "Senha", erros);
		if (hasErrors()) return null;
		PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
		try {
			PessoaVestibular usuario = dao.findByLoginSenha(obj.getCpf_cnpj(), UFRNUtils.toMD5(obj.getSenha(), ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.SALT_SENHAS_USUARIOS)), getRegistroEntrada());
			if (usuario == null) {
				addMensagemErro("CPF e/ou senha incorretos");
				return null;
			}
			this.obj = usuario;	
		} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
			return null;
		}finally {
			dao.close();	
		}
		inscricoesRealizadas = null;
		// verifica se o usu�rio est� com a foto v�lida
		if (obj.getIdFoto() == null) {
			return forward("/public/vestibular/acompanhamento/aviso_foto_invalida.jsp");
		}
		return paginaAcompanhamento();
	}

	/** Efetua o logoff da �rea pessoal do candidato. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/logon.jsp</li>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * <li>/sigaa.war/public/vestibular/inscricao/comprovante.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String logoff() throws DAOException {
		this.obj = new PessoaVestibular();
		ProcessoSeletivoVestibularMBean p = getMBean("processoSeletivoVestibular");
		p.resetBean();
		return listaVestibulares();
	}

	/**
	 * Emite o extrato de desempenho do candidato no vestibular.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String extratoDesempenho() throws DAOException{
		if (!isLogado()) return usuarioNaoLogado();
		addMensagemErro("N�o h� Extrato de Desempenho Dispon�vel.");
		return null;
	}
	
	/**
	 * Imprime a GRU para pagamento da taxa de inscri��o do vestibular.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String imprimirGRU() throws ArqException, NegocioException {
		if (!isLogado()) return usuarioNaoLogado();
		InscricaoVestibularMBean mBean = getMBean("inscricaoVestibular");
		int id = getParameterInt("id", 0);
		int inscricao = getParameterInt("inscricao", 0);
		return mBean.imprimirGRU(id, inscricao);
	}
	
	/**
	 * Emite o comprovante de inscri��o do candidato no vestibular.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String verComprovante() throws DAOException {
		if (!isLogado()) return usuarioNaoLogado();
		InscricaoVestibularMBean mBean = getMBean("inscricaoVestibular");
		int id = getParameterInt("id", 0);
		int inscricao = getParameterInt("inscricao", 0);
		return mBean.verComprovante(id, inscricao);
	}
	
	/** Indica se o candidato est� logado na sua �rea pessoa.
	 * @return
	 */
	public boolean isLogado() {
		return obj != null && obj.getId() != 0 && processoSeletivo != null && processoSeletivo.getId() != 0;
	}
	
	/** Adiciona uma mensagem avisando que o usu�rio n�o est� logado e redireciona para a listagem de processos seletivos.
	 * @return
	 */
	private String usuarioNaoLogado() {
		addMensagemErro("Por favor, realize o login na sua �rea pessoal.");
		return listaVestibulares();
	}

	/**
	 * Redireciona o usu�rio para a p�gina de acompanhamento do vestibular mais
	 * recente. Utilizado para links diretos de acessos externos.
	 * 
  	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>N�o invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String viewVestibularMaisRecente() throws ArqException{
		ProcessoSeletivoVestibularDao dao = getDAO(ProcessoSeletivoVestibularDao.class);
		Collection<ProcessoSeletivoVestibular> processos = dao.findByInscricaoCandidatoAtivo();
		if (!isEmpty(processos)) {
			processoSeletivo = processos.iterator().next();
			return redirect("/public/vestibular/acompanhamento/instrucoes_nova_inscricao.jsf");
		} else { 
			return redirect("/sigaa/public/vestibular/lista.jsf");
		}
	}

	/**
	 * Redireciona o usu�rio para a p�gina de acompanhamento do vestibular. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/view.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String acompanhamento() throws DAOException{
		int idProcessoSeletivo = getParameterInt("id", 0);
		GenericDAO dao = getGenericDAO();
		processoSeletivo = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
		// evitando lazy exception
		if (processoSeletivo != null && processoSeletivo.getQuestionario() != null){
			Questionario questionario = dao.findByPrimaryKey(processoSeletivo.getQuestionario().getId(), Questionario.class);
			questionario.setPerguntas((List<PerguntaQuestionario>) 
						getDAO(PerguntaQuestionarioDao.class).findAllPerguntasQuestionario(processoSeletivo.getQuestionario()));
			processoSeletivo.setQuestionario(questionario);
		}
		if (processoSeletivo == null) {
			addMensagemErro("N�o foi poss�vel determinar o Processo Seletivo. Por favor, inicie a opera��o novamente.");
			return cancelar();
		} else
			return formLogon();
	}

	/**
	 * Realiza um novo cadastro de dados pessoais do candidato do vestibular. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>
	 * /sigaa.war/public/vestibular/acompanhamento/confirma_dados_pessoais.jsp</li>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/logon.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String novoCadastro() throws ArqException{
		if(processoSeletivo.isInscricoesCandidatoAbertas()){
			inicializaAtributos();
			carregarMunicipios();
			operacao = NOVO_CADASTRO;
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR.getId());
			prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR);
			return formDadosPessoais();	
			
		}else{
			addMensagemErro("As Inscri��es para canditatos n�o est�o em aberto. Por favor, aguarde o prazo de inscri��o.");
			return redirect("/sigaa/public/vestibular/acompanhamento/logon.jsf");
		}
	}
	
	/** Recebe o upload e valida a foto 3x4 do candidato.
	 * 
  	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/upload_foto.jsp</li>
	 * </ul>
	 *   
	 * @return
	 * @throws IOException
	 */
	public String enviarArquivo() throws IOException {
		int largura = 0;
		int altura = 0;
		double resolucao = 0.0;
		Map<String, Object> dadosFoto = new LinkedHashMap<String, Object>();
		if (arquivoUpload != null &&
				("image/jpeg".equalsIgnoreCase(arquivoUpload.getContentType())
						||"image/pjpeg".equalsIgnoreCase(arquivoUpload.getContentType()))) {
			try{
				BufferedImage imagem = null; 
					imagem = ImageIO.read(arquivoUpload.getInputStream());
				dadosFoto = new LinkedHashMap<String, Object>();
				largura = imagem.getWidth(null);
				altura = imagem.getHeight(null);
				dadosFoto.put("Lagura", largura);
				dadosFoto.put("Altura", altura);
				// resolu��o m�nima
				resolucao = (double) largura * altura / 1000;
				if (resolucao < 1000)
					dadosFoto.put("Resolu��o", String.format("%.1f KPixel", resolucao));
				else
					dadosFoto.put("Resolu��o", String.format("%.1f MPixel", resolucao / 1000));
				// tamanho do arquivo
				double tamanho = arquivoUpload.getBytes().length / 1024;
				if (tamanho < 1024)
					dadosFoto.put("Tamanho do Arquivo", String.format("%.1f KBytes",tamanho));
				else
					dadosFoto.put("Tamanho do Arquivo", String.format("%.1f MBytes", tamanho/1024));
			} catch (Exception e) {
				addMensagemErro("N�o foi poss�vel abrir o arquivo enviado ou o arquivo enviado n�o � arquivo de foto v�lido.");
				return null;
			}
		} else {
			addMensagemErro("Por favor, envie um arquivo v�lido no formato JPEG (arquivo terminado em .jpeg ou .jpg).");
		}
		if (!hasOnlyErrors()) {
			foto = arquivoUpload;
			this.dadosFoto = dadosFoto;
			addMessage("Foto Enviada com sucesso!",	TipoMensagemUFRN.INFORMATION);
		}
		return null;
	}
	
	/** Inicia o processo de recupera��o de senha do candidato.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/logon.jsp</li>
	 * </ul>
	 * @return
	 */
	public String solicitarSenha(){
		this.captcha = null;
		return formRecuperarSenha();
	}
	
	/** Solicita a recupera��o de senha do candidato. Ser� enviado ao candidato, um e-mail com instru��es para recuperar a senha.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/recuperar_senha.jsp</li>
	 * </ul>
	 * @return
	 */
	public String recuperarSenha(){
		if(!validaCaptcha(captcha)) {
			addMensagemErro("Imagem: Valor inv�lido.");
			captcha = null;
			return null;
		}
		try {
			PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
			PessoaVestibular pessoa = dao.findByCPF(obj.getCpf_cnpj());
			if (pessoa != null) {
				if (pessoa.getEmail() != null && pessoa.getEmail().equalsIgnoreCase(obj.getEmail())) {
					MovimentoPessoaVestibular mov = new MovimentoPessoaVestibular();
					mov.setPessoaVestibular(pessoa);
					mov.setCodMovimento(SigaaListaComando.RECUPERAR_SENHA_VESTIBULAR);
					prepareMovimento(SigaaListaComando.RECUPERAR_SENHA_VESTIBULAR);
					execute(mov);
					return forward("/public/vestibular/acompanhamento/recuperacao_enviada.jsf");
				} else {
					addMensagem(MensagensGerais.EMAIL_DISTINTO,
							ParametroHelper.getInstance().getParametro(
							ParametrosVestibular.SIGLA_COMISSAO_RESPONSAVEL_PELO_VESTIBULAR));
				}
			} else {
				addMensagemErro("Usu�rio n�o localizado no sistema");
			}
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}
	
	/** Valida se foi enviado a foto 3x4 do candidato.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/upload_foto.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String submeterFoto() throws DAOException {
		if (foto == null && ValidatorUtil.isEmpty(obj.getIdFoto())) {
			addMensagemErro("Por favor, envie um arquivo de foto v�lido.");
			return null;
		} 
		erros.addAll(obj.validate());
		if (obj.getId() == 0 && obj.getCpf_cnpj() != null)
			validaCPFCadastrado(null);
		if (hasErrors())
			return null;
		if (ValidatorUtil.isEmpty(obj.getSenha()))
			return formSenha();
		else
			return confirmaDadosPessoais();
	}
	
	/**
	 *  Renderiza a foto no formul�rio de upload de foto.
	 * 
  	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/confirma_dados_pessoais.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/local_prova.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/upload_foto.jsp</li>
	 * </ul>
	 * 
	 * @param g2d
	 * @param obj
	 * @throws IOException
	 */
	public void imagemFoto(Graphics2D g2d, Object obj) throws IOException {
		 BufferedImage imagem = ImageIO.read(foto.getInputStream());
		 double largura = imagem.getWidth(null);
		 double altura = imagem.getHeight(null);
		 largura = 150/largura;
		 altura = 200/altura;
		 AffineTransform xform = AffineTransform.getScaleInstance(largura, largura);
		 g2d.drawRenderedImage(imagem, xform);
     }
	 
	/**
	 * Valida se ao CPF do candidato j� tem um cadastro associado.
	 * 
  	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public boolean validaCPFCadastrado(ActionEvent evt) throws DAOException{
		ListaMensagens lista = new ListaMensagens();
		if (obj.getCpf_cnpj() == 0)
			obj.setCpf_cnpj(null);
		validateRequired(obj.getCpf_cnpj(), "CPF", lista);
		if (obj.getCpf_cnpj() != null)
			validateCPF_CNPJ(obj.getCpf_cnpj(), "CPF", lista);
		if (lista.isErrorPresent()) {
			addMensagensAjax(lista);
			return false;
		}
		PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
		PessoaVestibular pessoaDB = dao.findByCPF(obj.getCpf_cnpj());
		boolean cadastroAntigo = false;
		// se tem pelo menos uma inscri��o que n�o foi migrada, o candidato dever� fazer o login na sua �rea pessoal
		if (pessoaDB != null && !pessoaDB.isMigrada()) {
			cadastroAntigo = true;
		}
		if (cadastroAntigo) {
			if (evt != null)
				addMensagemErroAjax("Existe um cadastro para o CPF "
						+ obj.getCpf_cnpjString()
						+ ". Utilize o login/senha do seu cadastro para acessar sua �rea pessoal.");
			else
				addMensagemErro("Existe um cadastro para o CPF "
						+ obj.getCpf_cnpjString()
						+ ". Utilize o login/senha do seu cadastro para acessar sua �rea pessoal.");
			return false;
		}
		return true;
	 }
	
	 /** Altera a senha de acesso � �rea pessoal do candidato.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * <li>/sigaa.war/public/vestibular/acompanhamento/senha.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String alterarSenha() throws ArqException {
		if (!isLogado()) 
			return logoff();
		 operacao = ALTERAR_SENHA;
		 setOperacaoAtiva(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR.getId());
		 prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR);
		 return formSenha();
	 }
	 
	 /** 
	  * Inicia a altera��o da foto 3x4 do candidato.<br/>M�todo n�o invocado por JSP�s.
	  * 
	  * M�todo chamado pela(s) seguinte(s) JSP(s):
 	  * <ul>
	  * <li>M�todo n�o invocado por jsp.</li>
	  * </ul>
      * 
	  * @return
	  * @throws ArqException
	  */
	public String alterarFoto() throws ArqException {
		 if (operacao == NOVA_INSCRICAO)
				operacao = NOVA_INSCRICAO_ATUALIZAR_DADOS_PESSOAIS;
			else
				operacao = ALTERAR_FOTO;
		 setOperacaoAtiva(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR.getId());
		 prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR);
		 return formUploadFoto();
	 }
	 
	/**
	 * Valida os dados pessoais.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @throws ParseException 
	 */
	public String submeterDadosPessoais() throws DAOException, ParseException {
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR.getId())) return listaVestibulares();
		erros.addAll(obj.validate());
		if (hasErrors())
			return null;
		if (obj.getId() == 0 && obj.getCpf_cnpj() != null && !validaCPFCadastrado(null))
			return null;
		// completa alguns dados para visualiza��o
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		obj.setTipoRaca(dao.refresh(obj.getTipoRaca()));
		obj.setTipoNecessidadeEspecial(dao.refresh(obj.getTipoNecessidadeEspecial()));
		obj.setEstadoCivil(dao.refresh(obj.getEstadoCivil()));
		obj.setPais(dao.refresh(obj.getPais()));
		if(isBrasil()){
			obj.setUnidadeFederativa(dao.refresh(obj.getUnidadeFederativa()));
			obj.setMunicipio(dao.refresh(obj.getMunicipio()));
		} else {
			obj.setUnidadeFederativa(null);
			obj.setMunicipio(null);
		}
		obj.getIdentidade().setUnidadeFederativa(dao.refresh(obj.getIdentidade().getUnidadeFederativa()));
		obj.getTituloEleitor().setUnidadeFederativa(dao.refresh(obj.getTituloEleitor().getUnidadeFederativa()));
		Endereco endereco = obj.getEnderecoContato();
		endereco.setTipoLogradouro(dao.refresh(endereco.getTipoLogradouro()));
		endereco.setMunicipio(dao.refresh(endereco.getMunicipio()));
		endereco.setUnidadeFederativa(dao.refresh(endereco.getUnidadeFederativa()));
		// verificando a escola de conclus�o do EM
		if (obj.getEscolaConclusaoEnsinoMedio() != null && obj.getEscolaConclusaoEnsinoMedio().getNome() != null
				&& !obj.getEscolaConclusaoEnsinoMedio().getNome().equals(obj.getNomeEscolaConclusaoEnsinoMedio()))
			obj.setEscolaConclusaoEnsinoMedio(new EscolaInep());
		
		permiteAlterarFoto = permiteAlterarFoto();
		return formUploadFoto();
	}
	
	/**
	 * Valida se o candidato pode ou n�o alterar a sua foto.
	 * 
	 * @return
	 * @throws ParseException
	 * @throws DAOException
	 */
	private boolean permiteAlterarFoto() throws ParseException, DAOException{
		if (processoSeletivo == null
				|| processoSeletivo.getFimInscricaoCandidato() == null
				|| processoSeletivo.getInicioLimiteAlteracaoFotos() == null
				|| processoSeletivo.getFimLimiteAlteracaoFotos() == null)
			return false;
		Date fimEnvioFoto = CalendarUtils.adicionaUmDia(processoSeletivo.getFimInscricaoCandidato());
		if (obj.getStatusFoto().isValida() == false 
				&& (CalendarUtils.isDentroPeriodo(processoSeletivo.getInicioInscricaoCandidato(), fimEnvioFoto)
				|| CalendarUtils.isDentroPeriodo(processoSeletivo.getInicioLimiteAlteracaoFotos(), 
						processoSeletivo.getFimLimiteAlteracaoFotos()))) 
			return true;
		return false;
	}
	
	/** Valida as senhas digitadas pelo usu�rio.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/senha.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String submeterSenha() throws SegurancaException, ArqException, NegocioException{
		if (obj.getId() != 0) {
			ValidatorUtil.validateRequired(senhaAtual, "Senha Atual", erros);
			ValidatorUtil.validateRequired(novaSenha, "Senha", erros);
			if (hasErrors()) return null;
			if (!obj.getSenha().equals(UFRNUtils.toMD5(senhaAtual, ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.SALT_SENHAS_USUARIOS))))
				addMensagemErro("A senha digitada n�o confere com a senha atual.");
			if (novaSenha != null && !novaSenha.equals(this.confirmacaoSenha))
				addMensagemErro("As senhas informadas n�o s�o iguais. Por favor, digite-as novamente.");
		} else {
			ValidatorUtil.validateRequired(novaSenha, "Senha", erros);
			ValidatorUtil.validateRequired(confirmacaoSenha, "Confirma��o da Senha", erros);
			if (hasErrors()) return null;
			if (novaSenha != null && !novaSenha.equals(this.confirmacaoSenha))
				addMensagemErro("As senhas informadas n�o s�o iguais. Por favor, digite-as novamente.");
		}
		ValidatorUtil.validateMinLength(confirmacaoSenha, 4, "Senha", erros);
		ValidatorUtil.validateMaxLength(confirmacaoSenha, 64, "Senha", erros);
		if (foto == null && ValidatorUtil.isEmpty(obj.getIdFoto())) {
			addMensagemErro("Por favor, envie um arquivo de foto v�lido.");
			return null;
		} 
		erros.addAll(obj.validate());
		if (obj.getId() == 0 && obj.getCpf_cnpj() != null)
			validaCPFCadastrado(null);
		if (hasErrors()) return null;
		obj.setSenha(UFRNUtils.toMD5(confirmacaoSenha, ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.SALT_SENHAS_USUARIOS)));
		if (isAlterarSenha())
			return cadastrar();
		if (foto == null) 
			return formUploadFoto();
		return confirmaDadosPessoais();
	}
	
	/** 
	 * Cancela a opera��o atual.
	 * 
   	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/confirma_dados_pessoais.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/nao_recuperado.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/nova_senha.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/recuperacao_enviada.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/recuperar_senha.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/senha.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/upload_foto.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		// se logado, volta para a �rea pessoal
		operacao = 0;
		getGenericDAO().clearSession();
		if (obj != null && obj.getId() != 0)
			return paginaAcompanhamento();
		else {
			// caso contr�rio, volta para �rea p�blica
			obj = new PessoaVestibular();
			return redirect("/sigaa/public");
		}
	}
	
	/** 
	 * Retorna a senha redigitada para confirma��o. 
	 * @return
	 */
	public String getConfirmacaoSenha() {
		return confirmacaoSenha;
	}

	/** Seta a senha redigitada para confirma��o.
	 * @param confirmacaoSenha
	 */
	public void setConfirmacaoSenha(String confirmacaoSenha) {
		if (confirmacaoSenha != null) confirmacaoSenha = confirmacaoSenha.trim();
		this.confirmacaoSenha = confirmacaoSenha;
	}
	
	/**
	 * Verifica o pais selecionado pelo candidato para nacionalidade e ajusta os dados de munic�pio e UF no formul�rio. <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void alterarPais(ValueChangeEvent e) throws DAOException {
		Integer idPais = (Integer) e.getNewValue();
		brasil = (idPais == Pais.BRASIL);
		if (brasil && obj.getMunicipio() == null) {
			obj.setMunicipio(new Municipio());
			obj.setUnidadeFederativa(new UnidadeFederativa());
		}
		obj.setPais(getGenericDAO().findByPrimaryKey(idPais, Pais.class));
	}
	
	/** Indica se o pa�s selecionado no formul�rio de dados pessoais � o brasil. Se for false n�o � pra renderizar estado e nem munic�pio.
	 * @return
	 */
	public boolean isBrasil() {
		return brasil;
	}

	/** Seta se o pa�s selecionado no formul�rio de dados pessoais � o brasil. Se for false n�o � pra renderizar estado e nem munic�pio.
	 * @param brasil
	 */
	public void setBrasil(boolean brasil) {
		this.brasil = brasil;
	}
	
	/**
	 * Carrega a lista de munic�pios de naturalidade e endere�o.
	 * 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 *
	 * @throws DAOException
	 */
	public void carregarMunicipios() throws DAOException {
		MunicipioDao dao = getDAO(MunicipioDao.class);
		int uf = UnidadeFederativa.ID_UF_PADRAO;
		if (obj.getUnidadeFederativa() != null
				&& obj.getUnidadeFederativa().getId() > 0)
			uf = obj.getUnidadeFederativa().getId();
		UnidadeFederativa ufNatur = dao.findByPrimaryKey(uf,
				UnidadeFederativa.class);

		Collection<Municipio> municipios = dao.findByUF(uf);
		municipiosNaturalidade = new ArrayList<SelectItem>(0);
		municipiosNaturalidade.add(new SelectItem(ufNatur.getCapital().getId(),
				ufNatur.getCapital().getNome()));
		municipiosNaturalidade.addAll(toSelectItems(municipios, "id", "nome"));

		uf = UnidadeFederativa.ID_UF_PADRAO;
		if (obj.getEnderecoContato() != null
				&& obj.getEnderecoContato().getUnidadeFederativa() != null
				&& obj.getEnderecoContato().getUnidadeFederativa()
						.getId() > 0)
			uf = obj.getEnderecoContato().getUnidadeFederativa()
					.getId();
		UnidadeFederativa ufEnd = dao.findByPrimaryKey(uf,
				UnidadeFederativa.class);

		municipios = dao.findByUF(uf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		municipiosEndereco.add(new SelectItem(ufEnd.getCapital().getId(), ufEnd
				.getCapital().getNome()));
		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
		if (obj.getEnderecoContato() == null) {
			obj.setEnderecoContato(new Endereco());
			obj.getEnderecoContato().setUnidadeFederativa(ufEnd);
			obj.getEnderecoContato().setMunicipio(ufEnd.getCapital());
		}
	}
	
	/**
	 * Carrega a lista de munic�pios de endere�o de uma determinada UF.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param idUf
	 * @throws DAOException
	 */
	public void carregarMunicipiosEndereco(Integer idUf) throws DAOException {
		if (idUf == null) {
			idUf = obj.getEnderecoContato().getUnidadeFederativa().getId();
		}

		MunicipioDao dao = getDAO(MunicipioDao.class);
		try {
			UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
			Collection<Municipio> municipios = dao.findByUF(idUf);
			municipiosEndereco = new ArrayList<SelectItem>(0);
			municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
			municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
	
			obj.getEnderecoContato().setUnidadeFederativa(new UnidadeFederativa());
			obj.getEnderecoContato().getUnidadeFederativa().setId(uf.getId());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Listener respons�vel por setar o valor da UF de conclus�o do Ensino
	 * M�dio.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void setaMunicipioConclusaoEnsinoMedio(ValueChangeEvent e) throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer idMunicipio = (Integer) e.getNewValue();
			if (idMunicipio != null) {
				EscolaInepDao dao = getDAO(EscolaInepDao.class);
				escolasInep = dao.findByMunicipioUF(idMunicipio, 0);
			}
		}
		obj.setEscolaConclusaoEnsinoMedio(new EscolaInep());
		obj.setNomeEscolaConclusaoEnsinoMedio(null);
	}
	
	/**
	 * Listener respons�vel por setar o valor do munic�pio da escola de conclus�o do Ensino
	 * M�dio.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void setaUfConclusaoEnsinoMedio(ValueChangeEvent e)
			throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();
			if (ufId != null) {
				escolasInep = new ArrayList<EscolaInep>();
			}
		}
		obj.setEscolaConclusaoEnsinoMedio(new EscolaInep());
		obj.setNomeEscolaConclusaoEnsinoMedio(null);
	}
	
	/**
	 * Listener respons�vel por carregar a lista de munic�pios de naturalidade
	 * ou endere�o, caso o valor da UF seja alterado no formul�rio.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMunicipios(ValueChangeEvent e) throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();
			if (selectId.toLowerCase().contains("natur")) {
				carregarMunicipiosNaturalidade(ufId);
			} else if (selectId.toLowerCase().contains("end")) {
				carregarMunicipiosEndereco(ufId);
			}
		}
	}
	
	/**
	 * Carrega a lista de munic�pios de naturalidade de uma determinada UF.
	 * 
   	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>N�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * 
	 * @param idUf
	 * @throws DAOException
	 */
	public void carregarMunicipiosNaturalidade(Integer idUf)
			throws DAOException {
		if (idUf == null) {
			idUf = obj.getUnidadeFederativa().getId();
		}

		MunicipioDao dao = getDAO(MunicipioDao.class);
		try{
			UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
			Collection<Municipio> municipios = dao.findByUF(idUf);
			municipiosNaturalidade = new ArrayList<SelectItem>(0);
			if (uf.getCapital() != null)
				municipiosNaturalidade.add(new SelectItem(uf.getCapital().getId(),uf.getCapital().getNome()));
			municipiosNaturalidade.addAll(toSelectItems(municipios, "id", "nome"));
	
			obj.setUnidadeFederativa(new UnidadeFederativa());
			obj.getUnidadeFederativa().setId(uf.getId());
		} finally {
			dao.close();
		}
	}
	
	
	
	/**
	 * Retorna uma lista de SelectItem de tipos de necessidades especiais.
	 * 
   	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getAllTipoNecessidadeEspecialCombo() throws DAOException {
		if (tipoNecessidadeCombo == null) {
			tipoNecessidadeCombo = new ArrayList<SelectItem>();
			for (TipoNecessidadeEspecial tipo : getGenericDAO().findAll(TipoNecessidadeEspecial.class, "descricao", "asc")) {
				if (tipo.getId() > 0)
					tipoNecessidadeCombo.add(new SelectItem(tipo.getId(), tipo.getDescricao()));
			}
		}
		return tipoNecessidadeCombo;
	}
	
	/** Retorna uma lista de SelectItem para escolha do munic�pio de naturalidade. 
	 * @return
	 */
	public Collection<SelectItem> getMunicipiosNaturalidade() {
		return municipiosNaturalidade;
	}

	/** Seta a lista de SelectItem para escolha do munic�pio de naturalidade.
	 * @param municipiosNaturalidade
	 */
	public void setMunicipiosNaturalidade(
			Collection<SelectItem> municipiosNaturalidade) {
		this.municipiosNaturalidade = municipiosNaturalidade;
	}

	/** Retorna uma lista de SelectItem para escolha do munic�pio do endere�o. 
	 * @return
	 */
	public Collection<SelectItem> getMunicipiosEndereco() {
		return municipiosEndereco;
	}

	/** Seta a lista de SelectItem para escolha do munic�pio do endere�o. 
	 * @param municipiosEndereco
	 */
	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
		this.municipiosEndereco = municipiosEndereco;
	}

	/**
	 * Retorna uma cole��o de EscolaInep para o recurso de autocompletar do
	 * formul�rio.
	 * 
   	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * acompanhamentoVestibular.autocompleteEscolaConclusao
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<EscolaInep> autocompleteEscolaConclusao(Object event)
			throws DAOException {
		String nome = event.toString();
		EscolaInepDao dao = getDAO(EscolaInepDao.class);
		obj.setEscolaConclusaoEnsinoMedio(new EscolaInep());
		return dao.findByNomeUF(nome, obj.getUfConclusaoEnsinoMedio().getId());
	}
	
	/**
	 * Atribui a escola de conclus�o de ensino m�dio selecionada pelo usu�rio.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void setEscolaInep(ActionEvent e) throws DAOException {
		int id = (Integer) e.getComponent().getAttributes().get("idEscola");
		EscolaInepDao dao = getDAO(EscolaInepDao.class);
		try {
			EscolaInep escola = dao.findByPrimaryKey(id, EscolaInep.class);
			if (escola != null) {
				obj.setEscolaConclusaoEnsinoMedio(escola);
				obj.setNomeEscolaConclusaoEnsinoMedio(null);
			}
		} finally {
			dao.close();
		}
	}
	
	/** "Pula" a confirma��o de dados pessoais e continua com o processo de inscri��o do vestibular.
	 *  <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/comprovante_cadastro_dados_pessoais.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String pulaConfirmacaoDadosPessoais() throws ArqException {
		inscricoesRealizadas = null;
		InscricaoVestibularMBean mBean = getMBean("inscricaoVestibular");
		return mBean.iniciarInscricao(processoSeletivo, obj, null);
	}
	
	/**
	 * Cadastra a inscri��o para o vestibular.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/confirma_dados_pessoais.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		addMensagens(obj.validate());
		if (hasErrors())
			return null;
		if (!isOperacaoAtiva(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR.getId())) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		// verificando se o praz de inscri��o expirou
		if (operacao == NOVA_INSCRICAO) {
			inscricoesRealizadas = null;
			InscricaoVestibularMBean mBean = getMBean("inscricaoVestibular");
			return mBean.iniciarInscricao(processoSeletivo, obj, null);
		} else {
			obj.anularAtributosVazios();
			obj.setTipoRedeEnsino(null);
			obj.setRegistroEntrada(getRegistroEntrada());		
			MovimentoPessoaVestibular mov = new MovimentoPessoaVestibular();
			mov.setPessoaVestibular(obj);
			mov.setFoto(foto);
			mov.setSenhaAberta(confirmacaoSenha);
			mov.setEnviaEmail(operacao != NOVO_CADASTRO);
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR);
			execute(mov);
			switch (operacao) {
				case ALTERAR_DADOS_PESSOAIS: addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Dados Pessoais"); break;
				case ALTERAR_FOTO: addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Foto"); break;
				case ALTERAR_SENHA: addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Senha"); break;
				case NOVO_CADASTRO: 
				case NOVA_INSCRICAO:
				case NOVA_INSCRICAO_ATUALIZAR_DADOS_PESSOAIS: addMensagemInformation("Dados Pessoais Cadastrados com sucesso!"); break;
			default:
				break;
			}
			
			removeOperacaoAtiva();
			inscricoesRealizadas = null;
			if (operacao == NOVA_INSCRICAO_ATUALIZAR_DADOS_PESSOAIS || operacao == NOVO_CADASTRO) {
				return forward("/public/vestibular/acompanhamento/comprovante_cadastro_dados_pessoais.jsp");
			} else {
				return paginaAcompanhamento();
			}
		}
	}

	/**
	 * Informa o local de prova do discente.
	 * 
   	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * </ul>
	 * 
	 * acompanhamentoVestibular.
	 * 
	 * @throws DAOException 
	 */
	public String localProva() throws DAOException{
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		try {
			inscricaoVestibular = dao.findValidadaByCpf(obj.getCpf_cnpj(), processoSeletivo.getId());			
			if (inscricaoVestibular == null) {
				addMensagemWarning("Ainda n�o foi definido o seu local de Prova. Consulte a agenda do candidato para saber em qual dia ser�o divulgados os locais de prova.");
				return null;
			}
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
			e.printStackTrace();
			return null;
		}
		return forward("/public/vestibular/acompanhamento/local_prova.jsp");
	}
	
	/**
	 * Redireciona o usu�rio para a p�gina contendo as inscri��es realizadas.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * <li></li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String listarInscricoes() throws DAOException {
		if (isEmpty(getInscricoesRealizadas())) {
			addMensagemErro("Voc� ainda n�o realizou uma inscri��o neste Processo Seletivo!");
			return null;
		} else {
			return forward("/public/vestibular/acompanhamento/lista_inscricoes.jsp");
		}
	}

	/** Retorna o arquivo de foto 3x4 do candidato. 
	 * @return
	 */
	public UploadedFile getFoto() {
		return foto;
	}

	/** Seta o arquivo de foto 3x4 do candidato.
	 * @param foto
	 */
	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

	/** 
	 * Retorna o mapa de dados da foto.
	 * 
   	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/upload_foto.jsp</li>
	 * </ul>
	 * 
	 * acompanhamentoVestibular.
	 *  
	 * @return
	 * @throws IOException 
	 */
	public Map<String, Object> getDadosFoto() throws IOException {
		if (dadosFoto == null) {
			Map<String, Object> dadosFoto = new LinkedHashMap<String, Object>();
			if (obj.getIdFoto() != null) {
				BufferedImage imagem = null;
				try {
					URL url = new URL(RepositorioDadosInstitucionais.getLinkSigaa()+ "/verFoto?idArquivo="+obj.getIdFoto()+"&key="+UFRNUtils.generateArquivoKey(obj.getIdFoto()));
					imagem = ImageIO.read(url);
				} catch (Exception e) {
				}
				if (imagem == null) return dadosFoto;
				int largura = imagem.getWidth(null);
				int altura = imagem.getHeight(null);
				dadosFoto.put("Lagura", largura);
				dadosFoto.put("Altura", altura);
				// verificando a qualidade da foto
				// propor��o da foto
				int numerador = Math.round(largura / (altura / 4));
				int denominador = Math.round(altura / (altura / 4));
				if (numerador != 3 || denominador != 4) {
					addMensagemErro("A imagem possui resolu��o de "+largura+"x"+altura+", estando na propor��o aproximada de "+numerador +"x"+denominador+". Por favor, envie outro arquivo no formato 3x4.");
				}
				// resolu��o m�nima
				double resolucao = (double) largura * altura / 1000;
				if (resolucao < 1000)
					dadosFoto.put("Resolu��o", String.format("%.1f KPixel", resolucao));
				else
					dadosFoto.put("Resolu��o", String.format("%.1f MPixel", resolucao / 1000));
				this.dadosFoto = dadosFoto; 
			}
		}
		return dadosFoto;
	}

	/** 
	 * Retorna o arquivo enviado pelo usu�rio, a ser utilizado como foto 3x4. 
	 * @return
	 */
	public UploadedFile getArquivoUpload() {
		return arquivoUpload;
	}

	/** Seta o arquivo enviado pelo usu�rio, a ser utilizado como foto 3x4.
	 * @param arquivoUpload
	 */
	public void setArquivoUpload(UploadedFile arquivoUpload) {
		this.arquivoUpload = arquivoUpload;
	}

	/** 
	 * Retorna a cole��o de inscri��es realizadas pelo candidato para o Processo Seletivo.
	 * 
   	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/acompanhamento.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/lista_inscricoes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<InscricaoVestibular> getInscricoesRealizadas() throws DAOException {
		if (!isLogado()) return null;
		if (inscricoesRealizadas == null) {
			InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
			inscricoesRealizadas = dao.findByPessoaVestibular(obj.getId(), processoSeletivo.getId());
		}
		return inscricoesRealizadas;
	}
	
	/** Indica que o controller est� controlando a altera��o da senha de acesso do candidato.
	 * @return
	 */
	public boolean isAlterarSenha(){
		return operacao == ALTERAR_SENHA;
	}
	
	/** Indica que o controller est� controlando a altera��o de dados pessoais do candidato.
	 * @return
	 */
	public boolean isAlterarDadosPessoais(){
		return operacao == ALTERAR_DADOS_PESSOAIS;
	}
	
	/** Indica que o controller est� controlando a altera��o da foto 3x4 do candidato.
	 * @return
	 */
	public boolean isAlterarFoto() {
		return operacao == ALTERAR_FOTO;
	}
	
	/** Indica que o controller est� controlando a inscri��o do candidato em um vestibular.
	 * @return
	 */
	public boolean isNovaInscricao() {
		return operacao == NOVA_INSCRICAO || operacao == NOVA_INSCRICAO_ATUALIZAR_DADOS_PESSOAIS;
	}
	
	/** Indica que a opera��o � novo cadastro de dados pessoais do candidato.
	 * @return
	 */
	public boolean isNovoCadastro() {
		return obj != null && obj.getId() == 0;
	}

	/** Retorna a senha atual do candidato, para confirma��o ao alterar a senha. 
	 * @return
	 */
	public String getSenhaAtual() {
		return senhaAtual;
	}

	/** Seta a senha atual do candidato, para confirma��o ao alterar a senha.
	 * @param senhaAtual
	 */
	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	/** Retorna o Processo Seletivo o qual o candidato est� acompanhando.
	 * @return
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	/** Retorna o c�digo de verifica��o de seguran�a.
	 * @return
	 */
	public String getCaptcha() {
		return captcha;
	}

	/** Seta o c�digo de verifica��o de seguran�a.
	 * @param captcha
	 */
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	/** Retorna  a nova senha a ser definida ao alterar a senha. 
	 * @return
	 */
	public String getNovaSenha() {
		return novaSenha;
	}

	/** Seta  a nova senha a ser definida ao alterar a senha.
	 * @param novaSenha
	 */
	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	/** Seta a informa��o se � poss�vel modificar a fotos */
	public void setPermiteAlterarFoto(boolean permiteAlterarFoto) {
		this.permiteAlterarFoto = permiteAlterarFoto;
	}
	
	/** Informa��o se � poss�vel modificar a fotos */
	public boolean isPermiteAlterarFoto() {
		return permiteAlterarFoto;
	}
	
	/**
	 * Tem como finalidade carregar todas as escolas de conclus�o.
	 * 
   	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>N�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getEscolasConclusao() {
		if (escolasInep != null) {
			return toSelectItems(escolasInep, "id", "nome");
		} else {
			return new ArrayList<SelectItem>();
		}
	}
	
	/**
	 * Retorna a inscri��o do Vestibular do Candidato
	 * @return
	 */
	public InscricaoVestibular getInscricaoVestibular() {
		return inscricaoVestibular;
	}

	/**
	 * Seta a inscri��o do Vestibular do Candidato
	 * @param inscricoesVestibular
	 */
	public void setInscricaoVestibular(InscricaoVestibular inscricaoVestibular) {
		this.inscricaoVestibular = inscricaoVestibular;
	}

	/**
	 * Verifica se existem dados cadastrados para acessar a �rea de acompanhamento. 
	 * Caso contr�rio redireciona para uma das p�ginas de in�cio de acesso ao 
	 * processo de inscri��o/acompanhamento.
	 * 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/vestibular/acompanhamento/instrucoes_nova_inscricao.jsp</li>
	 * </ul>
	 * 
	 * 
	 * acompanhamentoVestibular.verificarPersistenciaInscricao
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getVerificarPersistenciaInscricao() throws DAOException {
		if (isEmpty(obj)) {
			if ( isEmpty( processoSeletivo ) ) {
				addMensagemErro("Caro candidato, n�o foi identificada uma inscri��o ativa para o acesso ao acompanhamento do processo seletivo. Selecione o processo desejado para realizar seu acesso.");
				redirect( "/public/vestibular/lista.jsf");
			} else {
				addMensagemErro("Caro candidato, n�o foi identificada uma inscri��o ativa para o acesso ao acompanhamento do processo seletivo. Tente novamente acessar sua �rea pessoal ou, em caso de nova inscri��o, reiniciar o preenchimento do formul�rio.");
				redirect( "/public/vestibular/acompanhamento/instrucoes_nova_inscricao.jsf");
			}
		}
		return null;
	}

} 