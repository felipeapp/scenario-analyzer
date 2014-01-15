	
	<rich:tabPanel switchType="client">
        <rich:tab label="Alunos">
			<rich:tabPanel switchType="client">
        		<rich:tab label="Listagens">
		 			<ul><li>Lista de Alunos
		            <ul>
					<li> <h:commandLink id="id_assinaturaColacaoGrau" action="#{listaAssinaturasGraduandos.iniciar}" value="Assinaturas para Colação de Grau Coletiva" onclick="setAba('relatorios')" /> </li>
					<li><h:commandLink
							id="report_alunosNecessidadesEspeciais"
							action="#{relatoriosJasper.iniciarNecessidadesEspeciais}"
							value="Com necessidades especiais"
							onclick="setAba('relatorios')" />
					</li>
					<li> 
						<h:commandLink id="report_alunosPendenciaBiblioteca" value="Graduandos Com Empréstimo Pendente na Biblioteca" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')" >
							<f:param name="relatorio" value="GraduandosComEmprestimoPendenteBiblioteca"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunosSelecao" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Para eleição">
							<f:param value="seleciona_eleicao.jsf" name="relatorio"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunosPorCidade" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')" value="Por cidade de residência">
							<f:param name="relatorio" value="AlunosPorCidadeResidencia"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunosIngressantesNovoCurso" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Alunos Ingressantes em um Novo Curso" >
							<f:param value="seleciona_aluno_ingressante_outro_curso.jsf" name="relatorio"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_ingressantesSemSolicitacaoMatricula" action="#{relatorioDiscenteMatricula.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Relatório de Ingressantes sem solicitação de Matrícula">
							<f:param value="seleciona_ingressante_sem_matricula.jsf" name="relatorio"/>
						</h:commandLink>		
					</li>				
					<li> 
						<h:commandLink id="report_indicesAcademicos" action="#{ indiceAcademicoMBean.buscarIndicesDiscente }" onclick="setAba('relatorios')" value="Relatório dos Índices Acadêmicos do Aluno" >
							<f:param value="relatorio_indices_discente.jsf" name="relatorio"/>
						</h:commandLink> 
					</li>	
					<li>
						<h:commandLink id="report_alunosComMobilidadeEstudantil" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Relatório de Mobilidade Estudantil">
							<f:param value="seleciona_prazo_mobilidade_estudantil.jsf" name="relatorio"/>
						</h:commandLink>		
					</li>
					<ufrn:checkRole papel="<%=SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS %>">
					<li>
						<h:commandLink id="report_alunosConvocadosExcluidos" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Alunos Ausentes no Cadastramento" >
							<f:param value="seleciona_aluno_convocado_excluido.jsf" name="relatorio"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_relatorioContatoCandidatosConvocados" 
							action="#{relatoriosVestibular.iniciarRelatorioContatoCandidatosConvocados}"  
							onclick="setAba('relatorios')" value="Dados para Contato de Candidatos Convocados" />
					</li>
					</ufrn:checkRole>				
					</ul>
					</li></ul>        		

        		</rich:tab>
        		<rich:tab label="Ativos e Matriculados">
        			<ul><li>Ativos e Matriculados
		            <ul>
					<li>
						<h:commandLink id="report_DiscentesMaisVinculosAtivos" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Alunos com mais de um vinculo ativo" >
							<f:param value="seleciona_discentes_mais_vinculos_ativos.jsf" name="relatorio"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunosAtivosCurso" value="Ativos por curso" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')" >
							<f:param name="relatorio" value="AlunosAtivosPorCurso"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunosAtivosNaoMatriculados" value="Ativos e não matriculados por curso" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')" >
							<f:param name="relatorio" value="AlunosAtivosNaoMatriculadosPorCurso"/>
						</h:commandLink>
					</li>					
					<li>
						<h:commandLink id="report_alunosAtivosPorPrazoConclusao" value="Ativos por prazo de conclusão" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')" >
							<f:param name="relatorio" value="AlunosAtivosPorPrazoConclusao"/>
							<f:param name="permitirTodosOsCursos" value="true"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunosComDetalhamentosCH" value="Com detalhamento em carga horária" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')">
							<f:param name="relatorio" value="AlunosComDetalhamentosCH"/>
						</h:commandLink>
					</li>					
					<li>
						<h:commandLink id="report_percentualCHAluno" value="Com percentual de CH cumprida" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')" >
							<f:param name="relatorio" value="PercentualCHAluno"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunoRegistroEmDisciplina" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Com registro em uma disciplina">
							<f:param value="seleciona_registro_disciplina.jsf" name="relatorio"/>
						</h:commandLink>
					</li>				
					<li>
						<h:commandLink id="report_alunosIngressantes" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Ingressantes">
							<f:param value="seleciona_ingressantes.jsf" name="relatorio"/>
						</h:commandLink>
					</li>					
					<li>
						<h:commandLink id="report_alunosSemSucessoPorSemestre" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Insucessos em disciplinas por semestre">
							<f:param value="seleciona_insucessos.jsf" name="relatorio"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunosMatriculados" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Matriculados">
							<f:param value="seleciona_matriculados.jsf" name="relatorio"/>
						</h:commandLink>
					</li>					
					<li>
						<h:commandLink id="report_alunosMatriculadosDisciplinaCHEstagio" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')" value="Matriculados em disciplina com CH de estágio">
							<f:param name="relatorio" value="AlunosMatriculadosDisciplinaCHEstagio"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunosMatriculadosNumaAtividade" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Matriculados em uma atividade">
							<f:param value="seleciona_matriculado_atividade.jsf" name="relatorio"/>
						</h:commandLink>
					</li>					
					<li>
						<h:commandLink id="report_alunosPorPrazoMaximoConclusao" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')" value="Por prazo máximo de conclusão">
							<f:param name="relatorio" value="AlunosPorPrazoMaximoConclusao"/>
						</h:commandLink>
					</li>					
					<li>
						<h:commandLink id="report_AlunosComSolicitacaoTrancamento" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Solicitações de trancamentos">
							<f:param value="seleciona_motivo_trancamento.jsf" name="relatorio"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunosVinculadosNumaEstruturaCurricular" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Vinculados a uma estrutura curricular">
							<f:param value="seleciona_vinculados_estrutura.jsf" name="relatorio"/>
						</h:commandLink>		
					</li>	
													
					</ul>
					</li></ul>  
        		</rich:tab>
				<rich:tab label="Concluintes e Egressos">
					<ul><li>Concluintes e Egressos
					<ul>
					<li>
						<h:commandLink id="report_alunosComPrazoConclusaoSemestreAtual" value="Com prazo de conclusão no semestre atual"  action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')">
							<f:param name="relatorio" value="AlunosComPrazoConclusaoSemestreAtual"/>
						</h:commandLink>
					</li>	
					<li>
						<h:commandLink id="report_alunosConcluintes" value="Graduandos com Ano/Semestre" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')" >
							<f:param name="relatorio" value="AlunosConcluintes"/>
						</h:commandLink>
					</li>	
					<li><h:commandLink id="report_alunosConcluintesPorSemestreTurnoGenero"
							action="#{relatoriosPlanejamento.iniciar3711}"
							value="Concluintes nos cursos de graduação, por semestre, turno e gênero (Analítico)"
							onclick="setAba('relatorios')" />
					</li>		
					<li>
						<h:commandLink id="report_alunosLaureados" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Lista de Alunos Laureados">
							<f:param value="seleciona_laureados.jsf" name="relatorio"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunosPorTipoSaida" action="#{relatorioDiscente.carregarSelecaoRelatorio}" onclick="setAba('relatorios')" value="Por tipo de saída ">
							<f:param value="seleciona_tipo_saida.jsf" name="relatorio"/>
						</h:commandLink>
					</li>									
					</ul>
					</li></ul>  
        		</rich:tab>        		
        	</rich:tabPanel>
        </rich:tab>
        <rich:tab label="Quantitativos">
        <ul>
            <li>Alunos
            	<ul>
					<li>
						<h:commandLink id="report_alunosVestibularSemMatricula" value="Alunos cadastrados no vestibular sem matrícula"  action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')">
							<f:param name="relatorio" value="QuantitativoAlunosCadastradosVestibularSemMatricula"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_quantitativoAlunosConcluintes" value="Alunos concluintes"  action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')">
							<f:param name="relatorio" value="QuantitativoAlunosConcluintes"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_quantitativoAlunosGraduandos" value="Alunos graduandos" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')">
							<f:param name="relatorio" value="QuantitativoAlunosGraduandos"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_quantitativoAlunosMatriculados" value="Alunos matriculados"  action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')">
							<f:param name="relatorio" value="QuantitativoAlunosMatriculados"/>
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_alunosAtivosDiversosNiveis" action="#{relatoriosPlanejamento.iniciarRelatorioQuantitativoAlunosMatriculados}" onclick="setAba('relatorios')" value="Alunos matriculados/ativos nos diversos níveis"/>
					</li>
					<li>
						<a href="${ctx}/graduacao/relatorios/discente/selecionaq_sexo_egresso.jsf?aba=relatorios" id="report_alunosPorSexoEgresso"> Alunos por sexo e egresso </a>
					</li>
					<li>
						<a href="${ctx}/graduacao/relatorios/discente/selecionaq_sexo_ingresso.jsf?aba=relatorios" id="report_alunosPorSexoIngresso"> Alunos por sexo e ingresso </a>
					</li>	
					<li>
						<h:commandLink id="report_QuantitativoAlunosEntraramSegundaOpcaoVestibular" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Alunos que Entraram por Segunda Opção no Vestibular">
							<f:param value="seleciona_vestibular.jsf" name="relatorio"/>
						</h:commandLink>		
					</li>	
					<li>
						<h:commandLink id="report_quantitativoRegularPorGeneroStatus" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Discentes Regulares por Gênero e Status">
							<f:param value="seleciona_total_alunos_regulares.jsf" name="relatorio"/>
						</h:commandLink>		
					</li>								
					<li>
						<h:commandLink id="report_quantitativoRegularPorGeneroCurso" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Discentes Regulares por Gênero e Curso">
							<f:param value="seleciona_total_alunos_regulares_curso.jsf" name="relatorio"/>
						</h:commandLink>		
					</li>
				</ul>
			</li>
			
			<li>Alunos Probasica
	            <ul>
					<li><a href="${ctx}/graduacao/relatorios/discente/selecionaq_probasica_concluido.jsf?aba=relatorios" id="report_alunosProbasicaConcluidos"> Alunos de probasica concluídos </a></li>
					<li><a href="${ctx}/graduacao/relatorios/discente/selecionaq_probasica_matriculado.jsf?aba=relatorios" id="report_alunosProbasicaMatriculados"> Alunos de probasica matriculados</a></li>
					<li><a href="${ctx}/graduacao/relatorios/discente/selecionaq_probasica_sem_matricula.jsf?aba=relatorios" id="report_alunosProbasicaSemMatricula"> Alunos de probasica sem matrícula</a></li>
				</ul>
			</li>
		
			<li>Matriculas/Solicitações
				<ul>		
					<li><h:commandLink id="report_orientacoesPorCurso" action="#{relatoriosJasper.iniciarQuantitativoOrientacoesAcademicas}" value="Orientações Acadêmicas por Curso" onclick="setAba('relatorios')"/> </li>
					<li><h:commandLink  id="report_solicitacoesMatricula" action="#{relatorioCurso.gerarRelatorioTotaisSolicitacoesMatricula}"  onclick="setAba('relatorios')"  value="Solicitações de matrículas"/></li>
					<li><a href="${ctx}/graduacao/relatorios/discente/selecionaq_motivo_trancamento.jsf?aba=relatorios" id="report_solicitacoesTrancamento">Solicitações de trancamentos</a></li>
					<li><h:commandLink id="report_dadosPorComponente" action="#{relatorioQuantitativoTurmasSolicitacoesBean.iniciar}" value="Solicitações, turmas, matrículas e vagas por componente" onclick="setAba('relatorios')"/> </li>
				</ul>
			</li>
			
			<li>Turmas/Trancamentos
				<ul>
					<li><h:commandLink id="report_quantitativoTurmaNaoConsolidada" action="#{relatorioDiscente.iniciarRelatorioQuantitativoTurmaDepartamento}"  onclick="setAba('relatorios')" value="Relatório Quantitativos das Turmas não Consolidadas"/></li>
					<li><h:commandLink id="report_trancamentoDisciplina" action="#{relatorioDiscente.iniciarTrancamentoReuni}" onclick="setAba('relatorios')" value="Relatório Trancamento de Disciplinas" />
					<li><h:commandLink id="report_totalTurmaPorHorarioAula" action="#{relatorioTotalTurmasHorarioMBean.iniciarTotalTurmasPorHorarioDepartamentoOuCentro}" value="Total de turmas por horários de aula" onclick="setAba('relatorios')"/> </li>
					<li><h:commandLink id="report_quantitativoTurmaDisciplinaPorDepartamento" action="#{relatorioTurma.iniciarRelatorioQuantTurmaDisciplinaPorDepto}" value="Turmas e Disciplinas por Departamento" /></li> 
				</ul>
			</li>
			
			<li>Outros
				<ul>
					<li><h:commandLink id="report_espectroRenda" action="#{rendaEspectro.gerarRendaEspectro}"  onclick="setAba('relatorios')" value="Relatório Espectro de Renda" /></li>
					<li>
						<h:commandLink id="report_sumarioIndicesAcadêmicos" action="#{relatorioDiscente.iniciarSumarioIndicesAcademicos}" onclick="setAba('relatorios')" value="Sumário de Indices Acadêmicos por Curso de Graduação"/>
					</li>
				</ul>
			</li>
		</ul>
			
        </rich:tab>
        <rich:tab label="Outros">
            <ul>
			<li> Componentes Curriculares
				<ul>
				<li><h:commandLink id="report_componenteComProgramaCadastrado" action="#{relatorioPorDepartamento.iniciarRelatorioComponentesComPrograma}" value="Componentes com Programas Cadastrados" onclick="setAba('relatorios')"/></li>
				<li><h:commandLink id="report_componenteComProgramaInconpleto" action="#{relatorioPorDepartamento.iniciarRelatorioComponentesComProblemasCadastroEmPrograma}" value="Componentes com Programas Incompletos" onclick="setAba('relatorios')"/></li>
				</ul>
			</li>
            <li> Curso
				<ul>
				<li><a href="${ctx}/graduacao/relatorios/curso/seleciona_indice_trancamento.jsf?aba=relatorios" id="report_indiceTrancamentoCancelamento">Índice de trancamento e cancelamentos</a></li>		
				</ul>
			</li>
			
			<li>Biblioteca
				<ul>
					<li>
						<h:commandLink id="report_situacaoUsuario" value="Verificar Situação Usuário / Emitir Declaração de Quitação" action="#{verificaSituacaoUsuarioBibliotecaMBean.iniciarVerificacao}" onclick="setAba('relatorios')" />
					</li>
				</ul>
			</li>
			
			<li> Docentes
				<ul>
					<li><a href="${ctx}/graduacao/relatorios/docente/seleciona_lista_disciplina_anosemestre.jsf?aba=relatorios" id="report_disciplinaDocentePorDepartamento">Disciplinas de docentes por departamento/período</a></li>
					<li><h:commandLink id="report_docenteSemTurmaConsolidada" action="#{relatoriosJasper.iniciarDocentesQueNaoConsolidaramTurmas}" value="Docentes que não consolidaram as turmas" onclick="setAba('relatorios')"/></li>
					<li><h:commandLink id="report_mostrarAvisosFalta" action="#{avisoFalta.iniciarBusca}" value="Mostrar Avisos de Faltas" onclick="setAba('relatorios')"/></li>
					<li><h:commandLink id="report_situacaoDocente" value="Situação Docente Atual" action="#{relatoriosDepartamentoCpdi.iniciarSituacaoDocente}" onclick="setAba('relatorios')"/></li>
					<li><h:commandLink id="report_docenteCurso" value="Docentes Vinculados a um Curso" action="#{relatorioDocentesPorCursoBean.iniciar}" onclick="setAba('relatorios')"/></li>
					<li><h:commandLink id="report_bancasPorOrientador" value="Relatório de Bancas por Orientador" action="#{relatorioBancasOrientador.iniciarRelatorioBancasOrientador}" onclick="setAba('relatorios')"/></li>
				</ul>
			</li>
						
			<li> Transferência Voluntária
				<ul>
					<li>
						<h:commandLink id="report_inscritosPorMunicipio" action="#{relatorioInscricaoSelecao.iniciarRelatoriosInscritos}" 
							value="Inscritos por Município/Matriz Curricular" onclick="setAba('relatorios')">
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_inscritosPorAgendamento" action="#{relatorioInscricaoSelecao.iniciarRelatoriosInscritosAgendamento}" 
							value="Inscritos por Agendamento/Município" onclick="setAba('relatorios')">
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_quantitativoInscritosCurso" action="#{relatorioInscricaoSelecao.iniciarRelatorioQuantitativoCurso}" 
							value="Quantitativo de Inscritos por Curso" onclick="setAba('relatorios')">
						</h:commandLink>
					</li>
					<li>
						<h:commandLink id="report_quantitativoInscritosDia" action="#{relatorioInscricaoSelecao.iniciarRelatorioQuantitativo}" 
							value="Quantitativo de Inscritos por Dia" onclick="setAba('relatorios')">
						</h:commandLink>
					</li>
				</ul>
			</li>
			
			<li> Currículo
				<ul>
					<li><h:commandLink action="#{ relatoriosCoordenador.relatorioEquivalencias }">Relatório de Equivalências de um Currículo</h:commandLink></li>
				</ul>
			</li>
			
			<li> Turma
				<ul>
				<li><h:commandLink id="report_solicitacaoTurmaCurso" action="#{relatorioTurma.iniciarRelatorioSolicitacaoTurmaCurso}" value="Relatório de Cursos que Não Solicitaram Turmas" onclick="setAba('relatorios')"/></li>
				<li><a href="${ctx}/graduacao/relatorios/turma/seleciona_ch_estagio.jsf?aba=relatorios" id="report_disciplinasEstagio">Relatório de Disciplinas de Estágio</a></li>
				<li><h:commandLink id="report_ocupacaoVagaTurma" action="#{relatorioTurma.iniciarRelatorioOcupacaoVagas}" value="Relatório de Ocupação de Vagas de Turmas" onclick="setAba('relatorios')"/></li>
				<li><a href="${ctx}/graduacao/relatorios/turma/seleciona_turma.jsf?aba=relatorios" id="report_turmas">Relatório de Turmas</a></li>
				<li><h:commandLink id="report_turmaAbertaSemSolicitacao" action="#{relatorioTurma.iniciarRelatorioTurmaAbertaSemSolicitacao}" value="Relatório de Turmas Abertas sem Solicitação" onclick="setAba('relatorios')"/></li>
				<li><h:commandLink id="report_turmaNaoConsolidada" action="#{relatorioTurma.iniciarRelatorioTurmaNaoConsolidada}" value="Relatório de Turmas não Consolidadas" onclick="setAba('relatorios')"/></li>
				<li><h:commandLink id="report_solicitacaoDisciplinaFerias" action="#{relatorioTurma.iniciarVisualizacaoSolicitacaoDisciplinaFerias}" value="Relatório dos Alunos que Solicitaram Disciplinas de Férias" onclick="setAba('relatorios')"/></li>
				<li><h:commandLink id="report_turmasNaoConsolidadasSintetico" action="#{relatoriosJasper.iniciarTurmasNaoConsolidadasSintetico}" value="Relatório Sintético de Turmas Não Consolidadas" onclick="setAba('relatorios')"/></li>
				<li><h:commandLink id="report_situacaoTurmaSintetico" action="#{relatorioTurma.iniciarSituacaoTurma}" value="Relatório Sintético por Situação" onclick="setAba('relatorios')"/></li>
				<li><h:commandLink  id="report_turmasOfertadasCurso" action="#{relatorioTurma.iniciarRelatorioListaTurmasOfertadasCurso}" value="Turmas Ofertadas ao Curso" onclick="setAba('relatorios')"/></li>
				<li><a href="${ctx}/graduacao/relatorios/turma/seleciona_turmas_ano_periodo.jsf?aba=relatorios" id="report_turmasPorAnoPeriodo">Turmas por Período/Departamento/Status</a></li>
				<li><a href="${ctx}/graduacao/relatorios/turma/seleciona_docente.jsf?aba=relatorios" id="report_turmasPorQuantDocente">Turmas por Quantidade de docentes</a></li>
				</ul>
			</li>
			</ul>
        </rich:tab>
    </rich:tabPanel>