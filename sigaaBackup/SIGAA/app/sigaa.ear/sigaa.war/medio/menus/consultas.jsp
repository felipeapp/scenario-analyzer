<ul>
	<li>Consultas Gerais 
	    <ul>
	        <li><h:commandLink action="#{discenteMedio.listar}" value="Aluno" onclick="setAba('consultas')" /> </li>
	        <li><h:commandLink action="#{turmaSerie.listar}" value="Turma" onclick="setAba('consultas')" /> </li>
	  		<li><h:commandLink action="#{cursoMedio.listar}" value="Curso"  onclick="setAba('consultas')" /></li>
	  		<li><h:commandLink action="#{disciplinaMedioMBean.listar}" value="Disciplina"  onclick="setAba('consultas')"/></li>
	  		<li><h:commandLink action="#{curriculoMedio.listar}" value="Estrutura Curricular"  onclick="setAba('consultas')" /></li>
	    </ul>
	</li>  
 </ul>