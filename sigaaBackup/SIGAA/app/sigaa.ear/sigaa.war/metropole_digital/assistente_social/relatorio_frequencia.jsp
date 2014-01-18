<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<a4j:keepAlive beanName="relatorioFrequenciaAssitenteSocial"/>
<f:view>
	<h:form>
	
		<c:if test="${(empty relatorioFrequenciaAssitenteSocial.listaRelatorioFrequencia)}">
			<center><i> Nenhum discente e/ou período encontrado.</i></center>
		</c:if>
		
		<c:if test="${(not empty relatorioFrequenciaAssitenteSocial.listaRelatorioFrequencia)}">
		
			<c:set var="idTurmaAntiga" value="0"/>
			<c:set var="total" value="0"/>
			
			<table class="tabelaRelatorio" style="width: 100%">
				
				<tbody>
				
					<c:forEach items="#{relatorioFrequenciaAssitenteSocial.tutoriasSelecionadas}" var="itemTutoria" varStatus="status">
						<c:if test="${(itemTutoria.turmaEntrada.id) != (idTurmaAntiga)}">	
								
							<div align="center">
								<h2 align="center">
									${itemTutoria.turmaEntrada.especializacao.descricao} - ${itemTutoria.turmaEntrada.anoReferencia}.${itemTutoria.turmaEntrada.periodoReferencia } - ${itemTutoria.turmaEntrada.dadosTurmaIMD.cronograma.modulo.descricao }
									<br />TUTOR: ${itemTutoria.tutor.pessoa.nome}
									<br />${itemTutoria.turmaEntrada.opcaoPoloGrupo.descricao } - HORÁRIO: ${itemTutoria.turmaEntrada.dadosTurmaIMD.horario } 
								</h2>
							</div>
							
							<table cellspacing="1" width="100%" style="font-size: 10px;">
								<thead>
									<tr>
										<th style ="text-align: left;">Matrícula</th>
										<th style ="text-align: left;">Discente</th>
										<th style ="text-align: left;">CH Total</th>
										<th style ="text-align: left;">CH Executada</th>
										<th style ="text-align: left;">CH Faltas</th>
										<th style ="text-align: left;">Qtd. Faltas</th>
										<th style ="text-align: right;">% Faltas</th>
									</tr>
								</thead>
						</c:if>
						
						
						<c:forEach items="#{relatorioFrequenciaAssitenteSocial.tabelaRelatorioFrequencia}" var="novaLista" varStatus="status">
							
								<c:forEach items="#{novaLista}" var="item" varStatus="status">
									<c:if test="${(item.discente.turmaEntradaTecnico.id) == (itemTutoria.turmaEntrada.id)}">
										<tr style="background-color: ${status.count % 2 == 0 ? '#F1F1F1' : '#FFFFFF'}" align="right">
										
											<td style="width: 10%; text-align: left;">${item.discente.matricula}</td>
											<td style="width: 40%; text-align: left;">${item.discente.nome}</td>
											<td style="width: 10%; text-align: left;">${item.chTotal}h</td>
											<td style="width: 12%; text-align: left;">${item.chExecutada}h</td>
											<td style="width: 8%; text-align: left;">${item.chFaltas}h</td>
											
											<td style="width: 10%; text-align: left;">${item.qtdFaltas}</td>
											<c:if test="${item.percentualFaltas > 25}">
												<td style="color:red; width: 10%; text-align: right;"><b>${item.percentualFaltasTexto} %</b></td>
											</c:if>
											<c:if test="${item.percentualFaltas == 25}">
												<td style="color:blue; width: 10%; text-align: right;"><b>${item.percentualFaltasTexto} %</b></td>
											</c:if>
											<c:if test="${item.percentualFaltas < 25}">
												<td style="color:green; width: 10%; text-align: right;"><b>${item.percentualFaltasTexto} %</b></td>
											</c:if>
											<c:set var="total"  value="${total + 1}"/>
										</tr>
									</c:if>
								</c:forEach>
							
						</c:forEach>
						
						<c:if test="${total == 0}">
							<tr align="center"><td colspan="7">Nenhum registro encontrado.</td></tr>
						</c:if>
						
						</table>
						<br />
						<c:set var="idTurmaAntiga"  value="${itemTutoria.turmaEntrada.id}"/>
						<c:set var="total"  value="0"/>
							
					</c:forEach>
					
				</tbody>
				
			</table>
			<br />
				
		</c:if>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>