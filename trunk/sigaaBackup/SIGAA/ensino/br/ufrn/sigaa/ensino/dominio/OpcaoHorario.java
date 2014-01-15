/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 22/10/2010
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade que registra a preferência de horário escolhida pelo aluno
 * na matrícula por horário.
 * 
 * @author Leonardo Campos
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "opcao_horario", schema = "ensino", uniqueConstraints = {})
public class OpcaoHorario implements Validatable{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") }) 	
	@Column(name = "id_opcao_horario", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	private String opcao;
	
	private int ordem;
	
	@ManyToOne
	@JoinColumn(name="id_opcao_modulo")
	private OpcaoModulo opcaoModulo;
	
	public OpcaoHorario() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOpcao() {
		return opcao;
	}

	public void setOpcao(String opcao) {
		this.opcao = opcao;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public OpcaoModulo getOpcaoModulo() {
		return opcaoModulo;
	}

	public void setOpcaoModulo(OpcaoModulo opcaoModulo) {
		this.opcaoModulo = opcaoModulo;
	}

	public String getDescricao(){
		String dias = opcao.substring(0, opcao.length() - 1);
		String turno = opcao.substring(opcao.length() - 1);
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < dias.length(); i++) {
			switch (dias.charAt(i)) {
			case '2':
				sb.append("Segunda");
				break;
			case '3':
				sb.append("Terça");
				break;
			case '4':
				sb.append("Quarta");
				break;
			case '5':
				sb.append("Quinta");
				break;
			case '6':
				sb.append("Sexta");
				break;
			case '7':
				sb.append("Sábado");
				break;
			}
			if(i + 2 == dias.length())
				sb.append(" e ");
			else if(i != dias.length() - 1)
				sb.append(", ");
		}
		
		switch (turno.charAt(0)) {
		case 'M':
			sb.append(" - Manhã");
			break;
		case 'T':
			sb.append(" - Tarde");
			break;
		case 'N':
			sb.append(" - Noite");
			break;
		}
		
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "id", "opcao");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId(), getOpcao());
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
}
