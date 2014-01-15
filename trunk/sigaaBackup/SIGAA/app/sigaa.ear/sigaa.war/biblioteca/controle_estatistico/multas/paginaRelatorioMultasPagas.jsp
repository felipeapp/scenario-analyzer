<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	
	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	
	<table class="tabelaRelatorioBorda" style="width:100%;margin-bottom:30px; border-top:none;">
	
		<tbody>
			<c:forEach var="resul" items="#{ _abstractRelatorioBiblioteca.resultado }" >
				<tr>
					<td style="background-color: #DDDDDD; font-weight: bold;"> Usuário: ${ resul.infoUsuario }</td>
					
				</tr>
				<tr>
					<td>
						<table style="border: none; width: 100%;">
						
							<thead>
								<tr>
									<th style="text-align: center; width: 15%; ">Valor da Multa</th>
									<th style="text-align: center; width: 25%;">Tipo de Quitação</th>
									<th style="text-align: center; width: 30%;">Usuário Realizou Quitação</th>
									<th style="text-align: center; width: 15%;">Data da Quitação</th>
									<th style="text-align: center; width: 15%;">Nº de Referência GRU</th>
								</tr>
							</thead>
						
							<c:forEach var="info" items="#{ resul.infoMultasUsuario }">
								<tr> 
									<td style="text-align: right;">
										${info.valorFormatado}
									</td> 
									<td style="text-align: center;">
										${info.descricaoQuitacao}
									</td>
									<td >
										${info.usuarioQuitou}
									</td>
									<td style=" text-align: center;">
										${info.dataQuitacaoFormatada}
									</td>
									<td style=" text-align: center;">
										${info.numeroReferencia}
									</td>
								</tr>
								<c:if test="${not empty info.observacaoPagamento}">
									<tr>
										<td colspan="1" style="text-align: right; border-right: none;">
											Observação:
										</td>
										<td colspan="4" style="font-style: italic; border-left: none;">
											${info.observacaoPagamento}
										</td>
									</tr>
								</c:if>
							</c:forEach>
							
							
							<tfoot>
								<tr> 
									<td colspan="1" style="text-align: right;">
										Total: ${ resul.valorTotalUsuarioFormatado }
									</td> 
									<td colspan="4">
									</td>
								</tr>
							</tfoot>
							
						</table>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		
		<tfoot>
			<tr> 
				<td style="text-align: center;">
					Total no período: ${ _abstractRelatorioBiblioteca.valorTotalRelatorioFormatado }
				</td> 
			</tr>
		</tfoot>
		
	</table>
	
</f:view>


<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>