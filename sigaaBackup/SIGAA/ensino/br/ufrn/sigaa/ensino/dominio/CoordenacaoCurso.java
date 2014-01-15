/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 01/11/2006
*/

package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade usada para registrar as coordenações de curso e secretarias, informando também o prazo
 * de vigência do mandato do coordenador, o telefone para contado do coordenador, email, ramal, site 
 * oficial da coordenação.
 * 
 * @author wendell
 * 
 */
@Entity
@Table(name = "coordenacao_curso", schema = "ensino", uniqueConstraints = {})
public class CoordenacaoCurso implements Validatable, ViewAtividadeBuilder {

	// Fields

	/** Chave Primária */
	private int id;

	/** Cargo Acadêmico do coordenador do curso */
	private CargoAcademico cargoAcademico = new CargoAcademico();

	/** Curso de coordenação */
	private Curso curso = new Curso();

	/** Servidor que coordena o curso */
	private Servidor servidor = new Servidor();

	/** Informa a unidade quando se tratar do um programa de pós-graduação */
	private Unidade unidade;

	/** Início do mandato do coordenador */
	private Date dataInicioMandato;

	/** Fim do mandato do coordenador */
	private Date dataFimMandato;

	/** Telefones de contato do coordenador */
	private String telefoneContato1, telefoneContato2;

	/** Ramal de contato do coordenador */
	private String ramalTelefone1, ramalTelefone2;

	/** Email de contato do coordenador */
	private String emailContato;

	/** Registro de entrada do usuário que cadastrou o registro do mandato */
	private RegistroEntrada usuarioAtribuidor;

	/** Registro de entrada do usuário que cadastrou o fim do mandato */
	private RegistroEntrada usuarioFinalizador;

	/** Registro de entrada de quando ocorreu a ultima alteração no registro */
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;
	
	/** Status de validade do registro */
	private boolean ativo;

	/** Endereço web oficial da coordenação */
	private String paginaOficialCoordenacao;

	// Constructors

	/** default constructor */
	public CoordenacaoCurso() {
	}

