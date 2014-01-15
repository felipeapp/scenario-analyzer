package br.ufrn.sigaa.apedagogica.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.annotations.CascadeType.DELETE_ORPHAN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;

/**
 * Entidade que agrega um conjunto de atividades de atualização pedagógica.
 * 
 * Por exemplo: uma semana de um conjunto de capacitações, onde o docente
 * escolherá quais deverá participar.
 * 
 * @author Gleydson
 *
 */
@Entity
@Table(name = "grupo_atividades_atualizacao_pedagogica", schema = "apedagogica")
public class GrupoAtividadesAtualizacaoPedagogica implements Validatable {

	/** Atributo que define a unicidade. */
	@Id
	@Column(name = "id_grupo_atividades_atualizacao_pedagogica")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Atributo que define as atividades do grupo em que o docente pode se inscrever. */
	@OneToMany(mappedBy = "grupoAtividade",	cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Cascade(DELETE_ORPHAN)		
	private List<AtividadeAtualizacaoPedagogica> atividades;
	
	/** Atributo que define o texto que identifica o grupo ou evento. */
	private String denominacao;
	
	/** Atributo que define o período inicial das atividades. */
	private Date inicio;
	
	/** Atributo que define o período final das atividades. */
	private Date fim;
	
	/** Atributo que define o período inicial de inscricão das atividades. */
	@Column(name = "inicio_inscricao")
	private Date inicioInscricao;
	
	/** Atributo que define o período final  de inscricão das atividades. */
	@Column(name = "fim_inscricao")
	private Date fimInscricao;

	public GrupoAtividadesAtualizacaoPedagogica(){
		this.atividades = new ArrayList<AtividadeAtualizacaoPedagogica>();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
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
	
	public Date getInicioInscricao() {
		return inicioInscricao;
	}

	public void setInicioInscricao(Date inicioInscricao) {
		this.inicioInscricao = inicioInscricao;
	}

	public Date getFimInscricao() {
		return fimInscricao;
	}

	public void setFimInscricao(Date fimInscricao) {
		this.fimInscricao = fimInscricao;
	}

	/**
	 * Retorna as atividade associadas ordenadas pela data início.
	 * @return
	 */
	public List<AtividadeAtualizacaoPedagogica> getAtividades() {
		
		if( isEmpty(atividades) )
			return null;
		
		if( !isEmpty(atividades) ){
			Collections.sort( (List<AtividadeAtualizacaoPedagogica>) atividades, new Comparator<AtividadeAtualizacaoPedagogica>(){
				public int compare(AtividadeAtualizacaoPedagogica a1,	AtividadeAtualizacaoPedagogica a2) {						
					return a2.getInicio().compareTo(a1.getInicio()); 				
				}
			});
		}			
			
		
		return atividades;
	}
	
	/**
	 * Retorna somente as atividades ativas
	 * @return
	 */
	@Transient
	public List<AtividadeAtualizacaoPedagogica> getAtividadesAtivas(){
		
		List<AtividadeAtualizacaoPedagogica> atividadesAtivas = 
			new ArrayList<AtividadeAtualizacaoPedagogica>();
		
		if( isEmpty(getAtividades()) )
			return null;
		
		for (AtividadeAtualizacaoPedagogica a : getAtividades()) 
			if( a.isAtivo() )
				atividadesAtivas.add(a);
		
		 return atividadesAtivas;
		
	}

	public void setAtividades(List<AtividadeAtualizacaoPedagogica> atividades) {
		this.atividades = atividades;
	}
	
	/**
	 * Adiciona uma atividade ao grupo
	 * @param atividade
	 */
	public void addAtividade(AtividadeAtualizacaoPedagogica atividade){
		
		if (atividades == null) 
			atividades = new ArrayList<AtividadeAtualizacaoPedagogica>();	
		
		atividade.setGrupoAtividade(this);
		if (!atividades.contains(atividade)) 
			atividades.add(atividade);
		else
			atividades.set(atividades.indexOf(atividade), atividade);
		
	}
	
	/**
	 * Verifica se grupo está aberto.
	 * @return
	 */
	public boolean isAberto(){
		return CalendarUtils.isDentroPeriodo(getInicioInscricao(), getFimInscricao());
	}
		
	/**
	 * Define a regra de preenchimento dos campos no formulário.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		return lista;
		
	}
	
	@Override
	public String toString() {
		StringBuilder strClass = new StringBuilder();
		
		strClass.append(getDenominacao() + "(" + getInicio()  + "/" +  getFim() + ")");
		
		return strClass.toString();
	}

}
