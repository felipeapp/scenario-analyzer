<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2> <ufrn:subSistema /> &gt; Cadastro de Plano de Trabalho Volunt�rio &gt; Escolha da Cota </h2>

<div class="descricaoOperacao">
	<h3>
		Caro docente,
	</h3>
	<br/>
	<p>
		Para cadastrar um plano de trabalho volunt�rio voc� deve definir em qual cota tal plano ser� 
		executado, permitindo que todos sigam o mesmo calend�rio de envio de relat�rios parciais e finais. 
		O plano de trabalho deve ser vinculado a um projeto de pesquisa em execu��o 
		que voc� coordene e cujo per�odo possa comportar o per�odo da cota do plano de trabalho em quest�o. 
	</p>
	<p>
		Primeiramente, selecione a cota desejada da lista abaixo.
	</p>
</div>

<c:set var="lista" value="${formPlanoTrabalho.referenceData.cotas}"/>
<c:if test="${not empty lista}">

	<center>
		<div class="infoAltRem">
			<html:img page="/img/seta.gif" style="overflow: visible;" />: Selecionar Cota
		</div>
	</center>

	<table cellpadding="3" class="listagem">
		<caption class="listagem">Cotas abertas para cadastro de planos de trabalho volunt�rios</caption>
		<thead>
			<tr>
				<th style="text-align: left;">Cota</th>
				<th style="text-align: center;">Per�odo de Vig�ncia</th>
				<th style="text-align: center;">Per�odo de Submiss�o de Novos Planos</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${lista}" var="cota" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td style="text-align: left;">
						${cota.descricao}
					</td>
					<td align="center">
						<fmt:formatDate value="${cota.inicio}"pattern="dd/MM/yyyy" />
						a <fmt:formatDate value="${cota.fim}"pattern="dd/MM/yyyy" />
					</td>
					<td align="center">
						<fmt:formatDate value="${cota.inicioCadastroPlanoVoluntario}"pattern="dd/MM/yyyy" />
						a <fmt:formatDate value="${cota.fimCadastroPlanoVoluntario}"pattern="dd/MM/yyyy" />
					</td>
					<td align="center">
						<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=selecionarCota&idCota=${cota.id}">
							<img src="${ctx}/img/seta.gif"
								title="Selecionar Cota"
								alt="Selecionar Cota"/>
						</html:link>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5" style="text-align: center; font-weight: bold;	">
					${ fn:length(lista) } ${ fn:length(lista) > 1 ? 'editais' : 'edital'} de pesquisa ${ fn:length(lista) > 1 ? 'encontrados' : 'encontrado'}
				</td>
			</tr>
		</tfoot>
	</table>
</c:if>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>