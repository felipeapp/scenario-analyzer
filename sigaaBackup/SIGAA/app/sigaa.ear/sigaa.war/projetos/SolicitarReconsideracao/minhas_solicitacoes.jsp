<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>


	<h2><ufrn:subSistema /> > Solicita��es de Reconsidera��es da A��o Selecionada</h2>

			<div class="infoAltRem">
			    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Reconsidera��o
			</div>
			<br>
			
			<h:form>

			<table class="listagem">
			    <caption>Lista de Reconsidera��es Solicitadas da A��o Selecionada</caption>

			      <thead>
			      	<tr>
			        	<th width="60%">T�tulo</th>			        	
			        	<th>Situa��o</th>
			        	<th style="text-align:center;">Analisado em</th>
			        	<th>&nbsp;</th>
			        </tr>
			      </thead>
			        

				<c:set var="lista" value="#{solicitacaoReconsideracao.obj.projeto.solicitacoesReconsideracao}" />

				<c:if test="${empty lista}">
			        <tbody>
	                    <tr> <td colspan="5" align="center"> <font color="red">N�o h� solicita��es de reconsidera��o cadastradas para esta a��o acad�mica </font> </td></tr>
					</tbody>
				</c:if>

				<c:if test="${not empty lista}">

			        <tbody>
				        <c:forEach items="#{lista}" var="sr" varStatus="status">

					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

						                    <td> ${sr.projeto.ano} - ${sr.projeto.titulo} </td>
						                    <td> <c:if test="${not empty sr.dataParecer}">${sr.aprovado ? 'SOLICITA��O APROVADA' : 'SOLICITA��O N�O APROVADA'}</c:if><c:if test="${empty sr.dataParecer}">ANALISANDO</c:if></td>
						                    <td style="text-align:center;"> <c:if test="${not empty sr.dataParecer}"><fmt:formatDate value="${sr.dataParecer}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if> </td>

											<td>		
													<h:commandLink title="Visualizar Reconsidera��o" action="#{ solicitacaoReconsideracao.view }">
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