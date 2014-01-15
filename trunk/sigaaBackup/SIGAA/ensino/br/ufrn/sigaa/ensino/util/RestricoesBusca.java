package br.ufrn.sigaa.ensino.util;

public class RestricoesBusca {
		/** Indica se a busca vai ser feita em cima da matricula do discente */
		private boolean buscaMatricula;
		/** Indica se a busca vai ser feita em cima do nome do discente */
		private boolean buscaNome;
		/** Indica se a busca vai ser feita em cima do tipo */
		private boolean buscaTipo;
		/** Indica se a busca vai ser feita em cima da forma de Ingresso */
		private boolean buscaFormaIngresso;
		/** Indica se a busca vai ser feita em cima do ano de ingresso */
		private boolean buscaAnoIngresso;
		/** Indica se a busca vai ser feita em cima do período de ingresso  */
		private boolean buscaPeriodoIngresso;
		/** Indica se a busca vai ser feita em cima do status */
		private boolean buscaStatus;
		/** Indica se a busca vai ser feita em cima do nível */
		private boolean buscaNivel;
		/** Indica se a busca vai ser feita em cima dos matriculados em */
		private boolean buscaMatriculadosEm;
		/** Indica se a busca vai ser feita em cima dos não matriculados em */
		private boolean buscaNaoMatriculadosEm;
		/** Indica se a busca vai ser feita em cima dos trancados em um período */
		private boolean buscaTrancadosNoPeriodo;
		/** Indica se a busca vai ser feita em cima da cidade */
		private boolean buscaCidade;
		/** Indica se a busca vai ser feita em cima do estado */
		private boolean buscaEstado;
		/** Indica se a busca vai ser feita em cima do centro */
		private boolean buscaCentro;
		/** Indica se a busca vai ser feita em cima do programa */
		private boolean buscaPrograma;
		/** Indica se a busca vai ser feita em cima da escola */
		private boolean buscaEscola;
		/** Indica se a busca vai ser feita em cima do curso de Graduação */
		private boolean buscaCursoGraduacao;
		/** Indica se a busca vai ser feita em cima do curso de Lato */
		private boolean buscaCursoLato;
		/** Indica se a busca vai ser feita em cima do curso de Tecnico */
		private boolean buscaCursoTecnico;
		/** Indica se a busca vai ser feita em cima da Matriz */
		private boolean buscaMatriz;
		/** Indica se a busca vai ser feita em cima do Curriculo */
		private boolean buscaCurriculo;
		/** Indica se a busca vai ser feita em cima do Curriculo Stricto*/
		private boolean buscaCurriculoStricto;
		/** Indica se a busca vai ser feita em cima do turno*/
		private boolean buscaTurno;
		/** Indica se a busca vai ser feita em cima da modalidade */
		private boolean buscaModalidade;
		/** Indica se a busca vai ser feita em cima do prazo máximo */
		private boolean buscaPrazoMaximo;
		/** Indica se a busca vai ser feita em cima do polo */
		private boolean buscaPolo;
		/** Indica se a busca vai ser feita em cima da participação no Enade */
		private boolean buscaParticipacaoEnade;
		/** Indica se a busca vai ser feita em cima da data de defesa  */
		private boolean buscaPrevisaoDefesa;
		/** Indica se a busca vai ser feita em cima da data de qualificação */
		private boolean buscaPrevisaoQualificacao;
		/** Indica se a busca vai ser feita sobre os discente com prazo esgotado */
		private boolean buscaPrazoEsgotado;
		/** Indica se a busca vai ser feita sobre os discente com prazo a ser esgotado */
		private boolean buscaPrazoASerEsgotado;
		/** Indica se a busca vai ser feita sobre os discente com prazo prorrogado */
		private boolean buscaPrazoProrrogado;
		/** Indica se a busca vai ser feita sobre os discente desligados */
		private boolean buscaDiscentesDesligados;
		/** Indica se a busca vai ser feita sobre os discente homologados */
		private boolean buscaDiscentesHomologados;
		/** Indica se a busca vai ser feita sobre os discente que defenreram e ainda não foram homologados */
		private boolean buscaDiscentesDefenderamNaoHomologados;
		/** Indica se a busca vai ser feita sobre a turma de entrada do discente */
		private boolean buscaTurmaEntrada;
		/** Indica se a busca vai ser feita sobre da especilidade do discente */
		private boolean buscaEspecialidade;
		/** Indica se a busca vai ser feita sobre um período */
		private boolean buscaNoPeriodo;
		/** Indica se a busca vai ser feita sobre a idade dos discente  */
		private boolean buscaIdade;
		/** Indica se a busca vai ser feita sobre o sexo dos discente  */
		private boolean buscaSexo;
		/** Indica se a busca vai ser feita sobre o ano de saida do discente */
		private boolean buscaAnoSaida;
		/** Indica se a busca vai ser feita sobre o período de saida do discente */
		private boolean buscaPeriodoSaida;
		/** Indica se a busca vai ser feita sobre o tipo de saida do discente */
		private boolean buscaTipoSaida;
		/** Indica se a busca vai ser feita sobre o convênio do discente */
		private boolean buscaConvenio;
		/** Indica se a busca deve considerar os apostilamentos */
		private boolean buscaDesconsiderarApostilamentos;
		/** Indica se a busca vai ser feita sobre os discente com necessidade especial */
		private boolean buscaTipoNecessidadeEspecial;
		/** Indica se a busca deverá restringir os discentes pela opção de aprovação do no Vestibular. */
		private boolean buscaOpcaoAprovacao;
		/** Indica se a busca deverá restringir os discentes pelos valores de seus índices acadêmicos. */
		private boolean buscaIndiceAcademico;
		
