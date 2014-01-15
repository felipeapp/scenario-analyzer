<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<div class="secaoComunidade">

	<rich:panel header="Visualizar Conteúdo">	
		<table class="visualizacao" width="80%">
		<caption>Dados do Conteúdo</caption>
		
		<tr>
			<td><b>Tópico: </b>
				${ conteudoComunidadeMBean.object.topico.descricao }
			</td>
		</tr>
	
		<tr>
			<td><b>Título:</b>
				<h:outputText value="#{ conteudoComunidadeMBean.object.titulo }"/>
			</td>
		</tr>
	
		<tr> <td colspan="2" class="subFormulario">Conteúdo</td> </tr>
		<tr> <td  colspan="2" style="padding: 10px;">${ conteudoComunidadeMBean.object.conteudo }</td> </tr>
		</table>
	</div>
	
	<div class="voltar">
		<a href="javascript:history.back();"> Voltar </a>
	</div>
	</rich:panel>
</f:view>
	
<%@include file="/cv/include/rodape.jsp" %>