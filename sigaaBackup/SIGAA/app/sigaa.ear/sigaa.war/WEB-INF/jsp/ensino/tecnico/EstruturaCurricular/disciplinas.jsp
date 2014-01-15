<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
<ufrn:steps /></h2>

<html:form action="/ensino/tecnico/estruturaCurricular/wizard"  method="post" focus="disciplinaComplementar.disciplina.id" >
	<html:hidden property="disciplinaComplementar.estruturaCurricularTecnica.id"
			value="${ estruturaCurTecnicoForm.obj.id }" />
	<table class="formulario" width="90%">
	<caption>Adicionando Disciplinas Eletivas</caption>
	<tbody>
		<tr>
		<th class="obrigatorio">
		Disciplina:
		</th>
		<td>
		<c:set var="idAjax" value="disciplinaComplementar.disciplina.id"/>
    	<c:set var="nomeAjax" value="disciplinaComplementar.disciplina.nome"/>
		<%@include file="/WEB-INF/jsp/include/ajax/disciplina.jsp" %>
		</td>
		</tr>
		<tr>
		<th>
		Período de Oferta:
		</th>
		<td>
		<html:text property="disciplinaComplementar.periodoOferta" maxlength="2" size="2" />
		</td>
		</tr>

	</tbody>

	<tfoot>
		<tr>
		<td colspan="2">
		<html:button dispatch="addDisciplinaComplementar" value="Adicionar"/>
		</td>
		</tr>
	</tfoot>
	</table>

<br>
<!-- mensagem -->
<center><i style="color:gray">
Obs.: O cadastro de disciplinas complementares (ou eletivas) não acrescenta <br>
carga horária para a estrutura curricular.
</i></center>
<br>
<c:if test="${ empty estruturaCurTecnicoForm.obj.disciplinasComplementares }">
<br><center><i>Nenhuma disciplina complementar adicionada.</i></center>
</c:if>
<c:if test="${ not empty estruturaCurTecnicoForm.obj.disciplinasComplementares }">
<table class="listagem" >
	<caption class="listagem">Disciplinas Complementares Cadastradas</caption>
	<thead>
	<td>Disciplina</td>
	<td width="8%" align="center">C.H.</td>
	<td width="12%" align="center">Pr. Oferta</td>
	<td width="12%" align="center"> </td>
	</thead>

	<tbody>
	<c:forEach items="${estruturaCurTecnicoForm.obj.disciplinasComplementares}" var="disciplinaComplementar" varStatus="status">
<%--		<html:hidden property="disciplinaId" value="${disciplinaComplementar.disciplina.id}" />
		<html:hidden property="discComplId" value="${disciplinaComplementar.id}" />
--%>		<tr>
		<td>${disciplinaComplementar.disciplina.nome}</td>
		<td align="center">${disciplinaComplementar.disciplina.chTotal}</td>
		<td align="center">${disciplinaComplementar.periodoOferta}</td>
		<td align="center">
<%-- 		<html:button dispatch="remDisciplinaComplementar" value="Remover" /> --%>
			<html:link action="/ensino/tecnico/estruturaCurricular/wizard?dispatch=remDisciplinaComplementar&disciplinaId=${disciplinaComplementar.disciplina.id}&discComplId=${disciplinaComplementar.id}&posLista=${status.index}">
           			<img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover esta Disciplina do currículo" title="Remover" border="0"/>
            </html:link>
		</td>
		</tr>
	</c:forEach>
</table>
</c:if>

<center>
<br><br>
	<html:button view="dadosGerais">&lt;&lt; Dados Gerais</html:button>
	<html:button view="modulos">&lt;&lt; Módulos</html:button>
	<html:button dispatch="cancelar">Cancelar</html:button>
	<html:button dispatch="submeterDisciplinas">Próximo &gt;&gt;</html:button>
</html:form>

<br><br>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>