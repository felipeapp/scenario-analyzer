<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2> <ufrn:subSistema /> &gt; Solicita��o de Cota &gt; Escolha do Edital </h2>

<div class="descricaoOperacao">
	<p>
		<strong>Caro docente,</strong>
	</p>
	<br/>
	<p>
		Para solicitar uma cota de bolsa voc� deve cadastrar um plano de trabalho para concorrer a um edital
		de distribui��o de cotas. O plano de trabalho deve ser vinculado a um projeto de pesquisa em execu��o 
		que voc� coordene e cujo per�odo possa comportar o per�odo da cota do plano de trabalho em quest�o. 
	</p>
	<p>
		Primeiramente, selecione o edital que deseja concorrer da lista abaixo.
	</p>
</div>

<c:set var="lista" value="${formPlanoTrabalho.referenceData.editaisAbertos}"/>
<c:if test="${not empty lista}">

	<center>
		<div class="infoAltRem">
			<html:img page="/img/seta.gif" style="overflow: visible;" />: Selecionar Edital
		</div>
	</center>

	<table cellpadding="3" class="listagem">
		<caption class="listagem">Editais de Distribui��o de Cotas Abertos</caption>
		<thead>
			<tr>
				<th style="text-align: left;">Descri��o</th>
				<th style="text-align: left;">Cota</th>
				<th style="text-align: left;">Per�odo de Submiss�o</th>
				<th style="text-align: left;">Titula��o M�nima</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${lista}" var="edital" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td align="left">
						${edital.descricao}
					</td>
					<td style="text-align: left;">
						${edital.cota.descricaoCompleta}
					</td>
					<td align="left">
						<fmt:formatDate value="${edital.inicioSubmissao}"pattern="dd/MM/yyyy" />
						a <fmt:formatDate value="${edital.fimSubmissao}"pattern="dd/MM/yyyy" />
					</td>
					<td align="left">
						${ edital.titulacaoMinimaCotasDescricao }
					</td>
					<td align="center">
						<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=selecionarEdital&idEdital=${edital.id}">
							<img src="${ctx}/img/seta.gif"
								title="Selecionar Edital"
								alt="Selecionar Edital"/>
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