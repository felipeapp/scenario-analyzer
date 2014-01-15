/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 19/04/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe utilizada para registrar pré-inscrições de pessoas em processos seletivos
 * de qualquer nível de ensino.
 * 
 * @author Leonardo
 *
 */
@Entity
@Table(name="pre_inscricao", schema="ensino")
public class PreInscricao extends AbstractMovimento implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_pre_inscricao", nullable = false)
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;

	@ManyToOne
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivo processoSeletivo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	public ProcessoSeletivo getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivo processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	public ListaMensagens validate() {

		ListaMensagens erros = new ListaMensagens();

		PessoaValidator.validarDadosPessoais(pessoa, null, PessoaValidator.DISCENTE, erros);
		ValidatorUtil.validateRequired(processoSeletivo, "Processo Seletivo", erros);

		return erros;
	}

}