/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 18/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entidade que registra os dados complementares da atividade, tais como:
 * Orientador, coordenador, supervisor de estágio, etc.
 *
 * MatriculaComponente é quem referência esta entidade.
 *
 */
@Entity
@Table(name = "registro_atividade", schema = "ensino", uniqueConstraints = {})
public class RegistroAtividade implements PersistDB {

	/** Chave primária */
	@Id
    @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
						parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_registro_atividade", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Matrícula no componente curricular correspondente ao registro de atividade */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matricula_componente")
	private MatriculaComponente matricula;

	/** Orientações da ativadade registrada. */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "registroAtividade")
	private Set<OrientacaoAtividade> orientacoesAtividade = new TreeSet<OrientacaoAtividade>();

	/** Coordenador da atividade registrada */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_coordenador", unique = false, nullable = true, insertable = true, updatable = true)
	private Servidor coordenador;

	/** Supervisor da atividade registrada */
	private String supervisor;

	/** Indica se a atividade foi registrada através de uma matrícula compulsória */
	@Column(name = "matricula_compulsoria")
	private boolean matriculaCompulsoria;

	// Constructors

	/** default minimal constructor */
	public RegistroAtividade(int id) {
		this.id = id;
	}

	public RegistroAtividade() {
	}

	/**
	 * este método adiciona um orientador a lista de orientadores.
	 * Observe que um dos parâmetros NECESSARIAMENTE devem ser nulos!
	 * @param orientador utilizado quando o orientador é SERVIDOR @see Servidor
	 * @param orientadorExterno utilizado quando o orientador é DOCENTEEXTERNO @see DocenteExterno
	 * @param chDedicada a carga horária do orientador dedicada a orientação desta atividade 
	 * @param coorientadorExterno 
	 * @param coorientadorInterno 
	 */
	public void addOrientador(Servidor orientador, DocenteExterno orientadorExterno, int chDedicada, Servidor coorientadorInterno, DocenteExterno coorientadorExterno) {
		
		if( orientador == null && orientadorExterno == null )
			throw new IllegalArgumentException();
		
		if( chDedicada < 0  )
			throw new IllegalArgumentException();
		
		if (getOrientacoesAtividade() == null) {
			setOrientacoesAtividade(new TreeSet<OrientacaoAtividade>());
		}
		OrientacaoAtividade orientacao = new OrientacaoAtividade();
		orientacao.setCargaHoraria(chDedicada);
		orientacao.setRegistroAtividade(this);
		if( orientador != null ){
			orientacao.setOrientador(orientador);
			orientacao.setOrientadorExterno(null);
		}
		else if( orientadorExterno != null ){
			orientacao.setOrientadorExterno(orientadorExterno);
			orientacao.setOrientador(null);
		}
		getOrientacoesAtividade().add(orientacao);
		
		if( coorientadorInterno != null || coorientadorExterno != null){
			OrientacaoAtividade coorientacao = new OrientacaoAtividade();
			coorientacao.setCargaHoraria(chDedicada/2);
			coorientacao.setTipo(OrientacaoAtividade.COORIENTADOR);
			orientacao.setCargaHoraria(chDedicada/2);
			coorientacao.setRegistroAtividade(this);
			if( coorientadorInterno != null ){
				coorientacao.setOrientador(coorientadorInterno);
				coorientacao.setOrientadorExterno(null);
			}
			else if( coorientadorExterno != null ){
				coorientacao.setOrientadorExterno(coorientadorExterno);
				coorientacao.setOrientador(null);
			}
			getOrientacoesAtividade().add(coorientacao);
		}
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	public Servidor getCoordenador() {
		return coordenador;
	}

	// Property accessors

	public int getId() {
		return id;
	}

	public Set<OrientacaoAtividade> getOrientacoesAtividade() {
		return orientacoesAtividade;
	}

	public String getSupervisor() {
		return supervisor;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}

	public void setId(int idAproveitamento) {
		id = idAproveitamento;
	}

	public void setOrientacoesAtividade(
			Set<OrientacaoAtividade> orientacoesAtividade) {
		this.orientacoesAtividade = orientacoesAtividade;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	/**
	 * Retorna os nomes de todos os docentes orientadores da atividade.
	 * @return
	 */
	@Transient
	public String getDocentesNomes() {
		if (orientacoesAtividade != null) {
			StringBuffer nomes = new StringBuffer();
			Iterator<OrientacaoAtividade> it = orientacoesAtividade.iterator();
			int tamanho = orientacoesAtividade.size();
			for (int i = 1; i <= tamanho; i++) {
				nomes.append(it.next().getNome());
				if (i + 1 == tamanho)
					nomes.append(" e ");
				else if (i < tamanho)
					nomes.append(", ");
			}
			return nomes.toString();
		}
		return "";
	}

	public MatriculaComponente getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public void setAno(int ano ) {
		matricula.setAno((short)ano);
	}

	public void setPeriodo(int periodo ) {
		matricula.setPeriodo((byte)periodo);
	}

	public short getAno() {
		return matricula.getAno();
	}

	public byte getPeriodo() {
		return matricula.getPeriodo();
	}

	@Transient
	public ComponenteCurricular getAtividade() {
		return matricula.getComponente();
	}

	public void setAtividade(ComponenteCurricular atividade) {
		matricula.setComponente(atividade);
	}

	public boolean isMatriculaCompulsoria() {
		return matriculaCompulsoria;
	}

	public void setMatriculaCompulsoria(boolean matriculaCompulsoria) {
		this.matriculaCompulsoria = matriculaCompulsoria;
	}

	@Transient
	public boolean isDispensa() {
		return matricula != null && matricula.isDispensa();
	}
	
	/**
	 * diz se é necessário registrar produção intelectual,
	 * caso seja necessário terá mais um passo no final para realizar o cadastro da produção
	 */
	public boolean isNecessariaDefinicaoProducaoIntelectual() {
		return (getAtividade().isEstagio() || getAtividade().isTrabalhoConclusaoCurso()) 
			&& getOrientador() != null && (getOrientador().isServidor() || getAtividade().isLato());
	}
	
	/**
	 * Retorna o orientador da atividade.
	 * @return
	 */
	public OrientacaoAtividade getOrientador() {
		return getOrientacao(OrientacaoAtividade.ORIENTADOR);
	}

	/**
	 * Retorna o co-orientador da atividade, se houver.
	 * @return
	 */
	public OrientacaoAtividade getCoOrientador() {
		return getOrientacao(OrientacaoAtividade.COORIENTADOR);
	}
	
	/**
	 * Retorna a orientação do tipo informado.
	 * @param tipo
	 * @return
	 */
	private OrientacaoAtividade getOrientacao(char tipo){
		if (isEmpty(getOrientacoesAtividade())) {
			return null;
		}
		OrientacaoAtividade orientador = null;
		for(OrientacaoAtividade o: getOrientacoesAtividade()){
			if(o.getTipo() == tipo)
				orientador = o;
		}
		return orientador;
	}
}