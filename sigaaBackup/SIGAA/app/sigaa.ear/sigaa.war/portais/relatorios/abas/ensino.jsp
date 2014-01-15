<h2>Relat�rios de Ensino</h2>
<ul>
	
	<%-- 
	<li> Alunos
		<ul>
			<li><h:commandLink value="Alunos com Necessidades Especiais" action="#{relatoriosJasper.iniciarNecessidadesEspeciais}" /></li>
			<li>
				<h:commandLink value="Com Detalhamento em Carga Hor�ria" action="#{relatorioPorCurso.iniciar}">
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
	
	<li>Conclu�ntes - Gradua��o
		<ul>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar371}" value="Concluintes por semestre, turno e g�nero (3.7.1)"/></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar372}" value="Concluintes nas modalidades e habilita��es dos cursos de gradua��o, por turno, semestre e g�nero (3.7.2)"/></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar02}" value="Lista nominal de Concluintes" /></li>
			<li><h:commandLink action="#{relatoriosConcluintesMBean.iniciarPotenciaisConcluintes}" value="Prov�veis Concluintes por Ano/Semestre" /></li>
			<li><h:commandLink action="#{ relatorioTaxaConclusao.iniciarRelatorioTaxaConclusao }" value="Taxa de Conclus�o"/></li>
		</ul>							
	</li>	
		
	<li> Alunos Matriculados - T�cnico
		<ul>
			<li><h:commandLink action="#{relatoriosTecnico.iniciarRelatorioListaAlunosMatriculados}" value="Total de Matriculados" /></li>
		</ul>						
	</li>
	
	<li> Alunos Matriculados - M�dio
		<ul>
			<li><h:commandLink action="#{relatoriosMedio.iniciarRelatorioListaAlunosMatriculados}" value="Total de Matriculados" /></li>
		</ul>						
	</li>
	
	<li>Ingressantes - Gradua��o
		<ul>
			<li><h:commandLink action="#{ relatorioTaxaConclusao.iniciarRelatorioInsucessoConcluintes }" value="Relat�rio Anal�tico de Acontecimentos por Ano de Ingresso"/></li>
		</ul>
	</li>
	
	<li>P�s-Gradua��o
		<ul>
			<li><h:commandLink action="#{relatoriosStricto.iniciarRelatorioQuantitativoAlunosMatriculadosMes}" value="Quantitativo Geral de Alunos Matriculados por M�s" /></li>
			<li><h:commandLink action="#{relatoriosStricto.iniciarRelatorioQuantativoDefesasAnos}" value="Quantitativo Geral de Defesas por Ano" /></li>
			<li><h:commandLink action="#{relatorioTaxaSucessoStricto.iniciarTaxaSucesso}" value="Relat�rio de Taxa de Sucesso" id="iniciarTaxaSucesso"/></li>
		</ul>
	</li>
	
	
	<li> Alunos Matriculados - Gradua��o
		<ul>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar03}" value="Total de Matriculados" /></li>
			<li><h:commandLink action="#{relatoriosPlanejamento.iniciar05}" value="Total de Trancados"/></li>
			<li><h:commandLink action="#{ relatorioCurso.iniciaRelatorioMatriculasProjetadasCurso }" value="Relat�rio de Matr�culas Projetadas"/></li>
		</ul>						
	</li>
	
	<li>Plano Individual do Docente (PID)
		<ul>
			<li><h:commandLink action="#{relatorioPIDPorSemestre.iniciar}" value="PID - Relat�rio por Semestre" /></li>
		</ul>	
	</li>
	
	<li>Unidades Acad�micas
		<ul>
			<li><h:commandLink action="#{relatorioUnidadesAcademicasMBean.iniciar}" value="Total de Unidades Acad�micas por Tipo" /></li>
		</ul>	
	</li>	
	
	<li>Plano de Curso
		<ul>
			<li><h:commandLink action="#{relatorioPlanosCursosCadastradosMBean.iniciar}" value="Relat�rio de Planos de Cursos Cadastrados" /></li>
		</ul>	
	</li>	
	
	<li>�ndices Acad�micos
		<ul>
			<li><h:commandLink action="#{relatorioAnaliseDiscentesPorIndiceMBean.iniciar}" value="An�lise de Discentes por �ndice Acad�mico" /></li>
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