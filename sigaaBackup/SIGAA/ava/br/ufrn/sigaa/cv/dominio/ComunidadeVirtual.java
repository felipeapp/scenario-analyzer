/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe principal da Comunidade Virtual.
 * 
 * @author Gleydson
 * 
 */
@Entity 
@Table(name="comunidade_virtual", schema="cv")
public class ComunidadeVirtual implements Validatable {

	@Id 
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	private String nome;
	private String descricao;
	
	/** Indica se as listagens devem vir em ordem crescente, de acordo com a data. */
	@Column (name="ordem_crescente")
	private boolean ordemCrescente;

	@ManyToOne
	@JoinColumn(name="id_tipo_comunidade_virtual")
	private TipoComunidadeVirtual tipoComunidadeVirtual;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="comunidade", fetch=FetchType.EAGER)
	@JoinColumn(name="id_comunidade")
	private Set<MembroComunidade> participantesComunidade = new LinkedHashSet<MembroComunidade>();
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="comunidade", fetch=FetchType.EAGER)
	@JoinColumn(name="id_comunidade")
	private List<TopicoComunidade> topicos = new ArrayList<TopicoComunidade>();
	
	@ManyToOne 
	@JoinColumn(name="id_usuario")
	private Usuario usuarioCadastro;
	
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	private boolean ativa = Boolean.TRUE;
	
	@Column(name="id_grupo_associado")
	private Integer idGrupoAssociado;

	/**
	 * Indica se a comunidade poderá ter seus tópicos visualizados externamente no portal público
	 */
	@Column(name="permite_visualizacao_externa")
	private Boolean permiteVisualizacaoExterna = Boolean.FALSE;
	
	/** Indica se usuários que não possuem um login nos sistemas da instituição poderão participar da comunidade. */
	@Column(name="permite_acesso_externo")
	private Boolean permiteAcessoExterno = Boolean.FALSE;
	
	/** Código que deve ser informado para ingressantes obterem acesso imediato em comunidades moderadas. */
	@Column(name="codigo_acesso")
	private String codigoAcesso;
	
	/** Dica que será exibida quando o código de acesso for solicitado. */
	@Column(name="dica_codigo_acesso")
	private String dicaCodigoAcesso;
	
	public ComunidadeVirtual() {
		tipoComunidadeVirtual = new TipoComunidadeVirtual();
	}
	
	public ComunidadeVirtual(int id) {
		this();
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(nome, "Nome", lista);
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		ValidatorUtil.validateRequired(tipoComunidadeVirtual, "Tipo da Comunidade", lista);

		return lista;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Usuario getUsuario() {
		return usuarioCadastro;
	}

	public void setUsuario(Usuario usuario) {
		this.usuarioCadastro = usuario;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	public TipoComunidadeVirtual getTipoComunidadeVirtual() {
		return tipoComunidadeVirtual;
	}

	public void setTipoComunidadeVirtual(TipoComunidadeVirtual tipoComunidadeVirtual) {
		this.tipoComunidadeVirtual = tipoComunidadeVirtual;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Integer getIdGrupoAssociado() {
		return idGrupoAssociado;
	}

	public void setIdGrupoAssociado(Integer idGrupoAssociado) {
		this.idGrupoAssociado = idGrupoAssociado;
	}

	public Set<MembroComunidade> getParticipantesComunidade() {
		return participantesComunidade;
	}

	public void setParticipantesComunidade(Set<MembroComunidade> participantesComunidade) {
		this.participantesComunidade = participantesComunidade;
	}

	public Boolean getPermiteVisualizacaoExterna() {
		return permiteVisualizacaoExterna;
	}

	public void setPermiteVisualizacaoExterna(Boolean permiteVisualizacaoExterna) {
		this.permiteVisualizacaoExterna = permiteVisualizacaoExterna;
	}
	
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public List<TopicoComunidade> getTopicos() {
		ArrayList<TopicoComunidade> listagem = new ArrayList<TopicoComunidade>();
		
		Map<TopicoComunidade, List<MaterialComunidade>> materiais = new HashMap<TopicoComunidade, List<MaterialComunidade>>();
		Map<Integer, Integer> referenciaColecao = new HashMap<Integer, Integer>();
		
		// Construir árvore de tópicos
		for (TopicoComunidade topico : topicos) {
			List<MaterialComunidade> materiaisTopico = materiais.get(topico);
			if (!isEmpty(materiaisTopico))
				topico.getMateriais().addAll(materiaisTopico);
			
			referenciaColecao.put(topico.getId(), topico.getTopicoPai() == null ? 0 : topico.getTopicoPai().getId());
		}
		
		// Calcular o nível de cada tópico
		for (TopicoComunidade aula : topicos) {
			int idPai = referenciaColecao.get(aula.getId());
			int nivel = 0;
			while (idPai != 0 && referenciaColecao.get(idPai) != null && nivel < 50) {
				idPai = referenciaColecao.get(idPai);
				nivel++;
			}
			aula.setNivel(nivel);
		}

		ordenarTopicos(topicos, null, listagem);
			
		return listagem;
	}
	
	@SuppressWarnings("unchecked")
	private void ordenarTopicos(Collection<TopicoComunidade> aulas, final Integer pai, List<TopicoComunidade> result) {
		List<TopicoComunidade> itens = (List<TopicoComunidade>) CollectionUtils.select(aulas, new Predicate() {
			public boolean evaluate(Object o) {
				TopicoComunidade ta = (TopicoComunidade) o;
				if (ta.getTopicoPai() == null) {
					return pai == null;
				} else {
					if (pai == null) return false;
					else return ta.getTopicoPai().getId() == pai;
				}
			}
		});
		
		if (!isEmpty(itens)) {
			Collections.sort(itens, new Comparator<TopicoComunidade>() {
				public int compare(TopicoComunidade o1, TopicoComunidade o2) {
					return o1.getDataCadastro().compareTo(o2.getDataCadastro());
				}
			});
			
			if (pai == null) {
				if (result == null) result = new ArrayList<TopicoComunidade>();
				result.addAll(itens);
			} else {
				int index = result.indexOf(new TopicoComunidade(pai));
				result.addAll(index + 1, itens);
			}
			
			for (TopicoComunidade ta : itens) {
				ordenarTopicos(aulas, ta.getId(), result);
			}
		}
	}
	
	public void setTopicos(List<TopicoComunidade> topicos) {
		this.topicos = topicos;
	}

	public boolean isOrdemCrescente() {
		return ordemCrescente;
	}

	public void setOrdemCrescente(boolean ordemCrescente) {
		this.ordemCrescente = ordemCrescente;
	}

	public Boolean getPermiteAcessoExterno() {
		return permiteAcessoExterno;
	}

	public void setPermiteAcessoExterno(Boolean permiteAcessoExterno) {
		this.permiteAcessoExterno = permiteAcessoExterno;
	}

	public String getCodigoAcesso() {
		return codigoAcesso;
	}

	public void setCodigoAcesso(String codigoAcesso) {
		this.codigoAcesso = codigoAcesso;
	}

	public String getDicaCodigoAcesso() {
		return dicaCodigoAcesso;
	}

	public void setDicaCodigoAcesso(String dicaCodigoAcesso) {
		this.dicaCodigoAcesso = dicaCodigoAcesso;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime
				* result
				+ ((tipoComunidadeVirtual == null) ? 0 : tipoComunidadeVirtual
						.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComunidadeVirtual other = (ComunidadeVirtual) obj;
		if (id != other.id)
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (tipoComunidadeVirtual == null) {
			if (other.tipoComunidadeVirtual != null)
				return false;
		} else if (!tipoComunidadeVirtual.equals(other.tipoComunidadeVirtual))
			return false;
		return true;
	}

}