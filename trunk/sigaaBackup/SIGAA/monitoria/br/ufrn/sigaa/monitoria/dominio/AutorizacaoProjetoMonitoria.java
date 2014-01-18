package br.ufrn.sigaa.monitoria.dominio;

// Generated 26/02/2007 10:21:38 

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.projetos.dominio.TipoAutorizacaoDepartamento;

/*******************************************************************************
 * <p>
 * Classe que representa a autorização dada pelo chefe de departamento que
 * participa do projeto através de seus componentes curriculares.
 * </p>
 * <p>
 * Assim, se pelo menos um componente curricular do departamento em questão
 * estiver presente no projeto de monitoria, o chefe do referido departamento
 * deverá autorizar a inclusão do componente no projeto.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "autorizacao_projeto_monitoria", schema = "monitoria")
public class AutorizacaoProjetoMonitoria implements Validatable, Cloneable {

	// Fields
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_autorizacao_projeto_monitoria")
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_autorizacao_projeto_monitoria", unique = false, nullable = true, insertable = true, updatable = true)
	private TipoAutorizacaoDepartamento tipoAutorizacao = new TipoAutorizacaoDepartamento();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto_monitoria", unique = false, nullable = false, insertable = true, updatable = true)
	private ProjetoEnsino projetoEnsino;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade", unique = false, nullable = false, insertable = true, updatable = true)
	private Unidade unidade;

	@Column(name = "autorizado", unique = false, nullable = false, insertable = true, updatable = true)
	private boolean autorizado;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_autorizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 8)
	private Date dataAutorizacao;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_reuniao", unique = false, nullable = true, insertable = true, updatable = true, length = 8)
	private Date dataReuniao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroEntrada;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_devolucao", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntradaDevolucao;
	
	private boolean ativo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true, length = 8)
	@CriadoEm
	private Date dataCadastro;

	

	// Constructors


	/** default constructor */
	public AutorizacaoProjetoMonitoria() {
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public Unidade getUnidade() {
		return this.unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	/**
	 * Projeto de ensino do tipo Projeto de Monitoria
	 * 
	 * @return
	 */
	public ProjetoEnsino getProjetoEnsino() {
		return this.projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	public TipoAutorizacaoDepartamento getTipoAutorizacao() {
		return this.tipoAutorizacao;
	}

	public void setTipoAutorizacao(
			TipoAutorizacaoDepartamento tipoAutorizacao) {
		this.tipoAutorizacao = tipoAutorizacao;
	}

	public RegistroEntrada getRegistroEntrada() {
		return this.registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/**
	 * Informa a data da autorizaÃ§Ã£o/desautorização dada pelo chefe do
	 * departamento
	 * 
	 * @return
	 */
	public Date getDataAutorizacao() {
		return dataAutorizacao;
	}

	public void setDataAutorizacao(Date dataAutorizacao) {
		this.dataAutorizacao = dataAutorizacao;
	}

	public Date getDataReuniao() {
		return dataReuniao;
	}

	public void setDataReuniao(Date dataReuniao) {
		this.dataReuniao = dataReuniao;
	}

	public boolean isAutorizado() {
		return autorizado;
	}

	public void setAutorizado(boolean autorizado) {
		this.autorizado = autorizado;
	}

	public RegistroEntrada getRegistroEntradaDevolucao() {
		return registroEntradaDevolucao;
	}

	public void setRegistroEntradaDevolucao(RegistroEntrada registroEntradaDevolucao) {
		this.registroEntradaDevolucao = registroEntradaDevolucao;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "projetoEnsino.id", "unidade.id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getProjetoEnsino().getId(), unidade.getId());
	}
	
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(projetoEnsino, "Projeto de Monitoria", lista);
		ValidatorUtil.validateRequired(unidade,	"Unidade Responsável (Departamento)", lista);
		ValidatorUtil.validateRequired(registroEntrada, "Registro de Entrada", lista);
		if (isAutorizado()) {
			ValidatorUtil.validateRequired(tipoAutorizacao,	"Tipo de Autorização", lista);
			if ((tipoAutorizacao != null) && (tipoAutorizacao.getId() != TipoAutorizacaoDepartamento.AD_REFERENDUM)) {
			    ValidatorUtil.validateRequired(dataReuniao, "Data Reunião", lista);
			}
		} else {
			if (tipoAutorizacao != null) {
			    lista.addErro("Projeto Não será autorizado! Tipo de Autorizacao Não deve ser informado");
			}
			if (dataReuniao != null) {
			    lista.addErro("Projeto Não será autorizado! Data da Reunião Não deve ser informada");
			}
		}
		return lista;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {

		AutorizacaoProjetoMonitoria copia = (AutorizacaoProjetoMonitoria) super
				.clone();
		copia.setId(0);

		return copia;
	}

	public Date getDataCadastro() {
	    return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
	    this.dataCadastro = dataCadastro;
	}

}