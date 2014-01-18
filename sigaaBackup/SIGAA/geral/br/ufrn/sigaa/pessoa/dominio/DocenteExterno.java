/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on '21/03/2007'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isAllNotEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * O docente externo � um prestador de servi�o na IES. � uma pessoa de outra
 * institui��o que participa de atividades de ensino, como cursos Lato Sensu e
 * Extens�o.
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name = "docente_externo", schema = "ensino")
public class DocenteExterno implements Validatable, Docente {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_docente_externo", nullable = false)
	private int id;

	/** Dados pessoais do docente externo.*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa = new Pessoa();

	/** Forma��o acad�mica do docente.*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_formacao")
	private Formacao formacao = new Formacao();

	/** Institui��o de origem do docente.*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_instituicao")
	private InstituicoesEnsino instituicao = new InstituicoesEnsino();

	/** Tipo espec�fico de docente externo.*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_docente_externo")
	private TipoDocenteExterno tipoDocenteExterno = new TipoDocenteExterno();

	/** Servidor associado, nos casos quando o docente externo for um servidor t�cnico da institui��o.*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;
	
	/** Matr�cula de controle na institui��o (para docentes que ministrar�o aulas).*/
	private String matricula;

	/** Data em que o registro ir� expirar, suspendendo as atividades do docente.*/
	@Temporal(TemporalType.DATE)
	@Column(name = "prazo_validade", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date prazoValidade;

	/** Data em que o docente foi cadastrado no sistema.*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@CriadoEm
	private Date dataCadastro;

	/** Registro de entrada do usu�rio que cadastrou o docente externo. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Unidade � qual o docente externo est� vinculado.*/
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade", unique = false, nullable = true, insertable = true, updatable = true)
	private Unidade unidade;

	/** Status de validade do registro.*/
	private boolean ativo = true;	

	/** Perfil pessoal do docente. */
	@Column(name="id_perfil")
	private Integer idPerfil;

	/** Id do arquivo que cont�m a foto do docente. */
	@Column(name="id_foto")
	private Integer idFoto;
	
	/** N�vel de ensino ao qual o docente externo possui v�nculo. */
	@Column
	private Character nivel;

	/** Data em que o registro foi atualizado pela �ltima vez. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Registro de entrada do usu�rio que efetuou a �ltima altera��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Perfil pessoal do docente. */
	@Transient
	private PerfilPessoa perfil;

	/** Construtor padr�o. */
	public DocenteExterno() {
		this.servidor = new Servidor();
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param cpf
	 * @param nome
	 */
	public DocenteExterno(int id, Long cpf, String nome) {
		pessoa.setNome(nome);
		pessoa.setCpf_cnpj(cpf);
		setId(id);
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param cpf
	 * @param nome
	 * @param nomeUnidade
	 * @param siglaUnidade
	 */
	public DocenteExterno(int id, Long cpf, String nome, String nomeUnidade, String siglaUnidade) {
		pessoa.setNome(nome);
		pessoa.setCpf_cnpj(cpf);
		setId(id);
		unidade = new Unidade();
		unidade.setNome(nomeUnidade);
		unidade.setSigla(siglaUnidade);
	}

	/** Construtor parametrizado.
	 * @param id
	 */
	public DocenteExterno(int id) {
		this.id = id;
	}

	/** Retorna a forma��o acad�mica do docente.
	 * @return
	 */
	public Formacao getFormacao() {
		return formacao;
	}

	/** Seta a forma��o acad�mica do docente.
	 * @param formacao Forma��o acad�mica do docente.
	 */
	public void setFormacao(Formacao formacao) {
		this.formacao = formacao;
	}

	/** Retorna a chave prim�ria */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a institui��o de origem do docente.
	 * @return Institui��o de origem do docente.
	 */
	public InstituicoesEnsino getInstituicao() {
		return instituicao;
	}

	/** Seta a institui��o de origem do docente.
	 * @param instituicao Institui��o de origem do docente.
	 */
	public void setInstituicao(InstituicoesEnsino instituicao) {
		this.instituicao = instituicao;
	}

	/** Retorna os dados pessoais do docente externo.
	 * @return Dados pessoais do docente externo.
	 */
	public Pessoa getPessoa() {
		return pessoa;
	}

	/** Retorna os dados pessoais do docente externo.
	 * @param pessoa Dados pessoais do docente externo.
	 */
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	/** Compara o ID e a pessoa deste docente Externo com o passado por par�metro. */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "pessoa");
	}

	/** Calcula e retorna o c�digo hash deste objeto. */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Valida os dados: pessoa, institui��o, forma��o, tipo do docente externo e prazo de validade.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		PessoaValidator.validarDadosPessoais(pessoa, null, PessoaValidator.DOCENTE_EXTERNO, erros);
		ValidatorUtil.validateRequired(instituicao, "Institui��o", erros);
		ValidatorUtil.validateRequired(formacao, "Forma��o", erros);
		ValidatorUtil.validateRequired(tipoDocenteExterno, "Tipo", erros);
		ValidatorUtil.validateRequired(prazoValidade, "Prazo de Validade", erros);
		if(prazoValidade != null && prazoValidade.before(new Date()) && !DateUtils.isSameDay(prazoValidade, new Date()))
			erros.addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "V�lido At�", CalendarUtils.format(new Date(), "dd/MM/yyyy"));

