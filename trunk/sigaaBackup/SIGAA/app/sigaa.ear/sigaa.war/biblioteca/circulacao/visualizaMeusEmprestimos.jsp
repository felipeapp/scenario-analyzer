<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%--  
 *
 * Página utilizada quando usuário é redirecionado da parte pública do sistema
 * Atraves da ação verTelaLogin.do?urlRedirect=/sigaa/biblioteca/circulacao/visualizaMeusEmprestimos.jsf
 *  
--%>


<f:view>
	<h:form id="formulario">
		<h2><ufrn:subSistema /> &gt; Visualizar Meus Empréstimos em Aberto</h2>

		${meusEmprestimosBibliotecaMBean.carregaEmprestimosEmAberto}


		<c:if test="${not empty meusEmprestimosBibliotecaMBean.emprestimosEmAberto}">

			<div class="descricaoOperacao" width="70%">
				<p>A lista abaixo apresenta os seus empréstimos em aberto no sistema</p>
			</div>

			<table class="listagem" style="width: 100%;">
				<caption>Empréstimos em Abertos (${fn:length(meusEmprestimosBibliotecaMBean.emprestimosEmAberto)})</caption>
				<thead>
					<tr>
						<th>Informações do Material</th>
						<th style='text-align: center;'>Data do Empréstimo</th>
						<th>Tipo do Empréstimos</th>
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
									NÃO
								</td>
							</c:if>
						</tr>

					</c:forEach>
				</tbody>
				
				<tfoot>
					<tr>
						<td colspan="5" style="text-align: center;">
							<h:commandButton id="cmdButtonRedirecionaRenovacaoMeusEmprestimos" value="Renovar Empréstimos"  action="#{meusEmprestimosBibliotecaMBean.iniciarVisualizarEmprestimosRenovaveis}" />
						</td>
					</tr>
				</tfoot>
				
			</table>
		</c:if>

		<c:if test="${empty meusEmprestimosBibliotecaMBean.emprestimosEmAberto }">
			<div style="margin-top: 30px; color: red; text-align: center; font-weight: bold;"> Usuário não possui empréstimos ativos </div>
		</c:if>
		
	</h:form>
	
</f:view>  

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>