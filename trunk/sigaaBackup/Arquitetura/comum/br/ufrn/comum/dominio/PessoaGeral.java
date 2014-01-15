/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 15/09/2004
 */
package br.ufrn.comum.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;
import java.util.StringTokenizer;

import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Formacao;

/**
 * Classe de domínio das Pessoas dos sistemas.
 *
 * @author Gleydson Lima
 *
 */
public class PessoaGeral implements PersistDB, Cloneable {

	/** Texto a ser exibido no campo CPF quando a pessoa for extrangeira. */
	private static final String ESTRANGEIRO = "ESTRANGEIRO";
	
	/** Constante utilizada para Serialização */
	private static final long serialVersionUID = -2863325081475888817L;
	
	/**
	 * Constante para tipo de pessoa Pessoa Física
	 * @see #tipo
	 */
	public static final char PESSOA_FISICA = 'F';

	/**
	 * Constante para tipo de pessoa Pessoa Jurídica
	 * @see #tipo
	 */
	public static final char PESSOA_JURIDICA = 'J';
	
	/**
	 * Constante que indica que a pessoa é do tipo Unidade Gestora
	 */
	public static final char UNIDADE_GESTORA = 'G';
	
	/**
	 * Constante para atribuição do sexo feminino
	 */
	public static final char SEXO_FEMININO = 'F';
	
	/**
	 * Constante para atribuição do sexo masculino
	 */
	public static final char SEXO_MASCULINO = 'M';
	
	
	/** Identificador */
	protected int id;

	/** CPF ou CNPJ da pessoa */
	protected Long cpf_cnpj;

	/** Nome da pessoa ou razão social em caso de empresa */
	protected String nome;
	
	/** Nome da pessoa ou razão social em caso de empresa em ASCII, para auxiliar consultas */
	protected String nomeAscii;

	/** Em caso de pessoa jurídica, nome fantasia da empresa */
	protected String nomeFantasia;

	/** Em caso de pessoa jurídica, nome do representante da empresa */
	protected String nomeRepresentante;

	/** Endereço da pessoa/empresa */
	protected String endereco;

	/** Bairro da pessoa/empresa */
	protected String bairro;

	/** CEP da pessoa/empresa */
	protected String CEP;

	/** Cidade da pessoa/empresa */
	protected String cidade;

	/** Unidade Federativa da pessoa/empresa */
	protected String UF;

	/** Código da área nacional de telefone fixo da pessoa */
	protected Short codigoAreaNacionalTelefoneFixo;

	/** Código da área nacional de telefone celular da pessoa */
	protected Short codigoAreaNacionalTelefoneCelular;

	/** Telefone da pessoa/empresa */
	protected String telefone;

	/** FAX da pessoa */
	protected String fax;

	/** Celular da pessoa */
	protected String celular;

	/** NIT/PIS da pessoa física */
	protected Long nitPIS;

	/** Tipo de pessoa. F - Física; J - Jurídica. */
	protected char tipo;

	/** Data de nascimento da pessoa física */
	protected Date dataNascimento;

	/** Sexo da pessoa. M - Masculino; F - Feminino. */
	protected char sexo;

	/** E-Mail da pessoa/empresa */
	protected String email;
	
	/** Identidade: Registro Geral */
	protected String registroGeral;
	
	/** Órgão expedidor do RG (identidade) da pessoa*/
	protected String rgOrgaoExpedidor;
	
	/** Data da expedição do RG */
	protected Date rgDataExpedicao;
	
	/** Complemento do endereço residencial da pessoa */
	protected String enderecoComplemento;
	
	/** Número da casa/apto associado ao logradouro */
	protected String enderecoNumero;

	/** Em caso de pessoa física, nome da mãe da pessoa. */
	protected String nomeMae;

	/** Em caso de pessoa física, nome do pai da pessoa. */
	protected String nomePai;

	/** Informa se a pessoa tem CPF ou CNPJ válido.. usado para restringir a busca de pessoas migradas erradas */
	protected boolean valido;

	/**
	 * Para pessoa jurídica. Atributo utilizado pelo subsistema de faturas para indicar quando um
	 * fornecedor pode ou não emitir uma fatura.
	 */
	protected boolean emiteFatura = false;
	
	/**
	 * Para pessoa jurídica. Alíquota de imposto a ser descontado no valor bruto das faturas.
	 */
	protected Double aliquotaImposto = null;

	/**
	 * Atributo utilizado pelo subsistema de convênios para indicar quando um
	 * fornecedor é internacional e não possui cnpj.
	 */
	protected boolean internacional = false;
	

