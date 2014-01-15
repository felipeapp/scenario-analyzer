/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/08/2011
 */
package br.ufrn.sigaa.ensino.dominio;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * @author David Pereira
 *
 */
@Entity
@Table(schema = "ensino", name = "tipo_jubilamento")
public class TipoJubilamento implements Validatable {

	
	/** identificador da ação */
    @Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_tipo_jubilamento", unique = true, nullable = false)
	private int id;
	
    @Column(name = "nome")
	private String nome;
    
    @Column(name = "nivel")
	private char nivel;
	
    @Column(name = "classe_consulta")
    private String classeConsulta;
	
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_movimentacao_aluno")
	private TipoMovimentacaoAluno tipoMovimentacao;

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

	public String getClasseConsulta() {
		return classeConsulta;
	}

	public void setClasseConsulta(String classeConsulta) {
		this.classeConsulta = classeConsulta;
	}
	
	public TipoMovimentacaoAluno getTipoMovimentacao() {
		return tipoMovimentacao;
	}
	
	public void setTipoMovimentacao(TipoMovimentacaoAluno tipoMovimentacao) {
		this.tipoMovimentacao = tipoMovimentacao;
	}
	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}	
}
