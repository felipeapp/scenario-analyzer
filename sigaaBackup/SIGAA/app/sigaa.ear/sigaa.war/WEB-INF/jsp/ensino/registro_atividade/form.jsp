<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<html:form action="/ensino/registroAtividade?dispatch=dadosBasicos">
    <table class="formulario">
        <caption> Dados do Aluno </caption>
        <thead>
        <tr>
            <td> Aluno </td>
            <td>
                <c:set var="idAjax" value="obj.discente.id"/>
                <c:set var="nomeAjax" value="obj.discente.pessoa.nome"/>
				<c:set var="obrigatorio" value="true" />
                <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
            </td>
        </tr>

        <tr>
            <td> Disciplina </td>
            <td>
                <c:set var="idAjax" value="obj.disciplina.id"/>
                <c:set var="nomeAjax" value="obj.disciplina.nome"/>
                <c:set var="parameter" value="atividade=true"/>
                <%@include file="/WEB-INF/jsp/include/ajax/disciplina.jsp" %>
            </td>
        </tr>

        <tfoot>
        <tr>
            <td colspan="2">
                <ufrn:button action="ensino/registroAtividade" param="dispatch=cancelar" value="Cancelar">Cancelar</ufrn:button>
                <html:submit>Avançar >></html:submit>
            </td>
        </tr>

        </tfoot>
    </table>
</html:form>

<br><br>
<center>
    <ufrn:link action="/ensino/registroAtividade" param="dispatch=cancelar">Menu do Ensino Técnico</ufrn:link>
</center>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>