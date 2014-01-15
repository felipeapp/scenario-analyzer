/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/12/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio; 

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.monitoria.dominio.NotaItemMonitoria;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/*******************************************************************************
 * <p>Representa uma avaliação de extensão. Pode ser uma avaliação de relatório,
 * avaliação de ação (parcial ou final). Estas avaliações são realizada por
 * membros do comitê de extensão ou por avaliadores Ad Hoc selecionados pela
 * PROEX.
 * </p>
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "avaliacao_atividade", schema = "extensao")
public class AvaliacaoAtividade implements Validatable {
	

    /** Chave Primária */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
    @Column(name = "id_avaliacao_atividade")
    private int id;
    
    /** usado na avaliação de projetos*/ 
    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "avaliacaoAtividade")
    private List<NotaItemMonitoria> notasItem = new ArrayList<NotaItemMonitoria>();

    /** justificativa dada para a aprovação ou reprovação da ação.*/
    @Column(name = "justificativa")
    private String justificativa;

   
//    //Representa a aprovação ou reprovação da ação.
//    @Column(name = "aprovado")
//    private boolean aprovado;
   
    
    /**Representa o tipo de parecer dado pelo avaliador ao avaliar a proposta de uma ação de extensão.
    O avaliador pode 'aprovar', 'aprovar com recomendação' ou 'reprovar' a ação, por exemplo.*/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_parecer_avaliacao")
    private TipoParecerAvaliacaoExtensao parecer;

    /**Representa a nota data para a atividade.*/
    @Column(name = "nota")
    private double nota;
    
    /** Atributo utilizado para representar a quantidade de boslas propostas */
    @Column(name = "bolsas_propostas")
    private int bolsasPropostas;

    /**Representa o tipo de avaliação, a avaliação pode ou não ser feitas por membros da comissão.*/ 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_avaliacao")
    private TipoAvaliacao tipoAvaliacao;

    /**Representa o membro da comissão responsável pela avaliação.*/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_membro_comissao")
    private MembroComissao membroComissao;

    /**Representa o avaliador ad hoc responsável pela avaliação.*/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_avaliador")
    private AvaliadorAtividadeExtensao avaliadorAtividadeExtensao;

    /**Atividade sendo avaliada.*/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_atividade_extensao")
    private AtividadeExtensao atividade;

    /**Data da distribuição da avaliação para um membro da comissão ou avaliador ad hoc avaliar.*/
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_distribuicao")
    private Date dataDistribuicao;

    /**Data da avaliação*/
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_avaliacao")
    private Date dataAvaliacao;

    /**Status da avaliação*/ 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status_avaliacao")
    private StatusAvaliacao statusAvaliacao = new StatusAvaliacao();

    /** usuário que fez a avaliação*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada_avaliacao")
    private RegistroEntrada registroEntradaAvaliacao;

    /** usuário que fez a distribuição*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada_distribuicao")
    private RegistroEntrada registroEntradaDistribuicao;

    /** usuário que fez a remoção da distribuição*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada_exclusao")
    private RegistroEntrada registroEntradaExclusao;

    /** orçamento proposto pelo comitê de extensão*/
    @OneToMany(mappedBy = "avaliacaoAtividade")
    @OrderBy("elementoDespesa ASC")
    private Collection<AvaliacaoOrcamentoProposto> orcamentoProposto = new ArrayList<AvaliacaoOrcamentoProposto>();
    
    /** avaliação atividade ativa*/
    @Column(name="ativo")
    private boolean ativo = true;

    public int getId() {
    	return id;
    }

    public void setId(int id) {
    	this.id = id;
    }    

    public Date getDataAvaliacao() {
    	return dataAvaliacao;
    }

    public void setDataAvaliacao(Date dataAvaliacao) {
    	this.dataAvaliacao = dataAvaliacao;
    }

    public String getJustificativa() {
    	return justificativa;
    }

    public void setJustificativa(String justificativa) {
    	this.justificativa = justificativa;
    }

    public MembroComissao getMembroComissao() {
    	return membroComissao;
    }

    public void setMembroComissao(MembroComissao membroComissao) {
    	this.membroComissao = membroComissao;
    }

    /**
     * Método utilizado para validar os atributos da Avaliação da Atividade
     */
    public ListaMensagens validate() {
    	ListaMensagens lista = new ListaMensagens();
    	
    	if ((tipoAvaliacao != null) &&  parecer.getId() == TipoParecerAvaliacaoExtensao.APROVADO_COM_RECOMENDACAO 
    			&& justificativa.trim().equals("")) {
    		lista.addErro("O campo Justificativa é obrigatório quando a Ação de Extensão for APROVADA COM RECOMENDAÇÃO");
    	}
    	
    	if ((tipoAvaliacao != null) && (tipoAvaliacao.getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA)) {
    		for (NotaItemMonitoria nota : notasItem) {
    		    ValidatorUtil.validateMaxValue(nota.getNota(), nota.getItemAvaliacao().getNotaMaxima(), nota.getNota() + ", nota atribuída para '" + nota.getItemAvaliacao().getDescricao() + "'", lista);
    		    ValidatorUtil.validateMinValue(nota.getNota(), new Double(0), nota.getNota() + ", nota atribuída para '" + nota.getItemAvaliacao().getDescricao() + "'", lista);
    		}
    	}
    	
    	ValidatorUtil.validateRequired(atividade, "Ação de Extensão", lista);
    	ValidatorUtil.validateRequired(tipoAvaliacao, "Tipo de Avaliação", lista);
    	if ((getAvaliadorAtividadeExtensao() == null) && (getMembroComissao() == null)) {
    		lista.getMensagens().add(new MensagemAviso("Avaliador: campo obrigatório não informado.", TipoMensagemUFRN.ERROR));
    	}
    	
    	if ((tipoAvaliacao != null) && (tipoAvaliacao.getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA) && (dataAvaliacao != null)) {
    		if(nota < 0 || nota > 10)
    			lista.addErro( "Total: O somatório das notas das categorias deve estar entre 1 e 10" );
    	}
    	
    	return lista;
    }

    public double getNota() {
    	return nota;
    }

    public void setNota(double nota) {
    	this.nota = nota;
    }

    /**
     * Informa o tipo de avaliação. Dependendo do tipo de avaliação o avaliador
     * pode ser um membro da comissão de extensão ou um membro da comissão de
     * avaliadores Ad Hoc (pareceristas).
     * 
     * @return
     */
    public TipoAvaliacao getTipoAvaliacao() {
    	return tipoAvaliacao;
    }

    public void setTipoAvaliacao(TipoAvaliacao tipoAvaliacao) {
    	this.tipoAvaliacao = tipoAvaliacao;
    }

    public Date getDataDistribuicao() {
    	return dataDistribuicao;
    }

    public void setDataDistribuicao(Date dataDistribuicao) {
    	this.dataDistribuicao = dataDistribuicao;
    }

    public AvaliadorAtividadeExtensao getAvaliadorAtividadeExtensao() {
    	return avaliadorAtividadeExtensao;
    }

    public void setAvaliadorAtividadeExtensao(AvaliadorAtividadeExtensao avaliadorAtividadeExtensao) {
    	this.avaliadorAtividadeExtensao = avaliadorAtividadeExtensao;
    }

    public StatusAvaliacao getStatusAvaliacao() {
    	return statusAvaliacao;
    }

    public void setStatusAvaliacao(StatusAvaliacao statusAvaliacao) {
    	this.statusAvaliacao = statusAvaliacao;
    }

    public AtividadeExtensao getAtividade() {
    	return atividade;
    }

    public void setAtividade(AtividadeExtensao atividade) {
    	this.atividade = atividade;
    }

    /**
     * Método utilizado para verificar se o OBJ passado por parâmetro é igual ao OBJ atual da classe
     */
    @Override
    public boolean equals(Object obj) {
    	return EqualsUtil.testEquals(this, obj, "id", "atividade","avaliadorAtividadeExtensao", "membroComissao","dataDistribuicao");
    }

    /**
     * Método utilizado para informar o hashCode
     */
    @Override
    public int hashCode() {
    	return HashCodeUtil.hashAll(id, atividade, avaliadorAtividadeExtensao,membroComissao, dataDistribuicao);
    }

    public RegistroEntrada getRegistroEntradaDistribuicao() {
    	return registroEntradaDistribuicao;
    }

    public void setRegistroEntradaDistribuicao(RegistroEntrada registroEntradaDistribuicao) {
    	this.registroEntradaDistribuicao = registroEntradaDistribuicao;
    }

    public RegistroEntrada getRegistroEntradaExclusao() {
    	return registroEntradaExclusao;
    }

    public void setRegistroEntradaExclusao(RegistroEntrada registroEntradaExclusao) {
    	this.registroEntradaExclusao = registroEntradaExclusao;
    }

    public RegistroEntrada getRegistroEntradaAvaliacao() {
    	return registroEntradaAvaliacao;
    }

    public void setRegistroEntradaAvaliacao(RegistroEntrada registroEntradaAvaliacao) {
    	this.registroEntradaAvaliacao = registroEntradaAvaliacao;
    }

    public List<NotaItemMonitoria> getNotasItem() {
    	return notasItem;
	}

	public void setNotasItem(List<NotaItemMonitoria> notasItem) {
		this.notasItem = notasItem;
	}

	public Collection<AvaliacaoOrcamentoProposto> getOrcamentoProposto() {
		return orcamentoProposto;
    }

    public void setOrcamentoProposto(Collection<AvaliacaoOrcamentoProposto> orcamentoProposto) {
    	this.orcamentoProposto = orcamentoProposto;
    }

    public int getBolsasPropostas() {
    	return bolsasPropostas;
    }

    public void setBolsasPropostas(int bolsasPropostas) {
    	this.bolsasPropostas = bolsasPropostas;
    }

    /**
     * Retorna o servidor responsável por avaliar a ação de extensão. Caso o
     * avaliador não tenha sido determinado antes, um new Servidor() é retornado.
     * 
     * @return
     */
    public Servidor getAvaliador() {
    	return avaliadorAtividadeExtensao != null ? avaliadorAtividadeExtensao.getServidor() : (membroComissao != null ? membroComissao
			.getServidor() : new Servidor());
    }

    public boolean isAutorizacaoCertificado() {
    	return (dataAvaliacao != null ? true : false);
    }

    /**
     * Usado no texto do certificado de avaliador.
     * 
     * @return
     */
    public String getTipoAvaliadorString() {
    	if(membroComissao != null)
    		return "COMITÊ DE EXTENSÃO";
    	else
    		return "COMITÊ AD HOC";
    }
    
    /** adiciona nota para avaliação */
    public void addNota(double nota) {
		this.nota += nota;
	}
    
    /** adiciona um objeto NotaItemMOnitoria a coleção notasItem */
    public boolean addNotaItemMonitoria(NotaItemMonitoria obj) {
		obj.setAvaliacaoAtividade(this);
		return notasItem.add(obj);
	}
    
    /** calcula média da avaliação */
    public void calcularMedia() {
    	Double notaParcial = 0.0;
    	if (notasItem != null) {
    		for (NotaItemMonitoria nota : notasItem) {
    			notaParcial += (nota.getNota() * nota.getItemAvaliacao().getPeso());
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
    	if (notasItem != null) {
    		for (NotaItemMonitoria nota : notasItem) {
    			result += nota.getItemAvaliacao().getPeso();
    		}
    	}
    	return result;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public TipoParecerAvaliacaoExtensao getParecer() {
	return parecer;
    }

    public void setParecer(TipoParecerAvaliacaoExtensao parecer) {
	this.parecer = parecer;
    }

    /** Verifica se um membro do comitê de extensão pode avaliar a ação. */
    public boolean isPermitirAvaliacaoMembroComite(){
    	return (this.atividade.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO);
    }
    
    /** Verifica se a avaliação está pendente de ser concluída pelo avaliador. */
    public boolean isAvaliacaoPendente() {
    	if (this.getStatusAvaliacao() != null) {
    		return ((this.getStatusAvaliacao().getId() == StatusAvaliacao.AGUARDANDO_AVALIACAO) 	
    				|| (this.getStatusAvaliacao().getId() == StatusAvaliacao.AVALIACAO_EM_ANDAMENTO));    		
    	}
    	return false;
    }
    
    /** Verifica se é uma valiação do comitê de extensão. */
    public boolean isAvaliacaoComite() {
    	return tipoAvaliacao.getId() == TipoAvaliacao.AVALIACAO_ACAO_COMITE;    	
    }

    /** Verifica se é uma valiação de parecerista ad hoc de extensão. */
    public boolean isAvaliacaoParecerista() {
    	return tipoAvaliacao.getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA;    	
    }

    /** Verifica se é uma valiação de presidente do comitê de extensão. */
    public boolean isAvaliacaoPresidenteComite() {
    	return tipoAvaliacao.getId() == TipoAvaliacao.AVALIACAO_ACAO_PRESIDENTE_COMITE;    	
    }

}
