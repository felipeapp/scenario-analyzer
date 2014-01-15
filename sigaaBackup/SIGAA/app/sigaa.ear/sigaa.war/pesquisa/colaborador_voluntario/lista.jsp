<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Remoção de Colaboradores Voluntários</h2>
	<h:outputText value="#{colaboradorVoluntario.create}" />
	
	<c:if test="${empty colaboradorVoluntario.resultadosBusca}">
		<br><div style="font-style: italic; text-align:center">Nenhum registro a ser exibido</div>
	</c:if>
	<c:if test="${not empty colaboradorVoluntario.resultadosBusca}">
		<br>
		<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:
			Remover Colaborador<br/>
		</div>
		</center>
	<h:form>
			<table class=listagem>
			<caption class="listagem">Lista de Colaboradores Encontrados</caption>
			<thead>
				<tr>
					<td>Servidor</td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="#{colaboradorVoluntario.resultadosBusca}" var="item">
				<tr>
					<td>${item.servidor.pessoa.nome}</td>
					<td width="5%">
						<h:commandLink title="Remover Colaborador" action="#{colaboradorVoluntario.preRemover}">
							<f:param value="#{item.id}" name="id"/>
							<h:graphicImage url="/img/delete.gif"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</table>
	</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
