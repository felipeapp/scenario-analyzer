<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<a4j:keepAlive beanName="buscaEspacoFisico" />

<style>
<!--

/* Paineis de opçoes */
.titulo {
	background: #EFF3FA;
}

-->
</style>

<h2> <ufrn:subSistema /> > Visualização do Espaço Físico </h2>

	<table class="formulario" width="90%">
		<caption>Dados do Espaço Físico</caption>
		<tbody>
			<tr>
				<td class="titulo"><strong>Código</strong></td>
				<td class="titulo"><strong>Localização</strong></td>
				<td class="titulo" colspan="2">
			</tr>
			<tr>
				<td>${ buscaEspacoFisico.obj.codigo }</td>
				<td>${ buscaEspacoFisico.obj.unidadeResponsavel.nome }</td>
				<td></td>										
			</tr>
			<tr>
				<td class="titulo"><strong>Tipo de Espaço Físico</strong></td>
				<td class="titulo"><strong>Capacidade</strong></td>
				<td class="titulo"><strong>Área</strong></td>
			</tr>
			<tr>
				<td>${ buscaEspacoFisico.obj.tipo.denominacao }</td>
				<td>${ buscaEspacoFisico.obj.capacidade }</td>
				<td>${ buscaEspacoFisico.obj.area }m²</td>
			</tr>		
			<tr>
				<td class="titulo" colspan="3"><strong>Reservada Prioritáriamente</strong></td>
			</tr>		
			<tr>
				<td colspan="3">${ buscaEspacoFisico.obj.unidadePreferenciaReserva.nome }</td>
			</tr>			
			<tr>
				<td class="titulo" colspan="3"><strong>Descrição</strong></td>
			</tr>			
			<tr>
				<td colspan="3">${ buscaEspacoFisico.obj.descricao }</td>
			</tr>			
			<tr>
				<td colspan="3">
					<table class="listagem" width="100%">
						<caption>Recursos do Espaço Físico</caption>
						<thead>
							<tr>
								<th>Nome</th>
								<th>Quantidade</th>
							</tr>						
						</thead>
						<tbody>
							<c:forEach var="r" items="#{buscaEspacoFisico.obj.recursos}" varStatus="status">
								 <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${ r.tipo.denominacao }</td>
									<td>${ r.quantidade }</td>							
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</td>
			</tr>		
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