	/** Número do passaporte, para o caso de estrangeiros */
	protected String passaporte;

	/** Pais de origem de uma pessoa estrangeira* */
	protected String paisOrigem;

	/** Unidade em que está lotado * */
	protected UnidadeGeral lotacao;

	private Integer idPerfil;
	
	private Formacao formacao;
	
	/*
	 * Atributos utilizados no cadastro de algumas requisições,
	 * onde se exige dados de um proposto.
	 */
	
	/** Informação utilizada Matricula do proposto * */
	protected String matricula;

	/** Se a pessoa é funcionária da universidade */
	protected Boolean funcionario;

	
	/** Construtores */
	
	public PessoaGeral() {
		sexo = 'N';
		tipo = PESSOA_FISICA;
		lotacao = new UnidadeGeral();
	}

	public PessoaGeral(int id) {
		this.id = id;
	}

	
	@Override
	public Object clone() throws CloneNotSupportedException {
		PessoaGeral f = (PessoaGeral) super.clone();
		return f;
	}

	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof PessoaGeral) {
			PessoaGeral fornecedor = (PessoaGeral) other;
			if (id == fornecedor.getId()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, cpf_cnpj);
		return result;
	}
	
	
	/** Método para recuperar o Bairro */
	public String getBairro() {
		return bairro;
	}

	/** Método para setar o Bairro */
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	/** Método para recuperar o celular */
	public String getCelular() {
		return celular;
	}

	/** Método para setar o celular */
	public void setCelular(String celular) {
		this.celular = celular;
	}

	/** Método para recuperar o CEP */
	public String getCEP() {
		return CEP;
	}

	/** Método para setar o CEP */
	public void setCEP(String cep) {
		CEP = cep;
	}

	/** Método para recuperar a Cidade */
	public String getCidade() {
		return cidade;
	}

	/** Método para setar Cidade */
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	/** Método para recuperar cpf/cnpj */
	public Long getCpf_cnpj() {
		return cpf_cnpj;
	}

	/** Método para setar cpf/cnpj */
	public void setCpf_cnpj(Long cpf_cnpj) {
		this.cpf_cnpj = cpf_cnpj;
	}

	/** Método para setar cpf/cnpj como uma string */
	public void setCpf_cnpjString(String cpf_cnpj) {
		if (cpf_cnpj == null || cpf_cnpj.trim().isEmpty()) {
			this.cpf_cnpj = null;
		} else  if (cpf_cnpj.equalsIgnoreCase(ESTRANGEIRO)) {
			this.cpf_cnpj = null;
		} else {
			try {
				this.cpf_cnpj = Long.parseLong(cpf_cnpj.replaceAll("\\.", "").replaceAll("-", "").replaceAll("/", ""));
			} catch(NumberFormatException e) {
				this.cpf_cnpj = null;
			}
		}
	}

	/** Método para recuperar cpf/cnpj como string */
	public String getCpf_cnpjString() {
		String cpfCnpj = "";
		if(isInternacional())
			cpfCnpj = ESTRANGEIRO;
		else
			cpfCnpj = getCpfCnpjFormatado();
		
		return cpfCnpj;
	}

	/** Método para recuperar o endereço*/
	public String getEndereco() {
		return endereco;
	}

	/** Método para setar o endereço*/
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	/** Método para recuperar o fax*/
	public String getFax() {
		return fax;
	}

	/** Método para setar o fax*/
	public void setFax(String fax) {
		this.fax = fax;
	}

	/** Método para recuperar o id*/
	public int getId() {
		return id;
	}

	/** Método para setar o id*/
	public void setId(int id) {
		this.id = id;
	}
	
	/** Método para recuperar o nome representante*/
	public String getNomeRepresentante() {
		return nomeRepresentante;
	}

	/** Método para setar o nome representante*/
	public void setNomeRepresentante(String nomeRepresentante) {
		this.nomeRepresentante = nomeRepresentante;
	}

	/** Método de acesso do nome */
	public String getNome() {
		if(!isEmpty(nome)){
			return nome.trim();
		}
		return nome;
	}
	
	/** Método de acesso do nome e cpf/cnpj */
	public String getNomeCpfCnpj() {
		String cpfCnpj;
		if(getTipo() == 'G')
			cpfCnpj = getCpf_cnpjString();
		else if(isInternacional())
			cpfCnpj = ESTRANGEIRO;
		else
			cpfCnpj = getCpfCnpjFormatado();
		return getNome() + "(" + cpfCnpj + ")";
	}

	/** Método para setar o nome */
	public void setNome(String nome) {
		this.nome = nome;
		setNomeAscii(nome);
	}

	/** Método para recuperar o telefone*/
	public String getTelefone() {
		return telefone;
	}

	/** Método para setar o telefone*/
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	/** Método para recuperar o tipo da pessoa*/
	public char getTipo() {
		return tipo;
	}

	/** Método para setar o tipo da pessoa*/
	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	/** Método para recuperar a UF*/
	public String getUF() {
		return UF;
	}

	/** Método para setar a UF*/
	public void setUF(String uf) {
		UF = uf;
	}

	/** Método para recuperar o NitPIS */
	public Long getNitPIS() {
		return nitPIS;
	}

	/** Método para setar o NitPIS */
	public void setNitPIS(Long nitPIS) {
		this.nitPIS = nitPIS;
	}

	/**
	 * Informa a String do NIT ou o número inscrição do PIS/PASEP de um trabalhador
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não chamado por JSP(s).</li>
	 * </ul>
	 * @param nitpisString
	 */
	public void setNitPISString(String nitpisString) {
		
		if (nitpisString == null || nitpisString.trim().isEmpty()) {
			this.nitPIS = null;
		}else{
			try {
				this.nitPIS = Long.parseLong(nitpisString.replaceAll("[\\D]+", ""));
			} catch(NumberFormatException e) {
				this.nitPIS = null;
			}
		}
	}
	
	/**
	 * Obtém a String do NIT ou o número inscrição do PIS/PASEP de um trabalhador.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não chamado por JSP(s).</li>
	 * </ul>
	 * @return
	 */
	public String getNitPISString() {
		
		String nitpisString = "";
		
		if(this.nitPIS != null)
			nitpisString = Formatador.getInstance().formatarNITPISPASEP(this.nitPIS);
		
		return nitpisString;
	}
	
	/**
	 * Método para recuperar o NIT ou o número inscrição do PIS/PASEP de um trabalhador formatado. 
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não chamado por JSP(s).</li>
	 * </ul>
	 * @return
	 */
	public String getNitPISFormatado() {
		
		if(this.nitPIS == null)
			this.nitPIS = new Long(0);
		
		return Formatador.getInstance().formatarNITPISPASEP(this.nitPIS);
	}
	
	/** Método para recuperar o matrícula */
	public String getMatricula() {
		return matricula;
	}

	/** Método para setar o matrícula */
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	/** Método para recuperar o nome fantasia */
	public String getNomeFantasia() {
		return nomeFantasia;
	}

	/** Método para setar o nome fantasia */
	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	/** Método para recuperar a data de nascimento */
	public Date getDataNascimento() {
		return dataNascimento;
	}

	/** Método para setar a data de nascimento */
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	/** Método para recuperar o sexo */
	public char getSexo() {
		return sexo;
	}

	/** Método para setar o sexo */
	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	/** Método para recuperar o email */
	public String getEmail() {
		return email;
	}

	/** Método para setar o email */
	public void setEmail(String email) {
		this.email = email;
	}

	/** Método para recuperar a razão social */
	public String getRazaoSocial() {
		return nome;
	}

	/** Método para verificar se a pessoa é um funcionário */
	public Boolean isFuncionario() {
		return funcionario;
	}

	/** Método para setar  a pessoa como um funcionário */
	public void setFuncionario(Boolean funcionario) {
		this.funcionario = funcionario;
	}

	/** Método para recuperar o cpf/cnpj formatado */
	public String getCpfCnpjFormatado() {
		if (getCpf_cnpj() == null){
			cpf_cnpj = new Long(0);
		}
		
		if (tipo == 'J')
			return Formatador.getInstance().formatarCNPJ(getCpf_cnpj());
		else if(tipo == 'F')
			return Formatador.getInstance().formatarCPF_CNPJ(getCpf_cnpj());
		else
			return String.valueOf(getCpf_cnpj());
	}

	/** Método para verificar se a pessoa pode emitir uma fatura*/
	public boolean isEmiteFatura() {
		return emiteFatura;
	}

	/** Método para setar a pessoa para emitir uma fatura*/
	public void setEmiteFatura(boolean emiteFatura) {
		this.emiteFatura = emiteFatura;
	}

	/** Método para verificar se a pessoa é estrangeira*/
	public boolean isInternacional() {
		return internacional;
	}

	/** Método para setar a pessoa como estrangeira*/
	public void setInternacional(boolean internacional) {
		this.internacional = internacional;
	}

	/** Método para recuperar a aliquota de imposto*/
	public Double getAliquotaImposto() {
		return aliquotaImposto;
	}

	/** Método para recuperar a aliquota de imposto*/
	public void setAliquotaImposto(Double aliquotaImposto) {
		this.aliquotaImposto = aliquotaImposto;
	}

	/**
	 * Retorna representação XML do Pessoa. Usado em consultas AJAX.
	 *
	 * @return representação XML da Pessoa.
	 */
	public String toXML() {
		String cpf_cnpjFormatado = Formatador.getInstance().formatarCPF_CNPJ(
				getCpf_cnpj() == null ? 0 : getCpf_cnpj());
		String dataFmt = null;
		if (dataNascimento != null)
			dataFmt = Formatador.getInstance().formatarData(dataNascimento);

		StringBuilder s = new StringBuilder(500);
		s.append("<PESSOA>\n");
		s.append("<ID>" + id + "</ID>\n");
		s.append("<NOME>" + (nome != null ? nome.trim() : null) + "</NOME>\n");
		s.append("<CPF_CNPJ>" + cpf_cnpjFormatado + "</CPF_CNPJ>\n");
		s.append("<TIPO>" + (tipo == '\0' ? ' ' : tipo) + "</TIPO>\n");
		s.append("<SEXO>" + ((sexo == '\0') ? 'M' : sexo) + "</SEXO>\n");
		s.append("<ENDERECO>"
				+ ((endereco == null || endereco.length() == 0) ? null
						: endereco.trim()) + "</ENDERECO>\n");
		s.append("<BAIRRO>" + (bairro != null ? bairro.trim() : null)
				+ "</BAIRRO>\n");
		s.append("<CEP>" + (CEP != null ? CEP.trim() : null) + "</CEP>\n");
		s.append("<CIDADE>"
				+ (cidade != null && cidade.length() > 0 ? cidade.trim()
						: cidade) + "</CIDADE>\n");
		s.append("<UF>" + (UF != null && UF.length() > 0 ? UF.trim() : null)
				+ "</UF>\n");
		s.append("<TELEFONE>"
				+ ((telefone != null && telefone.length() > 0) ? telefone
						.trim() : null) + "</TELEFONE>\n");
		s.append("<CELULAR>"
				+ ((celular != null && celular.length() > 0) ? celular.trim()
						: null) + "</CELULAR>\n");
		s.append("<FAX>"
				+ ((fax != null && fax.length() > 0) ? fax.trim() : null)
				+ "</FAX>\n");
		s.append("<EMAIL>"
				+ ((email != null && email.length() > 0) ? email.trim() : null)
				+ "</EMAIL>\n");
		s.append("<DATA_NASCIMENTO>" + dataFmt + "</DATA_NASCIMENTO>\n");
		s.append("</PESSOA>");

		return s.toString();
	}

	/** Método para recuperar o passaporte*/
	public String getPassaporte() {
		return passaporte;
	}

	/** Método para setar o passaporte*/
	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	/** Método para recuperar o pais de origem*/
	public String getPaisOrigem() {
		return paisOrigem;
	}

	/** Método para setar o pais de origem*/
	public void setPaisOrigem(String paisOrigem) {
		this.paisOrigem = paisOrigem;
	}

	/** Método que retorna a unidade em que a pessoa está locada*/
	public UnidadeGeral getLotacao() {
		return lotacao;
	}

	/** Método que setar uma unidade a pessoa */
	public void setLotacao(UnidadeGeral lotacao) {
		this.lotacao = lotacao;
	}

	/** Método que retorna se um cpf/cnpj está válido*/
	public boolean isValido() {
		return valido;
	}

	/** Método para setar que a pessoa tem um cpf/cnpj está válido*/
	public void setValido(boolean valido) {
		this.valido = valido;
	}

	/**Método para recuperar o código de área do telefone fixo*/
	public Short getCodigoAreaNacionalTelefoneFixo() {
		return codigoAreaNacionalTelefoneFixo;
	}

	/**Método para setar o código de área do telefone fixo*/
	public void setCodigoAreaNacionalTelefoneFixo(
			Short codigoAreaNacionalTelefoneFixo) {
		this.codigoAreaNacionalTelefoneFixo = codigoAreaNacionalTelefoneFixo;
	}

	/**Método para recuperar o código de área do telefone celular*/
	public Short getCodigoAreaNacionalTelefoneCelular() {
		return codigoAreaNacionalTelefoneCelular;
	}

	/**Método para setar o código de área do telefone celular*/
	public void setCodigoAreaNacionalTelefoneCelular(
			Short codigoAreaNacionalTelefoneCelular) {
		this.codigoAreaNacionalTelefoneCelular = codigoAreaNacionalTelefoneCelular;
	}

	/**Método para recuperar o idPerfil*/
	public Integer getIdPerfil() {
		return idPerfil;
	}

	/**Método para setar o idPerfil*/
	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}
	
	/**Método para recuperar o NomeAscii*/
	public String getNomeAscii() {
		return nomeAscii;
	}

	/**Método para setar o NomeAscii*/
	public void setNomeAscii(String nomeAscii) {
		if (nomeAscii != null)
			this.nomeAscii = StringUtils.toAscii(nomeAscii);
	}
	
	/**Método para recuperar o registro geral*/
	public String getRegistroGeral() {
		return registroGeral;
	}

	/**Método para setar o registro geral*/
	public void setRegistroGeral(String registroGeral) {
		this.registroGeral = registroGeral;
	}

	/**Método para recuperar o endereço completo*/
	public String getEnderecoComplemento() {
		return enderecoComplemento;
	}

	/**Método para setar o endereço completo*/
	public void setEnderecoComplemento(String enderecoComplemento) {
		this.enderecoComplemento = enderecoComplemento;
	}
	
	/**Método para recuperar o endereço/número */
	public String getEnderecoNumero() {
		return enderecoNumero;
	}
	
	/**Método para setar o endereço/número*/
	public void setEnderecoNumero(String enderecoNumero) {
		this.enderecoNumero = enderecoNumero;
	}

	/**Método para recuperar o nome da mãe*/
	public String getNomeMae() {
		return nomeMae;
	}

	/**Método para setar o nome da mãe*/
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	
	/**Método para recuperar o nome do pai*/
	public String getNomePai() {
		return nomePai;
	}
	
	/**Método para setar o nome do pai*/
	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	/**Método para recuperar o orgão expedidor do resgistro nacional da pessoa*/
	public String getRgOrgaoExpedidor() {
		return rgOrgaoExpedidor;
	}

	/**Método para setar o orgão expedidor do resgistro nacional da pessoa*/
	public void setRgOrgaoExpedidor(String rgOrgaoExpedidor) {
		this.rgOrgaoExpedidor = rgOrgaoExpedidor;
	}

	/**Método para recuperar a data de expedição do RG*/
	public Date getRgDataExpedicao() {
		return rgDataExpedicao;
	}

	/**Método para setar a data de expedição do RG*/
	public void setRgDataExpedicao(Date rgDataExpedicao) {
		this.rgDataExpedicao = rgDataExpedicao;
	}

	/**Método para verificar se a pessoa é do sexo feminino*/
	public boolean isSexoFeminino() {
		return (!ValidatorUtil.isEmpty(this.sexo) && this.sexo == SEXO_FEMININO);
	}

	/**Método para verificar se a pessoa é do sexo masculino*/
	public boolean isSexoMasculino() {
		return (!ValidatorUtil.isEmpty(this.sexo) && this.sexo == SEXO_MASCULINO);
	}
	
	/**Método para recuperar a formação da pessoa*/
	public Formacao getFormacao() {
		return formacao;
	}

	/**Método para setar a formação da pessoa*/
	public void setFormacao(Formacao formacao) {
		this.formacao = formacao;
	}

	/**
	 * Retorna a idade da pessoa na data atual.
	 * @return
	 */
	@Transient
	public int getIdade() {
		return CalendarUtils.calculoAnos(dataNascimento, new Date());
	}
	
	/**
	 * Retorna o nome abreviado da pessoa.
	 * @return
	 */
	@Transient
	public String getNomeAbreviado() {
		if (nome != null) {
			StringTokenizer nomeTok = new StringTokenizer(nome.trim());
			int count = nomeTok.countTokens();
			int count1 = nomeTok.countTokens() - 1;
			String nomeAbreviado = nomeTok.nextToken() + (nomeTok.hasMoreTokens() ? " " + nomeTok.nextToken() : "");
			for (int i = 2; i < count; i++){
				String aux = nomeTok.nextToken();
				if(aux.equals("DE") || aux.equals("DA") || aux.equals("DO") )
					nomeAbreviado += " " + aux;
				else
					if(i != count1 ){
						nomeAbreviado += " " + aux.charAt(0);
						nomeAbreviado += ".";
					}
					else
						nomeAbreviado += " " + aux;
			}
			return nomeAbreviado;
		}else
			return "";
	}
	
	/**
	 * Retorna os primeiros nomes da pessoa.
	 * @return
	 */
	@Transient
	public String getPrimeirosNomes() {
		if (getNome() == null) return "";
		String[] nomes = getNome().trim().split(" ");
		return String.format("%s %s %s", nomes[0], nomes[1], nomes[1].matches("(DE)|(DO)|(DA)") ? nomes[2] : "");
	}
}
