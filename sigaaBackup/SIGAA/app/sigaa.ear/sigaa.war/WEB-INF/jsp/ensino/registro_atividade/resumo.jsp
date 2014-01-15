<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2 class="tituloPagina">
	<ufrn:subSistema /> &gt;
	 Registro de Atividade > Confirmação
</h2>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>
<c:if test="${acao == 'remover'}">
	<span class="subtitle">
		<fmt:message key="mensagem.confirma.remocao">
			<fmt:param value="da Turma de Técnico"></fmt:param>
		</fmt:message>
	</span>
	<br>
	<br>
</c:if>

<html:form action="ensino/registroAtividade.do?dispatch=persist">

    <div class="areaDeDados">

        <h2>Dados do Registro de Atividade</h2>
        <div class="dados">

			<div class="head">Aluno:</div>
            <div class="texto"> ${formRegistroAtividade.obj.discente.pessoa.nome} - (Mat. ${formRegistroAtividade.obj.discente.matricula})</div>

			<div class="head">Disciplina:</div>
            <div class="texto"> ${ formRegistroAtividade.obj.disciplina.nome }</div>

            <div class="head">Docentes:</div>
            <div class="texto">
                <table>
                <c:forEach items="${ formRegistroAtividade.obj.orientacoesAtividade }" var="o">
                    <tr>
                        <td> ${ o.servidor.pessoa.nome } </td>
                        <td> ${ o.cargaHoraria } horas </td>
                    </tr>
                </c:forEach>
                </table>
            </div>

        </div>

    	<div class="botoes">
    		<html:submit>Confirmar</html:submit>
            <ufrn:button action="ensino/registroAtividade" param="dispatch=cancelar" value="Cancelar"/>
            <ufrn:button action="ensino/registroAtividade" param="dispatch=dadosBasicos" value="<< Inserir Docente"/>
        </div>
	</div>
</html:form>

<br><br>
<center>
	<ufrn:link action="/ensino/registroAtividade" param="dispatch=cancelar">Menu do Ensino Técnico</ufrn:link>
</center>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>