<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:form prependId="false">


<style>
a.withoutFormat{
	color: black;
	font-weight: normal;
	font-size: inherit;
	text-decoration: none;
}
a.withoutFormatInativo{
	color: #555;
	font-weight: normal;
	font-size: inherit;
	text-decoration: none;
}

tr.selecionado td { 
	background-color: #C4D2EB;
}

.subFormulario .selecionado td { font-style: italic !important; }

</style>
<h2>Escolha seu Vínculo para operar o sistema</h2>

<div class="descricaoOperacao" style="width: 70%">
	<p><b>Caro Usuário,</b></p>
	
	<p>O sistema detectou que você possui mais de um vínculo ativo com a instituição.
	Por favor, selecione o vínculo com o qual você deseja trabalhar nesta sessão.</p>  
</div>

<center>
<div class="infoAltRem" style="width: 100%">
	<img src="img/icones/id_card_ok.png" width="24" height="24"/>: Selecionar Vínculo
</div>
</center>

<table class="formulario" style="width: 100%">
	<caption>Vínculos Encontrados (${ fn:length(usuario.vinculos) })</caption>
	<c:if test="${usuario.possuiVinculosAtivos}">
	<tr>
		<td>
			<table class="subFormulario" style="width: 100%">
				<caption>Ativos</caption>
				<thead>
				<tr>
					<td width="5%"></td>
					<th width="15%">Vínculo</th>
					<th width="10%" style="text-align: center">Identificador</th>
					<th width="10%" style="text-align: center">Ativo</th>
					<th width="60%">Outras Informações</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach var="vinculo" items="#{ usuario.vinculos }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }${ vinculo.ativo ? '' : ' inativo' }${ vinculo.numero == usuario.vinculoAtivo.numero ? ' selecionado' : ' ' }">
					<c:if test="${vinculo.tipoVinculo.ativo}">
					<td>
						<h:outputLink value="/sigaa/escolhaVinculo.do?dispatch=escolher&vinculo=#{vinculo.numero}" id="link">
							<h:graphicImage value="/img/icones/id_card_ok.png" width="24" height="24"/>
			        		<rich:componentControl for="panel" attachTo="link" operation="show" event="onclick"/>
						</h:outputLink>
					</td>
					<td id="tdTipo"><a href="${ctx}/escolhaVinculo.do?dispatch=escolher&vinculo=${vinculo.numero}" class="${ vinculo.tipoVinculo.ativo ? 'withoutFormat' : ' withoutFormatInativo' }">${ vinculo.tipoVinculo.tipo }</a></td>
					<td align="center"><a href="${ctx}/escolhaVinculo.do?dispatch=escolher&vinculo=${vinculo.numero}" class="${ vinculo.tipoVinculo.ativo ? 'withoutFormat' : ' withoutFormatInativo' }">${ vinculo.tipoVinculo.identificador }</a> </td>
					<td width="50" align="center"> <a href="${ctx}/escolhaVinculo.do?dispatch=escolher&vinculo=${vinculo.numero}" class="${ vinculo.tipoVinculo.ativo ? 'withoutFormat' : ' withoutFormatInativo' }">${ vinculo.tipoVinculo.ativo ? 'Sim' : 'Não' }</a></td>
					<td><a href="${ctx}/escolhaVinculo.do?dispatch=escolher&vinculo=${vinculo.numero}" class="${ vinculo.tipoVinculo.ativo ? 'withoutFormat' : ' withoutFormatInativo' }">${ vinculo.tipoVinculo.outrasInformacoes }</a></td>
					</c:if>
				</tr>
				</c:forEach>
				
				</tbody>
			</table>	
		</td>
	</tr>
	</c:if>
	<c:if test="${usuario.possuiVinculosInativos}">
	<tr>
		<td>
			<table class="subFormulario" style="width: 100%">
				<caption>Inativos</caption>
				<thead>
				<tr>
					<td width="5%"></td>
					<th width="15%">Vínculo</th>
					<th width="10%" style="text-align: center">Identificador</th>
					<th width="10%" style="text-align: center">Ativo</th>
					<th width="60%">Outras Informações</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach var="vinculo" items="#{ usuario.vinculos }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }${ vinculo.ativo ? '' : ' inativo' }${ vinculo.numero == usuario.vinculoAtivo.numero ? ' selecionado' : ' ' }">
					<c:if test="${not vinculo.tipoVinculo.ativo}">
					<td>
						<h:outputLink value="/sigaa/escolhaVinculo.do?dispatch=escolher&vinculo=#{vinculo.numero}" id="linkInativo">
							<h:graphicImage value="/img/icones/id_card_ok.png" width="24" height="24"/>
			        		<rich:componentControl for="panel" attachTo="linkInativo" operation="show" event="onclick"/>
						</h:outputLink>
					</td>
					<td id="tdTipo"><a href="${ctx}/escolhaVinculo.do?dispatch=escolher&vinculo=${vinculo.numero}" class="${ vinculo.tipoVinculo.ativo ? 'withoutFormat' : ' withoutFormatInativo' }">${ vinculo.tipoVinculo.tipo }</a></td>
					<td align="center"><a href="${ctx}/escolhaVinculo.do?dispatch=escolher&vinculo=${vinculo.numero}" class="${ vinculo.tipoVinculo.ativo ? 'withoutFormat' : ' withoutFormatInativo' }">${ vinculo.tipoVinculo.identificador }</a> </td>
					<td width="50" align="center"> <a href="${ctx}/escolhaVinculo.do?dispatch=escolher&vinculo=${vinculo.numero}" class="${ vinculo.tipoVinculo.ativo ? 'withoutFormat' : ' withoutFormatInativo' }">${ vinculo.tipoVinculo.ativo ? 'Sim' : 'Não' }</a></td>
					<td><a href="${ctx}/escolhaVinculo.do?dispatch=escolher&vinculo=${vinculo.numero}" class="${ vinculo.tipoVinculo.ativo ? 'withoutFormat' : ' withoutFormatInativo' }">${ vinculo.tipoVinculo.outrasInformacoes }</a></td>
					</c:if>
				</tr>
				</c:forEach>
				
				</tbody>
			</table>		
		</td>
	</tr>
	</c:if>
</table>

</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>