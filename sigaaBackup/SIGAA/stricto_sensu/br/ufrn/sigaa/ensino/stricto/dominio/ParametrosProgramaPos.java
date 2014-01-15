/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Esta entidade configura par�metros espec�ficos de um programa de p�s-gradua��o
 * @author Victor Hugo
 */
@Entity
@Table(name = "parametros_programa_pos", schema = "stricto_sensu", uniqueConstraints = {})
public class ParametrosProgramaPos implements Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_parametros_programa_pos", nullable = false)
	private int id;

	/**
	 * Programa ao qual estes par�metros pertencem.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_programa")
	private Unidade programa;

	/**
	 * N�mero m�ximo de disciplinas que alunos especiais deste programa pode pagar POR PER�ODO.
	 */
	@Column(name="maxdisciplinasalunoespecial")
	private Integer maxDisciplinasAlunoEspecial = 1;

	/**
	 * N�mero m�ximo de vezes que alunos de DOUTORADO deste programa podem renovar matriculas em atividades de qualifica��o.
	 */
	@Column(name="maxrenovacaoqualificacaodoutorado")
	private Integer maxRenovacaoQualificacaoDoutorado = 0;

	/**
	 * N�mero m�ximo de vezes que alunos de DOUTORADO deste programa podem renovar matr�culas em atividades de defesa.
	 */
	@Column(name="maxrenovacaodefesadoutorado")
	private Integer maxRenovacaoDefesaDoutorado = 0;
	
	/**
	 * N�mero m�ximo de vezes que alunos de MESTRADO deste programa podem renovar matriculas em atividades de qualifica��o.
	 */
	@Column(name="maxrenovacaoqualificacaomestrado")
	private Integer maxRenovacaoQualificacaoMestrado = 0;

	/**
	 * N�mero m�ximo de vezes que alunos de MESTRADO deste programa podem renovar matr�culas em atividades de defesa.
	 */
	@Column(name="maxrenovacaodefesamestrado")
	private Integer maxRenovacaoDefesaMestrado = 0;

	/**
	 * Indica se aluno deste programa pode solicitar matr�cula em uma atividade de defesa sem ter qualifica��o.
	 */
	@Column(name="permitematriculardefesaqualificacao")
	private boolean permiteMatricularDefesaQualificacao = false;

	/**
	 * Indica se alunos especiais podem realizar matricula online.
	 */
	@Column(name="permitematriculaonlineespeciais")
	private boolean permiteMatriculaOnlineEspeciais = false;

	/**
	 * Indica se na inscri��o do processo seletivo o aluno deve selecionar �rea de concentra��o e linha de pesquisa.
	 */
	@Column(name="solicitararealinhaprocessoseletivo")
	private boolean solicitarAreaLinhaProcessoSeletivo = false;

	/**
	 * Indica se na inscri��o do processo seletivo o aluno deve selecionar o orientador.
	 */
	@Column(name="solicitarorientadorprocessoseletivo")
	private boolean solicitarOrientadorProcessoSeletivo = false;
	
	/**
	 * Indica se na inscri��o do processo seletivo o aluno deve selecionar o orientador.
	 */
	@Column(name="maxDiasPassadosProcessoSeletivo")
	private Integer maxDiasPassadosProcessoSeletivo = 30;

	/** Data que o par�metro foi cadastrado. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data da ultima atualiza��o. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;

	/** Registro entrada da ultima atualiza��o. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_alteracao")
	@AtualizadoPor
	private RegistroEntrada registroAlteracao;
	
	/** Indica se o candidato, no ato da inscri��o, dever� enviar um arquivo com o projeto de pesquisa. */
	@Column(name="solicita_projeto_na_inscricao")
	private boolean solicitaProjetoNaInscricao;
	
	/** Indica se permite visualizar as defesas na �rea p�blica do programa. */
	@Column(name = "visualizar_defesa")
	private boolean visualizarDefesa;	

	/** Indica o prazo m�nimo em dias que uma banca de qualifica��o pode ser marcada ap�s seu cadastro */
	@Column(name = "prazo_min_cadastro_banca_qualificacao")
	private Integer prazoMinCadastroBancaQualificacao;	
	
	/** Indica o prazo m�nimo em dias que uma banca de defesa pode ser marcada ap�s seu cadastro */
	@Column(name = "prazo_min_cadastro_banca_defesa")
	private Integer prazoMinCadastroBancaDefesa;	
	
	/** Construtor padr�o. */
	public ParametrosProgramaPos() {
		solicitaProjetoNaInscricao = false;
	}

	/** Construtor parametrizado.
	 * @param programa
	 */
	public ParametrosProgramaPos(Unidade programa) {
		this.programa = programa;
	}

	/** Retorna a chave prim�ria. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o programa ao qual estes par�metros pertencem.
	 * @return
	 */
	public Unidade getPrograma() {
		return programa;
	}

	/** Seta o programa ao qual estes par�metros pertencem.
	 * @param programa
	 */
	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	/** Retorna o n�mero m�ximo de disciplinas que alunos especiais deste programa pode pagar POR PER�ODO.
	 * @return
	 */
	public Integer getMaxDisciplinasAlunoEspecial() {
		return maxDisciplinasAlunoEspecial;
	}

	/** Seta o n�mero m�ximo de disciplinas que alunos especiais deste programa pode pagar POR PER�ODO.
	 * @param maxDisciplinasAlunoEspecial
	 */
	public void setMaxDisciplinasAlunoEspecial(Integer maxDisciplinasAlunoEspecial) {
		this.maxDisciplinasAlunoEspecial = maxDisciplinasAlunoEspecial;
	}

	/** Retorna o n�mero m�ximo de vezes que alunos de DOUTORADO deste programa podem renovar matriculas em atividades de qualifica��o.
	 * @return
	 */
	public Integer getMaxRenovacaoQualificacaoDoutorado() {
		return maxRenovacaoQualificacaoDoutorado;
	}

	/** Seta o n�mero m�ximo de vezes que alunos de DOUTORADO deste programa podem renovar matriculas em atividades de qualifica��o.
	 * @param maxRenovacaoQualificacaoDoutorado
	 */
	public void setMaxRenovacaoQualificacaoDoutorado(Integer maxRenovacaoQualificacaoDoutorado) {
		this.maxRenovacaoQualificacaoDoutorado = maxRenovacaoQualificacaoDoutorado;
	}

	/** Retorna o n�mero m�ximo de vezes que alunos de DOUTORADO deste programa podem renovar matr�culas em atividades de defesa.
	 * @return
	 */
	public Integer getMaxRenovacaoDefesaDoutorado() {
		return maxRenovacaoDefesaDoutorado;
	}

	/** Seta o n�mero m�ximo de vezes que alunos de DOUTORADO deste programa podem renovar matr�culas em atividades de defesa.
	 * @param maxRenovacaoDefesaDoutorado
	 */
	public void setMaxRenovacaoDefesaDoutorado(Integer maxRenovacaoDefesaDoutorado) {
		this.maxRenovacaoDefesaDoutorado = maxRenovacaoDefesaDoutorado;
	}
	
	/** Retorna o n�mero m�ximo de vezes que alunos de MESTRADO deste programa podem renovar matriculas em atividades de qualifica��o.
	 * @return
	 */
	public Integer getMaxRenovacaoQualificacaoMestrado() {
		return maxRenovacaoQualificacaoMestrado;
	}

	/** Seta o n�mero m�ximo de vezes que alunos de MESTRADO deste programa podem renovar matriculas em atividades de qualifica��o.
	 * @param maxRenovacaoQualificacaoMestrado
	 */
	public void setMaxRenovacaoQualificacaoMestrado(Integer maxRenovacaoQualificacaoMestrado) {
		this.maxRenovacaoQualificacaoMestrado = maxRenovacaoQualificacaoMestrado;
	}

	/** Retorna o n�mero m�ximo de vezes que alunos de MESTRADO deste programa podem renovar matr�culas em atividades de defesa.
	 * @return
	 */
	public Integer getMaxRenovacaoDefesaMestrado() {
		return maxRenovacaoDefesaMestrado;
	}

	/** Seta o n�mero m�ximo de vezes que alunos de MESTRADO deste programa podem renovar matr�culas em atividades de defesa.
	 * @param maxRenovacaoDefesaMestrado
	 */
	public void setMaxRenovacaoDefesaMestrado(Integer maxRenovacaoDefesaMestrado) {
		this.maxRenovacaoDefesaMestrado = maxRenovacaoDefesaMestrado;
	}

	/** Indica se alunos especiais podem realizar matricula online.
	 * @return
	 */
	public boolean isPermiteMatriculaOnlineEspeciais() {
		return permiteMatriculaOnlineEspeciais;
	}

	/** Seta se alunos especiais podem realizar matricula online.
	 * @param permiteMatriculaOnlineEspeciais
	 */
	public void setPermiteMatriculaOnlineEspeciais(
			boolean permiteMatriculaOnlineEspeciais) {
		this.permiteMatriculaOnlineEspeciais = permiteMatriculaOnlineEspeciais;
	}

	/** Indica se na inscri��o do processo seletivo o aluno deve selecionar �rea de concentra��o e linha de pesquisa.
	 * @return
	 */
	public boolean isSolicitarAreaLinhaProcessoSeletivo() {
		return solicitarAreaLinhaProcessoSeletivo;
	}

	/** Seta se na inscri��o do processo seletivo o aluno deve selecionar �rea de concentra��o e linha de pesquisa.
	 * @param solicitarAreaLinhaProcessoSeletivo
	 */
	public void setSolicitarAreaLinhaProcessoSeletivo(
			boolean solicitarAreaLinhaProcessoSeletivo) {
		this.solicitarAreaLinhaProcessoSeletivo = solicitarAreaLinhaProcessoSeletivo;
	}

	/** Indica se na inscri��o do processo seletivo o aluno deve selecionar o orientador.
	 * @return
	 */
	public boolean isSolicitarOrientadorProcessoSeletivo() {
		return solicitarOrientadorProcessoSeletivo;
	}

	/** Seta se na inscri��o do processo seletivo o aluno deve selecionar o orientador.
	 * @param solicitarOrientadorProcessoSeletivo
	 */
	public void setSolicitarOrientadorProcessoSeletivo(
			boolean solicitarOrientadorProcessoSeletivo) {
		this.solicitarOrientadorProcessoSeletivo = solicitarOrientadorProcessoSeletivo;
	}

	/** Retorna a data que o par�metro foi cadastrado. 
	 * @return
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a data que o par�metro foi cadastrado.
	 * @param dataCadastro
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Retorna o registro entrada de quem cadastrou. 
	 * @return
	 */
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	/** Seta o registro entrada de quem cadastrou.
	 * @param registroCadastro
	 */
	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	/** Retorna a data da ultima atualiza��o. 
	 * @return
	 */
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	/** Seta a data da ultima atualiza��o.
	 * @param dataAlteracao
	 */
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	/** Retorna o registro entrada da ultima atualiza��o. 
	 * @return
	 */
	public RegistroEntrada getRegistroAlteracao() {
		return registroAlteracao;
	}

	/** Seta o registro entrada da ultima atualiza��o.
	 * @param registroAlteracao
	 */
	public void setRegistroAlteracao(RegistroEntrada registroAlteracao) {
		this.registroAlteracao = registroAlteracao;
	}

	/** Indica se aluno deste programa pode solicitar matr�cula em uma atividade de defesa sem ter qualifica��o.
	 * @return
	 */
	public boolean isPermiteMatricularDefesaQualificacao() {
		return permiteMatricularDefesaQualificacao;
	}

	/** Seta se aluno deste programa pode solicitar matr�cula em uma atividade de defesa sem ter qualifica��o.
	 * @param permiteMatricularDefesaQualificacao
	 */
	public void setPermiteMatricularDefesaQualificacao(
			boolean permiteMatricularDefesaQualificacao) {
		this.permiteMatricularDefesaQualificacao = permiteMatricularDefesaQualificacao;
	}
	
	/** Indica se permite visualizar as defesas na �rea p�blica do programa. 
	 * @return
	 */
	public boolean isVisualizarDefesa() {
		return visualizarDefesa;
	}

	/** Seta se permite visualizar as defesas na �rea p�blica do programa.
	 * @param visualizarDefesa
	 */
	public void setVisualizarDefesa(boolean visualizarDefesa) {
		this.visualizarDefesa = visualizarDefesa;
	}

	/**
	 * Valida os dados: M�ximo de disciplinas de aluno especial; M�ximo de
	 * disciplinas de aluno especial; M�ximo de disciplinas de aluno especial de DOUTORADO;
	 * M�ximo de disciplinas de aluno especial de DOUTORADO; M�ximo de disciplinas de aluno especial de MESTRADO;
	 * M�ximo de disciplinas de aluno especial de MESTRADO; M�ximo de disciplinas de aluno especial;
	 * M�ximo de renova��es da qualifica��o de um aluno de DOUTORADO; M�ximo de renova��es da defesa de um aluno de DOUTORADO; 
	 * M�ximo de renova��es da qualifica��o de um aluno de MESTRADO; M�ximo de renova��es da defesa de um aluno de MESTRADO;
	 * Permite matricula em defesa e qualifica��o no mesmo per�odo; Permite aluno especial realizar matricula on-line; 
	 * Solicitar �rea e linha de pesquisa na inscri��o da sele��o; Solicitar orientador na inscri��o da sele��o.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(programa, "Programa", lista);
		ValidatorUtil.validateRequired(maxDisciplinasAlunoEspecial, "M�ximo de disciplinas de aluno especial", lista);
		ValidatorUtil.validateRequired(maxRenovacaoQualificacaoDoutorado, "M�ximo de disciplinas de aluno especial de DOUTORADO", lista);
		ValidatorUtil.validateRequired(maxRenovacaoDefesaDoutorado, "M�ximo de disciplinas de aluno especial de DOUTORADO", lista);
		ValidatorUtil.validateRequired(maxRenovacaoQualificacaoMestrado, "M�ximo de disciplinas de aluno especial de MESTRADO", lista);
		ValidatorUtil.validateRequired(maxRenovacaoDefesaMestrado, "M�ximo de disciplinas de aluno especial de MESTRADO", lista);
		ValidatorUtil.validateMinValue(maxDisciplinasAlunoEspecial, 0, "M�ximo de disciplinas de aluno especial", lista);
		ValidatorUtil.validateMinValue(maxRenovacaoQualificacaoDoutorado, 0, "M�ximo de renova��es da qualifica��o de um aluno de DOUTORADO", lista);
		ValidatorUtil.validateMinValue(maxRenovacaoDefesaDoutorado, 0, "M�ximo de renova��es da defesa de um aluno de DOUTORADO", lista);
		ValidatorUtil.validateMinValue(maxRenovacaoQualificacaoMestrado, 0, "M�ximo de renova��es da qualifica��o de um aluno de MESTRADO", lista);
		ValidatorUtil.validateMinValue(maxRenovacaoDefesaMestrado, 0, "M�ximo de renova��es da defesa de um aluno de MESTRADO", lista);
		ValidatorUtil.validateMinValue(maxDiasPassadosProcessoSeletivo, 0, "M�ximo de dias que um processo seletivo pode ficar vis�vel", lista);
		ValidatorUtil.validateRequired(permiteMatricularDefesaQualificacao, "Permite matricula em defesa e qualifica��o no mesmo per�odo", lista);
		ValidatorUtil.validateRequired(permiteMatriculaOnlineEspeciais, "Permite aluno especial realizar matricula on-line", lista);
		ValidatorUtil.validateRequired(solicitarAreaLinhaProcessoSeletivo, "Solicitar �rea e linha de pesquisa na inscri��o da sele��o", lista);
		ValidatorUtil.validateRequired(solicitarOrientadorProcessoSeletivo, "Solicitar orientador na inscri��o da sele��o", lista);

		return lista;
	}

	/** Indica se o candidato, no ato da inscri��o, dever� enviar um arquivo com o projeto de pesquisa. 
	 * @return
	 */
	public boolean isSolicitaProjetoNaInscricao() {
		return solicitaProjetoNaInscricao;
	}

	/** Seta se o candidato, no ato da inscri��o, dever� enviar um arquivo com o projeto de pesquisa. 
	 * @param solicitaProjetoNaInscricao
	 */
	public void setSolicitaProjetoNaInscricao(boolean solicitaProjetoNaInscricao) {
		this.solicitaProjetoNaInscricao = solicitaProjetoNaInscricao;
	}

	public Integer getMaxDiasPassadosProcessoSeletivo() {
		return maxDiasPassadosProcessoSeletivo;
	}

	public void setMaxDiasPassadosProcessoSeletivo(
			Integer maxDiasPassadosProcessoSeletivo) {
		this.maxDiasPassadosProcessoSeletivo = maxDiasPassadosProcessoSeletivo;
	}

	public void setPrazoMinCadastroBancaDefesa(
			Integer prazoMinCadastroBancaDefesa) {
		this.prazoMinCadastroBancaDefesa = prazoMinCadastroBancaDefesa;
	}

	public Integer getPrazoMinCadastroBancaDefesa() {
		return prazoMinCadastroBancaDefesa;
	}

	public void setPrazoMinCadastroBancaQualificacao(
			Integer prazoMinCadastroBancaQualificacao) {
		this.prazoMinCadastroBancaQualificacao = prazoMinCadastroBancaQualificacao;
	}

	public Integer getPrazoMinCadastroBancaQualificacao() {
		return prazoMinCadastroBancaQualificacao;
	}
	

}
