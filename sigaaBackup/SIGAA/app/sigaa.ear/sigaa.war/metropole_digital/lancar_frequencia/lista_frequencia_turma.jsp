<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<a4j:keepAlive beanName="lancamentoFrequenciaIMD"/>
<f:view>
	<h:form>
	
		<c:if test="${(empty lancamentoFrequenciaIMD.listaRelatorioFrequencia)}">
			<center><i> Nenhum discente e/ou período encontrado.</i></center>
		</c:if>
		
		<c:if test="${(not empty lancamentoFrequenciaIMD.listaRelatorioFrequencia)}">
		
			<p align="center"><h2 align="center">RELATÓRIO DE FREQUÊNCIA POR TURMA<br />TURMA: ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.anoReferencia}.${lancamentoFrequenciaIMD.turmaEntradaSelecionada.periodoReferencia} - ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.especializacao.descricao} - ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.cursoTecnico.nome}
			<br />OPÇÃO PÓLO GRUPO: ${lancamentoFrequenciaIMD.turmaEntradaSelecionada.opcaoPoloGrupo.descricao}</h2></p>
			
			<table class="tabelaRelatorio" style="width: 100%">
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
				<tbody>
					<c:forEach items="#{lancamentoFrequenciaIMD.listaRelatorioFrequencia}" var="item" varStatus="status">
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
						</tr>
					</c:forEach>
				</tbody>
				
			</table>
			<br />
				
		</c:if>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>