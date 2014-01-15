/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 01/08/2008
 */

package br.ufrn.sigaa.biblioteca.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ServicosEmprestimosBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ServicosInformacaoReferenciaBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Endereco;

/**
 *   <p>Representa uma Biblioteca do sistema.</p>
 * 
 * 	 <p> <strong> PASSOS PARA CRIAÇÃO DE UMA NOVA BIBLIOTECA NO SISTEMA </strong> </p>
 * 
 *   <p> <strong>Criar a biblioteca propriamente dita: </strong> 
 *   	<pre>
 *       	insert into biblioteca.biblioteca (id_biblioteca, identificador, descricao, id_unidade, funciona_sabado, funciona_domingo, servico_emprestimos_ativos, acervo_publico, codigo_identificador_biblioteca, numero_gerador_codigo_assinatura, data_cadastro, ativo  )
 *          values (nextval('biblioteca.hibernate_sequence'), 'BS...', 'Biblioteca Setorial ....', xxx, false, false, true, true, 22, 0, '20xx-xx-xx xx:xx:xx.000',  true)
 *          returning id_biblioteca;
 *   	</pre>
 *   </p>
 * 
 *   <p><strong>Criar um usuário biblioteca para ele poder fazer empréstimos institucionais: </strong>
 *   	<pre>
 *        insert into biblioteca.usuario_biblioteca (id_usuario_biblioteca, id_biblioteca, ativo, id_registro_cadastro, data_cadastro, vinculo, identificacao_vinculo)
 *        values (nextval('biblioteca.usuario_biblioteca_sequence'), ?, true, 1, '????-??-?? 00:00:00.000', 5, xxxxx)
 *        </pre>
 *	 </p>
 *
 * 
 *   <p><strong>Criar os serviços de empréstimos para essa biblioteca (6 até o momento): </strong>  
 *   
 *   <pre>
 *  	insert into biblioteca.servicos_emprestimos_biblioteca (id_servicos_emprestimos_biblioteca, id_biblioteca
 *  	, emprestimo_institucional_interno, emprestimo_institucional_externo, empresta_alunos_graduacao_mesmo_centro, empresta_alunos_pos_mesmo_centro
 *     	, empresta_alunos_graduacao_outro_centro, empresta_alunos_pos_outro_centro, ativo)
 *     	values (nextval('biblioteca.resgistro_extras_sequence'), ?, true, false, false, false, false, false, false);
 *   
 *  	insert into biblioteca.servicos_emprestimos_biblioteca (id_servicos_emprestimos_biblioteca, id_biblioteca
 *  	, emprestimo_institucional_interno, emprestimo_institucional_externo, empresta_alunos_graduacao_mesmo_centro, empresta_alunos_pos_mesmo_centro
 *     	, empresta_alunos_graduacao_outro_centro, empresta_alunos_pos_outro_centro, ativo)
 *     	values (nextval('biblioteca.resgistro_extras_sequence'), ?, false, true, false, false, false, false, false);
 *
 *     	insert into biblioteca.servicos_emprestimos_biblioteca (id_servicos_emprestimos_biblioteca, id_biblioteca
 *  	, emprestimo_institucional_interno, emprestimo_institucional_externo, empresta_alunos_graduacao_mesmo_centro, empresta_alunos_pos_mesmo_centro
 *     	, empresta_alunos_graduacao_outro_centro, empresta_alunos_pos_outro_centro, ativo)
 *     	values (nextval('biblioteca.resgistro_extras_sequence'), ?, false, false, true, false, fals,e false, false);
 *		
 *     	insert into biblioteca.servicos_emprestimos_biblioteca (id_servicos_emprestimos_biblioteca, id_biblioteca
 *     	, emprestimo_institucional_interno, emprestimo_institucional_externo, empresta_alunos_graduacao_mesmo_centro, empresta_alunos_pos_mesmo_centro
 *     	, empresta_alunos_graduacao_outro_centro, empresta_alunos_pos_outro_centro, ativo)
 *     	values (nextval('biblioteca.resgistro_extras_sequence'), ?, false, false, false, true, false, false, false);
 *     	
 *     	insert into biblioteca.servicos_emprestimos_biblioteca (id_servicos_emprestimos_biblioteca, id_biblioteca
 *     	, emprestimo_institucional_interno, emprestimo_institucional_externo, empresta_alunos_graduacao_mesmo_centro, empresta_alunos_pos_mesmo_centro
 *     	, empresta_alunos_graduacao_outro_centro, empresta_alunos_pos_outro_centro, ativo)
 *     	values (nextval('biblioteca.resgistro_extras_sequence'), ?, false, false, false, false, true, false, false);
 *     	
 *     	insert into biblioteca.servicos_emprestimos_biblioteca (id_servicos_emprestimos_biblioteca, id_biblioteca
 *     	, emprestimo_institucional_interno, emprestimo_institucional_externo, empresta_alunos_graduacao_mesmo_centro, empresta_alunos_pos_mesmo_centro
 *     	, empresta_alunos_graduacao_outro_centro, empresta_alunos_pos_outro_centro, ativo)
 *     	values (nextval('biblioteca.resgistro_extras_sequence'), ?, false, false, false, false, false, true, false);
 *
 *   </pre>
 *   </p>
 * 
 * @author Fred_Castro
 * @author jadson
 * @version 1.5 separação dos serviços de empréstimos da entidade biblioteca. Como isso só é utilizando um vez no sistema, é um peso extra conter essa informação. 
 * 
 */
