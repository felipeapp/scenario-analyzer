<ul>
	 <li>Aluno
		<ul>
			<li> <h:commandLink id="buscaGeralDiscente" action="#{ buscaAvancadaDiscenteMBean.iniciarMedio }" value="Consulta Geral de Discentes" onclick="setAba('pedagogico')"/></li>
			<li> <h:commandLink id="emitirAtestadoMatriculaMedio" action="#{ atestadoMatriculaMedio.buscarDiscente }" value="Emitir Atestado de Matrícula" onclick="setAba('pedagogico')"/> </li>
			<li> <h:commandLink id="emitirBoletim"	action="#{boletimMedioMBean.iniciar}" value="Emitir Boletim" onclick="setAba('pedagogico')"/></li>
			<li> <h:commandLink id="emitirHistorico" action="#{historicoMedio.buscarDiscente}" value="Emitir Histórico" onclick="setAba('pedagogico')"/></li>
		</ul>
	</li>
	<li>Turma
		<ul>
			<li> <h:commandLink id="resultadoProcessamentoMatriculaSerie" action="#{processamentoMatriculaDiscenteSerie.iniciarResultado}" value="Resultado de Consolidação de Discentes em Série" onclick="setAba('pedagogico')"/> </li>
		</ul>
	</li>
	<li>Consultas Gerais 
	    <ul>
	        <li><h:commandLink action="#{discenteMedio.listar}" value="Aluno" onclick="setAba('pedagogico')" /> </li>
	        <li><h:commandLink action="#{turmaSerie.listar}" value="Turma" onclick="setAba('pedagogico')" /> </li>
	  		<li><h:commandLink action="#{cursoMedio.listar}" value="Curso"  onclick="setAba('pedagogico')" /></li>
	  		<li><h:commandLink action="#{disciplinaMedioMBean.listar}" value="Disciplina"  onclick="setAba('pedagogico')"/></li>
	  		<li><h:commandLink action="#{curriculoMedio.listar}" value="Estrutura Curricular"  onclick="setAba('pedagogico')" /></li>
	    </ul>
	</li>  
 </ul>