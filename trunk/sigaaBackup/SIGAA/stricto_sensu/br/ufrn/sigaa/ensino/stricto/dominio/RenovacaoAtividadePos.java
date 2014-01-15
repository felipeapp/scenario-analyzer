/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jul 10, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Esta entidade registra renovação de matrícula em atividade de qualificação ou defesa.
 * É utilizado quando um aluno passa mais de um período cursando uma destas atividades.
 * Ela garante que o aluno tem vinculo ativo com a instituição no ano.periodo indicado na renovação
 * @author Victor Hugo
 */
@Entity
@Table(name = "renovacao_atividade_pos", schema = "stricto_sensu", uniqueConstraints = {})
public class RenovacaoAtividadePos implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_renovacao_atividade_pos")
	private int id;

	/** atividade que foi renovada, só existe uma solicitação de matricula quando a renovação é realizada pelo discente através da matrícula online */
	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_solicitacao_matricula")
	private SolicitacaoMatricula solicitacaoMatricula;
	
	/**
	 * matricula que foi renovada, quando a renovação é realizada pelo programa não existe solicitação de matricula e sim uma matricula
	 */
	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_matricula_componente")
	private MatriculaComponente matricula;
	
	/** Discente matriculado. */
	@ManyToOne(fetch=FetchType.EAGER, targetEntity=Discente.class)
	@JoinColumn(name = "id_discente")	
	private DiscenteAdapter discente;

	/** ano que foi renovado */
	private int ano;

	/** período que foi renovado */
	private int periodo;

	/**
	 * indica se esta renovação é valida, ou seja, se é ativa
	 * utilizada caso o orientador ou coordenador aprove uma matricula e, ainda durante o período de analise, mude de
	 * idéia e cancele a matricula..
	 * A RenovacaoAtividade criada será inativada
	 */
	private boolean ativo = true;

	/** data do cadastro */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** registro entrada de quem cadastrou */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** data da última atualização */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;

	/** registro entrada da última atualização */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAlteracao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SolicitacaoMatricula getSolicitacaoMatricula() {
		return solicitacaoMatricula;
	}

	public void setSolicitacaoMatricula(SolicitacaoMatricula solicitacaoMatricula) {
		this.solicitacaoMatricula = solicitacaoMatricula;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public RegistroEntrada getRegistroAlteracao() {
		return registroAlteracao;
	}

	public void setRegistroAlteracao(RegistroEntrada registroAlteracao) {
		this.registroAlteracao = registroAlteracao;
	}
	
	public MatriculaComponente getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

}
