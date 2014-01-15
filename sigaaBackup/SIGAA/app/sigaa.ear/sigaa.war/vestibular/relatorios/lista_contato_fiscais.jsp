<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<f:view>
	<h2>Lista de Contato de Fiscais Reservas</h2>
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Processo Seletivo:</th>
		<td>${relatoriosVestibular.obj.nome}</td>
	</tr>
	<tr>
		<th>Local de Aplicação:</th>
		<td>${localAplicacao.nome}</td>
	</tr>
	<tr>
		<th>Titularidade:</th>
		<td>
			<c:choose>
				<c:when test="${relatoriosVestibular.titularReserva == 1}">Somente Fiscais Reservas</c:when>
				<c:when test="${relatoriosVestibular.titularReserva == 2}">Somente Fiscais Titulares</c:when>
				<c:otherwise>Fiscais Reservas e Titulares</c:otherwise>
			</c:choose>
		</td>
	</tr>
</table>
</div>
<br/>
<c:set var="_localGrupo" />
<c:set var="_alocacaoGrupo" />
<c:set var="fechaTabela" value="false" />

<c:forEach items="${lista}" var="item" >
	<c:set var="_alocacaoAtual" value="${item.tipo_alocacao}" />
	<c:if test="${_alocacaoAtual != _alocacaoGrupo}">
		<c:set var="_alocacaoGrupo" value="${_alocacaoAtual}" />
		<c:if test="${fechaTabela}">
			</tbody>
			</table>
			<br/>
			<c:set var="fechaTabela" value="false" />
		</c:if>
		<table class="tabelaRelatorioBorda" width="100%">
			<c:set var="fechaTabela" value="true" />
			<c:set var="_localGrupo" />
			<caption>FISCAIS 
				<c:choose>
					<c:when test="${item.reserva}">RESERVAS</c:when>
					<c:otherwise>TITULARES</c:otherwise>
				</c:choose> QUE ${_alocacaoAtual}</caption>
			<thead>
				<tr>
					<c:if test="${relatoriosVestibular.titularReserva == 0}">
						<th width="5%">Titularidade</th>
					</c:if>
					<th width="5%" style="text-align: right;">Matrícula</th>
					<th width="30%">Nome</th>
					<th width="30%">Curso</th>
					<th width="20%">Bairro</th>
					<th width="5%">Telefone</th>
					<th width="5%">Celular</th>
					<th width="5%" style="text-align: right;">Ordem*</th>
				</tr>
			</thead>
			<tbody>
	</c:if>
	<tr>
		<c:if test="${relatoriosVestibular.titularReserva == 0}">
			<td>
				<c:choose>
					<c:when test="${item.reserva}">Reserva</c:when>
					<c:otherwise>Titular</c:otherwise>
				</c:choose>
			</td>
		</c:if>
		<td style="text-align: right;">${item.matricula}</td>
		<td>${item.nome}</td>
		<td>
			${item.curso}
			 <c:if test="${not empty item.grau_academico}"> - ${item.grau_academico}</c:if>
			 <c:if test="${not empty item.turno}"> - ${item.turno}</c:if>
			 <c:if test="${not empty item.modalidade_educacao}"> - ${item.modalidade_educacao}</c:if>
		</td>
		<td>${item.bairro}</td>
		<td>${item.telefone_fixo}</td>
		<td>${item.telefone_celular}</td>
		<td style="text-align: right;">${item.ordem_preferencial}</td>
	</tr>
</c:forEach>
<c:if test="${fechaTabela}">
	</tbody>
	</table>
	<br/>
	<c:set var="fechaTabela" value="false" />
</c:if>
<div style="font-size: 9px"><b>*</b> Ordem de preferência do local de aplicação de prova optado durante a inscrição.
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
