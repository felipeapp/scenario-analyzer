<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	
	<div class="secaoComunidade" id="topicos">
		<rich:panel header="T�picos da Comunidade" headerClass="headerBloco">
		<h:form>
		<div class="infoAltRem">
			<c:if test="${ !comunidadeVirtualMBean.membro.visitante }">
				<h:graphicImage value="/img/adicionar.gif"/> <h:commandLink action="#{ topicoComunidadeMBean.novo }" value="Criar Novo T�pico para a Comunidade" />
				<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar}">
					<h:graphicImage value="/ava/img/page_white_put.png" /> <h:commandLink action="#{ configuracoesComunidadeVirtual.gerenciarTopicos }" value="Organizar T�picos" />
				</c:if>
				<h:graphicImage value="/cv/img/zoom.png"/>: Visualizar
				<img src="${ctx}/img/show.gif"/>: Exibir t�pico
				<img src="${ctx}/img/hide.gif"/>: Esconder t�pico
		       	<h:graphicImage value="/img/alterar.gif"/>: Alterar
		       	<h:graphicImage value="/img/garbage.png"/>: Remover
	       	</c:if>
		</div>
		</h:form>		
		<br/>
		<%@include file="/cv/TopicoComunidade/_topicos.jsp" %>
		</rich:panel>
	</div>

</f:view>
		
<%@include file="/cv/include/rodape.jsp" %>