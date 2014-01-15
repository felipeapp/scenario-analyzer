<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<a4j:keepAlive beanName="registroAcaoAva" />


	<h:form>
		<table class="formulario">
			<caption>Filtros</caption>
			<tr><th>Período:</th><td>de xx/xx/xxxx a xx/xx/xxxx</td></tr>
			<tr><th>Relatório:</th>
				<td>
					<h:selectOneRadio value="#{registroAcaoAva.entidade}">
						<f:selectItem itemValue="1" itemLabel="Acessos" />
						<f:selectItem itemValue="2" itemLabel="Tópicos de Aulas" />
						<f:selectItem itemValue="3" itemLabel="Arquivos" />
						<f:selectItem itemValue="4" itemLabel="Tarefas" />
						<f:selectItem itemValue="5" itemLabel="Indicação de Referência" />
						<f:selectItem itemValue="11" itemLabel="Conteúdo" />
					</h:selectOneRadio>
				</td>
			</tr>
			
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Gerar Relatório" action="#{registroAcaoAva.relatorioAcessos}" />
				</td></tr>
			</tfoot>
		</table>
	
	</h:form>

	<table>
		<tr>
			<th>Nome</th>
			<th></th>
		</tr>
		
		<c:forEach items="#{registroAcaoAva.registros}" var="r">
			<td>${ r.nome }</td>
			<c:forEach items="#{r.itens}" var="i">
				
			</c:forEach>
		
		</c:forEach>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>