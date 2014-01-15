<ul>
	<li> Relatórios NEE
		<ul>
			<li> <h:commandLink action="#{ solicitacaoApoioNee.listaAllSolicitacoes }" value="Listar Alunos com NEE" onclick="setAba('alunos')"/> </li>
			<li> <h:commandLink action="#{relatorioNee.iniciarRelatorioAlunosPorVestibular}"	value="Alunos com NEE por Processo Seletivo" onclick="setAba('consultas')" /> </li>
			<li> <h:commandLink id="report_alunosNecessidadesEspeciais" action="#{relatoriosJasper.iniciarNecessidadesEspeciais}" value="Alunos por Tipo de Necessidades Especiais" onclick="setAba('consultas')" /> </li>
		</ul>
	</li>

	<li> Consultas Gerais
		<ul>
		<li> <h:commandLink action="#{historicoDiscente.iniciar}" value="Alunos" onclick="setAba('consultas')"/> </li>
		<li> <h:commandLink action="#{ buscaAvancadaDiscenteMBean.iniciar}" value="Consulta Geral de Discentes"/></li>
		<li> <h:commandLink action="#{ componenteCurricular.popularBuscaGeral }" value="Componentes Curriculares" onclick="setAba('consultas')"/> </li>
		<li> <h:commandLink action="#{ cursoGrad.listar }" value="Cursos" onclick="setAba('consultas')"/> </li>		
		<li> <a href="${ctx}/graduacao/curriculo/lista.jsf?aba=consultas">Estruturas Curriculares </a></li>
		<li> <h:commandLink action="#{ orientacaoAtividadeNee.iniciarBusca }" value="Orientação de Atividades" onclick="setAba('consultas')"/> </li>
		<li> <h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Turmas" onclick="setAba('consultas')"/> </li>
		</ul>
	</li>
</ul>
</ul>	