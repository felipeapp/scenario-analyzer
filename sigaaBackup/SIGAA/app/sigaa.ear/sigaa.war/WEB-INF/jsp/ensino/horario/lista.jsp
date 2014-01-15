<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema /> &gt;
	Horários
</h2>


<c:if test="${lista == null || empty lista}">
<br>
<div style="font-style: italic; text-align:center">
Nenhum registro a ser exibido.
</div>
</c:if>

<c:if test="${not empty lista}">
<br />

	    <table class="listagem">
		<caption class="listagem">Horários</caption>
	        <thead>
	        <tr>
		        <td>Hora Início</td>
		        <td>Hora Fim</td>
		        <td>Turno</td>
		        <td>Ordem</td>
		    </tr>
	        </thead>
	        <tbody>


	        <c:forEach items="${lista}" var="horario">
	            <tr>
                    <td><ufrn:format type="hora" valor="${horario.inicio}"/></td>
                    <td><ufrn:format type="hora" valor="${horario.fim}"/></td>
                    <td>${horario.turno}</td>
                    <td>${horario.ordem } &ordm; horário</td>
	            </tr>
	        </c:forEach>
	    </table>

</c:if>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
