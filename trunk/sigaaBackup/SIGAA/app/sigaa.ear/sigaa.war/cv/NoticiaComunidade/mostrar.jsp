<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<div class="secaoComunidade">
	
	
	<rich:panel header="Visualizar Notícia">	
		<table class="visualizacao" width="80%" align="center">
		<caption>Dados da Notícia</caption>
		
		<tr>
			<td> <b>Descrição: </b>
				${ noticiaComunidadeMBean.object.descricao } 
			</td>
		</tr>
		<tr>
			<td> <b>Autor: </b>
				${ noticiaComunidadeMBean.object.usuarioCadastro.nome } 
			</td>
		</tr>
		<tr> <td colspan="2" class="subFormulario">Notícia</td> </tr>
		<tr> <td  colspan="2" style="padding: 10px;">${ noticiaComunidadeMBean.object.noticia }</td> </tr>
		</table>
	</div>
	</rich:panel>
	
	<div class="voltar">
		<a href="javascript:history.back();"> Voltar </a>
	</div>
	
</f:view>
	
<%@include file="/cv/include/rodape.jsp" %>