	/** minimal constructor */
	public CoordenacaoCurso(int idCoordenacaoCurso) {
		this.id = idCoordenacaoCurso;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_coordenacao_curso", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idCoordenacaoCurso) {
		this.id = idCoordenacaoCurso;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_cargo_academico", unique = false, nullable = true, insertable = true, updatable = true)
	public CargoAcademico getCargoAcademico() {
		return this.cargoAcademico;
	}

	public void setCargoAcademico(CargoAcademico cargoAcademico) {
		this.cargoAcademico = cargoAcademico;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	public Curso getCurso() {
		return this.curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getServidor() {
		return this.servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_mandato", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataInicioMandato() {
		return this.dataInicioMandato;
	}

	public void setDataInicioMandato(Date dataInicioMandato) {
		this.dataInicioMandato = dataInicioMandato;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_mandato", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataFimMandato() {
		return this.dataFimMandato;
	}

	public void setDataFimMandato(Date dataFimMandato) {
		this.dataFimMandato = dataFimMandato;
	}

	/**
	 * Implementação do método equals comparando o id, o servidro, o Cargo Academico, o Curso, a Unidade, a Data de Inicio do Mandato,
	 * a data Fim do Mandato e o email de Contato da atual com a passada como parâmetro.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id"); 
	}

	/**
	 * Implementação do método hashCode comparando o id, o servidro, o Cargo Academico, o Curso, a Unidade, a Data de Inicio do Mandato,
	 * a data Fim do Mandato e o email de Contato da atual com a passada como parâmetro.
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/**
	 * Valida se os campos obrigatórios foram preenchidos corretamente
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(cargoAcademico, "Função", lista);
		ValidatorUtil.validateRequired(servidor, "Coordenador", lista);
		ValidatorUtil.validateRequired(servidor.getNome(), "Coordenador", lista);
		
		ValidatorUtil.validateRequired(Formatador.getInstance().formatarData(dataInicioMandato), "Inicio do Mandato", lista);
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(dataFimMandato), "Fim do Mandato", lista);
		
		if (dataFimMandato != null) 
			ValidatorUtil.validateMinValue(dataFimMandato, dataInicioMandato, "Final do Mandato", lista);
		
		return lista;
	}

	@Column(name = "email_contato", unique = false, nullable = true, insertable = true, updatable = true)
	public String getEmailContato() {
		return emailContato;
	}

	public void setEmailContato(String emailContato) {
		this.emailContato = emailContato;
	}

	@Column(name = "telefone_contato1", unique = false, nullable = true, insertable = true, updatable = true)
	public String getTelefoneContato1() {
		return telefoneContato1;
	}

	public void setTelefoneContato1(String telefoneContato1) {
		this.telefoneContato1 = telefoneContato1;
	}

	@Column(name = "telefone_contato2")
	public String getTelefoneContato2() {
		return telefoneContato2;
	}

	public void setTelefoneContato2(String telefoneContato2) {
		this.telefoneContato2 = telefoneContato2;
	}

	@Column(name = "ramal_telefone1")
	public String getRamalTelefone1() {
		return ramalTelefone1;
	}

	public void setRamalTelefone1(String ramalTelefone1) {
		this.ramalTelefone1 = ramalTelefone1;
	}

	@Column(name = "ramal_telefone2")
	public String getRamalTelefone2() {
		return ramalTelefone2;
	}

	public void setRamalTelefone2(String ramalTelefone2) {
		this.ramalTelefone2 = ramalTelefone2;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade")
	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atribuidor")
	public RegistroEntrada getUsuarioAtribuidor() {
		return usuarioAtribuidor;
	}

	public void setUsuarioAtribuidor(RegistroEntrada usuarioAtribuidor) {
		this.usuarioAtribuidor = usuarioAtribuidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_finalizacao")
	public RegistroEntrada getUsuarioFinalizador() {
		return usuarioFinalizador;
	}

	public void setUsuarioFinalizador(RegistroEntrada usuarioFinalizador) {
		this.usuarioFinalizador = usuarioFinalizador;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Verifica se a coordenação está vigente, ou seja, se esta dentro do mandato
	 * 
	 * @return
	 */
	@Transient
	public boolean isVigente() {
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		return hoje.after(getDataInicioMandato()) && hoje.before(getDataFimMandato());
	}

	@Column(name = "pagina_oficial_coordenacao")
	public String getPaginaOficialCoordenacao() {
		return paginaOficialCoordenacao;
	}

	public void setPaginaOficialCoordenacao(String paginaOficialCoordenacao) {
		this.paginaOficialCoordenacao = paginaOficialCoordenacao;
	}

	/**
	 * Retorna em um HashMap o nome do curso, a data de Inicio do Mandato e a data Fim do Mandato. 
	 */
	@Transient
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("curso.nome", "cursoNome");
		itens.put("dataInicioMandato", null);
		itens.put("dataFimMandato", null);
		return itens;
	}

	/** Seta o nome do Curso */
	@Transient
	public void setCursoNome( String nome ){
		if( this.getCurso() == null )
			this.setCurso( new Curso() );
		this.getCurso().setNome(nome);
	}
	
	@Transient
	public float getQtdBase() {
		return CalendarUtils.calculoMeses(dataInicioMandato, dataFimMandato);
	}
	
	/**
	 * Método que retorna uma string formatada em HTML com informação
	 * do nome do curso, e uma versão formatada da data. Na interface,
	 * esse código fica emparelhado com o código gerado por getTituloView().
	 */
	@Transient
	public String getItemView() {
		return "  <td>"+getCurso().getDescricao()+"</td>" +
			   "  <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(dataInicioMandato)+" - "+ Formatador.getInstance().formatarData(dataFimMandato)+"</td>" +
			   "  <td style=\"text-align:center\">Não</td>";
	}

	/**
	 * Método que retorna uma string formatada em HTML com o título
	 * das informações mostradas pelo método getItemView().
	 */
	@Transient
	public String getTituloView() {
		return  "    <td>Atividade</td>" +
				"    <td style=\"text-align:center\">Período</td>" +
				"    <td style=\"text-align:center\">Remunerado</td>";
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	/**
	 * Método que verifica se é coordenador.
	 * @return
	 */
	@Transient
	public boolean isCoordenador(){
		return cargoAcademico.getId() == CargoAcademico.COORDENACAO;
	}
	
	/**
	 * Esse método tem como finalidade verificar se o mandato do coordenador está vigente.
	 * @return
	 */
	@Transient
	public boolean isVirgente(){		
		Date data = new Date(); 
		return getDataFimMandato().getTime() > data.getTime();
	}
	
	/**
	 * Esse método tem como finalidade retornar um endereço passível de ser inserido em um href. 
	 * @return
	 */
	@Transient
	public String getPaginaOficialCoordenacaoLinkFormatado() {		
		String enderecoCadastrado = getPaginaOficialCoordenacao();
		if( isEmpty(enderecoCadastrado) )
			return null;
		String enderecoFormatado = "http://" + enderecoCadastrado.replace("http://", "");	
		return enderecoFormatado;
	}
	
	
}