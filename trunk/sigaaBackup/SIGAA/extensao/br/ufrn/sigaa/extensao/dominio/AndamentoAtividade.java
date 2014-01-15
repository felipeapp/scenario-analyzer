package br.ufrn.sigaa.extensao.dominio;

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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;

/**
 * Classe reponsável pelo armazenamento das informações quanto o 
 * andamento das atividades das ações de extensão.  
 * @author jean
 */
@Entity
@Table(schema = "extensao", name = "andamento_atividade")
public class AndamentoAtividade implements Validatable {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "id_andamento_atividade", nullable = false)
	private int id;
	
	/** Tipo de relatório para qual o andamento foi cadastrado */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_relatorio_andamento", unique = true, updatable = false)
	private TipoRelatorioExtensao tipoRelatorio = new TipoRelatorioExtensao();
	
	/** Atividade a ser cadastrada o acompanhamento */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_atividade")
	private ObjetivoAtividades atividade;
	
	/** Armazena o andamento da atividade */
	@Column(name = "andamento_atividade")
	private int andamentoAtividade;
	
	/** Armazena o status da atividade */
	@Column(name = "status_atividade")
	private int statusAtividade;

	/** Data de cadastro do componente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada do usuário que cadastrou. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data da última atualização do componente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Registro entrada do usuário que realizou a última atualização. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;
	
	/**Id do Arquivo */
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	/** Indica se o componente é ativo ou não */
	@CampoAtivo(true)
	private boolean ativo = true;
	
	public boolean isAndamento() {
		return CalendarUtils.isDentroPeriodo(atividade.getDataInicio(), atividade.getDataFim())
				&& statusAtividade != 2 && statusAtividade != 3
				&& statusAtividade == 1 && andamentoAtividade > 0;
	}

	public boolean isConcluida() {
		return statusAtividade == 2;
	}

	public boolean isAtrasada() {
		return !CalendarUtils.isDentroPeriodo(atividade.getDataInicio(), atividade.getDataFim()) 
				&& statusAtividade == 1 && andamentoAtividade <= 100;
	}

	/** Retorna a Situação do status do andamento da atividade */
	public String getDescricaoSituacao() {
		switch (statusAtividade) {
		case 1:  return "EM CURSO";
		case 2:  return "CONCLUÍDO";
		case 3:  return "CANCELADO";
		default: return "NÃO INFORMADO";
		}
	}
	
	public boolean isCancelada() {
		return statusAtividade == 3;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ObjetivoAtividades getAtividade() {
		return atividade;
	}

	public void setAtividade(ObjetivoAtividades atividade) {
		this.atividade = atividade;
	}

	public int getAndamentoAtividade() {
		return andamentoAtividade;
	}

	public void setAndamentoAtividade(int andamentoAtividade) {
		this.andamentoAtividade = andamentoAtividade;
	}

	public int getStatusAtividade() {
		return statusAtividade;
	}

	public void setStatusAtividade(int statusAtividade) {
		this.statusAtividade = statusAtividade;
	}

	public TipoRelatorioExtensao getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(TipoRelatorioExtensao tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
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
	
	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}

}