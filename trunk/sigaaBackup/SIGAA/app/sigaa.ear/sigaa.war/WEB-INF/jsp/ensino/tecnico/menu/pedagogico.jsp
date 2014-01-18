<ul>
	<li>Aluno
		<ul>
			<li> <h:commandLink id="buscaGeralDiscente" action="#{ buscaAvancadaDiscenteMBean.iniciarTecnico}" value="Consulta Geral de Discentes" onclick="setAba('pedagogico')"/></li>
			<li> <h:commandLink id="emitirAtestadoMatriculaTecnico" action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matrícula" onclick="setAba('pedagogico')"/> </li>
            <li> <h:commandLink id="emitirHistoricoTecnico"	action="#{ historico.buscarDiscente }"	value="Emitir Histórico" onclick="setAba('pedagogico')"/></li>
		</ul>
	</li>
	<li>Consultas Gerais
		<ul>
			<li><h:commandLink action="#{discenteTecnico.listar }" value="Aluno" onclick="setAba('pedagogico')"/> </li>
	        <li><h:commandLink action="#{buscaTurmaBean.popularBuscaTecnico}" value="Turma" onclick="setAba('pedagogico')"/> </li>
	  		<li><h:commandLink action="#{cursoTecnicoMBean.listar}" value="Curso"  onclick="setAba('consultas')"/></li>
	  		<li><h:commandLink action="#{componenteCurricular.listar}" value="Componente Curricular"  onclick="setAba('pedagogico')"/></li>
	  		<li><h:commandLink action="#{estruturaCurricularTecnicoMBean.listar}" value="Estrutura Curricular"  onclick="setAba('pedagogico')"/></li>
		</ul>
	</li>
 </ul>
