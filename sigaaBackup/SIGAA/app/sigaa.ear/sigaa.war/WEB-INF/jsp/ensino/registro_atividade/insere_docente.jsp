<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.areaDeDados {
  width: 85%;
}
.areaDeDados .dados .texto {
  margin-left: 30%;
  text-align: left;
}
.areaDeDados .dados .texto2 {

  margin-left: 15%;
  text-align: left;
}
</style>

<h2 class="tituloPagina">
		<ufrn:subSistema /> &gt;
	Registro de Atividade
</h2>



<center>
<br>
<table class="formulario" cellpadding="3">
<caption class="listagem">Atividade</caption>
<tr>
	<th style="fontbweight: "> <b>Aluno:</b> </th>
	<td> ${formRegistroAtividade.obj.discente.pessoa.nome} (Mat. ${formRegistroAtividade.obj.discente.matricula})</td>
</tr>
<tr>
	<th style="font-weight: bold;"> Disciplina: </th>
	<td>
        ${formRegistroAtividade.obj.disciplina.nome }
	</td>
</tr>


</table>

<br>

<html:form action="/ensino/registroAtividade?dispatch=insereDocente" focus="docente">
	<table class="formulario">
	<caption class="listagem">zs</caption>
	<tr>
		<td>
		Docente:
		</td>
		<td>
		<c:set var="idAjax" value="docente.id"/>
		<c:set var="nomeAjax" value="docente.pessoa.nome"/>
		<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>

		</td>
	</tr>
	<tr>
		<td>
		Carga Horária:
		</td>
		<td>
		<html:text property="cargaHoraria" size="4" value=""/>
		</td>
	</tr>
	<tfoot>
		<tr>
		<td colspan=2>
		<html:submit onclick="submitMethod('insereDocente', this)"><fmt:message key="botao.adicionar" /></html:submit>
		</td>
		</tr>

	</tfoot>
	</table>

</html:form>

<c:if test="${not empty formRegistroAtividade.obj.orientacoesAtividade}">
<br>
    <table class="listagem" width="80%">
	  <caption class="listagem">Docentes</caption>
        <thread>
        <th>Nome</th>
        <th>Carga Horária</th>
        <th> </th>
        </thread>
        <tbody>

        <c:forEach items="${formRegistroAtividade.obj.orientacoesAtividade}" var="orientacaoAtividade">
            <tr>
                <html:form action="/ensino/registroAtividade?dispatch=removeDocente">
                    <html:hidden property="docente.id" value="${orientacaoAtividade.servidor.id}" />
                    <td>${orientacaoAtividade.servidor.pessoa.nome}</td>
                    <td  align="right">${orientacaoAtividade.cargaHoraria}</td>
                    <td align="center">
                        <html:image style="border: none" page="/img/delete.gif" alt="Remover Professor" title="Remover" value="Remover"></html:image>
                    </td>
                </html:form>
            </tr>
        </c:forEach>
    </table>
</c:if>
<br>

<br>
<center>
	<html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/ensino/registroAtividade.do?dispatch=navegar&view=form'"><< Dados Básicos</html:button>
    <html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/ensino/registroAtividade.do?dispatch=cancelar'"> Cancelar </html:button>
	<html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/ensino/registroAtividade.do?dispatch=confirmar'">Confirmar >> </html:button>
</center>

<br><br>
<center>
		<ufrn:link action="/ensino/registroAtividade" param="dispatch=cancelar">Menu do Ensino Técnico</ufrn:link>
</center>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
