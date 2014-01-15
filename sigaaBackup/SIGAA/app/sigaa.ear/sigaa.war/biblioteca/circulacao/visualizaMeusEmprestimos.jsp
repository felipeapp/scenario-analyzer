<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%--  
 *
 * P�gina utilizada quando usu�rio � redirecionado da parte p�blica do sistema
 * Atraves da a��o verTelaLogin.do?urlRedirect=/sigaa/biblioteca/circulacao/visualizaMeusEmprestimos.jsf
 *  
--%>


<f:view>
	<h:form id="formulario">
		<h2><ufrn:subSistema /> &gt; Visualizar Meus Empr�stimos em Aberto</h2>

		${meusEmprestimosBibliotecaMBean.carregaEmprestimosEmAberto}


		<c:if test="${not empty meusEmprestimosBibliotecaMBean.emprestimosEmAberto}">

			<div class="descricaoOperacao" width="70%">
				<p>A lista abaixo apresenta os seus empr�stimos em aberto no sistema</p>
			</div>

			<table class="listagem" style="width: 100%;">
				<caption>Empr�stimos em Abertos (${fn:length(meusEmprestimosBibliotecaMBean.emprestimosEmAberto)})</caption>
				<thead>
					<tr>
						<th>Informa��es do Material</th>
						<th style='text-align: center;'>Data do Empr�stimo</th>
						<th>Tipo do Empr�stimos</th>
						<th style='text-align: center;'>Prazo</th>
						<th style='text-align: center;'>Atrasado</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{meusEmprestimosBibliotecaMBean.emprestimosEmAberto}" var="e" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td style="width: 50%;">${e.material.informacao}</td>
							<td style="text-align:center">
								<h:outputText value="#{e.dataEmprestimo}" converter="convertData" />
							</td>
							<td  style="text-align:center">${e.politicaEmprestimo.tipoEmprestimo.descricao}</td>
							<td style="text-align:center">
								<h:outputText value="#{e.prazo}" converter="convertData">
									<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss" />
								</h:outputText>
							</td>
							<c:if test="${e.atrasado}">
								<td width="6%" style="text-align:center;color:red">
									SIM
								</td>
							</c:if>
							<c:if test="${ ! e.atrasado}">
								<td width="6%" style="text-align:center;color:green">
									N�O
								</td>
							</c:if>
						</tr>

					</c:forEach>
				</tbody>
				
				<tfoot>
					<tr>
						<td colspan="5" style="text-align: center;">
							<h:commandButton id="cmdButtonRedirecionaRenovacaoMeusEmprestimos" value="Renovar Empr�stimos"  action="#{meusEmprestimosBibliotecaMBean.iniciarVisualizarEmprestimosRenovaveis}" />
						</td>
					</tr>
				</tfoot>
				
			</table>
		</c:if>

		<c:if test="${empty meusEmprestimosBibliotecaMBean.emprestimosEmAberto }">
			<div style="margin-top: 30px; color: red; text-align: center; font-weight: bold;"> Usu�rio n�o possui empr�stimos ativos </div>
		</c:if>
		
	</h:form>
	
</f:view>  

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>