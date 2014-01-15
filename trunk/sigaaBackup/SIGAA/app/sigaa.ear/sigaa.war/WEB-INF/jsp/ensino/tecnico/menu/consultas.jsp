<ul>
	<li>Consultas Gerais
		<ul>
			<li><h:commandLink action="#{discenteTecnico.listar }" value="Aluno" onclick="setAba('consultas')"/> </li>
	        <li><h:commandLink action="#{buscaTurmaBean.popularBuscaTecnico}" value="Turma" onclick="setAba('consultas')"/> </li>
	  		<li><h:commandLink action="#{cursoTecnicoMBean.listar}" value="Curso"  onclick="setAba('consultas')"/></li>
	  		<li><h:commandLink action="#{componenteCurricular.listar}" value="Componente Curricular"  onclick="setAba('consultas')"/></li>
	  		<li><h:commandLink action="#{estruturaCurricularTecnicoMBean.listar}" value="Estrutura Curricular"  onclick="setAba('consultas')"/></li>
		</ul>
	</li>
</ul>