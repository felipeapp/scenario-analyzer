
<ul>
	<li> Alunos
		<ul>
<%-- 	
		<li>
			<h:commandLink action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('cdp-relatorios')" value="Componentes curriculares reprovados ou trancados em determinado período">
				<f:param value="seleciona_reprovados_trancados.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
--%>	
		<li>
			<h:commandLink value="Alunos com todos os componentes curriculares reprovados em um período letivo" 
				action="#{relatoriosJasper.iniciarAlunosTodosComp_Reprov}" onclick="setAba('cdp-relatorios')"/>
		</li>
		<li>
			<h:commandLink value="Alunos com todos os componentes curriculares trancados em um período letivo" 
				action="#{relatoriosJasper.iniciarAlunosTodosCompTranc}" onclick="setAba('cdp-relatorios')"/>
		</li>
		<li>
			<h:commandLink value="Relatório de Integralização de Currículo" action="#{ relatorioIntegralizacaoCurriculoMBean.iniciar }" onclick="setAba('cdp-relatorios')"/>
		</li>
		<li>
			<h:commandLink action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('cdp-relatorios')" value="Tipo de saída por forma de ingresso/egresso">
				<f:param value="seleciona_tipo_saida.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Alunos com trancamento em um determinado componente curricular">
				<f:param value="seleciona_trancamentos_componente.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li> <h:commandLink value="Relatório de Apostilamentos" action="#{relatoriosJasper.iniciarApostilamentos}" onclick="setAba('cdp-relatorios')"/> </li>
		</ul>
	</li>
	<li> Docentes 
		<ul>
			<li> <h:commandLink value="Situação Docente Atual" action="#{relatoriosDepartamentoCpdi.iniciarSituacaoDocente}" onclick="setAba('cdp-relatorios')"/> </li>
		</ul>	
	</li>
	<li> Estruturas curriculares 
		<ul>
			<li> <h:commandLink value="Relatório de Estruturas Curriculares" action="#{relatoriosJasper.iniciarEstruturasCurriculares}" onclick="setAba('cdp-relatorios')"/> </li>
		</ul>	
	</li>
	<li> Cursos 
		<ul>
			<li> <h:commandLink value="Unidade/Curso/Turno/Cidade/Modalidade/Habilitacao" action="#{relatoriosDepartamentoCpdi.relatorioQuantidadeCurso}" onclick="setAba('cdp-relatorios')"/> </li>
			<li> <h:commandLink value="Cursos com Reconhecimento" action="#{relatorioCursoReconhecimentoMBean.iniciar}" onclick="setAba('cdp-relatorios')"/> </li>
		</ul>	
	</li>	
</ul>
