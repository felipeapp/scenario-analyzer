/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 29/04/2008
 */
package br.ufrn.sigaa.avaliacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Registra os dados das avalia��es institucionais 
 * realizadas pelos discentes e docentes.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="avaliacao_institucional", schema="avaliacao")
public class AvaliacaoInstitucional implements PersistDB {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="avaliacao_seq")
	@GenericGenerator(name="avaliacao_seq", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="avaliacao_seq", value="avaliacao.avaliacao_seq") })
	private int id;
	
	/** Discente que fez a avalia��o.*/
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_discente")
	private Discente discente;
	
	/** Docente que fez a avalia��o. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_docente")
	private Servidor servidor;
	
	/** Docente externo fez a avalia��o. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_docente_externo")
	private DocenteExterno docenteExterno;
	
	/** Ano da avalia��o. */
	private int ano;
	
	/** Per�odo da avalia��o. */
	private int periodo;
	
	/** Indica se o discente concluiu a avalia��o. */
	private boolean finalizada;
	
	/** Lista de respostas dada pelo discente na avalia��o. */
	@OneToMany(mappedBy="avaliacao", cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<RespostaPergunta> respostas;
	
	/** Lista de observa��es acerca do trancamento do discente nas turmas. */
	@OneToMany(mappedBy="avaliacao", cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ObservacoesTrancamento> trancamentos;
	
	/** Lista de observa��es do discente acerca das turmas. */
	@OneToMany(mappedBy="avaliacao", cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ObservacoesDocenteTurma> observacoesDocenteTurma;
	
	/** Lista de observa��es do discente bolsista de doc�ncia assistida. */
	@OneToMany(mappedBy="avaliacao", cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ObservacoesDocenciaAssistida> observacoesDocenciaAssistida;
	
	/** Observa��es gerais do docente acerca da avalia��o institucional. */
	private String observacoes;
	
	/** Registro de entrada do usu�rio que fez a avalia��o. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	private RegistroEntrada criadoPor;
	
	/** Formul�rio de Avalia��o Institucional ao qual esta avalia��o est� vinculada. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_formulario_avaliacao")
	private FormularioAvaliacaoInstitucional formulario;

	/** Tutor de p�lo que respondeu esta Avalia��o. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_tutor_orientador")
	private TutorOrientador tutorOrientador;
	
	/** Construtor padr�o. */
	public AvaliacaoInstitucional() {
	}
	
	/** Construtor parametrizado. */
	public AvaliacaoInstitucional(int idAvaliacao) {
		setId(idAvaliacao);
	}

	/** Retorna a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o discente que fez a avalia��o.
	 * @return Discente que fez a avalia��o.
	 */
	public Discente getDiscente() {
		return discente;
	}

	/** Seta o discente que fez a avalia��o.
	 * @param discente Discente que fez a avalia��o.
	 */
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/** Retorna o docente que foi avaliado. 
	 * @return Docente que foi avaliado. 
	 */
	public Servidor getServidor() {
		return servidor;
	}

	/** Retorna o docente que foi avaliado.  
	 * @param servidor Docente que foi avaliado. 
	 */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	/** Retorna o ano da avalia��o. 
	 * @return Ano da avalia��o. 
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano da avalia��o. 
	 * @param ano Ano da avalia��o. 
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o per�odo da avalia��o. 
	 * @return Per�odo da avalia��o. 
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o per�odo da avalia��o. 
	 * @param periodo Per�odo da avalia��o. 
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Retorna a lista de respostas dada pelo discente na avalia��o. 
	 * @return Lista de respostas dada pelo discente na avalia��o. 
	 */
	public List<RespostaPergunta> getRespostas() {
		return respostas;
	}

	/** Seta a lista de respostas dada pelo discente na avalia��o.
	 * @param respostas Lista de respostas dada pelo discente na avalia��o. 
	 */
	public void setRespostas(List<RespostaPergunta> respostas) {
		this.respostas = respostas;
	}

	/** Retorna as observa��es gerais do docente acerca da avalia��o institucional. 
	 * @return Observa��es gerais do docente acerca da avalia��o institucional. 
	 */
	public String getObservacoes() {
		return observacoes;
	}

	/** Seta as observa��es gerais do docente acerca da avalia��o institucional. 
	 * @param observacoes Observa��es gerais do docente acerca da avalia��o institucional. 
	 */
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	/** Retorna o registro de entrada do usu�rio que fez a avalia��o. 
	 * @return Registro de entrada do usu�rio que fez a avalia��o. 
	 */
	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	/** Seta o registro de entrada do usu�rio que fez a avalia��o. 
	 * @param criadoPor Registro de entrada do usu�rio que fez a avalia��o. 
	 */
	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	/** Indica se o discente concluiu a avalia��o. 
	 * @return Caso true, indica que o discente concluiu a avalia��o. 
	 */
	public boolean isFinalizada() {
		return finalizada;
	}

	/** Seta se o discente concluiu a avalia��o. 
	 * @param finalizada True, se o discente concluiu a avalia��o. 
	 */
	public void setFinalizada(boolean finalizada) {
		this.finalizada = finalizada;
	}

	/** Adiciona a resposta � lista de respostas.
	 * @param resp
	 */
	public void addResposta(RespostaPergunta resp) {
		respostas.add(resp);
	}
	
	/** Retorna a resposta dada a pergunta especificada.
	 * @param pergunta
	 * @param idDocenteTurma
	 * @param alternativa
	 * @return
	 */
	public RespostaPergunta getResposta(Integer pergunta, Integer idDocenteTurma, Integer alternativa) {
		if (respostas != null) {
			for (RespostaPergunta resp : respostas) {
				if (resp.getPergunta().getId() == pergunta) {
					if (formulario != null && formulario.isAvaliacaoDocenciaAssistida()) {
						// para a avalia��o da docencencia assistida
						if (idDocenteTurma != null && resp.getTurmaDocenciaAssistida() != null) {
							if (idDocenteTurma == resp.getTurmaDocenciaAssistida().getId() && 
									(alternativa == null || alternativa != null && alternativa.equals(resp.getResposta())) ) {
								return resp;
							} 
						} else {
							if (alternativa != null && alternativa.equals(resp.getResposta()) ||
										alternativa == null && resp.getResposta() == null ) {
								return resp;
							}
						}
					} else {
						// para o modelo antigo
						if (idDocenteTurma != null && idDocenteTurma == resp.getDocenteTurma().getId()) {
							return resp;
						}
						if (alternativa != null && alternativa.equals(resp.getResposta())) {
							return resp;
						}
						if (resp.getDocenteTurma() == null && !resp.getPergunta().isMultiplaEscolha() && !resp.getPergunta().isEscolhaUnica()) {
							return resp;
						}
					}
				}
			}
		}
		return null;
	}
	
	/** Retorna as observa��es de um aluno para o DocenteTurma especificado.
	 * @param idDocenteTurma ID do DocenteTurma
	 * @return
	 */
	public ObservacoesDocenteTurma getObservacaoSimples(Integer idDocenteTurma) {
		if (!isEmpty(observacoesDocenteTurma)) {
			for (ObservacoesDocenteTurma obs : observacoesDocenteTurma) {
				if (!isEmpty(obs.getDocenteTurma()) && obs.getDocenteTurma().getId() == idDocenteTurma) {
					return obs;
				}
			}
		}
		return null;
	}
	
	/** Retorna as observa��es de um aluno para o DocenteTurma especificado.
	 * @param idDocenteTurma
	 * @return
	 */
	public ObservacoesDocenteTurma getObservacaoMultipla(Integer idDocenteTurma) {
		if (!isEmpty(observacoesDocenteTurma)) {
			for (ObservacoesDocenteTurma obs : observacoesDocenteTurma) {
				if (!isEmpty(obs.getTurma()) && obs.getTurma().getId() == idDocenteTurma) {
					return obs;
				}
			}
		}
		return null;
	}
	
	/** Retorna as observa��es acerca do trancamento para uma turma especificada.
	 * @param idTurma
	 * @return
	 */
	public ObservacoesTrancamento getTrancamento(Integer idTurma) {
		if (idTurma != null && trancamentos != null) {
			for (ObservacoesTrancamento ot : trancamentos) {
				if (ot.getTurma().getId() == idTurma) {
					return ot;
				}
			}
		}
		return null;
	}

	/** Retorna a lista de observa��es acerca do trancamento do discente nas turmas. 
	 * @return Lista de observa��es acerca do trancamento do discente nas turmas. 
	 */
	public List<ObservacoesTrancamento> getTrancamentos() {
		return trancamentos;
	}

	/** Seta a lista de observa��es acerca do trancamento do discente nas turmas. 
	 * @param trancamentos Lista de observa��es acerca do trancamento do discente nas turmas. 
	 */
	public void setTrancamentos(List<ObservacoesTrancamento> trancamentos) {
		this.trancamentos = trancamentos;
	}

	/** Retorna o docente externo que foi avaliado. 
	 * @return Docente externo que foi avaliado. 
	 */
	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	/** Seta o docente externo que foi avaliado.
	 * @param docenteExterno Docente externo que foi avaliado. 
	 */
	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	/** Retorna a lista de observa��es do discente acerca das turmas. 
	 * @return Lista de observa��es do discente acerca das turmas. 
	 */
	public List<ObservacoesDocenteTurma> getObservacoesDocenteTurma() {
		return observacoesDocenteTurma;
	}

	/** Seta  a lista de observa��es do discente acerca das turmas.
	 * @param observacoesDocenteTurma Lista de observa��es do discente acerca das turmas. 
	 */
	public void setObservacoesDocenteTurma(List<ObservacoesDocenteTurma> observacoesDocenteTurma) {
		this.observacoesDocenteTurma = observacoesDocenteTurma;
	}

	/** Retorna o Formul�rio de Avalia��o Institucional ao qual esta avalia��o est� vinculada. 
	 * @return
	 */
	public FormularioAvaliacaoInstitucional getFormulario() {
		return formulario;
	}

	/** Seta o Formul�rio de Avalia��o Institucional ao qual esta avalia��o est� vinculada.
	 * @param formulario
	 */
	public void setFormulario(FormularioAvaliacaoInstitucional formulario) {
		this.formulario = formulario;
	}

	/** Retorna a lista de observa��es do discente bolsista de doc�ncia assistida.
	 * @return
	 */
	public List<ObservacoesDocenciaAssistida> getObservacoesDocenciaAssistida() {
		return observacoesDocenciaAssistida;
	}

	/** Seta a lista de observa��es do discente bolsista de doc�ncia assistida.
	 * @param observacoesDocenciaAssistida
	 */
	public void setObservacoesDocenciaAssistida(
			List<ObservacoesDocenciaAssistida> observacoesDocenciaAssistida) {
		this.observacoesDocenciaAssistida = observacoesDocenciaAssistida;
	}
	
	/** Retorna as observa��es de um aluno para uma doc�ncia assistida.
	 * @param idDocenteTurma ID do DocenteTurma
	 * @return
	 */
	public ObservacoesDocenciaAssistida getObservacaoDocenciaAssistida(Integer idTurmaDocenciaAssistida) {
		if (!isEmpty(observacoesDocenciaAssistida)) {
			for (ObservacoesDocenciaAssistida obs : observacoesDocenciaAssistida) {
				if (!isEmpty(obs.getTurmaDocenciaAssistida()) && obs.getTurmaDocenciaAssistida().getId() == idTurmaDocenciaAssistida) {
					return obs;
				}
			}
		}
		return null;
	}

	public TutorOrientador getTutorOrientador() {
		return tutorOrientador;
	}

	public void setTutorOrientador(TutorOrientador tutorOrientador) {
		this.tutorOrientador = tutorOrientador;
	}

}