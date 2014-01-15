<%@include file="./include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-menu-mobile" data-theme="b">
	
			<div data-role="header" data-theme="b">				
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="content">
				<c:if test="${not empty acesso.usuario.discente }">
					<%@include file="/mobile/touch/portal_discente.jsp"%>
				</c:if>
				
				<c:if test="${not empty acesso.usuario.servidor }">
					<%@include file="/mobile/touch/portal_docente.jsp"%>
				</c:if>
				
				<c:if test="${empty acesso.usuario.discente && empty acesso.usuario.servidor }">
					<p style="color: blue; white-space: normal;"> O sistema no modo mobile não está diponível para este vínculo (${usuario.vinculoAtivo.tipo }). Favor mudar para o modo clássico</p>
				</c:if>
			</div>
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			<%@include file="./include/modo_classico.jsp"%>
     </div>
</f:view>
<%@include file="./include/rodape.jsp"%>
