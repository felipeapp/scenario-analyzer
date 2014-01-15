<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h2>Classifica��o de A��es de Extens�o	</h2>

		<div id="parametrosRelatorio">			 
			<table>
				<tr>
					<th>Edital:</th>
					<td>${classificarAcaoExtensao.edital.descricao}</td>
				</tr>
				<tr>
					<th>Resultado:</th>
					<td>${classificarAcaoExtensao.permitidoConfirmarClassificacao ? 'Classifica��o tempor�ria' : 'Classifica��o final'}</td>
				</tr>
				
			</table>
			
		</div>
		<h3 class="tituloTabelaRelatorio">Classifica��o das A��es</h3>
		<table class="tabelaRelatorio" width="100%">
	      <thead>
	      	<tr>
	        	<th width="1%" style="text-align: center">Class.</th>
	        	<th width="40%">T�tulo</th>
	        	<th width="5%" style="text-align: right;">Discentes Envolvidos</th>
	        	<th width="5%" style="text-align: right;">Bolsas Solicitadas</th>
	        	<th style="text-align: center">Notas</th>
	        	<th style="text-align: right;">M�dia</th>
	        </tr>
	 	</thead>
	 	<tbody>
	 	
			<c:forEach items="${classificarAcaoExtensao.acoesExtensao}" var="acao" varStatus="status">			
			    <td> <c:set value="${ !acao.projeto.selecionado ? 'gray' : '' }" var="cor" /> </td>
		      	<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" style="color: ${cor};">
				    <td style="text-align: center"> ${ status.count }� </td>
					<td> ${ acao.anoTitulo } </td>
					<td style="text-align: right;"> ${acao.totalDiscentes} </td>
					<td style="text-align: right;"> ${acao.bolsasSolicitadas} </td>
					<td style="text-align: center"> ${ acao.notasAvaliacoes } </td>					
					<td style="text-align: right;"> <fmt:formatNumber  pattern="#0.00" value="${ acao.projeto.media }" />	</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>