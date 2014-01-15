<h2>Relatórios de Ensino</h2>
<ul>
	
	<%-- 
	<li> Alunos
		<ul>
			<li><h:commandLink value="Alunos com Necessidades Especiais" action="#{relatoriosJasper.iniciarNecessidadesEspeciais}" /></li>
			<li>
				<h:commandLink value="Com Detalhamento em Carga Horária" action="#{relatorioPorCurso.iniciar}">
					<f:param name="relatorio" value="AlunosComDetalhamentosCH"/>
				</h:commandLink>
			</li>

			<li>
				<h:commandLink value="Alunos Reprovados e Desnivelados" action="#{relatorioDiscente.carregarSelecaoRelatorio}">
					<f:param value="seleciona_centro.jsf" name="relatorio"/>
				</h:commandLink>
			</li>
		</ul>			
	</li>
	--%>
	
	<li>Concluíntes - Graduação
		<ul>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar371}" value="Concluintes por semestre, turno e gênero (3.7.1)"/></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar372}" value="Concluintes nas modalidades e habilitações dos cursos de graduação, por turno, semestre e gênero (3.7.2)"/></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar02}" value="Lista nominal de Concluintes" /></li>
			<li><h:commandLink action="#{relatoriosConcluintesMBean.iniciarPotenciaisConcluintes}" value="Prováveis Concluintes por Ano/Semestre" /></li>
			<li><h:commandLink action="#{ relatorioTaxaConclusao.iniciarRelatorioTaxaConclusao }" value="Taxa de Conclusão"/></li>
		</ul>							
	</li>	
		
	<li> Alunos Matriculados - Técnico
		<ul>
			<li><h:commandLink action="#{relatoriosTecnico.iniciarRelatorioListaAlunosMatriculados}" value="Total de Matriculados" /></li>
		</ul>						
	</li>
	
	<li> Alunos Matriculados - Médio
		<ul>
			<li><h:commandLink action="#{relatoriosMedio.iniciarRelatorioListaAlunosMatriculados}" value="Total de Matriculados" /></li>
		</ul>						
	</li>
	
	<li>Ingressantes - Graduação
		<ul>
			<li><h:commandLink action="#{ relatorioTaxaConclusao.iniciarRelatorioInsucessoConcluintes }" value="Relatório Analítico de Acontecimentos por Ano de Ingresso"/></li>
		</ul>
	</li>
	
	<li>Pós-Graduação
		<ul>
			<li><h:commandLink action="#{relatoriosStricto.iniciarRelatorioQuantitativoAlunosMatriculadosMes}" value="Quantitativo Geral de Alunos Matriculados por Mês" /></li>
			<li><h:commandLink action="#{relatoriosStricto.iniciarRelatorioQuantativoDefesasAnos}" value="Quantitativo Geral de Defesas por Ano" /></li>
			<li><h:commandLink action="#{relatorioTaxaSucessoStricto.iniciarTaxaSucesso}" value="Relatório de Taxa de Sucesso" id="iniciarTaxaSucesso"/></li>
		</ul>
	</li>
	
	
	<li> Alunos Matriculados - Graduação
		<ul>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar03}" value="Total de Matriculados" /></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar05}" value="Total de Trancados"/></li>
			<li><h:commandLink action="#{ relatorioCurso.iniciaRelatorioMatriculasProjetadasCurso }" value="Relatório de Matrículas Projetadas"/></li>
		</ul>						
	</li>
	
	<li>Plano Individual do Docente (PID)
		<ul>
			<li><h:commandLink action="#{relatorioPIDPorSemestre.iniciar}" value="PID - Relatório por Semestre" /></li>
		</ul>	
	</li>
	
	<li>Unidades Acadêmicas
		<ul>
			<li><h:commandLink action="#{relatorioUnidadesAcademicasMBean.iniciar}" value="Total de Unidades Acadêmicas por Tipo" /></li>
		</ul>	
	</li>	
	
	<li>Plano de Curso
		<ul>
			<li><h:commandLink action="#{relatorioPlanosCursosCadastradosMBean.iniciar}" value="Relatório de Planos de Cursos Cadastrados" /></li>
		</ul>	
	</li>	
	
	<li>Índices Acadêmicos
		<ul>
			<li><h:commandLink action="#{relatorioAnaliseDiscentesPorIndiceMBean.iniciar}" value="Análise de Discentes por Índice Acadêmico" /></li>
		</ul>
	</li>	
<%--
	<li>Bolsistas
		<ul>
			<li>
				<h:commandLink value="Bolsistas com Mais de uma Bolsa" action="#{relatorioAcompanhamentoBolsas.iniciarRelatorioBolsistaDuploOuVinculo}"/>
			</li>			
		</ul>
	</li>
	 --%>
</ul>