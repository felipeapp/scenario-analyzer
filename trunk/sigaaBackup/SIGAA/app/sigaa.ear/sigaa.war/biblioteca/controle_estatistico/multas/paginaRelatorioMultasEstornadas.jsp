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
									<th style="text-align: center; width: 20%; ">Valor da Multa</th>
									<th style="text-align: center; width: 60%;">Usuário Realizou Estorno</th>
									<th style="text-align: center; width: 20%;">Data do Estorno</th>
								</tr>
							</thead>
						
							<c:forEach var="info" items="#{ resul.infoMultasUsuario }">
								<tr> 
									<td style="text-align: right;">
										${info.valorFormatado}
									</td> 
									<td>
										${info.usuarioEstorno}
									</td>
									<td style=" text-align: center;">
										${info.dataEstornoFormatada}
									</td>
								</tr>
								
								<tr>
									<td colspan="1" style="text-align: right;">
										Motivo Estorno:
									</td>
									<td colspan="2" style="font-style: italic;">
									${info.motivoEstorno}
									</td>
								</tr>
								
							</c:forEach>
							
							
							<tfoot>
								<tr> 
									<td colspan="1" style="text-align: right;">
										Total: ${ resul.valorTotalUsuarioFormatado }
									</td> 
									<td colspan="2">
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