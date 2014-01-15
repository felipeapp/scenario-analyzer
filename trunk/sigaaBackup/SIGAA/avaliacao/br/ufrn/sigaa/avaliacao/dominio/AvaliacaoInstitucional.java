/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * Registra os dados das avaliações institucionais 
 * realizadas pelos discentes e docentes.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="avaliacao_institucional", schema="avaliacao")
public class AvaliacaoInstitucional implements PersistDB {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="avaliacao_seq")
	@GenericGenerator(name="avaliacao_seq", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="avaliacao_seq", value="avaliacao.avaliacao_seq") })
	private int id;
	
	/** Discente que fez a avaliação.*/
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_discente")
	private Discente discente;
	
	/** Docente que fez a avaliação. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_docente")
	private Servidor servidor;
	
	/** Docente externo fez a avaliação. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_docente_externo")
	private DocenteExterno docenteExterno;
	
	/** Ano da avaliação. */
	private int ano;
	
	/** Período da avaliação. */
	private int periodo;
	
	/** Indica se o discente concluiu a avaliação. */
	private boolean finalizada;
	
	/** Lista de respostas dada pelo discente na avaliação. */
	@OneToMany(mappedBy="avaliacao", cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<RespostaPergunta> respostas;
	
	/** Lista de observações acerca do trancamento do discente nas turmas. */
	@OneToMany(mappedBy="avaliacao", cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ObservacoesTrancamento> trancamentos;
	
	/** Lista de observações do discente acerca das turmas. */
	@OneToMany(mappedBy="avaliacao", cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ObservacoesDocenteTurma> observacoesDocenteTurma;
	
	/** Lista de observações do discente bolsista de docência assistida. */
	@OneToMany(mappedBy="avaliacao", cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ObservacoesDocenciaAssistida> observacoesDocenciaAssistida;
	
	/** Observações gerais do docente acerca da avaliação institucional. */
	private String observacoes;
	
	/** Registro de entrada do usuário que fez a avaliação. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	private RegistroEntrada criadoPor;
	
	/** Formulário de Avaliação Institucional ao qual esta avaliação está vinculada. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_formulario_avaliacao")
	private FormularioAvaliacaoInstitucional formulario;

	/** Tutor de pólo que respondeu esta Avaliação. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_tutor_orientador")
	private TutorOrientador tutorOrientador;
	
	/** Construtor padrão. */
	public AvaliacaoInstitucional() {
	}
	
	/** Construtor parametrizado. */
	public AvaliacaoInstitucional(int idAvaliacao) {
		setId(idAvaliacao);
	}

	/** Retorna a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o discente que fez a avaliação.
	 * @return Discente que fez a avaliação.
	 */
	public Discente getDiscente() {
		return discente;
	}

	/** Seta o discente que fez a avaliação.
	 * @param discente Discente que fez a avaliação.
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

	/** Retorna o ano da avaliação. 
	 * @return Ano da avaliação. 
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano da avaliação. 
	 * @param ano Ano da avaliação. 
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o período da avaliação. 
	 * @return Período da avaliação. 
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o período da avaliação. 
	 * @param periodo Período da avaliação. 
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Retorna a lista de respostas dada pelo discente na avaliação. 
	 * @return Lista de respostas dada pelo discente na avaliação. 
	 */
	public List<RespostaPergunta> getRespostas() {
		return respostas;
	}

	/** Seta a lista de respostas dada pelo discente na avaliação.
	 * @param respostas Lista de respostas dada pelo discente na avaliação. 
	 */
	public void setRespostas(List<RespostaPergunta> respostas) {
		this.respostas = respostas;
	}

	/** Retorna as observações gerais do docente acerca da avaliação institucional. 
	 * @return Observações gerais do docente acerca da avaliação institucional. 
	 */
	public String getObservacoes() {
		return observacoes;
	}

	/** Seta as observações gerais do docente acerca da avaliação institucional. 
	 * @param observacoes Observações gerais do docente acerca da avaliação institucional. 
	 */
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	/** Retorna o registro de entrada do usuário que fez a avaliação. 
	 * @return Registro de entrada do usuário que fez a avaliação. 
	 */
	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	/** Seta o registro de entrada do usuário que fez a avaliação. 
	 * @param criadoPor Registro de entrada do usuário que fez a avaliação. 
	 */
	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	/** Indica se o discente concluiu a avaliação. 
	 * @return Caso true, indica que o discente concluiu a avaliação. 
	 */
	public boolean isFinalizada() {
		return finalizada;
	}

	/** Seta se o discente concluiu a avaliação. 
	 * @param finalizada True, se o discente concluiu a avaliação. 
	 */
	public void setFinalizada(boolean finalizada) {
		this.finalizada = finalizada;
	}

	/** Adiciona a resposta à lista de respostas.
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
						// para a avaliação da docencencia assistida
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
	
	/** Retorna as observações de um aluno para o DocenteTurma especificado.
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
	
	/** Retorna as observações de um aluno para o DocenteTurma especificado.
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
	
	/** Retorna as observações acerca do trancamento para uma turma especificada.
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

	/** Retorna a lista de observações acerca do trancamento do discente nas turmas. 
	 * @return Lista de observações acerca do trancamento do discente nas turmas. 
	 */
	public List<ObservacoesTrancamento> getTrancamentos() {
		return trancamentos;
	}

	/** Seta a lista de observações acerca do trancamento do discente nas turmas. 
	 * @param trancamentos Lista de observações acerca do trancamento do discente nas turmas. 
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

	/** Retorna a lista de observações do discente acerca das turmas. 
	 * @return Lista de observações do discente acerca das turmas. 
	 */
	public List<ObservacoesDocenteTurma> getObservacoesDocenteTurma() {
		return observacoesDocenteTurma;
	}

	/** Seta  a lista de observações do discente acerca das turmas.
	 * @param observacoesDocenteTurma Lista de observações do discente acerca das turmas. 
	 */
	public void setObservacoesDocenteTurma(List<ObservacoesDocenteTurma> observacoesDocenteTurma) {
		this.observacoesDocenteTurma = observacoesDocenteTurma;
	}

	/** Retorna o Formulário de Avaliação Institucional ao qual esta avaliação está vinculada. 
	 * @return
	 */
	public FormularioAvaliacaoInstitucional getFormulario() {
		return formulario;
	}

	/** Seta o Formulário de Avaliação Institucional ao qual esta avaliação está vinculada.
	 * @param formulario
	 */
	public void setFormulario(FormularioAvaliacaoInstitucional formulario) {
		this.formulario = formulario;
	}

	/** Retorna a lista de observações do discente bolsista de docência assistida.
	 * @return
	 */
	public List<ObservacoesDocenciaAssistida> getObservacoesDocenciaAssistida() {
		return observacoesDocenciaAssistida;
	}

	/** Seta a lista de observações do discente bolsista de docência assistida.
	 * @param observacoesDocenciaAssistida
	 */
	public void setObservacoesDocenciaAssistida(
			List<ObservacoesDocenciaAssistida> observacoesDocenciaAssistida) {
		this.observacoesDocenciaAssistida = observacoesDocenciaAssistida;
	}
	
	/** Retorna as observações de um aluno para uma docência assistida.
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