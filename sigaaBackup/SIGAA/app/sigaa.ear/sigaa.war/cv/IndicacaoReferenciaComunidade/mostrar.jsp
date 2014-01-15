<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<div class="secaoComunidade">
	

	<rich:panel header="Visualizar Refer�ncia">
		<table class="visualizacao" width="80%">
		<caption>Dados da Refer�ncia</caption>
		<tr>
			<td> <b>T�pico:</b>
				${ indicacaoReferenciaComunidadeMBean.object.topico.descricao } 
			</td>
		</tr>
	
		<tr>
			<td> <b> T�tulo: </b>
				<h:outputText value="#{ indicacaoReferenciaComunidadeMBean.object.titulo }"/>
			</td>
		</tr>
		
		<tr>
			<td> <b>Tipo: </b>
				<h:outputText value="#{ indicacaoReferenciaComunidadeMBean.object.tipoDesc }"/>
			</td>
		</tr>
		<tr>
			<td> <b>Endere�o (URL): </b>
				<a href="${ indicacaoReferenciaComunidadeMBean.object.url }" class="websnapr">${indicacaoReferenciaComunidadeMBean.object.url }</a>
			</td>
		</tr>
		<tr>
			<td> <b>Descri��o: </b>
				${ indicacaoReferenciaComunidadeMBean.object.descricao }
			</td>
		</tr>
		</table>
	</div>
	</rich:panel>	
	<div class="voltar">
		<a href="javascript:history.back();"> Voltar </a>
	</div>
	
</f:view>
	
<%@include file="/cv/include/rodape.jsp" %>