@Entity
@Table(name = "biblioteca", schema = "biblioteca")
public class Biblioteca implements Validatable {
	
	
	/** O id do objeto no banco. Não confundir com o identificador da biblioteca. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_biblioteca")
	private int id;

	/**
	 * Identificador da biblioteca. Utilizado nas fichas catalográficas.
	 * ex: da biblioteca do CCET: RN/UF/BSE-CCET
	 */
	private String identificador;
	
	
	/** Nome completo da biblioteca. */
	@Column(nullable=false)
	private String descricao;

	
	/** <p>A unidade do sistema que a biblioteca está associada.</p>
	 *  <p><strong>Observação 1: </strong>SE FOR BIBLIOTECA EXTERNA NÃO VAI POSSUIR UNIDADE.</p>
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade")
	private Unidade unidade = new Unidade();
	
	
	/** O endereço para bibliotecas externas. */
	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_endereco")
	private Endereco endereco = new Endereco();
	
	/** O telefone de contato da biblioteca. */
	private String telefone;

	/** O nome do responsável pela biblioteca. */
	@Column (name="nome_responsavel")
	private String responsavel;
	
	/** Endereço eletrônico da biblioteca. */
	private String email;
	
	/** Se a biblioteca trabalha aos Sábados */
	@Column(name="funciona_sabado")
	private boolean funcionaSabado = false;
	
	/** Se a biblioteca trabalha aos domingos */
	@Column(name="funciona_domingo")
	private boolean funcionaDomingo = false;
	
	/** 
	 * <p>Diz se a biblioteca está com o serviço de empréstimos e renovações ativo. Por algum motivo 
	 * a biblioteca pode querer fechar por um tempo e impedir a realização de novos empréstimos.</p>
	 * 
	 * <p>Também usado para bloquear os empréstimos e renovações durante o cadastro de interruções.
	 * A interrupção vai parar a circulação da biblioteca e ao finalizar vai liberar novamente.</p>
	 * 
	 */
	@Column(name="servico_emprestimos_ativos", nullable=false)
	private boolean servicoEmprestimosAtivos;
	
	
	/**
	 *  Configura se o acervo da biblioteca deve ser visualizado publicamente.
	 */
	@Column(name="acervo_publico", nullable=false)
	private boolean acervoPublico = true;
	
	
	/**
	 * Número sequencial que vai gerar parte do código das assinaturas dessa biblioteca.
	 * O restante do código é gerado pelo ano + o número que identifique a biblioteca
	 */
	@Column(name = "numero_gerador_codigo_assinatura", nullable=false)
	protected int numeroGeradorCodigoAssinatura;
	
	
	/** Atributo utilizado para gerar o código das assinaturas de periódicos da biblioteca. Somente bibliotecas internas possuem.*/
	@Column(name = "codigo_identificador_biblioteca")
	protected String codigoIdentificadorBiblioteca;
	

	/** Se a biblioteca está ativa no sistema ou não */
	private boolean ativo = true;

	
	
	
	////////////////////////////// auditoria  //////////////////////////////
	
