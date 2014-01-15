package br.ufrn.sigaa.ensino.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ParticipacaoEnade;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.pessoa.dominio.Municipio;



	/**
	 * Encapsula os parâmetros da pesquisa
	 * 
	 * @author henrique
	 */
	public class ParametrosBusca {
		/** Informação da matricula a ser utilizada na busca */
		private Long matricula;
		/** Informação do nome a ser utilizada na busca */
		private String nome;
		/** Informação do tipo a ser utilizada na busca */
		private int tipo;
		/** Informação da forma de ingresso a ser utilizada na busca */
		private FormaIngresso formaIngresso = new FormaIngresso();
		/** Informação do ano de ingresso a ser utilizada na busca */
		private Integer anoIngresso;
		/** Informação do período de ingresso a ser utilizada na busca */
		private Integer periodoIngresso;
		/** Informação do tipo de saída a ser utilizada na busca */
		private TipoMovimentacaoAluno tipoSaida = new TipoMovimentacaoAluno();
		/** Informação do ano de saída a ser utilizada na busca */
		private Integer anoSaida;
		/** Informação do período de saída a ser utilizada na busca */
		private Integer periodoSaida;
		/** Informação do status de saída a ser utilizada na busca */
		private int status;
		/** Informação do nível de saída a ser utilizada na busca */
		private char nivel;
		/** Informação do ano dos discentes matriculados a ser utilizada na busca */
		private Integer matriculadoEmAno;
		/** Informação do período dos discentes matriculados a ser utilizada na busca */
		private Integer matriculadoEmPeriodo;
		/** Informação dos não matriculados em um ano a ser utilizada na busca */
		private Integer naoMatriculadoEmAno;
		/** Informação dos não matriculados em um período a ser utilizada na busca */
		private Integer naoMatriculadoEmPeriodo;
		/** Informação do centro dos discentes a ser utilizada na busca */
		private Unidade centro = new Unidade();
		/** Informação do programa dos discentes a ser utilizada na busca */
		private Unidade programa = new Unidade();
		/** Informação da escola dos discentes a ser utilizada na busca */
		private Unidade escola = new Unidade();
		/** Informação do estado dos discentes a ser utilizada na busca */
		private UnidadeFederativa estado = new UnidadeFederativa();
		/** Informação da didade dos discentes a ser utilizada na busca */
		private Municipio cidade = new Municipio();
		/** Informação do curso de graduação dos discentes a ser utilizada na busca */
		private Curso cursoGraduacao = new Curso();
		/** Informação do curso de Lato dos discentes a ser utilizada na busca */
		private Curso cursoLato = new Curso();
		/** Informação do curso de Técnico dos discentes a ser utilizada na busca */
		private Curso cursoTecnico = new Curso();
		/** Informação do turno dos discentes a ser utilizada na busca */
		private Turno turno = new Turno();
		/** Informação da modalidade de educação dos discentes a ser utilizada na busca */
		private ModalidadeEducacao modalidade = new ModalidadeEducacao();
		/** Informação da matriz curricular dos discentes a ser utilizada na busca */
		private MatrizCurricular matrizCurricular = new MatrizCurricular();
		/** Informação da curricula dos discentes a ser utilizada na busca */
		private Curriculo curriculo = new Curriculo();
		/** Informação do polo dos discentes a ser utilizada na busca */
		private Polo polo = new Polo();
		/** Informação da participação do Enade dos discentes a ser utilizada na busca */
		private ParticipacaoEnade participacaoEnade = new ParticipacaoEnade();
		/** Informação da turma de entrada dos discentes a ser utilizada na busca */
		private TurmaEntradaTecnico turmaEntrada = new TurmaEntradaTecnico();
		/** Informação da especialidade da Turma de entrada dos discentes a ser utilizada na busca */
		private EspecializacaoTurmaEntrada especialidade = new EspecializacaoTurmaEntrada();
		/** Informação do prazo máximo ano dos discentes a ser utilizada na busca */
		private Integer prazoMaximoAno;
		/** Informação do prazo máximo período dos discentes a ser utilizada na busca */
		private Integer prazoMaximoPeriodo;
		/** Informação do período de trancamento dos discentes a ser utilizada na busca */
		private Integer trancadoNoPeriodo;
		/** Informação do motivo do desligamento do aluno a ser utilizada na busca */
		private int motivoDesligamentoAluno;
		/** Informação do período a utilizada na busca */
		private Integer noPeriodo;
		/** Informação do ano de trancamento do aluno a utilizada na busca */
		private Integer trancadoNoAno;
		/** Informação do prazo esgotado do aluno a utilizada na busca */
		private Date prazoEsgotadoAte;
		/** Informação da idade do aluno a utilizada na busca */
		private Integer idadeDe;
		/** Informação da idade máxima do aluno a utilizada na busca */
		private Integer idadeAte;
		/** Informação do sexo do aluno a utilizada na busca */
		private String sexo;
		/** Informação do convênio do aluno a utilizada na busca */
		private ConvenioAcademico convenio = new ConvenioAcademico();
		/** Informação do tipo de necessidade Especial do aluno a utilizada na busca */
		private TipoNecessidadeEspecial tipoNecessidadeEspecial = new TipoNecessidadeEspecial();
		/** Opção em que o discente foi aprovado no Vestibular (1º ou 2º Opção). */
		private Integer opcaoAprovacao;
		/** Expressão utilizada para restringir os valores dos índices acadêmicos dos alunos. */
		private String indicesAcademicos;
		
		public String getIndicesAcademicos() {
			return indicesAcademicos;
		}
		
		public void setIndicesAcademicos(String indicesAcademicos) {
			this.indicesAcademicos = indicesAcademicos;
		}
		
		public Long getMatricula() {
			return matricula;
		}

		public void setMatricula(Long matricula) {
			this.matricula = matricula;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public int getTipo() {
			return tipo;
		}

		public void setTipo(int tipo) {
			this.tipo = tipo;
		}

		public FormaIngresso getFormaIngresso() {
			return formaIngresso;
		}

		public void setFormaIngresso(FormaIngresso formaIngresso) {
			this.formaIngresso = formaIngresso;
		}

		public Integer getAnoIngresso() {
			return anoIngresso;
		}

		public void setAnoIngresso(Integer anoIngresso) {
			this.anoIngresso = anoIngresso;
		}

		public Integer getPeriodoIngresso() {
			return periodoIngresso;
		}

		public void setPeriodoIngresso(Integer periodoIngresso) {
			this.periodoIngresso = periodoIngresso;
		}

		public TipoMovimentacaoAluno getTipoSaida() {
			return tipoSaida;
		}

		public void setTipoSaida(TipoMovimentacaoAluno tipoSaida) {
			this.tipoSaida = tipoSaida;
		}

		public Integer getAnoSaida() {
			return anoSaida;
		}

		public void setAnoSaida(Integer anoSaida) {
			this.anoSaida = anoSaida;
		}

		public Integer getPeriodoSaida() {
			return periodoSaida;
		}

		public void setPeriodoSaida(Integer periodoSaida) {
			this.periodoSaida = periodoSaida;
		}

		public int getStatus() {
			return status;
		}

		public String getStatusDesc() {
			return StatusDiscente.getDescricao(status);
		}
		
		public void setStatus(int status) {
			this.status = status;
		}

		public char getNivel() {
			return nivel;
		}
		
		public String getNivelDesc() {
			return NivelEnsino.getDescricao(nivel);
		}

		public void setNivel(char nivel) {
			this.nivel = nivel;
		}

		public Integer getMatriculadoEmAno() {
			return matriculadoEmAno;
		}

		public void setMatriculadoEmAno(Integer matriculadoEmAno) {
			this.matriculadoEmAno = matriculadoEmAno;
		}

		public Integer getMatriculadoEmPeriodo() {
			return matriculadoEmPeriodo;
		}

		public void setMatriculadoEmPeriodo(Integer matriculadoEmPeriodo) {
			this.matriculadoEmPeriodo = matriculadoEmPeriodo;
		}

		public Integer getNaoMatriculadoEmAno() {
			return naoMatriculadoEmAno;
		}

		public void setNaoMatriculadoEmAno(Integer naoMatriculadoEmAno) {
			this.naoMatriculadoEmAno = naoMatriculadoEmAno;
		}

		public Integer getNaoMatriculadoEmPeriodo() {
			return naoMatriculadoEmPeriodo;
		}

		public void setNaoMatriculadoEmPeriodo(Integer naoMatriculadoEmPeriodo) {
			this.naoMatriculadoEmPeriodo = naoMatriculadoEmPeriodo;
		}

		public Unidade getCentro() {
			return centro;
		}

		public void setCentro(Unidade centro) {
			this.centro = centro;
		}

		public Unidade getPrograma() {
			return programa;
		}

		public void setPrograma(Unidade programa) {
			this.programa = programa;
		}

		public Unidade getEscola() {
			return escola;
		}

		public void setEscola(Unidade escola) {
			this.escola = escola;
		}

		public UnidadeFederativa getEstado() {
			return estado;
		}

		public void setEstado(UnidadeFederativa estado) {
			this.estado = estado;
		}

		public Municipio getCidade() {
			return cidade;
		}

		public void setCidade(Municipio cidade) {
			this.cidade = cidade;
		}

		public Curso getCursoGraduacao() {
			return cursoGraduacao;
		}

		public void setCursoGraduacao(Curso cursoGraduacao) {
			this.cursoGraduacao = cursoGraduacao;
		}

		public Curso getCursoLato() {
			return cursoLato;
		}

		public void setCursoLato(Curso cursoLato) {
			this.cursoLato = cursoLato;
		}

		public Curso getCursoTecnico() {
			return cursoTecnico;
		}

		public void setCursoTecnico(Curso cursoTecnico) {
			this.cursoTecnico = cursoTecnico;
		}

		public Turno getTurno() {
			return turno;
		}

		public void setTurno(Turno turno) {
			this.turno = turno;
		}

		public ModalidadeEducacao getModalidade() {
			return modalidade;
		}

		public void setModalidade(ModalidadeEducacao modalidade) {
			this.modalidade = modalidade;
		}

		public MatrizCurricular getMatrizCurricular() {
			return matrizCurricular;
		}

		public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
			this.matrizCurricular = matrizCurricular;
		}

		public Curriculo getCurriculo() {
			return curriculo;
		}

		public void setCurriculo(Curriculo curriculo) {
			this.curriculo = curriculo;
		}

		public Polo getPolo() {
			return polo;
		}

		public void setPolo(Polo polo) {
			this.polo = polo;
		}

		public ParticipacaoEnade getParticipacaoEnade() {
			return participacaoEnade;
		}

		public void setParticipacaoEnade(ParticipacaoEnade participacaoEnade) {
			this.participacaoEnade = participacaoEnade;
		}

		public TurmaEntradaTecnico getTurmaEntrada() {
			return turmaEntrada;
		}

		public void setTurmaEntrada(TurmaEntradaTecnico turmaEntrada) {
			this.turmaEntrada = turmaEntrada;
		}

		public EspecializacaoTurmaEntrada getEspecialidade() {
			return especialidade;
		}

		public void setEspecialidade(EspecializacaoTurmaEntrada especialidade) {
			this.especialidade = especialidade;
		}

		public Integer getPrazoMaximoAno() {
			return prazoMaximoAno;
		}

		public void setPrazoMaximoAno(Integer prazoMaximoAno) {
			this.prazoMaximoAno = prazoMaximoAno;
		}

		public Integer getPrazoMaximoPeriodo() {
			return prazoMaximoPeriodo;
		}

		public void setPrazoMaximoPeriodo(Integer prazoMaximoPeriodo) {
			this.prazoMaximoPeriodo = prazoMaximoPeriodo;
		}

		public Integer getTrancadoNoPeriodo() {
			return trancadoNoPeriodo;
		}

		public void setTrancadoNoPeriodo(Integer trancadoNoPeriodo) {
			this.trancadoNoPeriodo = trancadoNoPeriodo;
		}

		public int getMotivoDesligamentoAluno() {
			return motivoDesligamentoAluno;
		}

		public void setMotivoDesligamentoAluno(int motivoDesligamentoAluno) {
			this.motivoDesligamentoAluno = motivoDesligamentoAluno;
		}

		public Integer getNoPeriodo() {
			return noPeriodo;
		}

		public void setNoPeriodo(Integer noPeriodo) {
			this.noPeriodo = noPeriodo;
		}

		public Integer getTrancadoNoAno() {
			return trancadoNoAno;
		}

		public void setTrancadoNoAno(Integer trancadoNoAno) {
			this.trancadoNoAno = trancadoNoAno;
		}

		public Date getPrazoEsgotadoAte() {
			return prazoEsgotadoAte;
		}

		public void setPrazoEsgotadoAte(Date prazoEsgotadoAte) {
			this.prazoEsgotadoAte = prazoEsgotadoAte;
		}

		public Integer getIdadeDe() {
			return idadeDe;
		}

		public void setIdadeDe(Integer idadeDe) {
			this.idadeDe = idadeDe;
		}

		public Integer getIdadeAte() {
			return idadeAte;
		}

		public void setIdadeAte(Integer idadeAte) {
			this.idadeAte = idadeAte;
		}

		public String getSexo() {
			return sexo;
		}

		public void setSexo(String sexo) {
			this.sexo = sexo;
		}

		public ConvenioAcademico getConvenio() {
			return convenio;
		}

		public void setConvenio(ConvenioAcademico convenio) {
			this.convenio = convenio;
		}
		
		public TipoNecessidadeEspecial getTipoNecessidadeEspecial() {
			return tipoNecessidadeEspecial;
		}
		
		public void setTipoNecessidadeEspecial(TipoNecessidadeEspecial tipoNecessidadeEspecial) {
			this.tipoNecessidadeEspecial = tipoNecessidadeEspecial;
		}

		/**
		 * Retorna a data de nascimento do discente formadata
		 * @return
		 */
		public String getNascimentoDe() {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.set(Calendar.MONTH, Calendar.JANUARY);
			c.add(Calendar.YEAR, -1 * idadeAte);
			return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		}

		/**
		 * Retorna a data de nascimento máxima formadata
		 * @return
		 */
		public String getNascimentoAte() {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, 31);
			c.set(Calendar.MONTH, Calendar.DECEMBER);
			c.add(Calendar.YEAR, -1 * idadeDe);
			return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		}

		public Integer getOpcaoAprovacao() {
			return opcaoAprovacao;
		}

		public void setOpcaoAprovacao(Integer opcaoAprovacao) {
			this.opcaoAprovacao = opcaoAprovacao;
		}

	}