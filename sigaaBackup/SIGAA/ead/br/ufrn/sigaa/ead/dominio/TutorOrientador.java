package br.ufrn.sigaa.ead.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.parametros.dominio.ParametrosEAD;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe que determina quais as pessoas consideradas tutores de um determinado polo
 *
 * @author Andre M Dantas
 *
 */
@Entity
@Table(name = "tutor_orientador", schema = "ead")
public class TutorOrientador implements Validatable{

	/** Define se o tutor é presencial. */
	public static final char TIPO_TUTOR_PRESENCIAL = 'P';
	/** Define se o tutor é à distância. */
	public static final char TIPO_TUTOR_A_DISTANCIA = 'D';
	
	/** Chave primária.*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tutor_orientador", nullable = false)
	private int id;

	/** Pólo e curso nos quais o tutor trabalha */
	@ManyToOne()
	@JoinColumn(name = "id_polo_curso")
	private PoloCurso poloCurso;

	/** Os pólo e cursos nos quais o tutor trabalha. */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "tutor_polo_curso", schema = "ead",
		joinColumns = @JoinColumn (name="id_tutor_orientador"),
		inverseJoinColumns = @JoinColumn(name="id_polo_curso")
	)
	private List <PoloCurso> poloCursos = new ArrayList <PoloCurso> ();
	
	/** Pessoa que é um tutor */
	@ManyToOne()
	@JoinColumn(name="id_pessoa")
	private Pessoa pessoa;

	/** Vínculo do tutor */
	@ManyToOne()
	@JoinColumn(name="id_vinculo")
	private VinculoTutor vinculo;

	/** Horários de trabalho do tutor */
	@OneToMany(mappedBy="tutor")
	private List<HorarioTutor> horarios;

	/** Foto do perfil do tutor */
	@Column(name="id_foto")
	private Integer idFoto;

	/** Se o tutor está ativo */
	private boolean ativo;
	
	/** Perfil do tutor */
	@Column(name="id_perfil")
	private Integer idPerfil;

	/** Tipo de ensino do tutor 
	 *	<ul>
	 * 		<li>TIPO_TUTOR_PRESENCIAL = P;</li>
	 * 		<li>TIPO_TUTOR_A_DISTANCIA = D;</li>
	 *	</ul> 
	 */
	private char tipo;
	
	/** Variável que guarda o usuário do tutor. */
	@Transient
	private Usuario usuario;
	
	/** Variável que guarda o perfil do tutor. */
	@Transient
	private PerfilPessoa perfil;

	/** Usado para indicar o total de orientandos de um tutor. */
	@Transient
	private Long totalOrientandos;
	
	/** Usado para indicar se o tutor possui acesso completo. */
	@Transient
	private Boolean possuiAcessoCompleto;
	
	/** Lista de turmas do tutor à distância. */
	@Transient
	private ArrayList<Turma> turmas;
	
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

	public PoloCurso getPoloCurso() {
		return poloCurso;
	}

	public void setPoloCurso(PoloCurso poloCurso) {
		this.poloCurso = poloCurso;
	}

	public VinculoTutor getVinculo() {
		return vinculo;
	}

	public void setVinculo(VinculoTutor vinculo) {
		this.vinculo = vinculo;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(pessoa, "Pessoa", erros);
		return erros;
	}

	/**
	 * Retorna o nome do tutor.
	 * @return
	 */
	@Transient
	public String getNome() {
		if (pessoa != null)
			return pessoa.getNome();
		return "";
	}

	public List<HorarioTutor> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<HorarioTutor> horarios) {
		this.horarios = horarios;
	}

	/**
	 * Adiciona um horário na lista de horário (a validação deve ser feita antes da chamada desses método).
	 * @param horario
	 */
	public void addHorario(HorarioTutor horario) {
		if (horarios == null)
			horarios = new ArrayList<HorarioTutor>();
		horario.setTutor(this);
		if (!horarios.contains(horario))
			horarios.add(horario);
	}

	/**
	 * Remove um horário já inserido da lista de horários.
	 * @param horario
	 */
	public void removeHorario(HorarioTutor horario) {
		if (horarios != null && !horarios.isEmpty())
			horarios.remove(horario);
	}
	
	/**
	 * Método usado para verificar se o intervalo de horário já foi definido.  
	 * @param horario
	 * @return true se o horário já está inserido e false caso contrário.
	 */
	public boolean hasHorario(HorarioTutor horario) {
		if (horarios != null && !horarios.isEmpty()) {
			for(HorarioTutor h:horarios) {
				if (horario.getDia() == h.getDia()) { 
					if (!((horario.getHoraInicio() >= h.getHoraFim() && horario.getHoraFim() > h.getHoraFim()) ||
						(horario.getHoraInicio() < h.getHoraInicio() && horario.getHoraFim() <= h.getHoraInicio())) )
						return true;
				}
				
			}
			
			horario.setTutor(this);
			return false;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TutorOrientador other = (TutorOrientador) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public Integer getIdPerfil() {
		return idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}

	public PerfilPessoa getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Long getTotalOrientandos() {
		return totalOrientandos;
	}

	public void setTotalOrientandos(Long totalOrientandos) {
		this.totalOrientandos = totalOrientandos;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	public char getTipo() {
		return tipo;
	}
	
	/**
	 * Método usado para verificar se é tutor presencial e se possui acesso total ao módulo do Portal do Tutor
	 */
	public boolean isAcessoCompleto () {
		if ( tipo == TIPO_TUTOR_PRESENCIAL && isPossuiAcessoCompleto() )
			return true;
		else
			return false;
	}
	
	/**
	 * Método usado para verificar se o tutor presencial possui acesso total ao módulo do Portal do Tutor
	 */
	private Boolean isPossuiAcessoCompleto () {
		if ( possuiAcessoCompleto == null  )
			possuiAcessoCompleto = ParametroHelper.getInstance().getParametroBoolean(ParametrosEAD.ACESSO_TOTAL_TUTOR_PRESENCIAL);
		return possuiAcessoCompleto;
	}

	public void setPoloCursos(List <PoloCurso> poloCursos) {
		this.poloCursos = poloCursos;
	}

	public List <PoloCurso> getPoloCursos() {
		return poloCursos;
	}
	
	/**
	 * Retorna o nome dos cursos do Pólo-Cursos
	 */
	public String getDescricaoCursos () {
		String res = "";
		if (poloCursos!=null)
			for (PoloCurso pc : poloCursos)
				if (pc.getCurso()!=null)
					res += (res!="") ? " , " + pc.getCurso().getNome() : pc.getCurso().getNome();
		return res;		
	}
	
	/**
	 * Retorna as disciplinas das turmas
	 */
	public ArrayList<ComponenteCurricular> getDisciplinas () {
		if (isDistancia()){
			ArrayList<ComponenteCurricular> disciplinas = new ArrayList<ComponenteCurricular>();
			if (!ValidatorUtil.isEmpty(turmas)){
				for (Turma t : turmas)
					if (!disciplinas.contains(t.getDisciplina()))
						disciplinas.add(t.getDisciplina());
			}
			return disciplinas;
		}
		return null;
	}
	
	/**
	 * Retorna as pólos das turmas
	 */
	public ArrayList<Polo> getPolosTurma () {
		if (isDistancia()){
			ArrayList<Polo> polos = new ArrayList<Polo>();
			if (!ValidatorUtil.isEmpty(turmas)){
				for (Turma t : turmas)
					if (!polos.contains(t.getPolo()))
						polos.add(t.getPolo());
			}
			return polos;
		}
		return null;
	}
	
	public boolean isDistancia () {
		return tipo == TIPO_TUTOR_A_DISTANCIA;
	}
	
	public boolean isPresencial () {
		return tipo == TIPO_TUTOR_PRESENCIAL;
	}

	public void setTurmas(ArrayList<Turma> turmas) {
		this.turmas = turmas;
	}

	public ArrayList<Turma> getTurmas() {
		return turmas;
	}
}