	/**
	 * Registro entrada do usuário que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * Data de cadastro
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/**
	 * Registro de entrada do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * Data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	/////////////////////////////////////////////////////////////////
	
	
	/** Transiente que indica se a biblioteca foi selecionada ou não. */
	@Transient
	private boolean selecionada;
	
	
	/** 
	 *  <p>Transiente que guarda de forma temporária os serviços de empréstimos prestados pela biblioteca internas.</p>
	 * 
	 *  <p>Unidirecional, a biblioteca só precisa saber os serviços 
	 *  de empréstimos que ele presta no momento de realizar empréstimos. Então não precisa carregar as informações desses 
	 *  serviços para todas as partes do sistema.</p>
	 */
	@Transient
	private List<ServicosEmprestimosBiblioteca> servicosEmprestimos;
	
	
	
	/** Transiente que guarda de forma temporária os serviços referentes aos usuários prestados pela biblioteca internas. 
	 * 
	 *  <p>Unidirecional, a biblioteca só precisa saber os serviços 
	 *  ao usuário que ela presta na seção de informação e referência. Então não precisa carregar as informações desses 
	 *  serviços para todas as partes do sistema.</p> 
	 */
	@Transient
	private ServicosInformacaoReferenciaBiblioteca servicos;
	
	
	
	/**
	 * Construtor padrão
	 */
	public Biblioteca() {
		
	}

	/**
	 * Construtor de uma biblioteca
	 * @param id
	 */
	public Biblioteca(int id) {
		this.id = id;
	}

	public Biblioteca(String descricao) {
		this.descricao = descricao;
	}
	
	public Biblioteca(int id, String descricao) {
		this(id);
		this.descricao = descricao;
	}
	
	public Biblioteca(String identificador, String descricao) {
		this(descricao);
		this.identificador = identificador;
	}
	
	public Biblioteca(int id, Unidade unidade) {
		this(id);
		this.unidade = unidade;
	}
	
	
	public Biblioteca(int id, String identificador, String descricao, Unidade unidade) {
		this(id, unidade);
		this.identificador = identificador;
		this.descricao = descricao;
	}
	
	
	/** Apaga dados que foram criados mas o usuário não preencheu para a biblioteca, utilizado no cadastro. */
	public void apagaDadosTransientes(){
	
		if (this.getUnidade() != null && this.getUnidade().getId() <= 0)
			this.setUnidade(null);
		
		if (this.getEndereco() != null && this.getEndereco().getId() <= 0  )
			this.setEndereco(null);
		
		if (this.getEndereco() != null && this.getEndereco().getMunicipio() != null && this.getEndereco().getMunicipio().getId() <= 0)
			this.getEndereco().setMunicipio(null);
	}
	
	
	///////////////////////////////////////////////////////////////
	
	/** Incrementa o número gerador dos códigos das assinaturas. */
	public void incrementaCodigoGeradorBiblioteca(){
		numeroGeradorCodigoAssinatura ++;
	}
	
	/**
	 * Validar se as variáveis estão preenchidas corretamente.
	 */
	@Override
	public ListaMensagens validate() {

		ListaMensagens mensagens = new ListaMensagens();

		if(StringUtils.isEmpty(descricao))
			mensagens.addErro("É preciso informar o nome da Biblioteca");

		if(StringUtils.isEmpty(identificador))
			mensagens.addErro("É preciso informar o identificador da Biblioteca");

		return mensagens;
	}

	/**
	 * Indica se a biblioteca é a biblioteca central do sistema ou não.
	 */
	public boolean isBibliotecaCentral(){
		if ( this.id == BibliotecaUtil.getIdBibliotecaCentral() )
			return true;
		else
			return false;
	}
	
