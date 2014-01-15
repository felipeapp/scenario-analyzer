<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h2><ufrn:subSistema /> > Classificar Ações de Extensão	</h2>


<div class="descricaoOperacao">
	<b>Prezado(a) Gestor(a),</b><br/> 
	A classificação apresentada poderá ser confirmada através do botão 'Confirmar Classificação' apresentado no final da lista. 
	Mas atenção, uma vez confirmada a classificação ela não poderá ser realizada novamente e o processo de avaliação do 
	comitê Ad hoc será finalizado (todas as avaliações que não foram realizadas serão canceladas).<br/>
	A confirmação da classificação grava as médias obtidas por cada proposta possibitando a avaliação dos projetos pelos 
	membros do Comitê de Extensão.
</div>


<a4j:keepAlive beanName="classificarAcaoExtensao" />
<h:form id="form">

	<table class="listagem" width="100%">
		  <caption class="listagem">Classificação das Ações</caption>

	      <thead>
	      	<tr>
	        	<th width="1%" style="text-align: center">Class.</th>
	        	<th width="40%">Título</th>
	        	<th>Área Temática</th>
	        	<th>Unidade</th>
	        	<th width="8%" style="text-align: right;">Discentes Envolvidos</th>
	        	<th width="8%" style="text-align: right;">Bolsas Solicitadas</th>
	        	<th style="text-align: center">Notas</th>
	        	<th style="text-align: right;">Média</th>
	        </tr>
	 	</thead>
	 	<tbody>
	 	
			<c:forEach items="${classificarAcaoExtensao.acoesExtensao}" var="acao" varStatus="status">			
			    <td> <c:set value="${ !acao.projeto.selecionado ? 'gray' : '' }" var="cor" /> </td>
		      	<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" style="color: ${cor};">
				    <td  style="text-align: center"> ${ status.count }º </td>
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
						value="Confirmar Classificação" id="cmdBtConfirmarClassificacao" rendered="#{classificarAcaoExtensao.permitidoConfirmarClassificacao }"/>
					<h:commandButton action="#{ classificarAcaoExtensao.cancelar }" value="Cancelar" onclick="#{confirm}" id="cmdBtCancelarClassificacao"/>
					<h:commandButton action="#{ classificarAcaoExtensao.preView }" value="Versão para Impressão" id="cmdBtPreView"/>
				</td>
			</tr>				
		</tfoot>
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>