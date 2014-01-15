<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

	<style type="text/css">
	
		
	
		#tabelaHistoricoEmprestimosMaterial td{
			border-top: 0px;
			border-bottom: 0px;
			border-right: 0px;
			border-left: 0px;
		}	
	
		.tabelaRelatorioBorda tr{
			text-align:right;
		}

		.linhaImpar{
			background:#EEEEEE;
		}
	
		.linhaImpar td, .linhaImpar th{
			background:#EEEEEE;
		}
		
	</style>

	<f:view>

		<h2>Histórico de Empréstimos de um Material</h2>

		<div id="parametrosRelatorio" style="margin-bottom:10px;">
			<table>
				<tr>
					<th>Código de Barras:</th><td>${emiteRelatorioHistoricosMBean.materialInformacional.codigoBarras}</td>
				</tr>
				
				<tr>
					<th>Período:</th><td>de <ufrn:format type="data" valor="${emiteRelatorioHistoricosMBean.dataInicio}" /> até <ufrn:format type="data" valor="${emiteRelatorioHistoricosMBean.dataFim}" /></td>
				</tr>
			</table>
		</div>

		<style>
		
			.tabelaRelatorioBorda tr td, .tabelaRelatorioBorda thread tr th{
				text-align:left;
				vertical-align:top;
			}
			
		</style>

		<table id="tabelaHistoricoEmprestimosMaterial" class="tabelaRelatorioBorda" style="width:100%;">

			<c:forEach var="e" items="#{emiteRelatorioHistoricosMBean.emprestimos}" varStatus="linha">
				<tr>
					<td colspan="2" style="height: 20px;"></td>
				</tr>
				<tr class='${linha.index % 2 == 0 ? "linhaPar" : "linhaImpar" }'>
					<td colspan="2">
						<strong>Usuário:</strong> ${e.usuarioBiblioteca.nome}
					</td>
				</tr>	
				<tr class='${linha.index % 2 == 0 ? "linhaPar" : "linhaImpar" }'>
						
					<td style="width: 40%;">
						<strong>Data de Empréstimo:</strong> <ufrn:format type="dataHora" valor="${e.dataEmprestimo}" /> 
					</td>
					<td>
						<strong>Operador:</strong> ${e.usuarioRealizouEmprestimo.nome}
					</td>
				</tr>
				
				<tr class='${linha.index % 2 == 0 ? "linhaPar" : "linhaImpar" }'>
					<td colspan="2">
						<table style="width: 100%;">
							<tbody style="background-color: transparent;">
								<c:forEach var="p" items="#{e.prorrogacoes}">
									<tr>
										<td style="width: 40%">
											<strong>Data Renovação:</strong> <ufrn:format type="dataHora" valor="${p.dataCadastro}" /> 
										</td>
										<td>
											<strong>Operador:</strong> ${p.registroCadastro.usuario.pessoa.nome}
										</td>
									</tr>
								</c:forEach>
							</tbody>
							
						</table>
					</td>
				</tr>
				
				<c:if test="${e.usuarioRealizouDevolucao != null}">	
					<tr class='${linha.index % 2 == 0 ? "linhaPar" : "linhaImpar" }'>
						<td style="width: 40%; ">
							<strong>Data Devolução:</strong> <ufrn:format type="dataHora" valor="${e.dataDevolucao}" /> 	
						</td>
							
						<td>	
							<strong>Operador:</strong> ${e.usuarioRealizouDevolucao.nome}
						</td>	
					</tr>
				</c:if>
				
				<c:if test="${e.usuarioRealizouEstorno != null}">
					<tr class='${linha.index % 2 == 0 ? "linhaPar" : "linhaImpar" }'>
						<td style="width: 40%;">
							<strong>Data Estorno:</strong> <ufrn:format type="dataHora" valor="${e.dataEstorno}" />		
						</td>
						<td>	
							<strong>Operador:</strong> ${e.usuarioRealizouEstorno.nome}
						</td>
					</tr>
				</c:if>
				
			</c:forEach>
		</table>
	</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>