package br.ufrn.sigaa.apedagogica.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.annotations.CascadeType.DELETE_ORPHAN;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Atividade de atualização pedagógica, que faz parte de um conjunto de 
 * atividades de atualização.
 * 
 * @author Gleydson
 *
 */
@Entity
@Table(name = "atividade_atualizacao_pedagogica", schema = "apedagogica")
public class AtividadeAtualizacaoPedagogica implements Validatable {

	/** Atributo que define a unicidade */
	@Id
	@Column(name = "id_atividade_atualizacao_pedagogica")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	
	/** Atributo que define a qual grupo a atividade pertence. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_grupo_atividades_atualizacao_pedagogica")		
	private GrupoAtividadesAtualizacaoPedagogica grupoAtividade;
	
	/** Atributo que define os instrutores que mnistraram as atividades. */
	@OneToMany(mappedBy = "atividade",	cascade = CascadeType.ALL)
	@Cascade(DELETE_ORPHAN)		
	private List<InstrutorAtividadeAtualizacaoPedagogica> instrutores;
	
	/** Atributo que define o título que identifica a atividade. */
	private String nome;
	
	/** Atributo que define o texto que descreve a atividade. */
	private String descricao;
	
	/** Atributo que define o texto da ementa a ser exibida no certificado. */
	private String ementa;

	/** Atributo que define o horário inicial da atividade.
	 *	O valor não deve ser superior ao horário final. 
	 */
	@Column(name = "horario_inicio")
	private String horarioInicio;
	
	/** Atributo que define o horário final da atividade.
	 *	O valor não deve ser inferior ao horário inicial. 
	 */
	@Column(name = "horario_fim")
	private String horarioFim;
	
	/** Atributo que define a data inicial da atividade.
	 *	O valor não deve ser inferior ao prríodo inicial do grupo. 
	 */
	private Date inicio;
	
	/** Atributo que define a data final da atividade.
	 *	O valor não deve ser superior ao período final do grupo e nem inferior a data inicial. 
	 */
	private Date fim;
	
	/**
	 * Atributo que define a carga horária da atividade.
	 */
	private Integer ch;
		
	/**
	 * Atributo que define o número de vagas da atividade.
	 */
	@Column(name = "num_vagas")
	private Integer numVagas;
	
	/**
	 * Atributo que define o id do arquivo anexado como
	 * ementa da atividade.
	 */
	@Column(name = "id_arquivo")
	private Integer idArquivo;
	
	/**
	 * Atributo que define no cadastro da partipação
	 * quais atividades foram selecionada pelo docente. 
	 */
	@Transient
	private boolean selecionada;
	
	/**
	 * Atributo que define o número de vagas disponíveis.
	 * Utilizado nas consultas. 
	 */
	@Transient
	private Integer numVagasAtual;
	
	/**
	 * Atributo que define se a atividade está visível para o usuário.
	 */
	private boolean ativo;
	
	public AtividadeAtualizacaoPedagogica(){
		this.instrutores = new ArrayList<InstrutorAtividadeAtualizacaoPedagogica>();
		this.grupoAtividade = new GrupoAtividadesAtualizacaoPedagogica();
		this.ativo = true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public GrupoAtividadesAtualizacaoPedagogica getGrupoAtividade() {
		return grupoAtividade;
	}

	public void setGrupoAtividade(
			GrupoAtividadesAtualizacaoPedagogica grupoAtividade) {
		this.grupoAtividade = grupoAtividade;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public Integer getCh() {
		return ch;
	}

	public void setCh(Integer ch) {
		this.ch = ch;
	}

	public Integer getNumVagas() {
		return numVagas;
	}

	public void setNumVagas(Integer numVagas) {
		this.numVagas = numVagas;
	}
	
	public List<InstrutorAtividadeAtualizacaoPedagogica> getInstrutores() {
		return instrutores;
	}
	
	public void setInstrutores(ArrayList<InstrutorAtividadeAtualizacaoPedagogica> instrutores) {
		this.instrutores = instrutores;
	}
	
	/**
	 * Adiciona uma pessoa como instrutor de uma atividade
	 * @param atividade
	 */
	public void addInstrutor(Pessoa pessoa){
		
		if (instrutores == null) 
			instrutores = new ArrayList<InstrutorAtividadeAtualizacaoPedagogica>();	
		
		InstrutorAtividadeAtualizacaoPedagogica instrutor = new InstrutorAtividadeAtualizacaoPedagogica();
		instrutor.setPessoa(pessoa);
		instrutor.setAtividade(this);
		
		if (!instrutores.contains(instrutor)) 
			instrutores.add(instrutor);
		else
			instrutores.set(instrutores.indexOf(instrutor), instrutor);
		
	}
	
	/**
	 * Remove uma pessoa como instrutor de uma atividade
	 * @param atividade
	 */
	public void removeInstrutor(Pessoa pessoa){
		
		InstrutorAtividadeAtualizacaoPedagogica instrutor = new InstrutorAtividadeAtualizacaoPedagogica();
		instrutor.setPessoa(pessoa);
		instrutor.setAtividade(this);
		
		if ( !isEmpty(instrutores) ) 
			instrutores.remove(instrutor);
		
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "nome");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, nome);
	}
	
