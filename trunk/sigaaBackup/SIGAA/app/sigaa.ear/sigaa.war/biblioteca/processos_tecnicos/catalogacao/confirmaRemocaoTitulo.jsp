<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
%>


<style type="text/css">
	div.menu-botoes li.fasciculo-substituidor a p {
		background-image: url('/sigaa/img/refresh.png');
	}
 
</style>


<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO }  %>">

	<h2>  <ufrn:subSistema /> &gt; Remo��o de T�tulos </h2>
	
	<div class="descricaoOperacao"> 
	
		 <p> Confirme a remo��o do T�tulo do sistema.</p> 
		  
		<p> <strong>Observa��o:</strong></p>
		<p> Somente t�tulos que n�o possu�rem materiais poder�o ser removidos do sistema.</p>
		<p> Para T�tulos com materiais informacionais � preciso primeiro dar baixa em todos os materiais ou mov�-los 
		para outro T�tulo antes de realizar a remo��o. </p>			
		 
	
	</div>


	<f:view>

		<h:form>
	
				
				<a4j:keepAlive beanName="removerEntidadeDoAcervoMBean"></a4j:keepAlive>
	
				<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
				
				<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>
				
				<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
				<a4j:keepAlive beanName="cooperacaoTecnicaExportacaoMBean"></a4j:keepAlive>
	
				<%-- No caso em que o usu�rio come�ou a transfer�ncia e clicou na op��o de remover o t�tulo e depois utilizou o bot�o voltar --%>
				<a4j:keepAlive beanName="transfereExemplaresEntreTitulosMBean"></a4j:keepAlive>
				
	
				<table class="visualizacao"  style="width: 70%">
				
					<caption>Informa��es do T�tulo que ser� removido</caption>
		
					<tr>
						<th>N�mero do Sistema:</th>				
						<td>
							<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.numeroDoSistema}" />
						</td>
					</tr>
		
					<tr>	
						<th>T�tulo:</th>		
						<td>
							<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.titulo}" />
						</td>
					</tr>
		
					<tr>	
						<th>Autor:</th>
						<td>
							<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.autor}" />
						</td>
					</tr>
		
					<tr>
						<th>Edi��o:</th>
						<td>
							<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.edicao}" />
						</td>
					</tr>
		
					<tr>
						<th>Ano:</th>
						<td>
							<c:forEach var="ano" items="#{removerEntidadeDoAcervoMBean.obj.anosFormatados}">
								${ano}
							</c:forEach>
						</td>
					</tr>
					
					<tr>
						<th style="width: 30%"> Quantidade de Materiais Informacionais:</th>
						<td>
							<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.quantidadeMateriaisAtivosTitulo}" />
						</td>
					</tr>
				
					<tfoot>
						<tr>
							<td colspan="2" style="text-align: center;">
								<h:commandButton value="Remover T�tulo" action="#{removerEntidadeDoAcervoMBean.removerEntidade}" 
									onclick="return confirm('Confirma remo��o do T�tulo ? ');"/>
								<h:commandButton value="<< Voltar" action="#{removerEntidadeDoAcervoMBean.voltarTelaTitulos}" />
								<h:commandButton value="Cancelar"  action="#{removerEntidadeDoAcervoMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
							</td>
						</tr>
					</tfoot>
					
				</table>
				
		</h:form>

	</f:view>


</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>