<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<f:view>
<f:subview id="menu">
	<%@include file="/portais/docente/menu_docente.jsp" %>
</f:subview>
<h2 class="tituloPagina"> <ufrn:subSistema /> > Cadastrar Notas</h2>

<div class="descricaoOperacao">

Selecione uma turma aberta abaixo para modificar as notas.

</div>


<h:form id="escolher">

<h:messages showDetail="true"/>
<input type="hidden" name="gestor" value="${ param['gestor'] }"/>

<ufrn:checkNotRole papeis="<%= new int[]{SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR} %>">
<div class="infoAltRem" style="width: 100%">
	<h:graphicImage value="/img/seta.gif"style="overflow: visible;" />: Inserir Notas
	<ufrn:checkNotRole papeis="<%= new int[]{SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO} %>">
		<h:graphicImage value="/img/avaliar.gif" style="overflow: visible;" />: Gerenciar Plano de Curso
	</ufrn:checkNotRole>
</div>
</ufrn:checkNotRole>

<table class="formulario" width="100%">
<caption>Selecione uma Turma</caption>
<tbody>

<!-- Caso seja apenas um professor -->
<c:if test="${ !consolidarTurma.fromMenuGestor }">
<c:forEach var="t" items="#{ consolidarTurma.turmasProfessor }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
	<td>${ t.nome }</td>
	<td style="width:20px;">
 		<h:commandLink rendered="#{ t.disciplina.nivel != 'M' }" action="#{ consolidarTurma.turmaEscolhida }">
 			<h:graphicImage value="/img/seta.gif" alt="Inserir Notas" title="Inserir Notas"/>
 			<f:param name="idTurma" value="#{ t.id }"/>
 		</h:commandLink>
 		<h:commandLink rendered="#{ t.disciplina.nivel == 'M' }" action="#{ consolidarDisciplinaMBean.selecionarTurmaDisciplina }">
 			<h:graphicImage value="/img/seta.gif" alt="Inserir Notas" title="Inserir Notas"/>
 			<f:param name="id" value="#{ t.id}"/>
 		</h:commandLink>
	</td>
	<td style="width:20px;">
		<h:commandLink action="#{planoCurso.gerenciarPlanoCurso}" title="Gerenciar Plano de Curso">
			<h:graphicImage value="/img/avaliar.gif" />
			<f:param name="idTurma" value="#{ t.id }" />
		</h:commandLink>
	</td>
</tr>
</c:forEach>
</c:if>

<!-- Caso seja Gestor de Técnico ou de Lato -->
<c:if test="${ consolidarTurma.fromMenuGestor }"> 

<ufrn:checkRole papeis="<%= new int[]{SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR} %>">
	<tr>
		<th width="15%" class="required">Turma: </th>
		<td>
		
		<h:inputHidden id="idTurma" value="#{ consolidarTurma.turma.id }"  rendered="#{ consolidarTurma.fromMenuGestor }"/>
		<h:inputText id="nomeTurma" value="#{ consolidarTurma.turma.disciplina.nome }" size="100"  rendered="#{ consolidarTurma.fromMenuGestor }"/>
			<ajax:autocomplete source="escolher:nomeTurma" target="escolher:idTurma"
				baseUrl="/sigaa/ajaxTurma" className="autocomplete"
				indicator="indicatorTurma" minimumCharacters="3" parameters=""
				parser="new ResponseXmlToHtmlListParser()" />
	
			<span id="indicatorTurma" style="display:none; "> <img src="/sigaa/img/indicator.gif" alt="Carregando..." title="Carregando..."/> </span>
		</td>
	</tr>
</ufrn:checkRole>

<!-- Caso seja coordenador de curso Lato Sensu -->
<ufrn:checkRole papeis="<%= new int[]{SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO} %>">
<c:forEach var="t" items="#{ consolidarTurma.turmasCoordenadorLato }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
	<td>${ t.label }</td>
	<td style="width:20px;">
 		<h:commandLink action="#{ consolidarTurma.turmaEscolhida }">
 			<h:graphicImage value="/img/seta.gif" alt="Inserir Notas" title="Inserir Notas"/>
 			<f:param name="idTurma" value="#{ t.value }"/>
 		</h:commandLink>
	</td>
	<td style="width:0px;"></td>
</tr>
</c:forEach>
</ufrn:checkRole>
</c:if>

</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<c:if test="${ consolidarTurma.fromMenuGestor }">
					<h:commandButton action="#{ consolidarTurma.escolherTurma }" value="Inserir Notas >>"/>
				</c:if>
			
				<h:commandButton action="#{ consolidarTurma.cancelar }" value="Cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>

</table>

</h:form>

<div align="center" class="required-items">
	<span class="required">&nbsp;</span>
	Itens de Preenchimento Obrigatório
</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
