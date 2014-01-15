<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.curso td {padding: 20px 0 0;}
	tr.text td {padding: 20px 0 0; border-bottom: 1px solid #555; text-align: center;}
	tr.header td {padding: 0px; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
</style>

<f:view>
	<h2> RELATÓRIO DE MOVIMENTAÇÃO DE DISCENTE</h2>

			<div id="parametrosRelatorio">
				<table>
					<tr>
						<th>Ano.Período:</th>
						<td> 
							${ relatoriosSaeMBean.ano } . ${ relatoriosSaeMBean.periodo }
						</td>
					</tr>
					<tr>
						<th>Tipo(s) de Relatório(s) Escolhido(s):</th>
						<td> ${ relatoriosSaeMBean.relatorioMovimentacaoEscolhidoString } </td>
					</tr>
				</table>
			</div>
			<br/>

			<table style="width:100%">
			
				<c:set var="_status" />
				<c:forEach var="item" items="${ relatoriosSaeMBean.movimentacaoDiscente }" varStatus="status">
					
				<c:choose>
					<c:when test="${ item.exibirSoNomes }">
						<c:set var="statusAtual" value="${ item.discente.status }"/>
						 
						<c:if test="${_status != statusAtual}">
							<tr class="curso" style="margin-top: 20px;">
								<td colspan="8" style="text-align: center;">
									<h3> ${ item.descricaoRelatorio } </h3>
								</td>
							</tr>
						</c:if>
						
						<tbody>
							
							<tr class="curso">
								<td colspan="8"><b> Discente:</b> ${ item.discente.matricula } - ${ item.discente.pessoa.nome } </td>
							</tr>
							
							<tr>
								<td colspan="5"><b> Turma:</b> ${ item.matriculaComponente } </td>
								<td colspan="3"><b> Horário:</b> ${ item.descricaoHorario } </td>
							</tr>
	
							<tr class="header">
								<td width="5%" style="text-align: center;"> </td>
								<td width="5%" style="text-align: center;"> Segunda </td>
								<td width="5%" style="text-align: center;"> Terça </td>
								<td width="5%" style="text-align: center;"> Quarta </td>
								<td width="5%" style="text-align: center;"> Quinta </td>
								<td width="5%" style="text-align: center;"> Sexta </td>
								<td width="5%" style="text-align: center;"> Sábado </td>
								<td width="5%" style="text-align: center;"> Domingo </td>
							</tr>
							
							<tr class="header">
								<td width="5%" style="text-align: center;"> Café </td>
								${ item.horarioCafe }
							</tr>
							
							<tr class="header">
								<td width="5%" style="text-align: center;"> Almoço </td>
								${ item.horarioAlmoco }
							</tr>
							
							<tr class="header">
								<td width="5%" style="text-align: center;"> Jantar </td>
								${ item.horarioJanta }
							</tr>
							
						</tbody>
						
						<c:set var="_status" value="${ statusAtual }"/>
					
					</c:when>

					<c:otherwise>

						<c:set var="statusAtual" value="${ item.discente.status }"/>
						<c:set var="matriculaAtual" value="${ item.discente.matricula }"/>
						 
						<c:if test="${_status != statusAtual}">
							<tr class="curso" style="margin-top: 20px;">
								<td colspan="8" style="text-align: center;">
									<h3> ${ item.descricaoRelatorio } </h3>
								</td>
							</tr>
							
							<tr  class="header" style="border: 1px;">
								<td colspan="1"> Matricula </td>
								<td colspan="6"> Nome </td>
								<td colspan="1"> Status </td>
							</tr>
							
						</c:if>

						<c:if test="${_matricula != matriculaAtual }">
							<tr style="border: 1px solid #000;" >
								<td colspan="1"> ${ item.discente.matricula } </td>
								<td colspan="6"> ${ item.discente.pessoa.nome } </td>
								<td colspan="1"> ${ item.discente.statusString } </td>
							</tr>
						</c:if>
						
						<c:set var="_status" value="${ statusAtual }"/>
						<c:set var="_matricula" value="${ matriculaAtual }"/>
						
					</c:otherwise>
				</c:choose>

				</c:forEach>
		</table>
		
		<br />
		<table align="center">
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center; font-weight: bold;">
						${fn:length(relatoriosSaeMBean.movimentacaoDiscente)} registro(s) encontrado(s)
					</td>
				</tr>
			</tfoot>
		</table>
		
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>