		public boolean isBuscaIndiceAcademico() {
			return buscaIndiceAcademico;
		}
		
		public void setBuscaIndiceAcademico(boolean buscaIndiceAcademico) {
			this.buscaIndiceAcademico = buscaIndiceAcademico;
		}
		
		public boolean isBuscaDesconsiderarApostilamentos() {
			return buscaDesconsiderarApostilamentos;
		}

		public void setBuscaDesconsiderarApostilamentos(
				boolean buscaDesconsiderarApostilamentos) {
			this.buscaDesconsiderarApostilamentos = buscaDesconsiderarApostilamentos;
		}

		public boolean isBuscaConvenio() {
			return buscaConvenio;
		}

		public void setBuscaConvenio(boolean buscaConvenio) {
			this.buscaConvenio = buscaConvenio;
		}

		public boolean isBuscaAnoSaida() {
			return buscaAnoSaida;
		}

		public void setBuscaAnoSaida(boolean buscaAnoSaida) {
			this.buscaAnoSaida = buscaAnoSaida;
		}

		public boolean isBuscaPeriodoSaida() {
			return buscaPeriodoSaida;
		}

		public void setBuscaPeriodoSaida(boolean buscaPeriodoSaida) {
			this.buscaPeriodoSaida = buscaPeriodoSaida;
		}

		public boolean isBuscaTipoSaida() {
			return buscaTipoSaida;
		}

		public void setBuscaTipoSaida(boolean buscaTipoSaida) {
			this.buscaTipoSaida = buscaTipoSaida;
		}

		public boolean isBuscaDiscentesDefenderamNaoHomologados() {
			return buscaDiscentesDefenderamNaoHomologados;
		}

		public void setBuscaDiscentesDefenderamNaoHomologados(boolean buscaDiscentesDefenderamNaoHomologados) {
			this.buscaDiscentesDefenderamNaoHomologados = buscaDiscentesDefenderamNaoHomologados;
		}

		public boolean isBuscaMatricula() {
			return buscaMatricula;
		}

		public void setBuscaMatricula(boolean buscaMatricula) {
			this.buscaMatricula = buscaMatricula;
		}

		public boolean isBuscaNome() {
			return buscaNome;
		}

		public void setBuscaNome(boolean buscaNome) {
			this.buscaNome = buscaNome;
		}

		public boolean isBuscaTipo() {
			return buscaTipo;
		}

		public void setBuscaTipo(boolean buscaTipo) {
			this.buscaTipo = buscaTipo;
		}

		public boolean isBuscaAnoIngresso() {
			return buscaAnoIngresso;
		}

		public void setBuscaAnoIngresso(boolean buscaAnoIngresso) {
			this.buscaAnoIngresso = buscaAnoIngresso;
		}

		public boolean isBuscaPeriodoIngresso() {
			return buscaPeriodoIngresso;
		}

		public void setBuscaPeriodoIngresso(boolean buscaPeriodoIngresso) {
			this.buscaPeriodoIngresso = buscaPeriodoIngresso;
		}