	/**
	 * Retorna todos os instrutores separados por vírgula.
	 * @return
	 */
	public String getDescricaoInstrutores(){
		
		if( isEmpty(instrutores) )
			return null;
		
		StringBuilder strInstrutores = new StringBuilder();
		
		for (InstrutorAtividadeAtualizacaoPedagogica i : instrutores) {
			strInstrutores.append(",");
			strInstrutores.append(i.getPessoa().getNome());
		}
		
		return strInstrutores.deleteCharAt(0).toString();
		
	}
	
	@Override
	public String toString() {
		
		StringBuilder strClass = new StringBuilder();
		strClass.append(getNome() + "(" +  getInicio()  + "/" +  getFim() + ") \n");
		
		return strClass.toString();
		
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getHorarioInicio() {
		return horarioInicio;
	}

	public void setHorarioInicio(String horarioInicio) {
		this.horarioInicio = horarioInicio;
	}

	public String getHorarioFim() {
		return horarioFim;
	}

	public void setHorarioFim(String horarioFim) {
		this.horarioFim = horarioFim;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	/**
	 * Define a regra de preenchimento dos campos no formulário.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSelecionada() {
		return selecionada;
	}

	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}

	public Integer getNumVagasAtual() {
		return numVagasAtual;
	}

	public void setNumVagasAtual(Integer numVagasAtual) {
		this.numVagasAtual = numVagasAtual;
	}
	
	/**
	 * Verifica se a quantidade de vagas já foram preenchidas.
	 * @return
	 */
	public boolean isVagasEsgotadas(){
		return !isEmpty(numVagas) && !isEmpty(numVagasAtual) && numVagas.compareTo(numVagasAtual) <= 0;
	}

	public void setInstrutores(
			List<InstrutorAtividadeAtualizacaoPedagogica> instrutores) {
		this.instrutores = instrutores;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public String getEmenta() {
		return ementa;
	}

	public void setEmenta(String ementa) {
		this.ementa = ementa;
	}

	/**
	 * Retorna o a descrição do período.
	 * @return
	 */
	public String getDescricaoPeriodo(){
		SimpleDateFormat dia = new SimpleDateFormat("dd");
		SimpleDateFormat mes = new SimpleDateFormat("MMMM");
		SimpleDateFormat ano = new SimpleDateFormat("yyyy");
		
		if( inicio.equals(fim) ){
			return "no dia " + dia.format(inicio) + " de " + mes.format(inicio) + " de " + ano.format(inicio);
		}else if(CalendarUtils.getAno(inicio) == CalendarUtils.getAno(fim) && CalendarUtils.getMesByData(inicio) == CalendarUtils.getMesByData(fim))
			return CalendarUtils.getDiaByData(inicio) + " a " + CalendarUtils.format(fim, "dd/MM/yyyy");
		else 
			return CalendarUtils.format(inicio, "dd/MM/yyyy") + " a " + CalendarUtils.format(fim, "dd/MM/yyyy");
		
	}

	
	
}
