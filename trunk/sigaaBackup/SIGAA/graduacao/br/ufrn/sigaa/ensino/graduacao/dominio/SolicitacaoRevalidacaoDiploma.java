/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/01/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;

/**
 * Classe de domínio que representa o processo seletivo de revalidação de diplomas.
 *
 * @author Mario Rizzi
 */
@Entity
@Table(name = "solicitacao_revalidacao_diploma", schema = "graduacao")
public class SolicitacaoRevalidacaoDiploma implements Validatable {
		
	/** Constante que define  o país que deverá ser selecionado a quando o inscrito iniciar as inscrições */
	public static final int NACIONALIDADE_PADRAO = 31;
	
	public static final int UF_RN = 24;
		
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_solicitacao_revalidacao_diploma", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Atribui a data e horário escolhido pelo inscritos para entrega dos documentos */
	@ManyToOne
	@JoinColumn(name = "id_agenda_revalidacao_diploma", nullable = true)
	private AgendaRevalidacaoDiploma agendaRevalidacaoDiploma;

	/** Atribui a inscrição a um processo de revalidação de diploma*/
	@ManyToOne
	@JoinColumn(name = "id_edital_revalidacao_diploma", nullable = true)
	private EditalRevalidacaoDiploma editalRevalidacaoDiploma;
	
	/** Atribui um número relacionado a um determinado processo */
	@Column(name = "num_processo", nullable = true)
	private Integer numProcesso;
	
	/** Atribui um ano relacionado a um determinado processo */
	@Column(name = "ano_processo", nullable = true)
	private Integer anoProcesso;
	