		public boolean isBuscaStatus() {
			return buscaStatus;
		}

		public void setBuscaStatus(boolean buscaStatus) {
			this.buscaStatus = buscaStatus;
		}

		public boolean isBuscaNivel() {
			return buscaNivel;
		}

		public void setBuscaNivel(boolean buscaNivel) {
			this.buscaNivel = buscaNivel;
		}

		public boolean isBuscaMatriculadosEm() {
			return buscaMatriculadosEm;
		}

		public void setBuscaMatriculadosEm(boolean buscaMatriculadosEm) {
			this.buscaMatriculadosEm = buscaMatriculadosEm;
		}

		public boolean isBuscaNaoMatriculadosEm() {
			return buscaNaoMatriculadosEm;
		}

		public void setBuscaNaoMatriculadosEm(boolean buscaNaoMatriculadosEm) {
			this.buscaNaoMatriculadosEm = buscaNaoMatriculadosEm;
		}

		public boolean isBuscaTrancadosNoPeriodo() {
			return buscaTrancadosNoPeriodo;
		}

		public void setBuscaTrancadosNoPeriodo(boolean buscaTrancadosNoPeriodo) {
			this.buscaTrancadosNoPeriodo = buscaTrancadosNoPeriodo;
		}

		public boolean isBuscaCidade() {
			return buscaCidade;
		}

		public void setBuscaCidade(boolean buscaCidade) {
			this.buscaCidade = buscaCidade;
		}

		public boolean isBuscaEstado() {
			return buscaEstado;
		}

		public void setBuscaEstado(boolean buscaEstado) {
			this.buscaEstado = buscaEstado;
		}

		public boolean isBuscaCentro() {
			return buscaCentro;
		}

		public void setBuscaCentro(boolean buscaCentro) {
			this.buscaCentro = buscaCentro;
		}

		public boolean isBuscaPrograma() {
			return buscaPrograma;
		}

		public void setBuscaPrograma(boolean buscaPrograma) {
			this.buscaPrograma = buscaPrograma;
		}

		public boolean isBuscaEscola() {
			return buscaEscola;
		}

		public void setBuscaEscola(boolean buscaEscola) {
			this.buscaEscola = buscaEscola;
		}

		public boolean isBuscaCursoGraduacao() {
			return buscaCursoGraduacao;
		}

		public void setBuscaCursoGraduacao(boolean buscaCursoGraduacao) {
			this.buscaCursoGraduacao = buscaCursoGraduacao;
		}

		public boolean isBuscaCursoLato() {
			return buscaCursoLato;
		}

		public void setBuscaCursoLato(boolean buscaCursoLato) {
			this.buscaCursoLato = buscaCursoLato;
		}

		public boolean isBuscaCursoTecnico() {
			return buscaCursoTecnico;
		}

		public void setBuscaCursoTecnico(boolean buscaCursoTecnico) {
			this.buscaCursoTecnico = buscaCursoTecnico;
		}

		public boolean isBuscaMatriz() {
			return buscaMatriz;
		}

		public void setBuscaMatriz(boolean buscaMatriz) {
			this.buscaMatriz = buscaMatriz;
		}

		public boolean isBuscaTurno() {
			return buscaTurno;
		}

		public void setBuscaTurno(boolean buscaTurno) {
			this.buscaTurno = buscaTurno;
		}

		public boolean isBuscaModalidade() {
			return buscaModalidade;
		}

		public void setBuscaModalidade(boolean buscaModalidade) {
			this.buscaModalidade = buscaModalidade;
		}

		public boolean isBuscaPrazoMaximo() {
			return buscaPrazoMaximo;
		}

		public void setBuscaPrazoMaximo(boolean buscaPrazoMaximo) {
			this.buscaPrazoMaximo = buscaPrazoMaximo;
		}

		public boolean isBuscaPolo() {
			return buscaPolo;
		}

		public void setBuscaPolo(boolean buscaPolo) {
			this.buscaPolo = buscaPolo;
		}

		public boolean isBuscaParticipacaoEnade() {
			return buscaParticipacaoEnade;
		}

		public void setBuscaParticipacaoEnade(boolean buscaParticipacaoEnade) {
			this.buscaParticipacaoEnade = buscaParticipacaoEnade;
		}

		public boolean isBuscaPrevisaoDefesa() {
			return buscaPrevisaoDefesa;
		}

