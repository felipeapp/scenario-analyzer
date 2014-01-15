<%@include file="/biblioteca/cabecalho_popup.jsp"%>

<style type="text/css">

.tabelaRelatorioBorda td, td{
	border: 1px solid  #EEEEEE;
	text-align:left;
	vertical-align:top;	
}

</style>

	<f:view>
		<h2>Histórico de Empréstimos do Material</h2>
		
		${emiteRelatorioHistoricosMBean.limpaHistoricoEmprestimos}
		
		<c:set var="listaHistorico" value="#{emiteRelatorioHistoricosMBean.historicoEmprestimos}"/>
		
		<table class="tabelaRelatorioBorda" style="width:100%;">
			<c:if test="${! empty( listaHistorico ) }">
		
				<c:forEach var="e" items="#{listaHistorico}">
				
					<thead>
						<tr>
							<th style="text-align:left;"><strong>Usuário:</strong> ${e.usuarioBiblioteca.nome}	</th>
						</tr>
					</thead>
					
					<tbody>
						<tr>
							<td>
								<table class="tabelaRelatorioBorda" style="width:100%;">
									<tr>
										<td width="350px">
											<strong>Data de Empréstimo:</strong> <ufrn:format type="dataHora" valor="${e.dataEmprestimo}" />		
										</td>
										<td>
											<strong>Operador:</strong> ${e.usuarioRealizouEmprestimo.nome}										
										</td>
									</tr>

									<c:forEach var="p" items="#{e.prorrogacoes}">
										<tr>
											<td width="350px">
												<strong>Data Renovação:</strong> <ufrn:format type="dataHora" valor="${p.dataCadastro}" />
											</td>
											<td>
												<strong>Operador:</strong> ${p.registroCadastro.usuario.pessoa.nome}
											</td>											
										</tr>
									</c:forEach>
									
									<c:if test="${e.usuarioRealizouDevolucao != null}">
										<tr>
											<td width="350px">
												<strong>Data Devolução:</strong> <ufrn:format type="dataHora" valor="${e.dataDevolucao}" />
											</td>
											<td>
												<strong>Operador:</strong> ${e.usuarioRealizouDevolucao.nome}	
											</td>											
										</tr>
									</c:if>									
									
									<c:if test="${e.usuarioRealizouEstorno != null}">
										<tr>
											<td width="350px">
												<strong>Data Estorno:</strong> <ufrn:format type="dataHora" valor="${e.dataEstorno}" />
											</td>
											<td>
												<strong>Operador:</strong> ${e.usuarioRealizouEstorno.nome}	
											</td>
										</tr>
									</c:if>															
								</table>
								<br><br>
							</td>
						</tr>				
					</tbody>
				    
				</c:forEach>
				
			</c:if>
			<c:if test="${ empty( listaHistorico ) }">
				<p style="text-align:center; color: red"><b>Não existem empréstimos para este material.</b></p>
			</c:if>
		</table>
		${emiteRelatorioHistoricosMBean.limpaHistoricoEmprestimos}
	</f:view>

<%@include file="/biblioteca/rodape_popup.jsp"%>