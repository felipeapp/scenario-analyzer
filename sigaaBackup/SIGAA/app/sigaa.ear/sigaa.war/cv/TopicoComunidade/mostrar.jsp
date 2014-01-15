<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<div class="secaoComunidade">
	
	<rich:panel header="Tópico da Comunidade">
		<table class="visualizacao" width="80%" align="center">
		<caption>Tópico da Comunidade</caption>
		
		<tr>
			<td> <b>Descrição: </b>
				${ topicoComunidadeMBean.object.descricao } 
			</td>
		</tr>
	
		<tr> <td colspan="2" class="subFormulario">Tópico</td> </tr>
		<tr> <td  colspan="2" style="padding: 10px;">${ topicoComunidadeMBean.object.conteudo }</td> </tr>
		</table>
	</div>
	</rich:panel>
	<div class="voltar">
		<a href="javascript:history.back();"> Voltar </a>
	</div>
	
</f:view>
	
<%@include file="/cv/include/rodape.jsp" %>