<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Matricular </h2>
<br/>

<f:view>

<h:form id="matricula">
<table class="formulario" width="70%">
<caption>Informe os Dados para a Matrícula</caption>
<tbody>
<tr>
	<th class="obrigatorio"> Aluno: </th>
	<td> <h:inputHidden id="idDiscente" value="#{ matriculaTecnico.discente.id }"/>
		 <h:inputText id="nomeDiscente" value="#{ matriculaTecnico.discente.pessoa.nome }" size="60"/>

		<ajax:autocomplete source="matricula:nomeDiscente" target="matricula:idDiscente"
			baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
			indicator="indicatorDiscente" minimumCharacters="3" parameters="ignorarUnidade=sim" postFunction="retrieveInfoTurmas"
			parser="new ResponseXmlToHtmlListParser()" />

		<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
	</td>
</tr>
<c:if test="${ matriculaTecnico.tipo == 'alunoTurma' }">
<tr>
	<th class="obrigatorio">Turma: </th>
	<td>
		<c:choose>
			<c:when test="${ lato.portalCoordenadorLato }">	
	
				<h:selectOneMenu id="turma" value="#{ matriculaTecnico.turma.id }" style="width: 100%">
					<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
					<f:selectItems value="#{ lato.turmasMatricula }"/>
				</h:selectOneMenu>
	
			</c:when>
			<c:otherwise>
	
				<h:inputHidden id="idTurma" value="#{ matriculaTecnico.turma.id }"/>
				<h:inputText id="nomeTurma" value="#{ matriculaTecnico.turma.disciplina.nome }" size="60"/>
				<ajax:autocomplete source="matricula:nomeTurma" target="matricula:idTurma"
					baseUrl="/sigaa/ajaxTurma" className="autocomplete"
					indicator="indicatorTurma" minimumCharacters="3" parameters=""
					parser="new ResponseXmlToHtmlListParser()" />
		
				<span id="indicatorTurma" style="display:none; "> <img src="/sigaa/img/indicator.gif" alt="Carregando..." title="Carregando..."/> </span>
	
			</c:otherwise>
		</c:choose>
	</td>
</tr>
</c:if>
<c:if test="${ matriculaTecnico.tipo == 'alunoModulo' }">
<tr>
	<th class="obrigatorio"> Curso: </th>
	<td> <h:selectOneMenu id="curso" value="#{ matriculaTecnico.curso.id }" onchange="submit()" immediate="true">
			<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
			<f:selectItems value="#{ matriculaTecnico.cursos }"/>
		 </h:selectOneMenu> </td>
</tr>
<tr>
	<th class="obrigatorio">Módulos:</th>
	<td>
		<h:selectOneMenu id="modulo" value="#{ matriculaTecnico.modulo.id }">
		<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
			<f:selectItems value="#{ matriculaTecnico.modulos }"/>
		</h:selectOneMenu>
	</td>
</tr>
</c:if>
</tbody>

<tfoot>
<tr>
	<td colspan="2">
		<h:commandButton value="Cancelar" action="#{ matriculaTecnico.cancelar }" immediate="true" onclick="#{confirm}"/>
		<h:commandButton value="Avançar >>" action="#{ matriculaTecnico.realizarMatricula }"/>
	</td>
</tfoot>

</table>
</h:form>

<br/>&nbsp;

<div id="loading"></div>
<div id="turmas"></div>

	<center>	
		<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
<br/>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
Element.hide('turmas');
</script>