package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

@Entity
@Table(name="restricao_inscricao_selecao", schema="ensino")
public class RestricaoInscricaoSelecao implements Validatable {

	@Id
	@Column(name="id_restricao_inscricao_selecao")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	private String descricao;
	
	private String classe;
	
	private char nivel;
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		 this.id = id;
		
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		validateRequired(this.nivel, "Nível", lista);
		validateRequired(this.descricao, "Descrição", lista);
		validateRequired(this.classe, "Classe", lista);
		
		return lista;
	}

	
	
}
