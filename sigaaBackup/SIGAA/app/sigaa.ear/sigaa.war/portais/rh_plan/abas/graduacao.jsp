
<ul>
	<li> Consultas Gerais
		<ul>
		<li> <h:commandLink action="#{historicoDiscente.iniciar}" value="Alunos" onclick="setAba('graduacao')"/> </li>
		<li> <h:commandLink action="#{relatorioAvaliacaoMBean.iniciarConsultaPublica}" value="Avaliação Institucional" onclick="setAba('graduacao')"/> </li>
		<li> <h:commandLink action="#{ componenteCurricular.popularBuscaGeral }" value="Componentes Curriculares" onclick="setAba('graduacao')"/> </li>
		<li> <h:commandLink action="#{ cursoGrad.listarCursosGrad }" value="Cursos" onclick="setAba('graduacao')"/></li>
		<li> <a href="${ctx}/graduacao/curriculo/lista.jsf?aba=graduacao">Estruturas Curriculares </a></li>
		<li> <a	href="${ctx}/administracao/cadastro/GrauAcademico/lista.jsf?aba=graduacao">Graus Acadêmicos </a></li>
		<li> <a href="${ctx}/graduacao/habilitacao/lista.jsf?aba=graduacao"> Habilitações</a> </li>
		<li> <a href="${ctx}/graduacao/matriz_curricular/lista.jsf?aba=graduacao">Matrizes Curriculares</a> </li>
		<li> <a	href="${ctx}/administracao/cadastro/ModalidadeEducacao/lista.jsf?aba=graduacao">Modalidades de Educação</a></li>
		<li> <a	href="${ctx}/administracao/cadastro/Municipio/lista.jsf?aba=graduacao">Municípios	</a></li>
		<li> <a href="${ctx}/graduacao/reconhecimento/lista.jsf?aba=graduacao">Reconhecimentos</a></li>
		<li> <h:commandLink action="#{ relatorioCurso.iniciaRelatorioMatriculasProjetadasCurso }" value="Relatório de Matrículas Projetadas" onclick="setAba('graduacao')"/> </li>
		<li> <h:commandLink action="#{ relatorioTaxaConclusao.iniciarRelatorioTaxaConclusao }" value="Relatório de Taxa de Conclusão" onclick="setAba('graduacao')"/> </li>
		<li> <a href="${ctx}/administracao/cadastro/Servidor/lista.jsf">Servidores</a></li>
		<li> <h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Turmas" onclick="setAba('graduacao')"/> </li>
		<li> <h:commandLink action="#{relatorioTotalTurmasHorarioMBean.iniciarTotalTurmasPorHorarioDepartamentoOuCentro}" value="Total de Turmas por Horários de Aula" onclick="setAba('graduacao')"/> </li>
		<li> <a href="${ctx}/administracao/cadastro/Turno/lista.jsf?aba=graduacao">Turnos</a></li>
		</ul>
	</li>
	<li> Relatórios de Alunos
		<ul>
