<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<div class="secaoComunidade">
	

	<rich:panel header="Visualizar Referência">
		<table class="visualizacao" width="80%">
		<caption>Dados da Referência</caption>
		<tr>
			<td> <b>Tópico:</b>
				${ indicacaoReferenciaComunidadeMBean.object.topico.descricao } 
			</td>
		</tr>
	
		<tr>
			<td> <b> Título: </b>
				<h:outputText value="#{ indicacaoReferenciaComunidadeMBean.object.titulo }"/>
			</td>
		</tr>
		
		<tr>
			<td> <b>Tipo: </b>
				<h:outputText value="#{ indicacaoReferenciaComunidadeMBean.object.tipoDesc }"/>
			</td>
		</tr>
		<tr>
			<td> <b>Endereço (URL): </b>
				<a href="${ indicacaoReferenciaComunidadeMBean.object.url }" class="websnapr">${indicacaoReferenciaComunidadeMBean.object.url }</a>
			</td>
		</tr>
		<tr>
			<td> <b>Descrição: </b>
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