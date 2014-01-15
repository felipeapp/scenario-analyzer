/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 18/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entidade que associa os orientadores de uma atividade acad�mica. 
 */
@Entity
@Table(name = "orientacao_atividade", schema = "ensino", uniqueConstraints = {})
public class OrientacaoAtividade implements PersistDB, Comparable<OrientacaoAtividade> {

	/** Constante que indica o tipo de orienta��o ORIENTADOR. */
	public static final char ORIENTADOR = 'O';
	/** Constante que indica o tipo de orienta��o COORIENTADOR. */
	public static final char COORIENTADOR = 'C';
	
	/** Chave prim�ria. */
	private int id;

	/** Orientador da Atividade. */
	private Servidor orientador;
	
	/**
	 * Orientador Externo. Quando o orientador da atividade � um docente externo.
	 * Est�gio N�O PODEM ter docentes externos como orientadores.
	 */
	private DocenteExterno orientadorExterno;

	/** Registro de atividade do qual esta orienta��o faz parte. */
	private RegistroAtividade registroAtividade;

	/** Carga hor�ria da orienta��o.*/
	private int cargaHoraria;

	/** Tipo de orienta��o da atividade: ORIENTADOR ou COORIENTADOR */
	private char tipo = ORIENTADOR;
	
	/** Constructor padr�o. */
	public OrientacaoAtividade() {
	}

	/** Construtor parametrizado.
	 * @param id Chave prim�ria.
	 */
	public OrientacaoAtividade(int id) {
		this.id = id;
	}


	/** Retorna a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	@Id
    @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
						parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
    @Column(name = "id_orientacao_atividade", nullable = false)
	public int getId() {
		return this.id;
	}

	/** Seta a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int idAproveitamento) {
		this.id = idAproveitamento;
	}

	/** Retorna o registro de atividade do qual esta orienta��o faz parte. 
	 * @return Registro de atividade do qual esta orienta��o faz parte. 
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atividade", unique = false, nullable = true, insertable = true, updatable = true)
	public RegistroAtividade getRegistroAtividade() {
		return registroAtividade;
	}

	/** Seta o registro de atividade do qual esta orienta��o faz parte. 
	 * @param registroAtividade Registro de atividade do qual esta orienta��o faz parte. 
	 */
	public void setRegistroAtividade(RegistroAtividade registroAtividade) {
		this.registroAtividade = registroAtividade;
	}

	/** Retorna o Orientador da Atividade.
	 * @return Orientador da Atividade. 
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getOrientador() {
		return orientador;
	}

	/** Seta o Orientador da Atividade. 
	 * @param orientador Orientador da Atividade. 
	 */
	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}

	/** Retorna o Orientador Externo. Quando o orientador da atividade � um docente externo. Est�gios N�O PODEM ter docentes externos como orientadores.
	 * @return Orientador Externo. 
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_orientador_externo", unique = false, nullable = true, insertable = true, updatable = true)
	public DocenteExterno getOrientadorExterno() {
		return orientadorExterno;
	}

	/** Seta o Orientador Externo. Quando o orientador da atividade � um docente externo. Est�gios N�O PODEM ter docentes externos como orientadores.
	 * @param orientadorExterno Orientador Externo.
	 */
	public void setOrientadorExterno(DocenteExterno orientadorExterno) {
		this.orientadorExterno = orientadorExterno;
	}

	/** Retorna a carga hor�ria da orienta��o.
	 * @return Carga hor�ria da orienta��o.
	 */
	@Column(name = "carga_horaria", unique = false, nullable = false, insertable = true, updatable = true)
	public int getCargaHoraria() {
		return cargaHoraria;
	}

	/** Seta a carga hor�ria da orienta��o.
	 * @param cargaHoraria Carga hor�ria da orienta��o.
	 */
	public void setCargaHoraria(int cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	/** Compara as chaves prim�rias deste objeto com o passado no par�metro.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(OrientacaoAtividade o) {
		if (o.getOrientador() != null && getOrientador() != null)
			return getOrientador().getId() - o.getOrientador().getId();

		return 0;
	}
	
	/** Verifica se este objeto e o passado como par�metro possuem a mesma chave.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OrientacaoAtividade) {
			OrientacaoAtividade other = (OrientacaoAtividade) obj;
			if (this.id == other.id) return true;
		}
		return false;
	}
	
	/** Retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/** Retorna o nome do orientador.
	 * @return nome do orientador.
	 */
	@Transient
	public String getNome(){
		if( !isEmpty(orientador) )
			return orientador.getNome();
		else if( !isEmpty(orientadorExterno) )  
			return orientadorExterno.getNome();
		else return null;
	}

	/** Retorna o nome do orientador e a matr�cula (caso orientador da institui��o) ou o cpf (caso orientador externo).
	 * @return (SIAPE ou CPF e nome do orientador.
	 */
	@Transient
	public String getMatriculaNome(){
		if( !isEmpty(orientador) )
			return orientador.getSiapeNome();
		else if( !isEmpty(orientadorExterno) )  
			return orientadorExterno.getCpfNome();
		else return null;
	}

	/** Indica se o orientador � servidor da institui��o.
	 * @return
	 */
	@Transient
	public boolean isServidor() {
		return !isEmpty(orientador);
	}
	
	/**
	 * Retorna o id do docente 
	 * @return
	 */
	@Transient
	public int getIdDocente(){
		if( orientador != null )
			return orientador.getId();
		else if( orientadorExterno != null )
			return orientadorExterno.getId();
		return 0;
	}
	
	/** Retorna uma representa��o textual deste objeto no formato:
	 * c�digo e nome do componente curricular, seguido de '-', seguido do nome do orientador.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getRegistroAtividade().getMatricula().getComponenteCodigoNome()+ " - " + getNome();
	}

	@Column(name = "tipo", unique = false, nullable = false, insertable = true, updatable = true)
	public char getTipo() {
		return tipo;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}
	
	/**
	 * Retorna a descri��o do tipo de orienta��o: ORIENTADOR ou COORIENTADOR
	 * @return
	 */
	@Transient
	public String getDescricaoTipo(){
		return tipo == ORIENTADOR ? "(Orientador)" : "(Coorientador)";
	}
}
