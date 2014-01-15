<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
<h2> <a href="/sigaa/public"/>SIGAA - </a> Especializações por Centro </h2>


<f:view>
	<%--

	 --%>
	<table class="formulario" width="100%">
	<caption>Especializações</caption>
		<tr>
			<td>
			<table class="formulario" width="100%">
				<caption>CB - Centro de Biociências</caption>
				<thead>
				<td></td>
				<td></td>
				</thead>
				<tbody>
				<c:forEach items="${lato.allAtivos}" var="curso">
					<c:if test="${curso.unidade.unidadeResponsavel.id == 440}">
					<tr>
					<h:form>
						<input type="hidden" name="idCurso" id="idCurso" value="${curso.id}"/>
						<td> <span>${curso.descricao}</span> </td>
						<td align="right"><h:commandLink value="Detalhes" action="#{lato.verCurso}"/></td>
					</h:form>
					</tr>
					</c:if>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		<tr>
			<td>
			<table class="formulario" width="100%">
				<caption>CCET - Centro de Ciências Exatas e da Terra</caption>
				<thead>
				<td></td>
				<td></td>
				</thead>
				<tbody>
				<c:forEach items="${lato.allAtivos}" var="curso">
					<c:if test="${curso.unidade.unidadeResponsavel.id == 439}">
					<tr>
					<h:form>
						<input type="hidden" name="idCurso" id="idCurso" value="${curso.id}"/>
						<td> <span>${curso.descricao}</span> </td>
						<td align="right"><h:commandLink value="Detalhes" action="#{lato.verCurso}"/></td>
					</h:form>
					</tr>
					</c:if>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		<tr>
			<td>
			<table class="formulario" width="100%">
				<caption>CCHLA - Centro de Ciências Humanas Letras e Artes</caption>
				<thead>
				<td></td>
				<td></td>
				</thead>
				<tbody>
				<c:forEach items="${lato.allAtivos}" var="curso">
					<c:if test="${curso.unidade.unidadeResponsavel.id == 442}">
					<tr>
					<h:form>
						<input type="hidden" name="idCurso" id="idCurso" value="${curso.id}"/>
						<td> <span>${curso.descricao}</span> </td>
						<td align="right"><h:commandLink value="Detalhes" action="#{lato.verCurso}"/></td>
					</h:form>
					</tr>
					</c:if>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		<tr>
			<td>
			<table class="formulario" width="100%">
				<caption>CCSA - Centro de Ciências Sociais Aplicadas</caption>
				<thead>
				<td></td>
				<td></td>
				</thead>
				<tbody>
				<c:forEach items="${lato.allAtivos}" var="curso">
					<c:if test="${curso.unidade.unidadeResponsavel.id == 443}">
					<tr>
					<h:form>
						<input type="hidden" name="idCurso" id="idCurso" value="${curso.id}"/>
						<td> <span>${curso.descricao}</span> </td>
						<td align="right"><h:commandLink value="Detalhes" action="#{lato.verCurso}"/></td>
					</h:form>
					</tr>
					</c:if>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		<tr>
			<td>
			<table class="formulario" width="100%">
				<caption>CCS - Centro de Ciências da Saúde</caption>
				<thead>
				<td></td>
				<td></td>
				</thead>
				<tbody>
				<c:forEach items="${lato.allAtivos}" var="curso">
					<c:if test="${curso.unidade.unidadeResponsavel.id == 441}">
					<tr>
					<h:form>
						<input type="hidden" name="idCurso" id="idCurso" value="${curso.id}"/>
						<td> <span>${curso.descricao}</span> </td>
						<td align="right"><h:commandLink value="Detalhes" action="#{lato.verCurso}"/></td>
					</h:form>
					</tr>
					</c:if>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		<tr>
			<td>
			<table class="formulario" width="100%">
				<caption>CT - Centro de Tecnologia</caption>
				<thead>
				<td></td>
				<td></td>
				</thead>
				<tbody>
				<c:forEach items="${lato.allAtivos}" var="curso">
					<c:if test="${curso.unidade.unidadeResponsavel.id == 445}">
					<tr>
					<h:form>
						<input type="hidden" name="idCurso" id="idCurso" value="${curso.id}"/>
						<td> <span>${curso.descricao}</span> </td>
						<td align="right"><h:commandLink value="Detalhes" action="#{lato.verCurso}"/></td>
					</h:form>
					</tr>
					</c:if>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		<tr>
			<td>
			<table class="formulario" width="100%">
				<caption>CERES - Centro de Ensino Superior do Seridó</caption>
				<thead>
				<td></td>
				<td></td>
				</thead>
				<tbody>
				<c:forEach items="${lato.allAtivos}" var="curso">
					<c:if test="${curso.unidade.unidadeResponsavel.id == 1482}">
					<tr>
					<h:form>
						<input type="hidden" name="idCurso" id="idCurso" value="${curso.id}"/>
						<td> <span>${curso.descricao}</span> </td>
						<td align="right"><h:commandLink value="Detalhes" action="#{lato.verCurso}"/></td>
					</h:form>
					</tr>
					</c:if>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
	</table>
	
	<br>
	<center>
	<a href="/sigaa/public"/> << Voltar </a>
	</center>

</f:view>
<%@include file="/public/include/rodape.jsp" %>