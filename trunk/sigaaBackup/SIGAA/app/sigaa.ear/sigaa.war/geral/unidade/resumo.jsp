<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<c:if test="${acesso.discente}">
	<%@include file="/portais/discente/menu_discente.jsp" %>
</c:if>
<c:if test="${acesso.docente}">
	<%@include file="/portais/docente/menu_docente.jsp" %>
</c:if>
<h2><ufrn:subSistema/> > Detalhes da Unidade Acadêmica</h2>

<h:form>
	<table class="formulario" width="100%">
		<caption>Dados da Unidade Acadêmica</caption>

		<tbody>
			<tr>
				<td width="47px"><b>Código: </b> </td><td><h:outputText value="#{unidade.obj.codigo}" /></td>
			</tr>

			<tr>
				<td><b>Nome: </b></td><td><h:outputText value="#{unidade.obj.nome}" /></td>
			</tr>

			<tr>
				<td><b>Sigla: </b></td><td><h:outputText value="#{unidade.obj.sigla}" /></td>
			</tr>

			<tr>
				<td><b>Tipo:</b> </td><td><h:outputText value="#{unidade.obj.tipoAcademicaDesc}" /></td>
			</tr>
				
					<c:if test="${ not empty unidade.equipePrograma}">
					<tr>
						<th><b>Equipe do programa:</b></th>
						<td></td>
					</tr>
						<c:forEach var="n" items="#{ unidade.equipePrograma }" varStatus="loop">
								<tr>
								<td></td>
									<td>${ n.servidor.siape } - ${ n.servidor.pessoa.nome }</td>					
									<td>${ n.vinculo.denominacao }</td>					
									<td>${ n.nivel.denominacao }</td>					
								</tr>
						</c:forEach>
					</c:if>

					<c:if test="${ not empty unidade.areas}">
						<tr>
							<th><b>Áreas:</b></th>
							<td></td>
						</tr>
						<c:forEach var="areas" items="#{ unidade.areas }" varStatus="loop">
								<tr>
								<th></th>
									<td>${ areas.denominacao }</td>					
								</tr>
						</c:forEach>
					</c:if>
				
					<c:if test="${ not empty unidade.linhasPesquisas}">
						<tr>
							<th><b>Linhas de pesquisa:</b></th>
							<td></td>
						</tr>
						<c:forEach var="lp" items="#{ unidade.linhasPesquisas }" varStatus="loop">
								<tr>
								<th></th>
									<td>${ lp.denominacao }</td>					
								</tr>
						</c:forEach>
					</c:if>
			
				
		</tbody>
	</table>

</h:form>
	<br>
	<center>
	<a href="javascript: history.go(-1);"> << Voltar </a>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
