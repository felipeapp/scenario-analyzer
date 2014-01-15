package br.ufrn.sigaa.apedagogica.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validaInicioFim;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;


/**
 * Cadastro da participação em Atividades de atualização pedagógica anteriores a inscrição
 * on-line.
 * 
 * @author Gleydson
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "registro_participacao_atualizacao_pedagogica", schema = "apedagogica")
public class RegistroParticipacaoAtualizacaoPedagogica  implements Validatable {

	/**
	 *  Define o ano anterior a disponibilização da funcionalidade de participação de atividades
	 *  de atualização pedagógica.{@link ParticipanteAtividadeAtualizacaoPedagogica}
	 */
	private static final Date DATA_BASE_VALIDACAO_PERIODO = Formatador.getInstance().parseDate("13/06/2011");
	
	/** Define a chave unica que identifica o registro da participação do docente na atividade. */
	@Id
	@Column(name = "id_registro_participacao_atualizacao_pedagogica")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/** Define o título da atividade que o docente participou */
	@Column(name = "titulo")
	private String titulo;
	
	/** Define a descrição da atividade que o docente participou */
	@Column(name = "descricao_atividade")
	private String descricaoAtividade;

	/** Define o período inicial da atividade que o docente participou */
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Define o período final da atividade que o docente participou */
	@Column(name = "data_fim")
	private Date dataFim;

	/** Define a carga horária da atividade que o docente participou */
	@Column(name = "carga_horaria")
	private Integer cargaHoraria;

	/** Define qual docente participou da atividade */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_servidor", nullable = true)
	private Servidor servidor;

	/** Define os dados  de entrada do usuário no sistema para inserção da atividade. */
	@ManyToOne (cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Define a data em que a atividade foi cadastrada */
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	
	public RegistroParticipacaoAtualizacaoPedagogica(){
		this.servidor = new Servidor();
		this.cargaHoraria = 0;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricaoAtividade() {
		return descricaoAtividade;
	}

	public void setDescricaoAtividade(String descricaoAtividade) {
		this.descricaoAtividade = descricaoAtividade;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	/**
	 * Converte um log de registro de participação para
	 * uma participação.
	 * @return
	 */
	public ParticipanteAtividadeAtualizacaoPedagogica toParticipacao(){
		
		ParticipanteAtividadeAtualizacaoPedagogica p = 
			new ParticipanteAtividadeAtualizacaoPedagogica();

		p.getAtividade().setNome( this.titulo );
		p.getAtividade().setDescricao( this.descricaoAtividade );
		p.getAtividade().setInicio( this.dataInicio );
		p.getAtividade().setFim( this.dataFim );
		p.setDocente( this.servidor );
		
		return p;
		
	}

	/**
	 * Verifica se os campos obrigatórios do formulário de cadastro
	 * foram preenchidos.
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		validateRequired(titulo, "Título", lista);
		validateRequired(descricaoAtividade, "Descrição", lista);
		validateRequired(dataInicio, "Data Início do Período", lista);
		validateRequired(dataFim, "Data Final do Período", lista);
		
		if( lista.isEmpty() )
			ValidatorUtil.validaDataAnteriorIgual(dataInicio, DATA_BASE_VALIDACAO_PERIODO, "Data Início do Período", lista);
		
		validaInicioFim(dataInicio, dataFim, "Período", lista);
		validateMinValue(cargaHoraria,1,"Carga Horaria", lista);

		validateRequired(servidor, "Docente", lista);
		
		return lista;
	}

}
