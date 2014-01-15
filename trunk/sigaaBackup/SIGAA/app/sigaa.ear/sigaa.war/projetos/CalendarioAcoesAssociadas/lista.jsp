<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
<a4j:keepAlive beanName="calendarioAcoesAssociadas" />
	<h:form>
		<h2><ufrn:subSistema />&gt; Configurar Calend�rio de A��es Integradas</h2>
		<center>
			<h:messages />
			<div class="infoAltRem">
					<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />
						<h:commandLink value="Cadastrar Calendario" action="#{ calendarioAcoesAssociadas.preCadastrar }" id="linkCadastrar"/>
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Calend�rio de A��es Integradas
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Calend�rio de A��es Integradas				
			</div>
		</center>
		
		<c:if test="${not empty calendarioAcoesAssociadas.calendarios}">
			<table class="listagem">
				<caption>Lista de Calend�rios de A��es de Integradas</caption>
				<thead>
					<td style="text-align: center;">Ano</td>
					<td style="text-align: center;">Data de in�cio do cadastro dos Planos de Trabalho</td>
					<td style="text-align: center;">Data de fim do cadastro dos Planos de Trabalho</td>
					<td></td>
					<td></td>
				</thead>
				<c:forEach items="#{calendarioAcoesAssociadas.calendarios}" var="cal" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: center;">
							<h:outputText value="#{cal.anoReferencia}" />
						</td>
						<td style="text-align: center;">
							<h:outputText value="#{cal.inicioCadastroBolsa}" />
						</td>
						<td style="text-align: center;">
							<h:outputText value="#{cal.fimCadastroBolsa}" />
						</td>
						<td width=20>
							<h:commandLink title="Alterar Calend�rio de A��es Integradas" action="#{ calendarioAcoesAssociadas.atualizar }" id="linkAlterar">
								<f:param name="id" value="#{ cal.id }" />
								<h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
						</td>
						
						<td width=20>
							<h:commandLink title="Remover Calend�rio de A��es Integradas" action="#{ calendarioAcoesAssociadas.inativar }" id="linkRemover" onclick="#{confirm}" >
								<f:param name="id" value="#{ cal.id }"/>
								<h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
						</td>
						
					</tr>
				</c:forEach>
			</table>		
		</c:if>
		
		<c:if test="${ empty calendarioAcoesAssociadas.calendarios}">
			<center>Nenhum Calend�rio de A��es Associadas foi encontrado</center>
		</c:if>
		 
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>