<%--		<li><h:commandLink action="#{discenteGraduacao.preBuscar}"  onclick="setAba('relatorios')  value="Alunos por Curso"/></li> --%>
		<li><h:commandLink value="Alunos com Necessidades Especiais" action="#{relatoriosJasper.iniciarNecessidadesEspeciais}" onclick="setAba('graduacao')" /></li>
		<li>
			<h:commandLink value="Alunos Reprovados e Desnivelados" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('graduacao')" >
				<f:param value="seleciona_centro.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Ativos por Prazo de Conclusão" action="#{relatorioPorCurso.iniciar}" onclick="setAba('graduacao')" >
				<f:param name="relatorio" value="AlunosAtivosPorPrazoConclusao"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Bolsistas com CH Prevista para o Período Atual do Discente" action="#{relatorioAcompanhamentoBolsas.iniciarRelatorioBolsistaCHMatriculada}" onclick="setAba('graduacao')" />
		</li>
		<li>
			<h:commandLink value="Bolsistas com Mais de uma Bolsa" action="#{relatorioAcompanhamentoBolsas.iniciarRelatorioBolsistaDuploOuVinculo}" onclick="setAba('graduacao')" />
		</li>
		<li>
			<h:commandLink value="Bolsistas com Situação da Matrícula em Componente Curricular" action="#{relatorioAcompanhamentoBolsas.iniciarRelatorioBolsistaFrequencia}" onclick="setAba('graduacao')" />
		</li>
		<li>
			<h:commandLink value="Concluintes" action="#{relatorioPorCurso.iniciar}" onclick="setAba('graduacao')" >
				<f:param name="relatorio" value="AlunosConcluintes"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Com Detalhamento em Carga Horária" action="#{relatorioPorCurso.iniciar}" onclick="setAba('graduacao')">
				<f:param name="relatorio" value="AlunosComDetalhamentosCH"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Com Percentual de CH Cumprida" action="#{relatorioPorCurso.iniciar}" onclick="setAba('graduacao')" >
				<f:param name="relatorio" value="PercentualCHAluno"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Com Prazo de Conclusão no Semestre Atual"  action="#{relatorioPorCurso.iniciar}" onclick="setAba('graduacao')">
				<f:param name="relatorio" value="AlunosComPrazoConclusaoSemestreAtual"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Com Registro em uma Disciplina" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('graduacao')" >
				<f:param value="seleciona_registro_disciplina.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Ingressantes" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('graduacao')">
				<f:param value="seleciona_ingressantes.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Laureados" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('graduacao')" >
				<f:param value="seleciona_laureados.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Lista para Eleição" action="#{relatorioDiscente.carregarSelecaoRelatorio}" onclick="setAba('graduacao')" >
				<f:param value="seleciona_eleicao.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Matriculados" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('graduacao')" >
				<f:param value="seleciona_matriculados.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Matriculados em Disciplina com CH de Estágio" action="#{relatorioPorCurso.iniciar}" onclick="setAba('graduacao')" >
				<f:param name="relatorio" value="AlunosMatriculadosDisciplinaCHEstagio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Matriculados em uma Atividade" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('graduacao')" >
				<f:param value="seleciona_matriculado_atividade.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Por Cidade de Residência" action="#{relatorioPorCurso.iniciar}" onclick="setAba('graduacao')">
				<f:param name="relatorio" value="AlunosPorCidadeResidencia"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Por Prazo Máximo de Conclusão" action="#{relatorioPorCurso.iniciar}" onclick="setAba('graduacao')" >
				<f:param name="relatorio" value="AlunosPorPrazoMaximoConclusao"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Por Tipo de Saída" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('graduacao')" >
				<f:param value="seleciona_tipo_saida.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li><h:commandLink value="Quantitativo de Alunos Matriculados/Ativos nos Diversos Níveis" action="#{relatoriosPlanejamento.iniciarRelatorioQuantitativoAlunosMatriculados}"  onclick="setAba('graduacao')" /></li>
		<li><h:commandLink value="Relatório Espectro de Renda" action="#{rendaEspectro.gerarRendaEspectro}"	onclick="setAba('graduacao')"/></li>
		<li>
			<h:commandLink value="Solicitação de Trancamento" action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('graduacao')" >
				<f:param value="seleciona_motivo_trancamento.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		</ul>
	</li>
	<li>Quantitativos
		<ul>
		
		<li>
			<h:commandLink value="Alunos Concluintes"  action="#{relatorioPorCurso.iniciar}" onclick="setAba('graduacao')">
				<f:param name="relatorio" value="QuantitativoAlunosConcluintes"/>
			</h:commandLink>
		</li>
				
		<li>
			<h:commandLink value="Alunos Matriculados"  action="#{relatorioPorCurso.iniciar}" onclick="setAba('graduacao')">
				<f:param name="relatorio" value="QuantitativoAlunosMatriculados"/>
			</h:commandLink>
		</li>
		<li><a href="${ctx}/graduacao/relatorios/discente/selecionaq_sexo_egresso.jsf?aba=graduacao"> Alunos por Sexo e Egresso </a></li>		
		<li><a href="${ctx}/graduacao/relatorios/discente/selecionaq_sexo_ingresso.jsf?aba=graduacao"> Alunos por Sexo e Ingresso </a></li>
		<li><a href="${ctx}/graduacao/relatorios/discente/selecionaq_vest_sem_mat.jsf?aba=graduacao"> Alunos Cadastrados no Vestibular sem Matrícula</a></li>
		<li><a href="${ctx}/graduacao/relatorios/discente/selecionaq_probasica_concluido.jsf?aba=graduacao"> Alunos de Probasica Concluídos </a></li>
		<li><a href="${ctx}/graduacao/relatorios/discente/selecionaq_probasica_matriculado.jsf?aba=graduacao"> Alunos de Probasica Matriculados</a></li>
		<li><a href="${ctx}/graduacao/relatorios/discente/selecionaq_probasica_sem_matricula.jsf?aba=graduacao"> Alunos de Probasica sem Matrícula</a></li>
		<li>
			<h:commandLink id="report_quantitativoAlunosGraduandos" value="Alunos graduandos" action="#{relatorioPorCurso.iniciar}" onclick="setAba('relatorios')">
				<f:param name="relatorio" value="QuantitativoAlunosGraduandos"/>
			</h:commandLink>
		<li><a href="${ctx}/graduacao/relatorios/discente/selecionaq_motivo_trancamento.jsf?aba=graduacao">Solicitação de Trancamento</a></li>
		<li><a href="${ctx}/graduacao/relatorios/docente/seleciona_lista_quantitativo.jsf?aba=graduacao"> Docentes</a></li>
		<li><h:commandLink id="report_sumarioIndicesAcadêmicos" action="#{relatorioDiscente.iniciarSumarioIndicesAcademicos}" onclick="setAba('graduacao')" value="Sumário de Indices Acadêmicos por Curso de Graduação"/></li>
		</ul>
	</li>
	<li> Curso
		<ul>
		<li><a href="${ctx}/graduacao/relatorios/curso/seleciona_indice_trancamento.jsf?aba=graduacao">Índice de Trancamento e Cancelamentos</a></li>
		<li> <h:commandLink action="#{relatorioVagasOfertadas.iniciarRelatorioVagasOfertadas}" value="Relatório de Vagas Ofertadas" onclick="setAba('graduacao')"/> </li>
		</ul>
	</li>
	<li> Turma
		<ul>
		<li><a href="${ctx}/graduacao/relatorios/turma/seleciona_turma.jsf?aba=graduacao">Relatório de Turmas</a></li>
		<li><h:commandLink action="#{buscaTurmaBean.iniciarBuscaPorDepartamento}" value="Turmas por Período/Departamento/Status" onclick="setAba('graduacao')"/></li>
		<li><a href="${ctx}/graduacao/relatorios/turma/seleciona_docente.jsf?aba=graduacao">Relatório Turmas por Quantidade de docentes</a></li>
		<li><a href="${ctx}/graduacao/relatorios/turma/seleciona_ch_estagio.jsf?aba=graduacao">Relatório de Disciplinas de Estágio</a></li>
		<li><h:commandLink action="#{relatorioTurma.iniciarSituacaoTurma}" value="Situação Turma" onclick="setAba('graduacao')"/></li>
		</ul>
	</li>
	<li> Docentes
		<ul>
		<%-- <li><a href="${ctx}/graduacao/relatorios/docente/seleciona_lista_docente.jsf?aba=graduacao">Relatório de Docentes</a></li> --%>
		<li><h:commandLink value="Situação Docente Atual" action="#{relatoriosDepartamentoCpdi.iniciarSituacaoDocente}" onclick="setAba('graduacao')"/></li>		
		<li><a href="${ctx}/graduacao/relatorios/docente/seleciona_lista_disciplina_anosemestre.jsf?aba=graduacao">Disciplinas de Docentes por Departamento/período</a></li>
		<li><h:commandLink action="#{avisoFalta.iniciarBusca}" value="Listar Avisos de Falta" onclick="setAba('graduacao')" /></li>
		</ul>
	</li>
	<li> Relatórios ${ configSistema['siglaCDP'] } - Alunos
		<ul>
		<li>
			<h:commandLink action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('graduacao')" value="Componentes Curriculares Reprovados ou Trancados em Determinado Período">
				<f:param value="seleciona_reprovados_trancados.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('graduacao')" value="Tipo de Saída por Forma de Ingresso/Egresso">
				<f:param value="seleciona_tipo_saida.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		</ul>
	</li>
	<li> Relatórios Analíticos dos Alunos
		<ul>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar02}" value="Concluintes" onclick="setAba('graduacao')" /></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar02Jasper}" value="Concluintes (formato antigo)" onclick="setAba('graduacao')" /></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar04}" value="Evasões" onclick="setAba('graduacao')" /></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar01}" value="Ingressantes" onclick="setAba('graduacao')" /></li>
	 		<li><h:commandLink action="#{relatoriosPlanejamento.iniciar07}" value="Ingressantes por Outras Formas" onclick="setAba('graduacao')" /></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar03}" value="Matriculados" onclick="setAba('graduacao')" /></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar06}" value="Não Matriculados" onclick="setAba('graduacao')" /></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar08}"	value="Retornos de Trancamento"	onclick="setAba('graduacao')" /></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar05}" value="Trancados" onclick="setAba('graduacao')" /></li>
		</ul>
	</li>
</ul>