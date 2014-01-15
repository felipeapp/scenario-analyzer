
<%--   Acho que essa página não está sendo usado


<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>

	.linhaImpar td{
		background:#F8F8F8;
	}

</style>

<f:view>

	<div>
		${consultaHistoricoEmprestimosItem.tituloCatalograficoResumido.titulo}
		${consultaHistoricoEmprestimosItem.tituloCatalograficoResumido.subtitulo}
		${consultaHistoricoEmprestimosItem.tituloCatalograficoResumido.autor}
		${consultaHistoricoEmprestimosItem.tituloCatalograficoResumido.ano}
	</div>


	<h:form>
	
		<table class="listagem"> 
				<caption>Histórico de Empréstimos do Material </caption>
			<thead>
				<tr>
					<th width="40%">Usuário</th>
					<th>Operações</th>
				</tr>
			</thead>
			<tbody>
	
				<c:forEach var="e" items="#{consultaHistoricoEmprestimosItem.emprestimos}" varStatus="s">

					<tr class="${ s.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${e.usuario.pessoa.nome}</td>
						<td>
							<table width="100%">
								<thead>
									<tr>
										<th width="20%">Operação</th><th>Operador</th><th width="20%">Data</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>Empréstimo</td>
										<td><h:outputText value="#{e.registroEmprestimo.usuario.nome}"/></td>
										<td><h:outputText value="#{e.dataEmprestimo}" converter="convertData"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></td>
									</tr>
									<tr>
										<td>Renovação</td>
										<td><h:outputText value="#{e.registroRenovacao.usuario.nome}"/></td>
										<td><h:outputText value="#{e.dataEmprestimo}" converter="convertData"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></td>
									</tr>
									<tr>
										<td>Devolução</td>
										<td><h:outputText value="#{e.registroDevolucao.usuario.nome}"/></td>
										<td><h:outputText value="#{e.dataEmprestimo}" converter="convertData"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></td>
									</tr>
								</tbody>
							</table>
						</td>
					</tr>
				</c:forEach>

			</tbody>
		</table>

	</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

--%>