		return erros;
	}

	/** Retorna o nome do docente externo. 
	 * @see br.ufrn.sigaa.pessoa.dominio.Docente#getNome()
	 */
	@Transient
	public String getNome() {
		if (!isEmpty(servidor))
			return servidor.getPessoa().getNome();
		return pessoa.getNome();
	}

	/** Retorna o nome da institui��o do docente e o nome do docente
	 * @return
	 */
	@Transient
	public String getNomeInstituicaoENomeDocente() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getNome() + " - ");
		if (getNomeInstituicao() != null)
			sb.append(getNomeInstituicao());
		return sb.toString();
	}

	@Transient
	/**
	 * Retorna o nome da institui��o do docente e o nome do docente
	 * 
	 * @return
	 */
	public String getNomeInstituicao() {
		if (instituicao == null)
			return null;
		return (isEmpty(instituicao.getSigla()) ? instituicao.getNome() : instituicao.getSigla());
	}
	
	/**
	 *  Retorna o nome da institui��o concatenado com o departamento
	 */
	public String getNomeInstituicaoEUnidade() {
		if (instituicao == null)
			return null;
		
		StringBuilder sb = new StringBuilder();
		
		if (isNotEmpty(instituicao.getSigla()))
			sb.append(instituicao.getSigla());
		else
			sb.append(instituicao.getNome());
		
		if (unidade!=null && unidade.getSigla()!=null)
			sb.append("/" + unidade.getSigla());
		
		return sb.toString();
	}	
	
	
	/** Retorna o CPF do docente externo.
	 * @return
	 */
	public String getCpfNome() {
		return getPessoa().getCpfCnpjFormatado() + " - " + getNome();
	}

	/** Retorna cpf da pessoa concatenado com nome formatado para o ajax.
	 * @return
	 */
	public String getCpfNomeFormatado() {
		return "<div style='border-bottom: dashed 1px; padding: 2px'><div float: left; text-align: right'>"
		+ ( !isEmpty(pessoa.getCpf_cnpj()) ? pessoa.getCpfCnpjFormatado() + " - " : "" )
		+ ( isEmpty(pessoa.getCpf_cnpj()) && !isEmpty(pessoa.getPassaporte()) ? pessoa.getPassaporte() + " - " : "" )
		+ pessoa.getNome() + "</div><div style='margin-left: 25px; font-size: x-small;'>"
		+ getUnidade().getSiglaNome() + "</div> </div>";
	}

	/** Retorna o tipo espec�fico de docente externo.
	 * @return Tipo espec�fico de docente externo.
	 */
	public TipoDocenteExterno getTipoDocenteExterno() {
		return tipoDocenteExterno;
	}

	/** Retorna o tipo espec�fico de docente externo.
	 * @param tipoDocenteExterno Tipo espec�fico de docente externo.
	 */
	public void setTipoDocenteExterno(TipoDocenteExterno tipoDocenteExterno) {
		this.tipoDocenteExterno = tipoDocenteExterno;
	}

	/** Retorna o CPF do docente.
	 * @see br.ufrn.sigaa.pessoa.dominio.Docente#getIdentificacao()
	 */
	@Transient
	public String getIdentificacao() {
		return pessoa.getCpf_cnpjString();
	}

	/** Retorna o servidor associado, nos casos quando o docente externo for um servidor t�cnico da Institui��o.
	 * @return Servidor associado, nos casos quando o docente externo for um servidor t�cnico da Institui��o.
	 */
	public Servidor getServidor() {
		return servidor;
	}

	/** Seta o servidor associado, nos casos quando o docente externo for um servidor t�cnico da Institui��o.
	 * @param servidor Servidor associado, nos casos quando o docente externo for um servidor t�cnico da Institui��o.
	 */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	/** Retorna a data em que o docente foi cadastrado no sistema.
	 * @return Data em que o docente foi cadastrado no sistema.
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a data em que o docente foi cadastrado no sistema.
	 * @param dataCadastro Data em que o docente foi cadastrado no sistema.
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Retorna a data em que o registro ir� expirar, suspendendo as atividades do docente.
	 * @return Data em que o registro ir� expirar, suspendendo as atividades do docente. 
	 */
	public Date getPrazoValidade() {
		return prazoValidade;
	}

	/** Seta a data em que o registro ir� expirar, suspendendo as atividades do docente.
	 * @param prazoValidade Data em que o registro ir� expirar, suspendendo as atividades do docente.
	 */
	public void setPrazoValidade(Date prazoValidade) {
		this.prazoValidade = prazoValidade;
	}

	/** Retorna o registro de entrada do usu�rio que cadastrou o docente externo.
	 * @return Registro de entrada do usu�rio que cadastrou o docente externo. 
	 */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Seta o registro de entrada do usu�rio que cadastrou o docente externo. 
	 * @param registroEntrada Registro de entrada do usu�rio que cadastrou o docente externo. 
	 */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/** Retorna a matr�cula de controle na institui��o (para docentes que ministrar�o aulas).
	 * @return Matr�cula de controle na institui��o (para docentes que ministrar�o aulas).
	 */
	public String getMatricula() {
		return matricula;
	}

	/** Seta a matr�cula de controle na institui��o (para docentes que ministrar�o aulas).
	 * @param matricula Matr�cula de controle na institui��o (para docentes que ministrar�o aulas).
	 */
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	/** Retorna a unidade � qual o docente externo est� vinculado.
	 * @return Unidade � qual o docente externo est� vinculado.
	 */
	public Unidade getUnidade() {
		return unidade;
	}

	/** Seta a unidade � qual o docente externo est� vinculado.
	 * @param unidade Unidade � qual o docente externo est� vinculado.
	 */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	/** Indica se o docente externo � ativo.
	 * @return
	 */
	public boolean isAtivo() {
		// se ativo, verifica a data de validade
		if (ativo) {
			// Verificar se o prazo adicional de acesso tamb�m foi excedido
			Calendar cal = Calendar.getInstance();
			cal.setTime(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
			cal.add(Calendar.DAY_OF_MONTH, -ParametroHelper.getInstance().getParametroInt(ParametrosGerais.PERIODO_ACESSO_ADICIONAL_DOCENTE_EXTERNO) - 1);
			Date dataLimite = cal.getTime();
			if (this.prazoValidade != null && CalendarUtils.estorouPrazo(this.prazoValidade, dataLimite))
				this.ativo = false;
		}
		return ativo;
	}

	/** Seta se o docente � ativo. 
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}	

	/** Retorna o perfil pessoal do docente.
	 * @return Perfil pessoal do docente. 
	 */
	public PerfilPessoa getPerfil() {
		return perfil;
	}

	/** Seta o perfil pessoal do docente. 
	 * @param perfil Perfil pessoal do docente. 
	 */
	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}

	/** Retorna o ID do Perfil pessoal do docente. 
	 * @return ID do Perfil pessoal do docente. 
	 */
	public Integer getIdPerfil() {
		return idPerfil;
	}

	/** Seta o ID do Perfil pessoal do docente. 
	 * @param idPerfil ID do Perfil pessoal do docente.
	 */
	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}

	/** Retorna o Id do arquivo que cont�m a foto do docente. 
	 * @return Id do arquivo que cont�m a foto do docente. 
	 */
	public Integer getIdFoto() {
		return idFoto;
	}

	/** Seta o Id do arquivo que cont�m a foto do docente. 
	 * @param idFoto Id do arquivo que cont�m a foto do docente. 
	 */
	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	/** Retorna uma representa��o textual do docente externo, no formato: CPF, seguido de '-', seguido do nome do docente.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getCpfNome();
	}

	/** Indica se o docente externo � colaborador volunt�rio.
	 * @return
	 */
	public boolean isColaboradorVoluntario() {
		return tipoDocenteExterno != null && tipoDocenteExterno.isColaboradorVoluntario();
	}

	/** Retorna a data em que o registro foi atualizado pela �ltima vez.
	 * @return Data em que o registro foi atualizado pela �ltima vez. 
	 */
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	/** Seta a data em que o registro foi atualizado pela �ltima vez. 
	 * @param dataAtualizacao Data em que o registro foi atualizado pela �ltima vez. 
	 */
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	/** Retorna o registro de entrada do usu�rio que efetuou a �ltima altera��o.
	 * @return Registro de entrada do usu�rio que efetuou a �ltima altera��o. 
	 */
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	/** Seta o registro de entrada do usu�rio que efetuou a �ltima altera��o. 
	 * @param registroAtualizacao Registro de entrada do usu�rio que efetuou a �ltima altera��o. 
	 */
	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/**
	 * Define como null atributos que estiverem com valores vazios
	 */
	public void anularAtributosVazios() {

		if (getServidor() != null && getServidor().getId() == 0  ){
			setServidor(null);
		}
		
	}

}
