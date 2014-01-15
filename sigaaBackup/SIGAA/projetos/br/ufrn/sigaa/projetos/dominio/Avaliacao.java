package br.ufrn.sigaa.projetos.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;

@Entity(name="br.ufrn.sigaa.projetos.dominio.Avaliacao")
@Table(name = "avaliacao", schema = "projetos")
public class Avaliacao implements PersistDB, Comparable<Avaliacao>{

	/** Identificador único do objeto. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Texto com o parecer dado pelo avaliador ao projeto.*/
	private String parecer;
	
	/** Representa a nota final da avaliação. Média da nota de todos os itens da avaliação.*/
	private Double nota = 0.0;
	
	/** Informa se a avaliação está logicamente ativa no sistema.*/
	@CampoAtivo
	private boolean ativo;
	
	/** Indica a situação da avaliação. Ex. Pendente, Realizada, Cancelada.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_tipo_situacao_avaliacao")
	private TipoSituacaoAvaliacao situacao = new TipoSituacaoAvaliacao();
	
	/** Projeto objeto da avaliação.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_projeto")
	private Projeto projeto = new Projeto();
	
	/** Contém dados da distribuição que deu origem a esta avaliação.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_distribuicao_avaliacao")
	private DistribuicaoAvaliacao distribuicao = new DistribuicaoAvaliacao();
	
	/** Registro da última operação realizada na avaliação.*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;
	
	/**Data da avaliação*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_avaliacao")
	private Date dataAvaliacao;

	/** Avaliador do projeto.*/
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_usuario_avaliador")
	private Usuario avaliador;

	/** Lista com todas as notas dadas pelo avaliador ao projeto. */
	@OneToMany(mappedBy = "avaliacao")
	private List<NotaItemAvaliacao> notas;

	/** Construtor Padrão. */
	public Avaliacao() {
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

	public TipoSituacaoAvaliacao getSituacao() {
	    return situacao;
	}

	public void setSituacao(TipoSituacaoAvaliacao situacao) {
	    this.situacao = situacao;
	}

	public Projeto getProjeto() {
	    return projeto;
	}

	public void setProjeto(Projeto projeto) {
	    this.projeto = projeto;
	}

	public String getParecer() {
	    return parecer;
	}

	public void setParecer(String parecer) {
	    this.parecer = parecer;
	}

	public DistribuicaoAvaliacao getDistribuicao() {
	    return distribuicao;
	}

	public void setDistribuicao(DistribuicaoAvaliacao distribuicao) {
	    this.distribuicao = distribuicao;
	}

	public RegistroEntrada getRegistroEntrada() {
	    return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
	    this.registroEntrada = registroEntrada;
	}

	public Usuario getAvaliador() {
	    return avaliador;
	}

	public void setAvaliador(Usuario avaliador) {
	    this.avaliador = avaliador;
	}

	@Override
	public boolean equals(Object obj) {
	    return EqualsUtil.testEquals(this, obj, "id", "projeto.id", "avaliador.id");
	}

	@Override
	public int hashCode() {
	    return HashCodeUtil.hashAll(id, projeto.getId(), avaliador.getId());
	}

	public List<NotaItemAvaliacao> getNotas() {
	    return notas;
	}

	public void setNotas(List<NotaItemAvaliacao> notas) {
	    this.notas = notas;
	}
	
	/**
	 * Nota final do projeto.
	 * 
	 * @return
	 */
	public Double getNota() {
	    return nota;
	}

	public void setNota(Double nota) {
	    this.nota = nota;
	}
	
	/** calcula média da avaliação */
	public void calcularMedia() {
		Double notaParcial = 0.0;
		if (notas != null) {
			for (NotaItemAvaliacao notaItem : notas) {
				notaParcial += (notaItem.getNota() * notaItem.getItemAvaliacao().getPeso());
			}
			setNota(notaParcial / getSomaPesosAvaliacao());
		}
	}

	/**
	 * Retorna a soma dos pesos da avaliação.
	 * 
	 * @return
	 */
	public double getSomaPesosAvaliacao() {
		double result = 0.0;
		if (notas != null) {
			for (NotaItemAvaliacao notaItem : notas) {
				result += notaItem.getItemAvaliacao().getPeso();
			}
		}
		return result;
	}
	
	/**
	 * Prepara notas para primeira avaliação.
	 */
	public void inicializarNotas() {
		if (notas == null || notas.isEmpty()) {
			for(ItemAvaliacaoProjeto item : distribuicao.getModeloAvaliacao().getQuestionario().getItensAvaliacao()) {
				NotaItemAvaliacao nota = new NotaItemAvaliacao();
				nota.setAvaliacao(this);
				nota.setItemAvaliacao(item);
				nota.setNota(0);
				notas.add(nota);
			}
		}
	}

	public Date getDataAvaliacao() {
	    return dataAvaliacao;
	}

	public void setDataAvaliacao(Date dataAvaliacao) {
	    this.dataAvaliacao = dataAvaliacao;
	}

	public ListaMensagens validate() {
	    ListaMensagens lista = new ListaMensagens();

	    if ((dataAvaliacao != null) && parecer.trim().equals("")) {
	    	lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Parecer");
	    }	    
	    if (ValidatorUtil.isEmpty(projeto)) {
	    	lista.addErro("Esta avaliação foi cancelada. Por favor reinicie a avaliação.");
	    } else if(notas != null) {
	    	for (NotaItemAvaliacao nota : notas) {
	    		ValidatorUtil.validateMaxValue(nota.getNota(), nota.getItemAvaliacao().getNotaMaxima(), nota.getNota() + ", nota atribuída para '" + nota.getItemAvaliacao().getPergunta().getDescricao() + "'", lista);
	    		ValidatorUtil.validateMinValue(nota.getNota(), new Double(0), nota.getNota() + ", nota atribuída para '" + nota.getItemAvaliacao().getPergunta().getDescricao() + "'", lista);
	    	}	    	
	    }
	    ValidatorUtil.validateMaxLength(parecer, 2000, "Parecer", lista);
	    return lista;
	}

	
	@Override
	public int compareTo(Avaliacao o) {
		if (this.getProjeto().equals(o.getProjeto())) {
			if (this.getDistribuicao().equals(o.getDistribuicao())) {
				if (this.getNota() > o.getNota())
					return 1;
				if (this.getNota() < o.getNota())
					return -1;
			}
		}
		return 0;
	}

	/**
	 * Informa se a avaliação foi realizada por um membro do comitê interno.
	 * 
	 * @return
	 */
	public boolean isAvaliacaoComiteInterno() {
		return this.distribuicao.isDistribuicaoComiteInterno();
	}

	/**
	 * Informa se é uma avaliação de projetos.
	 * 
	 * @return
	 */
	public boolean isAvaliacaoProjeto() {
		return this.distribuicao.isDistribuicaoProjeto();
	}

	/**
	 * Informa se é uma avaliação de relatório.
	 * 
	 * @return
	 */
	public boolean isAvaliacaoRelatorio() {
		return this.distribuicao.isDistribuicaoRelatorio();
	}

}
