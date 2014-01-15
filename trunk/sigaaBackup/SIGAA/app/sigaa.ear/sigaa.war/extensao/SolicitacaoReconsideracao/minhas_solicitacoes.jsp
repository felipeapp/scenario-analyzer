<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h:outputText value="#{solicitacaoReconsideracao.create}" />

	<h2><ufrn:subSistema /> > Solicitações de Reconsiderações da Ação Selecionada</h2>

			<div class="infoAltRem">
			    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Reconsideração
			</div>
			<br>
			
			<h:form>

			<table class="listagem">
			    <caption>Lista de Reconsiderações Solicitadas da Ação Selecionada</caption>

			      <thead>
			      	<tr>
			        	<th width="60%">Título da ação</th>			        	
			        	<th>Situação</th>
			        	<th>Analisado em</th>
			        	<th>&nbsp;</th>
			        </tr>
			      </thead>
			        

				<c:set var="lista" value="#{solicitacaoReconsideracao.obj.atividade.solicitacoesReconsideracao}" />

				<c:if test="${empty lista}">
			        <tbody>
	                    <tr> <td colspan="5" align="center"> <font color="red">Não há solicitações de reconsideração cadastradas para esta ação de extensão </font> </td></tr>
					</tbody>
				</c:if>

				<c:if test="${not empty lista}">

			        <tbody>
				        <c:forEach items="#{lista}" var="sr" varStatus="status">

					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

						                    <td> ${sr.atividade.codigo} - ${sr.atividade.titulo} </td>
						                    <td> <c:if test="${not empty sr.dataParecer}">${sr.aprovado ? 'SOLICITAÇÃO APROVADA' : 'SOLICITAÇÃO NÃO APROVADA'}</c:if><c:if test="${empty sr.dataParecer}">ANALISANDO</c:if></td>
						                    <td> <c:if test="${not empty sr.dataParecer}"><fmt:formatDate value="${sr.dataParecer}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if> </td>

											<td>		
													<h:commandLink title="Visualizar Reconsideração" action="#{ solicitacaoReconsideracao.view }">
														<f:param name="idReconsideracao" value="#{sr.id}"/>
													    <h:graphicImage url="/img/view.gif"/>
													</h:commandLink>
											</td>
					              </tr>

				        </c:forEach>
					</tbody>

				</c:if>
				<tfoot>
					<tr>
						<td colspan="4"><center><input type="button" value="<< Voltar" onclick="javascript:history.back();" /></center></td>
					</tr>
				</tfoot>
		</table>
		</h:form>

<br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>