	/** Indica se a biblioteca não é uma biblioteca da instituição. */
	public boolean isBibliotecaExterna(){
		if(this.getUnidade() == null || this.getUnidade().getId() <= 0 )
			return true;
		else
			return false;
	}
	
	
	public String getDescricao() {
		return descricao;
	}
	
	
	/**
	 * Retorna a identificação resumida da biblioteca. A indicação "BC" ou "BS" mais os últimos 2 nomes.
	 */
	public String getDescricaoResumida() {
	
		String descricaoResumida = ""; // a descriação resumida retornada  pelo método
		String descricaoTemp = "";
		
		final String separador = " - ";
		
		String textoResumoSetorial = "B.S. ";
		String textoResumoCentral = "B.C. ";
		
		boolean resumiuCentral = false;
		boolean resumiuSetorial = false;
		
		if(descricao != null){
			
			descricaoTemp = descricao.toUpperCase().trim();
			
			/*
			 * Retira a descrição padrão das bibliotecas para substituir pelas iniciais
			 */
			if( descricaoTemp.trim().startsWith("BIBLIOTECA SETORIAL")){
				descricaoTemp = descricaoTemp.replace("BIBLIOTECA SETORIAL", "");
				resumiuSetorial = true;
			}
			
			if( descricaoTemp.trim().startsWith("BIBLIOTECA CENTRAL")){
				descricaoTemp = descricaoTemp.replace("BIBLIOTECA CENTRAL", "");
				resumiuCentral = true;
			}
			
			/*
			 * Retorna as 2 últimas palavras da descriação da biblioteca.
			 * Por exemplo: "Biblioteca Central Zila Mamede", retorna  : "Zila Mamede"
			 */
			
			String[] palavras = descricaoTemp.split(" ");
			
			int qtdPalavras = 2;
			
			for (int index = palavras.length-1; index >= 0 && qtdPalavras > 0 ; index--) {
				String temp = palavras[index];
				if(temp.length() > 2)
					qtdPalavras--;
				
				descricaoResumida = temp+" "+descricaoResumida;
			}
			
			
			
			if( resumiuCentral )
				descricaoResumida = textoResumoCentral+descricaoResumida;
			if( resumiuSetorial )
				descricaoResumida = textoResumoSetorial+descricaoResumida;
			
		}
			
		return (identificador != null ? (identificador +separador) : "") + descricaoResumida;
	}
	
	
	/**
	 * Retorna o identificador mais a descrição da biblioteca.
	 */
	public String getDescricaoCompleta() {
		return (identificador != null ? identificador +" - ": "") + descricao;
	}
	
	@Override
	public boolean equals(Object obj){
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode (){
		return HashCodeUtil.hashAll(id);
	}
	
	/** Retorna o  número para o código de barras dos fascículos. */
	public int getNumeroGeradorCodigoAssinatura(){
		return numeroGeradorCodigoAssinatura;
	}
	
	

	@Override
	public String toString() {
		return "Biblioteca: "+identificador+" - "+descricao+"("+id+") ";
	}

	
	// sets e gets

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public String getIdentificador() {
		return identificador;
	}

	/**
	 * Atribui o identificador da biblioteca, passando-o para letras "caixa alta"
	 *
	 * @param identificador
	 */
	public void setIdentificador(String identificador) {
		
		if(identificador != null)
			this.identificador = identificador.toUpperCase();
		else
			this.identificador = identificador;
	}

	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isFuncionaSabado() {
		return funcionaSabado;
	}

	public void setFuncionaSabado(boolean funcionaSabado) {
		this.funcionaSabado = funcionaSabado;
	}

	public boolean isFuncionaDomingo() {
		return funcionaDomingo;
	}

	public void setFuncionaDomingo(boolean funcionaDomingo) {
		this.funcionaDomingo = funcionaDomingo;
	}

	public boolean isSelecionada() {
		return selecionada;
	}

	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}
	
	public String getCodigoIdentificadorBiblioteca() {
		return codigoIdentificadorBiblioteca;
	}

	public boolean isAcervoPublico() {
		return acervoPublico;
	}

	public void setAcervoPublico(boolean acervoPublico) {
		this.acervoPublico = acervoPublico;
	}

	public boolean isServicoEmprestimosAtivos() {
		return servicoEmprestimosAtivos;
	}

	public void setServicoEmprestimosAtivos(boolean servicoEmprestimosAtivos) {
		this.servicoEmprestimosAtivos = servicoEmprestimosAtivos;
	}

	public ServicosInformacaoReferenciaBiblioteca getServicos() {
		return servicos;
	}

	public void setServicos(ServicosInformacaoReferenciaBiblioteca servicos) {
		this.servicos = servicos;
	}

	public List<ServicosEmprestimosBiblioteca> getServicosEmprestimos() {
		return servicosEmprestimos;
	}

	public void setServicosEmprestimos(List<ServicosEmprestimosBiblioteca> servicosEmprestimos) {
		this.servicosEmprestimos = servicosEmprestimos;
	}

	
	
	
	
}