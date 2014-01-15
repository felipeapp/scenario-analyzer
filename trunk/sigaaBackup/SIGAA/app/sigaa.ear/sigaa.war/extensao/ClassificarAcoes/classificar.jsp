<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h2><ufrn:subSistema /> > Classificar A��es de Extens�o	</h2>


<div class="descricaoOperacao">
	<b>Prezado(a) Gestor(a),</b><br/> 
	A classifica��o apresentada poder� ser confirmada atrav�s do bot�o 'Confirmar Classifica��o' apresentado no final da lista. 
	Mas aten��o, uma vez confirmada a classifica��o ela n�o poder� ser realizada novamente e o processo de avalia��o do 
	comit� Ad hoc ser� finalizado (todas as avalia��es que n�o foram realizadas ser�o canceladas).<br/>
	A confirma��o da classifica��o grava as m�dias obtidas por cada proposta possibitando a avalia��o dos projetos pelos 
	membros do Comit� de Extens�o.
</div>


<a4j:keepAlive beanName="classificarAcaoExtensao" />
<h:form id="form">

	<table class="listagem" width="100%">
		  <caption class="listagem">Classifica��o das A��es</caption>

	      <thead>
	      	<tr>
	        	<th width="1%" style="text-align: center">Class.</th>
	        	<th width="40%">T�tulo</th>
	        	<th>�rea Tem�tica</th>
	        	<th>Unidade</th>
	        	<th width="8%" style="text-align: right;">Discentes Envolvidos</th>
	        	<th width="8%" style="text-align: right;">Bolsas Solicitadas</th>
	        	<th style="text-align: center">Notas</th>
	        	<th style="text-align: right;">M�dia</th>
	        </tr>
	 	</thead>
	 	<tbody>
	 	
			<c:forEach items="${classificarAcaoExtensao.acoesExtensao}" var="acao" varStatus="status">			
			    <td> <c:set value="${ !acao.projeto.selecionado ? 'gray' : '' }" var="cor" /> </td>
		      	<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" style="color: ${cor};">
				    <td  style="text-align: center"> ${ status.count }� </td>
					<td> ${ acao.anoTitulo } </td>
					<td> ${acao.areaTematicaPrincipal.descricao} </td>
					<td> ${ acao.unidade.sigla } </td>
					<td style="text-align: right;"> ${acao.totalDiscentes} </td>
					<td style="text-align: right;"> ${acao.bolsasSolicitadas} </td>
					<td style="text-align: center"> ${ acao.notasAvaliacoes } </td>					
					<td style="text-align: right;"> <fmt:formatNumber  pattern="#0.00" value="${ acao.projeto.media }" />	</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="8" align="center">
					<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" />
					<h:commandButton action="#{classificarAcaoExtensao.confirmarClassificacao}" 
						value="Confirmar Classifica��o" id="cmdBtConfirmarClassificacao" rendered="#{classificarAcaoExtensao.permitidoConfirmarClassificacao }"/>
					<h:commandButton action="#{ classificarAcaoExtensao.cancelar }" value="Cancelar" onclick="#{confirm}" id="cmdBtCancelarClassificacao"/>
					<h:commandButton action="#{ classificarAcaoExtensao.preView }" value="Vers�o para Impress�o" id="cmdBtPreView"/>
				</td>
			</tr>				
		</tfoot>
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>