/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 31/10/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que relaciona os docentes que ministram a turma e suas cargas horárias
 * 
 * @author amdantas
 */
@Entity
@Table(name = "docente_turma", schema = "ensino", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DocenteTurma implements PersistDB, ViewAtividadeBuilder, Comparable<DocenteTurma> {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_docente_turma", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Servidor que é o docente da turma */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente", unique = false, nullable = true, insertable = true, updatable = true)
	private Servidor docente = new Servidor();

	/** Docente externo à instituição que ministrará as aulas. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente_externo")
	private DocenteExterno docenteExterno;

	/** Turma que o docente está vinculado */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma", unique = false, nullable = false, insertable = true, updatable = true)
	private Turma turma;

	/** Carga horária dedicada pelo docente à turma */
	@Column(name = "ch_dedicada_periodo", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer chDedicadaPeriodo;

	/** Horário do docente na turma. */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_docente_turma", nullable = false)
	@OrderBy("dataInicio ASC")
	private List<HorarioDocente> horarios = new ArrayList<HorarioDocente>(0);

	/** Grupo que o docente está vinculado à turma */
	@Column(name = "grupo_docente", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer grupoDocente;
	
	/** Usuário associado ao docente da turma */
	@Transient
	private Usuario usuario;
	
	/**
	 * Indica se a CH é referente a residência médica (sem turma). {@see CHDedicadaResidenciaMedica}.
	 * Podem existir turmas cadastradas no módulo de residência médica e nestas turmas podem ser definidas ch do docente.
	 * No entanto, essa variável expressa referência APENAS a ch de residência cadastradas FORA do fluxo de criação de turmas. 
	 */
	@Transient
	private boolean chResidenciaSemTurma = false;
	
	/**
	 * Indica a carga horária diluída entre as semanas no semestre
	 */
	@Transient
	private Double chDedicadaSemana;
		
	/** default constructor */
	public DocenteTurma() {
	}

	/** default minimal constructor */
	public DocenteTurma(int id) {
		this();
		this.id = id;
	}

	/** minimal constructor */
	public DocenteTurma(int idDocenteTurma, Turma turma) {
		this(idDocenteTurma);
		this.turma = turma;
	}

	/** full constructor */
	public DocenteTurma(int idDocenteTurma, Servidor servidor, Turma turma,
			Integer chDedicadaPeriodo) {
		this(idDocenteTurma, turma);
		this.docente = servidor;
		this.chDedicadaPeriodo = chDedicadaPeriodo;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int idDocenteTurma) {
		this.id = idDocenteTurma;
	}

	public Servidor getDocente() {
		return this.docente;
	}

	public void setDocente(Servidor servidor) {
		this.docente = servidor;
	}

	public Turma getTurma() {
		return this.turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Integer getChDedicadaPeriodo() {
		return this.chDedicadaPeriodo;
	}
	
	public void setChDedicadaPeriodo(Integer chDedicadaPeriodo) {
		this.chDedicadaPeriodo = chDedicadaPeriodo;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DocenteTurma))
			return false;
		boolean equals = EqualsUtil.testEquals(this, obj, "id", "docente", "docenteExterno");
		// como o EqualsUtil.testEquals(this, obj, "id", "docente", "docenteExterno","horarios"); não está funcionando bem,
		// compara se os objetos possuem o mesmo horário
		if (equals) {
			DocenteTurma other = (DocenteTurma) obj;
			if (this.horarios != null && other.horarios != null) {
				for (HorarioDocente hd : this.horarios) {
					equals = equals && other.horarios.contains(hd);
				}
				for (HorarioDocente hd : other.horarios) {
					equals = equals && this.horarios.contains(hd);
				}
			}
		}
		return equals;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, docente, docenteExterno);
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	/**
	 * Retorna identificação de um dos docentes da turma,
	 * seja ele externo ou servidor
	 * @return
	 */
	public String getDocenteDescricao() {
		if (getDocente() != null && getDocente().getId() != 0){
			return getDocente().getSiapeNome();
		}
		if (getDocenteExterno() != null && getDocenteExterno().getId() != 0){
			if( !isEmpty( getDocenteExterno().getInstituicao() ) )
				return getDocenteExterno().getNomeInstituicaoENomeDocente();
			else
				return getDocenteExterno().getCpfNome();
		}
		return null;
	}
	
	/**
	 * Retorna o nome de um dos docentes da turma,
	 * seja ele externo ou servidor
	 * @return
	 */
	public String getDocenteNome() {
		if (!isEmpty(getDocente()))
			return getDocente().getNome();
		if (!isEmpty(getDocenteExterno()))
			return getDocenteExterno().getNome();
		return null;
	}

	/** (non-Javadoc)
	 * @see br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder#getTituloView()
	 */
	public String getTituloView() {
		return " <td>Disciplina</td>"
			+ "	<td style=\"text-align:center\">Semestre</td>"
			+ " <td style=\"text-align:right\">Turma</td>"
			+ " <td style=\"text-align:right\">CH</td>"
			+ " <td style=\"text-align:center\">Remunerado</td>";
	}

	/** (non-Javadoc)
	 * @see br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder#getItemView()
	 */
	public String getItemView() {
		return "  <td>" + getDisciplina().getCodigo() + " - "
		+ getDisciplina().getNome() + "</td>" + "  <td style=\"text-align:center\">"
		+ getTurma().getAnoPeriodo() + "</td>" + "  <td style=\"text-align:right\">" + getTurma().getCodigo() + "</td>"
		+ "  <td style=\"text-align:right\">" + getChDedicadaPeriodo() + "</td>" + "  <td style=\"text-align:center\">Não</td>";
	}

	/** (non-Javadoc)
	 * @see br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder#getItens()
	 */
	@Transient
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("turma.disciplina.detalhes.nome", "nomeDisciplina");
		itens.put("turma.id", "idTurma");
		itens.put("turma.ano", "anoTurma");
		itens.put("turma.periodo", "periodoTurma");
		itens.put("turma.codigo", "codigoTurma");
		itens.put("turma.disciplina.codigo", "disciplinaCodigo");
		itens.put("turma.disciplina.nivel", "disciplinaNivel");
		itens.put("chDedicadaPeriodo", null);
		return itens;
	}

	/** (non-Javadoc)
	 * @see br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder#getQtdBase()
	 */
	public float getQtdBase() {
		return (float) UFRNUtils.truncateSemArrendondar( (float) getChDedicadaPeriodo() / 15 , 2);
	}

	/**
	 * Seta uma nova turma/disciplina caso atualmente seja null 
	 */
	public void popularDisciplina() {
		if (this.getTurma() == null) {
			this.setTurma(new Turma());
		}
		if (this.getDisciplina() == null) {
			this.getTurma().setDisciplina(new ComponenteCurricular());
		}
	}

	/**
	 * Seta o id da turma criando uma nova, caso não exista.
	 * 
	 * @param id
	 */
	public void setIdTurma(Integer id) {
		popularDisciplina();
		this.getTurma().setId(id);
	}
	
	/**
	 * Seta o ano da turma criando uma nova, caso não exista.
	 * 
	 * @param ano
	 */
	public void setAnoTurma(Integer ano) {
		popularDisciplina();
		this.getTurma().setAno(ano);
	}

	/**
	 * Seta o período da turma criando uma nova, caso não exista.
	 * 
	 * @param periodo
	 */
	public void setPeriodoTurma(Integer periodo) {
		popularDisciplina();
		this.getTurma().setPeriodo(periodo);
	}

	/**
	 * Seta o código da turma criando uma nova, caso não exista.
	 * 
	 * @param codigo
	 */
	public void setCodigoTurma(String codigo) {
		popularDisciplina();
		this.getTurma().setCodigo(codigo);
	}

	/**
	 * Seta o nome da disciplina criando uma nova, caso não exista.
	 * @param nome
	 */
	public void setNomeDisciplina(String nome) {
		popularDisciplina();
		this.getDisciplina().getDetalhes().setNome(nome);
	}

	/**
	 * Seta o código da disciplina criando uma nova, caso não exista.
	 * 
	 * @param codigo
	 */
	public void setDisciplinaCodigo(String codigo) {
		popularDisciplina();
		this.getDisciplina().setCodigo(codigo);
	}
	
	/**
	 * Seta o nível da disciplina criando uma nova, caso não exista.
	 * 
	 * @param nivel
	 */
	public void setDisciplinaNivel(Character nivel) {
		popularDisciplina();
		this.getDisciplina().setNivel(nivel);
	}

	/** Retorna o componente curricular que o docente está lecionando na turma.
	 * @return
	 */
	public ComponenteCurricular getDisciplina() {
		if ( getTurma() == null ) return null;
		return getTurma().getDisciplina();
	}
	
	/** Retorna o primeiro nome do docente.
	 * @return
	 */
	public String getPrimeiroNomeDocente() {
		if (getDocente() == null || getDocente().getId() == 0)
			return getDocenteExterno().getPessoa().getPrimeiroNome();
		else
			return getDocente().getPessoa().getPrimeiroNome();
	}

	/** Compara os {@link DocenteTurma} de acordo com seus nomes. */
	public int compareTo(DocenteTurma other) {
		if( other.getDocenteNome() != null && this.getDocenteNome() != null )
			return this.getDocenteNome().compareTo( other.getDocenteNome() );
		return 0;
	}

	public List<HorarioDocente> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<HorarioDocente> horarios) {
		this.horarios = horarios;
	}
		
	/** Retorna uma representação textual do horário do docente na turma.
	 * @return
	 */
	public String getDescricaoHorario(){
		// agrupa os horários por período, tipo e ordem.
		Map<String, Map<Short, Map<String, List<Short>>>> mapa = new HashMap<String, Map<Short, Map<String, List<Short>>>>();
		if (!ValidatorUtil.isEmpty(horarios)) {
			for (HorarioDocente hd : horarios) {
				String periodo = Formatador.getInstance().formatarData(hd.getDataInicio()) + " - " + Formatador.getInstance().formatarData(hd.getDataFim());
				Map<Short, Map<String, List<Short>>> grupoTipo = mapa.get(periodo);
				if (grupoTipo == null) grupoTipo = new HashMap<Short, Map<String,List<Short>>>();
				Map<String, List<Short>> grupoDia = grupoTipo.get(hd.getHorario().getTipo());
				if (grupoDia == null) grupoDia = new HashMap<String, List<Short>>();
				String listaDias = String.valueOf(hd.getDia());
				List<Short> ordens = grupoDia.get(listaDias);
				if (ordens == null) ordens = new ArrayList<Short>();
				if (!ordens.contains(hd.getHorario().getOrdem())) ordens.add(hd.getHorario().getOrdem());
				grupoDia.put(listaDias, ordens);
				grupoTipo.put(hd.getHorario().getTipo(), grupoDia);
				mapa.put(periodo, grupoTipo);
			}
		}
		// percorre o mapa agrupando os dias que são do mesmo tipo e possuem mesma ordem 
		for (String periodo : mapa.keySet()) {
			Map<Short, Map<String, List<Short>>> grupoTipo = mapa.get(periodo);
			for (Short tipo : grupoTipo.keySet()) {
				Map<String, List<Short>> grupoDia = grupoTipo.get(tipo);
				List<String> remover = null;
				do {
					remover = new ArrayList<String>();
					for (String dia : grupoDia.keySet()) {
						List<Short> ordens = grupoDia.get(dia);
						if (ordens == null) break;
						for(String outroDia : grupoDia.keySet()){
							if (outroDia == null) break;
							List<Short> outrasOrdens = grupoDia.get(outroDia);
							if (!dia.equals(outroDia) && ordens.equals(outrasOrdens)){
								// merge dos dias, ordenando
								List<Character> merge = new ArrayList<Character>();
								for (char c : dia.toCharArray())
									merge.add(Character.valueOf(c));
								for (char c : outroDia.toCharArray())
									merge.add(Character.valueOf(c));
								Collections.sort(merge);
								StringBuilder str = new StringBuilder();
								for (Character c : merge)
									str.append(c);
								// agrupa
								grupoDia.put(str.toString(), ordens);
								remover.add(outroDia);
								remover.add(dia);
								break;
							}
						}
						if (remover.size() > 0) break;
					}
					for (String dia : remover){
						grupoDia.remove(dia);
					}
				} while (remover.size() > 0);
			}
		}
		// monta a string do horário
		StringBuilder str = new StringBuilder();
		for (String periodo : mapa.keySet()) {
			Map<Short, Map<String, List<Short>>> grupoTipo = mapa.get(periodo);
			for (Short tipo : grupoTipo.keySet()) {
				Map<String, List<Short>> grupoDia = grupoTipo.get(tipo);
				for (String dia : grupoDia.keySet()) {
					List<Short> ordens = grupoDia.get(dia);
					StringBuilder strOrdens = new StringBuilder();
					for (Short ordem : ordens) {
						strOrdens.append(ordem);
					}
					str.append(" ");
					str.append(dia);
					switch (tipo) {
					case Horario.MANHA: str.append("M");break;
					case Horario.TARDE: str.append("T");break;
					case Horario.NOITE: str.append("N");break;
					}
					str.append(strOrdens);
				}
			}
			str.append(" (");
			str.append(periodo);
			str.append(")");
		}
		return str.toString();
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(getDocenteNome()).append(": ");
		if (turma != null) {
			if (turma.getDisciplina() != null)
				str.append(turma.getDisciplina().getCodigo());
			if (turma.getCodigo() != null)
				str.append(" - T").append(turma.getCodigo());
		}
		return str.toString(); 
	}

	public void setChResidenciaSemTurma(boolean chResidenciaSemTurma) {
		this.chResidenciaSemTurma = chResidenciaSemTurma;
	}

	public boolean isChResidenciaSemTurma() {
		return chResidenciaSemTurma;
	}

	public void setChDedicadaSemana(Double chDedicadaSemana) {
		this.chDedicadaSemana = chDedicadaSemana;
	}

	public Double getChDedicadaSemana() {
		return chDedicadaSemana;
	}

	public Integer getGrupoDocente() {
		return grupoDocente;
	}

	public void setGrupoDocente(Integer grupoDocente) {
		this.grupoDocente = grupoDocente;
	}

}