/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 11/06/2008
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * 
 * OBS: Não usei DocenteTurma porque se a turma tiver mais de um professor não iria dá pra saber qual docente faltou.
 * 
 * @author Henrique André
 */
@Entity
@Table(schema = "ensino", name = "dados_aviso_falta")
public class DadosAvisoFalta implements PersistDB, Validatable {

	/** Chave primária dos {@link DadosAvisoFalta}. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
	@Column(name = "id_dados_aviso_falta")
	private int id;

	/** Turma referente aos {@link DadosAvisoFalta}. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_turma")
	private Turma turma;

	/** Docente que teve a falta notificada. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_docente")
	private Servidor docente;

	/** Data da aula que o docente faltou. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_aula")
	private Date dataAula;

	/** Docente externo que teve a falta notificada. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_docente_externo")
	private DocenteExterno docenteExterno;

	/** Lista com os {@link AvisoFaltaDocente} referentes ao {@link DadosAvisoFalta}. */
	@OneToMany (mappedBy = "dadosAvisoFalta", fetch = FetchType.LAZY)
	private List<AvisoFaltaDocente> avisosFalta;
	
	/** Armazena o aviso homologado referente aos {@link DadosAvisoFalta}. */
	@Transient
	private AvisoFaltaDocenteHomologada avisoHomologado;
	
	/** Quantidade de {@link AvisoFaltaDocente} recebidos. */
	@Transient
	private int qtdAvisos;
	
	/**
	 * Indica se o docente é do quadro ou docente externo
	 * 
	 * @return
	 */
	public boolean isServidor() {
		return docente != null;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public Date getDataAula() {
		return dataAula;
	}

	public void setDataAula(Date dataAula) {
		this.dataAula = dataAula;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	public List<AvisoFaltaDocente> getAvisosFalta() {
		return avisosFalta;
	}

	public void setAvisosFalta(List<AvisoFaltaDocente> avisosFalta) {
		this.avisosFalta = avisosFalta;
	}

	public AvisoFaltaDocenteHomologada getAvisoHomologado() {
		return avisoHomologado;
	}

	public void setAvisoHomologado(AvisoFaltaDocenteHomologada avisoHomologado) {
		this.avisoHomologado = avisoHomologado;
	}

	public int getQtdAvisos() {
		return qtdAvisos;
	}

	public void setQtdAvisos(int qtdAvisos) {
		this.qtdAvisos = qtdAvisos;
	}

	/**
	 * Retorna o id do docente da turma
	 * 
	 * @return
	 */
	public int getIdProfessor() {
		if (isServidor())
			return docente.getId();
		return docenteExterno.getId();
	}
	
	/**
	 * Retorna o id do docente da turma
	 * 
	 * @return
	 */
	public String getDocenteNome() {
		if (isServidor())
			return docente.getNome();
		return docenteExterno.getNome();
	}	
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		if (docente != null)
			ValidatorUtil.validateRequiredId(docente.getId(), "Professor", erros);
		else 
			ValidatorUtil.validateRequiredId(docenteExterno.getId(), "Professor", erros);
		ValidatorUtil.validateRequiredId(turma.getId(), "Disciplina", erros);
		ValidatorUtil.validateRequired(dataAula, "Data da Aula", erros);
		return erros;
	}

	/**
	 * Retorna verdadeiro caso seja possivel homologar o aviso de falta, ou seja, caso ele ainda não tenha
	 * sido homologado ou, mesmo homologado, tenha sido estornado.
	 * 
	 * @return
	 */
	public boolean isPassivelHomologacao(){
		if(ValidatorUtil.isEmpty(avisoHomologado) || avisoHomologado.getMovimentacao().getId() == MovimentacaoAvisoFaltaHomologado.ESTORNADO.getId())
			return true;
		return false;
	}
	
	/**
	 * Retorna o status atual do aviso de falta, caso homologado retorna a respectiva movimentação e,
	 * caso contrário, 'NÃO ANALISADO'.
	 * @return
	 * @throws DAOException 
	 */
	public String getStatusAviso() throws DAOException{
		if(avisoHomologado == null)
			return "NÃO ANALISADO";
		return avisoHomologado.getMovimentacao().getDescricao();
	}
}
