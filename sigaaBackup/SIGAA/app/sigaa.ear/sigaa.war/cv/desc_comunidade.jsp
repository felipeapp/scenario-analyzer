<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	
	<div class="secaoComunidade" id="descricao">
		<rich:panel header="Sobre a comunidade">
			<ufrn:format type="texto" valor="${comunidadeVirtualMBean.comunidade.descricao}" lineWrap="210" />
		</rich:panel>
	</div>

</f:view>
		
<%@include file="/cv/include/rodape.jsp" %>