	/** Atribui um número de sequência para necessidade de se criar uma fila de espera */
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "fila", unique = true, nullable = false, insertable = true, updatable = true)
	private int fila;
	
	/** Atribui o tipo de logradouro (Rua/Av/Bloco) */
	@ManyToOne
	@JoinColumn(name = "id_tipo_logradouro")
	private TipoLogradouro tipoLogradouro;
	
	/** Atribui a nacionalidade */
	@ManyToOne
	@JoinColumn(name = "id_pais")
	private Pais pais;
	
	/** Utilizado somente quando a nacionalidade for a padrão **/
	@Column(name = "naturalidade")
	private String natural;
	
	/** Atribui a UF/estado do endereço do inscrito */
	@ManyToOne
	@JoinColumn(name = "id_unidade_federativa")
	private UnidadeFederativa unidadeFederativa;
	
	/** Atribui a cidade/município do endereço do inscrito */
	@ManyToOne
	@JoinColumn(name = "id_municipio")
	private Municipio municipio;
	
	/** Atribui a nome da instituição que o inscrito adquiriu o título. */
	private String universidade;
	
	/** Atribui o curso que o inscrito adquiriu o título. */
	private String curso;
	
	/** Atribui o curso que o inscrito adquiriu o título. */
	@Column(name = "ano_conclusao")
	private String anoConclusao;
	
	/** Atribui o nome do inscrito completo. */
	private String nome;
	
	/** Atribui o nome do pai do inscrito. */
	@Column(name="nome_pai")
	private String nomePai;
	
	/** Atribui o nome do mãe do inscrito. */
	@Column(name="nome_mae")
	private String nomeMae;
	
	/** Atribui o logradouro do inscrito. */
	private String logradouro;
	
	/** Atribui o número/bloco do logradouro do inscrito. */
	private String numero;
	
	/** Atribui o bairro onde se localiza o logradouro do inscrito. */
	private String bairro;
	
	/** Atribui o código de endereçamento postal onde se localiza o logradouro do inscrito. */
	private String cep;
	
	/** Atribui o e-mail de contato do inscrito. */
	private String email;
	
	/** Confirma se o e-mail está correto **/
	@Transient
	private String confirmaEmail;
	
	/** Atribui o telefone de contato do inscrito. */
	private String telefone;
	
	/** Atribui o prefixo do telefone do inscrito. */
	private String telefone_ddd;
	
	/** Atribui o fax de contato do inscrito. */
	private String fax;
	
	/** Atribui o prefixo do fax do inscrito. */
	private String fax_ddd;
	
	/** Atribui o CPF do inscrito, quando esse for brasileiro */
	private Long cpf;
	
	/** Atribui o número do passaporte do inscrito, quando esse for estrangeiro. */
	private String passaporte;
	
	/** Atribui o número de registro ou identidade do inscrito. */
	@Column(name="rg_numero")
	private String rgNumero;

	/** Atribui a instituição que emitiu a identidade do inscrito. */
	@Column(name="rg_orgao_expedidor")
	private String rgOrgaoExpedicao;

	/** Atribui a data de expedição da identidade do inscrito. */
	@Column(name="rg_data_expedicao")
	private Date rgDataExpedicao = new Date();
	
	@Column(name  = "logradouro_universidade")
	private String logradouroUniversidade;
	
	@Column(name  = "numero_universidade")
	private String numeroUniversidade;
	
	@ManyToOne
	@JoinColumn(name = "id_pais_universidade")
	private Pais paisUniversidade;
	
	@Column(name  = "cidade_universidade")
	private String cidadeUniversidade;
	
	@Column(name  = "pagina_universidade")
	private String paginaUniversidade;
	
	@Column(name  = "email_universidade")
	private String emailUniversidade;
	
	@Transient
	private String confirmaEmailUniversidade;

	@Column(name  = "telefone_universidade")
	private String telefoneUniversidade;
	
	@Column(name  = "ddi_telefone_universidade")
	private String ddiTelefoneUniversidade;
	
	@Column(name  = "ddd_telefone_universidade")
	private String dddTelefoneUniversidade;
		
	@Column(name  = "ddi_fax_universidade")
	private String ddiFaxUniversidade;
	
	@Column(name  = "ddd_fax_universidade")
	private String dddfaxUniversidade;
	
	@Column(name  = "fax_universidade")
	private String faxUniversidade;
	
	/** Atribui a data que o inscrito agendou para entrega dos documentos. */
	@Column(name = "data_agendado", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@CriadoEm
	private Date dataAgendado;
	
	/** Atribui o horário que o inscrito agendou para entrega dos documentos. */
	@Column(name = "horario_agendado", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@CriadoEm
	private Date horarioAgendado;	

	/** Atribui a data de cadastro da solicitação. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@CriadoEm
	private Date dataCadastro;

	/** Atribui a data em que a solicitação foi atualizada. */	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@AtualizadoEm
	private Date dataAtualizacao;
	
	/** GRU gerada para o pagamento da taxa de revalidação de diploma. */ 
	@Column(name = "id_gru")
	private Integer idGRU;
	
	public SolicitacaoRevalidacaoDiploma() {
		setPais(new Pais());
		setPaisUniversidade(new Pais());
		setUnidadeFederativa(new UnidadeFederativa());
		setMunicipio(new Municipio());
		setTipoLogradouro( new TipoLogradouro() );
		setAgendaRevalidacaoDiploma(new AgendaRevalidacaoDiploma());
	}	
	
	/**
	 * Retorna um hash code com os dados relevantes do inscrito
	 * @return
	 */
	public String getCodigoHash(){
		String cpfPassaporte = (cpf!=null)?String.valueOf(cpf):passaporte;
		return UFRNUtils.toSHA1Digest("PS"+ getEditalRevalidacaoDiploma().getId()+getId()+cpfPassaporte);
	}
		
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getNomePai() {
		return nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.passaporte = null;
		this.cpf = cpf;
	}
	
	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(String passaporte) {
		this.cpf = null;
		this.passaporte = passaporte;
	}

	public String getRgNumero() {
		return rgNumero;
	}

	public void setRgNumero(String rgNumero) {
		this.rgNumero = rgNumero;
	}

	public String getRgOrgaoExpedicao() {
		return rgOrgaoExpedicao;
	}

	public void setRgOrgaoExpedicao(String rgOrgaoExpedicao) {
		this.rgOrgaoExpedicao = rgOrgaoExpedicao;
	}

	public Date getRgDataExpedicao() {
		return rgDataExpedicao;
	}

	public void setRgDataExpedicao(Date rgDataExpedicao) {
		this.rgDataExpedicao = rgDataExpedicao;
	}
	
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public String getUniversidade() {
		return universidade;
	}

	public void setUniversidade(String universidade) {
		this.universidade = universidade;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public TipoLogradouro getTipoLogradouro() {
		return tipoLogradouro;
	}

	public void setTipoLogradouro(TipoLogradouro tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	public UnidadeFederativa getUnidadeFederativa() {
		return unidadeFederativa;
	}

	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}
	
	public Municipio getMunicipio() {
		return this.municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "nome", "cpf_cnpj");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/**
	 * @return the dataCadastro
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/**
	 * @param dataCadastro the dataCadastro to set
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
		
	public Date getDataAgendado() {
		return dataAgendado;
	}

	public void setDataAgendado(Date dataAgendado) {
		this.dataAgendado = dataAgendado;
	}

	public Date getHorarioAgendado() {
		return horarioAgendado;
	}

	public void setHorarioAgendado(Date horarioAgendado) {
		this.horarioAgendado = horarioAgendado;
	}
	
	public int getFila() {
		return fila;
	}

	public void setFila(int fila) {
		this.fila = fila;
	}

	public AgendaRevalidacaoDiploma getAgendaRevalidacaoDiploma() {
		return agendaRevalidacaoDiploma;
	}

	public void setAgendaRevalidacaoDiploma(
			AgendaRevalidacaoDiploma agendaRevalidacaoDiploma) {
		this.agendaRevalidacaoDiploma = agendaRevalidacaoDiploma;
	}
	
	public String getTelefone_ddd() {
		return telefone_ddd;
	}

	public void setTelefone_ddd(String telefone_ddd) {
		this.telefone_ddd = telefone_ddd;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		validateRequired(nome, "Nome", erros);
		validateRequired(email, "E-mail", erros);
		if(!isEmpty(getEmail()))
			if(!getEmail().equals(getConfirmaEmail()))
				erros.addErro("O campo Confirmar E-mail deve possuir o mesmo valor do campo E-mail.");
		
		validateRequired(getUniversidade(), "Universidade", erros);
		validateRequired(getCurso(), "Curso", erros);
		validateRequired(getPais(), "Nacionalidade", erros);
		
		if(!isEmpty(getNatural()))
			validateRequired(getNatural(), "Naturalidade", erros);
		
		validateRequired(getPais(), "Nacionalidade", erros);
		
		validateRequired(getLogradouro(), "Logradouro", erros);
		validateRequired(getNumero(), "Numero", erros);
		validateRequired(getMunicipio(), "Município", erros);
		validateRequired(getUnidadeFederativa(),"Unidade de Federação", erros);
		validateRequired(getBairro(), "Bairro", erros);
		validateRequired(getCep(), "CEP", erros);
		validateRequired(getTelefone_ddd(), "DDD do Telefone", erros);
		validateRequired(getTelefone(), "Telefone", erros);
		
		if(!isEmpty(getPais()) && getPais().getId() == NACIONALIDADE_PADRAO){
			
			String strCpf = Formatador.getInstance().formatarCPF(getCpf());
			ValidatorUtil.validateCPF_CNPJ(strCpf, "CPF", erros);
			validateRequired(getRgNumero(), "RG", erros);
			validateRequired(getRgOrgaoExpedicao(), "Orgão de Expedição", erros);
			validateRequired(getRgDataExpedicao(), "Data de Expedição", erros);
		}
		
		if(!isEmpty(getPais()) && getPais().getId() != NACIONALIDADE_PADRAO){
			validateRequired(getPassaporte(), "Passaporte", erros);
		}
		
		if(!isEmpty(getFax()))
			validateRequired(getFax_ddd(), "DDD do Fax", erros);
		
		validateRequired(getLogradouroUniversidade(), "Endereço da Instituição", erros);
		ValidatorUtil.validateMinValue(getAnoConclusao(), 4, "Ano de Conclusão", erros);
		validateRequired(getNumeroUniversidade(), "Número da Instituição", erros);
		
		validateRequired(getPaisUniversidade(), "País da Instituição", erros);
		validateRequired(getCidadeUniversidade(), "Cidade da Instituição", erros);
		validateRequired(getPaginaUniversidade(), "Página Eletrônica da Instituição", erros);
		validateRequired(getEmailUniversidade(), "E-mail da Instituição", erros);
		
		if(!isEmpty(getEmailUniversidade()))
			if(!getEmailUniversidade().equals(getConfirmaEmailUniversidade()))
				erros.addErro("O campo Confirmar E-mail Instituição deve possuir o mesmo valor do campo E-mail da Instituição.");
		
		validateRequired(getDdiTelefoneUniversidade(), "DDI do Telefone da Instituição", erros);
		validateRequired(getDddTelefoneUniversidade(), "DDD do Telefone da Instituição", erros);
		validateRequired(getTelefoneUniversidade(), "Telefone da Instituição", erros);
		
		if(!isEmpty(getFaxUniversidade())){
			validateRequired(getDdiFaxUniversidade(), "DDI do Fax da Instituição", erros);
			validateRequired(getDddfaxUniversidade(), "DDD do Fax da Instituição", erros);
		}
		
	
		
		return erros;
	}

	public Integer getNumProcesso() {
		return numProcesso;
	}

	public void setNumProcesso(Integer numProcesso) {
		this.numProcesso = numProcesso;
	}

	public Integer getAnoProcesso() {
		return anoProcesso;
	}

	public void setAnoProcesso(Integer anoProcesso) {
		this.anoProcesso = anoProcesso;
	}

	public EditalRevalidacaoDiploma getEditalRevalidacaoDiploma() {
		return editalRevalidacaoDiploma;
	}

	public void setEditalRevalidacaoDiploma(
			EditalRevalidacaoDiploma editalRevalidacaoDiploma) {
		this.editalRevalidacaoDiploma = editalRevalidacaoDiploma;
	}

	public String getNatural() {
		return natural;
	}

	public void setNatural(String natural) {
		this.natural = natural;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFax_ddd() {
		return fax_ddd;
	}

	public void setFax_ddd(String faxDdd) {
		fax_ddd = faxDdd;
	}

	public String getConfirmaEmail() {
		return confirmaEmail;
	}

	public void setConfirmaEmail(String confirmaEmail) {
		this.confirmaEmail = confirmaEmail;
	}

	public String getLogradouroUniversidade() {
		return logradouroUniversidade;
	}

	public void setLogradouroUniversidade(String logradouroUniversidade) {
		this.logradouroUniversidade = logradouroUniversidade;
	}

	public String getNumeroUniversidade() {
		return numeroUniversidade;
	}

	public void setNumeroUniversidade(String numeroUniversidade) {
		this.numeroUniversidade = numeroUniversidade;
	}

	public Pais getPaisUniversidade() {
		return paisUniversidade;
	}

	public void setPaisUniversidade(Pais paisUniversidade) {
		this.paisUniversidade = paisUniversidade;
	}

	public String getCidadeUniversidade() {
		return cidadeUniversidade;
	}

	public void setCidadeUniversidade(String cidadeUniversidade) {
		this.cidadeUniversidade = cidadeUniversidade;
	}

	public String getPaginaUniversidade() {
		return paginaUniversidade;
	}

	public void setPaginaUniversidade(String paginaUniversidade) {
		this.paginaUniversidade = paginaUniversidade;
	}

	public String getEmailUniversidade() {
		return emailUniversidade;
	}

	public void setEmailUniversidade(String emailUniversidade) {
		this.emailUniversidade = emailUniversidade;
	}

	public String getConfirmaEmailUniversidade() {
		return confirmaEmailUniversidade;
	}

	public void setConfirmaEmailUniversidade(String confirmaEmailUniversidade) {
		this.confirmaEmailUniversidade = confirmaEmailUniversidade;
	}

	public String getDddTelefoneUniversidade() {
		return dddTelefoneUniversidade;
	}

	public void setDddTelefoneUniversidade(String dddTelefoneUniversidade) {
		this.dddTelefoneUniversidade = dddTelefoneUniversidade;
	}

	public String getTelefoneUniversidade() {
		return telefoneUniversidade;
	}

	public void setTelefoneUniversidade(String telefoneUniversidade) {
		this.telefoneUniversidade = telefoneUniversidade;
	}

	public String getDddfaxUniversidade() {
		return dddfaxUniversidade;
	}

	public void setDddfaxUniversidade(String dddfaxUniversidade) {
		this.dddfaxUniversidade = dddfaxUniversidade;
	}

	public String getFaxUniversidade() {
		return faxUniversidade;
	}

	public void setFaxUniversidade(String faxUniversidade) {
		this.faxUniversidade = faxUniversidade;
	}

	public String getAnoConclusao() {
		return anoConclusao;
	}

	public void setAnoConclusao(String anoConclusao) {
		this.anoConclusao = anoConclusao;
	}

	public String getDdiTelefoneUniversidade() {
		return ddiTelefoneUniversidade;
	}

	public void setDdiTelefoneUniversidade(String ddiTelefoneUniversidade) {
		this.ddiTelefoneUniversidade = ddiTelefoneUniversidade;
	}

	public String getDdiFaxUniversidade() {
		return ddiFaxUniversidade;
	}

	public void setDdiFaxUniversidade(String ddiFaxUniversidade) {
		this.ddiFaxUniversidade = ddiFaxUniversidade;
	}

	public Integer getIdGru() {
		return idGRU;
	}

	public void setIdGru(Integer idGru) {
		this.idGRU = idGru;
	}

}