		public void setBuscaPrevisaoDefesa(boolean buscaPrevisaoDefesa) {
			this.buscaPrevisaoDefesa = buscaPrevisaoDefesa;
		}
		
		public boolean isBuscaPrevisaoQualificacao() {
			return buscaPrevisaoQualificacao;
		}

		public void setBuscaPrevisaoQualificacao(boolean buscaPrevisaoQualificacao) {
			this.buscaPrevisaoQualificacao = buscaPrevisaoQualificacao;
		}

		public boolean isBuscaPrazoEsgotado() {
			return buscaPrazoEsgotado;
		}

		public void setBuscaPrazoEsgotado(boolean buscaPrazoEsgotado) {
			this.buscaPrazoEsgotado = buscaPrazoEsgotado;
		}

		public boolean isBuscaTurmaEntrada() {
			return buscaTurmaEntrada;
		}

		public void setBuscaTurmaEntrada(boolean buscaTurmaEntrada) {
			this.buscaTurmaEntrada = buscaTurmaEntrada;
		}

		public boolean isBuscaEspecialidade() {
			return buscaEspecialidade;
		}

		public void setBuscaEspecialidade(boolean buscaEspecialidade) {
			this.buscaEspecialidade = buscaEspecialidade;
		}

		public boolean isBuscaNoPeriodo() {
			return buscaNoPeriodo;
		}

		public void setBuscaNoPeriodo(boolean buscaNoPeriodo) {
			this.buscaNoPeriodo = buscaNoPeriodo;
		}

		public boolean isBuscaPrazoASerEsgotado() {
			return buscaPrazoASerEsgotado;
		}

		public void setBuscaPrazoASerEsgotado(boolean buscaPrazoASerEsgotado) {
			this.buscaPrazoASerEsgotado = buscaPrazoASerEsgotado;
		}

		public boolean isBuscaPrazoProrrogado() {
			return buscaPrazoProrrogado;
		}

		public void setBuscaPrazoProrrogado(boolean buscaPrazoProrrogado) {
			this.buscaPrazoProrrogado = buscaPrazoProrrogado;
		}

		public boolean isBuscaDiscentesDesligados() {
			return buscaDiscentesDesligados;
		}

		public void setBuscaDiscentesDesligados(boolean buscaDiscentesDesligados) {
			this.buscaDiscentesDesligados = buscaDiscentesDesligados;
		}

		public boolean isBuscaDiscentesHomologados() {
			return buscaDiscentesHomologados;
		}

		public void setBuscaDiscentesHomologados(boolean buscaDiscentesHomologados) {
			this.buscaDiscentesHomologados = buscaDiscentesHomologados;
		}

		public boolean isBuscaFormaIngresso() {
			return buscaFormaIngresso;
		}

		public void setBuscaFormaIngresso(boolean buscaFormaIngresso) {
			this.buscaFormaIngresso = buscaFormaIngresso;
		}

		public boolean isBuscaIdade() {
			return buscaIdade;
		}

		public void setBuscaIdade(boolean buscaIdade) {
			this.buscaIdade = buscaIdade;
		}

		public boolean isBuscaSexo() {
			return buscaSexo;
		}

		public void setBuscaSexo(boolean buscaSexo) {
			this.buscaSexo = buscaSexo;
		}

		public boolean isBuscaCurriculo() {
			return buscaCurriculo;
		}

		public void setBuscaCurriculo(boolean buscaCurriculo) {
			this.buscaCurriculo = buscaCurriculo;
		}

		public boolean isBuscaCurriculoStricto() {
			return buscaCurriculoStricto;
		}

		public void setBuscaCurriculoStricto(boolean buscaCurriculoStricto) {
			this.buscaCurriculoStricto = buscaCurriculoStricto;
		}
		
		public boolean isBuscaTipoNecessidadeEspecial() {
			return buscaTipoNecessidadeEspecial;
		}
		
		public void setBuscaTipoNecessidadeEspecial(boolean buscaTipoNecessidadeEspecial) {
			this.buscaTipoNecessidadeEspecial = buscaTipoNecessidadeEspecial;
		}

		public boolean isBuscaOpcaoAprovacao() {
			return buscaOpcaoAprovacao;
		}

		public void setBuscaOpcaoAprovacao(boolean buscaOpcaoAprovacao) {
			this.buscaOpcaoAprovacao = buscaOpcaoAprovacao;
		}

}